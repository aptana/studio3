package com.aptana.editor.scripting.actions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringBufferInputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.WeakHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.editor.scripting.Activator;

@SuppressWarnings("deprecation")
public class Filter {
	static final String DEFAULT_CONSOLE_NAME = "Eclipse Mate";

	public static Map<String, String> computeEnvironment(IWorkbenchWindow workbenchWindow, IEditorPart editorPart) {
		Map<String, String> environment = new TreeMap<String, String>();
		
		if (editorPart instanceof ITextEditor) {
			ITextEditor abstractTextEditor = (ITextEditor) editorPart;
			IEditorInput editorInput = abstractTextEditor.getEditorInput();
			if (editorInput instanceof IFileEditorInput) {
				IFileEditorInput fileEditorInput = (IFileEditorInput) editorInput;
				IFile iFile = fileEditorInput.getFile();
				if (iFile != null) {
					environment.put(VARIABLES_NAMES.TM_SELECTED_FILE.name(), iFile.getLocation().toFile().getAbsolutePath());
					environment.put(VARIABLES_NAMES.TM_FILEPATH.name(), iFile.getLocation().toFile().getAbsolutePath());
					environment.put(VARIABLES_NAMES.TM_DIRECTORY.name(), iFile.getParent().getLocation().toFile().getAbsolutePath());
					environment.put(VARIABLES_NAMES.TM_PROJECT_DIRECTORY.name(), iFile.getProject().getLocation().toFile().getAbsolutePath());
					ISelectionProvider selectionProvider = abstractTextEditor.getSelectionProvider();
					ISelection selection = selectionProvider.getSelection();
					if (selection instanceof ITextSelection) {
						ITextSelection textSelection = (ITextSelection) selection;
						environment.put(VARIABLES_NAMES.TM_SELECTED_TEXT.name(), textSelection.getText());
						environment.put(VARIABLES_NAMES.TM_LINE_NUMBER.name(), String.valueOf(textSelection.getStartLine() + 1));
						environment.put(VARIABLES_NAMES.TM_SELECTION_OFFSET.name(), String.valueOf(textSelection.getOffset()));
						environment.put(VARIABLES_NAMES.TM_SELECTION_LENGTH.name(), String.valueOf(textSelection.getLength()));
						environment.put(VARIABLES_NAMES.TM_SELECTION_START_LINE_NUMBER.name(), String.valueOf(textSelection.getStartLine()));
						environment.put(VARIABLES_NAMES.TM_SELECTION_END_LINE_NUMBER.name(), String.valueOf(textSelection.getEndLine()));
						Object adapter = (Control) abstractTextEditor.getAdapter(Control.class);
						if (adapter instanceof Control) {
							Control control = (Control) adapter;
							if (control instanceof StyledText) {
								StyledText styledText = (StyledText) control;
								environment.put(VARIABLES_NAMES.TM_LINE_INDEX.name(), String.valueOf(textSelection.getOffset() - styledText.getOffsetAtLine(textSelection.getStartLine())));
								int caretOffset = styledText.getCaretOffset();
								int lineAtCaret = styledText.getLineAtOffset(caretOffset);
								environment.put(VARIABLES_NAMES.TM_CARET_LINE_NUMBER.name(), String.valueOf(lineAtCaret + 1));
								environment.put(VARIABLES_NAMES.TM_CARET_LINE_TEXT.name(), styledText.getLine(lineAtCaret));
							}
						}
					}
				}
			}
		} else {
			
		}
		return environment;
	}
	
	public static void launch(String command, 
			Map<String, String> environment,
			FilterInputProvider filterInputProvider) {
		launch(command,
				environment,
				filterInputProvider,
				new EclipseConsolePrintStreamOutputConsumer());
	};
	
	public static void launch(String command,
			Map<String, String> environment,
			FilterInputProvider filterInputProvider,
			FilterOutputConsumer filterSTDOUTConsumerProvider) {
		launch(command,
				environment,
				filterInputProvider,
				filterSTDOUTConsumerProvider,
				new EclipseConsolePrintStreamOutputConsumer());
	};
	
