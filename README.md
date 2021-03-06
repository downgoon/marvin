# Marvin Image Processing Framework

**Java Image Processing Framework** forked from  [gabrielarchanjo/marvinproject](https://github.com/gabrielarchanjo/marvinproject)

Pure Java cross-platform image processing framework that provides features for image and video frame processing, multi-threading image processing, GUI integration, extensibility via plug-ins, unit text automation among other things.

**WEBSITE:**
http://marvinproject.sourceforge.net/

## Maven Dependency

``` xml
<dependency>
  <groupId>com.github.downgoon</groupId>
  <artifactId>MarvinPlugins</artifactId>
  <version>1.5.5</version>
</dependency>

<dependency>
  <groupId>com.github.downgoon</groupId>
  <artifactId>MarvinFramework</artifactId>
  <version>1.5.5</version>
</dependency>
```

## QuickStart

- clone and import into IDE

``` bash
git clone https://github.com/downgoon/marvin
```

Dependency Tree: ``MarvinSamples`` => ``MarvinPlugins`` => ``MarvinFramework`` => ``JavaCV``

- [image-basic](MarvinSamples/docs/image-basic.md): 图片处理初步之拼图和颜色滤镜
- [color-filter](MarvinSamples/docs/color-filter.md): 常见滤镜之灰度，黑白，底片和浮雕
- [howto](MarvinSamples/docs/howto.md): ``how to`` step-by-step showcases
- [showcase](MarvinSamples/docs/showcase.md): user-story showcases
- [算法与插件](http://marvinproject.sourceforge.net/en/plugins.html)
  - [motion-detection](MarvinSamples/docs/motion-detection.md): 运动检测及其[DifferentRegions](MarvinSamples/docs/DifferentRegions.md)算法解读
