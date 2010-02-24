package com.aptana.editor.common.theme;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.aptana.editor.common.CommonEditorPlugin;

/**
 * A Utility class that makes "hijacking" a Tree and hooking it up to our themes easy.
 * 
 * @author cwilliams
 */
public class TreeThemer
{

	private static final boolean isWindows = Platform.getOS().equals(Platform.OS_WIN32);
	private static final boolean isMacOSX = Platform.getOS().equals(Platform.OS_MACOSX);
	private static final boolean isCocoa = Platform.getWS().equals(Platform.WS_COCOA);

	private TreeViewer treeViewer;
	private IPropertyChangeListener fontListener;
	private IPreferenceChangeListener fThemeChangeListener;

	public TreeThemer(TreeViewer treeViewer)
	{
		this.treeViewer = treeViewer;
	}

	public void apply()
	{
		// Set the background of tree to theme background.
		treeViewer.getTree().setBackground(
				CommonEditorPlugin.getDefault().getColorManager().getColor(
						getThemeManager().getCurrentTheme().getBackground()));
		addSelectionColorOverride();
		addMeasureItemListener();
		addFontListener();
		listenForThemeChanges();
		overrideLabelProvider();
	}

	private void overrideLabelProvider()
	{
		ViewerColumn viewer = (ViewerColumn) treeViewer.getTree().getData("org.eclipse.jface.columnViewer"); //$NON-NLS-1$
		ColumnViewer colViewer = viewer.getViewer();
		IBaseLabelProvider provider = colViewer.getLabelProvider();
		if (!(provider instanceof CellLabelProvider))
			return; // TODO Wrap in ThemedDelegatingLabelProvider?
		final CellLabelProvider cellProvider = (CellLabelProvider) provider;
		viewer.setLabelProvider(new CellLabelProvider()
		{

			@Override
			public void update(ViewerCell cell)
			{
				cellProvider.update(cell);
				Font font = JFaceResources.getFont(IThemeManager.VIEW_FONT_NAME);
				if (font == null)
				{
					font = JFaceResources.getTextFont();
				}
				if (font != null)
				{
					cell.setFont(font);
				}

				cell.setForeground(CommonEditorPlugin.getDefault().getColorManager().getColor(
						getThemeManager().getCurrentTheme().getForeground()));
			}
		});
	}

	protected void addSelectionColorOverride()
	{
		final Tree tree = treeViewer.getTree();
		// Override selection color to match what is set in theme
		tree.addListener(SWT.EraseItem, new Listener()
		{
			public void handleEvent(Event event)
			{
				if ((event.detail & SWT.SELECTED) != 0)
				{
					Tree tree = (Tree) event.widget;
					int clientWidth = tree.getClientArea().width;

					GC gc = event.gc;
					Color oldBackground = gc.getBackground();

					gc.setBackground(CommonEditorPlugin.getDefault().getColorManager().getColor(
							getThemeManager().getCurrentTheme().getSelection()));
					gc.fillRectangle(0, event.y, clientWidth, event.height);
					gc.setBackground(oldBackground);

					event.detail &= ~SWT.SELECTED;
					event.detail &= ~SWT.BACKGROUND;
				}
			}
		});
	}

	protected void addMeasureItemListener()
	{
		final Tree tree = treeViewer.getTree();
		// Hack to force a specific row height and width based on font
		tree.addListener(SWT.MeasureItem, new Listener()
		{
			public void handleEvent(Event event)
			{
				Font font = JFaceResources.getFont(IThemeManager.VIEW_FONT_NAME);
				if (font == null)
				{
					font = JFaceResources.getTextFont();
				}
				if (font != null)
				{
					event.gc.setFont(font);
					FontMetrics metrics = event.gc.getFontMetrics();
					int height = metrics.getHeight() + 2;
					TreeItem item = (TreeItem) event.item;
					int width = event.gc.stringExtent(item.getText()).x + 24;
					event.height = height;
					if (width > event.width)
						event.width = width;
					// HACK! This is a major hack to force down the height of the row when we resize our font to a
					// smaller height!
					if (isMacOSX && isCocoa)
					{
						try
						{
							Field f = Control.class.getField("view"); //$NON-NLS-1$
							if (f != null)
							{
								Object widget = f.get(tree);
								if (widget != null)
								{
									Method m = widget.getClass().getMethod("setRowHeight", Double.TYPE); //$NON-NLS-1$
									if (m != null)
										m.invoke(widget, height);
								}
							}
						}
						catch (Exception e)
						{
							CommonEditorPlugin.logError(e);
						}
					}
				}
			}
		});
	}

	protected void addFontListener()
	{
		final Tree tree = treeViewer.getTree();
		fontListener = new IPropertyChangeListener()
		{

			@Override
			public void propertyChange(PropertyChangeEvent event)
			{
				if (!event.getProperty().equals(IThemeManager.VIEW_FONT_NAME))
					return;
				Display.getCurrent().asyncExec(new Runnable()
				{

					@Override
					public void run()
					{
						// OK, the app explorer font changed. We need to force a refresh of the app explorer tree!
						treeViewer.refresh();
						if (isWindows)
						{
							try
							{
								Method m = Tree.class.getDeclaredMethod("setItemHeight", Integer.TYPE); //$NON-NLS-1$
								if (m != null)
								{
									m.setAccessible(true);
									Font font = JFaceResources.getFont(IThemeManager.VIEW_FONT_NAME);
									if (font == null)
									{
										font = JFaceResources.getTextFont();
									}
									if (font != null)
									{
										GC gc = new GC(Display.getDefault());
										gc.setFont(font);
										FontMetrics metrics = gc.getFontMetrics();
										int height = metrics.getHeight() + 2;
										m.invoke(tree, height);
										gc.dispose();
									}
								}
							}
							catch (Exception e)
							{
								CommonEditorPlugin.logError(e);
							}
						}
						tree.redraw();
						tree.update();
					}
				});

			}
		};
		JFaceResources.getFontRegistry().addListener(fontListener);
	}

	private void listenForThemeChanges()
	{
		fThemeChangeListener = new IPreferenceChangeListener()
		{

			@Override
			public void preferenceChange(PreferenceChangeEvent event)
			{
				if (event.getKey().equals(IThemeManager.THEME_CHANGED))
				{
					treeViewer.refresh();
					treeViewer.getTree().setBackground(
							CommonEditorPlugin.getDefault().getColorManager().getColor(
									getThemeManager().getCurrentTheme().getBackground()));
				}
			}
		};
		new InstanceScope().getNode(CommonEditorPlugin.PLUGIN_ID).addPreferenceChangeListener(fThemeChangeListener);
	}

	public void dispose()
	{
		removeFontListener();
		removeThemeListener();
	}

	protected void removeFontListener()
	{
		if (fontListener != null)
			JFaceResources.getFontRegistry().removeListener(fontListener);
		fontListener = null;
	}

	private void removeThemeListener()
	{
		if (fThemeChangeListener != null)
		{
			new InstanceScope().getNode(CommonEditorPlugin.PLUGIN_ID).removePreferenceChangeListener(
					fThemeChangeListener);
			fThemeChangeListener = null;
		}
	}

	protected static IThemeManager getThemeManager()
	{
		return CommonEditorPlugin.getDefault().getThemeManager();
	}
}
