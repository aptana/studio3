package com.aptana.editor.common.scripting.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringBufferInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.BreakIterator;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.scripting.snippets.SnippetsCompletionProcessor;
import com.aptana.scripting.model.CommandContext;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.CommandResult;
import com.aptana.scripting.model.InputType;
import com.aptana.scripting.model.OutputType;

@SuppressWarnings("deprecation")
public class CommandExecutionUtils
{

	public static final FilterInputProvider EOF = new StringInputProvider();

	static final String DEFAULT_CONSOLE_NAME = Messages.CommandExecutionUtils_DefaultConsoleName;

	public interface FilterInputProvider
	{
		public InputStream getInputStream();
	};

	public interface FilterOutputConsumer
	{
		public void consume(InputStream input);
	}

	public static class StringInputProvider implements FilterInputProvider
	{
		private final String string;

		public StringInputProvider()
		{
			this(""); //$NON-NLS-1$
		}

		public StringInputProvider(String string)
		{
			this.string = string;
		}

		public InputStream getInputStream()
		{
			return new StringBufferInputStream(string);
		}
	}

	public static class EclipseConsoleInputProvider implements FilterInputProvider
	{
		private final String consoleName;

		public EclipseConsoleInputProvider()
		{
			this(DEFAULT_CONSOLE_NAME);
		}

		public EclipseConsoleInputProvider(String consoleName)
		{
			this.consoleName = consoleName;
		}

		public InputStream getInputStream()
		{
			IOConsole messageConsole = getMessageConsole(consoleName);
			messageConsole.activate();
			return messageConsole.getInputStream();
		}
	}

	public static class PrintStreamOutputConsumer implements FilterOutputConsumer
	{
		private PrintStream printStream;

		public PrintStreamOutputConsumer()
		{
		}

		public PrintStreamOutputConsumer(PrintStream printStream)
		{
			this.printStream = printStream;
		}

		public PrintStream getPrintStream()
		{
			return printStream;
		}

		protected void setPrintStream(PrintStream printStream)
		{
			this.printStream = printStream;
		}

		public void consume(final InputStream outputStream)
		{
			new Thread(new Runnable()
			{
				public void run()
				{
					BufferedReader br = new BufferedReader(new InputStreamReader(outputStream));
					String line = null;
					try
					{
						while ((line = br.readLine()) != null)
						{
							if (printStream != null)
							{
								printStream.println(line);
								printStream.flush();
							}
						}
					}
					catch (IOException e)
					{
					}
					finally
					{
						printStream.close();
						try
						{
							outputStream.close();
						}
						catch (IOException e)
						{
							// ignore
						}
					}
				}
			}).start();
		}
	}

	public static class StringOutputConsumer implements FilterOutputConsumer
	{
		private BlockingQueue<String> outputQueue;

		public StringOutputConsumer()
		{
			this(new ArrayBlockingQueue<String>(1));
		}

		public StringOutputConsumer(BlockingQueue<String> outputQueue)
		{
			this.outputQueue = outputQueue;
		}

		public void consume(final InputStream outputStream)
		{
			new Thread(new Runnable()
			{
				public void run()
				{
					StringBuilder stringBuilder = new StringBuilder();
					BufferedReader br = new BufferedReader(new InputStreamReader(outputStream));
					String line = null;
					try
					{
						while ((line = br.readLine()) != null)
						{
							stringBuilder.append(line + "\n"); //$NON-NLS-1$
						}
					}
					catch (IOException e)
					{
					}
					finally
					{
						outputQueue.add(stringBuilder.toString());
						try
						{
							outputStream.close();
						}
						catch (IOException e)
						{
							// ignore
						}
					}
				}
			}).start();
		}

		public String getOutput() throws InterruptedException
		{
			return outputQueue.take();
		}
	}

	public static class EclipseConsolePrintStreamOutputConsumer implements FilterOutputConsumer
	{
		private final String consoleName;
		private final boolean isStdErr;

		public EclipseConsolePrintStreamOutputConsumer()
		{
			this(DEFAULT_CONSOLE_NAME, false);
		}

