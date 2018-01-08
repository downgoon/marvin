package basic;

import marvin.image.MarvinImage;
import marvin.io.MarvinImageIO;

public class InvertFilter {

	public static void main(String[] args) {
		MarvinImage imageIn = MarvinImageIO.loadImage("src/test/resources/howto.jpg");
		MarvinImage imageOut = imageIn.clone();

		for (int x = 0; x < imageIn.getWidth(); x++) {
			for (int y = 0; y < imageIn.getHeight(); y++) {
				int alpha = imageIn.getAlphaComponent(x, y); // alpha
				int r = imageIn.getIntComponent0(x, y); // c0
				int g = imageIn.getIntComponent1(x, y); // c1
				int b = imageIn.getIntComponent2(x, y); // c2
				imageOut.setIntColor(x,y,alpha, 255-r,255-g,255-b);
			}
		}
		
		MarvinImageIO.saveImage(imageOut, "target/howto-invert.png");
	}

}
