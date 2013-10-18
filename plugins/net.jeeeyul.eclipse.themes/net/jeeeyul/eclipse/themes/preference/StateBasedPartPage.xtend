package net.jeeeyul.eclipse.themes.preference

import net.jeeeyul.eclipse.themes.SharedImages
import net.jeeeyul.eclipse.themes.ui.ColorPicker
import net.jeeeyul.eclipse.themes.ui.ColorWell
import net.jeeeyul.eclipse.themes.ui.HSB
import net.jeeeyul.eclipse.themes.ui.SWTExtensions
import org.eclipse.jface.dialogs.IDialogConstants
import org.eclipse.jface.preference.IPreferenceStore
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Control
import static net.jeeeyul.eclipse.themes.preference.ChromeConstants.*
import net.jeeeyul.eclipse.themes.Messages

class StateBasedPartPage extends ChromePage {
	extension SWTExtensions = new SWTExtensions
	
	private boolean isActive

	ColorWell startColorWell
	ColorWell endColorWell	
	ColorWell outlineColorWell
	ColorWell selectedTitleColorWell
	ColorWell unselectedTitleColorWell
	
	ColorWell selectedTabStartColorWell
	ColorWell selectedTabEndColorWell
	
	Button autoEndColorButton
	Button syncEndColorHueButton
	Button autoOutlineColorButton
	Button syncOutlineColorHueButton
	Button partShinyShadowButton
	
	ColorPreview preview
	
	new(String name, boolean isActive){
		super(name, SharedImages::PART)
		this.isActive = isActive
	}

	override create(Composite parent) {
		preview = new ColorPreview(getTabFolder)
		
		parent=>[
			layout = newGridLayout
			
			newLabel[
				text = 	
					if(isActive) 
						Messages::ACTIVE_PART_DESCRIPTION
					else
						Messages::INACTIVE_PART_DESCRIPTION
			]
			
			newGroup[
				layoutData = FILL_HORIZONTAL
				text = Messages::COLORS
				layout = newGridLayout[
					numColumns = 5
					makeColumnsEqualWidth = false
				]
				
				newLabel[
					text= Messages::FILL_START + ":"
				]
				
				startColorWell = newColorWell[
					onSelection = [
						syncHueAndComputeAutoColors()
						updatePreview()
					]
				]
				
				newPushButton[
					text = Messages::CHANGE
					onClick = [
						showColorPicker(startColorWell)
					]
				]
				
				newLabel[
					layoutData = newGridData[
						horizontalSpan = 2
					]
				]
				
				
				newLabel[
					text = Messages::FILL_END + ":"
				]
				
				endColorWell = newColorWell[
					onSelection = [
						updatePreview()
					]
				]
				
				newPushButton[
					text = Messages::CHANGE
					onClick = [
						showColorPicker(endColorWell)
					]
				]
				
				autoEndColorButton = newCheckbox[
					text = Messages::AUTO
					onSelection = [
						updateEnablement()
						syncHueAndComputeAutoColors()
					]
				]
				
				syncEndColorHueButton = newCheckbox[
					text = Messages::SYNC_HUE
					onSelection = [
						updateSync()
					]
				]
				
				newLabel[
					text = Messages::OUTLINE + ":"
				]
				
				outlineColorWell = newColorWell[
					onSelection = [
						updatePreview()
					]
				]
								
				newPushButton[
					text = Messages::CHANGE
					onClick = [
						showColorPicker(outlineColorWell)
					]
				]
				
				autoOutlineColorButton = newCheckbox[
					text = Messages::AUTO
					onSelection = [
						updateEnablement()
						syncHueAndComputeAutoColors()
					]
				]
				
				syncOutlineColorHueButton = newCheckbox[
					text = Messages::SYNC_HUE
					onSelection = [
						updateSync()
					]
				]
				
			] // end Group
			
			newGroup[
				text = Messages::SELECTED_TAB
				layout = newGridLayout[numColumns = 3]
				layoutData = FILL_HORIZONTAL
				
				newLabel[
					text = Messages::TEXT + ":"
				]
				
				selectedTitleColorWell = newColorWell[
					onSelection = [
						updatePreview()
					]
				]
				newPushButton[
					text = Messages::CHANGE
					onClick = [
						showColorPicker(selectedTitleColorWell)
					]
				]
				
				newLabel[
					text=Messages::FILL_START + ":"
				]
				
				selectedTabStartColorWell = newColorWell[
					onSelection = [
						updatePreview()
					]
				]
				
				newPushButton[
					text = Messages::CHANGE
					onClick = [
						showColorPicker(selectedTabStartColorWell)
					]
				]
				
				newLabel[
					text=Messages::FILL_END + ":"
				]
				
				selectedTabEndColorWell = newColorWell[
					onSelection = [
						updatePreview()
					]
				]
				
				newPushButton[
					text = Messages::CHANGE
					onClick = [
						showColorPicker(selectedTabEndColorWell)
					]
				]
				
			] // End Group
			
			newGroup[
				text = Messages::UNSELECTED_TAB
				layout = newGridLayout[numColumns = 3]
				layoutData = FILL_HORIZONTAL
				
				newLabel[
					text = Messages::TEXT + ":"
				]
				
				unselectedTitleColorWell = newColorWell[
					onSelection = [
						updatePreview()
					]
				]
				newPushButton[
					text = Messages::CHANGE
					onClick = [
						showColorPicker(unselectedTitleColorWell)
					]
				]
				
				partShinyShadowButton = newCheckbox[
					text = Messages::SHINY_SHADOW
					layoutData = newGridData[horizontalSpan = 2]
					onSelection = [
						updatePreview()
					]
				]
			] // End Group
		]
	}
	def private void updateSync() { 
		endColorWell.setData("lock-hue", syncEndColorHueButton.selection)
		if(syncEndColorHueButton.selection && !autoEndColorButton.selection){
			endColorWell.selection = endColorWell.selection.rewriteHue(startColorWell.selection.hue)
		}
		
		outlineColorWell.setData("lock-hue", syncOutlineColorHueButton.selection)
		if(syncOutlineColorHueButton.selection && !autoOutlineColorButton.selection){
			outlineColorWell.selection = outlineColorWell.selection.rewriteHue(startColorWell.selection.hue)
		}
	}
	
