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

public abstract class AbstractFormatterNode implements IFormatterNode {

	private final IFormatterDocument document;

	/**
	 * @param document
	 */
	public AbstractFormatterNode(IFormatterDocument document) {
		this.document = document;
	}

	/*
	 * @see org.eclipse.dltk.ruby.formatter.node.IFormatterNode#getDocument()
	 */
	public IFormatterDocument getDocument() {
		return document;
	}

	protected String getShortClassName() {
		final String name = getClass().getName();
		int index = name.lastIndexOf('.');
		return index > 0 ? name.substring(index + 1) : name;
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getShortClassName();
	}

	protected int getInt(String key) {
		return document.getInt(key);
	}
}
