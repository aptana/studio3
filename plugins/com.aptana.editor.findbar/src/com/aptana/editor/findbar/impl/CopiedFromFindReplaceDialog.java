/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.findbar.impl;

import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.text.IFindReplaceTargetExtension;
import org.eclipse.jface.text.IFindReplaceTargetExtension3;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.texteditor.IEditorStatusLine;
import org.eclipse.ui.texteditor.IFindReplaceTargetExtension2;

/**
 * Copied to get the default replace actions. Unfortunately there's no API (and I believe there's little chance that an
 * API is made available for those).
 */
public class CopiedFromFindReplaceDialog
{

	private final IFindReplaceTarget fTarget;
	private final IEditorStatusLine fStatusLineManager;

	public CopiedFromFindReplaceDialog(IFindReplaceTarget target, IEditorStatusLine statusLineManager)
	{
		this.fTarget = target;
		this.fStatusLineManager = statusLineManager;

	}

	/**
	 * Replaces the selection with <code>replaceString</code>. If <code>regExReplace</code> is <code>true</code>,
	 * <code>replaceString</code> is a regex replace pattern which will get expanded if the underlying target supports
	 * it. Returns the region of the inserted text; note that the returned selection covers the expanded pattern in case
	 * of regex replace.
	 * 
	 * @param replaceString
	 *            the replace string (or a regex pattern)
	 * @param regExReplace
	 *            <code>true</code> if <code>replaceString</code> is a pattern
	 * @return the selection after replacing, i.e. the inserted text
	 * @since 3.0
	 */
	/* default */Point replaceSelection(String replaceString, boolean regExReplace)
	{
		if (fTarget instanceof IFindReplaceTargetExtension3)
			((IFindReplaceTargetExtension3) fTarget).replaceSelection(replaceString, regExReplace);
		else
			fTarget.replaceSelection(replaceString);

		return fTarget.getSelection();
	}

	/**
	 * Searches for a string starting at the given offset and using the specified search directives. If a string has
	 * been found it is selected and its start offset is returned.
	 * 
	 * @param offset
	 *            the offset at which searching starts
	 * @param findString
	 *            the string which should be found
	 * @param forwardSearch
	 *            the direction of the search
	 * @param caseSensitive
	 *            <code>true</code> performs a case sensitive search, <code>false</code> an insensitive search
	 * @param wholeWord
	 *            if <code>true</code> only occurrences are reported in which the findString stands as a word by itself
	 * @param regExSearch
	 *            if <code>true</code> findString represents a regular expression
	 * @return the position of the specified string, or -1 if the string has not been found
	 * @since 3.0
	 */
	/* default */int findAndSelect(int offset, String findString, boolean forwardSearch, boolean caseSensitive,
			boolean wholeWord, boolean regExSearch)
	{
		if (fTarget instanceof IFindReplaceTargetExtension3)
			return ((IFindReplaceTargetExtension3) fTarget).findAndSelect(offset, findString, forwardSearch,
					caseSensitive, wholeWord, regExSearch);
		return fTarget.findAndSelect(offset, findString, forwardSearch, caseSensitive, wholeWord);
	}

	/**
	 * Replaces all occurrences of the user's findString with the replace string. Returns the number of replacements
	 * that occur.
	 * 
	 * @param findString
	 *            the string to search for
	 * @param replaceString
	 *            the replacement string
	 * @param forwardSearch
	 *            the search direction
	 * @param caseSensitive
	 *            should the search be case sensitive
	 * @param wholeWord
	 *            does the search string represent a complete word
	 * @param regExSearch
	 *            if <code>true</code> findString represents a regular expression
	 * @return the number of occurrences
	 * @since 3.0
	 */
	/* default */int replaceAll(String findString, String replaceString, boolean forwardSearch, boolean caseSensitive,
			boolean wholeWord, boolean regExSearch)
	{

		int replaceCount = 0;
		int findReplacePosition = 0;

		findReplacePosition = 0;
		forwardSearch = true;

		if (!validateTargetState())
			return replaceCount;

		if (fTarget instanceof IFindReplaceTargetExtension)
			((IFindReplaceTargetExtension) fTarget).setReplaceAllMode(true);

		try
		{
			int index = 0;
			while (index != -1)
			{
				index = findAndSelect(findReplacePosition, findString, forwardSearch, caseSensitive, wholeWord,
						regExSearch);
				if (index != -1)
				{ // substring not contained from current position
					Point selection = replaceSelection(replaceString, regExSearch);
					replaceCount++;

					if (forwardSearch)
						findReplacePosition = selection.x + selection.y;
					else
					{
						findReplacePosition = selection.x - 1;
						if (findReplacePosition == -1)
							break;
					}
				}
			}
		}
		finally
		{
			if (fTarget instanceof IFindReplaceTargetExtension)
				((IFindReplaceTargetExtension) fTarget).setReplaceAllMode(false);
		}

		return replaceCount;
	}

	/**
	 * Validates the state of the find/replace target.
	 * 
	 * @return <code>true</code> if target can be changed, <code>false</code> otherwise
	 * @since 2.1
	 */
	private boolean validateTargetState()
	{

		if (fTarget instanceof IFindReplaceTargetExtension2)
		{
			IFindReplaceTargetExtension2 extension = (IFindReplaceTargetExtension2) fTarget;
			if (!extension.validateTargetState())
			{
				fStatusLineManager.setMessage(true, Messages.FindBarDecorator_MSG_ReadOnly, null);
				// updateButtonState(); -- fabioz: won't update button state
				return false;
			}
		}
		return isEditable();
	}

	/**
	 * Returns whether the target is editable.
	 * 
	 * @return <code>true</code> if target is editable
	 */
	private boolean isEditable()
	{
		boolean isEditable = (fTarget == null ? false : fTarget.isEditable());
		// fIsTargetEditable && isEditable -- fabioz: we don't really have the fIsTargetEditable.
		return isEditable;
	}
}
