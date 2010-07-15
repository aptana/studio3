package com.aptana.editor.findbar.api;

import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.editor.findbar.impl.FindBarDecorator;

/**
 * This is a factory for FindBarDecorator.
 * <p>
 * 
 * @see FindBarEditorExtension for the typical usage
 * @see ITextEditor
 * 
 * @author schitale
 *
 */
public class FindBarDecoratorFactory {
	/**
	 * Returns the Find Bar decorator for the texteEditor.
	 *  
	 * @param textEditor The textEditor to which the Find Bar is added
	 * @return the Find Bar decorator for the texteEditor
	 */
	public static IFindBarDecorator createFindBarDecorator(ITextEditor textEditor) {
		return new FindBarDecorator(textEditor);
	}
}
