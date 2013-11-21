/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects.wizards;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.RowDataFactory;
import org.eclipse.jface.layout.RowLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.projects.templates.IProjectTemplate;
import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.projects.ProjectsPlugin;
import com.aptana.projects.internal.wizards.Messages;
import com.aptana.projects.templates.IDefaultProjectTemplate;
import com.aptana.projects.templates.ProjectTemplatesManager;
import com.aptana.ui.util.SWTUtils;
import com.aptana.ui.util.UIUtils;
import com.aptana.ui.widgets.StepIndicatorComposite;

/**
 * Wizard page used to select a project template for new projects
 * 
 * @author Nam Le <nle@appcelerator.com>
 */
public class ProjectTemplateSelectionPage extends WizardPage implements IStepIndicatorWizardPage
{
	private static final int TEMPLATES_COMPOSITE_WIDTH = 450;
	public static final String COMMAND_PROJECT_FROM_TEMPLATE_PROJECT_TEMPLATE_NAME = "projectTemplateId"; //$NON-NLS-1$
	public static final String COMMAND_PROJECT_FROM_TEMPLATE_NEW_WIZARD_ID = "newWizardId"; //$NON-NLS-1$

	private static final int IMAGE_SIZE = 48;

	private TableViewer tagsListViewer;
	private Composite templatesListComposite;
	private Label previewImage;
	private Label previewLabel;
	private Label previewDescription;

	private IProjectTemplate[] fTemplates;
	private IProjectTemplate fSelectedTemplate;

	private static ImageDescriptor wizardDesc = ProjectsPlugin.getImageDescriptor("/icons/project_template_blank.png"); //$NON-NLS-1$
	private Image defaultTemplateImage = null;
	private Map<IProjectTemplate, Image> templateImages;
	private Map<Composite, IProjectTemplate> templateControlMap;
	private Map<String, List<IProjectTemplate>> templateTagsMap;

	protected StepIndicatorComposite stepIndicatorComposite;
	protected String[] stepNames;

	private Composite templatesDescriptionComp;

	private ISelectionChangedListener tagSelectionChangedListener = new ISelectionChangedListener()
	{

		public void selectionChanged(SelectionChangedEvent event)
		{
			ISelection selection = tagsListViewer.getSelection();
			if (!selection.isEmpty() && selection instanceof IStructuredSelection)
			{
				String tag = (String) ((IStructuredSelection) selection).getFirstElement();
				setSelectedTemplate(tag, templateTagsMap.get(tag).get(0));
			}
		}
	};

	public ProjectTemplateSelectionPage(String pageName, List<IProjectTemplate> templates)
	{
		super(pageName);

		if (templates == null)
		{
			fTemplates = new IProjectTemplate[0];
		}
		else
		{
			List<IProjectTemplate> modifiableTemplates = new ArrayList<IProjectTemplate>(templates);
			// sorts the list by priority first and then alphabetically
			Collections.sort(modifiableTemplates, new Comparator<IProjectTemplate>()
			{

				public int compare(IProjectTemplate o1, IProjectTemplate o2)
				{
					int result = o1.getPriority() - o2.getPriority();
					return (result == 0) ? o1.getDisplayName().compareToIgnoreCase(o2.getDisplayName()) : result;
				}
			});
			fTemplates = templates.toArray(new IProjectTemplate[templates.size()]);
		}
		setTitle(Messages.ProjectTemplateSelectionPage_Title);
		setDescription(Messages.ProjectTemplateSelectionPage_Description);
		templateImages = new HashMap<IProjectTemplate, Image>();
		templateControlMap = new LinkedHashMap<Composite, IProjectTemplate>();
		templateTagsMap = new HashMap<String, List<IProjectTemplate>>();
		populateTagsMap();
	}

	public IProjectTemplate getSelectedTemplate()
	{
		return fSelectedTemplate;
	}

