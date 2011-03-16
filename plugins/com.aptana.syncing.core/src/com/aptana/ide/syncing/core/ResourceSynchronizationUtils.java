/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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
