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

import java.lang.reflect.Field;
import java.util.WeakHashMap;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.IPresentationRepairer;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.SourceViewer;

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
	private WeakHashMap<IDocument, ExtendedDocumentInfo> infos = new WeakHashMap<IDocument, ExtendedDocumentInfo>();

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
		{
			return UNKNOWN;
		}
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
		return getPartitionScopeFragmentsAtOffset(document, offset);
	}

	public String getScopeAtOffset(ITextViewer viewer, int offset) throws BadLocationException
	{
		IDocument document = viewer.getDocument();
		String partitionFragment = getPartitionScopeFragmentsAtOffset(document, offset);
		String tokenPortion = null;
		try
		{
			Field f = SourceViewer.class.getDeclaredField("fPresentationReconciler"); //$NON-NLS-1$
			f.setAccessible(true);
			IPresentationReconciler reconciler = (IPresentationReconciler) f.get(viewer);
			if (reconciler != null)
			{
				ITypedRegion region = document.getPartition(offset);
				String contentType = region.getType();

				IPresentationRepairer repairer = reconciler.getRepairer(contentType);
				if (repairer instanceof DefaultDamagerRepairer)
				{
					DefaultDamagerRepairer ddr = (DefaultDamagerRepairer) repairer;
					f = DefaultDamagerRepairer.class.getDeclaredField("fScanner"); //$NON-NLS-1$
					f.setAccessible(true);
					ITokenScanner fScanner = (ITokenScanner) f.get(ddr);
					fScanner.setRange(document, region.getOffset(), region.getLength());
					while (true)
					{
						IToken token = fScanner.nextToken();
						if (token.isEOF())
							break;
						int tokenOffset = fScanner.getTokenOffset();
						if (tokenOffset > offset)
						{
							break;
						}
						if (offset >= tokenOffset && offset <= (tokenOffset + fScanner.getTokenLength()))
						{
							Object data = token.getData();
							if (data instanceof String)
							{
								tokenPortion = (String) data;
							}
							break;
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			// ignore
		}
		if (tokenPortion != null)
		{
			if (tokenPortion.length() == 0)
			{
				return partitionFragment;
			}

			if (!partitionFragment.endsWith(tokenPortion))
			{
				return partitionFragment + " " + tokenPortion; //$NON-NLS-1$
			}
		}
		return partitionFragment;
	}

	public String getPartitionScopeFragmentsAtOffset(IDocument document, int offset) throws BadLocationException
	{
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

	public void dispose()
	{
		infos.clear();
	}

}
