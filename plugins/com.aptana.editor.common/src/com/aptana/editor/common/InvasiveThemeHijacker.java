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
import org.eclipse.jface.viewers.Viewer;
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
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.navigator.IResourceNavigator;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.editor.common.outline.CommonOutlinePage;
import com.aptana.editor.common.preferences.IPreferenceConstants;
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

	private Listener listener;

	public InvasiveThemeHijacker()
	{
		super("Installing invasive theme hijacker!"); //$NON-NLS-1$

		IEclipsePreferences prefs = new InstanceScope().getNode(CommonEditorPlugin.PLUGIN_ID);
		prefs.addPreferenceChangeListener(this);
	}

	protected boolean invasiveThemesEnabled()
	{
		return Platform.getPreferencesService().getBoolean(CommonEditorPlugin.PLUGIN_ID,
				IPreferenceConstants.INVASIVE_THEMES, false, null);
	}

	@Override
	public IStatus runInUIThread(IProgressMonitor monitor)
	{
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (invasiveThemesEnabled())
		{
			applyThemeToJDTEditor(getCurrentTheme(), false);
			window.getActivePage().addPartListener(this);
			hijackCurrentViews(window, false);
		}
		else
		{
			window.getActivePage().removePartListener(this);
			applyThemeToJDTEditor(getCurrentTheme(), true);
			hijackCurrentViews(window, true);
		}
		return Status.OK_STATUS;
	}

	protected void hijackCurrentViews(IWorkbenchWindow window, boolean revertToDefaults)
	{
		IViewReference[] refs = window.getActivePage().getViewReferences();
		for (IViewReference ref : refs)
		{
			hijackView(ref.getView(false), revertToDefaults);
		}
	}

	@SuppressWarnings({ "deprecation", "restriction", "nls" })
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
			if (page instanceof CommonOutlinePage)
				return; // we already handle our own outlines
			hookTheme(page.getControl(), revertToDefaults);
			return;
		}
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
	}

	protected void hookTheme(Control tree, boolean revert)
	{
		if (revert)
		{
			tree.setBackground(null);
			tree.setForeground(null);
			tree.setFont(null);
		}
		else
		{
			tree.setBackground(CommonEditorPlugin.getDefault().getColorManager().getColor(
					getCurrentTheme().getBackground()));
			tree.setForeground(CommonEditorPlugin.getDefault().getColorManager().getColor(
					getCurrentTheme().getForeground()));
			tree.setFont(JFaceResources.getTextFont());
		}
		if (tree instanceof Tree)
		{
			overrideTreeDrawing((Tree) tree, revert);
		}
	}

	protected Theme getCurrentTheme()
	{
		return CommonEditorPlugin.getDefault().getThemeManager().getCurrentTheme();
	}

	private void overrideTreeDrawing(final Tree tree, boolean revertToDefaults)
	{
		if (listener == null)
		{
			listener = new Listener()
			{
				public void handleEvent(Event event)
				{
					// Override selection color to match what is set in theme
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
			};
		}
		if (revertToDefaults)
		{
			tree.removeListener(SWT.EraseItem, listener);
		}
		else
		{
			tree.addListener(SWT.EraseItem, listener);
		}
	}

	@SuppressWarnings("nls")
	protected void applyThemeToJDTEditor(Theme theme, boolean revertToDefaults)
	{
		// Set prefs for all editors
		setGeneralEditorValues(theme, new InstanceScope().getNode("org.eclipse.ui.texteditor"), revertToDefaults);

		// Now set for JDT...
		IEclipsePreferences prefs = new InstanceScope().getNode("org.eclipse.jdt.ui");
		setGeneralEditorValues(theme, prefs, revertToDefaults);

		// Set prefs for JDT so it's various tokens get colors that match up to our theme!
		// prefs = new InstanceScope().getNode("org.eclipse.jdt.ui");
		setToken(prefs, theme, "string.quoted.double.java", "java_string", revertToDefaults);
		setToken(prefs, theme, "source", "java_default", revertToDefaults);
		setToken(prefs, theme, "constant.language.java", "java_keyword", revertToDefaults);
		setToken(prefs, theme, "keyword.operator", "java_operator", revertToDefaults);
		setToken(prefs, theme, "keyword.control.java", "java_keyword_return", revertToDefaults);
		setToken(prefs, theme, "comment", "java_single_line_comment", revertToDefaults);
		setToken(prefs, theme, "comment.block", "java_multi_line_comment", revertToDefaults);
		setToken(prefs, theme, "punctuation.bracket.java", "java_bracket", revertToDefaults);
		setSemanticToken(prefs, theme, "storage.type.java", "class", revertToDefaults);
		setSemanticToken(prefs, theme, "storage.type.java", "enum", revertToDefaults);
		setSemanticToken(prefs, theme, "storage.type.java", "interface", revertToDefaults);
		setSemanticToken(prefs, theme, "constant.numeric.java", "number", revertToDefaults);
		setSemanticToken(prefs, theme, "variable.parameter.java", "parameterVariable", revertToDefaults);
		setSemanticToken(prefs, theme, "variable.other.java", "localVariable", revertToDefaults);
		setSemanticToken(prefs, theme, "variable.other.java", "field", revertToDefaults);
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			CommonEditorPlugin.logError(e);
		}
	}

	protected void setGeneralEditorValues(Theme theme, IEclipsePreferences prefs, boolean revertToDefaults)
	{
		if (revertToDefaults)
		{
			prefs.remove(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_FOREGROUND);
			prefs.remove(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND);
			prefs.remove(AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND);
			prefs.remove(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE_COLOR);
		}
		else
		{
			prefs.put(AbstractTextEditor.PREFERENCE_COLOR_SELECTION_FOREGROUND, toString(theme.getSelection()));
			prefs.put(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND, toString(theme.getBackground()));
			prefs.put(AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND, toString(theme.getForeground()));
			prefs.put(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE_COLOR, toString(theme
					.getLineHighlight()));
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
			CommonEditorPlugin.logError(e);
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
			prefs.put(jdtToken, toString(attr.getForeground().getRGB()));
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
		String prefix = "SemanticHighlighting.";
		jdtToken = prefix + jdtToken;
		if (revertToDefaults)
		{
			prefs.remove(jdtToken);
			prefs.remove(jdtToken + ".bold");
			prefs.remove(jdtToken + ".italic");
			prefs.remove(jdtToken + ".underline");
			prefs.remove(jdtToken + ".strikethrough");
		}
		else
		{
			TextAttribute attr = theme.getTextAttribute(ourTokenType);
			prefs.put(jdtToken, toString(attr.getForeground().getRGB()));
			prefs.putBoolean(jdtToken + ".bold", (attr.getStyle() & SWT.BOLD) != 0);
			prefs.putBoolean(jdtToken + ".italic", (attr.getStyle() & SWT.ITALIC) != 0);
			prefs.putBoolean(jdtToken + ".underline", (attr.getStyle() & TextAttribute.UNDERLINE) != 0);
			prefs.putBoolean(jdtToken + ".strikethrough", (attr.getStyle() & TextAttribute.STRIKETHROUGH) != 0);
		}
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
			if (invasiveThemesEnabled())
			{
				applyThemeToJDTEditor(getCurrentTheme(), false);
				hijackCurrentViews(PlatformUI.getWorkbench().getActiveWorkbenchWindow(), false);
			}
		}
		else if (event.getKey().equals(IPreferenceConstants.INVASIVE_THEMES))
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
		hijackView(view, false);
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
