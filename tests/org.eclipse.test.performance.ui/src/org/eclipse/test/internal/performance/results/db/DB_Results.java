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

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.Assert;
import org.eclipse.test.internal.performance.PerformanceTestPlugin;
import org.eclipse.test.internal.performance.data.Dim;
import org.eclipse.test.internal.performance.db.DB;
import org.eclipse.test.internal.performance.results.utils.IPerformancesConstants;
import org.eclipse.test.internal.performance.results.utils.Util;
import org.eclipse.test.performance.Dimension;

/**
 * Specific and private implementation of {@link org.eclipse.test.internal.performance.db.DB} class to get massive
 * results from performance results database. TODO (frederic) Should be at least a subclass of {@link DB}...
 */
public class DB_Results
{

	private static final String DEFAULT_DB_BASELINE_PREFIX = "R-";
	private static final Dim[] NO_DIMENSION = new Dim[0];
	private static final String[] EMPTY_LIST = new String[0];
	static final boolean DEBUG = false;
	static final boolean LOG = false;

	// the two supported DB types
	private static final String DERBY = "derby"; //$NON-NLS-1$
	private static final String CLOUDSCAPE = "cloudscape"; //$NON-NLS-1$

	private static DB_Results fgDefault;

	private Connection fConnection;
	private SQL_Results fSQL;
	// private boolean fIsEmbedded;
	private String fDBType; // either "derby" or "cloudscape"

	// Preferences info
	public static boolean DB_CONNECTION = false;
	private static String DB_NAME;
	private static String DB_LOCATION;
	private static String DB_BASELINE_PREFIX = DEFAULT_DB_BASELINE_PREFIX;
	private static String DB_VERSION;
	private static String DB_VERSION_REF;

	/**
	 * Get the name of the database.
	 * 
	 * @return The name as a string.
	 */
	public static String getDbName()
	{
		if (DB_NAME == null)
			initDbContants();
		return DB_NAME;
	}

	/**
	 * Set the name of the database.
	 * 
	 * @param dbName
	 *            The name as a string.
	 */
	public static void setDbName(String dbName)
	{
		Assert.isNotNull(dbName);
		DB_NAME = dbName;
	}

	/**
	 * Get the location of the database.
	 * 
	 * @return The location as a string.
	 */
	public static String getDbLocation()
	{
		if (!DB_CONNECTION)
			return null;
		if (DB_LOCATION == null)
			initDbContants();
		return DB_LOCATION;
	}

	/**
	 * Set the location of the database.
	 * 
	 * @param dbLocation
	 *            The location as a string.
	 */
	public static void setDbLocation(String dbLocation)
	{
		Assert.isNotNull(dbLocation);
		DB_LOCATION = dbLocation;
	}

	/**
	 * Get the default baseline prefix.
	 * 
	 * @return The prefix as a string.
	 */
	public static String getDbBaselinePrefix()
	{
		return DB_BASELINE_PREFIX;
	}

	/**
	 * Set the baseline prefix of the database.
	 * 
	 * @param baselinePrefix
	 *            The prefix as a string.
	 */
	public static void setDbDefaultBaselinePrefix(String baselinePrefix)
	{
		Assert.isNotNull(baselinePrefix);
		Assert.isTrue(baselinePrefix.startsWith(DEFAULT_DB_BASELINE_PREFIX));
		DB_BASELINE_PREFIX = baselinePrefix;
	}

	/**
	 * Get the baseline reference version of the database.
	 * 
	 * @return The version as a string.
	 */
	public static String getDbBaselineRefVersion()
	{
		if (DB_VERSION_REF == null)
			initDbContants();
		return DB_VERSION_REF;
	}

	/**
	 * Get the version of the database.
	 * 
	 * @return The version as a string.
	 */
	public static String getDbVersion()
	{
		if (DB_VERSION == null)
			initDbContants();
		return DB_VERSION;
	}

	/**
	 * Set the version of the database.
	 * 
	 * @param version
	 *            The version as a string.
	 */
	public static void setDbVersion(String version)
	{
		Assert.isNotNull(version);
		Assert.isTrue(version.startsWith("v3"));
		DB_VERSION = version;
	}

	/**
	 * Update the database constants from a new database location.
	 * 
	 * @param connected
	 *            Tells whether the database should be connected or not.
	 * @param databaseLocation
	 *            The database location. May be a path to a local folder or a net address (see
	 *            {@link IPerformancesConstants#NETWORK_DATABASE_LOCATION}).
	 */
	public static boolean updateDbConstants(boolean connected, int eclipseVersion, String databaseLocation)
	{
		if (DB_CONNECTION != connected
				|| DB_LOCATION == null
				|| DB_NAME == null
				|| ((databaseLocation == null && !DB_LOCATION.equals(IPerformancesConstants.NETWORK_DATABASE_LOCATION)) || !DB_LOCATION
						.equals(databaseLocation))
				|| !DB_NAME.equals(IPerformancesConstants.DATABASE_NAME_PREFIX + eclipseVersion))
		{
			shutdown();
			DB_CONNECTION = connected;
			DB_LOCATION = databaseLocation == null ? IPerformancesConstants.NETWORK_DATABASE_LOCATION
					: databaseLocation;
			DB_NAME = IPerformancesConstants.DATABASE_NAME_PREFIX + eclipseVersion;
			DB_VERSION = "v" + eclipseVersion;
			DB_VERSION_REF = "R-3." + (eclipseVersion % 10 - 1);
			if (connected)
			{
				return getDefault().fSQL != null;
			}
		}
		return true;
	}

	/**
	 * Returns a title including DB version and name.
	 * 
	 * @return A title as a string.
	 */
	public static String getDbTitle()
	{
		if (!DB_CONNECTION)
			return null;
		String title = "Eclipse " + DB_VERSION + " - ";
		if (DB_LOCATION.startsWith("net:"))
		{
			title += " Network DB";
		}
		else
		{
			title += " Local DB";
		}
		return title;
	}

	/**
	 * The list of all the configurations (i.e. machine) stored in the database.
	 */
	private static String[] CONFIGS;

