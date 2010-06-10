/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
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
package com.aptana.debug.internal.ui.actions;

import java.lang.reflect.Constructor;

import org.eclipse.debug.core.model.IExpression;
import org.eclipse.debug.core.model.IWatchExpressionResult;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.information.InformationPresenter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;

import com.aptana.debug.core.model.JSInspectExpression;
import com.aptana.debug.ui.DebugUiPlugin;
import com.aptana.ide.editors.unified.IUnifiedEditor;

/**
 * @author Max Stepanov
 */
public class PopupInspectAction extends InspectAction implements IInformationProvider
{
	/**
	 * ACTION_DEFININIITION_ID
	 */
	public static final String ACTION_DEFININIITION_ID = "com.aptana.debug.ui.commands.Inspect"; //$NON-NLS-1$

	private ITextViewer viewer;
	private JSInspectExpression expression;
	private InformationPresenter fInformationPresenter;

	/**
	 * see org.eclipse.jface.text.information.IInformationProvider#getInformation(org.eclipse.jface.text.ITextViewer,
	 *      org.eclipse.jface.text.IRegion)
	 * @param textViewer 
	 * @param subject 
	 * @return String
	 */
	public String getInformation(ITextViewer textViewer, IRegion subject)
	{
		// the ExpressionInformationControlAdapter was constructed with everything that it needs
		// returning null would result in popup not being displayed
		return "not null"; //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.jface.text.information.IInformationProvider#getSubject(org.eclipse.jface.text.ITextViewer, int)
	 */
	public IRegion getSubject(ITextViewer textViewer, int offset)
	{
		return getRegion();
	}

	/**
	 * showPopup
	 * 
	 * @param result
	 */
	protected void showPopup(final IWatchExpressionResult result)
	{
		try
		{
			Class.forName("org.eclipse.debug.ui.InspectPopupDialog"); //$NON-NLS-1$
			showPopup32(result);
		}
		catch (ClassNotFoundException e)
		{
			showPopup31(result);
		}
	}

	private void showPopup32(IWatchExpressionResult result)
	{
		expression = new JSInspectExpression(result);
		Window displayPopup = null;

		/*
		 * Compatibility replacement for: new org.eclipse.debug.ui.InspectPopupDialog(getShell(),
		 * getPopupAnchor(viewer), ACTION_DEFININIITION_ID, expression);
		 */
		try
		{
			Class clazz = Class.forName("org.eclipse.debug.ui.InspectPopupDialog"); //$NON-NLS-1$
			Constructor constructor = clazz.getConstructor(new Class[] { Shell.class, Point.class, String.class,
					IExpression.class });
			displayPopup = (Window) constructor.newInstance(new Object[] { getShell(), getPopupAnchor(viewer),
					ACTION_DEFININIITION_ID, expression });
		}
		catch (Exception e)
		{
			DebugUiPlugin.log(e);
		}
		if (displayPopup != null)
		{
			displayPopup.open();
		}
	}

	private void showPopup31(final IWatchExpressionResult result)
	{
		final InformationPresenter infoPresenter = new InformationPresenter(new IInformationControlCreator()
		{
			public IInformationControl createInformationControl(Shell parent)
			{
				IWorkbenchPage page = DebugUiPlugin.getActivePage();
				expression = new JSInspectExpression(result);

				IInformationControl control = null;
				/*
				 * Compatibility replacement for: new
				 * org.eclipse.debug.internal.ui.views.expression.ExpressionInformationControl(page, expression,
				 * ACTION_DEFININIITION_ID);
				 */
				try
				{
					Class clazz = Class
							.forName("org.eclipse.debug.internal.ui.views.expression.ExpressionInformationControl"); //$NON-NLS-1$
					Constructor constructor = clazz.getConstructor(new Class[] { IWorkbenchPage.class,
							IExpression.class, String.class });
					control = (IInformationControl) constructor.newInstance(new Object[] { page, expression,
							ACTION_DEFININIITION_ID });
				}
				catch (Exception e)
				{
					DebugUiPlugin.log(e);
				}
				if (control != null)
				{
					control.addDisposeListener(new DisposeListener()
					{
						public void widgetDisposed(DisposeEvent e)
						{
							getInformationPresenter().uninstall();
						}
					});
				}
				return control;
			}
		});

		setInformationPresenter(infoPresenter);

		DebugUiPlugin.getStandardDisplay().asyncExec(new Runnable()
		{
			public void run()
			{
				if (viewer != null)
				{
					Point p = viewer.getSelectedRange();
					IDocument doc = viewer.getDocument();
					try
					{
						String contentType = TextUtilities.getContentType(doc, infoPresenter.getDocumentPartitioning(),
								p.x, true);
						infoPresenter.setInformationProvider(PopupInspectAction.this, contentType);
						infoPresenter.install(viewer);
						infoPresenter.showInformation();
					}
					catch (BadLocationException e)
					{
						return;
					}
					finally
					{
						viewer = null;
					}
				}
			}
		});
	}

	/**
	 * @see com.aptana.debug.internal.ui.actions.InspectAction#displayResult(org.eclipse.debug.core.model.IWatchExpressionResult)
	 */
	protected void displayResult(final IWatchExpressionResult result)
	{
		IWorkbenchPart part = getTargetPart();
		viewer = (ITextViewer) part.getAdapter(ITextViewer.class);
		if (viewer == null)
		{
			if (part instanceof IUnifiedEditor)
			{
				viewer = ((IUnifiedEditor) part).getViewer();
			}
		}
		if (viewer == null)
		{
			super.displayResult(result);
		}
		else
		{
			showPopup(result);
		}
	}

	private IWorkbenchPart getTargetPart()
	{
		// TODO
		return DebugUiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
	}

	private InformationPresenter getInformationPresenter()
	{
		return fInformationPresenter;
	}

	private void setInformationPresenter(InformationPresenter informationPresenter)
	{
		fInformationPresenter = informationPresenter;
	}

	/**
	 * getRegion
	 * 
	 * @return IRegion
	 */
	protected IRegion getRegion()
	{
		Point point = viewer.getSelectedRange();
		return new Region(point.x, point.y);
	}

	private Shell getShell()
	{
		if (getTargetPart() != null)
		{
			return getTargetPart().getSite().getShell();
		}
		return DebugUiPlugin.getActiveWorkbenchShell();
	}

	/**
	 * Computes an anchor point for a popup dialog on top of a text viewer.
	 * 
	 * @param viewer
	 * @return desired anchor point
	 */
	private static Point getPopupAnchor(ITextViewer viewer)
	{
		StyledText textWidget = viewer.getTextWidget();
		Point docRange = textWidget.getSelectionRange();
		int midOffset = docRange.x + (docRange.y / 2);
		Point point = textWidget.getLocationAtOffset(midOffset);
		point = textWidget.toDisplay(point);

		GC gc = new GC(textWidget);
		gc.setFont(textWidget.getFont());
		int height = gc.getFontMetrics().getHeight();
		gc.dispose();
		point.y += height;
		return point;
	}
}
