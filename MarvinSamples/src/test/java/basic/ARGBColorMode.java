package basic;

import marvin.image.MarvinImage;
import marvin.io.MarvinImageIO;

public class ARGBColorMode {

	public static void main(String[] args) throws Exception {
		MarvinImage imageIn = MarvinImageIO.loadImage("src/test/resources/howto.jpg");

		System.out.println("width: " + imageIn.getWidth() + ", height: " + imageIn.getHeight());
		System.out.println("image format name: " + imageIn.getFormatName());

		int count = 0;
		for (int x = 0; x < imageIn.getWidth(); x++) {
			for (int y = 0; y < imageIn.getHeight(); y++) {
				Integer color = imageIn.getIntColor(x, y);
				int r = imageIn.getIntComponent0(x, y);
				int g = imageIn.getIntComponent1(x, y);
				int b = imageIn.getIntComponent2(x, y);
				int alpha = imageIn.getAlphaComponent(x, y);
				// ARGB 
				if (count++ < 8) {
					System.out.println(String.format("[%d,%d]: %08X => alpha: %x @(%x, %x, %x) ", x, y, color, alpha, r, g, b));
				}
			}
		}
		
		// 转成透明的：把alpha从0xFF设置为0x00
		for (int x = 0; x < imageIn.getWidth(); x++) {
			for (int y = 0; y < imageIn.getHeight(); y++) {
				imageIn.setAlphaComponent(x, y, 0);
			}
		}

		// 注意：必须保存为png，不能为jpg。因为png才能保存alpha值
		// jpg是不支持透明,可以考虑用bmp、png;
		MarvinImageIO.saveImage(imageIn, "target/argb-transparency.png");
		System.out.println("alpha set to 0x00");
		
	}

}
