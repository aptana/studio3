/*******************************************************************************
 * Copyright (c) 2003, 2008 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributors:
 * The following Wind River employees contributed to the Terminal component
 * that contains this file: Chris Thew, Fran Litterio, Stephen Lamb,
 * Helmut Haigermoser and Ted Williams.
 *
 * Contributors:
 * Michael Scharf (Wind River) - extracted from TerminalConsts
 * Martin Oberhuber (Wind River) - fixed copyright headers and beautified
 * Anna Dushistova (MontaVista) - [227537] moved actions from terminal.view to terminal plugin
 *******************************************************************************/
package org.eclipse.tm.internal.terminal.control.actions;

public interface ImageConsts {
	public final static String IMAGE_DIR_ROOT = "icons/"; //$NON-NLS-1$
	public final static String IMAGE_DIR_LOCALTOOL = IMAGE_DIR_ROOT + "clcl16/"; // basic colors - size 16x16 //$NON-NLS-1$
	public final static String IMAGE_DIR_DLCL = IMAGE_DIR_ROOT + "dlcl16/"; // disabled - size 16x16 //$NON-NLS-1$
	public final static String IMAGE_DIR_ELCL = IMAGE_DIR_ROOT+ "elcl16/"; // enabled - size 16x16 //$NON-NLS-1$

	public static final String IMAGE_CLCL_CLEAR_ALL = "ImageClclClearAll"; //$NON-NLS-1$

	public static final String IMAGE_DLCL_CLEAR_ALL = "ImageDlclClearAll"; //$NON-NLS-1$

	public static final String IMAGE_ELCL_CLEAR_ALL = "ImageElclClearAll"; //$NON-NLS-1$
}
