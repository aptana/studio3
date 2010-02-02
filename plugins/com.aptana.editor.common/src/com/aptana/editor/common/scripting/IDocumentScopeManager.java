package com.aptana.editor.common.scripting;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

import com.aptana.editor.common.IPartitioningConfiguration;

public interface IDocumentScopeManager
{

	public void registerConfiguration(IDocument document, IPartitioningConfiguration configuration);

	// FIXME Rename to getScopeAtOffset
	public String getContentTypeAtOffset(IDocument document, int offset) throws BadLocationException;

	public void registerConfigurations(IDocument document, IPartitioningConfiguration[] iPartitioningConfigurations);

	// FIXME Rename setTopLevelScope
	public void setDocumentContentType(IDocument document, String defaultContentType, String fileName);

}
