package ch13;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JLabel;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.Ml;
import org.opencv.ml.NormalBayesClassifier;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;

public class Ch13_3_3BayesForOnlineOCR {
	static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
	private JFrame frmOpencv;
	Mat paintMat=new Mat(140,140,0,new Scalar(255,255,255));
	Mat globalMat=new Mat();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Ch13_3_3BayesForOnlineOCR window = new Ch13_3_3BayesForOnlineOCR();
					window.frmOpencv.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Ch13_3_3BayesForOnlineOCR() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		//final Mat paintMat=Highgui.imread("C://opencv249//temp.png");
		final OcrDatabase ocr=new OcrDatabase();
final NormalBayesClassifier nBayes=NormalBayesClassifier.create();
		boolean r=nBayes.train(ocr.getTrainingDataMat(), Ml.ROW_SAMPLE, ocr.getTrainingLabelsMat());
		System.out.println("是否有訓練成功="+r);
		final BufferedImage image=matToBufferedImage(paintMat);
		
		frmOpencv = new JFrame();
		frmOpencv.setTitle("opencv ML練習");
		frmOpencv.getContentPane().setBackground(Color.LIGHT_GRAY);
		frmOpencv.setBounds(100, 100, 450, 300);
		frmOpencv.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmOpencv.getContentPane().setLayout(null);
		
		final JLabel lblShowXY = new JLabel("");
		lblShowXY.setBounds(10, 220, 124, 23);
		frmOpencv.getContentPane().add(lblShowXY);
		
		
		JPanel panel = new JPanel();
		final JLabel lblPaintLabel = new JLabel("");
		lblPaintLabel.setIcon(new ImageIcon(image));
		panel.add(lblPaintLabel);
		panel.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent arg0) {
				lblShowXY.setText("X:"+arg0.getX()+",Y:"+arg0.getY());
				BufferedImage newImage=matToBufferedImage(paint(paintMat,arg0.getX(),arg0.getY()));
				lblPaintLabel.setIcon(new ImageIcon(newImage));
			}
		});
		panel.setBounds(22, 23, 140, 140);
		frmOpencv.getContentPane().add(panel);
		
		JButton btnNewButton = new JButton("清除");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				paintMat=new Mat(140,140,0,new Scalar(255,255,255));
				BufferedImage newImage=matToBufferedImage(paintMat);
				lblPaintLabel.setIcon(new ImageIcon(newImage));
			}
		});
		btnNewButton.setBounds(172, 23, 87, 23);
		frmOpencv.getContentPane().add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("縮小至12x12");
		
		btnNewButton_1.setBounds(172, 66, 132, 23);
		frmOpencv.getContentPane().add(btnNewButton_1);
		
		final JLabel lblSmallImg = new JLabel("");
		lblSmallImg.setBounds(172, 116, 46, 35);
		frmOpencv.getContentPane().add(lblSmallImg);
		
		final JLabel lblMLresult = new JLabel("");
		lblMLresult.setBounds(159, 223, 273, 40);
		frmOpencv.getContentPane().add(lblMLresult);
		
		JButton btnMLpredict = new JButton("常態貝葉斯分類器預測");
		btnMLpredict.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				 float result=nBayes.predict(ocr.handleForML(getGlobalMat()));
				 System.out.println("預測結果="+result);
				 lblMLresult.setText("預測結果="+result);
				
			}
		});
		btnMLpredict.setBounds(172, 178, 238, 35);
		frmOpencv.getContentPane().add(btnMLpredict);
		
		btnNewButton_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				BufferedImage newImage=matToBufferedImage(resize(paintMat));
				lblSmallImg.setIcon(new ImageIcon(newImage));
			}
		});
		
	}
	
	public Mat paint(Mat src,int x,int y){
		Imgproc.circle(src, new Point(x,y), 2, new Scalar(0,0,0),2);
		
		return src;
		
	}
	public Mat resize(Mat src){
		Mat srcClone=src.clone();
		
		
		Mat dst=new Mat();
		Size size=new Size(12,12);
		//尋找輪廓
		 Imgproc.Canny(src,dst, 5, 100);
		 List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
         Mat hierarchy = new Mat(src.rows(),src.cols(),CvType.CV_8UC1,new Scalar(0));
         Imgproc.findContours(dst, contours, hierarchy, Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE);
         
         Mat drawing=Mat.zeros(dst.size(), CvType.CV_8UC3);
        	 //找到該輪廓最合適的外框
        	 Rect roiRect = Imgproc.boundingRect(contours.get(0));
        	
        	 //取的ROI的Mat
        	 Mat roi=new Mat(src,roiRect);
        	 Mat result=new Mat();
        	 Imgproc.resize(roi, result, size);//轉12x12
        	 setGlobalMat(result);
        	 return result;
	}
	
	
	public BufferedImage matToBufferedImage(Mat matrix) {  
	    int cols = matrix.cols();  
	    int rows = matrix.rows();  
	    int elemSize = (int)matrix.elemSize();  
	    byte[] data = new byte[cols * rows * elemSize];  
	    int type;  
	    matrix.get(0, 0, data);  
	    switch (matrix.channels()) {  
	      case 1:  
	        type = BufferedImage.TYPE_BYTE_GRAY;  
	        break;  
	      case 3:  
	        type = BufferedImage.TYPE_3BYTE_BGR;  
	        // bgr to rgb  
	        byte b;  
	        for(int i=0; i<data.length; i=i+3) {  
	          b = data[i];  
	          data[i] = data[i+2];  
	          data[i+2] = b;  
	        }  
	        break;  
	      default:  
	        return null;  
	    }  
	    BufferedImage image2 = new BufferedImage(cols, rows, type);  
	    image2.getRaster().setDataElements(0, 0, cols, rows, data);  
	    return image2;  
	  }

	public Mat getGlobalMat() {
		return globalMat;
	}

	public void setGlobalMat(Mat globalMat) {
		this.globalMat = globalMat;
	}  
}
