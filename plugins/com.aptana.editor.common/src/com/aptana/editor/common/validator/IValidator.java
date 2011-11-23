/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.validator;

import java.net.URI;
import java.util.List;

import com.aptana.core.build.IProblem;

public interface IValidator
{

	/**
	 * Parses the source for errors and warnings and add them to items list.
	 * 
	 * @param source
	 *            the source text
	 * @param path
	 *            the source path
	 * @param manager
	 *            the validation manager
	 * @param items
	 *            the list of validations
	 */
	public List<IProblem> validate(String source, URI path, IValidationManager manager);
}
