package com.aptana.editor.common;

import java.lang.reflect.Method;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.progress.ProgressView;
import org.eclipse.ui.internal.views.markers.ExtendedMarkersView;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.navigator.IResourceNavigator;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.editor.common.theme.IThemeManager;
import com.aptana.editor.common.theme.Theme;

/**
 * This is a UIJob that tries to expand the influence of our themes to the JDT Editor; all Outline pages; Problems,
 * Tasks and Bookmarks views; JDT's Package Explorer; the Project Explorer; the Progress View.
 * 
 * @author cwilliams
 */
class InvasiveThemeHijacker extends UIJob implements IPartListener, IPreferenceChangeListener
{
	private static final String INVASIVE_THEMES = "enable_invasive_themes"; //$NON-NLS-1$

	public InvasiveThemeHijacker()
	{
		super("Installing invasive theme hijacker!");

		IEclipsePreferences prefs = new InstanceScope().getNode(CommonEditorPlugin.PLUGIN_ID);
		prefs.addPreferenceChangeListener(this);
	}

	protected boolean invasiveThemesEnabled()
	{
		return Platform.getPreferencesService().getBoolean(CommonEditorPlugin.PLUGIN_ID, INVASIVE_THEMES, false, null);
	}

	@Override
	public IStatus runInUIThread(IProgressMonitor monitor)
	{
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (invasiveThemesEnabled())
		{
			applyThemeToJDTEditor(getCurrentTheme());
			window.getActivePage().addPartListener(this);
			hijackCurrentViews(window);
		}
		else
		{
			window.getActivePage().removePartListener(this);
			// TODO Revert to JDT Editor defaults
			// TODO Revert to system default color/font for views.
		}
		return Status.OK_STATUS;
	}

	protected void hijackCurrentViews(IWorkbenchWindow window)
	{
		IViewReference[] refs = window.getActivePage().getViewReferences();
		for (IViewReference ref : refs)
		{
			hijackView(ref.getView(false));
		}
	}

	@SuppressWarnings({ "deprecation", "restriction", "nls" })
	protected void hijackView(IViewPart view)
	{
		if (view == null)
			return;
		// TODO What about ConsoleView? It's a pagebook, like outline...
		if (view instanceof IResourceNavigator)
		{
			IResourceNavigator navigator = (IResourceNavigator) view;
			hookTheme(navigator.getViewer().getTree());
			return;
		}
		else if (view instanceof ExtendedMarkersView) // Problems, Tasks, Bookmarks
		{
			try
			{
				Method m = ExtendedMarkersView.class.getDeclaredMethod("getViewer");
				m.setAccessible(true);
				TreeViewer treeViewer = (TreeViewer) m.invoke(view);
				hookTheme(treeViewer.getTree());
			}
			catch (Exception e)
			{
				// ignore
			}
			return;
		}
		else if (view instanceof ProgressView)
		{
			ProgressView navigator = (ProgressView) view;
			hookTheme(navigator.getViewer().getControl());
			return;
		}
		else if (view instanceof ContentOutline)
		{
			ContentOutline outline = (ContentOutline) view;
			hookTheme(outline.getCurrentPage().getControl());
			return;
		}
		else if (view.getClass().getName().equals("org.eclipse.ui.navigator.resources.ProjectExplorer"))
		{
			try
			{
				Method m = view.getClass().getMethod("getCommonViewer");
				TreeViewer treeViewer = (TreeViewer) m.invoke(view);
				hookTheme(treeViewer.getTree());
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
				hookTheme(treeViewer.getTree());
			}
			catch (Exception e)
			{
				// ignore
			}
		}

	}

	protected void hookTheme(Control tree)
	{
		tree.setBackground(CommonEditorPlugin.getDefault().getColorManager()
				.getColor(getCurrentTheme().getBackground()));
		tree.setForeground(CommonEditorPlugin.getDefault().getColorManager()
				.getColor(getCurrentTheme().getForeground()));
		tree.setFont(JFaceResources.getTextFont());
		if (tree instanceof Tree)
		{
			overrideTreeDrawing((Tree) tree);
		}
	}

	protected Theme getCurrentTheme()
	{
		return CommonEditorPlugin.getDefault().getThemeManager().getCurrentTheme();
	}

	private void overrideTreeDrawing(final Tree tree)
	{
		// TODO Hook up the font...
		// Override selection color to match what is set in theme
		tree.addListener(SWT.EraseItem, new Listener()
		{
			public void handleEvent(Event event)
			{
				if ((event.detail & SWT.SELECTED) != 0)
				{
					Tree tree = (Tree) event.widget;
					int clientWidth = tree.getClientArea().width;

					GC gc = event.gc;
					Color oldBackground = gc.getBackground();

					gc.setBackground(CommonEditorPlugin.getDefault().getColorManager().getColor(
							getCurrentTheme().getSelection()));
					gc.fillRectangle(0, event.y, clientWidth, event.height);
					gc.setBackground(oldBackground);

					event.detail &= ~SWT.SELECTED;
					event.detail &= ~SWT.BACKGROUND;
				}
			}
		});
	}

