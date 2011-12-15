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
{
	abc: 10,
	def: "hello",
	ghi: abc[10],
	jkl: function(a, b, c) {}
}|,|
{
	abc: 10,
	def: "hello",
	ghi: abc[10],
	jkl: function(a, b, c) {}
}|,|
{
	abc: 10,
	def: "hello",
	ghi: abc[10],
	jkl: function(a, b, c) {}
}
|)
