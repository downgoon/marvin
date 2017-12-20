/**
Marvin Project <2007-2013>
http://www.marvinproject.org

License information:
http://marvinproject.sourceforge.net/en/license.html

Discussion group:
https://groups.google.com/forum/#!forum/marvin-project
*/

package org.marvinproject.image.statistical.minimum;

import marvin.gui.MarvinAttributesPanel;
import marvin.gui.MarvinFilterWindow;
import marvin.image.MarvinImage;
import marvin.image.MarvinImageMask;
import marvin.performance.MarvinPerformanceMeter;
import marvin.plugin.MarvinAbstractImagePlugin;
import marvin.util.MarvinAttributes;

/** 
 * @author Fabio Andrijauskas
 */
public class Minimum extends MarvinAbstractImagePlugin {

	private MarvinAttributesPanel	attributesPanel;
	MarvinAttributes 				attributes;
	MarvinPerformanceMeter 			performanceMeter;

	public void load() {
		attributes = getAttributes();
		attributes.set("size", 3);
		//attributes.set("shift", 0);
		//attributes.set("circlesDistance", 0);
		performanceMeter = new MarvinPerformanceMeter();
	}

	public void process
	(
		MarvinImage a_imageIn, 
		MarvinImage a_imageOut,
		MarvinAttributes a_attributesOut,
		MarvinImageMask a_mask, 
		boolean a_previewMode
	)
	{
		performanceMeter.start("Minimum Filter");


		int l_size = (Integer)attributes.get("size");
		int l_totalRed = 0;
		int l_totalGreen = 0;
		int l_totalBlue = 0;
		int qtd = 0;
		int tmpx = 0;
		int tmpy = 0;
		int width = a_imageIn.getWidth();
		int height = a_imageIn.getHeight();

		performanceMeter.enableProgressBar("Minimum Filter" ,a_imageIn.getWidth() *  a_imageIn.getHeight() );
		performanceMeter.startEvent("Minimum Filter");
		
		boolean[][] l_arrMask = a_mask.getMask();

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if(l_arrMask != null && !l_arrMask[x][y]){
					continue;
				}

				l_totalRed = 255;
				l_totalGreen = 255;
				l_totalBlue = 255;
				tmpx  = x - l_size;
				tmpy  = y - l_size;

				if(tmpx < 0)
					tmpx = 0;
				if(tmpy < 0)
					tmpy = 0;

				int finalX =  x + l_size;

				int finalY = y + l_size;

				if(finalX > width)
					finalX = width;

				if(finalY > height)
					finalY = height;

				for (int xm = tmpx; xm < finalX ; xm++) {
					for (int ym = tmpy; ym <  y + l_size ; ym++) {

						if(xm >= 0 && xm < width && ym >= 0 && ym < height  )
						{
							int rgb = a_imageIn.getIntColor(xm, ym);

							int r = (int)(rgb & 0x00FF0000) >>> 16;
				if(r < l_totalRed)
				{
					l_totalRed = r;	
				}


				int g = (int) ((rgb & 0x0000FF00) >>> 8);
				if( g < l_totalGreen)
				{
					l_totalGreen = g;	
				}


				int b = (int) (rgb & 0x000000FF);
				if(  b < l_totalBlue )
				{
					l_totalBlue = b;	
				}

						}
					}
				}

				a_imageOut.setIntColor(x, y, a_imageIn.getAlphaComponent(x, y), l_totalRed, l_totalGreen, l_totalBlue);

				l_totalRed = 0;
				l_totalGreen = 0;
				l_totalBlue = 0;
			}
			performanceMeter.incProgressBar(height);
			performanceMeter.stepsFinished(height);
		}
		performanceMeter.finishEvent();
		performanceMeter.finish();
	}

	public MarvinAttributesPanel getAttributesPanel(){
		if(attributesPanel == null){
			attributesPanel = new MarvinAttributesPanel();
			attributesPanel.addLabel("lblWidth", "Size:");
			attributesPanel.addTextField("txtSize", "size", attributes);
			attributesPanel.newComponentRow();
		}
		return attributesPanel;
	}
}
