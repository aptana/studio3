/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
