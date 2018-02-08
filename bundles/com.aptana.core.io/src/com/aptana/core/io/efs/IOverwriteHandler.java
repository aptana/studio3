/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.io.efs;

/**
 * A handler interface for handling situations where a copy operation fails due to an existing file in the target
 * destination. In those cases, this overwrite handler can be consulted with. For example, an implementor can ask the
 * user if the copy should be performed again with an 'overwrite' flag, or aborted.
 * 
 * @author sgibly@appcelerator.com
 */
public interface IOverwriteHandler
{
	/**
	 * Returns <code>true</code> if the operation should overwrite the filesystem.
	 * 
	 * @param data
	 *            An arbitrary data object that can be passed into this call.
	 * @return <code>true</code> if an overwrite should be done; <code>false</code> otherwise.
	 */
	boolean shouldOverwrite(Object data);
}