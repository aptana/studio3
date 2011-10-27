/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ui.s3.dialogs;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "com.aptana.ui.s3.dialogs.messages"; //$NON-NLS-1$

    public static String S3ConnectionPointPropertyDialog_ConfirmMessage;
    public static String S3ConnectionPointPropertyDialog_ConfirmTitle;
    public static String S3ConnectionPointPropertyDialog_DefaultErrorMsg;
    public static String S3ConnectionPointPropertyDialog_ERR_FailedCreate;
    public static String S3ConnectionPointPropertyDialog_ERR_InvalidHost;
    public static String S3ConnectionPointPropertyDialog_ERR_NameEmpty;
    public static String S3ConnectionPointPropertyDialog_ERR_NameExists;
    public static String S3ConnectionPointPropertyDialog_ERR_NoAccessKey;
    public static String S3ConnectionPointPropertyDialog_ErrorTitle;
    public static String S3ConnectionPointPropertyDialog_LBL_Edit;
    public static String S3ConnectionPointPropertyDialog_LBL_Example;
    public static String S3ConnectionPointPropertyDialog_LBL_GroupInfo;
    public static String S3ConnectionPointPropertyDialog_LBL_Options;
    public static String S3ConnectionPointPropertyDialog_LBL_SecretAccessKey;
    public static String S3ConnectionPointPropertyDialog_LBL_RemotePath;
    public static String S3ConnectionPointPropertyDialog_LBL_Save;
    public static String S3ConnectionPointPropertyDialog_LBL_Server;
    public static String S3ConnectionPointPropertyDialog_LBL_SiteName;
    public static String S3ConnectionPointPropertyDialog_LBL_Test;
    public static String S3ConnectionPointPropertyDialog_LBL_AccessKeyID;
    public static String S3ConnectionPointPropertyDialog_Message_Browse;
    public static String S3ConnectionPointPropertyDialog_MessageTitle_Edit;
    public static String S3ConnectionPointPropertyDialog_MessageTitle_New;
    public static String S3ConnectionPointPropertyDialog_Succeed_Message;
    public static String S3ConnectionPointPropertyDialog_Succeed_Title;
    public static String S3ConnectionPointPropertyDialog_Task_Browse;
    public static String S3ConnectionPointPropertyDialog_Title;
    public static String S3ConnectionPointPropertyDialog_Title_Browse;
    public static String S3ConnectionPointPropertyDialog_Title_Edit;
    public static String S3ConnectionPointPropertyDialog_Title_New;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
