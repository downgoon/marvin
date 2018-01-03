# howto系列

``howto``系列跟着[官方网站的howto](http://marvinproject.sourceforge.net/en/howto.html)学习。

<!-- TOC depthFrom:1 depthTo:6 withLinks:1 updateOnSave:1 orderedList:0 -->

- [howto系列](#howto系列)
	- [依赖库](#依赖库)
	- [图片复制](#图片复制)
	- [Resize/Scale 缩放](#resizescale-缩放)
	- [Crop 裁剪](#crop-裁剪)
	- [Thumbnail 缩略图](#thumbnail-缩略图)

<!-- /TOC -->

## 依赖库

``` xml
<dependency>
	<groupId>com.github.downgoon</groupId>
	<artifactId>MarvinPlugins</artifactId>
	<version>1.5.5-SNAPSHOT</version>
</dependency>

<dependency>
	<groupId>com.github.downgoon</groupId>
	<artifactId>MarvinFramework</artifactId>
	<version>1.5.5-SNAPSHOT</version>
</dependency>
```

## 图片复制

输入一张图片，输出一张相同的图片。需要用到``MarvinImage``表示一张图片，用``MarvinImageIO``做IO操作。代码见[Clone.java](../src/test/java/howto/Clone.java)：

``` java
package howto;

import marvin.image.MarvinImage;
import marvin.io.MarvinImageIO;

public class Clone {
	public static void main(String[] args) throws Exception {
		MarvinImage imageIn = MarvinImageIO.loadImage("src/test/resources/howto.jpg");
		MarvinImage imageOut = imageIn.clone();
		MarvinImageIO.saveImage(imageOut, "target/howto_clone.jpg");
	}
}
```

## Resize/Scale 缩放

把一张大的图片，缩放成一张小的图片。需要用到``scale``插件。为了方便管理``MarvinImagePlugin``，项目专门定义``MarvinPluginCollection``类集中管理插件。代码见[Resize.java](../src/test/java/howto/Resize.java)：

``` java
package howto;

import marvin.image.MarvinImage;
import marvin.io.MarvinImageIO;
import marvinplugins.MarvinPluginCollection;

public class Resize {

	public static void main(String[] args) throws Exception {
		MarvinImage imageIn = MarvinImageIO.loadImage("src/test/resources/howto.jpg");
		MarvinImage imageOut = new MarvinImage();
    // 借助Scale插件实现图片缩放
		MarvinPluginCollection.scale(imageIn, imageOut, 400, 300);
		MarvinImageIO.saveImage(imageOut, "target/howto_resize.jpg");
	}
}
```

官网对于静态类的静态方法的导入，换了一种叫``direct call``的方式：

``` java
// import static 方法
import static marvinplugins.MarvinPluginCollection.scale;

// 程序中直接调用静态方法，无需关联静态类
scale(imageIn, imageOut, 400, 300);
```

完整的代码见：[ResizeStatic.java](../src/test/java/howto/ResizeStatic.java)。

## Crop 裁剪

指定图片的起始点(x,y)坐标，指定宽度和高度，裁剪图片。程序中``352, 215, 212, 62``数字是用QQ截图软件手工比划得出的，输出的结果是裁剪出输入图片中人物的眼神部位。代码见[Crop.java](../src/test/java/howto/Crop.java)。

``` java
package howto;

import marvin.image.MarvinImage;
import marvin.io.MarvinImageIO;
import marvinplugins.MarvinPluginCollection;

public class Crop {

	public static void main(String[] args) throws Exception {
		MarvinImage imageIn = MarvinImageIO.loadImage("src/test/resources/howto.jpg");
		MarvinImage imageOut = new MarvinImage();
		// crop the position of eyes
		MarvinPluginCollection.crop(imageIn, imageOut, 352, 215, 212, 62);
		MarvinImageIO.saveImage(imageOut, "target/howto_crop.jpg");
	}
}
```

## Thumbnail 缩略图

- 关键代码

``` java
MarvinPluginCollection.thumbnailByWidth(imageIn, imageOut, 150);
MarvinPluginCollection.thumbnailByHeight(imageIn, imageOut, 80);
```

- 完整代码：[Thumbnail.java](../src/test/java/howto/Thumbnail.java)

``` java
package howto;

import marvin.image.MarvinImage;
import marvin.io.MarvinImageIO;
import marvinplugins.MarvinPluginCollection;

public class Thumbnail {

	public static void main(String[] args) {
		MarvinImage imageIn = MarvinImageIO.loadImage("src/test/resources/howto.jpg");
		MarvinImage imageOut = new MarvinImage();
		MarvinPluginCollection.scale(imageIn, imageOut, 400, 300);
		MarvinPluginCollection.thumbnailByWidth(imageIn, imageOut, 150);
		MarvinImageIO.saveImage(imageOut, "target/howto_thumbnail_w150.jpg");

		MarvinPluginCollection.thumbnailByHeight(imageIn, imageOut, 80);
		MarvinImageIO.saveImage(imageOut, "target/howto_thumbnail_h80.jpg");
	}

}
```
