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
package com.aptana.workbench.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.ISources;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.IEvaluationService;

import com.aptana.editor.common.internal.scripting.NewTemplateFileWizard;
import com.aptana.scripting.model.AbstractElement;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.TemplateElement;
import com.aptana.scripting.model.filters.IModelFilter;
import com.aptana.ui.util.UIUtils;

/**
 * @author Michael Xia
 */
public class NewFileTemplateMenuContributor extends ContributionItem
{

	public NewFileTemplateMenuContributor()
	{
	}

	public NewFileTemplateMenuContributor(String id)
	{
		super(id);
	}

	@Override
	public boolean isDynamic()
	{
		return true;
	}

	@Override
	public void fill(Menu menu, int index)
	{
		// finds the current selection
		IStructuredSelection selection = null;
		IEvaluationService evaluationService = (IEvaluationService) PlatformUI.getWorkbench().getService(
				IEvaluationService.class);
		if (evaluationService != null)
		{
			IEvaluationContext currentState = evaluationService.getCurrentState();
			Object variable = currentState.getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);
			if (variable instanceof IStructuredSelection)
			{
				selection = (IStructuredSelection) variable;
			}
		}
		final IStructuredSelection currentSelection = (selection == null) ? StructuredSelection.EMPTY : selection;

		// constructs the menus
		Map<String, List<TemplateElement>> templatesByBundle = getNewFileTemplates();
		Set<String> bundles = templatesByBundle.keySet();
		List<TemplateElement> templates;
		// first level shows the bundles which have file templates defined
		for (String bundle : bundles)
		{
			MenuItem bundleItem = new MenuItem(menu, SWT.CASCADE);
			bundleItem.setText(bundle);

			Menu bundleMenu = new Menu(menu);
			bundleItem.setMenu(bundleMenu);

			// second level shows the templates for each bundle
			templates = templatesByBundle.get(bundle);
			for (final TemplateElement template : templates)
			{
				MenuItem templateItem = new MenuItem(bundleMenu, SWT.PUSH);
				templateItem.setText(template.getDisplayName());
				templateItem.addSelectionListener(new SelectionAdapter()
				{

					@Override
					public void widgetSelected(SelectionEvent e)
					{
						NewTemplateFileWizard wizard = new NewTemplateFileWizard(template);
						wizard.init(PlatformUI.getWorkbench(), currentSelection);
						WizardDialog dialog = new WizardDialog(UIUtils.getActiveShell(), wizard);
						dialog.open();
					}
				});
			}
		}
	}

	private Map<String, List<TemplateElement>> getNewFileTemplates()
	{
		Map<String, List<TemplateElement>> templatesByBundle = new HashMap<String, List<TemplateElement>>();
		List<CommandElement> commands = BundleManager.getInstance().getExecutableCommands(new IModelFilter()
		{

			public boolean include(AbstractElement element)
			{
				if (element instanceof TemplateElement)
				{
					TemplateElement te = (TemplateElement) element;
					return te.getFiletype() != null && te.getOwningBundle() != null;
				}
				return false;
			}
		});
		if (commands != null)
		{
			String bundleName;
			List<TemplateElement> templates;
			for (CommandElement command : commands)
			{
				bundleName = command.getOwningBundle().getDisplayName();
				templates = templatesByBundle.get(bundleName);
				if (templates == null)
				{
					templates = new ArrayList<TemplateElement>();
					templatesByBundle.put(bundleName, templates);
				}
				templates.add((TemplateElement) command);
			}
		}
		return templatesByBundle;
	}
}
