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
import org.eclipse.jface.text.IDocument;
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

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.contentassist.CommonTextHover;
import com.aptana.editor.common.parsing.FileService;
import com.aptana.editor.css.CSSColors;
import com.aptana.editor.css.ICSSConstants;
import com.aptana.editor.css.contentassist.CSSIndexQueryHelper;
import com.aptana.editor.css.contentassist.model.ElementElement;
import com.aptana.editor.css.contentassist.model.PropertyElement;
import com.aptana.editor.css.parsing.ast.CSSDeclarationNode;
import com.aptana.editor.css.parsing.ast.CSSFunctionNode;
import com.aptana.editor.css.parsing.ast.CSSNode;
import com.aptana.editor.css.parsing.ast.CSSNodeTypes;
import com.aptana.editor.css.parsing.ast.CSSSimpleSelectorNode;
import com.aptana.editor.css.parsing.ast.CSSTermListNode;
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.ast.IParseNode;

public class CSSTextHover extends CommonTextHover implements ITextHover, ITextHoverExtension, ITextHoverExtension2
{
	private class RegionInfo
	{
		public final IRegion region;
		public final Object info;

		public RegionInfo(IRegion region, Object info)
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
		boolean forceParse = true;

		if (textViewer instanceof IAdaptable)
		{
			IAdaptable adaptable = (IAdaptable) textViewer;
			AbstractThemeableEditor editor = (AbstractThemeableEditor) adaptable
					.getAdapter(AbstractThemeableEditor.class);

			if (editor != null)
			{
				FileService fs = editor.getFileService();

				if (fs != null)
				{
					fs.parse();

					// TODO: check for failed parse status and abort?
					ast = fs.getParseResult();

					// we use this flag to prevent re-parsing of the document if the ast turns out to be null. No sense
					// in parsing twice to get nothing
					forceParse = false;
				}
			}
		}

		// if we couldn't get the AST via an editor's file service then parse content directly
		if (forceParse)
		{
			try
			{
				IDocument document = textViewer.getDocument();

				ast = ParserPoolFactory.parse(ICSSConstants.CONTENT_TYPE_CSS, document.get());
			}
			catch (Exception e)
			{
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
					case CSSNodeTypes.TERM:
					{
						IParseNode parent = cssNode.getParent();

						if (parent instanceof CSSDeclarationNode)
						{
							String text = cssNode.getText();

							if (text != null)
							{
								if (text.startsWith("#")) //$NON-NLS-1$
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

					case CSSNodeTypes.DECLARATION:
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

					case CSSNodeTypes.FUNCTION:
					{
						RegionInfo ri = this.getFunctionRegionInfo((CSSFunctionNode) cssNode);

						if (ri != null)
						{
							result = ri.region;
							info = ri.info;
						}
						break;
					}

					case CSSNodeTypes.SIMPLE_SELECTOR:
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
