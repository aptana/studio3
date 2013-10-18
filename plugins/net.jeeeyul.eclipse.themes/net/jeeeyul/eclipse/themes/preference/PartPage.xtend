package net.jeeeyul.eclipse.themes.preference

import net.jeeeyul.eclipse.themes.SharedImages
import net.jeeeyul.eclipse.themes.ui.SWTExtensions
import org.eclipse.jface.preference.IPreferenceStore
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Composite
import net.jeeeyul.eclipse.themes.Messages

class PartPage extends ChromePage {
	extension SWTExtensions = SWTExtensions::INSTANCE
	
	CommonPartPage commonPage
	StateBasedPartPage activePage
	StateBasedPartPage inactivePage
	
	new(){
		super(Messages::PART, SharedImages::PART);
		
		commonPage = new CommonPartPage
		activePage = new StateBasedPartPage(Messages::ACTIVE, true)
		inactivePage = new StateBasedPartPage(Messages::INACTIVE, false);
	}

	override create(Composite parent) {
		parent.layout = newGridLayout
		parent.newCTabFolder(SWT::BOTTOM)[
			layoutData = FILL_BOTH
			
			marginWidth = 0
			marginHeight = 0
			
			newCTabItem[
				text = Messages::COMMON
				image = SharedImages::getImage(SharedImages::LAYOUT)
				control = it.parent.newComposite[
					commonPage.parentPage = parentPage
					commonPage.tabFolder = tabFolder
					commonPage.create(it)
				]
				
				it.parent.selection = it
			]
			
			newCTabItem[
				text = Messages::ACTIVE
				image = SharedImages::getImage(SharedImages::ACTIVE_PART)
				control = it.parent.newComposite[
					activePage.parentPage = parentPage
					activePage.tabFolder = tabFolder
					activePage.create(it)
				]
			]
			
			newCTabItem[
				text = Messages::INACTIVE
				image = SharedImages::getImage(SharedImages::PART)
				control = it.parent.newComposite[
					inactivePage.parentPage = parentPage
					inactivePage.tabFolder = tabFolder
					inactivePage.create(it)
				]
			]
		]
	}
	
	override load(IPreferenceStore preferenceStore) {
		commonPage.load(preferenceStore)
		activePage.load(preferenceStore)
		inactivePage.load(preferenceStore)
	}
	
	override save(IPreferenceStore preferenceStore) {
		commonPage.save(preferenceStore)
		activePage.save(preferenceStore)
		inactivePage.save(preferenceStore)
	}
	
	override setToDefault(IPreferenceStore preferenceStore) {
		commonPage.setToDefault(preferenceStore)
		activePage.setToDefault(preferenceStore)
		inactivePage.setToDefault(preferenceStore)
	}
}