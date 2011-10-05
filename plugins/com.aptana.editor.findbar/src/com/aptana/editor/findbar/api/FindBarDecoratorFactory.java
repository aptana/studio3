/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.findbar.api;

import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.editor.findbar.impl.FindBarDecorator;

/**
 * This is a factory for FindBarDecorator.
 * <p>
 * 
 * @see FindBarEditorExtension for the typical usage
 * @see ITextEditor
 * @author schitale
 */
public class FindBarDecoratorFactory
{
	/**
	 * Returns the Find Bar decorator for the texteEditor.
	 * 
	 * @param textEditor
	 *            The textEditor to which the Find Bar is added
	 * @return the Find Bar decorator for the texteEditor
	 */
	public static IFindBarDecorator createFindBarDecorator(ITextEditor textEditor)
	{
		return new FindBarDecorator(textEditor);
	}
}
