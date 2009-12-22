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

import com.aptana.editor.common.DocumentContentTypeManager;
import com.aptana.editor.common.QualifiedContentType;
import com.aptana.editor.common.tmp.ContentTypeTranslation;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.TriggerOnlyFilter;

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
					ITextSelection selection = (ITextSelection) textEditor.getSelectionProvider().getSelection();
					if (selection.getLength() == 0) {
						int offset = selection.getOffset() - 1;
						try {
							String previousChar = document.get(offset, 1);
							if (!Character.isWhitespace(previousChar.charAt(0))) {
								int caretOffset = textViewer.getTextWidget().getCaretOffset();
								String contextTypeId = getContextType(document, caretOffset);
								boolean found = false;
								CommandElement[] commandsFromScope =
									BundleManager.getInstance().getCommandsFromScope(contextTypeId, new TriggerOnlyFilter());
								if (commandsFromScope.length > 0) {
									String prefix = SnippetsCompletionProcessor.extractPrefixFromDocument(document, caretOffset);
									for (CommandElement commandElement : commandsFromScope) {
										String trigger = commandElement.getTrigger();
										if (trigger != null && trigger.startsWith(prefix)) {
											found = true;
											break;
										}
									}
								}
								if (found) {
									if (contentAssistant != null) {
										contentAssistant.showPossibleCompletions();
										event.doit = false;
									}
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
	
	private static String getContextType(IDocument document, int offset) {
		String contentTypeString = ""; //$NON-NLS-1$
		try {
			contentTypeString = getContentTypeAtOffset(document, offset);
		} catch (BadLocationException e) {
			// TODO
		}
		return contentTypeString;
	}
	
	private static String getContentTypeAtOffset(IDocument document, int offset) throws BadLocationException {
		QualifiedContentType contentType = DocumentContentTypeManager.getInstance().getContentType(document, offset);
		if (contentType != null) {
			return ContentTypeTranslation.getDefault().translate(contentType).toString();
		}
		return document.getContentType(offset);
	}
}
