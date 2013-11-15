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

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.test.internal.performance.eval.StatisticsUtil;
import org.eclipse.test.internal.performance.results.db.AbstractResults;
import org.eclipse.test.internal.performance.results.db.BuildResults;
import org.eclipse.test.internal.performance.results.db.ConfigResults;
import org.eclipse.test.internal.performance.results.db.DB_Results;
import org.eclipse.test.internal.performance.results.utils.IPerformancesConstants;
import org.eclipse.test.internal.performance.results.utils.Util;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.osgi.service.prefs.BackingStoreException;

/**
 * An Organization Element
 */
public abstract class ResultsElement implements IAdaptable, IPropertySource, IWorkbenchAdapter, Comparable {

	// Image descriptors
	private static final ISharedImages WORKBENCH_SHARED_IMAGES = PlatformUI.getWorkbench().getSharedImages();
	public static final Image ERROR_IMAGE = WORKBENCH_SHARED_IMAGES.getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
	public static final ImageDescriptor ERROR_IMAGE_DESCRIPTOR = WORKBENCH_SHARED_IMAGES.getImageDescriptor(ISharedImages.IMG_OBJS_ERROR_TSK);
	public static final Image WARN_IMAGE = WORKBENCH_SHARED_IMAGES.getImage(ISharedImages.IMG_OBJS_WARN_TSK);
	public static final ImageDescriptor WARN_IMAGE_DESCRIPTOR = WORKBENCH_SHARED_IMAGES.getImageDescriptor(ISharedImages.IMG_OBJS_WARN_TSK);
	public static final Image INFO_IMAGE = WORKBENCH_SHARED_IMAGES.getImage(ISharedImages.IMG_OBJS_INFO_TSK);
	public static final ImageDescriptor INFO_IMAGE_DESCRIPTOR = WORKBENCH_SHARED_IMAGES.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK);
	public static final Image HELP_IMAGE = WORKBENCH_SHARED_IMAGES.getImage(ISharedImages.IMG_LCL_LINKTO_HELP);
	public static final ImageDescriptor HELP_IMAGE_DESCRIPTOR = WORKBENCH_SHARED_IMAGES.getImageDescriptor(ISharedImages.IMG_LCL_LINKTO_HELP);
	public static final ImageDescriptor FOLDER_IMAGE_DESCRIPTOR = WORKBENCH_SHARED_IMAGES.getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER);
	public static final ImageDescriptor CONNECT_IMAGE_DESCRIPTOR = WORKBENCH_SHARED_IMAGES.getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED);

	// Model
    ResultsElement parent;
	AbstractResults results;
	ResultsElement[] children;
	String name;
	int status = -1;

	// Stats
    double[] statistics;

	// Status constants
	// state
	static final int UNKNOWN = 0x01;
	static final int UNREAD = 0x02;
	static final int READ = 0x04;
	static final int MISSING = 0x08;
	public static final int STATE_MASK = 0x0F;
	// info
	static final int SMALL_VALUE = 0x0010;
	static final int STUDENT_TTEST = 0x0020;
	public static final int INFO_MASK = 0x0030;
	// warning
	static final int NO_BASELINE = 0x0040;
	static final int SINGLE_RUN = 0x0080;
	static final int BIG_ERROR = 0x0100;
	static final int NOT_STABLE = 0x0200;
	static final int NOT_RELIABLE = 0x0400;
	public static final int WARNING_MASK = 0x0FC0;
	// error
	static final int BIG_DELTA = 0x1000;
	public static final int ERROR_MASK = 0xF000;

	// Property descriptors
	static final String P_ID_STATUS_INFO = "ResultsElement.status_info"; //$NON-NLS-1$
	static final String P_ID_STATUS_WARNING = "ResultsElement.status_warning"; //$NON-NLS-1$
	static final String P_ID_STATUS_ERROR = "ResultsElement.status_error"; //$NON-NLS-1$
	static final String P_ID_STATUS_COMMENT = "ResultsElement.status_comment"; //$NON-NLS-1$

	static final String P_STR_STATUS_INFO = " info"; //$NON-NLS-1$
	static final String P_STR_STATUS_WARNING = "warning"; //$NON-NLS-1$
	static final String P_STR_STATUS_ERROR = "error"; //$NON-NLS-1$
	static final String P_STR_STATUS_COMMENT = "comment"; //$NON-NLS-1$
	static final String[] NO_VALUES = new String[0];

	private static Vector DESCRIPTORS;
	static final TextPropertyDescriptor COMMENT_DESCRIPTOR = new TextPropertyDescriptor(P_ID_STATUS_COMMENT, P_STR_STATUS_COMMENT);
	static final TextPropertyDescriptor ERROR_DESCRIPTOR = new TextPropertyDescriptor(P_ID_STATUS_ERROR, P_STR_STATUS_ERROR);
    static Vector initDescriptors(int status) {
		DESCRIPTORS = new Vector();
		// Status category
		DESCRIPTORS.add(getInfosDescriptor(status));
		DESCRIPTORS.add(getWarningsDescriptor(status));
		DESCRIPTORS.add(ERROR_DESCRIPTOR);
		ERROR_DESCRIPTOR.setCategory("Status");
		// Survey category
		DESCRIPTORS.add(COMMENT_DESCRIPTOR);
		COMMENT_DESCRIPTOR.setCategory("Survey");
		return DESCRIPTORS;
	}
    static Vector getDescriptors() {
    	return DESCRIPTORS;
	}
    static ComboBoxPropertyDescriptor getInfosDescriptor(int status) {
		List list = new ArrayList();
		if ((status & SMALL_VALUE) != 0) {
			list.add("Some builds have tests with small values");
		}
		if ((status & STUDENT_TTEST) != 0) {
			list.add("Some builds have student-t test error over the threshold");
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
			list.add("Some builds have tests with error over 3%");
		}
		if ((status & NOT_RELIABLE) != 0) {
			list.add("Some builds have no reliable tests");
		}
		if ((status & NOT_STABLE) != 0) {
			list.add("Some builds have no stable tests");
		}
		if ((status & NO_BASELINE) != 0) {
			list.add("Some builds have no baseline to compare with");
		}
		if ((status & SINGLE_RUN) != 0) {
			list.add("Some builds have single run tests");
		}
		String[] warnings = new String[list.size()];
		if (list.size() > 0) {
			list.toArray(warnings);
		}
		ComboBoxPropertyDescriptor warningDescriptor = new ComboBoxPropertyDescriptor(P_ID_STATUS_WARNING, P_STR_STATUS_WARNING, warnings);
		warningDescriptor.setCategory("Status");
		return warningDescriptor;
	}

