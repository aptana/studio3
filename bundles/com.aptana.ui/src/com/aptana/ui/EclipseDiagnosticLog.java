/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui;

import java.text.MessageFormat;

import org.eclipse.core.runtime.Platform;

import com.aptana.core.diagnostic.IDiagnosticLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.StringUtil;

public class EclipseDiagnosticLog implements IDiagnosticLog
{

	private static final char NEW_LINE = '\n';

	public String getLog()
	{
		StringBuilder buf = new StringBuilder();

		// --- OS ---
		// Host OS
		buf.append(Messages.EclipseDiagnosticLog_host_os);
		buf.append(System.getProperty("os.name")); //$NON-NLS-1$
		buf.append(NEW_LINE);

		// Host Version
		buf.append(Messages.EclipseDiagnosticLog_os_version);
		buf.append(System.getProperty("os.version")); //$NON-NLS-1$
		buf.append(NEW_LINE);

		// OS arch
		buf.append(Messages.EclipseDiagnosticLog_os_arch);
		buf.append(Platform.getOSArch());
		buf.append(NEW_LINE);
		buf.append(NEW_LINE);

		// --- JRE ---
		// JRE version
		buf.append(Messages.EclipseDiagnosticLog_jre_version);
		buf.append(System.getProperty("java.version")); //$NON-NLS-1$
		buf.append(NEW_LINE);

		// Java vendor
		buf.append(Messages.EclipseDiagnosticLog_jre_vendor);
		buf.append(System.getProperty("java.vendor")); //$NON-NLS-1$
		buf.append(NEW_LINE);

		// JRE home
		buf.append(Messages.EclipseDiagnosticLog_jre_home);
		buf.append(System.getProperty("java.home")); //$NON-NLS-1$
		buf.append(NEW_LINE);
		buf.append(NEW_LINE);

		// -- Product/RCP ---
		// Eclipse/Studio/Product version
		buf.append(MessageFormat.format(Messages.EclipseDiagnosticLog_version, Platform.getProduct().getName()));
		String version = EclipseUtil.getProductVersion();
		if (!StringUtil.isEmpty(version))
		{
			buf.append(version);
		}
		buf.append(NEW_LINE);

		// Install Directory
		buf.append(Messages.EclipseDiagnosticLog_install_dir);
		buf.append(Platform.getInstallLocation().getURL());
		buf.append(NEW_LINE);

		// workspace area
		buf.append(Messages.EclipseDiagnosticLog_workspace_dir);
		buf.append(Platform.getInstanceLocation().getURL());
		buf.append(NEW_LINE);

		// VM arguments
		buf.append(Messages.EclipseDiagnosticLog_vm_args);
		String property = System.getProperty("eclipse.vmargs"); //$NON-NLS-1$
		buf.append((property == null) ? StringUtil.EMPTY : property);
		buf.append(NEW_LINE);

		// Language
		buf.append(Messages.EclipseDiagnosticLog_language);
		buf.append(Platform.getNL());
		buf.append(NEW_LINE);

		return buf.toString();
	}
}
