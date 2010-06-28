package com.aptana.ide.syncing.ui.decorators;

import org.eclipse.ui.IDecoratorManager;

import com.aptana.ide.syncing.ui.SyncingUIPlugin;

public class DecoratorUtils {

    /**
     * Refreshes the cloaking decorator.
     */
    public static void updateCloakDecorator() {
        IDecoratorManager dm = SyncingUIPlugin.getDefault().getWorkbench().getDecoratorManager();
        dm.update("com.aptana.ide.syncing.ui.decorators.CloakedLabelDecorator"); //$NON-NLS-1$
    }
}
