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
package org.eclipse.test.internal.performance;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.test.internal.performance.data.Dim;
import org.eclipse.test.internal.performance.db.DB;
import org.eclipse.test.internal.performance.db.Variations;
import org.eclipse.test.performance.Dimension;
import org.osgi.framework.BundleContext;


/**
 * @since 3.1
 */
public class PerformanceTestPlugin extends Plugin {

    public static final String CONFIG= "config"; //$NON-NLS-1$
	public static final String BUILD= "build"; //$NON-NLS-1$

	private static final String DEFAULT_DB_NAME= "perfDB"; //$NON-NLS-1$
	private static final String DEFAULT_DB_USER= "guest"; //$NON-NLS-1$
	private static final String DEFAULT_DB_PASSWORD= "guest"; //$NON-NLS-1$

	private static final String DB_NAME= "dbname"; //$NON-NLS-1$
	private static final String DB_USER= "dbuser"; //$NON-NLS-1$
	private static final String DB_PASSWD= "dbpasswd"; //$NON-NLS-1$

    /*
	 * New properties
	 */
    private static final String ECLIPSE_PERF_DBLOC = "eclipse.perf.dbloc"; //$NON-NLS-1$
    private static final String ECLIPSE_PERF_ASSERTAGAINST = "eclipse.perf.assertAgainst"; //$NON-NLS-1$
    private static final String ECLIPSE_PERF_CONFIG = "eclipse.perf.config"; //$NON-NLS-1$
    private static final String ECLIPSE_PERF_DEFAULT_DIM = "eclipse.perf.default.dim"; //$NON-NLS-1$
    private static final String ECLIPSE_PERF_RESULTS_DIMENSIONS = "eclipse.perf.results.dimensions"; //$NON-NLS-1$
    private static final String ECLIPSE_PERF_CONFIGS_DESCRIPTOR = "eclipse.perf.configs.descriptor"; //$NON-NLS-1$

	/**
	 * Supported dimensions.
	 * <p>
	 * Currently all the dimensions accessible through this interface:
	 *	<ul>
	 * 		<li>{@link Dimension#KERNEL_TIME}</li>
	 * 		<li>{@link Dimension#CPU_TIME}</li>
	 * 		<li>{@link Dimension#WORKING_SET}</li>
	 * 		<li>{@link Dimension#ELAPSED_PROCESS}</li>
	 * 		<li>{@link Dimension#USED_JAVA_HEAP}</li>
	 * 		<li>{@link Dimension#WORKING_SET_PEAK}</li>
	 * 		<li>{@link Dimension#KERNEL_TIME}</li>
	 *	</ul>
	 * </p>
	 */
	private static final Dim[] SUPPORTED_DIMENSIONS = {
			InternalDimensions.KERNEL_TIME,
			InternalDimensions.CPU_TIME,
			InternalDimensions.WORKING_SET,
			InternalDimensions.ELAPSED_PROCESS,
			InternalDimensions.USED_JAVA_HEAP,
			InternalDimensions.WORKING_SET_PEAK,
			InternalDimensions.COMITTED,
	};

	/**
	 * Identifiers of the supported dimensions.
	 */
	private static final int[] SUPPORTED_DIMENSIONS_ID = new int[SUPPORTED_DIMENSIONS.length];
	static {
		int length = SUPPORTED_DIMENSIONS.length;
		for (int i = 0; i < length; i++) {
		    SUPPORTED_DIMENSIONS_ID[i] = SUPPORTED_DIMENSIONS[i].getId();
	    }
	}

	/**
	 * Default machines names.
	 */
	private static final String DEFAULT_CONFIG_DESCRIPTORS =
		"eplnx1,SLED 10 Sun 1.6.0_17 (2 x 3.00GHz - 3GB RAM); " + //$NON-NLS-1$
		"eplnx2,RHEL 5.0 Sun 6.0_04 (2 x 3.00GHz - 3GB RAM); " + //$NON-NLS-1$
		"epwin2,Win XP Sun 1.6.0_17 (2 x 3.00GHz - 3GB RAM); " + //$NON-NLS-1$
		"epwin3,Win XP Sun 6.0_17 (2 x 3.00GHz - 3GB RAM);"; //$NON-NLS-1$

	/**
	 * Default dimension use for performance results.
	 */
	private static final Dim DEFAULT_DIMENSION = InternalDimensions.ELAPSED_PROCESS;

	/**
	 * The dimensions which are put in the generated performance results pages.
	 */
	private static final Dim[] DEFAULT_RESULTS_DIMENSIONS = {
		InternalDimensions.ELAPSED_PROCESS,
		InternalDimensions.CPU_TIME,
	};

	/**
	 * The plug-in ID
	 */
    public static final String PLUGIN_ID= "org.eclipse.test.performance"; //$NON-NLS-1$

