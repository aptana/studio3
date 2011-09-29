/*******************************************************************************
 *  Copyright (c) 2008 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.filewatcher.poller.epl;

import org.eclipse.osgi.util.NLS;


public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.aptana.filewatcher.poller.epl.messages"; //$NON-NLS-1$

	public static String error_main_loop;
	public static String error_processing;
	public static String null_folder;
	public static String thread_not_started;
	public static String thread_started;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
		//
	}
}
