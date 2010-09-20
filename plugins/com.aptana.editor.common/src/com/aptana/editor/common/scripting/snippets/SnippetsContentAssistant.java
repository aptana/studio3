/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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
package com.aptana.editor.common.scripting.snippets;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.DefaultInformationControl.IInformationPresenter;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.aptana.editor.common.CommonEditorPlugin;

public class SnippetsContentAssistant extends ContentAssistant {
	
	static final int MAX_HEIGHT = (Platform.OS_WIN32.equals(Platform.getOS()) ? 12 : 14);

	private IContentAssistProcessor contentAssistProcessor;
	
	private static class StringInformationPresenter implements IInformationPresenter {
		public String updatePresentation(Display display, String hoverInfo,
				TextPresentation presentation, int maxWidth, int maxHeight) {
			return hoverInfo;
		}
	}
	
	private static class DefaultInformationControlCreator extends AbstractReusableInformationControlCreator {
		public IInformationControl doCreateInformationControl(Shell shell) {
			DefaultInformationControl defaultInformationControl = new DefaultInformationControl(shell, new StringInformationPresenter()) {
				private Font maxHeightTextFont;

				@Override
				protected void createContent(Composite parent) {
					super.createContent(parent);
					Control[] children = parent.getChildren();
					for (Control control : children) {
						if (control instanceof StyledText)
						{
							StyledText styledText = (StyledText) control;
							Font textFont = JFaceResources.getFont(JFaceResources.TEXT_FONT);
							FontData[] textFontData = textFont.getFontData();
							if (textFontData[0].getHeight() > MAX_HEIGHT)
							{
								maxHeightTextFont = new Font(textFont.getDevice(),
										textFontData[0].getName(),
										MAX_HEIGHT,
										textFontData[0].getStyle());
								styledText.setFont(maxHeightTextFont);
							}
							else
							{
								styledText.setFont(textFont);
							}
						}
					}
				}

				@Override
				public void dispose()
				{
					if (maxHeightTextFont != null)
					{
						if (!maxHeightTextFont.isDisposed())
						{
							maxHeightTextFont.dispose();
						}
						maxHeightTextFont = null;
					}
					super.dispose();
				}
			};
			return defaultInformationControl;
		}
	}

	public SnippetsContentAssistant() {
		super();
		enableAutoActivation(false);
		enablePrefixCompletion(true);
		enableAutoInsert(true);
		enableColoredLabels(true);
		// Arrange to remember the size of completion popup
		setRestoreCompletionProposalSize(CommonEditorPlugin.getDefault().getDialogSettings());
		setStatusLineVisible(true);
		setStatusMessage(Messages.SnippetsContentAssistant_MSG_SelectNthSnippet);
		setInformationControlCreator(new DefaultInformationControlCreator());
	}
	
	@Override
	public IContentAssistProcessor getContentAssistProcessor(
			String contentType) {
		if (contentAssistProcessor == null) {
			contentAssistProcessor = new SnippetsCompletionProcessor();
		}
		return contentAssistProcessor;
	}

	@Override
	protected void possibleCompletionsClosed() {
		super.possibleCompletionsClosed();
		if (contentAssistProcessor instanceof SnippetsCompletionProcessor)
		{
			((SnippetsCompletionProcessor) contentAssistProcessor).possibleCompletionsClosed();
		}
	}
}
