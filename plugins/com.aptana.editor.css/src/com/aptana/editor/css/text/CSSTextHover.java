/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.text;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.IEditorPart;

import com.aptana.core.util.StringUtil;
import com.aptana.css.core.CSSColors;
import com.aptana.css.core.index.CSSIndexQueryHelper;
import com.aptana.css.core.model.BaseElement;
import com.aptana.css.core.model.ICSSMetadataElement;
import com.aptana.css.core.model.PropertyElement;
import com.aptana.css.core.model.PseudoClassElement;
import com.aptana.css.core.model.PseudoElementElement;
import com.aptana.css.core.parsing.ast.CSSAttributeSelectorNode;
import com.aptana.css.core.parsing.ast.CSSDeclarationNode;
import com.aptana.css.core.parsing.ast.CSSFunctionNode;
import com.aptana.css.core.parsing.ast.CSSNode;
import com.aptana.css.core.parsing.ast.CSSTermListNode;
import com.aptana.css.core.parsing.ast.ICSSNodeTypes;
import com.aptana.editor.common.contentassist.CommonTextHover;
import com.aptana.editor.common.hover.CustomBrowserInformationControl;
import com.aptana.editor.css.CSSColorsUI;
import com.aptana.editor.css.internal.text.CSSModelFormatter;
import com.aptana.parsing.ast.IParseNode;

public class CSSTextHover extends CommonTextHover implements ITextHover, ITextHoverExtension, ITextHoverExtension2
{
	// A table that displays the CSS color in its background
	private static final String COLORED_TABLE = "<table style=\"background-color:{0}; width:100%; height:100%;\"><tr><td> </td></tr></table>"; //$NON-NLS-1$

	private static class RegionInfo
	{
		public final IRegion region;
		public final Object info;

		RegionInfo(IRegion region, Object info)
		{
			this.region = region;
			this.info = info;
		}
	}

	private static final Pattern RGB_CHANNELS = Pattern
			.compile("rgb\\(\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\)"); //$NON-NLS-1$

