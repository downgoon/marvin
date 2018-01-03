package howto;

import static marvinplugins.MarvinPluginCollection.scale;

import marvin.image.MarvinImage;
import marvin.io.MarvinImageIO;

public class ResizeStatic {

	public static void main(String[] args) {
		MarvinImage imageIn = MarvinImageIO.loadImage("src/test/resources/howto.jpg");
		MarvinImage imageOut = new MarvinImage();
		scale(imageIn, imageOut, 400, 300);
		MarvinImageIO.saveImage(imageOut, "target/howto_resize_static.jpg");
	}

}
