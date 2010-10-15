/*******************************************************************************
 * Copyright (c) 2009 xored software, Inc.  
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

/**
 * @since 2.0
 */
public class FormatterTextNodeWrapper implements IFormatterTextNode {

	protected final IFormatterTextNode target;

	public FormatterTextNodeWrapper(IFormatterTextNode target) {
		this.target = target;
	}

	public String getText() {
		return target.getText();
	}

	public void accept(IFormatterContext context, IFormatterWriter visitor)
			throws Exception {
		target.accept(context, visitor);
	}

	public IFormatterDocument getDocument() {
		return target.getDocument();
	}

	public int getEndOffset() {
		return target.getEndOffset();
	}

	public int getStartOffset() {
		return target.getStartOffset();
	}

	public boolean isEmpty() {
		return target.isEmpty();
	}

	@Override
	public String toString() {
		return target.toString();
	}

	/* (non-Javadoc)
	 * @see com.aptana.formatter.nodes.IFormatterNode#getSpacesCountBefore()
	 */
	public int getSpacesCountBefore()
	{
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.aptana.formatter.nodes.IFormatterNode#shouldConsumePreviousWhiteSpaces()
	 */
	public boolean shouldConsumePreviousWhiteSpaces()
	{
		return false;
	}
}
