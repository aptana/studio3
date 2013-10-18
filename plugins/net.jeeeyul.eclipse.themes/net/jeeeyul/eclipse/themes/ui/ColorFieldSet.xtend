package net.jeeeyul.eclipse.themes.ui

import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Event
import org.eclipse.swt.widgets.Text
import org.eclipse.xtext.xbase.lib.Procedures$Procedure1

class ColorFieldSet {
	extension SWTExtensions = new SWTExtensions
	
	boolean ignoreModify
	HSB fSelection = new HSB()
	
	Text redField;
	Text greenField;
	Text blueField;
	Text hueField;
	Text saturationField;
	Text brightnessField;
	Text htmlField;
	
	Composite control
	
	@Property Procedures$Procedure1<HSB> selectionHandler
	
	new(Composite parent){
		create(parent)
		updateField(null)
	}
	
	def getControl(){
		control;
	}
	
	def private void create(Composite parent){
		control = new Composite(parent, SWT::NORMAL) => [
			layout = newGridLayout[
				numColumns = 2
			]
			
			newLabel[
				text = "Red"
			]
			redField = newTextField[
				validRange = 0 -> 255
				kind = "rgb"
			]
			
			newLabel[
				text = "Green"
			]
			greenField = newTextField[
				validRange = 0 -> 255
				kind = "rgb"
			]
			
			newLabel[
				text = "Blue"
			]
			blueField = newTextField[
				validRange = 0 -> 255
				kind = "rgb"
			]
			
			newSeparator[]
			
			newLabel[
				text = "Hue"
			]
			hueField = newTextField[
				validRange = 0f -> 360f
				kind = "hsb"
			]
			
			newLabel[
				text = "Saturation"
			]
			saturationField = newTextField[
				validRange = 0f -> 1f
				kind = "hsb"
			]
			
			newLabel[
				text = "Brightness"
			]
			brightnessField = newTextField[
				validRange = 0f -> 1f
				kind = "hsb"
			]
			
			newSeparator[]
			
			newLabel[
				text = "HTML Code"
			]
			htmlField = newTextField[
				kind = "html"
				validExpression = "#?[0-9a-fA-F]{0,6}"
				onFocusOut = [
					ignoreModify = true
					updateHTMLField()
					ignoreModify = false
				]
			]
		]
		
		for(each : control.children.filter(typeof(Text)).toList){
			each.addListener(SWT::Modify)[
				handleModify(it)
			]
			each.layoutData = FILL_HORIZONTAL[
				widthHint = 100
			]
		}
	}
	
	def private void handleModify(Event event) {
		if(ignoreModify){
			return
		}
		
		var field = event.widget as Text
		var kind = field.kind
		var HSB newHSB = null
	
		switch(kind){
			case "hsb":{
				newHSB = new HSB(
					hueField.floatValue, 
					saturationField.floatValue, 
					brightnessField.floatValue
				);
			}
			
			case "rgb":{
				newHSB = new HSB(
					redField.intValue,
					greenField.intValue,
					blueField.intValue
				)
				
				if(newHSB.saturation == 0f){
					newHSB.hue = fSelection.hue
				}
			}
			
			case "html":{
				var code = htmlField.text.trim
				var offset = -1
				var length = -1
				var (String)=>int converter
				
				if(code.matches("#[0-9a-fA-F]{6}")){
					offset = 1
					length = 2
					converter = [
						Integer::parseInt(it, 16)
					]
				}
				else if(code.matches("[0-9a-fA-F]{6}")){
					offset = 0
					length = 2
					converter = [
						Integer::parseInt(it, 16)
					]
				}
				else if(code.matches("#[0-9a-fA-F]{3}")){
					offset = 1
					length = 1
					converter = [
						Integer::parseInt(it + it, 16)
					]
				}
				else if(code.matches("[0-9a-fA-F]{3}")){
					offset = 0
					length = 1
					converter = [
						Integer::parseInt(it + it, 16)
					]
				}
				else{
					return
				}
				
				var redString = code.substring(offset, offset + length)
				var greenString = code.substring(offset + length, offset + length * 2)
				var blueString = code.substring(offset + length * 2, offset + length * 3)
				
				newHSB = new HSB(
					converter.apply(redString),
					converter.apply(greenString),
					converter.apply(blueString)
				)
				
				if(newHSB.saturation == 0f){
					newHSB.hue = fSelection.hue
				}
			}
		}
		
		fSelection = newHSB
		updateField(kind)
		notifySelection()
	}
	
