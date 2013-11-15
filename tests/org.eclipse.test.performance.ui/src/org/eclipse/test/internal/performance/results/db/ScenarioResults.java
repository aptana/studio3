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
package org.eclipse.test.internal.performance.results.db;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.test.internal.performance.results.utils.Util;


/**
 * Class to handle performance results of a component's scenario
 * (for example 'org.eclipse.jdt.core.FullSourceWorkspaceSearchTest#searchAllTypeNames()').
 *
 * It gives access to results for each configuration run on this scenario.
 *
 * @see ConfigResults
 */
public class ScenarioResults extends AbstractResults {
	String fileName;
	String label;
	String shortName;

public ScenarioResults(int id, String name, String shortName) {
	super(null, id);
	this.name = name;
	this.label = shortName;
}

/*
 * Complete results with additional database information.
 */
void completeResults(String lastBuildName) {
	String[] builds = DB_Results.getBuilds();
	class BuildDateComparator implements Comparator {
		public int compare(Object o1, Object o2) {
	        String s1 = (String) o1;
	        String s2 = (String) o2;
	        return Util.getBuildDate(s1).compareTo(Util.getBuildDate(s2));
	    }
	}
	BuildDateComparator comparator = new BuildDateComparator();
	Arrays.sort(builds, comparator);
	int idx = Arrays.binarySearch(builds, lastBuildName, comparator);
	if (idx < 0) {
		builds = null;
	} else {
		int size = builds.length - ++idx;
		System.arraycopy(builds, idx, builds = new String[size], 0, size);
	}
//	String[] builds = null;
	int size = size();
	for (int i=0; i<size; i++) {
		ConfigResults configResults = (ConfigResults) this.children.get(i);
		configResults.completeResults(builds);
	}
}

/**
 * Returns the first configuration baseline build name.
 *
 * @return The name of the baseline build
 * @see ConfigResults#getBaselineBuildName()
 */
public String getBaselineBuildName() {
	int size = size();
	StringBuffer buffer = new StringBuffer();
	for (int i=0; i<size; i++) {
		ConfigResults configResults = (ConfigResults) this.children.get(i);
		if (configResults.isValid()) {
			return configResults.getBaselineBuildName();
			/* TODO (frederic) decide what return when baseline is not the same on all configs...
			 * Currently returns the first found, but may be a comma-separated list?
			String baselineName = configResults.getBaselineBuildName();
			if (buffer.indexOf(baselineName) < 0) {
				if (buffer.length() > 0) buffer.append('|');
				buffer.append(baselineName);
			}
			*/
		}
	}
	return buffer.toString();
}

Set getAllBuildNames() {
	Set buildNames = new HashSet();
	int size = size();
	for (int i=0; i<size; i++) {
		ConfigResults configResults = (ConfigResults) this.children.get(i);
		List builds = configResults.getBuilds(null);
		int length = builds.size();
		for (int j=0; j<length; j++) {
			buildNames.add(((BuildResults)builds.get(j)).getName());
		}
	}
	return buildNames;
}

/**
 * Return the results of the given configuration.
 *
 * @param config The configuration name
 * @return The {@link ConfigResults results} for the given configuration
 * 	or <code>null</code> if none was found.
 */
public ConfigResults getConfigResults(String config) {
	return (ConfigResults) getResults(config);
}

/**
 * Return a name which can be used as a file name to store information
 * related to this scenario. This name does not contain the extension.
 *
 * @return The file name
 */
public String getFileName() {
	if (this.fileName == null) {
		this.fileName = "Scenario" + this.id; //$NON-NLS-1$
	}
	return this.fileName;
}

/**
 * Returns the scenario label. If no label exist as there's no associated summary,
 * then the short name is returned
 *
 * @return The label of the scenario or it's short name if no summary exists
 */
public String getLabel() {
	return this.label == null ? getShortName() : this.label;
}

/**
 * Returns the short name of the scenario. Short name is the name scenario
 * from which package declaration has been removed.
 *
 * @return The scenario short name
 */
public String getShortName() {
	if (this.shortName == null) {
		// Remove class name qualification
		int testSeparator = this.name.indexOf('#');
		boolean hasClassName = testSeparator >= 0;
		if (!hasClassName) {
			testSeparator = this.name.lastIndexOf('.');
			if (testSeparator <= 0) {
				return this.shortName = this.name;
			}
		}
		int classSeparator = this.name.substring(0, testSeparator).lastIndexOf('.');
		if (classSeparator < 0) {
			return this.shortName = this.name;
		}
		int length = this.name.length();
		String testName = this.name.substring(classSeparator+1, length);
		if (!hasClassName && testName.startsWith("test.")) { // specific case for swt... //$NON-NLS-1$
			testName = testName.substring(5);
		}

		// Remove qualification from test name
		StringTokenizer tokenizer = new StringTokenizer(testName, " :,", true); //$NON-NLS-1$
		StringBuffer buffer = new StringBuffer(tokenizer.nextToken());
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			char fc = token.charAt(0);
			while (fc == ' ' || fc == ',' || fc == ':') {
				buffer.append(token); // add the separator
				token = tokenizer.nextToken();
				fc = token.charAt(0);
			}
			int last = token.lastIndexOf('.');
			if (last >= 3) {
				int first = token .indexOf('.');
				if (first == last) {
					buffer.append(token);
				} else {
					buffer.append(token.substring(last+1));
				}
			} else {
				buffer.append(token);
			}
		}
		this.shortName = buffer.toString();
	}
	return this.shortName;
}

