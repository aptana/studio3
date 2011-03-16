/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.RegistryFactory;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.CommonEditorPlugin;

public class ValidatorLoader
{

	private static final String EXTENSION_POINT_ID = CommonEditorPlugin.PLUGIN_ID + ".validator"; //$NON-NLS-1$
	private static final String ELEMENT_VALIDATOR = "validator"; //$NON-NLS-1$
	private static final String ELEMENT_LANGUAGE = "language"; //$NON-NLS-1$
	private static final String ATTR_NAME = "name"; //$NON-NLS-1$
	private static final String ATTR_TYPE = "type"; //$NON-NLS-1$
	private static final String ATTR_LANGUAGE = "language"; //$NON-NLS-1$

	// maps the languages by type
	private Map<String, ValidatorLanguage> languages;
	// maps language type to the list of validators that support it
	private Map<String, List<ValidatorReference>> validators;

	private static ValidatorLoader instance;

	private ValidatorLoader()
	{
		languages = new HashMap<String, ValidatorLanguage>();
		validators = new HashMap<String, List<ValidatorReference>>();
		readExtensionRegistry();
	}

	public static synchronized ValidatorLoader getInstance()
	{
		if (instance == null)
		{
			instance = new ValidatorLoader();
		}
		return instance;
	}

	public List<ValidatorLanguage> getLanguages()
	{
		List<ValidatorLanguage> result = new ArrayList<ValidatorLanguage>();
		result.addAll(languages.values());
		return result;
	}

	public List<ValidatorReference> getValidators(String languageType)
	{
		List<ValidatorReference> list = validators.get(languageType);
		if (list == null)
		{
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(list);
	}

	private void readExtensionRegistry()
	{
		IExtensionRegistry registry = RegistryFactory.getRegistry();
		if (registry != null)
		{
			IConfigurationElement[] elements = registry.getConfigurationElementsFor(EXTENSION_POINT_ID);
			processLanguages(elements);
			processValidators(elements);
		}
	}

	private void processLanguages(IConfigurationElement[] elements)
	{
		for (IConfigurationElement element : elements)
		{
			if (ELEMENT_LANGUAGE.equals(element.getName()))
			{
				String type = element.getAttribute(ATTR_TYPE);
				if (StringUtil.isEmpty(type))
				{
					continue;
				}
				String name = element.getAttribute(ATTR_NAME);
				languages.put(type, new ValidatorLanguage(type, name));
			}
		}
	}

	private void processValidators(IConfigurationElement[] elements)
	{
		for (IConfigurationElement element : elements)
		{
			if (ELEMENT_VALIDATOR.equals(element.getName()))
			{
				String name = element.getAttribute(ATTR_NAME);
				if (StringUtil.isEmpty(name))
				{
					continue;
				}
				String type = element.getAttribute(ATTR_TYPE);
				if (StringUtil.isEmpty(type))
				{
					continue;
				}
				String languageType = element.getAttribute(ATTR_LANGUAGE);
				ValidatorLanguage language = languages.get(languageType);
				if (language == null)
				{
					continue;
				}
				ValidatorReference validator = new ValidatorReference(name, type, language, element);
				List<ValidatorReference> list = validators.get(languageType);
				if (list == null)
				{
					list = new ArrayList<ValidatorReference>();
					validators.put(languageType, list);
				}
				list.add(validator);
			}
		}
	}
}
