package com.aptana.terminal;

import java.util.UUID;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.SWTKeySupport;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.part.WorkbenchPart;
import org.eclipse.ui.progress.UIJob;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.theme.IThemeManager;
import com.aptana.terminal.server.ProcessWrapper;
import com.aptana.terminal.server.TerminalServer;
import com.aptana.util.ClipboardUtil;

public class TerminalBrowser
{
	private static final String TERMINAL_URL = "http://{0}:{1}/webterm/"; //$NON-NLS-1$
	private static final String TEXTFONT_PROPERTY = "org.eclipse.jface.textfont"; //$NON-NLS-1$

	public static KeyStroke COPY_STROKE;
	public static KeyStroke PASTE_STROKE;
	public static KeyStroke SELECT_ALL_STROKE;

	private Browser _browser;
	private WorkbenchPart _owningPart;
	private String _id;
	private String _startingDirectory;
	
	private Action copy;
	private Action paste;
	private Action selectAll;
	
	private IPreferenceChangeListener _themeChangeListener;
	private IPropertyChangeListener _fontChangeListener;
	private KeyListener _keyListener;
	private IPartListener _partListener;
	private IWindowListener _windowListener;

	static
	{
		int modifiers = SWT.SHIFT | SWT.MOD1;

		COPY_STROKE = KeyStroke.getInstance(modifiers, 'C');
		PASTE_STROKE = KeyStroke.getInstance(modifiers, 'V');
		SELECT_ALL_STROKE = KeyStroke.getInstance(modifiers, 'A');

		// Add window listener to track deactivation of window
		PlatformUI.getWorkbench().addWindowListener(new IWindowListener()
		{
			@Override
			public void windowOpened(IWorkbenchWindow window)
			{
			}

			@Override
			public void windowDeactivated(IWorkbenchWindow window)
			{
				// We make sure that the key filtering is enabled
				// when a workbench window is deactivated
				setKeyFilterEnabled(true);
			}

			@Override
			public void windowClosed(IWorkbenchWindow window)
			{
			}

			@Override
			public void windowActivated(IWorkbenchWindow window)
			{
			}
		});
	}

	/**
	 * TerminalBrowser
	 * 
	 * @param owningPart
	 */
	public TerminalBrowser(WorkbenchPart owningPart)
	{
		this(owningPart, null);
	}

	/**
	 * TerminalBrowser
	 * 
	 * @param owningPart
	 * @param startingDirectory
	 */
	public TerminalBrowser(WorkbenchPart owningPart, String startingDirectory)
	{
		this._owningPart = owningPart;
		this._startingDirectory = startingDirectory;
		this._id = UUID.randomUUID().toString();
	}

