/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.internal.model.xhr;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;

import com.aptana.js.debug.core.model.xhr.IXHRService;
import com.aptana.js.debug.core.model.xhr.IXHRTransfer;

/**
 * @author Max Stepanov
 */
public class XHRService implements IXHRService {

	private Map<String, IXHRTransfer> transfers = new LinkedHashMap<String, IXHRTransfer>(); // preserver insertion
																								// order

	/**
	 * XHRService
	 */
	public XHRService() {
	}

	/**
	 * openRequest
	 * 
	 * @param rid
	 * @param method
	 * @param url
	 * @param auth
	 */
	public void openRequest(String rid, String method, String url, boolean auth) {
		XHRTransfer xhr = new XHRTransfer(method, url, auth);
		synchronized (transfers) {
			transfers.put(rid, xhr);
		}
		fireChangeEvent(null);
	}

	/**
	 * setRequestHeaders
	 * 
	 * @param rid
	 * @param headers
	 */
	public void setRequestHeaders(String rid, String[][] headers) {
		XHRTransfer xhr = (XHRTransfer) transfers.get(rid);
		if (xhr != null) {
			for (String[] header : headers) {
				xhr.addRequestHeader(header[0], header[1]);
			}
			fireChangeEvent(xhr);
		}
	}

	/**
	 * setRequestBody
	 * 
	 * @param rid
	 * @param body
	 */
	public void setRequestBody(String rid, String body) {
		XHRTransfer xhr = (XHRTransfer) transfers.get(rid);
		if (xhr != null) {
			xhr.setRequestBody(body);
			fireChangeEvent(xhr);
		}
	}

	/**
	 * setResponseHeaders
	 * 
	 * @param rid
	 * @param headers
	 */
	public void setResponseHeaders(String rid, String[][] headers) {
		XHRTransfer xhr = (XHRTransfer) transfers.get(rid);
		if (xhr != null) {
			for (String[] header : headers) {
				xhr.addResponseHeader(header[0], header[1]);
			}
			fireChangeEvent(xhr);
		}
	}

	/**
	 * setResponseBody
	 * 
	 * @param rid
	 * @param body
	 */
	public void setResponseBody(String rid, String body) {
		XHRTransfer xhr = (XHRTransfer) transfers.get(rid);
		if (xhr != null) {
			xhr.setResponseBody(body);
			fireChangeEvent(xhr);
		}
	}

	/**
	 * setResponseStatus
	 * 
	 * @param rid
	 * @param code
	 * @param text
	 */
	public void setResponseStatus(String rid, int code, String text) {
		XHRTransfer xhr = (XHRTransfer) transfers.get(rid);
		if (xhr != null) {
			xhr.setResponseStatus(code, text);
			fireChangeEvent(xhr);
		}
	}

	/**
	 * setError
	 * 
	 * @param rid
	 */
	public void setError(String rid) {
		XHRTransfer xhr = (XHRTransfer) transfers.get(rid);
		if (xhr != null) {
			xhr.setError(true);
			fireChangeEvent(xhr);
		}
	}

	/**
	 * @see com.aptana.js.debug.core.model.xhr.IXHRService#getTransfers()
	 */
	public IXHRTransfer[] getTransfers() {
		synchronized (transfers) {
			return (IXHRTransfer[]) transfers.values().toArray(new IXHRTransfer[transfers.size()]);
		}
	}

	/**
	 * @see com.aptana.js.debug.core.model.xhr.IXHRService#getTransfersCount()
	 */
	public int getTransfersCount() {
		return transfers.size();
	}

	/**
	 * remove
	 * 
	 * @param xhr
	 */
	public void remove(IXHRTransfer xhr) {
		synchronized (transfers) {
			for (Entry<String, IXHRTransfer> entry : transfers.entrySet()) {
				if (entry.getValue() == xhr) {
					transfers.remove(entry.getKey());
					break;
				}
			}
		}
		fireChangeEvent(null);
	}

	/**
	 * fireChangeEvent
	 * 
	 * @param data
	 */
	private void fireChangeEvent(Object data) {
		DebugEvent event = new DebugEvent(this, DebugEvent.CHANGE, DebugEvent.CONTENT);
		event.setData(data);
		DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] { event });
	}
}
