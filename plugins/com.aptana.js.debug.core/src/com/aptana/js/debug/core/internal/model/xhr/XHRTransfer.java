/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.internal.model.xhr;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.aptana.js.debug.core.model.xhr.IXHRTransfer;

/**
 * @author Max Stepanov
 */
class XHRTransfer implements IXHRTransfer {
	
	private String method;
	private String url;
	private boolean auth;
	private List<IHeader> requestHeaders = new ArrayList<IHeader>(); // preserver insertion order
	private List<IHeader> responseHeaders = new ArrayList<IHeader>();
	private String requestBody;
	private String responseBody;
	private Date requestDate;
	private Date responseDate;
	private int statusCode;
	private String statusText;
	private boolean error;

	/**
	 * XHRTransfer
	 * 
	 * @param method
	 * @param url
	 * @param auth
	 */
	public XHRTransfer(String method, String url, boolean auth) {
		this.method = method;
		this.url = url;
		this.auth = auth;
	}

	/*
	 * @see com.aptana.js.debug.core.model.xhr.IXHRTransfer#getURL()
	 */
	public String getURL() {
		return url;
	}

	/*
	 * @see com.aptana.js.debug.core.model.xhr.IXHRTransfer#getMethod()
	 */
	public String getMethod() {
		return method;
	}

	/*
	 * @see com.aptana.js.debug.core.model.xhr.IXHRTransfer#getRequestDate()
	 */
	public Date getRequestDate() {
		return requestDate;
	}

	/*
	 * @see com.aptana.js.debug.core.model.xhr.IXHRTransfer#getRequestHeaders()
	 */
	public IHeader[] getRequestHeaders() {
		return (IHeader[]) requestHeaders.toArray(new IHeader[requestHeaders.size()]);
	}

	/*
	 * @see com.aptana.js.debug.core.model.xhr.IXHRTransfer#getRequestBody()
	 */
	public String getRequestBody() {
		return requestBody;
	}

	/*
	 * @see com.aptana.js.debug.core.model.xhr.IXHRTransfer#getResponseDate()
	 */
	public Date getResponseDate() {
		return responseDate;
	}

	/*
	 * @see com.aptana.js.debug.core.model.xhr.IXHRTransfer#getResponseHeaders()
	 */
	public IHeader[] getResponseHeaders() {
		return (IHeader[]) responseHeaders.toArray(new IHeader[responseHeaders.size()]);
	}

	/*
	 * @see com.aptana.js.debug.core.model.xhr.IXHRTransfer#getResponseBody()
	 */
	public String getResponseBody() {
		return responseBody;
	}

	/*
	 * @see com.aptana.js.debug.core.model.xhr.IXHRTransfer#getStatusCode()
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/*
	 * @see com.aptana.js.debug.core.model.xhr.IXHRTransfer#getStatusText()
	 */
	public String getStatusText() {
		return statusText;
	}

	/*
	 * @see com.aptana.js.debug.core.model.xhr.IXHRTransfer#hasError()
	 */
	public boolean hasError() {
		return error;
	}

	/*
	 * @see com.aptana.js.debug.core.model.xhr.IXHRTransfer#isAuthenticated()
	 */
	public boolean isAuthenticated() {
		return auth;
	}

	/*
	 * addRequestHeader
	 * 
	 * @param name
	 * @param value
	 */
	protected void addRequestHeader(String name, String value) {
		requestHeaders.add(new Header(name, value));
	}

	/*
	 * addResponseHeader
	 * 
	 * @param name
	 * @param value
	 */
	protected void addResponseHeader(String name, String value) {
		responseHeaders.add(new Header(name, value));
	}

	/*
	 * setRequestBody
	 * 
	 * @param body
	 */
	protected void setRequestBody(String body) {
		this.requestBody = body;
		this.requestDate = new Date();
	}

	/*
	 * setResponseBody
	 * 
	 * @param body
	 */
	protected void setResponseBody(String body) {
		this.responseBody = body;
		this.responseDate = new Date();
	}

	/*
	 * setResponseStatus
	 * 
	 * @param statusCode
	 * @param statusText
	 */
	protected void setResponseStatus(int statusCode, String statusText) {
		this.statusCode = statusCode;
		this.statusText = statusText;
	}

	/*
	 * setError
	 * 
	 * @param error
	 */
	protected void setError(boolean error) {
		this.error = error;
	}

	/*
	 * Header implementation
	 */
	private class Header implements IHeader {
		private String name;
		private String value;

		/**
		 * Header
		 * 
		 * @param name
		 * @param value
		 */
		public Header(String name, String value) {
			this.name = name;
			this.value = value;
		}

		/**
		 * @see com.aptana.js.debug.core.model.xhr.IHeader#getName()
		 */
		public String getName() {
			return name;
		}

		/**
		 * @see com.aptana.js.debug.core.model.xhr.IHeader#getValue()
		 */
		public String getValue() {
			return value;
		}
	}
}
