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
package com.aptana.editor.ruby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.outline.CommonOutlineItem;
import com.aptana.editor.common.outline.CommonOutlinePage;
import com.aptana.editor.common.parsing.FileService;
import com.aptana.editor.ruby.core.IImportContainer;
import com.aptana.editor.ruby.core.IRubyElement;
import com.aptana.editor.ruby.core.IRubyField;
import com.aptana.editor.ruby.core.IRubyMethod;
import com.aptana.editor.ruby.core.IRubyType;
import com.aptana.editor.ruby.outline.RubyOutlineContentProvider;
import com.aptana.editor.ruby.outline.RubyOutlineLabelProvider;
import com.aptana.editor.ruby.parsing.IRubyParserConstants;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.lexer.IRange;

@SuppressWarnings("restriction")
public class RubySourceEditor extends AbstractThemeableEditor
{
	private static final char[] PAIR_MATCHING_CHARS = new char[] { '(', ')', '{', '}', '[', ']', '`', '`', '\'', '\'',
			'"', '"', '|', '|', '\u201C', '\u201D', '\u2018', '\u2019' }; // curly double quotes, curly single quotes
	private Map<Annotation, Position> fTagPairOccurrences;
	private boolean fIncludeBlocks;

	@Override
	protected void initializeEditor()
	{
		super.initializeEditor();

		setPreferenceStore(new ChainedPreferenceStore(new IPreferenceStore[] {
				RubyEditorPlugin.getDefault().getPreferenceStore(),
				CommonEditorPlugin.getDefault().getPreferenceStore(), EditorsPlugin.getDefault().getPreferenceStore() }));

		setSourceViewerConfiguration(new RubySourceViewerConfiguration(getPreferenceStore(), this));
		setDocumentProvider(new RubyDocumentProvider());
	}

	@Override
	protected FileService createFileService()
	{
		return new FileService(IRubyParserConstants.LANGUAGE);
	}

	protected char[] getPairMatchingCharacters()
	{
		return PAIR_MATCHING_CHARS;
	}

	@Override
	protected CommonOutlinePage createOutlinePage()
	{
		CommonOutlinePage outline = super.createOutlinePage();
		outline.setContentProvider(new RubyOutlineContentProvider());
		outline.setLabelProvider(new RubyOutlineLabelProvider());

		return outline;
	}

	@Override
	protected void setSelectedElement(IRange element)
	{
		if (element instanceof CommonOutlineItem)
		{
			IParseNode node = ((CommonOutlineItem) element).getReferenceNode();
			if (node instanceof IImportContainer)
			{
				// just sets the highlight range and moves the cursor
				setHighlightRange(element.getStartingOffset(), element.getLength(), true);
				return;
			}
		}
		super.setSelectedElement(element);
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
		updateOccurrences(textSelection);
	}

	@Override
	protected Object getOutlineElementAt(int caret)
	{
		fIncludeBlocks = false;
		Object obj = super.getOutlineElementAt(caret);
		fIncludeBlocks = true;
		return obj;
	}

	protected IParseNode getASTNodeAt(int offset)
	{
		IParseNode root = getFileService().getParseResult();
		if (root == null)
		{
			return null;
		}
		IParseNode node = root.getNodeAtOffset(offset);
		if (!fIncludeBlocks && node.getNodeType() == IRubyElement.BLOCK)
		{
			node = node.getParent();
		}
		return node;
	}

	private void updateOccurrences(ITextSelection textSelection)
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

		int offset = textSelection.getOffset();
		IParseNode currentNode = getASTNodeAt(offset);
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
		List<Position> positions = new ArrayList<Position>();
		if (currentNode instanceof IRubyType)
		{
			// Match "end" to "class/module ..."
			int endOffset = currentNode.getEndingOffset();
			int startOffset = currentNode.getStartingOffset();

			int length = 5;
			IRubyType type = (IRubyType) currentNode;
			if (type.isModule())
			{
				length = 6;
			}
			if ((offset <= endOffset && offset >= endOffset - 2)
					|| (offset >= startOffset && offset <= startOffset + length))
			{
				positions.add(new Position(startOffset, length));
				positions.add(new Position(endOffset - 2, 3));
			}
		}
		else if (currentNode instanceof IRubyMethod)
		{
			// Match "end" to "def ..."
			int endOffset = currentNode.getEndingOffset();
			int startOffset = currentNode.getStartingOffset();
			if ((offset <= endOffset && offset >= endOffset - 2)
					|| (offset >= startOffset && offset <= startOffset + 3))
			{
				positions.add(new Position(startOffset, 3));
				positions.add(new Position(endOffset - 2, 3));
			}
		}
		else if (currentNode.getNodeType() == IRubyElement.BLOCK)
		{
			// Match "end" to "do ..." only if it's a do/end block
			int endOffset = currentNode.getEndingOffset();
			IDocument document = getSourceViewer().getDocument();
			String endText = ""; //$NON-NLS-1$
			try
			{
				endText = document.get(endOffset, 1);
			}
			catch (BadLocationException e)
			{
				// ignore
			}
			if (endText.equals("d")) //$NON-NLS-1$
			{
				int startOffset = currentNode.getStartingOffset();
				if ((offset <= endOffset && offset >= endOffset - 2)
						|| (offset >= startOffset && offset <= startOffset + 3))
				{
					positions.add(new Position(startOffset, 2));
					positions.add(new Position(endOffset - 2, 3));
				}
			}
		}
		else if (currentNode instanceof IRubyField)
		{
			// TODO Find occurrences of variables!
		}
		// TODO Also match if/else/unless/begin/rescue/end blocks!

		if (!positions.isEmpty())
		{
			for (Position pos : positions)
			{
				occurrences.put(new Annotation(IRubyConstants.BLOCK_PAIR_OCCURRENCES_ID, false, null), pos);
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
