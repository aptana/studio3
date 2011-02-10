/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common.internal.scripting;

import java.lang.reflect.Field;
import java.util.WeakHashMap;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ISynchronizable;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.IPresentationRepairer;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.ISourceViewer;
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

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.scripting.IDocumentScopeManager#registerConfigurations(org.eclipse.jface.text.IDocument, com.aptana.editor.common.IPartitioningConfiguration[])
	 */
	public void registerConfigurations(IDocument document, IPartitioningConfiguration[] configurations)
	{
		for (IPartitioningConfiguration i : configurations)
		{
			registerConfiguration(document, i);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.scripting.IDocumentScopeManager#registerConfiguration(org.eclipse.jface.text.IDocument, com.aptana.editor.common.IPartitioningConfiguration)
	 */
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

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.scripting.IDocumentScopeManager#getContentType(org.eclipse.jface.text.IDocument, int)
	 */
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

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.scripting.IDocumentScopeManager#getScopeAtOffset(org.eclipse.jface.text.IDocument,
	 * int)
	 */
	public String getScopeAtOffset(IDocument document, int offset) throws BadLocationException
	{
		return getPartitionScopeFragmentsAtOffset(document, offset);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.editor.common.scripting.IDocumentScopeManager#getScopeAtOffset(org.eclipse.jface.text.ITextViewer,
	 * int)
	 */
	public String getScopeAtOffset(ITextViewer viewer, int offset) throws BadLocationException
	{
		if (viewer == null)
		{
			return ""; //$NON-NLS-1$
		}
		IDocument document = viewer.getDocument();
		String partitionFragment = getPartitionScopeFragmentsAtOffset(document, offset);
		String tokenPortion = getTokenScopeFragments(viewer, document, offset);
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
	
	private String getTokenScopeFragments(ITextViewer viewer, IDocument document, int offset)
	{
		if (!(viewer instanceof ISourceViewer))
		{
			return null;
		}

		try
		{
			Field f = SourceViewer.class.getDeclaredField("fPresentationReconciler"); //$NON-NLS-1$
			f.setAccessible(true);
			IPresentationReconciler reconciler = (IPresentationReconciler) f.get(viewer);
			if (reconciler == null)
			{
				return null;
			}
			ITypedRegion region = document.getPartition(offset);
			String contentType = region.getType();

			IPresentationRepairer repairer = reconciler.getRepairer(contentType);
			if (!(repairer instanceof DefaultDamagerRepairer))
			{
				return null;
			}
			f = DefaultDamagerRepairer.class.getDeclaredField("fScanner"); //$NON-NLS-1$
			f.setAccessible(true);
			ITokenScanner scanner = (ITokenScanner) f.get(repairer);
			if (scanner == null)
			{
				return null;
			}
			synchronized (getLockObject(document))
			{
				scanner.setRange(document, region.getOffset(), region.getLength());
				while (true)
				{
					IToken token = scanner.nextToken();
					if (token == null || token.isEOF())
					{
						// we unexpectedly hit EOF, stop looping
						break;
					}
					int tokenOffset = scanner.getTokenOffset();
					if (tokenOffset > offset) // we passed the offset, quit looping
					{
						break;
					}
					if (offset >= tokenOffset && offset < (tokenOffset + scanner.getTokenLength()))
					{
						// token spans the offset, should contain a String containing the token-level scope fragments
						Object data = token.getData();
						if (data instanceof String)
						{
							return (String) data;
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			CommonEditorPlugin.logError(e);
		}
		return null;
	}
	
	private static Object getLockObject(Object object)
	{
		if (object instanceof ISynchronizable)
		{
			Object lock = ((ISynchronizable) object).getLockObject();
			if (lock != null)
			{
				return lock;
			}
		}
		return object;
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
