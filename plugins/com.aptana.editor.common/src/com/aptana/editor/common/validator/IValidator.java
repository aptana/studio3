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

public interface IValidator
{

	/**
	 * Parses the source for errors and warnings and add them to the validation manager.
	 * 
	 * @param source
	 *            the source text
	 * @param path
	 *            the source path
	 * @param manager
	 *            the validation manager
	 * @return a list of validation items
	 */
	public List<IValidationItem> validate(String source, URI path, IValidationManager manager);
}
