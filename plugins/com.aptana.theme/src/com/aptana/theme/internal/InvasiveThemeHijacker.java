/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.theme.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.debug.internal.ui.views.memory.IMemoryViewPane;
import org.eclipse.debug.internal.ui.views.memory.MemoryView;
import org.eclipse.debug.ui.IDebugView;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.hyperlink.DefaultHyperlinkPresenter;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.search.ui.IQueryListener;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.team.ui.history.HistoryPage;
import org.eclipse.team.ui.history.IHistoryView;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.console.ConsoleView;
import org.eclipse.ui.internal.progress.ProgressView;
import org.eclipse.ui.internal.views.markers.ExtendedMarkersView;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.navigator.IResourceNavigator;
import org.eclipse.ui.views.properties.PropertySheet;
import org.osgi.framework.Version;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.util.EclipseUtil;
import com.aptana.theme.ConsoleThemer;
import com.aptana.theme.IControlThemerFactory;
import com.aptana.theme.IThemeManager;
import com.aptana.theme.Theme;
import com.aptana.theme.ThemePlugin;
import com.aptana.theme.preferences.IPreferenceConstants;
import com.aptana.ui.IAptanaHistory;

/**
 * This is a UIJob that tries to expand the influence of our themes to the JDT Editor; all Outline pages; Problems,
 * Tasks and Bookmarks views; JDT's Package Explorer; the Project Explorer; the Progress View.
 * 
 * @author cwilliams
 */
@SuppressWarnings({ "restriction", "deprecation" })
public class InvasiveThemeHijacker extends UIJob implements IPartListener, IPreferenceChangeListener, IStartup
{

	private ISelectionChangedListener pageListener;
	private Map<IViewPart, IQueryListener> queryListeners = new HashMap<IViewPart, IQueryListener>(3);
	private static boolean ranEarlyStartup = false;

	public InvasiveThemeHijacker()
	{
		super("Installing invasive theme hijacker!"); //$NON-NLS-1$
		setSystem(!EclipseUtil.showSystemJobs());
	}

	protected boolean invasiveThemesEnabled()
	{
		return Platform.getPreferencesService().getBoolean(ThemePlugin.PLUGIN_ID, IPreferenceConstants.INVASIVE_THEMES,
				false, null);
	}