	/**
	 * The list of all the components stored in the database.
	 */
	private static String[] COMPONENTS;

	/**
	 * The list of all the builds stored in the database.
	 */
	private static String[] BUILDS;

	/**
	 * The list of all the dimensions stored in the database.
	 */
	private static int[] DIMENSIONS;

	/**
	 * The default dimension used to display results (typically in fingerprints).
	 */
	private static Dim DEFAULT_DIM;
	private static int DEFAULT_DIM_INDEX;

	/**
	 * The list of all the dimensions displayed while generating results.
	 */
	private static Dim[] RESULTS_DIMENSIONS;

	/**
	 * The list of all the VMs stored in the database.
	 */
	private static String[] VMS;

	/**
	 * The list of possible test boxes.
	 * <p>
	 * Only used if no specific configurations are specified (see
	 * {@link PerformanceResults#readAll(String, String[][], String, File, int, org.eclipse.core.runtime.IProgressMonitor)}
	 * .
	 * </p>
	 * Note that this is a copy of the the property "eclipse.perf.config.descriptors" defined in
	 * org.eclipse.releng.eclipsebuilder/eclipse/helper.xml file
	 */
	private static String[] CONFIG_DESCRIPTIONS;

	/**
	 * The list of known Eclipse components.
	 */
	private final static String[] ECLIPSE_COMPONENTS = { "org.eclipse.ant", //$NON-NLS-1$
			"org.eclipse.compare", //$NON-NLS-1$
			"org.eclipse.core", //$NON-NLS-1$
			"org.eclipse.help", //$NON-NLS-1$
			"org.eclipse.jdt.core", //$NON-NLS-1$
			"org.eclipse.jdt.debug", //$NON-NLS-1$
			"org.eclipse.jdt.text", //$NON-NLS-1$
			"org.eclipse.jdt.ui", //$NON-NLS-1$
			"org.eclipse.jface", //$NON-NLS-1$
			"org.eclipse.osgi", //$NON-NLS-1$
			"org.eclipse.pde.api.tools", //$NON-NLS-1$
			"org.eclipse.pde.ui", //$NON-NLS-1$
			"org.eclipse.swt", //$NON-NLS-1$
			"org.eclipse.team", //$NON-NLS-1$
			"org.eclipse.ua", //$NON-NLS-1$
			"org.eclipse.ui", //$NON-NLS-1$
			"com.appcelerator.titanium", //$NON-NLS-1$
			"com.aptana" //$NON-NLS-1$
	};
	private static String[] KNOWN_COMPONENTS = ECLIPSE_COMPONENTS;

	// Store debug info
	final static StringWriter DEBUG_STR_WRITER;
	final static PrintWriter DEBUG_WRITER;
	static
	{
		if (DEBUG)
		{
			DEBUG_STR_WRITER = new StringWriter();
			DEBUG_WRITER = new PrintWriter(DEBUG_STR_WRITER);
		}
		else
		{
			DEBUG_STR_WRITER = null;
			DEBUG_WRITER = null;
		}
	}

	// Store log info
	final static StringWriter LOG_STR_WRITER = new StringWriter();
	final static LogWriter LOG_WRITER = new LogWriter();

	static class LogWriter extends PrintWriter
	{
		long[] starts = new long[10];
		long[] times = new long[10];
		StringBuffer[] buffers = new StringBuffer[10];
		int depth = -1, max = -1;

		public LogWriter()
		{
			super(LOG_STR_WRITER);
		}

		void starts(String log)
		{
			if (++this.depth >= this.buffers.length)
			{
				System.arraycopy(this.times, 0, this.times = new long[this.depth + 10], 0, this.depth);
				System.arraycopy(this.buffers, 0, this.buffers = new StringBuffer[this.depth + 10], 0, this.depth);
			}
			StringBuffer buffer = this.buffers[this.depth];
			if (this.buffers[this.depth] == null)
				buffer = this.buffers[this.depth] = new StringBuffer();
			buffer.append(log);
			this.starts[this.depth] = System.currentTimeMillis();
			if (this.depth > this.max)
				this.max = this.depth;
		}

		void ends(String log)
		{
			if (this.depth < 0)
				throw new RuntimeException("Invalid call to ends (missing corresponding starts call)!"); //$NON-NLS-1$
			this.buffers[this.depth].append(log);
			if (this.depth > 0)
			{
				this.times[this.depth] += System.currentTimeMillis() - this.starts[this.depth];
				this.depth--;
				return;
			}
			for (int i = 0; i < this.max; i++)
			{
				print(this.buffers[i].toString());
				print(" ( in "); //$NON-NLS-1$
				print(this.times[this.depth]);
				println("ms)"); //$NON-NLS-1$
			}
			this.depth = this.max = -1;
			this.starts = new long[10];
			this.times = new long[10];
			this.buffers = new StringBuffer[10];
		}

		public String toString()
		{
			return LOG_STR_WRITER.toString();
		}
	}

	// Data storage from queries
	static String LAST_CURRENT_BUILD, LAST_BASELINE_BUILD;
	private static int BUILDS_LENGTH;
	private static String[] SCENARII;
	private static String[] COMMENTS;

	// ---- private implementation

	/**
	 * Private constructor to block instance creation.
	 */
	private DB_Results()
	{
		// empty implementation
	}

	synchronized static DB_Results getDefault()
	{
		if (fgDefault == null)
		{
			fgDefault = new DB_Results();
			fgDefault.connect();
			if (PerformanceTestPlugin.getDefault() == null)
			{
				// not started as plugin
				Runtime.getRuntime().addShutdownHook(new Thread()
				{
					public void run()
					{
						shutdown();
					}
				});
			}
		}
		else if (fgDefault.fSQL == null)
		{
			fgDefault.connect();
		}
		return fgDefault;
	}

