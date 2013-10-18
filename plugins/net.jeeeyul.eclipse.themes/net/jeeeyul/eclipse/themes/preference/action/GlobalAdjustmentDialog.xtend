package net.jeeeyul.eclipse.themes.preference.action

import net.jeeeyul.eclipse.themes.preference.ChromeConstants
import net.jeeeyul.eclipse.themes.preference.ChromePreferenceExtensions
import net.jeeeyul.eclipse.themes.preference.ChromePreferenceInitializer
import net.jeeeyul.eclipse.themes.preference.ChromeThemePrefererncePage
import net.jeeeyul.eclipse.themes.preference.annotations.ValueType
import net.jeeeyul.eclipse.themes.ui.HSB
import net.jeeeyul.eclipse.themes.ui.SWTExtensions
import org.eclipse.jface.dialogs.Dialog
import org.eclipse.jface.preference.PreferenceStore
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Scale
import net.jeeeyul.eclipse.themes.SharedImages
import net.jeeeyul.eclipse.themes.Messages

class GlobalAdjustmentDialog extends Dialog {
	extension SWTExtensions = SWTExtensions::INSTANCE
	extension ChromePreferenceExtensions = new ChromePreferenceExtensions
	
	ChromeThemePrefererncePage prefPage
	PreferenceStore originalStore
	
	Scale hueScale
	Scale saturationScale
	Scale brightnessScale
	
	new(ChromeThemePrefererncePage page) {
		super(page.control.shell)
		prefPage = page
		
		originalStore = new PreferenceStore();
		new ChromePreferenceInitializer().initializeDefaultPreferences(originalStore);
		prefPage.pages.forEach[save(originalStore)]
	}
	
	override create() {
		super.create()
		shell.text = Messages::GLOBAL_ADJUSTMENT
		shell.image = SharedImages::getImage(SharedImages::PALETTE)
	}
	
	override protected createButtonBar(Composite parent) {
		parent.newSeparator[]
		super.createButtonBar(parent)
	}
	
	override protected createDialogArea(Composite parent) {
		var container = super.createDialogArea(parent) as Composite
		
		container.newComposite[
			layoutData = FILL_HORIZONTAL
			layout = newGridLayout[
				numColumns = 2
			]
			
			newLabel[
				text = Messages::HUE_SHIFTING
			]
			
			hueScale = newScale[
				layoutData = FILL_HORIZONTAL
				minimum = 0
				maximum = 360
				selection = 180
				pageIncrement = 30
				onSelection = [
					updatePreview()
				]
			]
			
			newLabel[
				text = Messages::SATURATION_AMP
			]
			
			saturationScale = newScale[
				layoutData = FILL_HORIZONTAL
				minimum = 0
				maximum = 200
				selection = 100
				pageIncrement = 20
				onSelection = [
					updatePreview()
				]
			]
			
			newLabel[
				text = Messages::BRIGHTNESS_AMP
			]
			
			brightnessScale = newScale[
				layoutData = FILL_HORIZONTAL
				minimum = 0
				maximum = 200
				selection = 100
				pageIncrement = 20
				onSelection = [
					updatePreview()
				]
			]
		]
		
		return container
	}

	
	def updatePreview(){
		var copy = originalStore.copy
		
		var hueFields = typeof(ChromeConstants).declaredFields.filter[
			it.annotations.exists[
				it.annotationType == typeof(ValueType) && 
				(it as ValueType).semantics == "hue" && 
				(it as ValueType).value == typeof(float)
			]
		]
		
		var saturationFields = typeof(ChromeConstants).declaredFields.filter[
			it.annotations.exists[
				it.annotationType == typeof(ValueType) && 
				(it as ValueType).semantics == "saturation" && 
				(it as ValueType).value == typeof(float)
			]
		]
		
		var brightnessFields = typeof(ChromeConstants).declaredFields.filter[
			it.annotations.exists[
				it.annotationType == typeof(ValueType) && 
				(it as ValueType).semantics == "brightness" && 
				(it as ValueType).value == typeof(float)
			]
		]
		
		var hsbFields = typeof(ChromeConstants).declaredFields.filter[
			it.annotations.exists[
				it.annotationType == typeof(ValueType) && 
				(it as ValueType).value == typeof(HSB)
			]
		]
		
		// shifting hue
		var hueAdjust = hueScale.selection - 180 + 360
		for(each : hueFields){
			var key = each.get((typeof(ChromeConstants))) as String
			var newHue = (originalStore.getFloat(key) + hueAdjust) % 360f
			copy.setValue(key, newHue)
		}
		
		// amp saturation
		var satAmp = saturationScale.selection / 100f
		for(each : saturationFields){
			var key = each.get((typeof(ChromeConstants))) as String
			var newSaturation = (originalStore.getFloat(key) * satAmp).limit(0f, 1f)
			copy.setValue(key, newSaturation)
		}
		
		
		// amp brightness
		var brightnessAmp = brightnessScale.selection / 100f
		for(each : brightnessFields){
			var key = each.get((typeof(ChromeConstants))) as String
			var newBrightness = (originalStore.getFloat(key) * brightnessAmp).limit(0f, 1f)
			copy.setValue(key, newBrightness)
		}
		
		for(each : hsbFields){
			var key = each.get((typeof(ChromeConstants))) as String
			var hsb = originalStore.getHSB(key).copy
			hsb.hue = (hsb.hue + hueAdjust) % 360f
			hsb = hsb.ampBrightness(brightnessAmp)
			hsb = hsb.ampSaturation(satAmp)
			copy.setValue(key, hsb)
		}
				
		copy.apply
	}
	
	def apply(PreferenceStore store){
		for(eachPage : prefPage.pages){
			eachPage.load(store)
		}
	}
	
	def PreferenceStore getCopy(PreferenceStore store){
		var result = new PreferenceStore()
		
		for(eachName : store.preferenceNames){
			result.setValue(eachName, store.getString(eachName))
		}
		
		return result
	}
	
	def static void main(String[] args) {
		var hueFields = typeof(ChromeConstants).declaredFields.filter[
			it.annotations.exists[
				it.annotationType == typeof(ValueType) && 
				(it as ValueType).semantics == "hue" && 
				(it as ValueType).value == typeof(float)
			]
		].toList
		
		for(each : hueFields){
			println(each)
		}
	}
	
	def limit(float original, float min, float max) {
		return Math::min(Math::max(original, min), max);
	}
}