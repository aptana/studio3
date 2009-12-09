package com.aptana.editor.common.scripting.commands;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.aptana.scripting.model.InputType;
import com.aptana.scripting.model.OutputType;

public class FilterThroughCommandDialog extends Dialog {

	private Map<String, String> environment;
	private Combo commandCombo;
	private String command;
	private static List<String> lastCommands = new LinkedList<String>();
	
	private Button noneButton;
	private Button selectionButton;
	private Button selectedLinesButton;
	private Button documentButton;
	private Button lineButton;
	private Button wordButton;
	private Button inputFromConsoleButton;
	
	private InputType inputType;
	private static InputType lastInputType = InputType.NONE;

	private Button discardButton;
	private Button replaceSelectionButton;
	private Button replaceSelectedLinesButton;
	private Button replaceLineButton;
	private Button replaceWordButton;
	private Button replaceDocumentButton;
	private Button insertAsTextButton;
	private Button insertAsSnippetButton;
	private Button showAsHTMLButton;
	private Button showAsToolTipButton;
	private Button createNewDocumentButton;
	
	private Button outputToConsoleButton;
	private Text   consoleNameText;
	private String consoleName;
	private static String lastConsoleName;

	private OutputType outputType;
	private static OutputType lastOutputType = OutputType.OUTPUT_TO_CONSOLE;

	public FilterThroughCommandDialog(Shell parentShell, Map<String, String> environment) {
		super(parentShell);
		this.environment = environment;
	}
	
