/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.js.debug.ui.internal;

import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.debug.internal.ui.ColorManager;
import org.eclipse.debug.ui.console.ConsoleColorProvider;
import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.Color;

import com.aptana.debug.core.IDebugCoreConstants;
import com.aptana.debug.core.IExtendedStreamsProxy;
import com.aptana.js.debug.core.IJSDebugConstants;
import com.aptana.js.debug.ui.JSDebugUIPlugin;

/**
 * @author Max Stepanov
 */
@SuppressWarnings("restriction")
public class JSConsoleColorProvider extends ConsoleColorProvider {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.ui.console.ConsoleColorProvider#connect(org.eclipse.debug.core.model.IProcess,
	 * org.eclipse.debug.ui.console.IConsole)
	 */
	@Override
	public void connect(IProcess process, IConsole console) {
		super.connect(process, console);
		IStreamsProxy streamsProxy = process.getStreamsProxy();
		if (streamsProxy instanceof IExtendedStreamsProxy) {
			IExtendedStreamsProxy extendedStreamsProxy = (IExtendedStreamsProxy) streamsProxy;
			for (String streamIdentifer : extendedStreamsProxy.getStreamIdentifers()) {
				if (!IDebugCoreConstants.ID_STANDARD_ERROR_STREAM.equals(streamIdentifer)
						&& !IDebugCoreConstants.ID_STANDARD_OUTPUT_STREAM.equals(streamIdentifer)) {
					console.connect(extendedStreamsProxy.getStreamMonitor(streamIdentifer), streamIdentifer);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.ui.console.ConsoleColorProvider#getColor(java.lang.String)
	 */
	@Override
	public Color getColor(String streamIdentifer) {
		if (IJSDebugConstants.ID_WARNING_STREAM.equals(streamIdentifer)) {
			return ColorManager.getDefault().getColor(
					PreferenceConverter.getColor(JSDebugUIPlugin.getDefault().getPreferenceStore(),
							IJSDebugUIConstants.CONSOLE_WARN_COLOR));
		}
		return super.getColor(streamIdentifer);
	}

}
