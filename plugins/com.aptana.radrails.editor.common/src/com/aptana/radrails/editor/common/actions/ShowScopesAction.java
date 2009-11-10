package com.aptana.radrails.editor.common.actions;

import java.util.ResourceBundle;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextSelection;
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
        try {
        	ITextSelection textSelection = (ITextSelection) textEditor.getSelectionProvider().getSelection();
        	// Assume a forward selection i.e. offset+length == caret position.
			System.out.println(textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput()).getContentType(textSelection.getOffset()+textSelection.getLength()));
        } catch (BadLocationException e) {
        	System.err.println(e.getMessage());
        }
	}

}
