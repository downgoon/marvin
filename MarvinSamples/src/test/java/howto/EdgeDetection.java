package howto;

import marvin.image.MarvinImage;
import marvin.io.MarvinImageIO;
import marvinplugins.MarvinPluginCollection;

public class EdgeDetection {

	public static void main(String[] args) throws Exception {
		MarvinImage imageIn = MarvinImageIO.loadImage("src/test/resources/howto.jpg");
		MarvinImage imageOut1 = new MarvinImage(imageIn.getWidth(), imageIn.getHeight());
		MarvinPluginCollection.prewitt(imageIn, imageOut1);
		MarvinImageIO.saveImage(imageOut1, "target/howto_edge_prewitt.jpg");
		
		
		MarvinImage imageOut2 = new MarvinImage(imageIn.getWidth(), imageIn.getHeight());
		MarvinPluginCollection.sobel(imageIn, imageOut2);
		MarvinImageIO.saveImage(imageOut2, "target/howto_edge_sobel.jpg");
		
		
		MarvinImage imageOut3 = new MarvinImage(imageIn.getWidth(), imageIn.getHeight());
		MarvinPluginCollection.roberts(imageIn, imageOut3);
		MarvinImageIO.saveImage(imageOut3, "target/howto_edge_roberts.jpg");
		
		System.out.println("edge detection fully finish");
	}
}
