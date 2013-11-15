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
package org.eclipse.test.internal.performance.results.db;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.test.internal.performance.InternalDimensions;
import org.eclipse.test.internal.performance.results.utils.Util;

/**
 * Class to handle results for an Eclipse performance test box (called a <i>configuration</i>). It gives access to
 * results for each build on which this configuration has been run.
 * 
 * @see BuildResults
 */
public class ConfigResults extends AbstractResults
{
	BuildResults baseline, current;
	boolean baselined = false, valid = false;
	double delta, error;

	public ConfigResults(AbstractResults parent, int id)
	{
		super(parent, id);
		if (id >= parent.getPerformance().sortedConfigNames.length)
		{
			id = parent.getPerformance().sortedConfigNames.length - 1;
		}
		if (id < 0)
		{
			id = 0;
		}
		this.name = parent.getPerformance().sortedConfigNames[id];
		this.printStream = parent.printStream;
	}

	/*
	 * Complete results with additional database information.
	 */
	void completeResults(String[] builds)
	{
		/* if (this.baseline == null || this.current == null) */initialize();
		ScenarioResults scenarioResults = (ScenarioResults) this.parent;
		DB_Results.queryScenarioSummaries(scenarioResults, this.name, builds);
	}

	/**
	 * Returns the baseline build name used to compare results with.
	 * 
	 * @return The name of the baseline build
	 * @see #getBaselineBuildResults()
	 */
	public String getBaselineBuildName()
	{
		if (this.baseline == null)
		{
			initialize();
		}
		return this.baseline.getName();
	}

	/**
	 * Returns the most recent baseline build results.
	 * 
	 * @return The {@link BuildResults baseline build results}.
	 * @see BuildResults
	 */
	public BuildResults getBaselineBuildResults()
	{
		if (this.baseline == null)
		{
			initialize();
		}
		return this.baseline;
	}

	/**
	 * Return the baseline build results run just before the given build name.
	 * 
	 * @param buildName
	 *            The build name
	 * @return The {@link BuildResults baseline results} preceding the given build name or <code>null</code> if none was
	 *         found.
	 */
	public BuildResults getBaselineBuildResults(String buildName)
	{
		if (this.baseline == null)
		{
			initialize();
		}
		int size = this.children.size();
		String buildDate = Util.getBuildDate(buildName);
		for (int i = size - 1; i >= 0; i--)
		{
			BuildResults buildResults = (BuildResults) this.children.get(i);
			if (buildResults.isBaseline() && buildResults.getDate().compareTo(buildDate) < 0)
			{
				return buildResults;
			}
		}
		return this.baseline;

	}

	/**
	 * Returns the most recent baseline build result value.
	 * 
	 * @return The value of the most recent baseline build results.
	 */
	public double getBaselineBuildValue()
	{
		if (this.baseline == null)
		{
			initialize();
		}
		return this.baseline.getValue();
	}

	/**
	 * Returns the configuration description (currently the box name).
	 * 
	 * @return The configuration description (currently the box name).
	 */
	public String getDescription()
	{
		return getPerformance().sortedConfigDescriptions[this.id];
	}

	/**
	 * Return the results for the given build name.
	 * 
	 * @param buildName
	 *            The build name
	 * @return The {@link BuildResults results} for the given build name or <code>null</code> if none was found.
	 */
	public BuildResults getBuildResults(String buildName)
	{
		return (BuildResults) getResults(buildName);
	}

	/**
	 * Returns the build results matching a given pattern.
	 * 
	 * @param buildPattern
	 *            The pattern of searched builds
	 * @return The list of the builds which names match the given pattern. The list is ordered by build results date.
	 */
	public List getBuilds(String buildPattern)
	{
		List builds = new ArrayList();
		int size = size();
		for (int i = 0; i < size; i++)
		{
			BuildResults buildResults = (BuildResults) this.children.get(i);
			if (buildPattern == null || buildResults.match(buildPattern))
			{
				builds.add(buildResults);
			}
		}
		return builds;
	}

	/**
	 * Returns the build results before a given build name.
	 * 
	 * @param buildName
	 *            Name of the last build (included)
	 * @return The list of the builds which precedes the given build name.
	 */
	public List getBuildsBefore(String buildName)
	{
		String buildDate = Util.getBuildDate(buildName);
		List builds = new ArrayList();
		int size = size();
		for (int i = 0; i < size; i++)
		{
			BuildResults buildResults = (BuildResults) this.children.get(i);
			if (buildName == null || buildResults.getDate().compareTo(buildDate) <= 0)
			{
				builds.add(buildResults);
			}
		}
		return builds;
	}

