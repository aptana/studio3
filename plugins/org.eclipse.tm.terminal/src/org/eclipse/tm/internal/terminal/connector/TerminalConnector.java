/*******************************************************************************
 * Copyright (c) 2007, 2009 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Michael Scharf (Wind River) - initial API and implementation
 * Michael Scharf (Wind River) - [200541] Extract from TerminalConnectorExtension.TerminalConnectorProxy
 * Martin Oberhuber (Wind River) - [225853][api] Provide more default functionality in TerminalConnectorImpl 
 * Uwe Stieber (Wind River) - [282996] [terminal][api] Add "hidden" attribute to terminal connector extension point
 *******************************************************************************/
package org.eclipse.tm.internal.terminal.connector;

import java.io.OutputStream;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.tm.internal.terminal.control.impl.TerminalMessages;
import org.eclipse.tm.internal.terminal.provisional.api.ISettingsPage;
import org.eclipse.tm.internal.terminal.provisional.api.ISettingsStore;
import org.eclipse.tm.internal.terminal.provisional.api.ITerminalConnector;
import org.eclipse.tm.internal.terminal.provisional.api.ITerminalControl;
import org.eclipse.tm.internal.terminal.provisional.api.Logger;
import org.eclipse.tm.internal.terminal.provisional.api.TerminalConnectorExtension;
import org.eclipse.tm.internal.terminal.provisional.api.TerminalState;
import org.eclipse.tm.internal.terminal.provisional.api.provider.TerminalConnectorImpl;

/**
 * An {@link ITerminalConnector} instance, also known as terminal connection
 * type, for maintaining a single terminal connection.
 *
 * It provides all terminal connector functions that can be provided by static
 * markup without loading the actual implementation class. The actual
 * {@link TerminalConnectorImpl} implementation class is lazily loaded by the
 * provided {@link TerminalConnector.Factory} interface when needed. class, and
 * delegates to the actual implementation when needed. The following methods can
 * be called without initializing the contributed implementation class:
 * {@link #getId()}, {@link #getName()}, {@link #getSettingsSummary()},{@link #load(ISettingsStore)},
 * {@link #setTerminalSize(int, int)}, {@link #save(ISettingsStore)},
 * {@link #getAdapter(Class)}
 *
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 *                Clients can get terminal connector instances through the
 *                {@link TerminalConnectorExtension} class.
 * @since org.eclipse.tm.terminal 2.0
 */
public class TerminalConnector implements ITerminalConnector {
	/**
	 * Creates an instance of TerminalConnectorImpl. This is used to lazily load
	 * classed defined in extensions.
	 *
	 * @since org.eclipse.tm.terminal 2.0
	 */
	public interface Factory {
		/**
		 * Factory method to create the actual terminal connector implementation
		 * when needed.
		 *
		 * @return a Connector
		 * @throws Exception
		 */
		TerminalConnectorImpl makeConnector() throws Exception;
	}
	/**
	 * The factory for creating impl instances.
	 */
	private final TerminalConnector.Factory fTerminalConnectorFactory;
	/**
	 * The (display) name of the TerminalConnector
	 */
	private final String fName;
	/**
	 * The unique id the connector
	 */
	private final String fId;
	/**
	 * Flag to mark the connector as hidden.
	 */
	private final boolean fHidden;
	/**
	 * The connector
	 */
	private TerminalConnectorImpl fConnector;
	/**
	 * If the initialization of the class specified in the extension fails,
	 * this variable contains the error
	 */
	private Exception fException;
	/**
	 * The store might be set before the real connector is initialized.
	 * This keeps the value until the connector is created.
	 */
	private ISettingsStore fStore;
	/**
	 * Constructor for the terminal connector.
	 *
	 * @param terminalConnectorFactory Factory for lazily instantiating the
	 *            TerminalConnectorImpl when needed.
	 * @param id terminal connector ID. The connector is publicly known under
	 *            this ID.
	 * @param name translatable name to display the connector in the UI.
	 */
	public TerminalConnector(TerminalConnector.Factory terminalConnectorFactory, String id, String name, boolean hidden) {
		fTerminalConnectorFactory = terminalConnectorFactory;
		fId = id;
		fName = name;
		fHidden = hidden;
	}
	public String getInitializationErrorMessage() {
		getConnectorImpl();
		if(fException!=null)
			return fException.getLocalizedMessage();
		return null;
	}
	public String getId() {
		return fId;
	}
	public String getName() {
		return fName;
	}
	public boolean isHidden() {
		return fHidden;
	}
	private TerminalConnectorImpl getConnectorImpl() {
		if(!isInitialized()) {
			try {
				fConnector=fTerminalConnectorFactory.makeConnector();
				fConnector.initialize();
			} catch (Exception e) {
				fException=e;
				fConnector=new TerminalConnectorImpl(){
					public void connect(ITerminalControl control) {
						// super.connect(control);
						control.setState(TerminalState.CLOSED);
						control.setMsg(getInitializationErrorMessage());
					}
					public OutputStream getTerminalToRemoteStream() {
						return null;
					}
					public String getSettingsSummary() {
						return null;
					}};
				// that's the place where we log the exception
				Logger.logException(e);
			}
			if(fConnector!=null && fStore!=null)
				fConnector.load(fStore);
		}
		return fConnector;
	}

	public boolean isInitialized() {
		return fConnector!=null || fException!=null;
	}
	public void connect(ITerminalControl control) {
		getConnectorImpl().connect(control);
	}
	public void disconnect() {
		getConnectorImpl().disconnect();
	}
	public OutputStream getTerminalToRemoteStream() {
		return getConnectorImpl().getTerminalToRemoteStream();
	}
	public String getSettingsSummary() {
		if(fConnector!=null)
			return getConnectorImpl().getSettingsSummary();
		else
			return TerminalMessages.NotInitialized;
	}
	public boolean isLocalEcho() {
		return getConnectorImpl().isLocalEcho();
	}
	public void load(ISettingsStore store) {
		if(fConnector==null) {
			fStore=store;
		} else {
			getConnectorImpl().load(store);
		}
	}
	public ISettingsPage makeSettingsPage() {
		return getConnectorImpl().makeSettingsPage();
	}
	public void save(ISettingsStore store) {
		// no need to save the settings: it cannot have changed
		// because we are not initialized....
		if(fConnector!=null)
			getConnectorImpl().save(store);
	}
	public void setTerminalSize(int newWidth, int newHeight) {
		// we assume that setTerminalSize is called also after
		// the terminal has been initialized. Else we would have to cache
		// the values....
		if(fConnector!=null) {
			fConnector.setTerminalSize(newWidth, newHeight);
		}
	}
	public Object getAdapter(Class adapter) {
		TerminalConnectorImpl connector=null;
		if(isInitialized())
			connector=getConnectorImpl();
		// if we cannot create the connector then we cannot adapt...
		if(connector!=null) {
			// maybe the connector is adaptable
			if(connector instanceof IAdaptable) {
				Object result =((IAdaptable)connector).getAdapter(adapter);
				// Not sure if the next block is needed....
				if(result==null)
					//defer to the platform
					result= Platform.getAdapterManager().getAdapter(connector, adapter);
				if(result!=null)
					return result;
			}
			// maybe the real adapter is what we need....
			if(adapter.isInstance(connector))
				return connector;
		}
		// maybe we have to be adapted....
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}
}