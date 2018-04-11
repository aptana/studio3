/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.scripting.commands;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
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
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.BreakIterator;
import java.text.MessageFormat;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.Region;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IOUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.scripting.snippets.SnippetsCompletionProcessor;
import com.aptana.scripting.ScriptLogger;
import com.aptana.scripting.ScriptUtils;
import com.aptana.scripting.model.CommandContext;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.CommandResult;
import com.aptana.scripting.model.InputType;
import com.aptana.scripting.model.InvocationType;

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

	// Delay after which the tooltip is hidden.
	private static final long DELAY = 10000;

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
			this(StringUtil.EMPTY);
		}

		public StringInputProvider(String string)
		{
			this.string = string;
		}

		public InputStream getInputStream()
		{
			try
			{
				// FIXME Use the encoding from the file/document!
				return new ByteArrayInputStream(string.getBytes(IOUtil.UTF_8));
			}
			catch (UnsupportedEncodingException e)
			{
				return new StringBufferInputStream(string);
			}
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
		if (textEditor != null)
		{
			// FIXME This is pretty bad here. What we want is the ISourceViewer of the editor (which is a subinterface
			// of ITextViewer). It just happens that sourceViewer.getTextOperationTarget returns self in this case.
			Object adapter = textEditor.getAdapter(ITextOperationTarget.class);
			if (adapter instanceof ITextViewer)
			{
				textViewer = (ITextViewer) adapter;
			}
		}
		return executeCommand(command, invocationType, textViewer, textEditor);
	}

	public static CommandResult executeCommand(CommandElement command, InvocationType invocationType,
			ITextViewer textViewer, ITextEditor textEditor)
	{
		InputType selected = InputType.UNDEFINED;
		InputType[] inputTypes = command.getInputTypes();
		if (inputTypes == null || inputTypes.length == 0)
		{
			inputTypes = new InputType[] { InputType.UNDEFINED };
		}

		FilterInputProvider filterInputProvider = null;
		if (textViewer != null)
		{
			for (InputType inputType : inputTypes)
			{
				try
				{
					filterInputProvider = getInputProvider(textViewer, command, inputType);
					if (filterInputProvider != null)
					{
						selected = inputType;
						break;
					}
				}
				catch (BadLocationException e)
				{
					IdeLog.logError(CommonEditorPlugin.getDefault(), e);
				}
			}
		}
		if (filterInputProvider == null)
		{
			filterInputProvider = CommandExecutionUtils.EOF;
			selected = inputTypes[0];
		}

		// Create command context
		CommandContext commandContext = command.createCommandContext();

		// Set input stream
		commandContext.setInputStream(filterInputProvider.getInputStream());
		commandContext.put(CommandContext.INPUT_TYPE, selected.getName());

		// Set invocation type
		commandContext.put(CommandContext.INVOKED_VIA, invocationType.getName());

		return command.execute(commandContext);
	}

	protected static FilterInputProvider getInputProvider(ITextViewer textWidget, CommandElement command,
			InputType inputType) throws BadLocationException
	{
		Point selectionRange = textWidget.getSelectedRange();
		switch (inputType)
		{
		// TODO Move this logic into the enum itself
			case UNDEFINED:
			case NONE:
				return CommandExecutionUtils.EOF;
			case SELECTION:
				if (selectionRange.y == 0)
					return null;
				IRegion selectedRegion = getSelectedRegion(textWidget);
				return new CommandExecutionUtils.StringInputProvider(textWidget.getDocument().get(
						selectedRegion.getOffset(), selectedRegion.getLength()));
			case SELECTED_LINES:
				if (selectionRange.y == 0)
					return null;
				IRegion region = getSelectedLinesRegion(textWidget);
				return new CommandExecutionUtils.StringInputProvider(textWidget.getDocument().get(region.getOffset(),
						region.getLength()));
			case DOCUMENT:
				return new CommandExecutionUtils.StringInputProvider(textWidget.getDocument().get());
			case LEFT_CHAR:
				if (getCaretOffset(textWidget) < 1)
					return null;
				return new CommandExecutionUtils.StringInputProvider(textWidget.getDocument().get(
						getCaretOffset(textWidget) - 1, 1));
			case RIGHT_CHAR:
				if (getCaretOffset(textWidget) < getEndOffset(textWidget))
					return new CommandExecutionUtils.StringInputProvider(textWidget.getDocument().get(
							getCaretOffset(textWidget), 1));
				return null;
			case CLIPBOARD:
				String contents = getClipboardContents();
				if (contents == null || contents.trim().length() == 0)
					return null;
				return new CommandExecutionUtils.StringInputProvider(contents);
			case LINE:
				return new CommandExecutionUtils.StringInputProvider(getCurrentLineText(textWidget));
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

	private static String getCurrentLineText(ITextViewer textWidget) throws BadLocationException
	{
		IRegion region = getCurrentLineRegion(textWidget);
		return textWidget.getDocument().get(region.getOffset(), region.getLength());
	}

	public static void processCommandResult(CommandElement command, CommandResult commandResult, ITextEditor textEditor)
	{
		ITextViewer textViewer = null;
		if (textEditor != null)
		{
			Object adapter = textEditor.getAdapter(ITextOperationTarget.class);
			if (adapter instanceof ITextViewer)
			{
				textViewer = (ITextViewer) adapter;
			}
		}
		processCommandResult(command, commandResult, textViewer);
	}

	public static void processCommandResult(CommandElement command, CommandResult commandResult, ITextViewer textViewer)
	{
		if (commandResult == null)
		{
			return;
		}

		if (!commandResult.executedSuccessfully())
		{
			outputToConsole(commandResult);
			return;
		}

		// separate out the commands that require a text editor and the ones that do not
		switch (commandResult.getOutputType())
		{
		// TODO Move this logic into the enum itself!
			case DISCARD:
			case UNDEFINED:
				break;
			case SHOW_AS_HTML:
				showAsHTML(command, commandResult);
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

		if (textViewer == null)
		{
			return;
		}
		try
		{
			final int caretOffset = getCaretOffset(textViewer);
			switch (commandResult.getOutputType())
			{
				case REPLACE_SELECTION:
					if (commandResult.getInputType() == InputType.DOCUMENT)
					{
						replaceDocument(textViewer, commandResult);
					}
					else if (commandResult.getInputType() == InputType.LINE)
					{
						replaceLine(textViewer, commandResult);
					}
					else if (commandResult.getInputType() == InputType.WORD)
					{
						replaceWord(textViewer, commandResult);
					}
					else
					{
						IRegion region = getSelectedRegion(textViewer);
						if (commandResult.getInputType() == InputType.RIGHT_CHAR)
						{
							region = new Region(caretOffset, 1);
						}
						else if (commandResult.getInputType() == InputType.LEFT_CHAR)
						{
							region = new Region(caretOffset - 1, 1);
						}
						else if (commandResult.getInputType() == InputType.SELECTED_LINES)
						{
							region = getSelectedLinesRegion(textViewer);
						}
						replaceTextRange(textViewer, region, commandResult.getOutputString());
					}
					break;
				case REPLACE_SELECTED_LINES:
					IRegion selectedLines = getSelectedLinesRegion(textViewer);
					replaceTextRange(textViewer, selectedLines, commandResult.getOutputString());
					break;
				case REPLACE_LINE:
					replaceLine(textViewer, commandResult);
					break;
				case REPLACE_WORD:
					replaceWord(textViewer, commandResult);
					break;
				case REPLACE_DOCUMENT:
					replaceDocument(textViewer, commandResult);
					break;
				case INSERT_AS_TEXT:
					int offsetToInsert = caretOffset;
					if (commandResult.getInputType() == InputType.SELECTION)
					{
						IRegion region = getSelectedRegion(textViewer);
						offsetToInsert = region.getOffset() + region.getLength();
					}
					else if (commandResult.getInputType() == InputType.SELECTED_LINES)
					{
						IRegion region = getSelectedLinesRegion(textViewer);
						offsetToInsert = region.getOffset() + region.getLength();
					}
					else if (commandResult.getInputType() == InputType.LINE)
					{
						IRegion region = getCurrentLineRegion(textViewer);
						offsetToInsert = region.getOffset() + region.getLength();
					}
					else if (commandResult.getInputType() == InputType.WORD)
					{
						IRegion region = findWordRegion(textViewer);
						offsetToInsert = region.getOffset() + region.getLength();
					}
					else if (commandResult.getInputType() == InputType.RIGHT_CHAR)
					{
						offsetToInsert = caretOffset + 1;
					}
					else if (commandResult.getInputType() == InputType.DOCUMENT)
					{
						offsetToInsert = getEndOffset(textViewer);
					}
					String outputString = commandResult.getOutputString();
					replaceTextRange(textViewer, offsetToInsert, 0, outputString);
					// Need to place cursor at end of inserted text!
					setCaretOffset(textViewer, caretOffset + outputString.length());
					break;
				case INSERT_AS_SNIPPET:
					IRegion region = new Region(caretOffset, 0);
					if (commandResult.getInputType() == InputType.SELECTION)
					{
						region = getSelectedRegion(textViewer);
					}
					else if (commandResult.getInputType() == InputType.SELECTED_LINES)
					{
						region = getSelectedLinesRegion(textViewer);
					}
					else if (commandResult.getInputType() == InputType.DOCUMENT)
					{
						region = new Region(0, getEndOffset(textViewer));
					}
					else if (commandResult.getInputType() == InputType.LINE)
					{
						region = getCurrentLineRegion(textViewer);
					}
					else if (commandResult.getInputType() == InputType.WORD)
					{
						region = findWordRegion(textViewer);
					}
					else if (commandResult.getInputType() == InputType.RIGHT_CHAR)
					{
						region = new Region(caretOffset, 1);
					}
					else if (commandResult.getInputType() == InputType.LEFT_CHAR)
					{
						region = new Region(caretOffset - 1, 1);
					}
					SnippetsCompletionProcessor.insertAsTemplate(textViewer, region, commandResult.getOutputString(),
							commandResult.getCommand());
					break;
				case SHOW_AS_TOOLTIP:
					showAsTooltip(commandResult, textViewer, caretOffset);
					break;
			}
		}
		catch (BadLocationException e)
		{
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
		}
	}

	private static void setCaretOffset(ITextViewer textViewer, int docOffset)
	{
		if (textViewer instanceof ITextViewerExtension5)
		{
			docOffset = ((ITextViewerExtension5) textViewer).modelOffset2WidgetOffset(docOffset);
		}
		textViewer.getTextWidget().setCaretOffset(docOffset);
	}

	private static void replaceTextRange(ITextViewer textViewer, IRegion region, String text)
			throws BadLocationException
	{
		replaceTextRange(textViewer, region.getOffset(), region.getLength(), text);
	}

	private static void replaceTextRange(ITextViewer textViewer, int offset, int length, String text)
			throws BadLocationException
	{
		textViewer.getDocument().replace(offset, length, text);
	}

	private static int getEndOffset(ITextViewer textViewer)
	{
		return textViewer.getDocument().getLength();
	}

	private static int getCaretOffset(ITextViewer textViewer)
	{
		StyledText textWidget = textViewer.getTextWidget();
		int caretOffset = textWidget.getCaretOffset();
		if (textViewer instanceof ITextViewerExtension5)
		{
			ITextViewerExtension5 extension = (ITextViewerExtension5) textViewer;
			return extension.widgetOffset2ModelOffset(caretOffset);
		}
		return caretOffset;
	}

	protected static void replaceWord(ITextViewer textWidget, CommandResult commandResult) throws BadLocationException
	{
		IRegion wordRegion = findWordRegion(textWidget);
		replaceTextRange(textWidget, wordRegion, commandResult.getOutputString());
	}

	private static IRegion getSelectedLinesRegion(ITextViewer textWidget) throws BadLocationException
	{
		Point selectionRange = textWidget.getSelectedRange();
		int startLine = textWidget.getDocument().getLineOfOffset(selectionRange.x);
		int endLine = textWidget.getDocument().getLineOfOffset(selectionRange.x + selectionRange.y);

		int startOffset = textWidget.getDocument().getLineOffset(startLine);
		IRegion endRegion = textWidget.getDocument().getLineInformation(endLine);
		int endOffset = endRegion.getOffset() + endRegion.getLength();
		return new Region(startOffset, endOffset - startOffset);
	}

	private static IRegion getSelectedRegion(ITextViewer textWidget)
	{
		return new Region(textWidget.getSelectedRange().x, textWidget.getSelectedRange().y);
	}

	protected static IRegion getCurrentLineRegion(ITextViewer textWidget) throws BadLocationException
	{
		final int caretOffset = getCaretOffset(textWidget);
		int lineAtCaret = textWidget.getDocument().getLineOfOffset(caretOffset);
		return textWidget.getDocument().getLineInformation(lineAtCaret);
	}

	protected static void replaceDocument(ITextViewer textWidget, CommandResult commandResult)
	{
		textWidget.getDocument().set(commandResult.getOutputString());
	}

	protected static void replaceLine(ITextViewer textWidget, CommandResult commandResult) throws BadLocationException
	{
		IRegion region = getCurrentLineRegion(textWidget);
		String output = commandResult.getOutputString();
		replaceTextRange(textWidget, region, output);
		setCaretOffset(textWidget, region.getOffset() + output.length());
	}

	private static void outputToConsole(CommandResult commandResult)
	{
		String outputString = commandResult.getOutputString();
		if (outputString != null)
		{
			ScriptLogger.print(outputString);
		}
		// Dump any errors or warning to the output
		String errorString = commandResult.getErrorString();
		if (errorString != null)
		{
			ScriptLogger.printError(errorString);
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
		copyToClipboard(commandResult.getOutputString());
	}

	public static void copyToClipboard(String contents)
	{
		getClipboard().setContents(new Object[] { contents }, new Transfer[] { TextTransfer.getInstance() });
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
			IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.openEditor(input, DEFAULT_TEXT_EDITOR_ID);
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
				IdeLog.logError(CommonEditorPlugin.getDefault(), e);
			}

		}
		catch (PartInitException e)
		{
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
		}
	}

	private static void showAsTooltip(CommandResult commandResult, ITextViewer textViewer, int caretOffset)
	{
		String output = commandResult.getOutputString();
		if (output == null || output.trim().length() == 0)
			return;
		DefaultInformationControl tooltip = new DefaultInformationControl(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), NLS.bind(
				Messages.CommandExecutionUtils_ClickToFocusTypeEscapeToDismissWhenFocused, DELAY / 1000), null)
		{
			@Override
			public void setVisible(boolean visible)
			{
				super.setVisible(visible);

				if (visible)
				{
					final Shell shell = getShell();
					final UIJob hideJob = new UIJob("Hide tooltip") //$NON-NLS-1$
					{
						@Override
						public IStatus runInUIThread(IProgressMonitor monitor)
						{
							if (isVisible())
							{
								setVisible(false);
							}
							return Status.OK_STATUS;
						}
					};
					hideJob.setPriority(Job.INTERACTIVE);
					EclipseUtil.setSystemForJob(hideJob);
					hideJob.schedule(DELAY);

					shell.addShellListener(new ShellAdapter()
					{
						@Override
						public void shellDeactivated(ShellEvent e)
						{
							// Hide
							setVisible(false);
						}

						@Override
						public void shellActivated(ShellEvent e)
						{
							// Cancel the job
							hideJob.cancel();
						}
					});
				}
			}
		};
		tooltip.setInformation(output);
		Point p = tooltip.computeSizeHint();
		tooltip.setSize(p.x, p.y);

		StyledText textWidget = textViewer.getTextWidget();
		caretOffset = textWidget.getCaretOffset();

		Point locationAtOffset = textWidget.getLocationAtOffset(caretOffset);
		Rectangle bounds = textWidget.getClientArea();
		// Is caret visible in the client area
		if (bounds.contains(locationAtOffset))
		{
			// Show the tooltip near it
			locationAtOffset = textWidget.toDisplay(locationAtOffset.x,
					locationAtOffset.y + textWidget.getLineHeight(caretOffset) + 2);
		}
		else
		{
			// Is y offset in the client area
			if (locationAtOffset.y > bounds.y && locationAtOffset.y < bounds.y + bounds.height)
			{
				// Show the tooltip near left margin below the current line
				locationAtOffset = textWidget.toDisplay(bounds.x + 2,
						locationAtOffset.y + textWidget.getLineHeight(caretOffset) + 2);
			}
			else
			{
				int topIndex = textWidget.getTopIndex();
				int offsetAtLine = textWidget.getOffsetAtLine(topIndex);
				locationAtOffset = textWidget.getLocationAtOffset(offsetAtLine);
				// Show the tool tip below first visible line
				locationAtOffset = textWidget.toDisplay(locationAtOffset.x + 2,
						locationAtOffset.y + textWidget.getLineHeight(caretOffset) + 2);
			}
		}
		tooltip.setLocation(locationAtOffset);
		tooltip.setVisible(true);
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
			IdeLog.logError(CommonEditorPlugin.getDefault(),
					Messages.CommandExecutionUtils_CouldNotCreateTemporaryFile, e);
		}
		if (tempHmtlFile != null)
		{
			tempHmtlFile.deleteOnExit();
			PrintWriter pw = null;
			try
			{
				pw = new PrintWriter(tempHmtlFile);
			}
			catch (FileNotFoundException e)
			{
				IdeLog.logError(CommonEditorPlugin.getDefault(), e);
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
								null, // Set the name to null. That way the browser tab will display the title of page
										// loaded in the browser.
								command.getDisplayName()).openURL(url);
					}
					else
					{
						support.getExternalBrowser().openURL(url);
					}
				}
				catch (PartInitException e)
				{
					IdeLog.logError(CommonEditorPlugin.getDefault(), e);
				}
				catch (MalformedURLException e)
				{
					IdeLog.logError(CommonEditorPlugin.getDefault(), e);
				}
			}
		}
	}

	private static String findWord(ITextViewer textWidget) throws BadLocationException
	{
		IRegion region = findWordRegion(textWidget);
		return textWidget.getDocument().get(region.getOffset(), region.getLength());
	}

	private static IRegion findWordRegion(ITextViewer textWidget) throws BadLocationException
	{
		int caretOffset = getCaretOffset(textWidget);
		int lineAtCaret = textWidget.getDocument().getLineOfOffset(caretOffset);
		IRegion lineInfo = textWidget.getDocument().getLineInformation(lineAtCaret);
		String currentLine = textWidget.getDocument().get(lineInfo.getOffset(), lineInfo.getLength());
		int lineOffset = textWidget.getDocument().getLineOffset(lineAtCaret);
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
			{
				start = offset;
			}
			else
			{
				end = offset;
			}
		}

		if (end == start)
		{
			return new Region(start, 0);
		}
		return new Region(start, end - start);
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
				IdeLog.logError(CommonEditorPlugin.getDefault(), "Failed to read output.", e); //$NON-NLS-1$
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
