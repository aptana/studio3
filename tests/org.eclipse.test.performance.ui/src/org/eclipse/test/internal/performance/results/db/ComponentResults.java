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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.test.internal.performance.results.utils.Util;

/**
 * Class to handle performance results of an eclipse component (for example 'org.eclipse.jdt.core'). It gives access to
 * results for each scenario run for this component.
 * 
 * @see ScenarioResults
 */
public class ComponentResults extends AbstractResults
{

public ComponentResults(AbstractResults parent, String name)
{
	super(parent, name);
	this.printStream = parent.printStream;
}

Set getAllBuildNames()
{
	Set buildNames = new HashSet();
	int size = size();
	for (int i = 0; i < size; i++)
	{
		ScenarioResults scenarioResults = (ScenarioResults) this.children.get(i);
		Set builds = scenarioResults.getAllBuildNames();
		buildNames.addAll(builds);
	}
	return buildNames;
}

/**
 * Return all the build names for this component sorted by ascending order.
 * 
 * @return An array of names
 */
public String[] getAllSortedBuildNames()
{
	return getAllSortedBuildNames(false/* ascending order */);
}

String[] getAllSortedBuildNames(final boolean reverse)
{
	Set allBuildNames = getAllBuildNames();
	String[] sortedNames = new String[allBuildNames.size()];
	allBuildNames.toArray(sortedNames);
	Arrays.sort(sortedNames, new Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			String s1 = (String) (reverse ? o2 : o1);
			String s2 = (String) (reverse ? o1 : o2);
			return Util.getBuildDate(s1).compareTo(Util.getBuildDate(s2));
		}
	});
	return sortedNames;
}

ComponentResults getComponentResults()
{
	return this;
}

/**
 * Get all results numbers for a given machine of the current component.
 * 
 * @param configName
 *            The name of the configuration to get numbers
 * @param fingerprints
 *            Set whether only fingerprints scenario should be taken into account
 * @return A list of lines. Each line represent a build and is a list of either strings or values. Values are an
 *         array of double:
 *         <ul>
 *         <li>{@link #BUILD_VALUE_INDEX}: the build value in milliseconds</li>
 *         <li>{@link #BASELINE_VALUE_INDEX}: the baseline value in milliseconds</li>
 *         <li>{@link #DELTA_VALUE_INDEX}: the difference between the build value and its more recent baseline</li>
 *         <li>{@link #DELTA_ERROR_INDEX}: the error made while computing the difference</li>
 *         <li>{@link #BUILD_ERROR_INDEX}: the error made while measuring the build value</li>
 *         <li>{@link #BASELINE_ERROR_INDEX}: the error made while measuring the baseline value</li>
 *         </ul>
 */
public List getConfigNumbers(String configName, boolean fingerprints, List differences)
{

	// Initialize lists
	AbstractResults[] scenarios = getChildren();
	int length = scenarios.length;

	// Print scenario names line
	List firstLine = new ArrayList();
	for (int i = 0; i < length; i++)
	{
		ScenarioResults scenarioResults = (ScenarioResults) scenarios[i];
		if (!fingerprints || scenarioResults.hasSummary())
		{
			firstLine.add(scenarioResults.getName());
		}
	}

	// Print each build line
	String[] builds = getAllSortedBuildNames(true/* descending order */);
	// int milestoneIndex = 0;
	// String milestoneDate = Util.getMilestoneDate(milestoneIndex);
	String currentBuildName = null;
	int buildsLength = builds.length;
	firstLine.add(0, new Integer(buildsLength));
	differences.add(firstLine);
	for (int i = 0; i < buildsLength; i++)
	{
		List line = new ArrayList();
		String buildName = builds[i];
		line.add(buildName);
		if (!buildName.startsWith(DB_Results.getDbBaselinePrefix()))
		{
			for (int j = 0; j < length; j++)
			{
				ScenarioResults scenarioResults = (ScenarioResults) scenarios[j];
				if (!fingerprints || scenarioResults.hasSummary())
				{
					ConfigResults configResults = scenarioResults.getConfigResults(configName);
					BuildResults buildResults = configResults == null ? null : configResults
							.getBuildResults(buildName);
					if (buildResults == null)
					{
						// no result for this scenario in this build
						line.add(NO_BUILD_RESULTS);
					}
					else
					{
						line.add(configResults.getNumbers(buildResults,
								configResults.getBaselineBuildResults(buildName)));
					}
				}
			}
			differences.add(line);
			if (currentBuildName != null && currentBuildName.charAt(0) != 'N')
			{
			}
			currentBuildName = buildName;
		}
		// if (milestoneDate != null) { // update previous builds
		// int dateComparison = milestoneDate.compareTo(Util.getBuildDate(buildName));
		// if (dateComparison <= 0) {
		// if (dateComparison == 0) {
		// }
		// if (++milestoneIndex == Util.MILESTONES.length) {
		// milestoneDate = null;
		// } else {
		// milestoneDate = Util.getMilestoneDate(milestoneIndex);
		// }
		// }
		// }
	}

	// Write differences lines
	int last = buildsLength - 1;
	String lastBuildName = builds[last];
	while (last > 0 && lastBuildName.startsWith(DB_Results.getDbBaselinePrefix()))
	{
		lastBuildName = builds[--last];
	}
	// appendDifferences(lastBuildName, configName, previousMilestoneName, differences, fingerprints);
	// appendDifferences(lastBuildName, configName, previousBuildName, differences, fingerprints);

	// Return the computed differences
	return differences;
}

