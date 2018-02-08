/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.scripting.snippets;

import org.eclipse.jface.text.templates.SimpleTemplateVariableResolver;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateVariable;

class TabStopVariableResolver extends SimpleTemplateVariableResolver
{

	static final String VARIABLE_TYPE = "tabstop"; //$NON-NLS-1$

	TabStopVariableResolver()
	{
		super(VARIABLE_TYPE, VARIABLE_TYPE);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void resolve(TemplateVariable variable, TemplateContext context)
	{
		if (!variable.getVariableType().getParams().isEmpty())
		{
			String[] values = (String[]) variable.getVariableType().getParams().toArray(new String[0]);
			variable.setValues(values);
			variable.setUnambiguous(false);
			variable.setResolved(true);
		}
		else
		{
			super.resolve(variable, context);
			setEvaluationString(variable.getName());
		}
	}

	protected boolean isUnambiguous(TemplateContext context)
	{
		return false;
	}
}
