/**
Marvin Project <2007-2013>
http://www.marvinproject.org

License information:
http://marvinproject.sourceforge.net/en/license.html

Discussion group:
https://groups.google.com/forum/#!forum/marvin-project
*/

package org.marvinproject.image.difference.differentRegions;

import java.util.Vector;

import marvin.gui.MarvinAttributesPanel;
import marvin.image.MarvinImage;
import marvin.image.MarvinImageMask;
import marvin.plugin.MarvinAbstractImagePlugin;
import marvin.util.MarvinAttributes;

/**
 * Find the different regions between two images.
 * 
 * @author Gabriel Ambrosio Archanjo
 */
public class DifferentRegionsComments extends MarvinAbstractImagePlugin {

	private MarvinAttributes attributes;
	private MarvinImage comparisonImage;

	private int[][] arrPixelsPerSubRegion;
	private boolean[][] arrRegionMask;

	private int width;
	private int height;
	
	/** 色差阈值：前后两张图片在同一个点，如果色差大于阈值，则计为运动点 */
	private int colorRange = 30;
	
	/* 区域粒度：将一张图片，按10像素*10像素 的方框去切割 */
	private int subRegionSide = 10;
	
	private boolean initialized = false;

	public void load() {
		attributes = getAttributes();
		attributes.set("colorRange", 30);
	}

	public MarvinAttributesPanel getAttributesPanel() {
		return null;
	}

	public void process(MarvinImage a_imageIn, MarvinImage a_imageOut, MarvinAttributes a_attributesOut,
			MarvinImageMask a_mask, boolean a_previewMode) {
		int l_redA, l_redB, l_greenA, l_greenB, l_blueA, l_blueB;

		comparisonImage = (MarvinImage) attributes.get("comparisonImage");
		colorRange = (Integer) attributes.get("colorRange");
		width = a_imageIn.getWidth();
		height = a_imageIn.getHeight();

		if (!initialized) {
			// 10*10的像素方块定义为一个区域，它的取值表示这个区域内运动点的强度
			arrPixelsPerSubRegion = new int[width / subRegionSide][height / subRegionSide];
			// 区域状态标记：false表示未被标记，true表示被标记。被标记表示这个点运动了。
			arrRegionMask = new boolean[width / subRegionSide][height / subRegionSide];
			initialized = true;
		}

		clearRegions();

		for (int y = 0; y < a_imageIn.getHeight(); y++) {
			for (int x = 0; x < a_imageIn.getWidth(); x++) {

				// 当前图片: A点
				l_redA = a_imageIn.getIntComponent0(x, y);
				l_greenA = a_imageIn.getIntComponent1(x, y);
				l_blueA = a_imageIn.getIntComponent2(x, y);

				// 比对图片：A'点
				l_redB = comparisonImage.getIntComponent0(x, y);
				l_greenB = comparisonImage.getIntComponent1(x, y);
				l_blueB = comparisonImage.getIntComponent2(x, y);

				/* 
				 * 前后两张图片，在同一个点，的色差：如果大于色差阈值，则标记为发现运动像素。
				 * 对于运动像素，计数到它所在的区域。
				 * 像素P(x,y)，它所属的区域是R(x / subRegionSide, y / subRegionSide) 。
				 */
				if (Math.abs(l_redA - l_redB) > colorRange || Math.abs(l_greenA - l_greenB) > colorRange
						|| Math.abs(l_blueA - l_blueB) > colorRange) {
					// arrPixelsPerSubRegion 区域运动计数器（区域内运动像素的数量）
					arrPixelsPerSubRegion[x / subRegionSide][y / subRegionSide]++;
				}
			}
		}

		// 中间结果 arrPixelsPerSubRegion 区域计数器记录了每个区域的运动像素个数
		
		Vector<int[]> l_vecRegions = new Vector<int[]>();
		int[] l_rect;

		while (true) {
			l_rect = new int[4];
			l_rect[0] = -1;

			JoinRegions(l_rect);
			if (l_rect[0] != -1) {
				l_vecRegions.add(l_rect);
			} else {
				break;
			}
		}
		a_attributesOut.set("regions", l_vecRegions);
	}

	private boolean JoinRegions(int[] a_rect) {
		for (int rx = 0; rx < width / subRegionSide; rx++) {
			for (int ry = 0; ry < height / subRegionSide; ry++) {
				/*
				 * 标记运动区域：arrRegionMask标记区域是否为运动区域
				 * 如果一个区域内超过半数的点是运动点，那么这个区域就标记为运动区域
				 * */
				if (arrPixelsPerSubRegion[rx][ry] > (subRegionSide * subRegionSide / 2) && !arrRegionMask[rx][ry]) {
					arrRegionMask[rx][ry] = true; // 标记为运动区域
					a_rect[0] = rx * subRegionSide;
					a_rect[1] = ry * subRegionSide;
					a_rect[2] = rx * subRegionSide;
					a_rect[3] = ry * subRegionSide;
					
					// 每当标记一个运动区域时，立即找出它的邻居区域也是运动的，但是尚未标记的
					testNeighbors(a_rect, rx, ry);
					return true;
				}
			}
		}
		return false;
	}

	private void testNeighbors(int[] a_rect, int rx, int ry) {
		for (int nrx = rx - 5; nrx < rx + 5; nrx++) {
			for (int nry = ry - 5; nry < ry + 5; nry++) {
				// 邻居区域：某个指定区域的纵横5个区域。也叫区域的邻居集。
				if ((nrx > 0 && nrx < width / subRegionSide) && (nry > 0 && nry < height / subRegionSide)) {
					// 对于邻居区域是运动的，但是却没有被标记的
					if (arrPixelsPerSubRegion[nrx][nry] > (subRegionSide * subRegionSide / 2) && !arrRegionMask[nrx][nry]) {
						
						// 跟邻居对比，左顶点往左靠
						if (nrx * subRegionSide < a_rect[0]) { // rect.x1
							a_rect[0] = nrx * subRegionSide;
						}
						if (nry * subRegionSide < a_rect[1]) { // rect.y1
							a_rect[1] = nry * subRegionSide;
						}
						
						// 跟邻居对比，右底点往右靠
						if (nrx * subRegionSide > a_rect[2]) { // rect.x2
							a_rect[2] = nrx * subRegionSide;
						}
						if (nry * subRegionSide > a_rect[3]) { // rect.y2
							a_rect[3] = nry * subRegionSide;
						}

						// 把邻居区域标记为运动区域
						arrRegionMask[nrx][nry] = true;
						
						// 每当标记一个运动区域时，立即找出它的邻居区域也是运动的，但是尚未标记的
						testNeighbors(a_rect, nrx, nry);
					}
				}
			}
		}
	}

	private void clearRegions() {
		for (int x = 0; x < width / subRegionSide; x++) {
			for (int y = 0; y < height / subRegionSide; y++) {
				arrPixelsPerSubRegion[x][y] = 0;
				arrRegionMask[x][y] = false;
			}
		}
	}
}
