/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.ui.io.navigator;

import org.eclipse.ui.internal.navigator.extensions.SafeDelegateCommonLabelProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * @author Max Stepanov
 *
 */
@SuppressWarnings("restriction")
public class FileTreeLabelProvider extends SafeDelegateCommonLabelProvider {

	/**
	 * @param labelProvider
	 */
	public FileTreeLabelProvider() {
		super(new WorkbenchLabelProvider());
	}

}
