package com.aptana.editor.scripting.actions;

import java.util.Map;
import java.util.ResourceBundle;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

public class ExecuteLineInsertingResultAction extends TextEditorAction {
	
	public static IAction create(ITextEditor textEditor) {
		return new ExecuteLineInsertingResultAction(ResourceBundle.getBundle(ExecuteLineInsertingResultAction.class.getName()), "ExecuteLineInsertingResultAction.", textEditor);
	}
	
	public static final String COMMAND_ID = "com.aptana.editor.scripting.command.ExecuteLineInsertingResult";
	
	private ITextViewer textViewer;
	private StyledText textWidget;

	private boolean deactivated = false;

	protected ExecuteLineInsertingResultAction(ResourceBundle bundle, String prefix, ITextEditor editor) {
		super(bundle, prefix, editor);
		setActionDefinitionId(COMMAND_ID);
		if (editor instanceof AbstractTextEditor) {
			AbstractTextEditor abstractTextEditor = (AbstractTextEditor) editor;
			Object adapter = abstractTextEditor.getAdapter(ITextOperationTarget.class);
			if (adapter instanceof ITextViewer) {
				textViewer = (ITextViewer) adapter;
				textWidget = textViewer.getTextWidget();
			}
		}
	}
	
	@Override
	public void run() {
		if (textWidget != null) {
			ITextEditor textEditor = getTextEditor();
			Map<String, String> environment = Filter.computeEnvironment(textEditor.getEditorSite().getWorkbenchWindow(), textEditor);
			Filter.StringOutputConsumer filterOutputConsumer = new Filter.StringOutputConsumer();
			Filter.launch(environment.get(VARIABLES_NAMES.TM_CARET_LINE_TEXT.name()), environment, Filter.EOF, filterOutputConsumer);
			try {
				String output = filterOutputConsumer.getOutput();
				StyledText styledText = (StyledText) textViewer.getTextWidget();
				int caretOffset = styledText.getCaretOffset();
				int lineAtCaret = styledText.getLineAtOffset(caretOffset);
				int lineCount = styledText.getLineCount();
				int startOffsetOfLineAtCaret = -1;
				String prefix = "";
				if (lineAtCaret == (lineCount - 1)) {
					// We are on the last line
					startOffsetOfLineAtCaret = styledText.getCharCount();
					prefix = styledText.getLineDelimiter();
				} else {
					startOffsetOfLineAtCaret = styledText.getOffsetAtLine(lineAtCaret+1);					
				}
				styledText.replaceTextRange(startOffsetOfLineAtCaret, 0, prefix + output);
			} catch (InterruptedException e) {
				// TODO
			}
		}
	}

	void adjustHandledState() {
		if (isDeactivated()) {
			deactivate();
			return;
		}
		if (!getTextEditor().isEditable()) {
			deactivate();
			return;
		}
		if (textWidget != null) {
			// TODO
			if (Boolean.TRUE.booleanValue()) {
				activate();
				return;
			} else {
				deactivate();
			}
		}
	}

	boolean isDeactivated() {
		return deactivated;
	}

	void setDeactivated(boolean deactivated) {
		this.deactivated = deactivated;
		adjustHandledState();
	}

	void activate() {
		getTextEditor().setAction(COMMAND_ID, this);
	}
	
	void deactivate() {
		getTextEditor().setAction(COMMAND_ID, null);
	}
}
