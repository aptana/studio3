package tabsnippetexpansion;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.AbstractTextEditor;

public class ExpandSnippet extends AbstractHandler {
	private ContentAssistant contentAssistant;

	ContentAssistant getContentAssistant() {
		if (contentAssistant == null) {
			contentAssistant = new SnippetsContentAssistant(this);
		}
		return contentAssistant;
	}

	private ITextViewer lastTextViewer = null;
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow activeWorkbenchWindow = HandlerUtil.getActiveWorkbenchWindow(event);
		IWorkbenchPage page = activeWorkbenchWindow.getActivePage();
		if (page == null) {
			return null;
		}
		IEditorPart editor = page.getActiveEditor();
		if (editor == null) {
			return null;
		}
		if (editor instanceof AbstractTextEditor) {
			AbstractTextEditor abstractTextEditor = (AbstractTextEditor) editor;
			if (abstractTextEditor.isEditable()) {
				final ITextOperationTarget textOperationTarget = (ITextOperationTarget) abstractTextEditor
						.getAdapter(ITextOperationTarget.class);
				// Get the word at offset
				if (textOperationTarget instanceof ITextViewer) {
					final ITextViewer textViewer = (ITextViewer) textOperationTarget;
					Object adapter = (Control) abstractTextEditor.getAdapter(Control.class);
					if (adapter instanceof Control) {
						Control control = (Control) adapter;
						if (control instanceof StyledText) {
							StyledText styledText = (StyledText) control;
							if (styledText.getSelectionCount() == 0) {
								int caretOffset = styledText.getCaretOffset();
								int lineAtOffset = styledText.getLineAtOffset(caretOffset);
								int offsetAtLine = styledText.getOffsetAtLine(lineAtOffset);
								if (caretOffset > 0) {
									if (Character.isJavaIdentifierStart(styledText.getLine(lineAtOffset).charAt(
											caretOffset - offsetAtLine - 1))) {
										BusyIndicator.showWhile(textViewer.getTextWidget().getDisplay(), new Runnable() {
											public void run() {
												IContentAssistant contentAssistant = getContentAssistant();
												// Better handle install/uninstalls
												if (lastTextViewer != textViewer) {
													if (lastTextViewer != null) {
														contentAssistant.uninstall();
													}
													lastTextViewer = textViewer;
												}
												contentAssistant.install(textViewer);
												contentAssistant.showPossibleCompletions();
											}
										});
									}
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	private boolean enabled = true;

	void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isHandled() {
		return isEnabled();
	}

}
