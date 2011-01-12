/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.debug.internal.ui.hovers;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.aptana.js.debug.core.IJSDebugConstants;
import com.aptana.js.debug.core.model.IJSStackFrame;

/**
 * @author Max Stepanov
 */
public class JSDebugHover implements ITextHover, ITextHoverExtension
{
	private IDebugModelPresentation modelPresentation;

	/**
	 * @see org.eclipse.jface.text.ITextHover#getHoverInfo(org.eclipse.jface.text.ITextViewer,
	 *      org.eclipse.jface.text.IRegion)
	 */
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion)
	{
		IJSStackFrame frame = getFrame();
		if (frame != null)
		{
			// first check for 'this' - code resolve does not resolve java elements for 'this'
			IDocument document = textViewer.getDocument();
			if (document != null)
			{
				try
				{
					String variableName = document.get(hoverRegion.getOffset(), hoverRegion.getLength());
					try
					{
						IVariable variable = frame.findVariable(variableName);
						if (variable != null)
						{
							return getVariableText(variable);
						}
					}
					catch (DebugException e)
					{
						return null;
					}
				}
				catch (BadLocationException e)
				{
					return null;
				}
			}
		}
		return null;
	}

	/**
	 * @see org.eclipse.jface.text.ITextHover#getHoverRegion(org.eclipse.jface.text.ITextViewer, int)
	 */
	public IRegion getHoverRegion(ITextViewer textViewer, int offset)
	{
		return null; /* JavaWordFinder.findWord(textViewer.getDocument(), offset); */
	}

	/**
	 * @see org.eclipse.jface.text.ITextHoverExtension#getHoverControlCreator()
	 */
	public IInformationControlCreator getHoverControlCreator()
	{
		return new IInformationControlCreator()
		{
			public IInformationControl createInformationControl(Shell parent)
			{
				return new DefaultInformationControl(parent, SWT.NONE,
				/* new HTMLTextPresenter(true) */null, Messages.JSDebugHover_PressF2ForFocus);
			}
		};
	}

	/**
	 * getFrame
	 * 
	 * @return IJSStackFrame
	 */
	protected IJSStackFrame getFrame()
	{
		IAdaptable adaptable = DebugUITools.getDebugContext();
		if (adaptable != null)
		{
			return (IJSStackFrame) adaptable.getAdapter(IJSStackFrame.class);
		}
		return null;
	}

	/**
	 * Returns HTML text for the given variable
	 */
	private String getVariableText(IVariable variable)
	{
		StringBuffer buffer = new StringBuffer();
		IDebugModelPresentation modelPresentation = getModelPresentation();
		buffer.append("<p><pre>"); //$NON-NLS-1$
		String variableText = modelPresentation.getText(variable);
		buffer.append(replaceHTMLChars(variableText));
		buffer.append("</pre></p>"); //$NON-NLS-1$
		if (buffer.length() > 0)
		{
			return buffer.toString();
		}
		return null;
	}

	/**
	 * Replaces reserved HTML characters in the given string with their escaped equivalents. This is to ensure that
	 * variable values containing reserved characters are correctly displayed.
	 */
	private static String replaceHTMLChars(String variableText)
	{
		StringBuffer buffer = new StringBuffer(variableText.length());
		char[] characters = variableText.toCharArray();
		for (int i = 0; i < characters.length; i++)
		{
			char character = characters[i];
			switch (character)
			{
				case '<':
					buffer.append("&lt;"); //$NON-NLS-1$
					break;
				case '>':
					buffer.append("&gt;"); //$NON-NLS-1$
					break;
				case '&':
					buffer.append("&amp;"); //$NON-NLS-1$
					break;
				case '"':
					buffer.append("&quot;"); //$NON-NLS-1$
					break;
				default:
					buffer.append(character);
			}
		}
		return buffer.toString();
	}

	private IDebugModelPresentation getModelPresentation()
	{
		if (modelPresentation == null)
		{
			modelPresentation = DebugUITools.newDebugModelPresentation(IJSDebugConstants.ID_DEBUG_MODEL);
			modelPresentation.setAttribute(IDebugModelPresentation.DISPLAY_VARIABLE_TYPE_NAMES, Boolean.TRUE);
		}
		return modelPresentation;
	}
}
