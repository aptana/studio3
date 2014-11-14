/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.text;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;

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
import org.eclipse.ui.IEditorPart;

import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.ObjectUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.contentassist.CommonTextHover;
import com.aptana.editor.common.hover.CustomBrowserInformationControl;
import com.aptana.editor.common.hover.DocumentationBrowserInformationControlInput;
import com.aptana.editor.js.contentassist.JSLocationIdentifier;
import com.aptana.editor.js.contentassist.JSModelFormatter;
import com.aptana.editor.js.contentassist.LocationType;
import com.aptana.editor.js.hyperlink.JSHyperlinkDetector;
import com.aptana.editor.js.internal.JSModelUtil;
import com.aptana.index.core.Index;
import com.aptana.js.core.model.PropertyElement;
import com.aptana.js.core.model.ReturnTypeElement;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.ui.epl.UIEplPlugin;
import com.aptana.ui.util.UIUtils;

@SuppressWarnings("restriction")
public class JSTextHover extends CommonTextHover implements ITextHover, ITextHoverExtension2
{
	private String fDocs;
	private String fHeader;

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
			AbstractThemeableEditor editorPart = getEditor(textViewer);

			Collection<PropertyElement> properties = JSModelUtil.getProperties(editorPart, activeNode);
			if (!CollectionsUtil.isEmpty(properties))
			{
				Index index = getIndex(editorPart);
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
	public String getHeader(Object element, IEditorPart editorPart, IRegion hoverRegion)
	{
		return fHeader;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.hover.AbstractDocumentationHover#getDocumentation(java.lang.Object,
	 * org.eclipse.ui.IEditorPart, org.eclipse.jface.text.IRegion)
	 */
	@Override
	public String getDocumentation(Object element, IEditorPart editorPart, IRegion hoverRegion)
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
		final OpenDeclarationAction openDeclarationAction = getDeclarationAction(iControl);
		final OpenHelpAction openHelpAction = getHelpAction(iControl);
		tbm.add(openDeclarationAction);
		tbm.add(openHelpAction);
		IInputChangedListener inputChangeListener = new IInputChangedListener()
		{
			public void inputChanged(Object newInput)
			{
				if (newInput instanceof BrowserInformationControlInput)
				{
					openDeclarationAction.update();
					openHelpAction.update();
				}
			}
		};
		iControl.addInputChangeListener(inputChangeListener);
	}

	protected OpenHelpAction getHelpAction(CustomBrowserInformationControl iControl)
	{
		return new OpenHelpAction(iControl);
	}

	protected OpenDeclarationAction getDeclarationAction(CustomBrowserInformationControl iControl)
	{
		return new OpenDeclarationAction(iControl);
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

	public class OpenHelpAction extends Action
	{
		private static final String IMG_OPEN_HELP = "icons/full/elcl16/open_browser.gif"; //$NON-NLS-1$
		private static final String IMG_OPEN_HELP_DISABLED = "icons/full/dlcl16/open_browser.gif"; //$NON-NLS-1$
		private CustomBrowserInformationControl iControl;
		private Collection<PropertyElement> properties;

		public OpenHelpAction(CustomBrowserInformationControl iControl)
		{
			setText(Messages.JSTextHover_openDocsTooltip);
			setImageDescriptor(UIEplPlugin.imageDescriptorFromPlugin(UIEplPlugin.PLUGIN_ID, IMG_OPEN_HELP));
			setDisabledImageDescriptor(UIEplPlugin.imageDescriptorFromPlugin(UIEplPlugin.PLUGIN_ID,
					IMG_OPEN_HELP_DISABLED));
			this.iControl = iControl;
		}

		/**
		 * Update the action
		 */
		void update()
		{
			properties = null;
			BrowserInformationControlInput input = iControl.getInput();
			if (input instanceof DocumentationBrowserInformationControlInput)
			{
				Object inputElement = input.getInputElement();
				if (inputElement instanceof IParseNode)
				{
					properties = JSModelUtil.getProperties((AbstractThemeableEditor) getEditor(),
							(IParseNode) inputElement);
				}
			}
			setEnabled(!CollectionsUtil.isEmpty(properties));
		}

		protected Collection<PropertyElement> getProperties()
		{
			return properties;
		}

		public void run()
		{
			iControl.dispose();
			// Resolve the help path. We already know that the properties are not empty, so we just grab the
			// first one.
			PropertyElement element = properties.iterator().next();
			String owningType = element.getOwningType();
			String name = element.getName();
			List<ReturnTypeElement> types = element.getTypes();
			String resolvedReturnType = getResolvedType(types);
			String url;
			if (ObjectUtil.areEqual(resolvedReturnType, name) || ObjectUtil.areEqual(name, owningType))
			{
				url = MessageFormat.format("{0}{1}.html", BASE_HELP_DOCS_URL, name); //$NON-NLS-1$
			}
			else if (!StringUtil.isEmpty(resolvedReturnType))
			{
				url = MessageFormat.format("{0}{1}.html?visibility=basic#{1}.{2}", //$NON-NLS-1$
						BASE_HELP_DOCS_URL, resolvedReturnType, name);
			}
			else
			{
				url = MessageFormat.format("{0}{1}.html?visibility=basic#{1}.{2}", //$NON-NLS-1$
						BASE_HELP_DOCS_URL, owningType, name);
			}
			UIUtils.openHelpInBrowser(url);
		}

		protected String getResolvedType(List<ReturnTypeElement> types)
		{
			return (CollectionsUtil.isEmpty(types) ? null : JSModelFormatter.getTypeDisplayName(types.get(0).getType()));
		}
	}
}