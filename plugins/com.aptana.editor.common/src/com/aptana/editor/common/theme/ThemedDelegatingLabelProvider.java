package com.aptana.editor.common.theme;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.CommonEditorPlugin;

/**
 * This label provider wraps another label provider and extends it to use them active theme's colors and font. This is
 * basically used to wrap label providers for the outline view.
 * 
 * @author cwilliams
 */
public class ThemedDelegatingLabelProvider implements ILabelProvider, IColorProvider, IFontProvider
{

	private ILabelProvider wrapped;

	public ThemedDelegatingLabelProvider(ILabelProvider wrapped)
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
		Font font = JFaceResources.getFont(IThemeManager.VIEW_FONT_NAME);
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
