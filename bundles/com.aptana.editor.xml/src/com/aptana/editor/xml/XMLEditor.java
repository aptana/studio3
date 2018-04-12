/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.text.reconciler.IFoldingComputer;
import com.aptana.editor.xml.internal.text.XMLFoldingComputer;
import com.aptana.editor.xml.outline.XMLOutlineContentProvider;
import com.aptana.editor.xml.outline.XMLOutlineLabelProvider;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.xml.core.IXMLConstants;
import com.aptana.xml.core.parsing.ast.XMLElementNode;

@SuppressWarnings("restriction")
public class XMLEditor extends AbstractThemeableEditor
{

	private static final char[] XML_PAIR_MATCHING_CHARS = new char[] { '(', ')', '{', '}', '[', ']', '`', '`', '\'',
			'\'', '"', '"', '<', '>', '\u201C', '\u201D', '\u2018', '\u2019' }; // curly double quotes, curly single
	private Map<Annotation, Position> fTagPairOccurrences;
	private String currentSelectedElementId;
	private static Collection<String> tagPartitions = new ArrayList<String>();
	static
	{
		tagPartitions.add(XMLSourceConfiguration.TAG);
	}

	@Override
	protected void initializeEditor()
	{
		super.initializeEditor();

		setPreferenceStore(getChainedPreferenceStore());

		setSourceViewerConfiguration(new XMLSourceViewerConfiguration(getPreferenceStore(), this));
		setDocumentProvider(XMLPlugin.getDefault().getXMLDocumentProvider());
	}

	public static IPreferenceStore getChainedPreferenceStore()
	{
		return new ChainedPreferenceStore(new IPreferenceStore[] { XMLPlugin.getDefault().getPreferenceStore(),
				CommonEditorPlugin.getDefault().getPreferenceStore(), EditorsPlugin.getDefault().getPreferenceStore() });
	}

	@Override
	public void createPartControl(Composite parent)
	{
		super.createPartControl(parent);
		// Install a verify key listener that auto-closes unclosed open tags!
		installOpenTagCloser();
	}

	/**
	 * Install a tag closer to auto-close unclosed open tags.
	 */
	protected void installOpenTagCloser()
	{
		new OpenTagCloser(getSourceViewer()).install();
	}

	/**
	 * Return an array of character pairs used in our pair matching highlighter. Even number chars are the start, odd
	 * are the end.
	 * 
	 * @return
	 */
	public char[] getPairMatchingCharacters()
	{
		return XML_PAIR_MATCHING_CHARS;
	}

	@Override
	public ITreeContentProvider getOutlineContentProvider()
	{
		return new XMLOutlineContentProvider();
	}

	@Override
	public ILabelProvider getOutlineLabelProvider()
	{
		return new XMLOutlineLabelProvider();
	}

	@Override
	protected IPreferenceStore getOutlinePreferenceStore()
	{
		return XMLPlugin.getDefault().getPreferenceStore();
	}

	@Override
	public String getContentType()
	{
		try
		{
			IContentType contentType = ((TextFileDocumentProvider) getDocumentProvider())
					.getContentType(getEditorInput());
			if (contentType != null)
			{
				IContentType baseType = contentType.getBaseType();
				if (baseType != null && IXMLConstants.CONTENT_TYPE_XML.equals(baseType.getId()))
				{
					return contentType.getId();
				}
			}
		}
		catch (Exception e)
		{
		}
		return IXMLConstants.CONTENT_TYPE_XML;
	}

	@Override
	protected void selectionChanged()
	{
		super.selectionChanged();

		ISelection selection = getSelectionProvider().getSelection();
		if (selection.isEmpty())
		{
			return;
		}
		ITextSelection textSelection = (ITextSelection) selection;
		int offset = textSelection.getOffset();
		highlightTagPair(offset);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.AbstractThemeableEditor#getPluginPreferenceStore()
	 */
	@Override
	protected IPreferenceStore getPluginPreferenceStore()
	{
		return XMLPlugin.getDefault().getPreferenceStore();
	}

	/**
	 * Given the offset, tries to determine if we're on an HTML close/start tag, and if so it will find the matching
	 * open/close and highlight the pair.
	 * 
	 * @param offset
	 */
	@SuppressWarnings("deprecation")
	private void highlightTagPair(int offset)
	{
		IDocumentProvider documentProvider = getDocumentProvider();
		if (documentProvider == null)
		{
			return;
		}
		IAnnotationModel annotationModel = documentProvider.getAnnotationModel(getEditorInput());
		if (annotationModel == null)
		{
			return;
		}

		if (fTagPairOccurrences != null)
		{
			// if the offset is included by one of these two positions, we don't need to wipe and re-calculate!
			for (Position pos : fTagPairOccurrences.values())
			{
				if (pos.includes(offset))
				{
					return;
				}
			}
			// New position, wipe the existing annotations in preparation for re-calculating...
			for (Annotation a : fTagPairOccurrences.keySet())
			{
				annotationModel.removeAnnotation(a);
			}
			fTagPairOccurrences = null;
		}

		// Calculate current pair
		Map<Annotation, Position> occurrences = new HashMap<Annotation, Position>();
		IDocument document = getSourceViewer().getDocument();

		IParseNode node = getASTNodeAt(offset, getAST());
		if (node instanceof XMLElementNode)
		{
			XMLElementNode en = (XMLElementNode) node;
			if (!en.isSelfClosing())
			{
				IRegion match = TagUtil.findMatchingTag(document, offset, tagPartitions);
				if (match != null)
				{
					// TODO Compare versus last positions, if they're the same don't wipe out the old ones and add new
					// ones!
					occurrences.put(new Annotation(IXMLEditorConstants.TAG_PAIR_OCCURRENCE_ID, false, null),
							new Position(match.getOffset(), match.getLength()));

				}
			}
			handleOccurences(offset, annotationModel, occurrences, document);
			return;
		}
		// no new pair, so don't highlight anything
		fTagPairOccurrences = null;
	}

	private void handleOccurences(int offset, IAnnotationModel annotationModel, Map<Annotation, Position> occurrences,
			IDocument document)
	{
		try
		{
			ITypedRegion partition = document.getPartition(offset);
			String offsetString = document.get(offset, partition.getLength());

			int startIndex = offsetString.indexOf("id="); //$NON-NLS-1$
			int endIndex = offsetString.indexOf("\"", startIndex + 4); //$NON-NLS-1$
			if (startIndex != -1 && endIndex != -1)
			{
				currentSelectedElementId = offsetString.substring(startIndex + 4, endIndex);

				occurrences.put(new Annotation(IXMLEditorConstants.TAG_PAIR_OCCURRENCE_ID, false, null), new Position(
						partition.getOffset(), partition.getLength()));
			}
		}
		catch (Exception e)
		{
			IdeLog.logWarning(XMLPlugin.getDefault(), e);
		}
		for (Map.Entry<Annotation, Position> entry : occurrences.entrySet())
		{
			annotationModel.addAnnotation(entry.getKey(), entry.getValue());
		}
		fTagPairOccurrences = occurrences;
		return;
	}

	@Override
	public IFoldingComputer createFoldingComputer(IDocument document)
	{
		return new XMLFoldingComputer(this, document);
	}

	public String getCurrentSelectedElementId()
	{
		return currentSelectedElementId;
	}

	public void setSelection(int offset)
	{
		highlightTagPair(offset);
	}
}
