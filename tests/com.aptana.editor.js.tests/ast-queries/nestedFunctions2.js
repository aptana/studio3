var global1, global2;

function functionA(functionAParam1, functionAParam2) {
	var functionALocal;
	
	function functionB(functionBParam1, functionBParam2) {
		var functionBLocal;
		
		function functionC(functionCParam1, functionCParam2) {
			var functionCLocal;
			
			${cursor}
		}
		${cursor}
	}
	${cursor}
	function functionB2(functionB2Param) {
		var functionB2Local;
		
		${cursor}
	}
	${cursor}
}
${cursor}