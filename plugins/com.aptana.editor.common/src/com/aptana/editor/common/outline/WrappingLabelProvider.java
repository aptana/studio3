package com.aptana.editor.common.outline;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.theme.IThemeManager;

public class WrappingLabelProvider implements ILabelProvider, IColorProvider, IFontProvider
{

	protected static final String APP_EXPLORER_FONT_NAME = "com.aptana.explorer.font"; //$NON-NLS-1$

	private ILabelProvider wrapped;

	WrappingLabelProvider(ILabelProvider wrapped)
	{
		this.wrapped = wrapped;
	}

	@Override
	public Image getImage(Object element)
	{
		return wrapped.getImage(element);
	}

	@Override
	public String getText(Object element)
	{
		return wrapped.getText(element);
	}

	@Override
	public Color getForeground(Object element)
	{
		return CommonEditorPlugin.getDefault().getColorManager().getColor(
				getThemeManager().getCurrentTheme().getForeground());
	}

	protected IThemeManager getThemeManager()
	{
		return CommonEditorPlugin.getDefault().getThemeManager();
	}

	@Override
	public Color getBackground(Object element)
	{
		return null;
	}

	@Override
	public Font getFont(Object element)
	{
		Font font = JFaceResources.getFont(APP_EXPLORER_FONT_NAME);
		if (font == null)
		{
			font = JFaceResources.getTextFont();
		}
		return font;
	}

	@Override
	public void addListener(ILabelProviderListener listener)
	{
		wrapped.addListener(listener);
	}

	@Override
	public void dispose()
	{
		wrapped.dispose();
	}

	@Override
	public boolean isLabelProperty(Object element, String property)
	{
		return wrapped.isLabelProperty(element, property);
	}

	@Override
	public void removeListener(ILabelProviderListener listener)
	{
		wrapped.removeListener(listener);
	}

}
