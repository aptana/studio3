package com.aptana.editor.common.scripting.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringBufferInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.BreakIterator;
import java.text.MessageFormat;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
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
import com.aptana.scripting.ScriptLogger;
import com.aptana.scripting.ScriptUtils;
import com.aptana.scripting.model.CommandContext;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.CommandResult;
import com.aptana.scripting.model.InputType;
import com.aptana.scripting.model.InvocationType;
import com.aptana.scripting.model.OutputType;

//import com.aptana.scripting.ui.ScriptingConsole;

@SuppressWarnings("deprecation")
public class CommandExecutionUtils
{

	/**
	 * Name used for new document created as output of command execution.
	 */
	private static final String NEW_DOCUMENT_TITLE = "Untitled.txt"; //$NON-NLS-1$

	/**
	 * ID of Editor used to open new document created as output of command execution.
	 */
	private static final String DEFAULT_TEXT_EDITOR_ID = "org.eclipse.ui.DefaultTextEditor"; //$NON-NLS-1$

	/**
	 * File extension used for temporary files generated to show output as HTML.
	 */
	private static final String HTML_FILE_EXTENSION = ".html"; //$NON-NLS-1$

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

	public static class FileInputProvider implements FilterInputProvider
	{
		private final String path;

		public FileInputProvider(String path)
		{
			this.path = path;
		}

		public InputStream getInputStream()
		{
			InputStream result = null;

			try
			{
				result = new FileInputStream(this.path);
			}
			catch (FileNotFoundException e)
			{
				String message = MessageFormat.format(Messages.CommandExecutionUtils_Input_File_Does_Not_Exist,
						new Object[] { path });

				ScriptUtils.logErrorWithStackTrace(message, e);
			}

			return result;
		}
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

	public static CommandResult executeCommand(CommandElement command, InvocationType invocationType,
			ITextEditor textEditor)
	{
		ITextViewer textViewer = null;
		Object adapter = textEditor.getAdapter(ITextOperationTarget.class);
		if (adapter instanceof ITextViewer)
		{
			textViewer = (ITextViewer) adapter;
		}
		return executeCommand(command, invocationType, textViewer, textEditor);
	}

	public static CommandResult executeCommand(CommandElement command, InvocationType invocationType,
			ITextViewer textViewer, ITextEditor textEditor)
	{
		StyledText textWidget = textViewer.getTextWidget();
		FilterInputProvider filterInputProvider = null;

		InputType selected = InputType.UNDEFINED;
		InputType[] inputTypes = command.getInputTypes();
		if (inputTypes == null || inputTypes.length == 0)
		{
			inputTypes = new InputType[] { InputType.UNDEFINED };
		}
		for (InputType inputType : inputTypes)
		{
			filterInputProvider = getInputProvider(textWidget, command, inputType);
			if (filterInputProvider != null)
			{
				selected = inputType;
				break;
			}
		}
		if (filterInputProvider == null)
		{
			filterInputProvider = CommandExecutionUtils.EOF;
			selected = InputType.UNDEFINED;
		}

		// Create command context
		CommandContext commandContext = command.createCommandContext();

		// Set input stream
		commandContext.setInputStream(filterInputProvider.getInputStream());
		commandContext.put(CommandContext.INPUT_TYPE, selected.toString());

		// Set invocation type
		commandContext.put(CommandContext.INVOKED_VIA, invocationType.getName());

		return command.execute(commandContext);
	}