	/**
	 * Returns a list of build results which names starts with one of the given prefixes.
	 * 
	 * @param prefixes
	 *            List of expected prefixes
	 * @return A list of builds which names start with one of the given patterns.
	 */
	public List getBuildsMatchingPrefixes(List prefixes)
	{
		List builds = new ArrayList();
		int size = size();
		int length = prefixes.size();
		for (int i = 0; i < size; i++)
		{
			AbstractResults buildResults = (AbstractResults) this.children.get(i);
			String buildName = buildResults.getName();
			for (int j = 0; j < length; j++)
			{
				if (buildName.startsWith((String) prefixes.get(j)))
				{
					builds.add(buildResults);
				}
			}
		}
		return builds;
	}

	/**
	 * Get all results numbers for the max last builds.
	 * 
	 * @param max
	 *            The number of last builds to get numbers.
	 * @return An 2 dimensions array of doubles. At the first level of the array each slot represents one build. That
	 *         means that the dimension of the array matches the given numbers as soon as there are enough builds in the
	 *         database.
	 *         <p>
	 *         The slots of the second level are the numbers values:
	 *         <ul>
	 *         <li>{@link #BUILD_VALUE_INDEX}: the build value in milliseconds</li>
	 *         <li>{@link #BASELINE_VALUE_INDEX}: the baseline value in milliseconds</li>
	 *         <li>{@link #DELTA_VALUE_INDEX}: the difference between the build value and its more recent baseline</li>
	 *         <li>{@link #DELTA_ERROR_INDEX}: the error made while computing the difference</li>
	 *         <li>{@link #BUILD_ERROR_INDEX}: the error made while measuring the build value</li>
	 *         <li>{@link #BASELINE_ERROR_INDEX}: the error made while measuring the baseline value</li>
	 *         </ul>
	 */
	public double[][] getLastNumbers(int max)
	{

		// Return null if no previous builds are expected
		if (max <= 0)
			return null;

		// Add numbers for each previous build
		int size = size();
		double[][] numbers = new double[Math.min(max, size)][];
		int n = 0;
		for (int i = size - 1; i >= 0 && n < max; i--)
		{
			BuildResults buildResults = (BuildResults) this.children.get(i);
			if (!buildResults.isBaseline())
			{
				numbers[n] = getNumbers(buildResults, getBaselineBuildResults(buildResults.getName()));
				n++;
			}
		}

		// Return the numbers
		return numbers;
	}

	/**
	 * Returns interesting numbers for the current configuration.
	 * 
	 * @return Values in an array of double:
	 *         <ul>
	 *         <li>{@link AbstractResults#BUILD_VALUE_INDEX}: the build value in milliseconds</li>
	 *         <li>{@link AbstractResults#BASELINE_VALUE_INDEX}: the baseline value in milliseconds</li>
	 *         <li>{@link AbstractResults#DELTA_VALUE_INDEX}: the difference between the build value and its more recent
	 *         baseline</li>
	 *         <li>{@link AbstractResults#DELTA_ERROR_INDEX}: the error made while computing the difference</li>
	 *         <li>{@link AbstractResults#BUILD_ERROR_INDEX}: the error made while measuring the build value</li>
	 *         <li>{@link AbstractResults#BASELINE_ERROR_INDEX}: the error made while measuring the baseline value</li>
	 *         </ul>
	 */
	public double[] getNumbers(BuildResults buildResults, BuildResults baselineResults)
	{
		if (baselineResults == null)
		{
			return null;
		}
		double[] values = new double[NUMBERS_LENGTH];
		for (int i = 0; i < NUMBERS_LENGTH; i++)
		{
			values[i] = Double.NaN;
		}
		double buildValue = buildResults.getValue();
		values[BUILD_VALUE_INDEX] = buildValue;
		double baselineValue = baselineResults.getValue();
		values[BASELINE_VALUE_INDEX] = baselineValue;
		double buildDelta = (baselineValue - buildValue) / baselineValue;
		values[DELTA_VALUE_INDEX] = buildDelta;
		if (Double.isNaN(buildDelta))
		{
			return values;
		}
		long baselineCount = baselineResults.getCount();
		long currentCount = buildResults.getCount();
		if (baselineCount > 1 && currentCount > 1)
		{
			double baselineError = baselineResults.getError();
			double currentError = buildResults.getError();
			values[BASELINE_ERROR_INDEX] = baselineError;
			values[BUILD_ERROR_INDEX] = currentError;
			values[DELTA_ERROR_INDEX] = Double.isNaN(baselineError) ? currentError / baselineValue : Math
					.sqrt(baselineError * baselineError + currentError * currentError) / baselineValue;
		}
		return values;
	}

