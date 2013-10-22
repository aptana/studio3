/*******************************************************************************
 * Copyright (C) 2007, Robin Rosenberg <robin.rosenberg@dewire.com>
 * Copyright (C) 2008, Roger C. Soares <rogersoares@intelinet.com.br>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.aptana.git.ui.internal.history;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareViewerPane;
import org.eclipse.compare.CompareViewerSwitchingPane;
import org.eclipse.compare.IEditableContent;
import org.eclipse.compare.IResourceProvider;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.contentmergeviewer.TextMergeViewer;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.compare.structuremergeviewer.ICompareInput;
import org.eclipse.compare.structuremergeviewer.IDiffElement;
import org.eclipse.compare.structuremergeviewer.IStructureComparator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.internal.core.history.LocalFileRevision;
import org.eclipse.team.internal.ui.Utils;
import org.eclipse.team.internal.ui.history.FileRevisionTypedElement;
import org.eclipse.team.internal.ui.synchronize.LocalResourceTypedElement;
import org.eclipse.team.ui.synchronize.SaveableCompareEditorInput;
import org.eclipse.ui.IWorkbenchPage;

import com.aptana.core.logging.IdeLog;
import com.aptana.git.core.IDebugScopes;
import com.aptana.git.ui.GitUIPlugin;
import com.aptana.theme.IControlThemerFactory;
import com.aptana.theme.ThemePlugin;

/**
 * The input provider for the compare editor when working on resources under Git control.
 */
@SuppressWarnings("restriction")
public class GitCompareFileRevisionEditorInput extends SaveableCompareEditorInput
{

	private ITypedElement left;
	private ITypedElement right;
	private CompareViewerSwitchingPane fPane;
	private CompareViewerPane fStructurePane;

	/**
	 * Creates a new CompareFileRevisionEditorInput.
	 * 
	 * @param left
	 * @param right
	 * @param page
	 */
	public GitCompareFileRevisionEditorInput(ITypedElement left, ITypedElement right, IWorkbenchPage page)
	{
		super(new CompareConfiguration(), page);
		this.left = left;
		this.right = right;
		setTitle(left.getName());
	}

	private FileRevisionTypedElement getRightRevision()
	{
		if (right instanceof FileRevisionTypedElement)
		{
			return (FileRevisionTypedElement) right;
		}
		return null;
	}

	private FileRevisionTypedElement getLeftRevision()
	{
		if (left instanceof FileRevisionTypedElement)
		{
			return (FileRevisionTypedElement) left;
		}
		return null;
	}

	private static void ensureContentsCached(FileRevisionTypedElement left, FileRevisionTypedElement right,
			IProgressMonitor monitor)
	{
		if (left != null)
		{
			try
			{
				left.cacheContents(monitor);
			}
			catch (CoreException e)
			{
				IdeLog.logError(GitUIPlugin.getDefault(), e, IDebugScopes.DEBUG);
			}
		}
		if (right != null)
		{
			try
			{
				right.cacheContents(monitor);
			}
			catch (CoreException e)
			{
				IdeLog.logError(GitUIPlugin.getDefault(), e, IDebugScopes.DEBUG);
			}
		}
	}

	private boolean isLeftEditable(ICompareInput input)
	{
		Object tmpLeft = input.getLeft();
		return isEditable(tmpLeft);
	}

	private boolean isRightEditable(ICompareInput input)
	{
		Object tmpRight = input.getRight();
		return isEditable(tmpRight);
	}

	private boolean isEditable(Object object)
	{
		if (object instanceof IEditableContent)
		{
			return ((IEditableContent) object).isEditable();
		}
		return false;
	}

	private IResource getResource(ICompareInput input)
	{
		if (getLocalElement() != null)
		{
			return ((IResourceProvider) getLocalElement()).getResource();
		}
		return null;
	}

	private ICompareInput createCompareInput()
	{
		return compare(left, right);
	}

