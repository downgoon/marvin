# 图片处理初步

本章通过两个例子来说明一个概念：**图片就是颜色的点阵**。

## 拼图

拼图：输入两张宽高比相同的图片，拼接成左右形式，输出一张图片

``` java
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

```

读完这段代码，我们总结2点：
- 图片是颜色的点阵：无论什么图片，``png``，还是``jpg``，读取到内存后，都是颜色的点阵。图片运算本质上是矩阵的运算。
- 序列化和反序列化：图片读取后，就成了颜色的点阵。但在磁盘上，不同的图片格式（比如``png``和``jpg``），有着不同的压缩存储格式。

**注意**
>因为图片在内存中处理要展开成矩阵，873*601=524,673 Bytes = 0.5MB 一张图片需要0.5M的存储。而原始图片在磁盘上只有 46KB，放内存中放大了10倍。jpg和png压缩算法的压缩率是很高的。

## 滤镜

最简单的滤镜就是颜色滤镜，不改变图片的外形，只改变颜色。比如灰度滤镜，就是把照片的彩色去掉。可能你也看到过``哈哈镜``滤镜，能把人的脸放大等，本节不介绍。

``` java
package org.marvinproject.image.color.grayScale;

public class GrayScale extends MarvinAbstractImagePlugin {

  public void process (
		MarvinImage imageIn, MarvinImage imageOut,
		MarvinAttributes attributesOut, MarvinImageMask mask,
		boolean previewMode
	) {
    boolean[][] l_arrMask = mask.getMask();
    performanceMeter.start("Gray");
    performanceMeter.startEvent("Gray");

    int r,g,b,finalColor;
    for (int x = 0; x < imageIn.getWidth(); x++) {
          for (int y = 0; y < imageIn.getHeight(); y++) {
              if(l_arrMask != null && !l_arrMask[x][y]) {
                  continue;
              }

              //Red - 30% / Green - 59% / Blue - 11%
              r = imageIn.getIntComponent0(x, y);
              g = imageIn.getIntComponent1(x, y);
              b = imageIn.getIntComponent2(x, y);
              finalColor = (int)((r*0.3)+(g*0.59)+(b*0.11));
              imageOut.setIntColor(x,y,imageIn.getAlphaComponent(x, y), finalColor,finalColor,finalColor);

          }
          performanceMeter.stepsFinished(imageIn.getHeight());
      }
    performanceMeter.finishEvent();
    performanceMeter.finish();
  }
}
```

代码很简单，就是从输入图片提取颜色，然后做了个颜色变换：

``` java
for (int x = 0; x < imageIn.getWidth(); x++) {
      for (int y = 0; y < imageIn.getHeight(); y++) {
            r = imageIn.getIntComponent0(x, y);
            g = imageIn.getIntComponent1(x, y);
            b = imageIn.getIntComponent2(x, y);
            finalColor = (int)((r*0.3)+(g*0.59)+(b*0.11)); // 颜色变换
            imageOut.setIntColor(x,y,imageIn.getAlphaComponent(x, y), finalColor,finalColor,finalColor);
      }
}
```
