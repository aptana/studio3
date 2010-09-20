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
package com.aptana.formatter;

import com.aptana.formatter.nodes.IFormatterNode;

public interface IFormatterContext {

	int getIndent();

	void incIndent();

	void decIndent();

	void resetIndent();

	IFormatterContext copy();

	boolean isIndenting();

	void setIndenting(boolean value);

	boolean isComment();

	void setComment(boolean value);

	int getBlankLines();

	void setBlankLines(int value);

	void resetBlankLines();

	/**
	 * @param node
	 */
	void enter(IFormatterNode node);

	/**
	 * @param node
	 */
	void leave(IFormatterNode node);

	IFormatterNode getParent();

	int getChildIndex();

	boolean isWrapping();

	void setWrapping(boolean value);

}