	private DiffNode compare(ITypedElement left, ITypedElement right)
	{
		if (left.getType().equals(ITypedElement.FOLDER_TYPE))
		{
			// return new MyDiffContainer(null, left,right);
			DiffNode diffNode = new DiffNode(null, Differencer.CHANGE, null, left, right);
			ITypedElement[] lc = (ITypedElement[]) ((IStructureComparator) left).getChildren();
			ITypedElement[] rc = (ITypedElement[]) ((IStructureComparator) right).getChildren();
			int li = 0;
			int ri = 0;
			while (li < lc.length && ri < rc.length)
			{
				ITypedElement ln = lc[li];
				ITypedElement rn = rc[ri];
				int compareTo = ln.getName().compareTo(rn.getName());
				// TODO: Git ordering!
				if (compareTo == 0)
				{
					if (!ln.equals(rn))
						diffNode.add(compare(ln, rn));
					++li;
					++ri;
				}
				else if (compareTo < 0)
				{
					DiffNode childDiffNode = new DiffNode(Differencer.ADDITION, null, ln, null);
					diffNode.add(childDiffNode);
					if (ln.getType().equals(ITypedElement.FOLDER_TYPE))
					{
						ITypedElement[] children = (ITypedElement[]) ((IStructureComparator) ln).getChildren();
						if (children != null && children.length > 0)
						{
							for (ITypedElement child : children)
							{
								childDiffNode.add(addDirectoryFiles(child, Differencer.ADDITION));
							}
						}
					}
					++li;
				}
				else
				{
					DiffNode childDiffNode = new DiffNode(Differencer.DELETION, null, null, rn);
					diffNode.add(childDiffNode);
					if (rn.getType().equals(ITypedElement.FOLDER_TYPE))
					{
						ITypedElement[] children = (ITypedElement[]) ((IStructureComparator) rn).getChildren();
						if (children != null && children.length > 0)
						{
							for (ITypedElement child : children)
							{
								childDiffNode.add(addDirectoryFiles(child, Differencer.DELETION));
							}
						}
					}
					++ri;
				}
			}
			while (li < lc.length)
			{
				ITypedElement ln = lc[li];
				DiffNode childDiffNode = new DiffNode(Differencer.ADDITION, null, ln, null);
				diffNode.add(childDiffNode);
				if (ln.getType().equals(ITypedElement.FOLDER_TYPE))
				{
					ITypedElement[] children = (ITypedElement[]) ((IStructureComparator) ln).getChildren();
					if (children != null && children.length > 0)
					{
						for (ITypedElement child : children)
						{
							childDiffNode.add(addDirectoryFiles(child, Differencer.ADDITION));
						}
					}
				}
				++li;
			}
			while (ri < rc.length)
			{
				ITypedElement rn = rc[ri];
				DiffNode childDiffNode = new DiffNode(Differencer.DELETION, null, null, rn);
				diffNode.add(childDiffNode);
				if (rn.getType().equals(ITypedElement.FOLDER_TYPE))
				{
					ITypedElement[] children = (ITypedElement[]) ((IStructureComparator) rn).getChildren();
					if (children != null && children.length > 0)
					{
						for (ITypedElement child : children)
						{
							childDiffNode.add(addDirectoryFiles(child, Differencer.DELETION));
						}
					}
				}
				++ri;
			}
			return diffNode;
		}
		return new DiffNode(left, right);
	}

	private DiffNode addDirectoryFiles(ITypedElement elem, int diffType)
	{
		ITypedElement l = null;
		ITypedElement r = null;
		if (diffType == Differencer.DELETION)
		{
			r = elem;
		}
		else
		{
			l = elem;
		}

		if (elem.getType().equals(ITypedElement.FOLDER_TYPE))
		{
			DiffNode diffNode = null;
			diffNode = new DiffNode(null, Differencer.CHANGE, null, l, r);
			ITypedElement[] children = (ITypedElement[]) ((IStructureComparator) elem).getChildren();
			for (ITypedElement child : children)
			{
				diffNode.add(addDirectoryFiles(child, diffType));
			}
			return diffNode;
		}
		return new DiffNode(diffType, null, l, r);
	}

