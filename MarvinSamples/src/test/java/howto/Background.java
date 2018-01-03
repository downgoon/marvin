package howto;

import java.awt.Color;

import marvin.image.MarvinImage;
import marvin.io.MarvinImageIO;
import marvinplugins.MarvinPluginCollection;

public class Background {

	public static void main(String[] args) throws Exception {
		MarvinImage imageIn = MarvinImageIO.loadImage("src/test/resources/howto.jpg");
		// MarvinImage imageOut = new MarvinImage();
		MarvinImage imageOut = imageIn.clone();
		MarvinPluginCollection.boundaryFill(imageIn, imageOut, 0, 0, Color.RED);
		imageOut.setAlphaToColor(0);
		MarvinPluginCollection.alphaBoundary(imageOut, 5);
		MarvinImageIO.saveImage(imageOut, "target/howto_background.jpg");
	}
}
