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

import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
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
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.IEditorLinkedResources;
import com.aptana.editor.common.outline.CommonOutlinePage;
import com.aptana.editor.common.text.reconciler.IFoldingComputer;
import com.aptana.editor.html.core.IHTMLConstants;
import com.aptana.editor.html.core.preferences.IPreferenceConstants;
import com.aptana.editor.html.outline.HTMLOutlineContentProvider;
import com.aptana.editor.html.outline.HTMLOutlineLabelProvider;
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
		tagPartitions.add(HTMLSourceConfiguration.HTML_TAG_CLOSE);
		tagPartitions.add(HTMLSourceConfiguration.HTML_SCRIPT);
		tagPartitions.add(HTMLSourceConfiguration.HTML_STYLE);
		tagPartitions.add(HTMLSourceConfiguration.HTML_SVG);
	}

	private IPreferenceChangeListener fPreferenceListener = new IPreferenceChangeListener()
	{

		public void preferenceChange(PreferenceChangeEvent event)
		{
			if (IPreferenceConstants.HTML_OUTLINE_TAG_ATTRIBUTES_TO_SHOW.equals(event.getKey()))
			{
				getOutlinePage().refresh();
			}
		}
	};

	@Override
	protected void initializeEditor()
	{
		super.initializeEditor();

		setPreferenceStore(getChainedPreferenceStore());
		setSourceViewerConfiguration(new HTMLSourceViewerConfiguration(getPreferenceStore(), this));
		setDocumentProvider(HTMLPlugin.getDefault().getHTMLDocumentProvider());
	}

	@Override
	public void dispose()
	{
		try
		{
			InstanceScope.INSTANCE.getNode(HTMLPlugin.PLUGIN_ID)
					.removePreferenceChangeListener(fPreferenceListener);
		}
		finally
		{
			super.dispose();
		}
	}

	public static IPreferenceStore getChainedPreferenceStore()
	{
		return new ChainedPreferenceStore(new IPreferenceStore[] { HTMLPlugin.getDefault().getPreferenceStore(),
				CommonEditorPlugin.getDefault().getPreferenceStore(), EditorsPlugin.getDefault().getPreferenceStore() });
	}

	@Override
	protected CommonOutlinePage createOutlinePage()
	{
		CommonOutlinePage outlinePage = super.createOutlinePage();
		InstanceScope.INSTANCE.getNode(HTMLPlugin.PLUGIN_ID).addPreferenceChangeListener(fPreferenceListener);

		return outlinePage;
	}

	@Override
	public String getContentType()
	{
		return IHTMLConstants.CONTENT_TYPE_HTML;
	}

	/**
	 * Return an array of character pairs used in our pair matching highlighter. Even number chars are the start, odd
	 * are the end.
	 * 
	 * @return
	 */
	public char[] getPairMatchingCharacters()
	{
		return HTML_PAIR_MATCHING_CHARS;
	}

	@Override
	public ITreeContentProvider getOutlineContentProvider()
	{
		return new HTMLOutlineContentProvider(this);
	}

	@Override
	public ILabelProvider getOutlineLabelProvider()
	{
		return new HTMLOutlineLabelProvider();
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

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.AbstractThemeableEditor#getPluginPreferenceStore()
	 */
	@Override
	protected IPreferenceStore getPluginPreferenceStore()
	{
		return HTMLPlugin.getDefault().getPreferenceStore();
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
				IdeLog.logError(HTMLPlugin.getDefault(), e);
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

	@Override
	public IFoldingComputer createFoldingComputer(IDocument document)
	{
		return new HTMLFoldingComputer(this, document);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.AbstractThemeableEditor#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter)
	{
		if (IEditorLinkedResources.class == adapter)
		{
			return new HTMLEditorLinkedResources(this);
		}
		return super.getAdapter(adapter);
	}
}
