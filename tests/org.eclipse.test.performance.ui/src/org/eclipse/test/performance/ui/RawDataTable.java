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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.test.internal.performance.data.Dim;
import org.eclipse.test.internal.performance.results.db.BuildResults;
import org.eclipse.test.internal.performance.results.db.ConfigResults;
import org.eclipse.test.internal.performance.results.db.DB_Results;
import org.eclipse.test.internal.performance.results.utils.Util;

/**
 * Class used to fill details file of scenario builds data.
 * @see ScenarioData
 */
public class RawDataTable {

	private ConfigResults configResults;
	private List buildPrefixes;
	private PrintStream stream;
	private Dim[] dimensions = DB_Results.getResultsDimensions();
	private boolean debug = false;

private RawDataTable(ConfigResults results, PrintStream ps) {
	this.configResults = results;
	this.stream = ps;
}

public RawDataTable(ConfigResults results, List prefixes, PrintStream ps) {
	this(results, ps);
	this.buildPrefixes = prefixes;
}
public RawDataTable(ConfigResults results, String baselinePrefix, PrintStream ps) {
	this(results, ps);
	this.buildPrefixes = new ArrayList();
	this.buildPrefixes.add(baselinePrefix);
}

/**
 * Print all build data to the current stream.
 */
public void print(){
	this.stream.print("<table border=\"1\">");
	printSummary();
	printDetails();
	this.stream.print("</table>\n");
}

/*
 * Print table columns headers.
 */
private void printColumnHeaders() {
	StringBuffer buffer = new StringBuffer();
	int length = this.dimensions.length;
	for (int i=0; i<length; i++) {
		buffer.append("<td><b>");
		buffer.append(this.dimensions[i].getName());
		buffer.append("</b></td>");
	}
	this.stream.print(buffer.toString());
}

/*
 * Print all build results in the table.
 */
private void printDetails() {
	this.stream.print("<tr><td><b>Build ID</b></td>");
	printColumnHeaders();
	this.stream.print("</tr>\n");

	List builds = this.configResults.getBuildsMatchingPrefixes(this.buildPrefixes);
	Collections.reverse(builds);
	int size = builds.size();
	for (int i=0; i<size; i++) {
		BuildResults buildResults = (BuildResults) builds.get(i);
		this.stream.print("<tr><td>");
		this.stream.print(buildResults.getName());
		this.stream.print("</td>");
		int dimLength = this.dimensions.length;
		for (int d=0; d<dimLength; d++) {
			Dim dimension = this.dimensions[d];
			int dim_id = dimension.getId();
			double value = buildResults.getValue(dim_id);
			printDimTitle(dimension.getName());
			String displayValue = dimension.getDisplayValue(value);
			this.stream.print(displayValue);
			if (this.debug) System.out.print("\t"+displayValue);
			this.stream.print("</td>");
		}
		if (this.debug) System.out.println();
		this.stream.print("</tr>\n");
	}
	if (this.debug) System.out.println("\n");
}

/*
 * Print summary on top of the table.
 */
private void printSummary() {
	this.stream.print("<tr><td><b>Stats</b></td>");
	printColumnHeaders();
	this.stream.print("</tr>\n");

	int length = this.dimensions.length;
	double[][] dimStats = new double[length][];
	for (int i=0; i<length; i++) {
		dimStats[i] = this.configResults.getStatistics(this.buildPrefixes, this.dimensions[i].getId());
	}

	this.stream.print("<tr><td>#BUILDS SAMPLED</td>");
	for (int i=0; i<length; i++) {
		String dimName = this.dimensions[i].getName();
		printDimTitle(dimName);
		this.stream.print((int)dimStats[i][0]);
		this.stream.print("</td>");
	}
	this.stream.print("</tr>\n");
	this.stream.print("<tr><td>MEAN</td>");
	printRowDoubles(dimStats, 1);
	this.stream.print("</tr>\n");
	this.stream.print("<tr><td>STD DEV</td>");
	printRowDoubles(dimStats, 2);
	this.stream.print("</tr>\n");
	this.stream.print("<tr><td>COEF. VAR</td>");
	printRowDoubles(dimStats, 3);
	this.stream.print("</tr>\n");

	// Blank line
	this.stream.print("<tr>");
	for (int i=0; i<length+1;	i++){
		this.stream.print("<td>&nbsp;</td>");
	}
	this.stream.print("</tr>\n");
}

/*
 * Print values in table row.
 */
private void printRowDoubles(double[][] stats, int idx) {
	int length = this.dimensions.length;
	for (int i=0; i<length; i++) {
		double value = stats[i][idx];
		String dimName = this.dimensions[i].getName();
		if (idx == 3) {
			if (value > 0.1 && value < 0.2) {
				this.stream.print("<td bgcolor=\"yellow\" title=\"");
			} else if (value >= 0.2) {
				this.stream.print("<td bgcolor=\"FF9900\" title=\"");
			} else {
				this.stream.print("<td title=\"");
			}
			this.stream.print(dimName);
			this.stream.print("\">");
			this.stream.print(Util.PERCENTAGE_FORMAT.format(value));
			this.stream.print("</td>");
		} else {
			printDimTitle(dimName);
			this.stream.print(this.dimensions[i].getDisplayValue(value));
			this.stream.print("</td>");
		}
	}
}

/*
 * Print dim title inside value reference.
 * TODO (frederic) See if this title is really necessary
 */
private void printDimTitle(String dimName) {
    this.stream.print("<td title=\"");
    this.stream.print(dimName);
    this.stream.print("\">");
}
}
