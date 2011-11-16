/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.internal.build;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.aptana.core.CorePlugin;
import com.aptana.core.build.IBuildParticipant;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IConfigurationElementProcessor;

public class BuildParticipantManager
{

	private static final String EXTENSION_ID = "buildParticipants"; //$NON-NLS-1$
	private static final String ELEMENT_PARTICIPANT = "participant"; //$NON-NLS-1$
	private static final String ATTR_CLASS = "class"; //$NON-NLS-1$
	private static final String ATTR_PRIORITY = "priority"; //$NON-NLS-1$
	private static final int DEFAULT_PRIORITY = 50;

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
						try
						{
							Object participantClass = element.createExecutableExtension(ATTR_CLASS);
							if (participantClass instanceof IBuildParticipant)
							{
								IBuildParticipant participant = (IBuildParticipant) participantClass;
								buildParticipants.add(participant);

								String priorityStr = element.getAttribute(ATTR_PRIORITY);
								int priority = DEFAULT_PRIORITY;
								if (priorityStr != null)
								{
									try
									{
										priority = Integer.parseInt(priorityStr);
									}
									catch (NumberFormatException e)
									{
										IdeLog.logError(CorePlugin.getDefault(), e);
									}
								}
								participant.setPriority(priority);
							}
							else
							{
								String pluginId = element.getDeclaringExtension().getNamespaceIdentifier();
								String message = MessageFormat
										.format("Build participant type ''{0}'' is not an instance of IBuildParticipant. This error occurred when processing the ''{1}'' element in the ''{2}'' extension in plugin ''{3}''", //$NON-NLS-1$
												participantClass.getClass().getName(), ELEMENT_PARTICIPANT,
												EXTENSION_ID, pluginId);
								IdeLog.logError(CorePlugin.getDefault(), message);
							}
						}
						catch (CoreException e)
						{
							IdeLog.logError(CorePlugin.getDefault(), "Error loading build participant", e); //$NON-NLS-1$
						}
					}

					public Set<String> getSupportElementNames()
					{
						return CollectionsUtil.newSet(ELEMENT_PARTICIPANT);
					}
				});
		Collections.sort(buildParticipants, new Comparator<IBuildParticipant>()
		{

			public int compare(IBuildParticipant arg0, IBuildParticipant arg1)
			{
				return arg0.getPriority() - arg1.getPriority();
			}
		});
	}
}
