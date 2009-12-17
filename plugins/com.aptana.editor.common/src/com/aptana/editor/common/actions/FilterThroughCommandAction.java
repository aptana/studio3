package com.aptana.editor.common.actions;

import java.util.Map;
import java.util.ResourceBundle;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

import com.aptana.editor.common.scripting.commands.CommandExecutionUtils;
import com.aptana.editor.common.scripting.commands.FilterThroughCommandDialog;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.CommandResult;

public class FilterThroughCommandAction extends TextEditorAction {
	
	public static IAction create(ITextEditor textEditor) {
		return new FilterThroughCommandAction(ResourceBundle.getBundle(FilterThroughCommandAction.class.getName()),
				"FilterThroughCommandAction.", textEditor);	//$NON-NLS-1$
	}
	
	public static final String COMMAND_ID = "com.aptana.editor.common.scripting.commands.FilterThroughCommand";	//$NON-NLS-1$
	
	private ITextViewer textViewer;
	private StyledText textWidget;

	private boolean deactivated = false;

	protected FilterThroughCommandAction(ResourceBundle bundle, String prefix, ITextEditor editor) {
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
		ITextEditor textEditor = getTextEditor();
		
		IWorkbenchWindow workbenchWindow = textEditor.getEditorSite().getWorkbenchWindow();
		Map<String, String> environment = CommandExecutionUtils.computeEnvironment(textEditor);
		FilterThroughCommandDialog filterThroughCommandDialog = new FilterThroughCommandDialog(workbenchWindow.getShell(), environment);
		if (filterThroughCommandDialog.open() == Window.OK) {
			CommandElement command = new CommandElement(null); // Use null value for path to create a one off command
			command.setInputType(filterThroughCommandDialog.getInputType().getName());
			command.setOutputType(filterThroughCommandDialog.getOuputType().getName());
			command.setInvoke(filterThroughCommandDialog.getCommand());
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
