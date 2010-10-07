/*******************************************************************************
 * Copyright (c) 2007 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Michael Scharf (Wind River) - initial API and implementation
 *******************************************************************************/
package org.eclipse.tm.internal.terminal.test.ui;

import org.eclipse.tm.terminal.model.ITerminalTextData;
import org.eclipse.tm.terminal.model.Style;

/**
 * Adds line by line
 *
 */
abstract class AbstractLineOrientedDataSource implements IDataSource {
	abstract public char[] dataSource();
	abstract public Style getStyle();

	abstract public void next();

	public int step(ITerminalTextData terminal) {
		next();
		char[] chars=dataSource();
		Style style= getStyle();
		int len;
		// keep the synchronized block short!
		synchronized (terminal) {
			terminal.addLine();
			len=Math.min(terminal.getWidth(),chars.length);
			int line=terminal.getHeight()-1;
			terminal.setChars(line, 0, chars, 0, len, style);
			terminal.setCursorLine(line);
			terminal.setCursorColumn(len-1);
		}
		return len;
	}
}