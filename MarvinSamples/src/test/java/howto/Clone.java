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
