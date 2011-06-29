/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.yaml;

import org.eclipse.jface.text.rules.IPartitionTokenScanner;

import com.aptana.editor.common.IPartitioningConfiguration;
import com.aptana.editor.common.SimpleDocumentProvider;

public class YAMLDocumentProvider extends SimpleDocumentProvider {
	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.SimpleDocumentProvider#getPartitionScanner()
	 */
	@Override
	public IPartitionTokenScanner createPartitionScanner() {
		return new YAMLSourcePartitionScanner();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonDocumentProvider#getDefaultContentType(java.lang.String)
	 */
	protected String getDefaultContentType(String filename) {
		return IYAMLConstants.CONTENT_TYPE_YAML;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.SimpleDocumentProvider#getPartitioningConfiguration()
	 */
	@Override
	public IPartitioningConfiguration getPartitioningConfiguration() {
		return YAMLSourceConfiguration.getDefault();
	}
}
