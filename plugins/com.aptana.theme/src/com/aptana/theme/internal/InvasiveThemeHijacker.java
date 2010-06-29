package com.aptana.theme.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.debug.internal.ui.views.memory.IMemoryViewPane;
import org.eclipse.debug.internal.ui.views.memory.MemoryView;
import org.eclipse.debug.ui.IDebugView;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
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
import com.aptana.theme.IThemeManager;
import com.aptana.theme.Theme;
import com.aptana.theme.ThemePlugin;
import com.aptana.theme.TreeThemer;
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

	private Map<Tree, TreeThemer> themers = new HashMap<Tree, TreeThemer>();
	private ISelectionChangedListener pageListener;

	public InvasiveThemeHijacker()
	{
		super("Installing invasive theme hijacker!"); //$NON-NLS-1$

		IEclipsePreferences prefs = new InstanceScope().getNode(ThemePlugin.PLUGIN_ID);
		prefs.addPreferenceChangeListener(this);
	}

	protected boolean invasiveThemesEnabled()
	{
		return Platform.getPreferencesService().getBoolean(ThemePlugin.PLUGIN_ID, IPreferenceConstants.INVASIVE_THEMES,
				false, null);
	}

	@Override
	public IStatus runInUIThread(IProgressMonitor monitor)
	{
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (invasiveThemesEnabled())
		{
			applyThemeToJDTEditor(getCurrentTheme(), false);
			applyThemeToConsole(getCurrentTheme(), false);
			window.getActivePage().addPartListener(this);
			hijackCurrentViews(window, false);
		}
		else
		{
			if (window != null && window.getActivePage() != null)
			{
				window.getActivePage().removePartListener(this);
			}
			applyThemeToJDTEditor(getCurrentTheme(), true);
			applyThemeToConsole(getCurrentTheme(), true);
			hijackCurrentViews(window, true);
		}
		return Status.OK_STATUS;
	}

	@SuppressWarnings("nls")
	private void applyThemeToConsole(Theme currentTheme, boolean revertToDefaults)
	{

		IEclipsePreferences prefs = new InstanceScope().getNode("org.eclipse.debug.ui");
		if (revertToDefaults)
		{
			prefs.remove("org.eclipse.debug.ui.errorColor");
			prefs.remove("org.eclipse.debug.ui.outColor");
			prefs.remove("org.eclipse.debug.ui.inColor");
			prefs.remove("org.eclipse.debug.ui.consoleBackground");
		}
		else
		{
			setColor(prefs, "org.eclipse.debug.ui.errorColor", currentTheme, ConsoleThemer.CONSOLE_ERROR, new RGB(0x80,
					0, 0));
			setColor(prefs, "org.eclipse.debug.ui.outColor", currentTheme, ConsoleThemer.CONSOLE_OUTPUT,
					currentTheme.getForeground());
			setColor(prefs, "org.eclipse.debug.ui.inColor", currentTheme, ConsoleThemer.CONSOLE_INPUT,
					currentTheme.getForeground());
			prefs.put("org.eclipse.debug.ui.consoleBackground", StringConverter.asString(currentTheme.getBackground()));
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

	protected void hijackCurrentViews(IWorkbenchWindow window, boolean revertToDefaults)
	{
		if (window == null || window.getActivePage() == null)
			return;
		IViewReference[] refs = window.getActivePage().getViewReferences();
		for (IViewReference ref : refs)
		{
			hijackView(ref.getView(false), revertToDefaults);
		}
		IEditorReference[] editorRefs = window.getActivePage().getEditorReferences();
		for (IEditorReference ref : editorRefs)
		{
			hijackEditor(ref.getEditor(false), revertToDefaults);
		}
	}

	@SuppressWarnings({ "nls" })
	protected void hijackView(IViewPart view, boolean revertToDefaults)
	{
		if (view == null)
			return;
		// TODO What about ConsoleView? It's a pagebook, like outline...
		if (view instanceof IResourceNavigator)
		{
			IResourceNavigator navigator = (IResourceNavigator) view;
			hookTheme(navigator.getViewer().getTree(), revertToDefaults);
			return;
		}
		else if (view instanceof ExtendedMarkersView) // Problems, Tasks, Bookmarks
		{
			if (new Version(EclipseUtil.getPluginVersion("org.eclipse.ui.ide"))
					.compareTo(Version.parseVersion("3.6.0")) >= 0)
			{
				try
				{
					Method m = ExtendedMarkersView.class.getDeclaredMethod("getViewer");
					m.setAccessible(true);
					TreeViewer treeViewer = (TreeViewer) m.invoke(view);
					hookTheme(treeViewer.getTree(), revertToDefaults);
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
				Method m = ProgressView.class.getDeclaredMethod("getViewer");
				m.setAccessible(true);
				Viewer treeViewer = (Viewer) m.invoke(view);
				hookTheme(treeViewer.getControl(), revertToDefaults);
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
			if (page.getClass().getName().endsWith("CommonOutlinePage"))
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

		}
		else if (view instanceof IDebugView)
		{
			IDebugView debug = (IDebugView) view;
			Viewer viewer = debug.getViewer();
			hookTheme(viewer.getControl(), revertToDefaults);
		}
		else if (view instanceof MemoryView)
		{
			MemoryView memory = (MemoryView) view;
			IMemoryViewPane[] memPaneArray = memory.getViewPanes();

			for (IMemoryViewPane memPane : memPaneArray)
			{
				hookTheme(memPane.getControl(), revertToDefaults);
			}
		}
		else if (view instanceof ConsoleView)
		{
			hijackConsole(view);
		}
		// else if (view.getClass().getName().equals("org.eclipse.search2.internal.ui.SearchView"))
		// {
		// PageBookView outline = (PageBookView) view;
		// IPage page = outline.getCurrentPage();
		// hookTheme(page.getControl(), revertToDefaults);
		// return;
		// }
		else if (view.getClass().getName().equals("org.eclipse.ui.navigator.resources.ProjectExplorer"))
		{
			try
			{
				Method m = view.getClass().getMethod("getCommonViewer");
				TreeViewer treeViewer = (TreeViewer) m.invoke(view);
				hookTheme(treeViewer.getTree(), revertToDefaults);
			}
			catch (Exception e)
			{
				// ignore
			}
			return;
		}
		else if (view.getClass().getName().equals("org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart"))
		{
			try
			{
				Method m = view.getClass().getMethod("getTreeViewer");
				TreeViewer treeViewer = (TreeViewer) m.invoke(view);
				hookTheme(treeViewer.getTree(), revertToDefaults);
			}
			catch (Exception e)
			{
				// ignore
			}
		}
		else if (view.getClass().getName().endsWith("CallHierarchyViewPart"))
		{
			try
			{
				Field f = view.getClass().getDeclaredField("fPagebook");
				f.setAccessible(true);
				PageBook pageBook = (PageBook) f.get(view);

				f = pageBook.getClass().getDeclaredField("currentPage");
				f.setAccessible(true);
				Control control = (Control) f.get(pageBook);
				if (control instanceof Label)
				{
					hookTheme(control, revertToDefaults);
					return;
				}

				Method m = view.getClass().getMethod("getViewer");
				TreeViewer treeViewer = (TreeViewer) m.invoke(view);
				hookTheme(treeViewer.getTree(), revertToDefaults);

				m = view.getClass().getDeclaredMethod("getLocationViewer");
				if (m != null)
				{
					m.setAccessible(true);
					Viewer viewer = (Viewer) m.invoke(view);
					hookTheme(viewer.getControl(), revertToDefaults);
				}
			}
			catch (Exception e)
			{
				// ignore
			}
		}
	}

	protected void hookTheme(Control control, boolean revert)
	{
		if (control instanceof Tree)
		{
			overrideTreeDrawing((Tree) control, revert);
		}
		else
		{
			control.setRedraw(false);
			if (revert)
			{
				control.setBackground(null);
				control.setForeground(null);
				control.setFont(null);
			}
			else
			{
				control.setBackground(ThemePlugin.getDefault().getColorManager()
						.getColor(getCurrentTheme().getBackground()));
				control.setForeground(ThemePlugin.getDefault().getColorManager()
						.getColor(getCurrentTheme().getForeground()));
				control.setFont(JFaceResources.getTextFont());
			}
			control.setRedraw(true);
		}
	}

	protected Theme getCurrentTheme()
	{
		return ThemePlugin.getDefault().getThemeManager().getCurrentTheme();
	}

	private void overrideTreeDrawing(final Tree tree, boolean revertToDefaults)
	{
		if (revertToDefaults)
		{
			TreeThemer themer = themers.remove(tree);
			if (themer != null)
				themer.dispose();
		}
		else
		{
			TreeThemer themer = new TreeThemer(tree);
			themers.put(tree, themer);
			themer.apply();
		}
	}

	@SuppressWarnings("nls")
	protected void applyThemeToJDTEditor(Theme theme, boolean revertToDefaults)
	{
		// Set prefs for all editors
		setGeneralEditorValues(theme, new InstanceScope().getNode("org.eclipse.ui.texteditor"), revertToDefaults);
		setEditorValues(theme, new InstanceScope().getNode("org.eclipse.ui.editors"), revertToDefaults);

		// PDE
		IEclipsePreferences pdePrefs = new InstanceScope().getNode("org.eclipse.pde.ui");
		setGeneralEditorValues(theme, pdePrefs, revertToDefaults);
		setPDEEditorValues(theme, pdePrefs, revertToDefaults);

		// Ant
		IEclipsePreferences antPrefs = new InstanceScope().getNode("org.eclipse.ant.ui");
		setGeneralEditorValues(theme, antPrefs, revertToDefaults);
		setAntEditorValues(theme, antPrefs, revertToDefaults);

		// Now set for JDT...
		IEclipsePreferences prefs = new InstanceScope().getNode("org.eclipse.jdt.ui");
		setGeneralEditorValues(theme, prefs, revertToDefaults);

		// Set prefs for JDT so it's various tokens get colors that match up to our theme!
		// prefs = new InstanceScope().getNode("org.eclipse.jdt.ui");
		setToken(prefs, theme, "string.quoted.double.java", "java_string", revertToDefaults);
		setToken(prefs, theme, "source.java", "java_default", revertToDefaults);
		setToken(prefs, theme, "keyword", "java_keyword", revertToDefaults);
		setToken(prefs, theme, "keyword.operator", "java_operator", revertToDefaults);
		setToken(prefs, theme, "keyword.control.java", "java_keyword_return", revertToDefaults);
		setToken(prefs, theme, "comment.line.double-slash.java", "java_single_line_comment", revertToDefaults);
		setToken(prefs, theme, "comment.block", "java_multi_line_comment", revertToDefaults);
		setToken(prefs, theme, "punctuation.bracket.java", "java_bracket", revertToDefaults);
		// Javadoc
		//String TASK_TAG= "java_comment_task_tag"; //$NON-NLS-1$
		setToken(prefs, theme, "keyword.other.documentation.java", "java_doc_keyword", revertToDefaults);
		setToken(prefs, theme, "entity.name.tag.inline.any.html", "java_doc_tag", revertToDefaults);
		setToken(prefs, theme, "markup.underline.link.javadoc", "java_doc_link", revertToDefaults);
		setToken(prefs, theme, "comment.block.documentation.javadoc", "java_doc_default", revertToDefaults);

		// deprecated
		// setToken(prefs, theme, "entity.name.function.java", "java_method_name", revertToDefaults);
		setToken(prefs, theme, "entity.name.type.class.java", "java_type", revertToDefaults);
		setToken(prefs, theme, "storage.type.annotation.java", "java_annotation", revertToDefaults);

		// Semantic
		setSemanticToken(prefs, theme, "entity.name.type.class.java", "class", revertToDefaults);
		setSemanticToken(prefs, theme, "entity.name.type.enum.java", "enum", revertToDefaults);
		setSemanticToken(prefs, theme, "entity.name.type.interface.java", "interface", revertToDefaults);
		setSemanticToken(prefs, theme, "constant.numeric.java", "number", revertToDefaults);
		setSemanticToken(prefs, theme, "variable.parameter.java", "parameterVariable", revertToDefaults);
		setSemanticToken(prefs, theme, "constant.other.java", "staticField", revertToDefaults);
		setSemanticToken(prefs, theme, "constant.other.java", "staticFinalField", revertToDefaults);
		setSemanticToken(prefs, theme, "entity.name.function.java", "methodDeclarationName", revertToDefaults);
		setSemanticToken(prefs, theme, "invalid.deprecated.java", "deprecatedMember", revertToDefaults);
		setSemanticToken(prefs, theme, "storage.type.annotation.java", "annotation", revertToDefaults);
		setSemanticToken(prefs, theme, "constant.other.key.java", "annotationElementReference", revertToDefaults);
		setSemanticToken(prefs, theme, "source.java", "localVariable", revertToDefaults);
		setSemanticToken(prefs, theme, "source.java", "field", revertToDefaults);
		setSemanticToken(prefs, theme, "source.java", "staticMethodInvocation", revertToDefaults);
		setSemanticToken(prefs, theme, "source.java", "inheritedMethodInvocation", revertToDefaults);
		setSemanticToken(prefs, theme, "source.java", "abstractMethodInvocation", revertToDefaults);
		setSemanticToken(prefs, theme, "source.java", "localVariableDeclaration", revertToDefaults);
		setSemanticToken(prefs, theme, "source.java", "method", revertToDefaults);
		setSemanticToken(prefs, theme, "source.java", "typeParameter", revertToDefaults);
		setSemanticToken(prefs, theme, "source.java", "autoboxing", revertToDefaults);
		setSemanticToken(prefs, theme, "source.java", "typeArgument", revertToDefaults);

		// Java *.properties files
		setToken(prefs, theme, "keyword.other.java-props", "pf_coloring_key", revertToDefaults);
		setToken(prefs, theme, "comment.line.number-sign.java-props", "pf_coloring_comment", revertToDefaults);
		setToken(prefs, theme, "string.java-props", "pf_coloring_value", revertToDefaults);
		setToken(prefs, theme, "punctuation.separator.key-value.java-props", "pf_coloring_assignment", revertToDefaults);
		setToken(prefs, theme, "string.interpolated.java-props", "pf_coloring_argument", revertToDefaults);

		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			ThemePlugin.logError(e);
		}
	}

	@SuppressWarnings("nls")
	protected void setPDEEditorValues(Theme theme, IEclipsePreferences pdePrefs, boolean revertToDefaults)
	{
		if (pdePrefs == null)
			return;
		if (revertToDefaults)
		{
			pdePrefs.remove("editor.color.xml_comment");
			pdePrefs.remove("editor.color.instr");
			pdePrefs.remove("editor.color.string");
			pdePrefs.remove("editor.color.externalized_string");
			pdePrefs.remove("editor.color.default");
			pdePrefs.remove("editor.color.tag");
			pdePrefs.remove("editor.color.header_key");
			pdePrefs.remove("editor.color.header_value");
			pdePrefs.remove("editor.color.header_assignment");
			pdePrefs.remove("editor.color.header_osgi");
			pdePrefs.remove("editor.color.header_attributes");
		}
		else
		{
			// plugin.xml
			setToken(pdePrefs, theme, "comment.block.xml", "editor.color.xml_comment", revertToDefaults);
			setToken(pdePrefs, theme, "meta.tag.preprocessor.xml", "editor.color.instr", revertToDefaults);
			setToken(pdePrefs, theme, "string.quoted.double.xml", "editor.color.string", revertToDefaults);
			setToken(pdePrefs, theme, "string.interpolated.xml", "editor.color.externalized_string", revertToDefaults);
			setToken(pdePrefs, theme, "text.xml", "editor.color.default", revertToDefaults);
			setToken(pdePrefs, theme, "entity.name.tag.xml", "editor.color.tag", revertToDefaults);
			// manifest.mf
			setToken(pdePrefs, theme, "keyword.other.manifest", "editor.color.header_key", revertToDefaults);
			setToken(pdePrefs, theme, "source.manifest", "editor.color.header_value", revertToDefaults);
			setToken(pdePrefs, theme, "punctuation.separator.key-value.manifest", "editor.color.header_assignment",
					revertToDefaults);
			setToken(pdePrefs, theme, "keyword.other.manifest.osgi", "editor.color.header_osgi", revertToDefaults);
			setToken(pdePrefs, theme, "string.manifest", "editor.color.header_attributes", revertToDefaults);
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

	protected void setGeneralEditorValues(Theme theme, IEclipsePreferences prefs, boolean revertToDefaults)
	{
		if (prefs == null)
			return;
		if (revertToDefaults)
		{
			prefs.remove(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_FOREGROUND);
			prefs.remove(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND);
			prefs.remove(AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND);
			prefs.remove(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE_COLOR);
		}
		else
		{
			prefs.put(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_FOREGROUND,
					StringConverter.asString(theme.getSelection()));
			prefs.put(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND, StringConverter.asString(theme.getBackground()));
			prefs.put(AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND, StringConverter.asString(theme.getForeground()));
			prefs.put(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE_COLOR,
					StringConverter.asString(theme.getLineHighlight()));
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
		if (revertToDefaults)
		{
			prefs.remove("occurrenceIndicationColor"); //$NON-NLS-1$
			prefs.remove("writeOccurrenceIndicationColor"); //$NON-NLS-1$
		}
		else
		{
			prefs.put("occurrenceIndicationColor", StringConverter.asString(theme.getSelection())); //$NON-NLS-1$
			prefs.put("writeOccurrenceIndicationColor", StringConverter.asString(theme.getSelection())); //$NON-NLS-1$
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

	@SuppressWarnings("nls")
	protected void setToken(IEclipsePreferences prefs, Theme theme, String ourTokenType, String jdtToken,
			boolean revertToDefaults)
	{
		if (revertToDefaults)
		{
			prefs.remove(jdtToken);
			prefs.remove(jdtToken + "_bold");
			prefs.remove(jdtToken + "_italic");
			prefs.remove(jdtToken + "_underline");
			prefs.remove(jdtToken + "_strikethrough");
		}
		else
		{
			TextAttribute attr = theme.getTextAttribute(ourTokenType);
			prefs.put(jdtToken, StringConverter.asString(attr.getForeground().getRGB()));
			prefs.putBoolean(jdtToken + "_bold", (attr.getStyle() & SWT.BOLD) != 0);
			prefs.putBoolean(jdtToken + "_italic", (attr.getStyle() & SWT.ITALIC) != 0);
			prefs.putBoolean(jdtToken + "_underline", (attr.getStyle() & TextAttribute.UNDERLINE) != 0);
			prefs.putBoolean(jdtToken + "_strikethrough", (attr.getStyle() & TextAttribute.STRIKETHROUGH) != 0);
		}
	}

	@SuppressWarnings("nls")
	protected void setSemanticToken(IEclipsePreferences prefs, Theme theme, String ourTokenType, String jdtToken,
			boolean revertToDefaults)
	{
		String prefix = "semanticHighlighting.";
		jdtToken = prefix + jdtToken;
		if (revertToDefaults)
		{
			prefs.remove(jdtToken + ".color");
			prefs.remove(jdtToken + ".bold");
			prefs.remove(jdtToken + ".italic");
			prefs.remove(jdtToken + ".underline");
			prefs.remove(jdtToken + ".strikethrough");
			prefs.remove(jdtToken + ".enabled");
		}
		else
		{
			TextAttribute attr = theme.getTextAttribute(ourTokenType);
			prefs.put(jdtToken + ".color", StringConverter.asString(attr.getForeground().getRGB()));
			prefs.putBoolean(jdtToken + ".bold", (attr.getStyle() & SWT.BOLD) != 0);
			prefs.putBoolean(jdtToken + ".italic", (attr.getStyle() & SWT.ITALIC) != 0);
			prefs.putBoolean(jdtToken + ".underline", (attr.getStyle() & TextAttribute.UNDERLINE) != 0);
			prefs.putBoolean(jdtToken + ".strikethrough", (attr.getStyle() & TextAttribute.STRIKETHROUGH) != 0);
			prefs.putBoolean(jdtToken + ".enabled", true);
		}
	}

	// IPreferenceChangeListener
	@Override
	public void preferenceChange(PreferenceChangeEvent event)
	{
		if (event.getKey().equals(IThemeManager.THEME_CHANGED)
				|| event.getKey().equals(IPreferenceConstants.INVASIVE_THEMES))
		{
			// enablement changed, schedule job to run (it'll stop if it's turned to false in "shouldRun()")
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
		sourceViewer.getTextWidget().setSelectionBackground(
				ThemePlugin.getDefault().getColorManager().getColor(getCurrentTheme().getSelection()));
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
	@Override
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

						@Override
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

	@Override
	public void partDeactivated(IWorkbenchPart part)
	{
		if (!(part instanceof IEditorPart))
			return;

		hijackOutline();
	}

	@Override
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
	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part)
	{
		partActivated(part);
	}

	@Override
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

					@Override
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
			HistoryPage page = (HistoryPage) historyView.getHistoryPage();
			if (page instanceof IAptanaHistory)
			{
				((IAptanaHistory) page).setTheme(false);
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

	@Override
	public void earlyStartup()
	{
		schedule();
	}
}
