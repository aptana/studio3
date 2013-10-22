/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.snippets.ui.views;

import java.io.File;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.search.internal.core.text.PatternConstructor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.events.ExpandListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.dnd.SnippetTransfer;
import com.aptana.editor.common.scripting.commands.CommandExecutionUtils;
import com.aptana.editor.common.scripting.commands.TextEditorUtils;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.CommandResult;
import com.aptana.scripting.model.InvocationType;
import com.aptana.scripting.model.LoadCycleListener;
import com.aptana.scripting.model.SnippetCategoryElement;
import com.aptana.scripting.model.SnippetElement;
import com.aptana.scripting.ui.ScriptingUIPlugin;
import com.aptana.theme.ColorManager;
import com.aptana.theme.IThemeManager;
import com.aptana.theme.Theme;
import com.aptana.theme.ThemePlugin;
import com.aptana.ui.util.UIUtils;
import com.aptana.ui.widgets.SearchComposite;

/**
 * View displays snippets that match the current active editor's context
 * 
 * @author nle
 */
public class SnippetsView extends ViewPart
{

	private static final String ICONS_FULL_ELCL16_EXPANDALL_GIF = "icons/full/elcl16/expandall.gif"; //$NON-NLS-1$
	public static final String ID = "com.aptana.snippets.ui.views.SnippetsView"; //$NON-NLS-1$
	private Image informationImage = ScriptingUIPlugin.getImage("icons/full/obj16/information.png"); //$NON-NLS-1$$
	private Image genericSnippetImage = ScriptingUIPlugin.getImage("icons/snippet.png"); //$NON-NLS-1$
	private Image genericDisabledSnippetImage = new Image(genericSnippetImage.getDevice(), genericSnippetImage,
			SWT.IMAGE_DISABLE);
	private Image insertSnippetImage = ScriptingUIPlugin.getImage("icons/full/elcl16/insert_snippet_tsk.png"); //$NON-NLS-1$

	private Map<String, ExpandItem> expandItems = new HashMap<String, ExpandItem>();
	private ScrolledComposite scrolledComposite;
	private ExpandBar expandBar;
	private SearchComposite search;
	private SnippetsFilter filter;

	private Map<String, SnippetCategoryElement> snippetCategories = new LinkedHashMap<String, SnippetCategoryElement>();
	private HashMap<String, List<SnippetData>> sortedSnippets = new LinkedHashMap<String, List<SnippetData>>();
	private Map<SnippetData, SnippetItem> snippetItems = new HashMap<SnippetData, SnippetItem>();
	private Map<String, List<ToolItem>> toolItemMap = new HashMap<String, List<ToolItem>>();

	private Font defaultFont, themeFont;
	private ImageRegistry imageRegistry, disabledImageRegistry;
	private ImageRegistry hotTagImageRegistry;
	private ColorManager colorManager;
	private IPreferenceChangeListener themeListener;
	private RGB defaultTagHotFg = new RGB(135, 160, 208);
	private RGB defaultTagHotBg = new RGB(192, 201, 219);
	private RGB defaultTagBg = new RGB(222, 231, 249);
	private RGB defaultTagFg = new RGB(165, 190, 238);

	private List<String> collapsedCategories = new ArrayList<String>();

	private String currentScope;
	private SnippetPopupDialog snippetDialog;
	private SnippetBundleListener snippetBundleListener;
	private int tagWidth = -1;
	private static Pattern descriptionReplacePattern = Pattern.compile("[\r\n\t]+"); //$NON-NLS-1$ 

