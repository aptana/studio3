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

import java.util.Vector;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class BuildResultsProperties {

	    // Property descriptors
    static final String P_ID_SMALL_VALUE = "BuildResultsProperties.small_value"; //$NON-NLS-1$
    static final String P_ID_NO_BASELINE = "BuildResultsProperties.no_baseline"; //$NON-NLS-1$
    static final String P_ID_SINGLE_RUN = "BuildResultsProperties.single_run"; //$NON-NLS-1$
    static final String P_ID_BIG_ERROR = "BuildResultsProperties.big_error"; //$NON-NLS-1$
    static final String P_ID_STUDENT_TTEST = "BuildResultsProperties.ttest"; //$NON-NLS-1$
    static final String P_ID_NOT_STABLE = "BuildResultsProperties.not_stable"; //$NON-NLS-1$
    static final String P_ID_NOT_RELIABLE = "BuildResultsProperties.not_reliable"; //$NON-NLS-1$
    static final String P_ID_BIG_DELTA = "BuildResultsProperties.big_delta"; //$NON-NLS-1$
    static final String P_STR_SMALL_VALUE = "small value"; //$NON-NLS-1$
    static final String P_STR_NO_BASELINE = "no baseline"; //$NON-NLS-1$
    static final String P_STR_SINGLE_RUN = "single run"; //$NON-NLS-1$
    static final String P_STR_BIG_ERROR = "big error"; //$NON-NLS-1$
    static final String P_STR_STUDENT_TTEST = "student ttest"; //$NON-NLS-1$
    static final String P_STR_NOT_STABLE = "not stable"; //$NON-NLS-1$
    static final String P_STR_NOT_RELIABLE = "not reliable"; //$NON-NLS-1$
    static final String P_STR_BIG_DELTA = "delta error"; //$NON-NLS-1$
    private static Vector descriptors;
    static {
        descriptors = new Vector();
        descriptors.addElement(new TextPropertyDescriptor(P_ID_SMALL_VALUE, P_STR_SMALL_VALUE));
        descriptors.addElement(new TextPropertyDescriptor(P_ID_NO_BASELINE, P_STR_NO_BASELINE));
        descriptors.addElement(new TextPropertyDescriptor(P_ID_SINGLE_RUN, P_STR_SINGLE_RUN));
        descriptors.addElement(new TextPropertyDescriptor(P_ID_BIG_ERROR, P_STR_BIG_ERROR));
        descriptors.addElement(new TextPropertyDescriptor(P_ID_STUDENT_TTEST, P_STR_STUDENT_TTEST));
        descriptors.addElement(new TextPropertyDescriptor(P_ID_NOT_STABLE, P_STR_NOT_STABLE));
        descriptors.addElement(new TextPropertyDescriptor(P_ID_NOT_RELIABLE, P_STR_NOT_RELIABLE));
        descriptors.addElement(new TextPropertyDescriptor(P_ID_BIG_DELTA, P_STR_BIG_DELTA));
    }
    static Vector getDescriptors() {
        return descriptors;
	}

    private int status;

public BuildResultsProperties() {
}

void setStatus(int status) {
	this.status = status;
}

public Object getEditableValue() {
	return null;
}

/* (non-Javadoc)
 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
 */
public IPropertyDescriptor[] getPropertyDescriptors() {
    return (IPropertyDescriptor[]) getDescriptors().toArray(
            new IPropertyDescriptor[getDescriptors().size()]);
}

/* (non-Javadoc)
 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
 */
public Object getPropertyValue(Object propKey) {
	if (propKey.equals(P_ID_SMALL_VALUE)) {
		if ((this.status & ResultsElement.SMALL_VALUE) != 0) {
			return "This test and/or its variation has a small value, hence it may not be necessary to spend time on fixing it if a regression occurs.";
		}
	}
	if (propKey.equals(P_ID_NO_BASELINE)) {
		if ((this.status & ResultsElement.NO_BASELINE) != 0) {
			return "There's no baseline to compare with.";
		}
	}
	if (propKey.equals(P_ID_SINGLE_RUN)) {
		if ((this.status & ResultsElement.SINGLE_RUN) != 0) {
			return "This test has only one run, hence no error can be computed to verify if it's stable enough to be reliable.";
		}
	}
	if (propKey.equals(P_ID_BIG_ERROR)) {
		if ((this.status & ResultsElement.BIG_ERROR) != 0) {
			return "The error on this test is over the 3% threshold, hence its result may not be really reliable.";
		}
	}
	if (propKey.equals(P_ID_STUDENT_TTEST)) {
		if ((this.status & ResultsElement.STUDENT_TTEST) != 0) {
			return "The student-t on this test is over the threshold";
		}
	}
	if (propKey.equals(P_ID_NOT_STABLE)) {
		if ((this.status & ResultsElement.NOT_STABLE) != 0) {
			return "The results history shows that the variation of its delta is between 10% and 20%, hence its result may not be really reliable.";
		}
	}
	if (propKey.equals(P_ID_NOT_RELIABLE)) {
		if ((this.status & ResultsElement.NOT_RELIABLE) != 0) {
			return "The results history shows that the variation of its delta is over 20%, hence its result is surely not reliable.";
		}
	}
	if (propKey.equals(P_ID_BIG_DELTA)) {
		if ((this.status & ResultsElement.BIG_DELTA) != 0) {
			return "The delta on this test is over the 10% threshold, hence may indicate a possible regression.";
		}
	}
	return null;
}

}
