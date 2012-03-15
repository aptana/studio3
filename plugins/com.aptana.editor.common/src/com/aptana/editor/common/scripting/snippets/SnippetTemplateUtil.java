/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.scripting.snippets;

import java.text.MessageFormat;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.IDebugScopes;
import com.aptana.scripting.model.SnippetElement;

/**
 * Utilities to manipulate SnippetTemplates
 * 
 * @author nle
 */
public class SnippetTemplateUtil
{
	/**
	 * Evaluate a snippet by replacing the tab stops with the default values. Note the snippet must have a scope or else
	 * the evaluation is not done
	 * 
	 * @param snippet
	 * @param document
	 * @param position
	 * @return
	 */
	public static String evaluateSnippet(SnippetElement snippet, IDocument document, Position position)
	{
		String expansion = snippet.getExpansion();
		Template template = new SnippetTemplate(snippet, expansion);
		String scope = snippet.getScope();

		if (scope != null)
		{
			SnippetTemplateContextType contextType = new SnippetTemplateContextType(scope);
			DocumentSnippetTemplateContext context = new DocumentSnippetTemplateContext(contextType, document, position);
			try
			{
				TemplateBuffer buffer = context.evaluate(template);
				if (buffer != null)
				{
					return buffer.getString();
				}
			}
			catch (Exception e)
			{
				IdeLog.logWarning(
						CommonEditorPlugin.getDefault(),
						MessageFormat.format("Error in template {0}. {1}", snippet.getDisplayName(), e.getMessage()), IDebugScopes.PRESENTATION); //$NON-NLS-1$
			}
		}

		return expansion;
	}
}
