==PREFS==
js.formatter.brace.position.function.declaration=same.line
js.formatter.brace.position.switch.block=same.line
js.formatter.brace.position.case.block=same.line
js.formatter.brace.position.blocks=same.line
js.formatter.line.preserve=1
js.formatter.line.after.function.declaration.expression=0
js.formatter.line.after.function.declaration=1
js.formatter.newline.before.dowhile=false
js.formatter.newline.before.if.in.elseif=false
js.formatter.newline.before.else=false
js.formatter.newline.before.finally=false
js.formatter.newline.before.catch=false
js.formatter.indent.group.body=true
js.formatter.indent.case.body=true
js.formatter.indent.switch.body=true
js.formatter.indent.function.body=true
js.formatter.indent.blocks=true
js.formatter.wrap.comments.length=80
js.formatter.wrap.comments=false
js.formatter.formatter.indentation.size=4
js.formatter.formatter.tabulation.size=4
js.formatter.formatter.tabulation.char=space
==CONTENT==
function test(){
    function test2(){
        //noformat
			var i;
		//format
   			 }
		}
==FORMATTED==
function test() {
    function test2() {
        //noformat
        var i;
        //format
    }

}
