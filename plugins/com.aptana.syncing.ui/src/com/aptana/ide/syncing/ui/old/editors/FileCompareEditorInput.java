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
 * with certain Eclipse Public Licensed code and certain additional terms
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
package com.aptana.ide.syncing.ui.old.editors;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.internal.BufferedResourceNode;
import org.eclipse.compare.internal.Utilities;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.DiffTreeViewer;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.compare.structuremergeviewer.IDiffContainer;
import org.eclipse.compare.structuremergeviewer.IDiffElement;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
@SuppressWarnings("restriction")
public class FileCompareEditorInput extends CompareEditorInput
{

	private Object fRoot;
	private FileNode fLeft;
	private FileNode fRight;
	private File fLeftResource;
	private File fRightResource;
	private DiffTreeViewer fDiffViewer;
	private IAction fOpenAction;

	class MyDiffNode extends DiffNode
	{

		private boolean fDirty = false;
		private ITypedElement fLastId;
		private String fLastName;

		/**
		 * MyDiffNode constructor
		 * 
		 * @param parent
		 * @param description
		 * @param ancestor
		 * @param left
		 * @param right
		 */
		public MyDiffNode(IDiffContainer parent, int description, ITypedElement ancestor, ITypedElement left,
				ITypedElement right)
		{
			super(parent, description, ancestor, left, right);
		}

		/**
		 * @see org.eclipse.compare.structuremergeviewer.DiffNode#fireChange()
		 */
		public void fireChange()
		{
			super.fireChange();
			setDirty(true);
			fDirty = true;
			if (fDiffViewer != null)
				fDiffViewer.refresh(this);
		}

		void clearDirty()
		{
			fDirty = false;
		}

		/**
		 * @see org.eclipse.compare.structuremergeviewer.DiffNode#getName()
		 */
		public String getName()
		{
			if (fLastName == null)
				fLastName = super.getName();
			if (fDirty)
				return '<' + fLastName + '>';
			return fLastName;
		}

		/**
		 * @see org.eclipse.compare.structuremergeviewer.DiffNode#getId()
		 */
		public ITypedElement getId()
		{
			ITypedElement id = super.getId();
			if (id == null)
				return fLastId;
			fLastId = id;
			return id;
		}
	}

	/**
	 * Creates an compare editor input for the given selection.
	 * 
	 * @param config
	 */
	public FileCompareEditorInput(CompareConfiguration config)
	{
		super(config);
	}

	/**
	 * @see org.eclipse.compare.CompareEditorInput#createDiffViewer(org.eclipse.swt.widgets.Composite)
	 */
	public Viewer createDiffViewer(Composite parent)
	{
		fDiffViewer = new DiffTreeViewer(parent, getCompareConfiguration())
		{
			protected void fillContextMenu(IMenuManager manager)
			{

				if (fOpenAction == null)
				{
					fOpenAction = new Action()
					{
						public void run()
						{
							handleOpen(null);
						}
					};
					Utilities.initAction(fOpenAction, getBundle(), "action.CompareContents."); //$NON-NLS-1$
				}

				boolean enable = false;
				ISelection selection = getSelection();
				if (selection instanceof IStructuredSelection)
				{
					IStructuredSelection ss = (IStructuredSelection) selection;
					if (ss.size() == 1)
					{
						Object element = ss.getFirstElement();
						if (element instanceof MyDiffNode)
						{
							ITypedElement te = ((MyDiffNode) element).getId();
							if (te != null)
								enable = !ITypedElement.FOLDER_TYPE.equals(te.getType());
						}
						else
							enable = true;
					}
				}
				fOpenAction.setEnabled(enable);

				manager.add(fOpenAction);

				super.fillContextMenu(manager);
			}
		};
		return fDiffViewer;
	}

	/**
	 * Sets the left resource
	 * 
	 * @param resource
	 */
	public void setLeftResource(File resource)
	{
		this.fLeftResource = resource;
		this.fLeft = new FileNode(this.fLeftResource);
	}

	/**
	 * Sets the right resource
	 * 
	 * @param resource
	 */
	public void setRightResource(File resource)
	{
		this.fRightResource = resource;
		this.fRight = new FileNode(this.fRightResource);
	}

	/**
	 * Initializes the images in the compare configuration.
	 */
	void initializeCompareConfiguration()
	{
		CompareConfiguration cc = getCompareConfiguration();
		if (fLeft != null)
		{
			cc.setLeftLabel(fLeft.getName());
			cc.setLeftImage(fLeft.getImage());
		}
		if (fRight != null)
		{
			cc.setRightLabel(fRight.getName());
			cc.setRightImage(fRight.getImage());
		}
	}

