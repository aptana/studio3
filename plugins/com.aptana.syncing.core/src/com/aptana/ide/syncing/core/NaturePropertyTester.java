package com.aptana.ide.syncing.core;

import java.text.MessageFormat;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * A property tester that will test the existence of the nature ID in the given
 * {@link IProject} receiver.
 * 
 * @author Shalom Gibly, Winston Prakash
 */
public class NaturePropertyTester extends PropertyTester {

    private static final String NATURE = "nature"; //$NON-NLS-1$

    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        if (NATURE.equals(property) && receiver instanceof IProject
                && expectedValue instanceof Boolean) {
            IProject project = (IProject) receiver;
            if (!project.isAccessible()) {
                return false;
            }
            try {
                if (args != null && args.length > 0) {
                    if (project.hasNature(args[0].toString())) {
                        return ((Boolean) expectedValue).booleanValue();
                    } else {
                        return !((Boolean) expectedValue).booleanValue();
                    }
                }
            } catch (CoreException e) {
                SyncingPlugin.log(new Status(IStatus.ERROR, SyncingPlugin.PLUGIN_ID, MessageFormat.format(
                        Messages.NaturePropertyTester_ERR_WhileTestingProjectNature, project
                                .getName()), e));
            }
        }
        return false;
    }
}
