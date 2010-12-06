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
package com.aptana.theme.extensions;

import java.util.Map;
import java.util.Set;

import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.ui.part.IPageBookViewPage;

import com.aptana.theme.ConsoleThemer;

/**
 * Will set the colors for any console created that has the "themeConsoleStreamToColor" properly set.
 */
public class ConsoleThemePageParticipant implements IConsolePageParticipant
{

	public static final String THEME_CONSOLE_STREAM_TO_COLOR_ATTRIBUTE = "themeConsoleStreamToColor"; //$NON-NLS-1$
	private ConsoleThemer extension;

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
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.console.IConsolePageParticipant#activated()
	 */
	public void activated()
	{
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