	public static void launch(final String command,
			final Map<String, String> environment,
			final FilterInputProvider filterInputProvider,
			final FilterOutputConsumer filterSTDOUTConsumerProvider,
			final FilterOutputConsumer filterSTDERRConsumerProvider) {
		// Launch command on a separate thread.
		new Thread(new Runnable() {
			public void run() {
				Activator activator = Activator.getDefault();
				List<String> commandList = new LinkedList<String>();
				
				if (Platform.OS_LINUX.equals(Platform.getOS()) || Platform.OS_MACOSX.equals(Platform.getOS())) {
					String shell = environment.get("SHELL");
					if (shell == null) {
						shell = "/bin/bash";
					}
					commandList.add(shell);
					commandList.add("-c");
				} else if (Platform.OS_WIN32.equals(Platform.getOS())){
					commandList.add("cmd");
					commandList.add("/C");
					commandList.add("start");
				}
				commandList.add(command);
				//Utilities.parseParameters(command);
				try {
					
					ProcessBuilder processBuilder = new ProcessBuilder();
					processBuilder.command(commandList);
					Map<String, String> inheritedEnvironment = processBuilder.environment();
					if (environment != null) {
						inheritedEnvironment.putAll(environment);
					}
					
					final Process process = processBuilder.start();
					filterSTDOUTConsumerProvider.consume(process.getInputStream());
					filterSTDERRConsumerProvider.consume(process.getErrorStream());
					
					// Input stream pump
					new Thread(new Runnable() {
						public void run() {
							InputStream is = filterInputProvider.getInputStream();
							OutputStream os = process.getOutputStream();
							byte bytes[] = new byte[1024];
							while (true) {
								try {
									int readCount = is.read(bytes);
									if (readCount == -1 || new String(bytes, 0, readCount).toLowerCase().startsWith("[eof]")) {
										os.flush();
										os.close();
										break;
									}
									os.write(bytes, 0, readCount);
									os.flush();
								} catch (IOException e) {
									// TODO
									break;
								}
							}
						}
					}).start();
					
					int status = process.waitFor();
					if (status == 0) {
						// Good
					} else {
						activator.getLog().log(
								new Status(IStatus.ERROR, activator.getBundle()
										.getSymbolicName(), "Process '"
										+ commandList.toString()
										+ "' exited with status: " + status));
					}
				} catch (InterruptedException ex) {
					activator.getLog().log(
							new Status(IStatus.ERROR, activator.getBundle()
									.getSymbolicName(),
									"Exception while executing '"
											+ commandList.toString() + "'", ex));
				} catch (IOException ioe) {
					activator.getLog().log(
							new Status(IStatus.ERROR, activator.getBundle()
									.getSymbolicName(),
									"Exception while executing '"
											+ commandList.toString() + "'", ioe));
				}

			}
		}, "Launching - " + command).start();
	}
	
	public interface FilterInputProvider {
		public InputStream getInputStream();
	};
	
	public interface FilterOutputConsumer {
		public void consume(InputStream input);
	}
	
	public static class StringInputProvider implements FilterInputProvider {
		private final String string;

		public StringInputProvider() {
			this("");
		}
		
		public StringInputProvider(String string) {
			this.string = string;
		}

		public InputStream getInputStream() {
			return new StringBufferInputStream(string);
		}
	}

	public static final FilterInputProvider EOF = new StringInputProvider();
	
	
	public static class EclipseConsoleInputProvider  implements FilterInputProvider {
		private final String consoleName;

		public EclipseConsoleInputProvider() {
			this(DEFAULT_CONSOLE_NAME);
		}
		
		public EclipseConsoleInputProvider(String consoleName) {
			this.consoleName = consoleName;
		}

		public InputStream getInputStream() {
			IOConsole messageConsole = getMessageConsole(consoleName);
			messageConsole.activate();
			return messageConsole.getInputStream();
		}
	}
	
	public static class PrintStreamOutputConsumer implements FilterOutputConsumer
	{
		private PrintStream printStream;
		
		public PrintStreamOutputConsumer() {
		}
		
		public PrintStreamOutputConsumer(PrintStream printStream) {
			this.printStream = printStream;
		}
		
		public PrintStream getPrintStream() {
			return printStream;
		}
		
		protected void setPrintStream(PrintStream printStream) {
			this.printStream = printStream;
		}

