package com.aptana.radrails.editor.common.actions;

import java.util.ResourceBundle;

import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.texteditor.FindReplaceAction;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.editor.findbar.api.IFindBarDecorated;
import com.aptana.editor.findbar.api.IFindBarDecorator;

public class ShowFindBarAction extends FindReplaceAction {

	private final ITextEditor textEditor;

	public ShowFindBarAction(ITextEditor textEditor) {
		super(ResourceBundle.getBundle(ShowScopesAction.class.getName()), ShowFindBarAction.class.getSimpleName()+".", textEditor); //$NON-NLS-1$
		this.textEditor = textEditor;
		setActionDefinitionId(IWorkbenchCommandConstants.EDIT_FIND_AND_REPLACE);
	}

	@Override
	public void run() {
		
		IFindBarDecorated findBarDecorated = (IFindBarDecorated) textEditor.getAdapter(IFindBarDecorated.class);
		if (findBarDecorated != null) {
			IFindBarDecorator findBarDecorator = findBarDecorated.getFindBarDecorator();
			if (findBarDecorator.isVisible()) {
				super.run();
			} else {
				findBarDecorator.setVisible(true);
			}
		} else {
			super.run();
		}
	}
}
