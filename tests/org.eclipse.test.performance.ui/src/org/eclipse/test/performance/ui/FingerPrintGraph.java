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

import java.io.File;
import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.widgets.Display;
import org.eclipse.test.internal.performance.results.db.BuildResults;
import org.eclipse.test.internal.performance.results.db.ConfigResults;
import org.eclipse.test.internal.performance.results.db.DB_Results;
import org.eclipse.test.internal.performance.results.db.ScenarioResults;
import org.eclipse.test.internal.performance.results.utils.Util;

/**
 * Abstract class to build graph with bars
 */
public class FingerPrintGraph {

	// Sizes
	static final int MARGIN= 5; // margin on all four sides
	static final int BAR_HEIGHT= 6; // height of bar
	static final int GAP= 10; // gap between bars
	static final int TGAP= 5; // gap between lines and labels
	static final int LINE_HEIGHT = 2*BAR_HEIGHT + GAP;

	// fraction of width reserved for bar graph
	static final double RATIO= 0.6;

	// Formatting constants
	static final NumberFormat NUMBER_FORMAT;
	static {
		NUMBER_FORMAT = NumberFormat.getInstance();
		NUMBER_FORMAT.setMaximumFractionDigits(1);
	}

	// Graphic constants
	static final Display DEFAULT_DISPLAY = Display.getDefault();
	static final Color BLACK= DEFAULT_DISPLAY.getSystemColor(SWT.COLOR_BLACK);
	static final Color BLUE= DEFAULT_DISPLAY.getSystemColor(SWT.COLOR_BLUE);
	static final Color GREEN= DEFAULT_DISPLAY.getSystemColor(SWT.COLOR_GREEN);
	static final Color RED = DEFAULT_DISPLAY.getSystemColor(SWT.COLOR_RED);
	static final Color GRAY = DEFAULT_DISPLAY.getSystemColor(SWT.COLOR_GRAY);
	static final Color DARK_GRAY = DEFAULT_DISPLAY.getSystemColor(SWT.COLOR_DARK_GRAY);
	static final Color YELLOW = DEFAULT_DISPLAY.getSystemColor(SWT.COLOR_YELLOW);
	static final Color WHITE = DEFAULT_DISPLAY.getSystemColor(SWT.COLOR_WHITE);

	// Bar graph kinds
	static final int NO_TIME = 0; // i.e. percentage
	static final int TIME_LINEAR = 1;
	static final int TIME_LOG = 2;
	static final int[] SUPPORTED_GRAPHS = {
//		NO_TIME,
		TIME_LINEAR,
		TIME_LOG,
	};

	// Graphic fields
	GC gc;
	Image image;
	int imageWidth;
	int imageHeight;
	int graphWidth;
	int graphHeight;
	Map resources = new HashMap();

	// Data fields
	int count = 0;
	ConfigResults[] results = new ConfigResults[10];
	BarGraphArea[] areas;

	// Values
	double maxValue = 0.0;
	double minValue = Double.MAX_VALUE;

	// File info
	File outputDir;
	String imageName;
	private final String defaultDimName = DB_Results.getDefaultDimension().getName();

	/*
	 * Member class defining a bar graph area.
	 * This area applies to a configuration results and is made of several zones.
	 */
	class BarGraphArea {
		List zones;
		private ConfigResults configResults;

		/*
		 * Member class defining a zone inside a bar graph area.
		 * Typically made of a rectangle and an associated text used as tooltip.
		 */
		class AreaZone {
			Rectangle zone;
			String title;

			AreaZone(Rectangle zone, String tooltip) {
	            super();
	            this.zone = zone;
	            this.title = tooltip;
            }

			void print(String url, PrintStream stream) {
				stream.print("		echo '<area shape=\"RECT\"");
				if (this.title != null) {
					stream.print(" title=\""+this.title+"\"");
				}
				stream.print("coords=\"");
				stream.print(this.zone.x);
				stream.print(',');
				stream.print(this.zone.y);
				stream.print(',');
				stream.print(this.zone.x+this.zone.width);
				stream.print(',');
				stream.print(this.zone.y+this.zone.height);
				stream.print('"');
				if (url != null) {
					stream.print(" href=\"");
					stream.print(url);
					stream.print('"');
				}
				stream.print(">';\n");
			}
		}

