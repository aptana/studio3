package com.aptana.editor.js.contentassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonContentAssistProcessor;
import com.aptana.editor.common.contentassist.CommonCompletionProposal;
import com.aptana.editor.common.contentassist.LexemeProvider;
import com.aptana.editor.js.Activator;
import com.aptana.editor.js.JSScopeScanner;
import com.aptana.editor.js.contentassist.index.JSIndexConstants;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.parsing.lexer.JSTokenType;
import com.aptana.parsing.lexer.Lexeme;

public class JSContentAssistProcessor extends CommonContentAssistProcessor
{
	private static enum Location
	{
		ERROR
	};

	private static final Image JS_FUNCTION = Activator.getImage("/icons/js_function.gif"); //$NON-NLS-1$
	private static final Image JS_PROPERTY = Activator.getImage("/icons/js_property.gif"); //$NON-NLS-1$

	private JSContentAssistHelper _helper;
	private IContextInformationValidator _validator;
	private Lexeme<JSTokenType> _currentLexeme;

	/**
	 * JSIndexContentAssitProcessor
	 * 
	 * @param editor
	 */
	public JSContentAssistProcessor(AbstractThemeableEditor editor)
	{
		super(editor);

		this._helper = new JSContentAssistHelper();
	}
	
	/**
	 * addProposal
	 * 
	 * @param proposals
	 * @param name
	 * @param icon
	 * @param userAgents
	 * @param offset
	 */
	private void addProposal(List<ICompletionProposal> proposals, String name, Image image, String description, Image[] userAgents, int offset)
	{
		int length = name.length();
		String displayName = name;
		IContextInformation contextInfo = null;

		// TEMP:
		int replaceLength = 0;

		if (this._currentLexeme != null)
		{
			offset = this._currentLexeme.getStartingOffset();
			replaceLength = this._currentLexeme.getLength();
		}

		// build proposal
		CommonCompletionProposal proposal = new CommonCompletionProposal(name, offset, replaceLength, length, image, displayName, contextInfo, description);
		proposal.setFileLocation(JSIndexConstants.CORE);
		proposal.setUserAgentImages(userAgents);

		// add it to the list
		proposals.add(proposal);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.editor.common.CommonContentAssistProcessor#computeCompletionProposals(org.eclipse.jface.text.ITextViewer
	 * , int, char, boolean)
	 */
	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset, char activationChar, boolean autoActivated)
	{
		// tokenize the current document
		IDocument document = viewer.getDocument();
		LexemeProvider<JSTokenType> lexemeProvider = new LexemeProvider<JSTokenType>(document, offset, new JSScopeScanner())
		{
			@Override
			protected JSTokenType getTypeFromName(String name)
			{
				return JSTokenType.get(name);
			}
		};

		// store a reference to the lexeme at the current position
		this._currentLexeme = lexemeProvider.getFloorLexeme(offset);

		// first step is to determine where we are
		Location location = this.getLocation(lexemeProvider, offset);

		List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();
		
		switch (location)
		{
			default:
				break;
		}

		// sort by display name
		Collections.sort(result, new Comparator<ICompletionProposal>()
		{
			@Override
			public int compare(ICompletionProposal o1, ICompletionProposal o2)
			{
				return o1.getDisplayString().compareToIgnoreCase(o2.getDisplayString());
			}
		});

		// select the current proposal based on the current lexeme
		if (this._currentLexeme != null)
		{
			this.setSelectedProposal(this._currentLexeme.getText(), result);
		}

		return result.toArray(new ICompletionProposal[result.size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
	 */
	@Override
	public char[] getCompletionProposalAutoActivationCharacters()
	{
		return new char[] { '.' };
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CommonContentAssistProcessor#getContextInformationValidator()
	 */
	@Override
	public IContextInformationValidator getContextInformationValidator()
	{
		if (this._validator == null)
		{
			this._validator = new JSContextInformationValidator();
		}

		return this._validator;
	}

	/**
	 * getGlobals
	 */
	public void getGlobals(List<ICompletionProposal> proposals, int offset)
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
			IContextInformation contextInfo = null; // new ContextInformation(JS_FUNCTION, "Display String", "Info");

			// build a proposal
			CompletionProposal proposal = new CompletionProposal(name, offset, 0, length, image, name, contextInfo, description);

			// add it to the list
			proposals.add(proposal);
		}
	}

	/**
	 * getLocation
	 * 
	 * @param lexemeProvider
	 * @param offset
	 * @return
	 */
	private Location getLocation(LexemeProvider<JSTokenType> lexemeProvider, int offset)
	{
		return Location.ERROR;
	}
}
