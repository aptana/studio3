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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;

import com.aptana.buildpath.core.BuildPathCorePlugin;
import com.aptana.core.build.IBuildParticipant;
import com.aptana.core.build.IBuildParticipantManager;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IConfigurationElementProcessor;
import com.aptana.core.util.StringUtil;

public class BuildParticipantManager implements IBuildParticipantManager
{
	/**
	 * Constants for dealing with build participants through the extension point.
	 */
	private static final String CONTENT_TYPE_ID = "contentTypeId"; //$NON-NLS-1$
	private static final String CONTENT_TYPE_BINDING = "contentTypeBinding"; //$NON-NLS-1$
	private static final String EXTENSION_ID = "buildParticipants"; //$NON-NLS-1$
	private static final String ELEMENT_PARTICIPANT = "participant"; //$NON-NLS-1$
	private static final String ATTR_CLASS = "class"; //$NON-NLS-1$
	private static final String ATTR_PRIORITY = "priority"; //$NON-NLS-1$
	private static final int DEFAULT_PRIORITY = 50;

	private Map<IConfigurationElement, Set<IContentType>> buildParticipants;

	public BuildParticipantManager()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.build.IBuildParticipantManager#getAllBuildParticipants()
	 */
	public List<IBuildParticipant> getAllBuildParticipants()
	{
		Set<IBuildParticipant> participants = new HashSet<IBuildParticipant>();
		Map<IConfigurationElement, Set<IContentType>> participantstoContentTypes = getBuildParticipants();
		for (Map.Entry<IConfigurationElement, Set<IContentType>> entry : participantstoContentTypes.entrySet())
		{
			try
			{
				IBuildParticipant participant = createParticipant(entry.getKey());
				if (participant != null)
				{
					participants.add(participant);
				}
			}
			catch (CoreException e)
			{
				IdeLog.logError(BuildPathCorePlugin.getDefault(),
						MessageFormat.format("Unable to generate instance of build participant: {0}", //$NON-NLS-1$
								entry.getKey().getAttribute(ATTR_CLASS)), e);
			}
		}

		List<IBuildParticipant> result = new ArrayList<IBuildParticipant>(participants);
		Collections.sort(result, new Comparator<IBuildParticipant>()
		{
			public int compare(IBuildParticipant arg0, IBuildParticipant arg1)
			{
				// sort higher first
				return arg1.getPriority() - arg0.getPriority();
			}
		});

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.build.IBuildParticipantManager#getBuildParticipants(java.lang.String)
	 */
	public List<IBuildParticipant> getBuildParticipants(String contentTypeId)
	{
		Set<IBuildParticipant> participants = new HashSet<IBuildParticipant>();
		Map<IConfigurationElement, Set<IContentType>> participantstoContentTypes = getBuildParticipants();
		for (Map.Entry<IConfigurationElement, Set<IContentType>> entry : participantstoContentTypes.entrySet())
		{
			if (hasType(contentTypeId, entry.getValue()))
			{
				try
				{
					IBuildParticipant participant = createParticipant(entry.getKey());
					if (participant != null)
					{
						participants.add(participant);
					}
				}
				catch (CoreException e)
				{
					IdeLog.logError(BuildPathCorePlugin.getDefault(),
							MessageFormat.format("Unable to generate instance of build participant: {0}", //$NON-NLS-1$
									entry.getKey().getAttribute(ATTR_CLASS)), e);
				}
			}
		}

		List<IBuildParticipant> result = new ArrayList<IBuildParticipant>(participants);
		Collections.sort(result, new Comparator<IBuildParticipant>()
		{
			public int compare(IBuildParticipant arg0, IBuildParticipant arg1)
			{
				// sort higher first
				return arg1.getPriority() - arg0.getPriority();
			}
		});

		return result;
	}

	private IBuildParticipant createParticipant(IConfigurationElement ice) throws CoreException
	{
		IBuildParticipant participant = (IBuildParticipant) ice.createExecutableExtension(ATTR_CLASS);
		String rawPriority = ice.getAttribute(ATTR_PRIORITY);
		int priority = DEFAULT_PRIORITY;
		if (!StringUtil.isEmpty(rawPriority))
		{
			try
			{
				priority = Integer.parseInt(rawPriority);
			}
			catch (Exception e)
			{
				IdeLog.logWarning(BuildPathCorePlugin.getDefault(), MessageFormat.format(
						"Unable to parse priority value ({0}) as an integer, defaulting to 50.", rawPriority), e); //$NON-NLS-1$
			}
		}
		participant.setPriority(priority);
		return participant;
	}

	/**
	 * Determines if a given content type is associated with the filename.
	 * 
	 * @param filename
	 * @param types
	 * @return
	 */
	private boolean hasType(String contentTypeId, Set<IContentType> types)
	{
		if (types == null || types.isEmpty())
		{
			// FIXME this means if no content type binding is specified then we assume the build participant is valid
			// for all types!
			return true;
		}
		for (IContentType type : types)
		{
			if (type == null)
			{
				continue;
			}
			if (type.getId().equals(contentTypeId))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Return a map from classname of the participant to a set of strings for the content type ids it applies to.
	 * 
	 * @return
	 */
	private synchronized Map<IConfigurationElement, Set<IContentType>> getBuildParticipants()
	{
		if (buildParticipants == null)
		{
			final Map<IConfigurationElement, Set<IContentType>> map = new HashMap<IConfigurationElement, Set<IContentType>>();
			final IContentTypeManager manager = Platform.getContentTypeManager();

			EclipseUtil.processConfigurationElements(BuildPathCorePlugin.PLUGIN_ID, EXTENSION_ID,
					new IConfigurationElementProcessor()
					{

						public void processElement(IConfigurationElement element)
						{
							Set<IContentType> types = new HashSet<IContentType>();

							IConfigurationElement[] contentTypes = element.getChildren(CONTENT_TYPE_BINDING);
							for (IConfigurationElement contentTypeBinding : contentTypes)
							{
								String contentTypeId = contentTypeBinding.getAttribute(CONTENT_TYPE_ID);
								IContentType type = manager.getContentType(contentTypeId);
								types.add(type);
							}
							map.put(element, types);
						}

						public Set<String> getSupportElementNames()
						{
							return CollectionsUtil.newSet(ELEMENT_PARTICIPANT);
						}
					});

			buildParticipants = map;
		}
		return buildParticipants;
	}
}