/*
 * double[] getConfigNumbers(BuildResults buildResults, BuildResults baselineResults) { if (baselineResults == null)
 * { return INVALID_RESULTS; } double[] values = new double[NUMBERS_LENGTH]; for (int i=0 ;i<NUMBERS_LENGTH; i++) {
 * values[i] = Double.NaN; } double buildValue = buildResults.getValue(); values[BUILD_VALUE_INDEX] = buildValue;
 * double baselineValue = baselineResults.getValue(); values[BASELINE_VALUE_INDEX] = baselineValue; double delta =
 * (baselineValue - buildValue) / baselineValue; values[DELTA_VALUE_INDEX] = delta; if (Double.isNaN(delta)) {
 * return values; } long baselineCount = baselineResults.getCount(); long currentCount = buildResults.getCount(); if
 * (baselineCount > 1 && currentCount > 1) { double baselineError = baselineResults.getError(); double currentError
 * = buildResults.getError(); values[BASELINE_ERROR_INDEX] = baselineError; values[BUILD_ERROR_INDEX] =
 * currentError; values[DELTA_ERROR_INDEX] = Double.isNaN(baselineError) ? currentError / baselineValue :
 * Math.sqrt(baselineError*baselineError + currentError*currentError) / baselineValue; } return values; }
 */

private ScenarioResults getScenarioResults(List scenarios, int searchedId)
{
	int size = scenarios.size();
	for (int i = 0; i < size; i++)
	{
		ScenarioResults scenarioResults = (ScenarioResults) scenarios.get(i);
		if (scenarioResults.id == searchedId)
		{
			return scenarioResults;
		}
	}
	return null;
}

/**
 * Returns a list of scenario results which have a summary
 * 
 * @param global
 *            Indicates whether the summary must be global or not.
 * @param config
 *            Configuration name
 * @return A list of {@link ScenarioResults scenario results} which have a summary
 */
public List getSummaryScenarios(boolean global, String config)
{
	int size = size();
	List scenarios = new ArrayList(size);
	for (int i = 0; i < size; i++)
	{
		ScenarioResults scenarioResults = (ScenarioResults) this.children.get(i);
		ConfigResults configResults = scenarioResults.getConfigResults(config);
		if (configResults != null)
		{
			BuildResults buildResults = configResults.getCurrentBuildResults();
			if ((global && buildResults.summaryKind == 1) || (!global && buildResults.summaryKind >= 0))
			{
				scenarios.add(scenarioResults);
			}
		}
	}
	return scenarios;
}

private String lastBuildName(int kind)
{
	String[] builds = getAllSortedBuildNames();
	int idx = builds.length <= 1 ? 0 : builds.length - 1;
	if (idx > 0)
	{
		idx--;
	}
	String lastBuildName = builds[idx];
	switch (kind)
	{
		case 1: // no ref
			while (lastBuildName.startsWith(DB_Results.getDbBaselinePrefix()))
			{
				lastBuildName = builds[idx--];
			}
			break;
		case 2: // only I-build or M-build
			char ch = lastBuildName.charAt(0);
			while (ch != 'I' && ch != 'M')
			{
				lastBuildName = builds[idx--];
				ch = lastBuildName.charAt(0);
			}
			break;
		default:
			break;
	}
	return lastBuildName;
}

/*
 * Read local file contents and populate the results model with the collected information.
 */
