/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.hyperlink;

import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.debug.ui.console.IConsoleLineTracker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import com.aptana.console.AdaptingHyperlink;
import com.aptana.core.logging.IdeLog;
import com.aptana.git.core.IDebugScopes;
import com.aptana.git.ui.GitUIPlugin;

/**
 * Tracks git processes in the console, detecting hyperlinks to files.
 * 
 * @author cwilliams
 */
public class GitConsoleLineTracker implements IConsoleLineTracker
{

	private IConsole fConsole;
	private HyperlinkDetector detector;

	public void init(IConsole console)
	{
		this.fConsole = console;
		this.detector = new HyperlinkDetector();
	}

	public void lineAppended(IRegion line)
	{
		try
		{
			String lineContents = fConsole.getDocument().get(line.getOffset(), line.getLength());

			IHyperlink[] links = detector.detectHyperlinks(lineContents);
			if (links != null)
			{
				for (IHyperlink link : links)
				{
					fConsole.addLink(new AdaptingHyperlink(link), line.getOffset()
							+ link.getHyperlinkRegion().getOffset(), link.getHyperlinkRegion().getLength());
				}
			}
		}
		catch (BadLocationException e)
		{
			IdeLog.logError(GitUIPlugin.getDefault(), e, IDebugScopes.DEBUG);
		}
	}

	public void dispose()
	{
		this.detector = null;
		this.fConsole = null;
	}
}
