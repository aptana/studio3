package com.aptana.editor.css.contentassist.index;

import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.core.util.IOUtil;
import com.aptana.editor.css.Activator;
import com.aptana.editor.css.CSSColors;
import com.aptana.editor.css.parsing.ICSSParserConstants;
import com.aptana.editor.css.parsing.ast.CSSAttributeSelectorNode;
import com.aptana.editor.css.parsing.ast.CSSRuleNode;
import com.aptana.editor.css.parsing.ast.CSSTermNode;
import com.aptana.index.core.IFileStoreIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.parsing.IParser;
import com.aptana.parsing.IParserPool;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.ast.IParseNode;

public class CSSFileIndexingParticipant implements IFileStoreIndexingParticipant
{
	@Override
	public void index(Set<IFileStore> files, Index index, IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, files.size());
		for (IFileStore file : files)
		{
			if (sub.isCanceled())
				return;
			try
			{
				if (file == null)
				{
					continue;
				}
				sub.subTask(file.getName());
				try
				{
					String fileContents = IOUtil.read(file.openInputStream(EFS.NONE, sub.newChild(-1)));
					if (fileContents != null && fileContents.trim().length() > 0)
					{
						ParseState parseState = new ParseState();
						parseState.setEditState(fileContents, "", 0, 0); //$NON-NLS-1$
						IParserPool pool = ParserPoolFactory.getInstance().getParserPool(ICSSParserConstants.LANGUAGE);
						if (pool != null)
						{
							IParser cssParser = pool.checkOut();
							IParseNode parseNode = cssParser.parse(parseState);
							pool.checkIn(cssParser);
							walkNode(index, file, parseNode);
						}
					}
				}
				catch (CoreException e)
				{
					Activator.logError(e);
				}
				catch (Throwable e)
				{
					Activator.logError(e.getMessage(), e);
				}
			}
			finally
			{
				monitor.worked(1);
			}
		}
		monitor.done();
	}

	public static void walkNode(Index index, IFileStore file, IParseNode parent)
	{
		if (parent == null)
			return;

		if (parent instanceof CSSAttributeSelectorNode)
		{
			CSSAttributeSelectorNode cssAttributeSelectorNode = (CSSAttributeSelectorNode) parent;
			String text = cssAttributeSelectorNode.getText();
			if (text != null && text.startsWith(".")) //$NON-NLS-1$
			{
				addIndex(index, file, CSSIndexConstants.CLASS, text.substring(1));
			}
			else if (text != null && text.startsWith("#")) //$NON-NLS-1$
			{
				addIndex(index, file, CSSIndexConstants.IDENTIFIER, text.substring(1));
			}
		}

		if (parent instanceof CSSTermNode)
		{
			CSSTermNode term = (CSSTermNode) parent;
			String value = term.getText();
			if (isColor(value))
			{
				addIndex(index, file, CSSIndexConstants.COLOR, CSSColors.to6CharHexWithLeadingHash(value.trim()));
			}
		}

		if (parent instanceof CSSRuleNode)
		{
			CSSRuleNode cssRuleNode = (CSSRuleNode) parent;
			for (IParseNode child : cssRuleNode.getSelectors())
			{
				walkNode(index, file, child);
			}
			for (IParseNode child : cssRuleNode.getDeclarations())
			{
				walkNode(index, file, child);
			}
		}
		else
		{
			for (IParseNode child : parent.getChildren())
			{
				walkNode(index, file, child);
			}
		}

	}

	private static boolean isColor(String value)
	{
		if (value == null || value.trim().length() == 0)
			return false;
		if (CSSColors.namedColorExists(value))
			return true;
		if (value.startsWith("#") && (value.length() == 4 || value.length() == 7)) //$NON-NLS-1$
			return true; // FIXME Check to make sure it's hex values!
		return false;
	}

	private static void addIndex(Index index, IFileStore file, String category, String word)
	{
		index.addEntry(category, word, file.toURI());
	}

}