	protected static FilterInputProvider getInputProvider(StyledText textWidget, CommandElement command,
			InputType inputType)
	{
		Point selectionRange = textWidget.getSelection();
		switch (inputType)
		{
			case SELECTION:
				if (selectionRange.x == selectionRange.y)
					return null;
				return new CommandExecutionUtils.StringInputProvider(textWidget.getSelectionText());
			case SELECTED_LINES:
				if (selectionRange.x == selectionRange.y)
					return null;

				int selectionStartOffsetLine = textWidget.getLineAtOffset(selectionRange.x);
				int selectionEndOffsetLine = textWidget.getLineAtOffset(selectionRange.y);
				int selectionStartOffsetLineStartOffset = textWidget.getOffsetAtLine(selectionStartOffsetLine);
				int selectionEndOffsetLineEndOffset = textWidget.getOffsetAtLine(selectionEndOffsetLine)
						+ textWidget.getLine(selectionEndOffsetLine).length();
				return new CommandExecutionUtils.StringInputProvider(textWidget.getText(
						selectionStartOffsetLineStartOffset, selectionEndOffsetLineEndOffset));
			case DOCUMENT:
				return new CommandExecutionUtils.StringInputProvider(textWidget.getText());
			case CLIPBOARD:
				String contents = getClipboardContents();
				if (contents == null || contents.trim().length() == 0)
					return null;
				return new CommandExecutionUtils.StringInputProvider(contents);
			case LINE:
				return new CommandExecutionUtils.StringInputProvider(textWidget.getLine(textWidget
						.getLineAtOffset(textWidget.getCaretOffset())));
			case WORD:
				String currentWord = findWord(textWidget);
				if (currentWord == null || currentWord.trim().length() == 0)
					return null;
				return new CommandExecutionUtils.StringInputProvider(currentWord);
			case INPUT_FROM_CONSOLE:
				return new CommandExecutionUtils.EclipseConsoleInputProvider(CommandExecutionUtils.DEFAULT_CONSOLE_NAME);
			case INPUT_FROM_FILE:
				return new CommandExecutionUtils.FileInputProvider(command.getInputPath());
		}
		return null;
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
		if (!commandResult.executedSuccessfully())
		{
			return;
		}

		StyledText textWidget = textViewer.getTextWidget();
		final int caretOffset = textWidget.getCaretOffset();
		OutputType ouputType = commandResult.getOutputType(); // OutputType.get(command.getOutputType());
		switch (ouputType)
		{
			case DISCARD:
				break;
			case REPLACE_SELECTION:
				if (commandResult.getInputType() == InputType.DOCUMENT)
				{
					replaceDocument(textWidget, commandResult);
				}
				else if (commandResult.getInputType() == InputType.LINE)
				{
					replaceLine(textWidget, commandResult);
				}
				else
				{
					IRegion region = getSelectedRegion(textWidget);
					if (commandResult.getInputType() == InputType.WORD)
					{
						region = findWordRegion(textWidget);
					}
					textWidget
							.replaceTextRange(region.getOffset(), region.getLength(), commandResult.getOutputString());
				}
				break;
			case REPLACE_SELECTED_LINES:
				IRegion selectedLines = getSelectedLinesRegion(textWidget);
				textWidget.replaceTextRange(selectedLines.getOffset(), selectedLines.getLength(), commandResult
						.getOutputString());
				break;
			case REPLACE_LINE:
				replaceLine(textWidget, commandResult);
				break;
			case REPLACE_DOCUMENT:
				replaceDocument(textWidget, commandResult);
				break;
			case INSERT_AS_TEXT:
				int offsetToInsert = caretOffset;
				if (commandResult.getInputType() == InputType.SELECTION)
				{
					IRegion region = getSelectedRegion(textWidget);
					offsetToInsert = region.getOffset() + region.getLength();
				}
				else if (commandResult.getInputType() == InputType.LINE)
				{
					IRegion region = getCurrentLineRegion(textWidget);
					offsetToInsert = region.getOffset() + region.getLength();
				}
				String outputString = commandResult.getOutputString();
				textWidget.replaceTextRange(offsetToInsert, 0, outputString);
				// Need to place cursor at end of inserted text!
				textWidget.setCaretOffset(caretOffset + outputString.length());
				break;
			case INSERT_AS_SNIPPET:
				IRegion region = new Region(caretOffset, 0);
				if (commandResult.getInputType() == InputType.SELECTION)
				{
					region = getSelectedRegion(textWidget);
				}
				else if (commandResult.getInputType() == InputType.SELECTED_LINES)
				{
					region = getSelectedLinesRegion(textWidget);
				}
				else if (commandResult.getInputType() == InputType.DOCUMENT)
				{
					region = new Region(0, textWidget.getCharCount());
				}
				else if (commandResult.getInputType() == InputType.LINE)
				{
					region = getCurrentLineRegion(textWidget);
				}
				else if (commandResult.getInputType() == InputType.WORD)
				{
					region = findWordRegion(textWidget);
				}
				SnippetsCompletionProcessor.insertAsTemplate(textViewer, region, commandResult.getOutputString());
				break;
			case SHOW_AS_HTML:
				showAsHTML(command, commandResult);
				break;
			case SHOW_AS_TOOLTIP:
				showAsTooltip(commandResult, textWidget, caretOffset);
				break;
			case CREATE_NEW_DOCUMENT:
				createNewDocument(commandResult);
				break;
			case COPY_TO_CLIPBOARD:
				copyToClipboard(commandResult);
				break;
			case OUTPUT_TO_CONSOLE:
				outputToConsole(commandResult);
				break;
			case OUTPUT_TO_FILE:
				outputToFile(commandResult);
				break;
		}
	}