	/**
	 * addFontChangeListener
	 */
	private void addFontChangeListener()
	{
		// refresh CSS when theme's font changes
		this._fontChangeListener = new IPropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent event)
			{
				if (_browser != null && event.getProperty().equals(TEXTFONT_PROPERTY))
				{
					applyTheme();
				}
			}
		};
		JFaceResources.getFontRegistry().addListener(this._fontChangeListener);

		// create our supporting process for access to the system's shell
		TerminalServer.getInstance().createProcess(this._id, this.getStartingDirectory());

		// load the terminal
		String url = NLS.bind(TERMINAL_URL, new Object[] { TerminalServer.getInstance().getHost(),
				TerminalServer.getInstance().getPort() })
				+ "?id=" + this._id; //$NON-NLS-1$
		this.setUrl(url);
	}

	/**
	 * addKeyListener
	 */
	private void addKeyListener()
	{
		this._keyListener = new KeyListener()
		{
			public void keyPressed(KeyEvent e)
			{
				int accelerator = SWTKeySupport.convertEventToUnmodifiedAccelerator(e);
				KeyStroke key = SWTKeySupport.convertAcceleratorToKeyStroke(accelerator);

				if (key.isComplete())
				{
					if (COPY_STROKE.equals(key))
					{
						copy();
					}
					else if (PASTE_STROKE.equals(key))
					{
						paste();
					}
					else if (SELECT_ALL_STROKE.equals(key))
					{
						selectAll();
					}
				}
			}

			public void keyReleased(KeyEvent e)
			{
			}
		};

		this._browser.addKeyListener(this._keyListener);
	}

	/**
	 * addPartListener
	 */
	private void addPartListener()
	{
		_partListener = new IPartListener()
		{

			@Override
			public void partOpened(IWorkbenchPart part)
			{
			}

			@Override
			public void partDeactivated(IWorkbenchPart part)
			{
				if (part == _owningPart)
				{
					// Enable key filter
					setKeyFilterEnabled(true);
				}
			}

			@Override
			public void partClosed(IWorkbenchPart part)
			{
			}

			@Override
			public void partBroughtToTop(IWorkbenchPart part)
			{
			}

			@Override
			public void partActivated(IWorkbenchPart part)
			{
				if (part == _owningPart)
				{
					// Disable key filter
					setKeyFilterEnabled(false);
				}
			}
		};

		// Add part listener to track Part activation and deactivation
		this._owningPart.getSite().getWorkbenchWindow().getPartService().addPartListener(_partListener);
	}

	private void addWindowListener()
	{
		_windowListener = new IWindowListener()
		{

			@Override
			public void windowOpened(IWorkbenchWindow window)
			{
			}

			@Override
			public void windowDeactivated(IWorkbenchWindow window)
			{
			}

			@Override
			public void windowClosed(IWorkbenchWindow window)
			{
			}

			@Override
			public void windowActivated(IWorkbenchWindow window)
			{
				IWorkbenchPage activePage = window.getActivePage();
				if (activePage == null)
				{
					return;
				}

				IWorkbenchPart activePart = activePage.getActivePart();
				if (activePart == _owningPart)
				{
					// We make sure that the key filtering is disabled
					// when a workbench window is activated and the active part
					// is this terminal browser's owner
					setKeyFilterEnabled(false);
				}

			}
		};

		// Add window listener to track activation of window
		PlatformUI.getWorkbench().addWindowListener(_windowListener);
	}

	/**
	 * addListeners
	 */
	private void addListeners()
	{
		this.addPartListener();
		this.addWindowListener();
		this.addThemeListener();
		this.addFontChangeListener();
		this.addKeyListener();
	}

	/**
	 * addThemeListener
	 */
	private void addThemeListener()
	{
		// Force refresh of CSS when theme changes
		this._themeChangeListener = new IPreferenceChangeListener()
		{
			@Override
			public void preferenceChange(PreferenceChangeEvent event)
			{
				if (_browser != null && event.getKey().equals(IThemeManager.THEME_CHANGED))
				{
					applyTheme();
				}
			}
		};
		new InstanceScope().getNode(CommonEditorPlugin.PLUGIN_ID).addPreferenceChangeListener(_themeChangeListener);
	}

	/**
	 * applyTheme
	 */
	private void applyTheme()
	{
		try
		{
			IWorkbench workbench = PlatformUI.getWorkbench();
			Display display = workbench.getDisplay();

			display.syncExec(new Runnable()
			{
				@Override
				public void run()
				{
					_browser.execute("onThemeChange()"); //$NON-NLS-1$
				}
			});
		}
		catch (IllegalStateException e)
		{
			// do nothing
		}
	}

	/**
	 * setKeyFilterEnabled
	 *
	 * @param enabled
	 */
	private static void setKeyFilterEnabled(boolean enable)
	{
		IBindingService bindingService = (IBindingService) PlatformUI.getWorkbench().getService(
				IBindingService.class);
		bindingService.setKeyFilterEnabled(enable);
	}

	/**
	 * copy
	 */
	public void copy()
	{
		if (this._browser != null)
		{
			final Object result = this._browser.evaluate("return copy();");
			
			// only copy when we have text
			if (result != null && result instanceof String && ((String) result).length() > 0)
			{
				UIJob job = new UIJob("Copy from Terminal")
				{
					public IStatus runInUIThread(IProgressMonitor monitor)
					{
						Display display = PlatformUI.getWorkbench().getDisplay();
						Clipboard clipboard = new Clipboard(display);
						
						clipboard.setContents(new Object[] { result }, new Transfer[] { TextTransfer.getInstance() });
		
						return Status.OK_STATUS;
					}
				};
				job.setSystem(true);
				job.setPriority(Job.INTERACTIVE);
				job.schedule();
			}
		}
	}
	
	/**
	 * createControl
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent)
	{
		// create browser control
		this._browser = new Browser(parent, SWT.NONE);

		this.makeActions();
		this.addListeners();
	}

	/**
	 * dispose
	 */
	public void dispose()
	{
		if (this._windowListener != null)
		{
			PlatformUI.getWorkbench().removeWindowListener(this._windowListener);
			this._windowListener = null;
		}

		if (this._partListener != null)
		{
			this._owningPart.getSite().getWorkbenchWindow().getPartService().removePartListener(this._partListener);
			this._partListener = null;
		}

		if (this._themeChangeListener != null)
		{
			new InstanceScope().getNode(CommonEditorPlugin.PLUGIN_ID).removePreferenceChangeListener(
					_themeChangeListener);
			this._themeChangeListener = null;
		}

		if (this._fontChangeListener != null)
		{
			JFaceResources.getFontRegistry().removeListener(this._fontChangeListener);
			this._fontChangeListener = null;
		}

		try
		{
			if (this._browser != null && this._browser.isDisposed() == false)
			{
				if (this._keyListener != null)
				{
					this._browser.removeKeyListener(this._keyListener);
					this._keyListener = null;
				}
				
				this._browser.dispose();
				this._browser = null;
			}
			else
			{
				this._keyListener = null;
			}
		}
		catch (Exception e)
		{
		}

		// Make sure to stop the redtty
		TerminalServer.getInstance().removeProcess(_id);
	}

	/**
	 * fillContextMenu
	 * 
	 * @param manager
	 */
	public void fillContextMenu(IMenuManager manager)
	{
		// set copy/paste enabled states
		copy.setEnabled(this.hasSelection());
		paste.setEnabled(ClipboardUtil.hasTextContent());
		
		manager.add(copy);
		manager.add(paste);
		manager.add(new Separator());
		manager.add(selectAll);
	}
	
	/**
	 * getControl
	 * 
	 * @return
	 */
	public Control getControl()
	{
		return this._browser;
	}

	/**
	 * getId
	 * 
	 * @return
	 */
	public String getId()
	{
		return _id;
	}

	/**
	 * getStartingDirectory
	 * 
	 * @return
	 */
	private String getStartingDirectory()
	{
		return this._startingDirectory;
	}

	/**
	 * hasSelection
	 * 
	 * @return
	 */
	public boolean hasSelection()
	{
		Object jsResult = this._browser.evaluate("return hasSelection();");
		boolean result = false;
		
		if (jsResult instanceof Boolean)
		{
			result = ((Boolean) jsResult).booleanValue();
		}
		
		return result;
	}
	
	private void makeActions()
	{
		// copy action
		copy = new Action()
		{
			public void run()
			{
				copy();
			}
		};
		copy.setText("Copy");
		copy.setToolTipText("Copy the selected text to the clipboard");
		copy.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
				ISharedImages.IMG_TOOL_COPY));
		copy.setAccelerator(SWTKeySupport.convertKeyStrokeToAccelerator(TerminalBrowser.COPY_STROKE));

		// paste action
		paste = new Action()
		{
			public void run()
			{
				paste();
			}
		};
		paste.setText("Paste");
		paste.setToolTipText("Paste clipboard text into the terminal");
		paste.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
				ISharedImages.IMG_TOOL_PASTE));
		paste.setAccelerator(SWTKeySupport.convertKeyStrokeToAccelerator(TerminalBrowser.PASTE_STROKE));
		
		// select-all action
		selectAll = new Action()
		{
			public void run()
			{
				selectAll();
			}
		};
		selectAll.setText("Select All");
		selectAll.setToolTipText("Select all text in the terminal");
		selectAll.setAccelerator(SWTKeySupport.convertKeyStrokeToAccelerator(TerminalBrowser.SELECT_ALL_STROKE));
	}
	
	/**
	 * paste
	 */
	public void paste()
	{
		if (this._browser != null)
		{
			UIJob job = new UIJob("Paste into Terminal")
			{
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					Display display = PlatformUI.getWorkbench().getDisplay();
					Clipboard clipboard = new Clipboard(display);
					String text = (String) clipboard.getContents(TextTransfer.getInstance());
	
					if (text != null && text.length() > 0)
					{
						ProcessWrapper process = TerminalServer.getInstance().getProcess(_id);
	
						if (process != null)
						{
							// send text
							process.sendText(text);
							
							// force update now
							update();
						}
					}
	
					return Status.OK_STATUS;
				}
			};
			job.setSystem(true);
			job.setPriority(Job.INTERACTIVE);
			job.schedule();
		}
	}

	/**
	 * selectAll
	 */
	public void selectAll()
	{
		if (this._browser != null)
		{
			this._browser.execute("selectAll();");
		}
	}
	
	/**
	 * setFocus
	 */
	public void setFocus()
	{
		if (this._browser != null)
		{
			this._browser.setFocus();
			this._browser.execute("window.focus();");
		}
	}

	/**
	 * setUrl
	 * 
	 * @param string
	 */
	private void setUrl(String string)
	{
		if (this._browser != null)
		{
			this._browser.setUrl(string);
		}
	}
	
	/**
	 * update
	 */
	protected void update()
	{
		this._browser.execute("getInput();");
	}
}
