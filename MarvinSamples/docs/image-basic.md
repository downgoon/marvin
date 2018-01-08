# 图片处理初步

本章通过3个例子来说明一个概念：**图片就是颜色的点阵**。

<!-- TOC depthFrom:1 depthTo:6 withLinks:1 updateOnSave:1 orderedList:0 -->

- [图片处理初步](#图片处理初步)
	- [ARGB](#argb)
	- [拼图](#拼图)
	- [滤镜](#滤镜)
	- [画方](#画方)

<!-- /TOC -->

## ARGB

读取一张图片，展开它的颜色点阵：（1）宽高矩阵构成的二维数组；（2）数组每个元素拆分成ARGB值

``` java
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
				// ARGB: %x以十六进制显示
				if (count++ < 8) {
					System.out.println(String.format("[%d,%d]: %x => alpha: %x @(%x, %x, %x) ", x, y, color, alpha, r, g, b));
				}
			}
		}
	}

}

```

输出信息如下：

``` text
width: 873, height: 601
image format name: JPEG  // 常见的JPEG和PNG
[0,0]: ff2c56ac => alpha: ff @(2c, 56, ac)
[0,1]: ff2c56ac => alpha: ff @(2c, 56, ac)
[0,2]: ff2c56ac => alpha: ff @(2c, 56, ac)
[0,3]: ff2c56ac => alpha: ff @(2c, 56, ac)
[0,4]: ff2c56ac => alpha: ff @(2c, 56, ac)
[0,5]: ff2c56ac => alpha: ff @(2c, 56, ac)
[0,6]: ff2c56ac => alpha: ff @(2c, 56, ac)
[0,7]: ff2c56ac => alpha: ff @(2c, 56, ac)
```

``ARGB`` 依次代表透明度（alpha）、红色(red)、绿色(green)、蓝色(blue)。
以第1个点为例，``[0,0]: ff2c56ac``，ff表示透明度，2c表示红色值，56表示绿色值，ac表示蓝色值。每个通道都占1个字节。

透明度分为256阶（0-255），计算机上用16进制表示为（00-ff）。透明就是0阶，不透明就是255阶,如果50%透明就是127阶（256的一半当然是128，但因为是从0开始，所以实际上是127）。透明度与alpha取值之间的关系？透明是0，透明度是0%；不透明是0xFF，透明度是100%，比如 35%的透明度的alpha值=256*35%=89.6=0x59

以下代码把图片转化成透明的，并保存为png格式：

``` java
// 转成透明的：把alpha从0xFF设置为0x00
for (int x = 0; x < imageIn.getWidth(); x++) {
			for (int y = 0; y < imageIn.getHeight(); y++) {
				imageIn.setAlphaComponent(x, y, 0);
			}
}

// 注意：必须保存为png，不能为jpg。因为png才能保存alpha值
// jpg是不支持透明,可以考虑用bmp和png
MarvinImageIO.saveImage(imageIn, "target/argb-transparency.png");
```

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

## 画方

给定一张图片，需要在图片中画个矩形和线条。主要代码如下：

``` java
imageOut.drawRect(x, y, w, h, Color.RED); // 画一个矩形，线条粗细为1个像素
imageOut.drawRect(x + 10, y + 10, w, h, 5, Color.GREEN); // 画一个矩形，线条粗细为5个像素
imageOut.drawLine(x, y, x + w, y + h, Color.YELLOW); // 画一条线
```

值得说明一下，画一个粗线条的矩形，其实是重复画多个1像素的矩形来实现的。

``` java
public void drawRect(int x, int y, int w, int h, int thickness, Color c){
		for(int i=0; i<thickness; i++) { // 粗细为5像素，则画5个
			drawRect(x+i, y+i, w-(i*2), h-(i*2), c);
		}
	}
```

完整的代码：

``` java
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
```
