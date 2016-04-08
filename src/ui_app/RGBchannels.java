package ui_app;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class RGBchannels {
	public static void getRed(BufferedImage image){
		int width = image.getWidth();
	    int height = image.getHeight();  
	    
	    int iRed=0;
	    int newPixel=0;
	    
	    if(image != null){		    
	    	for (int i = 0; i < width; i++)  {
	    		for (int j = 0; j < height; j++)
	    		{
	    			newPixel=0;
	    			iRed=new Color(image.getRGB(i, j)).getRed();
	    			newPixel = newPixel | iRed<<16; 	
	    			image.setRGB(i, j, newPixel);
	    		}
	    	}
	    }
	}
	public static void getGreen(BufferedImage image){
	    int width = image.getWidth();
	    int height = image.getHeight();    

	    int iGreen=0;
	    int newPixel=0;
	    
	    if(image != null){		    
	    	for (int i = 0; i < width; i++)  {
	    		for (int j = 0; j < height; j++)
	    		{
	    			newPixel=0;
	    			iGreen=new Color(image.getRGB(i, j)).getGreen();
	    			newPixel = newPixel | iGreen<<8;   			
	    			image.setRGB(i, j, newPixel);
	    		}
	    	}
	    }
	}
	public static void getBlue(BufferedImage image){
	    int width = image.getWidth();
	    int height = image.getHeight();    

	    int iBlue=0;
	    int newPixel=0;
	    
	    if(image != null){		    
	    	for (int i = 0; i < width; i++)  {
	    		for (int j = 0; j < height; j++)
	    		{
	    			newPixel=0;
	    			iBlue=new Color(image.getRGB(i, j)).getBlue();
	    			newPixel = newPixel | iBlue;   			
	    			image.setRGB(i, j, newPixel);
	    		}
	    	}
	    }
	}
	public static void getGray(BufferedImage image){
		int width = image.getWidth();
	    int height = image.getHeight();    
	    
	    if(image != null){		    
	    	for (int i = 0; i < width; i++)  {
	    		for (int j = 0; j < height; j++) {
				    int rgb = image.getRGB(i, j);
			        int r = (rgb >> 16) & 0xFF;
			        int g = (rgb >> 8) & 0xFF;
			        int b = (rgb & 0xFF);  
			        int grayLevel = (r + g + b) / 3;
			        int gray = (grayLevel << 16) + (grayLevel << 8) + grayLevel; 
			        image.setRGB(i, j, gray);
	    		}
	    	}
	    }	
	}
	public static BufferedImage getRGB(int index, BufferedImage buf_image1){
    	BufferedImage buf_image1_temp = null;
    	if(buf_image1 != null){
	    	if(index == 0){
	    		buf_image1_temp = Controller.deepCopy(buf_image1);
	    	}
	    	else if(index == 1){
	    		buf_image1_temp = Controller.deepCopy(buf_image1);
	    		getRed(buf_image1_temp);
	    	}
	    	else if(index == 2){
	    		buf_image1_temp = Controller.deepCopy(buf_image1);
	    		getGreen(buf_image1_temp);
	    	}
	    	else if(index == 3){
	    		buf_image1_temp = Controller.deepCopy(buf_image1);
	    		getBlue(buf_image1_temp);
	    	}
	    	else if(index == 4){
	    		buf_image1_temp = Controller.deepCopy(buf_image1);
	    		getGray(buf_image1_temp);
	    	}    	
    	}
    	return buf_image1_temp;
	}	
}