		public EclipseConsolePrintStreamOutputConsumer(String consoleName)
		{
			this(consoleName, false);
		}

		public EclipseConsolePrintStreamOutputConsumer(boolean err)
		{
			this(DEFAULT_CONSOLE_NAME, err);
		}

		public EclipseConsolePrintStreamOutputConsumer(String consoleName, boolean isStdErr)
		{
			this.consoleName = consoleName;
			this.isStdErr = isStdErr;
		}

		public void consume(InputStream outputStream)
		{
			IOConsole messageConsole = getMessageConsole(consoleName);
			messageConsole.activate();
			MessageConsoleWriter messageConsoleWriter = new MessageConsoleWriter(messageConsole, outputStream, isStdErr);
			new Thread(messageConsoleWriter).start();
		}
	}

	public static final FilterOutputConsumer DISCARD = new PrintStreamOutputConsumer();

	public static final FilterOutputConsumer TO_SYSOUT = new PrintStreamOutputConsumer(System.out);

	public static final FilterOutputConsumer TO_SYSERR = new PrintStreamOutputConsumer(System.err);

	private static Map<String, IOConsole> nameToMessageConsole = new WeakHashMap<String, IOConsole>();

	public static CommandResult executeCommand(CommandElement command, ITextEditor textEditor)
	{
		ITextViewer textViewer = null;
		Object adapter = textEditor.getAdapter(ITextOperationTarget.class);
		if (adapter instanceof ITextViewer)
		{
			textViewer = (ITextViewer) adapter;
		}
		return executeCommand(command, textViewer, textEditor);
	}

	public static CommandResult executeCommand(CommandElement command, ITextViewer textViewer, ITextEditor textEditor) {
		StyledText textWidget = textViewer.getTextWidget();
		Point selectionRange = textWidget.getSelection();
		int selectionStartOffsetLine = textWidget.getLineAtOffset(selectionRange.x);
		int selectionEndOffsetLine = textWidget.getLineAtOffset(selectionRange.y);

		int selectionStartOffsetLineStartOffset = textWidget.getOffsetAtLine(selectionStartOffsetLine);
		int selectionEndOffsetLineEndOffset = 
			textWidget.getOffsetAtLine(selectionEndOffsetLine) + textWidget.getLine(selectionEndOffsetLine).length();
		
		FilterInputProvider filterInputProvider = CommandExecutionUtils.EOF;

		InputType[] inputTypes = command.getInputTypes();
		InputType inputType = (inputTypes == null || inputTypes.length == 0) ? InputType.UNDEFINED : inputTypes[0];
		switch (inputType) {
		case SELECTION:
			filterInputProvider = new CommandExecutionUtils.StringInputProvider(textWidget.getSelectionText());
			break;
		case SELECTED_LINES:
			filterInputProvider = new CommandExecutionUtils.StringInputProvider(textWidget.getText(selectionStartOffsetLineStartOffset,
					selectionEndOffsetLineEndOffset));
			break;
		case DOCUMENT:
			filterInputProvider = new CommandExecutionUtils.StringInputProvider(textWidget.getText());
			break;
		case LINE:
			filterInputProvider = new CommandExecutionUtils.StringInputProvider(textWidget.getLine(textWidget.getLineAtOffset(textWidget.getCaretOffset())));
			break;
		case WORD:
			filterInputProvider = CommandExecutionUtils.EOF;
			break;
		case INPUT_FROM_CONSOLE:
			filterInputProvider = new CommandExecutionUtils.EclipseConsoleInputProvider(CommandExecutionUtils.DEFAULT_CONSOLE_NAME);
			break;
		}

		// Create command context
		CommandContext commandContext = command.createCommandContext();

		// Set input stream
		commandContext.setInputStream(filterInputProvider.getInputStream());

		Map<String, String> computedEnvironmentMap = computeEnvironment(textEditor);
		if (computedEnvironmentMap != null)
		{
			// augment it
			for (Map.Entry<String,String> entry : computedEnvironmentMap.entrySet())
			{
				commandContext.put(entry.getKey(), entry.getValue());
			}
		}

		return command.execute(commandContext);
	}

