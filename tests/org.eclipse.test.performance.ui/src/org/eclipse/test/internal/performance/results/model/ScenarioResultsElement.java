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

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.test.internal.performance.results.db.*;
import org.eclipse.test.internal.performance.results.utils.IPerformancesConstants;
import org.eclipse.test.internal.performance.results.utils.Util;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class ScenarioResultsElement extends ResultsElement {

	// Property descriptors
    static final String P_ID_SCENARIO_LABEL = "ScenarioResultsElement.label"; //$NON-NLS-1$
    static final String P_ID_SCENARIO_FILE_NAME = "ScenarioResultsElement.filename"; //$NON-NLS-1$
    static final String P_ID_SCENARIO_SHORT_NAME = "ScenarioResultsElement.shortname"; //$NON-NLS-1$

    static final String P_STR_SCENARIO_LABEL = "label"; //$NON-NLS-1$
    static final String P_STR_SCENARIO_FILE_NAME = "file name"; //$NON-NLS-1$
    static final String P_STR_SCENARIO_SHORT_NAME = "short name"; //$NON-NLS-1$

    private static final TextPropertyDescriptor SCENARIO_LABEL_DESCRIPTOR = new TextPropertyDescriptor(P_ID_SCENARIO_LABEL, P_STR_SCENARIO_LABEL);
	private static final TextPropertyDescriptor SCENARIO_FILE_NAME_DESCRIPTOR = new TextPropertyDescriptor(P_ID_SCENARIO_FILE_NAME, P_STR_SCENARIO_FILE_NAME);
	private static final TextPropertyDescriptor SCENARIO_SHORT_NAME_DESCRIPTOR = new TextPropertyDescriptor(P_ID_SCENARIO_SHORT_NAME, P_STR_SCENARIO_SHORT_NAME);

    private static Vector DESCRIPTORS;
    static Vector initDescriptors(int status) {
        DESCRIPTORS = new Vector();
		// Status category
		DESCRIPTORS.add(getInfosDescriptor(status));
		DESCRIPTORS.add(getWarningsDescriptor(status));
		DESCRIPTORS.add(ERROR_DESCRIPTOR);
		ERROR_DESCRIPTOR.setCategory("Status");
		// Results category
        DESCRIPTORS.addElement(SCENARIO_LABEL_DESCRIPTOR);
		SCENARIO_LABEL_DESCRIPTOR.setCategory("Results");
        DESCRIPTORS.addElement(SCENARIO_FILE_NAME_DESCRIPTOR);
		SCENARIO_FILE_NAME_DESCRIPTOR.setCategory("Results");
        DESCRIPTORS.addElement(SCENARIO_SHORT_NAME_DESCRIPTOR);
		SCENARIO_SHORT_NAME_DESCRIPTOR.setCategory("Results");
		// Survey category
		DESCRIPTORS.add(COMMENT_DESCRIPTOR);
		COMMENT_DESCRIPTOR.setCategory("Survey");
        return DESCRIPTORS;
	}
    static Vector getDescriptors() {
    	return DESCRIPTORS;
	}

ScenarioResultsElement(AbstractResults results, ResultsElement parent) {
    super(results, parent);
}

ResultsElement createChild(AbstractResults testResults) {
	return new ConfigResultsElement(testResults, this);
}

public String getLabel(Object o) {
	return ((ScenarioResults) this.results).getShortName();
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
	ScenarioResults scenarioResults = (ScenarioResults) this.results;
    if (propKey.equals(P_ID_SCENARIO_LABEL))
        return scenarioResults.getLabel();
    if (propKey.equals(P_ID_SCENARIO_FILE_NAME))
        return scenarioResults.getFileName();
    if (propKey.equals(P_ID_SCENARIO_SHORT_NAME))
        return scenarioResults.getShortName();
    return super.getPropertyValue(propKey);
}

/**
 * Returns whether one of the scenario's config has a summary or not.
 *
 * @return <code>true</code> if one of the scenario's config has a summary
 * 	<code>false</code> otherwise.
 */
public boolean hasSummary() {
	if (this.results == null) return false;
	return ((ScenarioResults)this.results).hasSummary();
}

void initStatus() {
	if (onlyFingerprints()) {
		if (hasSummary()) {
			super.initStatus();
		} else {
			this.status = READ;
		}
	} else {
		super.initStatus();
	}
}

StringBuffer getFailures(StringBuffer buffer, int kind, StringBuffer excluded) {
	// Write status for scenarios having error
	if ((getStatus() & ERROR_MASK) != 0) {

		// Get children status
		StringBuffer childrenBuffer = super.getFailures(new StringBuffer(), kind, excluded);

		// Write status on file if not excluded
		if (childrenBuffer.length() > 0) {
			buffer.append("	");
			buffer.append(getLabel(null));
			IEclipsePreferences preferences = new InstanceScope().getNode(IPerformancesConstants.PLUGIN_ID);
			String comment = preferences.get(getId(), null);
			if (comment != null) {
				if ((kind & IPerformancesConstants.STATUS_VALUES) != 0) {
					buffer.append("											");
				} else {
					buffer.append("		");
				}
				buffer.append(comment);
			}
			buffer.append(Util.LINE_SEPARATOR);
			buffer.append(childrenBuffer);
			buffer.append(Util.LINE_SEPARATOR);
		}
	}
	return buffer;
}

}
