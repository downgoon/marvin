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
