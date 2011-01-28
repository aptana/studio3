/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.configurations.processor;

import java.util.Set;


/**
 * A listener interface for any party that wish to be informed of any change of state during the configuration progress.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public interface IConfigurationProcessorListener
{
	/**
	 * Notify that a configuration state change has occurred.
	 * 
	 * @param status
	 *            The ConfigurationStatus that contains the most updated data.
	 * @param attributesChanged
	 *            An Set of changed/added/removed attributes that were involved with this event and can be requested
	 *            from the ConfigurationStatus
	 */
	public void configurationStateChanged(ConfigurationStatus status, Set<String> attributesChanged);
}
