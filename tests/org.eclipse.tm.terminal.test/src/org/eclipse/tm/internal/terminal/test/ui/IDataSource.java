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

interface IDataSource {
	/**
	 * @param terminal
	 * @return number of characters changed
	 */
	int step(ITerminalTextData terminal);
}