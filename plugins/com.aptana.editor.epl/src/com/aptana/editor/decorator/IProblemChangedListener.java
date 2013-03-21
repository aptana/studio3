/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.decorator;

import org.eclipse.core.resources.IResource;

/**
 * Can be added to a ProblemMarkerManager to get notified about problem marker changes. Used to update error ticks.
 */
public interface IProblemChangedListener
{

	/**
	 * Called when problems changed. This call is posted in an async exec, therefore passed resources must not exist.
	 * 
	 * @param changedResources
	 *            A set with elements of type <code>IResource</code> that describe the resources that had an problem
	 *            change.
	 * @param isMarkerChange
	 *            If set to <code>true</code>, the change was a marker change, if <code>false</code>, the change came
	 *            from an annotation model modification.
	 */
	void problemsChanged(IResource[] changedResources, boolean isMarkerChange);

}
