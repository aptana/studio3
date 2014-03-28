/**
 * Appcelerator Titanium Studio
 * Copyright (c) 2011 by Appcelerator, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

package com.aptana.editor.common.text.rules;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import com.aptana.core.util.StringUtil;

/**
 * @author Max Stepanov
 *
 */
public class ExtendedToken extends Token {

	private String contents;

	/**
	 * @param data
	 */
	public ExtendedToken(IToken token) {
		this(token.getData());
	}

	/**
	 * @param data
	 */
	private ExtendedToken(Object data) {
		super(data);
	}

	/**
	 * @return the contents
	 */
	public String getContents() {
		return contents != null ? contents : StringUtil.EMPTY;
	}

	/**
	 * @param contents the contents to set
	 */
	public void setContents(String contents) {
		this.contents = contents;
	}

	/**
	 * Returns contents substring
	 * @param beginIndex
	 * @return
	 */
	String getContentSubstring(int beginIndex) {
		if (contents != null && contents.length() > beginIndex) {
			return contents.substring(beginIndex);
		}
		return StringUtil.EMPTY;
	}

}
