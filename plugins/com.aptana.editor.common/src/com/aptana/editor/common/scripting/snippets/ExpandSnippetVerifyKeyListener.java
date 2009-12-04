package com.aptana.editor.common.scripting.snippets;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorExtension;
import org.eclipse.ui.texteditor.ITextEditorExtension2;

public class ExpandSnippetVerifyKeyListener implements VerifyKeyListener {

	private final ITextEditor textEditor;
	private ITextViewer textViewer;
	private IDocument document;
	private ContentAssistant contentAssistant;
	private boolean canModifyEditor;

	public ExpandSnippetVerifyKeyListener(ITextEditor textEditor) {
		this.textEditor = textEditor;
		this.canModifyEditor = canModifyEditor(textEditor); // Can we cache this value?
		Object adapter = textEditor.getAdapter(ITextOperationTarget.class);
		if (adapter instanceof ITextViewer) {
			this.textViewer = (ITextViewer) adapter;
		}
		document = textEditor.getDocumentProvider().getDocument(
				textEditor.getEditorInput());

		contentAssistant = new SnippetsContentAssistant();
		if (textViewer != null) {
			contentAssistant.install(textViewer);
		}
	}

	public void verifyKey(VerifyEvent event) {
		if (textViewer == null) {
			return;
		}
		if (document == null) {
			return;
		}

		if (canModifyEditor) {
			if (event.doit) {
				if (event.character == '\t') {
					ITextSelection selection = (ITextSelection) textEditor
					.getSelectionProvider().getSelection();
					if (selection.getLength() == 0) {
						int offset = selection.getOffset() - 1;
						try {
							String previousChar = document.get(offset, 1);
							if (previousChar.matches("\\S")) { //$NON-NLS-1$
								// TODO Check if there is at least one snippet
								if (contentAssistant != null) {
									contentAssistant.showPossibleCompletions();
									event.doit = false;									
								}
							}
						} catch (BadLocationException e) {
							return;
						}
					}
				}
			}
		}
	}
	
	private static boolean canModifyEditor(ITextEditor editor) {
		if (editor instanceof ITextEditorExtension2)
			return ((ITextEditorExtension2) editor).isEditorInputModifiable();
		else if (editor instanceof ITextEditorExtension)
			return !((ITextEditorExtension) editor).isEditorInputReadOnly();
		else if (editor != null)
			return editor.isEditable();
		else
			return false;
	}
}
