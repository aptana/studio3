/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.dnd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

import com.aptana.core.util.StringUtil;
import com.aptana.scripting.model.SnippetElement;

/**
 * Transfer used for Snippets within Studio
 * 
 * @author nle
 */
public class SnippetTransfer extends ByteArrayTransfer
{

	private static SnippetTransfer _instance = new SnippetTransfer();

	private static final String ID_NAME = "SNIPPET_TRANSFER"; //$NON-NLS-1$
	private static final int[] IDS = new int[] { registerType(ID_NAME) };

	public static SnippetTransfer getInstance()
	{
		return _instance;
	}

	@Override
	protected int[] getTypeIds()
	{
		return IDS;
	}

	@Override
	protected String[] getTypeNames()
	{
		return new String[] { ID_NAME };
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.dnd.ByteArrayTransfer#javaToNative(java.lang.Object, org.eclipse.swt.dnd.TransferData)
	 */
	@Override
	protected void javaToNative(Object object, TransferData transferData)
	{
		if (object instanceof SnippetElement)
		{
			SnippetElement snippetElement = (SnippetElement) object;
			byte[] bytes = getBytes(snippetElement);
			if (bytes != null)
			{
				super.javaToNative(bytes, transferData);
			}
		}
		else
		{
			super.javaToNative(object, transferData);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.dnd.ByteArrayTransfer#nativeToJava(org.eclipse.swt.dnd.TransferData)
	 */
	@Override
	protected Object nativeToJava(TransferData transferData)
	{
		byte[] bytes = (byte[]) super.nativeToJava(transferData);
		return fromBytes(bytes);
	}

	/*
	 * Snippet is serialized: (String) path (String) name (String) scope (String) expansion
	 */
	private byte[] getBytes(SnippetElement element)
	{
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(byteOut);

		byte[] bytes = null;

		try
		{
			String path = element.getPath();
			out.writeUTF(path != null ? path : StringUtil.EMPTY);
			String displayName = element.getDisplayName();
			out.writeUTF(displayName != null ? displayName : StringUtil.EMPTY);
			String scope = element.getScope();
			out.writeUTF(scope != null ? scope : StringUtil.EMPTY);
			String expansion = element.getExpansion();
			out.writeUTF(expansion != null ? expansion : StringUtil.EMPTY);

			out.close();
			bytes = byteOut.toByteArray();
		}
		catch (IOException e)
		{
			// when in doubt send nothing
		}

		return bytes;
	}

	/*
	 * Snippet is serialized: (String) path (String) name (String) scope (String) expansion
	 */
	private SnippetElement fromBytes(byte[] data)
	{
		ByteArrayInputStream byteIn = new ByteArrayInputStream(data);
		DataInputStream in = new DataInputStream(byteIn);

		SnippetElement element = null;

		try
		{
			String path = in.readUTF();
			if (path != null)
			{
				element = new SnippetElement(path);
				element.setDisplayName(in.readUTF());
				element.setScope(in.readUTF());
				element.setExpansion(in.readUTF());
			}

			in.close();
		}
		catch (IOException e)
		{
			// when in doubt send nothing
		}

		return element;
	}
}
