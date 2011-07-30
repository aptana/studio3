/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;

/**
 * SimpleDocumentProvider
 */
public abstract class SimpleDocumentProvider extends CommonDocumentProvider {

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonDocumentProvider#connect(java.lang.Object)
	 */
	@Override
	public void connect(Object element) throws CoreException {
		super.connect(element);

		IDocument document = this.getDocument(element);
		if (document != null) {
			IPartitioningConfiguration configuration = this.getPartitioningConfiguration();
			IDocumentPartitioner partitioner = new ExtendedFastPartitioner(this.createPartitionScanner(), configuration.getContentTypes());

			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);

			CommonEditorPlugin.getDefault().getDocumentScopeManager().registerConfiguration(document, configuration);
		}
	}

	/**
	 * Create a partition scanner for this editor's top-level language
	 * 
	 * @return
	 */
	public abstract IPartitionTokenScanner createPartitionScanner();

	/**
	 * Get the language's partition configuration
	 * 
	 * @return
	 */
	public abstract IPartitioningConfiguration getPartitioningConfiguration();
}
