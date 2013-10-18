package net.jeeeyul.eclipse.themes.preference

import net.jeeeyul.eclipse.themes.SharedImages
import net.jeeeyul.eclipse.themes.preference.internal.FontNameProvider
import net.jeeeyul.eclipse.themes.rendering.ChromeTabRendering
import net.jeeeyul.eclipse.themes.ui.SWTExtensions
import org.eclipse.jface.preference.IPreferenceStore
import org.eclipse.jface.viewers.ComboViewer
import org.eclipse.jface.viewers.IStructuredSelection
import org.eclipse.jface.viewers.StructuredSelection
import org.eclipse.swt.SWT
import org.eclipse.swt.program.Program
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Combo
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Label
import org.eclipse.swt.widgets.Scale
import org.eclipse.swt.widgets.Text

import static net.jeeeyul.eclipse.themes.preference.ChromeConstants.*
import net.jeeeyul.eclipse.themes.Messages

class CommonPartPage extends ChromePage {
	extension SWTExtensions = new SWTExtensions
	ComboViewer fontSelector
	Text fontSizeField
	Scale paddingScale
	Scale cornerRadiusScale
	Label paddingLabel
	Label cornerRadiusLabel
	Button useMruButton
	FontPreview fontPreview

	new() {
		super(Messages::PART, SharedImages::PART)
	}

	override create(Composite parent) {
		fontPreview = new FontPreview(tabFolder)
		parent=>[
			layout = newGridLayout
			newLabel[
				text = Messages::PART_DESCRIPTION
			]
			newGroup[
				layoutData = FILL_HORIZONTAL
				text = Messages::PART_TITLE
				layout = newGridLayout[
					numColumns = 2
					makeColumnsEqualWidth = false
				]
				newLabel[ text = Messages::FONT + ":" ]
				var combo = new Combo(it, SWT::READ_ONLY)=>[]
				fontSelector = new ComboViewer(combo)
				fontSelector.contentProvider = new FontNameProvider()
				fontSelector.input = new Object()
				fontSelector.addSelectionChangedListener[
					updatePreview()
				]
				newLabel[ text = Messages::SIZE + ":" ]
				fontSizeField = newTextField[
					layoutData = newGridData[
						widthHint = 50
					]
					onModified = [
						updatePreview()
					]
				]
			]
			// End Group
			
			newGroup[
				text = Messages::PART_STACK_SHAPE
				layoutData = FILL_HORIZONTAL
				layout = newGridLayout[
					numColumns = 3
				]
				
				newLabel[
					text = Messages::CORNER_RADIUS + ":"
				]
				cornerRadiusLabel = newLabel[
					text = "10px"
					layoutData = newGridData[
						widthHint = 35
					]
				]
				cornerRadiusScale = newScale[
					minimum = 6
					maximum = 25
					selection = 10
					pageIncrement = 1
					layoutData = FILL_HORIZONTAL
					onSelection = [
						updatePreview()
					]
				]
				
				newLabel[
					text = Messages::PART_PADDING + ":"
				]
				paddingLabel = newLabel[
					text = "10px"
				]
				paddingScale = newScale[
					minimum = 0
					maximum = 10
					selection = 0
					pageIncrement = 1
					layoutData = FILL_HORIZONTAL
					onSelection = [
						updatePreview()
					]
				]
				
				
			] // GROUP
			
			
			newComposite[
				layout = newGridLayout[
					numColumns = 2
					marginWidth = 0
					marginHeight = 0
				]
				
				/*
				 * 35: Expose the mru-visible css property
				 * https://github.com/jeeeyul/eclipse-themes/issues/issue/35
				 */
				useMruButton = newCheckbox[
					text = Messages::MAKE_MRU_VISIBLE
				]
				
				newLink[
					text = '''(<a href="http://help.eclipse.org/juno/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Freference%2Fapi%2Forg%2Feclipse%2Fswt%2Fcustom%2FCTabFolder.html&anchor=setMRUVisible(boolean)">«Messages::DETAIL_INFO»</a>)'''
					addListener(SWT::Selection)[
						Program::launch(it.text)
					]
				]
			]
			
			
			newCLabel[
				image = SharedImages::getImage(SharedImages::WARN_TSK)
				text = Messages::APPLY_TO_PREVIEW			
			]
		]
	}

