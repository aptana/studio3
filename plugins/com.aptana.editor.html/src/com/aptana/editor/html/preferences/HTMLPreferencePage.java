/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.preferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.aptana.core.util.EclipseUtil;
import com.aptana.editor.common.preferences.CommonEditorPreferencePage;
import com.aptana.editor.html.HTMLEditor;
import com.aptana.editor.html.HTMLPlugin;
import com.aptana.editor.html.core.preferences.IPreferenceConstants;
import com.aptana.ui.preferences.AptanaPreferencePage;

public class HTMLPreferencePage extends CommonEditorPreferencePage
{
	/**
	 * HTMLPreferencePage
	 */
	public HTMLPreferencePage()
	{
		super();
		setDescription(Messages.HTMLPreferencePage_LBL_Description);
		setPreferenceStore(HTMLPlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected void createFieldEditors()
	{
		super.createFieldEditors();

		Composite parent = getFieldEditorParent();
		Composite outlineGroup = AptanaPreferencePage.createGroup(parent, Messages.HTMLPreferencePage_OutlineGroup);
		outlineGroup.setLayout(GridLayoutFactory.swtDefaults().create());
		outlineGroup.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		createOutlineOptions(outlineGroup);
	}

	private void createOutlineOptions(Composite outlineGroup)
	{
		addField(new StringFieldEditor(IPreferenceConstants.HTML_OUTLINE_TAG_ATTRIBUTES_TO_SHOW,
				Messages.HTMLPreferencePage_LBL_TagAttributes, outlineGroup));
	}

	@Override
	protected Composite createContentAssistOptions(Composite parent)
	{
		Composite caOptions = super.createContentAssistOptions(parent);

		final Composite fieldEditorGroup = new Composite(parent, SWT.NONE);
		fieldEditorGroup.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).create());

		// Auto-insert closing tags
		BooleanFieldEditor closingTag = new BooleanFieldEditor(IPreferenceConstants.HTML_AUTO_CLOSE_TAG_PAIRS,
				Messages.HTMLPreferencePage_AutoInsertCloseTagLabel, fieldEditorGroup);
		addField(closingTag);

		// Hit remote URLs for src/href path children proposals
		BooleanFieldEditor remoteHREFProposals = new BooleanFieldEditor(
				IPreferenceConstants.HTML_REMOTE_HREF_PROPOSALS,
				Messages.HTMLPreferencePage_TraverseRemoteURIsForPathAssistLabel, fieldEditorGroup);
		addField(remoteHREFProposals);

		return caOptions;
	}

	@Override
	protected IEclipsePreferences getPluginPreferenceStore()
	{
		return EclipseUtil.instanceScope().getNode(HTMLPlugin.PLUGIN_ID);
	}

	@Override
	protected IPreferenceStore getChainedEditorPreferenceStore()
	{
		return HTMLEditor.getChainedPreferenceStore();
	}
}
