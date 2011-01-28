/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.outline.CommonOutlinePage;
import com.aptana.editor.common.parsing.FileService;
import com.aptana.editor.html.outline.HTMLOutlineContentProvider;
import com.aptana.editor.html.outline.HTMLOutlineLabelProvider;
import com.aptana.editor.html.parsing.HTMLParseState;
import com.aptana.editor.html.parsing.IHTMLParserConstants;
import com.aptana.editor.xml.TagUtil;

@SuppressWarnings("restriction")
public class HTMLEditor extends AbstractThemeableEditor
{
	private static final char[] HTML_PAIR_MATCHING_CHARS = new char[] { '(', ')', '{', '}', '[', ']', '`', '`', '\'',
			'\'', '"', '"', '<', '>', '\u201C', '\u201D', '\u2018', '\u2019' }; // curly double quotes, curly single
																				// quotes

	private Map<Annotation, Position> fTagPairOccurrences;

	private static Collection<String> tagPartitions = new ArrayList<String>();
	static
	{
		tagPartitions.add(HTMLSourceConfiguration.HTML_TAG);
		tagPartitions.add(HTMLSourceConfiguration.HTML_SCRIPT);
		tagPartitions.add(HTMLSourceConfiguration.HTML_STYLE);
		tagPartitions.add(HTMLSourceConfiguration.HTML_SVG);
	}

	@Override
	protected void initializeEditor()
	{
		super.initializeEditor();

		setPreferenceStore(getChainedPreferenceStore());
		setSourceViewerConfiguration(new HTMLSourceViewerConfiguration(getPreferenceStore(), this));
		setDocumentProvider(new HTMLDocumentProvider());
	}

	public static IPreferenceStore getChainedPreferenceStore()
	{
		return new ChainedPreferenceStore(new IPreferenceStore[] { HTMLPlugin.getDefault().getPreferenceStore(),
				CommonEditorPlugin.getDefault().getPreferenceStore(), EditorsPlugin.getDefault().getPreferenceStore() });
	}

	@Override
	protected FileService createFileService()
	{
		return new FileService(IHTMLParserConstants.LANGUAGE, new HTMLParseState());
	}

	/**
	 * Return an array of character pairs used in our pair matching highlighter. Even number chars are the start, odd
	 * are the end.
	 * 
	 * @return
	 */
	protected char[] getPairMatchingCharacters()
	{
		return HTML_PAIR_MATCHING_CHARS;
	}

	@Override
	protected CommonOutlinePage createOutlinePage()
	{
		CommonOutlinePage outline = super.createOutlinePage();
		outline.setContentProvider(new HTMLOutlineContentProvider());
		outline.setLabelProvider(new HTMLOutlineLabelProvider());

		return outline;
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
		new HTMLOpenTagCloser(getSourceViewer()).install();
	}

	@Override
	protected IPreferenceStore getOutlinePreferenceStore()
	{
		return HTMLPlugin.getDefault().getPreferenceStore();
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

	/**
	 * Given the offset, tries to determine if we're on an HTML close/start tag, and if so it will find the matching
	 * open/close and highlight the pair.
	 * 
	 * @param offset
	 */
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
		IRegion match = TagUtil.findMatchingTag(document, offset, tagPartitions);
		if (match != null)
		{
			// TODO Compare versus last positions, if they're the same don't wipe out the old ones and add new ones!
			occurrences.put(new Annotation(IHTMLConstants.TAG_PAIR_OCCURRENCE_ID, false, null),
					new Position(match.getOffset(), match.getLength()));

			try
			{
				// The current tag we're in!
				ITypedRegion partition = document.getPartition(offset);
				occurrences.put(new Annotation(IHTMLConstants.TAG_PAIR_OCCURRENCE_ID, false, null), new Position(
						partition.getOffset(), partition.getLength()));
			}
			catch (BadLocationException e)
			{
				HTMLPlugin.logError(e.getMessage(), e);
			}
			for (Map.Entry<Annotation, Position> entry : occurrences.entrySet())
			{
				annotationModel.addAnnotation(entry.getKey(), entry.getValue());
			}
			fTagPairOccurrences = occurrences;
		}
		else
		{
			// no new pair, so don't highlight anything
			fTagPairOccurrences = null;
		}
	}
}
