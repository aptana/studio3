/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common.internal.scripting;

import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TypedPosition;
import org.eclipse.jface.text.source.ISourceViewer;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.ICommonConstants;
import com.aptana.editor.common.IPartitioningConfiguration;
import com.aptana.editor.common.scripting.IContentTypeTranslator;
import com.aptana.editor.common.scripting.IDocumentScopeManager;
import com.aptana.editor.common.scripting.QualifiedContentType;
import com.aptana.editor.common.util.EditorUtil;

/**
 * @author Max Stepanov
 */
public class DocumentScopeManager implements IDocumentScopeManager
{

	/**
	 * The prefix used for meta scope fragments holding project nature ids we prepend to the total scope.
	 */
	private static final String PROJECT_NATURE_SCOPE_PREFIX = "meta.project."; //$NON-NLS-1$

	private static final QualifiedContentType UNKNOWN = new QualifiedContentType(ICommonConstants.CONTENT_TYPE_UKNOWN);
	private Map<IDocument, ExtendedDocumentInfo> infos = new WeakHashMap<IDocument, ExtendedDocumentInfo>();

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
	 * @see
	 * com.aptana.editor.common.scripting.IDocumentScopeManager#registerConfigurations(org.eclipse.jface.text.IDocument,
	 * com.aptana.editor.common.IPartitioningConfiguration[])
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
	 * @see
	 * com.aptana.editor.common.scripting.IDocumentScopeManager#registerConfiguration(org.eclipse.jface.text.IDocument,
	 * com.aptana.editor.common.IPartitioningConfiguration)
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
	 * @see com.aptana.editor.common.scripting.IDocumentScopeManager#getContentType(org.eclipse.jface.text.IDocument,
	 * int)
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
		try
		{
			return UNKNOWN.subtype(document.getContentType(offset));
		}
		catch (Exception e)
		{
			return UNKNOWN;
		}
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
			return StringUtil.EMPTY;
		}

		IDocument document = viewer.getDocument();
		String partitionFragment = getPartitionScopeFragmentsAtOffset(document, offset);

		// Prepend scope with meta scopes based on project natures of the associated project!
		partitionFragment = prependNaturesToScope(viewer) + partitionFragment;

		String tokenPortion = getTokenScopeFragments(viewer, document, offset);
		if (tokenPortion != null)
		{
			if (tokenPortion.length() == 0)
			{
				return partitionFragment;
			}

			if (!partitionFragment.endsWith(tokenPortion))
			{
				return partitionFragment + ' ' + tokenPortion;
			}
		}
		return partitionFragment;
	}

	/**
	 * We try to adapt the ITextViewer to our own base editor class and then from that grab the associated IProject if
	 * possible. When we can, we then grab the list of project natures and convert them into scope fragments of
	 * "meta.project.<nature.id>"
	 * 
	 * @param viewer
	 * @return
	 */
	private String prependNaturesToScope(ITextViewer viewer)
	{
		if (!(viewer instanceof IAdaptable))
		{
			return StringUtil.EMPTY;
		}

		IAdaptable adaptable = (IAdaptable) viewer;
		AbstractThemeableEditor editor = (AbstractThemeableEditor) adaptable.getAdapter(AbstractThemeableEditor.class);
		if (editor == null)
		{
			return StringUtil.EMPTY;
		}

		IProject project = EditorUtil.getProject(editor);
		if (project == null)
		{
			return StringUtil.EMPTY;
		}
		try
		{
			String[] natures = project.getDescription().getNatureIds();
			if (!ArrayUtil.isEmpty(natures))
			{
				return PROJECT_NATURE_SCOPE_PREFIX + StringUtil.join(" " + PROJECT_NATURE_SCOPE_PREFIX, natures) + ' '; //$NON-NLS-1$
			}
		}
		catch (CoreException e)
		{
			// ignore
		}
		return StringUtil.EMPTY;
	}

	private String getTokenScopeFragments(ITextViewer viewer, IDocument document, int offset)
	{
		if (!(viewer instanceof ISourceViewer))
		{
			return null;
		}

		try
		{
			// Force adding the category in case it doesn't exist yet...
			document.addPositionCategory(ICommonConstants.SCOPE_CATEGORY);
			Position[] scopes = document.getPositions(ICommonConstants.SCOPE_CATEGORY);
			int index = document.computeIndexInCategory(ICommonConstants.SCOPE_CATEGORY, offset);

			if (scopes == null || scopes.length == 0)
			{
				return null;
			}
			if (index >= scopes.length)
			{
				index = scopes.length - 1;
			}
			Position scope = scopes[index];
			if (scope == null)
			{
				return null;
			}
			if (!scope.includes(offset))
			{
				if (index > 0)
				{
					scope = scopes[--index];
					if (scope == null || !scope.includes(offset))
					{
						return null;
					}
				}
				else
				{
					return null;
				}
			}
			if (scope instanceof TypedPosition)
			{
				TypedPosition pos = (TypedPosition) scope;
				return pos.getType();
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
		}
		return null;
	}

	private String getPartitionScopeFragmentsAtOffset(IDocument document, int offset) throws BadLocationException
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
		try
		{
			return document.getContentType(offset);
		}
		catch (Exception e)
		{
			return UNKNOWN.toString();
		}
	}

	private IContentTypeTranslator getContentTypeTranslator()
	{
		return CommonEditorPlugin.getDefault().getContentTypeTranslator();
	}

	public void dispose()
	{
		infos.clear();
	}
}
