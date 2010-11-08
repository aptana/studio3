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
package com.aptana.editor.html;

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
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.outline.CommonOutlinePage;
import com.aptana.editor.common.parsing.FileService;
import com.aptana.editor.html.outline.HTMLOutlineContentProvider;
import com.aptana.editor.html.outline.HTMLOutlineLabelProvider;
import com.aptana.editor.html.parsing.HTMLParseState;
import com.aptana.editor.html.parsing.IHTMLParserConstants;
import com.aptana.editor.js.Activator;

public class HTMLEditor extends AbstractThemeableEditor
{
	private static final char[] HTML_PAIR_MATCHING_CHARS = new char[] { '(', ')', '{', '}', '[', ']', '`', '`', '\'',
			'\'', '"', '"', '<', '>', '\u201C', '\u201D', '\u2018', '\u2019' }; // curly double quotes, curly single

	private Map<Annotation, Position> fTagPairOccurrences;

	// quotes

	@Override
	protected void initializeEditor()
	{
		super.initializeEditor();

		setSourceViewerConfiguration(new HTMLSourceViewerConfiguration(getPreferenceStore(), this));
		setDocumentProvider(new HTMLDocumentProvider());
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
		OpenTagCloser.install(getSourceViewer());
	}

	@Override
	protected IPreferenceStore getOutlinePreferenceStore()
	{
		return Activator.getDefault().getPreferenceStore();
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
		IRegion match = OpenTagCloser.findMatchingTag(document, offset);
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
				Activator.logError(e.getMessage(), e);
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
