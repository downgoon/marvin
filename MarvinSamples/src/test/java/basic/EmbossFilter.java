package basic;

import marvin.image.MarvinImage;
import marvin.io.MarvinImageIO;

public class EmbossFilter {

	public static void main(String[] args) {
		MarvinImage imageIn = MarvinImageIO.loadImage("src/test/resources/howto.jpg");
		MarvinImage imageOut = imageIn.clone();

		int precolor = imageIn.getIntColor(0, 0); // 上一个像素点
		for (int x = 0; x < imageIn.getWidth(); x++) {
			for (int y = 0; y < imageIn.getHeight(); y++) {
				
				int alpha = imageIn.getAlphaComponent(x, y); // alpha
				int r = imageIn.getIntComponent0(x, y); // c0
				int g = imageIn.getIntComponent1(x, y); // c1
				int b = imageIn.getIntComponent2(x, y); // c2
				
				int nR = (r - (precolor&0x00FF0000 >> 16) ) + 128;
				int nG = (g - (precolor&0x0000FF00 >> 8) ) + 128;
				int nB = (b - (precolor&0x000000FF ) ) + 128;

				int nC = (int)(nR * 0.3 + nG * 0.59 + nB * 0.11);  
				
				// imageOut.setIntColor(x,y,alpha, nR, nG, nB);
				imageOut.setIntColor(x,y,alpha, nC, nC, nC);
				
				precolor = imageIn.getIntColor(x, y);
			}
		}
		
		MarvinImageIO.saveImage(imageOut, "target/howto-emboss.png");
	}

}