		public void consume(final InputStream outputStream) {
			new Thread(new Runnable() {
				public void run() {
					BufferedReader br = new BufferedReader(new InputStreamReader(outputStream));
					String line = null;
					try {
						while ((line = br.readLine()) != null) {
							if (printStream != null) {
								printStream.println(line);
								printStream.flush();
							}
						}
					} catch (IOException e) {
					} finally {
						printStream.close();
					}
				}
			}).start();
		}
	}
	
	public static class StringOutputConsumer implements FilterOutputConsumer{
		private BlockingQueue<String> outputQueue;
		
		public StringOutputConsumer() {
			this(new ArrayBlockingQueue<String>(1));
		}
		
		public StringOutputConsumer(BlockingQueue<String> outputQueue) {
			this.outputQueue = outputQueue;
		}

		public void consume(final InputStream outputStream) {
			new Thread(new Runnable() {
				public void run() {
					StringBuilder stringBuilder = new StringBuilder();
					BufferedReader br = new BufferedReader(new InputStreamReader(outputStream));
					String line = null;
					try {
						while ((line = br.readLine()) != null) {
							stringBuilder.append(line + "\n");
						}
					} catch (IOException e) {
					} finally {
						outputQueue.add(stringBuilder.toString());
					}
				}
			}).start();
		}
		
		public String getOutput() throws InterruptedException {
			return outputQueue.take();
		}
	}
	
	public static class EclipseConsolePrintStreamOutputConsumer implements FilterOutputConsumer {
		private final String consoleName;
		private final boolean isStdErr;

		public EclipseConsolePrintStreamOutputConsumer() {
			this(DEFAULT_CONSOLE_NAME, false);
		}
		
		public EclipseConsolePrintStreamOutputConsumer(String consoleName) {
			this(consoleName, false);
		}
		
		public EclipseConsolePrintStreamOutputConsumer(boolean err) {
			this(DEFAULT_CONSOLE_NAME, err);
		}
		
		public EclipseConsolePrintStreamOutputConsumer(String consoleName, boolean isStdErr) {
			this.consoleName = consoleName;
			this.isStdErr = isStdErr;
		}
		
		public void consume(InputStream outputStream) {
			IOConsole messageConsole = getMessageConsole(consoleName);
			messageConsole.activate();
			MessageConsoleWriter messageConsoleWriter =
				new MessageConsoleWriter(messageConsole, outputStream, isStdErr);
			new Thread(messageConsoleWriter).start();
		}
	}
	
//	public class NamedConsolePrintStreamOutputConsumer extends PrintStreamOutputConsumer {
//		
//	}
	
	public static final FilterOutputConsumer DISCARD = new PrintStreamOutputConsumer();
	
	public static final FilterOutputConsumer TO_SYSOUT = new PrintStreamOutputConsumer(System.out);
	
	public static final FilterOutputConsumer TO_SYSERR = new PrintStreamOutputConsumer(System.err);	
	
	private static Map<String, IOConsole> nameToMessageConsole = new WeakHashMap<String, IOConsole>();

	@SuppressWarnings("unused")
	private static IOConsole getMessageConsole() {
		return getMessageConsole(DEFAULT_CONSOLE_NAME);
	}
	
	private static IOConsole getMessageConsole(String name) {
		IOConsole messageConsole = nameToMessageConsole.get(name);
		if (messageConsole == null) {
			messageConsole = new IOConsole(name, null, null, true);
			ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[]{messageConsole});
			nameToMessageConsole.put(name, messageConsole);
		}
		return messageConsole;
	}
	
	private static class MessageConsoleWriter implements Runnable {		
		private final IOConsole messageConsole;
		private final InputStream from;
		private final boolean isStdErr;
		
		private MessageConsoleWriter(IOConsole messageConsole, InputStream from, boolean isStdErr) {
			this.messageConsole = messageConsole;
			this.from = from;
			this.isStdErr = isStdErr;
		}
		
		public void run() {
			IOConsoleOutputStream newOutputStream = messageConsole.newOutputStream();
			if (isStdErr) {
				// Set the color
			}
			PrintWriter printWriter = new PrintWriter(newOutputStream);
			BufferedReader reader = new BufferedReader(new InputStreamReader(from));
			String output = null;
			try {
				while ((output = reader.readLine()) != null) {
					printWriter.println(output);
					printWriter.flush();
				}
			} catch (IOException e) {
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
				}
				printWriter.flush();
				printWriter.close();
			}
		}		
	}

}
