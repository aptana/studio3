/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.test.performance.ui;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

public class BarGraph {

	private static final int MARGIN= 5; // margin on all four sides
	private static final int BARHEIGHT= 8; // height of bar
	private static final int GAP= 10; // gap between bars
	private static final int TGAP= 5; // gap between lines and labels

	private static final boolean NO_SCALE= true; //

	// if NO_SCALE is true the following values are used:
	private static final double RATIO= 0.6; // fraction of width reserved for bar graph; needs tweaking
	private static final int FROM_END= 20; // a break (//) is shown this far from end of bar
	private static final int SLANT= 8; // slant of break
	private static final int GAP2= 5; // width of break

	private StringBuffer fAreaBuffer;

	private static class BarItem {

		String title;
		double value;
		String url;
		String slowdownExpected;
		boolean significant;

		BarItem(String t, double[] stats, String u, String slow, boolean sig) {
			this.title= t;
			this.value= stats[0]==0 ? 0 : -stats[0] * 100;
			this.url= u;
			this.slowdownExpected= slow;
			this.significant= sig;
		}
	}

	private String fTitle;
	private List fItems;

	BarGraph(String title) {
		this.fTitle= title;
		this.fItems= new ArrayList();
	}

	public void addItem(String name, double[] stats, String url, String slow, boolean significant) {
		this.fItems.add(new BarItem(name, stats, url, slow, significant));
	}

	public int getHeight() {
		int n= this.fItems.size();
		int textHeight= 16;
		int titleHeight= 0;
		if (this.fTitle != null)
			titleHeight= textHeight + GAP;
		return MARGIN + titleHeight + n * (GAP + BARHEIGHT) + GAP + textHeight + MARGIN;
	}

