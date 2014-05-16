/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable staticFieldNamingConvention

package com.aptana.ide.ui.io.navigator.actions;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeMatcher;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IFileEditorMapping;
import org.eclipse.ui.ISources;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.services.IEvaluationService;
import org.eclipse.ui.wizards.newresource.BasicNewFileResourceWizard;

import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.editor.common.internal.scripting.NewTemplateFileWizard;
import com.aptana.scripting.model.AbstractElement;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.BundlePrecedence;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.TemplateElement;
import com.aptana.scripting.model.filters.IModelFilter;
import com.aptana.ui.util.UIUtils;

/**
 * @author Michael Xia
 */
public class NewFileTemplateMenuContributor extends ContributionItem
{

	// @formatter:off
	private static final String[] APTANA_EDITOR_PREFIX = new String[] {
			"com.aptana.editor.", //$NON-NLS-1$
			"org.python.pydev.editor." //$NON-NLS-1$
			};
	// @formatter:on

	// @formatter:off
	private static final Set<String> FILTERED_EDITORS = CollectionsUtil.newSet(
			"com.aptana.editor.xml.alloy", //$NON-NLS-1$
			"com.aptana.editor.dtd", //$NON-NLS-1$
			"com.aptana.editor.svg" //$NON-NLS-1$
			);
	// @formatter:on

	private static Map<String, String> aptanaEditors;

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
		if (aptanaEditors == null)
		{
			aptanaEditors = getAptanaEditorFiletypeMap();
		}

		List<String> editors = new ArrayList<String>(aptanaEditors.keySet());
		Collections.sort(editors, new Comparator<String>()
		{

			public int compare(String o1, String o2)
			{
				return o1.compareToIgnoreCase(o2);
			}
		});

