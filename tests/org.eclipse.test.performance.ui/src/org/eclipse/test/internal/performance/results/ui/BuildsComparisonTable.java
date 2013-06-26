/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.test.internal.performance.results.ui;

import java.io.PrintStream;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.test.internal.performance.results.db.AbstractResults;
import org.eclipse.test.internal.performance.results.db.BuildResults;
import org.eclipse.test.internal.performance.results.db.ConfigResults;
import org.eclipse.test.internal.performance.results.db.PerformanceResults;
import org.eclipse.test.internal.performance.results.db.ScenarioResults;
import org.eclipse.test.performance.ui.Utils;

/**
 * This class is responsible to print html table with the difference between two
 * builds for all the scenarios and the configuration of a specific component.
 */
public class BuildsComparisonTable {

	private static final double DEFAULT_FAILURE_THRESHOLD = PerformanceResults.DEFAULT_FAILURE_THRESHOLD / 100.0 / 2;
	private String component;
	private PrintStream stream;
	String buildName, referenceName;

public BuildsComparisonTable(String name, PrintStream stream, String build, String reference) {
    this.component = name;
    this.stream = stream;
    this.buildName = build;
    this.referenceName = reference;
}

/**
 * Prints the HTML representation of scenario status table into the given stream.
 */
public void print(PerformanceResults performanceResults) {

	List scenarios = performanceResults.getComponentScenarios(this.component);
	int size = scenarios.size();

	// Print titles
	printTitle();
	this.stream.print("<table border=\"1\">\n");
	this.stream.print("<tr>\n");
	this.stream.print("<td><h4>All ");
	this.stream.print(computeSize(scenarios));
	this.stream.print(" scenarios</h4></td>\n");
	printColumnsTitle(size, performanceResults);

	// Print one line per scenario results
	for (int i=0; i<size; i++) {
		ScenarioResults scenarioResults = (ScenarioResults) scenarios.get(i);
		if (!scenarioResults.isValid()) continue;
		this.stream.print("<tr>\n");
		this.stream.print("<td>");
		boolean hasSummary = scenarioResults.hasSummary();
		if (hasSummary) this.stream.print("<b>");
		this.stream.print(scenarioResults.getShortName());
		if (hasSummary) this.stream.print("</b>");
		this.stream.print("\n");
		String[] configs = performanceResults.getConfigNames(true/*sort*/);
		int length = configs.length;
		for (int j=0; j<length; j++) {
			printConfigStats(scenarioResults, configs[j]);
		}
	}
	this.stream.print("</table>\n");
}

private int computeSize(List scenarios) {
	int size = scenarios.size();
	int n = 0;
	for (int i=0; i<size; i++) {
		ScenarioResults scenarioResults = (ScenarioResults) scenarios.get(i);
		if (scenarioResults.isValid()) n++;
	}
	return n;
}

/*
 * Print the table columns title.
 */
private void printColumnsTitle(int size, PerformanceResults performanceResults) {
	String[] configNames = performanceResults.getConfigNames(true/*sort*/);
	String[] configBoxes = performanceResults.getConfigBoxes(true/*sort*/);
	int length = configNames.length;
	for (int i=0; i<length; i++) {
		String columnTitle = configNames[i];
		String boxName = configBoxes[i];
		int idx = boxName.indexOf('(');
		if (idx < 0) {
			columnTitle = boxName;
		} else {
			// first line
			StringTokenizer tokenizer = new StringTokenizer(boxName.substring(0, idx).trim(), " ");
			StringBuffer buffer = new StringBuffer(tokenizer.nextToken());
			while (tokenizer.hasMoreTokens()) {
				buffer.append("&nbsp;");
				buffer.append(tokenizer.nextToken());
			}
			buffer.append(' ');
			// second line
			tokenizer = new StringTokenizer(boxName.substring(idx).trim(), " ");
			buffer.append(tokenizer.nextToken());
			while (tokenizer.hasMoreTokens()) {
				buffer.append("&nbsp;");
				buffer.append(tokenizer.nextToken());
			}
			columnTitle = buffer.toString();
		}
		this.stream.print("<td><h5>");
		this.stream.print(columnTitle);
		this.stream.print("</h5>\n");
	}
}

/*
 * Print the scenario statistics value for the given configuration.
 */
private void printConfigStats(ScenarioResults scenarioResults, String config) {
	ConfigResults configResults = scenarioResults.getConfigResults(config);
	if (configResults == null || !configResults.isValid()) {
		this.stream.print("<td>n/a</td>");
		return;
	}
	final BuildResults buildResults = configResults.getBuildResults(this.buildName);
	if (buildResults == null) {
		this.stream.print("<td>no results</td>");
		return;
	}
	final BuildResults referenceResults = configResults.getBuildResults(this.referenceName);
	if (referenceResults == null) {
		this.stream.print("<td>no reference</td>");
		return;
	}
	double[] numbers = configResults.getNumbers(buildResults, referenceResults);
	final double delta = numbers[AbstractResults.DELTA_VALUE_INDEX];
	final double[] deviation = new double[] { -delta, numbers[AbstractResults.DELTA_ERROR_INDEX] };
	boolean hasFailure = delta < -DEFAULT_FAILURE_THRESHOLD;
	String comment = buildResults.getComment();
	String image = Utils.getImage(0, hasFailure, comment != null);
	this.stream.print("<td><a ");

	// write deviation with error in table
	this.stream.print("href=\"http://fullmoon.ottawa.ibm.com/downloads/drops/I20100817-0800/performance/");
	this.stream.print(configResults.getName());
	this.stream.print('/');
	this.stream.print(scenarioResults.getFileName());
	this.stream.print(".html\">\n");
	this.stream.print("<img hspace=\"10\" border=\"0\" src=\"");
	this.stream.print(image);
	this.stream.print("\"/></a>\n");
	String result = Utils.failureMessage(deviation, false);
	this.stream.print(result);
	this.stream.print("\n");
}

/*
 * Print the comparison table title.
 */
private void printTitle() {
	this.stream.print("<br><h3>Component ");
	this.stream.print(this.component);
	this.stream.print("</h3>\n");
}

}
