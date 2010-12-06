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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.tm.terminal.model.Style;
import org.eclipse.tm.terminal.model.StyleColor;

/**
 * Reads the file in an infinite loop.
 * Makes lines containing 'x' bold.
 *
 */
final class FileDataSource extends AbstractLineOrientedDataSource {
	private final String fFile;

	BufferedReader reader;

	String line;

	Style style;

	Style styleNormal=Style.getStyle(StyleColor.getStyleColor("black"),StyleColor.getStyleColor("white"));

	Style styleBold=styleNormal.setBold(true);

	FileDataSource(String file) {
		fFile = file;
	}

	public char[] dataSource() {
		return line.toCharArray();
	}

	public Style getStyle() {
		return style;
	}

	public void next() {
		try {
			if(reader==null)
				reader = new BufferedReader(new FileReader(fFile));
			line=reader.readLine();
			if(line==null) {
				reader.close();
				reader=null;
				// reopen the file
				next();
				return;
			}
			if(line.lastIndexOf('x')>0)
				style=styleBold;
			else
				style=styleNormal;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}