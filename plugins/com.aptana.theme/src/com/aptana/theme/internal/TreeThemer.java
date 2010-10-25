/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.theme.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import com.aptana.theme.IThemeManager;
import com.aptana.theme.ThemePlugin;
import com.aptana.theme.ThemedDelegatingLabelProvider;

/**
 * A Utility class that makes "hijacking" a Tree and hooking it up to our themes easy.
 * 
 * @author cwilliams
 */
class TreeThemer extends ControlThemer
{

	private TreeViewer fTreeViewer;
	private IPropertyChangeListener fontListener;
	private Listener measureItemListener;
	private Listener customDrawingListener;
	private Listener selectionPaintListener;

	public TreeThemer(TreeViewer treeViewer)
	{
		super(treeViewer.getTree());
		this.fTreeViewer = treeViewer;
	}

	public TreeThemer(Tree tree)
	{
		super(tree);
	}

	public void apply()
	{
		super.apply();
		addSelectionColorOverride();
		addCustomTreeControlDrawing();
		addMeasureItemListener();
		addFontListener();
		overrideLabelProvider();
	}

	@Override
	protected void applyTheme()
	{
		super.applyTheme();
		if (fTreeViewer != null)
		{
			fTreeViewer.refresh(true);
		}
	}

	private void overrideLabelProvider()
	{
		ViewerColumn viewer = (ViewerColumn) getTree().getData("org.eclipse.jface.columnViewer"); //$NON-NLS-1$
		if (viewer == null)
		{
			return;
		}
		ColumnViewer colViewer = viewer.getViewer();
		if (colViewer == null)
		{
			return;
		}
		DelegatingCellLabelProvider existing = getExistingDelegator(viewer);
		if (existing != null)
		{
			existing.enable();
			colViewer.refresh();
			return;
		}

		IBaseLabelProvider provider = colViewer.getLabelProvider();
		if (provider instanceof CellLabelProvider)
		{
			// wrap
			final CellLabelProvider cellProvider = (CellLabelProvider) provider;
			DelegatingCellLabelProvider duh = new DelegatingCellLabelProvider(cellProvider);
			viewer.setLabelProvider(duh);
		}
		else if (provider instanceof ThemedDelegatingLabelProvider)
		{
			// re-enable
			ThemedDelegatingLabelProvider delegating = (ThemedDelegatingLabelProvider) provider;
			delegating.enable();
			colViewer.refresh();
		}
		else if (provider instanceof ILabelProvider)
		{
			// wrap
			colViewer.setLabelProvider(new ThemedDelegatingLabelProvider((ILabelProvider) provider));
		}
	}

	private DelegatingCellLabelProvider getExistingDelegator(ViewerColumn viewer)
	{
		try
		{
			Method m = ViewerColumn.class.getDeclaredMethod("getLabelProvider"); //$NON-NLS-1$
			m.setAccessible(true);
			CellLabelProvider provider = (CellLabelProvider) m.invoke(viewer);
			if (provider instanceof DelegatingCellLabelProvider)
			{
				return (DelegatingCellLabelProvider) provider;
			}
		}
		catch (Exception e)
		{
			ThemePlugin.logError(e);
		}
		return null;
	}

	private class DelegatingCellLabelProvider extends CellLabelProvider implements ILabelProvider
	{

		private CellLabelProvider cellProvider;
		private boolean isDisabled;

		DelegatingCellLabelProvider(CellLabelProvider cellProvider)
		{
			this.cellProvider = cellProvider;
			// HACK Very ugly hack because when we set this wrapping label provider, dispose is called on the old one,
			// which disposes all the images for working sets in JDT but holds onto them!
			if (cellProvider instanceof DelegatingStyledCellLabelProvider)
			{
				DelegatingStyledCellLabelProvider delegating = (DelegatingStyledCellLabelProvider) cellProvider;
				IStyledLabelProvider styled = delegating.getStyledStringProvider();
				if (styled.getClass().getName()
						.equals("org.eclipse.jdt.internal.ui.packageview.PackageExplorerLabelProvider")) //$NON-NLS-1$
				{
					try
					{
						Field f = styled.getClass().getDeclaredField("fWorkingSetImages"); //$NON-NLS-1$
						f.setAccessible(true);
						f.set(styled, null);
					}
					catch (Exception e)
					{
						ThemePlugin.logError(e);
					}
				}
			}
		}

		@Override
		public void update(ViewerCell cell)
		{
			cellProvider.update(cell);
			if (isDisabled)
			{
				cell.setFont(null);
				cell.setForeground(null);
			}
			else
			{
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
		}

		public void disable()
		{
			isDisabled = true;
		}

		public void enable()
		{
			isDisabled = false;
		}

		public Image getImage(Object element)
		{
			if (cellProvider instanceof ILabelProvider)
			{
				return ((ILabelProvider) cellProvider).getImage(element);
			}
			return null;
		}

		public String getText(Object element)
		{
			if (cellProvider instanceof ILabelProvider)
			{
				return ((ILabelProvider) cellProvider).getText(element);
			}
			return null;
		}
	}