	public static void processCommandResult(CommandElement command, CommandResult commandResult, ITextEditor textEditor)
	{
		Object adapter = textEditor.getAdapter(ITextOperationTarget.class);
		if (adapter instanceof ITextViewer)
		{
			processCommandResult(command, commandResult, (ITextViewer) adapter);
		}
	}

	public static void processCommandResult(CommandElement command, CommandResult commandResult, ITextViewer textViewer)
	{
		StyledText textWidget = textViewer.getTextWidget();

		final int caretOffset = textWidget.getCaretOffset();
		int lineAtCaret = textWidget.getLineAtOffset(caretOffset);
		String lineText = textWidget.getLine(lineAtCaret);
		int lineLength = lineText.length();

		Point selectionRange = textWidget.getSelection();
		int selectionStartOffsetLine = textWidget.getLineAtOffset(selectionRange.x);
		int selectionEndOffsetLine = textWidget.getLineAtOffset(selectionRange.y);

		int selectionStartOffsetLineStartOffset = textWidget.getOffsetAtLine(selectionStartOffsetLine);
		int selectionEndOffsetLineEndOffset = textWidget.getOffsetAtLine(selectionEndOffsetLine)
				+ textWidget.getLine(selectionEndOffsetLine).length();

		OutputType ouputType = OutputType.get(command.getOutputType());
		switch (ouputType)
		{
			case DISCARD:
				break;
			case REPLACE_SELECTION:
				int start = Math.min(selectionRange.x, selectionRange.y);
				int end = Math.max(selectionRange.x, selectionRange.y);
				textWidget.replaceTextRange(start, end - start, commandResult.getOutputString());
				break;
			case REPLACE_SELECTED_LINES:
				textWidget.replaceTextRange(selectionStartOffsetLineStartOffset, selectionEndOffsetLineEndOffset
						- selectionStartOffsetLineStartOffset, commandResult.getOutputString());
				break;
			case REPLACE_LINE:
				int startOffsetOfLineAtCaret = textWidget.getOffsetAtLine(lineAtCaret);
				textWidget.replaceTextRange(startOffsetOfLineAtCaret, lineLength, commandResult.getOutputString());
				break;
			case REPLACE_DOCUMENT:
				textWidget.setText(commandResult.getOutputString());
				break;
			case INSERT_AS_TEXT:
				textWidget.replaceTextRange(caretOffset, 0, commandResult.getOutputString());
				break;
			case INSERT_AS_SNIPPET:
				// FIXME Should remove selection if that was the input
				SnippetsCompletionProcessor.insertAsTemplate(textViewer, caretOffset, commandResult.getOutputString());
				break;
			case SHOW_AS_HTML:
				// TODO Refactor into a method
				File tempHmtlFile = null;
				try
				{
					tempHmtlFile = File.createTempFile(CommonEditorPlugin.PLUGIN_ID, ".html"); //$NON-NLS-1$
				}
				catch (IOException e)
				{
					CommonEditorPlugin.logError(Messages.CommandExecutionUtils_CouldNotCreateTemporaryFile, e);
				}
				if (tempHmtlFile != null)
				{
					String output = commandResult.getOutputString();
					tempHmtlFile.deleteOnExit();
					PrintWriter pw = null;
					try
					{
						pw = new PrintWriter(tempHmtlFile);
					}
					catch (FileNotFoundException fne)
					{
						CommonEditorPlugin.logError(fne);
					}
					if (pw != null)
					{
						pw.println(output);
						pw.flush();
						pw.close();
						IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
						try
						{
							URL url = tempHmtlFile.toURI().toURL();
							if (support.isInternalWebBrowserAvailable())
							{
								support.createBrowser(
										IWorkbenchBrowserSupport.NAVIGATION_BAR | IWorkbenchBrowserSupport.LOCATION_BAR
												| IWorkbenchBrowserSupport.AS_EDITOR | IWorkbenchBrowserSupport.STATUS,
										"", //$NON-NLS-1$
										"", //$NON-NLS-1$
										command.getDisplayName()).openURL(url);
							}
							else
							{
								support.getExternalBrowser().openURL(url);
							}
						}
						catch (PartInitException e)
						{
							CommonEditorPlugin.logError(e);
						}
						catch (MalformedURLException e)
						{
							CommonEditorPlugin.logError(e);
						}
					}
				}
				break;
			case SHOW_AS_TOOLTIP:
				// TODO Refactor into a method
				DefaultInformationControl tooltip = new DefaultInformationControl(PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell(), Messages.CommandExecutionUtils_TypeEscapeToDismiss,
						null);
				tooltip.setInformation(commandResult.getOutputString());
				Point p = tooltip.computeSizeHint();
				tooltip.setSize(p.x, p.y);

				Point locationAtOffset = textWidget.getLocationAtOffset(caretOffset);
				locationAtOffset = textWidget.toDisplay(locationAtOffset.x, locationAtOffset.y
						+ textWidget.getLineHeight(caretOffset) + 2);
				tooltip.setLocation(locationAtOffset);
				tooltip.setVisible(true);
				tooltip.setFocus();
				break;
			case CREATE_NEW_DOCUMENT:
				// TODO Refactor into a method
				File file = Utilities.getFile();
				IEditorInput input = Utilities.createFileEditorInput(file, "Untitled.txt"); //$NON-NLS-1$
				String editorId = "org.eclipse.ui.DefaultTextEditor"; //$NON-NLS-1$
				try
				{
					IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(
							input, editorId);
					if (part instanceof ITextEditor)
					{
						ITextEditor openedTextEditor = (ITextEditor) part;
						IDocumentProvider dp = openedTextEditor.getDocumentProvider();
						IDocument doc = dp.getDocument(openedTextEditor.getEditorInput());
						try
						{
							String fileContents = commandResult.getOutputString();
							if (fileContents != null)
							{
								doc.replace(0, 0, fileContents);
							}
						}
						catch (BadLocationException e)
						{
							CommonEditorPlugin.logError(e);
						}
					}

				}
				catch (PartInitException e)
				{
					CommonEditorPlugin.logError(e);
				}
				break;
		}
	}

