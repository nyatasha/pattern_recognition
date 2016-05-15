package ui_app;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
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
	public static ImageIcon makeNewSize(BufferedImage buf_image, double zoomscale){
		ImageIcon imageIcon2 = null;
		BufferedImage resizedIcon = null;
		if(buf_image != null){
	    	ResampleOp resampleOp = new ResampleOp((int) (buf_image.getWidth() * zoomscale), (int) (buf_image.getHeight() * zoomscale));
	        resizedIcon = resampleOp.filter(buf_image, null);
	        imageIcon2 = new ImageIcon(resizedIcon);	        
		}
		return imageIcon2;
		//return resizedIcon;
    }
	public static BufferedImage deepCopy(BufferedImage bi) {		 
  	 	 ColorModel cm = bi.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = bi.copyData(null);
		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
    public static BufferedImage copyImage(BufferedImage source){
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }

}
