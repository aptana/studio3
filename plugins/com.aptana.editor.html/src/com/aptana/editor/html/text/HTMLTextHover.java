/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.text;

import java.text.MessageFormat;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.internal.text.html.BrowserInformationControlInput;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.ui.IEditorPart;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.contentassist.CommonTextHover;
import com.aptana.editor.common.hover.CustomBrowserInformationControl;
import com.aptana.editor.common.hover.DocumentationBrowserInformationControlInput;
import com.aptana.editor.html.contentassist.HTMLIndexQueryHelper;
import com.aptana.editor.html.contentassist.HTMLModelFormatter;
import com.aptana.editor.html.contentassist.model.AttributeElement;
import com.aptana.editor.html.contentassist.model.BaseElement;
import com.aptana.editor.html.parsing.ast.HTMLElementNode;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseNodeAttribute;
import com.aptana.parsing.lexer.IRange;
import com.aptana.parsing.lexer.Range;
import com.aptana.ui.epl.UIEplPlugin;
import com.aptana.ui.util.UIUtils;

/**
 * @author cwilliams, sgibly
 */
@SuppressWarnings({ "restriction", "rawtypes", "nls" })
public class HTMLTextHover extends CommonTextHover implements ITextHover, ITextHoverExtension2
{

	/**
	 * A map for HTML element tags to their equivalent DOM reference help page.
	 */

	private static final Map<String, String> TAG_TO_DOC;
	static
	{
		// @formatter:off
		TAG_TO_DOC = CollectionsUtil.newMap(
			"tr", "HTMLTableRowElement",
			"td", "HTMLTableCellElement",
			"th", "HTMLTableCellElement",
			"a", "HTMLLinkElement",
			"h1", "HTMLHeaderElement",
			"h2", "HTMLHeaderElement",
			"h3", "HTMLHeaderElement",
			"h4", "HTMLHeadingElement",
			"h5", "HTMLHeaderElement",
			"h6", "HTMLHeaderElement",
			"applet", "Applet",
			"q", "HTMLQuoteElement",
			"caption", "HTMLTableCaptionElement",
			"column", "HTMLTableColElement",
			"col", "HTMLTableColElement",
			"dl", "HTMLDListElement",
			"dd", "HTMLDListElement",
			"dt", "HTMLDListElement",
			"img", "HTMLImageElement",
			"ol", "HTMLOListElement",
			"p", "HTMLParagraphElement",
			"tbody", "HTMLTableBodyElement",
			"ul", "HTMLUListElement");
		// @formatter:on
	}