	public static Map<String, String> computeEnvironment(ITextEditor textEditor)
	{
		Map<String, String> environment = new HashMap<String, String>();
		if (textEditor == null)
			return environment;
		IEditorInput editorInput = textEditor.getEditorInput();
		if (editorInput instanceof IFileEditorInput)
		{
			IFileEditorInput fileEditorInput = (IFileEditorInput) editorInput;
			IFile iFile = fileEditorInput.getFile();
			if (iFile != null)
			{
				environment
						.put(VARIABLES_NAMES.TM_SELECTED_FILE.name(), iFile.getLocation().toFile().getAbsolutePath());
				environment.put(VARIABLES_NAMES.TM_FILEPATH.name(), iFile.getLocation().toFile().getAbsolutePath());
				environment.put(VARIABLES_NAMES.TM_DIRECTORY.name(), iFile.getParent().getLocation().toFile()
						.getAbsolutePath());
				environment.put(VARIABLES_NAMES.TM_PROJECT_DIRECTORY.name(), iFile.getProject().getLocation().toFile()
						.getAbsolutePath());
				ISelectionProvider selectionProvider = textEditor.getSelectionProvider();
				ISelection selection = selectionProvider.getSelection();
				if (selection instanceof ITextSelection)
				{
					ITextSelection textSelection = (ITextSelection) selection;
					environment.put(VARIABLES_NAMES.TM_SELECTED_TEXT.name(), textSelection.getText());
					environment.put(VARIABLES_NAMES.TM_LINE_NUMBER.name(), String
							.valueOf(textSelection.getStartLine() + 1));
					environment.put(VARIABLES_NAMES.TM_SELECTION_OFFSET.name(), String.valueOf(textSelection
							.getOffset()));
					environment.put(VARIABLES_NAMES.TM_SELECTION_LENGTH.name(), String.valueOf(textSelection
							.getLength()));
					environment.put(VARIABLES_NAMES.TM_SELECTION_START_LINE_NUMBER.name(), String.valueOf(textSelection
							.getStartLine()));
					environment.put(VARIABLES_NAMES.TM_SELECTION_END_LINE_NUMBER.name(), String.valueOf(textSelection
							.getEndLine()));
					Object adapter = textEditor.getAdapter(Control.class);
					if (adapter instanceof StyledText)
					{
						StyledText styledText = (StyledText) adapter;
						environment.put(VARIABLES_NAMES.TM_LINE_INDEX.name(), String.valueOf(textSelection.getOffset()
								- styledText.getOffsetAtLine(textSelection.getStartLine())));
						int caretOffset = styledText.getCaretOffset();
						int lineAtCaret = styledText.getLineAtOffset(caretOffset);
						environment.put(VARIABLES_NAMES.TM_CARET_LINE_NUMBER.name(), String.valueOf(lineAtCaret + 1));
						String currentLine = styledText.getLine(lineAtCaret);
						environment.put(VARIABLES_NAMES.TM_CARET_LINE_TEXT.name(), currentLine);
						environment.put(VARIABLES_NAMES.TM_CURRENT_LINE.name(), currentLine);
						int offsetInLine = caretOffset - styledText.getOffsetAtLine(lineAtCaret);
						String currentWord = findWord(currentLine, offsetInLine);
						environment.put(VARIABLES_NAMES.TM_CURRENT_WORD.name(), currentWord);
					}
				}
			}
		}
		return environment;
	}
	
