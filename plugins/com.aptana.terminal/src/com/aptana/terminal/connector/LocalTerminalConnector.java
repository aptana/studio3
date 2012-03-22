/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable closeInFinally
// $codepro.audit.disable closeWhereCreated
// $codepro.audit.disable disallowSleepInsideWhile

package com.aptana.terminal.connector;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.debug.internal.core.StreamsProxy;
import org.eclipse.tm.internal.terminal.provisional.api.ITerminalControl;
import org.eclipse.tm.internal.terminal.provisional.api.TerminalState;
import org.eclipse.tm.internal.terminal.provisional.api.provider.TerminalConnectorImpl;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.FileUtil;
import com.aptana.core.util.IOUtil;
import com.aptana.core.util.PlatformUtil;
import com.aptana.core.util.PlatformUtil.ProcessItem;
import com.aptana.terminal.IProcessConfiguration;
import com.aptana.terminal.TerminalPlugin;
import com.aptana.terminal.internal.IProcessListener;
import com.aptana.terminal.internal.ProcessConfigurations;
import com.aptana.terminal.internal.ProcessLauncher;
import com.aptana.terminal.internal.StreamsProxyOutputStream;

/**
 * @author Max Stepanov
 */
@SuppressWarnings("restriction")
public class LocalTerminalConnector extends TerminalConnectorImpl implements IProcessListener, IOutputFilter {

	public static final String ID = "com.aptana.terminal.connector.local"; //$NON-NLS-1$

	protected static final String ENCODING = IOUtil.UTF_8;
	private static final char DLE = '\u0010';
	private static final int PROCESS_LIST_TIMEOUT = 1500;

	// TODO: These shouldn't be in here. We're pulling the values from the explorer plugin
	// so as not to create a dependency on the two projects.
	private static final String ACTIVE_PROJECT_PROPERTY = "activeProject"; //$NON-NLS-1$
	private static final String EXPLORER_PLUGIN_ID = "com.aptana.explorer"; //$NON-NLS-1$

	private static final String USER_HOME_PROPERTY = "user.home"; //$NON-NLS-1$

	private ProcessLauncher processLauncher;
	private StreamsProxy streamsProxy;
	private OutputStream processInputStream;

	private int currentWidth = 0;
	private int currentHeight = 0;

	private StringBuffer filteredSequence = new StringBuffer();
	private List<Integer> processList = new ArrayList<Integer>();

