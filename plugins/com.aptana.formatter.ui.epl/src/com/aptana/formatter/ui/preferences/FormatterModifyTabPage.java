/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package com.aptana.formatter.ui.preferences;

import java.net.URL;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;

import com.aptana.editor.common.util.EditorUtil;
import com.aptana.formatter.ui.FormatterMessages;
import com.aptana.formatter.ui.IFormatterControlManager;
import com.aptana.formatter.ui.IFormatterModifyDialog;
import com.aptana.ui.preferences.ScrolledPageContent;

public abstract class FormatterModifyTabPage implements IFormatterModifiyTabPage
{

	protected static final String SHOW_INVISIBLE_PREFERENCE_KEY = "invisible.characters"; //$NON-NLS-1$

	private final IFormatterModifyDialog dialog;
	private ISourceViewer previewViewer;
	/**
	 * A pixel converter for layout calculations
	 */
	protected PixelConverter fPixelConverter;

	/**
	 * @param dialog
	 */
	public FormatterModifyTabPage(IFormatterModifyDialog dialog)
	{
		this.dialog = dialog;
	}

	private Button fShowInvisibleButton;

	public Composite createContents(IFormatterControlManager manager, Composite parent)
	{

		final int numColumns = 4;

		if (fPixelConverter == null)
		{
			fPixelConverter = new PixelConverter(parent);
		}

		final SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
		sashForm.setFont(parent.getFont());

		Composite scrollContainer = new Composite(sashForm, SWT.NONE);

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		scrollContainer.setLayoutData(gridData);
		scrollContainer.setFont(sashForm.getFont());
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		scrollContainer.setLayout(layout);

		ScrolledPageContent scroll = new ScrolledPageContent(scrollContainer, SWT.V_SCROLL | SWT.H_SCROLL);
		scroll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		final Composite settingsContainer = scroll.getBody();

		settingsContainer.setLayout(new PageLayout(scroll, 400, 400));
		settingsContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Composite settingsPane = new Composite(settingsContainer, SWT.NONE);
		settingsPane.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		layout = new GridLayout(1, false);
		layout.verticalSpacing = (int) (1.5 * fPixelConverter
				.convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING));
		layout.horizontalSpacing = fPixelConverter.convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.marginHeight = fPixelConverter.convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = fPixelConverter.convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		settingsPane.setLayout(layout);
		createOptions(manager, settingsPane);

		settingsContainer.setSize(settingsContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		Label sashHandle = new Label(scrollContainer, SWT.SEPARATOR | SWT.VERTICAL);
		gridData = new GridData(SWT.RIGHT, SWT.FILL, false, true);
		sashHandle.setLayoutData(gridData);

		final Composite previewPane = new Composite(sashForm, SWT.NONE);
		previewPane.setLayout(createGridLayout(numColumns, true));
		previewPane.setFont(sashForm.getFont());
		doCreatePreviewPane(previewPane, numColumns);

		sashForm.setWeights(new int[] { 3, 3 });
		return sashForm;
	}

	private WhitespaceCharacterPainter whitespaceCharacterPainter = null;

	protected void updateShowInvisible(boolean value)
	{
		if (value)
		{
			if (whitespaceCharacterPainter == null)
			{
				whitespaceCharacterPainter = new WhitespaceCharacterPainter(previewViewer);
				((ITextViewerExtension2) previewViewer).addPainter(whitespaceCharacterPainter);
			}
		}
		else
		{
			if (whitespaceCharacterPainter != null)
			{
				((ITextViewerExtension2) previewViewer).removePainter(whitespaceCharacterPainter);
				whitespaceCharacterPainter = null;
			}
		}
	}

