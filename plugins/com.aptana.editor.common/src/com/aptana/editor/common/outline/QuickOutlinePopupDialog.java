package com.aptana.editor.common.outline;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.theme.ThemePlugin;

public class QuickOutlinePopupDialog extends PopupDialog
{
	/**
	 * Content outline Page.
	 */
	private CommonQuickOutlinePage fPage;

	/**
	 * The active editor that the quick outline is based upon.
	 */
	private AbstractThemeableEditor fEditor;

	/**
	 * QuickOutlinePopupDialog constructor.
	 * 
	 * @param parent
	 *            - A Shell used as the parent for this dialog.
	 * @param editor
	 *            - The active editor we're basing the outline on.
	 */
	public QuickOutlinePopupDialog(Shell parent, AbstractThemeableEditor editor)
	{
		super(parent, SWT.BORDER | SWT.RESIZE, true, false, false, true, true, null, null);
		this.fPage = new CommonQuickOutlinePage(editor);
		this.fEditor = editor;
		// FIXME Auto-close when editor closes?
	}

	/**
	 * Override to use theme background color.
	 */
	@Override
	protected Color getBackground()
	{
		return ThemePlugin.getDefault().getColorManager()
				.getColor(ThemePlugin.getDefault().getThemeManager().getCurrentTheme().getBackground());
	}

	/**
	 * Override to use theme foreground color.
	 */
	@Override
	protected Color getForeground()
	{
		return ThemePlugin.getDefault().getColorManager()
				.getColor(ThemePlugin.getDefault().getThemeManager().getCurrentTheme().getForeground());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().applyTo(composite);

		fPage.createControl(composite);

		// Try to select the current item we're on in editor in the outline
		ISourceViewer viewer = fEditor.getISourceViewer();
		int offset = viewer.getTextWidget().getCaretOffset();
		if (viewer instanceof ITextViewerExtension5)
		{
			ITextViewerExtension5 v5 = (ITextViewerExtension5) viewer;
			offset = v5.widgetOffset2ModelOffset(offset);
		}
		if (offset != -1)
		{
			fPage.revealPosition(offset);
		}
		GridDataFactory.fillDefaults().hint(320, 240).applyTo(composite);

		return composite;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createTitleControl(Composite parent)
	{
		return fPage.createSearchArea(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void fillDialogMenu(IMenuManager dialogMenu)
	{
		super.fillDialogMenu(dialogMenu);
		fPage.contributeToQuickOutlineMenu(dialogMenu);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control getFocusControl()
	{
		return fPage.getSearchBox();
	}
}
