/**
 * Aptana Studio
 * Copyright (c) 2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.dispatch.actionControllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aptana.configurations.processor.ConfigurationStatus;
import com.aptana.core.projects.templates.IProjectTemplate;
import com.aptana.core.projects.templates.TemplateType;
import com.aptana.core.util.ResourceUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.jetty.util.epl.ajax.JSON;
import com.aptana.projects.ProjectsPlugin;

/**
 * Action controller for Template operations
 * 
 * @author nle
 */
public class TemplateActionController extends AbstractActionController
{
	/**
	 * Template-Info enum.
	 */
	public static enum TEMPLATE_INFO
	{
		ID("id"), //$NON-NLS-1$
		NAME("name"), //$NON-NLS-1$
		DESCRIPTION("description"), //$NON-NLS-1$
		TEMPLATE_TYPE("type"), //$NON-NLS-1$
		IMAGE_URL("image"), //$NON-NLS-1$
		TAG("tag"); //$NON-NLS-1$

		private String key;

		private TEMPLATE_INFO(String key)
		{
			this.key = key;
		}

		public String toString()
		{
			return key;
		}
	};

	private static final String[] ALL_TYPES = new String[] { TemplateType.PHP.name(), TemplateType.PYTHON.name(),
			TemplateType.RAILS.name(), TemplateType.RUBY.name(), TemplateType.TITANIUM_DESKTOP.name(),
			TemplateType.TITANIUM_MOBILE.name(), TemplateType.WEB.name() };

	public void configurationStateChanged(ConfigurationStatus status, Set<String> attributesChanged)
	{
	}

	/**
	 * Return the template types
	 * 
	 * @return
	 */
	@ControllerAction
	public Object getTemplateTypes()
	{
		return JSON.toString(ALL_TYPES);
	}

	/**
	 * Return the template for give types
	 * 
	 * @return
	 */
	@ControllerAction
	public Object getTemplates(Object templateTypes)
	{
		List<TemplateType> types = new ArrayList<TemplateType>();
		if (!(templateTypes instanceof Object[]) || ((Object[]) templateTypes).length < 1)
		{
			templateTypes = ALL_TYPES;
		}

		for (Object object : (Object[]) templateTypes)
		{
			if (object instanceof String)
			{
				TemplateType templateType = TemplateType.valueOf((String) object);
				if (templateType != null)
				{
					types.add(templateType);
				}
			}
		}

		List<IProjectTemplate> templates = ProjectsPlugin.getDefault().getTemplatesManager()
				.getTemplates(types.toArray(new TemplateType[types.size()]));
		List<Map<String, String>> templateObjects = new ArrayList<Map<String, String>>();

		for (IProjectTemplate template : templates)
		{
			if (template.getId() == null || template.getId().length() == 0)
			{
				continue;
			}

			Map<String, String> properties = new HashMap<String, String>();
			properties.put(TEMPLATE_INFO.ID.toString(), template.getId());
			properties.put(TEMPLATE_INFO.NAME.toString(), template.getDisplayName());
			properties.put(TEMPLATE_INFO.DESCRIPTION.toString(), template.getDescription());
			properties.put(TEMPLATE_INFO.TEMPLATE_TYPE.toString(), template.getType().name());
			if (template.getIconURL() != null)
			{
				URI iconPath = ResourceUtil.resourcePathToURI(template.getIconURL());
				properties.put(TEMPLATE_INFO.IMAGE_URL.toString(), iconPath.toASCIIString());
			}
			properties.put(TEMPLATE_INFO.TAG.toString(), StringUtil.join(",", template.getTags())); //$NON-NLS-1$
			templateObjects.add(properties);
		}

		return JSON.toString(templateObjects.toArray(new Map[templateObjects.size()]));
	}
}
