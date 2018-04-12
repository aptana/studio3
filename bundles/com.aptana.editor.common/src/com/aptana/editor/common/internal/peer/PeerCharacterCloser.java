/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.internal.peer;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.scope.IScopeSelector;
import com.aptana.scope.ScopeSelector;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.SmartTypingPairsElement;
import com.aptana.scripting.model.filters.ScopeFilter;

/**
 * A class that can be installed on a ITextViewer and will auto-insert the closing peer character for typical paired
 * characters like (), [], {}, '', "", ``, <>. This class will wrap selected text in the pair if there's a selected
 * length > 0. If the length is 0, it will only insert the pair if the following character is not a closing char. When
 * inserting the pair the editor will enter linked mode, so that if user types close character manually they will just
 * "overwrite" the smart close char we inserted.
 * 
 * @author cwilliams
 */
public class PeerCharacterCloser implements VerifyKeyListener, ILinkedModeListener
{

	private ITextViewer textViewer;
	private final String CATEGORY = toString();
	private IPositionUpdater fUpdater = new ExclusivePositionUpdater(CATEGORY);
	private Stack<BracketLevel> fBracketLevelStack = new Stack<BracketLevel>();
	private List<Character> pairs = Collections.emptyList();
	private boolean autoInsertEnabled = true;
	private boolean autoWrapEnabled = true;

	public PeerCharacterCloser(ITextViewer textViewer)
	{
		this.textViewer = textViewer;
	}

	public void install()
	{
		textViewer.getTextWidget().addVerifyKeyListener(this);
	}