	def private void updateEnablement() {
		endColorWell.next.enabled = !autoEndColorButton.selection
		syncEndColorHueButton.enabled = !autoEndColorButton.selection
		outlineColorWell.next.enabled = !autoOutlineColorButton.selection
		syncOutlineColorHueButton.enabled = !autoOutlineColorButton.selection
	}
	
	def Control next(Control control){
		var index = control.parent.children.indexOf(control)
		return control.parent.children.get(index + 1)
	}
	
	def private void syncHueAndComputeAutoColors() {
		if(autoEndColorButton.selection){
			var startRGB = startColorWell.selection
			endColorWell.selection = startRGB.ampSaturation(1.15f).ampBrightness(0.95f)
		}
		
		else if(syncEndColorHueButton.selection){
			endColorWell.selection = endColorWell.selection.rewriteHue(startColorWell.selection.hue)
		}
		
		if(autoOutlineColorButton.selection){
			var startRGB = startColorWell.selection
			outlineColorWell.selection = startRGB.ampSaturation(3f).ampBrightness(0.7f)
		}
		
		else if(syncOutlineColorHueButton.selection){
			outlineColorWell.selection = outlineColorWell.selection.rewriteHue(startColorWell.selection.hue)
		}
	}
	
	override dispose() {
		preview.dispose()
	}