	public static void shutdown()
	{
		if (fgDefault != null)
		{
			fgDefault.disconnect();
			fgDefault = null;
			BUILDS = null;
			LAST_BASELINE_BUILD = null;
			LAST_CURRENT_BUILD = null;
			DIMENSIONS = null;
			CONFIGS = null;
			COMPONENTS = null;
			SCENARII = null;
			COMMENTS = null;
			DB_VERSION = null;
			DB_VERSION_REF = null;
			DEFAULT_DIM = null;
			DEFAULT_DIM_INDEX = -1;
			RESULTS_DIMENSIONS = null;
			VMS = null;
			CONFIG_DESCRIPTIONS = null;
			KNOWN_COMPONENTS = ECLIPSE_COMPONENTS;
		}
		if (DEBUG)
		{
			DEBUG_WRITER.println("DB.shutdown"); //$NON-NLS-1$
			System.out.println(DEBUG_STR_WRITER.toString());
		}
		if (LOG)
		{
			System.out.println(LOG_STR_WRITER.toString());
		}
	}

	/**
	 * Return the build id from a given name.
	 * 
	 * @param name
	 *            The build name (eg. I20070615-1200)
	 * @return The id of the build (ie. the index in the {@link #BUILDS} list)
	 */
	static int getBuildId(String name)
	{
		if (BUILDS == null)
			return -1;
		return Arrays.binarySearch(BUILDS, name, Util.BUILD_DATE_COMPARATOR);
	}

	/**
	 * Return the build name from a given id.
	 * 
	 * @param id
	 *            The build id
	 * @return The name of the build (eg. I20070615-1200)
	 */
	static String getBuildName(int id)
	{
		if (BUILDS == null)
			return null;
		return BUILDS[id];
	}

	/**
	 * Returns all the builds names read from the database.
	 * 
	 * @return The list of all builds names matching the scenario pattern used while reading data
	 */
	public static String[] getBuilds()
	{
		if (BUILDS == null)
		{
			queryAllVariations("%"); //$NON-NLS-1$
		}
		if (BUILDS_LENGTH == 0)
			return EMPTY_LIST;
		String[] builds = new String[BUILDS_LENGTH];
		System.arraycopy(BUILDS, 0, builds, 0, BUILDS_LENGTH);
		return builds;
	}

	/**
	 * Returns the number of builds stored int the database.
	 * 
	 * @return The number of builds stored in the database.
	 */
	public static int getBuildsNumber()
	{
		if (BUILDS == null)
		{
			queryAllVariations("%"); //$NON-NLS-1$
		}
		return BUILDS_LENGTH;
	}

	/**
	 * Get component name from a scenario.
	 * 
	 * @param scenarioName
	 *            The name of the scenario
	 * @return The component name
	 */
	static String getComponentNameFromScenario(String scenarioName)
	{
		int length = KNOWN_COMPONENTS.length;
		for (int i = 0; i < length; i++)
		{
			if (scenarioName.startsWith(KNOWN_COMPONENTS[i]))
			{
				return KNOWN_COMPONENTS[i];
			}
		}
		StringTokenizer tokenizer = new StringTokenizer(scenarioName, ".");
		StringBuffer buffer = new StringBuffer(tokenizer.nextToken());
		if (tokenizer.hasMoreTokens())
		{
			buffer.append('.');
			buffer.append(tokenizer.nextToken());
			if (tokenizer.hasMoreTokens())
			{
				buffer.append('.');
				buffer.append(tokenizer.nextToken());
			}
		}
		String componentName = buffer.toString();
		System.err
				.println(scenarioName
						+ " does not belongs to a known Eclipse component. So use scenario prefix " + componentName + " as component name by default and add it to the know components"); //$NON-NLS-1$
		System.arraycopy(KNOWN_COMPONENTS, 0, KNOWN_COMPONENTS = new String[length + 1], 0, length);
		KNOWN_COMPONENTS[length] = componentName;
		return componentName;
	}

	/**
	 * Get all components read from database.
	 * 
	 * @return A list of component names matching the given pattern
	 */
	public static String[] getComponents()
	{
		if (COMPONENTS == null)
			return EMPTY_LIST;
		int length = COMPONENTS.length;
		String[] components = new String[length];
		System.arraycopy(COMPONENTS, 0, components, 0, length);
		return components;
	}

	/**
	 * Return the name of the configuration from the given id.
	 * 
	 * @param id
	 *            The index of the configuration in the stored list.
	 * @return The name of the configuration (eg. eclipseperflnx1_R3.3)
	 */
	static String getConfig(int id)
	{
		return CONFIGS[id];
	}

	/**
	 * Get all configurations read from the database.
	 * 
	 * @return A list of configuration names
	 */
	public static String[] getConfigs()
	{
		if (CONFIGS == null)
			return EMPTY_LIST;
		int length = CONFIGS.length;
		String[] configs = new String[length];
		System.arraycopy(CONFIGS, 0, configs, 0, length);
		return configs;
	}

	/**
	 * Set the default dimension used for performance results.
	 */
	public static void setConfigs(String[] configs)
	{
		CONFIGS = configs;
	}

	/**
	 * Get all configurations read from the database.
	 * 
	 * @return A list of configuration names
	 */
	public static String[] getConfigDescriptions()
	{
		if (CONFIG_DESCRIPTIONS == null)
		{
			if (CONFIGS == null)
				return null;
			int length = CONFIGS.length;
			CONFIG_DESCRIPTIONS = new String[length];
			String[][] configDescriptors = PerformanceTestPlugin.getConfigDescriptors();
			int cdLength = configDescriptors.length;
			for (int i = 0; i < length; i++)
			{
				boolean found = false;
				for (int j = 0; j < cdLength; j++)
				{
					if (configDescriptors[j][0].equals(CONFIGS[i]))
					{
						CONFIG_DESCRIPTIONS[i] = configDescriptors[j][1];
						found = true;
						break;
					}
				}
				if (!found)
				{
					String kind = CONFIGS[i].indexOf("epwin") < 0 ? "Linux" : "Win XP";
					CONFIG_DESCRIPTIONS[i] = kind + " perf test box " + CONFIGS[i].substring(5);
				}
			}
		}
		int length = CONFIG_DESCRIPTIONS.length;
		String[] descriptions = new String[length];
		System.arraycopy(CONFIG_DESCRIPTIONS, 0, descriptions, 0, length);
		return descriptions;
	}

