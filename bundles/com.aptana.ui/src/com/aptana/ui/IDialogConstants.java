/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui;

import com.aptana.core.CoreStrings;
import com.aptana.core.util.StringUtil;

/**
 * @author Max Stepanov
 */
public interface IDialogConstants extends org.eclipse.jface.dialogs.IDialogConstants
{

	public static final int APPLY_ID = 31;
	public static final int BROWSE_ID = 32;

	public static final String APPLY_LABEL = Messages.IDialogConstants_LBL_Apply;
	public static final String BROWSE_LABEL = StringUtil.ellipsify(CoreStrings.BROWSE);
	public static final String OVERWRITE_LABEL = Messages.IDialogConstants_LBL_Overwrite;
	public static final String RENAME_LABEL = CoreStrings.RENAME;
}
