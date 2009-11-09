package com.aptana.radrails.editor.common.actions;

import java.util.ResourceBundle;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

public class ShowScopesAction extends TextEditorAction {
	
	public static IAction create(ITextEditor textEditor) {
		return new ShowScopesAction(ResourceBundle.getBundle(ShowScopesAction.class.getName()), "ShowScopesAction.", textEditor);
	}
	
	public static final String COMMAND_ID = "com.aptana.radrails.editor.common.showscopes";

	public ShowScopesAction(ResourceBundle bundle, String prefix,
			ITextEditor editor) {
		super(bundle, prefix, editor);
		setActionDefinitionId(COMMAND_ID);
	}
	
	@Override
	public void run() {
        ITextEditor textEditor = getTextEditor();
        if (textEditor instanceof AbstractTextEditor) {
        	AbstractTextEditor abstractTextEditor = (AbstractTextEditor) textEditor;
        	Object adapter = abstractTextEditor.getAdapter(ITextOperationTarget.class);
			if (adapter instanceof ITextViewer) {
				ITextViewer textViewer = (ITextViewer) adapter;
				try {
					System.out.println(textViewer.getDocument().getContentType(textViewer.getTextWidget().getCaretOffset()));
				} catch (BadLocationException e) {
					System.err.println(e.getMessage());
				}
			}
        }
	}

}
