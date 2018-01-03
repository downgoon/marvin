package howto;

import marvin.image.MarvinImage;
import marvin.io.MarvinImageIO;
import marvinplugins.MarvinPluginCollection;

public class Resize {

	public static void main(String[] args) throws Exception {
		MarvinImage imageIn = MarvinImageIO.loadImage("src/test/resources/howto.jpg");
		MarvinImage imageOut = new MarvinImage();
		MarvinPluginCollection.scale(imageIn, imageOut, 400, 300);
		MarvinImageIO.saveImage(imageOut, "target/howto_resize.jpg");
	}
}
