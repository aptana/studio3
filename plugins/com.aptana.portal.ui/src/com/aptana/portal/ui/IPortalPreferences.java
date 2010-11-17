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
package com.aptana.portal.ui;

/**
 * Holds preferences keys that are used by the portal (dev-toolbox)
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public interface IPortalPreferences
{
	/**
	 * Holds the preferences key for the installed applications cached versions and location.<br>
	 * The value that is stored on that key is a JSON string in that structure: {appName:{version:appVersion, location:appLocation}}
	 */
	String CACHED_VERSIONS_PROPERTY_NAME = PortalUIPlugin.PLUGIN_ID + ".cached_versions"; //$NON-NLS-1$
	String CACHED_VERSION_PROPERTY = "version"; //$NON-NLS-1$
	String CACHED_LOCATION_PROPERTY = "location"; //$NON-NLS-1$
	
	/**
	 * indicate whether or not the developer toolbox should be displayed on startup
	 */
	String SHOULD_OPEN_DEV_TOOLBOX = "open_developer_toolbox"; //$NON-NLS-1$
}
