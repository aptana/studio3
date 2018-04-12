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

import com.aptana.editor.common.text.rules.CompositePartitionScanner;

/**
 * @author Max Stepanov
 */
public abstract class CompositeDocumentProvider extends CommonDocumentProvider {

	private String documentContentType;
	private IPartitioningConfiguration defaultPartitioningConfiguration;
	private IPartitioningConfiguration primaryPartitioningConfiguration;
	private IPartitionerSwitchStrategy partitionerSwitchStrategy;

	/**
	 * @param documentContentType
	 * @param defaultPartitioningConfiguration
	 * @param primaryPartitioningConfiguration
	 * @param partitionerSwitchStrategy
	 */
	protected CompositeDocumentProvider(String documentContentType, IPartitioningConfiguration defaultPartitioningConfiguration,
			IPartitioningConfiguration primaryPartitioningConfiguration, IPartitionerSwitchStrategy partitionerSwitchStrategy) {
		super();
		this.documentContentType = documentContentType;
		this.defaultPartitioningConfiguration = defaultPartitioningConfiguration;
		this.primaryPartitioningConfiguration = primaryPartitioningConfiguration;
		this.partitionerSwitchStrategy = partitionerSwitchStrategy;
	}

	@Override
	public void connect(Object element) throws CoreException {
		super.connect(element);

		IDocument document = getDocument(element);
		if (document != null) {
			CompositePartitionScanner partitionScanner = new CompositePartitionScanner(defaultPartitioningConfiguration.createSubPartitionScanner(),
					primaryPartitioningConfiguration.createSubPartitionScanner(), partitionerSwitchStrategy);
			IDocumentPartitioner partitioner = new ExtendedFastPartitioner(partitionScanner, TextUtils.combine(new String[][] { CompositePartitionScanner.SWITCHING_CONTENT_TYPES,
					defaultPartitioningConfiguration.getContentTypes(), primaryPartitioningConfiguration.getContentTypes() }));
			partitionScanner.setPartitioner((IExtendedPartitioner) partitioner);
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
			CommonEditorPlugin.getDefault().getDocumentScopeManager()
					.registerConfigurations(document, new IPartitioningConfiguration[] { defaultPartitioningConfiguration, primaryPartitioningConfiguration });
		}
	}

	protected String getDefaultContentType(String filename) {
		return documentContentType;
	}
}
