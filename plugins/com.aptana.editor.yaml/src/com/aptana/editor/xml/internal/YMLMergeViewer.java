/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.internal;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.contentmergeviewer.ContentMergeViewer;
import org.eclipse.compare.contentmergeviewer.TextMergeViewer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.widgets.Composite;

import com.aptana.editor.common.ExtendedFastPartitioner;
import com.aptana.editor.common.viewer.CommonMergeViewer;
import com.aptana.editor.yaml.YAMLEditor;
import com.aptana.editor.yaml.YAMLSourceConfiguration;
import com.aptana.editor.yaml.YAMLSourcePartitionScanner;
import com.aptana.editor.yaml.YAMLSourceViewerConfiguration;

/**
 * @author cwilliams
 */
public class YMLMergeViewer extends CommonMergeViewer
{
	public YMLMergeViewer(Composite parent, CompareConfiguration configuration)
	{
		super(parent, configuration);
	}

	@Override
	protected IDocumentPartitioner getDocumentPartitioner()
	{
		IDocumentPartitioner partitioner = new ExtendedFastPartitioner(new YAMLSourcePartitionScanner(),
				YAMLSourceConfiguration.getDefault().getContentTypes());
		return partitioner;
	}

	@Override
	protected void configureTextViewer(TextViewer textViewer)
	{
		super.configureTextViewer(textViewer);

		if (textViewer instanceof SourceViewer)
		{
			SourceViewer sourceViewer = (SourceViewer) textViewer;
			sourceViewer.unconfigure();
			IPreferenceStore preferences = YAMLEditor.getChainedPreferenceStore();
			YAMLSourceViewerConfiguration config = new YAMLSourceViewerConfiguration(preferences, null);
			sourceViewer.configure(config);
		}
	}
}
