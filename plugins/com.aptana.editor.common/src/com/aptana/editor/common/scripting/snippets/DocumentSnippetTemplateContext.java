/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.scripting.snippets;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateException;
import org.eclipse.jface.text.templates.TemplateTranslator;

public class DocumentSnippetTemplateContext extends DocumentTemplateContext
{
	private Template template;

	public DocumentSnippetTemplateContext(TemplateContextType type, IDocument document, int offset, int length)
	{
		super(type, document, offset, length);
	}

	public DocumentSnippetTemplateContext(TemplateContextType type, IDocument document, Position position)
	{
		super(type, document, position);
	}

	/*
	 * @see org.eclipse.jface.text.templates.TemplateContext#evaluate(org.eclipse.jface.text.templates.Template)
	 */
	public TemplateBuffer evaluate(Template template) throws BadLocationException, TemplateException
	{
		if (!canEvaluate(template))
			return null;

		try
		{
			this.template = template;

			TemplateTranslator translator = new SnippetTemplateTranslator();
			TemplateBuffer buffer = translator.translate(template);

			getContextType().resolve(buffer, this);

			return buffer;
		}
		finally
		{
			this.template = null;
		}
	}

	Template getTemplate()
	{
		return template;
	}

}
