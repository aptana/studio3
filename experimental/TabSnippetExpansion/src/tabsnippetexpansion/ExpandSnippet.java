package tabsnippetexpansion;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;


public class ExpandSnippet extends AbstractHandler {
	
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
		
		IWorkbenchPartSite site = editor.getSite();
		if (editor instanceof ITextEditor) {
			ITextEditor abstractTextEditor = (ITextEditor) editor;
			if (abstractTextEditor.isEditable()) {
				final ITextOperationTarget textOperationTarget = (ITextOperationTarget) abstractTextEditor.getAdapter(ITextOperationTarget.class);
				Object adapter = (Control) abstractTextEditor.getAdapter(Control.class);
				if (adapter instanceof Control) {
					Control control = (Control) adapter;
					if (control instanceof StyledText) {
						StyledText styledText = (StyledText) control;
						if (styledText.getSelectionCount() > 0) {
							return null;
						}
						
						int caretOffset = styledText.getCaretOffset();
						int lineAtOffset = styledText.getLineAtOffset(caretOffset);
						int offsetAtLine = styledText.getOffsetAtLine(lineAtOffset);
						if (offsetAtLine == caretOffset) {
							return null;
						}
						
						if (Character.isJavaIdentifierStart(styledText.getLine(lineAtOffset).charAt(caretOffset - offsetAtLine - 1))) {
							// Get the word at offset
							if (textOperationTarget != null) {
								Display display= null;
								Shell shell= site.getShell();
								if (shell != null && !shell.isDisposed())
									display= shell.getDisplay();

								BusyIndicator.showWhile(display, new Runnable() {
									public void run() {
										textOperationTarget.doOperation(ISourceViewer.CONTENTASSIST_PROPOSALS);
									}
								});
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	private boolean enabled = true;
	
	@Override
	public void setEnabled(Object object) {
		if (object instanceof IEvaluationContext) {
			IEvaluationContext evaluationContext = (IEvaluationContext) object;
			IEditorPart editor = (IEditorPart) evaluationContext.getVariable(ISources.ACTIVE_EDITOR_NAME);
			if (editor instanceof AbstractTextEditor) {
				AbstractTextEditor abstractTextEditor = (AbstractTextEditor) editor;
				if (!abstractTextEditor.isEditable()) {
					enabled = false;
					return;
				}
				Object adapter = (Control) abstractTextEditor.getAdapter(Control.class);
				if (adapter instanceof Control) {
					Control control = (Control) adapter;
					if (control instanceof StyledText) {
						StyledText styledText = (StyledText) control;
						if (styledText.getSelectionCount() > 0) {
							enabled = false;
							return;
						}
						int caretOffset = styledText.getCaretOffset();
						int lineAtOffset = styledText.getLineAtOffset(caretOffset);
						int offsetAtLine = styledText.getOffsetAtLine(lineAtOffset);
						if (offsetAtLine == caretOffset) {
							enabled = false;
							return;
						}
					}
				}
				if (editor instanceof ITextEditor) {
					ITextEditor textEditor = (ITextEditor) editor;
					IDocumentProvider provider= textEditor.getDocumentProvider();
					if (provider != null) {
						IDocument document= provider.getDocument(editor.getEditorInput());
						if (document != null) {
							enabled = (!LinkedModeModel.hasInstalledModel(document));
							return;
						}
					}
				}
			}
		}
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public boolean isHandled() {
		return isEnabled();
	}

	public void removeHandlerListener(IHandlerListener handlerListener) {
	}

}