	@Override
	protected boolean isResizable() {
		return true;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		GridLayout compositeGridLayout = new GridLayout(3, false);
		composite.setLayout(compositeGridLayout);

		Label commandLabel = new Label(composite, SWT.TRAIL);
		commandLabel.setText(Messages.FilterThroughCommandDialog_LBL_Command);
		GridData commandLabelGridData = new GridData(SWT.LEAD, SWT.TOP, false, false);
		commandLabel.setLayoutData(commandLabelGridData);
		
		commandCombo = new Combo(composite, SWT.NONE);
		GridData commandComboGridData = new GridData(SWT.FILL, SWT.TOP, true, false);
		commandComboGridData.horizontalSpan = 2;
		commandCombo.setLayoutData(commandComboGridData);
		
		Label padding = new Label(composite, SWT.NONE);
		GridData paddingGridData = new GridData(SWT.LEAD, SWT.TOP, false, false);
		padding.setLayoutData(paddingGridData);
		
		Group inputGroup = new Group(composite, SWT.NONE);
		inputGroup.setText(Messages.FilterThroughCommandDialog_LBL_Input);
		GridData inputGroupGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		inputGroup.setLayoutData(inputGroupGridData);
		
		inputGroup.setLayout(new RowLayout(SWT.VERTICAL));

		noneButton = new Button(inputGroup, SWT.RADIO);
		noneButton.setText(Messages.FilterThroughCommandDialog_LBL_None);
		selectionButton = new Button(inputGroup, SWT.RADIO);
		selectionButton.setText(Messages.FilterThroughCommandDialog_LBL_Selection);
		selectedLinesButton = new Button(inputGroup, SWT.RADIO);
		selectedLinesButton.setText(Messages.FilterThroughCommandDialog_LBL_SelectedLines);
		documentButton = new Button(inputGroup, SWT.RADIO);
		documentButton.setText(Messages.FilterThroughCommandDialog_LBL_Document);
		lineButton = new Button(inputGroup, SWT.RADIO);
		lineButton.setText(Messages.FilterThroughCommandDialog_LBL_Line);
		wordButton = new Button(inputGroup, SWT.RADIO);
		wordButton.setText(Messages.FilterThroughCommandDialog_LBL_Word);
		inputFromConsoleButton = new Button(inputGroup, SWT.RADIO);
		inputFromConsoleButton.setText(Messages.FilterThroughCommandDialog_LBL_FromConsole);
		
		Group outputGroup = new Group(composite, SWT.NONE);
		outputGroup.setText(Messages.FilterThroughCommandDialog_LBL_Output);
		GridData outputGroupGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		outputGroupGridData.verticalSpan = 2;
		outputGroup.setLayoutData(outputGroupGridData);
		
		outputGroup.setLayout(new RowLayout(SWT.VERTICAL));
		
		discardButton = new Button(outputGroup, SWT.RADIO);
		discardButton.setText(Messages.FilterThroughCommandDialog_LBL_Discard);
		replaceSelectionButton = new Button(outputGroup, SWT.RADIO);
		replaceSelectionButton.setText(Messages.FilterThroughCommandDialog_LBL_ReplaceSelection);
		replaceSelectedLinesButton = new Button(outputGroup, SWT.RADIO);
		replaceSelectedLinesButton.setText(Messages.FilterThroughCommandDialog_LBL_ReplaceSelectedLines);
		replaceLineButton = new Button(outputGroup, SWT.RADIO);
		replaceLineButton.setText(Messages.FilterThroughCommandDialog_LBL_ReplaceLine);
		replaceWordButton = new Button(outputGroup, SWT.RADIO);
		replaceWordButton.setText(Messages.FilterThroughCommandDialog_LBL_ReplaceWord);
		replaceDocumentButton = new Button(outputGroup, SWT.RADIO);
		replaceDocumentButton.setText(Messages.FilterThroughCommandDialog_LBL_ReplaceDocument);
		insertAsTextButton = new Button(outputGroup, SWT.RADIO);
		insertAsTextButton.setText(Messages.FilterThroughCommandDialog_LBL_InsertAsText);
		insertAsSnippetButton = new Button(outputGroup, SWT.RADIO);
		insertAsSnippetButton.setText(Messages.FilterThroughCommandDialog_LBL_InsertAsSnippet);
		showAsHTMLButton = new Button(outputGroup, SWT.RADIO);
		showAsHTMLButton.setText(Messages.FilterThroughCommandDialog_LBL_ShowAsHTML);
		showAsToolTipButton = new Button(outputGroup, SWT.RADIO);
		showAsToolTipButton.setText(Messages.FilterThroughCommandDialog_LBL_ShowAsToolTip);
		createNewDocumentButton = new Button(outputGroup, SWT.RADIO);
		createNewDocumentButton.setText(Messages.FilterThroughCommandDialog_LBL_CreateNewDocument);
		outputToConsoleButton = new Button(outputGroup, SWT.RADIO);
		outputToConsoleButton.setText(Messages.FilterThroughCommandDialog_LBL_ToConsole);
		
		padding = new Label(composite, SWT.NONE);
		paddingGridData = new GridData(SWT.LEAD, SWT.TOP, false, false);
		padding.setLayoutData(paddingGridData);

		Button showEnvironmentButton = new Button(composite, SWT.PUSH);
		showEnvironmentButton.setText(Messages.FilterThroughCommandDialog_LBL_ShowEnvironment);
		GridData showEnvironmentButtonGridData = new GridData(SWT.FILL, SWT.TOP, true, false);
		showEnvironmentButton.setLayoutData(showEnvironmentButtonGridData);
		showEnvironmentButton.addSelectionListener(new SelectionListener() {			
			public void widgetSelected(SelectionEvent e) {
				EnvironmentDialog environmentDialog = new EnvironmentDialog(FilterThroughCommandDialog.this.getShell(), environment);
				environmentDialog.open();
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		
		Label consoleLabel = new Label(composite, SWT.NONE);
		consoleLabel.setText(Messages.FilterThroughCommandDialog_LBL_ConsoleName);
		GridData consoleLabelGridData = new GridData(SWT.LEAD, SWT.CENTER, false, false);
		consoleLabel.setLayoutData(consoleLabelGridData);
		
		consoleNameText = new Text(composite, SWT.BORDER);
		GridData consoleNameTextGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		consoleNameTextGridData.horizontalSpan = 2;
		consoleNameText.setLayoutData(consoleNameTextGridData);
		
		inputFromConsoleButton.addSelectionListener(new SelectionListener() {			
			public void widgetSelected(SelectionEvent e) {
				adjustConsoleNameTextSate();
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		
		outputToConsoleButton.addSelectionListener(new SelectionListener() {			
			public void widgetSelected(SelectionEvent e) {
				adjustConsoleNameTextSate();
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		
		applyState();
		
		return composite;
	}

	private void adjustConsoleNameTextSate() {
		consoleNameText.setEnabled(inputFromConsoleButton.getSelection() || outputToConsoleButton.getSelection());
	}
	
	public String getCommand() {
		return command;
	}
	
	public InputType getInputType() {
		return inputType;
	}
	
	public OutputType getOuputType() {
		return outputType;
	}
	
	public String getConsoleName() {
		return consoleName;
	}
	
	@Override
	protected void okPressed() {
		command = commandCombo.getText();
		
		if (command.trim().length() == 0) {
			getShell().getDisplay().beep();
			return;
		}
		
		lastCommands.add(0, command);
		
		if (noneButton.getSelection()) {
			inputType = InputType.NONE;
		} else if (selectionButton.getSelection()) {
			inputType = InputType.SELECTION;
		} else if (selectedLinesButton.getSelection()) {
			inputType = InputType.SELECTED_LINES;
		} else if (documentButton.getSelection()) {
			inputType = InputType.DOCUMENT;
		} else if (lineButton.getSelection()) {
			inputType = InputType.LINE;
		} else if (wordButton.getSelection()) {
			inputType = InputType.WORD;
		} else if (inputFromConsoleButton.getSelection()) {
			inputType = InputType.INPUT_FROM_CONSOLE;
		}
		
		if (discardButton.getSelection()) {
			outputType = OutputType.DISCARD;
		} else if (replaceSelectionButton.getSelection()) {
			outputType = OutputType.REPLACE_SELECTION;
		} else if (replaceSelectedLinesButton.getSelection()) {
			outputType = OutputType.REPLACE_SELECTED_LINES;
		} else if (replaceDocumentButton.getSelection()) {
			outputType = OutputType.REPLACE_DOCUMENT;
		} else if (replaceLineButton.getSelection()) {
			outputType = OutputType.REPLACE_LINE;
		} else if (replaceWordButton.getSelection()) {
			outputType = OutputType.REPLACE_WORD;
		} else if (insertAsTextButton.getSelection()) {
			outputType = OutputType.INSERT_AS_TEXT;
		} else if (insertAsSnippetButton.getSelection()) {
			outputType = OutputType.INSERT_AS_SNIPPET;
		} else if (showAsHTMLButton.getSelection()) {
			outputType = OutputType.SHOW_AS_HTML;
		} else if (showAsToolTipButton.getSelection()) {
			outputType = OutputType.SHOW_AS_TOOLTIP;
		} else if (createNewDocumentButton.getSelection()) {
			outputType = OutputType.CREATE_NEW_DOCUMENT;
		} else if (outputToConsoleButton.getSelection()) {
			outputType = OutputType.OUTPUT_TO_CONSOLE;
		} 
		
		consoleName = consoleNameText.getText();
		if (consoleName.trim().length() == 0)
		{
			consoleName = CommandExecutionUtils.DEFAULT_CONSOLE_NAME;
		}
		
		saveState();
		
		super.okPressed();
	}
	
	private void applyState() {
		switch (lastInputType) {
		case NONE:
			noneButton.setSelection(true);
			break;
		case SELECTION:
			selectionButton.setSelection(true);
			break;
		case SELECTED_LINES:
			selectedLinesButton.setSelection(true);
			break;
		case DOCUMENT:
			documentButton.setSelection(true);
			break;
		case LINE:
			lineButton.setSelection(true);
			break;
		case WORD:
			wordButton.setSelection(true);
			break;
		case INPUT_FROM_CONSOLE:
			inputFromConsoleButton.setSelection(true);
			break;
		}
		
		switch (lastOutputType) {
		case DISCARD:
			discardButton.setSelection(true);
			break;
		case REPLACE_SELECTION:
			replaceSelectionButton.setSelection(true);
			break;
		case REPLACE_SELECTED_LINES:
			replaceSelectedLinesButton.setSelection(true);
			break;
		case REPLACE_DOCUMENT:
			replaceDocumentButton.setSelection(true);
			break;
		case REPLACE_LINE:
			replaceLineButton.setSelection(true);
			break;
		case REPLACE_WORD:
			replaceWordButton.setSelection(true);
			break;		
		case CREATE_NEW_DOCUMENT:
			createNewDocumentButton.setSelection(true);
			break;
		case SHOW_AS_HTML:
			showAsHTMLButton.setSelection(true);
			break;
		case SHOW_AS_TOOLTIP:
			showAsToolTipButton.setSelection(true);
			break;
		case OUTPUT_TO_CONSOLE:
			outputToConsoleButton.setSelection(true);
			break;
		case INSERT_AS_TEXT:
			insertAsTextButton.setSelection(true);
			break;
		case INSERT_AS_SNIPPET:
			insertAsSnippetButton.setSelection(true);
			break;
		}
		
		consoleNameText.setEnabled(outputToConsoleButton.getSelection());
		if (lastConsoleName == null || lastConsoleName.trim().equals("")) { //$NON-NLS-1$
			consoleNameText.setText(CommandExecutionUtils.DEFAULT_CONSOLE_NAME);
		} else {
			consoleNameText.setText(lastConsoleName);
		}
		
		if (lastCommands.size() > 0) {
			commandCombo.setItems(lastCommands.toArray(new String[lastCommands.size()]));
			commandCombo.select(0);
		}
		
	}
	
	private void saveState() {
		lastInputType = inputType;
		lastOutputType = outputType;
		lastConsoleName = consoleName;
		if (lastConsoleName.equals(CommandExecutionUtils.DEFAULT_CONSOLE_NAME)) {
			lastConsoleName = ""; //$NON-NLS-1$
		}
	}

}
