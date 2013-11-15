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
package org.eclipse.test.internal.performance.results.model;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.test.internal.performance.results.db.*;
import org.eclipse.test.internal.performance.results.ui.BuildsComparisonTable;
import org.eclipse.test.internal.performance.results.utils.IPerformancesConstants;
import org.eclipse.test.internal.performance.results.utils.Util;
import org.eclipse.test.performance.ui.Utils;

public class PerformanceResultsElement extends ResultsElement {

// Singleton pattern
public static PerformanceResultsElement PERF_RESULTS_MODEL = new PerformanceResultsElement();

	String[] buildNames;
	String lastBuildName;
	boolean fingerprints = true;

public PerformanceResultsElement() {
	super();
}

ResultsElement createChild(AbstractResults testResults) {
	return new ComponentResultsElement(testResults, this);
}

public String[] getBaselines() {
	getBuildNames();
	if (this.buildNames == null) {
		return new String[0];
	}
	int length = this.buildNames.length;
	String[] baselines = new String[length];
	int count = 0;
	for (int i=0; i<length; i++) {
		if (this.buildNames[i].startsWith("R-")) {
			baselines[count++] = this.buildNames[i];
		}
	}
	if (count < length) {
		System.arraycopy(baselines, 0, baselines = new String [count], 0, count);
	}
	return baselines;
}

public String[] getBuildNames() {
	if (this.buildNames == null) {
		this.buildNames = DB_Results.DB_CONNECTION
			? DB_Results.getBuilds()
			: this.results == null
				? new String[0]
				: getPerformanceResults().getAllBuildNames();
	}
	return this.buildNames;
}

public Object[] getBuilds() {
	getBuildNames();
	int length = this.buildNames == null ? 0 : this.buildNames.length;
	BuildResultsElement[] elements = new BuildResultsElement[length];
	for (int i=0; i<length; i++) {
		elements[i] = new BuildResultsElement(this.buildNames[i], this);
	}
	return elements;
}

public String[] getComponents() {
	if (!isInitialized()) {
		String[] components = DB_Results.getComponents();
		int length = components.length;
		if (length == 0) {
			DB_Results.queryAllScenarios();
			components = DB_Results.getComponents();
		}
		return components;
	}
	return getPerformanceResults().getComponents();
}

/**
 * Returns the names of the configurations.
 *
 * @return An array of String
 */
public String[] getConfigs() {
	if (!isInitialized()) {
		String[] configs = DB_Results.getConfigs();
		int length = configs.length;
		if (length == 0) {
			DB_Results.queryAllScenarios();
			configs = DB_Results.getConfigs();
		}
		return configs;
	}
	return getPerformanceResults().getConfigNames(false);
}

/**
 * Returns the descriptions of the configurations.
 *
 * @return An array of String
 */
public String[] getConfigDescriptions() {
	if (!isInitialized()) {
		String[] descriptions = DB_Results.getConfigDescriptions();
		int length = descriptions.length;
		if (length == 0) {
			DB_Results.queryAllScenarios();
			descriptions = DB_Results.getConfigDescriptions();
		}
		return descriptions;
	}
	return getPerformanceResults().getConfigBoxes(false);
}

public Object[] getElements() {
	if (!isInitialized()) {
		String[] components = getComponents();
		int length = components.length;
		ComponentResultsElement[] elements = new ComponentResultsElement[length];
		for (int i=0; i<length; i++) {
			elements[i] = new ComponentResultsElement(components[i], this);
		}
		return elements;
	}
	return getChildren(null);
}

public PerformanceResults getPerformanceResults() {
	return (PerformanceResults) this.results;
}

boolean hasRead(BuildResultsElement buildResultsElement) {
	String[] builds = this.results == null ? getBuildNames() : getPerformanceResults().getAllBuildNames();
	if (Arrays.binarySearch(builds, buildResultsElement.getName(), Util.BUILD_DATE_COMPARATOR) < 0) {
		return false;
	}
	return true;
}

public boolean isInitialized() {
	return super.isInitialized() && this.results.size() > 0;
}

public void readLocal(File dataDir, IProgressMonitor monitor, String lastBuild) {
	reset(lastBuild);
	PerformanceResults performanceResults = getPerformanceResults();
	performanceResults.setLastBuildName(lastBuild);
	performanceResults.readLocal(dataDir, monitor);
}

public void reset(String buildName) {
	if (buildName == null) {
		this.results = new PerformanceResults(this.lastBuildName, null, null, System.out);
	} else {
		this.results = new PerformanceResults(buildName, null, null, System.out);
	}
	this.children = null;
	this.buildNames = null;
}

public void resetBuildNames() {
	this.buildNames = null;
}

public void updateBuild(String buildName, boolean force, File dataDir, IProgressMonitor monitor) {
	if (this.results == null) {
		reset(buildName);
	}
	getPerformanceResults().updateBuild(buildName, force, dataDir, monitor);
}

public void updateBuilds(String[] builds, boolean force, File dataDir, IProgressMonitor monitor) {
	if (this.results == null) {
		reset(null);
	}
	getPerformanceResults().updateBuilds(builds, force, dataDir, monitor);
}

/**
 * Set whether only fingerprints should be taken into account or not.
 *
 * @param fingerprints
 */
public void setFingerprints(boolean fingerprints) {
	this.fingerprints = fingerprints;
	resetStatus();
}

public void setLastBuildName(String lastBuildName) {
	this.lastBuildName = lastBuildName;
	this.name = null;
}

/*
 * Write the component status in the given file
 */
public StringBuffer writeFailures(File resultsFile, int kind) {
	if (this.results == null) {
		return null;
	}
	boolean values = (kind & IPerformancesConstants.STATUS_VALUES) != 0;
	// Write status only for component with error
	StringBuffer excluded = new StringBuffer();
	try {
		DataOutputStream stream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(resultsFile)));
		try {
			StringBuffer buffer = new StringBuffer();
			// Print build name
			buffer.append("Status for ");
			buffer.append(getPerformanceResults().getName());
			buffer.append(Util.LINE_SEPARATOR);
			// Print status options
			if ((kind & ~IPerformancesConstants.STATUS_VALUES) > 0) {
				buffer.append("Options: ");
				buffer.append(Util.LINE_SEPARATOR);
				final int errorLevel = kind & IPerformancesConstants.STATUS_ERROR_LEVEL_MASK;
				if (errorLevel != 0) {
					buffer.append("	error level: ");
					switch (errorLevel) {
						case IPerformancesConstants.STATUS_ERROR_NONE:
							buffer.append("include all failures whatever the error level is");
							break;
						case IPerformancesConstants.STATUS_ERROR_NOTICEABLE:
							buffer.append("all failures with at least a noticeable error (> 3%) are excluded!");
							break;
						case IPerformancesConstants.STATUS_ERROR_SUSPICIOUS:
							buffer.append("all failures with at least a suspicious error (> 25%) are excluded!");
							break;
						case IPerformancesConstants.STATUS_ERROR_WEIRD:
							buffer.append("all failures with at least a weird error (> 50%) are excluded!");
							break;
						case IPerformancesConstants.STATUS_ERROR_INVALID:
							buffer.append("all failures with an invalid error (> 100%) are excluded!");
							break;
					}
					buffer.append(Util.LINE_SEPARATOR);
				}
				final int smallValue = kind & IPerformancesConstants.STATUS_SMALL_VALUE_MASK;
				if (smallValue > 0) {
					buffer.append("	small value: ");
					switch (smallValue) {
						case IPerformancesConstants.STATUS_SMALL_VALUE_BUILD:
							buffer.append("all failures with a small build value (<100ms) are excluded!");
							break;
						case IPerformancesConstants.STATUS_SMALL_VALUE_DELTA:
							buffer.append("all failures with a small delta value (<100ms) are excluded!");
							break;
						case IPerformancesConstants.STATUS_SMALL_VALUE_MASK:
							buffer.append("all failures with a small build or delta value (<100ms) are excluded!");
							break;
					}
					buffer.append(Util.LINE_SEPARATOR);
				}
				final int stats = kind & IPerformancesConstants.STATUS_STATISTICS_MASK;
				if (stats > 0) {
					buffer.append("	statistics: ");
					switch (stats) {
						case IPerformancesConstants.STATUS_STATISTICS_ERRATIC:
							buffer.append("all failures with erratic baseline results (variation > 20%) are excluded!");
							break;
						case IPerformancesConstants.STATUS_STATISTICS_UNSTABLE:
							buffer.append("all failures with unstable baseline results (10% < variation < 20%) are excluded!");
							break;
					}
					buffer.append(Util.LINE_SEPARATOR);
				}
				int buildsNumber = kind & IPerformancesConstants.STATUS_BUILDS_NUMBER_MASK;
				buffer.append("	builds to confirm a regression: ");
				buffer.append(buildsNumber);
				buffer.append(Util.LINE_SEPARATOR);
			}
			// Print columns title
			buffer.append("Component");
			buffer.append("	Scenario");
			buffer.append("	Machine");
			if (values) {
				buffer.append("			Build		");
				buffer.append("		History		");
			}
			buffer.append("	Comment");
			buffer.append(Util.LINE_SEPARATOR);
			if (values) {
				buffer.append("			value");
				buffer.append("	baseline");
				buffer.append("	variation");
				buffer.append("	delta");
				buffer.append("	error");
				buffer.append("	n");
				buffer.append("	mean");
				buffer.append("	deviation");
				buffer.append("	coeff");
				buffer.append(Util.LINE_SEPARATOR);
			}
			stream.write(buffer.toString().getBytes());
			StringBuffer componentBuffer = getFailures(new StringBuffer(), kind, excluded);
			if (componentBuffer.length() > 0) {
				stream.write(componentBuffer.toString().getBytes());
			}
		}
		finally {
			stream.close();
		}
	} catch (FileNotFoundException e) {
		System.err.println("Can't create output file"+resultsFile); //$NON-NLS-1$
	} catch (IOException e) {
		e.printStackTrace();
	}
	return excluded;
}

