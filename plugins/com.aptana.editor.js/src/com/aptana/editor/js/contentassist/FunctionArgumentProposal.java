/**
 * Aptana Studio
 * Copyright (c) 2014 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension5;
import org.eclipse.swt.graphics.Image;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.contentassist.CommonCompletionProposal;
import com.aptana.js.core.index.JSIndexQueryHelper;
import com.aptana.js.core.model.PropertyElement;

/**
 * This class represents a defined "constant" value that can be used for a function argument. The value is assumed to be
 * a String holding the fully qualified name of a property - which we use for the image, docs and "location" displayed
 * in the proposal popup. Because this value comes straight from JSCA definitions, we set the relevance to EXACT. This
 * is a way of defining enumerated values for function arguments.
 * 
 * @author Chris Williams <cwilliams@appcelerator.com>
 */
public class FunctionArgumentProposal extends CommonCompletionProposal implements ICompletionProposalExtension5
{

	// FIXME We share this across proposals but really it should be cached by project somewhere.
	private JSIndexQueryHelper fgQueryHelper;

	private IProject project;
	private PropertyElement fProp;

	public FunctionArgumentProposal(String constant, int offset, int replaceLength, IProject project)
	{
		super(constant, offset, replaceLength, constant.length(), null, constant, null, null);
		this.project = project;
		setRelevance(CommonCompletionProposal.RELEVANCE_EXACT);
	}

	@Override
	public String getFileLocation()
	{
		if (_fileLocation == null)
		{
			PropertyElement prop = getProperty();
			if (prop != null)
			{
				_fileLocation = JSModelFormatter.getTypeDisplayName(getProperty().getOwningType());
			}
		}
		return super.getFileLocation();
	}

	@Override
	public Image getImage()
	{
		if (_image == null)
		{
			PropertyElement prop = getProperty();
			if (prop != null)
			{
				_image = JSModelFormatter.ADDITIONAL_INFO.getImage(prop);
			}
		}
		return super.getImage();
	}

	public Object getAdditionalProposalInfo(IProgressMonitor monitor)
	{
		PropertyElement prop = getProperty();
		if (prop == null)
		{
			return StringUtil.EMPTY;
		}
		return JSModelFormatter.ADDITIONAL_INFO.getDescription(prop, project.getLocationURI());
	}

	/**
	 * This gets the referenced property that the value is pointing at. i.e. Ti.UI.FILL
	 * 
	 * @return
	 */
	private synchronized PropertyElement getProperty()
	{
		if (fProp == null)
		{
			String fullName = getDisplayString();
			int index = StringUtil.lastIndexOf(fullName, '.');

			List<PropertyElement> props = getQueryHelper().getProperties(fullName.substring(0, index),
					fullName.substring(index + 1));
			if (!CollectionsUtil.isEmpty(props))
			{
				fProp = props.get(0);
			}
			else
			{
				// FIXME What if we can't look up the property because the constant value is actually a number or string
				// or something?! We need to not try to look it up again and just be ok
			}
		}
		return fProp;
	}

	protected JSIndexQueryHelper getQueryHelper()
	{
		synchronized (FunctionArgumentProposal.class)
		{
			if (fgQueryHelper == null)
			{
				fgQueryHelper = new JSIndexQueryHelper(project);
			}
			return fgQueryHelper;
		}
	}

}