	/**
	 * Return the deviation value and its associated standard error for the default dimension (currently
	 * {@link InternalDimensions#ELAPSED_PROCESS}).
	 * 
	 * @return an array of double. First number is the deviation itself and the second is the standard error.
	 */
	public double[] getCurrentBuildDeltaInfo()
	{
		if (this.baseline == null || this.current == null)
		{
			initialize();
		}
		return new double[] { this.delta, this.error };
	}

	/**
	 * Returns the error of the current build results
	 * 
	 * @return the error made during the current build measure
	 */
	public double getCurrentBuildError()
	{
		if (this.current == null)
		{
			initialize();
		}
		return this.current.getError();
	}

	/**
	 * Returns the current build name.
	 * 
	 * @return The name of the current build
	 * @see #getCurrentBuildResults()
	 */
	public String getCurrentBuildName()
	{
		if (this.current == null)
		{
			initialize();
		}
		return this.current.getName();
	}

	/**
	 * Returns the current build results.
	 * <p>
	 * This build is currently the last integration or nightly build which has performance results in the database. It
	 * may differ from the {@link PerformanceResults#getName()}.
	 * 
	 * @return The current build results.
	 * @see BuildResults
	 */
	public BuildResults getCurrentBuildResults()
	{
		if (this.current == null)
		{
			initialize();
		}
		return this.current;
	}

	/**
	 * Returns the current build result value.
	 * 
	 * @return The value of the current build results.
	 */
	public double getCurrentBuildValue()
	{
		if (this.current == null)
		{
			initialize();
		}
		return this.current.getValue();
	}

	/**
	 * Returns the delta between current and baseline builds results.
	 * 
	 * @return the delta
	 */
	public double getDelta()
	{
		if (this.baseline == null || this.current == null)
		{
			initialize();
		}
		return this.delta;
	}

	/**
	 * Returns the standard error of the delta between current and baseline builds results.
	 * 
	 * @return the delta
	 * @see #getDelta()
	 */
	public double getError()
	{
		if (this.baseline == null || this.current == null)
		{
			initialize();
		}
		return this.error;
	}

	/**
	 * Return the name of the machine associated with the current config.
	 * 
	 * @return The name of the machine.
	 */
	public String getLabel()
	{
		return this.parent.getPerformance().sortedConfigDescriptions[this.id];
	}

	/**
	 * Get all dimension builds default dimension statistics for all builds.
	 * 
	 * @return An array of double built as follows:
	 *         <ul>
	 *         <li>0: numbers of values</li>
	 *         <li>1: mean of values</li>
	 *         <li>2: standard deviation of these values</li>
	 *         <li>3: coefficient of variation of these values</li>
	 *         </ul>
	 */
	public double[] getStatistics()
	{
		return getStatistics(Util.ALL_BUILD_PREFIXES, DB_Results.getDefaultDimension().getId());
	}

	/**
	 * Get all dimension builds default dimension statistics for a given list of build prefixes.
	 * 
	 * @param prefixes
	 *            List of prefixes to filter builds. If <code>null</code> then all the builds are taken to compute
	 *            statistics.
	 * @return An array of double built as follows:
	 *         <ul>
	 *         <li>0: numbers of values</li>
	 *         <li>1: mean of values</li>
	 *         <li>2: standard deviation of these values</li>
	 *         <li>3: coefficient of variation of these values</li>
	 *         </ul>
	 */
	public double[] getStatistics(List prefixes)
	{
		return getStatistics(prefixes, DB_Results.getDefaultDimension().getId());
	}

