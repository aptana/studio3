/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.findbar.impl;

import java.lang.ref.WeakReference;
import java.util.regex.PatternSyntaxException;

import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.text.IFindReplaceTargetExtension;
import org.eclipse.jface.text.IFindReplaceTargetExtension3;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.core.util.ObjectUtil;
import com.aptana.core.util.StringUtil;

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
	private IRegion scope;
	private WeakReference<FindBarDecorator> findBarDecorator;

	public FindBarFinder(ITextEditor textEditor, ISourceViewer sourceViewer, FindBarDecorator findBarDecorator)
	{
		this.textEditor = textEditor;
		this.sourceViewer = sourceViewer;
		// Don't create cycles...
		this.findBarDecorator = new WeakReference<FindBarDecorator>(findBarDecorator);
	}

	/* default */boolean find(boolean forward)
	{
		return find(forward, false);
	}

	/* default */boolean find(boolean forward, boolean incremental)
	{
		return find(forward, incremental, true);
	}

	/* default */boolean find(boolean forward, boolean incremental, boolean wrap)
	{
		return find(forward, incremental, wrap, false);
	}

	/* default */boolean find(boolean forward, boolean incremental, boolean wrap, boolean wrapping)
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
				StyledText textWidget = sourceViewer.getTextWidget();
				Point selection = textWidget.getSelection();
				String originalText = dec.textFind.getText();
				String findText = dec.convertTextString(originalText);

				// If the find string was converted then it should be run as a regular expression
				boolean runRegEx = dec.getConfiguration().getRegularExpression()
						|| ObjectUtil.areNotEqual(originalText, findText);
				int offset = textWidget.getCaretOffset();
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
				if (findReplaceTarget instanceof IFindReplaceTargetExtension)
				{
					IFindReplaceTargetExtension findReplaceTargetExtension = (IFindReplaceTargetExtension) findReplaceTarget;

					// If a session was previously started, end it
					if (findReplaceTargetExtension.getScope() != null)
					{
						findReplaceTargetExtension.endSession();
					}
					findReplaceTargetExtension.beginSession();

					// Set the scope based on whether selection is enabled
					if (dec.searchSelection.getSelection())
					{
						enableScope(findReplaceTargetExtension);
					}
					else
					{
						findReplaceTargetExtension.setScope(null);
					}
				}
				if (findReplaceTarget instanceof IFindReplaceTargetExtension3)
				{
					try
					{
						// When searching backward, we have to get the offset-1 (otherwise it doesn't work properly)
						newOffset = ((IFindReplaceTargetExtension3) findReplaceTarget).findAndSelect(forward ? offset
								: offset - 1, findText, forward, dec.getConfiguration().getCaseSensitive(), dec
								.getWholeWord(), runRegEx);

					}
					catch (PatternSyntaxException e)
					{
						dec.statusLineManager.setMessage(true, e.getMessage(), null);
						return false;
					}
				}
				else
				{
					newOffset = findReplaceTarget.findAndSelect(offset, findText, forward, dec.getConfiguration()
							.getCaseSensitive(), dec.getWholeWord());
				}

				if (newOffset != -1)
				{
					dec.textFind.setBackground(null);
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
						dec.statusLineManager.setMessage(false, StringUtil.EMPTY, null);
					}
				}
				else
				{
					if (wrap)
					{
						if (!wrapping)
						{
							return find(forward, incremental, wrap, true);
						}
					}
					dec.textFind.setBackground(dec.getfStringNotFoundColor());
					dec.statusLineManager.setMessage(false, Messages.FindBarDecorator_MSG_StringNotFound, null);
				}
			}
			finally
			{
				if (findReplaceTarget instanceof IFindReplaceTargetExtension && !dec.searchSelection.getSelection())
				{
					((IFindReplaceTargetExtension) findReplaceTarget).endSession();
				}
			}
		}
		return true;
	}

	public void resetIncrementalOffset()
	{
		incrementalOffset = -1;
	}

	void resetScope()
	{
		scope = null;
		IFindReplaceTarget findReplaceTarget = (IFindReplaceTarget) textEditor.getAdapter(IFindReplaceTarget.class);
		if (findReplaceTarget instanceof IFindReplaceTargetExtension)
		{
			((IFindReplaceTargetExtension) findReplaceTarget).endSession();
		}
	}

	void enableScope(IFindReplaceTargetExtension findReplaceTargetExtension)
	{
		FindBarDecorator dec = findBarDecorator.get();
		if (dec == null)
		{
			return;
		}

		if (findReplaceTargetExtension == null)
		{
			IFindReplaceTarget findReplaceTarget = (IFindReplaceTarget) textEditor.getAdapter(IFindReplaceTarget.class);
			if (findReplaceTarget instanceof IFindReplaceTargetExtension)
			{
				findReplaceTargetExtension = (IFindReplaceTargetExtension) findReplaceTarget;
			}
		}

		if (findReplaceTargetExtension != null)
		{
			if (dec.searchSelection.getSelection())
			{
				if (scope == null)
				{
					Point lineSelection = findReplaceTargetExtension.getLineSelection();
					scope = new Region(lineSelection.x, lineSelection.y);
				}
				findReplaceTargetExtension.setSelection(sourceViewer.getTextWidget().getCaretOffset(), 0);
				if (findReplaceTargetExtension.getScope() != null)
				{
					findReplaceTargetExtension.setScope(null);
				}
				findReplaceTargetExtension.setScope(scope);
			}
		}
	}

	IRegion getScope()
	{
		return scope;
	}
}
