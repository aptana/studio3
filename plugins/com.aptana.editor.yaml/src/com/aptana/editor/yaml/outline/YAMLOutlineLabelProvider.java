package com.aptana.editor.yaml.outline;

import java.util.regex.Pattern;

import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.outline.CommonOutlineLabelProvider;
import com.aptana.editor.yaml.YAMLPlugin;
import com.aptana.editor.yaml.parsing.MapParseNode;
import com.aptana.editor.yaml.parsing.NodeTupleNode;
import com.aptana.editor.yaml.parsing.ScalarParseNode;
import com.aptana.editor.yaml.parsing.SequenceParseNode;
import com.aptana.parsing.ast.IParseNode;

public class YAMLOutlineLabelProvider extends CommonOutlineLabelProvider
{

	private static final String TUPLE_IMAGE = "icons/property.png"; //$NON-NLS-1$
	private static final String SEQUENCE_IMAGE = "icons/array-literal.png"; //$NON-NLS-1$
	private static final String SELECTOR_IMAGE = "icons/selector.png"; //$NON-NLS-1$
	private static final String STRING_IMAGE = "icons/string.png"; //$NON-NLS-1$
	private static final String NUMBER_IMAGE = "icons/number.png"; //$NON-NLS-1$
	// FIXME This number detection pattern was stolen from YAMLCodeScanner
	private final static Pattern p = Pattern
			.compile("(\\+|-)?((0(x|X|o|O)[0-9a-fA-F]*)|(([0-9]+\\.?[0-9]*)|(\\.[0-9]+))((e|E)(\\+|-)?[0-9]+)?)(L|l|UL|ul|u|U|F|f)?"); //$NON-NLS-1$

	@Override
	public String getText(Object element)
	{
		if (element instanceof IParseNode)
		{
			IParseNode parseNode = (IParseNode) element;
			return parseNode.getText();
		}
		return super.getText(element);
	}

	@Override
	public Image getImage(Object element)
	{
		// TODO use special icons for references/pointers. Can we get that from Node.isResolved() ?
		if (element instanceof ScalarParseNode)
		{
			// TODO Check for date format
			ScalarParseNode spn = (ScalarParseNode) element;
			String text = spn.getText();
			if (p.matcher(text).matches())
			{
				return YAMLPlugin.getImage(NUMBER_IMAGE);
			}
			return YAMLPlugin.getImage(STRING_IMAGE);
		}
		if (element instanceof MapParseNode)
		{
			return YAMLPlugin.getImage(SELECTOR_IMAGE);
		}
		if (element instanceof SequenceParseNode)
		{
			return YAMLPlugin.getImage(SEQUENCE_IMAGE);
		}
		if (element instanceof NodeTupleNode)
		{
			return YAMLPlugin.getImage(TUPLE_IMAGE);
		}
		return super.getImage(element);
	}

}
