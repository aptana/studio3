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

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;

import com.aptana.js.debug.core.model.xhr.IXHRService;
import com.aptana.js.debug.core.model.xhr.IXHRTransfer;

/**
 * @author Max Stepanov
 */
public class XHRService implements IXHRService {
	
	private Map<String, IXHRTransfer> transfers = new LinkedHashMap<String, IXHRTransfer>(); // preserver insertion order

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
			transfers.remove(xhr);
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
