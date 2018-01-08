package basic;

import java.awt.Color;

import marvin.image.MarvinImage;
import marvin.io.MarvinImageIO;

public class DrawRect {

	public static void main(String[] args) {
		
		MarvinImage imageIn = MarvinImageIO.loadImage("src/test/resources/howto.jpg");

		System.out.println("width: " + imageIn.getWidth() + ", height: " + imageIn.getHeight());
		System.out.println("image format name: " + imageIn.getFormatName());
		
		MarvinImage imageOut = imageIn.clone();

		int x = 100;
		int y = 200;
		int w = 180;
		int h = 50;
		
		imageOut.drawRect(x, y, w, h, Color.RED); 
		imageOut.drawRect(x + 10, y + 10, w, h, 5, Color.GREEN);
		imageOut.drawLine(x, y, x + w, y + h, Color.YELLOW);
		
		MarvinImageIO.saveImage(imageOut, "target/howto-draw.png");
	}

}