	private IPath initialDirectory;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.provisional.api.provider.TerminalConnectorImpl#getSettingsSummary()
	 */
	@Override
	public String getSettingsSummary() {
		return "TODO - LocalTerminalConnector.getSettingsSummary()"; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.provisional.api.provider.TerminalConnectorImpl#getTerminalToRemoteStream()
	 */
	@Override
	public OutputStream getTerminalToRemoteStream() {
		return processInputStream;
	}

	@Override
	public void connect(final ITerminalControl control) {
		super.connect(control);
		control.setState(TerminalState.CONNECTING);
		new Thread("Terminal Process Launcher") { //$NON-NLS-1$
			@Override
			public void run() {
				if (startProcess(control)) {
					control.setState(TerminalState.CONNECTED);
				} else {
					control.setState(TerminalState.CLOSED);
				}
			}
		}.start();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.provisional.api.provider.TerminalConnectorImpl#setTerminalSize(int, int)
	 */
	@Override
	public void setTerminalSize(int newWidth, int newHeight) {
		if (currentWidth == newWidth && currentHeight == newHeight) {
			return;
		}
		currentWidth = newWidth;
		currentHeight = newHeight;
		sendTerminalSize();
	}

	private void sendTerminalSize() {
		if (streamsProxy == null) {
			return;
		}
		try {
			streamsProxy
					.write("\u001b[8;" + Integer.toString(currentHeight) + ";" + Integer.toString(currentWidth) + "t"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} catch (IOException e) {
			TerminalPlugin.log("Send terminal size failed.", e); //$NON-NLS-1$
		}
	}

	private Integer[] getProcessList() {
		processList.clear();
		if (streamsProxy != null) {
			try {
				streamsProxy.write(DLE + "$p"); //$NON-NLS-1$
			} catch (IOException e) {
				TerminalPlugin.log("Get terminal process list failed.", e); //$NON-NLS-1$
				return null;
			}
			synchronized (processList) {
				if (processList.isEmpty()) {
					try {
						processList.wait(PROCESS_LIST_TIMEOUT);
					} catch (InterruptedException ignore) {
						ignore.getCause();
					}
				}
				return processList.toArray(new Integer[processList.size()]);
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.terminal.internal.IProcessListener#processCompleted()
	 */
	public void processCompleted() {
		fControl.setState(TerminalState.CLOSED);
		if (streamsProxy != null) {
			streamsProxy.close();
			streamsProxy = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.tm.internal.terminal.provisional.api.provider.TerminalConnectorImpl#doDisconnect()
	 */
	@Override
	protected void doDisconnect() {
		processLauncher.destroy();
	}

	/**
	 * @param listener
	 * @see com.aptana.terminal.internal.ProcessLauncher#addProcessListener(com.aptana.terminal.internal.IProcessListener)
	 */
	public void addProcessListener(IProcessListener listener) {
		processLauncher.addProcessListener(listener);
	}

	/**
	 * @param listener
	 * @see com.aptana.terminal.internal.ProcessLauncher#removeProcessListener(com.aptana.terminal.internal.IProcessListener)
	 */
	public void removeProcessListener(IProcessListener listener) {
		processLauncher.removeProcessListener(listener);
	}

	public void setWorkingDirectory(IPath workingDirectory) {
		this.initialDirectory = workingDirectory;
	}

	/**
	 * @return the initialDirectory
	 */
	public IPath getWorkingDirectory() {
		return initialDirectory;
	}

	private boolean startProcess(ITerminalControl control) {
		try {
			initialDirectory = getInitialDirectory();
			if (!FileUtil.isDirectoryAccessible(initialDirectory.toFile())) {
				control.displayTextInTerminal(MessageFormat.format(
						Messages.LocalTerminalConnector_WorkingDirectoryPermissionErrorMessage,
						initialDirectory.toOSString()));
				initialDirectory = null;
				initialDirectory = getInitialDirectory();
			}
			processLauncher = new ProcessLauncher(getCurrentConfiguration(), initialDirectory);
			processLauncher.addProcessListener(this);
			processLauncher.launch();

			streamsProxy = new StreamsProxy(processLauncher.getProcess(), ENCODING);
			sendTerminalSize();

			// Hook up standard input:
			//
			processInputStream = new BufferedOutputStream(new StreamsProxyOutputStream(streamsProxy, ENCODING), 1024);

			// Hook up standard output:
			//
			IStreamMonitor outputMonitor = streamsProxy.getOutputStreamMonitor();
			LocalTerminalOutputListener outputListener = new LocalTerminalOutputListener(control, this);
			outputMonitor.addListener(outputListener);
			outputListener.streamAppended(outputMonitor.getContents(), outputMonitor);

			// Hook up standard error:
			//
			IStreamMonitor errorMonitor = streamsProxy.getErrorStreamMonitor();
			LocalTerminalOutputListener errorListener = new LocalTerminalOutputListener(control, null);
			errorMonitor.addListener(errorListener);
			errorListener.streamAppended(errorMonitor.getContents(), errorMonitor);
			while (streamsProxy != null && !outputListener.hasOutput()) {
				Thread.sleep(250);
			}
			return streamsProxy != null;
		} catch (Exception e) {
			TerminalPlugin.log("Starting terminal process failed.", e); //$NON-NLS-1$
		}
		control.displayTextInTerminal(Messages.LocalTerminalConnector_NoShellErrorMessage);
		return false;
	}

	private IProcessConfiguration getCurrentConfiguration() {
		return ProcessConfigurations.getInstance().getProcessConfigurations()[0];
	}

	private IPath getInitialDirectory() {
		if (initialDirectory != null && initialDirectory.toFile().isDirectory()) {
			return initialDirectory;
		}
		String activeProjeectName = Platform.getPreferencesService().getString(EXPLORER_PLUGIN_ID,
				ACTIVE_PROJECT_PROPERTY, null, null);
		if (activeProjeectName != null) {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(activeProjeectName);
			if (project != null) {
				IPath location = project.getLocation();
				if (location != null && location.toFile().isDirectory()
						&& FileUtil.isDirectoryAccessible(location.toFile())) {
					return location;
				}
			}
		}
		String home = System.getProperty(USER_HOME_PROPERTY);
		if (home != null) {
			IPath homePath = Path.fromOSString(home);
			if (homePath.toFile().isDirectory()) {
				return homePath;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.terminal.connector.IOutputFilter#filterOutput(char[])
	 */
	public char[] filterOutput(char[] output) {
		StringBuffer result = new StringBuffer(output.length);
		boolean filtering = filteredSequence.length() != 0;
		for (int i = 0; i < output.length; ++i) {
			if (filtering) {
				filteredSequence.append(output[i]);
				if (Character.isLetter(output[i])) {
					processCommandResponse(filteredSequence.toString());
					filteredSequence.setLength(0);
					filtering = false;
				}
			} else if (output[i] == DLE) {
				filteredSequence.append(output[i]);
				filtering = true;
			} else {
				result.append(output[i]);
			}
		}
		return (result.length() == output.length) ? output : result.toString().toCharArray();
	}

	private void processCommandResponse(String response) {
		if (response.startsWith(DLE + "$") && response.endsWith("p")) { //$NON-NLS-1$ //$NON-NLS-2$
			synchronized (processList) {
				processList.notifyAll();
				processList.clear();
				response = response.substring(2, response.length() - 1); // $codepro.audit.disable questionableAssignment
				for (String pid : response.split(",")) { //$NON-NLS-1$
					try {
						processList.add(Integer.parseInt(pid));
					} catch (NumberFormatException e) {
						IdeLog.logError(TerminalPlugin.getDefault(), e);
					}
				}
			}
		} else {
			TerminalPlugin.log("LocalTerminalConnector:UNKNOWN COMMAND RESPONSE: " + response); //$NON-NLS-1$
		}
	}

	public List<String> getRunningProcesses() {
		List<String> processes = new ArrayList<String>();
		Integer[] list = getProcessList();
		if (list != null && list.length > 0) {
			Map<Integer, String> map = new HashMap<Integer, String>();
			for (ProcessItem i : PlatformUtil.getRunningProcesses()) {
				map.put(i.getPid(), i.getExecutableName());
			}
			if (Platform.OS_WIN32.equals(Platform.getOS())) {
				String processName = map.get(list[0]);
				Map<String, String> env = System.getenv();
				if (env != null && processName != null) {
					String comSpec = env.get("ComSpec"); //$NON-NLS-1$
					if (comSpec == null) {
						comSpec = env.get("COMSPEC"); //$NON-NLS-1$
					}
					if (processName.equals(comSpec)) {
						map.remove(list[0]);
					}
				}
			}
			for (int pid : list) {
				String processName = map.get(pid);
				if (processName != null) {
					if (Platform.OS_WIN32.equals(Platform.getOS())) {
						processName = Path.fromOSString(processName).removeFileExtension().lastSegment();
					} else {
						if (processName.length() > 0 && processName.charAt(0) == '-') {
							processName = processName.substring(1);
						}
						int index = processName.indexOf(' ');
						if (index > 0) {
							processName = processName.substring(0, index);
						}
						index = processName.lastIndexOf('/');
						if (index != -1) {
							processName = processName.substring(index + 1, processName.length());
						}
					}
					processes.add(processName);
				}
			}
		}
		return processes;
	}

}
