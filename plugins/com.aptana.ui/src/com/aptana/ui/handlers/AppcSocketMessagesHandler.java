/**
 * Aptana Studio
 * Copyright (c) 2015 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.handlers;

import com.aptana.core.resources.RequestCancelledException;
import com.aptana.core.resources.SocketMessagesHandler;
import com.aptana.core.util.StringUtil;
import com.aptana.ui.dialogs.InputMessageDialog;
import com.aptana.ui.dialogs.MultipleInputMessageDialog;
import com.aptana.ui.util.UIUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * The default handler to address the requests or messages coming from the underlying CLI. This will just prompt the
 * user with the set of questions coming from the CLI and respond back with the user responses.
 * <p>
 * If the user is willing to cancel it, then it will take care of sending a <code>null</code> back.
 * </p>
 *
 * @author pinnamuri
 */
public class AppcSocketMessagesHandler extends SocketMessagesHandler
{

	private static final String MESSAGE = "message"; //$NON-NLS-1$
	private static final String ERROR = "error"; //$NON-NLS-1$
	private static final String QUESTION = "question"; //$NON-NLS-1$
	private static final String TYPE = "type"; //$NON-NLS-1$
	private final String actionName;
	private String description;

	public AppcSocketMessagesHandler()
	{
		this(Messages.AppcSocketMessagesHandler_title, Messages.AppcSocketMessagesHandler_Description);
	}

	public AppcSocketMessagesHandler(String actionName, String description)
	{
		this.actionName = actionName;
		this.description = description;

	}

	public JsonNode handleRequest(JsonNode request) throws RequestCancelledException
	{
		final JsonNode type = request.path(TYPE);
		if (QUESTION.equals(type.asText()))
		{
			return handleQuestion(request);
		}
		else if (ERROR.equals(type.asText()))
		{
			System.err.println(request.path(MESSAGE).asText());
			if (request.has(QUESTION))
			{
				return handleQuestion(request);
			}
		}
		return null;
	}

	private JsonNode handleQuestion(final JsonNode type) throws RequestCancelledException
	{
		final JsonNode questionNode = type.path(QUESTION);
		final ObjectNode[] response = new ObjectNode[1];
		final JsonNode qType = type.path(TYPE);
		String asText = type.path(MESSAGE).asText();
		if (ERROR.equals(qType.asText()) && !StringUtil.isEmpty(asText))
		{
			description = asText;
		}
		else
		{
			description = Messages.AppcSocketMessagesHandler_Description;
		}
		UIUtils.getDisplay().syncExec(new Runnable()
		{
			public void run()
			{
				InputMessageDialog dialog;
				if (questionNode.isArray())
				{
					dialog = new MultipleInputMessageDialog(questionNode, actionName, description);
				}
				else
				{
					dialog = new InputMessageDialog(questionNode, actionName, description);
				}

				int exitCode = dialog.open();
				if (exitCode == 0)
				{
					response[0] = (ObjectNode) dialog.getValue();
				}
				else
				{
					response[0] = null;
				}
			}
		});
		if (response[0] == null)
		{
			throw new RequestCancelledException();
		}
		return response[0];
	}

}
