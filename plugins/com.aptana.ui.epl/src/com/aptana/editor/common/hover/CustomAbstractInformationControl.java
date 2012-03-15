/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.editor.common.hover;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.internal.text.revisions.Colors;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IInformationControlExtension;
import org.eclipse.jface.text.IInformationControlExtension2;
import org.eclipse.jface.text.IInformationControlExtension3;
import org.eclipse.jface.text.IInformationControlExtension4;
import org.eclipse.jface.text.IInformationControlExtension5;
import org.eclipse.jface.util.Geometry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.ToolBar;

/**
 * <b>Appcelerator Mod: Better control over the coloring of the AbstractInformationControl</b><br>
 * <br>
 * An abstract information control that can show content inside a shell. The information control can be created in two
 * styles:
 * <ul>
 * <li>non-resizable tooltip with optional status</li>
 * <li>resizable tooltip with optional tool bar</li>
 * </ul>
 * Additionally it can present either a status line containing a status text or a toolbar containing toolbar buttons.
 * <p>
 * Subclasses must either override {@link IInformationControl#setInformation(String)} or implement
 * {@link IInformationControlExtension2}. They should also extend {@link #computeTrim()} if they create a content area
 * with additional trim (e.g. scrollbars) and override {@link #getInformationPresenterControlCreator()}.
 * </p>
 * 
 * @since 3.4
 */
