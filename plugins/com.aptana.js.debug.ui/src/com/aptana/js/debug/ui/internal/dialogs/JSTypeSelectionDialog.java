/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal.dialogs;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import com.aptana.js.debug.ui.internal.JSElementLabelProvider;

/**
 * @author Max Stepanov
 */
public class JSTypeSelectionDialog extends ElementListSelectionDialog {
	/** TODO: get type list from JS lang model */
	private static final String[] TYPE_LIST = { /* errors/exceptions */"Error", //$NON-NLS-1$
			"EvalError", //$NON-NLS-1$
			"RangeError", //$NON-NLS-1$
			"ReferenceError", //$NON-NLS-1$
			"SyntaxError", //$NON-NLS-1$
			"TypeError", //$NON-NLS-1$
			"URIError", //$NON-NLS-1$
			/* basic types */"Number", //$NON-NLS-1$
			"String", //$NON-NLS-1$
			"Boolean", //$NON-NLS-1$
			"Date", //$NON-NLS-1$
			"Array", //$NON-NLS-1$
			"Object" //$NON-NLS-1$
	};

	/**
	 * JSTypeSelectionDialog
	 * 
	 * @param parent
	 */
	public JSTypeSelectionDialog(Shell parent) {
		super(parent, new JSElementLabelProvider());
		setElements(TYPE_LIST);
	}
}
