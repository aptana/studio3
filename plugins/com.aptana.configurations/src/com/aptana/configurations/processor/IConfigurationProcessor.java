/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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
