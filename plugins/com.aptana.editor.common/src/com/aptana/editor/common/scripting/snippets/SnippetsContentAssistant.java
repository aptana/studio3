/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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

public class SnippetsContentAssistant extends ContentAssistant
{

	static final int MAX_HEIGHT = (Platform.OS_WIN32.equals(Platform.getOS()) ? 12 : 14);

	private IContentAssistProcessor contentAssistProcessor;

	private static class StringInformationPresenter implements IInformationPresenter
	{
		public String updatePresentation(Display display, String hoverInfo, TextPresentation presentation,
				int maxWidth, int maxHeight)
		{
			return hoverInfo;
		}
	}

	private static class DefaultInformationControlCreator extends AbstractReusableInformationControlCreator
	{
		public IInformationControl doCreateInformationControl(Shell shell)
		{
			DefaultInformationControl defaultInformationControl = new DefaultInformationControl(shell,
					new StringInformationPresenter())
			{
				private Font maxHeightTextFont;

				@Override
				protected void createContent(Composite parent)
				{
					super.createContent(parent);
					Control[] children = parent.getChildren();
					for (Control control : children)
					{
						if (control instanceof StyledText)
						{
							StyledText styledText = (StyledText) control;
							Font textFont = JFaceResources.getFont(JFaceResources.TEXT_FONT);
							FontData[] textFontData = textFont.getFontData();
							if (textFontData[0].getHeight() > MAX_HEIGHT)
							{
								if (maxHeightTextFont == null)
								{
									maxHeightTextFont = new Font(textFont.getDevice(), textFontData[0].getName(),
											MAX_HEIGHT, textFontData[0].getStyle());
								}
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

	public SnippetsContentAssistant()
	{
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
	public IContentAssistProcessor getContentAssistProcessor(String contentType)
	{
		if (contentAssistProcessor == null)
		{
			contentAssistProcessor = new SnippetsCompletionProcessor();
		}
		return contentAssistProcessor;
	}

	@Override
	protected void possibleCompletionsClosed()
	{
		super.possibleCompletionsClosed();
		if (contentAssistProcessor != null)
		{
			((SnippetsCompletionProcessor) contentAssistProcessor).possibleCompletionsClosed();
		}
	}
}
