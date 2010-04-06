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
package org.eclipse.tm.terminal.model;

import org.eclipse.tm.internal.terminal.model.SynchronizedTerminalTextData;
import org.eclipse.tm.internal.terminal.model.TerminalTextData;

public class TerminalTextDataFactory {
	static public ITerminalTextData makeTerminalTextData() {
		return new SynchronizedTerminalTextData(new TerminalTextData());
	}
}
