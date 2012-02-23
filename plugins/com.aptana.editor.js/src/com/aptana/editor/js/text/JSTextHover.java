/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.text;

import java.net.URI;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.internal.text.html.BrowserInformationControlInput;
import org.eclipse.jface.text.IInputChangedListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IURIEditorInput;

import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.contentassist.CommonTextHover;
import com.aptana.editor.common.hover.CustomBrowserInformationControl;
import com.aptana.editor.common.hover.DocumentationBrowserInformationControlInput;
import com.aptana.editor.js.contentassist.JSLocationIdentifier;
import com.aptana.editor.js.contentassist.JSModelFormatter;
import com.aptana.editor.js.contentassist.LocationType;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.hyperlink.JSHyperlinkDetector;
import com.aptana.editor.js.internal.JSModelUtil;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.ui.epl.UIEplPlugin;

@SuppressWarnings("restriction")
public class JSTextHover extends CommonTextHover implements ITextHover, ITextHoverExtension2
{

	private String fDocs;
	private String fHeader;

	/**
	 * getActiveNode
	 * 
	 * @param textViewer
	 * @param offset
	 * @return
	 */
	protected IParseNode getActiveNode(ITextViewer textViewer, int offset)
	{
		IParseNode result = null;

		if (this.isHoverEnabled())
		{
			AbstractThemeableEditor editor = this.getEditor(textViewer);
			IParseNode ast = editor.getAST();

			if (ast != null)
			{
				result = ast.getNodeAtOffset(offset);

				// We won't get a current node if the cursor is outside of the positions
				// recorded by the AST
				if (result == null)
				{
					if (offset < ast.getStartingOffset())
					{
						result = ast.getNodeAtOffset(ast.getStartingOffset());
					}
					else if (ast.getEndingOffset() < offset)
					{
						result = ast.getNodeAtOffset(ast.getEndingOffset());
					}
				}
			}
		}

		return result;
	}

