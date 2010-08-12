function abc() {
	return def();
}

/**
 * @return {Number}
 */
function def() {
	return abc{}
}

abc();