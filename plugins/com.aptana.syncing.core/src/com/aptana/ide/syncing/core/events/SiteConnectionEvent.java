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
package com.aptana.ide.syncing.core.events;

import java.util.EventObject;

import com.aptana.ide.syncing.core.ISiteConnection;

/**
 * @author Michael Xia
 * @author Max Stepanov
 */
public final class SiteConnectionEvent extends EventObject {

	private static final long serialVersionUID = 1L;

    /**
     * Event kind constant (bit mask) indicating an after-the-fact report of
     * creation to a site connection
     */
    public static final int POST_ADD = 1;

    /**
     * Event kind constant (bit mask) indicating an after-the-fact report of
     * deletion to a site connection
     */
    public static final int POST_DELETE = 2;

    /**
     * Event kind constant (bit mask) indicating an after-the-fact report of
     * alteration to a site connection
     */
    public static final int POST_CHANGE = 4;

    
	private ISiteConnection fSiteConnection;
	private int fKind;
	
	public SiteConnectionEvent(Object source, int kind, ISiteConnection siteConnection) {
		super(source);
		fSiteConnection = siteConnection;
		fKind = kind;
	}

    /**
     * Returns the site connection in question or <code>null</code> if not
     * applicable to this type of event.
     * 
     * @return the site connection, or <code>null</code> if not applicable
     */
    public ISiteConnection getSiteConnection() {
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
    public int getKind() {
    	return fKind;
    }
	
}
