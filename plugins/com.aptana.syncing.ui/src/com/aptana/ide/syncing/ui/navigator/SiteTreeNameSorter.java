package com.aptana.ide.syncing.ui.navigator;

import com.aptana.ide.ui.io.navigator.FileTreeNameSorter;

public class SiteTreeNameSorter extends FileTreeNameSorter {
    @Override
    public int category(Object element) {
        if (element instanceof ProjectSiteConnections) {
            return 10;
        }
        return super.category(element);
    }
}
