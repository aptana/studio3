package net.jeeeyul.eclipse.themes.userpreset

import java.util.List
import net.jeeeyul.eclipse.themes.ui.SWTExtensions
import org.eclipse.jface.viewers.IStructuredSelection
import org.eclipse.jface.viewers.SelectionChangedEvent
import org.eclipse.jface.viewers.TableViewer
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Composite
import org.eclipse.jface.dialogs.InputDialog
import org.eclipse.jface.dialogs.IDialogConstants
import net.jeeeyul.eclipse.themes.Messages
import net.jeeeyul.eclipse.themes.ui.CollectionContentProvider

class UserPresetEditor {
	extension SWTExtensions = SWTExtensions::INSTANCE
	TableViewer viewer
	Button renameButton
	Button removeButton
	Button upButton
	Button downButton
	
	List<UserPreset> model
	
	new(Composite parent){
		create(parent)
	}
	
	def private void create(Composite container){
		container.newComposite[
			layoutData = FILL_BOTH
			layout = newGridLayout[
				numColumns = 2
			]
			
			viewer = new TableViewer(it, SWT::BORDER || SWT::MULTI)
			viewer.control =>[
				layoutData = FILL_BOTH[
					verticalSpan = 4
					widthHint = 200
					heightHint = 300
				]
			]
			
			
			viewer.contentProvider = new CollectionContentProvider()
			viewer.labelProvider = new UserPresetLabelProvider()
			if(model != null){
				viewer.input = model				
			}
			
			viewer.addSelectionChangedListener[
				handleSelectionChange(it)
			]
			
			removeButton = newPushButton[
				text = Messages::REMOVE
				layoutData = newGridData[
					verticalAlignment = SWT::TOP
					horizontalAlignment = SWT::FILL
				]
				onClick = [
					removeSelected()
				]
			]
			
			renameButton = newPushButton[
				text = Messages::RENAME
				layoutData = newGridData[
					verticalAlignment = SWT::TOP
					horizontalAlignment = SWT::FILL
				]
				onClick = [
					renameSelected()
				]
			]
			
			upButton = newPushButton[
				text = Messages::UP
				layoutData = newGridData[
					verticalAlignment = SWT::TOP
					horizontalAlignment = SWT::FILL
				]
				onClick = [
					moveUp()
				]
			]
			
			downButton = newPushButton[
				text = Messages::DOWN
				layoutData = newGridData[
					verticalAlignment = SWT::TOP
					horizontalAlignment = SWT::FILL
				]
				onClick = [
					moveDown()
				]
			]
			
			updateButtons()
		]
	}
	
	def void setModel(List<UserPreset> model){
		this.model = model
		if(viewer != null){
			viewer.setInput(model)
		}
	}
	
	def List<UserPreset> getModel(){
		return model
	}
	
	def private void moveDown() {
		var target = selection.head
		var index = model.indexOf(target)
		if(index < model.size - 1){
			model.swap(index, index + 1)
		}
		viewer.refresh()
	}

	def private void moveUp() {
		var target = selection.head
		var index = model.indexOf(target)
		if(index > 0){
			model.swap(index, index - 1)
		}
		viewer.refresh()
	}
	
	def private void renameSelected() { 
		var dialog = new InputDialog(viewer.control.shell, "Jeeeyul's Eclipse Themes", Messages::ENTER_PRESET_NAME + ":", Messages::MY_PRESET, null);
		
		if (dialog.open() == IDialogConstants::OK_ID) {
			selection.head.name = dialog.value
		}
		
		viewer.refresh()
	}

	def private void removeSelected() {
		model.removeAll(selection)
		viewer.refresh()
	}

	
	def private void handleSelectionChange(SelectionChangedEvent event) {
		updateButtons()
	}
	
	def private updateButtons() {
		upButton.enabled = selection.size == 1
		downButton.enabled = selection.size == 1
		renameButton.enabled = selection.size == 1
		removeButton.enabled = selection.size > 0
	}

	def private getSelection(){
		var selection = viewer.selection as IStructuredSelection
		selection.toArray.map[it as UserPreset].toList
	}
	
	def private <T> void swap(List<T> list, int index1, int index2){
		var t1 = list.get(index1)
		var t2 = list.get(index2)
		list.set(index1, t2)
		list.set(index2, t1)
	}
}