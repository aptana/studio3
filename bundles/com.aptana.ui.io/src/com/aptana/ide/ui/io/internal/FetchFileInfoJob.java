/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.ui.io.internal;

import java.text.MessageFormat;

import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.aptana.core.logging.IdeLog;
import com.aptana.ide.ui.io.IOUIPlugin;

/**
 * @author Max Stepanov
 *
 */
public class FetchFileInfoJob extends Job {

	private final IFileStore fileStore;
	private final int options;
	
	/**
	 * @param name
	 */
	public FetchFileInfoJob(IFileStore fileStore, int options) {
		super(MessageFormat.format(Messages.FetchFileInfoJob_Title, fileStore.toString()));
		this.fileStore = fileStore;
		this.options = options;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		if (monitor.isCanceled()) {
			return Status.CANCEL_STATUS;
		}
		IFileInfo fileInfo;
		try {
			fileInfo = fileStore.fetchInfo(options, monitor);
		} catch (CoreException e) {
			IdeLog.logWarning(IOUIPlugin.getDefault(), Messages.FetchFileInfoJob_FailedToFetch, e);
			return e.getStatus();
		}
		return new FetchFileInfoStatus(fileInfo);
	}
}