	override load(IPreferenceStore store) {
		startColorWell.selection = new HSB(
			store.getFloat(CHROME_ACTIVE_START_HUE.applyActive),
			store.getFloat(CHROME_ACTIVE_START_SATURATION.applyActive), 
			store.getFloat(CHROME_ACTIVE_START_BRIGHTNESS.applyActive)
		)
		
		endColorWell.selection = new HSB(
			store.getFloat(CHROME_ACTIVE_END_HUE.applyActive),
			store.getFloat(CHROME_ACTIVE_END_SATURATION.applyActive), 
			store.getFloat(CHROME_ACTIVE_END_BRIGHTNESS.applyActive)
		)
		
		outlineColorWell.selection = new HSB(
			store.getFloat(CHROME_ACTIVE_OUTLINE_HUE.applyActive),
			store.getFloat(CHROME_ACTIVE_OUTLINE_SATURATION.applyActive), 
			store.getFloat(CHROME_ACTIVE_OUTLINE_BRIGHTNESS.applyActive)
		)
		
		selectedTitleColorWell.selection = new HSB(
			store.getFloat(CHROME_ACTIVE_SELECTED_TITLE_HUE.applyActive),
			store.getFloat(CHROME_ACTIVE_SELECTED_TITLE_SATURATION.applyActive), 
			store.getFloat(CHROME_ACTIVE_SELECTED_TITLE_BRIGHTNESS.applyActive)
		)
		
		unselectedTitleColorWell.selection = new HSB(
			store.getFloat(CHROME_ACTIVE_UNSELECTED_TITLE_HUE.applyActive),
			store.getFloat(CHROME_ACTIVE_UNSELECTED_TITLE_SATURATION.applyActive), 
			store.getFloat(CHROME_ACTIVE_UNSELECTED_TITLE_BRIGHTNESS.applyActive)
		)
		
		autoEndColorButton.selection = store.getBoolean(CHROME_AUTO_ACTIVE_END_COLOR.applyActive)
		autoOutlineColorButton.selection = store.getBoolean(CHROME_AUTO_ACTIVE_OUTLINE_COLOR.applyActive)
		syncEndColorHueButton.selection = store.getBoolean(CHROME_LOCK_ACTIVE_END_HUE.applyActive)
		syncOutlineColorHueButton.selection = store.getBoolean(CHROME_LOCK_ACTIVE_OUTLINE_HUE.applyActive)
		
		partShinyShadowButton.selection = store.getBoolean(CHROME_ACTIVE_UNSELECTED_TITLE_SHINY_SHADOW.applyActive)
		
		selectedTabStartColorWell.selection = new HSB(
			store.getFloat(CHROME_ACTIVE_SELECTED_TAB_START_HUE.applyActive),
			store.getFloat(CHROME_ACTIVE_SELECTED_TAB_START_SATURATION.applyActive), 
			store.getFloat(CHROME_ACTIVE_SELECTED_TAB_START_BRIGHTNESS.applyActive)
		)
		
		selectedTabEndColorWell.selection = new HSB(
			store.getFloat(CHROME_ACTIVE_SELECTED_TAB_END_HUE.applyActive),
			store.getFloat(CHROME_ACTIVE_SELECTED_TAB_END_SATURATION.applyActive), 
			store.getFloat(CHROME_ACTIVE_SELECTED_TAB_END_BRIGHTNESS.applyActive)
		)
		
		updateEnablement()
		updateSync()
		updatePreview()
	}
	
	def private void updatePreview(){
		var hasToUpdate = parentPage.activePage == this || isActive
		if(!hasToUpdate){
			return
		}
		
		preview.gradientStart = startColorWell.selection.toRGB
		preview.gradientEnd = endColorWell.selection.toRGB
		preview.outline = outlineColorWell.selection.toRGB
		preview.selectedTitle = selectedTitleColorWell.selection.toRGB
		preview.unselectedTitle = unselectedTitleColorWell.selection.toRGB
		preview.castShinyShadow = partShinyShadowButton.selection
		preview.selectedTabStart = selectedTabStartColorWell.selection.toRGB
		preview.selectedTabEnd = selectedTabEndColorWell.selection.toRGB
		
		preview.run();
	}
	
