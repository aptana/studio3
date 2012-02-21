/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.text.reconciler.IFoldingComputer;
import com.aptana.editor.js.actions.IJSActions;
import com.aptana.editor.js.actions.OpenDeclarationAction;
import com.aptana.editor.js.internal.text.JSFoldingComputer;
import com.aptana.editor.js.outline.JSOutlineContentProvider;
import com.aptana.editor.js.outline.JSOutlineLabelProvider;
import com.aptana.editor.js.parsing.JSParseState;
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.ast.IParseRootNode;

@SuppressWarnings("restriction")
public class JSSourceEditor extends AbstractThemeableEditor
{

	@Override
	protected void initializeEditor()
	{
		super.initializeEditor();

		setPreferenceStore(getChainedPreferenceStore());

		setSourceViewerConfiguration(new JSSourceViewerConfiguration(getPreferenceStore(), this));
		setDocumentProvider(JSPlugin.getDefault().getJSDocumentProvider());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.AbstractThemeableEditor#createActions()
	 */
	@Override
	protected void createActions()
	{
		super.createActions();
		IAction action = new OpenDeclarationAction(Messages.getResourceBundle(), this);
		action.setActionDefinitionId(IJSActions.OPEN_DECLARATION);
		setAction(IJSActions.OPEN_DECLARATION, action);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#editorContextMenuAboutToShow(org.eclipse.jface.action.
	 * IMenuManager)
	 */
	@Override
	protected void editorContextMenuAboutToShow(IMenuManager menu)
	{
		super.editorContextMenuAboutToShow(menu);

		IAction action = getAction(IJSActions.OPEN_DECLARATION);

		if (action != null)
		{
			menu.appendToGroup("group.open", action); //$NON-NLS-1$
		}
	}

	public static IPreferenceStore getChainedPreferenceStore()
	{
		return new ChainedPreferenceStore(new IPreferenceStore[] { JSPlugin.getDefault().getPreferenceStore(),
				CommonEditorPlugin.getDefault().getPreferenceStore(), EditorsPlugin.getDefault().getPreferenceStore() });
	}

	@Override
	public ITreeContentProvider getOutlineContentProvider()
	{
		return new JSOutlineContentProvider();
	}

	@Override
	public ILabelProvider getOutlineLabelProvider()
	{
		return new JSOutlineLabelProvider();
	}

	@Override
	protected IPreferenceStore getOutlinePreferenceStore()
	{
		return JSPlugin.getDefault().getPreferenceStore();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.AbstractThemeableEditor#getPluginPreferenceStore()
	 */
	@Override
	protected IPreferenceStore getPluginPreferenceStore()
	{
		return JSPlugin.getDefault().getPreferenceStore();
	}

	@Override
	public IFoldingComputer createFoldingComputer(IDocument document)
	{
		return new JSFoldingComputer(this, document);
	}

	@Override
	public String getContentType()
	{
		return IJSConstants.CONTENT_TYPE_JS;
	}

	@Override
	public IParseRootNode getAST()
	{
		try
		{
			// Don't attach or collect comments for hovers/outline
			IDocument document = getDocument();
			JSParseState parseState = new JSParseState();
			parseState.setAttachComments(false);
			parseState.setCollectComments(false);
			parseState.setEditState(document.get());
			return ParserPoolFactory.parse(getContentType(), parseState);
		}
		catch (Exception e)
		{
			IdeLog.logTrace(JSPlugin.getDefault(), "Failed to parse JS editor contents", e, //$NON-NLS-1$
					com.aptana.parsing.IDebugScopes.PARSING);
		}
		return null;
	}
}