	private void revertLabelProvider()
	{
		if (getTree() == null || getTree().isDisposed())
		{
			return;
		}
		ViewerColumn viewer = (ViewerColumn) getTree().getData("org.eclipse.jface.columnViewer"); //$NON-NLS-1$
		if (viewer == null)
		{
			return;
		}
		DelegatingCellLabelProvider existing = getExistingDelegator(viewer);
		if (existing != null)
		{
			existing.disable();
		}
		ColumnViewer colViewer = viewer.getViewer();
		if (colViewer == null)
		{
			return;
		}
		IBaseLabelProvider provider = colViewer.getLabelProvider();
		if (provider instanceof ThemedDelegatingLabelProvider)
		{
			ThemedDelegatingLabelProvider delegating = (ThemedDelegatingLabelProvider) provider;
			delegating.disable();
		}
		else if (provider instanceof DelegatingCellLabelProvider)
		{
			DelegatingCellLabelProvider delegating = (DelegatingCellLabelProvider) provider;
			delegating.disable();
		}
		colViewer.refresh();
	}

	protected void addSelectionColorOverride()
	{
		super.addSelectionColorOverride();
		final Tree tree = getTree();
		// This draws from right end of item to full width of tree, needed on windows so selection is full width of view
		selectionPaintListener = new Listener()
		{
			public void handleEvent(Event event)
			{
				try
				{
					TreeItem[] items = tree.getSelection();
					if (items == null || items.length == 0)
					{
						return;
					}
					Rectangle clientArea = tree.getClientArea();
					int clientWidth = clientArea.x + clientArea.width;

					GC gc = event.gc;
					Color oldBackground = gc.getBackground();

					gc.setBackground(getSelection());
					int columns = tree.getColumnCount();
					for (TreeItem item : items)
					{
						if (item != null)
						{						
							Rectangle bounds;
							if (columns == 0)
							{
								bounds = item.getBounds();
							}
							else
							{
								bounds = item.getBounds(columns - 1);
							}
							int x = bounds.x + bounds.width;
							if (x < clientWidth)
							{
								gc.fillRectangle(x, bounds.y, clientWidth - x, bounds.height);
							}
						}
					}
					gc.setBackground(oldBackground);

					// force foreground color. Otherwise on dark themes we get black FG (all the time on Win, on
					// non-focus for Mac)
					gc.setForeground(getForeground());
				}
				catch (Exception e)
				{
					// ignore
				}
			}
		};
		tree.addListener(SWT.Paint, selectionPaintListener);
	}

	private void addCustomTreeControlDrawing()
	{
		// Hack to overdraw the native tree expand/collapse controls and use custom plus/minus box.
		if (!isWindows)
		{
			return;
		}
		final Tree tree = getTree();
		customDrawingListener = new Listener()
		{
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
				{
					return;
				}
				final int width = 10;
				final int height = 12;
				final int x = event.x - 16;
				final int y = event.y + 4;
				Color oldBackground = gc.getBackground();
				gc.setBackground(getBackground());
				// wipe out the native control
				gc.fillRectangle(x, y, width + 1, height - 1); // +1 and -1 because of hovering selecting on windows
				// vista
				// draw a plus/minus based on expansion!
				gc.setBackground(getForeground());
				// draw surrounding box (with alpha so that it doesn't get too strong).
				gc.setAlpha(195);
				gc.drawRectangle(x + 1, y + 1, width - 2, width - 2); // make it smaller than the area erased
				gc.setAlpha(255);
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
		};
		tree.addListener(SWT.PaintItem, customDrawingListener);
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
					event.height = height;
					if (width > event.width)
					{
						event.width = width;
					}
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
			public void propertyChange(PropertyChangeEvent event)
			{
				if (!event.getProperty().equals(IThemeManager.VIEW_FONT_NAME))
				{
					return;
				}
				Display.getCurrent().asyncExec(new Runnable()
				{
					public void run()
					{
						Font font = getFont();
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
									ThemePlugin.logError(e);
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
											{
												m.invoke(widget, height);
											}
										}
									}
								}
								catch (Exception e)
								{
									ThemePlugin.logError(e);
								}
							}
						}
						// OK, the app explorer font changed. We need to force a refresh of the app explorer tree
						if (fTreeViewer != null)
						{
							fTreeViewer.refresh();
						}
						tree.redraw();
						tree.update();
					}
				});

			}
		};
		JFaceResources.getFontRegistry().addListener(fontListener);
	}

	public void dispose()
	{
		super.dispose();
		revertLabelProvider();
		removeSelectionOverride();
		removeCustomTreeControlDrawing();
		removeMeasureItemListener();
		removeFontListener();
	}

	private void removeCustomTreeControlDrawing()
	{
		if (customDrawingListener != null && getTree() != null && !getTree().isDisposed())
		{
			getTree().removeListener(SWT.PaintItem, customDrawingListener);
		}
		customDrawingListener = null;
	}

	protected void removeSelectionOverride()
	{
		super.removeSelectionOverride();

		if (selectionPaintListener != null && getTree() != null && !getTree().isDisposed())
		{
			getTree().removeListener(SWT.Paint, selectionPaintListener);
		}
		selectionPaintListener = null;
	}

	private void removeMeasureItemListener()
	{
		if (measureItemListener != null && getTree() != null && !getTree().isDisposed())
		{
			getTree().removeListener(SWT.MeasureItem, measureItemListener);
		}
		measureItemListener = null;
	}

	protected Tree getTree()
	{
		return (Tree) getControl();
	}

	private void removeFontListener()
	{
		if (fontListener != null)
		{
			JFaceResources.getFontRegistry().removeListener(fontListener);
		}
		fontListener = null;
	}
}