	override save(IPreferenceStore store) {
		store.setValue(CHROME_ACTIVE_START_HUE.applyActive, startColorWell.selection.hue)
		store.setValue(CHROME_ACTIVE_START_SATURATION.applyActive, startColorWell.selection.saturation)
		store.setValue(CHROME_ACTIVE_START_BRIGHTNESS.applyActive, startColorWell.selection.brightness)
		
		store.setValue(CHROME_ACTIVE_END_HUE.applyActive, endColorWell.selection.hue)
		store.setValue(CHROME_ACTIVE_END_SATURATION.applyActive, endColorWell.selection.saturation)
		store.setValue(CHROME_ACTIVE_END_BRIGHTNESS.applyActive, endColorWell.selection.brightness)
		
		store.setValue(CHROME_ACTIVE_OUTLINE_HUE.applyActive, outlineColorWell.selection.hue)
		store.setValue(CHROME_ACTIVE_OUTLINE_SATURATION.applyActive, outlineColorWell.selection.saturation)
		store.setValue(CHROME_ACTIVE_OUTLINE_BRIGHTNESS.applyActive, outlineColorWell.selection.brightness)
		
		store.setValue(CHROME_ACTIVE_SELECTED_TITLE_HUE.applyActive, selectedTitleColorWell.selection.hue)
		store.setValue(CHROME_ACTIVE_SELECTED_TITLE_SATURATION.applyActive, selectedTitleColorWell.selection.saturation)
		store.setValue(CHROME_ACTIVE_SELECTED_TITLE_BRIGHTNESS.applyActive, selectedTitleColorWell.selection.brightness)
		
		store.setValue(CHROME_ACTIVE_UNSELECTED_TITLE_HUE.applyActive, unselectedTitleColorWell.selection.hue)
		store.setValue(CHROME_ACTIVE_UNSELECTED_TITLE_SATURATION.applyActive, unselectedTitleColorWell.selection.saturation)
		store.setValue(CHROME_ACTIVE_UNSELECTED_TITLE_BRIGHTNESS.applyActive, unselectedTitleColorWell.selection.brightness)
		
		store.setValue(CHROME_AUTO_ACTIVE_END_COLOR.applyActive, autoEndColorButton.selection)
		store.setValue(CHROME_AUTO_ACTIVE_OUTLINE_COLOR.applyActive, autoOutlineColorButton.selection)
		store.setValue(CHROME_LOCK_ACTIVE_END_HUE.applyActive, syncEndColorHueButton.selection)
		store.setValue(CHROME_LOCK_ACTIVE_OUTLINE_HUE.applyActive, syncOutlineColorHueButton.selection)
		
		store.setValue(CHROME_ACTIVE_UNSELECTED_TITLE_SHINY_SHADOW.applyActive, partShinyShadowButton.selection)
		
		store.setValue(CHROME_ACTIVE_SELECTED_TAB_START_HUE.applyActive, selectedTabStartColorWell.selection.hue)
		store.setValue(CHROME_ACTIVE_SELECTED_TAB_START_SATURATION.applyActive, selectedTabStartColorWell.selection.saturation)
		store.setValue(CHROME_ACTIVE_SELECTED_TAB_START_BRIGHTNESS.applyActive, selectedTabStartColorWell.selection.brightness)
		
		store.setValue(CHROME_ACTIVE_SELECTED_TAB_END_HUE.applyActive, selectedTabEndColorWell.selection.hue)
		store.setValue(CHROME_ACTIVE_SELECTED_TAB_END_SATURATION.applyActive, selectedTabEndColorWell.selection.saturation)
		store.setValue(CHROME_ACTIVE_SELECTED_TAB_END_BRIGHTNESS.applyActive, selectedTabEndColorWell.selection.brightness)
	}

