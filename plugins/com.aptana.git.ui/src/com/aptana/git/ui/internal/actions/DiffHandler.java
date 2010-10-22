package com.aptana.git.ui.internal.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.ISources;
import org.eclipse.ui.PlatformUI;

import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.ChangedFile;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoryManager;
import com.aptana.git.ui.DiffFormatter;
import com.aptana.git.ui.GitUIPlugin;
import com.aptana.git.ui.actions.Messages;

public class DiffHandler extends AbstractHandler
{
	private boolean enabled;

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}

	@Override
	public void setEnabled(Object evaluationContext)
	{
		this.enabled = !getSelectedChangedFiles(evaluationContext).isEmpty();
	}

	private Set<IResource> getSelectedFiles(Object evalContext)
	{
		ISelection sel = getSelection(evalContext);
		Set<IResource> resources = new HashSet<IResource>();
		if (sel == null || sel.isEmpty())
			return resources;
		if (!(sel instanceof IStructuredSelection))
			return resources;
		IStructuredSelection structured = (IStructuredSelection) sel;
		for (Object element : structured.toList())
		{
			if (element == null)
				continue;

			if (element instanceof IResource)
				resources.add((IResource) element);

			if (element instanceof IAdaptable)
			{
				IAdaptable adapt = (IAdaptable) element;
				IResource resource = (IResource) adapt.getAdapter(IResource.class);
				if (resource != null)
					resources.add(resource);
			}
		}
		return resources;
	}

	private ISelection getSelection(Object evalContext)
	{
		if (evalContext instanceof IEvaluationContext)
		{
			Object obj = ((IEvaluationContext) evalContext).getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);
			if (obj instanceof ISelection)
			{
				return (ISelection) obj;
			}
			// TODO Handle list/array/collection!
			return new StructuredSelection(obj);
		}
		return null;
	}

	private List<ChangedFile> getSelectedChangedFiles(Object evalContext)
	{
		GitRepository repo = null;
		List<ChangedFile> changedFiles = new ArrayList<ChangedFile>();
		for (IResource resource : getSelectedFiles(evalContext))
		{
			if (repo == null)
			{
				repo = getGitRepositoryManager().getAttached(resource.getProject());
			}
			changedFiles.addAll(getChangedFilesForResource(repo, resource));
		}
		return changedFiles;
	}

	private List<ChangedFile> getChangedFilesForResource(GitRepository repo, IResource resource)
	{
		List<ChangedFile> files = new ArrayList<ChangedFile>();
		if (resource instanceof IContainer)
		{
			files.addAll(repo.getChangedFilesForContainer((IContainer) resource));
		}
		else
		{
			ChangedFile changedFile = repo.getChangedFileForResource(resource);
			if (changedFile != null)
				files.add(changedFile);
		}
		return files;
	}

	protected IGitRepositoryManager getGitRepositoryManager()
	{
		return GitPlugin.getDefault().getGitRepositoryManager();
	}

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		Map<String, String> diffs = new HashMap<String, String>();
		List<ChangedFile> changedFiles = getSelectedChangedFiles(event.getApplicationContext());
		if (changedFiles == null || changedFiles.isEmpty())
		{
			return null;
		}

		GitRepository repo = getGitRepository(event.getApplicationContext());
		for (ChangedFile file : changedFiles)
		{
			if (file == null)
				continue;

			if (diffs.containsKey(file.getPath()))
				continue; // already calculated diff...
			String diff = repo.index().diffForFile(file, file.hasStagedChanges(), 3);
			diffs.put(file.getPath(), diff);
		}
		if (diffs.isEmpty())
		{
			return null;
		}

		String diff = ""; //$NON-NLS-1$
		try
		{
			diff = DiffFormatter.toHTML(diffs);
		}
		catch (Throwable t)
		{
			GitUIPlugin.logError("Failed to turn diff into HTML", t); //$NON-NLS-1$
		}

		final String finalDiff = diff;
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
		{

			public void run()
			{
				// FIXME This freaking dialog won't close!
				MessageDialog dialog = new MessageDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getShell(), Messages.GitProjectView_GitDiffDialogTitle, null,
						"", 0, new String[] { IDialogConstants.OK_LABEL }, 0) //$NON-NLS-1$
				{
					@Override
					protected Control createCustomArea(Composite parent)
					{
						Browser diffArea = new Browser(parent, SWT.BORDER);
						GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
						data.heightHint = 300;
						diffArea.setLayoutData(data);
						diffArea.setText(finalDiff);
						return diffArea;
					}

					@Override
					protected boolean isResizable()
					{
						return true;
					}
				};
				dialog.open();
			}
		});

		return null;
	}

	private GitRepository getGitRepository(Object applicationContext)
	{
		Set<IResource> files = getSelectedFiles(applicationContext);
		if (files == null || files.isEmpty())
		{
			return null;
		}
		return getGitRepositoryManager().getAttached(files.iterator().next().getProject());
	}

}
