package com.aptana.editor.js.contentassist;

import java.net.URI;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension5;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.contentassist.CommonCompletionProposal;
import com.aptana.js.core.model.PropertyElement;

public class PropertyElementProposal extends CommonCompletionProposal implements ICompletionProposalExtension5
{

	private PropertyElement property;
	private URI uri;

	public PropertyElementProposal(PropertyElement property, int offset, int replaceLength, URI uri)
	{
		super(property.getName(), offset, replaceLength, property.getName().length(), null, property.getName(), null,
				null);
		this.property = property;
		this.uri = uri;
	}

	@Override
	public String getFileLocation()
	{
		if (_fileLocation == null)
		{
			_fileLocation = JSModelFormatter.getTypeDisplayName(property.getOwningType());
		}
		return super.getFileLocation();
	}

	@Override
	public Image getImage()
	{
		if (_image == null)
		{
			_image = JSModelFormatter.ADDITIONAL_INFO.getImage(property);
		}
		return super.getImage();
	}

	public Object getAdditionalProposalInfo(IProgressMonitor monitor)
	{
		return JSModelFormatter.ADDITIONAL_INFO.getDescription(property, uri);
	}

}