/**
 * Returns whether one of the scenario's config has a summary or not.
 *
 * @return <code>true</code> if one of the scenario's config has a summary
 * 	<code>false</code> otherwise.
 */
public boolean hasSummary() {
	int size = size();
	for (int i=0; i<size; i++) {
		ConfigResults configResults = (ConfigResults) this.children.get(i);
		BuildResults currentBuildResults = configResults.getCurrentBuildResults();
		if (currentBuildResults != null && currentBuildResults.hasSummary()) return true;
	}
	return false;
}

/* (non-Javadoc)
 * @see org.eclipse.test.internal.performance.results.AbstractResults#hashCode()
 */
public int hashCode() {
	return this.id;
}

/**
 * Returns whether the current scenario is valid or not.
 *
 * @return <code>true</code> if all the builds contained in the database are
 * 	known by the scenario (ie. at least one its configuration knows each of the
 * 	db builds), <code>false</code> otherwise.
 */
public boolean isValid() {
	int size = this.children.size();
	for (int i=0; i<size; i++) {
		ConfigResults configResults = (ConfigResults) this.children.get(i);
		if (configResults.isValid()) {
			return true;
		}
	}
	return false;
}

/**
 * Returns whether the current build of the given config has valid results or not.
 *
 * @param config The name of the configuration
 * @return <code>true</code> if the build has valid results
 * 	<code>false</code> otherwise.
 */
public boolean isValid(String config) {
	return getResults(config) != null;
}

/**
 * Returns whether the current scenario knows a build or not.
 *
 * @param buildName The name of the build
 * @return <code>true</code> if the at least one of scenario configuration
 * 	knows the given build, <code>false</code> otherwise.
 */
public boolean knowsBuild(String buildName) {
	String[] buildNames = buildName == null
		? DB_Results.getBuilds()
		: new String[] { buildName };
	Set scenarioBuilds = getAllBuildNames();
	int length = buildNames.length;
	for (int i=0; i<length; i++) {
		if (!scenarioBuilds.contains(buildNames[i])) {
			return false;
		}
	}
	return true;
}

