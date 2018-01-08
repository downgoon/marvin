package basic;

import marvin.image.MarvinImage;
import marvin.io.MarvinImageIO;

public class BlackWhiteFilter {

	public static void main(String[] args) {
		
		MarvinImage imageIn = MarvinImageIO.loadImage("src/test/resources/howto.jpg");

		System.out.println("width: " + imageIn.getWidth() + ", height: " + imageIn.getHeight());
		System.out.println("image format name: " + imageIn.getFormatName());
		
		MarvinImage imageOut = imageIn.clone();

		for (int x = 0; x < imageIn.getWidth(); x++) {
			for (int y = 0; y < imageIn.getHeight(); y++) {
				int alpha = imageIn.getAlphaComponent(x, y); // alpha
				int r = imageIn.getIntComponent0(x, y); // c0
				int g = imageIn.getIntComponent1(x, y); // c1
				int b = imageIn.getIntComponent2(x, y); // c2
				
				int finalColor = (int)((r + g + b) / 3); // 平均值算法
				// 255：白色； 0: 黑色
				finalColor = finalColor > 100 ? 255 : 0;
				// (x, y) <= alpha, c0, c1, c2
				imageOut.setIntColor(x,y,alpha, finalColor,finalColor,finalColor);
			}
		}
		
		MarvinImageIO.saveImage(imageOut, "target/howto-bw.png");
	}

}
