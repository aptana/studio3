/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.scripting;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;

import com.aptana.editor.common.IPartitioningConfiguration;

public interface IDocumentScopeManager
{

	public void registerConfiguration(IDocument document, IPartitioningConfiguration configuration);

	public void registerConfigurations(IDocument document, IPartitioningConfiguration[] iPartitioningConfigurations);

	/**
	 * Performs the full scope lookup: partition + token level scopes and does any translations. This method should be
	 * preferred over {@link #getScopeAtOffset(IDocument, int)} when you need the token level scope. Note that this can
	 * be more expensive to calculate!
	 * 
	 * @param document
	 * @param offset
	 * @return
	 * @throws BadLocationException
	 */
	public String getScopeAtOffset(ITextViewer viewer, int offset) throws BadLocationException;

	/**
	 * Performs dynamic scope determination at given offset for document. This will lookup the default scope we assigned
	 * as well as the partition at the offset. We'll then translate from partition names to scope names. Lastly we'll do
	 * any overrides of the top level scope by trying to match the filename patterns contributed by bundles to the
	 * override scopes (See {@link com.aptana.scripting.model.BundleElement#associateScope(String, String)}. This only
	 * performs partition-level scope lookups. To get partition plus token level you should invoke
	 * {@link #getScopeAtOffset(ITextViewer, int)}
	 * 
	 * @param document
	 * @param offset
	 * @return
	 * @throws BadLocationException
	 */
	public String getScopeAtOffset(IDocument document, int offset) throws BadLocationException;

	/**
	 * Associated an IDocument with a default top level scope to use and the filename the document represents. Scope is
	 * determined on demand by using the partitions (a.k.a. content types), translation, and possible top-level scope
	 * overrides set by bundles.
	 */
	public void setDocumentScope(IDocument document, String defaultScope, String fileName);

	/**
	 * Returns a qualified, un-translated, content-type at a specific offset. <br>
	 * 
	 * @param document
	 * @param offset
	 * @return A QualifiedContentType of the content at a specific offset.
	 * @throws BadLocationException
	 */
	public QualifiedContentType getContentType(IDocument document, int offset) throws BadLocationException;
}
