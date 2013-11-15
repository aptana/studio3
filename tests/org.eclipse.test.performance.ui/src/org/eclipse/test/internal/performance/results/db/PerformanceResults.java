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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.test.internal.performance.results.utils.Util;

/**
 * Root class to handle performance results. Usually performance results are built for a current build vs. a baseline
 * build. This class allow to read all data from releng performance database for given configurations and scenario
 * pattern. Then it provides easy and speedy access to all stored results.
 */
public class PerformanceResults extends AbstractResults
{

	String[] allBuildNames = null;
	Map allScenarios;
	String lastBuildName; // Name of the last used build
	String baselineName; // Name of the baseline build used for comparison
	String baselinePrefix;
	private String scenarioPattern = "%"; //$NON-NLS-1$
	private String[] components;
	String[] configNames, sortedConfigNames;
	String[] configDescriptions, sortedConfigDescriptions;
	private String configPattern;

	boolean dbRequired;
	boolean needToUpdateLocalFile;

	/*
	 * Local class helping to guess remaining time while reading results from DB
	 */
	class RemainingTimeGuess
	{
		int all, count;
		long start;
		double remaining;

		RemainingTimeGuess(int all, long start)
		{
			this.all = all;
			this.start = start;
		}

		String display()
		{
			StringBuffer buffer = new StringBuffer(" [elapsed: "); //$NON-NLS-1$
			long elapsed = getElapsed();
			buffer.append(Util.timeChrono(elapsed));
			if (this.count > 0)
			{
				buffer.append(" | left: "); //$NON-NLS-1$
				long remainingTime = getRemainingTime(elapsed);
				buffer.append(Util.timeChrono(remainingTime));
				buffer.append(" | end: "); //$NON-NLS-1$
				buffer.append(Util.timeEnd(remainingTime));
			}
			buffer.append(']');
			return buffer.toString();
		}

		private long getRemainingTime(long elapsed)
		{
			return (long) ((((double) elapsed) / this.count) * (this.all - this.count));
		}

		private long getElapsed()
		{
			return System.currentTimeMillis() - this.start;
		}
	}

	// Failure threshold
	public static final int DEFAULT_FAILURE_THRESHOLD = 10;
	int failure_threshold = DEFAULT_FAILURE_THRESHOLD;

	public PerformanceResults(PrintStream stream)
	{
		super(null, null);
		this.printStream = stream;
		this.dbRequired = false;
		setDefaults();
	}

	public PerformanceResults(String name, String baseline, String baselinePrefix, PrintStream stream)
	{
		super(null, name);
		this.baselineName = baseline;
		this.baselinePrefix = baselinePrefix;
		this.printStream = stream;
		this.dbRequired = true;
		setDefaults();
	}

	/**
	 * Returns the list of all builds currently read.
	 * 
	 * @return The names list of all currently known builds
	 */
	public String[] getAllBuildNames()
	{
		if (this.allBuildNames == null)
		{
			setAllBuildNames();
		}
		return this.allBuildNames;
	}

	/**
	 * Returns the name of the baseline used for extracted results
	 * 
	 * @return The build name of the baseline of <code>null</code> if no specific baseline is used for the extracted
	 *         results.
	 */
	public String getBaselineName()
	{
		return this.baselineName;
	}

	/*
	 * Get the baseline prefix (computed from #baselineName).
	 */
	String getBaselinePrefix()
	{
		return this.baselinePrefix;
	}

	/*
	 * Get the build date (see #getBuildDate(String, String)).
	 */
	public String getBuildDate()
	{
		String buildName = getName();
		if (buildName == null)
			return ""; //$NON-NLS-1$
		return Util.getBuildDate(getName(), getBaselinePrefix());
	}

	/**
	 * Return the list of components concerned by performance results.
	 * 
	 * @return The list of the components
	 */
	public String[] getComponents()
	{
		return this.components;
	}

