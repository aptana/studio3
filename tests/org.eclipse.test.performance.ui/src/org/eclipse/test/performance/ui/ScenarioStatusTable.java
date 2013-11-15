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

import java.io.PrintStream;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.test.internal.performance.results.db.BuildResults;
import org.eclipse.test.internal.performance.results.db.ConfigResults;
import org.eclipse.test.internal.performance.results.db.PerformanceResults;
import org.eclipse.test.internal.performance.results.db.ScenarioResults;

/**
 * Class used to print a scenario status table.
 */
public class ScenarioStatusTable {

	private String component;
	private PrintStream stream;
	private int jsIdCount;

/**
 * Creates an HTML table of red x/green check for a scenario for each
 * configuration.
 */
public ScenarioStatusTable(String  name, PrintStream stream) {
    this.component = name;
    this.stream = stream;
}

/**
 * Prints the HTML representation of scenario status table into the given stream.
 */
public void print(PerformanceResults performanceResults) {

	String baselineName = performanceResults.getBaselineName();
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
	this.jsIdCount = 0;
	for (int i=0; i<size; i++) {
		ScenarioResults scenarioResults = (ScenarioResults) scenarios.get(i);
		if (!scenarioResults.isValid()) continue;
		this.stream.print("<tr>\n");
		this.stream.print("<td>");
		boolean hasSummary = scenarioResults.hasSummary();
		if (hasSummary) this.stream.print("<b>");
		String scenarioBaseline = scenarioResults.getBaselineBuildName();
		boolean hasBaseline = baselineName.equals(scenarioBaseline);
		if (!hasBaseline) {
			this.stream.print("*");
			this.stream.print(scenarioResults.getShortName());
			this.stream.print(" <small>(vs.&nbsp;");
			this.stream.print(scenarioBaseline);
			this.stream.print(")</small>");
		} else {
			this.stream.print(scenarioResults.getShortName());
		}
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
	BuildResults currentBuildResults = configResults.getCurrentBuildResults();
	String failure = currentBuildResults.getFailure();
	double[] deviation = configResults.getCurrentBuildDeltaInfo();
	int confidence = Utils.confidenceLevel(deviation);
	boolean hasFailure = failure != null;
	String comment = currentBuildResults.getComment();
	String image = Utils.getImage(confidence, hasFailure, comment != null);
	this.stream.print("<td><a ");
	if (!hasFailure|| (confidence & Utils.NAN) != 0 || failure.length() == 0){
		// write deviation with error in table when test pass
		this.stream.print("href=\"");
		this.stream.print(configResults.getName());
		this.stream.print('/');
		this.stream.print(scenarioResults.getFileName());
		this.stream.print(".html\">\n");
		this.stream.print("<img hspace=\"10\" border=\"0\" src=\"");
		this.stream.print(image);
		this.stream.print("\"/></a>\n");
	} else {
		// create message with tooltip text including deviation with error plus failure message
		this.jsIdCount+=1;
		this.stream.print("class=\"tooltipSource\" onMouseover=\"show_element('toolTip");
		this.stream.print(this.jsIdCount);
		this.stream.print("')\" onMouseout=\"hide_element('toolTip");
		this.stream.print(this.jsIdCount);
		this.stream.print("')\" \nhref=\"");
		this.stream.print(configResults.getName());
		this.stream.print('/');
		this.stream.print(scenarioResults.getFileName());
		this.stream.print(".html\">\n");
		this.stream.print("<img hspace=\"10\" border=\"0\" src=\"");
		this.stream.print(image);
		this.stream.print("\"/>\n");
		this.stream.print("<span class=\"hidden_tooltip\" id=\"toolTip");
		this.stream.print(this.jsIdCount);
		this.stream.print("\">");
		this.stream.print(failure);
		this.stream.print("</span></a>\n");
	}
	String result = Utils.failureMessage(deviation, false);
	this.stream.print(result);
	this.stream.print("\n");
}

/*
 * Print the status table explanationtitle.
 */
private void printTitle() {
	this.stream.print("<br><h4>Scenario Status</h4>\n");
	this.stream.print("The following table gives a complete but compact view of performance results for the component.<br>\n");
	this.stream.print("Each line of the table shows the results for one scenario on all machines.<br><br>\n");
	this.stream.print("The name of the scenario is in <b>bold</b> when its results are also displayed in the fingerprints<br>\n");
	this.stream.print("and starts with an '*' when the scenario has no results in the last baseline run.<br><br>\n");
	this.stream.print("Here are information displayed for each test (ie. in each cell):\n");
	this.stream.print("<ul>\n");
	this.stream.print("<li>an icon showing whether the test fails or passes and whether it's reliable or not.<br>\n");
	this.stream.print("The legend for this icon is:\n");
	this.stream.print("<ul>\n");
	this.stream.print("<li>Green (<img src=\"");
	this.stream.print(Utils.OK_IMAGE);
	this.stream.print("\">): mark a <b>successful result</b>, which means this test has neither significant performance regression nor significant standard error</li>");
	this.stream.print("<li>Red (<img src=\"");
	this.stream.print(Utils.FAIL_IMAGE);
	this.stream.print("\">): mark a <b>failing result</b>, which means this test shows a significant performance regression (more than 10%)</li>\n");
	this.stream.print("<li>Gray (<img src=\"");
	this.stream.print(Utils.FAIL_IMAGE_EXPLAINED);
	this.stream.print("\">): mark a <b>failing result</b> (see above) with a comment explaining this degradation.</li>\n");
	this.stream.print("<li>Yellow (<img src=\"");
	this.stream.print(Utils.FAIL_IMAGE_WARN);
	this.stream.print("\"> or <img src=\"");
	this.stream.print(Utils.OK_IMAGE_WARN);
	this.stream.print("\">): mark a <b>failing or successful result</b> with a significant standard error (more than ");
	this.stream.print(Utils.STANDARD_ERROR_THRESHOLD_STRING);
	this.stream.print(")</li>\n");
	this.stream.print("<li>Black (<img src=\"");
	this.stream.print(Utils.UNKNOWN_IMAGE);
	this.stream.print("\">): mark an <b>undefined result</b>, which means that deviation on this test is not a number (<code>NaN</code>) or is infinite (happens when the reference value is equals to 0!)</li>");
	this.stream.print("<li>\"n/a\": mark a test for with <b>no</b> performance results</li>\n");
	this.stream.print("</ul></li>\n");
	this.stream.print("<li>the value of the deviation from the baseline as a percentage (ie. formula is: <code>(build_test_time - baseline_test_time) / baseline_test_time</code>)</li>\n");
	this.stream.print("<li>the value of the standard error of this deviation as a percentage (ie. formula is: <code>sqrt(build_test_stddev^2 / N + baseline_test_stddev^2 / N) / baseline_test_time</code>)<br>\n");
	this.stream.print("When test only has one measure, the standard error cannot be computed and is replaced with a '<font color=\"#CCCC00\">[n/a]</font>'.</li>\n");
	this.stream.print("</ul>\n");
	this.stream.print("<u>Hints</u>:<ul>\n");
	this.stream.print("<li>fly over image of failing tests to see the complete error message</li>\n");
	this.stream.print("<li>to look at the complete and detailed test results, click on its image</li>\n");
	this.stream.print("</ul>\n");
}
}
