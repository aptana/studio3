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

import java.util.Random;

import org.eclipse.tm.terminal.model.ITerminalTextData;
import org.eclipse.tm.terminal.model.Style;
import org.eclipse.tm.terminal.model.StyleColor;

public class RandomDataSource implements IDataSource {
	Random fRandom=new Random();
	Style styleNormal=Style.getStyle(StyleColor.getStyleColor("black"),StyleColor.getStyleColor("green"));
	Style styles[]=new Style[] {
			styleNormal,
			styleNormal.setBold(true),
			styleNormal.setForground("red"),
			styleNormal.setForground("yellow"),
			styleNormal.setBold(true).setUnderline(true),
			styleNormal.setReverse(true),
			styleNormal.setReverse(true).setBold(true),
			styleNormal.setReverse(true).setUnderline(true)
	};

	public int step(ITerminalTextData terminal) {
		int N=fRandom.nextInt(1000);
		int h=terminal.getHeight();
		int w=terminal.getWidth();
		synchronized (terminal) {
			for (int i = 0; i < N; i++) {
				int line=fRandom.nextInt(h);
				int col=fRandom.nextInt(w);
				char c=(char)('A'+fRandom.nextInt('z'-'A'));
				Style style=styles[fRandom.nextInt(styles.length)];
				terminal.setChar(line, col, c, style);
			}
		}
		return N;
	}

}
