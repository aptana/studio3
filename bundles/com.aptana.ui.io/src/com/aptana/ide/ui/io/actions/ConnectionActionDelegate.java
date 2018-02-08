/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.ui.io.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.aptana.ide.core.io.IConnectionPoint;

/**
 * @author Max Stepanov
 *
 */
public abstract class ConnectionActionDelegate implements IObjectActionDelegate {

	protected IConnectionPoint connectionPoint;
	protected IWorkbenchPart targetPart;
	
	private List<IConnectionPoint> connectionPoints;

	protected ConnectionActionDelegate() {
	    connectionPoints = new ArrayList<IConnectionPoint>();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	    connectionPoints.clear();
		connectionPoint = null;
		if (selection instanceof IStructuredSelection) {
		    Object[] elements = ((IStructuredSelection) selection).toArray();
		    for (Object element : elements) {
		        if (element instanceof IConnectionPoint) {
		            connectionPoints.add((IConnectionPoint) element);
		        } else if (element instanceof IAdaptable) {
		        	IConnectionPoint connection = (IConnectionPoint) ((IAdaptable) element).getAdapter(IConnectionPoint.class);
		        	if (connection != null) {
		        		connectionPoints.add(connection);
		        	}
		        }
		    }
		    if (connectionPoints.size() > 0) {
		        connectionPoint = connectionPoints.get(0);
		    }
		}
	}

	protected IConnectionPoint[] getSelectedConnectionPoints() {
	    return connectionPoints.toArray(new IConnectionPoint[connectionPoints.size()]);
	}
}
