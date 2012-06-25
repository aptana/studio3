/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.dispatch.actionControllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.ui.IPerspectiveDescriptor;

import com.aptana.configurations.processor.ConfigurationStatus;
import com.aptana.core.util.ArrayUtil;
import com.aptana.jetty.util.epl.ajax.JSON;
import com.aptana.ui.util.UIUtils;

/**
 * A action controller for workbench related actions.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class WorkbenchActionController extends AbstractActionController
{
	private static final String ID = "id"; //$NON-NLS-1$
	private static final String DESCRIPTION = "description"; //$NON-NLS-1$
	private static final String LABEL = "label"; //$NON-NLS-1$

	// ############## Actions ###############
	/**
	 * Returns a JSON map representation for the active perspective.
	 * 
	 * <pre>
	 *   <b>Sample JS code:</b>
	 *   <code>result = dispatch($H({controller:'portal.workbench', action:"getActivePerspective"}).toJSON());</code>
	 * </pre>
	 * 
	 * @return A JSON map with <code>label</code>, <code>description</code> and <code>id</code> key-value pairs.
	 */
	@ControllerAction
	public Object getActivePerspective()
	{
		IPerspectiveDescriptor descriptor = UIUtils.getActivePerspectiveDescriptor();
		Map<String, String> result = new HashMap<String, String>();
		if (descriptor == null)
		{
			return JSON.toString(ArrayUtil.NO_STRINGS);
		}
		result.put(LABEL, descriptor.getLabel());
		result.put(DESCRIPTION, descriptor.getDescription());
		result.put(ID, descriptor.getId());
		return JSON.toString(result);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.configurations.processor.IConfigurationProcessorListener#configurationStateChanged(com.aptana.
	 * configurations.processor.ConfigurationStatus, java.util.Set)
	 */
	public void configurationStateChanged(ConfigurationStatus status, Set<String> attributesChanged)
	{
		// Nothing to do here
	}
}
