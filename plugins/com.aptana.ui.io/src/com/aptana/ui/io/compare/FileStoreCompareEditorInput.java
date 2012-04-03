/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable unnecessaryExceptions
package com.aptana.ui.io.compare;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

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
import org.eclipse.core.filesystem.IFileStore;
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
 * @author Michael Xia (mxia@appcelerator.com)
 */
@SuppressWarnings("restriction")
public class FileStoreCompareEditorInput extends CompareEditorInput
{

	private Object fRoot;
	private FileStoreNode fLeft;
	private FileStoreNode fRight;
	private IFileStore fLeftFileStore;
	private IFileStore fRightFileStore;
	private DiffTreeViewer fDiffViewer;
	private IAction fOpenAction;

	class MyDiffNode extends DiffNode
	{
		private boolean fDirty = false;
		private ITypedElement fLastId;
		private String fLastName;

		protected MyDiffNode(IDiffContainer parent, int description, ITypedElement ancestor, ITypedElement left,
				ITypedElement right)
		{
			super(parent, description, ancestor, left, right);
		}

		@Override
		public void fireChange()
		{
			super.fireChange();
			setDirty(true);
			fDirty = true;
			if (fDiffViewer != null)
			{
				fDiffViewer.refresh(this);
			}
		}

		void clearDirty()
		{
			fDirty = false;
		}

		@Override
		public String getName()
		{
			if (fLastName == null)
			{
				fLastName = super.getName();
			}
			if (fDirty)
			{
				return MessageFormat.format("<{0}>", fLastName); //$NON-NLS-1$
			}
			return fLastName;
		}

		@Override
		public ITypedElement getId()
		{
			ITypedElement id = super.getId();
			if (id == null)
			{
				return fLastId;
			}
			fLastId = id;
			return id;
		}
	}

	/**
	 * Creates an compare editor input for the given file store selection.
	 * 
	 * @param config
	 */
	public FileStoreCompareEditorInput(CompareConfiguration config)
	{
		super(config);
	}

	@Override
	public Viewer createDiffViewer(Composite parent)
	{
		fDiffViewer = new DiffTreeViewer(parent, getCompareConfiguration())
		{

			@Override
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
							ITypedElement typedElement = ((MyDiffNode) element).getId();
							if (typedElement != null)
							{
								enable = !ITypedElement.FOLDER_TYPE.equals(typedElement.getType());
							}
						}
						else
						{
							enable = true;
						}
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
	 * Sets the left file store.
	 * 
	 * @param fileStore
	 */
	public void setLeftFileStore(IFileStore fileStore)
	{
		setLeftFileStore(fileStore, null);
	}

	/**
	 * Sets the left file store with a specific name.
	 * 
	 * @param fileStore
	 */
	public void setLeftFileStore(IFileStore fileStore, String name)
	{
		fLeftFileStore = fileStore;
		fLeft = new FileStoreNode(fLeftFileStore, name);
	}

	/**
	 * Sets the right file store.
	 * 
	 * @param resource
	 */
	public void setRightFileStore(IFileStore fileStore)
	{
		setRightFileStore(fileStore, null);
	}

	/**
	 * Sets the right file store with a specific name.
	 * 
	 * @param resource
	 */
	public void setRightFileStore(IFileStore fileStore, String name)
	{
		fRightFileStore = fileStore;
		fRight = new FileStoreNode(fRightFileStore, name);
	}

	/**
	 * Initializes the images in the compare configuration.
	 */
	public void initializeCompareConfiguration()
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
	 * Method for any file prep-work before running the differecer. Does nothing by default; subclasses should override
	 */
	protected void prepareFiles()
	{
	}

	@Override
	public Object prepareInput(IProgressMonitor pm) throws InvocationTargetException
	{
		try
		{
			pm.beginTask(Utilities.getString("ResourceCompare.taskName"), IProgressMonitor.UNKNOWN); //$NON-NLS-1$

			prepareFiles();

			String leftLabel = fLeft.getName();
			String rightLabel = fRight.getName();

			String format = Utilities.getString("ResourceCompare.twoWay.title"); //$NON-NLS-1$
			String title = MessageFormat.format(format, new Object[] { leftLabel, rightLabel });
			setTitle(title);

			Differencer d = new Differencer()
			{

				@Override
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

	@Override
	public String getToolTipText()
	{
		if (fLeftFileStore != null && fRightFileStore != null)
		{
			String leftLabel = fLeftFileStore.toString();
			String rightLabel = fRightFileStore.toString();

			String format = Utilities.getString("ResourceCompare.twoWay.tooltip"); //$NON-NLS-1$
			return MessageFormat.format(format, new Object[] { leftLabel, rightLabel });
		}
		return super.getToolTipText();
	}

	@Override
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
				{
					fDiffViewer.refresh();
				}
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
		{
			((MyDiffNode) node).clearDirty();
		}

		ITypedElement left = node.getLeft();
		if (left instanceof BufferedResourceNode)
		{
			((BufferedResourceNode) left).commit(pm);
		}

		ITypedElement right = node.getRight();
		if (right instanceof BufferedResourceNode)
		{
			((BufferedResourceNode) right).commit(pm);
		}

		IDiffElement[] children = node.getChildren();
		if (children != null)
		{
			for (IDiffElement element : children)
			{
				if (element instanceof DiffNode)
				{
					commit(pm, (DiffNode) element);
				}
			}
		}
	}
}
