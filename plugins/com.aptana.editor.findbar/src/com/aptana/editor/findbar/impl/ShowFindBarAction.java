package com.aptana.editor.findbar.impl;

import java.util.ResourceBundle;

import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.texteditor.FindReplaceAction;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.editor.findbar.api.IFindBarDecorated;
import com.aptana.editor.findbar.api.IFindBarDecorator;

public class ShowFindBarAction extends FindReplaceAction {

	private final ITextEditor textEditor;

	public ShowFindBarAction(ITextEditor textEditor) {
		super(ResourceBundle.getBundle(ShowFindBarAction.class.getName()), ShowFindBarAction.class.getSimpleName()+".", textEditor); //$NON-NLS-1$
		this.textEditor = textEditor;
		setActionDefinitionId(ActionFactory.FIND.create(textEditor.getSite().getWorkbenchWindow()).getActionDefinitionId());
	}

	@Override
	public void run() {
		
		IFindBarDecorated findBarDecorated = (IFindBarDecorated) textEditor.getAdapter(IFindBarDecorated.class);
		if (findBarDecorated != null) {
			IFindBarDecorator findBarDecorator = findBarDecorated.getFindBarDecorator();
			if (((FindBarDecorator)findBarDecorator).isActive()) {
				super.run();
			} else {
				findBarDecorator.setVisible(true);
			}
		} else {
			super.run();
		}
	}
}
