/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
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
import com.aptana.editor.common.contentassist.CommonTextHover;
import com.aptana.editor.common.hover.CustomBrowserInformationControl;
import com.aptana.editor.css.CSSColors;
import com.aptana.editor.css.contentassist.CSSIndexQueryHelper;
import com.aptana.editor.css.contentassist.model.PropertyElement;
import com.aptana.editor.css.internal.text.CSSModelFormatter;
import com.aptana.editor.css.parsing.ast.CSSDeclarationNode;
import com.aptana.editor.css.parsing.ast.CSSFunctionNode;
import com.aptana.editor.css.parsing.ast.CSSNode;
import com.aptana.editor.css.parsing.ast.CSSTermListNode;
import com.aptana.editor.css.parsing.ast.ICSSNodeTypes;
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

	private Object info;

	private String fHeader;

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
		if (info instanceof String)
		{
			return (String) info;
		}
		else if (info instanceof RGB)
		{
			// Wrap the info color in a HTML table that is set with this background color.
			return MessageFormat.format(COLORED_TABLE, getHexColor((RGB) info));
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
		// assume no hover region
		IRegion result = null;
		info = null;
		fHeader = null;

		IParseNode node = getActiveNode(textViewer, offset);

		if (node instanceof CSSNode)
		{
			CSSNode cssNode = (CSSNode) node;

			switch (cssNode.getNodeType())
			{
				case ICSSNodeTypes.TERM:
				{
					IParseNode parent = cssNode.getParent();
					if (parent instanceof CSSTermListNode)
					{
						// find owning statement for this expression
						while (parent instanceof CSSTermListNode)
						{
							parent = parent.getParent();
						}
					}
					if (parent instanceof CSSDeclarationNode)
					{
						String text = cssNode.getText();

						if (!StringUtil.isEmpty(text))
						{
							if (text.charAt(0) == '#')
							{
								info = CSSColors.hexToRGB(text);
							}
							else if (CSSColors.namedColorExists(text))
							{
								info = CSSColors.namedColorToRGB(text);
							}
							else
							{
								info = text;
							}
							result = new Region(cssNode.getStartingOffset(), cssNode.getLength());
							break;
						}
					}
					else if (parent instanceof CSSFunctionNode)
					{
						RegionInfo ri = this.getFunctionRegionInfo((CSSFunctionNode) parent);

						if (ri != null)
						{
							result = ri.region;
							info = ri.info;
						}
					}
					break;
				}

				case ICSSNodeTypes.DECLARATION:
				{
					CSSDeclarationNode decl = (CSSDeclarationNode) cssNode;
					String propertyName = decl.getIdentifier();
					int startingOffset = decl.getStartingOffset();

					if (propertyName != null && startingOffset <= offset
							&& offset < startingOffset + propertyName.length())
					{
						CSSIndexQueryHelper queryHelper = new CSSIndexQueryHelper();
						PropertyElement property = queryHelper.getProperty(propertyName);

						if (property != null)
						{
							result = new Region(cssNode.getStartingOffset(), propertyName.length());

							fHeader = CSSModelFormatter.TEXT_HOVER.getHeader(property);
							info = CSSModelFormatter.TEXT_HOVER.getDocumentation(property);
						}
					}
					break;
				}

				case ICSSNodeTypes.FUNCTION:
				{
					RegionInfo ri = this.getFunctionRegionInfo((CSSFunctionNode) cssNode);

					if (ri != null)
					{
						result = ri.region;
						info = ri.info;
					}
					break;
				}
			}
		}

		if (result == null)
		{
			info = null;
		}
		return result;
	}
}
