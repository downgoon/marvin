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
