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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;
import org.eclipse.test.internal.performance.results.db.ConfigResults;
import org.eclipse.test.internal.performance.results.db.DB_Results;
import org.eclipse.test.internal.performance.results.db.PerformanceResults;
import org.eclipse.test.internal.performance.results.db.ScenarioResults;

/**
 * Class used to create scenario fingerprint.
 */
public class FingerPrint {

private static final int GRAPH_WIDTH = 1000;

	String component;
	PrintStream stream;
	File outputDir;

public FingerPrint(String name, PrintStream ps, File outputDir) {
	if (!name.startsWith("global")) this.component = name;
	this.stream = ps;
	this.outputDir = outputDir;
}

/**
 * Create and save fingerprints as image and print their reference in the current stream.
 *
 * @param performanceResults The performance results used to print the fingerprints
 */
public void print(final PerformanceResults performanceResults) {
	String buildName = performanceResults.getName();

	// Compute fingerprint output file name prefix
	int currentUnderscoreIndex = buildName.indexOf('_');
	if  (currentUnderscoreIndex != -1){
		buildName = buildName.substring(0, currentUnderscoreIndex);
	}
	StringBuffer buffer = new StringBuffer("FP_");
	if (this.component != null) {
		buffer.append(this.component);
		buffer.append('_');
	}
	buffer.append(DB_Results.getDbBaselineRefVersion());
	buffer.append('_');
	buffer.append(buildName);
	String filePrefix = buffer.toString();

	// Print the legend
	this.stream.print("The following fingerprints show results for the most representative tests of the ");
	if (this.component == null) {
		this.stream.print("current build.<br>\n");
	} else {
		this.stream.print(this.component);
		this.stream.print(" component.<br>\n");
	}
	this.stream.print("<table border=\"0\">\n");
	this.stream.print("<tr><td valign=\"top\">Select which kind of scale you want to use:</td>\n");
	this.stream.print("<td valign=\"top\">\n");
	this.stream.print("  <form>\n");
	this.stream.print("    <select onChange=\"toggleFingerprints();\">\n");
	this.stream.print("      <option>percentage</option>\n");
	this.stream.print("      <option>time (linear)</option>\n");
	this.stream.print("      <option>time (log)</option>\n");
	this.stream.print("    </select>\n");
	this.stream.print("  </form>\n");
	this.stream.print("</td>\n");
//	this.stream.print("<td valign=\"top\">\n");
//	this.stream.print("<a href=\"help.html\"><img hspace=\"10\" border=\"0\" src=\""+Utils.LIGHT+"\" title=\"Some tips on fingerprints\"/></a>\n");
//	this.stream.print("</td></tr></table>\n");
	this.stream.print("</tr></table>\n");
	this.stream.print("<img hspace=\"10\" border=\"0\" src=\""+Utils.LIGHT+"\"><a href=\""+Utils.HELP+"\">Help on fingerprints</a>\n");

	// Print script to reset dropdown list selection
	this.stream.print("<script type=\"text/javascript\">\n");
	this.stream.print("	setFingerprintsType();\n");
	this.stream.print("</script>\n");

	// Create each fingerprint and save it
	String[] configNames = performanceResults.getConfigNames(false/* not sorted*/);
	String[] configBoxes = performanceResults.getConfigBoxes(false/* not sorted*/);
	int length = configNames.length;
	for (int c=0; c<length; c++) {
		String configName  = configNames[c];
		List scenarios = performanceResults.getComponentSummaryScenarios(this.component, configName);
		if (scenarios == null) continue;

		// Create BarGraph
		// TODO use FingerPrintGraph instead
		BarGraph barGraph = null;
		List allResults = new ArrayList();
		String defaultDimName = DB_Results.getDefaultDimension().getName();
		for (int i=0, size=scenarios.size(); i<size; i++) {
			ScenarioResults scenarioResults = (ScenarioResults) scenarios.get(i);
			ConfigResults configResults = scenarioResults.getConfigResults(configName);
			if (configResults == null || !configResults.isValid()) continue;
			double[] results = configResults.getCurrentBuildDeltaInfo();
			double percent = -results[0] * 100.0;
			if (results != null && Math.abs(percent) < 200) {
				String name = scenarioResults.getLabel() + " (" + defaultDimName + ")";
				if (!configResults.getCurrentBuildName().equals(buildName)) {
					continue; // the test didn't run on last build, skip it
				}
				if (!configResults.isBaselined()) {
					name = "*" + name + " (" + configResults.getBaselineBuildName() + ")";
				}
				if (barGraph == null) {
					barGraph = new BarGraph(null);
				}
				barGraph.addItem(name,
				    results,
				    configName + "/" + scenarioResults.getFileName() + ".html",
				    configResults.getCurrentBuildResults().getComment(),
				    (Utils.confidenceLevel(results) & Utils.ERR) == 0);

				// add results
				allResults.add(configResults);
			}
		}
		if (barGraph == null) continue;

		// Save image file
		String fileName = filePrefix + '.' + configName ;
		File outputFile = new File(this.outputDir, fileName+".gif");
		save(barGraph, outputFile);

		// Print image file reference in stream
		String boxName = configBoxes[c];
		if (outputFile.exists()) {
			String areas = barGraph.getAreas();
			if (areas == null) areas = "";
			this.stream.print("<h4>");
			this.stream.print(boxName);
			this.stream.print("</h4>\n");
			this.stream.print("<?php\n");
			this.stream.print("	$type=$_SERVER['QUERY_STRING'];\n");
			this.stream.print("	if ($type==\"\" || $type==\"fp_type=0\") {\n");
			this.stream.print("		echo '<img src=\"");
			this.stream.print(fileName);
			this.stream.print(".gif\" usemap=\"#");
			this.stream.print(fileName);
			this.stream.print("\" name=\"");
			this.stream.print(configName);
			this.stream.print("\">';\n");
			this.stream.print("		echo '<map name=\"");
			this.stream.print(fileName);
			this.stream.print("\">';\n");
			this.stream.print(areas);
			this.stream.print("		echo '</map>';\n");
			this.stream.print("	}\n");
		} else {
			this.stream.print("<br><br>There is no fingerprint for ");
			this.stream.print(boxName);
			this.stream.print("<br><br>\n");
		}

		// Create, paint and print the time bars graph
		FingerPrintGraph graph = new FingerPrintGraph(this.outputDir, fileName, GRAPH_WIDTH, allResults);
		graph.paint(this.stream);
		this.stream.print("?>\n");
	}
}

/*
 * Save the computed bar graph.
 */
private void save(BarGraph barGraph, File outputFile) {

	// Create and paint image
	Display display = Display.getDefault();
	int height = barGraph.getHeight();
	Image image = new Image(display, GRAPH_WIDTH, height);
	GC gc = new GC(image);
	barGraph.paint(display, GRAPH_WIDTH, height, gc);
	gc.dispose();

	saveImage(outputFile, image);
}

/**
 * @param outputFile
 * @param image
 */
private void saveImage(File outputFile, Image image) {
	// Save image
	ImageData data = Utils.downSample(image);
	ImageLoader imageLoader = new ImageLoader();
	imageLoader.data = new ImageData[] { data };

	OutputStream out = null;
	try {
		out = new BufferedOutputStream(new FileOutputStream(outputFile));
		imageLoader.save(out, SWT.IMAGE_GIF);
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} finally {
		image.dispose();
		if (out != null) {
			try {
				out.close();
			} catch (IOException e1) {
				// silently ignored
			}
		}
	}
}
}