	/**
	 * Method for any file prep-work before running the differencer
	 */
	protected void prepareFiles()
	{
		// Does nothing by default, subclasses should override
	}

	/**
	 * @see org.eclipse.compare.CompareEditorInput#prepareInput(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public Object prepareInput(IProgressMonitor pm) throws InvocationTargetException
	{

		try
		{

			pm.beginTask(Utilities.getString("ResourceCompare.taskName"), IProgressMonitor.UNKNOWN); //$NON-NLS-1$

			prepareFiles();

			String leftLabel = fLeftResource.getName();
			String rightLabel = fRightResource.getName();

			String format = Utilities.getString("ResourceCompare.twoWay.title"); //$NON-NLS-1$
			String title = MessageFormat.format(format, new Object[] { leftLabel, rightLabel });
			setTitle(title);

			Differencer d = new Differencer()
			{
				protected Object visit(Object parent, int description, Object ancestor, Object left, Object right)
				{
					return new MyDiffNode((IDiffContainer) parent, description, (ITypedElement) ancestor,
							(ITypedElement) left, (ITypedElement) right);
				}
			};

			fRoot = d.findDifferences(false, pm, null, null, fLeft, fRight);
			return fRoot;

		}
		finally
		{
			pm.done();
		}
	}

	/**
	 * @see org.eclipse.compare.CompareEditorInput#getToolTipText()
	 */
	public String getToolTipText()
	{
		if (fLeftResource != null && fRightResource != null)
		{
			String leftLabel = fLeftResource.getAbsolutePath();
			String rightLabel = fRightResource.getAbsolutePath();

			String format = Utilities.getString("ResourceCompare.twoWay.tooltip"); //$NON-NLS-1$
			return MessageFormat.format(format, new Object[] { leftLabel, rightLabel });
		}
		// fall back
		return super.getToolTipText();
	}

	/**
	 * @see org.eclipse.compare.CompareEditorInput#saveChanges(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void saveChanges(IProgressMonitor pm) throws CoreException
	{
		super.saveChanges(pm);
		if (fRoot instanceof DiffNode)
		{
			try
			{
				commit(pm, (DiffNode) fRoot);
			}
			finally
			{
				if (fDiffViewer != null)
					fDiffViewer.refresh();
				setDirty(false);
			}
		}
	}

	/*
	 * Recursively walks the diff tree and commits all changes.
	 */
	private static void commit(IProgressMonitor pm, DiffNode node) throws CoreException
	{

		if (node instanceof MyDiffNode)
			((MyDiffNode) node).clearDirty();

		ITypedElement left = node.getLeft();
		if (left instanceof BufferedResourceNode)
			((BufferedResourceNode) left).commit(pm);

		ITypedElement right = node.getRight();
		if (right instanceof BufferedResourceNode)
			((BufferedResourceNode) right).commit(pm);

		IDiffElement[] children = node.getChildren();
		if (children != null)
		{
			for (int i = 0; i < children.length; i++)
			{
				IDiffElement element = children[i];
				if (element instanceof DiffNode)
					commit(pm, (DiffNode) element);
			}
		}
	}

	/**
	 * @see org.eclipse.compare.CompareEditorInput#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter)
	{
		if (IFile[].class.equals(adapter))
		{
			HashSet<IResource> collector = new HashSet<IResource>();
			collectDirtyResources(fRoot, collector);
			return collector.toArray(new IFile[collector.size()]);
		}
		return super.getAdapter(adapter);
	}

	private void collectDirtyResources(Object o, Set<IResource> collector)
	{
		if (o instanceof DiffNode)
		{
			DiffNode node = (DiffNode) o;

			ITypedElement left = node.getLeft();
			if (left instanceof BufferedResourceNode)
			{
				BufferedResourceNode bn = (BufferedResourceNode) left;
				if (bn.isDirty())
				{
					IResource resource = bn.getResource();
					if (resource instanceof IFile)
						collector.add(resource);
				}
			}

			ITypedElement right = node.getRight();
			if (right instanceof BufferedResourceNode)
			{
				BufferedResourceNode bn = (BufferedResourceNode) right;
				if (bn.isDirty())
				{
					IResource resource = bn.getResource();
					if (resource instanceof IFile)
						collector.add(resource);
				}
			}

			IDiffElement[] children = node.getChildren();
			if (children != null)
			{
				for (int i = 0; i < children.length; i++)
				{
					IDiffElement element = children[i];
					if (element instanceof DiffNode)
						collectDirtyResources(element, collector);
				}
			}
		}
	}
}
