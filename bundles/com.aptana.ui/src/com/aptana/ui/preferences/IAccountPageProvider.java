/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.preferences;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public interface IAccountPageProvider
{

	public static interface IValidationListener
	{
		/**
		 * Notifies that validation is about to start.
		 */
		public void preValidationStart();

		/**
		 * Notifies that validation is completed.
		 */
		public void postValidationEnd();
	}

	/**
	 * Creates the contents contributed to the Accounts preferences page.
	 * 
	 * @return the container control
	 */
	public Control createContents(Composite parent);

	/**
	 * Notifies the provider that the OK button of the Accounts preferences page has been pressed.
	 * 
	 * @return false to abort the processing of OK operation and true to allow the operation to happen
	 */
	public boolean performOk();

	/**
	 * @return the priority of the provider that will be used to determine the order in which each appears in the
	 *         preferences page
	 */
	public int getPriority();

	/**
	 * Sets the progress monitor to be used when performing the validation.
	 * 
	 * @param progressMonitor
	 *            the progress monitor
	 */
	public void setProgressMonitor(IProgressMonitor progressMonitor);

	/**
	 * Adds a listener to get notified when the validation of credentials happens.
	 * 
	 * @param listener
	 *            the listener
	 */
	public void addValidationListener(IValidationListener listener);

	/**
	 * Removes a listener from getting notified when the validation of credentials happens.
	 * 
	 * @param listener
	 *            the listener
	 */
	public void removeValidationListener(IValidationListener listener);
}
