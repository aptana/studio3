package com.aptana.editor.js.contentassist;

import java.net.URI;

import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.contentassist.CommonCompletionProposal;
import com.aptana.editor.js.contentassist.model.PropertyElement;

public class PropertyElementProposal extends CommonCompletionProposal
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
		// lazy load
		if (_fileLocation == null)
		{
			_fileLocation = JSModelFormatter.ADDITIONAL_INFO.getTypeDisplayName(property.getOwningType());
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

	@Override
	public String getAdditionalProposalInfo()
	{
		if (_additionalProposalInformation == null)
		{
			_additionalProposalInformation = JSModelFormatter.ADDITIONAL_INFO.getDescription(property, uri);
		}
		return super.getAdditionalProposalInfo();
	}

}
