# showcase 系列

前面 [howto系列](howto.md) 主要演示图片处理的基本操作。比如：缩放、裁剪、背景填充、缩略图和边缘识别等。接下来 ``showcase系列``主要演示``用户故事``级的功能。比如：实时滤镜，文本OCR等。官方教程地址是[examples.html](http://marvinproject.sourceforge.net/en/examples.html)。

<!-- TOC depthFrom:1 depthTo:6 withLinks:1 updateOnSave:1 orderedList:0 -->

- [showcase 系列](#showcase-系列)
	- [Filters 滤镜](#filters-滤镜)
	- [Motion Detection 运动检测](#motion-detection-运动检测)

<!-- /TOC -->

## Filters 滤镜

滤镜两个例子：一个是图片滤镜 [Filters.java](../src/main/java/image/filters/Filters.java)，另一个是视频实时滤镜 [VideoFilters.java](../src/main/java/video/videoFilters/VideoFilters.java)。

- 图片滤镜：官方教程地址是[examples/filters.html](http://marvinproject.sourceforge.net/en/examples/filters.html)
- 视频滤镜：官方教程地址是[examples/videoFilters.html](http://marvinproject.sourceforge.net/en/examples/videoFilters.html)

## Motion Detection 运动检测

摄像头监控的应用场景中，深夜时间，如果出现运动的物体在镜头中，就可以消息推送给订阅人。官方教程地址是[examples/motionRegions.html](http://marvinproject.sourceforge.net/en/examples/motionRegions.html)，代码是：[DetectMotionRegions.java](../src/main/java/video/detectMotionRegions/DetectMotionRegions.java)。

>This application detects the regions of image that has **object in motion**, considering a sequence of video frames. >The principle is simple:

>- **find the distinct pixels** of the current frame (time t) compared with the frame at t-1.
>- **separate the pixels in clusters** by analysing their neighborhood.
>- **return a rectangle for each cluster**, in other words, region with an object in motion.

``Motion Detection``的原理比较简单：
- 找出不同像素：从视频中抽取若干帧，用当前帧与上一帧对比，找出不同的像素。
- 将像素分组：通过分析他们的邻近关系，将这些像素分成若干组。
- 框出每组的运动区：每个组里面都有一个运动区，对这个运动区用矩形图框出来。
