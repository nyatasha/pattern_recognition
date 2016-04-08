package ui_app;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import com.mortennobel.imagescaling.ResampleOp;

public class Controller {
    public static boolean checkSelectedFile(File file){
        return getFileExtension(file).equals("png") || getFileExtension(file).equals("jpg") || getFileExtension(file).equals("jpeg")
        		|| getFileExtension(file).equals("JPG") ;
    }
    public static BufferedImage resizeImagetoLabel(BufferedImage image, JLabel imgLabel) {
    	int width = imgLabel.getWidth();
	    int height = imgLabel.getHeight();  
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
        Graphics2D g2d = (Graphics2D) bi.createGraphics();
        g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();
        return bi;
    }
    public static String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }
	public static ImageIcon makeNewSize(BufferedImage buf_image2, double zoomscale){
		ImageIcon imageIcon2 = null;
		if(buf_image2 != null){
	    	ResampleOp resampleOp2 = new ResampleOp((int) (buf_image2.getWidth() * zoomscale), (int) (buf_image2.getHeight() * zoomscale));
	        BufferedImage resizedIcon2 = resampleOp2.filter(buf_image2, null);
	        imageIcon2 = new ImageIcon(resizedIcon2);	        
		}
		return imageIcon2;
    }
	public static BufferedImage deepCopy(BufferedImage bi) {		 
  	 	 ColorModel cm = bi.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = bi.copyData(null);
		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

}
