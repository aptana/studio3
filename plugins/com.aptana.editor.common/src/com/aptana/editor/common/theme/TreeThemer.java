package com.aptana.editor.common.theme;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

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

	private TreeViewer fTreeViewer;
	private Tree fTree;
	private IPropertyChangeListener fontListener;
	private IPreferenceChangeListener fThemeChangeListener;
	private Listener measureItemListener;
	private Listener selectionOverride;

	public TreeThemer(TreeViewer treeViewer)
	{
		this.fTreeViewer = treeViewer;
	}

	public TreeThemer(Tree tree)
	{
		this.fTree = tree;
	}

	public void apply()
	{
		// Set the background of tree to theme background.
		getTree().setBackground(getBackground());
		getTree().setForeground(getForeground());
		addSelectionColorOverride();
		addMeasureItemListener();
		addFontListener();
		addThemeChangeListener();
		overrideLabelProvider();
	}

	private void overrideLabelProvider()
	{
		ViewerColumn viewer = (ViewerColumn) getTree().getData("org.eclipse.jface.columnViewer"); //$NON-NLS-1$
		if (viewer == null)
			return;
		ColumnViewer colViewer = viewer.getViewer();
		if (colViewer == null)
			return;
		IBaseLabelProvider provider = colViewer.getLabelProvider();
		if (provider instanceof CellLabelProvider)
		{
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

					cell.setForeground(getForeground());
				}
			});
		}
		else if (provider instanceof ILabelProvider)
		{
			colViewer.setLabelProvider(new ThemedDelegatingLabelProvider((ILabelProvider) provider));
		}
	}

	private void addSelectionColorOverride()
	{
		final Tree tree = getTree();
		// Override selection color to match what is set in theme
		selectionOverride = new Listener()
		{
			public void handleEvent(Event event)
			{
				if ((event.detail & SWT.SELECTED) != 0)
				{
					Tree tree = (Tree) event.widget;
					int clientWidth = tree.getClientArea().width;

					GC gc = event.gc;
					Color oldBackground = gc.getBackground();

					gc.setBackground(getSelection());
					gc.fillRectangle(0, event.y, clientWidth, event.height);
					gc.setBackground(oldBackground);

					event.detail &= ~SWT.SELECTED;
					event.detail &= ~SWT.BACKGROUND;
				}
			}
		};
		tree.addListener(SWT.EraseItem, selectionOverride);
		// Hack to overdraw the native tree expand/collapse controls and use custom plus/minus box.
		if (isWindows)
		{
			tree.addListener(SWT.PaintItem, new Listener() 
			{
				
				@Override
				public void handleEvent(Event event) 
				{
					GC gc = event.gc;
					Widget item = event.item;
					boolean isExpanded = false;
					boolean draw = false;
					if (item instanceof TreeItem)
					{
						TreeItem tItem = (TreeItem) item;
						isExpanded = tItem.getExpanded();
						draw = tItem.getItemCount() > 0;
					}
					if (!draw)
						return;
					final int width = 10;
					final int height = 12;
					final int x = event.x - 16;
					final int y = event.y + 4;
					Color oldBackground = gc.getBackground();
					gc.setBackground(getBackground());
					// wipe out the native control
					gc.fillRectangle(x, y, width, height);
					// draw a plus/minus based on expansion!
					gc.setBackground(getForeground());
					// draw surrounding box
					gc.drawRectangle(x, y, width, width);
					// draw '-'
					gc.drawLine(x + 3, y + (width / 2), x + 7, y + (width / 2));
					if (!isExpanded)
					{
						// draw '|' to make it a plus
						gc.drawLine(x + (width / 2), y + 3, x + (width / 2), y + 7);
					}
					gc.setBackground(oldBackground);
					
					event.detail &= ~SWT.BACKGROUND;
				}
			});
		}
	}

	private void addMeasureItemListener()
	{
		final Tree tree = getTree();
		// Hack to force a specific row height and width based on font
		measureItemListener = new Listener()
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
					int width = event.gc.stringExtent(item.getText()).x + 24; // minimum width we need for text plus eye
					// FIX For RR3 #200, Tree items aren't expanding to full width of view, breaking our hover code
					if (isWindows)
					{
						int clientWidth = item.getParent().getClientArea().width; // width of view area
						Rectangle bounds = item.getBounds(); // bounds of the actual item
						clientWidth -= bounds.x; // subtract where this item starts on left from width of client area
						clientWidth += 19; // width of tree control arrows
						width = Math.max(width, clientWidth);
					}
					event.height = height;
					if (width > event.width)
						event.width = width;
				}
			}
		};
		tree.addListener(SWT.MeasureItem, measureItemListener);
	}

	private void addFontListener()
	{
		final Tree tree = getTree();
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
						Font font = JFaceResources.getFont(IThemeManager.VIEW_FONT_NAME);
						if (font == null)
						{
							font = JFaceResources.getTextFont();
						}
						if (font != null)
						{
							tree.setFont(font);

							GC gc = new GC(Display.getDefault());
							gc.setFont(font);
							FontMetrics metrics = gc.getFontMetrics();
							int height = metrics.getHeight() + 2;
							gc.dispose();

							if (isWindows)
							{
								try
								{
									Method m = Tree.class.getDeclaredMethod("setItemHeight", Integer.TYPE); //$NON-NLS-1$
									if (m != null)
									{
										m.setAccessible(true);
										m.invoke(tree, height);
									}
								}
								catch (Exception e)
								{
									CommonEditorPlugin.logError(e);
								}
							}
							// HACK! This is a major hack to force down the height of the row when we resize our font to
							// a
							// smaller height!
							else if (isMacOSX && isCocoa)
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
						// OK, the app explorer font changed. We need to force a refresh of the app explorer tree
						if (fTreeViewer != null)
							fTreeViewer.refresh();
						tree.redraw();
						tree.update();
					}
				});

			}
		};
		JFaceResources.getFontRegistry().addListener(fontListener);
	}

	private void addThemeChangeListener()
	{
		fThemeChangeListener = new IPreferenceChangeListener()
		{

			@Override
			public void preferenceChange(PreferenceChangeEvent event)
			{
				if (event.getKey().equals(IThemeManager.THEME_CHANGED))
				{
					getTree().setBackground(getBackground());
					getTree().setForeground(getForeground());
					if (fTreeViewer != null)
						fTreeViewer.refresh();
				}
			}
		};
		new InstanceScope().getNode(CommonEditorPlugin.PLUGIN_ID).addPreferenceChangeListener(fThemeChangeListener);
	}

	public void dispose()
	{
		removeSelectionOverride();
		removeMeasureItemListener();
		removeFontListener();
		removeThemeListener();
	}

	private void removeSelectionOverride()
	{
		if (selectionOverride != null && getTree() != null && !getTree().isDisposed())
		{
			getTree().removeListener(SWT.EraseItem, selectionOverride);
		}
		selectionOverride = null;
	}

	private void removeMeasureItemListener()
	{
		if (measureItemListener != null && getTree() != null && !getTree().isDisposed())
		{
			getTree().removeListener(SWT.MeasureItem, measureItemListener);
		}
		measureItemListener = null;
	}

	private Tree getTree()
	{
		if (fTree == null && fTreeViewer != null)
			fTree = fTreeViewer.getTree();
		return fTree;
	}

	private void removeFontListener()
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

	protected IThemeManager getThemeManager()
	{
		return CommonEditorPlugin.getDefault().getThemeManager();
	}

	protected Color getForeground()
	{
		return getColorManager().getColor(getThemeManager().getCurrentTheme().getForeground());
	}

	/**
	 * @return
	 */
	protected Color getBackground()
	{
		return getColorManager().getColor(getThemeManager().getCurrentTheme().getBackground());
	}

	protected Color getSelection()
	{
		return getColorManager().getColor(getThemeManager().getCurrentTheme().getSelection());
	}

	/**
	 * @return
	 */
	protected ColorManager getColorManager()
	{
		return CommonEditorPlugin.getDefault().getColorManager();
	}
}
