/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui;

import org.eclipse.core.runtime.Platform;

public class EclipseDiagnosticLog implements IDiagnosticLog
{

	public String getLog()
	{
		StringBuilder buf = new StringBuilder();

		// Host OS
		buf.append(Messages.EclipseDiagnosticLog_host_os);
		buf.append(System.getProperty("os.name")); //$NON-NLS-1$
		buf.append("\n"); //$NON-NLS-1$

		// OS arch
		buf.append(Messages.EclipseDiagnosticLog_os_arch);
		buf.append(Platform.getOSArch());
		buf.append("\n"); //$NON-NLS-1$

		// JRE version
		buf.append(Messages.EclipseDiagnosticLog_jre_version);
		buf.append(System.getProperty("java.version")); //$NON-NLS-1$
		buf.append("\n"); //$NON-NLS-1$

		// Java vendor
		buf.append(Messages.EclipseDiagnosticLog_jre_vendor);
		buf.append(System.getProperty("java.vendor")); //$NON-NLS-1$
		buf.append("\n"); //$NON-NLS-1$

		// JRE home
		buf.append(Messages.EclipseDiagnosticLog_jre_home);
		buf.append(System.getProperty("java.home")); //$NON-NLS-1$
		buf.append("\n"); //$NON-NLS-1$

		// Install Directory
		buf.append(Messages.EclipseDiagnosticLog_install_dir);
		buf.append(Platform.getInstallLocation().getURL());
		buf.append("\n"); //$NON-NLS-1$

		// Eclipse version
		buf.append(Messages.EclipseDiagnosticLog_eclipse_version);
		String property = System.getProperty("osgi.framework.version"); //$NON-NLS-1$
		int index = property.indexOf(".v"); //$NON-NLS-1$
		if (index > -1)
		{
			property = property.substring(0, index);
		}
		buf.append(property);
		buf.append("\n"); //$NON-NLS-1$

		// VM arguments
		buf.append(Messages.EclipseDiagnosticLog_vm_args);
		property = System.getProperty("eclipse.vmargs"); //$NON-NLS-1$
		buf.append((property == null) ? "" : property); //$NON-NLS-1$
		buf.append("\n"); //$NON-NLS-1$

		// workspace area
		buf.append(Messages.EclipseDiagnosticLog_workspace_dir);
		buf.append(Platform.getInstanceLocation().getURL());
		buf.append("\n"); //$NON-NLS-1$

		// Language
		buf.append(Messages.EclipseDiagnosticLog_language);
		buf.append(Platform.getNL());
		buf.append("\n"); //$NON-NLS-1$

		return buf.toString();
	}

}
