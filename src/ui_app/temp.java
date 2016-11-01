package ui_app;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
/*import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;*/

//TemplateMatching BACKUP
public class temp {
    private double corr_func = 0;
    private Point matchLoc = new Point(0,0);
    Graphics2D g2d;
    int w, h, W, H;
    public int[][] convertToHSI(BufferedImage bi, int w, int h){
        int[] r = new int[w*h];
        int[] g = new int[w*h];
        int[] b = new int[w*h];
        int[] max = new int[w*h];
        int[] min = new int[w*h];
        int[][] ii = new int[w][h];

        for( int i = 0; i < w; i++ )
            for( int j = 0; j < h; j++ ){
                r[i*h + j] = new Color(bi.getRGB(i, j)).getRed();
                g[i*h + j] = new Color(bi.getRGB(i, j)).getGreen();
                b[i*h + j] = new Color(bi.getRGB(i, j)).getBlue();
                max[i*h + j] = maxOfThree(r[i*h + j], g[i*h + j], b[i*h + j]);
                min[i*h + j] = minOfThree(r[i*h + j], g[i*h + j], b[i*h + j]);
                ii[i][j] = (max[i*h + j] + min[i*h + j])/2;
            }
        return ii;
    }
    public double[][] compare(BufferedImage buf_image1_temp,BufferedImage buf_image2_temp, int formula_index){
        w = buf_image2_temp.getWidth();
        h = buf_image2_temp.getHeight();
        W = buf_image1_temp.getWidth();
        H = buf_image1_temp.getHeight();

        double[][] result = new double[(W-w+1)][(H-h+1)];

        int[][] ti = new int[w][h];
        ti = convertToHSI(buf_image2_temp, w, h);
        int[][] ii = new int[W][H];
        ii = convertToHSI(buf_image1_temp, W, H);

        if(formula_index == 0){

            double templ_average = 0;
            for(int x = 0; x < w; x++){
                for(int y = 0; y < h; y++){
                    templ_average += ti[x][y];
                }
            }
            templ_average /= w*h;

            double image_average = 0;
            double up_left = 0;
            double up_right = 0;
            double sum_up = 0;
            double sum_down1 = 0;
            double sum_down2 = 0;
            for(int rx = 0; rx < (W-w+1); rx++){
                for(int ry = 0; ry < (H-h+1); ry++){
                    image_average = 0;
                    for(int x = 0; x < w; x++){
                        for(int y = 0; y < h; y++){
                            image_average += ii[x+rx][y+ry];
                        }
                    }
                    image_average /= w*h;
                    sum_up = 0;
                    sum_down1 = 0;
                    sum_down2 = 0;
                    for(int x = 0; x < w; x++){
                        for(int y = 0; y < h; y++){
                            up_left = ti[x][y] - templ_average;
                            up_right = ii[x+rx][y+ry] - image_average;
                            sum_up += up_left * up_right;
                            sum_down1 += Math.pow(up_left,2);
                            sum_down2 += Math.pow(up_right,2);
                        }
                    }
                    result[rx][ry] = sum_up/Math.sqrt(sum_down1)/Math.sqrt(sum_down2);
                    //System.out.println(result[rx][ry] + "["+rx+"]" +ry);
                }
            }
            max(result, W-w+1, H-h+1);
        }
        else if(formula_index == 1){
            double sum = 0;
            for(int rx = 0; rx < (W-w+1); rx++){
                for(int ry = 0; ry < (H-h+1); ry++){
                    sum = 0;
                    for(int x = 0; x < w; x++){
                        for(int y = 0; y < h; y++){
                            sum += Math.pow((ti[x][y] - ii[x+rx][y+ry]), 2);
                        }
                    }
                    result[rx][ry] = sum;
                }
            }
            min(result, W-w+1, H-h+1);
        }
        else if(formula_index == 2){
            double templ_average = 0;
            for(int x = 0; x < w; x++){
                for(int y = 0; y < h; y++){
                    templ_average += ti[x][y];
                }
            }
            templ_average/=w*h;

            double up_left = 0;
            double up_right = 0;
            double down_left = 0;
            double down_right = 0;
            double image_average = 0;
            double sum = 0;
            for(int rx = 0; rx < (W-w+1); rx++){
                for(int ry = 0; ry < (H-h+1); ry++){
                    image_average = 0;
                    for(int x = 0; x < w; x++){
                        for(int y = 0; y < h; y++){
                            image_average += ii[x+rx][y+ry];
                        }
                    }
                    image_average /= w*h;
                    down_left = 0;
                    down_right = 0;
                    for(int x = 0; x < w; x++){
                        for(int y = 0; y < h; y++){
                            down_left += Math.pow(ti[x][y] - templ_average, 2);
                            down_right += Math.pow(ii[x+rx][y+ry] - image_average, 2);
                        }
                    }
                    down_left = Math.sqrt(down_left);
                    down_right = Math.sqrt(down_right);
                    sum = 0;
                    for(int x = 0; x < w; x++){
                        for(int y = 0; y < h; y++){
                            up_left = ti[x][y] - templ_average;
                            up_right = ii[x+rx][y+ry] - image_average;
                            sum += Math.pow(up_left/down_left - up_right/down_right, 2);
                        }
                    }
                    result[rx][ry] = sum;
                    //System.out.println(result[rx][ry] + "[" +rx+"]"+"["+ry+"]");
                }
            }
            min(result, W-w+1, H-h+1);
        }
        else if(formula_index == 3){
            BufferedImage image1 = Controller.deepCopy(buf_image1_temp);
            BufferedImage image2 = buf_image2_temp;

            for(int rx = 0; rx < (W-w+1); rx++){
                for(int ry = 0; ry < (H-h+1); ry++){

                    result[rx][ry] = 9;
                }
            }
            File outputfile = new File("temp.jpg");
            try {
                ImageIO.write(buf_image2_temp, "jpg", outputfile);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                image2 = ImageIO.read(new File("temp.jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            new temp1().run(image1, image2);
            try{
                if(outputfile.delete()){
                    System.out.println("is deleted!");
                }else{
                    System.out.println("Delete operation is failed.");
                }
            }catch(Exception e){
                e.printStackTrace();
            }
			/*int temp_up = 0;
			int temp_down2 = 0;
			double temp_down1 = 0;
			int temp1_sum = 0;

			for(int x = 0; x < w; x++){
				for(int y = 0; y < h; y++){
					temp1_sum += Math.pow(ti[x][y], 2);
				}
			}
			temp_down1 = Math.sqrt(temp1_sum);
			for(int rx = 0; rx < (W-w+1); rx++){
				for(int ry = 0; ry < (H-h+1); ry++){
					temp_up = 0;
					temp_down2 = 0;
					for(int x = 0; x < w; x++){
						for(int y = 0; y < h; y++){
							temp_up += Math.pow((ti[x][y]-ii[(rx+x)][ry+y]),2);
							temp_down2 += Math.pow(ii[(rx+x)][ry+y], 2);
						}
					}
					result[rx][ry] = temp_up/temp_down1/Math.sqrt(temp_down2);
					//System.out.println(result[r][ry] + "["+r+"]" +ry);
				}
			}
			min(result, W-w+1, H-h+1);	*/
        }

		/*g2d = buf_image1_temp.createGraphics();
		g2d.setStroke(new BasicStroke(2));
		g2d.setColor(Color.black);
	    g2d.draw(new Rectangle2D.Double(getMatchLoc().x, getMatchLoc().y, w, h));
	    //g2d.dispose();*/

        return result;
    }
    public void remove(){
        g2d.clearRect(getMatchLoc().x, getMatchLoc().y, w, h);
    }
    public Point getMatchLoc(){
        return matchLoc;
    }
    public void setMatchLoc(int x, int y){
        matchLoc.x = x;
        matchLoc.y = y;
    }
    public double getCorrFunc(){
        return corr_func;
    }
    public void setCorrFunc(double a){
        corr_func = a;
    }
    public int maxOfThree(int a, int b, int c){
        if(a <= c && b <= c)
            return c;
        if(c <= b && a <= b)
            return b;
        else
            return a;
    }
    public int minOfThree(int a, int b, int c){
        if(a >= c && b >= c)
            return c;
        if(c >= b && a >= b)
            return b;
        else
            return a;
    }
    public void max(double[][] arr, int w,int h){
        setCorrFunc(arr[0][0]);
        for(int x = 0; x < w; x++){
            for(int y = 0; y < h; y++){
                if(getCorrFunc() < arr[x][y]){
                    setCorrFunc(arr[x][y]);
                    setMatchLoc(x, y);
                }
            }
        }
        System.out.println("MAX: " + getCorrFunc()+"---"+getMatchLoc().x + " and "+getMatchLoc().y);
    }
    public void min(double[][] arr, int w,int h){
        setCorrFunc(arr[0][0]);
        for(int x = 0; x < w; x++){
            for(int y = 0; y < h; y++){
                if(getCorrFunc() > arr[x][y]){
                    setCorrFunc(arr[x][y]);
                    setMatchLoc(x, y);
                }
            }
        }
        System.out.println("MIN: " + getCorrFunc()+"---"+getMatchLoc().x + " and "+getMatchLoc().y);
    }
    public Point getCoord(int coord, int h){
        Point p = new Point();
        p.x = (int)coord/h;
        p.y = coord - p.x * h;
        System.out.println("X = "+ p.x);
        System.out.println("Y = "+ p.y);
        return p;
    }
    public int max(int[] arr, int w,int h){
        int max_int = (int)arr[0];
        for(int x = 0; x < w*h; x++){
            if(max_int < arr[x]){
                max_int = x;
            }
        }
        System.out.println("MAX: " + max_int);
        return max_int;
    }
    public int min(double[] arr, int w,int h){
        int min_int = 0;
        for(int x = 0; x < w*h; x++){
            if(arr[min_int] > arr[x]){
                min_int = x;
            }
        }
        System.out.println("MIN: " + arr[min_int]);
        return min_int;
    }
    class temp1 {
        public void run(BufferedImage myimg_buf, BufferedImage mytempl_buf) {
            System.out.println("Hello, OpenCV");

            // Load the native library.
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

            System.out.println("\nRunning DetectFaceDemo");

            //Mat myimg = Highgui.imread(myimg_path);
            //Mat mytempl = Highgui.imread(mytempl_path);
            Mat myimg = bufferedImageToMat(myimg_buf);
            Mat mytempl = bufferedImageToMat(mytempl_buf);
            Highgui.imwrite("check.jpg", mytempl);

            int result_cols = myimg.cols() - mytempl.cols() + 1;
            int result_rows = myimg.rows() - mytempl.rows() + 1;
            Mat result = new Mat(result_cols, result_rows, CvType.CV_32FC1);

            // / Do the Matching and Normalize
            Imgproc.matchTemplate(myimg, mytempl, result, Imgproc.TM_CCOEFF);
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

            // / Show me what you got
            Core.rectangle(
                    myimg,
                    matchLoc,
                    new org.opencv.core.Point(matchLoc.x + mytempl.cols(), matchLoc.y
                            + mytempl.rows()), new Scalar(0, 0, 255));

            // Save the visualized detection.
            System.out.println("Writing ");
            //return matToBufferedImage(myimg);
            Highgui.imwrite("out.jpg", myimg);
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
	/*public void compare1(){
	int w = buf_image2_temp.getWidth();
	int h = buf_image2_temp.getHeight();
	int W = buf_image1_temp.getWidth();
	int H = buf_image1_temp.getHeight();

	double[] result = new double[(W-w+1)*(H-h+1)];

	int[][] image = new int[W][H];for( int i = 0; i < W; i++ ) for( int j = 0; j < H; j++ )	image[i][j] = buf_image1_temp.getRGB( i, j );
	int[][] templ = new int[w][h];for( int i = 0; i < w; i++ ) for( int j = 0; j < h; j++ )	templ[i][j] = buf_image2_temp.getRGB( i, j );

	int[] ti = new int[w*h];
	ti = convertToHSI(buf_image2_temp, w, h);
	int[] ii = new int[W*H];
	ii = convertToHSI(buf_image1_temp, W, H);

	int temp_up = 0;
	int temp_down1 = 0;
	int temp_down2 = 0;
	int[] temp_down1_sum = new int[w*h];

	for(int x = 0; x < w*h; x++){
		temp_down1_sum[x] = ti[x]*ti[x];
		for(int k = 0; k < x; k++){
			temp_down1_sum[x] += temp_down1_sum[k];
		}
	}
	for(int rx = 0; rx < (W-w+1)*(H-h+1); rx++){
			temp_up = 0;
			temp_down2 = 0;
			for(int x = 0; x < w*h; x++){
					temp_up += Math.pow((ti[x]-ii[(x+rx)]),2);
					temp_down2 += Math.pow(ii[(x*h+rx)],2);
					temp_down1 = temp_down1_sum[x];
			}
			result[rx] = temp_up/Math.sqrt(temp_down1)/Math.sqrt(temp_down2);
			//System.out.println(result[rx]);
	}
	Point matchLoc = getCoord(min(result,(W-w+1),(H-h+1)),(H-h+1));
	Graphics2D g2d = buf_image1_temp.createGraphics();
    g2d.draw(new Rectangle2D.Double(matchLoc.x, matchLoc.y, w, h));
    g2d.dispose();
    File outputfile = new File("image.jpg");
		try {
			ImageIO.write(buf_image1_temp, "jpg", outputfile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}*/
/*class TemplateMatchingLib {
  public static void main(String[] args) {
    System.out.println("Hello, OpenCV");

    // Load the native library.
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    new TemplateMatchingLibFunctions().run();
  }
  public static BufferedImage run(BufferedImage myimg_buf, BufferedImage mytempl_buf) {
	System.out.println("Hello, OpenCV");

	    // Load the native library.
	System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

    System.out.println("\nRunning DetectFaceDemo");

    //Mat myimg = Highgui.imread(myimg_path);
    //Mat mytempl = Highgui.imread(mytempl_path);
    Mat myimg = bufferedImageToMat(myimg_buf);
    Mat mytempl = bufferedImageToMat(mytempl_buf);

    int result_cols = myimg.cols() - mytempl.cols() + 1;
    int result_rows = myimg.rows() - mytempl.rows() + 1;
    Mat result = new Mat(result_cols, result_rows, CvType.CV_32FC1);

    // / Do the Matching and Normalize
    Imgproc.matchTemplate(myimg, mytempl, result, Imgproc.TM_CCOEFF);
    Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1,
            new Mat());

    // / Localizing the best match with minMaxLoc
    MinMaxLocResult mmr = Core.minMaxLoc(result);

    org.opencv.core.Point matchLoc;
    if (Imgproc.TM_CCOEFF == Imgproc.TM_SQDIFF
            || Imgproc.TM_CCOEFF == Imgproc.TM_SQDIFF_NORMED)
        matchLoc = mmr.minLoc;
    else
        matchLoc = mmr.maxLoc;

    // / Show me what you got
    Core.rectangle(
    		myimg,
            matchLoc,
            new org.opencv.core.Point(matchLoc.x + mytempl.cols(), matchLoc.y
                    + mytempl.rows()), new Scalar(0, 0, 255));

    // Save the visualized detection.
    System.out.println("Writing ");
    return matToBufferedImage(myimg);
    //Highgui.imwrite("out.jpg", myimg);
  }
  public static BufferedImage matToBufferedImage(Mat myMat)
  {
	  Mat mat = myMat;
	  byte[] data = new byte[mat.rows()*mat.cols()*(int)(mat.elemSize())];
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
  public static Mat bufferedImageToMat(BufferedImage bi) {
	  Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
	  byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
	  mat.put(0, 0, data);
	  return mat;
  }
}*/