	public void createControl(Composite parent)
	{
		defaultTemplateImage = wizardDesc.createImage();
		parent.addDisposeListener(new DisposeListener()
		{

			public void widgetDisposed(DisposeEvent e)
			{
				if (defaultTemplateImage != null)
				{
					defaultTemplateImage.dispose();
					defaultTemplateImage = null;
				}
				if (templateImages != null)
				{
					for (Image image : templateImages.values())
					{
						if (image != null && !image.isDisposed())
						{
							image.dispose();
						}
					}
				}
				templateImages = null;
			}
		});

		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(GridLayoutFactory.fillDefaults().spacing(0, 10).create());
		main.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		stepIndicatorComposite = new StepIndicatorComposite(main, stepNames);
		stepIndicatorComposite.setSelection(getStepName());

		Label label = new Label(main, SWT.NONE);
		label.setText(Messages.ProjectTemplateSelectionPage_AvailableTemplates_TXT);
		label.setLayoutData(GridDataFactory.swtDefaults().create());

		Composite templateList = createTemplatesList(main);
		templateList.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		// go through each tag and auto-select the first default template found; otherwise selects the first template in
		// the first tag
		if (!ArrayUtil.isEmpty(fTemplates))
		{
			boolean foundDefault = false;
			TableItem[] items = tagsListViewer.getTable().getItems();
			for (TableItem item : items)
			{
				String tag = item.getText();
				List<IProjectTemplate> templates = templateTagsMap.get(tag);
				for (IProjectTemplate template : templates)
				{
					if (template instanceof IDefaultProjectTemplate
							|| (!StringUtil.isEmpty(template.getDisplayName()) && template.getDisplayName().contains(
									"Default"))) //$NON-NLS-1$
					{
						foundDefault = true;
						setSelectedTemplate(tag, template);
						break;
					}
				}
				if (foundDefault)
				{
					break;
				}
			}
			if (!foundDefault)
			{
				String tag = items[0].getText();
				setSelectedTemplate(tag, templateTagsMap.get(tag).get(0));
			}
		}

		// Let left/right arrow keys traverse "list" of templates
		KeyListener keyListener = new KeyListener()
		{
			public void keyReleased(KeyEvent e)
			{
			}

			public void keyPressed(KeyEvent e)
			{
				if (e.keyCode == SWT.ARROW_RIGHT)
				{
					setSelectedTemplate(getNextTemplate(fSelectedTemplate));
				}
				else if (e.keyCode == SWT.ARROW_LEFT)
				{
					setSelectedTemplate(getPreviousTemplate(fSelectedTemplate));
				}
			}
		};
		// When template list has focus this takes effect
		templatesListComposite.addKeyListener(keyListener);
		// When tag list has focus, still let left/right key listener work
		tagsListViewer.getTable().addKeyListener(keyListener);
		tagsListViewer.getTable().setFocus();

		Dialog.applyDialogFont(main);
		setControl(main);
	}

	private int getTemplateIndex(IProjectTemplate template)
	{
		Control[] children = templatesListComposite.getChildren();
		for (Entry<Composite, IProjectTemplate> entry : templateControlMap.entrySet())
		{
			if (entry.getValue().equals(template))
			{
				Composite comp = entry.getKey();
				for (int i = 0; i < children.length; i++)
				{
					if (comp == children[i])
					{
						return i;
					}
				}
			}
		}
		return -1;
	}

	protected IProjectTemplate getPreviousTemplate(IProjectTemplate selectedTemplate)
	{
		int index = getTemplateIndex(selectedTemplate);
		if (index == -1)
		{
			return selectedTemplate;
		}
		Control[] children = templatesListComposite.getChildren();
		int prevIndex = index - 1;
		if (prevIndex < 0)
		{
			prevIndex = children.length - 1;
		}
		return templateControlMap.get(children[prevIndex]);
	}

	protected IProjectTemplate getNextTemplate(IProjectTemplate selectedTemplate)
	{
		int index = getTemplateIndex(selectedTemplate);
		if (index == -1)
		{
			return selectedTemplate;
		}
		Control[] children = templatesListComposite.getChildren();
		int nextIndex = index + 1;
		if (nextIndex >= children.length)
		{
			nextIndex = 0;
		}
		return templateControlMap.get(children[nextIndex]);
	}