	@SuppressWarnings("nls")
	protected void applyThemeToJDTEditor(Theme theme)
	{
		// Set prefs for all editors
		setGeneralEditorValues(theme, new InstanceScope().getNode("org.eclipse.ui.texteditor"));

		// Now set for JDT...
		IEclipsePreferences prefs = new InstanceScope().getNode("org.eclipse.jdt.ui");
		setGeneralEditorValues(theme, prefs);

		// Set prefs for JDT so it's various tokens get colors that match up to our theme!
		// prefs = new InstanceScope().getNode("org.eclipse.jdt.ui");
		setToken(prefs, theme, "string.quoted.double.java", "java_string");
		setToken(prefs, theme, "source", "java_default");
		setToken(prefs, theme, "constant.language.java", "java_keyword");
		setToken(prefs, theme, "keyword.operator", "java_operator");
		setToken(prefs, theme, "keyword.control.java", "java_keyword_return"); // FIXME Should be a special keyword
																				// subtoken...
		setToken(prefs, theme, "comment", "java_single_line_comment");
		setToken(prefs, theme, "comment.block", "java_multi_line_comment");
		setToken(prefs, theme, "punctuation.bracket.java", "java_bracket");
		setSemanticToken(prefs, theme, "storage.type.java", "class");
		setSemanticToken(prefs, theme, "storage.type.java", "enum");
		setSemanticToken(prefs, theme, "storage.type.java", "interface");
		setSemanticToken(prefs, theme, "constant.numeric.java", "number");
		setSemanticToken(prefs, theme, "variable.parameter.java", "parameterVariable");
		setSemanticToken(prefs, theme, "variable.other.java", "localVariable");
		setSemanticToken(prefs, theme, "variable.other.java", "field");
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			CommonEditorPlugin.logError(e);
		}
	}

	protected void setGeneralEditorValues(Theme theme, IEclipsePreferences prefs)
	{
		prefs.putBoolean(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_FOREGROUND_SYSTEM_DEFAULT, false);
		prefs.put(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_FOREGROUND, toString(theme.getSelection()));

		prefs.putBoolean(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT, false);
		prefs.put(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND, toString(theme.getBackground()));

		prefs.put(AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND, toString(theme.getForeground()));
		prefs.putBoolean(AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND_SYSTEM_DEFAULT, false);

		prefs.put(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE_COLOR, toString(theme
				.getLineHighlight()));

		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			CommonEditorPlugin.logError(e);
		}
	}

	@SuppressWarnings("nls")
	protected void setToken(IEclipsePreferences prefs, Theme theme, String ourTokenType, String jdtToken)
	{
		TextAttribute attr = theme.getTextAttribute(ourTokenType);
		prefs.put(jdtToken, toString(attr.getForeground().getRGB()));
		prefs.putBoolean(jdtToken + "_bold", (attr.getStyle() & SWT.BOLD) != 0);
		prefs.putBoolean(jdtToken + "_italic", (attr.getStyle() & SWT.ITALIC) != 0);
		prefs.putBoolean(jdtToken + "_underline", (attr.getStyle() & TextAttribute.UNDERLINE) != 0);
		prefs.putBoolean(jdtToken + "_strikethrough", (attr.getStyle() & TextAttribute.STRIKETHROUGH) != 0);
	}

	@SuppressWarnings("nls")
	protected void setSemanticToken(IEclipsePreferences prefs, Theme theme, String ourTokenType, String jdtToken)
	{
		String prefix = "SemanticHighlighting.";
		jdtToken = prefix + jdtToken;
		TextAttribute attr = theme.getTextAttribute(ourTokenType);
		prefs.put(jdtToken, toString(attr.getForeground().getRGB()));
		prefs.putBoolean(jdtToken + ".bold", (attr.getStyle() & SWT.BOLD) != 0);
		prefs.putBoolean(jdtToken + ".italic", (attr.getStyle() & SWT.ITALIC) != 0);
		prefs.putBoolean(jdtToken + ".underline", (attr.getStyle() & TextAttribute.UNDERLINE) != 0);
		prefs.putBoolean(jdtToken + ".strikethrough", (attr.getStyle() & TextAttribute.STRIKETHROUGH) != 0);
	}

	@SuppressWarnings("nls")
	private static String toString(RGB selection)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(selection.red).append(",").append(selection.green).append(",").append(selection.blue);
		return builder.toString();
	}

	// IPreferenceChangeListener
	@Override
	public void preferenceChange(PreferenceChangeEvent event)
	{
		if (event.getKey().equals(IThemeManager.THEME_CHANGED))
		{
			// Theme has changed, need to apply new theme to editor and views
			applyThemeToJDTEditor(getCurrentTheme());
			hijackCurrentViews(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
		}
		else if (event.getKey().equals(INVASIVE_THEMES))
		{
			// enablement changed, schedule job to run (it'll stop if it's turned to false in "shouldRun()")
			schedule();
		}
	}

	// IPartListener
	@Override
	public void partOpened(IWorkbenchPart part)
	{
		if (!(part instanceof IViewPart))
			return;

		IViewPart view = (IViewPart) part;
		hijackView(view);
	}

	@Override
	public void partDeactivated(IWorkbenchPart part)
	{
	}

	@Override
	public void partClosed(IWorkbenchPart part)
	{
	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part)
	{
	}

	@Override
	public void partActivated(IWorkbenchPart part)
	{
	}
}
