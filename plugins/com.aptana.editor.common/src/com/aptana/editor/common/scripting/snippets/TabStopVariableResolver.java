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
