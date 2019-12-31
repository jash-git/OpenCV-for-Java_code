package ch05;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Ch05_6_1Sharpness {
   public static void main( String[] args )
   {
	   System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
      try{
       
         Mat source = Imgcodecs.imread("C://opencv3.1//samples//lena.jpg", 
         Imgcodecs.CV_LOAD_IMAGE_COLOR);
         Mat destination = new Mat(source.rows(),source.cols(),source.type());
         Imgproc.medianBlur(source, destination,  9);
         Core.addWeighted(source, 2.1, destination, -1.1, 0, destination);
         Imgcodecs.imwrite("C://opencv3.1//samples//lena-sharped.jpg", destination);
         }catch (Exception e) {
            System.out.println("error: " + e.getMessage());
         }
   }
}