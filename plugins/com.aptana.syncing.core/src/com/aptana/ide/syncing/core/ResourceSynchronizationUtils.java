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
package com.aptana.ide.syncing.core;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

/**
 * This is a utility class for synchronization related functionality and
 * settings for containers (i.e. projects and resource folders).
 * 
 * @author Sandip V. Chitale (schitale@aptana.com)
 */
public class ResourceSynchronizationUtils {

    public static final String LAST_SYNC_CONNECTION_KEY = "lastSyncConnection"; //$NON-NLS-1$
    public static final String REMEMBER_DECISION_KEY = "rememberDecision"; //$NON-NLS-1$

    /**
     * Returns the value of "Remember my decision" setting which indicate
     * whether to show the Choose Synchronization connection dialog when
     * multiple connections are associated with the container.
     * 
     * @param container
     * @return
     * 
     * @throws NullPointerException
     *             if the specified container is null
     */
    public static boolean isRememberDecision(IContainer container) {
        if (container == null) {
            throw new NullPointerException("Null resource container."); //$NON-NLS-1$
        }

        try {
            String remeberMyDecisionStringValue = container
                    .getPersistentProperty(new QualifiedName("", //$NON-NLS-1$
                            ResourceSynchronizationUtils.REMEMBER_DECISION_KEY));
            if (remeberMyDecisionStringValue != null) {
                return Boolean.TRUE.toString().equals(remeberMyDecisionStringValue);
            }
        } catch (CoreException e) {
        }
        return false;
    }

    /**
     * Sets the value of "Remember my decision" setting which indicate whether
     * to show the Choose Synchronization connection dialog when multiple
     * connections are associated with the container.
     * 
     * @param container
     * @param rememberMyDecision
     * 
     * @throws NullPointerException
     *             if the specified container is null
     */
    public static void setRememberDecision(IContainer container, boolean rememberMyDecision) {
        if (container == null) {
            throw new NullPointerException("Null resource container."); //$NON-NLS-1$
        }

        try {
            container.setPersistentProperty(new QualifiedName("", //$NON-NLS-1$
                    ResourceSynchronizationUtils.REMEMBER_DECISION_KEY), String
                    .valueOf(rememberMyDecision));
        } catch (CoreException e) {
        }
    }

    /**
     * Returns the last synchronization connection in a serialized form.
     * 
     * @param container
     * @return the last synchronization connection in a serialized form.
     * 
     * @throws NullPointerException
     *             if the specified container is null
     */
    public static String getLastSyncConnection(IContainer container) {
        if (container == null) {
            throw new NullPointerException("Null resource container."); //$NON-NLS-1$
        }

        try {
            return container.getPersistentProperty(new QualifiedName("", //$NON-NLS-1$
                    ResourceSynchronizationUtils.LAST_SYNC_CONNECTION_KEY));
        } catch (CoreException e) {
        }
        return null;
    }

    /**
     * Sets the value of last synchronization connection.
     * 
     * @param container
     * @param connection
     *            a string representing the last synchronization connection. A
     *            <code>null</code> or <code>""</code> removes the persistent
     *            setting.
     * @throws NullPointerException
     *             if the specified container is null
     */
    public static void setLastSyncConnection(IContainer container, String connection) {
        try {
            if (connection == null || connection.equals("")) { //$NON-NLS-1$
                container.setPersistentProperty(new QualifiedName("", //$NON-NLS-1$
                        ResourceSynchronizationUtils.LAST_SYNC_CONNECTION_KEY), null);
            } else {
                container.setPersistentProperty(new QualifiedName("", //$NON-NLS-1$
                        ResourceSynchronizationUtils.LAST_SYNC_CONNECTION_KEY), connection);
            }
        } catch (CoreException e) {
        }
    }
}
