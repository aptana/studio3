/* ***** BEGIN LICENSE BLOCK *****
 * This file Copyright (c) 2005-2007 Aptana, Inc. This program is
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
 * with certain Eclipse Public Licensed code and certain additional terms
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
 * 
 * Contributor(s):
 *     Max Stepanov (Aptana, Inc.)
 *
 * ***** END LICENSE BLOCK ***** */

(function() {

// ************************************************************************************************

const nsISupports = Components.interfaces.nsISupports;
const nsISocketTransportService = Components.interfaces.nsISocketTransportService;
const nsIServerSocket = Components.interfaces.nsIServerSocket;
const nsIServerSocketListener = Components.interfaces.nsIServerSocketListener;
const nsITransport = Components.interfaces.nsITransport;
const nsIScriptableInputStream = Components.interfaces.nsIScriptableInputStream;
const nsIAsyncInputStream = Components.interfaces.nsIAsyncInputStream;
const nsIInputStreamCallback = Components.interfaces.nsIInputStreamCallback;

const NS_ERROR_NO_INTERFACE = Components.results.NS_ERROR_NO_INTERFACE;
const NS_ERROR_ABORT = Components.results.NS_ERROR_ABORT;
const NS_BASE_STREAM_CLOSED = Components.results.NS_BASE_STREAM_CLOSED;

const PACKET_START = new RegExp(/^[0-9]+\*.+/);

const isClientDebugger = (typeof(document) != "undefined");
const ff3 = !isClientDebugger || (AptanaUtils.compareVersion(AptanaUtils.getAppVersion(), "3.0*") >= 0);

function createSocketForTransport(transport, listener)
{	
	const outstream = transport.openOutputStream(nsITransport.OPEN_BLOCKING,0,0);
	const stream = transport.openInputStream(0,0,0);

	const instream = Components.classes["@mozilla.org/scriptableinputstream;1"]
								.createInstance(nsIScriptableInputStream);
	instream.init(stream);

	var socket = 
	{
		transport: transport,
		output :outstream,
		input: stream,
		
		close: function()
		{
			if (this.closed) {
				return;
			}
			dd("Socket closed.");
			this.closed = true;
			this.input.close();
			this.output.close();
		},

		send: function(data)
		{
			if( this.closed ) {
				return;
			}
			var outputData = data.length + "*" + data;
			this.output.write(outputData, outputData.length);
			dd("Socket sent: >" + outputData + "<");
		}
	};
	
	var inputStreamPump = {
		astream: stream.QueryInterface(nsIAsyncInputStream),
		eventTarget: null,
		state: 0,
		started: false,
		data: "",

		onStartRequest: function() {},
		onStopRequest: function() {
			if (!socket.closed) {
				socket.close();
				listener.onClose();
			}
		},
		onDataAvailable: function(count) {
			this.data += instream.read(count);
			while( this.data.match(PACKET_START) ) {
				var index = this.data.indexOf("*");
				var packetEnd = parseInt(this.data.substring(0, index));
				if( this.data.length < packetEnd ) {
					return;
				}
				var packet = this.data.substring(index+1, index+1+packetEnd);
				this.data = this.data.substring(index+1+packetEnd);
				dd("Socket recv: >" + packet + "<");
				listener.onPacket(packet);
			}
		},
		
		startInThread: function() {
			if (this.started || socket.closed) {
				return;
			}
			if (ff3) {
				this.eventTarget = Components.classes["@mozilla.org/thread-manager;1"]
								.getService(Components.interfaces.nsIThreadManager)
								.currentThread;
			} else {
				this.eventTarget = Components.classes["@mozilla.org/event-queue-service;1"]
								.getService(Components.interfaces.nsIEventQueueService)
								.getSpecialEventQueue(Components.interfaces.nsIEventQueueService.CURRENT_THREAD_EVENT_QUEUE);
			}
			
			this.astream.asyncWait(this, 0, 0, this.eventTarget);
			this.started = true;
		},

		stopInThread: function() {
			if (!this.started || socket.closed) {
				return;
			}
			/* hack to clear socket stream callback */
			this.astream.asyncWait(null, 0, 0, null);
			this.started = false;
		},
						
		onInputStreamReady: function(astream) {
			var state = -1;
			while( state != this.state ) {
				state = this.state;
				try {
					var count = astream.available();
					if ( state == 1 ) {
						if ( count > 0 ) {
							this.onDataAvailable(count);
						}
					} else if ( state == 0 ) {
						this.onStartRequest();
						++this.state;
					}
					astream.asyncWait(this, 0, 0, this.eventTarget);
				} catch(exc) {
					if ( exc.result != NS_BASE_STREAM_CLOSED
						&& exc.result != NS_ERROR_ABORT) {
						dd(exc,'err');
						astream.closeWithStatus(exc.result);
					}
					this.onStopRequest();
					return;
				}
			}
		},
		
		QueryInterface: function(iid) {
			if (iid.equals(nsIInputStreamCallback) || iid.equals(nsISupports)) {
				return this;
			}
			throw NS_ERROR_NO_INTERFACE;
		}
	};
	
	socket.startProcessing = function() {
		try {
			inputStreamPump.startInThread();
		} catch(exc) {
			dd(exc);
		}
	};
	socket.stopProcessing = function() {
		try {
			inputStreamPump.stopInThread();
		} catch(exc) {
			dd(exc);
		}
	};
	
	socket.startProcessing();

	return socket;
}

function createServerSocket(port, listener)
{
	const serverSocket = Components.classes["@mozilla.org/network/server-socket;1"]
										.createInstance(nsIServerSocket);
	serverSocket.init(port, false, 1);

	var socketProxy = 
	{
		socket: null,
		
		get closed() {
			if ( this.socket ) {
				return this.socket.closed;
			}
			return false;
		},
		
		close: function() {
			if ( this.socket ) {
				this.socket.close();
			} else {
				serverSocket.close();
			}
		},

		send: function(data) {
			if ( this.socket ) {
				this.socket.send(data);
			}
		}
	};
	
	const serverListener = {
		QueryInterface: function(iid) {
			if (iid.equals(nsIServerSocketListener) || iid.equals(nsISupports)) {
				return this;
			}
			throw NS_ERROR_NO_INTERFACE;
		},
		
		onSocketAccepted: function(serverSocket, transport) {
			socketProxy.socket = createSocketForTransport(transport, listener);
			serverSocket.close();
		},
		
		onStopListening: function(serverSocket, status) {
		}
	};
	serverSocket.asyncListen(serverListener);
	
	return socketProxy;
}

this.createSocket = function(host, port, listener)
{
	if ( host == "*" ) {
		return createServerSocket(port, listener);
	}
	
	const transportService = Components.classes["@mozilla.org/network/socket-transport-service;1"]
											.getService(nsISocketTransportService);
	const transport = transportService.createTransport(null,0,host,port,null);
	transport.setTimeout(nsISocketTransportService.TIMEOUT_CONNECT,2/*seconds*/);
	
	return createSocketForTransport(transport, listener);
}

// ************************************************************************************************

function dd(message,level)
{
	if ( typeof(message) == 'object' && "fileName" in message && "lineNumber" in message ) {
		message = ""+message+" at "+message.fileName+":"+message.lineNumber;
	}
	try {
		if ( level == 'err' ) {
			AptanaLogger.logError(message);
			return;
		}
		if ( AptanaDebugger.DEBUG ) {
			AptanaLogger.log(message,level);
		}
	} catch(exc) {
	}
}

// ************************************************************************************************

}).apply(AptanaDebugger);
