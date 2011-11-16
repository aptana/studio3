/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects.templates;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.aptana.core.projects.templates.IProjectTemplate;
import com.aptana.core.projects.templates.TemplateType;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IConfigurationElementProcessor;
import com.aptana.core.util.ResourceUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.projects.ProjectsPlugin;

/**
 * Project templates manager for templates contributions through the <code>"projectTemplates"</code> extension point.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class ProjectTemplatesManager
{

	private static final String EXTENSION_POINT = "projectTemplates"; //$NON-NLS-1$
	private static final String ELEMENT_TEMPLATEINFO = "templateInfo"; //$NON-NLS-1$
	private static final String ELEMENT_LOCAL = "local"; //$NON-NLS-1$
	private static final String ELEMENT_REMOTE = "remote"; //$NON-NLS-1$
	private static final String ATTR_NAME = "name"; //$NON-NLS-1$
	private static final String ATTR_LOCATION = "location"; //$NON-NLS-1$
	private static final String ATTR_DESCRIPTION = "description"; //$NON-NLS-1$
	private static final String ATTR_ICON = "icon"; //$NON-NLS-1$
	private static final String ATTR_TYPE = "type"; //$NON-NLS-1$
	private static final String ATTR_REPLACE_PARAMETERS = "replaceParameters"; //$NON-NLS-1$

	private Map<TemplateType, List<IProjectTemplate>> projectTemplates;

	public ProjectTemplatesManager()
	{
		projectTemplates = new HashMap<TemplateType, List<IProjectTemplate>>();
		readExtensionRegistry();
	}

	public List<IProjectTemplate> getTemplatesForType(TemplateType projectType)
	{
		List<IProjectTemplate> samples = projectTemplates.get(projectType);
		if (samples == null)
		{
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(samples);
	}

	private void readExtensionRegistry()
	{
		EclipseUtil.processConfigurationElements(ProjectsPlugin.PLUGIN_ID, EXTENSION_POINT,
				new IConfigurationElementProcessor()
				{

					public void processElement(IConfigurationElement element)
					{
						readElement(element);
					}

					public Set<String> getSupportElementNames()
					{
						return CollectionsUtil.newSet(ELEMENT_TEMPLATEINFO);
					}
				});
	}

	private void readElement(IConfigurationElement element)
	{
		if (ELEMENT_TEMPLATEINFO.equals(element.getName()))
		{
			// either a local path or remote git url needs to be defined
			String path = null;
			Bundle bundle = Platform.getBundle(element.getNamespaceIdentifier());
			IConfigurationElement[] localPaths = element.getChildren(ELEMENT_LOCAL);
			if (localPaths.length > 0)
			{
				String location = localPaths[0].getAttribute(ATTR_LOCATION);
				URL url = bundle.getEntry(location);
				path = ResourceUtil.resourcePathToString(url);
			}
			else
			{
				IConfigurationElement[] remotePaths = element.getChildren(ELEMENT_REMOTE);
				if (remotePaths.length > 0)
				{
					path = remotePaths[0].getAttribute(ATTR_LOCATION);
				}
			}
			if (StringUtil.isEmpty(path))
			{
				return;
			}
			TemplateType type = TemplateType.valueOf(element.getAttribute(ATTR_TYPE).toUpperCase());
			List<IProjectTemplate> templates = projectTemplates.get(type);
			if (templates == null)
			{
				templates = new ArrayList<IProjectTemplate>();
				projectTemplates.put(type, templates);
			}
			String name = element.getAttribute(ATTR_NAME);
			if (name == null)
			{
				name = StringUtil.EMPTY;
			}

			String description = element.getAttribute(ATTR_DESCRIPTION);
			if (description == null)
			{
				description = StringUtil.EMPTY;
			}

			String icon = element.getAttribute(ATTR_ICON);
			URL iconURL = null;
			if (icon != null)
			{
				iconURL = bundle.getEntry(icon);
			}

			boolean replacingParameters = Boolean.parseBoolean(element.getAttribute(ATTR_REPLACE_PARAMETERS));
			IProjectTemplate projectTemplate = new ProjectTemplate(path, type, name, replacingParameters, description,
					iconURL);
			templates.add(projectTemplate);
		}
	}
}
