package howto;

import marvin.image.MarvinImage;
import marvin.io.MarvinImageIO;
import marvin.video.MarvinJavaCVAdapter;
import marvin.video.MarvinVideoInterface;
import marvin.video.MarvinVideoInterfaceException;

public class WebcamPicture {

	public static void main(String[] args) {
		try {
			MarvinVideoInterface videoAdapter = new MarvinJavaCVAdapter();
			videoAdapter.connect(0); // deviceIndex
			MarvinImage image = videoAdapter.getFrame();
			MarvinImageIO.saveImage(image, "target/webcam_picture.jpg");
		} catch (MarvinVideoInterfaceException e) {
			e.printStackTrace();
		}
	}

}
