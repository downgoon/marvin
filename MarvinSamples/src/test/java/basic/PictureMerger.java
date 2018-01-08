package basic;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class PictureMerger {

	public static void main(String[] args) throws Exception {
		File fileOne = new File("src/test/resources/howto.jpg");
		BufferedImage ImageOne = ImageIO.read(fileOne);
		int width = ImageOne.getWidth();
		int height = ImageOne.getHeight();
		System.out.println("widith: " + width + ", height: " + height);

		// 序列化：从图片中读取RGB
		int[] ImageArrayOne = new int[width * height];
		ImageArrayOne = ImageOne.getRGB(0, 0, width, height, ImageArrayOne, 0, width);

		// 第二张图片：对第二张图片做相同处理。两张图片的宽度和高度必须一致。
		File fileTwo = new File("src/test/resources/howto.jpg");
		BufferedImage ImageTwo = ImageIO.read(fileTwo);
		int[] ImageArrayTwo = new int[width * height];
		ImageArrayTwo = ImageTwo.getRGB(0, 0, width, height, ImageArrayTwo, 0, width);

		// 生成新图片：反之是反序列化
		BufferedImage ImageNew = new BufferedImage(width * 2, height, BufferedImage.TYPE_INT_RGB);
		ImageNew.setRGB(0, 0, width, height, ImageArrayOne, 0, width); // 设置左半部分的RGB
		ImageNew.setRGB(width, 0, width, height, ImageArrayTwo, 0, width); // 设置右半部分的RGB

		// 转换格式：输入是jpg格式，输出转变成png格式
		File outFile = new File("target/howto-merge.png");
		ImageIO.write(ImageNew, "png", outFile);
		System.out.println("merge finished");
	}

}
