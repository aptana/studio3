/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.scripting.snippets;

import org.eclipse.jface.text.templates.GlobalTemplateVariables;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateException;

class SnippetTemplateContextType extends TemplateContextType
{

	SnippetTemplateContextType(String scope)
	{
		super(scope);
		addGlobalResolvers();
	}

	private void addGlobalResolvers()
	{

		// Global
		addResolver(new GlobalTemplateVariables.Cursor());
		addResolver(new GlobalTemplateVariables.WordSelection());
		addResolver(new GlobalTemplateVariables.LineSelection());
		addResolver(new GlobalTemplateVariables.Dollar());
		addResolver(new GlobalTemplateVariables.Date());
		addResolver(new GlobalTemplateVariables.Year());
		addResolver(new GlobalTemplateVariables.Time());
		addResolver(new GlobalTemplateVariables.User());

		// Tabstops resolver
		addResolver(new TabStopVariableResolver());
		// Environment variables resolver
		addResolver(new EnvironmentVariableVariableResolver());
	}

	public void validate(String pattern) throws TemplateException
	{
		// assume valid
	}

}