	private void initLabels(ICompareInput input)
	{
		CompareConfiguration cc = getCompareConfiguration();
		if (getLeftRevision() != null)
		{
			String leftLabel = getFileRevisionLabel(getLeftRevision());
			cc.setLeftLabel(leftLabel);
		}
		else if (getResource(input) != null)
		{
			String label = NLS.bind(Messages.GitCompareFileRevisionEditorInput_workspace, new Object[] { input
					.getLeft().getName() });
			cc.setLeftLabel(label);
		}
		if (getRightRevision() != null)
		{
			String rightLabel = getFileRevisionLabel(getRightRevision());
			cc.setRightLabel(rightLabel);
		}
	}

	private String getFileRevisionLabel(FileRevisionTypedElement element)
	{
		IFileRevision fileObject = element.getFileRevision();
		if (fileObject instanceof LocalFileRevision)
		{
			return NLS.bind(Messages.GitCompareFileRevisionEditorInput_localRevision, new Object[] { element.getName(),
					element.getTimestamp() });
		}
		return NLS.bind(Messages.GitCompareFileRevisionEditorInput_repository, new Object[] { element.getName(),
				element.getContentIdentifier() });
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.compare.CompareEditorInput#getToolTipText()
	 */
	public String getToolTipText()
	{
		Object[] titleObject = new Object[3];
		titleObject[0] = getLongName(left);
		titleObject[1] = getContentIdentifier(getLeftRevision());
		titleObject[2] = getContentIdentifier(getRightRevision());
		return NLS.bind(Messages.GitCompareFileRevisionEditorInput_CompareResourceAndVersion, titleObject);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.compare.CompareEditorInput#getTitle()
	 */
	public String getTitle()
	{
		Object[] titleObject = new Object[3];
		titleObject[0] = getShortName(left);
		titleObject[1] = getContentIdentifier(getLeftRevision());
		titleObject[2] = getContentIdentifier(getRightRevision());
		return NLS.bind(Messages.GitCompareFileRevisionEditorInput_CompareResourceAndVersion, titleObject);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.compare.CompareEditorInput#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter)
	{
		if (adapter == IFile.class || adapter == IResource.class)
		{
			if (getLocalElement() != null)
			{
				return getLocalElement().getResource();
			}
			return null;
		}
		return super.getAdapter(adapter);
	}

	private String getShortName(ITypedElement element)
	{
		if (element instanceof FileRevisionTypedElement)
		{
			FileRevisionTypedElement fileRevisionElement = (FileRevisionTypedElement) element;
			return fileRevisionElement.getName();
		}
		else if (element instanceof LocalResourceTypedElement)
		{
			LocalResourceTypedElement typedContent = (LocalResourceTypedElement) element;
			return typedContent.getResource().getName();
		}
		return element.getName();
	}

	private String getLongName(ITypedElement element)
	{
		if (element instanceof FileRevisionTypedElement)
		{
			FileRevisionTypedElement fileRevisionElement = (FileRevisionTypedElement) element;
			return fileRevisionElement.getPath();
		}
		else if (element instanceof LocalResourceTypedElement)
		{
			LocalResourceTypedElement typedContent = (LocalResourceTypedElement) element;
			return typedContent.getResource().getFullPath().toString();
		}
		return element.getName();
	}

	private String getContentIdentifier(ITypedElement element)
	{
		if (element instanceof FileRevisionTypedElement)
		{
			FileRevisionTypedElement fileRevisionElement = (FileRevisionTypedElement) element;
			Object fileObject = fileRevisionElement.getFileRevision();
			if (fileObject instanceof LocalFileRevision)
			{
				try
				{
					IStorage storage = ((LocalFileRevision) fileObject).getStorage(new NullProgressMonitor());
					if (Utils.getAdapter(storage, IFileState.class) != null)
					{
						// local revision
						return Messages.GitCompareFileRevisionEditorInput_0;
					}
					else if (Utils.getAdapter(storage, IFile.class) != null)
					{
						// current revision
						return Messages.GitCompareFileRevisionEditorInput_1;
					}
				}
				catch (CoreException e)
				{
					IdeLog.logError(GitUIPlugin.getDefault(),
							Messages.GitCompareFileRevisionEditorInput_ProblemGettingContent_Error, e,
							IDebugScopes.DEBUG);
				}
			}
			else
			{
				return fileRevisionElement.getContentIdentifier();
			}
		}
		return Messages.GitCompareFileRevisionEditorInput_2;
	}

	@Override
	protected void fireInputChange()
	{
	}

	//
	// /* (non-Javadoc)
	// * @see org.eclipse.team.ui.synchronize.SaveableCompareEditorInput#contentsCreated()
	// */
	// protected void contentsCreated() {
	// super.contentsCreated();
	// notifier.initialize();
	// }
	//
	// /* (non-Javadoc)
	// * @see org.eclipse.team.ui.synchronize.SaveableCompareEditorInput#handleDispose()
	// */
	// protected void handleDispose() {
	// super.handleDispose();
	// notifier.dispose();
	// if (getLocalElement() != null) {
	// getLocalElement().discardBuffer();
	// }
	// }
	//
	private LocalResourceTypedElement getLocalElement()
	{
		if (left instanceof LocalResourceTypedElement)
		{
			return (LocalResourceTypedElement) left;
		}
		return null;
	}

	@Override
	protected ICompareInput prepareCompareInput(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException
	{
		ICompareInput input = createCompareInput();
		getCompareConfiguration().setLeftEditable(isLeftEditable(input));
		getCompareConfiguration().setRightEditable(isRightEditable(input));
		ensureContentsCached(getLeftRevision(), getRightRevision(), monitor);
		initLabels(input);
		setTitle(NLS.bind(Messages.GitCompareFileRevisionEditorInput_CompareInputTitle,
				new String[] { input.getName() }));

		// The compare editor (Structure Compare) will show the diff filenames
		// with their project relative path. So, no need to also show directory entries.
		DiffNode flatDiffNode = new DiffNode(null, Differencer.CHANGE, null, left, right);
		flatDiffView(flatDiffNode, (DiffNode) input);

		return flatDiffNode;
	}

	private void flatDiffView(DiffNode rootNode, DiffNode currentNode)
	{
		if (currentNode != null)
		{
			IDiffElement[] dElems = currentNode.getChildren();
			if (dElems != null)
			{
				for (IDiffElement dElem : dElems)
				{
					DiffNode dNode = (DiffNode) dElem;
					if (dNode.getChildren() != null && dNode.getChildren().length > 0)
					{
						flatDiffView(rootNode, dNode);
					}
					else
					{
						rootNode.add(dNode);
					}
				}
			}
		}
	}

	protected void contentsCreated()
	{
		super.contentsCreated();
		if (fPane == null)
		{
			try
			{
				Field f = CompareEditorInput.class.getDeclaredField("fContentInputPane"); //$NON-NLS-1$
				f.setAccessible(true);
				fPane = (CompareViewerSwitchingPane) f.get(this);
			}
			catch (Exception e)
			{
				IdeLog.logError(GitUIPlugin.getDefault(), e, IDebugScopes.DEBUG);
			}
		}

		if (fPane != null && (fPane.getViewer() instanceof TextMergeViewer))
		{
			// TODO Set up source viewer in way so we actually get syntax coloring. We'd need to be able to grab one by
			// the file's content type!
			((TextMergeViewer) fPane.getViewer()).setBackgroundColor(ThemePlugin.getDefault().getThemeManager()
					.getCurrentTheme().getBackground());
			((TextMergeViewer) fPane.getViewer()).setForegroundColor(ThemePlugin.getDefault().getThemeManager()
					.getCurrentTheme().getForeground());
		}
	}

	protected void handleDispose()
	{
		super.handleDispose();
		fPane = null;
	}

	private IControlThemerFactory getControlThemerFactory()
	{
		return ThemePlugin.getDefault().getControlThemerFactory();
	}

	protected CompareViewerPane createStructureInputPane(Composite parent)
	{
		CompareViewerPane pane = super.createStructureInputPane(parent);
		fStructurePane = pane;
		return pane;
	}
}
