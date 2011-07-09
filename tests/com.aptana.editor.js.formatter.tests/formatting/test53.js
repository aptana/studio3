==PREFS==
js.formatter.spaces.after.commas=1
js.formatter.spaces.after.assignment.operator=1
js.formatter.spaces.after.arithmetic.operator=1
js.formatter.spaces.after.conditional.operator=1
js.formatter.indent.case.body=true
js.formatter.indent.switch.body=true
js.formatter.spaces.before.for.semicolon.operator=0
js.formatter.spaces.before.parentheses=0
js.formatter.spaces.after.semicolon.operator=1
js.formatter.spaces.before.postfix.operator=0
js.formatter.spaces.after.postfix.operator=0
js.formatter.spaces.before.relational.operator=1
js.formatter.spaces.before.case.colon.operator=1
js.formatter.spaces.before.unary.operator=0
js.formatter.wrap.comments=false
js.formatter.indent.group.body=true
js.formatter.line.after.function.declaration=1
js.formatter.spaces.after.concatenation.operator=1
js.formatter.wrap.comments.length=80
js.formatter.spaces.before.conditional.operator=1
js.formatter.formatter.tabulation.size=4
js.formatter.spaces.after.relational.operator=1
js.formatter.spaces.after.case.colon.operator=1
js.formatter.spaces.before.prefix.operator=0
js.formatter.indent.blocks=true
js.formatter.spaces.before.concatenation.operator=1
js.formatter.line.preserve=1
js.formatter.brace.position.case.block=same.line
js.formatter.brace.position.switch.block=same.line
js.formatter.formatter.tabulation.char=editor
js.formatter.indent.function.body=true
js.formatter.line.after.function.declaration.expression=0
js.formatter.formatter.indentation.size=4
js.formatter.newline.before.name.value.pairs=true
js.formatter.spaces.after.parentheses=0
js.formatter.spaces.after.for.semicolon.operator=1
js.formatter.newline.before.if.in.elseif=false
js.formatter.spaces.before.key.value.operator=1
js.formatter.spaces.before.commas=0
js.formatter.spaces.after.unary.operator=0
js.formatter.spaces.before.arithmetic.operator=1
js.formatter.spaces.before.assignment.operator=1
js.formatter.newline.before.dowhile=false
js.formatter.newline.before.else=false
js.formatter.spaces.before.semicolon.operator=0
js.formatter.newline.before.finally=false
js.formatter.newline.before.catch=false
js.formatter.spaces.after.prefix.operator=0
js.formatter.brace.position.function.declaration=same.line
js.formatter.spaces.after.key.value.operator=1
js.formatter.brace.position.blocks=same.line
==CONTENT==
// Dojo configuration and Variable Initialization
// Put this code in index.php before the line where you include the javascript for dojo
// djConfig = { isDebug: true };
dojo.require("dojo.io.*");
dojo.require("dojo.io.IframeIO");
ctr = 0;
function upload_file_submit(){
    var bindArgs = {
        formNode: document.getElementById("upload_file"), //form's id
        mimetype: "text/plain", //Enter file type info here
        content: {
            increment: ctr++,
            name: "select_file", //file name in the form
            post_field: "" // add more fields here .. field will be accessible by $_POST["post_field"]
        },
        handler: function(type, data, evt){
            //handle successful response here
            if (type == "error") 
                alert("Error occurred.");
            else {
                //getting error message from PHP's file upload script
                res = dojo.byId("dojoIoIframe").contentWindow.document.getElementById("output").innerHTML;
                //Incase of an error, display the error message
                if (res != "true") 
                    alert(res);
                else 
                    alert("File uploaded successfully.");
            }
        }
    };
    var request = dojo.io.bind(bindArgs);
}
==FORMATTED==
// Dojo configuration and Variable Initialization
// Put this code in index.php before the line where you include the javascript for dojo
// djConfig = { isDebug: true };
dojo.require("dojo.io.*");
dojo.require("dojo.io.IframeIO");
ctr = 0;
function upload_file_submit() {
    var bindArgs = {
        formNode : document.getElementById("upload_file"), //form's id
        mimetype : "text/plain", //Enter file type info here
        content : {
            increment : ctr++,
            name : "select_file", //file name in the form
            post_field : "" // add more fields here .. field will be accessible by $_POST["post_field"]
        },
        handler : function(type, data, evt) {
            //handle successful response here
            if(type == "error")
                alert("Error occurred.");
            else {
                //getting error message from PHP's file upload script
                res = dojo.byId("dojoIoIframe").contentWindow.document.getElementById("output").innerHTML;
                //Incase of an error, display the error message
                if(res != "true")
                    alert(res);
                else
                    alert("File uploaded successfully.");
            }
        }
    };
    var request = dojo.io.bind(bindArgs);
}