	/**
	 * @see org.eclipse.swt.custom.VerifyKeyListener#verifyKey(org.eclipse.swt.events.VerifyEvent)
	 */
	public void verifyKey(VerifyEvent event)
	{
		// early pruning to slow down normal typing as little as possible
		if (!event.doit || !isAutoInsertEnabled() || isModifierKey(event.keyCode))
		{
			// TODO prune more aggressively on keys that fall outside the superset of all pairs to help increase
			// performance!
			return;
		}

		IDocument document = textViewer.getDocument();
		final Point selection = textViewer.getSelectedRange();
		final int offset = selection.x;
		final int length = selection.y;

		try
		{
			String scope = getScopeAtOffset(document, offset);
			this.pairs = getPairs(scope);
			if (this.pairs == null || this.pairs.size() <= 0 || !isAutoInsertCharacter(event.character))
			{
				return;
			}

			if (length > 0 && isAutoWrapEnabled())
			{
				wrapSelection(event, document, offset, length);
				return;
			}

			// Don't auto-close if next char is a letter or digit
			if (document.getLength() > offset)
			{
				char nextChar = document.getChar(offset);
				if (Character.isJavaIdentifierPart(nextChar))
				{
					return;
				}
			}

			// Don't auto-close if we have an open pair!
			if (isUnclosedPair(event, document, offset)) // We have an open string or pair, just insert the single
															// character, don't do anything special
			{
				return;
			}

			final char closingCharacter = getPeerCharacter(event.character);
			// If this is the start char and there's no unmatched close char, insert the close char
			if (unpairedClose(event.character, closingCharacter, document, offset))
			{
				return;
			}

			final StringBuffer buffer = new StringBuffer();
			buffer.append(event.character);
			buffer.append(closingCharacter);
			if (offset == document.getLength())
			{
				String delim = null;
				if (document instanceof IDocumentExtension4)
				{
					delim = ((IDocumentExtension4) document).getDefaultLineDelimiter();
				}
				if (delim == null)
				{
					delim = System.getProperty("line.separator", "\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				buffer.append(delim);
			}

			document.replace(offset, length, buffer.toString());

			BracketLevel level = new BracketLevel();
			fBracketLevelStack.push(level);

			LinkedPositionGroup group = new LinkedPositionGroup();
			group.addPosition(new LinkedPosition(document, offset + 1, 0, LinkedPositionGroup.NO_STOP));

			LinkedModeModel model = new LinkedModeModel();
			model.addLinkingListener(this);
			model.addGroup(group);
			model.forceInstall();

			// set up position tracking for our magic peers
			if (fBracketLevelStack.size() == 1)
			{
				document.addPositionCategory(CATEGORY);
				document.addPositionUpdater(fUpdater);
			}
			level.fFirstPosition = new Position(offset, 1);
			level.fSecondPosition = new Position(offset + 1, 1);
			document.addPosition(CATEGORY, level.fFirstPosition);
			document.addPosition(CATEGORY, level.fSecondPosition);

			level.fUI = new EditorLinkedModeUI(model, textViewer);
			level.fUI.setSimpleMode(true);
			level.fUI.setExitPolicy(new ExitPolicy(textViewer, closingCharacter, getEscapeCharacter(closingCharacter),
					fBracketLevelStack));
			level.fUI.setExitPosition(textViewer, offset + 2, 0, Integer.MAX_VALUE);
			level.fUI.setCyclingMode(LinkedModeUI.CYCLE_NEVER);
			level.fUI.enter();

			IRegion newSelection = level.fUI.getSelectedRegion();
			textViewer.setSelectedRange(newSelection.getOffset(), newSelection.getLength());

			event.doit = false;
		}
		catch (BadLocationException e)
		{
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
		}
		catch (BadPositionCategoryException e)
		{
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
		}
	}

	private boolean isModifierKey(int keyCode)
	{
		// TODO Add more non alphanumeric keys we should skip!
		switch (keyCode)
		{
			case SWT.SHIFT:
			case SWT.BS:
			case SWT.CR:
			case SWT.DEL:
			case SWT.ESC:
			case SWT.LF:
			case SWT.TAB:
			case SWT.CTRL:
			case SWT.COMMAND:
			case SWT.ALT:
			case SWT.ARROW_DOWN:
			case SWT.ARROW_LEFT:
			case SWT.ARROW_RIGHT:
			case SWT.ARROW_UP:
				return true;
		}
		return false;
	}

	protected List<Character> getPairs(String scope)
	{
		ScopeFilter filter = new ScopeFilter(scope);
		List<SmartTypingPairsElement> pairs = BundleManager.getInstance().getPairs(filter);
		if (pairs == null || pairs.isEmpty())
		{
			return Collections.emptyList();
		}
		Map<IScopeSelector, SmartTypingPairsElement> map = new HashMap<IScopeSelector, SmartTypingPairsElement>();
		for (SmartTypingPairsElement pe : pairs)
		{
			IScopeSelector ss = pe.getScopeSelector();
			if (ss == null)
			{
				continue;
			}
			map.put(ss, pe);
		}
		IScopeSelector bestMatch = ScopeSelector.bestMatch(map.keySet(), scope);
		SmartTypingPairsElement yay = map.get(bestMatch);
		if (yay == null)
		{
			return Collections.emptyList();
		}
		return yay.getPairs();
	}

	protected String getScopeAtOffset(IDocument document, final int offset) throws BadLocationException
	{
		if (textViewer == null)
		{
			return CommonEditorPlugin.getDefault().getDocumentScopeManager().getScopeAtOffset(document, offset);
		}
		return CommonEditorPlugin.getDefault().getDocumentScopeManager().getScopeAtOffset(textViewer, offset);
	}

	boolean unpairedClose(char openingChar, char closingCharacter, IDocument document, int offset)
	{
		try
		{
			String partition = document.getContentType(offset);
			int index = partition.indexOf('_', 2);
			String prefix = partition.substring(0, index);

			// Iterate through partitions sharing same prefix, which is a hacky way of doing "same language"
			int stackLevel = 0;
			ITypedRegion[] partitions = computePartitioning(document, 0, document.getLength());
			for (ITypedRegion part : partitions)
			{
				// HACK We skip comment or string partitions here. We rely on naming convention for partitions to do so!
				if (part.getType().contains("_comment") || part.getType().contains("_string") //$NON-NLS-1$ //$NON-NLS-2$
						|| !part.getType().startsWith(prefix))
				{
					continue;
				}

				int start = part.getOffset();
				int end = start + part.getLength();
				if (offset > start)
				{
					int beforeEnd = end;
					// read up until offset
					if (offset < end)
					{
						beforeEnd = offset;
					}
					String before = document.get(start, beforeEnd - start);
					if (before.trim().length() != 0) // skip whitespace only partitions for perf reasons
					{
						for (int i = 0; i < before.length(); i++)
						{

							char c = before.charAt(i);
							if (c == openingChar && openingChar == closingCharacter)
							{
								stackLevel++;
								stackLevel = stackLevel % 2;
							}
							else if (c == openingChar)
							{
								stackLevel++;
							}
							else if (c == closingCharacter)
							{
								stackLevel--;
							}
						}
					}
				}
				if (offset < end)
				{
					int startAfter = start;
					if (offset > start)
					{
						startAfter = offset;
					}
					String after = document.get(startAfter, end - startAfter);

					// offsets to match for comment scope
					// matching
					for (int i = 0; i < after.length(); i++)
					{
						char c = after.charAt(i);
						if (c == openingChar && openingChar == closingCharacter)
						{
							stackLevel++;
							stackLevel = stackLevel % 2;
						}
						else if (c == openingChar)
						{
							stackLevel++;
						}
						else if (c == closingCharacter)
						{
							stackLevel--;
							if (stackLevel < 0)
								return true;
						}
					}
				}
			}
			return stackLevel != 0;
		}
		catch (Exception e)
		{
			IdeLog.logWarning(CommonEditorPlugin.getDefault(), MessageFormat.format(
					"Failed to determine if we have an unclosed pair. Open: ''{0}'', close: ''{1}'', offset: {2}", //$NON-NLS-1$
					openingChar, closingCharacter, offset), e);
		}
		return false;
	}

	protected ITypedRegion[] computePartitioning(IDocument document, int offset, int length)
			throws BadLocationException
	{
		return document.computePartitioning(offset, length);
	}

	private boolean isUnclosedPair(VerifyEvent event, IDocument document, int offset) throws BadLocationException
	{
		final char closingCharacter = getPeerCharacter(event.character);
		// This doesn't matter if the user is not typing an "end" character.
		if (closingCharacter != event.character)
			return false;
		char c = event.character;
		int beginning = 0;
		// Don't check from very beginning of the document! Be smarter/quicker and check from beginning of
		// partition if we can.
		// FIXME What type of partitions does this make sense for? We should check across "code" partitions. Limit to
		// single string/comment partition?
		if (document instanceof IDocumentExtension3)
		{
			try
			{
				IDocumentExtension3 ext = (IDocumentExtension3) document;
				ITypedRegion region = getPartition(ext, IDocumentExtension3.DEFAULT_PARTITIONING, offset, false);
				beginning = region.getOffset();
			}
			catch (BadPartitioningException e)
			{
				// ignore
			}
		}
		// Now check leading source and see if we're an unclosed pair.
		String previous = document.get(beginning, offset - beginning);
		boolean open = false;
		int index = -1;
		while ((index = previous.indexOf(c, index + 1)) != -1)
		{
			// if (ignoreScope(document, beginning + index))
			// continue;
			open = !open;
			if (open)
			{
				c = closingCharacter;
			}
			else
			{
				c = event.character;
			}
		}
		return open;
	}

	protected ITypedRegion getPartition(IDocumentExtension3 ext, String defaultPartitioning, int offset, boolean b)
			throws BadLocationException, BadPartitioningException
	{
		return ext.getPartition(defaultPartitioning, offset, b);
	}

	/**
	 * Return the character which escapes the closing character passed in. '\' for string chars ('"', ''')
	 * 
	 * @param character
	 * @return
	 */
	private static char getEscapeCharacter(char character)
	{
		switch (character)
		{
			case '"':
			case '\'':
				return '\\';
			default:
				return 0;
		}
	}

	/**
	 * Wraps the selected text in the smart pair.
	 * 
	 * @param event
	 * @param document
	 * @param offset
	 * @param length
	 * @throws BadLocationException
	 */
	private void wrapSelection(VerifyEvent event, IDocument document, final int offset, final int length)
			throws BadLocationException
	{
		final char closingCharacter = getPeerCharacter(event.character);
		final StringBuffer buffer = new StringBuffer();
		buffer.append(event.character);
		buffer.append(document.get(offset, length));
		buffer.append(closingCharacter);
		document.replace(offset, length, buffer.toString());
		event.doit = false;
	}

	/**
	 * Return the closing character of the pair.
	 * 
	 * @param character
	 * @return
	 */
	private char getPeerCharacter(char character)
	{
		for (int i = 0; i < pairs.size(); i += 2)
		{
			if (pairs.get(i).charValue() == character)
			{
				return pairs.get(i + 1);
			}
		}
		return character;
	}

	/**
	 * Is the character typed one of the ones we do smart pairing of?
	 * 
	 * @param character
	 * @return
	 */
	private boolean isAutoInsertCharacter(char character)
	{
		for (int i = 0; i < pairs.size(); i += 2)
		{
			if (pairs.get(i).charValue() == character)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Do we automatically insert matching characters?
	 */
	public boolean isAutoInsertEnabled()
	{
		return autoInsertEnabled;
	}

	/**
	 * Set the automatic insertion of matching characters on or off
	 * 
	 * @param autoInsertEnabled
	 */
	public void setAutoInsertEnabled(boolean autoInsertEnabled)
	{
		this.autoInsertEnabled = autoInsertEnabled;
	}

	/**
	 * Do we automatically wrap selected text?
	 */
	public boolean isAutoWrapEnabled()
	{
		return autoWrapEnabled;
	}

	/**
	 * Set the automatic wrapping of selected text on or off
	 * 
	 * @param autoWrapEnabled
	 */
	public void setAutoWrapEnabled(boolean autoWrapEnabled)
	{
		this.autoWrapEnabled = autoWrapEnabled;
	}

	/**
	 * Simple class to hold linked mode and two positions.
	 * 
	 * @author cwilliams
	 */
	static class BracketLevel
	{
		LinkedModeUI fUI;
		Position fFirstPosition;
		Position fSecondPosition;
	}

	/**
	 * Position updater that takes any changes at the borders of a position to not belong to the position.
	 */
	private static class ExclusivePositionUpdater implements IPositionUpdater
	{

		/** The position category. */
		private final String fCategory;

		/**
		 * Creates a new updater for the given <code>category</code>.
		 * 
		 * @param category
		 *            the new category.
		 */
		public ExclusivePositionUpdater(String category)
		{
			fCategory = category;
		}

		/*
		 * @see org.eclipse.jface.text.IPositionUpdater#update(org.eclipse.jface.text.DocumentEvent)
		 */
		public void update(DocumentEvent event)
		{

			int eventOffset = event.getOffset();
			int eventOldLength = event.getLength();
			int eventNewLength = (event.getText() == null) ? 0 : event.getText().length();
			int deltaLength = eventNewLength - eventOldLength;

			try
			{
				Position[] positions = event.getDocument().getPositions(fCategory);

				for (int i = 0; i != positions.length; i++)
				{

					Position position = positions[i];

					if (position.isDeleted())
						continue;

					int offset = position.getOffset();
					int length = position.getLength();
					int end = offset + length;

					if (offset >= eventOffset + eventOldLength)
					{
						// position comes
						// after change - shift
						position.setOffset(offset + deltaLength);
					}
					else if (end <= eventOffset)
					{
						// position comes way before change -
						// leave alone
						continue;
					}
					else if (offset <= eventOffset && end >= eventOffset + eventOldLength)
					{
						// event completely internal to the position - adjust
						// length
						position.setLength(length + deltaLength);
					}
					else if (offset < eventOffset)
					{
						// event extends over end of position - adjust length
						int newEnd = eventOffset;
						position.setLength(newEnd - offset);
					}
					else if (end > eventOffset + eventOldLength)
					{
						// event extends from before position into it - adjust
						// offset
						// and length
						// offset becomes end of event, length ajusted
						// acordingly
						int newOffset = eventOffset + eventNewLength;
						position.setOffset(newOffset);
						position.setLength(end - newOffset);
					}
					else
					{
						// event consumes the position - delete it
						position.delete();
					}
				}
			}
			catch (BadPositionCategoryException e)
			{
				// ignore and return
			}
		}
	}

	/*
	 * @see org.eclipse.jface.text.link.ILinkedModeListener#left(org.eclipse.jface.text.link.LinkedModeModel, int)
	 */
	public void left(LinkedModeModel environment, int flags)
	{
		final BracketLevel level = fBracketLevelStack.pop();

		if (flags != ILinkedModeListener.EXTERNAL_MODIFICATION)
		{
			return;
		}

		// remove brackets
		final IDocument document = textViewer.getDocument();
		if (document instanceof IDocumentExtension)
		{
			IDocumentExtension extension = (IDocumentExtension) document;
			extension.registerPostNotificationReplace(null, new IDocumentExtension.IReplace()
			{

				public void perform(IDocument d, IDocumentListener owner)
				{
					if ((level.fFirstPosition.isDeleted || level.fFirstPosition.length == 0)
							&& !level.fSecondPosition.isDeleted
							&& level.fSecondPosition.offset == level.fFirstPosition.offset)
					{
						try
						{
							document.replace(level.fSecondPosition.offset, level.fSecondPosition.length, null);
						}
						catch (BadLocationException e)
						{
							IdeLog.logError(CommonEditorPlugin.getDefault(), e);
						}
					}

					if (fBracketLevelStack.size() == 0)
					{
						document.removePositionUpdater(fUpdater);
						try
						{
							document.removePositionCategory(CATEGORY);
						}
						catch (BadPositionCategoryException e)
						{
							IdeLog.logError(CommonEditorPlugin.getDefault(), e);
						}
					}
				}
			});
		}
	}

	/*
	 * @see org.eclipse.jface.text.link.ILinkedModeListener#suspend(org.eclipse.jface.text.link.LinkedModeModel)
	 */
	public void suspend(LinkedModeModel environment)
	{
	}

	/*
	 * @see org.eclipse.jface.text.link.ILinkedModeListener#resume(org.eclipse.jface.text.link.LinkedModeModel, int)
	 */
	public void resume(LinkedModeModel environment, int flags)
	{
	}
}