	private static IRegion getSelectedLinesRegion(StyledText textWidget)
	{
		Point selectionRange = textWidget.getSelection();
		int selectionStartOffsetLine = textWidget.getLineAtOffset(selectionRange.x);
		int selectionEndOffsetLine = textWidget.getLineAtOffset(selectionRange.y);

		int selectionStartOffsetLineStartOffset = textWidget.getOffsetAtLine(selectionStartOffsetLine);
		int selectionEndOffsetLineEndOffset = textWidget.getOffsetAtLine(selectionEndOffsetLine)
				+ textWidget.getLine(selectionEndOffsetLine).length();
		return new Region(selectionStartOffsetLineStartOffset, selectionEndOffsetLineEndOffset
				- selectionStartOffsetLineStartOffset);
	}

	private static IRegion getSelectedRegion(StyledText textWidget)
	{
		Point selectionRange = textWidget.getSelection();
		int start = Math.min(selectionRange.x, selectionRange.y);
		int end = Math.max(selectionRange.x, selectionRange.y);
		return new Region(start, end - start);
	}

	protected static IRegion getCurrentLineRegion(StyledText textWidget)
	{
		final int caretOffset = textWidget.getCaretOffset();
		int lineAtCaret = textWidget.getLineAtOffset(caretOffset);
		int lineLength = textWidget.getLine(lineAtCaret).length();
		return new Region(textWidget.getOffsetAtLine(lineAtCaret), lineLength);
	}

	protected static void replaceDocument(StyledText textWidget, CommandResult commandResult)
	{
		textWidget.setText(commandResult.getOutputString());
	}

	protected static void replaceLine(StyledText textWidget, CommandResult commandResult)
	{
		IRegion region = getCurrentLineRegion(textWidget);
		textWidget.replaceTextRange(region.getOffset(), region.getLength(), commandResult.getOutputString());
	}

	private static void outputToConsole(CommandResult commandResult)
	{
		ScriptLogger.print(commandResult.getOutputString());
		if (!commandResult.executedSuccessfully())
		{
			// Dump the error output if any
			ScriptLogger.printError(commandResult.getErrorString());
		}
	}

	private static void outputToFile(CommandResult commandResult)
	{
		FileWriter writer = null;
		String path = commandResult.getCommand().getOutputPath();

		try
		{
			writer = new FileWriter(path);
			writer.write(commandResult.getOutputString());
		}
		catch (IOException e)
		{
			String message = MessageFormat.format(Messages.CommandExecutionUtils_Unable_To_Write_To_Output_File,
					new Object[] { path });

			ScriptUtils.logErrorWithStackTrace(message, e);
		}
		finally
		{
			if (writer != null)
			{
				try
				{
					writer.close();
				}
				catch (IOException e)
				{
				}
			}
		}
	}

	private static void copyToClipboard(CommandResult commandResult)
	{
		getClipboard().setContents(new Object[] { commandResult.getOutputString() },
				new Transfer[] { TextTransfer.getInstance() });
	}

	private static String getClipboardContents()
	{
		return (String) getClipboard().getContents(TextTransfer.getInstance());
	}

