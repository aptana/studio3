/**
 * @param {String} a
 *             This is the first parameter
 * @param {Object} b
 *             The second parameter
 * @param {Number} c
 *             Last one
 */
function testing(a, b, c) {}

testing(|
	abc(1, "two", { abc: 3, def: function(a, b, c) {} })|,|
	def(4, "five", { def: 6, ghi: function(a, b, c) {} })|,|
	ghi(7, "eight", { ghi: 9, jkl: function(a, b, c) {} })
|)
