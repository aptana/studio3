/**
 * Appcelerator Titanium Studio
 * Copyright (c) 2014 by Appcelerator, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

package com.aptana.editor.iterators;

import java.text.BreakIterator;
import java.text.CharacterIterator;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.eclipse.ui.texteditor.TextNavigationAction;

/**
 * Represents a common base editor that needs to provide the functionality for camelCase selection of the words.
 * 
 * @author pinnamuri
 */
public abstract class AbstractBaseTextEditor extends AbstractDecoratedTextEditor
{

	/**
	 * Text navigation action to navigate to the next sub-word.
	 *
	 * @since 3.0
	 */
	protected abstract class NextSubWordAction extends TextNavigationAction
	{

		protected CamelCaseWordIterator fIterator = new CamelCaseWordIterator();
		private String prefKey;

		/**
		 * Creates a new next sub-word action.
		 *
		 * @param code
		 *            Action code for the default operation. Must be an action code from @see org.eclipse.swt.custom.ST.
		 * @param prefKey
		 */
		protected NextSubWordAction(int code, String prefKey)
		{
			super(getSourceViewer().getTextWidget(), code);
			this.prefKey = prefKey;
		}

		/*
		 * @see org.eclipse.jface.action.IAction#run()
		 */
		@Override
		public void run()
		{
			// Check whether we are in a java code partition and the preference is enabled
			final IPreferenceStore store = getPreferenceStore();
			if (!store.getBoolean(prefKey))
			{
				super.run();
				return;
			}

			final ISourceViewer viewer = getSourceViewer();
			final IDocument document = viewer.getDocument();
			try
			{
				fIterator.setText((CharacterIterator) new DocumentCharacterIterator(document));
				int position = widgetOffset2ModelOffset(viewer, viewer.getTextWidget().getCaretOffset());
				if (position == -1)
					return;

				int next = findNextPosition(position);
				if (isBlockSelectionModeEnabled()
						&& document.getLineOfOffset(next) != document.getLineOfOffset(position))
				{
					super.run(); // may navigate into virtual white space
				}
				else if (next != BreakIterator.DONE)
				{
					setCaretPosition(next);
					getTextWidget().showSelection();
					fireSelectionChanged();
				}
			}
			catch (Exception x)
			{
				// ignore
				super.run();
				return;
			}
		}

		/**
		 * Finds the next position after the given position.
		 *
		 * @param position
		 *            the current position
		 * @return the next position
		 */
		protected int findNextPosition(int position)
		{
			ISourceViewer viewer = getSourceViewer();
			int widget = -1;
			int next = position;
			while (next != BreakIterator.DONE && widget == -1)
			{ // XXX: optimize
				next = fIterator.following(next);
				if (next != BreakIterator.DONE)
					widget = modelOffset2WidgetOffset(viewer, next);
			}

			IDocument document = viewer.getDocument();
			LinkedModeModel model = LinkedModeModel.getModel(document, position);
			if (model != null && next != BreakIterator.DONE)
			{
				LinkedPosition linkedPosition = model.findPosition(new LinkedPosition(document, position, 0));
				if (linkedPosition != null)
				{
					int linkedPositionEnd = linkedPosition.getOffset() + linkedPosition.getLength();
					if (position != linkedPositionEnd && linkedPositionEnd < next)
						next = linkedPositionEnd;
				}
				else
				{
					LinkedPosition nextLinkedPosition = model.findPosition(new LinkedPosition(document, next, 0));
					if (nextLinkedPosition != null)
					{
						int nextLinkedPositionOffset = nextLinkedPosition.getOffset();
						if (position != nextLinkedPositionOffset && nextLinkedPositionOffset < next)
							next = nextLinkedPositionOffset;
					}
				}
			}
			return next;
		}

		/**
		 * Sets the caret position to the sub-word boundary given with <code>position</code>.
		 *
		 * @param position
		 *            Position where the action should move the caret
		 */
		protected abstract void setCaretPosition(int position);
	}

	/**
	 * Text operation action to select the next sub-word.
	 *
	 * @since 3.0
	 */
	protected class SelectNextSubWordAction extends NextSubWordAction
	{

		/**
		 * Creates a new select next sub-word action.
		 * 
		 * @param prefKey
		 */
		public SelectNextSubWordAction(String prefKey)
		{
			super(ST.SELECT_WORD_NEXT, prefKey);
		}

		/*
		 * @see org.eclipse.jdt.internal.ui.javaeditor.JavaEditor.NextSubWordAction#setCaretPosition(int)
		 */
		@Override
		protected void setCaretPosition(final int position)
		{
			final ISourceViewer viewer = getSourceViewer();

			final StyledText text = viewer.getTextWidget();
			if (text != null && !text.isDisposed())
			{

				final Point selection = text.getSelection();
				final int caret = text.getCaretOffset();
				final int offset = modelOffset2WidgetOffset(viewer, position);

				if (caret == selection.x)
					text.setSelectionRange(selection.y, offset - selection.y);
				else
					text.setSelectionRange(selection.x, offset - selection.x);
			}
		}
	}

