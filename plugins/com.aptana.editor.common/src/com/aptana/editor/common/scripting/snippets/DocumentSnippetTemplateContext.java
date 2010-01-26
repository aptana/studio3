package com.aptana.editor.common.scripting.snippets;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;

public class DocumentSnippetTemplateContext extends DocumentTemplateContext {

	public DocumentSnippetTemplateContext(TemplateContextType type, IDocument document, int offset, int length) {
		super(type, document, offset, length);
	}

	public DocumentSnippetTemplateContext(TemplateContextType type, IDocument document, Position position) {
		super( type, document, position);
	}

}
