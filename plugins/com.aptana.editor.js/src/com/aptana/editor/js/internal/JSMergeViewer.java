/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.internal;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.widgets.Composite;

import com.aptana.editor.common.ExtendedFastPartitioner;
import com.aptana.editor.common.viewer.CommonMergeViewer;
import com.aptana.editor.js.JSSourceConfiguration;
import com.aptana.editor.js.JSSourceEditor;
import com.aptana.editor.js.JSSourceViewerConfiguration;
import com.aptana.editor.js.text.JSSourcePartitionScannerJFlex;

/**
 * @author cwilliams
 */
public class JSMergeViewer extends CommonMergeViewer
{
	public JSMergeViewer(Composite parent, CompareConfiguration configuration)
	{
		super(parent, configuration);
	}

	@Override
	protected IDocumentPartitioner getDocumentPartitioner()
	{
		IDocumentPartitioner partitioner = new ExtendedFastPartitioner(new JSSourcePartitionScannerJFlex(),
				JSSourceConfiguration.getDefault().getContentTypes());
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
			IPreferenceStore preferences = JSSourceEditor.getChainedPreferenceStore();
			JSSourceViewerConfiguration config = new JSSourceViewerConfiguration(preferences, null);
			sourceViewer.configure(config);
		}
	}
}