	/**
	 * Text navigation action to navigate to the previous sub-word.
	 *
	 * @since 3.0
	 */
	protected abstract class PreviousSubWordAction extends TextNavigationAction
	{

		protected CamelCaseWordIterator fIterator = new CamelCaseWordIterator();
		private String prefKey;

		/**
		 * Creates a new previous sub-word action.
		 *
		 * @param code
		 *            Action code for the default operation. Must be an action code from @see org.eclipse.swt.custom.ST.
		 */
		protected PreviousSubWordAction(final int code, String prefKey)
		{
			super(getSourceViewer().getTextWidget(), code);
			this.prefKey = prefKey;
		}

		/*
		 * @see org.eclipse.jface.action.IAction#run()
		 */
		@Override
		public void run()
		{
			// Check whether we are in a java code partition and the preference is enabled
			final IPreferenceStore store = getPreferenceStore();
			if (!store.getBoolean(prefKey))
			{
				super.run();
				return;
			}

			final ISourceViewer viewer = getSourceViewer();
			final IDocument document = viewer.getDocument();
			try
			{
				fIterator.setText((CharacterIterator) new DocumentCharacterIterator(document));
				int position = widgetOffset2ModelOffset(viewer, viewer.getTextWidget().getCaretOffset());
				if (position == -1)
					return;

				int previous = findPreviousPosition(position);
				if (previous != BreakIterator.DONE)
				{
					setCaretPosition(previous);
					getTextWidget().showSelection();
					fireSelectionChanged();
				}
			}
			catch (Exception x)
			{
				// ignore - getLineOfOffset failed. Should we fall back to default behavior then ?
				super.run();
				return;
			}
		}

		/**
		 * Finds the previous position before the given position.
		 *
		 * @param position
		 *            the current position
		 * @return the previous position
		 */
		protected int findPreviousPosition(int position)
		{
			ISourceViewer viewer = getSourceViewer();
			int widget = -1;
			int previous = position;
			while (previous != BreakIterator.DONE && widget == -1)
			{ // XXX: optimize
				previous = fIterator.preceding(previous);
				if (previous != BreakIterator.DONE)
					widget = modelOffset2WidgetOffset(viewer, previous);
			}

			IDocument document = viewer.getDocument();
			LinkedModeModel model = LinkedModeModel.getModel(document, position);
			if (model != null && previous != BreakIterator.DONE)
			{
				LinkedPosition linkedPosition = model.findPosition(new LinkedPosition(document, position, 0));
				if (linkedPosition != null)
				{
					int linkedPositionOffset = linkedPosition.getOffset();
					if (position != linkedPositionOffset && previous < linkedPositionOffset)
						previous = linkedPositionOffset;
				}
				else
				{
					LinkedPosition previousLinkedPosition = model
							.findPosition(new LinkedPosition(document, previous, 0));
					if (previousLinkedPosition != null)
					{
						int previousLinkedPositionEnd = previousLinkedPosition.getOffset()
								+ previousLinkedPosition.getLength();
						if (position != previousLinkedPositionEnd && previous < previousLinkedPositionEnd)
							previous = previousLinkedPositionEnd;
					}
				}
			}
			return previous;
		}

		/**
		 * Sets the caret position to the sub-word boundary given with <code>position</code>.
		 *
		 * @param position
		 *            Position where the action should move the caret
		 */
		protected abstract void setCaretPosition(int position);
	}

	/**
	 * Text operation action to select the previous sub-word.
	 *
	 * @since 3.0
	 */
	protected class SelectPreviousSubWordAction extends PreviousSubWordAction
	{

		/**
		 * Creates a new select previous sub-word action.
		 * 
		 * @param prefKey
		 */
		public SelectPreviousSubWordAction(String prefKey)
		{
			super(ST.SELECT_WORD_PREVIOUS, prefKey);
		}

		/*
		 * @see org.eclipse.jdt.internal.ui.javaeditor.JavaEditor.PreviousSubWordAction#setCaretPosition(int)
		 */
		@Override
		protected void setCaretPosition(final int position)
		{
			final ISourceViewer viewer = getSourceViewer();

			final StyledText text = viewer.getTextWidget();
			if (text != null && !text.isDisposed())
			{

				final Point selection = text.getSelection();
				final int caret = text.getCaretOffset();
				final int offset = modelOffset2WidgetOffset(viewer, position);

				if (caret == selection.x)
					text.setSelectionRange(selection.y, offset - selection.y);
				else
					text.setSelectionRange(selection.x, offset - selection.x);
			}
		}
	}

}
