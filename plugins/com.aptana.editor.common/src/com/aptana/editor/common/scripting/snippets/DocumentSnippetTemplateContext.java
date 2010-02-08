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
