package com.aptana.editor.js.inferencing;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.contentassist.UserAgentManager;
import com.aptana.editor.common.contentassist.UserAgentManager.UserAgent;
import com.aptana.editor.js.JSTypeConstants;
import com.aptana.editor.js.contentassist.model.BaseElement;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.ParameterElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.ReturnTypeElement;
import com.aptana.editor.js.contentassist.model.UserAgentElement;
import com.aptana.editor.js.parsing.ast.JSDeclarationNode;
import com.aptana.editor.js.parsing.ast.JSFunctionNode;
import com.aptana.editor.js.parsing.ast.JSNameValuePairNode;
import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.editor.js.parsing.ast.JSNodeTypes;
import com.aptana.editor.js.sdoc.model.DocumentationBlock;
import com.aptana.editor.js.sdoc.model.ExampleTag;
import com.aptana.editor.js.sdoc.model.ParamTag;
import com.aptana.editor.js.sdoc.model.ReturnTag;
import com.aptana.editor.js.sdoc.model.Tag;
import com.aptana.editor.js.sdoc.model.TagType;
import com.aptana.editor.js.sdoc.model.Type;
import com.aptana.editor.js.sdoc.model.TypeTag;
import com.aptana.parsing.ast.IParseNode;

public class JSTypeUtil
{
	private static final Set<String> FILTERED_TYPES;

	/**
	 * static initializer
	 */
	static
	{
		FILTERED_TYPES = new HashSet<String>();
		FILTERED_TYPES.add(JSTypeConstants.ARRAY_TYPE);
		FILTERED_TYPES.add(JSTypeConstants.BOOLEAN_TYPE);
		FILTERED_TYPES.add(JSTypeConstants.FUNCTION_TYPE);
		FILTERED_TYPES.add(JSTypeConstants.NUMBER_TYPE);
		FILTERED_TYPES.add(JSTypeConstants.OBJECT_TYPE);
		FILTERED_TYPES.add(JSTypeConstants.REG_EXP_TYPE);
		FILTERED_TYPES.add(JSTypeConstants.STRING_TYPE);
		FILTERED_TYPES.add(JSTypeConstants.UNDEFINED_TYPE);
		FILTERED_TYPES.add(JSTypeConstants.VOID_TYPE);
		FILTERED_TYPES.add(JSTypeConstants.WINDOW_TYPE);
		FILTERED_TYPES.add(JSTypeConstants.WINDOW_PROPERTY);
	}

	/**
	 * addAllUserAgents
	 * 
	 * @param element
	 */
	public static void addAllUserAgents(BaseElement element)
	{
		if (element != null)
		{
			// make valid in all user agents
			for (UserAgent userAgent : UserAgentManager.getInstance().getAllUserAgents())
			{
				UserAgentElement ua = new UserAgentElement();

				ua.setPlatform(userAgent.ID);

				element.addUserAgent(ua);
			}
		}
	}

	/**
	 * applyDocumentation
	 * 
	 * @param function
	 * @param block
	 */
	public static void applyDocumentation(FunctionElement function, DocumentationBlock block)
	{
		if (block != null)
		{
			// apply description
			function.setDescription(block.getText());

			// apply parameters
			for (Tag tag : block.getTags(TagType.PARAM))
			{
				ParamTag paramTag = (ParamTag) tag;
				ParameterElement parameter = new ParameterElement();

				parameter.setName(paramTag.getName());
				parameter.setDescription(paramTag.getText());
				parameter.setUsage(paramTag.getUsage().getName());

				for (Type type : paramTag.getTypes())
				{
					parameter.addType(type.toSource());
				}

				function.addParameter(parameter);
			}

			// apply return types
			for (Tag tag : block.getTags(TagType.RETURN))
			{
				ReturnTag returnTag = (ReturnTag) tag;

				for (Type type : returnTag.getTypes())
				{
					ReturnTypeElement returnType = new ReturnTypeElement();

					returnType.setType(type.toSource());
					returnType.setDescription(returnTag.getText());

					function.addReturnType(returnType);
				}
			}

			// apply examples
			for (Tag tag : block.getTags(TagType.EXAMPLE))
			{
				ExampleTag exampleTag = (ExampleTag) tag;

				function.addExample(exampleTag.getText());
			}
		}
	}

	/**
	 * applyDocumentation
	 * 
	 * @param property
	 * @param block
	 */
	public static void applyDocumentation(PropertyElement property, DocumentationBlock block)
	{
		if (property instanceof FunctionElement)
		{
			applyDocumentation((FunctionElement) property, block);
		}
		else
		{
			if (block != null)
			{
				// apply description
				property.setDescription(block.getText());

				// apply types
				for (Tag tag : block.getTags(TagType.TYPE))
				{
					TypeTag typeTag = (TypeTag) tag;

					for (Type type : typeTag.getTypes())
					{
						ReturnTypeElement returnType = new ReturnTypeElement();

						returnType.setType(type.toSource());
						returnType.setDescription(typeTag.getText());

						property.addType(returnType);
					}
				}

				// apply examples
				for (Tag tag : block.getTags(TagType.EXAMPLE))
				{
					ExampleTag exampleTag = (ExampleTag) tag;

					property.addExample(exampleTag.getText());
				}
			}
		}
	}

	/**
	 * getName
	 * 
	 * @param node
	 * @return
	 */
	public static String getName(JSNode node)
	{
		String result = null;

		if (node != null)
		{
			List<String> parts = new ArrayList<String>();
			JSNode current = node;

			while (current != null)
			{
				switch (current.getNodeType())
				{
					case JSNodeTypes.IDENTIFIER:
						parts.add(current.getText());
						break;

					case JSNodeTypes.FUNCTION:
						JSFunctionNode function = (JSFunctionNode) current;
						IParseNode functionName = function.getName();

						if (functionName.isEmpty() == false)
						{
							parts.add(functionName.getText());
						}
						// else
						// {
						// parts.add("function-" + node.getStartingOffset());
						// }
						break;

					case JSNodeTypes.NAME_VALUE_PAIR:
						JSNameValuePairNode entry = (JSNameValuePairNode) current;
						IParseNode entryName = entry.getName();
						String name = entryName.getText();

						if (entryName.getNodeType() == JSNodeTypes.STRING)
						{
							name = name.substring(1, name.length() - 1);
						}

						parts.add(name);
						break;

					case JSNodeTypes.DECLARATION:
						JSDeclarationNode declaration = (JSDeclarationNode) current;
						IParseNode declarationName = declaration.getIdentifier();

						parts.add(declarationName.getText());
						break;

					default:
						break;
				}

				IParseNode parent = current.getParent();

				current = (parent instanceof JSNode) ? (JSNode) parent : null;
			}

			if (parts.size() > 0)
			{
				Collections.reverse(parts);

				result = StringUtil.join(".", parts); //$NON-NLS-1$
			}
		}

		// Don't allow certain names to avoid confusion an to prevent overwriting
		// core types
		if (FILTERED_TYPES.contains(result))
		{
			result = null;
		}

		return result;
	}

	/**
	 * getUniqueTypeName
	 * 
	 * @return
	 */
	public static String getUniqueTypeName()
	{
		UUID uuid = UUID.randomUUID();

		return MessageFormat.format("{0}{1}", JSTypeConstants.DYNAMIC_CLASS_PREFIX, uuid); //$NON-NLS-1$
	}

	/**
	 * JSContentAssistUtil
	 */
	private JSTypeUtil()
	{
	}
}
