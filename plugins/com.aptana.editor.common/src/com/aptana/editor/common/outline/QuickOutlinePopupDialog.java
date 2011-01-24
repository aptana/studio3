package com.aptana.editor.common.outline;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.theme.ThemePlugin;

public class QuickOutlinePopupDialog extends PopupDialog implements DisposeListener
{
	/**
	 * Content outline page.
	 */
	private ContentOutlinePage page;

	private AbstractThemeableEditor fEditor;

	/**
	 * QuickOutlinePopupDialog constructor.
	 * 
	 * @param parent
	 *            - parent.
	 * @param editor
	 *            - editor.
	 * @param infoText
	 *            - info text.
	 */
	public QuickOutlinePopupDialog(Shell parent, AbstractThemeableEditor editor, ContentOutlinePage page,
			String infoText)
	{
		super(parent, SWT.BORDER | SWT.RESIZE, true, false, false, true, true, null, infoText);
		this.page = page;
		this.fEditor = editor;
		// FIXME Auto-close when editor closes
	}

	@Override
	protected Color getBackground()
	{
		return ThemePlugin.getDefault().getColorManager()
				.getColor(ThemePlugin.getDefault().getThemeManager().getCurrentTheme().getBackground());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		if (page instanceof CommonQuickOutlinePage)
		{
			((CommonQuickOutlinePage) page).createControl(composite, false);

			ISourceViewer viewer = fEditor.getISourceViewer();
			int offset = viewer.getTextWidget().getCaretOffset();
			if (viewer instanceof ITextViewerExtension5)
			{
				ITextViewerExtension5 v5 = (ITextViewerExtension5) viewer;
				offset = v5.widgetOffset2ModelOffset(offset);
			}
			if (offset != -1)
			{
				((CommonQuickOutlinePage) page).revealPosition(offset);
			}
		}
		else
		{
			page.createControl(composite);
		}
		getShell().addDisposeListener(this);

		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = 320;
		gd.heightHint = 240;
		composite.setLayoutData(gd);

		return composite;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createTitleControl(Composite parent)
	{
		if (page instanceof CommonQuickOutlinePage)
		{
			return ((CommonQuickOutlinePage) page).createSearchArea(parent, true);
		}
		return super.createTitleControl(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void fillDialogMenu(IMenuManager dialogMenu)
	{
		super.fillDialogMenu(dialogMenu);
		if (page instanceof CommonQuickOutlinePage)
		{
			((CommonQuickOutlinePage) page).contributeToQuickOutlineMenu(dialogMenu);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control getFocusControl()
	{
		if (page instanceof CommonQuickOutlinePage)
		{
			return ((CommonQuickOutlinePage) page).getSearchBox();
		}
		return super.getFocusControl();
	}

	public void widgetDisposed(DisposeEvent e)
	{
		// TODO Auto-generated method stub
		close();
	}
}