/*
 * Write the comparison between two builds in the given file
 */
public void writeComparison(File resultsFile, String build, String reference) {
	if (this.results == null) {
		return;
	}
	try {
		// Create the stream
		PrintStream stream = new PrintStream(new BufferedOutputStream(new FileOutputStream(resultsFile)));

		// Print main title
		stream.print("<link href=\""+Utils.TOOLTIP_STYLE+"\" rel=\"stylesheet\" type=\"text/css\">\n");
		stream.print("<script src=\""+Utils.TOOLTIP_SCRIPT+"\"></script>\n");
		stream.print("<script src=\""+Utils.FINGERPRINT_SCRIPT+"\"></script>\n");
		stream.print(Utils.HTML_DEFAULT_CSS);
		stream.print("<body>");
		stream.print("<h2>Performance comparison of ");
		stream.print(build);
		stream.print(" relative to ");
		int index = reference.indexOf('_');
		if (index > 0) {
			stream.print(reference.substring(0, index));
			stream.print(" (");
			index = reference.lastIndexOf('_');
			stream.print(reference.substring(index+1, reference.length()));
			stream.print(')');
		} else {
			stream.print(reference);
		}
		stream.print("</h2>\n");

		// Print a comparison table for each component
		try {
			int length = this.children.length;
			for (int i=0; i<length; i++) {
				BuildsComparisonTable table = new BuildsComparisonTable(this.children[i].getName(), stream, build, reference);
				table.print(getPerformanceResults());
			}
		}
		finally {
			stream.print("</body>");
			stream.close();
		}
	} catch (FileNotFoundException e) {
		System.err.println("Can't create output file"+resultsFile); //$NON-NLS-1$
	}
}

}