ResultsElement() {
}

ResultsElement(AbstractResults results, ResultsElement parent) {
    this.parent = parent;
    this.results = results;
}

ResultsElement(String name, ResultsElement parent) {
	this.parent = parent;
	this.name = name;
}

public int compareTo(Object o) {
	if (this.results == null) {
		if (o instanceof ResultsElement && this.name != null) {
			ResultsElement element = (ResultsElement) o;
			return this.name.compareTo(element.getName());
		}
		return -1;
	}
	if (o instanceof ResultsElement) {
		return this.results.compareTo(((ResultsElement)o).results);
	}
	return -1;
}

abstract ResultsElement createChild(AbstractResults testResults);

/* (non-Javadoc)
 * Method declared on IAdaptable
 */
public Object getAdapter(Class adapter) {
    if (adapter == IPropertySource.class) {
        return this;
    }
    if (adapter == IWorkbenchAdapter.class) {
        return this;
    }
    return null;
}

/**
 * Iterate the element children.
 */
public ResultsElement[] getChildren() {
	if (this.results == null) {
		return new ResultsElement[0];
	}
	if (this.children == null) {
		initChildren();
	}
    return this.children;
}

/* (non-Javadoc)
 * Method declared on IWorkbenchAdapter
 */
public Object[] getChildren(Object o) {
	if (this.results == null) {
		return new Object[0];
	}
	if (this.children == null) {
		initChildren();
	}
    return this.children;
}

/* (non-Javadoc)
 * Method declared on IPropertySource
 */
public Object getEditableValue() {
    return this;
}

final String getId() {
	return getId(new StringBuffer()).toString();
}

private StringBuffer getId(StringBuffer buffer) {
	if (this.parent != null) {
		return this.parent.getId(buffer).append('/').append(getName());
	}
	return buffer.append(DB_Results.getDbName());
}

/* (non-Javadoc)
 * Method declared on IWorkbenchAdapter
 */
public ImageDescriptor getImageDescriptor(Object object) {
	if (object instanceof ResultsElement) {
		ResultsElement resultsElement = (ResultsElement) object;
// DEBUG
//		if (resultsElement.getName().equals("I20090806-0100")) {
//			if (resultsElement.results != null) {
//				String toString = resultsElement.results.getParent().toString();
//				String toString = resultsElement.results.toString();
//				if (toString.indexOf("testStoreExists")>0 && toString.indexOf("eplnx2")>0) {
//					System.out.println("stop");
//				}
//			}
//		}
		int elementStatus = resultsElement.getStatus();
		if (elementStatus == MISSING) {
			return HELP_IMAGE_DESCRIPTOR;
		}
		if ((elementStatus & ResultsElement.ERROR_MASK) != 0) {
			return ERROR_IMAGE_DESCRIPTOR;
		}
		if ((elementStatus & ResultsElement.WARNING_MASK) != 0) {
			return WARN_IMAGE_DESCRIPTOR;
		}
		if ((elementStatus & ResultsElement.INFO_MASK) != 0) {
			return INFO_IMAGE_DESCRIPTOR;
		}
	}
	return null;
}

