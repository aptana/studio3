package com.aptana.editor.yaml;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.outline.CommonOutlinePage;
import com.aptana.editor.common.parsing.FileService;
import com.aptana.editor.common.text.reconciler.IFoldingComputer;
import com.aptana.editor.yaml.internal.text.YAMLFoldingComputer;
import com.aptana.editor.yaml.outline.YAMLOutlineContentProvider;
import com.aptana.editor.yaml.outline.YAMLOutlineLabelProvider;
import com.aptana.editor.yaml.parsing.IYAMLParserConstants;

public class YAMLEditor extends AbstractThemeableEditor
{

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.AbstractThemeableEditor#initializeEditor()
	 */
	@Override
	protected void initializeEditor()
	{
		super.initializeEditor();

		ChainedPreferenceStore store = new ChainedPreferenceStore(new IPreferenceStore[] {
				YAMLPlugin.getDefault().getPreferenceStore(), getPreferenceStore() });
		setPreferenceStore(store);
		
		setSourceViewerConfiguration(new YAMLSourceViewerConfiguration(getPreferenceStore(), this));
		setDocumentProvider(new YAMLDocumentProvider());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.AbstractThemeableEditor#createFileService()
	 */
	@Override
	protected FileService createFileService()
	{
		return new FileService(IYAMLParserConstants.LANGUAGE);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.AbstractThemeableEditor#createOutlinePage()
	 */
	@Override
	protected CommonOutlinePage createOutlinePage()
	{
		CommonOutlinePage outline = super.createOutlinePage();
		outline.setContentProvider(new YAMLOutlineContentProvider());
		outline.setLabelProvider(new YAMLOutlineLabelProvider());

		return outline;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.AbstractThemeableEditor#getOutlinePreferenceStore()
	 */
	@Override
	protected IPreferenceStore getOutlinePreferenceStore()
	{
		return YAMLPlugin.getDefault().getPreferenceStore();
	}

	@Override
	public IFoldingComputer createFoldingComputer(IDocument document)
	{
		return new YAMLFoldingComputer(this, document);
	}

}
