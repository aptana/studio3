/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.debug.ui.console;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.console.IPatternMatchListenerDelegate;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;

import com.aptana.core.logging.IdeLog;
import com.aptana.debug.ui.DebugUiPlugin;

/**
 * @author Max Stepanov
 */
public class ConsoleTracker implements IPatternMatchListenerDelegate {
	private TextConsole fConsole;

	/*
	 * @see org.eclipse.ui.console.IPatternMatchListenerDelegate#connect(org.eclipse.ui.console.TextConsole)
	 */
	public void connect(TextConsole console) {
		fConsole = console;
	}

	/*
	 * @see org.eclipse.ui.console.IPatternMatchListenerDelegate#disconnect()
	 */
	public void disconnect() {
		fConsole = null;
	}

	/*
	 * @see org.eclipse.ui.console.IPatternMatchListenerDelegate#matchFound(org.eclipse.ui.console.PatternMatchEvent)
	 */
	public void matchFound(PatternMatchEvent event) {
		try {
			int lineNumber = 0;
			String text = getMatchText(event);
			int index = text.lastIndexOf(':');
			try {
				lineNumber = Integer.parseInt(text.substring(index + 1));
			} catch (NumberFormatException ignore) {
				ignore.getCause();
			}
			text = text.substring(0, index);
			IHyperlink link = new ConsoleHyperlink(fConsole, text, lineNumber);
			fConsole.addHyperlink(link, event.getOffset() + 1, event.getLength() - 2);
		} catch (BadLocationException e) {
			IdeLog.logWarning(DebugUiPlugin.getDefault(), e);
		}
	}

	private String getMatchText(PatternMatchEvent event) throws BadLocationException {
		IDocument document = ((TextConsole) event.getSource()).getDocument();
		int lineNumber = document.getLineOfOffset(event.getOffset());
		IRegion lineInformation = document.getLineInformation(lineNumber);
		int lineOffset = lineInformation.getOffset();
		String line = document.get(lineOffset, lineInformation.getLength());
		int beginIndex = event.getOffset() - lineOffset + 1;
		line = line.substring(beginIndex, beginIndex + event.getLength() - 2);
		return line;
	}
}
