/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package com.aptana.ruby.formatter.preferences;

import org.eclipse.osgi.util.NLS;

/**
 * @author Alexey
 *
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.aptana.ruby.formatter.preferences.messages"; //$NON-NLS-1$
	public static String RubyFormatterPreferencePage_description;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
