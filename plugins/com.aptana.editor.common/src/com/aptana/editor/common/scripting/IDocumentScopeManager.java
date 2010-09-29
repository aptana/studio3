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

import com.aptana.editor.common.IPartitioningConfiguration;
import com.aptana.scripting.model.BundleElement;

public interface IDocumentScopeManager
{

	public void registerConfiguration(IDocument document, IPartitioningConfiguration configuration);

	public void registerConfigurations(IDocument document, IPartitioningConfiguration[] iPartitioningConfigurations);

	/**
	 * Performs dynamic scope determination at given offset for document. This will lookup the default scope we assigned
	 * as well as the partition at the offset. We'll then translate from partition names to scope names. Lastly we'll do
	 * any overrides of the top level scope by trying to match the filename patterns contributed by bundles to the
	 * override scopes (See {@link BundleElement#associateScope(String, String)}.
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

}
