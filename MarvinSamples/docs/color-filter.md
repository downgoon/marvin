# 颜色滤镜

<!-- TOC depthFrom:1 depthTo:6 withLinks:1 updateOnSave:1 orderedList:0 -->

- [颜色滤镜](#颜色滤镜)
	- [灰度滤镜](#灰度滤镜)
	- [黑白过滤](#黑白过滤)
	- [底片过滤](#底片过滤)
	- [浮雕效果](#浮雕效果)
	- [算法回顾](#算法回顾)
	- [官方样例](#官方样例)
		- [Gray](#gray)
		- [Invert](#invert)
		- [Sepia](#sepia)
	- [参考资料](#参考资料)

<!-- /TOC -->

## 灰度滤镜

**彩色照片**：每一个像素的颜色值由红、绿、蓝三种值混合而成，红绿蓝的取值分别由很多种，于是像素的颜色值也可以有很多种颜色值，这就是彩色图片的原理。

**灰度照片**：将图片颜色值的 **RGB三个通道值设为一样**。丢失掉色彩，留下亮度。以前RGB的256*256*256种颜色就只有256种了。

灰度处理一般有三种算法：
1. 最大值法：即新的颜色值R＝G＝B＝Max(R，G，B)，这种方法处理后的图片看起来亮度值偏高。
2. 平均值法：即新的颜色值R＝G＝B＝(R＋G＋B)／3，这样处理的图片十分柔和
3. 加权平均值法：即新的颜色值R＝G＝B＝(R ＊ Wr＋G＊Wg＋B＊Wb)，一般由于 **人眼对不同颜色的敏感度不一样，所以三种颜色值的权重不一样**，一般来说绿色最高，红色其次，蓝色最低，最合理的取值分别为Wr ＝ 30％，Wg ＝ 59％，Wb ＝ 11％

``` java
package basic;

import marvin.image.MarvinImage;
import marvin.io.MarvinImageIO;

public class GrayFilter {

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

				// int finalColor = (int)((r + g + b) / 3); // 平均值算法
				// int finalColor = Math.max(r, Math.max(g, b)); // 最大值算法
				int finalColor = (int)((r*0.3)+(g*0.59)+(b*0.11)); // 加权平均值算法

				// (x, y) <= alpha, c0, c1, c2
				imageOut.setIntColor(x,y,alpha, finalColor,finalColor,finalColor);
			}
		}

		MarvinImageIO.saveImage(imageOut, "target/howto-gray.png");
	}

}
```

## 黑白过滤

灰度有256种颜色，而黑白则是只保留黑和白这 **两种** 颜色。
黑白图片的处理算法更简单：
求RGB平均值Avg ＝ (R + G + B) / 3，如果Avg >= 100，则新的颜色值为R＝G＝B＝255；如果Avg < 100，则新的颜色值为R＝G＝B＝0；255就是白色，0就是黑色；至于为什么用100作比较，这是一个经验值吧。

``` java
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
				finalColor = finalColor > 100 ? 255 : 0; // 只有两种颜色
				// (x, y) <= alpha, c0, c1, c2
				imageOut.setIntColor(x,y,alpha, finalColor,finalColor,finalColor);
			}
		}

		MarvinImageIO.saveImage(imageOut, "target/howto-bw.png");
	}

}
```

## 底片过滤

修改后的素点的RGB值 = 255 - 当前点的RGB值。即：
R = 255 – R；G = 255 – G；B = 255 – B；

``` java
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

```

## 浮雕效果

浮雕算法：``新颜色 = (当前像素.RGB - 相邻像素.RGB) + 128``
原理：因为相邻的两个像素颜色通常是比较接近的，用浮雕算法处理后，只有色差比较大的，才会凸显；色差接近的，则会接近128，也就是灰色。

在实际的效果中，这样处理后，有些区域可能还是会有”彩色”的一些点或者条状痕迹，所以最好再对新的RGB值做一个灰度处理。

``` java
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
```

## 算法回顾

- 灰度算法

``` java
int finalColor = (int)((r + g + b) / 3); // 平均值算法
int finalColor = Math.max(r, Math.max(g, b)); // 最大值算法
int finalColor = (int)((r*0.3)+(g*0.59)+(b*0.11)); // 加权平均值算法
```

- 黑白算法

``` java
int finalColor = (int)((r + g + b) / 3); // 平均值算法
finalColor = finalColor > 100 ? 255 : 0; // 只有两种颜色
```

- 底片滤镜

``` java
for (int x = 0; x < imageIn.getWidth(); x++) {
  for (int y = 0; y < imageIn.getHeight(); y++) {
    int alpha = imageIn.getAlphaComponent(x, y); // alpha
    int r = imageIn.getIntComponent0(x, y); // c0
    int g = imageIn.getIntComponent1(x, y); // c1
    int b = imageIn.getIntComponent2(x, y); // c2
    imageOut.setIntColor(x,y,alpha, 255-r,255-g,255-b);
  }
}
```

- 浮雕算法

```
浮雕算法：新颜色 = (当前像素.RGB - 相邻像素.RGB) + 128
```

------

## 官方样例

官方的滤镜样例代码 [Filters.java](MarvinSamples/src/main/java/image/filters/Filters.java)，里面提到3种滤镜：Gray, Invert 和

### Gray

``` java
public class GrayScale extends MarvinAbstractImagePlugin {
	public void process
	(
		MarvinImage imageIn,
		MarvinImage imageOut,
		MarvinAttributes attributesOut,
		MarvinImageMask mask,
		boolean previewMode
	) {

		for (int x = 0; x < imageIn.getWidth(); x++) {
			for (int y = 0; y < imageIn.getHeight(); y++) {
				r = imageIn.getIntComponent0(x, y);
				g = imageIn.getIntComponent1(x, y);
				b = imageIn.getIntComponent2(x, y);
				finalColor = (int)((r*0.3)+(g*0.59)+(b*0.11));
				imageOut.setIntColor(x,y,imageIn.getAlphaComponent(x, y), finalColor,finalColor,finalColor);
			}
		}
	}
}
```

----

### Invert

``` java
public class Invert extends MarvinAbstractImagePlugin {
	public void process
	(
		MarvinImage imageIn,
		MarvinImage imageOut,
		MarvinAttributes attributesOut,
		MarvinImageMask mask,
		boolean previewMode
	) {

		for (int x = 0; x < imageIn.getWidth(); x++) {
			for (int y = 0; y < imageIn.getHeight(); y++) {

				r = (255-(int)imageIn.getIntComponent0(x, y));
				g = (255-(int)imageIn.getIntComponent1(x, y));
				b = (255-(int)imageIn.getIntComponent2(x, y));

				imageOut.setIntColor(x,y,imageIn.getAlphaComponent(x, y), r,g,b);
			}
		}

	}
}
```

### Sepia

``` java
public class Sepia extends MarvinAbstractImagePlugin implements ChangeListener, KeyListener{
	public void process
	(
		MarvinImage imageIn,
		MarvinImage imageOut,
		MarvinAttributes attributesOut,
		MarvinImageMask mask,
		boolean previewMode
	)
	{
			depth = Integer.parseInt(attributes.get("intensity").toString());

			for (int x = 0; x < imageIn.getWidth(); x++) {
			for (int y = 0; y < imageIn.getHeight(); y++) {

				//Captura o RGB do ponto...
				r = imageIn.getIntComponent0(x, y);
				g = imageIn.getIntComponent1(x, y);
				b = imageIn.getIntComponent2(x, y);

				//Define a cor como a m�dia aritm�tica do pixel...
				corfinal = (r + g + b) / 3;
				r = g = b = corfinal;

				r = truncate(r + (depth * 2));
				g = truncate(g + depth);

				imageOut.setIntColor(x, y, imageIn.getAlphaComponent(x, y), r, g, b);
			}
		}


	}
}

public int truncate(int a) {
		if      (a <   0) return 0;
		else if (a > 255) return 255;
		else              return a;
	}

```

## 参考资料

- [图像滤镜处理算法：灰度、黑白、底片、浮雕](http://blog.csdn.net/fishmai/article/details/52710681)
