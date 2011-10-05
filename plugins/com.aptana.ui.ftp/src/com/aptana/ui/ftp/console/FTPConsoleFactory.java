/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable staticFieldNamingConvention

package com.aptana.ui.ftp.console;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleFactory;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import com.aptana.theme.ConsoleThemer;
import com.aptana.theme.extensions.ConsoleThemePageParticipant;
import com.aptana.ui.ftp.FTPUIPlugin;

/**
 * @author Max Stepanov
 *
 */
public class FTPConsoleFactory implements IConsoleFactory {

	private static MessageConsole console;
	private static MessageConsoleStream consoleOutputStream;

	/* (non-Javadoc)
	 * @see org.eclipse.ui.console.IConsoleFactory#openConsole()
	 */
	public void openConsole() {
    	ConsolePlugin.getDefault().getConsoleManager().showConsoleView(getOrcreateConsole());
	}

	public static OutputStream newConsoleOutputStream() {
		getOrcreateConsole();
		return consoleOutputStream;
	}

	private static MessageConsole getOrcreateConsole() {
		if (console == null) {
			console = new MessageConsole(Messages.FTPConsoleFactory_FTPConsole, FTPUIPlugin.getImageDescriptor("/icons/full/eview16/ftp.png")); //$NON-NLS-1$

			Map<MessageConsoleStream, String> themeConsoleStreamToColor = new HashMap<MessageConsoleStream, String>();
			consoleOutputStream = console.newMessageStream();
			themeConsoleStreamToColor.put(consoleOutputStream, ConsoleThemer.CONSOLE_OUTPUT);

			console.setAttribute(ConsoleThemePageParticipant.THEME_CONSOLE_STREAM_TO_COLOR_ATTRIBUTE, themeConsoleStreamToColor);
			ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { console });
		}
		return console;
	}
}