	protected static Clipboard getClipboard()
	{
		Display display = Display.getCurrent();
		if (display == null)
		{
			display = Display.getDefault();
		}
		return new Clipboard(display);
	}

	private static void createNewDocument(CommandResult commandResult)
	{
		File file = Utilities.getFile();
		IEditorInput input = Utilities.createFileEditorInput(file, NEW_DOCUMENT_TITLE);
		try
		{
			IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input,
					DEFAULT_TEXT_EDITOR_ID);
			if (!(part instanceof ITextEditor))
				return;
			ITextEditor openedTextEditor = (ITextEditor) part;
			IDocumentProvider dp = openedTextEditor.getDocumentProvider();
			if (dp == null)
				return;
			IDocument doc = dp.getDocument(openedTextEditor.getEditorInput());
			if (doc == null)
				return;
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
		catch (PartInitException e)
		{
			CommonEditorPlugin.logError(e);
		}
	}

	private static void showAsTooltip(CommandResult commandResult, StyledText textWidget, final int caretOffset)
	{
		String output = commandResult.getOutputString();
		if (output == null || output.trim().length() == 0)
			return;
		DefaultInformationControl tooltip = new DefaultInformationControl(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), Messages.CommandExecutionUtils_TypeEscapeToDismiss, null);
		tooltip.setInformation(output);
		Point p = tooltip.computeSizeHint();
		tooltip.setSize(p.x, p.y);

		Point locationAtOffset = textWidget.getLocationAtOffset(caretOffset);
		locationAtOffset = textWidget.toDisplay(locationAtOffset.x, locationAtOffset.y
				+ textWidget.getLineHeight(caretOffset) + 2);
		tooltip.setLocation(locationAtOffset);
		tooltip.setVisible(true);
//		tooltip.setFocus();
	}

	private static void showAsHTML(CommandElement command, CommandResult commandResult)
	{
		String output = commandResult.getOutputString();
		if (output == null || output.trim().length() == 0)
			return; // Don't open a browser when there's no content
		File tempHmtlFile = null;
		try
		{
			tempHmtlFile = File.createTempFile(CommonEditorPlugin.PLUGIN_ID, HTML_FILE_EXTENSION);
		}
		catch (IOException e)
		{
			CommonEditorPlugin.logError(Messages.CommandExecutionUtils_CouldNotCreateTemporaryFile, e);
		}
		if (tempHmtlFile != null)
		{
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
										| IWorkbenchBrowserSupport.AS_EDITOR | IWorkbenchBrowserSupport.STATUS, "", //$NON-NLS-1$
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
	}

	private static String findWord(StyledText textWidget)
	{
		IRegion region = findWordRegion(textWidget);
		return textWidget.getTextRange(region.getOffset(), region.getLength());
	}

	private static IRegion findWordRegion(StyledText textWidget)
	{
		int caretOffset = textWidget.getCaretOffset();
		int lineAtCaret = textWidget.getLineAtOffset(caretOffset);
		String currentLine = textWidget.getLine(lineAtCaret);
		int lineOffset = textWidget.getOffsetAtLine(lineAtCaret);
		int offsetInLine = caretOffset - lineOffset;
		IRegion region = findWordRegion(currentLine, offsetInLine);
		return new Region(region.getOffset() + lineOffset, region.getLength());
	}

	/**
	 * Tries to find the word at the given offset.
	 * 
	 * @param line
	 *            the line
	 * @param offset
	 *            the offset
	 * @return the word or <code>null</code> if none
	 */
	protected static IRegion findWordRegion(String line, int offset)
	{
		BreakIterator breakIter = BreakIterator.getWordInstance();
		breakIter.setText(line);

		int start = breakIter.preceding(offset);
		if (start == BreakIterator.DONE)
			start = 0;

		int end = breakIter.following(offset);
		if (end == BreakIterator.DONE)
			end = line.length();

		if (breakIter.isBoundary(offset))
		{
			if (end - offset > offset - start)
				start = offset;
			else
				end = offset;
		}

		if (end == start)
			return new Region(start, 0);
		return new Region(start, end - start);
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
