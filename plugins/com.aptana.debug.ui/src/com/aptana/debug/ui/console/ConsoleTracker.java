/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
			}
			text = text.substring(0, index);
			IHyperlink link = new ConsoleHyperlink(fConsole, text, lineNumber);
			fConsole.addHyperlink(link, event.getOffset() + 1, event.getLength() - 2);
		} catch (BadLocationException ignore) {
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
