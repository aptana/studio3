/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.preview;

import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IReusableEditor;
import org.eclipse.ui.IShowEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.aptana.browser.WebBrowserViewer;
import com.aptana.core.util.PlatformUtil;
import com.aptana.preview.internal.Messages;

/**
 * @author Max Stepanov
 */
public class PreviewEditorPart extends EditorPart implements IReusableEditor, IShowEditorInput
{

	public static final String EDITOR_ID = "com.aptana.preview.editor"; //$NON-NLS-1$

	private WebBrowserViewer webBrowser;
	private Browser nativeBrowser;
	private int progressWorked;
	private String initialURL;
	private Image image;
	private boolean disposed;

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime. IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor)
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		setSite(site);
		super.setInput(input);
		showEditorInput();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose()
	{
		if (image != null && !image.isDisposed())
		{
			image.dispose();
			image = null;
		}
		super.dispose();
		disposed = true;
	}

	public boolean isDisposed()
	{
		return disposed;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets .Composite)
	 */
	@Override
	public void createPartControl(Composite parent)
	{
		int style = WebBrowserViewer.NAVIGATION_BAR;
		if (PlatformUtil.isLinux())
		{
			// use Eclipse browser on Linux
			nativeBrowser = new Browser(parent, style);
			nativeBrowser.addProgressListener(new ProgressListener()
			{
				public void changed(ProgressEvent event)
				{
					if (event.total == 0)
					{
						return;
					}
					if (event.current == 0)
					{
						IProgressMonitor progressMonitor = getStatusBarProgressMonitor();
						progressMonitor.done();
						progressMonitor.beginTask("", event.total); //$NON-NLS-1$
						progressWorked = 0;
					}
					if (progressWorked < event.current)
					{
						getStatusBarProgressMonitor().worked(event.current - progressWorked);
						progressWorked = event.current;
					}
				}

				public void completed(ProgressEvent event)
				{
					getStatusBarProgressMonitor().done();
				}
			});
			nativeBrowser.addTitleListener(new TitleListener()
			{
				public void changed(TitleEvent event)
				{
					setTitleToolTip(event.title);
				}
			});
			nativeBrowser.setUrl(initialURL);
		}
		else
		{
			webBrowser = createBrowser(parent, style);
			webBrowser.addProgressListener(new ProgressListener()
			{
				public void changed(ProgressEvent event)
				{
					if (event.total == 0)
					{
						return;
					}
					if (event.current == 0)
					{
						IProgressMonitor progressMonitor = getStatusBarProgressMonitor();
						progressMonitor.done();
						progressMonitor.beginTask("", event.total); //$NON-NLS-1$
						progressWorked = 0;
					}
					if (progressWorked < event.current)
					{
						getStatusBarProgressMonitor().worked(event.current - progressWorked);
						progressWorked = event.current;
					}
				}

				public void completed(ProgressEvent event)
				{
					getStatusBarProgressMonitor().done();
				}
			});
			webBrowser.addTitleListener(new TitleListener()
			{
				public void changed(TitleEvent event)
				{
					setTitleToolTip(event.title);
				}
			});
			webBrowser.setURL(initialURL);
		}
	}

	protected WebBrowserViewer createBrowser(Composite parent, int style)
	{
		return new WebBrowserViewer(parent, style);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus()
	{
		if (webBrowser != null)
		{
			webBrowser.setFocus();
		}
		else if (nativeBrowser != null)
		{
			nativeBrowser.setFocus();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void setInput(IEditorInput input)
	{
		super.setInput(input);
		showEditorInput();
		firePropertyChange(PROP_INPUT);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IShowEditorInput#showEditorInput(org.eclipse.ui.IEditorInput )
	 */
	public void showEditorInput(IEditorInput editorInput)
	{
		setInput(editorInput);
	}

	private void showEditorInput()
	{
		PreviewEditorInput pei = getPreviewEditorInput();
		if (pei != null)
		{
			initialURL = null;
			URL url = pei.getURL();
			if (url != null)
				initialURL = url.toExternalForm();
			if (webBrowser != null)
			{
				webBrowser.setURL(initialURL);
			}
			else if (nativeBrowser != null)
			{
				nativeBrowser.setUrl(initialURL);
			}

			setPartName(getEditorInputName());
			setTitleToolTip(pei.getToolTipText());
			Image oldImage = image;
			ImageDescriptor id = pei.getImageDescriptor();
			image = id.createImage();

			setTitleImage(image);
			if (oldImage != null && !oldImage.isDisposed())
				oldImage.dispose();
		}
	}

	protected String getEditorInputName()
	{
		PreviewEditorInput pei = getPreviewEditorInput();
		if (pei != null)
		{
			return MessageFormat.format(Messages.PreviewEditorPart_Title, pei.getName());
		}
		return null;
	}

	private IProgressMonitor getStatusBarProgressMonitor()
	{
		IStatusLineManager statusLineManager = getEditorSite().getActionBars().getStatusLineManager();
		return statusLineManager.getProgressMonitor();
	}

	public boolean close()
	{
		final boolean[] result = new boolean[1];
		Display.getDefault().asyncExec(new Runnable()
		{
			public void run()
			{
				result[0] = getEditorSite().getPage().closeEditor(PreviewEditorPart.this, false);
			}
		});
		return result[0];
	}

	protected PreviewEditorInput getPreviewEditorInput()
	{
		IEditorInput input = getEditorInput();
		if (input instanceof PreviewEditorInput)
			return (PreviewEditorInput) input;
		return null;
	}

}
