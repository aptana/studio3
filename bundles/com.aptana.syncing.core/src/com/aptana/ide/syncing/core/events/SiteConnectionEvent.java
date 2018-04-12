/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.core.events;

import java.util.EventObject;

import com.aptana.ide.syncing.core.ISiteConnection;

/**
 * @author Michael Xia
 * @author Max Stepanov
 */
public final class SiteConnectionEvent extends EventObject
{

	private static final long serialVersionUID = 1L;

	/**
	 * Event kind constant (bit mask) indicating an after-the-fact report of creation to a site connection
	 */
	public static final int POST_ADD = 1;

	/**
	 * Event kind constant (bit mask) indicating an after-the-fact report of deletion to a site connection
	 */
	public static final int POST_DELETE = 2;

	/**
	 * Event kind constant (bit mask) indicating an after-the-fact report of alteration to a site connection
	 */
	public static final int POST_CHANGE = 4;

	private ISiteConnection fSiteConnection;
	private int fKind;

	public SiteConnectionEvent(Object source, int kind, ISiteConnection siteConnection)
	{
		super(source);
		fSiteConnection = siteConnection;
		fKind = kind;
	}

	/**
	 * Returns the site connection in question or <code>null</code> if not applicable to this type of event.
	 * 
	 * @return the site connection, or <code>null</code> if not applicable
	 */
	public ISiteConnection getSiteConnection()
	{
		return fSiteConnection;
	}

	/**
	 * Returns the kind of event being reported.
	 * 
	 * @return one of the event kind constants
	 * @see #POST_ADD
	 * @see #POST_DELETE
	 * @see #POST_CHANGE
	 */
	public int getKind()
	{
		return fKind;
	}

}
