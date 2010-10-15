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
