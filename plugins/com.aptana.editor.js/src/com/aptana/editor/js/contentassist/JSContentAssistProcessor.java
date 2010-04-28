package com.aptana.editor.js.contentassist;

import java.util.List;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.IndexContentAssistProcessor;
import com.aptana.editor.js.Activator;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.index.core.Index;

public class JSContentAssistProcessor extends IndexContentAssistProcessor
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
		super(abstractThemeableEditor);
		
		this._helper = new JSContentAssistHelper();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.IndexContentAssistProcessor#computeCompletionProposalsUsingIndex(org.eclipse.jface.text.ITextViewer, int, com.aptana.index.core.Index, java.util.List)
	 */
	@Override
	protected void computeCompletionProposalsUsingIndex(ITextViewer viewer, int offset, Index index, List<ICompletionProposal> completionProposals)
	{
		List<PropertyElement> globals = this._helper.getGlobals();
		
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
			completionProposals.add(proposal);
		}
	}
}