		 BarGraphArea(ConfigResults results) {
			this.configResults = results;
			this.zones = new ArrayList();
        }

		void print(PrintStream stream) {
			String url = this.configResults.getName() + "/" + ((ScenarioResults) this.configResults.getParent()).getFileName() + ".html";
			int size = this.zones.size();
			for (int i=0; i<size; i++) {
				AreaZone zone = (AreaZone) this.zones.get(i);
				zone.print(url, stream);
			}
		}

		void addArea(Rectangle rec, String tooltip) {
			AreaZone zone = new AreaZone(rec, tooltip);
			this.zones.add(zone);
		}

	}


FingerPrintGraph(File dir, String fileName, int width, List results) {
    super();
    this.imageWidth = width;
    this.count = results.size();
    this.results = new ConfigResults[this.count];
    results.toArray(this.results);
    this.outputDir = dir;
    this.imageName = fileName;
}

/**
 */
void drawBars(int kind) {

	// Get/Set graphical resources
	Font italicFont = (Font) this.resources.get("italicFont");
	if (italicFont == null) {
		String fontDataName = this.gc.getFont().getFontData()[0].toString();
		FontData fdItalic = new FontData(fontDataName);
		fdItalic.setStyle(SWT.ITALIC);
		italicFont = new Font(DEFAULT_DISPLAY, fdItalic);
		this.resources.put("italicFont", italicFont);
	}
	Color blueref = (Color) this.resources.get("blueref");
	if (blueref == null) {
		blueref = new Color(DEFAULT_DISPLAY, 200, 200, 255);
		this.resources.put("blueref", blueref);
	}
	Color lightyellow= (Color) this.resources.get("lightyellow");
	if (lightyellow == null) {
		lightyellow = new Color(DEFAULT_DISPLAY, 255, 255, 160);
		this.resources.put("lightyellow", lightyellow);
	}
	Color darkyellow= (Color) this.resources.get("darkyellow");
	if (darkyellow == null) {
		darkyellow = new Color(DEFAULT_DISPLAY, 160, 160, 0);
		this.resources.put("darkyellow", darkyellow);
	}
	Color okColor= (Color) this.resources.get("lightgreen");
	if (okColor == null) {
		okColor = new Color(DEFAULT_DISPLAY, 95, 191, 95);
		this.resources.put("lightgreen", okColor);
	}
	Color failureColor = (Color) this.resources.get("lightred");
	if (failureColor == null) {
		failureColor = new Color(DEFAULT_DISPLAY, 220, 50, 50);
		this.resources.put("lightred", failureColor);
	}

	// Build each scenario bar graph
	this.areas = new BarGraphArea[this.count];
	double max = kind == TIME_LOG ? Math.log(this.maxValue) : this.maxValue;
	for (int i=0, y=MARGIN; i < this.count; i++, y+=LINE_HEIGHT) {

		// get builds info
		ConfigResults configResults = this.results[i];
		this.areas[i] = new BarGraphArea(configResults);
		BarGraphArea graphArea = this.areas[i];
		BuildResults currentBuildResults = configResults.getCurrentBuildResults();
		double currentValue = currentBuildResults.getValue();
		double currentError = currentBuildResults.getError();
		double error = configResults.getError();
		boolean singleTest = Double.isNaN(error);
		boolean isSignificant = singleTest || error < Utils.STANDARD_ERROR_THRESHOLD;
		boolean isCommented = currentBuildResults.getComment() != null;
		BuildResults baselineBuildResults = configResults.getBaselineBuildResults();
		double baselineValue = baselineBuildResults.getValue();
		double baselineError = baselineBuildResults.getError();

		// draw baseline build bar
		Color whiteref = (Color) this.resources.get("whiteref");
		if (whiteref == null) {
			whiteref = new Color(DEFAULT_DISPLAY, 240, 240, 248);
			this.resources.put("whiteref", whiteref);
		}
		this.gc.setBackground(whiteref);
		double baselineGraphValue = kind == TIME_LOG ? Math.log(baselineValue) : baselineValue;
		int baselineBarLength= (int) (baselineGraphValue / max * this.graphWidth);
		int baselineErrorLength= (int) (baselineError / max * this.graphWidth / 2);
		int labelxpos = MARGIN + baselineBarLength;
		if (kind == TIME_LOG || baselineErrorLength <= 1) {
			this.gc.fillRectangle(MARGIN, y + (GAP/2), baselineBarLength, BAR_HEIGHT);
			Rectangle rec = new Rectangle(MARGIN, y + (GAP/2), baselineBarLength, BAR_HEIGHT);
			this.gc.drawRectangle(rec);
			graphArea.addArea(rec, "Time for baseline build "+baselineBuildResults.getName()+": "+Util.timeString((long)baselineValue));
		} else {
			int wr = baselineBarLength - baselineErrorLength;
			Rectangle recValue = new Rectangle(MARGIN, y + (GAP/2), wr, BAR_HEIGHT);
			this.gc.fillRectangle(recValue);
			this.gc.setBackground(YELLOW);
			Rectangle recError = new Rectangle(MARGIN+wr, y + (GAP/2), baselineErrorLength*2, BAR_HEIGHT);
			this.gc.fillRectangle(recError);
			Rectangle rec = new Rectangle(MARGIN, y + (GAP/2), baselineBarLength+baselineErrorLength, BAR_HEIGHT);
			this.gc.drawRectangle(rec);
			StringBuffer tooltip = new StringBuffer("Time for baseline build ");
			tooltip.append(baselineBuildResults.getName());
			tooltip.append(": ");
			tooltip.append(Util.timeString((long)baselineValue));
			tooltip.append(" [&#177;");
			tooltip.append(Util.timeString((long)baselineError));
			tooltip.append(']');
			graphArea.addArea(rec, tooltip.toString());
			labelxpos += baselineErrorLength;
		}

		// set current build bar color
		if (baselineValue < currentValue) {
			if (isCommented) {
				this.gc.setBackground(GRAY);
			} else  {
				this.gc.setBackground(failureColor);
			}
		} else {
			this.gc.setBackground(okColor);
		}

		// draw current build bar
		double currentGraphValue = kind == TIME_LOG ? Math.log(currentValue) : currentValue;
		int currentBarLength= (int) (currentGraphValue / max * this.graphWidth);
		int currentErrorLength= (int) (currentError / max * this.graphWidth / 2);
		if (kind == TIME_LOG || currentErrorLength <= 1) {
			this.gc.fillRectangle(MARGIN, y + (GAP/2) + BAR_HEIGHT, currentBarLength, BAR_HEIGHT);
			Rectangle rec = new Rectangle(MARGIN, y + (GAP/2) + BAR_HEIGHT, currentBarLength, BAR_HEIGHT);
			this.gc.drawRectangle(rec);
			String tooltip = "Time for current build "+currentBuildResults.getName()+": "+Util.timeString((long)currentValue);
			if (isCommented) {
				tooltip += ".		" + currentBuildResults.getComment();
			}
			graphArea.addArea(rec, tooltip);
			if (labelxpos < (MARGIN+currentBarLength)) {
				labelxpos = MARGIN + currentBarLength;
			}
		} else {
			int wr = currentBarLength - currentErrorLength;
			Rectangle recValue = new Rectangle(MARGIN, y + (GAP/2) + BAR_HEIGHT, wr, BAR_HEIGHT);
			this.gc.fillRectangle(recValue);
			this.gc.setBackground(YELLOW);
			Rectangle recError = new Rectangle(MARGIN+wr, y + (GAP/2) + BAR_HEIGHT, currentErrorLength*2, BAR_HEIGHT);
			this.gc.fillRectangle(recError);
			Rectangle rec = new Rectangle(MARGIN, y + (GAP/2) + BAR_HEIGHT, currentBarLength+currentErrorLength, BAR_HEIGHT);
			this.gc.drawRectangle(rec);
			StringBuffer tooltip = new StringBuffer("Time for current build ");
			tooltip.append(currentBuildResults.getName());
			tooltip.append(": ");
			tooltip.append(Util.timeString((long)currentValue));
			tooltip.append(" [&#177;");
			tooltip.append(Util.timeString((long)currentError));
			tooltip.append(']');
			if (isCommented) {
				tooltip.append(".		");
				tooltip.append(currentBuildResults.getComment());
			}
			graphArea.addArea(rec, tooltip.toString());
			if (labelxpos < (MARGIN+currentBarLength+currentErrorLength)) {
				labelxpos = MARGIN + currentBarLength+currentErrorLength;
			}
		}

		// set delta value style and color
		boolean hasFailure = currentBuildResults.getFailure() != null;
		if (hasFailure) {
			if (isCommented) {
				this.gc.setForeground(DARK_GRAY);
			} else  {
				this.gc.setForeground(RED);
			}
		} else {
			this.gc.setForeground(BLACK);
		}

		// draw delta value
		double delta = -configResults.getDelta();
		String label = delta > 0 ? "+" : "";
		label += NUMBER_FORMAT.format(delta*100) + "%";
		Point labelExtent= this.gc.stringExtent(label);
		int labelvpos= y + (LINE_HEIGHT - labelExtent.y) / 2;
		this.gc.drawString(label, labelxpos+TGAP, labelvpos, true);
		this.gc.setForeground(BLACK);
		this.gc.setFont(null);
		int titleStart = (int) (RATIO * this.imageWidth);
		if (singleTest || !isSignificant) {
			String deltaTooltip = null;
			if (singleTest) {
				deltaTooltip = "This test performed only one iteration; hence its reliability cannot be assessed";
			} else if (!isSignificant) {
				deltaTooltip = "This test has a bad reliability: error is "+NUMBER_FORMAT.format(error*100)+"% (> 3%)!";
			}
			Image warning = (Image) this.resources.get("warning");
			int xi = labelxpos+TGAP+labelExtent.x;
			this.gc.drawImage(warning, xi, labelvpos);
			ImageData imageData = warning.getImageData();
			// Set zones
			// - first one is between end of bar and warning image beginning
			Rectangle deltaZone = new Rectangle(labelxpos, labelvpos-2, xi-labelxpos, labelExtent.y+4);
			graphArea.addArea(deltaZone, null);
			// - second one is the warning image
			Rectangle warningZone = new Rectangle(xi, labelvpos, imageData.width, imageData.height);
			graphArea.addArea(warningZone, deltaTooltip);
			// - last one is between end of the warning image and the scenario title beginning
			int warningImageEnd = xi+imageData.width;
			Rectangle emptyZone = new Rectangle(warningImageEnd, labelvpos, titleStart-warningImageEnd, imageData.height);
			graphArea.addArea(emptyZone, deltaTooltip);
		} else {
			// No tooltip => delta zone is between end of bar and the scenario title beginning
			Rectangle deltaZone = new Rectangle(labelxpos, labelvpos-2, titleStart-labelxpos, labelExtent.y+4);
			graphArea.addArea(deltaZone, null);
		}

		// set title style
		Color oldfg= this.gc.getForeground();
		this.gc.setForeground(BLUE);

		// draw scenario title
		int x= titleStart;
		ScenarioResults scenarioResults = (ScenarioResults) configResults.getParent();
		String title = scenarioResults.getLabel() + " (" + this.defaultDimName + ")";
		Point e= this.gc.stringExtent(title);
		this.gc.drawLine(x, labelvpos + e.y - 1, x + e.x, labelvpos + e.y - 1);
		this.gc.drawString(title, x, labelvpos, true);
		this.gc.setForeground(oldfg);
		this.gc.setFont(null);
		Rectangle titleZone = new Rectangle(x, labelvpos, e.x, e.y);
		graphArea.addArea(titleZone, null/*no tooltip*/);
		if (!configResults.isBaselined()) {
			Image warning = (Image) this.resources.get("warning");
			this.gc.drawImage(warning, x+e.x, labelvpos);
			ImageData imageData = warning.getImageData();
			Rectangle warningZone = new Rectangle(x+e.x, labelvpos, imageData.width, imageData.height);
			String titleTooltip =  "This test has no baseline result, hence use build "+configResults.getBaselineBuildName()+" for reference!";
			graphArea.addArea(warningZone, titleTooltip);
		}
	}
}

void drawLinearScale() {

	// Draw scale background
	drawScaleBackground();

	// Draw scale grid lines
	int gridValue = 100;
	int n = (int) (this.maxValue / gridValue);
	while (n > 10) {
		switch (gridValue) {
			case 100:
				gridValue = 200;
				break;
			case 200:
				gridValue = 500;
				break;
			case 500:
				gridValue = 1000;
				break;
			default:
				gridValue += 1000;
				break;
		}
		n = (int) (this.maxValue / gridValue);
	}
	int gridWidth = (int) (this.graphWidth * gridValue / this.maxValue);
	int x = MARGIN;
	long value = 0; // TODO use minValue instead
	while (x < this.graphWidth) {

		// draw line
		this.gc.setForeground(GRAY);
		if (x > 0) {
			this.gc.setLineStyle(SWT.LINE_DOT);
			this.gc.drawLine(x, MARGIN, x, this.graphHeight + TGAP);
		}

		// draw value
		this.gc.setForeground(BLACK);
		String val= Util.timeString(value);
		Point point= this.gc.stringExtent(val);
		this.gc.drawString(val, x - point.x / 2, this.graphHeight + TGAP, true);

		// compute next grid position
		x += gridWidth;
		value += gridValue; // value is expressed in seconds
	}
	this.gc.setLineStyle(SWT.LINE_SOLID);
	this.gc.drawLine(0, this.graphHeight, this.graphWidth, this.graphHeight);
}

void drawLogarithmScale() {

	// Draw scale background
	drawScaleBackground();

	// Draw scale grid lines
	double max = Math.log(this.maxValue);
	int gridValue = 100;
	int x = MARGIN;
	long value = 0; // TODO use minValue instead
	while (x < this.graphWidth) {

		// draw line
		this.gc.setForeground(GRAY);
		if (x > MARGIN) {
			this.gc.setLineStyle(SWT.LINE_DOT);
			this.gc.drawLine(x, MARGIN, x, this.graphHeight + TGAP);
		}

		// draw value
		this.gc.setForeground(BLACK);
		String str = Util.timeString(value);
		Point point= this.gc.stringExtent(str);
		this.gc.drawString(str, x - point.x / 2, this.graphHeight + TGAP, true);

		// compute next grid position
		value += gridValue;
		int v = (int) (value / 100);
		int c = 1;
		while (v > 10) {
			v = v / 10;
			c *= 10;
		}
		switch (v) {
			case 3:
				gridValue = 200*c;
				break;
			case 5:
				gridValue = 500*c;
				break;
			case 10:
				gridValue = 1000*c;
				break;
		}
		x = MARGIN + (int) (this.graphWidth * Math.log(value) / max);
	}
	this.gc.setLineStyle(SWT.LINE_SOLID);
	this.gc.drawLine(0, this.graphHeight, this.graphWidth, this.graphHeight);
}

/**
 * Draw the scale depending on the bar time graph kind.
 */
void drawScale(int kind) {
	switch (kind) {
		case TIME_LINEAR:
			drawLinearScale();
			break;
		case TIME_LOG:
			drawLogarithmScale();
			break;
	}
}

private void drawScaleBackground() {

	// Draw striped background
	Color lightblue = (Color) this.resources.get("lightblue");
	if (lightblue == null) {
		lightblue = new Color(DEFAULT_DISPLAY, 237, 243, 254);
		this.resources.put("lightblue", lightblue);
	}
	this.gc.setBackground(lightblue);
	for (int i= 0; i<this.count; i++) {
		if (i % 2 == 0) {
	        this.gc.fillRectangle(0, MARGIN + i * LINE_HEIGHT, this.imageWidth, LINE_HEIGHT);
        }
	}

	// Draw bottom vertical line
	int yy= MARGIN + this.count * LINE_HEIGHT;
	this.gc.drawLine(MARGIN, MARGIN, MARGIN, yy + TGAP);
}

String getImageName(int kind) {
	switch (kind) {
		case TIME_LINEAR:
			return this.imageName+"_linear";
		case TIME_LOG:
			return this.imageName+"_log";
	}
	return this.imageName;
}

void paint(int kind) {

	// Set image
	this.graphHeight = MARGIN + this.count * LINE_HEIGHT;
	this.imageHeight = this.graphHeight + GAP + 16 + MARGIN;
	this.image = new Image(DEFAULT_DISPLAY, this.imageWidth, this.imageHeight);
	this.gc = new GC(this.image);

	// draw white background
	this.gc.setBackground(WHITE);
	this.gc.fillRectangle(0, 0, this.imageWidth, this.imageHeight);

	// Set widths and heights
	int width= (int) (RATIO * this.imageWidth); // width for results bar
	this.graphWidth= width - this.gc.stringExtent("-999.9%").x - TGAP - MARGIN; // reserve space //$NON-NLS-1$

	// Get warning image width
	Image warning = (Image) this.resources.get("warning");
	if (warning == null) {
		warning = new Image(this.gc.getDevice(), new File(this.outputDir, Utils.WARNING_OBJ).toString());
		this.resources.put("warning", warning);
	}
	this.graphWidth -= warning.getImageData().width;

	// Set maximum of values
	this.maxValue = 0.0;
	this.minValue = Double.MAX_VALUE;
	for (int i= 0; i<this.count; i++) {
		BuildResults baselineBuildResults = this.results[i].getBaselineBuildResults();
		double value = baselineBuildResults.getValue();
		double error = baselineBuildResults.getError();
		if (!Double.isNaN(error)) value += Math.abs(error);
		if (value < 1000000 && value > this.maxValue) {
			this.maxValue = value;
		}
		if (value < this.minValue) {
			this.minValue = value;
		}
		BuildResults currentBuildResults = this.results[i].getCurrentBuildResults();
		value = currentBuildResults.getValue();
		error = currentBuildResults.getError();
		if (!Double.isNaN(error)) value += Math.abs(error);
		if (value < 1000000 && value > this.maxValue) {
			this.maxValue = value;
		}
		if (value < this.minValue) {
			this.minValue = value;
		}
	}
	this.minValue = 0; // do not use minValue for now...

	// Draw the scale
	drawScale(kind);

	// Draw the bars
	drawBars(kind);

	// Dispose
	this.gc.dispose();
}

/**
 * Create, paint and save all supported bar graphs and add the corresponding
 * image and map references in the given stream.
 *
 * @param stream
 */
final public void paint(PrintStream stream) {

	// Paint supported graphs
	int length = SUPPORTED_GRAPHS.length;
	for (int i=0; i<length; i++) {
		int kind = SUPPORTED_GRAPHS[i];
		paint(kind);
		save(kind, stream);
	}

	// Dispose created graphic resources
	Iterator iterator = this.resources.values().iterator();
	while (iterator.hasNext()) {
		Resource resource = (Resource) iterator.next();
		resource.dispose();
	}
	this.resources.clear();
}

void print(int kind, PrintStream stream) {
	String imgName = getImageName(kind);
	stream.print("	if ($type==\"fp_type="+kind+"\") {\n");
	stream.print("		echo '<img src=\"");
	stream.print(imgName);
	stream.print(".gif\" usemap=\"#");
	stream.print(imgName);
	stream.print("\" name=\"");
	stream.print(imgName.substring(imgName.lastIndexOf('.')));
	stream.print("\">';\n");
	stream.print("		echo '<map name=\"");
	stream.print(imgName);
	stream.print("\">';\n");
	if (this.areas != null) {
		for (int i=0; i<this.count; i++) {
			this.areas[i].print(stream);
		}
	}
	stream.print("		echo '</map>';\n");
	stream.print("	}\n");
}

void save(int kind, PrintStream stream) {
	File file = new File(this.outputDir, getImageName(kind)+".gif");
	Utils.saveImage(file, this.image);
	if (file.exists()) {
		print(kind, stream);
	} else {
		stream.print("<br><br>There is no fingerprint for ");
		stream.print(this.imageName);
		stream.print(" (kind=");
		stream.print(kind);
		stream.print(")<br><br>\n");
	}
}
}
