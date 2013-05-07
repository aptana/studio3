/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.webserver.core;

import java.net.URI;
import java.net.URL;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;

import com.aptana.core.IURIMapper;
import com.aptana.core.Identifiable;
import com.aptana.core.epl.IMemento;

/**
 * An instance of a server. This extends IURIMapper so that we can resolve files to URIs and URIs to files based on the
 * document root and base URL.
 * 
 * @author cwilliams
 */
public interface IServer extends IURIMapper, Identifiable
{

	/**
	 * Server state.
	 * 
	 * @author cwilliams
	 */
	public enum State
	{
		UNKNOWN, STARTING, STARTED, STOPPING, STOPPED, NOT_APPLICABLE
	}

	/**
	 * Returns true if this server instance should be persisted by manager
	 * 
	 * @return
	 */
	public boolean isPersistent();

	/**
	 * @return
	 */
	public IServerType getType();

	/**
	 * @return the name
	 */
	public String getName();

	/**
	 * FIXME Can we avoid making this public?
	 * 
	 * @param memento
	 */
	public void saveState(IMemento memento);

	/**
	 * FIXME Can we avoid making this public?
	 * 
	 * @param memento
	 */
	public void loadState(IMemento memento);

	/**
	 * This method returns whether the server is in a state where it can be stopped
	 * 
	 * @return
	 */
	public boolean canStop();

	/**
	 * This method runs synchronously. Wrap calls in a Job if you want async behavior.
	 * 
	 * @param force
	 * @param monitor
	 *            optional progress monitor
	 */
	public IStatus stop(boolean force, IProgressMonitor monitor);

	/**
	 * This method returns whether the server is in a state where it can be restarted
	 * 
	 * @return
	 */
	public boolean canRestart();

	/**
	 * This method runs synchronously. Wrap calls in a Job if you want async behavior.
	 * 
	 * @param mode
	 * @param monitor
	 *            optional progress monitor
	 */
	public IStatus restart(String mode, IProgressMonitor monitor);

	/**
	 * This method returns whether the server is in a state where it can be started
	 * 
	 * @return
	 */
	public boolean canStart();

	/**
	 * This method runs synchronously. Wrap calls in a Job if you want async behavior.
	 * 
	 * @param mode
	 * @param monitor
	 *            optional progress monitor
	 */
	public IStatus start(String mode, IProgressMonitor monitor);

	/**
	 * Returns the ILaunchManager mode that the server is in. This method will return null if the server is not running.
	 * 
	 * @return the mode in which a server is running, one of the mode constants defined by
	 *         {@link org.eclipse.debug.core.ILaunchManager}, or <code>null</code> if the server is stopped.
	 */
	public String getMode();

	/**
	 * Returns the current state of this server.
	 * 
	 * @return one of the server {@link State} enum values declared on IServer
	 */
	public State getState();

	/**
	 * Returns the launch that was used to start the server, if available. If the server is not running, or does not
	 * uses launches will return <code>null</code>.
	 * 
	 * @return the launch used to start the currently running server, or <code>null</code> if the launch is unavailable
	 *         or could not be found
	 */
	public ILaunch getLaunch();

	/**
	 * Returns the IProcesses related to the server if applicable.
	 * 
	 * @return - array of processes
	 */
	public IProcess[] getProcesses();

	/**
	 * Gets the hostname of this server.
	 * 
	 * @return - just the hostname
	 */
	public String getHostname();

	/**
	 * Gets the port of this server FIXME We need to separate out local versus remote port here. What port did we start
	 * it on locally and what port is it serving up on.
	 * 
	 * @return - just the port
	 */
	public int getPort();

	/**
	 * Gets the document root for this server. Typically this will be a file URI to a location on local disk, but can
	 * map to a remote location over SFTP/FTP/etc
	 * 
	 * @return - path of document root
	 */
	public URI getDocumentRoot();

	/**
	 * Returns the URL to the base of the server out on the web. To be used for opening browsers up to the server
	 * output.
	 * 
	 * @return
	 */
	public URL getBaseURL();

	/**
	 * Returns the Set of launch modes supported by this server.
	 * 
	 * @return
	 */
	public Set<String> getAvailableModes();

	public IStatus start(String mode, ILaunch launch, IProgressMonitor monitor);
}