	/**
	 * getEditor
	 * 
	 * @param textViewer
	 * @return
	 */
	protected AbstractThemeableEditor getEditor(ITextViewer textViewer)
	{
		AbstractThemeableEditor result = null;

		if (textViewer instanceof IAdaptable)
		{
			result = (AbstractThemeableEditor) ((IAdaptable) textViewer).getAdapter(AbstractThemeableEditor.class);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextHoverExtension2#getHoverInfo2(org.eclipse.jface.text.ITextViewer,
	 * org.eclipse.jface.text.IRegion)
	 */
	public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion)
	{
		try
		{
			IParseNode activeNode = getActiveNode(textViewer, hoverRegion.getOffset());
			if (activeNode == null)
			{
				return null;
			}

			// To avoid duplicating work, we generate the header and documentation together here
			// and then getHeader and getDocumentation just return the values.
			AbstractThemeableEditor editorPart = (AbstractThemeableEditor) getEditor();

			Index index = getIndex(editorPart);
			List<PropertyElement> properties = JSModelUtil.getProperties(editorPart, activeNode);
			if (!CollectionsUtil.isEmpty(properties))
			{
				fHeader = JSModelFormatter.TEXT_HOVER.getHeader(properties, index.getRoot());
				fDocs = JSModelFormatter.TEXT_HOVER.getDocumentation(properties);
				return getHoverInfo(activeNode, isBrowserControlAvailable(textViewer), null, editorPart, hoverRegion);
			}

			return null;
		}
		finally
		{
			fHeader = null;
			fDocs = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.hover.AbstractDocumentationHover#getHeader(java.lang.Object,
	 * org.eclipse.ui.IEditorPart, org.eclipse.jface.text.IRegion)
	 */
	@Override
	protected String getHeader(Object element, IEditorPart editorPart, IRegion hoverRegion)
	{
		return fHeader;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.hover.AbstractDocumentationHover#getDocumentation(java.lang.Object,
	 * org.eclipse.ui.IEditorPart, org.eclipse.jface.text.IRegion)
	 */
	@Override
	protected String getDocumentation(Object element, IEditorPart editorPart, IRegion hoverRegion)
	{
		return fDocs;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.hover.AbstractDocumentationHover#populateToolbarActions(org.eclipse.jface.action.
	 * ToolBarManager, com.aptana.editor.common.hover.CustomBrowserInformationControl)
	 */
	@Override
	public void populateToolbarActions(ToolBarManager tbm, CustomBrowserInformationControl iControl)
	{
		final OpenDeclarationAction openDeclarationAction = new OpenDeclarationAction(iControl);
		tbm.add(openDeclarationAction);
		IInputChangedListener inputChangeListener = new IInputChangedListener()
		{
			public void inputChanged(Object newInput)
			{
				if (newInput instanceof BrowserInformationControlInput)
				{
					openDeclarationAction.update();
				}
			}
		};
		iControl.addInputChangeListener(inputChangeListener);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextHover#getHoverRegion(org.eclipse.jface.text.ITextViewer, int)
	 */
	public IRegion getHoverRegion(ITextViewer textViewer, int offset)
	{
		IParseNode activeNode = this.getActiveNode(textViewer, offset);
		IRegion result = null;

		if (activeNode != null)
		{
			JSLocationIdentifier identifier = new JSLocationIdentifier(offset, activeNode);
			LocationType type = identifier.getType();

			switch (type)
			{
				case UNKNOWN:
				case NONE:
					break;

				default:
					IParseNode targetNode = identifier.getTargetNode();

					if (targetNode != null)
					{
						result = new Region(targetNode.getStartingOffset(), targetNode.getLength());
					}
			}
		}

		if (result == null)
		{
			result = new Region(offset, 0);
		}

		return result;
	}

	/**
	 * getEditorURI
	 * 
	 * @param textViewer
	 * @return
	 */
	protected URI getEditorURI(IEditorPart editorPart)
	{
		AbstractThemeableEditor editor = (AbstractThemeableEditor) editorPart;
		URI result = null;

		if (editor != null)
		{
			IEditorInput editorInput = editor.getEditorInput();

			if (editorInput instanceof IURIEditorInput)
			{
				IURIEditorInput fileEditorInput = (IURIEditorInput) editorInput;

				result = fileEditorInput.getURI();
			}
		}

		return result;
	}

	/**
	 * getIndex
	 * 
	 * @param editorPart
	 * @return
	 */
	protected Index getIndex(IEditorPart editorPart)
	{
		AbstractThemeableEditor editor = (AbstractThemeableEditor) editorPart;
		Index result = null;

		if (editor != null)
		{
			IEditorInput input = editor.getEditorInput();

			if (input instanceof IFileEditorInput)
			{
				IFile file = ((IFileEditorInput) input).getFile();
				IProject project = file.getProject();

				result = IndexManager.getInstance().getIndex(project.getLocationURI());
			}
		}

		return result;
	}

	/**
	 * Open declaration action.
	 */
	public class OpenDeclarationAction extends Action
	{
		private static final String IMG_OPEN_DECLARATION = "icons/full/elcl16/goto_input.gif"; //$NON-NLS-1$
		private static final String IMG_OPEN_DECLARATION_DISABLED = "icons/full/dlcl16/goto_input.gif"; //$NON-NLS-1$
		private CustomBrowserInformationControl iControl;
		private IHyperlink[] hyperlinks;

		/**
		 * @param iControl
		 */
		public OpenDeclarationAction(CustomBrowserInformationControl iControl)
		{
			setText(Messages.JSTextHover_openDeclarationTooltip);
			setImageDescriptor(UIEplPlugin.imageDescriptorFromPlugin(UIEplPlugin.PLUGIN_ID, IMG_OPEN_DECLARATION));
			setDisabledImageDescriptor(UIEplPlugin.imageDescriptorFromPlugin(UIEplPlugin.PLUGIN_ID,
					IMG_OPEN_DECLARATION_DISABLED));
			this.iControl = iControl;
		}

		/**
		 * Update the action
		 */
		void update()
		{
			BrowserInformationControlInput input = iControl.getInput();
			if (input instanceof DocumentationBrowserInformationControlInput)
			{
				JSHyperlinkDetector detector = new JSHyperlinkDetector();
				IRegion hoverRegion = ((DocumentationBrowserInformationControlInput) input).getHoverRegion();
				if (hoverRegion != null)
				{
					hyperlinks = detector.detectHyperlinks((AbstractThemeableEditor) getEditor(), hoverRegion, false);
					setEnabled(!ArrayUtil.isEmpty(hyperlinks) && hyperlinks[0] != null);
					return;
				}

			}
			setEnabled(false);
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.action.Action#run()
		 */
		@Override
		public void run()
		{
			// We already know that this hyperlink is valid. A check was made at the update call.
			iControl.dispose();
			hyperlinks[0].open();
		}
	}
}
