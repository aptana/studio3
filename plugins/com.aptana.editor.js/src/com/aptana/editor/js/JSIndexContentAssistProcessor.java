package com.aptana.editor.js;

import java.util.List;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.IndexContentAssistProcessor;
import com.aptana.editor.js.index.JSContentAssistHelper;
import com.aptana.editor.js.model.FunctionElement;
import com.aptana.editor.js.model.PropertyElement;
import com.aptana.index.core.Index;

public class JSIndexContentAssistProcessor extends IndexContentAssistProcessor
{
	private static final Image JS_FUNCTION = Activator.getImage("/icons/js_function.gif");
	private static final Image JS_PROPERTY = Activator.getImage("/icons/js_property.gif");
	
	private JSContentAssistHelper _helper;
	
	/**
	 * JSIndexContentAssitProcessor
	 * 
	 * @param abstractThemeableEditor
	 */
	public JSIndexContentAssistProcessor(AbstractThemeableEditor abstractThemeableEditor)
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
			String name = property.toString();
			int length = isFunction ? name.length() - 1 : name.length();
			String description = property.getDescription();
			Image image = isFunction ? JS_FUNCTION : JS_PROPERTY;
			
			// build a proposal
			CompletionProposal proposal = new CompletionProposal(name, offset, 0, length, image, name, null, description);
			
			// add it to the list
			completionProposals.add(proposal);
		}
	}
}