/* (non-Javadoc)
 * Method declared on IWorkbenchAdapter
 */
public String getLabel(Object o) {
    return getName();
}

/**
 * Returns the name
 */
public String getName() {
	if (this.name == null && this.results != null) {
		this.name = this.results.getName();
	}
	return this.name;
}

/**
 * Returns the parent
 */
public Object getParent(Object o) {
    return this.parent;
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
	if (propKey.equals(P_ID_STATUS_INFO)) {
		if ((getStatus() & INFO_MASK) != 0) {
			return new Integer(0);
		}
	}
	if (propKey.equals(P_ID_STATUS_WARNING)) {
		if ((getStatus() & WARNING_MASK) != 0) {
			return new Integer(0);
		}
	}
	if (propKey.equals(P_ID_STATUS_ERROR)) {
		if ((getStatus() & BIG_DELTA) != 0) {
			return "Some builds have tests with regression";
		}
	}
	if (propKey.equals(P_ID_STATUS_COMMENT)) {
		IEclipsePreferences preferences = new InstanceScope().getNode(IPerformancesConstants.PLUGIN_ID);
		return preferences.get(getId(), "");
	}
	return null;
}

public ResultsElement getResultsElement(String resultName) {
	int length = getChildren(null).length;
	for (int i=0; i<length; i++) {
		ResultsElement searchedResults = this.children[i];
		if (searchedResults.getName().equals(resultName)) {
			return searchedResults;
		}
	}
	return null;
}

/**
 * Return the status of the element.
 *
 * The status is a bit mask pattern where digits are
 * allowed as follow:
 *	<ul>
 * 		<li>0-3: bits for state showing whether the element is
 * 			<ul>
 * 				<li>{@link #UNKNOWN} : not connected to a db</li>
 * 				<li>{@link #UNREAD} : is not valid (e.g. NaN results)</li>
 * 				<li>{@link #MISSING} : no results (e.g. the perf machine crashed and didn't store any results)</li>
 * 				<li>{@link #READ} : has valid results</li>
 * 			</ul>
 * 		</li>
 * 		<li>4-5: bits for information. Current possible information are
 * 			<ul>
 * 				<li>{@link #SMALL_VALUE} : build results or delta with baseline value is under 100ms</li>
 * 				<li>{@link #STUDENT_TTEST} : the Student T-test is over the threshold (old yellow color for test results)</li>
 * 			</ul>
 * 		</li>
 * 		<li>6-11: bits for warnings. Current possible warnings are
 * 			<ul>
 * 				<li>{@link #NO_BASELINE} : no baseline for the current build</li>
 * 				<li>{@link #SINGLE_RUN} : the test has only one run (i.e. no error could be computed), hence its reliability cannot be evaluated</li>
 * 				<li>{@link #BIG_ERROR} : the test result is over the 3% threshold</li>
 * 				<li>{@link #NOT_STABLE} : the test history shows a deviation between 10% and 20% (may mean that this test is not so reliable)</li>
 * 				<li>{@link #NOT_RELIABLE} : the test history shows a deviation over 20% (surely means that this test is too erratic to be reliable)</li>
 * 			</ul>
 * 		</li>
 * 		<li>12-15: bits for errors. Current possible errors are
 * 			<ul>
 * 				<li>{@link #BIG_DELTA} : the delta for the test is over the 10% threshold</li>
 * 			</ul>
 * 		</li>
 *	</ul>
 *
 * Note that these explanation applied to {@link BuildResultsElement}, and {@link DimResultsElement}.
 * For {@link ComponentResultsElement}, and {@link ScenarioResultsElement}, it's the merge of all the children status
 * and means "Some tests have..." instead of "The test has...". For {@link ConfigResultsElement}, it means the status
 * of the most recent build compared to its most recent baseline.
 *
 * @return An int with each bit set when the corresponding symptom applies.
 */
public final int getStatus() {
	if (this.status < 0) {
		initStatus();
	}
	return this.status;
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
	return this.statistics;
}

/**
 * Returns whether the element (or one in its hierarchy) has an error.
 *
 * @return <code> true</code> if the element or one in its hierarchy has an error,
 * 	<code> false</code>  otherwise
 */
public final boolean hasError() {
	return (getStatus() & ERROR_MASK) != 0;
}

