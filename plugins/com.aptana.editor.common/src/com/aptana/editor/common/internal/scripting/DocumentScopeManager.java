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

package com.aptana.editor.common.internal.scripting;

import java.util.WeakHashMap;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.ICommonConstants;
import com.aptana.editor.common.IPartitioningConfiguration;
import com.aptana.editor.common.scripting.IContentTypeTranslator;
import com.aptana.editor.common.scripting.IDocumentScopeManager;
import com.aptana.editor.common.scripting.QualifiedContentType;

/**
 * @author Max Stepanov
 */
public class DocumentScopeManager implements IDocumentScopeManager
{

	private static final QualifiedContentType UNKNOWN = new QualifiedContentType(ICommonConstants.CONTENT_TYPE_UKNOWN);

	private static DocumentScopeManager instance;

	private WeakHashMap<IDocument, ExtendedDocumentInfo> infos = new WeakHashMap<IDocument, ExtendedDocumentInfo>();

	/**
	 * 
	 */
	private DocumentScopeManager()
	{
	}

	public static DocumentScopeManager getInstance()
	{
		if (instance == null)
		{
			instance = new DocumentScopeManager();
		}
		return instance;
	}

	/**
	 * Store the filename for the document so we can dynamically look up the scope later.
	 * 
	 * @param document
	 * @param defaultContentType
	 * @param filename
	 */
	public void setDocumentScope(IDocument document, String defaultContentType, String filename)
	{
		infos.put(document, new ExtendedDocumentInfo(defaultContentType, filename));
	}

	public void registerConfigurations(IDocument document, IPartitioningConfiguration[] configurations)
	{
		for (IPartitioningConfiguration i : configurations)
		{
			registerConfiguration(document, i);
		}
	}

	public void registerConfiguration(IDocument document, IPartitioningConfiguration configuration)
	{
		ExtendedDocumentInfo info = infos.get(document);
		if (info != null)
		{
			// associate raw partition names with top level scope/content type for document
			for (String i : configuration.getContentTypes())
			{
				info.associateContentType(i, configuration.getDocumentContentType(i));
			}
		}
	}

	public QualifiedContentType getContentType(IDocument document, int offset) throws BadLocationException
	{
		if (document == null)
			return UNKNOWN;
		ExtendedDocumentInfo info = infos.get(document);
		if (info != null)
		{
			return info.getContentType(document, offset);
		}
		// Return an unknown top level scope, with the raw partition name as sub-scope
		return UNKNOWN.subtype(document.getContentType(offset));
	}

	public String getScopeAtOffset(IDocument document, int offset) throws BadLocationException
	{
		// TODO We still don't append the token to end of scope, which Textmate does. Unfortunately I have no idea how
		// we'd get that unless we ran a token scanner on the whole partition until we hit the token for our offset, and
		// even then we need the token name, not the text attribute.
		QualifiedContentType contentType = getContentType(document, offset);
		if (contentType != null)
		{
			// Now we translate our custom top level content types into scopes, and our partition names into scopes as
			// well.
			QualifiedContentType translation = getContentTypeTranslator().translate(contentType);
			ExtendedDocumentInfo info = infos.get(document);
			if (info != null)
			{
				translation = info.modify(translation);
			}
			return translation.toString();
		}
		return document.getContentType(offset);
	}

	protected IContentTypeTranslator getContentTypeTranslator()
	{
		return CommonEditorPlugin.getDefault().getContentTypeTranslator();
	}

}