	/**
	 * Tries to find the word at the given offset.
	 * 
	 * @param document the document
	 * @param offset the offset
	 * @return the word or <code>null</code> if none
	 */
	protected static String findWord(String line, int offset) {
		BreakIterator breakIter= BreakIterator.getWordInstance();
		breakIter.setText(line);

		int start= breakIter.preceding(offset);
		if (start == BreakIterator.DONE)
			start= 0;

		int end= breakIter.following(offset);
		if (end == BreakIterator.DONE)
			end= line.length();

		if (breakIter.isBoundary(offset)) {
			if (end - offset > offset - start)
				start= offset;
			else
				end= offset;
		}

		if (end == start)
			return null;
		return line.substring(start, end);
	}

	@SuppressWarnings("unused")
	private static IOConsole getMessageConsole()
	{
		return getMessageConsole(DEFAULT_CONSOLE_NAME);
	}

	private static IOConsole getMessageConsole(String name)
	{
		IOConsole messageConsole = nameToMessageConsole.get(name);
		if (messageConsole == null)
		{
			messageConsole = new IOConsole(name, null, null, true);
			ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { messageConsole });
			nameToMessageConsole.put(name, messageConsole);
		}
		return messageConsole;
	}

	private static class MessageConsoleWriter implements Runnable
	{
		private final IOConsole messageConsole;
		private final InputStream from;
		private final boolean isStdErr;

		private MessageConsoleWriter(IOConsole messageConsole, InputStream from, boolean isStdErr)
		{
			this.messageConsole = messageConsole;
			this.from = from;
			this.isStdErr = isStdErr;
		}

		public void run()
		{
			IOConsoleOutputStream newOutputStream = messageConsole.newOutputStream();
			if (isStdErr)
			{
				// Set the color
			}
			PrintWriter printWriter = new PrintWriter(newOutputStream);
			BufferedReader reader = new BufferedReader(new InputStreamReader(from));
			String output = null;
			try
			{
				while ((output = reader.readLine()) != null)
				{
					printWriter.println(output);
					printWriter.flush();
				}
			}
			catch (IOException e)
			{
				CommonEditorPlugin.logError("Failed to read output.", e); //$NON-NLS-1$
			}
			finally
			{
				try
				{
					reader.close();
				}
				catch (IOException e)
				{
				}
				printWriter.flush();
				printWriter.close();
			}
		}
	}

}
