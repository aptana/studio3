/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.findbar.impl;

import java.lang.ref.WeakReference;
import java.util.regex.PatternSyntaxException;

import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.text.IFindReplaceTargetExtension;
import org.eclipse.jface.text.IFindReplaceTargetExtension3;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Helper for actually doing the find actions.
 * 
 * @author Fabio Zadrozny
 */
public class FindBarFinder
{
	private ITextEditor textEditor;
	private int incrementalOffset = -1;
	private ISourceViewer sourceViewer;
	private WeakReference<FindBarDecorator> findBarDecorator;

	public FindBarFinder(ITextEditor textEditor, ISourceViewer sourceViewer, FindBarDecorator findBarDecorator)
	{
		this.textEditor = textEditor;
		this.sourceViewer = sourceViewer;
		//Don't create cycles...
		this.findBarDecorator = new WeakReference<FindBarDecorator>(findBarDecorator);
	}

	/* default */void find(boolean forward)
	{
		find(forward, false);
	}

	/* default */void find(boolean forward, boolean incremental)
	{
		find(forward, incremental, true);
	}

	/* default */void find(boolean forward, boolean incremental, boolean wrap)
	{
		find(forward, incremental, wrap, false, false);
	}

	/* default */boolean find(boolean forward, boolean incremental, boolean wrap, boolean wrapping,
			boolean initialSearchBeforeReplace)
	{
		FindBarDecorator dec = findBarDecorator.get();
		if (dec == null)
		{
			return false;
		}
		IFindReplaceTarget findReplaceTarget = (IFindReplaceTarget) textEditor.getAdapter(IFindReplaceTarget.class);
		if (findReplaceTarget != null)
		{
			try
			{
				if (findReplaceTarget instanceof IFindReplaceTargetExtension)
				{
					IFindReplaceTargetExtension findReplaceTargetExtension = (IFindReplaceTargetExtension) findReplaceTarget;
					findReplaceTargetExtension.beginSession();
				}
				String findText = dec.combo.getText();
				StyledText textWidget = sourceViewer.getTextWidget();
				int offset = textWidget.getCaretOffset();
				Point selection = textWidget.getSelection();
				if (wrapping)
				{
					if (forward)
					{
						offset = 0;
					}
					else
					{
						offset = sourceViewer.getDocument().getLength() - 1;
					}
				}
				else
				{
					if (forward)
					{
						if (incremental)
						{
							if (incrementalOffset == -1)
							{
								incrementalOffset = offset;
							}
							else
							{
								offset = incrementalOffset;
							}
						}
						else
						{
							incrementalOffset = selection.x;
						}
					}
					else
					{
						incrementalOffset = selection.x;
						if (selection.x != offset)
						{
							offset = selection.x;
						}
					}
				}
				int newOffset = -1;
				if (initialSearchBeforeReplace)
				{
					String selectionText = textWidget.getSelectionText();
					if (selectionText.equals(dec.combo.getText()))
					{
						offset -= (selection.y - selection.x);
					}
					else
					{
						return false;
					}
				}
				if (findReplaceTarget instanceof IFindReplaceTargetExtension3)
				{
					try
					{
						// When searching backward, we have to get the offset-1 (otherwise it doesn't work properly)
						newOffset = ((IFindReplaceTargetExtension3) findReplaceTarget).findAndSelect(forward ? offset
								: offset - 1, findText, forward, dec.getConfiguration().getCaseSensitive(), dec.getWholeWord(),
								dec.getConfiguration().getRegularExpression());
					}
					catch (PatternSyntaxException e)
					{
						dec.statusLineManager.setMessage(true, e.getMessage(), null);
						return false;
					}
				}
				else
				{
					newOffset = findReplaceTarget.findAndSelect(offset, findText, forward,
							dec.getConfiguration().getCaseSensitive(), dec.getWholeWord());
				}

				if (newOffset != -1)
				{
					dec.combo.setBackground(null);
					if (!forward)
					{
						selection = textWidget.getSelection();
						incrementalOffset = selection.x;
					}
					if (wrapping)
					{
						dec.statusLineManager.setMessage(false, Messages.FindBarDecorator_MSG_Wrapped, null);
					}
					else
					{
						dec.statusLineManager.setMessage(false, "", null);
					}
				}
				else
				{
					if (wrap)
					{
						if (!wrapping)
						{
							return find(forward, incremental, wrap, true, initialSearchBeforeReplace);
						}
					}
					dec.combo.setBackground(dec.getfStringNotFoundColor());
					dec.statusLineManager.setMessage(false, Messages.FindBarDecorator_MSG_StringNotFound, null);
				}
			}
			finally
			{
				if (findReplaceTarget instanceof IFindReplaceTargetExtension)
				{
					IFindReplaceTargetExtension findReplaceTargetExtension = (IFindReplaceTargetExtension) findReplaceTarget;
					findReplaceTargetExtension.endSession();
				}
			}
		}
		return true;
	}

	public void resetIncrementalOffset()
	{
		incrementalOffset = -1;

	}

}
