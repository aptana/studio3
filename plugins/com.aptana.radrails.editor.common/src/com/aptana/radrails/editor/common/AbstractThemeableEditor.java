package com.aptana.radrails.editor.common;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;

import com.aptana.editor.findbar.api.FindBarDecoratorFactory;
import com.aptana.editor.findbar.api.IFindBarDecorator;
import com.aptana.radrails.editor.common.actions.ShowScopesAction;
import com.aptana.radrails.editor.common.theme.ThemeUtil;

/**
 * Provides a way to override the editor fg, bg and selection fg, bg from what is set in global text editor color prefs.
 * TODO Need a way to override the caret color!
 * 
 * @author cwilliams
 * @author schitale
 */
@SuppressWarnings("restriction")
public abstract class AbstractThemeableEditor extends AbstractDecoratedTextEditor
{

	/**
	 * AbstractThemeableEditor
	 */
	public AbstractThemeableEditor()
	{
		super();
	}
	
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException
	{
		super.init(site, input);
		IContextService contextService = (IContextService) getSite().getService(IContextService.class);
		contextService.activateContext("com.aptana.editor.scope"); //$NON-NLS-1$
	}
	
    /* (non-Javadoc)
     * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#createPartControl(org.eclipse.swt.widgets.Composite)
     */
	@Override
    public void createPartControl(Composite parent)
	{
	    Composite findBarComposite = getfindBarDecorator().createFindBarComposite(parent);
	    super.createPartControl(findBarComposite);
	    getfindBarDecorator().createFindBar(getSourceViewer());
	}
    
	@Override
	protected void initializeEditor()
	{
		setPreferenceStore(new ChainedPreferenceStore(new IPreferenceStore[] {
				CommonEditorPlugin.getDefault().getPreferenceStore(), EditorsPlugin.getDefault().getPreferenceStore() }));
	}

	@Override
	protected void initializeViewerColors(ISourceViewer viewer)
	{
		ThemeUtil.getActiveTheme();
		super.initializeViewerColors(viewer);
	}

	@Override
	protected void handlePreferenceStoreChanged(PropertyChangeEvent event)
	{
		super.handlePreferenceStoreChanged(event);
		if (event.getProperty().equals(ThemeUtil.ACTIVE_THEME))
		{
			getSourceViewer().invalidateTextPresentation();
		}
	}
	
	@Override
	protected void createActions()
	{
		super.createActions();
		setAction(ShowScopesAction.COMMAND_ID, ShowScopesAction.create(this, getSourceViewer()));
		getfindBarDecorator().createActions();
	}
	 
	private IFindBarDecorator findBarDecorator;
	private IFindBarDecorator getfindBarDecorator()
	{
		if (findBarDecorator == null) {
			findBarDecorator = FindBarDecoratorFactory.createFindBarDecorator(this, getStatusLineManager());
		}
		return findBarDecorator;
	}
}
