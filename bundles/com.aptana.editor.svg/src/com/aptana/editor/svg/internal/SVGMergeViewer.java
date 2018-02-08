/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.svg.internal;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.contentmergeviewer.TextMergeViewer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.widgets.Composite;

import com.aptana.editor.common.ExtendedFastPartitioner;
import com.aptana.editor.common.NullPartitionerSwitchStrategy;
import com.aptana.editor.common.text.rules.CompositePartitionScanner;
import com.aptana.editor.common.text.rules.NullSubPartitionScanner;
import com.aptana.editor.common.viewer.CommonMergeViewer;
import com.aptana.editor.svg.SVGEditor;
import com.aptana.editor.svg.SVGSourceConfiguration;
import com.aptana.editor.svg.SVGSourceViewerConfiguration;

/**
 * @author cwilliams
 */
public class SVGMergeViewer extends CommonMergeViewer
{
	public SVGMergeViewer(Composite parent, CompareConfiguration configuration)
	{
		super(parent, configuration);
	}

	@Override
	protected IDocumentPartitioner getDocumentPartitioner()
	{
		CompositePartitionScanner partitionScanner = new CompositePartitionScanner( //
				SVGSourceConfiguration.getDefault().createSubPartitionScanner(), //
				new NullSubPartitionScanner(), //
				new NullPartitionerSwitchStrategy() //
		);
		IDocumentPartitioner partitioner = new ExtendedFastPartitioner( //
				partitionScanner, //
				SVGSourceConfiguration.getDefault().getContentTypes() //
		);
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
			IPreferenceStore preferences = SVGEditor.getChainedPreferenceStore();
			SVGSourceViewerConfiguration config = new SVGSourceViewerConfiguration(preferences, null);
			sourceViewer.configure(config);
		}
	}
}