	/**
	 * Set the default dimension used for performance results.
	 */
	public static void setConfigDescriptions(String[] descriptions)
	{
		CONFIG_DESCRIPTIONS = descriptions;
	}

	/**
	 * Get all dimensions read from the database.
	 * 
	 * @return A list of dimensions.
	 */
	public static Dim[] getDimensions()
	{
		if (DIMENSIONS == null)
			return NO_DIMENSION;
		int length = DIMENSIONS.length;
		Dim[] dimensions = new Dim[length];
		for (int i = 0; i < length; i++)
		{
			Dimension dimension = PerformanceTestPlugin.getDimension(DIMENSIONS[i]);
			if (dimension == null)
			{
				throw new RuntimeException("There is an unsupported dimension stored in the database: " + DIMENSIONS[i]);
			}
			dimensions[i] = (Dim) dimension;
		}
		return dimensions;
	}

	/**
	 * Return the default dimension used for performance results.
	 * 
	 * @return The {@link Dim default dimension}.
	 */
	public static Dim getDefaultDimension()
	{
		if (DEFAULT_DIM == null)
		{
			DEFAULT_DIM = (Dim) PerformanceTestPlugin.getDefaultDimension();
		}
		return DEFAULT_DIM;
	}

	/**
	 * Set the default dimension used for performance results.
	 */
	public static void setDefaultDimension(String dim)
	{
		DEFAULT_DIM = (Dim) PerformanceTestPlugin.getDimension(dim);
		if (DIMENSIONS != null)
		{
			DEFAULT_DIM_INDEX = Arrays.binarySearch(DIMENSIONS, DEFAULT_DIM.getId());
		}
	}

	public static Dim[] getResultsDimensions()
	{
		if (RESULTS_DIMENSIONS == null)
		{
			Dimension[] resultsDimensions = PerformanceTestPlugin.getResultsDimensions();
			int length = resultsDimensions.length;
			RESULTS_DIMENSIONS = new Dim[length];
			for (int i = 0; i < length; i++)
			{
				RESULTS_DIMENSIONS[i] = (Dim) resultsDimensions[i];
			}
		}
		return RESULTS_DIMENSIONS;
	}

	/**
	 * Set the default dimension used for performance results.
	 */
	public static void setResultsDimensions(String[] dimensions)
	{
		int length = dimensions.length;
		RESULTS_DIMENSIONS = new Dim[length];
		for (int i = 0; i < length; i++)
		{
			RESULTS_DIMENSIONS[i] = (Dim) PerformanceTestPlugin.getDimension(dimensions[i]);
		}
	}

	/**
	 * Return the default dimension used for performance results.
	 * 
	 * @return The {@link Dim default dimension}.
	 */
	public static int getDefaultDimensionIndex()
	{
		if (DEFAULT_DIM == null || DEFAULT_DIM_INDEX == -1)
		{
			getDefaultDimension(); // init default dimension
			getDimensions(); // init dimensions
			DEFAULT_DIM_INDEX = Arrays.binarySearch(DIMENSIONS, DEFAULT_DIM.getId());
		}
		return DEFAULT_DIM_INDEX;
	}

	/**
	 * Return the ID of the last baseline build before the given date.
	 * 
	 * @param date
	 *            The date the baseline must be run before. If <code>null</code> return the last baseline build stored
	 *            in the DB.
	 * @return the ID of the last baseline build before the given date or <code>null</code> if none was run before it...
	 */
	public static String getLastBaselineBuild(String date)
	{
		if (BUILDS == null)
		{
			queryAllVariations("%"); //$NON-NLS-1$
		}
		if (date == null)
		{
			if (LAST_BASELINE_BUILD == null)
			{
				return BUILDS[0];
			}
			return LAST_BASELINE_BUILD;
		}
		String lastBaselineBuild = null;
		for (int i = 0; i < BUILDS_LENGTH; i++)
		{
			String build = BUILDS[i];
			if (build.startsWith(DB_VERSION_REF))
			{
				String buildDate = build.substring(build.indexOf('_') + 1);
				if (buildDate.compareTo(date) < 0)
				{
					if (lastBaselineBuild == null || build.compareTo(lastBaselineBuild) > 0)
					{
						lastBaselineBuild = build;
					}
				}
			}
		}
		if (lastBaselineBuild == null && BUILDS.length > 0)
		{
			return BUILDS[0];
		}
		return lastBaselineBuild;
	}

	/**
	 * Return the ID of the last baseline build.
	 * 
	 * @return the ID of the last baseline build.
	 */
	public static String getLastCurrentBuild()
	{
		if (BUILDS == null)
		{
			queryAllVariations("%"); //$NON-NLS-1$
		}
		return LAST_CURRENT_BUILD;
	}

	/**
	 * Returns all the scenarios names read from the database.
	 * 
	 * @return The list of all scenarios matching the pattern for a given build.
	 * @see #internalQueryBuildScenarios(String, String)
	 */
	public static List getScenarios()
	{
		return Arrays.asList(SCENARII);
	}

	/**
	 * Init the constants if necessary.
	 */
	public static void initDbContants()
	{
		if (DB_LOCATION == null)
		{
			DB_LOCATION = PerformanceTestPlugin.getDBLocation();
			if (DB_LOCATION == null)
			{
				new RuntimeException("Cannot connect to the DB without a location!");
			}
		}
		if (DB_NAME == null)
		{
			DB_NAME = PerformanceTestPlugin.getDBName();
			if (DB_NAME == null)
			{
				new RuntimeException("Cannot connect to the DB without a name!");
			}
		}
		if (DB_VERSION == null)
		{
			DB_VERSION = "v" + DB_NAME.substring(DB_NAME.length() - 2);
			DB_VERSION_REF = "R-3." + (Character.digit(DB_NAME.charAt(DB_NAME.length() - 1), 10) - 1);
		}
	}

