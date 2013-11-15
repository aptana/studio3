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
import java.util.Iterator;
import java.util.List;

import junit.framework.AssertionFailedError;

import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;
import org.eclipse.test.internal.performance.data.Dim;
import org.eclipse.test.internal.performance.results.db.BuildResults;
import org.eclipse.test.internal.performance.results.db.ComponentResults;
import org.eclipse.test.internal.performance.results.db.ConfigResults;
import org.eclipse.test.internal.performance.results.db.DB_Results;
import org.eclipse.test.internal.performance.results.db.PerformanceResults;
import org.eclipse.test.internal.performance.results.db.ScenarioResults;
import org.eclipse.test.internal.performance.results.utils.Util;

/**
 * Class used to print scenario all builds data.
 */
public class ScenarioData {
	private String baselinePrefix = null;
	private List pointsOfInterest;
	private List buildIDStreamPatterns;
	private File rootDir;
	private static final int GRAPH_WIDTH = 600;
	private static final int GRAPH_HEIGHT = 200;
	private Dim[] dimensions = DB_Results.getResultsDimensions();

/**
 * Summary of results for a scenario for a given build compared to a
 * reference.
 *
 * @param baselinePrefix The prefix of the baseline build names
 * @param pointsOfInterest A list of buildId's to highlight on line graphs
 * @param buildIDPatterns
 * @param outputDir The directory root where the files are generated
 *
*/
public ScenarioData(String baselinePrefix, List pointsOfInterest, List buildIDPatterns, File outputDir) {
	this.baselinePrefix = baselinePrefix;
	this.pointsOfInterest = pointsOfInterest;
	this.buildIDStreamPatterns = buildIDPatterns;
	this.rootDir = outputDir;
}

/*
 * Create a file handle verifying that its name does not go over
 * the maximum authorized length.
 */
private File createFile(File outputDir, String subdir, String name, String extension) {
	File dir = outputDir;
	if (subdir != null) {
		dir = new File(outputDir, subdir);
		if (!dir.exists()) {
			dir.mkdir();
		}
	}
	return new File(dir, name + '.' + extension);
}

/*
 * Returns a LineGraph object representing measurements for a scenario over builds.
 */
private TimeLineGraph getLineGraph(ScenarioResults scenarioResults, ConfigResults configResults, Dim dim, List highlightedPoints, List currentBuildIdPrefixes) {
	Display display = Display.getDefault();

	Color black = display.getSystemColor(SWT.COLOR_BLACK);
	Color yellow = display.getSystemColor(SWT.COLOR_DARK_YELLOW);
	Color magenta = display.getSystemColor(SWT.COLOR_MAGENTA);

	String scenarioName = scenarioResults.getName();
	TimeLineGraph graph = new TimeLineGraph(scenarioName + ": " + dim.getName(), dim);
	String baseline = configResults.getBaselineBuildName();
	String current = configResults.getCurrentBuildName();

	final String defaultBaselinePrefix = DB_Results.getDbBaselinePrefix();
	Iterator builds = configResults.getResults();
	List lastSevenNightlyBuilds = configResults.lastNightlyBuildNames(7);
	buildLoop: while (builds.hasNext()) {
		BuildResults buildResults = (BuildResults) builds.next();
		String buildID = buildResults.getName();
		int underscoreIndex = buildID.indexOf('_');
		String label = (underscoreIndex != -1 && buildID.equals(current)) ? buildID.substring(0, underscoreIndex) : buildID;
		if (buildID.startsWith(defaultBaselinePrefix)) {
			label = defaultBaselinePrefix+buildID.charAt(defaultBaselinePrefix.length())+buildID.substring(underscoreIndex);
		}

		double value = buildResults.getValue(dim.getId());

		if (buildID.equals(current)) {
			Color color = black;
			if (buildID.startsWith("N"))
				color = yellow;

			graph.addItem("main", label, dim.getDisplayValue(value), value, color, true, Utils.getDateFromBuildID(buildID), true);
			continue;
		}
		if (highlightedPoints.contains(buildID)) {
			graph.addItem("main", label, dim.getDisplayValue(value), value, black, false, Utils.getDateFromBuildID(buildID, false), true);
			continue;
		}
		if (buildID.charAt(0) == 'N') {
			if (lastSevenNightlyBuilds.contains(buildID)) {
				graph.addItem("main", buildID, dim.getDisplayValue(value), value, yellow, false, Utils.getDateFromBuildID(buildID), false);
			}
			continue;
		}
		for (int i=0;i<currentBuildIdPrefixes.size();i++){
			if (buildID.startsWith(currentBuildIdPrefixes.get(i).toString())) {
				graph.addItem("main", buildID, dim.getDisplayValue(value), value, black, false, Utils.getDateFromBuildID(buildID), false);
				continue buildLoop;
			}
		}
		if (buildID.equals(baseline)) {
			boolean drawBaseline = (this.baselinePrefix != null) ? false : true;
			graph.addItem("reference", label, dim.getDisplayValue(value), value, magenta, true, Utils.getDateFromBuildID(buildID, true), true, drawBaseline);
			continue;
		}
		if (this.baselinePrefix != null) {
			if (buildID.startsWith(this.baselinePrefix) && !buildID.equals(baseline) && Utils.getDateFromBuildID(buildID, true) <= Utils.getDateFromBuildID(baseline, true)) {
				graph.addItem("reference", label, dim.getDisplayValue(value), value, magenta, false, Utils.getDateFromBuildID(buildID, true), false);
				continue;
			}
		}
	}
	return graph;
}

/**
 * Print the scenario all builds data from the given performance results.
 *
 * @param performanceResults The needed information to generate scenario data
 */
public void print(PerformanceResults performanceResults, PrintStream printStream, final SubMonitor subMonitor) {
	String[] configNames = performanceResults.getConfigNames(false/*not sorted*/);
	String[] configBoxes = performanceResults.getConfigBoxes(false/*not sorted*/);
	int length = configNames.length;
	int size = performanceResults.size();
	double total = length * size;
	subMonitor.setWorkRemaining(length*size);
	int progress = 0;
	for (int i=0; i<length; i++) {
		final String configName = configNames[i];
		final String configBox = configBoxes[i];

		// Manage monitor
//		subMonitor.setTaskName("Generating data for "+configBox);
		final String subTaskPrefix = "Generating data: "+configBox;
		subMonitor.subTask(subTaskPrefix);
		if (subMonitor.isCanceled()) throw new OperationCanceledException();

		long start = System.currentTimeMillis();
		if (printStream != null) printStream.print("		+ "+configName);
		final File outputDir = new File(this.rootDir, configName);
		outputDir.mkdir();
		Iterator components = performanceResults.getResults();
		while (components.hasNext()) {
			if (printStream != null) printStream.print(".");
			final ComponentResults componentResults = (ComponentResults) components.next();

			// Manage monitor
			int percentage = (int) ((progress++ / total) * 100);
//			subMonitor.setTaskName("Generating data for "+configBox+": "+percentage+"%");
//			subMonitor.subTask("Component "+componentResults.getName()+"...");
			subMonitor.subTask(subTaskPrefix + " (" +componentResults.getName()+ ") "+ percentage + "%...");

			Display display = Display.getDefault();
		     display.syncExec(
				new Runnable() {
					public void run(){
						printSummary(configName, configBox, componentResults, outputDir, subMonitor);
					}
				}
			);
//			printSummary(configName, configBox, componentResults, outputDir, monitor);
			printDetails(configName, configBoxes[i], componentResults, outputDir);

			subMonitor.worked(1);
			if (subMonitor.isCanceled()) throw new OperationCanceledException();
		}
		if (printStream != null) {
			String duration = Util.timeString(System.currentTimeMillis()-start);
			printStream.println(" done in "+duration);
		}
	}
}

/*
 * Print the summary file of the builds data.
 */
void printSummary(String configName, String configBox, ComponentResults componentResults, File outputDir, SubMonitor subMonitor) {
	Iterator scenarios = componentResults.getResults();
	while (scenarios.hasNext()) {
		List highlightedPoints = new ArrayList();
		ScenarioResults scenarioResults = (ScenarioResults) scenarios.next();
		ConfigResults configResults = scenarioResults.getConfigResults(configName);
		if (configResults == null || !configResults.isValid()) continue;

		// get latest points of interest matching
		if (this.pointsOfInterest != null) {
			Iterator buildPrefixes = this.pointsOfInterest.iterator();
			while (buildPrefixes.hasNext()) {
				String buildPrefix = (String) buildPrefixes.next();
				List builds = configResults.getBuilds(buildPrefix);
				if (buildPrefix.indexOf('*') <0 && buildPrefix.indexOf('?') < 0) {
					if (builds.size() > 0) {
						highlightedPoints.add(builds.get(builds.size()-1));
					}
				} else {
					highlightedPoints.addAll(builds);
				}
			}
		}

		String scenarioFileName = scenarioResults.getFileName();
		File outputFile = new File(outputDir, scenarioFileName+".html");
		PrintStream stream = null;
		try {
			stream = new PrintStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
		} catch (FileNotFoundException e) {
			System.err.println("can't create output file" + outputFile); //$NON-NLS-1$
		}
		if (stream == null) {
			stream = System.out;
		}
		stream.print(Utils.HTML_OPEN);
		stream.print(Utils.HTML_DEFAULT_CSS);

		stream.print("<title>" + scenarioResults.getName() + "(" + configBox + ")" + "</title></head>\n"); //$NON-NLS-1$
		stream.print("<h4>Scenario: " + scenarioResults.getName() + " (" + configBox + ")</h4><br>\n"); //$NON-NLS-1$ //$NON-NLS-2$

		String failureMessage = Utils.failureMessage(configResults.getCurrentBuildDeltaInfo(), true);
 		if (failureMessage != null){
   			stream.print("<table><tr><td><b>"+failureMessage+"</td></tr></table>\n");
 		}

 		BuildResults currentBuildResults = configResults.getCurrentBuildResults();
 		String comment = currentBuildResults.getComment();
		if (comment != null) {
			stream.print("<p><b>Note:</b><br>\n");
			stream.print(comment + "</p>\n");
		}

		// Print link to raw data.
		String rawDataFile = "raw/" + scenarioFileName+".html";
		stream.print("<br><br><b><a href=\""+rawDataFile+"\">Raw data and Stats</a></b><br><br>\n");
		stream.print("<b>Click measurement name to view line graph of measured values over builds.</b><br><br>\n");
		if (subMonitor.isCanceled()) throw new OperationCanceledException();

		try {
			// Print build result table
			stream.print("<table border=\"1\">\n"); //$NON-NLS-1$
			stream.print("<tr><td><b>Build Id</b></td>"); //$NON-NLS-1$
			int dimLength = this.dimensions.length;
			for (int d=0; d<dimLength; d++) {
				Dim dim = this.dimensions[d];
				stream.print("<td><a href=\"#" + dim.getLabel() + "\"><b>" + dim.getName() + "</b></a></td>");
			}
			stream.print("</tr>\n");

			// Write build lines
			printTableLine(stream, currentBuildResults);
			printTableLine(stream, configResults.getBaselineBuildResults());

			// Write difference line
			printDifferenceLine(stream, configResults);

			// End of table
			stream.print("</table>\n");
			stream.print("*Delta values in red and green indicate degradation > 10% and improvement > 10%,respectively.<br><br>\n");
			stream.print("<br><hr>\n\n");

			// print text legend.
			stream.print("Black and yellow points plot values measured in integration and last seven nightly builds.<br>\n" + "Magenta points plot the repeated baseline measurement over time.<br>\n"
					+ "Boxed points represent previous releases, milestone builds, current reference and current build.<br><br>\n"
					+ "Hover over any point for build id and value.\n");

			// print image maps of historical
			for (int d=0; d<dimLength; d++) {
				Dim dim = this.dimensions[d];
				TimeLineGraph lineGraph = getLineGraph(scenarioResults, configResults, dim, highlightedPoints, this.buildIDStreamPatterns);
				if (subMonitor.isCanceled()) throw new OperationCanceledException();

				String dimShortName = dim.getLabel();
				String imgFileName = scenarioFileName + "_" + dimShortName;
				File imgFile = createFile(outputDir, "graphs", imgFileName, "gif");
				saveGraph(lineGraph, imgFile);
				stream.print("<br><a name=\"" + dimShortName + "\"></a>\n");
				stream.print("<br><b>" + dim.getName() + "</b><br>\n");
				stream.print(dim.getDescription() + "<br><br>\n");
				stream.print("<img src=\"graphs/");
				stream.print(imgFile.getName());
				stream.print("\" usemap=\"#" + lineGraph.fTitle + "\">");
				stream.print("<map name=\"" + lineGraph.fTitle + "\">");
				stream.print(lineGraph.getAreas());
				stream.print("</map>\n");
				if (subMonitor.isCanceled()) throw new OperationCanceledException();
			}
			stream.print("<br><br></body>\n");
			stream.print(Utils.HTML_CLOSE);
			if (stream != System.out)
				stream.close();

		} catch (AssertionFailedError e) {
			e.printStackTrace();
			continue;
		}
	}
}

/*
 * Print the data for a build results.
 */
private void printTableLine(PrintStream stream, BuildResults buildResults) {
	stream.print("<tr><td>");
	stream.print(buildResults.getName());
	if (buildResults.isBaseline()) stream.print(" (reference)");
	stream.print("</td>");
	int dimLength = this.dimensions.length;
	for (int d=0; d<dimLength; d++) {
		Dim dim = this.dimensions[d];
		int dim_id = dim.getId();
		double stddev = buildResults.getDeviation(dim_id);
		String displayValue = dim.getDisplayValue(buildResults.getValue(dim_id));
		stream.print("<td>");
		stream.print(displayValue);
		if (stddev < 0) {
			stream.print(" [n/a]\n");
		} else if (stddev > 0) {
			stream.print(" [");
			stream.print(dim.getDisplayValue(stddev));
			stream.print("]");
		}
		stream.print( "</td>");
	}
	stream.print("</tr>\n");
}

/*
 * Print the line showing the difference between current and baseline builds.
 */
private void printDifferenceLine(PrintStream stream, ConfigResults configResults) {
	stream.print("<tr><td>*Delta</td>");
	int dimLength = this.dimensions.length;
	for (int d=0; d<dimLength; d++) {
		Dim currentDim = this.dimensions[d];
		int dim_id = currentDim.getId();
		BuildResults currentBuild = configResults.getCurrentBuildResults();
		BuildResults baselineBuild = configResults.getBaselineBuildResults();

		// Compute difference values
		double baselineValue = baselineBuild.getValue(dim_id);
		double diffValue = baselineValue - currentBuild.getValue(dim_id);
		double diffPercentage =  baselineValue == 0 ? 0 : Math.round(diffValue / baselineValue * 1000) / 10.0;
		String diffDisplayValue = currentDim.getDisplayValue(diffValue);

		// Set colors
		String fontColor = "";
		if (diffPercentage > 10) {
			fontColor = "#006600";	// green
		}
		if (diffPercentage < -10) {
			fontColor = "#FF0000";	// red
		}

		// Print line
		String percentage = (diffPercentage == 0) ? "" : "<br>" + diffPercentage + " %";
		if (diffPercentage > 10 || diffPercentage < -10) {
			stream.print("<td><FONT COLOR=\"" + fontColor + "\"><b>" + diffDisplayValue + percentage + "</b></FONT></td>");
		} else {
			stream.print("<td>" + diffDisplayValue + percentage + "</td>");
		}
	}
	stream.print("</tr></font>");
}

/*
 * Print details file of the scenario builds data.
 */
private void printDetails(String configName, String configBox, ComponentResults componentResults, File outputDir) {
	Iterator scenarios = componentResults.getResults();
	while (scenarios.hasNext()) {
		ScenarioResults scenarioResults = (ScenarioResults) scenarios.next();
		ConfigResults configResults = scenarioResults.getConfigResults(configName);
		if (configResults == null || !configResults.isValid()) continue;
		String scenarioName= scenarioResults.getName();
		String scenarioFileName = scenarioResults.getFileName();
		File outputFile = createFile(outputDir, "raw", scenarioFileName, "html");
		PrintStream stream = null;
		try {
			stream = new PrintStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
		} catch (FileNotFoundException e) {
			System.err.println("can't create output file" + outputFile); //$NON-NLS-1$
		}
		if (stream == null) stream = System.out;
		RawDataTable currentResultsTable = new RawDataTable(configResults, this.buildIDStreamPatterns, stream);
		RawDataTable baselineResultsTable = new RawDataTable(configResults, this.baselinePrefix, stream);
		stream.print(Utils.HTML_OPEN);
		stream.print(Utils.HTML_DEFAULT_CSS);
		stream.print("<title>" + scenarioName + "(" + configBox + ")" + " - Details</title></head>\n"); //$NON-NLS-1$
		stream.print("<h4>Scenario: " + scenarioName + " (" + configBox + ")</h4>\n"); //$NON-NLS-1$
		stream.print("<a href=\"../"+scenarioFileName+".html\">VIEW GRAPH</a><br><br>\n"); //$NON-NLS-1$
		stream.print("<table><td><b>Current Stream Test Runs</b></td><td><b>Baseline Test Runs</b></td></tr>\n");
		stream.print("<tr valign=\"top\">\n");
		stream.print("<td>");
		currentResultsTable.print();
		stream.print("</td>\n");
		stream.print("<td>");
		baselineResultsTable.print();
		stream.print("</td>\n");
		stream.print("</tr>\n");
		stream.print("</table>\n");
		stream.close();
	}
}

/*
 * Prints a LineGraph object as a gif file.
 */
private void saveGraph(LineGraph p, File outputFile) {
	Image image = new Image(Display.getDefault(), GRAPH_WIDTH, GRAPH_HEIGHT);
	p.paint(image);

	/* Downscale to 8 bit depth palette to save to gif */
	ImageData data = Utils.downSample(image);
	ImageLoader il = new ImageLoader();
	il.data = new ImageData[] { data };
	OutputStream out = null;
	try {
		out = new BufferedOutputStream(new FileOutputStream(outputFile));
		il.save(out, SWT.IMAGE_GIF);

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
