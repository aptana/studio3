// General purpose functions. 

function hide_element(elementId) {
	element = document.getElementById(elementId);
	if(element != null) {
		currentClass = element.className;
		if(currentClass =='visible_tooltip') {
			element.className = 'hidden_tooltip';
		}
	}
}

function show_element(elementId) {
	element = document.getElementById(elementId);
	if(element != null) {
		currentClass = element.className;
		if(currentClass == 'hidden_tooltip') {
			element.className = 'visible_tooltip';
		}
	}
}


