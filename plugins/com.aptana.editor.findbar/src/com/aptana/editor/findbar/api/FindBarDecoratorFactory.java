package com.aptana.editor.findbar.api;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.editor.findbar.impl.FindBarDecorator;

/**
 * This is a factory for FindBarDecorator.
 * <p>
 * The typical usage is as follows:
 * <pre>
 * public void createPartControl(Composite parent) {
 *     Composite findBarComposite = getfindBarDecorator().createFindBarComposite(parent);
 *     super.createPartControl(findBarComposite);
 *     getfindBarDecorator().createFindBar(getSourceViewer());
 * }
 * 
 * protected void createActions() {
 *     super.createActions();
 *     getfindBarDecorator().createActions();
 * }
 * 
 * private IFindBarDecorator findBarDecorator;
 * private IFindBarDecorator getfindBarDecorator() {
 * 	if (findBarDecorator == null) {
 * 		findBarDecorator = FindBarDecoratorFactory.createFindBarDecorator(this, getStatusLineManager());
 * 	}
 * 	return findBarDecorator;
 * }
 * </pre>
 * 
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
	 * @param statusLineManager The statusLineManager for the workbench window containing the textEditor
	 * @return the Find Bar decorator for the texteEditor
	 */
	public static IFindBarDecorator createFindBarDecorator(ITextEditor textEditor, IStatusLineManager statusLineManager) {
		return new FindBarDecorator(textEditor, statusLineManager);
	}
}
