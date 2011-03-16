/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.validator;

import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.aptana.editor.common.CommonEditorPlugin;

public class ValidatorReference
{

	private static final String ATTR_CLASS = "class"; //$NON-NLS-1$

	private final String name;
	private final ValidatorLanguage language;
	private final String type;
	private final IConfigurationElement configElement;
	private IValidator validator;

	public ValidatorReference(String name, String type, ValidatorLanguage language, IConfigurationElement configElement)
	{
		this.name = name;
		this.type = type;
		this.language = language;
		this.configElement = configElement;
	}

	public String getName()
	{
		return name;
	}

	public String getType()
	{
		return type;
	}

	public ValidatorLanguage getLanguage()
	{
		return language;
	}

	public synchronized IValidator getValidator()
	{
		if (validator == null)
		{
			try
			{
				validator = (IValidator) configElement.createExecutableExtension(ATTR_CLASS);
			}
			catch (CoreException e)
			{
				CommonEditorPlugin.logError(
						MessageFormat.format(Messages.ValidatorReference_ERR_FailToCreateValidator, name), e);
			}
		}
		return validator;
	}
}
