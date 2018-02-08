/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.extensions;

import java.lang.ref.WeakReference;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.editor.findbar.api.FindBarDecoratorFactory;
import com.aptana.editor.findbar.api.IFindBarDecorated;
import com.aptana.editor.findbar.api.IFindBarDecorator;

/**
 * Decorates an editor with a find bar (so, ctrl+F won't bring the default dialog, but a bar that appears similar to the
 * firefox search). The default usage for an editor that wants the find bar is: <code>
 *  public MyEditor() {
 *      super();
 *      fThemeableEditorFindBarExtension = new FindBarEditorExtension(this);
 *  }
 * 
 *  protected void createActions() {
 *      super.createActions();
 *      this.fThemeableEditorFindBarExtension.createFindBarActions();
 *  }
 * 
 *  protected void dispose() {
 *      super.dispose();
 *      this.fThemeableEditorFindBarExtension.dispose();
 *  }
 * 
 *  public void createPartControl(Composite parent) {
 *      Composite findBarComposite = (Composite) this.fThemeableEditorFindBarExtension.createFindBarComposite(parent);
 *      super.createPartControl(findBarComposite);
 *  }
 * 
 *  public Object getAdapter(Class adapter) {
 *      Object adaptable = this.fThemeableEditorFindBarExtension.getFindBarDecoratorAdapter(adapter);
 *      if (adaptable != null) {
 *          return adaptable;
 *      }
 *      return super.getAdapter(adapter);
 *  }
 * </code>
 * 
 * @author Fabio
 */
public class FindBarEditorExtension
{

	/**
	 * Keep a weak-reference to the editor which will have the find bar added.
	 */
	private WeakReference<ITextEditor> fEditor;

	private IFindBarDecorated fFindBarDecorated;
	private IFindBarDecorator fFindBarDecorator;

	/**
	 * @param editor
	 *            must either be an ITextEditor or provide adaptation to ITextEditor.class.
	 */
	public FindBarEditorExtension(IAdaptable editor)
	{
		ITextEditor textEditor;
		if (editor instanceof ITextEditor)
		{
			textEditor = (ITextEditor) editor;
		}
		else
		{
			textEditor = (ITextEditor) editor.getAdapter(ITextEditor.class);
			if (textEditor == null)
			{
				throw new AssertionError("The editor passed must either be an ITextEditor or provide adaptation to it."); //$NON-NLS-1$
			}
		}
		this.fEditor = new WeakReference<ITextEditor>(textEditor);
	}

	/**
	 * Add a composite that will hold the find bar and the actual editor. When called, the return must be the new
	 * Composite parent to be used for creating the editor.
	 */
	public Composite createFindBarComposite(Composite parent)
	{
		Composite findBarComposite = getFindBarDecorator().createFindBarComposite(parent);
		return findBarComposite;
	}

	/**
	 * Create the find bar
	 */
	public void createFindBar(ISourceViewer sourceViewer)
	{
		getFindBarDecorator().createFindBar(sourceViewer);
	}

	/**
	 * Create the actions for the find bar in the original editor.
	 */
	public void createFindBarActions()
	{
		getFindBarDecorator().installActions();
	}

	/**
	 * Delegates the context activation to the find bar
	 * 
	 * @param contextIds
	 */
	public void activateContexts(String[] contextIds)
	{
		getFindBarDecorator().activateContexts(contextIds);
	}

	/**
	 * Provide an adapter to IFindBarDecorator.
	 */
	@SuppressWarnings("rawtypes")
	public Object getFindBarDecoratorAdapter(Class adapter)
	{
		if (IFindBarDecorated.class.equals(adapter))
		{
			return getFindBarDecorated();
		}

		return null;
	}

	IFindBarDecorated getFindBarDecorated()
	{
		if (fFindBarDecorated == null)
		{
			fFindBarDecorated = new IFindBarDecorated()
			{
				public IFindBarDecorator getFindBarDecorator()
				{
					return FindBarEditorExtension.this.getFindBarDecorator();
				}
			};
		}
		return fFindBarDecorated;
	}

	private IFindBarDecorator getFindBarDecorator()
	{
		if (fFindBarDecorator == null)
		{
			fFindBarDecorator = FindBarDecoratorFactory.createFindBarDecorator(fEditor.get());
		}
		return fFindBarDecorator;
	}

	public void dispose()
	{
		this.fFindBarDecorated = null;
		if (fFindBarDecorator != null)
		{
			fFindBarDecorator.dispose();
		}
		this.fFindBarDecorator = null;
		if (this.fEditor != null)
		{
			this.fEditor.clear();
			this.fEditor = null;
		}
	}

}
