function abc() {
	return def();
}

function def() {
	return abc{}
}

abc();