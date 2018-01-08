package video.detectMotionRegions;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.marvinproject.image.difference.differentRegions.DifferentRegions;

import marvin.gui.MarvinImagePanel;
import marvin.image.MarvinImage;
import marvin.image.MarvinImageMask;
import marvin.plugin.MarvinImagePlugin;
import marvin.util.MarvinAttributes;
import marvin.video.MarvinJavaCVAdapter;
import marvin.video.MarvinVideoInterface;
import marvin.video.MarvinVideoInterfaceException;

/**
 * Detect the motion regions considering the difference between frames.
 * 
 * @author Gabriel Ambrosio Archanjo
 */
public class MotionDetectionCoupling extends JFrame implements Runnable {

	private static final long serialVersionUID = -1992437126562575898L;
	
	/* UI Components */
	private MarvinImagePanel videoImagePanel;
	
	private JPanel sensibilityPanel;
	private JSlider sensibilitySlider;
	private JLabel sensibilityLabel;
	private int sensibilityValue = 30;
	
	/* Backend Modules */
	private MarvinVideoInterface videoInterface;
	private int videoImageWidth, videoImageHeight;
	
	/* Motion Detection Modules*/
	private MarvinImagePlugin motionDetectPlugin;
	private Thread motionDetectThread;

	public MotionDetectionCoupling() {
		try {
			videoInterface = new MarvinJavaCVAdapter();
			videoInterface.connect(0); // cam on iOS

			videoImageWidth = videoInterface.getImageWidth();
			videoImageHeight = videoInterface.getImageHeight();
			
			loadGUI();
			
			motionDetectPlugin = new DifferentRegions();
			motionDetectPlugin.load();
			motionDetectThread = new Thread(this, "motion-detection");
			motionDetectThread.start();
			
		} catch (MarvinVideoInterfaceException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		MarvinImage filteredImage = new MarvinImage(videoImageWidth, videoImageHeight);
		MarvinImage lastFrame = null;
		
		try {
			while (true) {
				MarvinImage originalFrame = videoInterface.getFrame();
				MarvinImage.copyColorArray(originalFrame, filteredImage);

				if (lastFrame != null) { // 做运动识别处理
					
					motionDetectPlugin.setAttribute("comparisonImage", lastFrame);
					motionDetectPlugin.setAttribute("colorRange", sensibilityValue);
					MarvinAttributes attributesOut = new MarvinAttributes(null);
					motionDetectPlugin.process(originalFrame, filteredImage, attributesOut, MarvinImageMask.NULL_MASK, false);
					
					@SuppressWarnings("unchecked")
					Vector<int[]> motionRegions = (Vector<int[]>) attributesOut.get("regions");
					for (int i = 0; i < motionRegions.size(); i++) {
						int[] region = motionRegions.get(i);
						
						// (x1, y1) 是矩形的左上顶点；(x2, y2) 是矩形的右下顶点
						int x1 = region[0];
						int y1 = region[1];
						int x2 = region[2];
						int y2 = region[3];
						
						int width = x2 - x1;
						int height = y2 - y1;
						
						filteredImage.drawRect(x1, y1, // x, y 
								width,height, // w, h
								2, // 线条粗细为2个像素
								Color.GREEN);
						
					}
					
				} else {
					lastFrame = new MarvinImage(videoImageWidth, videoImageHeight);
				}

				// UI层 与 后端的交互点
				videoImagePanel.setImage(filteredImage);
				MarvinImage.copyColorArray(originalFrame, lastFrame);
				
			}
		} catch (MarvinVideoInterfaceException e) {
			e.printStackTrace();
		}
	}
	
	private void loadGUI() {
		/* video Panel*/
		videoImagePanel = new MarvinImagePanel();
		
		/* sensibility Panel */
		sensibilityLabel = new JLabel("灵敏度"); // Sensibility
		
		sensibilitySlider = new JSlider(JSlider.HORIZONTAL, 0, 60, 30);
		sensibilitySlider.setMinorTickSpacing(2);
		sensibilitySlider.setPaintTicks(true);
		sensibilitySlider.addChangeListener(new SliderHandler());

		sensibilityPanel = new JPanel();
		sensibilityPanel.add(sensibilityLabel);
		sensibilityPanel.add(sensibilitySlider);

		/* append into container of the jframe */
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		container.add(videoImagePanel, BorderLayout.NORTH);
		container.add(sensibilityPanel, BorderLayout.SOUTH);

		// JFrame Settings
		setTitle("运动检测");
		setSize(videoImageWidth + 10, videoImageHeight + 100);
		setVisible(true);
	}

	private class SliderHandler implements ChangeListener {
		public void stateChanged(ChangeEvent a_event) {
			sensibilityValue = (60 - sensibilitySlider.getValue());
		}
	}

	public static void main(String args[]) {
		MotionDetectionCoupling md = new MotionDetectionCoupling();
		md.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