	protected Composite doCreatePreviewPane(Composite composite, int numColumns)
	{
		createLabel(numColumns - 1, composite, FormatterMessages.FormatterModifyTabPage_preview_label_text);

		fShowInvisibleButton = new Button(composite, SWT.CHECK);
		fShowInvisibleButton.setText(FormatterMessages.FormatterModifyTabPage_showInvisible);
		fShowInvisibleButton.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false));
		fShowInvisibleButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				final boolean newValue = fShowInvisibleButton.getSelection();
				updateShowInvisible(newValue);
				getDialogSettings().put(SHOW_INVISIBLE_PREFERENCE_KEY, newValue);
			}
		});
		previewViewer = dialog.getOwner().createPreview(composite);
		final boolean savedValue = getDialogSettings().getBoolean(SHOW_INVISIBLE_PREFERENCE_KEY);
		fShowInvisibleButton.setSelection(savedValue);
		updateShowInvisible(savedValue);

		if (previewViewer instanceof TextViewer)
		{
			GridData gd = createGridData(numColumns, GridData.FILL_BOTH, 0);
			gd.widthHint = 100;
			gd.heightHint = 100;
			((TextViewer) previewViewer).getControl().setLayoutData(gd);
		}

		return composite;
	}

	private IDialogSettings getDialogSettings()
	{
		return ((FormatterModifyDialog) dialog).fDialogSettings;
	}

	public void updatePreview()
	{
		if (previewViewer != null)
		{
			FormatterPreviewUtils.updatePreview(previewViewer, getPreviewContent(), getSubstitutionStrings(),
					dialog.getFormatterFactory(), dialog.getPreferences());
		}
	}

	protected abstract void createOptions(IFormatterControlManager manager, Composite parent);

	protected URL getPreviewContent()
	{
		return null;
	}

	/**
	 * Returns an array of substitution strings that will be used to substitute strings in the preview-content that were
	 * marked with {0}, {1}, etc.
	 * 
	 * @return A substitution strings array; Null, in case there is no substitution.
	 */
	protected String[] getSubstitutionStrings()
	{
		return null;
	}

	/**
	 * Layout used for the settings part. Makes sure to show scrollbars if necessary. The settings part needs to be
	 * layouted on resize.
	 */
	private static class PageLayout extends Layout
	{

		private final ScrolledComposite fContainer;
		private final int fMinimalWidth;
		private final int fMinimalHight;

		private PageLayout(ScrolledComposite container, int minimalWidth, int minimalHight)
		{
			fContainer = container;
			fMinimalWidth = minimalWidth;
			fMinimalHight = minimalHight;
		}

		public Point computeSize(Composite composite, int wHint, int hHint, boolean force)
		{
			if (wHint != SWT.DEFAULT && hHint != SWT.DEFAULT)
			{
				return new Point(wHint, hHint);
			}

			int x = fMinimalWidth;
			int y = fMinimalHight;
			Control[] children = composite.getChildren();
			for (int i = 0; i < children.length; i++)
			{
				Point size = children[i].computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
				x = Math.max(x, size.x);
				y = Math.max(y, size.y);
			}

			Rectangle area = fContainer.getClientArea();
			if (area.width > x)
			{
				fContainer.setExpandHorizontal(true);
			}
			else
			{
				fContainer.setExpandHorizontal(false);
			}

			if (area.height > y)
			{
				fContainer.setExpandVertical(true);
			}
			else
			{
				fContainer.setExpandVertical(false);
			}

			if (wHint != SWT.DEFAULT)
			{
				x = wHint;
			}
			if (hHint != SWT.DEFAULT)
			{
				y = hHint;
			}

			return new Point(x, y);
		}

		public void layout(Composite composite, boolean force)
		{
			Rectangle rect = composite.getClientArea();
			Control[] children = composite.getChildren();
			for (int i = 0; i < children.length; i++)
			{
				children[i].setSize(rect.width, rect.height);
			}
		}
	}

	/*
	 * Convenience method to create a label.
	 */
	protected static Label createLabel(int numColumns, Composite parent, String text)
	{
		return createLabel(numColumns, parent, text, GridData.FILL_HORIZONTAL);
	}

	/*
	 * Convenience method to create a label
	 */
	protected static Label createLabel(int numColumns, Composite parent, String text, int gridDataStyle)
	{
		final Label label = new Label(parent, SWT.WRAP);
		label.setFont(parent.getFont());
		label.setText(text);

		PixelConverter pixelConverter = new PixelConverter(parent);
		label.setLayoutData(createGridData(numColumns, gridDataStyle, pixelConverter.convertHorizontalDLUsToPixels(150)));
		return label;
	}

	/*
	 * Create a GridLayout with the default margin and spacing settings, as well as the specified number of columns.
	 */
	protected GridLayout createGridLayout(int numColumns, boolean margins)
	{
		final GridLayout layout = new GridLayout(numColumns, false);
		layout.verticalSpacing = fPixelConverter.convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = fPixelConverter.convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		if (margins)
		{
			layout.marginHeight = fPixelConverter.convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
			layout.marginWidth = fPixelConverter.convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		}
		else
		{
			layout.marginHeight = 0;
			layout.marginWidth = 0;
		}
		return layout;
	}

	/*
	 * Convenience method to create a GridData.
	 */
	protected static GridData createGridData(int numColumns, int style, int widthHint)
	{
		final GridData gd = new GridData(style);
		gd.horizontalSpan = numColumns;
		gd.widthHint = widthHint;
		return gd;
	}

	/**
	 * Update the tab-width value to match the editor's setting.<br>
	 * In case no specific editor's setting are found, the default workspace setting will be used.
	 * 
	 * @param qualifier
	 *            A qualifier string for the editor's preferences.
	 * @param textWidgets
	 *            The {@link Text}s to update with the read settings.
	 */
	protected void setEditorTabWidth(String qualifier, Text... textWidgets)
	{
		int tabWidth = EditorUtil.getSpaceIndentSize(qualifier);
		String tabValue = String.valueOf(tabWidth);
		for (Text t : textWidgets)
		{
			t.setText(tabValue);
		}
	}
}