	def private notifySelection() { 
		if(selectionHandler != null){
			selectionHandler.apply(fSelection)
		}
	}
	
	def void setSelection(HSB hsb){
		if(this.fSelection == hsb || this.fSelection.equals(hsb)){
			return;
		}
		this.fSelection = hsb.getCopy()
		updateField(null)
	}

	def private updateField(String excludeKind) {
		ignoreModify = true;
		
		if(excludeKind != "hsb"){
			hueField.text = fSelection.hue.toString
			saturationField.text = fSelection.saturation.toString
			brightnessField.text = fSelection.brightness.toString
		}
		
		if(excludeKind != "rgb"){
			redField.text = fSelection.toRGB.red.toString
			greenField.text = fSelection.toRGB.green.toString
			blueField.text = fSelection.toRGB.blue.toString
		}
		
		if(excludeKind != "html"){
			updateHTMLField()
		}
		
		ignoreModify = false;
	}
	
	def private updateHTMLField(){
		htmlField.text = String::format("#%02x%02x%02x", fSelection.toRGB.red, fSelection.toRGB.green, fSelection.toRGB.blue).toUpperCase
	}
	
	def private setValidRange(Text text, IntRange range){
		text.addListener(SWT::Verify)[
			if(ignoreModify){
				return
			}
			
			var before = text.text
			var after = before.subSequence(0, text.selection.x) + it.text + before.substring(text.selection.y)
			var evaluate = after.trim()
			if(evaluate.empty){
				evaluate = "0"
			}
			if(!evaluate.matches("[0-9]*")){
				it.doit = false
				return
			}
			
			var value = Integer::parseInt(evaluate)
			it.doit = (range.min <= value && value <= range.max)
		]
	}
	
	def private setValidRange(Text text, FloatRange range){
		text.addListener(SWT::Verify)[
			if(ignoreModify){
				return
			}
			
			var before = text.text
			var after = before.subSequence(0, text.selection.x) + it.text + before.substring(text.selection.y)
			var evaluate = after.trim()
			if(evaluate.empty){
				evaluate = "0"
			}
			if(!evaluate.matches("[0-9]*(\\.[0-9]*)?")){
				it.doit = false
				return
			}
			if(evaluate == "."){
				evaluate = "0"
			}
			var value = Float::parseFloat(evaluate)
			it.doit = (range.min <= value && value <= range.max)
		]
	}
	
	def private setValidExpression(Text text, String regExp){
		text.addListener(SWT::Verify)[
			if(ignoreModify){
				return
			}
			
			var before = text.text
			var after = before.subSequence(0, text.selection.x) + it.text + before.substring(text.selection.y)
			var evaluate = after.trim()
			
			it.doit = evaluate.matches(regExp)
		]
	}
	
	def private setKind(Text text, String kind){
		text.setData("kind", kind)
	}
	
	def private String getKind(Text text){
		text.getData("kind") as String
	}
	
	def private operator_mappedTo(int min, int max){
		return new IntRange() => [
			it.min = min
			it.max = max
		]
	}
	
	def private operator_mappedTo(float min, float max){
		return new FloatRange() => [
			it.min = min
			it.max = max
		]
	}
	
	def private intValue(Text text){
		var eval = text.text.trim
		if(eval.empty){
			eval = "0";
		}
		return Integer::parseInt(eval)
	}
	
	def private floatValue(Text text){
		var eval = text.text.trim
		if(eval.empty){
			eval = "0";
		}
		if(eval == "."){
			return 0f
		}
		return Float::parseFloat(eval)
	}
}