	override setToDefault(IPreferenceStore store) {
		startColorWell.selection = new HSB(
			store.getDefaultFloat(CHROME_ACTIVE_START_HUE.applyActive),
			store.getDefaultFloat(CHROME_ACTIVE_START_SATURATION.applyActive), 
			store.getDefaultFloat(CHROME_ACTIVE_START_BRIGHTNESS.applyActive)
		)
		
		endColorWell.selection = new HSB(
			store.getDefaultFloat(CHROME_ACTIVE_END_HUE.applyActive),
			store.getDefaultFloat(CHROME_ACTIVE_END_SATURATION.applyActive), 
			store.getDefaultFloat(CHROME_ACTIVE_END_BRIGHTNESS.applyActive)
		)
		
		outlineColorWell.selection = new HSB(
			store.getDefaultFloat(CHROME_ACTIVE_OUTLINE_HUE.applyActive),
			store.getDefaultFloat(CHROME_ACTIVE_OUTLINE_SATURATION.applyActive), 
			store.getDefaultFloat(CHROME_ACTIVE_OUTLINE_BRIGHTNESS.applyActive)
		)
		
		selectedTitleColorWell.selection = new HSB(
			store.getDefaultFloat(CHROME_ACTIVE_SELECTED_TITLE_HUE.applyActive),
			store.getDefaultFloat(CHROME_ACTIVE_SELECTED_TITLE_SATURATION.applyActive), 
			store.getDefaultFloat(CHROME_ACTIVE_SELECTED_TITLE_BRIGHTNESS.applyActive)
		)
		
		unselectedTitleColorWell.selection = new HSB(
			store.getDefaultFloat(CHROME_ACTIVE_UNSELECTED_TITLE_HUE.applyActive),
			store.getDefaultFloat(CHROME_ACTIVE_UNSELECTED_TITLE_SATURATION.applyActive), 
			store.getDefaultFloat(CHROME_ACTIVE_UNSELECTED_TITLE_BRIGHTNESS.applyActive)
		)
		
		autoEndColorButton.selection = store.getDefaultBoolean(CHROME_AUTO_ACTIVE_END_COLOR.applyActive)
		autoOutlineColorButton.selection = store.getDefaultBoolean(CHROME_AUTO_ACTIVE_OUTLINE_COLOR.applyActive)
		syncEndColorHueButton.selection = store.getDefaultBoolean(CHROME_LOCK_ACTIVE_END_HUE.applyActive)
		syncOutlineColorHueButton.selection = store.getDefaultBoolean(CHROME_LOCK_ACTIVE_OUTLINE_HUE.applyActive)
		partShinyShadowButton.selection = store.getDefaultBoolean(CHROME_ACTIVE_UNSELECTED_TITLE_SHINY_SHADOW.applyActive)
		
		selectedTabStartColorWell.selection = new HSB(
			store.getDefaultFloat(CHROME_ACTIVE_SELECTED_TAB_START_HUE.applyActive),
			store.getDefaultFloat(CHROME_ACTIVE_SELECTED_TAB_START_SATURATION.applyActive), 
			store.getDefaultFloat(CHROME_ACTIVE_SELECTED_TAB_START_BRIGHTNESS.applyActive)
		)
		
		selectedTabEndColorWell.selection = new HSB(
			store.getDefaultFloat(CHROME_ACTIVE_SELECTED_TAB_END_HUE.applyActive),
			store.getDefaultFloat(CHROME_ACTIVE_SELECTED_TAB_END_SATURATION.applyActive), 
			store.getDefaultFloat(CHROME_ACTIVE_SELECTED_TAB_END_BRIGHTNESS.applyActive)
		)
		
		updateEnablement()
		updateSync()
		updatePreview()
	}
	
	def private void showColorPicker(ColorWell well) { 
		var picker = new ColorPicker()
		var original = well.selection
		
		picker.selection = well.selection
		picker.continuosSelectionHandler = [
			well.selection = it
		]
		if(well.getData("lock-hue") == true){
			picker.lockHue = true
		}

		if(picker.open() == IDialogConstants::OK_ID){
			well.selection = picker.selection
		}else{
			well.selection = original
		}
	}
	
	def String applyActive(String key){
		if(!isActive){
			return key.replace("active", "inactive")
		}
		else{
			return key
		}
	}
	
	override onPageSelected() {
		updatePreview()
	}
	
	override onPageDeselected() {
		if(!isActive){
			var activePage = parentPage.pages.filter(typeof(StateBasedPartPage)).findFirst[it.isActive]
			activePage.updatePreview()
		}
	}
	
}