		// constructs the menus
		final Map<String, List<TemplateElement>> templatesByBundle = getNewFileTemplates();
		for (final String filetype : editors)
		{
			List<TemplateElement> templates = templatesByBundle.get(filetype);
			boolean hasTemplates = !CollectionsUtil.isEmpty(templates);
			MenuItem editorItem = new MenuItem(menu, hasTemplates ? SWT.CASCADE : SWT.PUSH);
			editorItem.setText(filetype);

			if (hasTemplates)
			{
				Menu editorMenu = new Menu(menu);
				editorItem.setMenu(editorMenu);

				// sorts by precedence first
				Collections.sort(templates, new Comparator<TemplateElement>()
				{

					public int compare(TemplateElement e1, TemplateElement e2)
					{
						BundlePrecedence p1 = e1.getOwningBundle().getBundlePrecedence();
						BundlePrecedence p2 = e2.getOwningBundle().getBundlePrecedence();
						return p1.compareTo(p2);
					}
				});

				boolean userLevel = true;
				int size = templates.size();
				for (int i = 0; i < size; ++i)
				{
					final TemplateElement template = templates.get(i);
					if (userLevel && template.getOwningBundle().getBundlePrecedence() != BundlePrecedence.USER)
					{
						userLevel = false;
						if (i > 0)
						{
							// adds a separator between the user templates and system templates
							new MenuItem(editorMenu, SWT.SEPARATOR);
						}
					}
					MenuItem templateItem = new MenuItem(editorMenu, SWT.PUSH);
					templateItem.setText(template.getDisplayName());
					templateItem.addSelectionListener(new SelectionAdapter()
					{

						@Override
						public void widgetSelected(SelectionEvent e)
						{
							createNewFileFromTemplate(template);
						}
					});
				}
				// adds a separator if there are built-in templates
				new MenuItem(editorMenu, SWT.SEPARATOR);

				// Blank file for filetype
				String fileExtension = templates.get(0).getFiletype();
				// strips the leading *. if there is one
				int dotIndex = fileExtension.lastIndexOf('.');
				if (dotIndex > -1)
				{
					fileExtension = fileExtension.substring(dotIndex + 1);
				}
				createBlankFileMenu(editorMenu, filetype, fileExtension);
			}
			else
			{
				// Make the top level filetype entry create the blank file!
				editorItem.addSelectionListener(new SelectionAdapter()
				{

					@Override
					public void widgetSelected(SelectionEvent e)
					{
						createNewBlankFile(filetype, aptanaEditors.get(filetype));
					}
				});
			}
		}
	}

	private MenuItem createBlankFileMenu(Menu parent, final String editorType, final String fileExtension)
	{
		MenuItem item = new MenuItem(parent, SWT.PUSH);
		item.setText(Messages.NewFileTemplateMenuContributor_LBL_BlankFile);
		item.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				createNewBlankFile(editorType, fileExtension);
			}
		});
		return item;
	}

	protected void createNewFileFromTemplate(final TemplateElement template)
	{
		IStructuredSelection selection = getActiveSelection();
		if (!selection.isEmpty())
		{
			Object element = selection.getFirstElement();
			if (element instanceof IAdaptable)
			{
				IFileStore fileStore = (IFileStore) ((IAdaptable) element).getAdapter(IFileStore.class);
				if (fileStore != null)
				{
					// this is a non-workspace selection
					String filetype = template.getFiletype();
					// strips the leading * before . if there is one
					int index = filetype.lastIndexOf('.');
					if (index > -1)
					{
						filetype = filetype.substring(index);
					}
					NewFileAction action = new NewFileAction("new_file" + filetype, template); //$NON-NLS-1$
					action.updateSelection(selection);
					action.run();
					return;
				}
			}
		}

		NewTemplateFileWizard wizard = new NewTemplateFileWizard(template);
		wizard.init(PlatformUI.getWorkbench(), selection);
		WizardDialog dialog = new WizardDialog(UIUtils.getActiveShell(), wizard);
		dialog.open();
	}

	/**
	 * @param editorType
	 * @param fileExtension
	 */
	protected void createNewBlankFile(String editorType, String fileExtension)
	{
		final String initialFileName = "new_file." + fileExtension; //$NON-NLS-1$

		IStructuredSelection selection = getActiveSelection();
		if (!selection.isEmpty())
		{
			Object element = selection.getFirstElement();
			if (element instanceof IAdaptable)
			{
				IFileStore fileStore = (IFileStore) ((IAdaptable) element).getAdapter(IFileStore.class);
				if (fileStore != null)
				{
					// this is a non-workspace selection
					NewFileAction action = new NewFileAction(initialFileName)
					{

						@Override
						protected InputStream getInitialContents()
						{
							// empty content
							return new ByteArrayInputStream(ArrayUtil.NO_BYTES);
						}
					};
					action.updateSelection(selection);
					action.run();
					return;
				}
			}
		}

		BasicNewFileResourceWizard wizard = new BasicNewFileResourceWizard()
		{

			@Override
			public void addPages()
			{
				super.addPages();
				((WizardNewFileCreationPage) getPages()[0]).setFileName(initialFileName);
			}
		};
		wizard.init(PlatformUI.getWorkbench(), selection);
		WizardDialog dialog = new WizardDialog(UIUtils.getActiveShell(), wizard);
		dialog.open();
	}

	private static Map<String, String> getAptanaEditorFiletypeMap()
	{
		// finds the editors we contribute and the file extension each maps to
		Map<String, String> editorMap = new TreeMap<String, String>();
		IFileEditorMapping[] mappings = getFileEditorMappings();
		final IContentTypeMatcher matcher = getContentTypeMatcher();
		for (IFileEditorMapping mapping : mappings)
		{
			IEditorDescriptor[] editors = mapping.getEditors();
			String extension = getExtension(matcher, mapping);
			for (IEditorDescriptor editor : editors)
			{
				String editorId = editor.getId();
				if (FILTERED_EDITORS.contains(editorId))
				{
					continue;
				}

				for (String prefix : APTANA_EDITOR_PREFIX)
				{
					if (editorId.startsWith(prefix))
					{
						String name = editor.getLabel();
						// grabs the first word as it will be used to link the editor type with the bundle's name
						// (e.g. HTML Editor -> HTML)
						name = (new StringTokenizer(name)).nextToken();
						if (!editorMap.containsKey(name))
						{
							editorMap.put(name, extension);
						}
						break;
					}
				}
			}
		}
		return editorMap;
	}

	protected static String getExtension(IContentTypeMatcher matcher, IFileEditorMapping mapping)
	{
		String extension = mapping.getExtension();
		if (matcher != null)
		{
			IContentType type = matcher.findContentTypeFor("new_file." + extension); //$NON-NLS-1$
			if (type != null)
			{
				String[] extensions = type.getFileSpecs(IContentType.FILE_EXTENSION_SPEC);
				if (!ArrayUtil.isEmpty(extensions))
				{
					return extensions[0];
				}
			}
		}
		return extension;
	}

	protected static IFileEditorMapping[] getFileEditorMappings()
	{
		return PlatformUI.getWorkbench().getEditorRegistry().getFileEditorMappings();
	}

	protected static IContentTypeMatcher getContentTypeMatcher()
	{
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		if (!ArrayUtil.isEmpty(projects))
		{
			try
			{
				return projects[0].getContentTypeMatcher();
			}
			catch (CoreException e)
			{
				// ignore
			}
		}
		return null;
	}

	private static IStructuredSelection getActiveSelection()
	{
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
		return (selection == null) ? StructuredSelection.EMPTY : selection;
	}

	private Map<String, List<TemplateElement>> getNewFileTemplates()
	{
		List<CommandElement> commands = getBundleManager().getExecutableCommands(new IModelFilter()
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
		if (CollectionsUtil.isEmpty(commands))
		{
			return Collections.emptyMap();
		}

		Map<String, List<TemplateElement>> templatesByBundle = new TreeMap<String, List<TemplateElement>>();
		for (CommandElement command : commands)
		{
			String bundleName = command.getOwningBundle().getDisplayName();
			List<TemplateElement> templates = templatesByBundle.get(bundleName);
			if (templates == null)
			{
				templates = new ArrayList<TemplateElement>(1);
				templatesByBundle.put(bundleName, templates);
			}
			templates.add((TemplateElement) command);
		}
		return templatesByBundle;
	}

	protected BundleManager getBundleManager()
	{
		return BundleManager.getInstance();
	}
}
