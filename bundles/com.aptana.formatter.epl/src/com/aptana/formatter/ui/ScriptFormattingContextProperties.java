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
package com.aptana.formatter.ui;

public class ScriptFormattingContextProperties
{

	/**
	 * Property key of the project property. The property must be <code>org.eclipse.core.resources.IProject</code>. If
	 * set the preferences of the specified project will be used first.
	 * <p>
	 * Value: <code>"formatting.context.project"</code>
	 */
	public static final String CONTEXT_PROJECT = "formatting.context.project"; //$NON-NLS-1$

	/**
	 * Property key of the formatter id property. The property must be of the type <code>java.lang.String</code>.
	 * <p>
	 * Value: <code>"formatting.context.formatterId"</code>
	 */
	public static final String CONTEXT_FORMATTER_ID = "formatting.context.formatterId"; //$NON-NLS-1$

	/**
	 * Property key that indicates that the formatter is running as a 'slave'. The property must be of the type
	 * <code>java.lang.Boolean</code>.
	 * <p>
	 * Value: <code>"formatting.context.isSlave"</code>
	 */
	public static final String CONTEXT_FORMATTER_IS_SLAVE = "formatting.context.isSlave"; //$NON-NLS-1$

	/**
	 * Property key that indicates that the formatter may consume any previously inserted indentation. The property will
	 * only be evaluated when the formatter is a 'slave' formatter and it must be of the type
	 * <code>java.lang.Boolean</code>.
	 * <p>
	 * Value: <code>"formatting.context.canConsumeIndent"</code>
	 */
	public static final String CONTEXT_FORMATTER_CAN_CONSUME_INDENTATION = "formatting.context.canConsumeIndent"; //$NON-NLS-1$

	/**
	 * A key that indicates the original offset for the context. Since we lose the real offset during a slave
	 * formatting, we can maintain the original offset in this key and retrieve it later when needed. This can be set on
	 * the {@link com.aptana.formatter.FormatterDocument} for an easy access when formatting the nodes that want to know
	 * about this offset.
	 */
	public static final String CONTEXT_ORIGINAL_OFFSET = "formatting.context.originalOffset"; //$NON-NLS-1$

}
