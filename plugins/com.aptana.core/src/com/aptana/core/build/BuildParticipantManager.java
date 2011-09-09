/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.build;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.aptana.core.CorePlugin;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IConfigurationElementProcessor;

public class BuildParticipantManager
{

	private static final String EXTENSION_ID = "buildParticipants"; //$NON-NLS-1$
	private static final String ELEMENT_PARTICIPANT = "participant"; //$NON-NLS-1$
	private static final String ATTR_CLASS = "class"; //$NON-NLS-1$

	private static BuildParticipantManager instance;

	private List<IBuildParticipant> buildParticipants;

	public synchronized static BuildParticipantManager getInstance()
	{
		if (instance == null)
		{
			instance = new BuildParticipantManager();
		}
		return instance;
	}

	public List<IBuildParticipant> getBuildParticipants()
	{
		return Collections.unmodifiableList(buildParticipants);
	}

	private BuildParticipantManager()
	{
		buildParticipants = new ArrayList<IBuildParticipant>();
		loadExtension();
	}

	private void loadExtension()
	{
		EclipseUtil.processConfigurationElements(CorePlugin.PLUGIN_ID, EXTENSION_ID,
				new IConfigurationElementProcessor()
				{

					public void processElement(IConfigurationElement element)
					{
						if (ELEMENT_PARTICIPANT.equals(element.getName()))
						{
							try
							{
								Object participantClass = element.createExecutableExtension(ATTR_CLASS);
								if (participantClass instanceof IBuildParticipant)
								{
									buildParticipants.add((IBuildParticipant) participantClass);
								}
								else
								{
									IdeLog.logError(CorePlugin.getDefault(),
											"The build participant does not subclass IBuildParticipant"); //$NON-NLS-1$
								}
							}
							catch (CoreException e)
							{
								IdeLog.logError(CorePlugin.getDefault(), "Error loading build participant", e); //$NON-NLS-1$
							}
						}
					}
				}, ELEMENT_PARTICIPANT);
	}
}
