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
package com.aptana.portal.ui.dispatch;

/**
 * Constants to be used when creating JSON strings that will be passes to the browser when we need to notify the its
 * observers.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public interface IBrowserNotificationConstants
{
	// #################################################################################################################
	/*
	 * Event related constants. The Event is fired from the Eclipse/Studio side to the portal Browser and is handled by
	 * JavaScript.
	 */
	/**
	 * Event identifier (the JSON should contain a value next to this event key)
	 */
	public static final String EVENT = "event"; //$NON-NLS-1$
	/**
	 * Any appended data should be inserted as a value to this key
	 */
	public static final String EVENT_DATA = "data"; //$NON-NLS-1$
	/**
	 * An event type (any one of the EVENT_TYPE_XXX values)
	 */
	public static final String EVENT_TYPE = "eventType"; //$NON-NLS-1$
	/**
	 * An event which signals a change.
	 */
	public static final String EVENT_TYPE_CHANGED = "changed"; //$NON-NLS-1$
	/**
	 * An event which signals an addition.
	 */
	public static final String EVENT_TYPE_ADDED = "added"; //$NON-NLS-1$
	/**
	 * An event which signals a deletion.
	 */
	public static final String EVENT_TYPE_DELETED = "deleted"; //$NON-NLS-1$
	/**
	 * An event which signals an immediate response to some action dispatch.
	 */
	public static final String EVENT_TYPE_RESPONSE = "response"; //$NON-NLS-1$

	// #################################################################################################################
	/*
	 * Dispatch related constants. A dispatch is triggered by the browser side, and is handled by the registered
	 * dispatch BrowserFunctions.
	 */
	/**
	 * The browser function name for dispatching an action.
	 */
	public static final String DISPATCH_FUNCTION_NAME = "dispatch"; //$NON-NLS-1$

	/**
	 * The controller key string that the browser placed in the JSON dispatch command to identify the controller type.
	 */
	public static final String DISPATCH_CONTROLLER = "controller"; //$NON-NLS-1$

	/**
	 * The action key string that the browser placed in the JSON dispatch command to identify the action.
	 */
	public static final String DISPATCH_ACTION = "action"; //$NON-NLS-1$

	/**
	 * The arguments key string that the browser placed in the JSON dispatch command to identify the arguments.
	 */
	public static final String DISPATCH_ARGUMENTS = "args"; //$NON-NLS-1$

	// #################################################################################################################
	/*
	 * JSON response constants that are use to signal error/success states to the browser side.
	 */
	/**
	 * Indicate a successful action dispatch.
	 */
	public static final String JSON_OK = "ok"; //$NON-NLS-1$

	/**
	 * Indicate an unsuccessful action dispatch.
	 */
	public static final String JSON_ERROR = "error"; //$NON-NLS-1$

	/**
	 * Indicate that the returned JSON object contains extra error details that the Studio provided.
	 */
	public static final String ERROR_DETAILS = "errorDetails"; //$NON-NLS-1$

	/**
	 * An error type that indicates an unsuccessful action dispatch due to an argument error (wrong number of arguments,
	 * for example).
	 */
	public static final String JSON_ERROR_WRONG_ARGUMENTS = "Argument Error"; //$NON-NLS-1$

	/**
	 * An error type that indicates an unsuccessful action dispatch due to an unknown controller request.
	 */
	public static final String JSON_ERROR_UNKNOWN_CONTROLLER = "Unknown Controller"; //$NON-NLS-1$

	/**
	 * An error type that indicates an unsuccessful action dispatch due to an unknown controller-action request.
	 */
	public static final String JSON_ERROR_UNKNOWN_ACTION = "Unknown Action"; //$NON-NLS-1$

	// #################################################################################################################

	/*
	 * Event ID constants that the browser recognize and handle when they are fired as a result of Eclipse/Studio event.
	 */
	/**
	 * An event id for notifying a recent-files change.
	 */
	public static final String EVENT_ID_RECENT_FILES = "recentFiles"; //$NON-NLS-1$

	/**
	 * An event id for notifying a change in the Gems list.
	 */
	public static final String EVENT_ID_GEM_LIST = "gemList"; //$NON-NLS-1$
	
	/**
	 * An event id for notifying a change in the plugins list.
	 */
	public static final String EVENT_ID_PLUGINS = "plugins"; //$NON-NLS-1$
	
	/**
	 * An event id for notifying a change in the applications versions list.
	 */
	public static final String EVENT_ID_VERSIONS_LIST = "app-versions"; //$NON-NLS-1$

}