	private static String activeNodeName;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextHoverExtension2#getHoverInfo2(org.eclipse.jface.text.ITextViewer,
	 * org.eclipse.jface.text.IRegion)
	 */
	public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion)
	{
		IParseNode activeNode = getActiveNode(textViewer, hoverRegion.getOffset());
		if (!(activeNode instanceof HTMLElementNode))
		{
			return null;
		}
		activeNodeName = (activeNode.getNameNode() != null) ? activeNode.getNameNode().getName() : StringUtil.EMPTY;
		BaseElement element = getMatchingElement(textViewer, hoverRegion, (HTMLElementNode) activeNode);

		// To avoid duplicating work, we generate the header and documentation together here
		// and then getHeader and getDocumentation just return the values.
		if (element != null)
		{
			return getHoverInfo(element, isBrowserControlAvailable(textViewer), null, getEditor(textViewer),
					hoverRegion);
		}

		return null;

	}

	protected BaseElement getMatchingElement(ITextViewer textViewer, IRegion hoverRegion, HTMLElementNode node)
	{
		// Hover over start tag?
		IRange elementNameRange = node.getNameNode().getNameRange();
		if (!elementNameRange.contains(hoverRegion.getOffset()))
		{
			return null;
		}

		// Check if we're hovering over the tag/element name
		try
		{
			IDocument doc = textViewer.getDocument();
			String openTagContent = doc.get(elementNameRange.getStartingOffset(), elementNameRange.getLength());
			int index = openTagContent.indexOf(node.getName());
			IRange tagNameRange = new Range(elementNameRange.getStartingOffset() + index,
					elementNameRange.getStartingOffset() + index + node.getName().length());

			if (tagNameRange.contains(hoverRegion.getOffset()))
			{
				return new HTMLIndexQueryHelper().getElement(node.getElementName().toLowerCase());
			}
		}
		catch (BadLocationException e)
		{
			// ignore
		}

		// Are we hovering over an attribute?
		IParseNodeAttribute attr = node.getAttributeAtOffset(hoverRegion.getOffset());
		if (attr == null)
		{
			return null;
		}

		// Are we over the attribute name?
		IRange nameRange = attr.getNameRange();
		if (nameRange != null && nameRange.contains(hoverRegion.getOffset()))
		{
			return new HTMLIndexQueryHelper().getAttribute(node.getElementName().toLowerCase(), attr.getName());
		}

		// We must be hovering over empty space, or attribute value, show no hover
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.hover.AbstractDocumentationHover#getHeader(java.lang.Object,
	 * org.eclipse.ui.IEditorPart, org.eclipse.jface.text.IRegion)
	 */
	@Override
	public String getHeader(Object element, IEditorPart editorPart, IRegion hoverRegion)
	{
		if (!(element instanceof BaseElement))
		{
			return null;
		}
		return HTMLModelFormatter.TEXT_HOVER.getHeader((BaseElement) element);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.hover.AbstractDocumentationHover#getDocumentation(java.lang.Object,
	 * org.eclipse.ui.IEditorPart, org.eclipse.jface.text.IRegion)
	 */
	@Override
	public String getDocumentation(Object element, IEditorPart editorPart, IRegion hoverRegion)
	{
		if (!(element instanceof BaseElement))
		{
			return null;
		}
		return HTMLModelFormatter.TEXT_HOVER.getDocumentation((BaseElement) element);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextHover#getHoverRegion(org.eclipse.jface.text.ITextViewer, int)
	 */
	public IRegion getHoverRegion(ITextViewer textViewer, int offset)
	{
		IParseNode activeNode = getActiveNode(textViewer, offset);
		if (!(activeNode instanceof HTMLElementNode))
		{
			return null;
		}

		// Are we over a start tag?
		HTMLElementNode node = (HTMLElementNode) activeNode;
		IRange elementNameRange = node.getNameNode().getNameRange();
		if (!elementNameRange.contains(offset))
		{
			return null;
		}

		// Check if we're hovering over the tag/element name
		try
		{
			IDocument doc = textViewer.getDocument();
			String openTagContent = doc.get(elementNameRange.getStartingOffset(), elementNameRange.getLength());
			int start = elementNameRange.getStartingOffset() + openTagContent.indexOf(node.getName());
			IRange tagNameRange = new Range(start, start + node.getName().length() - 1);

			if (tagNameRange.contains(offset))
			{
				return new Region(tagNameRange.getStartingOffset(), tagNameRange.getLength());
			}
		}
		catch (BadLocationException e)
		{
			// ignore
		}

		// Are we hovering over an attribute?
		IParseNodeAttribute attr = node.getAttributeAtOffset(offset);
		if (attr == null)
		{
			return null;
		}

		// Are we over the name of the attribute?
		IRange nameRange = attr.getNameRange();
		if (nameRange != null && nameRange.contains(offset))
		{
			return new Region(nameRange.getStartingOffset(), nameRange.getLength());
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.hover.AbstractDocumentationHover#populateToolbarActions(org.eclipse.jface.action.
	 * ToolBarManager, com.aptana.editor.common.hover.CustomBrowserInformationControl)
	 */
	@Override
	public void populateToolbarActions(ToolBarManager tbm, CustomBrowserInformationControl iControl)
	{
		// @formatter:off
		/*
		final OpenDOMReferenceAction openReferenceAction = new OpenDOMReferenceAction(iControl);
		tbm.add(openReferenceAction);
		IInputChangedListener inputChangeListener = new IInputChangedListener()
		{

			public void inputChanged(Object newInput)
			{
				if (newInput instanceof BrowserInformationControlInput)
				{
					openReferenceAction.update();
				}
			}
		};
		iControl.addInputChangeListener(inputChangeListener);
		*/
		// @formatter:on
	}

	/**
	 * This action will try to resolve the node we hover, and will open the help-view for its DOM reference. The action
	 * works on the element itself, or on its attributes. When dealing will attributes, the DOM help page that will be
	 * opened is the element's page (with an anchor to the attribute name).
	 */
	public class OpenDOMReferenceAction extends Action
	{
		private static final String IMG_OPEN_HELP = "icons/full/elcl16/open_browser.gif"; //$NON-NLS-1$
		private static final String IMG_OPEN_HELP_DISABLED = "icons/full/dlcl16/open_browser.gif"; //$NON-NLS-1$
		private CustomBrowserInformationControl iControl;
		private BaseElement node;

		public OpenDOMReferenceAction(CustomBrowserInformationControl iControl)
		{
			setText(Messages.HTMLTextHover_openDomReferenceAction);
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
			node = null;
			BrowserInformationControlInput input = iControl.getInput();
			if (input instanceof DocumentationBrowserInformationControlInput)
			{
				Object inputElement = input.getInputElement();
				if (inputElement instanceof BaseElement)
				{
					node = (BaseElement) inputElement;
				}
			}
			setEnabled(node != null);
		}

		public void run()
		{
			iControl.dispose();
			// Compute the help docs URL (the node has to be valid at this point).
			String name = null;
			String attribute = null;
			if (node instanceof AttributeElement)
			{
				attribute = node.getName();
				name = activeNodeName;
			}
			else
			{
				name = node.getName().toLowerCase();
			}
			if (TAG_TO_DOC.containsKey(name))
			{
				name = TAG_TO_DOC.get(name);
			}
			else
			{
				name = MessageFormat.format("HTML{0}Element", name); //$NON-NLS-1$
			}
			String url;
			if (attribute != null)
			{
				// Open the element's DOM page with an anchor to the attribute
				url = MessageFormat.format("{0}{1}.html?visibility=basic#{1}.{2}", BASE_HELP_DOCS_URL, name, attribute); //$NON-NLS-1$
			}
			else
			{
				url = MessageFormat.format("{0}{1}.html", BASE_HELP_DOCS_URL, name); //$NON-NLS-1$
			}
			UIUtils.openHelp(url);
		}
	}
}
