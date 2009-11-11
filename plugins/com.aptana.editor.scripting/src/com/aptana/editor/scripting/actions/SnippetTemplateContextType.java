/**
 * 
 */
package com.aptana.editor.scripting.actions;

import org.eclipse.jface.text.templates.GlobalTemplateVariables;
import org.eclipse.jface.text.templates.TemplateContextType;

class SnippetTemplateContextType extends TemplateContextType {
	SnippetTemplateContextType() {
		super("snippets");
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
		addResolver(new TabStopVariableResolver("1", "1st tab stop"));
		addResolver(new TabStopVariableResolver("2", "2nd tab stop"));
		addResolver(new TabStopVariableResolver("3", "3rt tab stop"));
		addResolver(new TabStopVariableResolver("4", "4th tab stop"));
		addResolver(new TabStopVariableResolver("5", "5th tab stop"));
		addResolver(new TabStopVariableResolver("6", "6th tab stop"));
		addResolver(new TabStopVariableResolver("7", "7th tab stop"));
		addResolver(new TabStopVariableResolver("8", "8th tab stop"));
		addResolver(new TabStopVariableResolver("9", "9th tab stop"));
	}
}