/*
 * Read scenario results information from database.
 *
void read(String buildName, long lastBuildTime) {

	// Get values
	print("	+ scenario '"+getShortName()+"': values..."); //$NON-NLS-1$ //$NON-NLS-2$
	long start = System.currentTimeMillis();
	String configPattern = getPerformance().getConfigurationsPattern();
	DB_Results.queryScenarioValues(this, configPattern, buildName, lastBuildTime);
	print(timeString(System.currentTimeMillis()-start));

	// Set baseline and current builds
	print(", infos..."); //$NON-NLS-1$
	start = System.currentTimeMillis();
	int size = size();
	String[] builds = buildName == null ? null : new String[] { buildName };
	for (int i=0; i<size; i++) {
		ConfigResults configResults = (ConfigResults) this.children.get(i);
		configResults.completeResults(builds);
	}
	println(timeString(System.currentTimeMillis()-start));
}
*/

/*
 * Read data from a local file
 */
void readData(DataInputStream stream) throws IOException {

	// Read data stored locally
	int size = stream.readInt();
	for (int i=0; i<size; i++) {
		int config_id = stream.readInt();
		ConfigResults configResults = (ConfigResults) getResults(config_id);
		if (configResults == null) {
			configResults = new ConfigResults(this, config_id);
			addChild(configResults, true);
		}
		configResults.readData(stream);
	}
}

/*
 * Read new data from the database.
 * This is typically needed when the build results are not in the local file...
 *
boolean readNewData(String lastBuildName, boolean force) {
	if (lastBuildName == null) {
		read(null, -1);
		return true;
	}
	PerformanceResults performanceResults = getPerformance();
	String lastBuildDate = getBuildDate(lastBuildName, getBaselinePrefix());
	if (force || performanceResults.getBuildDate().compareTo(lastBuildDate) > 0) {
		long lastBuildTime = 0;
	    try {
		    lastBuildTime = DATE_FORMAT.parse(lastBuildDate).getTime();
	    } catch (ParseException e) {
		    // should not happen
	    }
	    read(lastBuildName, lastBuildTime);
		return true;
	}
	return false;
}
*/

/*
 * Set value from database information.
 */
void setInfos(int config_id, int build_id, int summaryKind, String comment) {
	ConfigResults configResults = (ConfigResults) getResults(config_id);
	if (configResults == null) {
		configResults = new ConfigResults(this, config_id);
		addChild(configResults, true);
	}
	configResults.setInfos(build_id, summaryKind, comment);
}

/*
 * Set value from database information.
 */
void setValue(int build_id, int dim_id, int config_id, int step, long value) {
	ConfigResults configResults = (ConfigResults) getResults(config_id);
	if (configResults == null) {
		configResults = new ConfigResults(this, config_id);
		addChild(configResults, true);
	}
	configResults.setValue(build_id, dim_id, step, value);
}

/*
 * Read scenario results information from database.
 */
boolean updateBuild(String buildName, boolean force) {

	if (!force && knowsBuild(buildName)) {
		return false;
	}

	// Get values
	print("	+ scenario '"+getShortName()+"': values..."); //$NON-NLS-1$ //$NON-NLS-2$
	long start = System.currentTimeMillis();
	String configPattern = getPerformance().getConfigurationsPattern();
	DB_Results.queryScenarioValues(this, configPattern, buildName);
	print(Util.timeString(System.currentTimeMillis()-start));

	// Set baseline and current builds
	print(", infos..."); //$NON-NLS-1$
	start = System.currentTimeMillis();
	int size = size();
	String[] builds = buildName == null ? null : new String[] { buildName };
	for (int i=0; i<size; i++) {
		ConfigResults configResults = (ConfigResults) this.children.get(i);
		configResults.completeResults(builds);
	}
	println(Util.timeString(System.currentTimeMillis()-start));
	return true;
}

void write(DataOutputStream stream) throws IOException {
	int size = size();
	stream.writeInt(this.id);
	stream.writeInt(size);
	for (int i=0; i<size; i++) {
		ConfigResults configResults = (ConfigResults) this.children.get(i);
		configResults.write(stream);
	}
}

}
