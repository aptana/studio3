package com.aptana.editor.js.contentassist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.js.Activator;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;

public class JSContentAssistProcessor implements IContentAssistProcessor
{
	private static final Image JS_FUNCTION = Activator.getImage("/icons/js_function.gif");
	private static final Image JS_PROPERTY = Activator.getImage("/icons/js_property.gif");
	
	private JSContentAssistHelper _helper;
	
	/**
	 * JSIndexContentAssitProcessor
	 * 
	 * @param abstractThemeableEditor
	 */
	public JSContentAssistProcessor(AbstractThemeableEditor abstractThemeableEditor)
	{
		this._helper = new JSContentAssistHelper();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeCompletionProposals(org.eclipse.jface.text.ITextViewer, int)
	 */
	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset)
	{
		List<PropertyElement> globals = this._helper.getGlobals();
		List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();
		
		for (PropertyElement property : globals)
		{
			// slightly change behavior if this is a function
			boolean isFunction = (property instanceof FunctionElement);
			
			// grab the interesting parts
			String name = JSModelFormatter.getName(property);
			int length = isFunction ? name.length() - 1 : name.length();
			String description = JSModelFormatter.getDescription(property);
			Image image = isFunction ? JS_FUNCTION : JS_PROPERTY;
			IContextInformation contextInfo = null; //new ContextInformation(JS_FUNCTION, "Display String", "Info");
			
			// build a proposal
			CompletionProposal proposal = new CompletionProposal(name, offset, 0, length, image, name, contextInfo, description);
			
			// add it to the list
			result.add(proposal);
		}
		
		return result.toArray(new ICompletionProposal[result.size()]);
	}

	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IContextInformationValidator getContextInformationValidator()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getErrorMessage()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
