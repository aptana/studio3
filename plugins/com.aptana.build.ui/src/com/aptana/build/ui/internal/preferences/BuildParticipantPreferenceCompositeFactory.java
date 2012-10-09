/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.build.ui.internal.preferences;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Composite;

import com.aptana.build.ui.BuildUIPlugin;
import com.aptana.build.ui.preferences.IBuildParticipantPreferenceCompositeFactory;
import com.aptana.build.ui.preferences.ValidatorFiltersPreferenceComposite;
import com.aptana.core.build.IBuildParticipantWorkingCopy;
import com.aptana.core.logging.IdeLog;

/**
 * The "master" Factory for creating preference composites for a build participant. This will handle the case of no
 * participant or no custom composite, but otherwise delegates to a "slave" implementation specific to a participant.
 * 
 * @author cwilliams
 */
public class BuildParticipantPreferenceCompositeFactory implements IBuildParticipantPreferenceCompositeFactory
{

	private static final String EXTENSION_ID = "buildParticipantPreferenceComposite"; //$NON-NLS-1$
	private static final String ELEMENT_PARTICIPANT = "preferenceComposite"; //$NON-NLS-1$
	private static final String ATTR_PARTICIPANT_ID = "buildParticipantId"; //$NON-NLS-1$
	private static final String ATTR_CLASS = "class"; //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.build.ui.preferences.IBuildParticipantPreferenceCompositeFactory#createPreferenceComposite(org.eclipse
	 * .swt.widgets.Composite, com.aptana.core.build.IBuildParticipant)
	 */
	public Composite createPreferenceComposite(Composite parent, IBuildParticipantWorkingCopy participant)
	{
		if (participant == null)
		{
			return new NoParticipantPreferenceComposite(parent);
		}

		try
		{
			String participantId = participant.getId();
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			if (registry != null)
			{
				IExtensionPoint extensionPoint = registry.getExtensionPoint(BuildUIPlugin.PLUGIN_ID, EXTENSION_ID);
				if (extensionPoint != null)
				{
					IExtension[] extensions = extensionPoint.getExtensions();
					for (IExtension extension : extensions)
					{
						IConfigurationElement[] elements = extension.getConfigurationElements();
						for (IConfigurationElement element : elements)
						{
							if (ELEMENT_PARTICIPANT.equals(element.getName())
									&& participantId.equals(element.getAttribute(ATTR_PARTICIPANT_ID)))
							{
								IBuildParticipantPreferenceCompositeFactory blah = (IBuildParticipantPreferenceCompositeFactory) element
										.createExecutableExtension(ATTR_CLASS);
								return blah.createPreferenceComposite(parent, participant);
							}
						}
					}
				}
			}
		}
		catch (InvalidRegistryObjectException e)
		{
			IdeLog.logError(BuildUIPlugin.getDefault(), e);
		}
		catch (CoreException e)
		{
			IdeLog.logError(BuildUIPlugin.getDefault(), e);
		}
		// Fallback to default composite
		return new ValidatorFiltersPreferenceComposite(parent, participant);
	}
}
