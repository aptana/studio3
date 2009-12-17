package com.aptana.editor.common.actions;

import java.util.ResourceBundle;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

import com.aptana.editor.common.scripting.commands.CommandExecutionUtils;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.CommandResult;
import com.aptana.scripting.model.InputType;
import com.aptana.scripting.model.OutputType;

public class ExecuteLineInsertingResultAction extends TextEditorAction {
	
	public static IAction create(ITextEditor textEditor) {
		return new ExecuteLineInsertingResultAction(ResourceBundle.getBundle(ExecuteLineInsertingResultAction.class.getName()),
				"ExecuteLineInsertingResultAction.", textEditor); //$NON-NLS-1$
	}
	
	public static final String COMMAND_ID = "com.aptana.editor.common.scripting.commands.ExecuteLineInsertingResult"; //$NON-NLS-1$
	
	private ITextViewer textViewer;
	private StyledText textWidget;

	private boolean deactivated = false;

	protected ExecuteLineInsertingResultAction(ResourceBundle bundle, String prefix, ITextEditor editor) {
		super(bundle, prefix, editor);
		setActionDefinitionId(COMMAND_ID);
		Object adapter = editor.getAdapter(ITextOperationTarget.class);
		if (adapter instanceof ITextViewer) {
			textViewer = (ITextViewer) adapter;
			textWidget = textViewer.getTextWidget();
		}
	}
	
	@Override
	public void run() {
		if (textWidget != null) {
			ITextEditor textEditor = getTextEditor();
			CommandElement command = new CommandElement(null); // Use null value for path to create a one off command
			command.setInputType(InputType.LINE.getName());
			command.setOutputType(OutputType.REPLACE_LINE.getName());
			int caretOffset = textWidget.getCaretOffset();
			int lineAtCaret = textWidget.getLineAtOffset(caretOffset);
			command.setInvoke(textWidget.getLine(lineAtCaret));
			CommandResult commandResult = CommandExecutionUtils.executeCommand(command, textEditor);
			CommandExecutionUtils.processCommandResult(command, commandResult, textEditor);
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
