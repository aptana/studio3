package com.aptana.explorer;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.IWorkbenchAdapter2;
import org.eclipse.ui.navigator.CommonNavigator;

import com.aptana.editor.common.theme.ThemeUtil;
import com.aptana.explorer.internal.ui.SingleProjectView;

public class AdapterFactory implements IAdapterFactory
{

	protected static final String APP_EXPLORER_FONT_NAME = "com.aptana.explorer.font"; //$NON-NLS-1$

	static
	{
		JFaceResources.getFontRegistry().addListener(new IPropertyChangeListener()
		{

			@Override
			public void propertyChange(PropertyChangeEvent event)
			{
				if (!event.getProperty().equals(APP_EXPLORER_FONT_NAME))
					return;
				Display.getCurrent().asyncExec(new Runnable()
				{

					@Override
					public void run()
					{
						// OK, the app explorer font changed. We need to force a refresh of the app explorer tree!
						IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
						if (window == null)
							return;
						IWorkbenchPage page = window.getActivePage();
						if (page == null)
							return;
						IViewPart view = page.findView(SingleProjectView.ID);
						if (view == null)
							return;
						CommonNavigator nav = (CommonNavigator) view;
						nav.getCommonViewer().refresh();
					}
				});

			}
		});
	}

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