	def void updatePreview() {
		paddingLabel.text = paddingScale.selection + "px"
		cornerRadiusLabel.text = cornerRadiusScale.selection + "px"
		
		var selectedFontName = (fontSelector.selection as IStructuredSelection).firstElement as String
		var fontSize = 9f
		try{
			fontSize = Float::parseFloat(fontSizeField.text.trim)
		}catch(Exception e){
			fontSize = 9f
		}
		if(fontSize > 20f) {
			fontSize = 20f;
		} else if(fontSize < 5f) {
			fontSize = 5f;
		}
		
		fontPreview.fontHeight = fontSize
		fontPreview.fontName = selectedFontName
		fontPreview.run()
	}

	override load(IPreferenceStore store) {
		fontSelector.selection = new StructuredSelection(store.getString(CHROME_PART_FONT_NAME))
		fontSizeField.text = Float::toString(store.getFloat(CHROME_PART_FONT_SIZE))
		cornerRadiusScale.selection = store.getInt(CHROME_PART_STACK_CORNER_RADIUS)
		paddingScale.selection = store.getInt(CHROME_PART_STACK_PADDING)
		useMruButton.selection = store.getBoolean(CHROME_PART_STACK_USE_MRU)
		
		previewLayout()
		updatePreview()
	}

	override save(IPreferenceStore store) {
		var selectedFontName = (fontSelector.selection as IStructuredSelection).firstElement as String
		if(selectedFontName == null || selectedFontName.trim.empty) {
			selectedFontName = display.systemFont.fontData.get(0).getName()
		}
		store.setValue(CHROME_PART_FONT_NAME, selectedFontName)
		var fontSize = 9f
		try{
			fontSize = Float::parseFloat(fontSizeField.text.trim)
		}catch(Exception e){
			fontSize = 9f
		}
		if(fontSize > 20f) {
			fontSize = 20f;
		} else
			if(fontSize < 5f) {
				fontSize = 5f;
			}
		store.setValue(CHROME_PART_FONT_SIZE, fontSize)
		store.setValue(CHROME_PART_STACK_PADDING, paddingScale.selection)
		store.setValue(CHROME_PART_STACK_CORNER_RADIUS, cornerRadiusScale.selection)
		store.setValue(CHROME_PART_STACK_USE_MRU, useMruButton.selection)
		
		previewLayout()		
	}

	def private void previewLayout(){
		var dirty = false;
		
		var renderer = tabFolder.renderer as ChromeTabRendering


		if(renderer.cornerRadius != cornerRadiusScale.selection){
			renderer.cornerRadius = cornerRadiusScale.selection
			dirty = true	
		}
		
		var pad = paddingScale.selection
		
		// top, right, bottom, left
		var oldPaddings = renderer.padding 
		
		if(oldPaddings.x != pad || oldPaddings.y != pad + 5 || oldPaddings.width != pad + 7 || oldPaddings.height != pad + 5){
			// left, right, top, bottom
			renderer.setPadding(pad + 5, pad + 5, pad, pad + 7)
			dirty = true
		}
		
		if(dirty){
			tabFolder.getParent().layout(true, true);	
		}
	}

	override setToDefault(IPreferenceStore store) {
		fontSelector.selection = new StructuredSelection(store.getDefaultString(CHROME_PART_FONT_NAME))
		fontSizeField.text = Float::toString(store.getDefaultFloat(CHROME_PART_FONT_SIZE))
		cornerRadiusScale.selection = store.getDefaultInt(CHROME_PART_STACK_CORNER_RADIUS)
		paddingScale.selection = store.getDefaultInt(CHROME_PART_STACK_PADDING)
		useMruButton.selection = store.getDefaultBoolean(CHROME_PART_STACK_USE_MRU)
		updatePreview()
		previewLayout()		
	}

	override dispose() {
		fontPreview.dispose()
	}
}