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
		// TODO use special icons for references. Can we get that from Node.isResolved() ?
		if (element instanceof ScalarParseNode)
		{
			// TODO If scalar matches certain formats, we can say it's not just a string, but a number/date/whatever
			ScalarParseNode spn = (ScalarParseNode) element;
			String text = spn.getText();
			// FIXME Stolen from YAMLCodeScanner
			Pattern p = Pattern
					.compile("(\\+|-)?((0(x|X|o|O)[0-9a-fA-F]*)|(([0-9]+\\.?[0-9]*)|(\\.[0-9]+))((e|E)(\\+|-)?[0-9]+)?)(L|l|UL|ul|u|U|F|f)?");
			if (p.matcher(text).matches())
			{
				return YAMLPlugin.getImage("icons/number.png");
			}
			return YAMLPlugin.getImage("icons/string.png");
		}
		if (element instanceof MapParseNode)
		{
			return YAMLPlugin.getImage("icons/selector.png");
		}
		if (element instanceof SequenceParseNode)
		{
			return YAMLPlugin.getImage("icons/array-literal.png");
		}
		if (element instanceof NodeTupleNode)
		{
			return YAMLPlugin.getImage("icons/property.png");
		}
		return super.getImage(element);
	}

}
