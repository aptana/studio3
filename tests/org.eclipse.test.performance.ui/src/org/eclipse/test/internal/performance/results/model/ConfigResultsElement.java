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
package org.eclipse.test.internal.performance.results.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.test.internal.performance.results.db.*;
import org.eclipse.test.internal.performance.results.utils.IPerformancesConstants;
import org.eclipse.test.internal.performance.results.utils.Util;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class ConfigResultsElement extends ResultsElement {

	// Elements
	BuildResultsElement currentBuild, baselineBuild;

	// Property descriptors
	static final String P_ID_CONFIG_NAME = "ConfigResultsElement.name"; //$NON-NLS-1$
	static final String P_ID_CONFIG_DESCRIPTION = "ConfigResultsElement.description"; //$NON-NLS-1$
	static final String P_ID_CONFIG_CURRENT_BUILD = "ConfigResultsElement.currentbuild"; //$NON-NLS-1$
	static final String P_ID_CONFIG_BASELINE_BUILD = "ConfigResultsElement.baselinebuild"; //$NON-NLS-1$
	static final String P_ID_CONFIG_BASELINED = "ConfigResultsElement.baselined"; //$NON-NLS-1$
	static final String P_ID_CONFIG_VALID = "ConfigResultsElement.valid"; //$NON-NLS-1$
	static final String P_ID_CONFIG_DELTA = "ConfigResultsElement.delta"; //$NON-NLS-1$
	static final String P_ID_CONFIG_ERROR = "ConfigResultsElement.error"; //$NON-NLS-1$

	static final String P_STR_CONFIG_NAME = "internal name"; //$NON-NLS-1$
	static final String P_STR_CONFIG_DESCRIPTION = "description"; //$NON-NLS-1$
	static final String P_STR_CONFIG_CURRENT_BUILD = "current build"; //$NON-NLS-1$
	static final String P_STR_CONFIG_BASELINE_BUILD = "baseline build"; //$NON-NLS-1$
	static final String P_STR_CONFIG_BASELINED = "has baseline"; //$NON-NLS-1$
	static final String P_STR_CONFIG_VALID = "is valid"; //$NON-NLS-1$
	static final String P_STR_CONFIG_DELTA = "delta with baseline"; //$NON-NLS-1$
	static final String P_STR_CONFIG_ERROR = "delta error"; //$NON-NLS-1$

	private static final TextPropertyDescriptor CONFIG_NAME_DESCRIPTOR = new TextPropertyDescriptor(P_ID_CONFIG_NAME, P_STR_CONFIG_NAME);
	private static final TextPropertyDescriptor CONFIG_DESCRIPTION_DESCRIPTOR = new TextPropertyDescriptor(P_ID_CONFIG_DESCRIPTION, P_STR_CONFIG_DESCRIPTION);
	private static final PropertyDescriptor CONFIG_CURRENT_BUILD_DESCRIPTOR = new PropertyDescriptor(P_ID_CONFIG_CURRENT_BUILD, P_STR_CONFIG_CURRENT_BUILD);
	private static final PropertyDescriptor CONFIG_BASELINE_BUILD_DESCRIPTOR = new PropertyDescriptor(P_ID_CONFIG_BASELINE_BUILD, P_STR_CONFIG_BASELINE_BUILD);
	private static final PropertyDescriptor CONFIG_BASELINED_DESCRIPTOR = new PropertyDescriptor(P_ID_CONFIG_BASELINED, P_STR_CONFIG_BASELINED);
	private static final PropertyDescriptor CONFIG_VALID_DESCRIPTOR = new PropertyDescriptor(P_ID_CONFIG_VALID, P_STR_CONFIG_VALID);
	private static final PropertyDescriptor CONFIG_DELTA_DESCRIPTOR = new PropertyDescriptor(P_ID_CONFIG_DELTA, P_STR_CONFIG_DELTA);
	private static final PropertyDescriptor CONFIG_ERROR_DESCRIPTOR = new PropertyDescriptor(P_ID_CONFIG_ERROR, P_STR_CONFIG_ERROR);

    private static Vector DESCRIPTORS;
    static Vector initDescriptors(int status) {
		DESCRIPTORS = new Vector();
		// Status category
		DESCRIPTORS.add(getInfosDescriptor(status));
		DESCRIPTORS.add(getWarningsDescriptor(status));
		DESCRIPTORS.add(ERROR_DESCRIPTOR);
		ERROR_DESCRIPTOR.setCategory("Status");
		// Results category
		DESCRIPTORS.addElement(CONFIG_NAME_DESCRIPTOR);
		CONFIG_NAME_DESCRIPTOR.setCategory("Results");
		DESCRIPTORS.addElement(CONFIG_DESCRIPTION_DESCRIPTOR);
		CONFIG_DESCRIPTION_DESCRIPTOR.setCategory("Results");
		DESCRIPTORS.addElement(CONFIG_CURRENT_BUILD_DESCRIPTOR);
		CONFIG_CURRENT_BUILD_DESCRIPTOR.setCategory("Results");
		DESCRIPTORS.addElement(CONFIG_BASELINE_BUILD_DESCRIPTOR);
		CONFIG_BASELINE_BUILD_DESCRIPTOR.setCategory("Results");
		DESCRIPTORS.addElement(CONFIG_BASELINED_DESCRIPTOR);
		CONFIG_BASELINED_DESCRIPTOR.setCategory("Results");
		DESCRIPTORS.addElement(CONFIG_VALID_DESCRIPTOR);
		CONFIG_VALID_DESCRIPTOR.setCategory("Results");
		DESCRIPTORS.addElement(CONFIG_DELTA_DESCRIPTOR);
		CONFIG_DELTA_DESCRIPTOR.setCategory("Results");
		DESCRIPTORS.addElement(CONFIG_ERROR_DESCRIPTOR);
		CONFIG_ERROR_DESCRIPTOR.setCategory("Results");
		// Survey category
		DESCRIPTORS.add(COMMENT_DESCRIPTOR);
		COMMENT_DESCRIPTOR.setCategory("Survey");
		return DESCRIPTORS;
	}
    static ComboBoxPropertyDescriptor getInfosDescriptor(int status) {
		List list = new ArrayList();
		if ((status & SMALL_VALUE) != 0) {
			list.add("This test and/or its variation has a small value on this machine, hence it may not be necessary to spend time on fixing it if a regression occurs");
		}
		if ((status & STUDENT_TTEST) != 0) {
			list.add("The student-t test error on this machine is over the threshold");
		}
		String[] infos = new String[list.size()];
		if (list.size() > 0) {
			list.toArray(infos);
		}
		ComboBoxPropertyDescriptor infoDescriptor = new ComboBoxPropertyDescriptor(P_ID_STATUS_INFO, P_STR_STATUS_INFO, infos);
		infoDescriptor.setCategory("Status");
		return infoDescriptor;
	}
    static PropertyDescriptor getWarningsDescriptor(int status) {
		List list = new ArrayList();
		if ((status & BIG_ERROR) != 0) {
			list.add("The error on this machine is over the 3% threshold, hence its result may not be really reliable");
		}
		if ((status & NOT_RELIABLE) != 0) {
			list.add("The results history for this machine shows that the variation of its delta is over 20%, hence its result is surely not reliable");
		}
		if ((status & NOT_STABLE) != 0) {
			list.add("The results history for this machine shows that the variation of its delta is between 10% and 20%, hence its result may not be really reliable");
		}
		if ((status & NO_BASELINE) != 0) {
			list.add("There's no baseline for this machine to compare with");
		}
		if ((status & SINGLE_RUN) != 0) {
			list.add("This test has only one run on this machine, hence no error can be computed to verify if it's stable enough to be reliable");
		}
		if ((status & STUDENT_TTEST) != 0) {
			list.add("The student-t test error on this machine is over the threshold");
		}
		String[] warnings = new String[list.size()];
		if (list.size() > 0) {
			list.toArray(warnings);
		}
		ComboBoxPropertyDescriptor warningDescriptor = new ComboBoxPropertyDescriptor(P_ID_STATUS_WARNING, P_STR_STATUS_WARNING, warnings);
		warningDescriptor.setCategory("Status");
		return warningDescriptor;
	}
    static Vector getDescriptors() {
    	return DESCRIPTORS;
	}

public ConfigResultsElement(AbstractResults results, ResultsElement parent) {
	super(results, parent);
}

public int compareTo(Object o) {
	// TODO Auto-generated method stub
	return super.compareTo(o);
}
ResultsElement createChild(AbstractResults testResults) {
	return new BuildResultsElement(testResults, this);
}

BuildResultsElement getBaselineBuild() {
	if (this.baselineBuild == null) {
		this.baselineBuild = new BuildResultsElement(getConfigResults().getBaselineBuildResults(), this);
	}
	return this.baselineBuild;
}

/**
 * Get the baseline build used for this configuration.
 *
 * @param buildName The name of the build to have the baseline
 * @return The baseline build as {@link BuildResultsElement}.
 */
public String getBaselineBuildName(String buildName) {
	return getConfigResults().getBaselineBuildResults(buildName).getName();
}

private ConfigResults getConfigResults() {
	return (ConfigResults) this.results;
}

BuildResultsElement getCurrentBuild() {
	if (this.currentBuild == null) {
		this.currentBuild = new BuildResultsElement(getConfigResults().getCurrentBuildResults(), this);
	}
	return this.currentBuild;
}

public String getLabel(Object o) {
	String description = getConfigResults().getDescription();
	int index = description.indexOf(" (");
	if (index <= 0) {
		return description;
	}
	return description.substring(0, index);
}

/*
 * (non-Javadoc)
 *
 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
 */
public IPropertyDescriptor[] getPropertyDescriptors() {
	Vector descriptors = getDescriptors();
	if (descriptors == null) {
		descriptors = initDescriptors(getStatus());
	}
	int size = descriptors.size();
	IPropertyDescriptor[] descriptorsArray = new IPropertyDescriptor[size];
	descriptorsArray[0] = getInfosDescriptor(getStatus());
	descriptorsArray[1] = getWarningsDescriptor(getStatus());
	for (int i=2; i<size; i++) {
		descriptorsArray[i] = (IPropertyDescriptor) descriptors.get(i);
	}
	return descriptorsArray;
}

/*
 * (non-Javadoc)
 *
 * @see
 * org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang
 * .Object)
 */
public Object getPropertyValue(Object propKey) {
	ConfigResults configResults = getConfigResults();
	if (propKey.equals(P_ID_CONFIG_NAME)) {
		return configResults.getName();
	}
	if (propKey.equals(P_ID_CONFIG_DESCRIPTION)) {
		return configResults.getDescription();
	}
	if (propKey.equals(P_ID_CONFIG_CURRENT_BUILD)) {
		return getCurrentBuild();
	}
	if (propKey.equals(P_ID_CONFIG_BASELINE_BUILD)) {
		return getBaselineBuild();
	}
	if (propKey.equals(P_ID_CONFIG_BASELINED)) {
		return new Boolean(configResults.isBaselined());
	}
	if (propKey.equals(P_ID_CONFIG_VALID)) {
		return new Boolean(configResults.isValid());
	}
	if (propKey.equals(P_ID_CONFIG_DELTA)) {
		return new Double(configResults.getDelta());
	}
	if (propKey.equals(P_ID_CONFIG_ERROR)) {
		return new Double(configResults.getError());
	}
	if (propKey.equals(P_ID_STATUS_ERROR)) {
		if (getStatus() == MISSING) {
			PerformanceResultsElement performanceResultsElement = (PerformanceResultsElement) ((ResultsElement)((ResultsElement)getParent(null)).getParent(null)).getParent(null);
			return "No result for build "+performanceResultsElement.getName()+" on this machine!";
		}
		if ((getStatus() & BIG_DELTA) != 0) {
			return "The delta on this machine is over the 10% threshold, hence may indicate a possible regression";
		}
	}
	return super.getPropertyValue(propKey);
}

/**
 * Return the statistics of the build along its history.
 *
 * @return An array of double built as follows:
 * <ul>
 * <li>0:	numbers of values</li>
 * <li>1:	mean of values</li>
 * <li>2:	standard deviation of these values</li>
 * <li>3:	coefficient of variation of these values</li>
 * </ul>
 */
public double[] getStatistics() {
	if (this.statistics  == null) {
		this.statistics = getConfigResults().getStatistics(Util.BASELINE_BUILD_PREFIXES);
	}
	return this.statistics;
}

void initStatus() {
	ConfigResults configResults = getConfigResults();
	if (configResults.isValid()) {
		initStatus(configResults.getCurrentBuildResults());
	} else {
		this.status = MISSING;
	}
}

/*
 * Write the element status in the given stream
 */
StringBuffer getFailures(StringBuffer buffer, int kind, StringBuffer excluded) {
	if ((this.status & BIG_DELTA) != 0) { // there's a failure on this config

		// Get numbers
		int buildsNumber = kind & IPerformancesConstants.STATUS_BUILDS_NUMBER_MASK;
		ConfigResults configResults = getConfigResults();
		double[][] numbers = configResults.getLastNumbers(buildsNumber);
		int numbersLength = numbers.length;

		// if there are several builds to confirm the regression, then verify all deltas
		if (numbersLength > 1) {
			if (numbersLength < buildsNumber) {
				// there's not enough builds to wee whether there's a real regression, hence skip result
				if (excluded != null) {
					excluded.append(configResults+" excluded from status because there's only "+numbersLength+" builds available although "+buildsNumber+" is required to decide a regression is confirmed or not!");
					excluded.append(Util.LINE_SEPARATOR);
				}
				return buffer;
			}
			int confirmed = 1;
			for (int i=1; i<numbersLength; i++) {
				if (numbers[i] != null && numbers[i][AbstractResults.DELTA_VALUE_INDEX] < -0.1) {
					confirmed++;
				}
			}
			float ratio = ((float) confirmed) / numbersLength;
			if (ratio < 0.8) {
				// more than 20% of previous build didn't fail, hence skip result
				if (excluded != null) {
					excluded.append(configResults+" excluded from status because only "+confirmed+" builds failed on last "+buildsNumber+" ones!");
					excluded.append(Util.LINE_SEPARATOR);
				}
				return buffer;
			}
		}

		// Add values
		double[] values = numbers[0];
		double buildValue = values[AbstractResults.BUILD_VALUE_INDEX];
		double baselineValue = values[AbstractResults.BASELINE_VALUE_INDEX];
		double delta = values[AbstractResults.DELTA_VALUE_INDEX];
		double error = values[AbstractResults.DELTA_ERROR_INDEX];
		StringBuffer localBuffer = new StringBuffer("		");
		localBuffer.append(configResults.getName());
		double[] stats = null;
		boolean printValues = (kind & IPerformancesConstants.STATUS_VALUES) != 0;
		if (printValues) {
			localBuffer.append("	");
			localBuffer.append(buildValue);
			localBuffer.append("	");
			localBuffer.append(baselineValue);
			localBuffer.append("	");
			localBuffer.append(buildValue-baselineValue);
			localBuffer.append("	");
			localBuffer.append(Util.PERCENTAGE_FORMAT.format(delta));
			localBuffer.append("	");
			localBuffer.append(Util.PERCENTAGE_FORMAT.format(error));
			stats = getStatistics();
			if (stats != null) {
				localBuffer.append("	");
				localBuffer.append((int) stats[0]);
				localBuffer.append("	");
				localBuffer.append(Util.DOUBLE_FORMAT.format(stats[1]));
				localBuffer.append("	");
				localBuffer.append(Util.DOUBLE_FORMAT.format(stats[2]));
				localBuffer.append("	");
				localBuffer.append(Util.PERCENTAGE_FORMAT.format(stats[3]));
			}
		}

		/* Add comment
		IEclipsePreferences preferences = new InstanceScope().getNode(IPerformancesConstants.PLUGIN_ID);
		String comment = preferences.get(getId(), null);
		if (comment != null) {
			if (stats == null && printValues) {
				buffer.append("				");
			}
			buffer.append("	");
			buffer.append(comment);
		}
		*/

		// Add status info
		if (this.status != BIG_DELTA) { // there's some other info in the status
//			if (comment == null) {
				if (stats == null && printValues) {
					localBuffer.append("				");
				}
//			}
			localBuffer.append("	");
			String separator = "";

			// Error
			if ((this.status & BIG_ERROR) != 0) {
				int statusErrorLevel = kind & IPerformancesConstants.STATUS_ERROR_LEVEL_MASK;
				if (statusErrorLevel == IPerformancesConstants.STATUS_ERROR_NOTICEABLE) {
					// Skip result
					if (excluded != null) {
						excluded.append(configResults+" excluded from status due to a noticeable error!");
						excluded.append(Util.LINE_SEPARATOR);
					}
					return buffer;
				}
				localBuffer.append(separator);
				localBuffer.append("error (");
				localBuffer.append(Util.PERCENTAGE_FORMAT.format(error));
				localBuffer.append(")");
				separator = "+";
				double ratio = -(error/delta);
				if (ratio > 1) {
					switch (statusErrorLevel) {
						case IPerformancesConstants.STATUS_ERROR_INVALID:
						case IPerformancesConstants.STATUS_ERROR_WEIRD:
						case IPerformancesConstants.STATUS_ERROR_SUSPICIOUS:
							// Skip result
							if (excluded != null) {
								excluded.append(configResults+" excluded from status due to an invalid error!");
								excluded.append(Util.LINE_SEPARATOR);
							}
							return buffer;
					}
					localBuffer.append(": invalid measure!");
				} else if (ratio > 0.5) {
					switch (statusErrorLevel) {
						case IPerformancesConstants.STATUS_ERROR_WEIRD:
						case IPerformancesConstants.STATUS_ERROR_SUSPICIOUS:
							// Skip result
							if (excluded != null) {
								excluded.append(configResults+" excluded from status due to a weird error!");
								excluded.append(Util.LINE_SEPARATOR);
							}
							return buffer;
					}
					localBuffer.append(": weird measure!");
				} else if (ratio > 0.25) {
					if (statusErrorLevel == IPerformancesConstants.STATUS_ERROR_SUSPICIOUS) {
						// Skip result
						if (excluded != null) {
							excluded.append(configResults+" excluded from status due to a suspicious error!");
							excluded.append(Util.LINE_SEPARATOR);
						}
						return buffer;
					}
					localBuffer.append(": suspicious measure!");
				}
			}

			// Small value
			if ((this.status & SMALL_VALUE) != 0) {
				int statusSmallValue = kind & IPerformancesConstants.STATUS_SMALL_VALUE_MASK;
				localBuffer.append(separator);
				if (buildValue < 100) {
					if (statusSmallValue == IPerformancesConstants.STATUS_SMALL_VALUE_BUILD) {
						// Skip result
						if (excluded != null) {
							excluded.append(configResults+" excluded from status due to a small build value!");
							excluded.append(Util.LINE_SEPARATOR);
						}
						return buffer;
					}
					localBuffer.append("small build value (");
					localBuffer.append((int)buildValue);
					localBuffer.append("ms)");
				}
				int diff = (int) Math.abs(baselineValue - buildValue);
				if (diff < 100) {
					if (statusSmallValue == IPerformancesConstants.STATUS_SMALL_VALUE_DELTA) {
						// Skip result
						if (excluded != null) {
							excluded.append(configResults+" excluded from status due to a small delta value!");
							excluded.append(Util.LINE_SEPARATOR);
						}
						return buffer;
					}
					localBuffer.append("small delta value (");
					localBuffer.append(diff);
					localBuffer.append("ms)");
				}
				separator = "+";
			}

			// Statistics
			if ((this.status & NOT_RELIABLE) != 0) {
				switch (kind & IPerformancesConstants.STATUS_STATISTICS_MASK) {
					case IPerformancesConstants.STATUS_STATISTICS_UNSTABLE:
					case IPerformancesConstants.STATUS_STATISTICS_ERRATIC:
						// Skip result
						if (excluded != null) {
							excluded.append(configResults+" excluded from status due to erratic statistics!");
							excluded.append(Util.LINE_SEPARATOR);
						}
						return buffer;
				}
				localBuffer.append(separator);
				localBuffer.append("erratic");
				separator = "+";
			} else if ((this.status & NOT_STABLE) != 0) {
				if ((kind & IPerformancesConstants.STATUS_STATISTICS_UNSTABLE) != 0) {
					// Skip result
					if (excluded != null) {
						excluded.append(configResults+" excluded from status due to unstable statistics!");
						excluded.append(Util.LINE_SEPARATOR);
					}
					return buffer;
				}
				localBuffer.append(separator);
				localBuffer.append("unstable");
				separator = "+";
			}
		}

		// Write status
		buffer.append(localBuffer);
		buffer.append(Util.LINE_SEPARATOR);
	}
	return buffer;
}

}