	/** Status code describing an internal error */
	public static final int INTERNAL_ERROR= 1;

	/**
	 * The shared instance.
	 */
	private static PerformanceTestPlugin fgPlugin;

	/* temporary code */
	private static boolean fgOldDBInitialized;
	private static boolean fgOldDB;	// true if we are talking to the old perfDB in Ottawa

	/**
	 * The constructor.
	 */
	public PerformanceTestPlugin() {
	    super();
		fgPlugin= this;
	}

	static boolean isOldDB() {
	    if (!fgOldDBInitialized) {
			String loc= getDBLocation();
			if (loc != null && loc.indexOf("relengbuildserv") >= 0) //$NON-NLS-1$
			    fgOldDB= true;
	        fgOldDBInitialized= true;
	    }
	    return fgOldDB;
	}

	public void stop(BundleContext context) throws Exception {
		DB.shutdown();
		super.stop(context);
	}

	/*
	 * Returns the shared instance.
	 */
	public static PerformanceTestPlugin getDefault() {
		return fgPlugin;
	}

	/*
	 * -Declipse.perf.dbloc=net://localhost
	 */
	public static String getDBLocation() {
		String dbloc= System.getProperty(ECLIPSE_PERF_DBLOC);
		if (dbloc != null) {
		    Variations keys= new Variations();
		    keys.parsePairs(ECLIPSE_PERF_DBLOC + '=' + dbloc);
		    return keys.getProperty(ECLIPSE_PERF_DBLOC);
		}
		return null;
	}

	public static String getDBName() {
		String dbloc= System.getProperty(ECLIPSE_PERF_DBLOC);
		if (dbloc != null) {
		    Variations keys= new Variations();
		    keys.parsePairs(ECLIPSE_PERF_DBLOC + '=' + dbloc);
		    return keys.getProperty(DB_NAME, DEFAULT_DB_NAME);
		}
	    return DEFAULT_DB_NAME;
	}

	public static String getDBUser() {
		String dbloc= System.getProperty(ECLIPSE_PERF_DBLOC);
		if (dbloc != null) {
		    Variations keys= new Variations();
		    keys.parsePairs(ECLIPSE_PERF_DBLOC + '=' + dbloc);
		    return keys.getProperty(DB_USER, DEFAULT_DB_USER);
		}
	    return DEFAULT_DB_USER;
	}

	/**
	 * Returns the default dimension used for performance results.
	 *
	 * @return The default {@link Dimension}  or <code>null</code> if the specified default dimension
	 * does not match any known dimensions name.
	 */
	public static Dimension getDefaultDimension() {
		String defaultDim = System.getProperty(ECLIPSE_PERF_DEFAULT_DIM);
		if (defaultDim == null)  return DEFAULT_DIMENSION;
		Dimension dimension = getDimension(defaultDim);
		Assert.isNotNull(dimension, "Invalid default dimension found in system property '"+ECLIPSE_PERF_DEFAULT_DIM+"': "+defaultDim); //$NON-NLS-1$ //$NON-NLS-2$
		return dimension;
	}

