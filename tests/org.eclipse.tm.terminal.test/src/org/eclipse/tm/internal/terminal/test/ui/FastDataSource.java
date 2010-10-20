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

import org.eclipse.tm.terminal.model.Style;

final class FastDataSource extends AbstractLineOrientedDataSource {
	char lines[][]=new char[][]{
			"123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 ".toCharArray(),
			"abcdefghi abcdefghi abcdefghi abcdefghi abcdefghi abcdefghi abcdefghi abcdefghi ".toCharArray(),
	};


	int pos;

	public char[] dataSource() {
		return lines[pos%lines.length];
	}

	public Style getStyle() {
		return null;
	}

	public void next() {
		pos++;
	}
}