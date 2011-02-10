/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.core.io.internal;

import org.eclipse.core.expressions.PropertyTester;

import com.aptana.ide.core.io.IConnectionPointCategory;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class ConnectionCategoryPropertyTester extends PropertyTester {

    private static final String PROPERTY_ID = "id"; //$NON-NLS-1$

    public ConnectionCategoryPropertyTester() {
    }

    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        if (property.equals(PROPERTY_ID) && receiver instanceof IConnectionPointCategory) {
            IConnectionPointCategory category = (IConnectionPointCategory) receiver;
            return category.getId().equals(expectedValue.toString());
        }
        return false;
    }

}