	/**
	 * Returns the dimensions displayed in the performance results.
	 *
	 * @return The list of {@link Dimension}   which will be displayed in the generated results data pages.
	 */
	public static Dimension[] getResultsDimensions() {
		String resultsDimension = System.getProperty(ECLIPSE_PERF_RESULTS_DIMENSIONS);
		if (resultsDimension == null)  return DEFAULT_RESULTS_DIMENSIONS;
		StringTokenizer tokenizer = new StringTokenizer(resultsDimension, ","); //$NON-NLS-1$
		List list = new ArrayList();
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken().trim();
			Dimension dimension = getDimension(token);
			if (dimension == null) {
				try {
					dimension = getDimension(Integer.parseInt(token));
				} catch (NumberFormatException e) {
					// skip
				}
				if (dimension == null) {
					System.err.println("Skip invalid results dimension found in system property '"+ECLIPSE_PERF_RESULTS_DIMENSIONS+"': "+resultsDimension); //$NON-NLS-1$ //$NON-NLS-2$
					continue;
				}
			}
			list.add(dimension);
		}
		int size = list.size();
		if (size == 0) {
			System.err.println("No valid dimension was found in system property '"+ECLIPSE_PERF_RESULTS_DIMENSIONS+"'!"); //$NON-NLS-1$ //$NON-NLS-2$
			System.err.println("=> default results dimensions will be used instead!"); //$NON-NLS-1$
			return DEFAULT_RESULTS_DIMENSIONS;
		}
		Dimension[] dimensions = new Dimension[size];
		list.toArray(dimensions);
		return dimensions;
	}

	/**
	 * Returns the names of the dimensions which may be used in the performance results.
	 *
	 * @return The list of the dimension names
	 */
	public static List getDimensions() {
		List dimensions = new ArrayList();
		for (int i = 0; i < SUPPORTED_DIMENSIONS.length; i++) {
	        dimensions.add(SUPPORTED_DIMENSIONS[i].getName());
        }
		return dimensions;
	}

	/**
	 * Return the dimension corresponding to the given id.
	 *
	 * @param id The id of the searched dimension.
	 * @return The {@link Dimension}  or <code>null</code> if none is found.
	 */
	public static Dimension getDimension(int id) {
		int length = SUPPORTED_DIMENSIONS.length;
		for (int i = 0; i < length; i++) {
			if (SUPPORTED_DIMENSIONS[i].getId() == id) {
				return SUPPORTED_DIMENSIONS[i];
			}
		}
		return null;
	}

	/**
	 * Return the dimension corresponding to the given id.
	 *
	 * @param name The name of the searched dimension.
	 * @return The {@link Dimension}  or <code>null</code> if none is found.
	 */
	public static Dimension getDimension(String name) {
		int length = SUPPORTED_DIMENSIONS.length;
		for (int i = 0; i < length; i++) {
			if (SUPPORTED_DIMENSIONS[i].getName().equals(name)) {
				return SUPPORTED_DIMENSIONS[i];
			}
		}
		return null;
	}

	/**
	 * Return the descriptors of the configurations used for the performances.
	 *
	 * @return An array of 2 strings, the former is the name of the configuration and the latter its description.
	 */
	public static String[][] getConfigDescriptors() {
		String descriptors = System.getProperty(ECLIPSE_PERF_CONFIGS_DESCRIPTOR, DEFAULT_CONFIG_DESCRIPTORS);
		StringTokenizer tokenizer = new StringTokenizer(descriptors, ",;"); //$NON-NLS-1$
		List list = new ArrayList();
		while (tokenizer.hasMoreTokens()) {
			String[] config = new String[2];
			config[0] = tokenizer.nextToken().trim(); // config name
			config[1] = tokenizer.nextToken().trim(); // config description
			list.add(config);
		}
		String[][] configDescriptors = new String[list.size()][];
		list.toArray(configDescriptors);
		return configDescriptors;
	}

	public static String getDBPassword() {
		String dbloc= System.getProperty(ECLIPSE_PERF_DBLOC);
		if (dbloc != null) {
		    Variations keys= new Variations();
		    keys.parsePairs(ECLIPSE_PERF_DBLOC + '=' + dbloc);
		    return keys.getProperty(DB_PASSWD, DEFAULT_DB_PASSWORD);
		}
	    return DEFAULT_DB_PASSWORD;
	}

	/*
	 * -Declipse.perf.config=<varname1>=<varval1>;<varname2>=<varval2>;...;<varnameN>=<varvalN>
	 */
	public static Variations getVariations() {
	    Variations keys= new Variations();
		String configKey= System.getProperty(ECLIPSE_PERF_CONFIG);
		if (configKey != null)
		    keys.parsePairs(configKey);
	    return keys;
	}

	/*
	 * -Declipse.perf.assertAgainst=<varname1>=<varval1>;<varname2>=<varval2>;...;<varnameN>=<varvalN>
	 * Returns null if assertAgainst property isn't defined.
	 */
	public static Variations getAssertAgainst() {
		String assertKey= System.getProperty(ECLIPSE_PERF_ASSERTAGAINST);
		if (assertKey != null) {
		    Variations keys= getVariations();
		    if (keys == null)
		        keys= new Variations();
		    keys.parsePairs(assertKey);
		    return keys;
		}
	    return null;
	}

	// logging

	public static void logError(String message) {
		if (message == null)
			message= ""; //$NON-NLS-1$
		log(new Status(IStatus.ERROR, PLUGIN_ID, INTERNAL_ERROR, message, null));
	}

	public static void logWarning(String message) {
		if (message == null)
			message= ""; //$NON-NLS-1$
		log(new Status(IStatus.WARNING, PLUGIN_ID, IStatus.OK, message, null));
	}

	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, INTERNAL_ERROR, "Internal Error", e)); //$NON-NLS-1$
	}

	public static void log(IStatus status) {
	    if (fgPlugin != null) {
	        fgPlugin.getLog().log(status);
	    } else {
	        switch (status.getSeverity()) {
	        case IStatus.ERROR:
		        System.err.println("Error: " + status.getMessage()); //$NON-NLS-1$
	            break;
	        case IStatus.WARNING:
		        System.err.println("Warning: " + status.getMessage()); //$NON-NLS-1$
	            break;
	        }
	        Throwable exception= status.getException();
	        if (exception != null)
	            exception.printStackTrace(System.err);
	    }
	}
}
