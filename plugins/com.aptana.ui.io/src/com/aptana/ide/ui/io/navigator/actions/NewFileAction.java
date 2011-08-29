/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator.actions;

import java.io.InputStream;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

import com.aptana.core.util.StringUtil;
import com.aptana.scripting.model.TemplateElement;
import com.aptana.ui.util.UIUtils;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class NewFileAction extends BaseSelectionListenerAction
{

	private IAdaptable fSelectedElement;
	private String fInitialFilename;
	private TemplateElement fTemplate;

	public NewFileAction()
	{
		this(StringUtil.EMPTY);
	}

	public NewFileAction(String initialName)
	{
		this(initialName, null);
	}

	public NewFileAction(String initialName, TemplateElement template)
	{
		super(Messages.NewFileAction_Text);
		fInitialFilename = initialName;
		fTemplate = template;

		setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FILE));
		setToolTipText(Messages.NewFileAction_ToolTip);
	}

	public void run()
	{
		if (fSelectedElement == null)
		{
			return;
		}

		NewExternalFileWizard wizard = new NewExternalFileWizard(fInitialFilename, getInitialContents(),
				fSelectedElement, fTemplate);
		WizardDialog dialog = new WizardDialog(UIUtils.getActiveShell(), wizard);
		dialog.open();
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection)
	{
		fSelectedElement = null;

		if (selection != null && !selection.isEmpty())
		{
			Object element = selection.getFirstElement();
			if (element instanceof IAdaptable)
			{
				fSelectedElement = (IAdaptable) element;
			}
		}

		return super.updateSelection(selection) && fSelectedElement != null;
	}

	protected InputStream getInitialContents()
	{
		return null;
	}
}
