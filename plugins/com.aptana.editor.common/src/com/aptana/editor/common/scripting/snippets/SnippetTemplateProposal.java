package com.aptana.editor.common.scripting.snippets;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.InclusivePositionUpdater;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.jface.text.templates.GlobalTemplateVariables;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateException;
import org.eclipse.jface.text.templates.TemplateProposal;
import org.eclipse.jface.text.templates.TemplateVariable;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

public class SnippetTemplateProposal extends TemplateProposal implements ICompletionProposalExtension6
{

	private static final int SPACE_INDENT_SIZE = 2;

	protected ICompletionProposal[] templateProposals;
	protected char triggerChar;
	protected char[] triggerChars;

	private final Template fTemplate;
	private final TemplateContext fContext;
	private final IRegion fRegion;

	private ICompletionProposal delegateTemplateProposal;
	private IRegion fSelectedRegion; // initialized by apply()

	private InclusivePositionUpdater fUpdater;

	private StyledString styledDisplayString;

	private Styler styler;

	public SnippetTemplateProposal(Template template, TemplateContext context, IRegion region, Image image,
			int relevance)
	{
		super(template, context, region, image, relevance);

		fTemplate = template;
		fContext = context;
		fRegion = region;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.templates.TemplateProposal#getAdditionalProposalInfo()
	 */
	public String getAdditionalProposalInfo()
	{
		return getTemplate().getPattern();
	}

	@Override
	public void apply(final ITextViewer viewer, char trigger, int stateMask, final int offset)
	{
		delegateTemplateProposal = null;
		if (contains(triggerChars, trigger))
		{
			if (triggerChar == trigger)
			{
				doApply(viewer, trigger, stateMask, offset);
			}
			else
			{
				delegateTemplateProposal = templateProposals[trigger - '1'];
				((ICompletionProposalExtension2) templateProposals[trigger - '1']).apply(viewer, trigger, stateMask,
						offset);
			}
		}
		else
		{
			doApply(viewer, trigger, stateMask, offset);
		}
	}

	protected void doApply(final ITextViewer viewer, char trigger, int stateMask, final int offset)
	{
		IDocument document = viewer.getDocument();
		try
		{
			fContext.setReadOnly(false);
			int start;
			TemplateBuffer templateBuffer;
			{
				int oldReplaceOffset = getReplaceOffset();

				if (fTemplate instanceof SnippetTemplate)
				{
					// Reset indented pattern
					((SnippetTemplate) fTemplate).setIndentedPattern(null);

					IRegion lineInformationOfStart = document.getLineInformationOfOffset(oldReplaceOffset);
					int lineInformationOfStartOffset = lineInformationOfStart.getOffset();
					if (oldReplaceOffset > lineInformationOfStartOffset)
					{
						// Get the text from beginning of line to the replacement start offset
						String prefix = document.get(lineInformationOfStartOffset, oldReplaceOffset
								- lineInformationOfStartOffset);

						// Is there any leading white space?
						if (prefix.matches("\\s+.*")) //$NON-NLS-1$
						{
							// Yes. Prefix each line in the template's pattern with the same white space
							String indentedPattern = fTemplate.getPattern().replaceAll("(\r\n|\r|\n)", //$NON-NLS-1$
									"$1" + prefix.replaceFirst("(\\s+).*", "$1")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

							// We need to convert so that the pattern uses the same type of indent (spaces vs tabs)
							indentedPattern = conformIndents(prefix, indentedPattern);

							((SnippetTemplate) fTemplate).setIndentedPattern(indentedPattern);
						}
					}
				}

				try
				{
					// this may already modify the document
					templateBuffer = fContext.evaluate(fTemplate);
				}
				catch (TemplateException e1)
				{
					fSelectedRegion = fRegion;
					return;
				}

				start = getReplaceOffset();
				int shift = start - oldReplaceOffset;
				int end = Math.max(getReplaceEndOffset(), offset + shift);

				// insert template string
				String templateString = templateBuffer.getString();
				document.replace(start, end - start, templateString);
			}

			// translate positions
			LinkedModeModel model = new LinkedModeModel();
			TemplateVariable[] variables = templateBuffer.getVariables();

			// If there is no tab stop variable in this template use
			// default sequence number
			int defaultSequenceNumber = LinkedPositionGroup.NO_STOP;
			for (TemplateVariable templateVariable : variables)
			{
				String type = templateVariable.getType();
				if (TabStopVariableResolver.VARIABLE_TYPE.equals(type))
				{
					try
					{
						Integer.parseInt(templateVariable.getName());
						// The non tab stop variables are visited after
						// visiting the tab stop variables
						defaultSequenceNumber = Integer.MAX_VALUE - 1;
						break;
					}
					catch (NumberFormatException nfe)
					{
						// ignore
					}
				}
			}

			boolean hasPositions = false;
			for (int i = 0; i != variables.length; i++)
			{
				TemplateVariable variable = variables[i];

				if (variable.isUnambiguous())
					continue;

				LinkedPositionGroup group = new LinkedPositionGroup();

				int[] offsets = variable.getOffsets();
				int length = variable.getLength();

				int sequenceNumber = defaultSequenceNumber;

				String type = variable.getType();
				if (TabStopVariableResolver.VARIABLE_TYPE.equals(type))
				{
					try
					{
						sequenceNumber = Integer.parseInt(variable.getName());
					}
					catch (NumberFormatException nfe)
					{
						// ignore
					}
				}

				LinkedPosition first;
				{
					String[] values = variable.getValues();
					ICompletionProposal[] proposals = new ICompletionProposal[values.length];
					for (int j = 0; j < values.length; j++)
					{
						ensurePositionCategoryInstalled(document, model);
						Position pos = new Position(offsets[0] + start, length);
						document.addPosition(getCategory(), pos);
						proposals[j] = new PositionBasedCompletionProposal(values[j], pos, length);
					}

					if (proposals.length > 1)
						first = new ProposalPosition(document, offsets[0] + start, length, sequenceNumber, proposals);
					else
						first = new LinkedPosition(document, offsets[0] + start, length, sequenceNumber);
				}

				for (int j = 0; j != offsets.length; j++)
				{
					if (j == 0)
					{
						group.addPosition(first);
					}
					else
					{
						group.addPosition(new LinkedPosition(document, offsets[j] + start, length));
					}
				}

				model.addGroup(group);
				hasPositions = true;
			}

			if (hasPositions)
			{
				model.forceInstall();
				LinkedModeUI ui = new LinkedModeUI(model, viewer);

				// Do not cycle
				ui.setCyclingMode(LinkedModeUI.CYCLE_NEVER);
				ui.setExitPosition(viewer, getCaretOffset(templateBuffer) + start, 0, Integer.MAX_VALUE);
				ui.enter();

				fSelectedRegion = ui.getSelectedRegion();
			}
			else
			{
				ensurePositionCategoryRemoved(document);
				fSelectedRegion = new Region(getCaretOffset(templateBuffer) + start, 0);
			}

		}
		catch (BadLocationException e)
		{
			openErrorDialog(viewer.getTextWidget().getShell(), e);
			ensurePositionCategoryRemoved(document);
			fSelectedRegion = fRegion;
		}
		catch (BadPositionCategoryException e)
		{
			openErrorDialog(viewer.getTextWidget().getShell(), e);
			fSelectedRegion = fRegion;
		}

	}

	/**
	 * Given the prefix text in the editor, modify the snippet patterns' indents to use same type of indentation (tabs
	 * vs spaces). TODO Figure out space tab width and use it rather than constant of 2!
	 * 
	 * @param prefix
	 * @param indentedPattern
	 * @return
	 */
	protected String conformIndents(String prefix, String indentedPattern)
	{
		boolean useTabs = prefix.contains("\t"); //$NON-NLS-1$

		Pattern p = Pattern.compile("(\r\n|\r|\n)(\\s*)"); //$NON-NLS-1$
		Matcher m = p.matcher(indentedPattern);
		int startIndex = 0;
		StringBuilder builder = new StringBuilder();
		while (m.find(startIndex))
		{
			builder.append(indentedPattern.substring(startIndex, m.start(1)));
			startIndex = m.end(2);
			String indent = convertIndent(m.group(2), useTabs);
			builder.append(m.group(1));
			builder.append(indent);
		}
		builder.append(indentedPattern.substring(startIndex));
		indentedPattern = builder.toString();
		return indentedPattern;
	}

	private String convertIndent(String indent, boolean useTabs)
	{
		if (useTabs && indent.contains(" ")) //$NON-NLS-1$
		{
			String newIndent = ""; //$NON-NLS-1$
			for (int i = 0; i < indent.length() / SPACE_INDENT_SIZE; i++)
			{
				newIndent += '\t';
			}
			return newIndent;
		}
		if (!useTabs && indent.contains("\t")) //$NON-NLS-1$
		{
			String newIndent = ""; //$NON-NLS-1$
			for (int i = 0; i < indent.length() * SPACE_INDENT_SIZE; i++)
			{
				newIndent += " "; //$NON-NLS-1$
			}
			return newIndent;
		}
		return indent;
	}

	private void ensurePositionCategoryInstalled(final IDocument document, LinkedModeModel model)
	{
		if (!document.containsPositionCategory(getCategory()))
		{
			document.addPositionCategory(getCategory());
			fUpdater = new InclusivePositionUpdater(getCategory());
			document.addPositionUpdater(fUpdater);

			model.addLinkingListener(new ILinkedModeListener()
			{
				/*
				 * @see
				 * org.eclipse.jface.text.link.ILinkedModeListener#left(org.eclipse.jface.text.link.LinkedModeModel,
				 * int)
				 */
				public void left(LinkedModeModel environment, int flags)
				{
					ensurePositionCategoryRemoved(document);
				}

				public void suspend(LinkedModeModel environment)
				{
				}

				public void resume(LinkedModeModel environment, int flags)
				{
				}
			});
		}
	}

	private void ensurePositionCategoryRemoved(IDocument document)
	{
		if (document.containsPositionCategory(getCategory()))
		{
			try
			{
				document.removePositionCategory(getCategory());
			}
			catch (BadPositionCategoryException e)
			{
				// ignore
			}
			document.removePositionUpdater(fUpdater);
		}
	}

	private String getCategory()
	{
		return "SnippetTemplateProposalCategory_" + toString(); //$NON-NLS-1$
	}

	private int getCaretOffset(TemplateBuffer buffer)
	{

		TemplateVariable[] variables = buffer.getVariables();
		for (int i = 0; i != variables.length; i++)
		{
			TemplateVariable variable = variables[i];
			if (variable.getType().equals(GlobalTemplateVariables.Cursor.NAME))
				return variable.getOffsets()[0];
		}

		return buffer.getString().length();
	}

	private void openErrorDialog(Shell shell, Exception e)
	{
		MessageDialog.openError(shell, Messages.SnippetTemplateProposal_TITLE_SnippetTemplateProposalError,
				e.getMessage());
	}

	/*
	 * @see ICompletionProposal#getSelection(IDocument)
	 */
	public Point getSelection(IDocument document)
	{
		if (delegateTemplateProposal == null)
		{
			return new Point(fSelectedRegion.getOffset(), fSelectedRegion.getLength());
		}
		return delegateTemplateProposal.getSelection(document);
	}

	@Override
	public char[] getTriggerCharacters()
	{
		return triggerChars;
	}

	@Override
	public String getDisplayString()
	{
		return getStyledDisplayString().getString().trim();
	}

	@Override
	public StyledString getStyledDisplayString()
	{
		if (styledDisplayString == null)
		{
			Template template = getTemplate();
			styledDisplayString = new StyledString(String.format("%1$-20.20s%2$10.10s ", //$NON-NLS-1$
					template.getDescription(), template.getName() + "\u00bb") //$NON-NLS-1$
					+ (triggerChar == '\000' ? " " : String.valueOf(triggerChar)) //$NON-NLS-1$
					// Need padding on windows to work around the width computation
					+ (Platform.OS_WIN32.equals(Platform.getOS()) ? "                                " : ""), //$NON-NLS-1$ //$NON-NLS-2$ 
					styler);
		}
		return styledDisplayString;
	}

	Template getTemplateSuper()
	{
		return super.getTemplate();
	}

	void setTriggerChar(char triggerChar)
	{
		this.triggerChar = triggerChar;
	}

	void setStyler(Styler styler)
	{
		this.styler = styler;
	}

	private static final String TRIGGER_CHARS = "123456789"; //$NON-NLS-1$

	void setTemplateProposals(ICompletionProposal[] templateProposals)
	{
		this.templateProposals = templateProposals;
		triggerChars = new char[Math.min(templateProposals.length, TRIGGER_CHARS.length())];
		TRIGGER_CHARS.getChars(0, triggerChars.length, triggerChars, 0);
	}

	protected static boolean contains(char[] characters, char c)
	{
		if (characters == null)
			return false;

		for (int i = 0; i < characters.length; i++)
		{
			if (c == characters[i])
				return true;
		}

		return false;
	}

}
