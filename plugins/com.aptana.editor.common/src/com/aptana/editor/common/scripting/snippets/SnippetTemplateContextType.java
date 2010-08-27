/**
 * 
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