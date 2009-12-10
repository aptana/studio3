package com.aptana.editor.haml;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import com.aptana.editor.common.CompositeSubPartitionScanner;
import com.aptana.editor.common.IPartitionScannerSwitchStrategy;
import com.aptana.editor.common.ISubPartitionScanner;
import com.aptana.editor.common.PartitionScannerSwitchStrategy;
import com.aptana.editor.common.SubPartitionScanner;

public class HAMLSubPartitionScanner extends CompositeSubPartitionScanner
{

	private static final int TYPE_JS = 1;

	private static final String[] JS_SWITCH_SEQUENCES = new String[] { "</script>" //$NON-NLS-1$
	};

	private static final String[][] EMPTY = new String[0][];

	/**
	 *
	 */
	public HAMLSubPartitionScanner()
	{
		super(
				new ISubPartitionScanner[] {
						new SubPartitionScanner(HAMLSourceConfiguration.getDefault().getPartitioningRules(),
								HAMLSourceConfiguration.CONTENT_TYPES, new Token(HAMLSourceConfiguration.DEFAULT)),
						JSSourceConfiguration.getDefault().createSubPartitionScanner() },
				new IPartitionScannerSwitchStrategy[] { new PartitionScannerSwitchStrategy(JS_SWITCH_SEQUENCES, EMPTY) });
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.CompositeSubPartitionScanner#setLastToken(org.eclipse.jface.text.rules.IToken)
	 */
	@Override
	public void setLastToken(IToken token)
	{
		if (!(token.getData() instanceof String))
		{
			current = TYPE_DEFAULT;
			return;
		}
		String contentType = (String) token.getData();
		if (HAMLSourceConfiguration.HTML_SCRIPT.equals(contentType))
		{
			current = TYPE_JS;
		}
		else if (HAMLSourceConfiguration.DEFAULT.equals(contentType)
				|| IDocument.DEFAULT_CONTENT_TYPE.equals(contentType))
		{
			current = TYPE_DEFAULT;
		}
		else
		{
			for (int i = 0; i < subPartitionScanners.length; ++i)
			{
				if (subPartitionScanners[i].hasContentType(contentType))
				{
					current = i;
					break;
				}
			}
		}
	}

}