	/**
	 * Get the scenarios of a given component.
	 * 
	 * @param componentName
	 *            The component name. Should not be <code>null</code>
	 * @return A list of {@link ScenarioResults scenario results}
	 */
	public List getComponentScenarios(String componentName)
	{
		ComponentResults componentResults = (ComponentResults) getResults(componentName);
		if (componentResults == null)
			return null;
		return Collections.unmodifiableList(componentResults.children);
	}

	/**
	 * Get the scenarios which have a summary for a given component.
	 * 
	 * @param componentName
	 *            The component name
	 * @param config
	 *            Configuration name
	 * @return A list of {@link ScenarioResults scenario results} which have a summary
	 */
	public List getComponentSummaryScenarios(String componentName, String config)
	{
		if (componentName == null)
		{
			int size = size();
			List scenarios = new ArrayList();
			for (int i = 0; i < size; i++)
			{
				ComponentResults componentResults = (ComponentResults) this.children.get(i);
				scenarios.addAll(componentResults.getSummaryScenarios(true, config));
			}
			return scenarios;
		}
		ComponentResults componentResults = (ComponentResults) getResults(componentName);
		return componentResults.getSummaryScenarios(false, config);
	}

	/**
	 * Return the configuration boxes considered for this performance results sorted or not depending on the given flag.
	 * 
	 * @param sort
	 *            Indicates whether the list must be sorted or not. The order is defined by the configuration names, not
	 *            by the box names
	 * @return The list of configuration boxes sorted by configuration names
	 */
	public String[] getConfigBoxes(boolean sort)
	{
		return sort ? this.sortedConfigDescriptions : this.configDescriptions;
	}

	/**
	 * Return the configuration names considered for this performance results sorted or not depending on the given flag.
	 * 
	 * @param sort
	 *            Indicates whether the list must be sorted or not
	 * @return The list of configuration names
	 */
	public String[] getConfigNames(boolean sort)
	{
		return sort ? this.sortedConfigNames : this.configNames;
	}

	/*
	 * Compute a SQL pattern from all stored configuration names. For example 'eclipseperflnx1', 'eclipseperflnx2' and
	 * 'eclipseperflnx3' will return 'eclipseperflnx_'.
	 */
	String getConfigurationsPattern()
	{
		if (this.configPattern == null)
		{
			int length = this.sortedConfigNames == null ? 0 : this.sortedConfigNames.length;
			if (length == 0)
				return null;
			this.configPattern = this.sortedConfigNames[0];
			int refLength = this.configPattern.length();
			for (int i = 1; i < length; i++)
			{
				String config = this.sortedConfigNames[i];
				StringBuffer newConfig = null;
				if (refLength != config.length())
					return null; // strings have not the same length => cannot find a pattern
				for (int j = 0; j < refLength; j++)
				{
					char c = this.configPattern.charAt(j);
					if (config.charAt(j) != c)
					{
						if (newConfig == null)
						{
							newConfig = new StringBuffer(refLength);
							if (j == 0)
								return null; // first char is already different => cannot find a pattern
							newConfig.append(this.configPattern.substring(0, j));
						}
						newConfig.append('_');
					}
					else if (newConfig != null)
					{
						newConfig.append(c);
					}
				}
				if (newConfig != null)
				{
					this.configPattern = newConfig.toString();
				}
			}
		}
		return this.configPattern;
	}

	/**
	 * Return the name of the last build name except baselines.
	 * 
	 * @return the name of the last build
	 */
	public String getLastBuildName()
	{
		return getLastBuildName(1/* all except baselines */);
	}

	/**
	 * Return the name of the last build name
	 * 
	 * @param kind
	 *            Decide what kind of build is taken into account 0: all kind of build 1: all except baseline builds 2:
	 *            all except baseline and nightly builds 3: only integration builds
	 * @return the name of the last build of the selected kind
	 */
	public String getLastBuildName(int kind)
	{
		if (this.name == null)
		{
			getAllBuildNames(); // init build names if necessary
			int idx = this.allBuildNames.length - 1;
			this.name = this.allBuildNames[idx];
			if (kind > 0)
			{
				loop: while (idx-- >= 0)
				{
					switch (this.name.charAt(0))
					{
						case 'N':
							if (kind < 2)
								break loop;
							break;
						case 'M':
							if (kind < 3)
								break loop;
							break;
						case 'I':
							if (kind < 4)
								break loop;
							break;
					}
					this.name = this.allBuildNames[idx];
				}
			}
		}
		return this.name;
	}

