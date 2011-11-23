/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Shell;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.contentassist.CommonTextHover;
import com.aptana.editor.css.CSSColors;
import com.aptana.editor.css.contentassist.CSSIndexQueryHelper;
import com.aptana.editor.css.contentassist.model.ElementElement;
import com.aptana.editor.css.contentassist.model.PropertyElement;
import com.aptana.editor.css.parsing.ast.CSSDeclarationNode;
import com.aptana.editor.css.parsing.ast.CSSFunctionNode;
import com.aptana.editor.css.parsing.ast.CSSNode;
import com.aptana.editor.css.parsing.ast.CSSSimpleSelectorNode;
import com.aptana.editor.css.parsing.ast.CSSTermListNode;
import com.aptana.editor.css.parsing.ast.ICSSNodeTypes;
import com.aptana.parsing.ast.IParseNode;

public class CSSTextHover extends CommonTextHover implements ITextHover, ITextHoverExtension, ITextHoverExtension2
{
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

	/**
	 * getAST
	 * 
	 * @param textViewer
	 * @param offset
	 * @return
	 */
	protected IParseNode getAST(ITextViewer textViewer, int offset)
	{
		IParseNode ast = null;

		if (textViewer instanceof IAdaptable)
		{
			IAdaptable adaptable = (IAdaptable) textViewer;
			AbstractThemeableEditor editor = (AbstractThemeableEditor) adaptable
					.getAdapter(AbstractThemeableEditor.class);

			if (editor != null)
			{
				ast = editor.getAST();
			}
		}

		return ast;
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
			result = new RegionInfo(
				new Region(node.getStartingOffset(), node.getLength()),
				new RGB(red, green, blue)
			);
			// @formatter:on
		}

		return result;
	}

	/*
	 * @see org.eclipse.jface.text.ITextHoverExtension#getHoverControlCreator()
	 */
	public IInformationControlCreator getHoverControlCreator()
	{
		return new IInformationControlCreator()
		{
			public IInformationControl createInformationControl(Shell parent)
			{
				return new CSSTextHoverInformationControl(parent);
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextHover#getHoverInfo(org.eclipse.jface.text.ITextViewer,
	 * org.eclipse.jface.text.IRegion)
	 */
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion)
	{
		// Not called
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextHoverExtension2#getHoverInfo2(org.eclipse.jface.text.ITextViewer,
	 * org.eclipse.jface.text.IRegion)
	 */
	public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion)
	{
		return (this.isHoverEnabled()) ? info : null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextHover#getHoverRegion(org.eclipse.jface.text.ITextViewer, int)
	 */
	public IRegion getHoverRegion(ITextViewer textViewer, int offset)
	{
		// assume no hover region
		IRegion result = null;

		// grab document's parse model
		IParseNode ast = getAST(textViewer, offset);

		if (ast != null)
		{
			IParseNode node = ast.getNodeAtOffset(offset);

			if (node instanceof CSSNode)
			{
				CSSNode cssNode = (CSSNode) node;

				switch (cssNode.getNodeType())
				{
					case ICSSNodeTypes.TERM:
					{
						IParseNode parent = cssNode.getParent();

						if (parent instanceof CSSDeclarationNode)
						{
							String text = cssNode.getText();

							if (!StringUtil.isEmpty(text))
							{
								if (text.charAt(0) == '#')
								{
									result = new Region(cssNode.getStartingOffset(), cssNode.getLength());
									info = CSSColors.hexToRGB(text);
								}
								else if (CSSColors.namedColorExists(text))
								{
									result = new Region(cssNode.getStartingOffset(), cssNode.getLength());
									info = CSSColors.namedColorToRGB(text);
								}
								break;
							}
						}
						else if (parent instanceof CSSTermListNode)
						{
							// find owning statement for this expression
							while (parent instanceof CSSTermListNode)
							{
								parent = parent.getParent();
							}

							if (parent instanceof CSSFunctionNode)
							{
								RegionInfo ri = this.getFunctionRegionInfo((CSSFunctionNode) parent);

								if (ri != null)
								{
									result = ri.region;
									info = ri.info;
								}
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
								info = property.getDescription();
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

					case ICSSNodeTypes.SIMPLE_SELECTOR:
					{
						CSSSimpleSelectorNode simpleSelector = (CSSSimpleSelectorNode) cssNode;
						String elementName = simpleSelector.getTypeSelector();
						int startingOffset = simpleSelector.getStartingOffset();

						if (elementName != null && startingOffset <= offset
								&& offset < startingOffset + elementName.length())
						{
							CSSIndexQueryHelper queryHelper = new CSSIndexQueryHelper();
							ElementElement element = queryHelper.getElement(elementName);

							if (element != null)
							{
								result = new Region(cssNode.getStartingOffset(), elementName.length());
								info = element.getDescription();
							}
						}
						break;
					}
				}
			}
		}

		if (result == null)
		{
			info = null;
			result = new Region(offset, 0);
		}

		return result;
	}
}
