package com.aptana.terminal;

import java.util.UUID;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
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
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.part.WorkbenchPart;
import org.eclipse.ui.progress.UIJob;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.theme.IThemeManager;
import com.aptana.terminal.server.ProcessWrapper;
import com.aptana.terminal.server.TerminalServer;

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
	private IPreferenceChangeListener _themeChangeListener;
	private IPropertyChangeListener _fontChangeListener;
	private KeyListener _keyListener;

	static
	{
		boolean isMac = Platform.getOS().equals(Platform.OS_MACOSX);
		int modifiers = SWT.SHIFT | ((isMac) ? SWT.COMMAND : SWT.CONTROL);

		COPY_STROKE = KeyStroke.getInstance(modifiers, 'C');
		PASTE_STROKE = KeyStroke.getInstance(modifiers, 'V');
		SELECT_ALL_STROKE = KeyStroke.getInstance(modifiers, 'A');
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
	 * addFocusListener
	 */
	private void addFocusListener()
	{
		// create focus listener so we can enable/disable key bindings
		final IBindingService bindingService = (IBindingService) this._owningPart.getSite().getService(
				IBindingService.class);

		this._browser.addFocusListener(new FocusListener()
		{
			public void focusGained(FocusEvent e)
			{
				bindingService.setKeyFilterEnabled(false);
			}

			public void focusLost(FocusEvent e)
			{
				bindingService.setKeyFilterEnabled(true);
			}
		});
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
	 * addListeners
	 */
	private void addListeners()
	{
		this.addFocusListener();
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

		this.addListeners();
	}

	/**
	 * dispose
	 */
	public void dispose()
	{
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

		if (this._keyListener != null)
		{
			this._browser.removeKeyListener(this._keyListener);
			this._keyListener = null;
		}

		try
		{
			if (this._browser != null)
			{
				this._browser.dispose();
				this._browser = null;
			}
		}
		catch (Exception e)
		{
		}
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
