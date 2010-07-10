/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.ui.preferences.formatter;

import java.text.ParseException;
import java.util.Map;
import java.util.Vector;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * 
 *
 */
public abstract class Preview
{

	private final class JavaSourcePreviewerUpdater
	{

		final IPropertyChangeListener fontListener = new IPropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent event)
			{
				fSourceViewer.getTextWidget().setBackground(languageColorizer.getBackground());
				// if (event.getProperty().equals(PreferenceConstants.EDITOR_TEXT_FONT)) {
				// final Font font= JFaceResources.getFont(PreferenceConstants.EDITOR_TEXT_FONT);
				// fSourceViewer.getTextWidget().setFont(font);
				// if (fMarginPainter != null) {
				// fMarginPainter.initialize();
				// }
				// }
			}
		};

		final IPropertyChangeListener propertyListener = new IPropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent event)
			{
				fSourceViewer.getTextWidget().setBackground(languageColorizer.getBackground());
				updateWidget();
			}
		};

		/**
		 * 
		 */
		public JavaSourcePreviewerUpdater()
		{

			JFaceResources.getFontRegistry().addListener(fontListener);
			fPreferenceStore.addPropertyChangeListener(propertyListener);

			fSourceViewer.getTextWidget().addDisposeListener(new DisposeListener()
			{
				public void widgetDisposed(DisposeEvent e)
				{
					JFaceResources.getFontRegistry().removeListener(fontListener);
					fPreferenceStore.removePropertyChangeListener(propertyListener);
				}
			});
		}
	}

	/**
	 * 
	 */
	protected final Document fPreviewDocument;
	/**
	 * 
	 */
	protected IParseState parseState;
	/**
	 * 
	 */
	protected final IParser parser;
	/**
	 * 
	 */
	protected final ICodeFormatter formatter;
	/**
	 * 
	 */
	protected final IPreferenceStore fPreferenceStore;
	/**
	 * 
	 */
	protected TextViewer fSourceViewer;

	/**
	 * 
	 */
	// protected final MarginPainter fMarginPainter;
	protected Map fWorkingValues;

	private int fTabSize = 0;
	private PreviewWhitespacePainter fWhitespaceCharacterPainter;
	private UnifiedColorizer colorizer = UnifiedColorizer.getInstance();
	private LanguageColorizer languageColorizer;

	/**
	 * Create a new Java preview
	 * 
	 * @param workingValues
	 * @param parent
	 * @param language
	 * @param pstore
	 */
	public Preview(Map workingValues, Composite parent, String language, IPreferenceStore pstore)
	{
		parser = LanguageRegistry.createParser(language);
		formatter = LanguageRegistry.getCodeFormatter(language);
		parseState = parser.createParseState(null);
		fPreviewDocument = new Document();
		fWorkingValues = workingValues;
		languageColorizer=LanguageRegistry.getLanguageColorizer(language);
		IPreferenceStore prioritizedSettings = UnifiedEditorsPlugin.getDefault().getPreferenceStore();
		
		fPreferenceStore = prioritizedSettings;
		fSourceViewer = new TextViewer(parent, SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		fSourceViewer.setDocument(fPreviewDocument);
		fSourceViewer.getTextWidget().setBackground(languageColorizer.getBackground());
		fSourceViewer.getTextWidget().addLineStyleListener(new LineStyleListener()
		{

			public void lineGetStyle(LineStyleEvent e)
			{

				{
					if (parseState == null)
					{
						return;
					}

					LexemeList lexemeList = parseState.getLexemeList();

					if (lexemeList == null)
					{
						IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.UnifiedEditor_LexemeListIsNull);
						return;
					}

					int orgOffset = e.lineOffset;
					int offset = orgOffset;
					int extra = 0;
					int lineLength = e.lineText.length();
					int maxLineLength = lineLength;
					Lexeme[] lexemes = null;

					synchronized (lexemeList)
					{
						int startingIndex = lexemeList.getLexemeCeilingIndex(offset);
						int endingIndex = lexemeList.getLexemeFloorIndex(offset + maxLineLength);

						if (startingIndex == -1 && endingIndex != -1)
						{
							startingIndex = endingIndex;
						}

						if (endingIndex == -1 && startingIndex != -1)
						{
							endingIndex = startingIndex;
						}

						if (startingIndex != -1 && endingIndex != -1)
						{
							lexemes = lexemeList.cloneRange(startingIndex, endingIndex);
						}
					}

					if (lexemes != null)
					{

						Vector styles = new Vector();

						colorizer.createStyles(parseState, styles, lexemes, true);

						StyleRange[] styleResults = (StyleRange[]) styles.toArray(new StyleRange[] {});

						// move styles back to actual widget offsets in case of
						// folding
						if (extra > 0)
						{
							for (int i = 0; i < styleResults.length; i++)
							{
								StyleRange range = styleResults[i];
								range.start -= extra;
							}
						}

						e.styles = styleResults;
					}
				}
			};

		});
		// fSourceViewer.configure(fViewerConfiguration);
		fSourceViewer.getTextWidget().setFont(JFaceResources.getFont("org.eclipse.jface.textfont")); //$NON-NLS-1$

		new JavaSourcePreviewerUpdater();
		updateWidget();
	}

	/**
	 * @return Control
	 */
	public Control getControl()
	{
		return fSourceViewer.getControl();
	}

	/**
	 * 
	 */
	public void update()
	{
		if (fWorkingValues == null)
		{
			fPreviewDocument.set(""); //$NON-NLS-1$
			return;
		}

		// update the tab size
		final int tabSize = getPositiveIntValue((String) fWorkingValues
				.get(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE), 0);
		if (tabSize != fTabSize)
			fSourceViewer.getTextWidget().setTabs(tabSize);
		fTabSize = tabSize;

		final StyledText widget = (StyledText) fSourceViewer.getControl();
		final int height = widget.getClientArea().height;
		final int top0 = widget.getTopPixel();

		final int totalPixels0 = getHeightOfAllLines(widget);
		final int topPixelRange0 = totalPixels0 > height ? totalPixels0 - height : 0;

		widget.setRedraw(false);
		doFormatPreview();
		updateWidget();

		fSourceViewer.setSelection(null);

		final int totalPixels1 = getHeightOfAllLines(widget);
		final int topPixelRange1 = totalPixels1 > height ? totalPixels1 - height : 0;

		final int top1 = topPixelRange0 > 0 ? (int) (topPixelRange1 * top0 / (double) topPixelRange0) : 0;
		widget.setTopPixel(top1);
		widget.setRedraw(true);
		updateWidget();
	}

	/**
	 * 
	 */
	protected void updateWidget()
	{
		String string = fSourceViewer.getDocument().get();
		parseState = parser.createParseState(null);
		parseState.setEditState(string, null, 0, 0);
		try
		{
			parser.parse(parseState);
			LexemeList lexemeList = parseState.getLexemeList();
			for (int a = 0; a < lexemeList.size(); a++)
			{
				LanguageRegistry.getLanguageColorizer(lexemeList.get(a).getLanguage());
			}
		}
		catch (ParseException e1)
		{
			IdeLog.logError(Activator.getDefault(), e1.getMessage());
		}
		catch (LexerException e1)
		{
			IdeLog.logError(Activator.getDefault(), e1.getMessage());
		}
		fSourceViewer.getDocument().set(fSourceViewer.getDocument().get());
	}

	private int getHeightOfAllLines(StyledText styledText)
	{
		int height = 0;
		int lineCount = styledText.getLineCount();
		for (int i = 0; i < lineCount; i++)
			height = height + styledText.getLineHeight(styledText.getOffsetAtLine(i));
		return height;
	}

	/**
	 * 
	 */
	protected abstract void doFormatPreview();

	private static int getPositiveIntValue(String string, int defaultValue)
	{
		try
		{
			int i = Integer.parseInt(string);
			if (i >= 0)
			{
				return i;
			}
		}
		catch (NumberFormatException e)
		{
		}
		return defaultValue;
	}

	/**
	 * @return wValues
	 */
	public Map getWorkingValues()
	{
		return fWorkingValues;
	}

	/**
	 * @param workingValues
	 */
	public void setWorkingValues(Map workingValues)
	{
		fWorkingValues = workingValues;
	}

	/**
	 * @param enable
	 */
	public void showInvisibleCharacters(boolean enable)
	{
		if (enable)
		{
			if (fWhitespaceCharacterPainter == null)
			{
				// This white-space painter is used only for the 3.2 compatibility.
				// FIXME - SG: Once we stop support the 3.2 base, this should be replaced with org.eclipse.jface.text.WhitespaceCharacterPainter
				String spaceChar = String.valueOf((char) 183);
				String tabChar = String.valueOf((char) 187);
				fWhitespaceCharacterPainter = new PreviewWhitespacePainter(fSourceViewer, spaceChar, tabChar, 1000);
				fWhitespaceCharacterPainter
						.setColor(UnifiedColorManager.getInstance().getColor(new RGB(200, 200, 200)));
				fWhitespaceCharacterPainter.handleDrawRequest(null);
				fSourceViewer.addPainter(fWhitespaceCharacterPainter);
			}
		}
		else
		{
			if (fWhitespaceCharacterPainter != null)
			{
				fSourceViewer.removePainter(fWhitespaceCharacterPainter);
				fWhitespaceCharacterPainter.deactivate(true);
				fWhitespaceCharacterPainter.dispose();
			}
			fWhitespaceCharacterPainter = null;
		}
	}
}
