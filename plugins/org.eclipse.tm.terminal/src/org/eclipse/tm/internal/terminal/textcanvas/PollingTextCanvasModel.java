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
package org.eclipse.tm.internal.terminal.textcanvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.tm.terminal.model.ITerminalTextDataSnapshot;

/**
 * @author Michael.Scharf@scharf-software.com
 *
 */
public class PollingTextCanvasModel extends AbstractTextCanvasModel {
	int fPollInterval=50;
	/**
	 * 
	 */
	public PollingTextCanvasModel(ITerminalTextDataSnapshot snapshot) {
		super(snapshot);
		Display.getDefault().timerExec(fPollInterval,new Runnable(){
			public void run() {
				update();
				Display.getDefault().timerExec(fPollInterval,this);
			}});
	}
	public void setUpdateInterval(int t) {
		fPollInterval=t;
	}
}
