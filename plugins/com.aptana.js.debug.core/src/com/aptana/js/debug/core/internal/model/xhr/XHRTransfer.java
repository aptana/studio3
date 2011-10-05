/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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
	 * @param name
	 * @param value
	 */
	protected void addRequestHeader(String name, String value) {
		requestHeaders.add(new Header(name, value));
	}

	/*
	 * addResponseHeader
	 * @param name
	 * @param value
	 */
	protected void addResponseHeader(String name, String value) {
		responseHeaders.add(new Header(name, value));
	}

	/*
	 * setRequestBody
	 * @param body
	 */
	protected void setRequestBody(String body) {
		this.requestBody = body;
		this.requestDate = new Date();
	}

	/*
	 * setResponseBody
	 * @param body
	 */
	protected void setResponseBody(String body) {
		this.responseBody = body;
		this.responseDate = new Date();
	}

	/*
	 * setResponseStatus
	 * @param statusCode
	 * @param statusText
	 */
	protected void setResponseStatus(int statusCode, String statusText) {
		this.statusCode = statusCode;
		this.statusText = statusText;
	}

	/*
	 * setError
	 * @param error
	 */
	protected void setError(boolean error) {
		this.error = error;
	}

	/*
	 * Header implementation
	 */
	private static class Header implements IHeader {
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
