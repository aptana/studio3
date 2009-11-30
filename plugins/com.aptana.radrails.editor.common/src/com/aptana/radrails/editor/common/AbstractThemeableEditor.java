package com.aptana.radrails.editor.common;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Caret;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;

import com.aptana.editor.findbar.api.FindBarDecoratorFactory;
import com.aptana.editor.findbar.api.IFindBarDecorated;
import com.aptana.editor.findbar.api.IFindBarDecorator;
import com.aptana.radrails.editor.common.actions.ShowScopesAction;
import com.aptana.radrails.editor.common.theme.ThemeUtil;

/**
 * Provides a way to override the editor fg, bg and selection fg, bg from what is set in global text editor color prefs.
 * TODO Need a way to override the caret color!
 * 
 * @author cwilliams
 * @author schitale
 */
@SuppressWarnings("restriction")
public abstract class AbstractThemeableEditor extends AbstractDecoratedTextEditor
{
	// Adapter factory to adapt to IFindBarDecorated
	private static IAdapterFactory factory = new IAdapterFactory()
	{
		@SuppressWarnings("unchecked")
		public Class[] getAdapterList()
		{
			return new Class[] { AbstractThemeableEditor.class, IFindBarDecorated.class };
		}

		@SuppressWarnings("unchecked")
		public Object getAdapter(Object adaptableObject, Class adapterType)
		{
			if (adaptableObject instanceof AbstractThemeableEditor)
			{
				AbstractThemeableEditor abstractThemeableEditor = (AbstractThemeableEditor) adaptableObject;
				return abstractThemeableEditor.getAdapter(IFindBarDecorated.class);
			}
			return null;
		}
	};

	static
	{
		Platform.getAdapterManager().registerAdapters(factory, AbstractThemeableEditor.class);
	}
	private Image fCaretImage;
	private RGB fCaretColor;

	/**
	 * AbstractThemeableEditor
	 */
	public AbstractThemeableEditor()
	{
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class required)
	{
		if (IFindBarDecorated.class.equals(required))
		{
			return AbstractThemeableEditor.this.getFindBarDecorated();
		}
		return super.getAdapter(required);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent)
	{
		Composite findBarComposite = getFindBarDecorator().createFindBarComposite(parent);
		super.createPartControl(findBarComposite);
		getFindBarDecorator().createFindBar(getSourceViewer());
		overrideCaretColor();
	}

	protected void overrideCaretColor()
	{
		if (getSourceViewer().getTextWidget() == null)
			return;

		Caret caret = getSourceViewer().getTextWidget().getCaret();
		RGB caretColor = ThemeUtil.getActiveTheme().getCaret();
		if (caretColor == null)
			return;
		// This is an ugly hack. Setting a black image doesn't work for some reason, but setting no image will cause it
		// to be black.
		if (caretColor.equals(new RGB(0, 0, 0)))
		{
			caret.setImage(null);
			return;
		}

		// Shortcut for when color is same, don't do any heavy lifting
		if (this.fCaretImage != null && fCaretColor.equals(caretColor))
			return;

		PaletteData data = new PaletteData(new RGB[] { caretColor });
		int x = caret.getSize().x;
		int y = caret.getSize().y;
		// Apparently the current caret may have invalid sizings
		// that will cause errors when an attempt to
		// change the color is made. So perform the check and catch
		// errors and exceptions so caret coloring
		// doesn't affect opening the editor.
		if (x > 0 && y > 0)
		{
			try
			{
				ImageData iData = new ImageData(x, y, 1, data);
				caret.setImage(null);
				if (this.fCaretImage != null)
				{
					this.fCaretImage.dispose();
					this.fCaretImage = null;
				}
				this.fCaretImage = new Image(caret.getDisplay(), iData);
				caret.setImage(this.fCaretImage);
				fCaretColor = caretColor;
			}
			catch (Error e)
			{
			}
			catch (Exception e)
			{
			}
		}

	}

	@Override
	public void dispose()
	{
		if (fCaretImage != null)
		{
			fCaretImage.dispose();
			fCaretImage = null;
		}
		super.dispose();
	}

	@Override
	protected void initializeEditor()
	{
		setPreferenceStore(new ChainedPreferenceStore(new IPreferenceStore[] {
				CommonEditorPlugin.getDefault().getPreferenceStore(), EditorsPlugin.getDefault().getPreferenceStore() }));
	}

	@Override
	protected void initializeViewerColors(ISourceViewer viewer)
	{
		ThemeUtil.getActiveTheme();
		super.initializeViewerColors(viewer);
	}

	@Override
	protected void handlePreferenceStoreChanged(PropertyChangeEvent event)
	{
		super.handlePreferenceStoreChanged(event);
		if (event.getProperty().equals(ThemeUtil.ACTIVE_THEME) || event.getProperty().equals(ThemeUtil.THEME_CHANGED))
		{
			overrideCaretColor();
			getSourceViewer().invalidateTextPresentation();
		}
	}

	@Override
	protected void createActions()
	{
		super.createActions();
		setAction(ShowScopesAction.COMMAND_ID, ShowScopesAction.create(this, getSourceViewer()));
		getFindBarDecorator().installActions();
	}

	private IFindBarDecorated findBarDecorated;

	private IFindBarDecorated getFindBarDecorated()
	{
		if (findBarDecorated == null)
		{
			findBarDecorated = new IFindBarDecorated()
			{
				public IFindBarDecorator getFindBarDecorator()
				{
					return AbstractThemeableEditor.this.getFindBarDecorator();
				}
			};
		}
		return findBarDecorated;
	}

	private IFindBarDecorator findBarDecorator;

	private IFindBarDecorator getFindBarDecorator()
	{
		if (findBarDecorator == null)
		{
			findBarDecorator = FindBarDecoratorFactory.createFindBarDecorator(this, getStatusLineManager());
		}
		return findBarDecorator;
	}
}