	@Override
	public synchronized IStatus runInUIThread(IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, 4);
		if (sub.isCanceled())
		{
			return Status.CANCEL_STATUS;
		}
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (invasiveThemesEnabled())
		{
			if (window != null && window.getActivePage() != null)
			{
				window.getActivePage().addPartListener(this);
			}
			if (sub.isCanceled())
			{
				return Status.CANCEL_STATUS;
			}
			sub.setWorkRemaining(3);

			applyThemeToJDTEditor(getCurrentTheme(), false, sub.newChild(1));
			if (sub.isCanceled())
			{
				return Status.CANCEL_STATUS;
			}
			applyThemeToConsole(getCurrentTheme(), false, sub.newChild(1));
			if (sub.isCanceled())
			{
				return Status.CANCEL_STATUS;
			}
			hijackCurrentViews(window, false, sub.newChild(1));
		}
		else
		{
			if (window != null && window.getActivePage() != null)
			{
				window.getActivePage().removePartListener(this);
			}
			if (sub.isCanceled())
			{
				return Status.CANCEL_STATUS;
			}
			sub.setWorkRemaining(3);

			applyThemeToJDTEditor(getCurrentTheme(), true, sub.newChild(1));
			if (sub.isCanceled())
			{
				return Status.CANCEL_STATUS;
			}
			applyThemeToConsole(getCurrentTheme(), true, sub.newChild(1));
			if (sub.isCanceled())
			{
				return Status.CANCEL_STATUS;
			}
			hijackCurrentViews(window, true, sub.newChild(1));
		}
		sub.done();
		return Status.OK_STATUS;
	}

	private void applyThemeToConsole(Theme currentTheme, boolean revertToDefaults, IProgressMonitor monitor)
	{
		IEclipsePreferences prefs = new InstanceScope().getNode("org.eclipse.debug.ui"); //$NON-NLS-1$
		if (revertToDefaults)
		{
			prefs.remove("org.eclipse.debug.ui.errorColor"); //$NON-NLS-1$
			prefs.remove("org.eclipse.debug.ui.outColor"); //$NON-NLS-1$
			prefs.remove("org.eclipse.debug.ui.inColor"); //$NON-NLS-1$
			prefs.remove("org.eclipse.debug.ui.consoleBackground"); //$NON-NLS-1$
			prefs.remove("org.eclipse.debug.ui.PREF_CHANGED_VALUE_BACKGROUND"); //$NON-NLS-1$
		}
		else
		{
			setColor(prefs, "org.eclipse.debug.ui.errorColor", currentTheme, ConsoleThemer.CONSOLE_ERROR, new RGB(0x80, //$NON-NLS-1$
					0, 0));
			setColor(prefs, "org.eclipse.debug.ui.outColor", currentTheme, ConsoleThemer.CONSOLE_OUTPUT, //$NON-NLS-1$
					currentTheme.getForeground());
			setColor(prefs, "org.eclipse.debug.ui.inColor", currentTheme, ConsoleThemer.CONSOLE_INPUT, //$NON-NLS-1$
					currentTheme.getForeground());
			prefs.put("org.eclipse.debug.ui.consoleBackground", StringConverter.asString(currentTheme.getBackground())); //$NON-NLS-1$
			prefs.put("org.eclipse.debug.ui.PREF_CHANGED_VALUE_BACKGROUND", //$NON-NLS-1$
					StringConverter.asString(currentTheme.getBackgroundAsRGB("markup.changed.variable"))); //$NON-NLS-1$
		}
		if (monitor.isCanceled())
		{
			return;
		}
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			ThemePlugin.logError(e);
		}
	}

	protected void setColor(IEclipsePreferences prefs, String prefKey, Theme currentTheme, String tokenName,
			RGB defaultColor)
	{
		RGB rgb = defaultColor;
		if (currentTheme.hasEntry(tokenName))
		{
			rgb = currentTheme.getForegroundAsRGB(tokenName);
		}
		prefs.put(prefKey, StringConverter.asString(rgb));
	}

	protected void hijackCurrentViews(IWorkbenchWindow window, boolean revertToDefaults, IProgressMonitor monitor)
	{
		if (window == null || window.getActivePage() == null)
			return;
		IViewReference[] refs = window.getActivePage().getViewReferences();
		for (IViewReference ref : refs)
		{
			if (monitor.isCanceled())
			{
				return;
			}
			hijackView(ref.getView(false), revertToDefaults);
		}
		IEditorReference[] editorRefs = window.getActivePage().getEditorReferences();
		for (IEditorReference ref : editorRefs)
		{
			if (monitor.isCanceled())
			{
				return;
			}
			hijackEditor(ref.getEditor(false), revertToDefaults);
		}
	}

	protected void hijackView(final IViewPart view, final boolean revertToDefaults)
	{
		if (view == null)
			return;
		// TODO What about ConsoleView? It's a pagebook, like outline...
		if (view instanceof IResourceNavigator)
		{
			IResourceNavigator navigator = (IResourceNavigator) view;
			hookTheme(navigator.getViewer(), revertToDefaults);
			return;
		}
		else if (view instanceof ExtendedMarkersView) // Problems, Tasks, Bookmarks
		{
			if (new Version(EclipseUtil.getPluginVersion("org.eclipse.ui.ide")) //$NON-NLS-1$
					.compareTo(Version.parseVersion("3.6.0")) >= 0) //$NON-NLS-1$
			{
				try
				{
					Method m = ExtendedMarkersView.class.getDeclaredMethod("getViewer"); //$NON-NLS-1$
					m.setAccessible(true);
					TreeViewer treeViewer = (TreeViewer) m.invoke(view);
					hookTheme(treeViewer, revertToDefaults);
				}
				catch (Exception e)
				{
					// ignore
				}
			}
			return;
		}
		else if (view instanceof ProgressView)
		{
			try
			{
				// FIXME This isn't coloring anything right now
				Method m = ProgressView.class.getDeclaredMethod("getViewer"); //$NON-NLS-1$
				m.setAccessible(true);
				Viewer treeViewer = (Viewer) m.invoke(view);
				hookTheme(treeViewer, revertToDefaults);
			}
			catch (Exception e)
			{
				// ignore
			}
			return;
		}
		else if (view instanceof ContentOutline)
		{
			ContentOutline outline = (ContentOutline) view;
			IPage page = outline.getCurrentPage();
			String name = page.getClass().getName();
			if (name.endsWith("CommonOutlinePage") || name.endsWith("PyOutlinePage")) //$NON-NLS-1$ //$NON-NLS-2$
				return; // we already handle our own outlines
			Control control = page.getControl();
			if (control instanceof PageBook)
			{
				PageBook book = (PageBook) control;
				Control[] children = book.getChildren();
				if (children != null && children.length > 0)
				{
					for (Control child : children)
					{
						hookTheme(child, revertToDefaults);
					}
				}
				return;
			}
			else if (page instanceof MessagePage)
			{
				// Grab the label
				control = ((Composite) control).getChildren()[0];
			}
			hookTheme(control, revertToDefaults);
			return;
		}
		else if (view instanceof PropertySheet)
		{
			PropertySheet outline = (PropertySheet) view;
			IPage page = outline.getCurrentPage();
			hookTheme(page.getControl(), revertToDefaults);
			return;
		}
		else if (view instanceof IHistoryView)
		{
			if (!hijackHistory(view))
			{
				IHistoryView historyView = (IHistoryView) view;
				HistoryPage page = (HistoryPage) historyView.getHistoryPage();
				hookTheme(page.getControl(), revertToDefaults);
			}
			return;
		}
		else if (view instanceof IDebugView)
		{
			IDebugView debug = (IDebugView) view;
			Viewer viewer = debug.getViewer();
			hookTheme(viewer, revertToDefaults);
			return;
		}
		else if (view instanceof MemoryView)
		{
			MemoryView memory = (MemoryView) view;
			IMemoryViewPane[] memPaneArray = memory.getViewPanes();
			for (IMemoryViewPane memPane : memPaneArray)
			{
				hookTheme(memPane.getControl(), revertToDefaults);
			}
			return;
		}
		else if (view instanceof ConsoleView)
		{
			hijackConsole(view);
			return;
		}
		else if (view.getClass().getName().equals("org.eclipse.search2.internal.ui.SearchView")) //$NON-NLS-1$
		{
			hijackSearchView(view, revertToDefaults);
			return;
		}
		else if (view.getClass().getName().equals("org.eclipse.ui.navigator.resources.ProjectExplorer")) //$NON-NLS-1$
		{
			try
			{
				Method m = view.getClass().getMethod("getCommonViewer"); //$NON-NLS-1$
				TreeViewer treeViewer = (TreeViewer) m.invoke(view);
				hookTheme(treeViewer, revertToDefaults);
			}
			catch (Exception e)
			{
				// ignore
			}
			return;
		}
		else if (view.getClass().getName().equals("org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart")) //$NON-NLS-1$
		{
			try
			{
				Method m = view.getClass().getMethod("getTreeViewer"); //$NON-NLS-1$
				TreeViewer treeViewer = (TreeViewer) m.invoke(view);
				hookTheme(treeViewer, revertToDefaults);
			}
			catch (Exception e)
			{
				// ignore
			}
			return;
		}
		else if (view.getClass().getName().endsWith("CallHierarchyViewPart")) //$NON-NLS-1$
		{
			hijackCallHierarchy(view, revertToDefaults);
			return;
		}
	}

	protected void hijackSearchView(final IViewPart view, final boolean revertToDefaults)
	{
		PageBookView outline = (PageBookView) view;
		IPage page = outline.getCurrentPage();
		if (page != null)
		{
			if (page instanceof AbstractTextSearchViewPage)
			{
				// Need to hook to the table/tree, not the return value of getControl() (which is the pagebook)
				try
				{
					AbstractTextSearchViewPage blah = (AbstractTextSearchViewPage) page;
					Method m = blah.getClass().getDeclaredMethod("getViewer"); //$NON-NLS-1$
					m.setAccessible(true);
					Viewer v = (Viewer) m.invoke(blah);
					hookTheme(v, revertToDefaults);
				}
				catch (Exception e)
				{
					// ignore
				}
			}
			else
			{
				if (page.getClass().getName().endsWith("EmptySearchView")) //$NON-NLS-1$
				{
					// Have to explicitly hook to child label too, since it's bg is set to non-null value
					Composite comp = (Composite) page.getControl();
					Control label = comp.getChildren()[0];
					hookTheme(label, revertToDefaults);
					comp.layout();
				}
				hookTheme(page.getControl(), revertToDefaults);
			}
		}
		// Hook a query listener up to this view, so we can hijack the search result pages
		IQueryListener listener = queryListeners.get(view);
		if (listener == null)
		{
			listener = new IQueryListener()
			{
				public void queryStarting(ISearchQuery query)
				{

				}

				public void queryRemoved(ISearchQuery query)
				{
					hijackView(view, revertToDefaults);
				}

				public void queryFinished(ISearchQuery query)
				{

				}

				public void queryAdded(ISearchQuery query)
				{
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
					{
						public void run()
						{
							hijackView(view, revertToDefaults);
						}
					});

				}
			};
			NewSearchUI.addQueryListener(listener);
			queryListeners.put(view, listener);
		}
	}

	protected void hijackCallHierarchy(final IViewPart view, final boolean revertToDefaults)
	{
		try
		{
			Field f = view.getClass().getDeclaredField("fPagebook"); //$NON-NLS-1$
			f.setAccessible(true);
			PageBook pageBook = (PageBook) f.get(view);

			f = pageBook.getClass().getDeclaredField("currentPage"); //$NON-NLS-1$
			f.setAccessible(true);
			Control control = (Control) f.get(pageBook);
			if (control instanceof Label)
			{
				hookTheme(control, revertToDefaults);
				return;
			}

			Method m = view.getClass().getMethod("getViewer"); //$NON-NLS-1$
			TreeViewer treeViewer = (TreeViewer) m.invoke(view);
			hookTheme(treeViewer, revertToDefaults);

			m = view.getClass().getDeclaredMethod("getLocationViewer"); //$NON-NLS-1$
			if (m != null)
			{
				m.setAccessible(true);
				Viewer viewer = (Viewer) m.invoke(view);
				hookTheme(viewer, revertToDefaults);
			}
		}
		catch (Exception e)
		{
			// ignore
		}
	}

	protected void hookTheme(Control control, boolean revert)
	{
		if (revert)
		{
			getControlThemerFactory().dispose(control);
		}
		else
		{
			getControlThemerFactory().apply(control);
		}
	}

	protected void hookTheme(Viewer viewer, boolean revert)
	{
		if (revert)
		{
			getControlThemerFactory().dispose(viewer);
		}
		else
		{
			getControlThemerFactory().apply(viewer);
		}
	}

	protected IControlThemerFactory getControlThemerFactory()
	{
		return ThemePlugin.getDefault().getControlThemerFactory();
	}

	protected Theme getCurrentTheme()
	{
		return ThemePlugin.getDefault().getThemeManager().getCurrentTheme();
	}

	protected void applyThemeToJDTEditor(Theme theme, boolean revertToDefaults, IProgressMonitor monitor)
	{
		// Set prefs for all editors
		setHyperlinkValues(theme, new InstanceScope().getNode("org.eclipse.ui.workbench"), revertToDefaults); //$NON-NLS-1$
		setHyperlinkValues(theme, new InstanceScope().getNode(ThemePlugin.PLUGIN_ID), revertToDefaults);

		setGitAndMercurialValues(theme, new InstanceScope().getNode("org.eclipse.ui.workbench"), revertToDefaults); //$NON-NLS-1$

		setGeneralEditorValues(theme, new InstanceScope().getNode("org.eclipse.ui.texteditor"), revertToDefaults); //$NON-NLS-1$
		setEditorValues(theme, new InstanceScope().getNode("org.eclipse.ui.editors"), revertToDefaults); //$NON-NLS-1$

		if (monitor.isCanceled())
		{
			return;
		}

		// PDE
		IEclipsePreferences pdePrefs = new InstanceScope().getNode("org.eclipse.pde.ui"); //$NON-NLS-1$
		setGeneralEditorValues(theme, pdePrefs, revertToDefaults);
		setPDEEditorValues(theme, pdePrefs, revertToDefaults);

		if (monitor.isCanceled())
		{
			return;
		}

		// Ant
		IEclipsePreferences antPrefs = new InstanceScope().getNode("org.eclipse.ant.ui"); //$NON-NLS-1$
		setGeneralEditorValues(theme, antPrefs, revertToDefaults);
		setAntEditorValues(theme, antPrefs, revertToDefaults);

		if (monitor.isCanceled())
		{
			return;
		}

		// Now set for JDT...
		IEclipsePreferences prefs = new InstanceScope().getNode("org.eclipse.jdt.ui"); //$NON-NLS-1$
		setGeneralEditorValues(theme, prefs, revertToDefaults);

		// Set prefs for JDT so it's various tokens get colors that match up to our theme!
		// prefs = new InstanceScope().getNode("org.eclipse.jdt.ui");
		setToken(prefs, theme, "string.quoted.double.java", "java_string", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "source.java", "java_default", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "keyword", "java_keyword", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "keyword.operator", "java_operator", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "keyword.control.java", "java_keyword_return", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "comment.line.double-slash.java", "java_single_line_comment", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "comment.block", "java_multi_line_comment", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "punctuation.bracket.java", "java_bracket", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		// Javadoc
		//String TASK_TAG= "java_comment_task_tag"; //$NON-NLS-1$
		setToken(prefs, theme, "keyword.other.documentation.java", "java_doc_keyword", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "entity.name.tag.inline.any.html", "java_doc_tag", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "markup.underline.link.javadoc", "java_doc_link", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "comment.block.documentation.javadoc", "java_doc_default", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$

		// deprecated
		// setToken(prefs, theme, "entity.name.function.java", "java_method_name", revertToDefaults);
		setToken(prefs, theme, "entity.name.type.class.java", "java_type", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "storage.type.annotation.java", "java_annotation", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$

		// Semantic
		setSemanticToken(prefs, theme, "entity.name.type.class.java", "class", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "entity.name.type.enum.java", "enum", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "entity.name.type.interface.java", "interface", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "constant.numeric.java", "number", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "variable.parameter.java", "parameterVariable", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "constant.other.java", "staticField", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "constant.other.java", "staticFinalField", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "entity.name.function.java", "methodDeclarationName", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "invalid.deprecated.java", "deprecatedMember", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "storage.type.annotation.java", "annotation", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "constant.other.key.java", "annotationElementReference", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "source.java", "localVariable", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "source.java", "field", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "source.java", "staticMethodInvocation", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "source.java", "inheritedMethodInvocation", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "source.java", "abstractMethodInvocation", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "source.java", "localVariableDeclaration", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "source.java", "method", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "source.java", "typeParameter", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "source.java", "autoboxing", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setSemanticToken(prefs, theme, "source.java", "typeArgument", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$

		// Java *.properties files
		setToken(prefs, theme, "keyword.other.java-props", "pf_coloring_key", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "comment.line.number-sign.java-props", "pf_coloring_comment", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "string.java-props", "pf_coloring_value", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "punctuation.separator.key-value.java-props", "pf_coloring_assignment", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		setToken(prefs, theme, "string.interpolated.java-props", "pf_coloring_argument", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$

		// Override pair matching colors
		if (!revertToDefaults)
		{
			// FIXME Revert back to default value if revertToDefaults!
			prefs.put("matchingBracketsColor", StringConverter.asString(theme.getCharacterPairColor())); //$NON-NLS-1$
		}

		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			ThemePlugin.logError(e);
		}

		// Override JDT editor font
		Font fFont = JFaceResources.getFontRegistry().get(JFaceResources.TEXT_FONT);
		JFaceResources.getFontRegistry().put("org.eclipse.jdt.ui.editors.textfont", fFont.getFontData()); //$NON-NLS-1$
	}

	private void setGitAndMercurialValues(Theme theme, IEclipsePreferences prefs, boolean revertToDefaults)
	{
		if (prefs == null)
			return;
		if (revertToDefaults)
		{
			// EGit colors
			prefs.remove("org.eclipse.egit.ui.UncommittedChangeBackgroundColor"); //$NON-NLS-1$
			prefs.remove("org.eclipse.egit.ui.UncommittedChangeForegroundColor"); //$NON-NLS-1$

			// Mercurial colors
			prefs.remove("com.vectrace.mercurialeclipse.ui.colorsandfonts.addedForegroundColor"); //$NON-NLS-1$
			prefs.remove("com.vectrace.mercurialeclipse.ui.colorsandfonts.addedBackgroundColor"); //$NON-NLS-1$

			prefs.remove("com.vectrace.mercurialeclipse.ui.colorsandfonts.changedForegroundColor"); //$NON-NLS-1$
			prefs.remove("com.vectrace.mercurialeclipse.ui.colorsandfonts.changedBackgroundColor"); //$NON-NLS-1$

			prefs.remove("com.vectrace.mercurialeclipse.ui.colorsandfonts.removedForegroundColor"); //$NON-NLS-1$
			prefs.remove("com.vectrace.mercurialeclipse.ui.colorsandfonts.removedBackgroundColor"); //$NON-NLS-1$

			prefs.remove("com.vectrace.mercurialeclipse.ui.colorsandfonts.deletedForegroundColor"); //$NON-NLS-1$
			prefs.remove("com.vectrace.mercurialeclipse.ui.colorsandfonts.deletedBackgroundColor"); //$NON-NLS-1$

			prefs.remove("com.vectrace.mercurialeclipse.ui.colorsandfonts.unknownForegroundColor"); //$NON-NLS-1$
			prefs.remove("com.vectrace.mercurialeclipse.ui.colorsandfonts.unknownBackgroundColor"); //$NON-NLS-1$

			prefs.remove("com.vectrace.mercurialeclipse.ui.colorsandfonts.conflictForegroundColor"); //$NON-NLS-1$
			prefs.remove("com.vectrace.mercurialeclipse.ui.colorsandfonts.conflictBackgroundColor"); //$NON-NLS-1$

			prefs.remove("com.vectrace.mercurialeclipse.ui.colorsandfonts.IgnoredForegroundColor"); //$NON-NLS-1$
			prefs.remove("com.vectrace.mercurialeclipse.ui.colorsandfonts.IgnoredBackgroundColor"); //$NON-NLS-1$

		}
		else
		{
			TextAttribute changedFile = theme.getTextAttribute("markup.changed"); //$NON-NLS-1$
			TextAttribute addedFile = theme.getTextAttribute("markup.inserted"); //$NON-NLS-1$
			TextAttribute deletedFile = theme.getTextAttribute("markup.deleted"); //$NON-NLS-1$
			TextAttribute conflictFile = theme.getTextAttribute("invalid"); //$NON-NLS-1$

			// FIXME Grab colors from GitColors class, which should be moved to theme plugin and called something like
			// SCMColors or added to Theme class

			// EGit colors
			// TODO do we walso need to override font?
			prefs.put("org.eclipse.egit.ui.UncommittedChangeForegroundColor", //$NON-NLS-1$
					StringConverter.asString(addedFile.getForeground().getRGB()));
			prefs.put("org.eclipse.egit.ui.UncommittedChangeBackgroundColor", //$NON-NLS-1$
					StringConverter.asString(addedFile.getBackground().getRGB()));

			// Mercurial colors
			// TODO Do we also need to override fonts?
			prefs.put("com.vectrace.mercurialeclipse.ui.colorsandfonts.addedForegroundColor", //$NON-NLS-1$
					StringConverter.asString(addedFile.getForeground().getRGB()));
			prefs.put("com.vectrace.mercurialeclipse.ui.colorsandfonts.addedBackgroundColor", //$NON-NLS-1$
					StringConverter.asString(addedFile.getBackground().getRGB()));

			prefs.put("com.vectrace.mercurialeclipse.ui.colorsandfonts.changedForegroundColor", //$NON-NLS-1$
					StringConverter.asString(changedFile.getForeground().getRGB()));
			prefs.put("com.vectrace.mercurialeclipse.ui.colorsandfonts.changedBackgroundColor", //$NON-NLS-1$
					StringConverter.asString(changedFile.getBackground().getRGB()));

			prefs.put("com.vectrace.mercurialeclipse.ui.colorsandfonts.deletedForegroundColor", //$NON-NLS-1$
					StringConverter.asString(deletedFile.getForeground().getRGB()));
			prefs.put("com.vectrace.mercurialeclipse.ui.colorsandfonts.deletedBackgroundColor", //$NON-NLS-1$
					StringConverter.asString(deletedFile.getBackground().getRGB()));

			prefs.put("com.vectrace.mercurialeclipse.ui.colorsandfonts.removedForegroundColor", //$NON-NLS-1$
					StringConverter.asString(deletedFile.getForeground().getRGB()));
			prefs.put("com.vectrace.mercurialeclipse.ui.colorsandfonts.removedBackgroundColor", //$NON-NLS-1$
					StringConverter.asString(deletedFile.getBackground().getRGB()));

			prefs.put("com.vectrace.mercurialeclipse.ui.colorsandfonts.unknownForegroundColor", //$NON-NLS-1$
					StringConverter.asString(theme.getForeground()));
			prefs.put("com.vectrace.mercurialeclipse.ui.colorsandfonts.unknownBackgroundColor", //$NON-NLS-1$
					StringConverter.asString(theme.getBackground()));

			prefs.put("com.vectrace.mercurialeclipse.ui.colorsandfonts.conflictForegroundColor", //$NON-NLS-1$
					StringConverter.asString(conflictFile.getForeground().getRGB()));
			prefs.put("com.vectrace.mercurialeclipse.ui.colorsandfonts.conflictBackgroundColor", //$NON-NLS-1$
					StringConverter.asString(conflictFile.getBackground().getRGB()));

			prefs.put("com.vectrace.mercurialeclipse.ui.colorsandfonts.IgnoredForegroundColor", //$NON-NLS-1$
					StringConverter.asString(theme.getForeground()));
			prefs.put("com.vectrace.mercurialeclipse.ui.colorsandfonts.IgnoredBackgroundColor", //$NON-NLS-1$
					StringConverter.asString(theme.getBackground()));
		}
	}

	protected void setPDEEditorValues(Theme theme, IEclipsePreferences pdePrefs, boolean revertToDefaults)
	{
		if (pdePrefs == null)
			return;
		if (revertToDefaults)
		{
			pdePrefs.remove("editor.color.xml_comment"); //$NON-NLS-1$
			pdePrefs.remove("editor.color.instr"); //$NON-NLS-1$
			pdePrefs.remove("editor.color.string"); //$NON-NLS-1$
			pdePrefs.remove("editor.color.externalized_string"); //$NON-NLS-1$
			pdePrefs.remove("editor.color.default"); //$NON-NLS-1$
			pdePrefs.remove("editor.color.tag"); //$NON-NLS-1$
			pdePrefs.remove("editor.color.header_key"); //$NON-NLS-1$
			pdePrefs.remove("editor.color.header_value"); //$NON-NLS-1$
			pdePrefs.remove("editor.color.header_assignment"); //$NON-NLS-1$
			pdePrefs.remove("editor.color.header_osgi"); //$NON-NLS-1$
			pdePrefs.remove("editor.color.header_attributes"); //$NON-NLS-1$
		}
		else
		{
			// plugin.xml
			setToken(pdePrefs, theme, "comment.block.xml", "editor.color.xml_comment", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
			setToken(pdePrefs, theme, "meta.tag.preprocessor.xml", "editor.color.instr", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
			setToken(pdePrefs, theme, "string.quoted.double.xml", "editor.color.string", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
			setToken(pdePrefs, theme, "string.interpolated.xml", "editor.color.externalized_string", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
			setToken(pdePrefs, theme, "text.xml", "editor.color.default", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
			setToken(pdePrefs, theme, "entity.name.tag.xml", "editor.color.tag", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
			// manifest.mf
			setToken(pdePrefs, theme, "keyword.other.manifest", "editor.color.header_key", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
			setToken(pdePrefs, theme, "source.manifest", "editor.color.header_value", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
			setToken(pdePrefs, theme, "punctuation.separator.key-value.manifest", "editor.color.header_assignment", //$NON-NLS-1$ //$NON-NLS-2$
					revertToDefaults);
			setToken(pdePrefs, theme, "keyword.other.manifest.osgi", "editor.color.header_osgi", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
			setToken(pdePrefs, theme, "string.manifest", "editor.color.header_attributes", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		}
		try
		{
			pdePrefs.flush();
		}
		catch (BackingStoreException e)
		{
			ThemePlugin.logError(e);
		}
	}

	protected void setHyperlinkValues(Theme theme, IEclipsePreferences prefs, boolean revertToDefaults)
	{
		if (prefs == null)
			return;
		if (revertToDefaults)
		{
			// Console preferences
			prefs.remove(JFacePreferences.HYPERLINK_COLOR);
			prefs.remove(JFacePreferences.ACTIVE_HYPERLINK_COLOR);

			// Editor preferences
			prefs.remove(DefaultHyperlinkPresenter.HYPERLINK_COLOR_SYSTEM_DEFAULT);
			prefs.remove(DefaultHyperlinkPresenter.HYPERLINK_COLOR);

		}
		else
		{
			TextAttribute editorHyperlink = theme.getTextAttribute("hyperlink"); //$NON-NLS-1$

			prefs.put(JFacePreferences.HYPERLINK_COLOR,
					StringConverter.asString(editorHyperlink.getForeground().getRGB()));
			JFaceResources.getColorRegistry().put(JFacePreferences.HYPERLINK_COLOR,
					editorHyperlink.getForeground().getRGB());
			prefs.put(JFacePreferences.ACTIVE_HYPERLINK_COLOR,
					StringConverter.asString(editorHyperlink.getForeground().getRGB()));
			JFaceResources.getColorRegistry().put(JFacePreferences.ACTIVE_HYPERLINK_COLOR,
					editorHyperlink.getForeground().getRGB());
			prefs.putBoolean(DefaultHyperlinkPresenter.HYPERLINK_COLOR_SYSTEM_DEFAULT, false);
			prefs.put(DefaultHyperlinkPresenter.HYPERLINK_COLOR,
					StringConverter.asString(editorHyperlink.getForeground().getRGB()));

		}

	}

	protected void setGeneralEditorValues(Theme theme, IEclipsePreferences prefs, boolean revertToDefaults)
	{
		if (prefs == null)
			return;
		if (revertToDefaults)
		{
			prefs.remove(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_BACKGROUND);
			prefs.remove(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_FOREGROUND);
			prefs.remove(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND);
			prefs.remove(AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND);
			prefs.remove(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE_COLOR);
		}
		else
		{
			prefs.put(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_BACKGROUND,
					StringConverter.asString(theme.getSelectionAgainstBG()));
			prefs.put(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_FOREGROUND,
					StringConverter.asString(theme.getForeground()));
			prefs.put(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND, StringConverter.asString(theme.getBackground()));
			prefs.put(AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND, StringConverter.asString(theme.getForeground()));
			prefs.put(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE_COLOR,
					StringConverter.asString(theme.getLineHighlightAgainstBG()));
		}

		prefs.putBoolean(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_FOREGROUND_SYSTEM_DEFAULT, revertToDefaults);
		prefs.putBoolean(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT, revertToDefaults);
		prefs.putBoolean(AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND_SYSTEM_DEFAULT, revertToDefaults);

		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			ThemePlugin.logError(e);
		}
	}

	protected void setEditorValues(Theme theme, IEclipsePreferences prefs, boolean revertToDefaults)
	{
		// FIXME Check for overrides in theme
		if (revertToDefaults)
		{
			prefs.remove("occurrenceIndicationColor"); //$NON-NLS-1$
			prefs.remove("writeOccurrenceIndicationColor"); //$NON-NLS-1$
			prefs.remove("currentIPColor"); //$NON-NLS-1$
			prefs.remove("secondaryIPColor"); //$NON-NLS-1$

			prefs.putBoolean(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT, true);
			prefs.remove(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND);
			prefs.putBoolean(AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND_SYSTEM_DEFAULT, true);
			prefs.remove(AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND);
			prefs.putBoolean(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_BACKGROUND_SYSTEM_DEFAULT, true);
			prefs.remove(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_BACKGROUND);
			prefs.putBoolean(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_FOREGROUND_SYSTEM_DEFAULT, true);
			prefs.remove(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_FOREGROUND);

			// FIXME Revert back to default current line color...
			// prefs.put(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE_COLOR,
			// StringConverter.asString(theme.getLineHighlightAgainstBG()));
		}
		else
		{
			prefs.putBoolean(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT, false);
			prefs.put(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND, StringConverter.asString(theme.getBackground()));
			prefs.putBoolean(AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND_SYSTEM_DEFAULT, false);
			prefs.put(AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND, StringConverter.asString(theme.getForeground()));
			prefs.putBoolean(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_BACKGROUND_SYSTEM_DEFAULT, false);
			prefs.put(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_BACKGROUND,
					StringConverter.asString(theme.getSelectionAgainstBG()));
			prefs.putBoolean(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_FOREGROUND_SYSTEM_DEFAULT, false);
			prefs.put(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_FOREGROUND,
					StringConverter.asString(theme.getForeground()));

			prefs.put(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE_COLOR,
					StringConverter.asString(theme.getLineHighlightAgainstBG()));

			prefs.put("occurrenceIndicationColor", StringConverter.asString(theme.getSelectionAgainstBG())); //$NON-NLS-1$
			prefs.put("writeOccurrenceIndicationColor", StringConverter.asString(theme.getSelectionAgainstBG())); //$NON-NLS-1$
			// Override the debug line highlight colors
			prefs.put("currentIPColor", StringConverter.asString(theme.getBackgroundAsRGB("meta.diff.header"))); //$NON-NLS-1$ //$NON-NLS-2$
			prefs.put("secondaryIPColor", StringConverter.asString(theme.getBackgroundAsRGB("meta.diff.header"))); //$NON-NLS-1$ //$NON-NLS-2$
		}

		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			ThemePlugin.logError(e);
		}
	}

	protected void setAntEditorValues(Theme theme, IEclipsePreferences prefs, boolean revertToDefaults)
	{
		if (prefs == null)
			return;
		if (revertToDefaults)
		{
			prefs.remove("org.eclipse.ant.ui.commentsColor"); //$NON-NLS-1$
			prefs.remove("org.eclipse.ant.ui.processingInstructionsColor"); //$NON-NLS-1$
			prefs.remove("org.eclipse.ant.ui.constantStringsColor"); //$NON-NLS-1$
			prefs.remove("org.eclipse.ant.ui.textColor"); //$NON-NLS-1$
			prefs.remove("org.eclipse.ant.ui.tagsColor"); //$NON-NLS-1$
			prefs.remove("org.eclipse.ant.ui.dtdColor"); //$NON-NLS-1$
		}
		else
		{
			setToken(prefs, theme, "comment.block.xml.ant", "org.eclipse.ant.ui.commentsColor", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
			setToken(prefs, theme,
					"meta.tag.preprocessor.xml.ant", "org.eclipse.ant.ui.processingInstructionsColor", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
			setToken(prefs, theme,
					"string.quoted.double.xml.ant", "org.eclipse.ant.ui.constantStringsColor", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
			setToken(prefs, theme, "text.xml.ant", "org.eclipse.ant.ui.textColor", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
			setToken(prefs, theme, "entity.name.tag.target.xml.ant", "org.eclipse.ant.ui.tagsColor", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
			setToken(prefs, theme, "meta.tag.preprocessor.xml.ant", "org.eclipse.ant.ui.dtdColor", revertToDefaults); //$NON-NLS-1$ //$NON-NLS-2$
		}

		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			ThemePlugin.logError(e);
		}
	}

	protected void setToken(IEclipsePreferences prefs, Theme theme, String ourTokenType, String jdtToken,
			boolean revertToDefaults)
	{
		if (revertToDefaults)
		{
			prefs.remove(jdtToken);
			prefs.remove(jdtToken + "_bold"); //$NON-NLS-1$
			prefs.remove(jdtToken + "_italic"); //$NON-NLS-1$
			prefs.remove(jdtToken + "_underline"); //$NON-NLS-1$
			prefs.remove(jdtToken + "_strikethrough"); //$NON-NLS-1$
		}
		else
		{
			TextAttribute attr = theme.getTextAttribute(ourTokenType);
			prefs.put(jdtToken, StringConverter.asString(attr.getForeground().getRGB()));
			prefs.putBoolean(jdtToken + "_bold", (attr.getStyle() & SWT.BOLD) != 0); //$NON-NLS-1$
			prefs.putBoolean(jdtToken + "_italic", (attr.getStyle() & SWT.ITALIC) != 0); //$NON-NLS-1$
			prefs.putBoolean(jdtToken + "_underline", (attr.getStyle() & TextAttribute.UNDERLINE) != 0); //$NON-NLS-1$
			prefs.putBoolean(jdtToken + "_strikethrough", (attr.getStyle() & TextAttribute.STRIKETHROUGH) != 0); //$NON-NLS-1$
		}
	}

	protected void setSemanticToken(IEclipsePreferences prefs, Theme theme, String ourTokenType, String jdtToken,
			boolean revertToDefaults)
	{
		String prefix = "semanticHighlighting."; //$NON-NLS-1$
		jdtToken = prefix + jdtToken;
		if (revertToDefaults)
		{
			prefs.remove(jdtToken + ".color"); //$NON-NLS-1$
			prefs.remove(jdtToken + ".bold"); //$NON-NLS-1$
			prefs.remove(jdtToken + ".italic"); //$NON-NLS-1$
			prefs.remove(jdtToken + ".underline"); //$NON-NLS-1$
			prefs.remove(jdtToken + ".strikethrough"); //$NON-NLS-1$
			prefs.remove(jdtToken + ".enabled"); //$NON-NLS-1$
		}
		else
		{
			TextAttribute attr = theme.getTextAttribute(ourTokenType);
			prefs.put(jdtToken + ".color", StringConverter.asString(attr.getForeground().getRGB())); //$NON-NLS-1$
			prefs.putBoolean(jdtToken + ".bold", (attr.getStyle() & SWT.BOLD) != 0); //$NON-NLS-1$
			prefs.putBoolean(jdtToken + ".italic", (attr.getStyle() & SWT.ITALIC) != 0); //$NON-NLS-1$
			prefs.putBoolean(jdtToken + ".underline", (attr.getStyle() & TextAttribute.UNDERLINE) != 0); //$NON-NLS-1$
			prefs.putBoolean(jdtToken + ".strikethrough", (attr.getStyle() & TextAttribute.STRIKETHROUGH) != 0); //$NON-NLS-1$
			prefs.putBoolean(jdtToken + ".enabled", true); //$NON-NLS-1$
		}
	}

	// IPreferenceChangeListener
	public void preferenceChange(PreferenceChangeEvent event)
	{
		// If invaisive themes are on and we changed the theme, schedule. Also schedule if we toggled invasive theming.
		if (event.getKey().equals(IPreferenceConstants.INVASIVE_THEMES)
				|| (event.getKey().equals(IThemeManager.THEME_CHANGED) && invasiveThemesEnabled()))
		{
			cancel();
			schedule();
		}
	}

	protected void overrideSelectionColor(AbstractTextEditor editor)
	{
		try
		{
			if (Class.forName("com.aptana.editor.common.extensions.IThemeableEditor").isInstance(editor)) // we already handle our own editors //$NON-NLS-1$
				return;
		}
		catch (ClassNotFoundException e1)
		{
			// ignore
		}

		ISourceViewer sourceViewer = null;
		try
		{
			Method m = AbstractTextEditor.class.getDeclaredMethod("getSourceViewer"); //$NON-NLS-1$
			m.setAccessible(true);
			sourceViewer = (ISourceViewer) m.invoke(editor);
		}
		catch (Exception e)
		{
			// ignore
		}
		if (sourceViewer == null || sourceViewer.getTextWidget() == null)
			return;

		// Force selection color
		Color existingSelectionBG = sourceViewer.getTextWidget().getSelectionBackground();
		RGB selectionRGB = getCurrentTheme().getSelectionAgainstBG();
		if (!existingSelectionBG.getRGB().equals(selectionRGB))
		{
			sourceViewer.getTextWidget().setSelectionBackground(
					ThemePlugin.getDefault().getColorManager().getColor(selectionRGB));
		}
		if (!Platform.getOS().equals(Platform.OS_MACOSX))
		{
			// Linux and windows need selection fg set or we just see a block of color.
			sourceViewer.getTextWidget().setSelectionForeground(
					ThemePlugin.getDefault().getColorManager().getColor(getCurrentTheme().getForeground()));
		}
	}

	protected void hijackEditor(IEditorPart part, boolean revertToDefaults)
	{
		if (part instanceof AbstractTextEditor)
		{
			overrideSelectionColor((AbstractTextEditor) part);
		}
	}

	// IPartListener
	public void partOpened(IWorkbenchPart part)
	{
		if (part instanceof IEditorPart)
		{
			hijackEditor((IEditorPart) part, false);
			if (part instanceof MultiPageEditorPart)
			{
				MultiPageEditorPart multi = (MultiPageEditorPart) part;
				if (pageListener == null)
				{
					pageListener = new ISelectionChangedListener()
					{

						public void selectionChanged(SelectionChangedEvent event)
						{
							hijackOutline();
						}
					};
				}
				multi.getSite().getSelectionProvider().addSelectionChangedListener(pageListener);
			}
			return;
		}

		if (!(part instanceof IViewPart))
			return;

		IViewPart view = (IViewPart) part;
		hijackView(view, false);
	}

	public void partDeactivated(IWorkbenchPart part)
	{
		if (!(part instanceof IEditorPart))
			return;

		hijackOutline();
	}

	public void partClosed(IWorkbenchPart part)
	{
		if (part instanceof MultiPageEditorPart)
		{
			MultiPageEditorPart multi = (MultiPageEditorPart) part;
			if (pageListener != null)
			{
				multi.getSite().getSelectionProvider().removeSelectionChangedListener(pageListener);
			}
		}
		// If it's a search view, remove any query listeners for it!
		else if (part instanceof IViewPart)
		{
			IViewPart view = (IViewPart) part;
			if (queryListeners.containsKey(view))
			{
				NewSearchUI.removeQueryListener(queryListeners.remove(view));
			}
		}
	}

	public void partBroughtToTop(IWorkbenchPart part)
	{
		partActivated(part);
	}

	public void partActivated(final IWorkbenchPart part)
	{
		if (part instanceof IViewPart)
		{
			hijackHistory((IViewPart) part);
			hijackConsole((IViewPart) part);
			if (part.getClass().getName().endsWith("CallHierarchyViewPart")) //$NON-NLS-1$
			{
				Display.getCurrent().asyncExec(new Runnable()
				{
					public void run()
					{
						hijackView((IViewPart) part, false);
					}
				});
			}
		}
		if (!(part instanceof IEditorPart))
			return;

		hijackOutline();
	}

	protected boolean hijackHistory(IViewPart view)
	{
		if (view instanceof IHistoryView)
		{
			IHistoryView historyView = (IHistoryView) view;
			final HistoryPage page = (HistoryPage) historyView.getHistoryPage();
			if (page instanceof IAptanaHistory)
			{
				Display.getCurrent().asyncExec(new Runnable()
				{
					public void run()
					{
						((IAptanaHistory) page).setTheme(false);
					}
				});
				return true;
			}

		}
		return false;
	}

	protected void hijackConsole(IViewPart view)
	{
		if (view instanceof ConsoleView)
		{
			IPage currentPage = ((ConsoleView) view).getCurrentPage();
			hookTheme(currentPage.getControl(), false);
		}
	}

	protected void hijackOutline()
	{
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null || window.getActivePage() == null)
		{
			return;
		}
		IViewReference[] refs = window.getActivePage().getViewReferences();
		for (IViewReference ref : refs)
		{
			if (ref.getId().equals(IPageLayout.ID_OUTLINE))
			{
				hijackView(ref.getView(false), false);
				return;
			}
		}
	}

	/**
	 * Schedules itself to override Java/PDE views and editors' coloring only if invasive themes are enabled.
	 */
	public synchronized void earlyStartup()
	{
		if (ranEarlyStartup)
			return;
		ranEarlyStartup = true;
		if (invasiveThemesEnabled())
		{
			schedule();
		}
	}

	public void apply()
	{
		earlyStartup();
		IEclipsePreferences prefs = new InstanceScope().getNode(ThemePlugin.PLUGIN_ID);
		prefs.addPreferenceChangeListener(this);
	}

	public void dispose()
	{
		IEclipsePreferences prefs = new InstanceScope().getNode(ThemePlugin.PLUGIN_ID);
		prefs.removePreferenceChangeListener(this);

		pageListener = null;
	}
}