@SuppressWarnings("restriction")
public abstract class CustomAbstractInformationControl implements IInformationControl, IInformationControlExtension,
		IInformationControlExtension3, IInformationControlExtension4, IInformationControlExtension5
{

	/** The information control's shell. */
	private final Shell fShell;
	/** Composite containing the content created by subclasses. */
	private final Composite fContentComposite;
	/** Whether the information control is resizable. */
	private final boolean fResizable;

	/** Composite containing the status line content or <code>null</code> if none. */
	private Composite fStatusComposite;
	/** Separator between content and status line or <code>null</code> if none. */
	private Label fSeparator;
	/** Label in the status line or <code>null</code> if none. */
	private Label fStatusLabel;
	/**
	 * Font for the label in the status line or <code>null</code> if none.
	 * 
	 * @since 3.4.2
	 */
	private Font fStatusLabelFont;
	/**
	 * Color for the label in the status line or <code>null</code> if none.
	 * 
	 * @since 3.6
	 */
	private Color fStatusLabelForeground;
	/** The toolbar manager used by the toolbar or <code>null</code> if none. */
	private final ToolBarManager fToolBarManager;
	/** Status line toolbar or <code>null</code> if none. */
	private ToolBar fToolBar;

	/** Listener for shell activation and deactivation. */
	private Listener fShellListener;
	/** All focus listeners registered to this information control. */
	private final ListenerList fFocusListeners = new ListenerList(ListenerList.IDENTITY);

	/** Size constraints, x is the maxWidth and y is the maxHeight, or <code>null</code> if not set. */
	private Point fSizeConstraints;
	/** The size of the resize handle if already set, -1 otherwise */
	private int fResizeHandleSize;

	/**
	 * Creates an abstract information control with the given shell as parent. The control will not be resizable and
	 * optionally show a status line with the given status field text.
	 * <p>
	 * <em>Important: Subclasses are required to call {@link #create()} at the end of their constructor.</em>
	 * </p>
	 * 
	 * @param parentShell
	 *            the parent of this control's shell
	 * @param statusFieldText
	 *            the text to be used in the status field or <code>null</code> to hide the status field
	 */
	public CustomAbstractInformationControl(Shell parentShell, String statusFieldText)
	{
		this(parentShell, SWT.TOOL | SWT.ON_TOP, statusFieldText, null);
	}

	/**
	 * Creates an abstract information control with the given shell as parent. The control will be resizable and
	 * optionally show a tool bar managed by the given tool bar manager.
	 * <p>
	 * <em>Important: Subclasses are required to call {@link #create()} at the end of their constructor.</em>
	 * </p>
	 * 
	 * @param parentShell
	 *            the parent of this control's shell
	 * @param toolBarManager
	 *            the manager or <code>null</code> if toolbar is not desired
	 */
	public CustomAbstractInformationControl(Shell parentShell, ToolBarManager toolBarManager)
	{
		this(parentShell, SWT.TOOL | SWT.ON_TOP | SWT.RESIZE, null, toolBarManager);
	}

	/**
	 * Creates an abstract information control with the given shell as parent.
	 * <p>
	 * <em>Important: Subclasses are required to call {@link #create()} at the end of their constructor.</em>
	 * </p>
	 * 
	 * @param parentShell
	 *            the parent of this control's shell
	 * @param isResizable
	 *            <code>true</code> if the control should be resizable
	 */
	public CustomAbstractInformationControl(Shell parentShell, boolean isResizable)
	{
		this(parentShell, SWT.TOOL | SWT.ON_TOP | (isResizable ? SWT.RESIZE : 0), null, null);
	}

	/**
	 * Creates an abstract information control with the given shell as parent. The given shell style is used for the
	 * shell (NO_TRIM will be removed to make sure there's a border).
	 * <p>
	 * The control will optionally show either a status line or a tool bar. At most one of <code>toolBarManager</code>
	 * or <code>statusFieldText</code> can be non-null.
	 * </p>
	 * <p>
	 * <strong>Important:</strong>: Subclasses are required to call {@link #create()} at the end of their constructor.
	 * </p>
	 * 
	 * @param parentShell
	 *            the parent of this control's shell
	 * @param shellStyle
	 *            style of this control's shell
	 * @param statusFieldText
	 *            the text to be used in the status field or <code>null</code> to hide the status field
	 * @param toolBarManager
	 *            the manager or <code>null</code> if toolbar is not desired
	 * @deprecated clients should use one of the public constructors
	 */
	CustomAbstractInformationControl(Shell parentShell, int shellStyle, final String statusFieldText,
			final ToolBarManager toolBarManager)
	{
		Assert.isTrue(statusFieldText == null || toolBarManager == null);
		fResizeHandleSize = -1;
		fToolBarManager = toolBarManager;

		if ((shellStyle & SWT.NO_TRIM) != 0)
			shellStyle &= ~(SWT.NO_TRIM | SWT.SHELL_TRIM); // make sure we get the OS border but no other trims

		fResizable = (shellStyle & SWT.RESIZE) != 0;
		fShell = new Shell(parentShell, shellStyle);
		Display display = fShell.getDisplay();
		Color foreground = display.getSystemColor(SWT.COLOR_INFO_FOREGROUND);
		Color background = display.getSystemColor(SWT.COLOR_INFO_BACKGROUND);
		setColor(fShell, foreground, background);

		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		fShell.setLayout(layout);

		fContentComposite = new Composite(fShell, SWT.NONE);
		fContentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		fContentComposite.setLayout(new FillLayout());
		setColor(fContentComposite, foreground, background);

		createStatusComposite(statusFieldText, toolBarManager, foreground, background);

		addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent e)
			{
				handleDispose();
			}
		});

	}

	private void createStatusComposite(final String statusFieldText, final ToolBarManager toolBarManager,
			Color foreground, Color background)
	{
		if (toolBarManager == null && statusFieldText == null)
			return;

		fStatusComposite = new Composite(fShell, SWT.NONE);
		GridData gridData = new GridData(SWT.FILL, SWT.BOTTOM, true, false);
		fStatusComposite.setLayoutData(gridData);
		GridLayout statusLayout = new GridLayout(1, false);
		statusLayout.marginHeight = 0;
		statusLayout.marginWidth = 0;
		statusLayout.verticalSpacing = 1;
		fStatusComposite.setLayout(statusLayout);

		fSeparator = new Label(fStatusComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
		fSeparator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		if (statusFieldText != null)
		{
			createStatusLabel(statusFieldText, foreground, background);
		}
		else
		{
			createToolBar(toolBarManager);
		}
	}

	private void createStatusLabel(final String statusFieldText, Color foreground, Color background)
	{
		fStatusLabel = new Label(fStatusComposite, SWT.RIGHT);
		fStatusLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fStatusLabel.setText(statusFieldText);

		FontData[] fontDatas = JFaceResources.getDialogFont().getFontData();
		for (int i = 0; i < fontDatas.length; i++)
		{
			fontDatas[i].setHeight(fontDatas[i].getHeight() * 9 / 10);
		}
		fStatusLabelFont = new Font(fStatusLabel.getDisplay(), fontDatas);
		fStatusLabel.setFont(fStatusLabelFont);

		// Appcelerator Mod
		updateStatusColors(foreground, background);
	}

	/**
	 * [Appcelerator Mod]<br>
	 * Update the status component colors.<br>
	 * Note: It's up to the caller to dispose any custom colors when needed.
	 * 
	 * @param foreground
	 * @param background
	 */
	protected void updateStatusColors(Color foreground, Color background)
	{
		if (foreground != null && background != null)
		{
			if (fStatusLabelForeground != null && !fStatusLabelForeground.isDisposed())
			{
				fStatusLabelForeground.dispose();
				fStatusLabelForeground = null;
			}
			fStatusLabelForeground = new Color(fStatusLabel.getDisplay(), Colors.blend(background.getRGB(),
					foreground.getRGB(), 0.56f));
		}
		if (fStatusLabelForeground != null)
		{
			fStatusLabel.setForeground(fStatusLabelForeground);
		}
		else
		{
			fStatusLabel.setForeground(foreground);
		}
		fStatusLabel.setBackground(background);
		setColor(fStatusComposite, foreground, background);
	}

	private void createToolBar(ToolBarManager toolBarManager)
	{
		final Composite bars = new Composite(fStatusComposite, SWT.NONE);
		bars.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		GridLayout layout = new GridLayout(3, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		bars.setLayout(layout);

		fToolBar = toolBarManager.createControl(bars);
		GridData gd = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		fToolBar.setLayoutData(gd);

		Composite spacer = new Composite(bars, SWT.NONE);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint = 0;
		gd.heightHint = 0;
		spacer.setLayoutData(gd);

		addMoveSupport(spacer);
		addResizeSupportIfNecessary(bars);
	}

	private void addResizeSupportIfNecessary(final Composite bars)
	{
		// XXX: workarounds for
		// - https://bugs.eclipse.org/bugs/show_bug.cgi?id=219139 : API to add resize grip / grow box in lower right
		// corner of shell
		// - https://bugs.eclipse.org/bugs/show_bug.cgi?id=23980 : platform specific shell resize behavior
		String platform = SWT.getPlatform();
		final boolean isWin = platform.equals("win32"); //$NON-NLS-1$
		if (!isWin && !platform.equals("gtk")) //$NON-NLS-1$
			return;

		final Canvas resizer = new Canvas(bars, SWT.NONE);

		int size = getResizeHandleSize(bars);

		GridData data = new GridData(SWT.END, SWT.END, false, true);
		data.widthHint = size;
		data.heightHint = size;
		resizer.setLayoutData(data);
		resizer.addPaintListener(new PaintListener()
		{
			public void paintControl(PaintEvent e)
			{
				Point s = resizer.getSize();
				int x = s.x - 2;
				int y = s.y - 2;
				int min = Math.min(x, y);
				if (isWin)
				{
					// draw dots
					e.gc.setBackground(resizer.getDisplay().getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
					int end = min - 1;
					for (int i = 0; i <= 2; i++)
						for (int j = 0; j <= 2 - i; j++)
							e.gc.fillRectangle(end - 4 * i, end - 4 * j, 2, 2);
					end--;
					e.gc.setBackground(resizer.getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
					for (int i = 0; i <= 2; i++)
						for (int j = 0; j <= 2 - i; j++)
							e.gc.fillRectangle(end - 4 * i, end - 4 * j, 2, 2);

				}
				else
				{
					// draw diagonal lines
					e.gc.setForeground(resizer.getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
					for (int i = 1; i < min; i += 4)
					{
						e.gc.drawLine(i, y, x, i);
					}
					e.gc.setForeground(resizer.getDisplay().getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
					for (int i = 2; i < min; i += 4)
					{
						e.gc.drawLine(i, y, x, i);
					}
				}
			}
		});

		final boolean isRTL = (resizer.getShell().getStyle() & SWT.RIGHT_TO_LEFT) != 0;
		resizer.setCursor(resizer.getDisplay().getSystemCursor(isRTL ? SWT.CURSOR_SIZESW : SWT.CURSOR_SIZESE));
		MouseAdapter resizeSupport = new MouseAdapter()
		{
			private MouseMoveListener fResizeListener;

			public void mouseDown(MouseEvent e)
			{
				Rectangle shellBounds = fShell.getBounds();
				final int shellX = shellBounds.x;
				final int shellY = shellBounds.y;
				final int shellWidth = shellBounds.width;
				final int shellHeight = shellBounds.height;
				Point mouseLoc = resizer.toDisplay(e.x, e.y);
				final int mouseX = mouseLoc.x;
				final int mouseY = mouseLoc.y;
				fResizeListener = new MouseMoveListener()
				{
					public void mouseMove(MouseEvent e2)
					{
						Point mouseLoc2 = resizer.toDisplay(e2.x, e2.y);
						int dx = mouseLoc2.x - mouseX;
						int dy = mouseLoc2.y - mouseY;
						if (isRTL)
						{
							setLocation(new Point(shellX + dx, shellY));
							setSize(shellWidth - dx, shellHeight + dy);
						}
						else
						{
							setSize(shellWidth + dx, shellHeight + dy);
						}
					}
				};
				resizer.addMouseMoveListener(fResizeListener);
			}

			public void mouseUp(MouseEvent e)
			{
				resizer.removeMouseMoveListener(fResizeListener);
				fResizeListener = null;
			}
		};
		resizer.addMouseListener(resizeSupport);
	}

	private int getResizeHandleSize(Composite parent)
	{
		if (fResizeHandleSize == -1)
		{
			Slider sliderV = new Slider(parent, SWT.VERTICAL);
			Slider sliderH = new Slider(parent, SWT.HORIZONTAL);
			int width = sliderV.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
			int height = sliderH.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
			sliderV.dispose();
			sliderH.dispose();
			fResizeHandleSize = Math.min(width, height);
		}

		return fResizeHandleSize;
	}

	/**
	 * Adds support to move the shell by dragging the given control.
	 * 
	 * @param control
	 *            the control that can be used to move the shell
	 */
	private void addMoveSupport(final Control control)
	{
		MouseAdapter moveSupport = new MouseAdapter()
		{
			private MouseMoveListener fMoveListener;

			public void mouseDown(MouseEvent e)
			{
				Point shellLoc = fShell.getLocation();
				final int shellX = shellLoc.x;
				final int shellY = shellLoc.y;
				Point mouseLoc = control.toDisplay(e.x, e.y);
				final int mouseX = mouseLoc.x;
				final int mouseY = mouseLoc.y;
				fMoveListener = new MouseMoveListener()
				{
					public void mouseMove(MouseEvent e2)
					{
						Point mouseLoc2 = control.toDisplay(e2.x, e2.y);
						int dx = mouseLoc2.x - mouseX;
						int dy = mouseLoc2.y - mouseY;
						fShell.setLocation(shellX + dx, shellY + dy);
					}
				};
				control.addMouseMoveListener(fMoveListener);
			}

			public void mouseUp(MouseEvent e)
			{
				control.removeMouseMoveListener(fMoveListener);
				fMoveListener = null;
			}
		};
		control.addMouseListener(moveSupport);
	}

	/**
	 * Utility to set the foreground and the background color of the given control
	 * 
	 * @param control
	 *            the control to modify
	 * @param foreground
	 *            the color to use for the foreground
	 * @param background
	 *            the color to use for the background
	 */
	private static void setColor(Control control, Color foreground, Color background)
	{
		control.setForeground(foreground);
		control.setBackground(background);
	}

	/**
	 * The shell of the popup window.
	 * 
	 * @return the shell used for the popup window
	 */
	protected final Shell getShell()
	{
		return fShell;
	}

	/**
	 * The toolbar manager used to manage the toolbar, or <code>null</code> if no toolbar is shown.
	 * 
	 * @return the tool bar manager or <code>null</code>
	 */
	protected final ToolBarManager getToolBarManager()
	{
		return fToolBarManager;
	}

	/**
	 * Creates the content of this information control. Subclasses must call this method at the end of their
	 * constructor(s).
	 */
	protected final void create()
	{
		createContent(fContentComposite);
	}

	/**
	 * Creates the content of the popup window.
	 * <p>
	 * Implementors will usually take over {@link Composite#getBackground()} and {@link Composite#getForeground()} from
	 * <code>parent</code>.
	 * </p>
	 * <p>
	 * Implementors must either use the dialog font or override {@link #computeSizeConstraints(int, int)}.
	 * </p>
	 * <p>
	 * Implementors are expected to consider {@link #isResizable()}: If <code>true</code>, they should show scrollbars
	 * if their content may exceed the size of the information control. If <code>false</code>, they should never show
	 * scrollbars.
	 * </p>
	 * <p>
	 * The given <code>parent</code> comes with a {@link FillLayout}. Subclasses may set a different layout.
	 * </p>
	 * 
	 * @param parent
	 *            the container of the content
	 */
	protected abstract void createContent(Composite parent);

	/**
	 * Sets the information to be presented by this information control.
	 * <p>
	 * The default implementation does nothing. Subclasses must either override this method or implement
	 * {@link IInformationControlExtension2}.
	 * 
	 * @param information
	 *            the information to be presented
	 * @see org.eclipse.jface.text.IInformationControl#setInformation(java.lang.String)
	 */
	public void setInformation(String information)
	{

	}

	/**
	 * Returns whether the information control is resizable.
	 * 
	 * @return <code>true</code> if the information control is resizable, <code>false</code> if it is not resizable.
	 */
	public boolean isResizable()
	{
		return fResizable;
	}

	/*
	 * @see IInformationControl#setVisible(boolean)
	 */
	public void setVisible(boolean visible)
	{
		if (fShell.isVisible() == visible)
			return;

		fShell.setVisible(visible);
	}

	/*
	 * @see IInformationControl#dispose()
	 */
	public void dispose()
	{
		if (fShell != null && !fShell.isDisposed())
			fShell.dispose();
	}

	/**
	 * Frees all resources allocated by this information control. Internally called when the information control's shell
	 * has been disposed.
	 * 
	 * @since 3.6
	 */
	protected void handleDispose()
	{
		if (fStatusLabelFont != null)
		{
			fStatusLabelFont.dispose();
			fStatusLabelFont = null;
		}
		if (fStatusLabelForeground != null)
		{
			fStatusLabelForeground.dispose();
			fStatusLabelForeground = null;
		}
	}

	/*
	 * @see IInformationControl#setSize(int, int)
	 */
	public void setSize(int width, int height)
	{
		fShell.setSize(width, height);
	}

	/*
	 * @see IInformationControl#setLocation(Point)
	 */
	public void setLocation(Point location)
	{
		fShell.setLocation(location);
	}

	/*
	 * @see IInformationControl#setSizeConstraints(int, int)
	 */
	public void setSizeConstraints(int maxWidth, int maxHeight)
	{
		fSizeConstraints = new Point(maxWidth, maxHeight);
	}

	/**
	 * Returns the size constraints.
	 * 
	 * @return the size constraints or <code>null</code> if not set
	 * @see #setSizeConstraints(int, int)
	 */
	protected final Point getSizeConstraints()
	{
		return fSizeConstraints != null ? Geometry.copy(fSizeConstraints) : null;
	}

	/*
	 * @see IInformationControl#computeSizeHint()
	 */
	public Point computeSizeHint()
	{
		// XXX: Verify whether this is a good default implementation. If yes, document it.
		Point constrains = getSizeConstraints();
		if (constrains == null)
			return fShell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);

		return fShell.computeSize(constrains.x, constrains.y, true);
	}

	/**
	 * Computes the trim (status text and tool bar are considered as trim). Subclasses can extend this method to add
	 * additional trim (e.g. scroll bars for resizable information controls).
	 * 
	 * @see org.eclipse.jface.text.IInformationControlExtension3#computeTrim()
	 */
	public Rectangle computeTrim()
	{
		Rectangle trim = fShell.computeTrim(0, 0, 0, 0);

		if (fStatusComposite != null)
			trim.height += fStatusComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;

		return trim;
	}

	/*
	 * @see org.eclipse.jface.text.IInformationControlExtension3#getBounds()
	 */
	public Rectangle getBounds()
	{
		return fShell.getBounds();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The default implementation always returns <code>false</code>.
	 * </p>
	 * 
	 * @see org.eclipse.jface.text.IInformationControlExtension3#restoresLocation()
	 */
	public boolean restoresLocation()
	{
		return false;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The default implementation always returns <code>false</code>.
	 * </p>
	 * 
	 * @see org.eclipse.jface.text.IInformationControlExtension3#restoresSize()
	 */
	public boolean restoresSize()
	{
		return false;
	}

	/*
	 * @see IInformationControl#addDisposeListener(DisposeListener)
	 */
	public void addDisposeListener(DisposeListener listener)
	{
		fShell.addDisposeListener(listener);
	}

	/*
	 * @see IInformationControl#removeDisposeListener(DisposeListener)
	 */
	public void removeDisposeListener(DisposeListener listener)
	{
		fShell.removeDisposeListener(listener);
	}

	/*
	 * @see IInformationControl#setForegroundColor(Color)
	 */
	public void setForegroundColor(Color foreground)
	{
		fContentComposite.setForeground(foreground);
		// Appcelerator Mod
		if (fStatusLabel != null && !fStatusLabel.isDisposed())
		{
			updateStatusColors(foreground, fStatusLabel.getBackground());
		}
	}

	/*
	 * @see IInformationControl#setBackgroundColor(Color)
	 */
	public void setBackgroundColor(Color background)
	{
		fContentComposite.setBackground(background);
		// Appcelerator Mod
		if (fStatusLabel != null && !fStatusLabel.isDisposed())
		{
			updateStatusColors(fStatusLabel.getForeground(), background);
		}
	}

	/**
	 * {@inheritDoc} This method is not intended to be overridden by subclasses.
	 */
	public boolean isFocusControl()
	{
		return fShell.getDisplay().getActiveShell() == fShell;
	}

	/**
	 * This default implementation sets the focus on the popup shell. Subclasses can override or extend.
	 * 
	 * @see IInformationControl#setFocus()
	 */
	public void setFocus()
	{
		boolean focusTaken = fShell.setFocus();
		if (!focusTaken)
			fShell.forceFocus();
	}

	/**
	 * {@inheritDoc} This method is not intended to be overridden by subclasses.
	 */
	public void addFocusListener(final FocusListener listener)
	{
		if (fFocusListeners.isEmpty())
		{
			fShellListener = new Listener()
			{

				public void handleEvent(Event event)
				{
					Object[] listeners = fFocusListeners.getListeners();
					for (int i = 0; i < listeners.length; i++)
					{
						FocusListener focusListener = (FocusListener) listeners[i];
						if (event.type == SWT.Activate)
						{
							focusListener.focusGained(new FocusEvent(event));
						}
						else
						{
							focusListener.focusLost(new FocusEvent(event));
						}
					}
				}
			};
			fShell.addListener(SWT.Deactivate, fShellListener);
			fShell.addListener(SWT.Activate, fShellListener);
		}
		fFocusListeners.add(listener);
	}

	/**
	 * {@inheritDoc} This method is not intended to be overridden by subclasses.
	 */
	public void removeFocusListener(FocusListener listener)
	{
		fFocusListeners.remove(listener);
		if (fFocusListeners.isEmpty())
		{
			fShell.removeListener(SWT.Activate, fShellListener);
			fShell.removeListener(SWT.Deactivate, fShellListener);
			fShellListener = null;
		}
	}

	/**
	 * Sets the text of the status field.
	 * <p>
	 * The default implementation currently only updates the status field when the popup shell is not visible. The
	 * status field can currently only be shown if the information control has been created with a non-null status field
	 * text.
	 * </p>
	 * 
	 * @param statusFieldText
	 *            the text to be used in the optional status field or <code>null</code> if the status field should be
	 *            hidden
	 * @see org.eclipse.jface.text.IInformationControlExtension4#setStatusText(java.lang.String)
	 */
	public void setStatusText(String statusFieldText)
	{
		if (fStatusLabel != null && !getShell().isVisible())
		{
			if (statusFieldText == null)
			{
				fStatusComposite.setVisible(false);
			}
			else
			{
				fStatusLabel.setText(statusFieldText);
				fStatusComposite.setVisible(true);
			}
		}
	}

	/*
	 * @see org.eclipse.jface.text.IInformationControlExtension5#containsControl(org.eclipse.swt.widgets.Control)
	 */
	public boolean containsControl(Control control)
	{
		do
		{
			if (control == fShell)
				return true;
			if (control instanceof Shell)
				return false;
			control = control.getParent();
		}
		while (control != null);
		return false;
	}

	/*
	 * @see org.eclipse.jface.text.IInformationControlExtension5#isVisible()
	 */
	public boolean isVisible()
	{
		return fShell != null && !fShell.isDisposed() && fShell.isVisible();
	}

	/**
	 * {@inheritDoc} This default implementation returns <code>null</code>. Subclasses may override.
	 */
	public IInformationControlCreator getInformationPresenterControlCreator()
	{
		return null;
	}

	/**
	 * Computes the size constraints based on the {@link JFaceResources#getDialogFont() dialog font}. Subclasses can
	 * override or extend.
	 * 
	 * @see org.eclipse.jface.text.IInformationControlExtension5#computeSizeConstraints(int, int)
	 */
	public Point computeSizeConstraints(int widthInChars, int heightInChars)
	{
		GC gc = new GC(fContentComposite);
		gc.setFont(JFaceResources.getDialogFont());
		int width = gc.getFontMetrics().getAverageCharWidth();
		int height = gc.getFontMetrics().getHeight();
		gc.dispose();

		return new Point(widthInChars * width, heightInChars * height);
	}

}
