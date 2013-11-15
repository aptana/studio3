package net.jeeeyul.eclipse.themes;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.ui.css.swt.theme.IThemeEngine;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.ui.progress.UIJob;

/**
 * Add "empty" class(CSS) into {@link CTabFolder} when there is no item.
 */
@SuppressWarnings("restriction")
public class UpdateCTabFolderClassesJob extends UIJob {

	private CTabFolder folder;

	public UpdateCTabFolderClassesJob(CTabFolder folder) {
		super("Update CTabFolder CSS");
		this.folder = folder;
		this.setSystem(true);
	}

	@Override
	public IStatus runInUIThread(IProgressMonitor arg0) {
		if (folder == null || folder.isDisposed()) {
			return Status.OK_STATUS;
		}

		CSSClasses classes = CSSClasses.getStyleClasses(folder);
		boolean haveToSetEmpty = folder.getItemCount() == 0;

		if (haveToSetEmpty) {
			classes.add("empty");
			classes.remove("nonEmpty");
		} else {
			classes.remove("empty");
			classes.add("nonEmpty");
		}

		CSSClasses.setStyleClasses(folder, classes);
		getThemeEngine().applyStyles(folder, true);

		return Status.OK_STATUS;
	}

	private IThemeEngine getThemeEngine() {
		return (IThemeEngine) folder.getDisplay().getData("org.eclipse.e4.ui.css.swt.theme");
	}

	@Override
	public boolean shouldSchedule() {
		return folder != null && !folder.isDisposed();
	}

	@Override
	public boolean shouldRun() {
		return shouldSchedule();
	}
}