	class SnippetBundleListener implements LoadCycleListener
	{
		private final Job job = new Job("Force reconcile on bundle change") //$NON-NLS-1$
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				Display.getDefault().asyncExec(new Runnable()
				{

					public void run()
					{
						if (scrolledComposite != null && !scrolledComposite.isDisposed())
						{
							createSnippetDrawers();
							updateBasedOnPart(getSite().getPage().getActiveEditor());
						}
					}
				});
				return Status.OK_STATUS;
			}
		};

		/**
		 *
		 */
		public SnippetBundleListener()
		{
			BundleManager.getInstance().addLoadCycleListener(this);
		}

		public void dispose()
		{
			BundleManager.getInstance().removeLoadCycleListener(this);
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.scripting.model.LoadCycleListener#scriptLoaded(java.io.File)
		 */
		public void scriptLoaded(File script)
		{
			bundleFileChanged(script);
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.scripting.model.LoadCycleListener#scriptReloaded(java.io.File)
		 */
		public void scriptReloaded(File script)
		{
			bundleFileChanged(script);
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.scripting.model.LoadCycleListener#scriptUnloaded(java.io.File)
		 */
		public void scriptUnloaded(File script)
		{
			bundleFileChanged(script);
		}

		private void bundleFileChanged(File script)
		{
			if (script == null
					|| (!script.getName().equals(BundleManager.BUNDLE_FILE) && !(script.getParentFile() != null && BundleManager.SNIPPETS_DIRECTORY_NAME
							.equals(script.getParentFile().getName()))))
			{
				return;
			}
			// Run in a job on a delay and cancel/reschedule if it already exists and is scheduled... This should
			// basically only make us run once if we get hit multiple times in a row. We'll still probably run a few
			// times, but this should cut it down a lot.
			job.cancel();
			EclipseUtil.setSystemForJob(job);
			job.schedule(750);
		}
	}

	private class InsertSnippetListener extends MouseAdapter
	{
		private SnippetElement snippet;

		InsertSnippetListener(SnippetElement snippet)
		{
			this.snippet = snippet;
		}

		public void mouseDoubleClick(MouseEvent e)
		{
			insertSnippet(snippet);
		}
	}

	private ISelectionListener selectionListener = new ISelectionListener()
	{

		public void selectionChanged(IWorkbenchPart part, ISelection selection)
		{
			if (selection instanceof ITextSelection)
			{
				updateBasedOnPart(part);
			}
		}
	};

	Comparator<SnippetCategoryElement> snippetCategoryElementComparator = new Comparator<SnippetCategoryElement>()
	{
		public int compare(SnippetCategoryElement arg0, SnippetCategoryElement arg1)
		{
			if ((arg0 == null && arg1 == null)
					|| (arg0 != null && arg0.getDisplayName() == null && arg1 != null && arg1.getDisplayName() == null))
			{
				return 0;
			}
			else if (arg0 == null || arg0.getDisplayName() == null)
			{
				return 1;
			}
			else if (arg1 == null || arg1.getDisplayName() == null)
			{
				return -1;
			}

			return arg0.getDisplayName().compareTo(arg1.getDisplayName());
		}
	};

	/**
	 * UI model for a snippet element. Maintains UI state for a SnippetItem
	 * 
	 * @author nle
	 */
	private class SnippetData
	{
		private SnippetElement snippet;
		private boolean visuallyEnabled = true;
		private boolean filtered = false;

		SnippetData(SnippetElement snippet)
		{
			this.snippet = snippet;
		}
	}

	/**
	 * UI representation of a snippet in the Snippets view
	 * 
	 * @author nle
	 */
	private class SnippetItem extends Composite
	{
		private SnippetData snippetData;
		private MouseListener mouseListener;
		private Label imageLabel;
		private Composite textComposite;
		private CLabel titleLabel;
		private CLabel descLabel;
		private ToolBar toolbar;
		private ToolBar tagToolBar;
		private String categoryName;

		// Default margin for the snippet item
		final int SNIPPET_MARGIN = 5;

		SnippetItem(Composite parent, SnippetData snippetData)
		{
			super(parent, SWT.NONE);
			this.snippetData = snippetData;
			mouseListener = new InsertSnippetListener(snippetData.snippet);
			categoryName = getSnippetCategoryName(snippetData.snippet);

			setLayout(GridLayoutFactory.fillDefaults()
					.extendedMargins(SNIPPET_MARGIN, SNIPPET_MARGIN, 1, SNIPPET_MARGIN).spacing(2, 0).numColumns(3)
					.create());
			GridData gridData = GridDataFactory.fillDefaults().grab(true, false).create();
			gridData.exclude = snippetData.filtered;
			setLayoutData(gridData);

			addPaintListener(new PaintListener()
			{
				public void paintControl(PaintEvent e)
				{
					Color foreground = e.gc.getForeground();
					e.gc.setForeground(colorManager.getColor(Theme.alphaBlend(getBackground().getRGB(), getForeground()
							.getRGB(), 100)));
					Rectangle clientArea = getClientArea();
					e.gc.drawLine(clientArea.x - SNIPPET_MARGIN, clientArea.y + clientArea.height - 2, clientArea.x
							+ clientArea.width + SNIPPET_MARGIN, clientArea.y + clientArea.height - 2);
					e.gc.drawLine(clientArea.x, clientArea.y, clientArea.x, clientArea.y + clientArea.height - 1);
					e.gc.drawLine(clientArea.x + clientArea.width - 1, clientArea.y, clientArea.x + clientArea.width
							- 1, clientArea.y + clientArea.height - 1);
					Point newPoint = UIUtils.getDisplay().map(SnippetItem.this, getParent(),
							new Point(clientArea.x, clientArea.y));
					if (newPoint.y == 0)
					{
						e.gc.drawLine(clientArea.x - SNIPPET_MARGIN, clientArea.y, clientArea.x + clientArea.width
								+ SNIPPET_MARGIN, clientArea.y);
					}
					e.gc.setForeground(foreground);
				}
			});

			addMouseListener(mouseListener);

			addTitleAndDescriptionForSnippet(snippetData.snippet, this, mouseListener);
			addActionToobarForSnippet(snippetData.snippet, this);
			addTagToolbarForSnippet(snippetData.snippet, this);

			applyVisualEnablement();
		}

		protected void setFiltered(boolean filtered)
		{
			snippetData.filtered = filtered;
			applyFiltered();
		}

		protected void applyFiltered()
		{
			setVisible(!snippetData.filtered);
			((GridData) getLayoutData()).exclude = snippetData.filtered;
		}

		/**
		 * Enablement for the snippets is mostly visual. We still want to allow users to operate on snippets even though
		 * they my not be compatible with the current scope
		 * 
		 * @param enabled
		 */
		protected void setVisualEnabled(boolean enabled)
		{
			snippetData.visuallyEnabled = enabled;
			applyVisualEnablement();
		}

		protected void applyVisualEnablement()
		{
			titleLabel.setForeground(descLabel.getForeground());

			if (tagToolBar != null && tagToolBar.isDisposed())
			{
				tagToolBar.setEnabled(snippetData.visuallyEnabled);
			}

			imageLabel.setImage(getSnippetImage(snippetData.visuallyEnabled));
		}

		private void addTitleAndDescriptionForSnippet(SnippetElement snippet, Composite itemComposite,
				MouseListener mouseListener)
		{
			imageLabel = new Label(itemComposite, SWT.NONE);
			imageLabel.setLayoutData(GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).create());
			imageLabel.setImage(getSnippetImage(true));
			imageLabel.addMouseListener(mouseListener);

			textComposite = new Composite(itemComposite, SWT.NONE);
			textComposite.setLayout(GridLayoutFactory.fillDefaults().spacing(3, 0).numColumns(2).create());
			textComposite.setLayoutData(GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER)
					.grab(true, false).create());
			textComposite.addMouseListener(mouseListener);

			titleLabel = new CLabel(textComposite, SWT.NONE);
			titleLabel.setText(snippet.getDisplayName());
			titleLabel.setLayoutData(GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false)
					.create());
			titleLabel.addMouseListener(mouseListener);

			descLabel = new CLabel(textComposite, SWT.NONE);
			String descText = snippet.getDescription();
			if (descText == null || StringUtil.isEmpty(descText))
			{
				descText = descriptionReplacePattern.matcher(snippet.getExpansion()).replaceAll(" "); //$NON-NLS-1$
			}

			descLabel.setText(descText);
			descLabel.setLayoutData(GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(true, false)
					.create());
			descLabel.addMouseListener(mouseListener);

			addDragDropForSnippet();
		}

		private void addActionToobarForSnippet(final SnippetElement snippet, final Composite itemComposite)
		{
			toolbar = new ToolBar(itemComposite, SWT.HORIZONTAL);
			toolbar.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).create());
			toolbar.setVisible(false);

			ToolItem insertItem = new ToolItem(toolbar, SWT.NONE);
			insertItem.setImage(insertSnippetImage);
			insertItem.setToolTipText(Messages.SnippetsView_Insert_Snippet_desc);
			insertItem.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					insertSnippet(snippet);
				}
			});

			ToolItem infoItem = new ToolItem(toolbar, SWT.NONE);
			infoItem.setImage(informationImage);
			infoItem.setToolTipText(Messages.SnippetsView_Show_Information_desc);
			infoItem.addSelectionListener(new SelectionAdapter()
			{

				@Override
				public void widgetSelected(SelectionEvent e)
				{
					closeSnippetDialog();

					snippetDialog = new SnippetPopupDialog(getShell(), snippet, toolbar);
					snippetDialog.open();
				}

			});

			itemComposite.addMouseMoveListener(new MouseMoveListener()
			{

				public void mouseMove(MouseEvent e)
				{
					boolean contains = itemComposite.getClientArea().contains(e.x, e.y);
					if (contains)
					{
						for (SnippetItem item : snippetItems.values())
						{
							ToolBar tempToolbar = item.toolbar;
							if (tempToolbar != toolbar)
							{
								tempToolbar.setVisible(false);
							}
						}
					}

					toolbar.setVisible(contains);
				}
			});
		}

		private void addTagToolbarForSnippet(final SnippetElement snippet, final Composite itemComposite)
		{
			List<String> tags = snippet.getTags();
			if (!CollectionsUtil.isEmpty(tags))
			{
				new Label(itemComposite, SWT.NONE);

				tagToolBar = new ToolBar(itemComposite, SWT.HORIZONTAL | SWT.WRAP);
				tagToolBar.setLayoutData(GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).grab(true, false)
						.span(2, 1).create());

				int height = -1;

				for (String tag : tags)
				{
					height = createTagToolItem(tagToolBar, height, tag);
				}
			}
		}

		private void addDragDropForSnippet()
		{
			// Add DnD
			int operations = DND.DROP_COPY;
			Transfer[] types = new Transfer[] { SnippetTransfer.getInstance() };

			DragSourceListener listener = new DragSourceListener()
			{

				public void dragStart(DragSourceEvent event)
				{
					event.doit = true;
					event.image = insertSnippetImage;
				}

				public void dragSetData(DragSourceEvent event)
				{
					event.data = snippetData.snippet;
				}

				public void dragFinished(DragSourceEvent event)
				{
				}
			};

			Control[] controls = new Control[] { imageLabel, titleLabel, descLabel };
			for (Control control : controls)
			{
				DragSource titleSource = new DragSource(control, operations);
				titleSource.setTransfer(types);
				titleSource.addDragListener(listener);
			}
		}

		private Image getSnippetImage(boolean enabled)
		{
			URL iconURL = snippetData.snippet.getIconURL();
			Image image = null;
			if (enabled)
			{
				image = iconURL != null ? getImage(iconURL) : genericSnippetImage;
			}
			else
			{
				image = iconURL != null ? getDisabledImage(iconURL) : genericDisabledSnippetImage;
			}

			return image;
		}
	}

	/**
	 * Job used to show/hide snippets
	 * 
	 * @author nle
	 */
	private class FilterSnippetsJob extends Job
	{
		List<SnippetData> snippets = new ArrayList<SnippetData>();

		public FilterSnippetsJob(List<SnippetData> newSnippets)
		{
			super("Filter snippets"); //$NON-NLS-1$
			snippets.addAll(newSnippets);
		}

		@Override
		protected IStatus run(IProgressMonitor monitor)
		{
			Display.getDefault().asyncExec(new Runnable()
			{
				public void run()
				{
					showSnippets(snippets);
				}
			});
			return Status.OK_STATUS;
		}
	}

	FilterSnippetsJob filterSnippetsJob = null;

	private IPartListener partListener = new IPartListener()
	{

		public void partOpened(IWorkbenchPart part)
		{
			// No-op
		}

		public void partDeactivated(IWorkbenchPart part)
		{
			// No-op
		}

		public void partClosed(IWorkbenchPart part)
		{
			// No-op
		}

		public void partBroughtToTop(IWorkbenchPart part)
		{
			// No-op
		}

		public void partActivated(IWorkbenchPart part)
		{
			updateBasedOnPart(part);
		}
	};

	public SnippetsView()
	{
		setPartName(Messages.SnippetsView_partName);
		imageRegistry = new ImageRegistry();
		disabledImageRegistry = new ImageRegistry();
		hotTagImageRegistry = new ImageRegistry();
		colorManager = new ColorManager();

		snippetBundleListener = new SnippetBundleListener();
	}

	private void updateThemeFont()
	{
		themeFont = defaultFont;
	}

	private void updateThemeColors()
	{
		updateThemeFont();

		// Update tag images
		int height = -1;

		for (String tagName : toolItemMap.keySet())
		{
			List<ToolItem> toolItems = toolItemMap.get(tagName);

			for (ToolItem toolItem : toolItems)
			{
				toolItem.setImage(null);
				toolItem.setHotImage(null);
			}

			imageRegistry.remove(tagName);
			hotTagImageRegistry.remove(tagName);

			for (ToolItem toolItem : toolItems)
			{
				height = createTagImagesForToolItem(toolItem.getParent(), height, tagName, toolItem);
			}
		}

		for (SnippetItem item : snippetItems.values())
		{
			item.applyVisualEnablement();
		}

		updateSnippetDrawers();
	}

	protected Font getFont()
	{
		return JFaceResources.getTextFont();
	}

	synchronized private void closeSnippetDialog()
	{
		if (snippetDialog != null)
		{
			snippetDialog.close();
			snippetDialog = null;
		}
	}

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(final Composite parent)
	{
		filter = new SnippetsFilter();
		defaultFont = parent.getFont();

		updateThemeColors();

		themeListener = new IPreferenceChangeListener()
		{

			public void preferenceChange(PreferenceChangeEvent event)
			{
				String key = event.getKey();
				if (IThemeManager.THEME_CHANGED.equals(key))
				{
					updateThemeColors();
				}
			}
		};

		parent.setLayout(GridLayoutFactory.fillDefaults().margins(0, 1).spacing(0, 0).create());

		search = new SearchComposite(parent, SWT.NONE, false, new SearchComposite.Client()
		{

			@SuppressWarnings("restriction")
			public void search(String text, boolean isCaseSensitive, boolean isRegularExpression)
			{
				Pattern pattern = PatternConstructor.createPattern(text, isCaseSensitive, isRegularExpression);
				filter.setPattern(pattern);

				List<SnippetElement> snippets = new ArrayList<SnippetElement>();
				Map<SnippetElement, SnippetData> mapping = new HashMap<SnippetElement, SnippetsView.SnippetData>();

				for (List<SnippetData> list : sortedSnippets.values())
				{
					for (SnippetData data : list)
					{
						snippets.add(data.snippet);
						mapping.put(data.snippet, data);
					}
				}

				Object[] filtered = filter.filter(null, (Object) null,
						snippets.toArray(new SnippetElement[snippets.size()]));

				List<SnippetData> snippetDatas = new ArrayList<SnippetsView.SnippetData>();

				for (Object object : filtered)
				{
					snippetDatas.add(mapping.get(object));
				}

				// Updates the snippets on a delay
				if (filterSnippetsJob != null)
				{
					filterSnippetsJob.cancel();
				}

				filterSnippetsJob = new FilterSnippetsJob(snippetDatas);
				filterSnippetsJob.schedule(750);
			}
		});

		search.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		search.setSearchOnEnter(false);
		search.setInitialText(Messages.SnippetsView_Initial_filter_text);

		scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		scrolledComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		scrolledComposite.setLayout(GridLayoutFactory.fillDefaults().spacing(0, 0).create());
		scrolledComposite.getVerticalBar().setIncrement(6);

		expandBar = new ExpandBar(scrolledComposite, SWT.NONE);
		expandBar.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		scrolledComposite.setContent(expandBar);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		createSnippetDrawers();

		expandBar.addExpandListener(new ExpandListener()
		{

			public void itemExpanded(ExpandEvent e)
			{
				expandDrawer((String) ((ExpandItem) e.item).getData(), false);
				updateScrollMinSize((ExpandItem) e.item, true);
				collapsedCategories.remove((String) e.item.getData());
			}

			public void itemCollapsed(ExpandEvent e)
			{
				updateScrollMinSize((ExpandItem) e.item, false);
				collapsedCategories.add((String) e.item.getData());
			}
		});

		initializeToolBar();

		IPartService service = (IPartService) getSite().getService(IPartService.class);
		if (service != null)
		{
			service.addPartListener(partListener);
		}

		IWorkbenchPage page = getSite().getPage();
		if (page != null)
		{
			updateBasedOnPart(page.getActiveEditor());
		}

		getSite().getWorkbenchWindow().getSelectionService().addPostSelectionListener(selectionListener);
	}

	private void updateBasedOnPart(IWorkbenchPart part)
	{
		if (part instanceof AbstractThemeableEditor)
		{
			AbstractThemeableEditor textEditor = (AbstractThemeableEditor) part;
			String scope = null;

			try
			{
				ISourceViewer viewer = TextEditorUtils.getSourceViewer(textEditor);
				int caretOffset = textEditor.getCaretOffset();
				if (viewer == null)
				{
					IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
					scope = CommonEditorPlugin.getDefault().getDocumentScopeManager()
							.getScopeAtOffset(document, caretOffset);
				}
				else
				{
					// Get the scope at caret offset
					scope = CommonEditorPlugin.getDefault().getDocumentScopeManager()
							.getScopeAtOffset(viewer, caretOffset);
				}
			}
			catch (Exception e)
			{

			}

			if (currentScope == null || !currentScope.equals(scope))
			{
				updateSnippetEnablement(scope);
			}
		}
		else if (part instanceof IEditorPart)
		{
			updateSnippetEnablement(null);
		}
	}

	/*
	 * Updates the minimum size of the scrolled composite after a drawer is expanded/collapsed. This ensures the
	 * scrolling works properly
	 */
	private void updateScrollMinSize(ExpandItem item, boolean expanded)
	{
		if (item == null)
		{
			Point size = expandBar.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			scrolledComposite.setMinSize(size.x, size.y);
		}
		else
		{
			int minHeight = scrolledComposite.getMinHeight();
			minHeight = expanded ? minHeight + item.getHeight() : minHeight - item.getHeight();
			scrolledComposite.setMinHeight(minHeight);
		}
	}

	protected void showSnippets(List<SnippetData> snippets)
	{
		Map<String, List<SnippetElement>> localSnippets = new HashMap<String, List<SnippetElement>>();

		for (SnippetData snippetData : snippets)
		{
			String category = getSnippetCategoryName(snippetData.snippet);
			List<SnippetElement> snippetList = localSnippets.get(category);
			if (snippetList == null)
			{
				snippetList = new ArrayList<SnippetElement>();
				localSnippets.put(category, snippetList);
			}

			snippetList.add(snippetData.snippet);
		}

		// Determine whether to show or hide the snippet
		Map<String, List<Control>> changedSnippets = new HashMap<String, List<Control>>();
		for (String category : sortedSnippets.keySet())
		{
			List<SnippetData> datas = sortedSnippets.get(category);
			for (SnippetData data : datas)
			{
				SnippetItem item = snippetItems.get(data);
				boolean newExclude = !snippets.contains(data);
				boolean oldExclude = data.filtered;
				data.filtered = newExclude;
				boolean itemChanged = false;

				if (item != null)
				{
					if (newExclude != oldExclude)
					{
						itemChanged = true;
					}

					item.setFiltered(newExclude);
				}
				else if (!newExclude)
				{
					ExpandItem expandItem = expandItems.get(category);
					if (expandItem.getExpanded())
					{
						expandDrawer(category, false);
						itemChanged = true;
						item = snippetItems.get(data);
						if (item != null)
						{
							item.setFiltered(newExclude);
						}
					}
				}

				if (itemChanged)
				{
					List<Control> list = changedSnippets.get(category);
					if (list == null)
					{
						list = new ArrayList<Control>();
						changedSnippets.put(category, list);
					}
					list.add(item);
				}
			}
		}

		// Update category text and layout if necessary
		for (String category : expandItems.keySet())
		{
			int size = 0;
			List<SnippetElement> sortedSnippets = localSnippets.get(category);
			if (sortedSnippets != null)
			{
				size = sortedSnippets.size();
			}

			ExpandItem expandItem = expandItems.get(category);
			expandItem.setText(MessageFormat.format(Messages.SnippetsView_Snippet_drawer_title,
					category != null ? category : Messages.SnippetsView_Snippet_drawer_other, String.valueOf(size)));

			List<Control> list = changedSnippets.get(category);
			if (!CollectionsUtil.isEmpty(list))
			{
				((Composite) expandItem.getControl()).layout(list.toArray(new Control[list.size()]));
				expandItem.setHeight(expandItem.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
			}
		}

		updateScrollMinSize(null, false);
	}

	protected String getSnippetCategoryName(SnippetElement snippet)
	{
		return snippet.getCategory() != null ? snippet.getCategory() : snippet.getOwningBundle().getDisplayName();
	}

	protected void createSnippetDrawers()
	{
		snippetItems.clear();
		toolItemMap.clear();
		currentScope = null;

		EclipseUtil.instanceScope().getNode(ThemePlugin.PLUGIN_ID).removePreferenceChangeListener(themeListener);

		List<String> expandedDrawers = new ArrayList<String>();
		for (ExpandItem item : expandItems.values())
		{
			if (item.isDisposed())
			{
				continue;
			}
			else if (item.getExpanded())
			{
				expandedDrawers.add((String) item.getData());
			}

			item.getControl().dispose();
			item.dispose();
		}

		expandItems.clear();

		updateCategoriesAndSnippets(expandedDrawers);

		updateScrollMinSize(null, false);
		scrolledComposite.layout(true, true);

		updateSnippetDrawers();

		EclipseUtil.instanceScope().getNode(ThemePlugin.PLUGIN_ID).addPreferenceChangeListener(themeListener);
	}

	/**
	 * Lazy load the create of SnippetItems when the category is expanded
	 * 
	 * @param category
	 * @param shouldExpand
	 */
	private void expandDrawer(String category, boolean shouldExpand)
	{
		ExpandItem expandItem = expandItems.get(category);
		if (expandItem != null)
		{
			boolean adjustHeight = false;
			Composite composite = (Composite) expandItem.getControl();
			List<SnippetData> elements = sortedSnippets.get(category);
			if (!CollectionsUtil.isEmpty(elements))
			{
				for (SnippetData element : elements)
				{
					if (snippetItems.get(element) == null)
					{
						snippetItems.put(element, new SnippetItem(composite, element));
						adjustHeight = true;
					}
				}
			}

			if (adjustHeight)
			{
				expandItem.setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
			}

			if (shouldExpand)
			{
				expandItem.setExpanded(true);
			}
		}
	}

	private void updateSnippetEnablement(String scope)
	{
		currentScope = scope;
		for (List<SnippetData> datas : sortedSnippets.values())
		{
			for (SnippetData data : datas)
			{
				boolean matches = data.snippet.getScopeSelector().matches(scope);
				SnippetItem item = snippetItems.get(data);
				if (item != null)
				{
					item.setVisualEnabled(matches);
				}
				else
				{
					data.visuallyEnabled = matches;
				}
			}
		}

		updateSnippetDrawers();
	}

	private void updateCategoriesAndSnippets(List<String> expandedDrawers)
	{
		snippetCategories.clear();

		for (SnippetCategoryElement category : BundleManager.getInstance().getSnippetCategories(null))
		{
			snippetCategories.put(category.getDisplayName(), category);
		}

		List<SnippetElement> snippets = BundleManager.getInstance().getSnippets(null);

		if (CollectionsUtil.isEmpty(snippets))
		{
			currentScope = null;
		}

		snippetItems.clear();
		sortedSnippets.clear();
		toolItemMap.clear();

		Set<String> tags = new HashSet<String>();

		for (SnippetElement snippet : snippets)
		{
			String category = getSnippetCategoryName(snippet);

			if (!snippetCategories.containsKey(category))
			{
				SnippetCategoryElement snippetCategoryElement = new SnippetCategoryElement(null);
				snippetCategoryElement.setDisplayName(category);
				snippetCategories.put(category, snippetCategoryElement);
			}

			List<SnippetData> snippetList = sortedSnippets.get(category);
			if (snippetList == null)
			{
				snippetList = new ArrayList<SnippetData>();
				sortedSnippets.put(category, snippetList);
			}

			snippetList.add(new SnippetData(snippet));
			tags.addAll(snippet.getTags());
		}

		// Only on windows: Ensure the image widths are all the same for tags, due to the platform scaling all toolitems
		// to be the same width
		if (Platform.OS_WIN32.equals(Platform.getOS()))
		{
			tagWidth = -1;
			GC gc = new GC(UIUtils.getDisplay());
			if (!CollectionsUtil.isEmpty(tags))
			{
				for (String tag : tags)
				{
					if (!StringUtil.isEmpty(tag))
					{
						int extent = gc.textExtent(MessageFormat.format("__{0}__", tag)).x; //$NON-NLS-1$
						if (extent > tagWidth)
						{
							tagWidth = extent;
						}
					}
				}
			}

			gc.dispose();
		}

		List<SnippetCategoryElement> categories = new ArrayList<SnippetCategoryElement>();
		categories.addAll(snippetCategories.values());
		Collections.sort(categories, snippetCategoryElementComparator);

		for (SnippetCategoryElement category : categories)
		{
			List<SnippetData> snippetList = sortedSnippets.get(category.getDisplayName());
			ExpandItem expandItem = createSnippetDrawer(expandBar, category, snippetList);
			if (expandedDrawers.contains(expandItem.getData()))
			{
				expandDrawer((String) expandItem.getData(), true);
			}
		}
	}

	private void updateSnippetDrawers()
	{
		for (ExpandItem item : expandItems.values())
		{
			Composite control = (Composite) item.getControl();
			control.layout(true, true);
			item.setHeight(control.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		}
	}

	protected ExpandItem createSnippetDrawer(ExpandBar expandBar, SnippetCategoryElement category,
			List<SnippetData> snippets)
	{
		Composite composite = new Composite(expandBar, SWT.NONE);
		composite.setForeground(UIUtils.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BORDER));
		composite.setLayout(GridLayoutFactory.fillDefaults().spacing(0, 0).create());

		ExpandItem expandItem = new ExpandItem(expandBar, SWT.NONE);
		int size = 0;
		if (snippets != null)
		{
			size = snippets.size();
		}

		expandItem.setText(MessageFormat.format(Messages.SnippetsView_Snippet_drawer_title, category.getDisplayName(),
				String.valueOf(size)));
		expandItem.setControl(composite);
		expandItem.setImage(category != null && category.getIconURL() != null ? getImage(category.getIconURL())
				: genericSnippetImage);
		expandItem.setData(category != null ? category.getDisplayName() : null);
		expandItem.setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);

		expandItems.put(category != null ? category.getDisplayName() : null, expandItem);

		return expandItem;
	}

	private void insertSnippet(SnippetElement snippet)
	{
		IEditorPart activeEditor = UIUtils.getActiveEditor();
		ITextEditor textEditor = null;
		if (activeEditor instanceof MultiPageEditorPart)
		{
			Object selectedPage = ((MultiPageEditorPart) activeEditor).getSelectedPage();
			if (selectedPage instanceof ITextEditor)
			{
				textEditor = (ITextEditor) selectedPage;
			}
		}
		else if (activeEditor instanceof ITextEditor)
		{
			textEditor = (ITextEditor) activeEditor;
		}

		if (textEditor != null)
		{
			CommandResult commandResult = CommandExecutionUtils
					.executeCommand(snippet, InvocationType.MENU, textEditor);
			if (commandResult != null)
			{
				CommandExecutionUtils.processCommandResult(snippet, commandResult, textEditor);
				activeEditor.setFocus();
			}
		}
	}

	private int createTagToolItem(ToolBar tagToolBar, int height, final String tag)
	{
		ToolItem item = new ToolItem(tagToolBar, SWT.PUSH);
		item.setData("tag", tag); //$NON-NLS-1$
		height = createTagImagesForToolItem(tagToolBar, height, tag, item);

		item.addSelectionListener(new SelectionAdapter()
		{
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				search.getTextControl().setFocus();
				search.getTextControl().setText(tag);
			}
		});

		List<ToolItem> toolItems = toolItemMap.get(tag);
		if (toolItems == null)
		{
			toolItems = new ArrayList<ToolItem>();
			toolItemMap.put(tag, toolItems);
		}

		toolItems.add(item);

		return height;
	}

	private int createTagImagesForToolItem(ToolBar tagToolBar, int height, String tag, ToolItem item)
	{
		item.setHotImage(null);
		item.setImage(null);

		item.setText(MessageFormat.format("__{0}__", tag)); //$NON-NLS-1$
		Rectangle bounds = item.getBounds();
		int width = bounds.width;
		item.setText(StringUtil.EMPTY);

		if (height < 0)
		{
			GC gc = new GC(Display.getCurrent());
			gc.setFont(themeFont);
			Point textExtent = gc.textExtent("apt", SWT.DRAW_TRANSPARENT); //$NON-NLS-1$
			height = textExtent.y + (textExtent.y % 2 == 0 ? 4 : 5);
			gc.dispose();
		}

		Image image = imageRegistry.get(tag);

		if (image == null)
		{
			image = createTagImage(tagToolBar, height, width, tag, false);
		}
		item.setImage(image);

		Image hotImage = hotTagImageRegistry.get(tag);

		if (hotImage == null)
		{
			hotImage = createTagImage(tagToolBar, height, width, tag, true);
		}
		item.setHotImage(hotImage);

		return height;
	}

	private Image createTagImage(ToolBar tagToolBar, int height, int width, String tagName, boolean isHot)
	{
		if (tagWidth > 0)
		{
			width = tagWidth;
		}

		Image image = new Image(UIUtils.getDisplay(), width, height);
		GC gc = new GC(image);
		gc.setAntialias(SWT.ON);
		Color fg = gc.getForeground();
		Color bgColor = tagToolBar.getBackground();

		gc.setFont(themeFont);
		Point textSize = gc.textExtent(tagName);

		gc.setBackground(bgColor);
		gc.fillRectangle(0, 0, width, height);

		Color tempFg = null;
		Color tempBg = null;

		if (isHot)
		{

			tempFg = colorManager.getColor(defaultTagHotFg);
			tempBg = colorManager.getColor(defaultTagHotBg);

		}
		else
		{

			tempFg = colorManager.getColor(defaultTagFg);
			tempBg = colorManager.getColor(defaultTagBg);

		}

		gc.setForeground(tempFg);
		gc.setBackground(tempBg);

		gc.setAntialias(SWT.OFF);
		gc.fillRoundRectangle(1, 1, width - 2, height - 2, height - 2, height - 2);
		gc.setAntialias(SWT.ON);
		gc.drawRoundRectangle(0, 0, width - 1, height - 1, height - 1, height - 1);

		gc.setForeground(fg);

		gc.drawText(tagName, (width - textSize.x) / 2, (height - textSize.y) / 2, true);

		ImageData imageData = image.getImageData();
		imageData.transparentPixel = imageData.palette.getPixel(bgColor.getRGB());
		image.dispose();
		gc.dispose();

		image = new Image(UIUtils.getDisplay(), imageData);

		ImageRegistry registry = isHot ? hotTagImageRegistry : imageRegistry;
		registry.put(tagName, image);

		return image;
	}

	private Image getImage(URL url)
	{
		String urlString = url.toString();
		Image image = imageRegistry.get(urlString);

		if (image == null)
		{
			imageRegistry.put(urlString, ImageDescriptor.createFromURL(url));
			image = imageRegistry.get(urlString);
		}

		return image;
	}

	private Image getDisabledImage(URL url)
	{
		String urlString = url.toString();
		Image image = disabledImageRegistry.get(urlString);

		if (image == null)
		{
			ImageDescriptor descriptor = imageRegistry.getDescriptor(urlString);
			disabledImageRegistry.put(urlString, ImageDescriptor.createWithFlags(descriptor, SWT.IMAGE_DISABLE));
			image = disabledImageRegistry.get(urlString);
		}

		return image;
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar()
	{
		IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
		Action collapseAll = new Action()
		{

			/*
			 * (non-Javadoc)
			 * @see org.eclipse.jface.action.Action#run()
			 */
			@Override
			public void run()
			{
				for (ExpandItem item : expandItems.values())
				{
					boolean expanded = item.getExpanded();
					item.setExpanded(false);
					if (expanded)
					{
						updateScrollMinSize(item, false);
					}
				}
			}
		};

		collapseAll.setText(Messages.SnippetsView_Collapse_All_Action);
		collapseAll.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_ELCL_COLLAPSEALL));
		toolbarManager.add(collapseAll);

		Action expandAll = new Action()
		{
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.jface.action.Action#run()
			 */
			@Override
			public void run()
			{
				for (ExpandItem item : expandItems.values())
				{
					boolean expanded = item.getExpanded();
					expandDrawer((String) item.getData(), true);
					if (!expanded)
					{
						updateScrollMinSize(item, true);
					}
				}
			}
		};

		expandAll.setText(Messages.SnippetsView_Expand_All_Action);
		expandAll.setImageDescriptor(ScriptingUIPlugin.getImageDescriptor(ICONS_FULL_ELCL16_EXPANDALL_GIF));
		toolbarManager.add(expandAll);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose()
	{
		imageRegistry.dispose();
		disabledImageRegistry.dispose();
		hotTagImageRegistry.dispose();

		EclipseUtil.instanceScope().getNode(ThemePlugin.PLUGIN_ID).removePreferenceChangeListener(themeListener);
		snippetBundleListener.dispose();

		super.dispose();
		colorManager.dispose();

		getSite().getWorkbenchWindow().getSelectionService().removePostSelectionListener(selectionListener);

		IPartService service = (IPartService) getSite().getService(IPartService.class);
		if (service != null)
		{
			service.removePartListener(partListener);
		}
	}

	@Override
	public void setFocus()
	{
		expandBar.setFocus();
	}
}