	public void paint(Display display, int width, int height, GC gc) {

		NumberFormat nf= NumberFormat.getInstance();

		BarItem[] bars= (BarItem[]) this.fItems.toArray(new BarItem[this.fItems.size()]);

		// draw white background
		Color bg= display.getSystemColor(SWT.COLOR_WHITE);
		gc.setBackground(bg);
		gc.fillRectangle(0, 0, width, height);

		// determine the widths of the bar and the label areas
		int w;
		if (NO_SCALE) {
			// we use a fixed width
			w= (int) (RATIO * width);
		} else {
			// we calculate the max width
			int maxNameLength= 0;
			for (int i= 0; i < bars.length; i++) {
				Point es= gc.stringExtent(bars[i].title);
				maxNameLength= Math.max(maxNameLength, es.x);
			}
			w= width - maxNameLength - TGAP - 2 * MARGIN;
		}

		Color fg= display.getSystemColor(SWT.COLOR_BLACK);

		int vstart= 0; // start rows here
		if (this.fTitle != null) {
			vstart= gc.stringExtent(this.fTitle).y + GAP;
			gc.drawString(this.fTitle, MARGIN, MARGIN, true); // draw title left aligned
		}

		int center= MARGIN + w / 2;
		int w2= w / 2 - gc.stringExtent("-999.9").x - TGAP; // reserve space //$NON-NLS-1$

		// determine maximum of values
		double max= 0.0;
		for (int i= 0; i < bars.length; i++)
			max= Math.max(max, Math.abs(bars[i].value));

		double d;
		if (NO_SCALE) {
			d= 25;
			max= 125;
		} else {
			if (max > 400.0) {
				d= 200;
			} else if (max > 200.0) {
				d= 100;
			} else if (max > 100.0) {
				d= 50;
			} else if (max > 50) {
				d= 25;
			} else if (max > 25) {
				d= 10;
			} else if (max > 10) {
				d= 5;
			} else if (max > 5) {
				d= 2.5;
			} else {
				d= 1.0;
			}
		}

		// draw striped background
		int y= MARGIN + vstart;
		Color lightblue= new Color(display, 237, 243, 254);
		gc.setBackground(lightblue);
		for (int i= 0; i < bars.length; i++)
			if (i % 2 == 0)
				gc.fillRectangle(0, y + i * (BARHEIGHT + GAP), width, BARHEIGHT + GAP);

		// draw grid
		int yy= y + bars.length * (BARHEIGHT + GAP);
		gc.drawLine(center, y, center, yy + TGAP);
		Color grey= display.getSystemColor(SWT.COLOR_GRAY);
		for (int i= 1; d * i < max; i++) {

			double xx= d * i;
			int x= (int) ((xx / max) * w2);

			gc.setForeground(grey);
			gc.drawLine(center - x, y, center - x, yy + TGAP);
			gc.drawLine(center + x, y, center + x, yy + TGAP);

			gc.setForeground(fg);

			String s3= nf.format(-xx) + "%"; //$NON-NLS-1$
			Point es3= gc.stringExtent(s3);
			gc.drawString(s3, center - x - es3.x / 2, yy + TGAP, true);

			String s4= nf.format(xx) + "%"; //$NON-NLS-1$
			Point es4= gc.stringExtent(s4);
			gc.drawString(s4, center + x - es4.x / 2, yy + TGAP, true);
		}
		gc.drawLine(0, yy, w, yy);

		nf.setMaximumFractionDigits(1);

		// link color
		Color blue= display.getSystemColor(SWT.COLOR_BLUE);
		// draw bars
//		Color green= display.getSystemColor(SWT.COLOR_GREEN);
//		Color red= display.getSystemColor(SWT.COLOR_RED);
		Color green = new Color(display, 95, 191, 95);
		Color red = new Color(display, 225, 50, 50);
		Color gray= display.getSystemColor(SWT.COLOR_GRAY);
		Color yellow= display.getSystemColor(SWT.COLOR_YELLOW);
		Color white= display.getSystemColor(SWT.COLOR_WHITE);
		for (int i= 0; i < bars.length; i++) {

			BarItem bar= bars[i];
			double delta = bar.value;
			double orgDelta= delta;

			boolean clamped= false;
			if (NO_SCALE) {
				if (delta > max) {
					delta= max;
					clamped= true;
				} else if (delta < -max) {
					delta= -max;
					clamped= true;
				}
			}

			int barLength= (int) (delta / max * w2);

			if (delta < 0) {
				if (bar.slowdownExpected != null) {
					gc.setBackground(gray);
				} else if (!bar.significant) {
					gc.setBackground(yellow);
				} else  {
					gc.setBackground(red);
				}
			} else if (!bar.significant) {
				gc.setBackground(yellow);
			} else {
				gc.setBackground(green);
			}

			if (barLength > 0) {
				gc.fillRectangle(center, y + (GAP / 2), barLength, BARHEIGHT);
				gc.drawRectangle(center, y + (GAP / 2), barLength, BARHEIGHT);
			} else if (barLength < 0) {
				gc.fillRectangle(center+barLength, y + (GAP / 2), -barLength, BARHEIGHT);
				gc.drawRectangle(center+barLength, y + (GAP / 2), -barLength, BARHEIGHT);
			}

			if (clamped) {

				int h2= (BARHEIGHT + GAP);
				int x= center + barLength;
				if (barLength > 0)
					x-= FROM_END;
				else
					x+= FROM_END - GAP2 - SLANT;
				int[] pts= new int[] { x, y + h2 - 1, x + SLANT, y + 1, x + SLANT + GAP2, y + 1, x + GAP2, y + h2 - 1};
				if (i % 2 == 0)
					gc.setBackground(lightblue);
				else
					gc.setBackground(white);
				gc.fillPolygon(pts);
				gc.drawLine(pts[0], pts[1], pts[2], pts[3]);
				gc.drawLine(pts[4], pts[5], pts[6], pts[7]);
			}

			String label= nf.format(orgDelta);
			Point labelExtent= gc.stringExtent(label);
			int labelxpos= center + barLength;
			int labelvpos= y + (BARHEIGHT + GAP - labelExtent.y) / 2;
			if (orgDelta > 0.0) {
				gc.drawString(label, labelxpos + TGAP, labelvpos, true);
			} else {
				gc.drawString(label, labelxpos - TGAP - labelExtent.x, labelvpos, true);
			}

			int x= MARGIN + w + TGAP;
			String title= bar.title;
			boolean hasURL= bar.url != null;
			Color oldfg= gc.getForeground();
			if (hasURL) {
				gc.setForeground(blue);
				Point e= gc.stringExtent(title);
				gc.drawLine(x, labelvpos + e.y - 1, x + e.x, labelvpos + e.y - 1);
			}
			gc.drawString(title, x, labelvpos, true);
			if (hasURL)
				gc.setForeground(oldfg);

			int y0= y;
			y+= BARHEIGHT + GAP;

			if (hasURL) {
				if (this.fAreaBuffer == null)
					this.fAreaBuffer= new StringBuffer();
				this.fAreaBuffer.append("		echo '<area shape=\"RECT\" coords=\"0," + y0 + ',' + width + ',' + y + "\" href=\"" + bar.url + "\">';\n");
			}
		}

		lightblue.dispose();
		red.dispose();
		green.dispose();
	}

	public String getAreas() {
		if (this.fAreaBuffer != null) {
			String s= this.fAreaBuffer.toString();
			this.fAreaBuffer= null;
			return s;
		}
		return null;
	}
}
