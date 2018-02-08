/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.theme.extensions;

import java.util.Map;
import java.util.Set;

import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.ui.console.TextConsolePage;
import org.eclipse.ui.part.IPageBookViewPage;

import com.aptana.theme.ConsoleThemer;
import com.aptana.theme.internal.TextViewerThemer;

/**
 * Will set the colors for any console created that has the "themeConsoleStreamToColor" properly set.
 */
public class ConsoleThemePageParticipant implements IConsolePageParticipant
{

	public static final String THEME_CONSOLE_STREAM_TO_COLOR_ATTRIBUTE = "themeConsoleStreamToColor"; //$NON-NLS-1$
	private ConsoleThemer extension;
	private TextViewerThemer themer;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.console.IConsolePageParticipant#init(org.eclipse.ui.part.IPageBookViewPage,
	 * org.eclipse.ui.console.IConsole)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void init(IPageBookViewPage page, IConsole console)
	{
		if (console instanceof TextConsole)
		{
			TextConsole textConsole = (TextConsole) console;
			Object themeConsoleStreamToColor = textConsole.getAttribute(THEME_CONSOLE_STREAM_TO_COLOR_ATTRIBUTE);
			if (themeConsoleStreamToColor instanceof Map<?, ?>)
			{
				Map m = (Map) themeConsoleStreamToColor;
				Set<Map.Entry> entrySet = m.entrySet();
				for (Map.Entry entry : entrySet)
				{
					if (!(entry.getKey() instanceof IOConsoleOutputStream) || !(entry.getValue() instanceof String))
					{
						return; // Cannot handle it.
					}
				}
				this.extension = new ConsoleThemer(textConsole, (Map) themeConsoleStreamToColor);
			}
			if (page instanceof TextConsolePage)
			{
				TextConsolePage tcp = (TextConsolePage) page;
				themer = new TextViewerThemer(tcp.getViewer());
				themer.apply();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.console.IConsolePageParticipant#activated()
	 */
	public void activated()
	{
		themer.apply();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.console.IConsolePageParticipant#deactivated()
	 */
	public void deactivated()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.console.IConsolePageParticipant#dispose()
	 */
	public void dispose()
	{
		if (this.extension != null)
		{
			this.extension.dispose();
			this.extension = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter)
	{
		return null;
	}

}