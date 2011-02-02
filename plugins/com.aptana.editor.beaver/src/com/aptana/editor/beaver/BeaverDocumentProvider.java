/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.beaver;

import org.eclipse.jface.text.rules.IPartitionTokenScanner;

import com.aptana.editor.common.IPartitioningConfiguration;
import com.aptana.editor.common.SimpleDocumentProvider;

public class BeaverDocumentProvider extends SimpleDocumentProvider
{
	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.SimpleDocumentProvider#getPartitionScanner()
	 */
	@Override
	public IPartitionTokenScanner createPartitionScanner()
	{
		return new BeaverPartitionScanner();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonDocumentProvider#getDefaultContentType(java.lang.String)
	 */
	protected String getDefaultContentType(String filename)
	{
		return IBeaverConstants.CONTENT_TYPE_BEAVER;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.SimpleDocumentProvider#getPartitioningConfiguration()
	 */
	@Override
	public IPartitioningConfiguration getPartitioningConfiguration()
	{
		return BeaverSourceConfiguration.getDefault();
	}
}