	/**
	 * Get all scenarios read from database.
	 * 
	 * @return A list of all scenario names matching the default pattern
	 */
	public static Map queryAllScenarios()
	{
		return getDefault().internalQueryBuildScenarios("%", null); //$NON-NLS-1$
	}

	/**
	 * Get all scenarios read from database matching a given pattern. Note that all scenarios are returned if the
	 * pattern is <code>null</code>.
	 * 
	 * @param scenarioPattern
	 *            The pattern of the requested scenarios
	 * @return A map of all scenarios matching the given pattern. The map keys are component names and values are the
	 *         scenarios list for each component.
	 */
	static Map queryAllScenarios(String scenarioPattern)
	{
		String pattern = scenarioPattern == null ? "%" : scenarioPattern; //$NON-NLS-1$
		return getDefault().internalQueryBuildScenarios(pattern, null);
	}

	/**
	 * Get all scenarios read from database matching a given pattern. Note that all scenarios are returned if the
	 * pattern is <code>null</code>.
	 * 
	 * @param scenarioPattern
	 *            The pattern of the requested scenarios
	 * @param buildName
	 *            The build name
	 * @return A list of scenario names matching the given pattern
	 */
	static Map queryAllScenarios(String scenarioPattern, String buildName)
	{
		return getDefault().internalQueryBuildScenarios(scenarioPattern, buildName);
	}

	/**
	 * Get all variations read from database matching a given configuration pattern.
	 * 
	 * @param configPattern
	 *            The pattern of the requested configurations
	 */
	static void queryAllVariations(String configPattern)
	{
		getDefault().internalQueryAllVariations(configPattern);
	}

	/**
	 * Get all summaries from DB for a given scenario and configuration pattern
	 * 
	 * @param scenarioResults
	 *            The scenario results where to store data
	 * @param configPattern
	 *            The configuration pattern concerned by the query
	 * @param builds
	 *            All builds to get summaries, if <code>null</code>, then all DB builds will be concerned.
	 */
	static void queryScenarioSummaries(ScenarioResults scenarioResults, String configPattern, String[] builds)
	{
		getDefault().internalQueryScenarioSummaries(scenarioResults, configPattern, builds);
	}

	/**
	 * Query and store all values for given scenario results
	 * 
	 * @param scenarioResults
	 *            The scenario results where the values has to be put
	 * @param configPattern
	 *            The pattern of the configuration concerned by the query
	 * @param buildName
	 *            Name of the last build on which data were stored locally
	 */
	static void queryScenarioValues(ScenarioResults scenarioResults, String configPattern, String buildName)
	{
		getDefault().internalQueryScenarioValues(scenarioResults, configPattern, buildName);
	}