	/**
	 * Get all dimension builds statistics for a given list of build prefixes and a given dimension.
	 * 
	 * @param prefixes
	 *            List of prefixes to filter builds. If <code>null</code> then all the builds are taken to compute
	 *            statistics.
	 * @param dim_id
	 *            The id of the dimension on which the statistics must be computed
	 * @return An array of double built as follows:
	 *         <ul>
	 *         <li>0: numbers of values</li>
	 *         <li>1: mean of values</li>
	 *         <li>2: standard deviation of these values</li>
	 *         <li>3: coefficient of variation of these values</li>
	 *         </ul>
	 */
	public double[] getStatistics(List prefixes, int dim_id)
	{
		int size = size();
		int length = prefixes == null ? 0 : prefixes.size();
		int count = 0;
		double mean = 0, stddev = 0, variation = 0;
		double[] values = new double[size];
		count = 0;
		mean = 0.0;
		for (int i = 0; i < size; i++)
		{
			BuildResults buildResults = (BuildResults) this.children.get(i);
			String buildName = buildResults.getName();
			if (isBuildConcerned(buildResults))
			{
				if (prefixes == null)
				{
					double value = buildResults.getValue(dim_id);
					values[count] = value;
					mean += value;
					count++;
				}
				else
				{
					for (int j = 0; j < length; j++)
					{
						if (buildName.startsWith((String) prefixes.get(j)))
						{
							double value = buildResults.getValue(dim_id);
							values[count] = value;
							mean += value;
							count++;
						}
					}
				}
			}
		}
		mean /= count;
		for (int i = 0; i < count; i++)
		{
			stddev += Math.pow(values[i] - mean, 2);
		}
		stddev = Math.sqrt((stddev / (count - 1)));
		variation = stddev / mean;
		return new double[] { count, mean, stddev, variation };
	}

	private void initialize()
	{
		reset();
		// Get performance results builds name
		PerformanceResults perfResults = getPerformance();
		String baselineBuildName = perfResults.getBaselineName();
		String baselineBuildDate = baselineBuildName == null ? null : Util.getBuildDate(baselineBuildName);
		String currentBuildName = perfResults.name;
		String currentBuildDate = currentBuildName == null ? null : Util.getBuildDate(currentBuildName);

		// Set baseline and current builds
		BuildResults lastBaseline = null;
		int size = size();
		if (size == 0)
			return;
		for (int i = 0; i < size; i++)
		{
			BuildResults buildResults = (BuildResults) this.children.get(i);
			if (buildResults.values != null)
			{
				buildResults.cleanValues();
			}
			if (buildResults.isBaseline())
			{
				if (lastBaseline == null || baselineBuildDate == null
						|| baselineBuildDate.compareTo(buildResults.getDate()) >= 0)
				{
					lastBaseline = buildResults;
				}
				if (baselineBuildName != null && buildResults.getName().equals(baselineBuildName))
				{
					this.baseline = buildResults;
					this.baselined = true;
				}
			}
			else if (currentBuildName == null || currentBuildDate == null
					|| (this.current == null && buildResults.getDate().compareTo(currentBuildDate) >= 0))
			{
				this.current = buildResults;
				this.valid = true;
			}
		}
		if (this.baseline == null)
		{
			this.baseline = (lastBaseline == null) ? (BuildResults) this.children.get(0) : lastBaseline;
		}
		if (this.current == null)
		{
			int idx = size() - 1;
			BuildResults previous = (BuildResults) this.children.get(idx--);
			while (idx >= 0 && previous.isBaseline())
			{
				previous = (BuildResults) this.children.get(idx--);
			}
			this.current = previous;
		}

		// Set delta between current vs. baseline and the corresponding error
		int dim_id = DB_Results.getDefaultDimension().getId();
		double baselineValue = this.baseline.getValue();
		double currentValue = this.current.getValue();
		this.delta = (currentValue - baselineValue) / baselineValue;
		if (Double.isNaN(this.delta))
		{
			this.error = Double.NaN;
		}
		else
		{
			long baselineCount = this.baseline.getCount(dim_id);
			long currentCount = this.current.getCount(dim_id);
			if (baselineCount == 1 || currentCount == 1)
			{
				this.error = Double.NaN;
			}
			else
			{
				double baselineError = this.baseline.getError(dim_id);
				double currentError = this.current.getError(dim_id);
				this.error = Double.isNaN(baselineError) ? currentError / baselineValue : Math.sqrt(baselineError
						* baselineError + currentError * currentError)
						/ baselineValue;
			}
		}

		// Set the failure on the current build if necessary
		int failure_threshold = getPerformance().failure_threshold;
		if (this.delta >= (failure_threshold / 100.0))
		{
			StringBuffer buffer = new StringBuffer("Performance criteria not met when compared to '"); //$NON-NLS-1$
			buffer.append(this.baseline.getName());
			buffer.append("': "); //$NON-NLS-1$
			buffer.append(DB_Results.getDefaultDimension().getName());
			buffer.append("= "); //$NON-NLS-1$
			buffer.append(Util.timeString((long) this.current.getValue()));
			buffer.append(" is not within [0%, "); //$NON-NLS-1$
			buffer.append(100 + failure_threshold);
			buffer.append("'%] of "); //$NON-NLS-1$
			buffer.append(Util.timeString((long) this.baseline.getValue()));
			this.current.setFailure(buffer.toString());
		}
	}