void initChildren() {
	AbstractResults[] resultsChildren = this.results.getChildren();
	int length = resultsChildren.length;
	this.children = new ResultsElement[length];
	int count = 0;
	for (int i=0; i<length; i++) {
		ResultsElement childElement = createChild(resultsChildren[i]);
		if (childElement != null) {
			this.children[count++] = childElement;
		}
	}
	if (count < length) {
		System.arraycopy(this.children, 0, this.children = new ResultsElement[count], 0, count);
	}
}
void initStatus() {
	this.status = READ;
	if (this.results != null) {
		if (this.children == null) initChildren();
		int length = this.children.length;
		for (int i=0; i<length; i++) {
			this.status |= this.children[i].getStatus();
		}
	}
}

int initStatus(BuildResults buildResults) {
	this.status = READ;

	// Get values
	double buildValue = buildResults.getValue();
	ConfigResults configResults = (ConfigResults) buildResults.getParent();
	BuildResults baselineResults = configResults.getBaselineBuildResults(buildResults.getName());
	double baselineValue = baselineResults.getValue();
	double delta = (baselineValue - buildValue) / baselineValue;

	// Store if there's no baseline
	if (Double.isNaN(delta)) {
		this.status |= NO_BASELINE;
	}

	// Store if there's only one run
	long baselineCount = baselineResults.getCount();
	long currentCount = buildResults.getCount();
	double error = Double.NaN;
	if (baselineCount == 1 || currentCount == 1) {
		this.status |= SINGLE_RUN;
	}

	// Store if the T-test is not good
	double ttestValue = Util.computeTTest(baselineResults, buildResults);
	int degreeOfFreedom = (int) (baselineResults.getCount()+buildResults.getCount()-2);
	if (ttestValue >= 0 && StatisticsUtil.getStudentsT(degreeOfFreedom, StatisticsUtil.T90) >= ttestValue) {
		this.status |= STUDENT_TTEST;
	}

	// Store if there's a big error (over 3%)
	double baselineError = baselineResults.getError();
	double currentError = buildResults.getError();
	error = Double.isNaN(baselineError)
			? currentError / baselineValue
			: Math.sqrt(baselineError*baselineError + currentError*currentError) / baselineValue;
	if (error > 0.03) {
		this.status |= BIG_ERROR;
	}

	// Store if there's a big delta (over 10%)
	if (delta <= -0.1) {
		this.status |= BIG_DELTA;
		double currentBuildValue = buildResults.getValue();
		double diff = Math.abs(baselineValue - currentBuildValue);
		if (currentBuildValue < 100 || diff < 100) { // moderate the status when
			// diff is less than 100ms
			this.status |= SMALL_VALUE;
		} else {
			double[] stats = getStatistics();
			if (stats != null) {
				if (stats[3] > 0.2) { // invalidate the status when the test
					// historical deviation is over 20%
					this.status |= NOT_RELIABLE;
				} else if (stats[3] > 0.1) { // moderate the status when the test
					// historical deviation is between 10%
					// and 20%
					this.status |= NOT_STABLE;
				}
			}
		}
	}

	return this.status;
}

public boolean isInitialized() {
	return this.results != null;
}

/* (non-Javadoc)
 * Method declared on IPropertySource
 */
public boolean isPropertySet(Object property) {
    return false;
}

boolean onlyFingerprints() {
	if (this.parent != null) {
		return this.parent.onlyFingerprints();
	}
	return ((PerformanceResultsElement)this).fingerprints;
}

/* (non-Javadoc)
 * Method declared on IPropertySource
 */
public void resetPropertyValue(Object property) {
}

void resetStatus() {
	this.status = -1;
	if (this.results != null) {
		if (this.children == null) initChildren();
		int length = this.children.length;
		for (int i=0; i<length; i++) {
			this.children[i].resetStatus();
		}
	}
}

public void setPropertyValue(Object name, Object value) {
	if (name.equals(P_ID_STATUS_COMMENT)) {
		IEclipsePreferences preferences = new InstanceScope().getNode(IPerformancesConstants.PLUGIN_ID);
		preferences.put(getId(), (String) value);
		try {
			preferences.flush();
		} catch (BackingStoreException e) {
			// skip
		}
	}
}

/**
 * Sets the image descriptor
 */
void setImageDescriptor(ImageDescriptor desc) {
//    this.imageDescriptor = desc;
}

public String toString() {
	if (this.results == null) {
		return getName();
	}
	return this.results.toString();
}

/*
 * Write the failures of the element in the given buffer
 */
StringBuffer getFailures(StringBuffer buffer, int kind, StringBuffer excluded) {
	int length = getChildren().length;
	for (int i=0; i<length; i++) {
		this.children[i].getFailures(buffer, kind, excluded);
	}
	return buffer;
}


}
