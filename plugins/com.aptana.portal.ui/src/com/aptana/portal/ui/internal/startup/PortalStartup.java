package com.aptana.portal.ui.internal.startup;

import org.eclipse.ui.IStartup;

import com.aptana.portal.ui.internal.Portal;

/**
 * Aptana portal browser startup.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class PortalStartup implements IStartup
{
	@Override
	public void earlyStartup()
	{
		Portal.getInstance().openPortal(null);
	}
}
