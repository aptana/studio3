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

import org.eclipse.test.internal.performance.data.Dim;
import org.eclipse.test.internal.performance.results.db.*;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class DimResultsElement extends ResultsElement {

	Dim dim;

	// Property descriptors
    static final String P_ID_DIMENSION = "DimResultsElement.dim"; //$NON-NLS-1$
    static final String P_ID_COUNT = "DimResultsElement.count"; //$NON-NLS-1$
    static final String P_ID_AVERAGE = "DimResultsElement.average"; //$NON-NLS-1$
    static final String P_ID_STDDEV = "DimResultsElement.stddev"; //$NON-NLS-1$
    static final String P_ID_ERROR= "DimResultsElement.error"; //$NON-NLS-1$
    static final String P_ID_HAD_VALUES = "DimResultsElement.hadvalues"; //$NON-NLS-1$

    static final String P_STR_DIMENSION = "dimension"; //$NON-NLS-1$
    static final String P_STR_COUNT= "count"; //$NON-NLS-1$
    static final String P_STR_AVERAGE = "average"; //$NON-NLS-1$
    static final String P_STR_STDDEV = "std dev"; //$NON-NLS-1$
    static final String P_STR_ERROR = "error"; //$NON-NLS-1$
    static final String P_STR_HAD_VALUES = "had values"; //$NON-NLS-1$

	private static final TextPropertyDescriptor DIMENSION_DESCRIPTOR = new TextPropertyDescriptor(P_ID_DIMENSION, P_STR_DIMENSION);
	private static final PropertyDescriptor DIM_COUNT_DESCRIPTOR = new PropertyDescriptor(P_ID_COUNT, P_STR_COUNT);
	private static final PropertyDescriptor DIM_AVERAGE_DESCRIPTOR = new PropertyDescriptor(P_ID_AVERAGE, P_STR_AVERAGE);
	private static final PropertyDescriptor DIM_STDDEV_DESCRIPTOR = new PropertyDescriptor(P_ID_STDDEV, P_STR_STDDEV);
	private static final PropertyDescriptor DIM_ERROR_DESCRIPTOR = new PropertyDescriptor(P_ID_ERROR, P_STR_ERROR);
	private static final PropertyDescriptor DIM_HAD_VALUES_DESCRIPTOR = new PropertyDescriptor(P_ID_HAD_VALUES, P_STR_HAD_VALUES);

    private static Vector DESCRIPTORS;
    static Vector initDescriptors(int status) {
        DESCRIPTORS = new Vector();
		// Status category
		DESCRIPTORS.add(getInfosDescriptor(status));
		DESCRIPTORS.add(getWarningsDescriptor(status));
		DESCRIPTORS.add(ERROR_DESCRIPTOR);
		ERROR_DESCRIPTOR.setCategory("Status");
		// Results category
        DESCRIPTORS.addElement(DIMENSION_DESCRIPTOR);
		DIMENSION_DESCRIPTOR.setCategory("Results");
        DESCRIPTORS.addElement(DIM_COUNT_DESCRIPTOR);
		DIM_COUNT_DESCRIPTOR.setCategory("Results");
        DESCRIPTORS.addElement(DIM_AVERAGE_DESCRIPTOR);
		DIM_AVERAGE_DESCRIPTOR.setCategory("Results");
        DESCRIPTORS.addElement(DIM_STDDEV_DESCRIPTOR);
		DIM_STDDEV_DESCRIPTOR.setCategory("Results");
        DESCRIPTORS.addElement(DIM_ERROR_DESCRIPTOR);
		DIM_ERROR_DESCRIPTOR.setCategory("Results");
        DESCRIPTORS.addElement(DIM_HAD_VALUES_DESCRIPTOR);
		DIM_HAD_VALUES_DESCRIPTOR.setCategory("Results");
		// Survey category
		DESCRIPTORS.add(COMMENT_DESCRIPTOR);
		COMMENT_DESCRIPTOR.setCategory("Survey");
        return DESCRIPTORS;
   	}
    static Vector getDescriptors() {
    	return DESCRIPTORS;
	}

public DimResultsElement(AbstractResults results, ResultsElement parent, Dim d) {
	super(results, parent);
	this.dim = d;
}

ResultsElement createChild(AbstractResults testResults) {
	return null;
}

private BuildResults getBuildResults() {
	return (BuildResults) this.results;
}

public String getLabel(Object o) {
	return this.dim.getName();
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
    if (propKey.equals(P_ID_DIMENSION)) {
    	return  this.dim.getDescription();
    }
    if (propKey.equals(P_ID_COUNT)) {
	    long count = buildResults.getCount(this.dim.getId());
	    return new Double(count);
    }
    if (propKey.equals(P_ID_AVERAGE))
        return new Double(buildResults.getValue(this.dim.getId()));
    if (propKey.equals(P_ID_STDDEV))
        return new Double(buildResults.getDeviation(this.dim.getId()));
    if (propKey.equals(P_ID_ERROR))
        return new Double(buildResults.getError(this.dim.getId()));
    if (propKey.equals(P_ID_HAD_VALUES))
        return new Boolean(buildResults.hadValues());
    return super.getPropertyValue(propKey);
}

}
