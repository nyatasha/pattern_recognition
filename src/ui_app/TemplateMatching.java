package ui_app;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

public class TemplateMatching {
    String bestLoc = "";
    double corrFunc = 0;

	public BufferedImage compare(BufferedImage image1,BufferedImage buf_image2_temp, int formula_index){
		BufferedImage result = null;
        BufferedImage image2 = buf_image2_temp;

        File outputfile = new File("temp.jpg");
        try {
            ImageIO.write(buf_image2_temp, "jpg", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            image2 = ImageIO.read(outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
		if(formula_index == 0){
            result = new TemplateMatchingOpenCV().run(image1, image2, Imgproc.TM_CCORR_NORMED);
		}
		else if(formula_index == 1){
            result = new TemplateMatchingOpenCV().run(image1, image2, Imgproc.TM_SQDIFF);
		}
		else if(formula_index == 2){
            result = new TemplateMatchingOpenCV().run(image1, image2, Imgproc.TM_SQDIFF_NORMED);
		}
        try{
            outputfile.delete();
        }catch(Exception e){
            e.printStackTrace();
        }
		return result;
	}
    public double getCorrFunc(){
        return corrFunc;
    }
    public void setCorrFunc(double a){
        corrFunc = a;
    }
	public Point getMatchLoc(){
        if(bestLoc != ""){
            String s  = bestLoc.substring(1, bestLoc.length()-1);
            String[] coords = s.split(", ");
            coords[0] = coords[0].substring(0, coords[0].length()-2);
            coords[1] = coords[1].substring(0, coords[1].length()-2);
            return new Point(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
        }
        else
            return new Point(0,0);
	}
	public void setMatchLoc(String matchLoc){
        bestLoc = matchLoc;
	}

    class TemplateMatchingOpenCV {
        public BufferedImage run(BufferedImage myimg_buf, BufferedImage mytempl_buf, int method) {

            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

            Mat myimg = bufferedImageToMat(myimg_buf);
            Mat mytempl = bufferedImageToMat(mytempl_buf);

            int result_cols = myimg.cols() - mytempl.cols() + 1;
            int result_rows = myimg.rows() - mytempl.rows() + 1;
            Mat result = new Mat(result_cols, result_rows, CvType.CV_32FC1);

            Imgproc.matchTemplate(myimg, mytempl, result, method);
            Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1,
                    new Mat());

            // / Localizing the best match with minMaxLoc
            Core.MinMaxLocResult mmr = Core.minMaxLoc(result);

            org.opencv.core.Point matchLoc;
            if (Imgproc.TM_CCOEFF == Imgproc.TM_SQDIFF
                    || Imgproc.TM_CCOEFF == Imgproc.TM_SQDIFF_NORMED)
                matchLoc = mmr.minLoc;
            else
                matchLoc = mmr.maxLoc;

            setMatchLoc(matchLoc.toString());

            if(method == Imgproc.TM_SQDIFF || method == Imgproc.TM_SQDIFF_NORMED)
                setCorrFunc(mmr.minVal);
            else
                setCorrFunc(mmr.maxVal);

            Core.rectangle(
                    myimg,
                    matchLoc,
                    new org.opencv.core.Point(matchLoc.x + mytempl.cols(), matchLoc.y
                            + mytempl.rows()), new Scalar(0, 0, 255));

            // Save the visualized detection.
            System.out.println("Writing ");
            Highgui.imwrite("out.jpg", myimg);
            return matToBufferedImage(myimg);
        }

        public  BufferedImage matToBufferedImage(Mat myMat) {
            Mat mat = myMat;
            byte[] data = new byte[mat.rows() * mat.cols() * (int) (mat.elemSize())];
            mat.get(0, 0, data);
            if (mat.channels() == 3) {
                for (int i = 0; i < data.length; i += 3) {
                    byte temp = data[i];
                    data[i] = data[i + 2];
                    data[i + 2] = temp;
                }
            }
            BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), BufferedImage.TYPE_3BYTE_BGR);
            image.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), data);
            return image;
        }

        public  Mat bufferedImageToMat(BufferedImage bi) {
            Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
            byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
            mat.put(0, 0, data);
            return mat;
        }
    }
}