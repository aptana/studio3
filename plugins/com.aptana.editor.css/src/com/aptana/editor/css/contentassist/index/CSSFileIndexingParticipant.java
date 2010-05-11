package com.aptana.editor.css.contentassist.index;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;

import com.aptana.core.util.IOUtil;
import com.aptana.editor.css.Activator;
import com.aptana.editor.css.CSSColors;
import com.aptana.editor.css.ICSSConstants;
import com.aptana.editor.css.parsing.CSSParser;
import com.aptana.editor.css.parsing.ast.CSSAttributeSelectorNode;
import com.aptana.editor.css.parsing.ast.CSSRuleNode;
import com.aptana.editor.css.parsing.ast.CSSTermNode;
import com.aptana.index.core.IFileIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.IParseNode;

public class CSSFileIndexingParticipant implements IFileIndexingParticipant
{
	private static final String CSS_EXTENSION = "css"; //$NON-NLS-1$

	@Override
	public void index(Set<IFile> files, Index index, IProgressMonitor monitor)
	{		
		for (IFile file : files)
		{
			if (isCSSFile(file))
			{
				try
				{
					String fileContents = IOUtil.read(file.getContents());
					ParseState parseState = new ParseState();
					parseState.setEditState(fileContents, "", 0, 0); //$NON-NLS-1$
					CSSParser cssParser = new CSSParser();
					IParseNode parseNode = cssParser.parse(parseState);
					walkNode(index, file, parseNode);
				}
				catch (CoreException e)
				{
					Activator.logError(e);
				}
				catch (Exception e)
				{
					Activator.logError(e.getMessage(), e);
				}
			}
		}
	}
	
	private boolean isCSSFile(IFile file)
	{
		InputStream stream = null;
		IContentTypeManager manager = Platform.getContentTypeManager();
		try
		{
			stream = file.getContents();
			IContentType[] types = manager.findContentTypesFor(stream, file.getName());
			for (IContentType type : types)
			{
				if (type.getId().equals(ICSSConstants.CONTENT_TYPE_CSS))
				return true;
			}
		}
		catch (CoreException e)
		{
			Activator.logError(e);
		}
		catch (Exception e)
		{
			Activator.logError(e.getMessage(), e);
		}
		finally
		{
			try
			{
				if (stream != null)
					stream.close();
			}
			catch (IOException e)
			{
				// ignore
			}
		}
		
		return CSS_EXTENSION.equalsIgnoreCase(file.getFileExtension());
	}

	public static void walkNode(Index index, IFile file, IParseNode parent)
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

	private static void addIndex(Index index, IFile file, String category, String word)
	{
		index.addEntry(category, word, file.getProjectRelativePath().toPortableString());
	}

}
