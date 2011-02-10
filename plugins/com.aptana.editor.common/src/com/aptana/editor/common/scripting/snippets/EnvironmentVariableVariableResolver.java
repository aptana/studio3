/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.scripting.snippets;

import java.util.Map;

import org.eclipse.jface.text.templates.SimpleTemplateVariableResolver;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateVariable;

import com.aptana.scripting.model.CommandElement;

public class EnvironmentVariableVariableResolver extends SimpleTemplateVariableResolver
{
	public static final String VARIABLE_TYPE = "environment"; //$NON-NLS-1$

	EnvironmentVariableVariableResolver()
	{
		super(VARIABLE_TYPE, VARIABLE_TYPE);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void resolve(TemplateVariable variable, TemplateContext context)
	{
		if (context instanceof DocumentSnippetTemplateContext)
		{
			DocumentSnippetTemplateContext documentSnippetTemplateContext = (DocumentSnippetTemplateContext) context;
			Template template = documentSnippetTemplateContext.getTemplate();
			if (template instanceof SnippetTemplate)
			{
				SnippetTemplate snippetTemplate = (SnippetTemplate) template;
				CommandElement snippet = snippetTemplate.getCommandElement();
				Map<String, String> environment = snippet.getEnvironment();
				String name = variable.getName();
				String value = environment.get(name);
				if (value == null)
				{
					if (!variable.getVariableType().getParams().isEmpty())
					{
						String[] values = (String[]) variable.getVariableType().getParams().toArray(new String[0]);
						variable.setValues(values);
						variable.setUnambiguous(false);
					}
					else
					{
						super.resolve(variable, context);
					}
				}
				else
				{
					variable.setValues(new String[] { value });
				}
				variable.setResolved(true);
			}
		}
	}

	protected boolean isUnambiguous(TemplateContext context)
	{
		return false;
	}
}