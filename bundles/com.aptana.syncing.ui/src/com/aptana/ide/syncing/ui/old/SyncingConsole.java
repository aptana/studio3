/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.old;

import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import com.aptana.core.logging.IdeLog;
import com.aptana.ide.syncing.ui.SyncingUIPlugin;

/**
 * @author Paul Colton
 */
public class SyncingConsole extends MessageConsole
{
	private static SyncingConsole _console;
	private MessageConsoleStream _consoleStream;

	/*
	 * Constructors
	 */

	/**
	 * ScriptingConsole
	 * 
	 * @param name
	 * @param imageDescriptor
	 */
	public SyncingConsole(String name, ImageDescriptor imageDescriptor)
	{
		super(name, imageDescriptor);
		_consoleStream = this.newMessageStream();
	}

	/**
	 * getMessageStream
	 * 
	 * @return MessageConsoleStream
	 */
	public MessageConsoleStream getMessageStream()
	{
		return _consoleStream;
	}

	/**
	 * getConsole
	 * 
	 * @return SyncingConsole
	 */
	public static SyncingConsole getConsole()
	{
		if (_console == null)
		{
			initConsole();
		}

		return _console;
	}

	/**
	 * initConsole
	 */
	private static void initConsole()
	{
		_console = new SyncingConsole(Messages.SyncingConsole_AptanaSyncingConsole, null);
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { _console });
	}

	/**
	 * @param message
	 */
	public static void println(String message)
	{
		try
		{
			SyncingConsole console = getConsole();
			if (console != null)
			{
				MessageConsoleStream ms = console.getMessageStream();
				ms.write(message);
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(SyncingUIPlugin.getDefault(),
					MessageFormat.format(Messages.SyncingConsole_UnableToWriteToConsole, message), e);
		}
	}

	/**
	 * @see org.eclipse.ui.console.AbstractConsole#dispose()
	 */
	protected void dispose()
	{
		super.dispose();

		try
		{
			_consoleStream.close();
		}
		catch (IOException e)
		{
			IdeLog.logError(SyncingUIPlugin.getDefault(), Messages.SyncingConsole_ErrorClosingStream, e);
		}
	}
}
