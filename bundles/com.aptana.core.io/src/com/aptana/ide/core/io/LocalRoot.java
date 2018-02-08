/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.core.io;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.PlatformObject;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.PlatformUtil;
import com.aptana.core.util.StringUtil;

/**
 * @author Max Stepanov
 *
 */
public final class LocalRoot extends PlatformObject {

    private static final String HOME_DIR = PlatformUtil
            .expandEnvironmentStrings(PlatformUtil.HOME_DIRECTORY);
    private static final String DESKTOP = PlatformUtil
            .expandEnvironmentStrings(PlatformUtil.DESKTOP_DIRECTORY);
    private static final boolean ON_WINDOWS = Platform.OS_WIN32.equals(Platform.getOS());

	private final String name;
	private final File root;

	/**
	 *
	 */
	private LocalRoot(String name, File root) {
		super();
		this.name = StringUtil.isEmpty(name) ? Path.ROOT.toOSString() : name;
		this.root = root;
	}
	
	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.IConnectionPoint#getName()
	 */
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.IConnectionPoint#getRootURI()
	 */
	public URI getRootURI() {
		return (EFS.getLocalFileSystem().fromLocalFile(root)).toURI();
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.IConnectionPoint#getRoot()
	 */
	public IFileStore getRoot() {
		return EFS.getLocalFileSystem().fromLocalFile(root);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.PlatformObject#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter) {
		if (File.class.equals(adapter)) {
			return getFile();
		}
		if (IFileStore.class.equals(adapter)) {
			return getRoot();
		}
		return super.getAdapter(adapter);
	}

	/**
	 * @return the file
	 */
	public File getFile() {
		return root;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof LocalRoot)) {
			return false;
		}
		return root.equals(((LocalRoot) obj).root);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return root.hashCode();
	}

    @Override
    public String toString() {
        return getFile().toString();
    }

	public static LocalRoot[] createRoots() {
		List<LocalRoot> list = new ArrayList<LocalRoot>();
		if (Platform.OS_MACOSX.equals(Platform.getOS())) {
			for (File root : new File("/Volumes").listFiles()) { //$NON-NLS-1$
				try {
					if (root.listFiles() != null) {
						LocalRoot localRoot = new LocalRoot(root.getName(), root.getCanonicalFile());
						if ("/".equals(localRoot.getFile().getCanonicalPath())) { //$NON-NLS-1$
							list.add(0, localRoot);
						} else {
							list.add(localRoot);
						}
					}
				} catch (IOException e) {
					IdeLog.logWarning(CoreIOPlugin.getDefault(), e);
				}
			}
		} else if (!ON_WINDOWS) {
			for (File root : File.listRoots()) {
				try {
					list.add(new LocalRoot(root.getName(), root.getCanonicalFile()));
				} catch (IOException e) {
					IdeLog.logWarning(CoreIOPlugin.getDefault(), e);
				}
			}			
		}
		{	/* Home */
		    File homeFile;
            if (ON_WINDOWS) {
                homeFile = getWindowsHomeFile();
            } else {
                IPath homePath = new Path(HOME_DIR);
                homeFile = homePath.toFile();
            }
            if (homeFile != null && homeFile.exists() && homeFile.isDirectory()) {
                try {
                    list.add(new LocalRoot(homeFile.getName(), homeFile.getCanonicalFile()));
                } catch (IOException e) {
                	IdeLog.logWarning(CoreIOPlugin.getDefault(), e);
                }
            }
		}
		{	/* Desktop */
		    File desktopFile;
            if (ON_WINDOWS) {
                desktopFile = getWindowsDesktopFile();
            } else {
                IPath desktopPath = new Path(DESKTOP);
                desktopFile = desktopPath.toFile();
            }
            if (desktopFile != null && desktopFile.exists() && desktopFile.isDirectory()) {
                try {
                    list.add(new LocalRoot(desktopFile.getName(), desktopFile.getCanonicalFile()));
                } catch (IOException e) {
                	IdeLog.logWarning(CoreIOPlugin.getDefault(), e);
                }
            }
		}
		{	/* Documents */
			IPath docsPath = new Path(PlatformUtil.expandEnvironmentStrings(PlatformUtil.DOCUMENTS_DIRECTORY));
			File docsFile = docsPath.toFile();
			if (docsFile.exists() && docsFile.isDirectory()) {
				try {
					list.add(new LocalRoot(docsPath.lastSegment(), docsFile.getCanonicalFile()));
				} catch (IOException e) {
					IdeLog.logWarning(CoreIOPlugin.getDefault(), e);
				}				
			}
		}

		return list.toArray(new LocalRoot[list.size()]);
	}

	public static LocalRoot[] createWindowsSubroots(File root) {
		File[] drives = FileSystemView.getFileSystemView().getFiles(root, false);
		List<LocalRoot> subroots = new ArrayList<LocalRoot>();
		for (File drive : drives) {
			try {
				subroots.add(new LocalRoot(FileSystemView.getFileSystemView()
						.getSystemDisplayName(drive), drive.getCanonicalFile()));
			} catch (IOException e) {
				IdeLog.logWarning(CoreIOPlugin.getDefault(), e);
			}
		}
		return subroots.toArray(new LocalRoot[subroots.size()]);
	}

    private static File getWindowsHomeFile() {
        File desktop = getWindowsDesktopFile();
        if (desktop == null) {
            return null;
        }
        IPath homePath = new Path(HOME_DIR);
        String homeFilename = homePath.lastSegment();
        File[] files = FileSystemView.getFileSystemView().getFiles(desktop, false);
        for (File file : files) {
            if (file.getName().equals(homeFilename)) {
                return file;
            }
        }
        return null;
    }

    private static File getWindowsDesktopFile() {
        IPath desktopPath = new Path(DESKTOP);
        String desktopFilename = desktopPath.lastSegment();
        File[] files = FileSystemView.getFileSystemView().getRoots();
        for (File file : files) {
            if (file.getName().equals(desktopFilename)) {
                return file;
            }
        }
        return null;
    }
}
