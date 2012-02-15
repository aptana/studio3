/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.hover;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.internal.text.html.BrowserInformationControl;
import org.eclipse.jface.internal.text.html.HTMLPrinter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IInformationControlExtension4;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.editors.text.EditorsUI;
import org.osgi.framework.Bundle;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.IOUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.ui.epl.UIEplPlugin;

/**
 * A base class for documentation hovers which provides the UI infrastructure for the subclassing hovers.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
@SuppressWarnings({ "restriction", "nls" })
public abstract class AbstractDocumentationHover extends AbstractCommonTextHover
{
	private static final String DOCUMENTATION_STYLE_CSS = "/documentationStyle.css"; //$NON-NLS-1$
	private static String styleSheet;
	// Patterns used for stripping HTML in case there is no support for a CustomBrowserInformationControl on the system
	private static final Map<Pattern, String> htmlStrippingMap;
	static
	{
		htmlStrippingMap = new HashMap<Pattern, String>();
		htmlStrippingMap.put(Pattern.compile("<b>|</b>"), StringUtil.EMPTY);
		htmlStrippingMap.put(Pattern.compile("<p>|</p>"), "\n");
		htmlStrippingMap.put(Pattern.compile("<br>|</br>"), "\n");
		htmlStrippingMap.put(Pattern.compile("&lt"), "<");
		htmlStrippingMap.put(Pattern.compile("&gt"), ">");
	}

	/**
	 * The hover control creator.
	 */
	protected IInformationControlCreator fHoverControlCreator;
	/**
	 * The presentation control creator.
	 */
	protected IInformationControlCreator fPresenterControlCreator;

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.php.internal.ui.hover.AbstractPHPTextHover#getInformationPresenterControlCreator()
	 */
	public IInformationControlCreator getInformationPresenterControlCreator()
	{
		if (fPresenterControlCreator == null)
			fPresenterControlCreator = new PresenterControlCreator(this);
		return fPresenterControlCreator;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.php.internal.ui.hover.AbstractPHPTextHover#getHoverControlCreator()
	 */
	public IInformationControlCreator getHoverControlCreator()
	{
		if (fHoverControlCreator == null)
			fHoverControlCreator = new HoverControlCreator(getInformationPresenterControlCreator());
		return fHoverControlCreator;
	}

	/**
	 * Strip out, or replace with regular ascii characters, any 'b', 'br', 'lt' or 'gt' tags.
	 * 
	 * @param content
	 * @return A HTML-stripped content.
	 */
	public static String stripBasicHTML(String content)
	{
		for (Pattern strippingPattern : htmlStrippingMap.keySet())
		{
			content = strippingPattern.matcher(content).replaceAll(htmlStrippingMap.get(strippingPattern));
		}
		return content;
	}

	/**
	 * @see org.eclipse.jface.text.ITextHover#getHoverInfo(org.eclipse.jface.text.ITextViewer,
	 *      org.eclipse.jface.text.IRegion)
	 * @deprecated As of 3.4, replaced by {@link ITextHoverExtension2#getHoverInfo2(ITextViewer, IRegion)}
	 */
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion)
	{
		// This one is a deprecated, so we set up this default behavior, just in case someone calls it.
		Object info2 = getHoverInfo2(textViewer, hoverRegion);
		if (info2 == null)
		{
			return null;
		}
		if (info2 instanceof DocumentationBrowserInformationControlInput)
		{
			return ((DocumentationBrowserInformationControlInput) info2).getHtml();
		}
		return info2.toString();
	}

	/**
	 * Returns the header string that will be placed at the top of the hover area.
	 * 
	 * @param element
	 * @param editorPart
	 * @param hoverRegion
	 * @return A header string, or <code>null</code> to indicate that there is no header.
	 */
	protected abstract String getHeader(Object element, IEditorPart editorPart, IRegion hoverRegion);

	/**
	 * Returns the documentation string that will be placed inside the hover area.<br>
	 * The string should be formed in HTML. However, the HTML may get stripped out in case the current Eclipse does not
	 * have an active browser-support.
	 * 
	 * @param element
	 * @param editorPart
	 * @param hoverRegion
	 * @return A documentation string.
	 */
	protected abstract String getDocumentation(Object element, IEditorPart editorPart, IRegion hoverRegion);

	/**
	 * Returns the background color
	 * 
	 * @return A background color (may be <code>null</code>)
	 */
	protected Color getBackgroundColor()
	{
		return null;
	}

	/**
	 * Returns the foreground color in
	 * 
	 * @return A foreground color (may be <code>null</code>)
	 */
	protected Color getForegroundColor()
	{
		return null;
	}

	/**
	 * Attach actions in the tool-bar that will be displayed when the hover gets the focus.
	 * 
	 * @param tbm
	 */
	public abstract void populateToolbarActions(ToolBarManager tbm);

	/**
	 * Computes the hover info and returns a {@link DocumentationBrowserInformationControlInput} for it.
	 * 
	 * @param element
	 *            the resolved element
	 * @param useHTMLTags
	 * @param previousInput
	 *            the previous input, or <code>null</code>
	 * @param editorPart
	 *            (can be <code>null</code>)
	 * @param hoverRegion
	 * @return the HTML hover info for the given element(s) or <code>null</code> if no information is available
	 */
	@SuppressWarnings("unused")
	protected DocumentationBrowserInformationControlInput getHoverInfo(Object element, boolean useHTMLTags,
			DocumentationBrowserInformationControlInput previousInput, IEditorPart editorPart, IRegion hoverRegion)
	{
		if (element == null)
		{
			return null;
		}
		StringBuffer buffer = new StringBuffer();
		String base = null;

		int leadingImageWidth = 0;
		setHeader(getHeader(element, editorPart, hoverRegion), buffer, useHTMLTags);
		setDocumentation(getDocumentation(element, editorPart, hoverRegion), buffer, useHTMLTags);
		if (buffer.length() > 0)
		{
			if (useHTMLTags)
			{
				Color bgColor = getBackgroundColor();
				Color fgColor = getForegroundColor();
				RGB bgRGB = (bgColor != null) ? bgColor.getRGB() : null;
				RGB fgRGB = (fgColor != null) ? fgColor.getRGB() : null;
				HTMLPrinter.insertPageProlog(buffer, 0, fgRGB, bgRGB, getStyleSheet());
				if (base != null)
				{
					int endHeadIdx = buffer.indexOf("</head>"); //$NON-NLS-1$
					buffer.insert(endHeadIdx, "\n<base href='" + base + "'>\n"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				HTMLPrinter.addPageEpilog(buffer);
			}
			return new DocumentationBrowserInformationControlInput(previousInput, element, buffer.toString(),
					leadingImageWidth);
		}
		return null;
	}

	/**
	 * Perform a CustomBrowserInformationControl.isAvailable check in the UI thread, and return its result.
	 * 
	 * @param textViewer
	 *            An {@link ITextViewer}
	 * @return <code>true</code>, if a CustomBrowserInformationControl is available; <code>false</code>, otherwise.
	 */
	protected boolean isBrowserControlAvailable(final ITextViewer textViewer)
	{
		final boolean[] browserAvailable = new boolean[1];
		// Run in UI thread
		Display.getDefault().syncExec(new Runnable()
		{
			public void run()
			{

				try
				{
					browserAvailable[0] = CustomBrowserInformationControl.isAvailable(textViewer.getTextWidget()
							.getShell());
				}
				catch (Exception e)
				{
					IdeLog.logError(UIEplPlugin.getDefault(), e);
				}
			}
		});
		return browserAvailable[0];
	}

	/**
	 * Set the header string.
	 * 
	 * @param header
	 *            A string to set (may be <code>null</code>)
	 * @param buffer
	 * @param useHTMLTags
	 */
	private void setHeader(String header, StringBuffer buffer, boolean useHTMLTags)
	{
		if (StringUtil.isEmpty(header))
		{
			return;
		}
		if (useHTMLTags)
		{
			buffer.append("<div class=\"header\""); //$NON-NLS-1$
			HTMLPrinter.addSmallHeader(buffer, header);
			buffer.append("</div>"); //$NON-NLS-1$
		}
		else
		{
			// plain printing
			buffer.append('[');
			buffer.append(header);
			buffer.append("]\n"); //$NON-NLS-1$
		}

	}

	private void setDocumentation(String documentation, StringBuffer buffer, boolean useHTMLTags)
	{
		if (StringUtil.isEmpty(documentation))
		{
			return;
		}
		{
			if (!useHTMLTags)
			{
				documentation = stripBasicHTML(documentation);
			}
			buffer.append(documentation);
		}
	}

	/**
	 * Returns the PHP hover style sheet
	 */
	private String getStyleSheet()
	{
		if (styleSheet == null)
		{
			styleSheet = loadStyleSheet();
		}
		String css = styleSheet;
		if (css != null)
		{
			FontData fontData = JFaceResources.getFontRegistry().getFontData("Dialog")[0]; //$NON-NLS-1$
			css = HTMLPrinter.convertTopLevelFont(css, fontData);
		}

		return css;
	}

	/**
	 * Loads the hover style sheet.
	 */
	private static String loadStyleSheet()
	{
		Bundle bundle = Platform.getBundle(UIEplPlugin.PLUGIN_ID);
		URL styleSheetURL = bundle.getEntry(DOCUMENTATION_STYLE_CSS);
		if (styleSheetURL != null)
		{
			try
			{
				return IOUtil.read(styleSheetURL.openStream());
			}
			catch (IOException ex)
			{
				IdeLog.logError(UIEplPlugin.getDefault(), "Documentation hover - Error loading the style-sheet", ex); //$NON-NLS-1$
				return StringUtil.EMPTY;
			}
		}
		return null;
	}

	public static void addImageAndLabel(StringBuffer buf, String imageName, int imageWidth, int imageHeight,
			int imageLeft, int imageTop, String label, int labelLeft, int labelTop)
	{

		if (imageName != null)
		{
			StringBuffer imageStyle = new StringBuffer("position: absolute; ");
			imageStyle.append("width: ");
			imageStyle.append(imageWidth);
			imageStyle.append("px; ");
			imageStyle.append("height: ");
			imageStyle.append(imageHeight);
			imageStyle.append("px; ");
			imageStyle.append("top: ");
			imageStyle.append(imageTop);
			imageStyle.append("px; ");
			imageStyle.append("left: ");
			imageStyle.append(imageLeft);
			imageStyle.append("px; ");

			buf.append("<!--[if lte IE 6]><![if gte IE 5.5]>\n");
			buf.append("<span style=\"");
			buf.append(imageStyle);
			buf.append("filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='");
			buf.append(imageName);
			buf.append("')\"></span>\n");
			buf.append("<![endif]><![endif]-->\n");

			buf.append("<!--[if !IE]>-->\n");
			buf.append("<img style='");
			buf.append(imageStyle);
			buf.append("' src='");
			buf.append(imageName);
			buf.append("'/>\n");
			buf.append("<!--<![endif]-->\n");
			buf.append("<!--[if gte IE 7]>\n");
			buf.append("<img style='");
			buf.append(imageStyle);
			buf.append("' src='");
			buf.append(imageName);
			buf.append("'/>\n");
			buf.append("<![endif]-->\n");
		}

		buf.append("<div style='word-wrap:break-word;");
		if (imageName != null)
		{
			buf.append("margin-left: ").append(labelLeft).append("px; ");
			buf.append("margin-top: ").append(labelTop).append("px; ");
		}
		buf.append("'>");
		buf.append(label);
		buf.append("</div>");
	}

	/**
	 * Presenter control creator.
	 */
	public static class PresenterControlCreator extends AbstractReusableInformationControlCreator
	{

		protected AbstractDocumentationHover documentationHover;

		/**
		 * @param documentationHover
		 */
		public PresenterControlCreator(AbstractDocumentationHover documentationHover)
		{
			this.documentationHover = documentationHover;
		}

		/*
		 * @see
		 * org.eclipse.jdt.internal.ui.text.java.hover.AbstractReusableInformationControlCreator#doCreateInformationControl
		 * (org.eclipse.swt.widgets.Shell)
		 */
		public IInformationControl doCreateInformationControl(Shell parent)
		{
			if (BrowserInformationControl.isAvailable(parent))
			{
				ToolBarManager tbm = new ToolBarManager(SWT.FLAT);
				documentationHover.populateToolbarActions(tbm);
				CustomBrowserInformationControl iControl = new CustomBrowserInformationControl(parent, null, tbm);
				iControl.setBackgroundColor(documentationHover.getBackgroundColor());
				iControl.setForegroundColor(documentationHover.getForegroundColor());
				return iControl;
			}
			else
			{
				return new DefaultInformationControl(parent, true);
			}
		}
	}

	/**
	 * Hover control creator.
	 */
	private class HoverControlCreator extends AbstractReusableInformationControlCreator
	{
		/**
		 * The information presenter control creator.
		 */
		private final IInformationControlCreator informationPresenterControlCreator;
		/**
		 * <code>true</code> to use the additional info affordance, <code>false</code> to use the hover affordance.
		 */
		@SuppressWarnings("unused")
		private final boolean fAdditionalInfoAffordance;

		/**
		 * @param informationPresenterControlCreator
		 *            control creator for enriched hover
		 */
		public HoverControlCreator(IInformationControlCreator informationPresenterControlCreator)
		{
			this(informationPresenterControlCreator, false);
		}

		/**
		 * @param informationPresenterControlCreator
		 *            control creator for enriched hover
		 * @param additionalInfoAffordance
		 *            <code>true</code> to use the additional info affordance, <code>false</code> to use the hover
		 *            affordance
		 */
		public HoverControlCreator(IInformationControlCreator informationPresenterControlCreator,
				boolean additionalInfoAffordance)
		{
			this.informationPresenterControlCreator = informationPresenterControlCreator;
			fAdditionalInfoAffordance = additionalInfoAffordance;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * org.eclipse.jface.text.AbstractReusableInformationControlCreator#doCreateInformationControl(org.eclipse.swt
		 * .widgets.Shell)
		 */
		public IInformationControl doCreateInformationControl(Shell parent)
		{
			if (CustomBrowserInformationControl.isAvailable(parent))
			{
				CustomBrowserInformationControl iControl = new CustomBrowserInformationControl(parent, null,
						EditorsUI.getTooltipAffordanceString())
				{
					public IInformationControlCreator getInformationPresenterControlCreator()
					{
						return informationPresenterControlCreator;
					}
				};
				iControl.setBackgroundColor(getBackgroundColor());
				iControl.setForegroundColor(getForegroundColor());
				return iControl;
			}
			else
			{
				// return new ThemedInformationControl(parent, null, EditorsUI.getTooltipAffordanceString());
				return new DefaultInformationControl(parent, true);
			}
		}

		/*
		 * (non-Javadoc)
		 * @seeorg.eclipse.jface.text.AbstractReusableInformationControlCreator#canReuse(org.eclipse.jface.text.
		 * IInformationControl)
		 */
		public boolean canReuse(IInformationControl control)
		{
			if (!super.canReuse(control))
				return false;

			if (control instanceof IInformationControlExtension4)
			{
				((IInformationControlExtension4) control).setStatusText(EditorsUI.getTooltipAffordanceString());
			}

			return true;
		}
	}
}
