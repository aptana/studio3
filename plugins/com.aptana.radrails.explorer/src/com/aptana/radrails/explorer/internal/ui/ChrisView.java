package com.aptana.radrails.explorer.internal.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.navigator.CommonNavigator;

public class ChrisView extends CommonNavigator
{
	@Override
	public void createPartControl(Composite aParent)
	{
		// Create our own parent
		Composite myComposite = new Composite(aParent, SWT.NONE);
		myComposite.setLayout(new FillLayout(SWT.VERTICAL));

		// Create our special git stuff
		Composite gitStuff = new Composite(myComposite, SWT.NONE);
		gitStuff.setLayout(new RowLayout());
		
		Combo combo = new Combo(gitStuff, SWT.DROP_DOWN | SWT.MULTI);
		combo.setText("Hurray!");

		// Now create the typical stuff for the navigator
		Composite viewer = new Composite(myComposite, SWT.NONE);
		viewer.setLayout(new FillLayout());
		super.createPartControl(viewer);
	}

}
