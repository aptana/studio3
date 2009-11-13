/**
 * 
 */
package com.aptana.editor.scripting.actions;

import org.eclipse.jface.text.templates.GlobalTemplateVariables;
import org.eclipse.jface.text.templates.TemplateContextType;

class SnippetTemplateContextType extends TemplateContextType {
	SnippetTemplateContextType(String scope) {
		super(scope);
		addGlobalResolvers();
	}

	private void addGlobalResolvers() {
		
		// Global
		addResolver(new GlobalTemplateVariables.Cursor());
		addResolver(new GlobalTemplateVariables.WordSelection());
		addResolver(new GlobalTemplateVariables.LineSelection());
		addResolver(new GlobalTemplateVariables.Dollar());
		addResolver(new GlobalTemplateVariables.Date());
		addResolver(new GlobalTemplateVariables.Year());
		addResolver(new GlobalTemplateVariables.Time());
		addResolver(new GlobalTemplateVariables.User());
		
		// Tab stops
		addResolver(new TabStopVariableResolver("1", "1st tab stop")); //$NON-NLS-1$ //$NON-NLS-2$
		addResolver(new TabStopVariableResolver("2", "2nd tab stop")); //$NON-NLS-1$ //$NON-NLS-2$
		addResolver(new TabStopVariableResolver("3", "3rt tab stop")); //$NON-NLS-1$ //$NON-NLS-2$
		addResolver(new TabStopVariableResolver("4", "4th tab stop")); //$NON-NLS-1$ //$NON-NLS-2$
		addResolver(new TabStopVariableResolver("5", "5th tab stop")); //$NON-NLS-1$ //$NON-NLS-2$
		addResolver(new TabStopVariableResolver("6", "6th tab stop")); //$NON-NLS-1$ //$NON-NLS-2$
		addResolver(new TabStopVariableResolver("7", "7th tab stop")); //$NON-NLS-1$ //$NON-NLS-2$
		addResolver(new TabStopVariableResolver("8", "8th tab stop")); //$NON-NLS-1$ //$NON-NLS-2$
		addResolver(new TabStopVariableResolver("9", "9th tab stop")); //$NON-NLS-1$ //$NON-NLS-2$
	}
}