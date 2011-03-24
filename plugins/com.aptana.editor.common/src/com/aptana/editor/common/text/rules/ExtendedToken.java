/**
 * Appcelerator Titanium Studio
 * Copyright (c) 2011 by Appcelerator, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

package com.aptana.editor.common.text.rules;

import org.eclipse.jface.text.rules.Token;

/**
 * @author Max Stepanov
 *
 */
public class ExtendedToken extends Token {

	private String contents;
	
	/**
	 * @param data
	 */
	public ExtendedToken(Object data) {
		super(data);
	}

	/**
	 * @return the contents
	 */
	public String getContents() {
		return contents;
	}

	/**
	 * @param contents the contents to set
	 */
	public void setContents(String contents) {
		this.contents = contents;
	}

}