	/**
	 * Returns whether the configuration has results for the performance baseline build or not.
	 * 
	 * @return <code>true</code> if the configuration has results for the performance baseline build, <code>false</code>
	 *         otherwise.
	 */
	public boolean isBaselined()
	{
		if (this.baseline == null || this.current == null)
		{
			initialize();
		}
		return this.baselined;
	}

	boolean isBuildConcerned(BuildResults buildResults)
	{
		String buildDate = buildResults.getDate();
		String currentBuildDate = getCurrentBuildResults() == null ? null : getCurrentBuildResults().getDate();
		String baselineBuildDate = getBaselineBuildResults() == null ? null : getBaselineBuildResults().getDate();
		return ((currentBuildDate == null || buildDate.compareTo(currentBuildDate) <= 0) && (baselineBuildDate == null || buildDate
				.compareTo(baselineBuildDate) <= 0));
	}

	/**
	 * Returns whether the configuration has results for the performance current build or not.
	 * 
	 * @return <code>true</code> if the configuration has results for the performance current build, <code>false</code>
	 *         otherwise.
	 */
	public boolean isValid()
	{
		if (this.baseline == null || this.current == null)
		{
			initialize();
		}
		return this.valid;
	}

	/**
	 * Returns the 'n' last nightly build names.
	 * 
	 * @param n
	 *            Number of last nightly builds to return
	 * @return Last n nightly build names preceding current.
	 */
	public List lastNightlyBuildNames(int n)
	{
		List labels = new ArrayList();
		for (int i = size() - 2; i >= 0; i--)
		{
			BuildResults buildResults = (BuildResults) this.children.get(i);
			if (isBuildConcerned(buildResults))
			{
				String buildName = buildResults.getName();
				if (buildName.startsWith("N")) { //$NON-NLS-1$
					labels.add(buildName);
					if (labels.size() >= n)
					{
						break;
					}
				}
			}
		}
		return labels;
	}

	/*
	 * Read all configuration builds results data from the given stream.
	 */
	void readData(DataInputStream stream) throws IOException
	{
		int size = stream.readInt();
		for (int i = 0; i < size; i++)
		{
			BuildResults buildResults = new BuildResults(this);
			buildResults.readData(stream);
			String lastBuildName = getPerformance().lastBuildName;
			if (lastBuildName == null || buildResults.getDate().compareTo(Util.getBuildDate(lastBuildName)) <= 0)
			{
				addChild(buildResults, true);
			}
		}
	}

	private void reset()
	{
		this.current = null;
		this.baseline = null;
		this.baselined = false;
		this.valid = false;
		this.delta = 0;
		this.error = -1;
	}

	/*
	 * Set the configuration value from database information
	 */
	void setInfos(int build_id, int summaryKind, String comment)
	{
		BuildResults buildResults = (BuildResults) getResults(build_id);
		if (buildResults == null)
		{
			buildResults = new BuildResults(this, build_id);
			addChild(buildResults, true);
		}
		buildResults.summaryKind = summaryKind;
		buildResults.comment = comment;
	}

	/*
	 * Set the configuration value from database information
	 */
	void setValue(int build_id, int dim_id, int step, long value)
	{
		// reset();
		BuildResults buildResults = (BuildResults) getResults(build_id);
		if (buildResults == null)
		{
			buildResults = new BuildResults(this, build_id);
			addChild(buildResults, true);
		}
		buildResults.setValue(dim_id, step, value);
	}

	/*
	 * Write all configuration builds results data into the given stream.
	 */
	void write(DataOutputStream stream) throws IOException
	{
		int size = size();
		stream.writeInt(this.id);
		stream.writeInt(size);
		for (int i = 0; i < size; i++)
		{
			BuildResults buildResults = (BuildResults) this.children.get(i);
			buildResults.write(stream);
		}
	}

}
