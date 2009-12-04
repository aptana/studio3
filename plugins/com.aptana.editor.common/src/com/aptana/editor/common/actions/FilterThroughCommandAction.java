package com.aptana.editor.common.actions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.scripting.commands.Filter;
import com.aptana.editor.common.scripting.commands.FilterThroughCommandDialog;
import com.aptana.editor.common.scripting.commands.INPUT_TYPE;
import com.aptana.editor.common.scripting.commands.OUTPUT_TYPE;
import com.aptana.editor.common.scripting.commands.Utilities;
import com.aptana.editor.common.scripting.commands.Filter.FilterInputProvider;
import com.aptana.editor.common.scripting.snippets.SnippetsCompletionProcessor;

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
			IWorkbenchWindow workbenchWindow = textEditor.getEditorSite().getWorkbenchWindow();
			Map<String, String> environment = Filter.computeEnvironment(workbenchWindow, textEditor);
			
			final int caretOffset = textWidget.getCaretOffset();
			int lineAtCaret = textWidget.getLineAtOffset(caretOffset);
			String lineText = textWidget.getLine(lineAtCaret);
			int lineLength = lineText.length();
			
			Point selectionRange = textWidget.getSelection();
			int selectionStartOffsetLine = textWidget.getLineAtOffset(selectionRange.x);
			int selectionEndOffsetLine = textWidget.getLineAtOffset(selectionRange.y);

			int selectionStartOffsetLineStartOffset = textWidget.getOffsetAtLine(selectionStartOffsetLine);
			int selectionEndOffsetLineEndOffset = 
				textWidget.getOffsetAtLine(selectionEndOffsetLine) + textWidget.getLine(selectionEndOffsetLine).length();
			
			FilterThroughCommandDialog filterThroughCommandDialog = new FilterThroughCommandDialog(workbenchWindow.getShell(), environment);
			if (filterThroughCommandDialog.open() == Window.OK) {
				INPUT_TYPE inputType = filterThroughCommandDialog.getInputType();
				FilterInputProvider filterInputProvider = Filter.EOF;

				switch (inputType) {
				case SELECTION:
					filterInputProvider = new Filter.StringInputProvider(textWidget.getSelectionText());
					break;
				case SELECTED_LINES:
					filterInputProvider = new Filter.StringInputProvider(textWidget.getText(selectionStartOffsetLineStartOffset,
							selectionEndOffsetLineEndOffset));
					break;
				case DOCUMENT:
					filterInputProvider = new Filter.StringInputProvider(textWidget.getText());
					break;
				case LINE:
					filterInputProvider = new Filter.StringInputProvider(textWidget.getLine(textWidget.getLineAtOffset(textWidget.getCaretOffset())));
					break;
				case WORD:
					filterInputProvider = Filter.EOF;
					break;
				case INPUT_FROM_CONSOLE:
					filterInputProvider = new Filter.EclipseConsoleInputProvider(filterThroughCommandDialog.getConsoleName());
					break;
				}

				Filter.FilterOutputConsumer filterOutputConsumer = null;
				OUTPUT_TYPE ouputType = filterThroughCommandDialog.getOuputType();
				switch (ouputType) {
				case DISCARD:
					filterOutputConsumer = Filter.DISCARD;
					break;
				case OUTPUT_TO_CONSOLE:
					filterOutputConsumer = new Filter.EclipseConsolePrintStreamOutputConsumer(filterThroughCommandDialog.getConsoleName());
					break;
				default:
					filterOutputConsumer = new Filter.StringOutputConsumer();
					break;
				}
				
				Filter.launch(filterThroughCommandDialog.getCommand(), environment, filterInputProvider, filterOutputConsumer);
				
				try {
					switch (ouputType) {
					case DISCARD:
						break;
					case REPLACE_SELECTION:
						int start = Math.min(selectionRange.x, selectionRange.y);
						int end = Math.max(selectionRange.x, selectionRange.y);
						textWidget.replaceTextRange(start, end - start, 
								((Filter.StringOutputConsumer)filterOutputConsumer).getOutput());
						break;
					case REPLACE_SELECTED_LINES:
						textWidget.replaceTextRange(selectionStartOffsetLineStartOffset, 
								selectionEndOffsetLineEndOffset - selectionStartOffsetLineStartOffset, 
								((Filter.StringOutputConsumer)filterOutputConsumer).getOutput());
						break;
					case REPLACE_LINE:
						int startOffsetOfLineAtCaret = textWidget.getOffsetAtLine(lineAtCaret);
						textWidget.replaceTextRange(startOffsetOfLineAtCaret, lineLength, 
								((Filter.StringOutputConsumer)filterOutputConsumer).getOutput());
						break;
					case REPLACE_DOCUMENT:
						textWidget.setText( 
								((Filter.StringOutputConsumer)filterOutputConsumer).getOutput());
						break;
					case INSERT_AS_TEXT:
						textWidget.replaceTextRange(caretOffset, 0, 
								((Filter.StringOutputConsumer)filterOutputConsumer).getOutput());
						break;
					case INSERT_AS_SNIPPET:
						SnippetsCompletionProcessor.insertAsTemplate(textViewer,
								caretOffset,
								((Filter.StringOutputConsumer)filterOutputConsumer).getOutput());
						break;
					case SHOW_AS_HTML:
						File tempHmtlFile = null;
						try {
							tempHmtlFile = File.createTempFile(CommonEditorPlugin.PLUGIN_ID, ".html"); //$NON-NLS-1$
						} catch (IOException e) {
							CommonEditorPlugin.logError("Could not create temporary file.", e); //$NON-NLS-1$
						}
						if (tempHmtlFile != null) {
							String output = ((Filter.StringOutputConsumer)filterOutputConsumer).getOutput();
							tempHmtlFile.deleteOnExit();
							PrintWriter pw = null;
							try {
								pw = new PrintWriter(tempHmtlFile);
							} catch (FileNotFoundException e1) {
								e1.printStackTrace();
							}
							if (pw != null) {
								pw.println(output);
								pw.flush();
								pw.close();
								IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
								try {
									URL url = tempHmtlFile.toURI().toURL();
									if (support.isInternalWebBrowserAvailable()) {
										support.createBrowser(
												IWorkbenchBrowserSupport.NAVIGATION_BAR
												| IWorkbenchBrowserSupport.LOCATION_BAR 
												| IWorkbenchBrowserSupport.AS_EDITOR
												| IWorkbenchBrowserSupport.STATUS,
												getText(),
												getText(),
												filterThroughCommandDialog.getCommand()).openURL(url);
									} else {
										support.getExternalBrowser().openURL(url);
									}
								} catch (PartInitException e) {
									CommonEditorPlugin.logError("Could not launch browser.", e); //$NON-NLS-1$
								} catch (MalformedURLException e) {
									CommonEditorPlugin.logError("Malformed URL: "+tempHmtlFile.toURI(), e); //$NON-NLS-1$
								}
							}
						}
						break;
					case SHOW_AS_TOOLTIP:													
						DefaultInformationControl tooltip = new DefaultInformationControl(workbenchWindow.getShell(),
								"Type escape to dismiss.", null); //$NON-NLS-1$
						tooltip.setInformation(((Filter.StringOutputConsumer)filterOutputConsumer).getOutput());
						Point p = tooltip.computeSizeHint();
						tooltip.setSize(p.x, p.y);

						Point locationAtOffset = textWidget.getLocationAtOffset(caretOffset);
						locationAtOffset = textWidget.toDisplay(locationAtOffset.x, locationAtOffset.y + textWidget.getLineHeight(caretOffset) + 2);
						tooltip.setLocation(locationAtOffset);
						tooltip.setVisible(true);
						tooltip.setFocus();
						break;
					case CREATE_NEW_DOCUMENT:
						File file = Utilities.getNonExistingFileBackingStore();
						IEditorInput input = Utilities.createNonExistingFileEditorInput(file, "Untitled.txt");	//$NON-NLS-1$
						String editorId = "org.eclipse.ui.DefaultTextEditor"; //$NON-NLS-1$
						try
						{
							IEditorPart part = workbenchWindow.getActivePage().openEditor(input, editorId);

							if (part instanceof ITextEditor)
							{
								ITextEditor openedTextEditor = (ITextEditor) part;
								IDocumentProvider dp = openedTextEditor.getDocumentProvider();
								IDocument doc = dp.getDocument(openedTextEditor.getEditorInput());
								try
								{
									String fileContents = ((Filter.StringOutputConsumer)filterOutputConsumer).getOutput();
									if (fileContents != null)
									{
										doc.replace(0, 0, fileContents);
									}
								}
								catch (BadLocationException e)
								{
									CommonEditorPlugin.logError("", e); //$NON-NLS-1$
								}
							}

						}
						catch (PartInitException e)
						{
							CommonEditorPlugin.logError("Error opening editor.", e); //$NON-NLS-1$
						}
						break;
					}
				} catch (InterruptedException e) {
					CommonEditorPlugin.logError("", e); //$NON-NLS-1$
				}
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
