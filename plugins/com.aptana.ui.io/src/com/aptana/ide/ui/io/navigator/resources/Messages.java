/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator.resources;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "com.aptana.ide.ui.io.navigator.resources.messages"; //$NON-NLS-1$

    public static String FileDropAdapterAssistant_ERR_Copying;
    public static String FileDropAdapterAssistant_ERR_DragAndDrop_Title;
    public static String FileDropAdapterAssistant_ERR_DropLocalRoot;
    public static String FileDropAdapterAssistant_ERR_Importing;
    public static String FileDropAdapterAssistant_ERR_InvalidDropSelection;
    public static String FileDropAdapterAssistant_ERR_Moving;
    public static String FileDropAdapterAssistant_ERR_NotAdaptable;
    public static String FileDropAdapterAssistant_ERR_NotIFileStore;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
