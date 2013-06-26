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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.test.internal.performance.data.Dim;
import org.eclipse.test.internal.performance.eval.StatisticsUtil;
import org.eclipse.test.internal.performance.results.db.*;
import org.eclipse.test.internal.performance.results.utils.Util;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class BuildResultsElement extends ResultsElement {

	// Property descriptors
    static final String P_ID_BUILD_DATE = "BuildResultsElement.date"; //$NON-NLS-1$
    static final String P_ID_BUILD_BASELINE = "BuildResultsElement.baseline"; //$NON-NLS-1$
    static final String P_ID_BUILD_COMMENT = "BuildResultsElement.comment"; //$NON-NLS-1$
    static final String P_ID_BUILD_SUMMARY_KIND = "BuildResultsElement.summarykind"; //$NON-NLS-1$
    static final String P_ID_BUILD_IS_BASELINE = "BuildResultsElement.isbaseline"; //$NON-NLS-1$
    static final String P_ID_BUILD_FAILURE = "BuildResultsElement.failure"; //$NON-NLS-1$
    static final String P_ID_BUILD_DELTA = "BuildResultsElement.delta"; //$NON-NLS-1$
    static final String P_ID_BUILD_ERROR = "BuildResultsElement.error"; //$NON-NLS-1$
    static final String P_ID_BUILD_TTEST = "BuildResultsElement.ttest"; //$NON-NLS-1$

    static final String P_STR_BUILD_DATE = "date"; //$NON-NLS-1$
    static final String P_STR_BUILD_COMMENT = "comment"; //$NON-NLS-1$
    static final String P_STR_BUILD_SUMMARY_KIND = "summary kind"; //$NON-NLS-1$
    static final String P_STR_BUILD_IS_BASELINE = "is baseline"; //$NON-NLS-1$
    static final String P_STR_BUILD_BASELINE = "baseline"; //$NON-NLS-1$
    static final String P_STR_BUILD_FAILURE = "failure"; //$NON-NLS-1$
    static final String P_STR_BUILD_DELTA = "delta with baseline"; //$NON-NLS-1$
    static final String P_STR_BUILD_ERROR = "delta error"; //$NON-NLS-1$
    static final String P_STR_BUILD_TTEST = "student's ttest"; //$NON-NLS-1$

	private static final TextPropertyDescriptor BUILD_DATE_DESCRIPTOR = new TextPropertyDescriptor(P_ID_BUILD_DATE, P_STR_BUILD_DATE);
	private static final TextPropertyDescriptor BUILD_COMMENT_DESCRIPTOR = new TextPropertyDescriptor(P_ID_BUILD_COMMENT, P_STR_BUILD_COMMENT);
	private static final TextPropertyDescriptor BUILD_SUMMARY_DESCRIPTOR = new TextPropertyDescriptor(P_ID_BUILD_SUMMARY_KIND, P_STR_BUILD_SUMMARY_KIND);
	private static final PropertyDescriptor BUILD_IS_BASELINE_DESCRIPTOR = new PropertyDescriptor(P_ID_BUILD_IS_BASELINE, P_STR_BUILD_IS_BASELINE);
	private static final PropertyDescriptor BUILD_BASELINE_DESCRIPTOR = new PropertyDescriptor(P_ID_BUILD_BASELINE, P_STR_BUILD_BASELINE);
	private static final TextPropertyDescriptor BUILD_TEST_FAILURE_DESCRIPTOR = new TextPropertyDescriptor(P_ID_BUILD_FAILURE, P_STR_BUILD_FAILURE);
	private static final PropertyDescriptor BUILD_TEST_DELTA_DESCRIPTOR = new PropertyDescriptor(P_ID_BUILD_DELTA, P_STR_BUILD_DELTA);
	private static final PropertyDescriptor BUILD_TEST_ERROR_DESCRIPTOR = new PropertyDescriptor(P_ID_BUILD_ERROR, P_STR_BUILD_ERROR);
	private static final PropertyDescriptor BUILD_STUDENTS_TTEST_DESCRIPTOR = new PropertyDescriptor(P_ID_BUILD_TTEST, P_STR_BUILD_TTEST);

    private static Vector DESCRIPTORS;
    static Vector initDescriptors(int status) {
		DESCRIPTORS = new Vector();
		// Status category
		DESCRIPTORS.add(getInfosDescriptor(status));
		DESCRIPTORS.add(getWarningsDescriptor(status));
		DESCRIPTORS.add(ERROR_DESCRIPTOR);
		ERROR_DESCRIPTOR.setCategory("Status");
		// Results category
        DESCRIPTORS.add(BUILD_DATE_DESCRIPTOR);
		BUILD_DATE_DESCRIPTOR.setCategory("Results");
        DESCRIPTORS.add(BUILD_BASELINE_DESCRIPTOR);
		BUILD_BASELINE_DESCRIPTOR.setCategory("Results");
        DESCRIPTORS.add(BUILD_COMMENT_DESCRIPTOR);
		BUILD_COMMENT_DESCRIPTOR.setCategory("Results");
        DESCRIPTORS.add(BUILD_SUMMARY_DESCRIPTOR);
		BUILD_SUMMARY_DESCRIPTOR.setCategory("Results");
        DESCRIPTORS.add(BUILD_IS_BASELINE_DESCRIPTOR);
		BUILD_IS_BASELINE_DESCRIPTOR.setCategory("Results");
        DESCRIPTORS.add(BUILD_TEST_FAILURE_DESCRIPTOR);
		BUILD_TEST_FAILURE_DESCRIPTOR.setCategory("Results");
        DESCRIPTORS.add(BUILD_TEST_DELTA_DESCRIPTOR);
		BUILD_TEST_DELTA_DESCRIPTOR.setCategory("Results");
        DESCRIPTORS.add(BUILD_TEST_ERROR_DESCRIPTOR);
		BUILD_TEST_ERROR_DESCRIPTOR.setCategory("Results");
        DESCRIPTORS.add(BUILD_STUDENTS_TTEST_DESCRIPTOR);
		BUILD_STUDENTS_TTEST_DESCRIPTOR.setCategory("Results");
		// Survey category
		DESCRIPTORS.add(COMMENT_DESCRIPTOR);
		COMMENT_DESCRIPTOR.setCategory("Survey");
        return DESCRIPTORS;
	}
    static ComboBoxPropertyDescriptor getInfosDescriptor(int status) {
		List list = new ArrayList();
		if ((status & SMALL_VALUE) != 0) {
			list.add("This test and/or its variation has a small value, hence it may not be necessary to spend time on fixing it if a regression occurs");
		}
		if ((status & STUDENT_TTEST) != 0) {
			list.add("The student-t test error on this test is over the threshold");
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
			list.add("The error on this test is over the 3% threshold, hence its result may not be really reliable");
		}
		if ((status & NOT_RELIABLE) != 0) {
			list.add("The results history shows that the variation of its delta is over 20%, hence its result is surely not reliable");
		}
		if ((status & NOT_STABLE) != 0) {
			list.add("The results history shows that the variation of its delta is between 10% and 20%, hence its result may not be really reliable");
		}
		if ((status & NO_BASELINE) != 0) {
			list.add("There's no baseline to compare with");
		}
		if ((status & SINGLE_RUN) != 0) {
			list.add("This test has only one run, hence no error can be computed to verify if it's stable enough to be reliable");
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

	    // Model info
	boolean important;
	boolean milestone;

public BuildResultsElement(AbstractResults results, ResultsElement parent) {
	super(results, parent);
	initInfo();
}

public BuildResultsElement(String name, ResultsElement parent) {
	super(name, parent);
	initInfo();
}

public int compareTo(Object o) {
	if (o instanceof BuildResultsElement && getName() != null) {
		BuildResultsElement element = (BuildResultsElement)o;
		if (element.getName() != null) {
			String buildDate = Util.getBuildDate(element.name);
			return Util.getBuildDate(this.name).compareTo(buildDate);
		}
	}
	return super.compareTo(o);
}

ResultsElement createChild(AbstractResults testResults) {
	return null;
}

BuildResults getBuildResults() {
	return (BuildResults) this.results;
}

public Object[] getChildren(Object o) {
	if (this.results == null) {
		return new Object[0];
	}
	if (this.children == null) {
		initChildren();
	}
	return this.children;
}

public Object getEditableValue() {
	if (this.results == null)  {
		return "Build "+this.name;
	}
	return this.results.toString();
}

/* (non-Javadoc)
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

/* (non-Javadoc)
 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
 */
public Object getPropertyValue(Object propKey) {
	BuildResults buildResults = getBuildResults();
	if (buildResults != null) {
		ConfigResults configResults = (ConfigResults) buildResults.getParent();
		BuildResults baselineResults = configResults.getBaselineBuildResults(buildResults.getName());
	    if (propKey.equals(P_ID_BUILD_DATE))
	        return buildResults.getDate();
	    if (propKey.equals(P_ID_BUILD_COMMENT))
	        return buildResults.getComment();
	    if (propKey.equals(P_ID_BUILD_SUMMARY_KIND)) {
	    	int summaryKind = buildResults.getSummaryKind();
			if (summaryKind == 1) {
	    		return "global";
	    	}
	    	if (summaryKind >= 0) {
	    		return "component";
	    	}
	    	return "none";
	    }
	    if (propKey.equals(P_ID_BUILD_IS_BASELINE))
	        return new Boolean(buildResults.isBaseline());
	    if (propKey.equals(P_ID_BUILD_FAILURE))
	        return buildResults.getFailure();
	    if (baselineResults != null) {
		    if (propKey.equals(P_ID_BUILD_BASELINE)) {
		        return baselineResults.getName();
		    }
			double buildValue = buildResults.getValue();
			double baselineValue = baselineResults.getValue();
			double delta = (baselineValue - buildValue) / baselineValue;
			if (Double.isNaN(delta)) {
			    if (propKey.equals(P_ID_BUILD_DELTA) || propKey.equals(P_ID_BUILD_ERROR)) {
			        return new Double(Double.NaN);
			    }
			} else  if (propKey.equals(P_ID_BUILD_DELTA)) {
				return new Double(delta);
		    } else {
				long baselineCount = baselineResults.getCount();
				long currentCount = buildResults.getCount();
				if (baselineCount > 1 && currentCount > 1) {
					if (propKey.equals(P_ID_BUILD_TTEST)) {
						double ttestValue = Util.computeTTest(baselineResults, buildResults);
						int degreeOfFreedom = (int) (baselineResults.getCount()+buildResults.getCount()-2);
						if (ttestValue >= 0 && StatisticsUtil.getStudentsT(degreeOfFreedom, StatisticsUtil.T90) >= ttestValue) {
							return new Double(ttestValue);
						}
					}
				    if (propKey.equals(P_ID_BUILD_ERROR)) {
						double baselineError = baselineResults.getError();
						double currentError = buildResults.getError();
						double error = Double.isNaN(baselineError)
								? currentError / baselineValue
								: Math.sqrt(baselineError*baselineError + currentError*currentError) / baselineValue;
				        return new Double(error);
				    }
				} else {
				    if (propKey.equals(P_ID_BUILD_ERROR))
				        return new Double(-1);
				}
			}
	    }
	}
	if (propKey.equals(P_ID_STATUS_ERROR)) {
		if ((getStatus() & BIG_DELTA) != 0) {
			return "The delta on this test is over the 10% threshold, hence may indicate a possible regression.";
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
double[] getStatistics() {
	if (this.statistics  == null) {
		this.statistics = ((ConfigResults)getBuildResults().getParent()).getStatistics(Util.BASELINE_BUILD_PREFIXES);
	}
	return this.statistics;
}

void initChildren() {
	BuildResults buildResults = (BuildResults) this.results;
	Dim[] dimensions = buildResults.getDimensions();
	int length = dimensions.length;
	this.children = new DimResultsElement[length];
	for (int i=0; i<length; i++) {
		this.children[i] = new DimResultsElement(this.results, this, dimensions[i]);
	}
}

/*
 * Init information
 */
void initInfo() {
	this.milestone = Util.isMilestone(getName());
	this.important = this.milestone || Util.getNextMilestone(this.name) == null;
}

void initStatus() {
	if (this.results == null) {
		if (this.parent.isInitialized()) {
			if (((PerformanceResultsElement) this.parent).hasRead(this)) {
				this.status = READ;
			} else {
				this.status = UNREAD;
			}
		} else {
			this.status = UNKNOWN;
		}
	} else if (getBuildResults().isBaseline()) {
		// TODO (frederic) report high variation in baseline results along history
		this.status = READ;
	} else {
		initStatus(getBuildResults());
	}
}

/**
 * Returns whether the build is important to be shown.
 * This is the case for milestone builds or for the last builds.
 *
 * @return <code>true</code>  or <code>false</code> .
 */
public boolean isImportant() {
	return this.important;
}

/**
 * Returns whether the build is a milestone one or not.
 *
 * @return <code>true</code>  or <code>false</code> .
 */
public boolean isMilestone() {
	return this.milestone;
}

public boolean isRead() {
	return (getStatus()  & STATE_MASK) == READ;
}

public boolean isUnknown() {
	return (getStatus()  & STATE_MASK) == UNKNOWN;
}

/* (non-Javadoc)
 * @see java.lang.Object#toString()
 */
public String toString() {
	return getName();
}
public boolean isBefore(String build) {
	if (this.results != null) {
		return Util.getBuildDate(this.name).compareTo(Util.getBuildDate(build)) <= 0;
	}
	return true;
}

}
