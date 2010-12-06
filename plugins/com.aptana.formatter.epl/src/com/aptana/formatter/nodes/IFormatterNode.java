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
package com.aptana.formatter.nodes;

import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.IFormatterWriter;

public interface IFormatterNode
{

	int DEFAULT_OFFSET = 0;

	void accept(IFormatterContext context, IFormatterWriter visitor) throws Exception;

	boolean isEmpty();

	int getStartOffset();

	int getEndOffset();

	IFormatterDocument getDocument();

	/**
	 * Returns true if the formatter node should ignore and consume any white-spaces that appear between the
	 * <b>previous</b> node and this node.
	 * 
	 * @return True if these white-spaces should be consumed; False, otherwise.
	 */
	boolean shouldConsumePreviousWhiteSpaces();

	/**
	 * Returns the number of spaces that will be maintained or inserted before this node.
	 * 
	 * @return The number of spaces to maintain.
	 */
	int getSpacesCountBefore();

	/**
	 * Returns the number of spaces that will be inserted after this node.<br>
	 * Note that a node that arrive after this node might consume these spaces if it's defined to do so.
	 * 
	 * @return The number of spaces to insert.
	 */
	int getSpacesCountAfter();
}
