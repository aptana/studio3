/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 *
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 *
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 *
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.contentassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonContentAssistProcessor;
import com.aptana.editor.common.contentassist.CommonCompletionProposal;
import com.aptana.editor.common.contentassist.UserAgentManager;
import com.aptana.editor.css.Activator;
import com.aptana.editor.css.contentassist.index.CSSIndexConstants;
import com.aptana.editor.css.contentassist.model.ElementElement;
import com.aptana.editor.css.contentassist.model.PropertyElement;

public class CSSContentAssistProcessor extends CommonContentAssistProcessor
{
	/**
	 * Location
	 */
	private static enum Location
	{
		OUTSIDE_RULE,
		INSIDE_RULE,
		ARG_ASSIST,
		ERROR
	};

	private static final Image ELEMENT_ICON = Activator.getImage("/icons/element.gif");
	private static final Image PROPERTY_ICON = Activator.getImage("/icons/property.gif");
	
	private IContextInformationValidator _validator;
	private CSSIndexQueryHelper _queryHelper;

	/**
	 * CSSContentAssistProcessor
	 * 
	 * @param editor
	 */
	public CSSContentAssistProcessor(AbstractThemeableEditor editor)
	{
		super(editor);
		
		this._queryHelper = new CSSIndexQueryHelper();
	}

	/**
	 * getElementProposals
	 * 
	 * @param proposals
	 * @param offset
	 */
	protected void addAllElementProposals(List<ICompletionProposal> proposals, int offset)
	{
		List<ElementElement> elements = this._queryHelper.getElements();
		
		if (elements != null)
		{
			for (ElementElement element : elements)
			{
				String name = element.getName();
				int length = name.length();
				String description = element.getDescription();
				Image image = ELEMENT_ICON;
				IContextInformation contextInfo = null;
				String[] userAgents = element.getUserAgentNames(); 
				Image[] userAgentIcons = UserAgentManager.getInstance().getUserAgentImages(userAgents);
				
				// build a proposal
				CommonCompletionProposal proposal = new CommonCompletionProposal(name, offset, 0, length, image, name, contextInfo, description);
				proposal.setFileLocation(CSSIndexConstants.METADATA);
				proposal.setUserAgentImages(userAgentIcons);
				
				// add it to the list
				proposals.add(proposal);
			}
		}
	}

	/**
	 * getAllPropertyProposals
	 * 
	 * @param proposals
	 * @param offset
	 */
	protected void addAllPropertyProposals(List<ICompletionProposal> proposals, int offset)
	{
		List<PropertyElement> properties = this._queryHelper.getProperties();
		
		if (properties != null)
		{
			for (PropertyElement property : properties)
			{
				String name = property.getName();
				int length = name.length();
				String description = property.getDescription();
				Image image = PROPERTY_ICON;
				IContextInformation contextInfo = null;
				String[] userAgents = property.getUserAgentNames(); 
				Image[] userAgentIcons = UserAgentManager.getInstance().getUserAgentImages(userAgents);
				
				// build a proposal
				CommonCompletionProposal proposal = new CommonCompletionProposal(name, offset, 0, length, image, name, contextInfo, description);
				proposal.setFileLocation(CSSIndexConstants.METADATA);
				proposal.setUserAgentImages(userAgentIcons);
				
				// add it to the list
				proposals.add(proposal);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonContentAssistProcessor#computeCompletionProposals(org.eclipse.jface.text.ITextViewer, int, char, boolean)
	 */
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset, char activationChar, boolean autoActivated)
	{
		Location location = this.getLocation(viewer.getDocument(), offset);
		List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();
		
		switch (location)
		{
			case OUTSIDE_RULE:
				this.addAllElementProposals(result, offset);
				break;
				
			case INSIDE_RULE:
				this.addAllPropertyProposals(result, offset);
				break;
				
			case ARG_ASSIST:
				// TODO: lookup specific property and shows its values
				break;
			
			default:
				break;
		}
		
		Collections.sort(result, new Comparator<ICompletionProposal>()
		{
			@Override
			public int compare(ICompletionProposal o1, ICompletionProposal o2)
			{
				return o1.getDisplayString().compareToIgnoreCase(o2.getDisplayString());
			}
		});
		
		return result.toArray(new ICompletionProposal[result.size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
	 */
	@Override
	public char[] getCompletionProposalAutoActivationCharacters()
	{
		return new char[] { ':', '\t', '{', ';' };
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationValidator()
	 */
	@Override
	public IContextInformationValidator getContextInformationValidator()
	{
		if (_validator == null)
		{
			_validator = new CSSContextInformationValidator();
		}
		
		return _validator;
	}
	
	/**
	 * getLocation
	 * 
	 * @param tokenLexems
	 * @param offset
	 * @return
	 */
	private Location getLocation(IDocument document, int offset)
	{
		Location result = Location.ERROR;
		
		if (offset == 0)
		{
			result = Location.OUTSIDE_RULE;
		}
		else
		{
			int i;
			
			LOOP:
			try
			{
				for (i = offset; i >= 0; i--)
				{
					char c = document.getChar(i);
					
					switch (c)
					{
						case '{':
						case ')':
						case ';':
						case ':':
							result = Location.INSIDE_RULE;
							break LOOP;
							
						case '}':
							result = Location.OUTSIDE_RULE;
							break LOOP;
							
						case '(':
							result = Location.ARG_ASSIST;
							break LOOP;
							
						default:
							break;
					}
				}
				
				if (i == 0 && result == Location.ERROR)
				{
					result = Location.OUTSIDE_RULE;
				}
			}
			catch (BadLocationException e)
			{
			}
		}
		
		return result;
	}
}