String readLocalFile(File dir, List scenarios) throws FileNotFoundException
{
	// if (!dir.exists()) return null;
	File dataFile = new File(dir, getName() + ".dat"); //$NON-NLS-1$
	if (!dataFile.exists())
		throw new FileNotFoundException();
	DataInputStream stream = null;
	try
	{
		// Read local file info
		stream = new DataInputStream(new BufferedInputStream(new FileInputStream(dataFile)));
		print(" - read local files info"); //$NON-NLS-1$
		String lastBuildName = stream.readUTF(); // first string is the build name

		// Next field is the number of scenarios for the component
		int size = stream.readInt();

		// Then follows all the scenario information
		for (int i = 0; i < size; i++)
		{
			// ... which starts with the scenario id
			int scenario_id = stream.readInt();
			ScenarioResults scenarioResults = scenarios == null ? null : getScenarioResults(scenarios, scenario_id);
			if (scenarioResults == null)
			{
				// this can happen if scenario pattern does not cover all those stored in local data file
				// hence, creates a fake scenario to read the numbers and skip to the next scenario
				scenarioResults = new ScenarioResults(-1, null, null);
				// scenarioResults.parent = this;
				// scenarioResults.readData(stream);
				// Should no longer occur as we get all scenarios from database now
				//				throw new RuntimeException("Unexpected unfound scenario!"); //$NON-NLS-1$
			}
			scenarioResults.parent = this;
			scenarioResults.printStream = this.printStream;
			scenarioResults.readData(stream);
			addChild(scenarioResults, true);
			if (this.printStream != null)
				this.printStream.print('.');
		}
		println();
		println("	=> " + size + " scenarios data were read from file " + dataFile); //$NON-NLS-1$ //$NON-NLS-2$

		// Return last build name stored in the local files
		return lastBuildName;
	}
	catch (IOException ioe)
	{
		println("	!!! " + dataFile + " should be deleted as it contained invalid data !!!"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	finally
	{
		try
		{
			stream.close();
		}
		catch (IOException e)
		{
			// nothing else to do!
		}
	}
	return null;
}

/*
 * Read the database values for a build name and a list of scenarios. The database is read only if the components
 * does not already knows the given build (i.e. if it has not been already read) or if the force arguments is set.
 */
void updateBuild(String buildName, List scenarios, boolean force, File dataDir, SubMonitor subMonitor,
		PerformanceResults.RemainingTimeGuess timeGuess)
{

	// Read all variations
	println("Component '" + this.name + "':"); //$NON-NLS-1$ //$NON-NLS-2$

	// manage monitor
	int size = scenarios.size();
	subMonitor.setWorkRemaining(size + 1);
	StringBuffer buffer = new StringBuffer("Component "); //$NON-NLS-1$
	buffer.append(this.name);
	buffer.append("..."); //$NON-NLS-1$
	String title = buffer.toString();
	subMonitor.subTask(title + timeGuess.display());
	timeGuess.count++;
	subMonitor.worked(1);
	if (subMonitor.isCanceled())
		return;

	// Read new values for the local result
	boolean dirty = false;
	long readTime = System.currentTimeMillis();
	String log = " - read scenarios from DB:"; //$NON-NLS-1$
	if (size > 0)
	{
		for (int i = 0; i < size; i++)
		{

			// manage monitor
			subMonitor.subTask(title + timeGuess.display());
			timeGuess.count++;
			if (log != null)
			{
				println(log);
				log = null;
			}

			// read results
			ScenarioResults nextScenarioResults = (ScenarioResults) scenarios.get(i);
			ScenarioResults scenarioResults = (ScenarioResults) getResults(nextScenarioResults.id);
			if (scenarioResults == null)
			{
				// Scenario is not known yet, force an update
				scenarioResults = nextScenarioResults;
				scenarioResults.parent = this;
				scenarioResults.printStream = this.printStream;
				scenarioResults.updateBuild(buildName, true);
				dirty = true;
				addChild(scenarioResults, true);
			}
			else
			{
				if (scenarioResults.updateBuild(buildName, force))
				{
					dirty = true;
				}
			}
			if (dataDir != null && dirty && (System.currentTimeMillis() - readTime) > 300000)
			{ // save every 5mn
				writeData(buildName, dataDir, true, true);
				dirty = false;
				readTime = System.currentTimeMillis();
			}

			// manage monitor
			subMonitor.worked(1);
			if (subMonitor.isCanceled())
				return;
		}
	}

	// Write local files
	if (dataDir != null)
	{
		writeData(buildName, dataDir, false, dirty);
	}

	// Print global time
	printGlobalTime(readTime);

}

/*
 * Write the component results data to the file '<component name>.dat' in the given directory.
 */
void writeData(String buildName, File dir, boolean temp, boolean dirty)
{
	// if (!dir.exists() && !dir.mkdirs()) {
	//		System.err.println("can't create directory "+dir); //$NON-NLS-1$
	// }
	File tmpFile = new File(dir, getName() + ".tmp"); //$NON-NLS-1$
	File dataFile = new File(dir, getName() + ".dat"); //$NON-NLS-1$
	if (!dirty)
	{ // only possible on final write
		if (tmpFile.exists())
		{
			if (dataFile.exists())
				dataFile.delete();
			tmpFile.renameTo(dataFile);
			println("	=> rename temporary file to " + dataFile); //$NON-NLS-1$
		}
		return;
	}
	if (tmpFile.exists())
	{
		tmpFile.delete();
	}
	File file;
	if (temp)
	{
		file = tmpFile;
	}
	else
	{
		if (dataFile.exists())
		{
			dataFile.delete();
		}
		file = dataFile;
	}
	try
	{
		DataOutputStream stream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
		try
		{
			int size = this.children.size();
			stream.writeUTF(lastBuildName(0));
			stream.writeInt(size);
			for (int i = 0; i < size; i++)
			{
				ScenarioResults scenarioResults = (ScenarioResults) this.children.get(i);
				scenarioResults.write(stream);
			}
		}
		finally
		{
			stream.close();
			println("	=> extracted data " + (temp ? "temporarily " : "") + "written in file " + file); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
	}
	catch (FileNotFoundException e)
	{
		System.err.println("can't create output file" + file); //$NON-NLS-1$
	}
	catch (IOException e)
	{
		e.printStackTrace();
	}
}

}