	public String getName()
	{
		if (this.name == null)
		{
			setAllBuildNames();
		}
		return this.name;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.test.internal.performance.results.AbstractResults#getPerformance()
	 */
	PerformanceResults getPerformance()
	{
		return this;
	}

	/**
	 * Get the results of a given scenario.
	 * 
	 * @param scenarioName
	 *            The scenario name
	 * @return The {@link ScenarioResults scenario results}
	 */
	public ScenarioResults getScenarioResults(String scenarioName)
	{
		ComponentResults componentResults = (ComponentResults) getResults(DB_Results
				.getComponentNameFromScenario(scenarioName));
		return componentResults == null ? null : (ScenarioResults) componentResults.getResults(scenarioName);
	}

	/*
	 * Init configurations from performance results database.
	 */
	private void initConfigs()
	{
		// create config names
		this.configNames = DB_Results.getConfigs();
		this.configDescriptions = DB_Results.getConfigDescriptions();
		int length = this.configNames.length;
		this.sortedConfigNames = new String[length];
		for (int i = 0; i < length; i++)
		{
			this.sortedConfigNames[i] = this.configNames[i];
		}

		// Sort the config names
		Arrays.sort(this.sortedConfigNames);
		this.sortedConfigDescriptions = new String[length];
		for (int i = 0; i < length; i++)
		{
			for (int j = 0; j < length; j++)
			{
				if (this.sortedConfigNames[i] == this.configNames[j])
				{ // == is intentional!
					this.sortedConfigDescriptions[i] = this.configDescriptions[j];
					break;
				}
			}
		}
	}

	/*
	 * Read or update data for a build from a directory supposed to have local files.
	 */
	private String[] read(boolean local, String buildName, String[][] configs, boolean force, File dataDir,
			String taskName, SubMonitor subMonitor)
	{
		if (local && dataDir == null)
		{
			throw new IllegalArgumentException("Must specify a directory to read local files!"); //$NON-NLS-1$
		}
		subMonitor.setWorkRemaining(100);

		// Update info
		long start = System.currentTimeMillis();
		int allScenariosSize;
		if (DB_Results.DB_CONNECTION)
		{
			try
			{
				// Read all scenarios
				allScenariosSize = readScenarios(buildName, subMonitor.newChild(10));
				if (allScenariosSize < 0)
				{
					return null;
				}

				// Read all builds
				DB_Results.queryAllVariations(getConfigurationsPattern());

				// Refresh configs
				if (configs == null)
				{
					initConfigs();
				}
				else
				{
					setConfigInfo(configs);
				}
			}
			catch (OperationCanceledException e)
			{
				return null;
			}
		}
		else
		{
			if (this.allScenarios == null)
				return null;
			allScenariosSize = this.allScenarios.size();
			if (configs != null)
			{
				setConfigInfo(configs);
			}
		}

		// Create corresponding children
		int componentsLength = this.components.length;
		subMonitor.setWorkRemaining(componentsLength);
		RemainingTimeGuess timeGuess = null;
		for (int i = 0; i < componentsLength; i++)
		{
			String componentName = this.components[i];
			List scenarios = this.allScenarios == null ? null : (List) this.allScenarios.get(componentName);

			// Manage monitor
			int percentage = (int) ((((double) (i + 1)) / (componentsLength + 1)) * 100);
			StringBuffer tnBuffer = taskName == null ? new StringBuffer() : new StringBuffer(taskName);
			tnBuffer.append(" ("); //$NON-NLS-1$
			if (buildName != null)
			{
				tnBuffer.append(buildName).append(": "); //$NON-NLS-1$
			}
			tnBuffer.append(percentage).append("%)"); //$NON-NLS-1$
			subMonitor.setTaskName(tnBuffer.toString());
			StringBuffer subTaskBuffer = new StringBuffer("Component "); //$NON-NLS-1$
			subTaskBuffer.append(componentName);
			subTaskBuffer.append("..."); //$NON-NLS-1$
			subMonitor.subTask(subTaskBuffer.toString());

			// Get component results
			if (scenarios == null && !local)
				continue;
			ComponentResults componentResults;
			if (local || (buildName == null && force))
			{
				componentResults = new ComponentResults(this, componentName);
				addChild(componentResults, true);
			}
			else
			{
				componentResults = (ComponentResults) getResults(componentName);
				if (componentResults == null)
				{
					componentResults = new ComponentResults(this, componentName);
					addChild(componentResults, true);
				}
			}

			// Read the component results
			if (local)
			{
				try
				{
					componentResults.readLocalFile(dataDir, scenarios);
				}
				catch (FileNotFoundException ex)
				{
					return null;
				}
				subMonitor.worked(1);
			}
			else
			{
				if (timeGuess == null)
				{
					timeGuess = new RemainingTimeGuess(1 + componentsLength + allScenariosSize, start);
				}
				componentResults.updateBuild(buildName, scenarios, force, dataDir, subMonitor.newChild(1), timeGuess);
			}
			if (subMonitor.isCanceled())
				return null;
		}

		// Update names
		setAllBuildNames();
		writeData(dataDir);

		// Print time
		printGlobalTime(start);

		return this.allBuildNames;
	}

	/**
	 * Read all data from performance database for the given configurations and scenario pattern. This method is
	 * typically called when generated performance results from a non-UI application.
	 * 
	 * @param buildName
	 *            The name of the build
	 * @param configs
	 *            All configurations to extract results. If <code>null</code>, then all known configurations (
	 *            {@link DB_Results#getConfigs()}) are read.
	 * @param pattern
	 *            The pattern of the concerned scenarios
	 * @param dataDir
	 *            The directory where data will be read/stored locally. If <code>null</code>, then database will be read
	 *            instead and no storage will be performed
	 * @param threshold
	 *            The failure percentage threshold over which a build result value compared to the baseline is
	 *            considered as failing.
	 * @param monitor
	 *            The progress monitor
	 * @return All known build names
	 */
	public String[] readAll(String buildName, String[][] configs, String pattern, File dataDir, int threshold,
			IProgressMonitor monitor)
	{

		// Init
		this.scenarioPattern = pattern == null ? "%" : pattern; //$NON-NLS-1$
		this.failure_threshold = threshold;
		SubMonitor subMonitor = SubMonitor.convert(monitor, 1000);

		// Set default names
		setDefaults();

		// Read local data files first
		reset(dataDir);
		String[] names = null;// read(true, null, configs, true, dataDir, null, subMonitor.newChild(100));
		if (names == null)
		{
			// if one local files is missing then force a full DB read!
			// TODO moderate this to force the DB read only for the missing file...
			return read(false, buildName, configs, true, dataDir, null, subMonitor.newChild(900));
		}

		// Search build name in read data
		boolean buildMissing = true;
		if (buildName != null)
		{
			this.name = buildName;
			buildMissing = Arrays.binarySearch(names, buildName, Util.BUILD_DATE_COMPARATOR) < 0;
		}

		// Look for missing builds
		if (buildMissing)
		{
			String[] builds = DB_Results.getBuilds();
			Arrays.sort(builds, Util.BUILD_DATE_COMPARATOR);
			for (int i = builds.length - 1; i >= 0; i--)
			{
				if (Arrays.binarySearch(names, builds[i], Util.BUILD_DATE_COMPARATOR) >= 0)
				{
					break;
				}
				read(false, builds[i], configs, true, dataDir, null, subMonitor.newChild(900));
			}
		}
		return this.allBuildNames;
	}

	/**
	 * Read all data from performance database for the given configurations and scenario pattern. Note that calling this
	 * method flush all previous read data.
	 * 
	 * @param dataDir
	 *            The directory where local files are located
	 * @param monitor
	 *            The progress monitor
	 */
	public void readLocal(File dataDir, IProgressMonitor monitor)
	{

		// Print title
		String taskName = "Read local performance results"; //$NON-NLS-1$
		println(taskName);

		// Create monitor
		SubMonitor subMonitor = SubMonitor.convert(monitor, 1000);
		subMonitor.setTaskName(taskName);

		// Read
		reset(dataDir);
		read(true, null, null, true, dataDir, taskName, subMonitor);
	}

	void readLocalFile(File dir)
	{
		if (!dir.exists())
			return;
		File dataFile = new File(dir, "performances.dat"); //$NON-NLS-1$
		if (!dataFile.exists())
			return;
		DataInputStream stream = null;
		try
		{
			// Read local file info
			stream = new DataInputStream(new BufferedInputStream(new FileInputStream(dataFile)));

			// Read build info
			String str = stream.readUTF();
			this.needToUpdateLocalFile = this.name == null
					|| Util.getBuildDate(this.name).compareTo(Util.getBuildDate(str)) > 0;
			if (this.name != null && Util.getBuildDate(this.name).compareTo(Util.getBuildDate(str)) >= 0)
			{
				return;
			}
			println(" - read performance results local files info: "); //$NON-NLS-1$
			println("		+ name : " + str);
			this.name = str == "" ? null : str;
			str = stream.readUTF();
			println("		+ baseline : " + str);
			if (this.baselineName == null)
			{
				this.baselineName = str == "" ? null : str;
			}
			str = stream.readUTF();
			println("		+ baseline prefix: " + str);
			this.baselinePrefix = str == "" ? null : str;

			// Write configs info
			int length = stream.readInt();
			println("		+ " + length + " configs");
			this.configNames = new String[length];
			this.sortedConfigNames = new String[length];
			this.configDescriptions = new String[length];
			this.sortedConfigDescriptions = new String[length];
			for (int i = 0; i < length; i++)
			{
				this.configNames[i] = stream.readUTF();
				this.sortedConfigNames[i] = this.configNames[i];
				this.configDescriptions[i] = stream.readUTF();
				this.sortedConfigDescriptions[i] = this.configDescriptions[i];
			}
			DB_Results.setConfigs(this.configNames);
			DB_Results.setConfigDescriptions(this.configDescriptions);

			// Write builds info
			length = stream.readInt();
			println("		+ " + length + " builds");
			this.allBuildNames = new String[length];
			for (int i = 0; i < length; i++)
			{
				this.allBuildNames[i] = stream.readUTF();
			}

			// Write scenarios info
			length = stream.readInt();
			println("		+ " + length + " components");
			this.components = new String[length];
			this.allScenarios = new HashMap();
			for (int i = 0; i < length; i++)
			{
				this.components[i] = stream.readUTF();
				int size = stream.readInt();
				List scenarios = new ArrayList(size);
				for (int j = 0; j < size; j++)
				{
					scenarios.add(new ScenarioResults(stream.readInt(), stream.readUTF(), stream.readUTF()));
				}
				this.allScenarios.put(this.components[i], scenarios);
			}
			println("	=> read from file " + dataFile); //$NON-NLS-1$
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
	}

	private int readScenarios(String buildName, SubMonitor subMonitor) throws OperationCanceledException
	{
		subMonitor.setWorkRemaining(10);
		long start = System.currentTimeMillis();
		String titleSuffix;
		if (buildName == null)
		{
			titleSuffix = "all database scenarios..."; //$NON-NLS-1$
		}
		else
		{
			titleSuffix = "all database scenarios for " + buildName + " build..."; //$NON-NLS-1$ //$NON-NLS-2$
		}
		print("	+ get " + titleSuffix); //$NON-NLS-1$
		subMonitor.subTask("Get " + titleSuffix); //$NON-NLS-1$
		this.allScenarios = DB_Results.queryAllScenarios(this.scenarioPattern, buildName);
		if (this.allScenarios == null)
			return -1;
		int allScenariosSize = 0;
		List componentsSet = new ArrayList(this.allScenarios.keySet());
		Collections.sort(componentsSet);
		int componentsSize = componentsSet.size();
		componentsSet.toArray(this.components = new String[componentsSize]);
		for (int i = 0; i < componentsSize; i++)
		{
			String componentName = this.components[i];
			List scenarios = (List) this.allScenarios.get(componentName);
			allScenariosSize += scenarios.size();
		}
		println(" -> " + allScenariosSize + " found in " + (System.currentTimeMillis() - start) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		subMonitor.worked(10);
		if (subMonitor.isCanceled())
			throw new OperationCanceledException();
		return allScenariosSize;
	}

	void reset(File dataDir)
	{
		this.allBuildNames = null;
		this.children = new ArrayList();
		// this.name = null;
		this.components = null;
		this.allScenarios = null;
		readLocalFile(dataDir);
	}

	private void setAllBuildNames()
	{
		SortedSet builds = new TreeSet(Util.BUILD_DATE_COMPARATOR);
		int size = size();
		if (size == 0)
			return;
		for (int i = 0; i < size; i++)
		{
			ComponentResults componentResults = (ComponentResults) this.children.get(i);
			Set names = componentResults.getAllBuildNames();
			builds.addAll(names);
		}
		int buildsSize = builds.size();
		this.allBuildNames = new String[buildsSize];
		if (buildsSize > 0)
		{
			int n = 0;
			Iterator buildNames = builds.iterator();
			while (buildNames.hasNext())
			{
				String buildName = (String) buildNames.next();
				if (this.lastBuildName == null
						|| Util.getBuildDate(buildName).compareTo(Util.getBuildDate(this.lastBuildName)) <= 0)
				{
					this.allBuildNames[n++] = buildName;
				}
			}
			if (n < buildsSize)
			{
				System.arraycopy(this.allBuildNames, 0, this.allBuildNames = new String[n], 0, n);
			}
			int idx = n - 1;
			String lastBuild = this.allBuildNames[idx--];
			while (idx > 0 && lastBuild.startsWith(DB_Results.getDbBaselinePrefix()))
			{
				lastBuild = this.allBuildNames[idx--];
			}
			this.needToUpdateLocalFile = this.name == null
					|| Util.getBuildDate(lastBuild).compareTo(Util.getBuildDate(this.name)) > 0;
			this.name = lastBuild;
			if (this.baselineName != null)
			{
				String lastBuildDate = Util.getBuildDate(lastBuild);
				if (Util.getBuildDate(this.baselineName).compareTo(lastBuildDate) > 0)
				{
					this.baselineName = DB_Results.getLastBaselineBuild(lastBuildDate);
				}
			}
		}
	}

	private void setConfigInfo(String[][] configs)
	{
		if (configs == null)
			return;

		// Store config information
		int length = configs.length;
		this.configNames = new String[length];
		this.sortedConfigNames = new String[length];
		this.configDescriptions = new String[length];
		for (int i = 0; i < length; i++)
		{
			this.configNames[i] = this.sortedConfigNames[i] = configs[i][0];
			this.configDescriptions[i] = configs[i][1];
		}

		// Sort the config names
		Arrays.sort(this.sortedConfigNames);
		length = this.sortedConfigNames.length;
		this.sortedConfigDescriptions = new String[length];
		for (int i = 0; i < length; i++)
		{
			for (int j = 0; j < length; j++)
			{
				if (this.sortedConfigNames[i] == this.configNames[j])
				{ // == is intentional!
					this.sortedConfigDescriptions[i] = this.configDescriptions[j];
					break;
				}
			}
		}
	}

	/**
	 * Set the name of the baseline used for extracted results
	 * 
	 * @param buildName
	 *            The name of the baseline build
	 */
	public void setBaselineName(String buildName)
	{
		this.baselineName = buildName;
		if (this.baselinePrefix == null || !this.baselineName.startsWith(this.baselinePrefix))
		{
			// Usually hat baseline name format is *always* x.y_yyyyMMddhhmm_yyyyMMddhhmm
			int index = this.baselineName.lastIndexOf('_');
			if (index > 0)
			{
				this.baselinePrefix = this.baselineName.substring(0, index);
			}
			else
			{
				// this.baselinePrefix = DB_Results.getDbBaselinePrefix();
				this.baselinePrefix = this.baselineName;
			}
		}
	}

	private void setDefaults()
	{

		// Set builds if none
		if (size() == 0 && DB_Results.DB_CONNECTION)
		{
			this.allBuildNames = DB_Results.getBuilds();
			this.components = DB_Results.getComponents();
			initConfigs();
		}

		// Set name if null
		if (this.name == null)
		{
			setAllBuildNames();
			if (this.name == null)
			{ // does not know any build
				this.name = DB_Results.getLastCurrentBuild();
				if (this.dbRequired)
				{
					if (this.name == null)
					{
						//					throw new RuntimeException("Cannot find any current build!"); //$NON-NLS-1$
						this.name = "No current build!";
					}
					this.allBuildNames = DB_Results.getBuilds();
					this.components = DB_Results.getComponents();
					initConfigs();
				}
				if (this.printStream != null)
				{
					this.printStream.println("	+ no build specified => use last one: " + this.name); //$NON-NLS-1$
				}
			}
		}

		// Init baseline name if not set
		if (this.baselineName == null && getName() != null)
		{
			String buildDate = Util.getBuildDate(getName());
			this.baselineName = DB_Results.getLastBaselineBuild(buildDate);
			if (this.baselineName == null && this.dbRequired)
			{
				throw new RuntimeException("Cannot find any baseline to refer!"); //$NON-NLS-1$
			}
			if (this.printStream != null)
			{
				this.printStream.println("	+ no baseline specified => use last one: " + this.baselineName); //$NON-NLS-1$
			}
		}

		// Init baseline prefix if not set
		if (this.baselineName != null)
		{
			if (this.baselinePrefix == null || !this.baselineName.startsWith(this.baselinePrefix))
			{
				// Usually hat baseline name format is *always* x.y_yyyyMMddhhmm_yyyyMMddhhmm
				int index = this.baselineName.lastIndexOf('_');
				if (index > 0)
				{
					this.baselinePrefix = this.baselineName.substring(0, index);
				}
				else
				{
					// this.baselinePrefix = DB_Results.getDbBaselinePrefix();
					this.baselinePrefix = this.baselineName;
				}
			}
		}

		// Set scenario pattern default
		if (this.scenarioPattern == null)
		{
			this.scenarioPattern = "%"; //$NON-NLS-1$
		}

		// Flush print stream
		if (this.printStream != null)
		{
			this.printStream.println();
			this.printStream.flush();
		}
	}

	public void setLastBuildName(String lastBuildName)
	{
		this.lastBuildName = lastBuildName;
		// if (lastBuildName == null) {
		// int idx = this.allBuildNames.length-1;
		// String lastBuild = this.allBuildNames[idx--];
		// while (this.name.startsWith(DB_Results.getDbBaselinePrefix())) {
		// lastBuild = this.allBuildNames[idx--];
		// }
		// this.name = lastBuild;
		// } else {
		// this.name = lastBuildName;
		// }
	}

	/**
	 * Update a given build information with database contents.
	 * 
	 * @param builds
	 *            The builds to read new data
	 * @param force
	 *            Force the update from the database, even if the build is already known.
	 * @param dataDir
	 *            The directory where data should be stored locally if necessary. If <code>null</code>, then information
	 *            changes won't be persisted.
	 * @param monitor
	 *            The progress monitor
	 * @return All known builds
	 */
	public String[] updateBuilds(String[] builds, boolean force, File dataDir, IProgressMonitor monitor)
	{

		// Print title
		StringBuffer buffer = new StringBuffer("Update data for "); //$NON-NLS-1$
		int length = builds == null ? 0 : builds.length;
		switch (length)
		{
			case 0:
				buffer.append("all builds"); //$NON-NLS-1$
				reset(dataDir);
				break;
			case 1:
				buffer.append("one build"); //$NON-NLS-1$
				break;
			default:
				buffer.append("several builds"); //$NON-NLS-1$
				break;
		}
		String taskName = buffer.toString();
		println(buffer);

		// Create sub-monitor
		SubMonitor subMonitor = SubMonitor.convert(monitor, 1000 * length);
		subMonitor.setTaskName(taskName);

		// Read
		for (int i = 0; i < length; i++)
		{
			read(false, builds[i], null, force, dataDir, taskName, subMonitor.newChild(1000));
		}

		// Return new builds list
		return this.allBuildNames;
	}

	/**
	 * Update a given build information with database contents.
	 * 
	 * @param buildName
	 *            The build name to read new data
	 * @param force
	 *            Force the update from the database, even if the build is already known.
	 * @param dataDir
	 *            The directory where data should be stored locally if necessary. If <code>null</code>, then information
	 *            changes won't be persisted.
	 * @param monitor
	 *            The progress monitor
	 * @return All known builds
	 */
	public String[] updateBuild(String buildName, boolean force, File dataDir, IProgressMonitor monitor)
	{

		// Print title
		StringBuffer buffer = new StringBuffer("Update data for "); //$NON-NLS-1$
		if (buildName == null)
		{
			buffer.append("all builds"); //$NON-NLS-1$
			reset(dataDir);
		}
		else
		{
			buffer.append("one build"); //$NON-NLS-1$
		}
		String taskName = buffer.toString();
		println(buffer);

		// Create sub-monitor
		SubMonitor subMonitor = SubMonitor.convert(monitor, 1000);
		subMonitor.setTaskName(taskName);

		// Read
		read(false, buildName, null, force, dataDir, taskName, subMonitor);

		// Refresh name
		if (buildName != null && !buildName.startsWith(DB_Results.getDbBaselinePrefix()))
		{
			this.name = buildName;
		}

		// Return new list all build names
		return this.allBuildNames;
	}

	/*
	 * Write general information.
	 */
	void writeData(File dir)
	{
		if (!DB_Results.DB_CONNECTION)
		{
			// Only write new local file if there's a database connection
			// otherwise contents may not be complete...
			return;
		}
		if (dir == null || (!dir.exists() && !dir.mkdirs()))
		{
			System.err.println("can't create directory " + dir); //$NON-NLS-1$
			return;
		}
		File dataFile = new File(dir, "performances.dat"); //$NON-NLS-1$
		if (dataFile.exists())
		{
			if (!this.needToUpdateLocalFile)
			{
				return;
			}
			dataFile.delete();
		}
		try
		{
			DataOutputStream stream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(dataFile)));

			// Write build info
			stream.writeUTF(this.name == null ? DB_Results.getLastCurrentBuild() : this.name);
			stream.writeUTF(this.baselineName == null ? DB_Results.getLastBaselineBuild(null) : this.baselineName);
			stream.writeUTF(this.baselinePrefix == null ? "" : this.baselinePrefix);

			// Write configs info
			int length = this.sortedConfigNames.length;
			stream.writeInt(length);
			for (int i = 0; i < length; i++)
			{
				stream.writeUTF(this.sortedConfigNames[i]);
				stream.writeUTF(this.sortedConfigDescriptions[i]);
			}

			// Write builds info
			String[] builds = this.allBuildNames == null ? DB_Results.getBuilds() : this.allBuildNames;
			length = builds.length;
			stream.writeInt(length);
			for (int i = 0; i < length; i++)
			{
				stream.writeUTF(builds[i]);
			}

			// Write scenarios info
			length = this.components.length;
			stream.writeInt(length);
			for (int i = 0; i < length; i++)
			{
				stream.writeUTF(this.components[i]);
				List scenarios = (List) this.allScenarios.get(this.components[i]);
				int size = scenarios.size();
				stream.writeInt(size);
				for (int j = 0; j < size; j++)
				{
					final ScenarioResults scenarioResults = (ScenarioResults) scenarios.get(j);
					stream.writeInt(scenarioResults.getId());
					stream.writeUTF(scenarioResults.getName());
					stream.writeUTF(scenarioResults.getLabel());
				}
			}

			// Close
			stream.close();
			println("	=> performance results general data  written in file " + dataFile); //$NON-NLS-1$
		}
		catch (FileNotFoundException e)
		{
			System.err.println("can't create output file" + dataFile); //$NON-NLS-1$
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
