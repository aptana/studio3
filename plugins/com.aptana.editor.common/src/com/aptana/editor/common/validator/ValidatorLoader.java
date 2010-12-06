/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
				String languageType = element.getAttribute(ATTR_LANGUAGE);
				ValidatorLanguage language = languages.get(languageType);
				if (language == null)
				{
					continue;
				}
				ValidatorReference validator = new ValidatorReference(name, language, element);
				List<ValidatorReference> list = validators.get(language);
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
