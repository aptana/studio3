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
package com.aptana.ui.preferences.formatter;

public class ScriptFormattingContextProperties {

	/**
	 * Property key of the project property. The property must be
	 * <code>org.eclipse.core.resources.IProject</code>. If set the preferences
	 * of the specified project will be used first.
	 * <p>
	 * Value: <code>"formatting.context.project"</code>
	 */
	public static final String CONTEXT_PROJECT = "formatting.context.project"; //$NON-NLS-1$

	/**
	 * Property key of the formatter id property. The property must be of the
	 * type <code>java.lang.String</code>.
	 * <p>
	 * Value: <code>"formatting.context.formatterId"</code>
	 */
	public static final String CONTEXT_FORMATTER_ID = "formatting.context.formatterId"; //$NON-NLS-1$

}
