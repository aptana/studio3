/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.configurations.processor;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A configuration processor interface.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public interface IConfigurationProcessor
{
	/**
	 * Returns the ID of this processor.
	 * 
	 * @return The configuration processor ID, as specified in the extension that contributed it.
	 */
	public String getID();

	/**
	 * Returns the name of this processor.
	 * 
	 * @return The configuration processor name, as specified in the extension that contributed it.
	 */
	public String getName();

	/**
	 * Returns <code>true</code> if this processor is enabled; <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if this processor is enabled; <code>false</code> otherwise.
	 */
	public boolean isEnabled();

	/**
	 * Returns the configuration state.<br>
	 * In case the refresh is true, the configuration processor will refresh its status by (optionally) running some
	 * procedures. Otherwise, the returned result may be a cashed result that was computed before, or a result that
	 * indicates an unknown state.<br>
	 * The implementation of this call must be synchronous. When the refresh is true, the call is already made from a
	 * Job. Any Jobs inside the implementation should be joined to the caller thread.
	 * 
	 * @param progressMonitor
	 *            A progress monitor. Can be null if the caller is not interested in a progress update.
	 * @param attributes
	 *            An arbitrary attribute object that can be used in the status computation, mainly when the refresh
	 *            argument is true (can be null).
	 * @param refresh
	 *            True, when the caller wants this processor to re-check its state; False, to just return the known
	 *            state of the configuration.
	 * @return A {@link ConfigurationStatus} which holds the complete state details of this processor
	 */
	public ConfigurationStatus getStatus(IProgressMonitor progressMonitor, Object attributes, boolean refresh);

	/**
	 * Run the configuration process that this processor is designed to do. During this configuration, the registered
	 * configuration progress listeners might be informed of some changes.
	 * 
	 * @param progressMonitor
	 *            An optional progress monitor. May be null.
	 * @param attributes
	 *            An optional instance that can provide extra data that is needed for this processor. It can be anything
	 *            that the processor expects to get, and it can also be null.
	 * @see IConfigurationProcessor#addConfigurationProcessorListener(IConfigurationProcessorListener)
	 * @return A {@link ConfigurationStatus} which holds the complete state details of this processor <b>after</b> the
	 *         completion of this call.
	 */
	public ConfigurationStatus configure(IProgressMonitor progressMonitor, Object attributes);

	/**
	 * Add an IConfigurationProcessorListener to be notified when there are changes in the configuration state. This
	 * listener will be notified, usually, when the {@link #configure(IProgressMonitor)} is called and there are state
	 * changes.
	 * 
	 * @param listener
	 *            An {@link IConfigurationProcessorListener}. In case the same listener is added twice, nothing happens.
	 */
	public void addConfigurationProcessorListener(IConfigurationProcessorListener listener);

	/**
	 * Removes an IConfigurationProcessorListener.
	 * 
	 * @param listener
	 *            An {@link IConfigurationProcessorListener}
	 */
	public void removeConfigurationProcessorListener(IConfigurationProcessorListener listener);
}
