/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.v8.test;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;

import com.aptana.core.util.SocketUtil;
import com.aptana.js.debug.core.internal.ProtocolLogger;
import com.aptana.js.debug.core.internal.model.DebugConnection;
import com.aptana.js.debug.core.internal.model.JSDebugProcess;
import com.aptana.js.debug.core.internal.model.JSDebugTarget;
import com.aptana.js.debug.core.v8.V8DebugConnection;
import com.aptana.js.debug.core.v8.V8DebugHost;
import com.aptana.js.debug.core.v8.V8DebugPlugin;

/**
 * @author Max Stepanov
 *
 */
public class V8LaunchConfigurationDelegate extends LaunchConfigurationDelegate {

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ILaunchConfigurationDelegate#launch(org.eclipse.debug.core.ILaunchConfiguration, java.lang.String, org.eclipse.debug.core.ILaunch, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		int port = SocketUtil.findFreePort(null);
		V8DebugHost debugHost = V8DebugHost.createDebugHost(new InetSocketAddress("127.0.0.1", port));
		ProcessBuilder pb = new ProcessBuilder("./lineprocessor", "capitalizer.js", "-p", Integer.toString(port), "--wait-for-connection", "--main-cycle-in-cpp");
		pb.directory(new File("/Users/max/dev/public/v8"));
		try {
			Process process = pb.start();
			DebugPlugin.newProcess(launch, process, "V8 Process");
			JSDebugTarget debugTarget = null;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			AppURIMapper uriMapper = new AppURIMapper(ResourcesPlugin.getWorkspace().getRoot().getProject("v8test"), Path.fromPortableString("Resources"));
			DebugConnection controller = V8DebugConnection.createConnection(debugHost, new ProtocolLogger("v8testdebugger", V8DebugPlugin.PLUGIN_ID));
			IProcess debugProcess = new JSDebugProcess(launch, process, false, "V8 Debug Process", null);
			debugTarget = new JSDebugTarget(launch, "V8 Debugger", debugProcess, uriMapper, controller, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