	/**
	 * The object we're using as the source for the hover. For colors this is an {@link RGB} object. For properties,
	 * pseudo-classes and pseudo-elements this is an {@link ICSSMetadataElement}.
	 */
	private Object info;

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.hover.AbstractDocumentationHover#getHeader(java.lang.Object,
	 * org.eclipse.ui.IEditorPart, org.eclipse.jface.text.IRegion)
	 */
	@Override
	public String getHeader(Object element, IEditorPart editorPart, IRegion hoverRegion)
	{
		if (element instanceof RGB)
		{
			return Messages.CSSTextHover_cssColorHeaderText;
		}
		if (element instanceof ICSSMetadataElement)
		{
			return CSSModelFormatter.TEXT_HOVER.getHeader((ICSSMetadataElement) element);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.hover.AbstractDocumentationHover#getDocumentation(java.lang.Object,
	 * org.eclipse.ui.IEditorPart, org.eclipse.jface.text.IRegion)
	 */
	@Override
	public String getDocumentation(Object element, IEditorPart editorPart, IRegion hoverRegion)
	{
		if (element instanceof String)
		{
			return (String) element;
		}
		else if (element instanceof RGB)
		{
			// Wrap the info color in a HTML table that is set with this background color.
			return MessageFormat.format(COLORED_TABLE, getHexColor((RGB) element));
		}
		else if (element instanceof ICSSMetadataElement)
		{
			return CSSModelFormatter.TEXT_HOVER.getDocumentation((ICSSMetadataElement) element);
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
		// No toolbar actions for CSS (yet)

	}

	/**
	 * getFunctionRegionInfo
	 * 
	 * @param node
	 * @return
	 */
	private RegionInfo getFunctionRegionInfo(CSSFunctionNode node)
	{
		RegionInfo result = null;
		Matcher m = RGB_CHANNELS.matcher(node.toString());

		if (m.matches())
		{
			int red = Integer.parseInt(m.group(1));
			int green = Integer.parseInt(m.group(2));
			int blue = Integer.parseInt(m.group(3));

			// @formatter:off
			result = new RegionInfo(new Region(node.getStartingOffset(), node.getLength()), new RGB(red, green, blue));
			// @formatter:on
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
		return getHoverInfo(info, isBrowserControlAvailable(textViewer), null, getEditor(), hoverRegion);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextHover#getHoverRegion(org.eclipse.jface.text.ITextViewer, int)
	 */
	public IRegion getHoverRegion(ITextViewer textViewer, int offset)
	{
		info = null;

		IParseNode node = getActiveNode(textViewer, offset);
		if (!(node instanceof CSSNode))
		{
			return null;
		}

		// assume no hover region
		RegionInfo ri = null;

		CSSNode cssNode = (CSSNode) node;
		switch (cssNode.getNodeType())
		{
			case ICSSNodeTypes.TERM:
			{
				ri = getTermRegionInfo(cssNode);
				break;
			}

			case ICSSNodeTypes.DECLARATION:
			{
				ri = getPropertyNameRegionInfo((CSSDeclarationNode) cssNode, offset);
				break;
			}

			case ICSSNodeTypes.FUNCTION:
			{
				CSSFunctionNode functionNode = (CSSFunctionNode) cssNode;
				ri = getFunctionRegionInfo(functionNode);

				if (ri == null)
				{
					// This may be a pseudo-class that takes arguments!
					String functionName = functionNode.getName();
					BaseElement matching = getPsuedoClass(functionName);
					if (matching != null)
					{
						ri = new RegionInfo(new Region(cssNode.getStartingOffset(), functionName.length()), matching);
					}
				}
				break;
			}

			case ICSSNodeTypes.ATTRIBUTE_SELECTOR:
			{
				ri = getPseudoSelector((CSSAttributeSelectorNode) cssNode);
				break;
			}
		}

		if (ri != null)
		{
			info = ri.info;
			return ri.region;
		}

		return null;
	}

	private RegionInfo getPropertyNameRegionInfo(CSSDeclarationNode decl, int offset)
	{
		String propertyName = decl.getIdentifier();
		int startingOffset = decl.getStartingOffset();

		if (propertyName != null && startingOffset <= offset && offset < startingOffset + propertyName.length())
		{
			PropertyElement property = getQueryHelper().getProperty(propertyName);

			if (property != null)
			{
				return new RegionInfo(new Region(decl.getStartingOffset(), propertyName.length()), property);
			}
		}

		return null;
	}

	private RegionInfo getTermRegionInfo(CSSNode cssNode)
	{
		IParseNode parent = getOwningStatement(cssNode.getParent());
		if (parent instanceof CSSDeclarationNode)
		{
			return getColorRegionInfo(cssNode);
		}
		else if (parent instanceof CSSFunctionNode)
		{
			return getFunctionRegionInfo((CSSFunctionNode) parent);
		}

		return null;
	}

	// Check for psuedo-classes and -elements
	private RegionInfo getPseudoSelector(CSSAttributeSelectorNode selectorNode)
	{
		String rawAttribute = selectorNode.toString();
		if (StringUtil.isEmpty(rawAttribute) || rawAttribute.charAt(0) != ':')
		{
			return null;
		}

		String psuedoSomething = rawAttribute.substring(1);
		boolean isPsuedoElement = false;
		if (!StringUtil.isEmpty(psuedoSomething) && psuedoSomething.charAt(0) == ':')
		{
			psuedoSomething = psuedoSomething.substring(1);
			isPsuedoElement = true;
		}

		BaseElement matching = getPsuedoElement(psuedoSomething);
		if (matching == null && !isPsuedoElement)
		{
			matching = getPsuedoClass(psuedoSomething);
		}

		if (matching != null)
		{
			return new RegionInfo(new Region(selectorNode.getStartingOffset(), rawAttribute.length()), matching);
		}

		return null;
	}

	private RegionInfo getColorRegionInfo(CSSNode cssNode)
	{
		if (cssNode == null)
		{
			return null;
		}

		String text = cssNode.getText();
		if (StringUtil.isEmpty(text))
		{
			return null;
		}

		IRegion region = new Region(cssNode.getStartingOffset(), cssNode.getLength());
		if (text.charAt(0) == '#')
		{
			return new RegionInfo(region, CSSColorsUI.hexToRGB(text));
		}
		else if (CSSColors.namedColorExists(text))
		{
			return new RegionInfo(region, CSSColorsUI.namedColorToRGB(text));
		}
		return new RegionInfo(region, text);
	}

	// find owning statement for this expression
	private IParseNode getOwningStatement(IParseNode parent)
	{
		if (parent instanceof CSSTermListNode)
		{
			while (parent instanceof CSSTermListNode)
			{
				parent = parent.getParent();
			}
		}
		return parent;
	}

	protected BaseElement getPsuedoElement(String psuedoSomething)
	{
		for (PseudoElementElement pee : getQueryHelper().getPseudoElements())
		{
			if (pee.getName().equals(psuedoSomething))
			{
				return pee;
			}
		}
		return null;
	}

	protected BaseElement getPsuedoClass(String functionName)
	{
		for (PseudoClassElement pce : getQueryHelper().getPseudoClasses())
		{
			if (pce.getName().equals(functionName))
			{
				return pce;
			}
		}
		return null;
	}

	protected CSSIndexQueryHelper getQueryHelper()
	{
		return new CSSIndexQueryHelper();
	}
}