	private Composite createTemplatesList(Composite parent)
	{
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(GridLayoutFactory.fillDefaults().spacing(0, 0).numColumns(2).create());
		Color background = main.getDisplay().getSystemColor(SWT.COLOR_WHITE);
		main.setBackground(background);

		// the left side is the list of template tags
		Composite templateTags = new Composite(main, SWT.BORDER);
		templateTags.setLayout(GridLayoutFactory.swtDefaults().create());
		// If there is only one tag, don't bother showing the left column.
		boolean exclude = templateTagsMap.size() <= 1;
		templateTags.setLayoutData(GridDataFactory.fillDefaults().grab(false, true).hint(150, SWT.DEFAULT)
				.exclude(exclude).create());
		templateTags.setBackground(background);

		List<String> tags = new ArrayList<String>(templateTagsMap.keySet());
		Collections.sort(tags);
		tagsListViewer = new TableViewer(templateTags, SWT.SINGLE | SWT.FULL_SELECTION);
		tagsListViewer.setContentProvider(ArrayContentProvider.getInstance());
		tagsListViewer.setLabelProvider(new LabelProvider()
		{

			@Override
			public Image getImage(Object element)
			{
				if (element instanceof String)
				{
					return getProjectTemplatesManager().getImageForTag((String) element);
				}
				return super.getImage(element);
			}
		});
		tagsListViewer.setInput(tags);
		tagsListViewer.setComparator(new ViewerComparator()
		{

			@Override
			public int category(Object element)
			{
				// make sure the "Others" tag appears at the bottom
				return ProjectTemplatesManager.TAG_OTHERS.equals(element) ? 1 : 0;
			}
		});
		Table tagsList = tagsListViewer.getTable();
		tagsList.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		tagsList.setBackground(background);
		FontData[] fontData = SWTUtils.resizeFont(tagsList.getFont(), 2);
		final Font tagFont = new Font(tagsList.getDisplay(), fontData);
		tagsList.setFont(tagFont);
		tagsList.addDisposeListener(new DisposeListener()
		{

			public void widgetDisposed(DisposeEvent e)
			{
				tagFont.dispose();
			}
		});
		tagsListViewer.addSelectionChangedListener(tagSelectionChangedListener);

		// the right side has the list of templates for the selected tag and the details on the selected template
		Composite rightComp = new Composite(main, SWT.BORDER);
		rightComp.setLayout(GridLayoutFactory.fillDefaults().create());
		rightComp.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		rightComp.setBackground(background);

		templatesListComposite = new Composite(rightComp, SWT.NONE);
		templatesListComposite.setLayout(RowLayoutFactory.swtDefaults().extendedMargins(5, 5, 5, 5).spacing(10)
				.fill(true).create());
		templatesListComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true)
				.hint(TEMPLATES_COMPOSITE_WIDTH, 250).create());
		templatesListComposite.setBackground(background);

		Label separator = new Label(rightComp, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		Composite descriptionComp = createTemplateDescription(rightComp);
		descriptionComp.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).hint(SWT.DEFAULT, 110).create());

		return main;
	}

	protected Composite createTemplateDescription(final Composite parent)
	{
		ScrolledComposite scrolledComp = new ScrolledComposite(parent, SWT.V_SCROLL);
		scrolledComp.setLayout(new FillLayout());
		templatesDescriptionComp = new Composite(scrolledComp, SWT.NONE);
		templatesDescriptionComp.setLayout(GridLayoutFactory.swtDefaults().extendedMargins(7, 0, 0, 0).numColumns(2)
				.create());

		scrolledComp.setContent(templatesDescriptionComp);

		Color background = templatesDescriptionComp.getDisplay().getSystemColor(SWT.COLOR_WHITE);
		scrolledComp.setBackground(background);
		templatesDescriptionComp.setBackground(background);

		previewImage = new Label(templatesDescriptionComp, SWT.CENTER);
		previewImage.setBackground(background);
		previewImage.setLayoutData(GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).create());
		previewLabel = new Label(templatesDescriptionComp, SWT.LEFT);
		previewLabel.setBackground(background);
		previewLabel.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false)
				.create());
		FontData[] fontData = SWTUtils.resizeFont(previewLabel.getFont(), 2);
		for (FontData data : fontData)
		{
			data.setStyle(data.getStyle() | SWT.BOLD);
		}
		final Font previewFont = new Font(previewLabel.getDisplay(), fontData);
		previewLabel.setFont(previewFont);
		previewLabel.addDisposeListener(new DisposeListener()
		{

			public void widgetDisposed(DisposeEvent e)
			{
				previewFont.dispose();
			}
		});

		previewDescription = new Label(templatesDescriptionComp, SWT.WRAP);
		previewDescription.setBackground(background);
		previewDescription.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(2, 1).create());

		templatesDescriptionComp.setSize(templatesDescriptionComp.computeSize(TEMPLATES_COMPOSITE_WIDTH, SWT.DEFAULT));
		return scrolledComp;
	}

	private void setSelectedTag(String tag)
	{
		tagsListViewer.removeSelectionChangedListener(tagSelectionChangedListener);
		tagsListViewer.setSelection(new StructuredSelection(tag));
		tagsListViewer.addSelectionChangedListener(tagSelectionChangedListener);
		// re-construct the list of templates shown on the right
		Control[] children = templatesListComposite.getChildren();
		for (Control templateControl : children)
		{
			templateControl.dispose();
		}
		templateControlMap.clear();

		List<IProjectTemplate> templates = templateTagsMap.get(tag);
		Color background = templatesListComposite.getBackground();
		for (IProjectTemplate template : templates)
		{
			final Composite templateControl = new Composite(templatesListComposite, SWT.NONE);
			templateControl.setLayout(GridLayoutFactory.fillDefaults().extendedMargins(0, 0, 5, 5).create());
			templateControl.setLayoutData(RowDataFactory.swtDefaults().hint(95, SWT.DEFAULT).create());
			templateControl.setBackground(background);

			Label image = new Label(templateControl, SWT.CENTER);
			image.setImage(defaultTemplateImage);
			templateImages.put(template, defaultTemplateImage);
			loadImageInBackground(image, template);

			image.setBackground(background);
			image.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).align(SWT.CENTER, SWT.CENTER).create());
			Label text = new Label(templateControl, SWT.CENTER | SWT.WRAP);
			text.setText(template.getDisplayName());
			text.setBackground(background);
			text.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).align(SWT.CENTER, SWT.BEGINNING)
					.create());

			MouseAdapter mouseAdapter = new MouseAdapter()
			{

				@Override
				public void mouseDown(MouseEvent e)
				{
					setSelectedTemplate(templateControlMap.get(templateControl));
				}

				@Override
				public void mouseDoubleClick(MouseEvent e)
				{
					// Treat double-click like selecting the template and clicking "Next"
					if (canFlipToNextPage())
					{
						getContainer().showPage(getNextPage());
					}
				}
			};
			templateControl.addMouseListener(mouseAdapter);
			image.addMouseListener(mouseAdapter);
			text.addMouseListener(mouseAdapter);

			templateControlMap.put(templateControl, template);
		}
		templatesListComposite.layout(true, true);
	}

	private void loadImageInBackground(final Label label, final IProjectTemplate template)
	{
		final URL iconPath = template.getIconURL();
		if (iconPath != null)
		{
			final ImageData[] imageData = new ImageData[1];
			Job loadImageJob = new Job("Loading template image...") //$NON-NLS-1$
			{
				@Override
				protected IStatus run(IProgressMonitor monitor)
				{
					try
					{
						ImageDescriptor descriptor = ImageDescriptor.createFromURL(iconPath);
						imageData[0] = descriptor.getImageData();
					}
					catch (Exception e)
					{
						IdeLog.logWarning(ProjectsPlugin.getDefault(), "Failed to retrieve the template's image: " + e); //$NON-NLS-1$
					}
					return Status.OK_STATUS;
				}
			};
			EclipseUtil.setSystemForJob(loadImageJob);
			loadImageJob.schedule();
			loadImageJob.addJobChangeListener(new JobChangeAdapter()
			{
				@Override
				public void done(IJobChangeEvent event)
				{
					if (imageData[0] != null)
					{
						UIUtils.getDisplay().asyncExec(new Runnable()
						{
							public void run()
							{
								Image image = new Image(UIUtils.getDisplay(), imageData[0]);
								if (image != null)
								{
									// Scale the image to 48x48 in case it's not.
									ImageData scaledImageData = image.getImageData();
									if (scaledImageData.x != IMAGE_SIZE || scaledImageData.y != IMAGE_SIZE)
									{
										// dispose the previous one
										image.dispose();
										// Scale the image data and create a new image
										scaledImageData = scaledImageData.scaledTo(IMAGE_SIZE, IMAGE_SIZE);
										image = ImageDescriptor.createFromImageData(scaledImageData).createImage();
									}
								}
								label.setImage(image);
								templateImages.put(template, image);
							}
						});
					}
				}
			});
		}
	}

	private void setSelectedTemplate(IProjectTemplate template)
	{
		// make the corresponding template control appear selected
		Set<Composite> templateControls = templateControlMap.keySet();
		for (Composite composite : templateControls)
		{
			Color background;
			if (templateControlMap.get(composite) == template)
			{
				background = composite.getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION);
			}
			else
			{
				background = composite.getDisplay().getSystemColor(SWT.COLOR_WHITE);
			}
			composite.setBackground(background);
			Control[] children = composite.getChildren();
			for (Control childComp : children)
			{
				childComp.setBackground(background);
			}
		}
		// update the preview area according to the template selection
		previewImage.setImage(getImage(template));
		previewLabel.setText(template.getDisplayName());
		String text = (template == null) ? null : template.getDescription();
		previewDescription.setText(text == null ? StringUtil.EMPTY : text);
		fSelectedTemplate = template;

		templatesDescriptionComp.layout();
		templatesDescriptionComp.setSize(templatesDescriptionComp.computeSize(TEMPLATES_COMPOSITE_WIDTH, SWT.DEFAULT));
	}

	private void setSelectedTemplate(String tag, IProjectTemplate template)
	{
		setSelectedTag(tag);
		setSelectedTemplate(template);
	}

	private Image getImage(IProjectTemplate template)
	{
		Image image = templateImages.get(template);
		if (image == null)
		{
			image = defaultTemplateImage;
		}
		return image;
	}

	private void populateTagsMap()
	{
		templateTagsMap.clear();
		List<IProjectTemplate> others = new ArrayList<IProjectTemplate>();
		for (IProjectTemplate template : fTemplates)
		{
			List<String> tags = template.getTags();
			if (!CollectionsUtil.isEmpty(tags))
			{
				for (String tag : tags)
				{
					List<IProjectTemplate> tagTemplates = templateTagsMap.get(tag);
					if (tagTemplates == null)
					{
						tagTemplates = new ArrayList<IProjectTemplate>();
						templateTagsMap.put(tag, tagTemplates);
					}
					tagTemplates.add(template);
				}
			}
			else
			{
				others.add(template);
			}
		}
		// add an "Others" tag to hold the list of templates with no tags assigned
		if (!others.isEmpty())
		{
			templateTagsMap.put(ProjectTemplatesManager.TAG_OTHERS, others);
		}
	}

	public void initStepIndicator(String[] stepNames)
	{
		this.stepNames = stepNames;
	}

	public String getStepName()
	{
		return getTitle();
	}

	protected ProjectTemplatesManager getProjectTemplatesManager()
	{
		return ProjectsPlugin.getDefault().getTemplatesManager();
	}
}
