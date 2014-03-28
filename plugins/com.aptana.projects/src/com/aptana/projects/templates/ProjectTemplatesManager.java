/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects.templates;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

import com.aptana.core.projects.templates.IProjectTemplate;
import com.aptana.core.projects.templates.ProjectTemplate;
import com.aptana.core.projects.templates.TemplateType;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IConfigurationElementProcessor;
import com.aptana.core.util.ResourceUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.projects.ProjectsPlugin;
import com.aptana.scripting.model.AbstractElement;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.ElementVisibilityListener;
import com.aptana.scripting.model.ProjectTemplateElement;
import com.aptana.scripting.model.filters.IModelFilter;
import com.aptana.ui.util.UIUtils;

/**
 * Project templates manager for templates contributions through the <code>"projectTemplates"</code> extension point.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class ProjectTemplatesManager
{
	/**
	 * A special generated tag where all templates without tags end up.
	 */
	public static final String TAG_OTHERS = Messages.ProjectTemplatesManager_OthersTagName;

	private static final String EXTENSION_POINT = "projectTemplates"; //$NON-NLS-1$
	private static final String ELEMENT_TEMPLATEINFO = "templateInfo"; //$NON-NLS-1$
	private static final String ELEMENT_LOCAL = "local"; //$NON-NLS-1$
	private static final String ELEMENT_REMOTE = "remote"; //$NON-NLS-1$
	private static final String ATTR_NAME = "name"; //$NON-NLS-1$
	private static final String ATTR_LOCATION = "location"; //$NON-NLS-1$
	private static final String ATTR_DESCRIPTION = "description"; //$NON-NLS-1$
	private static final String ATTR_ICON = "icon"; //$NON-NLS-1$
	private static final String ATTR_TYPE = "type"; //$NON-NLS-1$
	private static final String ATTR_ID = "id"; //$NON-NLS-1$
	private static final String ATTR_REPLACE_PARAMETERS = "replaceParameters"; //$NON-NLS-1$

	private Map<TemplateType, Set<IProjectTemplate>> projectTemplates;
	private ImageRegistry templateTagsImageRegistry;

	private ElementVisibilityListener elementListener = new ElementVisibilityListener()
	{

		public void elementBecameHidden(AbstractElement element)
		{
			if (element instanceof ProjectTemplateElement)
			{
				ProjectTemplateElement template = (ProjectTemplateElement) element;
				removeTemplate(template);
			}
		}

		public void elementBecameVisible(AbstractElement element)
		{
			if (element instanceof ProjectTemplateElement)
			{
				ProjectTemplateElement template = (ProjectTemplateElement) element;
				addTemplate(template);
			}
		}
	};

	private List<IProjectTemplateListener> templateListeners;

	public ProjectTemplatesManager()
	{
		projectTemplates = new HashMap<TemplateType, Set<IProjectTemplate>>();
		templateListeners = new ArrayList<IProjectTemplateListener>();
		readExtensionRegistry();
		loadTemplatesFromBundles();

		BundleManager.getInstance().addElementVisibilityListener(elementListener);
	}

	public void dispose()
	{
		BundleManager.getInstance().removeElementVisibilityListener(elementListener);
	}

	public void addListener(IProjectTemplateListener listener)
	{
		if (!templateListeners.contains(listener))
		{
			templateListeners.add(listener);
		}
	}

	public void removeListener(IProjectTemplateListener listener)
	{
		templateListeners.remove(listener);
	}

	/**
	 * Returns a list of {@link IProjectTemplate} for the given type.<br>
	 * 
	 * @param projectType
	 *            The specific project type
	 * @return a list of project templates matching the type
	 */
	public List<IProjectTemplate> getTemplatesForType(TemplateType projectType)
	{
		Set<IProjectTemplate> templates = projectTemplates.get(projectType);
		if (templates == null)
		{
			return Collections.emptyList();
		}
		return new ArrayList<IProjectTemplate>(templates);
	}

	/**
	 * Returns a list of {@link IProjectTemplate} that match any of the given types.<br>
	 * 
	 * @param projectTypes
	 *            an array of project types
	 * @return a list of project templates matching the types
	 */
	public List<IProjectTemplate> getTemplates(TemplateType[] projectTypes)
	{
		List<IProjectTemplate> templates = new ArrayList<IProjectTemplate>();
		for (TemplateType type : projectTypes)
		{
			templates.addAll(getTemplatesForType(type));
		}
		return templates;
	}

	public Image getImageForTag(String tag)
	{
		ImageRegistry reg = getImageRegistry();
		if (reg != null)
		{
			return reg.get(tag);
		}
		return null;
	}

	public void putImageForTag(String tag, ImageDescriptor imageDescriptor)
	{
		ImageRegistry reg = getImageRegistry();
		if (reg != null)
		{
			reg.put(tag, imageDescriptor);
		}
	}

	private ImageRegistry getImageRegistry()
	{
		try
		{
			if (templateTagsImageRegistry == null)
			{
				templateTagsImageRegistry = new ImageRegistry(UIUtils.getDisplay());
			}
			return templateTagsImageRegistry;
		}
		catch (IllegalStateException e)
		{
			// ignore - heppsn when headless in tests
		}
		return null;
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

			String id = element.getAttribute(ATTR_ID);
			if (id == null)
			{
				id = StringUtil.EMPTY;
			}

			boolean replacingParameters = Boolean.parseBoolean(element.getAttribute(ATTR_REPLACE_PARAMETERS));

			addTemplate(new ProjectTemplate(path, type, name, replacingParameters, description, iconURL, id));
		}
	}

	private void loadTemplatesFromBundles()
	{
		List<ProjectTemplateElement> templates = BundleManager.getInstance().getProjectTemplates(new IModelFilter()
		{
			public boolean include(AbstractElement element)
			{
				return (element instanceof ProjectTemplateElement);
			}
		});
		for (IProjectTemplate template : templates)
		{
			addTemplate(template);
		}
	}

	public void addTemplate(IProjectTemplate template)
	{
		TemplateType type = template.getType();
		Set<IProjectTemplate> templates = projectTemplates.get(type);
		if (templates == null)
		{
			templates = new TreeSet<IProjectTemplate>(new ProjectTemplateComparator());
			projectTemplates.put(type, templates);
		}
		templates.add(template);
		fireTemplateAdded(template);
	}

	public void removeTemplate(IProjectTemplate template)
	{
		TemplateType type = template.getType();
		Set<IProjectTemplate> templates = projectTemplates.get(type);
		if (templates != null)
		{
			templates.remove(template);
			fireTemplateRemoved(template);
		}
	}

	private void fireTemplateAdded(IProjectTemplate template)
	{
		for (IProjectTemplateListener listener : templateListeners)
		{
			listener.templateAdded(template);
		}
	}

	private void fireTemplateRemoved(IProjectTemplate template)
	{
		for (IProjectTemplateListener listener : templateListeners)
		{
			listener.templateRemoved(template);
		}
	}

	/**
	 * A project template comparator that gives priority to {@link IDefaultProjectTemplate} instances first, and then
	 * compare by the template's name.
	 */
	private class ProjectTemplateComparator implements Comparator<IProjectTemplate>
	{
		/*
		 * (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(IProjectTemplate pt1, IProjectTemplate pt2)
		{
			boolean isPt1Default = pt1 instanceof IDefaultProjectTemplate;
			boolean isPt2Default = pt2 instanceof IDefaultProjectTemplate;
			// Give priority to the default. If both are defaults, give priority by name.
			if (isPt1Default && isPt2Default || !(isPt1Default || isPt2Default))
			{
				return pt1.getDisplayName().compareTo(pt2.getDisplayName());
			}
			if (isPt1Default)
			{
				return -1;
			}
			return 1;
		}

	}

}