	/**
	 * dbloc= embed in home directory dbloc=/tmp/performance embed given location dbloc=net://localhost connect to local
	 * server dbloc=net://www.eclipse.org connect to remove server
	 */
	private void connect()
	{

		if (this.fConnection != null || !DB_CONNECTION)
			return;

		if (DEBUG)
			DriverManager.setLogWriter(new PrintWriter(System.out));

		// Init DB location and name if not already done
		if (DB_LOCATION == null)
		{
			initDbContants();
		}

		String url = null;
		java.util.Properties info = new java.util.Properties();

		if (DEBUG)
		{
			DEBUG_WRITER.println();
			DEBUG_WRITER.println("==========================================================="); //$NON-NLS-1$
			DEBUG_WRITER.println("Database debug information stored while processing"); //$NON-NLS-1$
		}
		if (LOG)
		{
			LOG_WRITER.println();
			LOG_WRITER.println("==========================================================="); //$NON-NLS-1$
			LOG_WRITER.println("Database log information stored while processing"); //$NON-NLS-1$
		}

		this.fDBType = DERBY; // assume we are using Derby
		try
		{
			if (DB_LOCATION.startsWith("net://")) { //$NON-NLS-1$
				// remote
				// fIsEmbedded = false;
				// connect over network
				if (DEBUG)
					DEBUG_WRITER.println("Trying to connect over network..."); //$NON-NLS-1$
				Class.forName("com.ibm.db2.jcc.DB2Driver"); //$NON-NLS-1$
				info.put("user", PerformanceTestPlugin.getDBUser()); //$NON-NLS-1$
				info.put("password", PerformanceTestPlugin.getDBPassword()); //$NON-NLS-1$
				info.put("retrieveMessagesFromServerOnGetMessage", "true"); //$NON-NLS-1$ //$NON-NLS-2$
				info.put("create", "true"); //$NON-NLS-1$ //$NON-NLS-2$
				url = DB_LOCATION + '/' + DB_NAME;
			}
			else if (DB_LOCATION.startsWith("//")) { //$NON-NLS-1$
				// remote
				// fIsEmbedded = false;
				// connect over network
				if (DEBUG)
					DEBUG_WRITER.println("Trying to connect over network..."); //$NON-NLS-1$
				Class.forName("org.apache.derby.jdbc.ClientDriver"); //$NON-NLS-1$
				info.put("user", PerformanceTestPlugin.getDBUser()); //$NON-NLS-1$
				info.put("password", PerformanceTestPlugin.getDBPassword()); //$NON-NLS-1$
				info.put("create", "true"); //$NON-NLS-1$ //$NON-NLS-2$
				url = DB_LOCATION + '/' + DB_NAME;
			}
			else
			{
				// workaround for Derby issue:
				// http://nagoya.apache.org/jira/browse/DERBY-1
				if ("Mac OS X".equals(System.getProperty("os.name"))) //$NON-NLS-1$//$NON-NLS-2$
					System.setProperty("derby.storage.fileSyncTransactionLog", "true"); //$NON-NLS-1$ //$NON-NLS-2$

				// embedded
				try
				{
					Class.forName("org.apache.derby.jdbc.EmbeddedDriver"); //$NON-NLS-1$
					// fIsEmbedded = true;
				}
				catch (ClassNotFoundException e)
				{
					Class.forName("com.ihost.cs.jdbc.CloudscapeDriver"); //$NON-NLS-1$
					this.fDBType = CLOUDSCAPE;
				}
				if (DEBUG)
					DEBUG_WRITER.println("Loaded embedded " + this.fDBType); //$NON-NLS-1$
				File f;
				if (DB_LOCATION.length() == 0)
				{
					String user_home = System.getProperty("user.home"); //$NON-NLS-1$
					if (user_home == null)
						return;
					f = new File(user_home, this.fDBType);
				}
				else
					f = new File(DB_LOCATION);
				url = new File(f, DB_NAME).getAbsolutePath();
				info.put("user", PerformanceTestPlugin.getDBUser()); //$NON-NLS-1$
				info.put("password", PerformanceTestPlugin.getDBPassword()); //$NON-NLS-1$
				info.put("create", "true"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			try
			{
				this.fConnection = DriverManager.getConnection("jdbc:" + this.fDBType + ":" + url, info); //$NON-NLS-1$ //$NON-NLS-2$
			}
			catch (SQLException e)
			{
				if ("08001".equals(e.getSQLState()) && DERBY.equals(this.fDBType)) { //$NON-NLS-1$
					if (DEBUG)
						DEBUG_WRITER.println("DriverManager.getConnection failed; retrying for cloudscape"); //$NON-NLS-1$
					// try Cloudscape
					this.fDBType = CLOUDSCAPE;
					this.fConnection = DriverManager.getConnection("jdbc:" + this.fDBType + ":" + url, info); //$NON-NLS-1$ //$NON-NLS-2$
				}
				else
					throw e;
			}
			if (DEBUG)
				DEBUG_WRITER.println("connect succeeded!"); //$NON-NLS-1$

			this.fConnection.setAutoCommit(false);
			this.fSQL = new SQL_Results(this.fConnection);
			this.fConnection.commit();

		}
		catch (SQLException ex)
		{
			PerformanceTestPlugin.logError(ex.getMessage());

		}
		catch (ClassNotFoundException e)
		{
			PerformanceTestPlugin.log(e);
		}
	}

	private void disconnect()
	{
		if (DEBUG)
			DEBUG_WRITER.println("disconnecting from DB"); //$NON-NLS-1$
		if (this.fSQL != null)
		{
			try
			{
				this.fSQL.dispose();
			}
			catch (SQLException e1)
			{
				PerformanceTestPlugin.log(e1);
			}
			this.fSQL = null;
		}
		if (this.fConnection != null)
		{
			try
			{
				this.fConnection.commit();
			}
			catch (SQLException e)
			{
				PerformanceTestPlugin.log(e);
			}
			try
			{
				this.fConnection.close();
			}
			catch (SQLException e)
			{
				PerformanceTestPlugin.log(e);
			}
			this.fConnection = null;
		}

		/*
		 * if (fIsEmbedded) { try { DriverManager.getConnection("jdbc:" + fDBType + ":;shutdown=true"); //$NON-NLS-1$
		 * //$NON-NLS-2$ } catch (SQLException e) { String message = e.getMessage(); if
		 * (message.indexOf("system shutdown.") < 0) //$NON-NLS-1$ e.printStackTrace(); } }
		 */
	}

	/*
	 * Return the index of the given configuration in the stored list.
	 */
	private int getConfigId(String config)
	{
		if (CONFIGS == null)
			return -1;
		return Arrays.binarySearch(CONFIGS, config);
	}

	SQL_Results getSQL()
	{
		return this.fSQL;
	}

	/*
	 * Query all comments from database
	 */
	private void internalQueryAllComments()
	{
		if (this.fSQL == null)
			return;
		if (COMMENTS != null)
			return;
		long start = System.currentTimeMillis();
		if (DEBUG)
			DEBUG_WRITER.print("		[DB query all comments..."); //$NON-NLS-1$
		ResultSet result = null;
		try
		{
			String[] comments = null;
			result = this.fSQL.queryAllComments();
			while (result.next())
			{
				int commentID = result.getInt(1);
				// Ignore kind as there's only one
				// int commentKind = result.getInt(2);
				String comment = result.getString(3);
				if (comments == null)
				{
					comments = new String[commentID + 10];
				}
				else if (commentID >= comments.length)
				{
					int length = comments.length;
					System.arraycopy(comments, 0, comments = new String[commentID + 10], 0, length);
				}
				comments[commentID] = comment;
			}
			COMMENTS = comments;
		}
		catch (SQLException e)
		{
			PerformanceTestPlugin.log(e);
		}
		finally
		{
			if (result != null)
			{
				try
				{
					result.close();
				}
				catch (SQLException e1)
				{
					// ignored
				}
			}
			if (DEBUG)
				DEBUG_WRITER.println("done in " + (System.currentTimeMillis() - start) + "ms]"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/*
	 * Query all variations. This method stores all config and build names.
	 */
	private void internalQueryAllVariations(String configPattern)
	{
		if (this.fSQL == null)
			return;
		if (BUILDS != null)
			return;
		long start = System.currentTimeMillis();
		if (DEBUG)
		{
			DEBUG_WRITER.print("	- DB query all variations for configuration pattern: " + configPattern); //$NON-NLS-1$
			DEBUG_WRITER.print("..."); //$NON-NLS-1$
		}
		ResultSet result = null;
		try
		{
			CONFIGS = null;
			BUILDS = null;
			BUILDS_LENGTH = 0;
			result = this.fSQL.queryAllVariations(configPattern);
			// Ignore first 3 rows.
			result.next();
			result.next();
			result.next();
			while (result.next())
			{
				String variation = result.getString(1); // something like
														// "||build=I20070615-1200||config=eclipseperfwin2_R3.3||jvm=sun|"
				if (variation.contains("build=||") || variation.contains("testing"))
				{
					continue;
				}
				StringTokenizer tokenizer = new StringTokenizer(variation, "=|"); //$NON-NLS-1$
				tokenizer.nextToken(); // 'build'
				storeBuildName(tokenizer.nextToken()); // 'I20070615-1200'
				tokenizer.nextToken(); // 'config'
				storeConfig(tokenizer.nextToken()); // 'eclipseperfwin2_R3.3'
				tokenizer.nextToken(); // 'jvm'
				storeVm(tokenizer.nextToken()); // 'sun'
			}
			if (BUILDS_LENGTH == 0)
			{
				BUILDS = EMPTY_LIST;
			}
		}
		catch (SQLException e)
		{
			PerformanceTestPlugin.log(e);
		}
		finally
		{
			if (result != null)
			{
				try
				{
					result.close();
				}
				catch (SQLException e1)
				{
					// ignored
				}
			}
			if (DEBUG)
				DEBUG_WRITER.println("done in " + (System.currentTimeMillis() - start) + "ms]"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private Map internalQueryBuildScenarios(String scenarioPattern, String buildName)
	{
		if (this.fSQL == null)
			return null;
		long start = System.currentTimeMillis();
		if (DEBUG)
		{
			DEBUG_WRITER.print("	- DB query all scenarios"); //$NON-NLS-1$
			if (scenarioPattern != null)
				DEBUG_WRITER.print(" with pattern " + scenarioPattern); //$NON-NLS-1$
			if (buildName != null)
				DEBUG_WRITER.print(" for build: " + buildName); //$NON-NLS-1$
		}
		ResultSet result = null;
		Map allScenarios = new HashMap();
		try
		{
			if (buildName == null)
			{
				result = this.fSQL.queryBuildAllScenarios(scenarioPattern);
			}
			else
			{
				result = this.fSQL.queryBuildScenarios(scenarioPattern, buildName);
			}
			int previousId = -1;
			List scenarios = null;
			List scenariosNames = new ArrayList();
			while (result.next())
			{
				int id = result.getInt(1);
				String name = result.getString(2);
				scenariosNames.add(name);
				String shortName = result.getString(3);
				int component_id = storeComponent(getComponentNameFromScenario(name));
				if (component_id != previousId)
				{
					allScenarios.put(COMPONENTS[component_id], scenarios = new ArrayList());
					previousId = component_id;
				}
				scenarios.add(new ScenarioResults(id, name, shortName));
			}
			SCENARII = new String[scenariosNames.size()];
			scenariosNames.toArray(SCENARII);
		}
		catch (SQLException e)
		{
			PerformanceTestPlugin.log(e);
		}
		finally
		{
			if (result != null)
			{
				try
				{
					result.close();
				}
				catch (SQLException e1)
				{ // ignored
				}
			}
			if (DEBUG)
				DEBUG_WRITER.println("done in " + (System.currentTimeMillis() - start) + "ms]"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return allScenarios;
	}

	private void internalQueryScenarioValues(ScenarioResults scenarioResults, String configPattern, String buildName)
	{
		if (this.fSQL == null)
			return;
		if (DEBUG)
		{
			DEBUG_WRITER
					.print("	- DB query all data points for config pattern: " + configPattern + " for scenario: " + scenarioResults.getShortName()); //$NON-NLS-1$ //$NON-NLS-2$
			if (buildName != null)
				DEBUG_WRITER.print(" for build: " + buildName); //$NON-NLS-1$
		}
		internalQueryAllVariations(configPattern); // need to read all variations to have all build names
		ResultSet result = null;
		try
		{
			int count = 0;

			result = buildName == null ? this.fSQL.queryScenarioDataPoints(configPattern, scenarioResults.getId())
					: this.fSQL.queryScenarioBuildDataPoints(configPattern, scenarioResults.getId(), buildName);
			int i = 0;
			while (result.next())
			{
				++i;
				int dp_id = result.getInt(1);
				int step = result.getInt(2);
				String variation = result.getString(3); // something like
														// "|build=I20070615-1200||config=eclipseperfwin2_R3.3||jvm=sun|"
				if (variation.contains("build=||") || variation.contains("testing"))
				{
					continue;
				}
				StringTokenizer tokenizer = new StringTokenizer(variation, "=|"); //$NON-NLS-1$
				tokenizer.nextToken(); // 'build'
				int build_id = getBuildId(tokenizer.nextToken()); // 'I20070615-1200'
				tokenizer.nextToken(); // 'config'
				int config_id = getConfigId(tokenizer.nextToken()); // 'eclipseperflnx3'
				ResultSet rs2 = this.fSQL.queryDimScalars(dp_id);
				while (rs2.next())
				{
					int dim_id = rs2.getInt(1);
					storeDimension(dim_id);
					BigDecimal decimalValue = rs2.getBigDecimal(2);
					long value = decimalValue.longValue();
					if (build_id >= 0)
					{ // build id may be negative (i.e. not stored in the array) if new run starts while we're getting
						// results
						scenarioResults.setValue(build_id, dim_id, config_id, step, value);
					}
					count = count + 1;
				}
			}
			if (LOG)
				LOG_WRITER.ends("		-> " + count + " values read"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		catch (SQLException e)
		{
			PerformanceTestPlugin.log(e);
		}
		finally
		{
			if (result != null)
			{
				try
				{
					result.close();
				}
				catch (SQLException e1)
				{
					// ignored
				}
			}
		}
	}

	private void internalQueryScenarioSummaries(ScenarioResults scenarioResults, String config, String[] builds)
	{
		if (this.fSQL == null)
			return;
		long start = System.currentTimeMillis();
		if (DEBUG)
		{
			DEBUG_WRITER
					.print("	- DB query all summaries for scenario '" + scenarioResults.getShortName() + "' of '" + config + "' config"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		internalQueryAllComments();
		ResultSet result = null;
		try
		{
			int scenarioID = scenarioResults.getId();
			// First try to get summaries of elapsed process dimension
			result = this.fSQL.queryScenarioSummaries(scenarioID, config, builds);
			while (result.next())
			{
				String variation = result.getString(1); // something like
														// "|build=I20070615-1200||config=eclipseperfwin2_R3.3||jvm=sun|"
				if (variation.contains("build=||") || variation.contains("testing"))
				{
					continue;
				}
				int summaryKind = result.getShort(2);
				int comment_id = result.getInt(3);
				int dim_id = result.getInt(4);
				if (dim_id != 0)
					storeDimension(dim_id);
				StringTokenizer tokenizer = new StringTokenizer(variation, "=|"); //$NON-NLS-1$
				tokenizer.nextToken(); // 'build'
				String buildName = tokenizer.nextToken(); // 'I20070615-1200'
				tokenizer.nextToken(); // 'config'
				int config_id = getConfigId(tokenizer.nextToken()); // 'eclipseperflnx3'
				int build_id = getBuildId(buildName);
				if (build_id >= 0)
				{
					scenarioResults.setInfos(config_id, build_id, dim_id == 0 ? -1 : summaryKind, COMMENTS[comment_id]);
				}
			}
		}
		catch (SQLException e)
		{
			PerformanceTestPlugin.log(e);
		}
		finally
		{
			if (result != null)
			{
				try
				{
					result.close();
				}
				catch (SQLException e1)
				{
					// ignored
				}
			}
			if (DEBUG)
				DEBUG_WRITER.println("done in " + (System.currentTimeMillis() - start) + "ms]"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/*
	 * Store a build name in the dynamic list. The list is sorted alphabetically.
	 */
	private int storeBuildName(String build)
	{
		boolean isVersion = build.startsWith(DB_BASELINE_PREFIX);
		if (BUILDS == null)
		{
			BUILDS = new String[1];
			BUILDS[BUILDS_LENGTH++] = build;
			if (isVersion)
			{
				LAST_BASELINE_BUILD = build;
			}
			else
			{
				LAST_CURRENT_BUILD = build;
			}
			return 0;
		}
		int idx = Arrays.binarySearch(BUILDS, build, Util.BUILD_DATE_COMPARATOR);
		if (idx >= 0)
			return idx;
		int index = -idx - 1;
		int length = BUILDS.length;
		if (BUILDS_LENGTH == length)
		{
			String[] array = new String[length + 1];
			if (index > 0)
				System.arraycopy(BUILDS, 0, array, 0, index);
			array[index] = build;
			if (index < length)
			{
				System.arraycopy(BUILDS, index, array, index + 1, length - index);
			}
			BUILDS = array;
		}
		BUILDS_LENGTH++;
		if (isVersion)
		{
			if (LAST_BASELINE_BUILD == null || LAST_CURRENT_BUILD == null)
			{
				LAST_BASELINE_BUILD = build;
			}
			else
			{
				String buildDate = LAST_CURRENT_BUILD.substring(1, 9)
						+ LAST_CURRENT_BUILD.substring(10, LAST_CURRENT_BUILD.length());
				String baselineDate = LAST_BASELINE_BUILD.substring(LAST_BASELINE_BUILD.indexOf('_') + 1);
				if (build.compareTo(LAST_BASELINE_BUILD) > 0 && baselineDate.compareTo(buildDate) < 0)
				{
					LAST_BASELINE_BUILD = build;
				}
			}
		}
		else
		{
			if (LAST_CURRENT_BUILD == null || build.substring(1).compareTo(LAST_CURRENT_BUILD.substring(1)) >= 0)
			{
				LAST_CURRENT_BUILD = build;
			}
		}
		return index;
	}

	/*
	 * Store a configuration in the dynamic list. The list is sorted alphabetically.
	 */
	private int storeConfig(String config)
	{
		if (CONFIGS == null)
		{
			CONFIGS = new String[1];
			CONFIGS[0] = config;
			return 0;
		}
		int idx = Arrays.binarySearch(CONFIGS, config);
		if (idx >= 0)
			return idx;
		int length = CONFIGS.length;
		System.arraycopy(CONFIGS, 0, CONFIGS = new String[length + 1], 0, length);
		CONFIGS[length] = config;
		Arrays.sort(CONFIGS);
		return length;
	}

	/*
	 * Store a component in the dynamic list. The list is sorted alphabetically. Note that the array is rebuilt each
	 * time a new component is discovered as this does not happen so often (e.g. eclipse only has 10 components).
	 */
	private int storeComponent(String component)
	{
		if (COMPONENTS == null)
		{
			COMPONENTS = new String[1];
			COMPONENTS[0] = component;
			return 0;
		}
		int idx = Arrays.binarySearch(COMPONENTS, component);
		if (idx >= 0)
			return idx;
		int length = COMPONENTS.length;
		System.arraycopy(COMPONENTS, 0, COMPONENTS = new String[length + 1], 0, length);
		COMPONENTS[length] = component;
		Arrays.sort(COMPONENTS);
		return length;
	}

	/*
	 * Store a dimension in the dynamic list. The list is sorted in ascending order. Note that the array is rebuilt each
	 * time a new dimension is discovered as this does not happen so often (e.g. eclipse only stores two dimensions).
	 */
	public static int storeDimension(int id)
	{
		if (DIMENSIONS == null)
		{
			DIMENSIONS = new int[1];
			DIMENSIONS[0] = id;
			return 0;
		}
		int idx = Arrays.binarySearch(DIMENSIONS, id);
		if (idx >= 0)
			return idx;
		int length = DIMENSIONS.length;
		System.arraycopy(DIMENSIONS, 0, DIMENSIONS = new int[length + 1], 0, length);
		DIMENSIONS[length] = id;
		Arrays.sort(DIMENSIONS);
		return length;
	}

	/*
	 * Store a dimension in the dynamic list. The list is sorted alphabetically. Note that the array is rebuilt each
	 * time a new dimension is discovered as this does not happen so often (e.g. eclipse only stores two dimensions).
	 */
	private int storeVm(String vm)
	{
		if (VMS == null)
		{
			VMS = new String[1];
			VMS[0] = vm;
			return 0;
		}
		int idx = Arrays.binarySearch(VMS, vm);
		if (idx >= 0)
			return idx;
		int length = VMS.length;
		System.arraycopy(VMS, 0, VMS = new String[length + 1], 0, length);
		VMS[length] = vm;
		Arrays.sort(VMS);
		return length;
	}

}
