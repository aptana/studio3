package com.aptana.explorer;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.model.IWorkbenchAdapter2;

import com.aptana.editor.common.theme.ThemeUtil;

public class AdapterFactory implements IAdapterFactory
{

	protected static final String APP_EXPLORER_FONT_NAME = "com.aptana.explorer.font"; //$NON-NLS-1$

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType)
	{
		if (adapterType.equals(IWorkbenchAdapter2.class))
		{
			return new IWorkbenchAdapter2()
			{

				@Override
				public RGB getForeground(Object element)
				{
					return ThemeUtil.getActiveTheme().getForeground();
				}

				@Override
				public FontData getFont(Object element)
				{
					// FIXME We need to listen for changes to this font and force a redraw of the app explorer (and
					// maybe any other views of resources)!
					FontDescriptor fd = JFaceResources.getFontDescriptor(APP_EXPLORER_FONT_NAME);
					if (fd == null)
					{
						fd = JFaceResources.getTextFontDescriptor();
					}
					return fd.getFontData()[0];
				}

				@Override
				public RGB getBackground(Object element)
				{
					return null;
				}
			};
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class[] getAdapterList()
	{
		return new Class[] { IWorkbenchAdapter2.class };
	}

}
