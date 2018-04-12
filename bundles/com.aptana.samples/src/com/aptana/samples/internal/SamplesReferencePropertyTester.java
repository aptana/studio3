/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples.internal;

import org.eclipse.core.expressions.PropertyTester;

import com.aptana.samples.model.SamplesReference;

public class SamplesReferencePropertyTester extends PropertyTester
{

	private static final String CAN_PREVIEW = "canPreview"; //$NON-NLS-1$
	private static final String HAS_HELP = "hasHelp"; //$NON-NLS-1$

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
	{
		if (receiver instanceof SamplesReference)
		{
			SamplesReference samplesRef = (SamplesReference) receiver;
			if (CAN_PREVIEW.equals(property))
			{
				return (samplesRef.getPreviewHandler() != null) == toBoolean(expectedValue);
			}
			if (HAS_HELP.equals(property))
			{
				return (samplesRef.getInfoFile() != null) == toBoolean(expectedValue);
			}
		}
		return false;
	}

	private static boolean toBoolean(Object value)
	{
		if (value instanceof Boolean)
		{
			return ((Boolean) value).booleanValue();
		}
		if (value instanceof String)
		{
			return Boolean.parseBoolean((String) value);
		}
		return false;
	}
}
