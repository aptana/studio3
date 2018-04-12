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

public class FormatterSyntaxProblemException extends FormatterException
{

	private static final long serialVersionUID = 4527887872127464243L;

	public FormatterSyntaxProblemException()
	{
		// empty
	}

	public FormatterSyntaxProblemException(String msg)
	{
		super(msg);
	}

}
