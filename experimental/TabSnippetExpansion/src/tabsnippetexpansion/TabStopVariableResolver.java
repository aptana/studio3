package tabsnippetexpansion;

import org.eclipse.jface.text.templates.SimpleTemplateVariableResolver;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateVariable;

class TabStopVariableResolver extends SimpleTemplateVariableResolver {

	TabStopVariableResolver(String type, String description) {
		super(type, description);
	}
	
	@Override
	public void resolve(TemplateVariable variable, TemplateContext context) {
		super.resolve(variable, context);
		if (variable.getType().equals(variable.getName())) {
			variable.setValues(new String[] {""});
			setEvaluationString("");
		} else {
			setEvaluationString(variable.getName());			
		}
	}

	protected boolean isUnambiguous(TemplateContext context) {
		return false;
	}
}
