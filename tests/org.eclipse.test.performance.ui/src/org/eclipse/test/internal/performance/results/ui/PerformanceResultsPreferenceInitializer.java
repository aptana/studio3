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
package org.eclipse.test.internal.performance.results.ui;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.test.internal.performance.PerformanceTestPlugin;
import org.eclipse.test.internal.performance.data.Dim;
import org.eclipse.test.internal.performance.results.utils.IPerformancesConstants;
import org.eclipse.test.performance.Dimension;

/**
 * Default performances preferences initializer.
 */
public class PerformanceResultsPreferenceInitializer extends AbstractPreferenceInitializer implements IPerformancesConstants {

/*
 * (non-Javadoc)
 *
 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
 */
public void initializeDefaultPreferences() {
	IEclipsePreferences defaultPreferences = ((IScopeContext) new DefaultScope()).getNode(PLUGIN_ID);

	// Eclipse version
	defaultPreferences.putInt(PRE_ECLIPSE_VERSION, IPerformancesConstants.DEFAULT_ECLIPSE_VERSION);

	// Database
	defaultPreferences.putBoolean(PRE_DATABASE_CONNECTION, IPerformancesConstants.DEFAULT_DATABASE_CONNECTION);
	defaultPreferences.putBoolean(PRE_DATABASE_LOCAL, IPerformancesConstants.DEFAULT_DATABASE_LOCAL);
	defaultPreferences.put(PRE_DATABASE_LOCATION, IPerformancesConstants.NETWORK_DATABASE_LOCATION);

	// Status
	defaultPreferences.putInt(PRE_WRITE_STATUS, IPerformancesConstants.DEFAULT_WRITE_STATUS);

	// Comparison
	defaultPreferences.putInt(PRE_COMPARISON_THRESHOLD_FAILURE, IPerformancesConstants.DEFAULT_COMPARISON_THRESHOLD_FAILURE);
	defaultPreferences.putInt(PRE_COMPARISON_THRESHOLD_ERROR, IPerformancesConstants.DEFAULT_COMPARISON_THRESHOLD_ERROR);
	defaultPreferences.putInt(PRE_COMPARISON_THRESHOLD_IMPROVEMENT, IPerformancesConstants.DEFAULT_COMPARISON_THRESHOLD_IMPROVEMENT);

	// Config descriptors
	String[][] configDescriptors = PerformanceTestPlugin.getConfigDescriptors();
	int cdLength = configDescriptors.length;
	for (int i = 0; i < cdLength; i++) {
		String[] descriptor = configDescriptors[i];
		defaultPreferences.put(PRE_CONFIG_DESCRIPTOR_NAME + "." + i, descriptor[0]);
		defaultPreferences.put(PRE_CONFIG_DESCRIPTOR_DESCRIPTION + "." + i, descriptor[1]);
	}

	// Default dimension
	defaultPreferences.put(PRE_DEFAULT_DIMENSION, ((Dim) PerformanceTestPlugin.getDefaultDimension()).getName());

	// Result dimensions
	Dimension[] dimensions = PerformanceTestPlugin.getResultsDimensions();
	int length = dimensions.length;
	for (int i = 0; i < length; i++) {
		Dim dim = (Dim) dimensions[i];
		defaultPreferences.put(PRE_RESULTS_DIMENSION + "." + i, dim.getName());
	}

	// Filters
	defaultPreferences.putBoolean(PRE_FILTER_ADVANCED_SCENARIOS, IPerformancesConstants.DEFAULT_FILTER_ADVANCED_SCENARIOS);
	defaultPreferences.putBoolean(PRE_FILTER_OLD_BUILDS, IPerformancesConstants.DEFAULT_FILTER_OLD_BUILDS);
	defaultPreferences.putBoolean(PRE_FILTER_NIGHTLY_BUILDS, IPerformancesConstants.DEFAULT_FILTER_NIGHTLY_BUILDS);

	// Milestones
	String[] milestones = IPerformancesConstants.V36_MILESTONES;
	String prefix = PRE_MILESTONE_BUILDS + "." + ECLIPSE_MAINTENANCE_VERSION;
	length = milestones.length;
	for (int i = 0; i < length; i++) {
		defaultPreferences.put(prefix + i, milestones[i]);
	}
	milestones = IPerformancesConstants.V37_MILESTONES;
	prefix = PRE_MILESTONE_BUILDS + "." + ECLIPSE_DEVELOPMENT_VERSION;
	length = milestones.length;
	for (int i = 0; i < length; i++) {
		defaultPreferences.put(prefix + i, milestones[i]);
	}
}

}
