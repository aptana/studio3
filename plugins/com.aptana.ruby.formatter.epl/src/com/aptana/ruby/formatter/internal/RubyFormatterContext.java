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
package com.aptana.ruby.formatter.internal;

import com.aptana.formatter.FormatterContext;
import com.aptana.formatter.IFormatterContainerNode;
import com.aptana.formatter.IFormatterNode;
import com.aptana.ruby.formatter.internal.nodes.FormatterRequireNode;

public class RubyFormatterContext extends FormatterContext {

	public RubyFormatterContext(int indent) {
		super(indent);
	}

	protected boolean isCountable(IFormatterNode node) {
		return node instanceof IFormatterContainerNode
				|| node instanceof FormatterRequireNode;
	}

}
