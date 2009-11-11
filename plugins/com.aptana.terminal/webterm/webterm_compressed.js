/*
 *
 * This program is Copyright (C) 2009 Aptana, Inc. All Rights Reserved
 *
 */
function clamp(_1,_2,_3){
var _4=_1;
if(isNumber(_1)&&isNumber(_2)&&isNumber(_3)){
_4=Math.min(_3,Math.max(_2,_1));
}
return _4;
};
function createXHR(){
var _5;
if(window.XMLHttpRequest){
_5=new XMLHttpRequest();
}else{
_5=new ActiveXObject("Microsoft.XMLHTTP");
}
return _5;
};
function isBoolean(b){
return b!==null&&b!==undefined&&b.constructor===Boolean;
};
function isCharacter(ch){
return ch!==null&&ch!==undefined&&ch.constructor===String&&ch.length>0;
};
function isDefined(o){
return o!==null&&o!==undefined;
};
function isNumber(n){
return n!==null&&n!==undefined&&n.constructor===Number;
};
function isString(s){
return s!==null&&s!==undefined&&s.constructor===String;
};
function protectedClone(_6){
var f=function(){
};
f.prototype=_6;
var _7=new f();
_7.$parent=_6;
return _7;
};
Attribute.DEFAULT_BACKGROUND="b";
Attribute.DEFAULT_FOREGROUND="f";
function Attribute(){
this.reset();
};
Attribute.prototype.copy=function(){
var _8=new Attribute();
_8.foreground=this.foreground;
_8.background=this.background;
_8.bold=this.bold;
_8.italic=this.italic;
_8.underline=this.underline;
_8.inverse=this.inverse;
_8.strikethrough=this.strikethrough;
_8.blink=this.blink;
return _8;
};
Attribute.prototype.equals=function(_9){
var _a=false;
if(_9 instanceof Attribute){
_a=this===_9||(this.foreground==_9.foreground&&this.background==_9.background&&this.bold==_9.bold&&this.italic==_9.italic&&this.underline==_9.underline&&this.inverse==_9.inverse&&this.strikethrough==_9.strikethrough&&this.blink==_9.blink);
}
return _a;
};
Attribute.prototype.getStartingHTML=function(){
var _b=[];
var _c=(isNumber(this.background))?this.background:Attribute.DEFAULT_BACKGROUND;
var _d=(isNumber(this.foreground))?this.foreground:Attribute.DEFAULT_FOREGROUND;
if(this.inverse){
_b.push("f"+_c);
_b.push("b"+_d);
}else{
_b.push("f"+_d);
_b.push("b"+_c);
}
if(this.bold){
_b.push("b");
}
if(this.italic){
_b.push("i");
}
if(this.underline){
_b.push("u");
}else{
if(this.strikethrough){
_b.push("lt");
}else{
if(this.blink){
_b.push("bl");
}
}
}
return "<span class=\""+_b.join(" ")+"\">";
};
Attribute.prototype.getEndingHTML=function(){
return "</span>";
};
Attribute.prototype.reset=function(){
this.resetBackground();
this.resetForeground();
this.bold=false;
this.italic=false;
this.underline=false;
this.inverse=false;
this.strikethrough=false;
this.blink=false;
};
Attribute.prototype.resetBackground=function(){
this.background=Attribute.DEFAULT_BACKGROUND;
};
Attribute.prototype.resetForeground=function(){
this.foreground=Attribute.DEFAULT_FOREGROUND;
};
Line.DEFAULT_WIDTH=80;
Line.MIN_WIDTH=20;
Line.MAX_WIDTH=512;
function Line(_e){
if(isNumber(_e)){
_e=clamp(_e,Line.MIN_WIDTH,Line.MAX_WIDTH);
}else{
_e=Line.DEFAULT_WIDTH;
}
this._chars=new Array(_e);
this._attributes=new Array(_e);
this.clear();
};
Line.prototype.clear=function(ch){
ch=(isCharacter(ch))?ch.charAt(0):" ";
for(var i=0;i<this._chars.length;i++){
this._chars[i]=ch;
this._attributes[i]=new Attribute();
}
};
Line.prototype.clearLeft=function(_f){
if(isNumber(_f)&&0<=_f&&_f<this._chars.length){
for(var i=0;i<=_f;i++){
this._chars[i]=" ";
this._attributes[i]=new Attribute();
}
}
};
Line.prototype.clearRight=function(_10){
if(isNumber(_10)&&0<=_10&&_10<this._chars.length){
for(var i=_10;i<this._chars.length;i++){
this._chars[i]=" ";
this._attributes[i]=new Attribute();
}
}
};
Line.prototype.deleteCharacter=function(_11,_12){
if(isNumber(_11)){
var _13=this._chars.length;
_12=(isNumber(_12))?_12:1;
if(_12>0&&0<=_11&&_11<_13){
if(_11+_12>_13){
_12=_13-_11;
}
this._chars.splice(_11,_12);
this._attributes.splice(_11,_12);
for(var i=0;i<_12;i++){
this._chars.push(" ");
this._attributes.push(new Attribute());
}
}
}
};
Line.prototype.getHTMLInfo=function(_14,_15){
var _16=[];
for(var i=0;i<this._chars.length;i++){
var ch=this._chars[i];
var _17=this._attributes[i];
if(_17&&_17.equals(_14)==false){
if(_14!==null){
_16.push(_14.getEndingHTML());
}
_16.push(_17.getStartingHTML());
_14=_17;
}
if(i===_15){
_16.push("<span class=\"cursor\">");
}
switch(ch){
case "&":
_16.push("&amp;");
break;
case "<":
_16.push("&lt;");
break;
case ">":
_16.push("&gt;");
break;
case " ":
_16.push("&nbsp;");
break;
default:
_16.push(ch);
break;
}
if(i===_15){
_16.push("</span>");
}
}
return {html:_16.join(""),attribute:_14};
};
Line.prototype.getWidth=function(){
return this._chars.length;
};
Line.prototype.insertCharacter=function(ch,_18,_19){
if(isCharacter(ch)&&isNumber(_18)){
var _1a=this._chars.length;
_19=(isNumber(_19))?_19:1;
if(_19>0&&0<=_18&&_18<_1a){
ch=ch.charAt(0);
if(_18+_19>_1a){
_19=_1a-_18;
}
this._chars.splice(_1a-_19,_19);
this._attributes.splice(_1a-_19,_19);
var _1b=new Array(_19);
var _1c=new Array(_19);
for(var i=0;i<_19;i++){
this._chars.splice(_18+i,0,ch);
this._attributes.splice(_18+i,0,new Attribute());
}
}
}
};
Line.prototype.putCharacter=function(ch,_1d,_1e){
if(isCharacter(ch)&&isDefined(_1d)&&_1d.constructor==Attribute&&isNumber(_1e)){
if(0<=_1e&&_1e<this._chars.length){
this._chars[_1e]=ch.charAt(0);
this._attributes[_1e]=_1d;
}
}
};
Line.prototype.resize=function(_1f){
if(isNumber(_1f)){
var _20=this._chars.length;
if(Line.MIN_WIDTH<=_1f&&_1f<=Line.MAX_WIDTH&&_20!=_1f){
this._chars.length=_1f;
if(_1f>_20){
for(var i=_20;i<_1f;i++){
this._chars[i]=" ";
this._attributes[i]=new Attribute();
}
}
}
}
};
Line.prototype.toString=function(){
return this._chars.join("");
};
KeyHandler.BACKSPACE="";
KeyHandler.DELETE="[3~";
KeyHandler.ESCAPE="";
KeyHandler.F1="[[A";
KeyHandler.F2="[[B";
KeyHandler.F3="[[C";
KeyHandler.F4="[[D";
KeyHandler.F5="[[E";
KeyHandler.F6="[17~";
KeyHandler.F7="[18~";
KeyHandler.F8="[19~";
KeyHandler.F9="[20~";
KeyHandler.F10="[21~";
KeyHandler.F11="[23~";
KeyHandler.F12="[24~";
KeyHandler.F13="[25~";
KeyHandler.F14="[26~";
KeyHandler.F15="[28~";
KeyHandler.F16="[29~";
KeyHandler.F17="[31~";
KeyHandler.F18="[32~";
KeyHandler.F19="[33~";
KeyHandler.F20="[34~";
KeyHandler.INSERT="[2~";
KeyHandler.TAB="\t";
KeyHandler.APP_UP="OA";
KeyHandler.APP_DOWN="OB";
KeyHandler.APP_RIGHT="OC";
KeyHandler.APP_LEFT="OD";
KeyHandler.APP_HOME="OH";
KeyHandler.APP_END="OF";
KeyHandler.UP="[A";
KeyHandler.DOWN="[B";
KeyHandler.RIGHT="[C";
KeyHandler.LEFT="[D";
KeyHandler.HOME="[H";
KeyHandler.END="[F";
KeyHandler.PAGE_UP="[5~";
KeyHandler.PAGE_DOWN="[6~";
function KeyHandler(){
var _21=this;
this._queue=[];
this._applicationKeys=false;
document.onkeypress=function(e){
return _21.processKeyPress(e);
};
document.onkeydown=function(e){
return _21.processKeyDown(e);
};
};
KeyHandler.prototype.addKeys=function(_22){
this._queue.push(_22);
if(isDefined(this.callback)){
this.callback(true);
}
};
KeyHandler.prototype.dequeueAll=function(){
var _23=this._queue.join("");
this._queue.length=0;
return _23;
};
KeyHandler.prototype.hasContent=function(){
return this._queue.length>0;
};
KeyHandler.prototype.processKeyDown=function(e){
if(!e){
e=window.event;
}
var _24=e.keyCode;
var _25=null;
var _26=this._applicationKeys;
switch(_24){
case 8:
_25=KeyHandler.BACKSPACE;
break;
case 9:
_25=KeyHandler.TAB;
break;
case 27:
_25=KeyHandler.ESCAPE;
break;
case 33:
_25=KeyHandler.PAGE_UP;
break;
case 34:
_25=KeyHandler.PAGE_DOWN;
break;
case 35:
_25=(_26)?KeyHandler.APP_END:KeyHandler.END;
break;
case 36:
_25=(_26)?KeyHandler.APP_HOME:KeyHandler.HOME;
break;
case 37:
_25=(_26)?KeyHandler.APP_LEFT:KeyHandler.LEFT;
break;
case 38:
_25=(_26)?KeyHandler.APP_UP:KeyHandler.UP;
break;
case 39:
_25=(_26)?KeyHandler.APP_RIGHT:KeyHandler.RIGHT;
break;
case 40:
_25=(_26)?KeyHandler.APP_DOWN:KeyHandler.DOWN;
break;
case 45:
_25=KeyHandler.INSERT;
break;
case 46:
_25=KeyHandler.DELETE;
break;
case 112:
_25=e.shiftKey?KeyHandler.F13:KeyHandler.F1;
break;
case 113:
_25=e.shiftKey?KeyHandler.F14:KeyHandler.F2;
break;
case 114:
_25=e.shiftKey?KeyHandler.F15:KeyHandler.F3;
break;
case 115:
_25=e.shiftKey?KeyHandler.F16:KeyHandler.F4;
break;
case 116:
_25=e.shiftKey?KeyHandler.F17:KeyHandler.F5;
break;
case 117:
_25=e.shiftKey?KeyHandler.F18:KeyHandler.F6;
break;
case 118:
_25=e.shiftKey?KeyHandler.F19:KeyHandler.F7;
break;
case 119:
_25=e.shiftKey?KeyHandler.F20:KeyHandler.F8;
break;
case 120:
_25=KeyHandler.F9;
break;
case 121:
_25=KeyHandler.F10;
break;
case 122:
_25=KeyHandler.F11;
break;
case 123:
_25=KeyHandler.F12;
break;
default:
if(!e.ctrlKey||(e.ctrlKey&&e.altKey)||(e.keyCode==17)){
}else{
if(e.shiftKey){
switch(_24){
case 50:
_25=String.fromCharCode(0);
break;
case 54:
_25=String.fromCharCode(30);
break;
case 94:
_25=String.fromCharCode(30);
break;
case 109:
_25=String.fromCharCode(31);
break;
default:
break;
}
}else{
switch(_24){
case 32:
_25=String.fromCharCode(0);
break;
case 190:
_25=String.fromCharCode(30);
break;
case 219:
_25=String.fromCharCode(27);
break;
case 220:
_25=String.fromCharCode(28);
break;
case 221:
_25=String.fromCharCode(29);
break;
default:
if(65<=_24&&_24<=90){
_25=String.fromCharCode(_24-64);
}
break;
}
}
}
break;
}
if(_25!==null){
this.addKeys(_25);
return this.stopEvent(e);
}else{
return this.suppressEvent(e);
}
};
KeyHandler.prototype.processKeyPress=function(e){
if(!e){
e=window.event;
}
if(!e||e.metaKey){
return true;
}
if((e.ctrlKey&&!e.altKey)||(e.which==0)||(e.keyCode==8)||(e.keyCode==16)){
}else{
var _27;
if(e.keyCode){
_27=e.keyCode;
}
if(e.which){
_27=e.which;
}
if(e.altKey&&!e.ctrlKey){
this.addKeys(KeyHandler.ESCAPE);
}
this.addKeys(String.fromCharCode(_27));
}
return this.stopEvent(e);
};
KeyHandler.prototype.setApplicationKeys=function(_28){
if(isBoolean(_28)){
this._applicationKeys=_28;
}
};
KeyHandler.prototype.stopEvent=function(e){
if(e){
e.cancelBubble=true;
if(e.stopPropagtion){
e.stopPropagation();
}
if(e.preventDefault){
e.preventDefault();
}
}
return false;
};
KeyHandler.prototype.suppressEvent=function(e){
if(e){
e.cancelBubble=true;
if(e.stopPropagtion){
e.stopPropagation();
}
}
return true;
};
var XTermTables={format:"rle",version:1,actions:[["<error>"],["ANSI",2,0],["ANSI_SYS"],["APC"],["APP_CTRL",2,2],["BEL"],["BS"],["CBT",2,1],["CHA",2,1],["CHT",2,1],["CNL",2,1],["CPL",2,1],["CR"],["CSI"],["CUB",2,1],["CUD",2,1],["CUF",2,1],["CUP",2,1],["CURSOR_LOWER_LEFT"],["CUU",2,1],["DA1",2,1],["DA2",3,1],["DCH",2,1],["DCS"],["DECALN"],["DECCARA",2,2],["DECCRA",2,2],["DECDHL_BH"],["DECDHL_TH"],["DECDWL"],["DECEFR",2,2],["DECELR",2,2],["DECERA",2,2],["DECFRA",2,2],["DECID"],["DECPAM"],["DECPNM"],["DECRARA",2,2],["DECRC"],["DECREQTPARM_OR_DECSACE",2,1],["DECRQLP",2,2],["DECRQSS",4,2],["DECRST",3,1],["DECSC"],["DECSCA",2,2],["DECSCL",2,2],["DECSED",3,1],["DECSEL",3,1],["DECSERA",2,2],["DECSET",3,1],["DECSLE",2,2],["DECSTBM",2,1],["DECSTR"],["DECSWL"],["DECUDK",2,2],["DEFAULT_CHARSET"],["DL",2,1],["DSR",2,1],["DSR2",3,1],["ECH",2,1],["ED",2,1],["EL",2,1],["ENQ"],["EPA"],["FF"],["G0_CHARSET",2,0],["G1_CHARSET",2,0],["G2_CHARSET",2,0],["G3_CHARSET",2,0],["HPA",2,1],["HTS"],["HVP",2,1],["ICH",2,1],["IL",2,1],["IND"],["LF"],["LS1R"],["LS2"],["LS2R"],["LS3"],["LS3R"],["MC",2,1],["MC2",3,1],["MEM_LOCK"],["MEM_UNLOCK"],["MOUSE_TRACKING",2,1],["NEL"],["OSC"],["PM"],["PRI_MSG",2,2],["REP",2,1],["RESTORE_MODE",3,1],["RI"],["RIS"],["RM",2,1],["S7C1T"],["S8C1T"],["SAVE_MODE",3,1],["SD",2,1],["SET_TEXT_PARAMS",2,1],["SET_TEXT_PARAMS2",2,2],["SGR",2,1],["SI"],["SM",2,1],["SO"],["SOS"],["SPA"],["SS2"],["SS3"],["ST"],["SU",2,1],["TAB"],["TBC",2,1],["UTF8_CHARSET"],["VPA",2,1],["VT"]],nodes:[[[-5,256,-1,512,768,1024,1280,1536,1792,2048,2304,2560,-11,2816,-104,3072,3328,-2,3584,-4,3840,4096,4352,4608,-5,4864,5120,5376,-1,5632,5888,6144,6400,6656,6912,-96],-1],[[-256],62],[[-256],5],[[-256],6],[[-256],111],[[-256],75],[[-256],115],[[-256],64],[[-256],12],[[-256],104],[[-256],102],[[-32,7168,-2,7424,-1,7680,-2,7936,8192,8448,8704,-11,8960,9216,-4,9472,9728,-5,9984,10240,10496,-1,10752,-4,11008,11264,11520,11776,-5,12032,12288,12544,-1,12800,13056,13312,13568,13824,14080,-3,14336,-8,14592,14848,15104,15360,-12,15616,15872,16128,-129],-1],[[-256],74],[[-256],86],[[-256],70],[[-256],92],[[-256],107],[[-256],108],[[-256],23],[[-256],106],[[-256],63],[[-256],105],[[-256],34],[[-256],13],[[-256],109],[[-256],87],[[-256],88],[[-256],3],[[16393,-1,16385,-1,16439,16640,16896,16567],-1],[[-51,17152,17408,17664,17920,-1,18176,-199],-1],[[-64,18432,-6,18688,-184],-1],[[18953,-1,18945,-1,19185],-1],[[19209,-1,19201,-1,19441],-1],[[19465,-1,19457,-1,19697],-1],[[19721,-1,19713,-1,19953],-1],[[-256],43],[[-256],38],[[-256],35],[[-256],36],[[-256],74],[[-256],86],[[-256],18],[[-256],70],[[-256],92],[[-256],107],[[-256],108],[[-36,19968,-11,20233,-1,20480,-196],23],[[-256],106],[[-256],63],[[-256],105],[[-256],34],[[-33,20736,20992,-13,21257,-1,21504,-2,21760,22016,22272,22528,22784,23040,23296,23552,23808,24064,24320,24576,24832,25088,25344,25600,-2,25856,-2,26112,26368,-3,26624,-1,26880,-5,27136,-1,27392,27648,27904,-2,28160,28416,28672,-2,28928,29184,29440,-4,29696,-1,29952,-2,30208,-135],13],[[-256],109],[[-48,30473,-1,30720,-196],87],[[31002,31232,31203],88],[[31514,31744,31715],3],[[-256],93],[[-256],83],[[-256],84],[[-256],77],[[-256],79],[[-256],80],[[-256],78],[[-256],76],[[-256],1],[[-256],95],[[-256],96],[[-256],28],[[-256],27],[[-256],53],[[-256],29],[[-256],24],[[-256],55],[[-256],113],[[-256],65],[[-256],66],[[-256],67],[[-256],68],[[-113,32000,-142],-1],[[-48,20233,-1,20480,-196],-1],[[-48,32265,-66,32512,-131],-1],[[-112,32768,-143],-1],[[-113,33024,-142],-1],[[-34,20992,-13,21257,-1,21504,-4,22272,22528,22784,23040,23296,23552,23808,24064,-1,24576,24832,25088,25344,25600,-2,25856,-2,26112,26368,-3,26624,-1,26880,-5,27136,-1,27392,27648,27904,-2,28160,28416,28672,-2,28928,29184,29440,-9,30208,-135],-1],[[-34,33280,-4,33536,-8,33801,-1,34048,-12,34304,-23,34560,-5,34816,-1,28416,28672,-2,28928,29184,-4,35072,-141],-1],[[-48,35337,-41,35584,-156],-1],[[-48,35849,-1,36096,-14,36352,36608,-28,36864,37120,-2,37376,-1,37632,-3,37888,38144,-140],-1],[[-256],72],[[-256],19],[[-256],15],[[-256],16],[[-256],14],[[-256],10],[[-256],11],[[-256],8],[[-256],17],[[-256],9],[[-256],60],[[-256],61],[[-256],73],[[-256],56],[[-256],22],[[-256],110],[[-256],98],[[-256],59],[[-256],7],[[-123,38400,38656,-131],69],[[-256],90],[[-256],20],[[-256],114],[[-256],112],[[-256],103],[[-256],81],[[-256],94],[[-256],101],[[-256],57],[[-256],2],[[-256],2],[[-256],39],[[-48,30473,-1,30720,-196],-1],[[38918,39168,38930,39424,39139],-1],[[31002,31232,31203],-1],[[-92,39680,-163],-1],[[31514,31744,31715],-1],[[-92,39936,-163],-1],[[40218,40448,40419],-1],[[-48,32265,-66,32512,-131],-1],[[40730,40960,40931],-1],[[-256],52],[[-256],44],[[-112,41216,-143],-1],[[-122,41472,-133],-1],[[-34,33280,-4,33536,-8,33801,-1,34048,-12,34304,-23,34560,-5,34816,-1,28416,28672,-2,28928,29184,-4,35072,-141],-1],[[-48,41737,-1,41984,-36,34560,-7,28416,28672,-2,28928,29184,-146],-1],[[-256],17],[[-123,38400,-132],-1],[[-256],71],[[-256],51],[[-48,35337,-41,35584,-156],-1],[[-256],21],[[-48,35849,-1,36096,-14,36352,36608,-28,36864,37120,-2,37376,-1,37632,-3,37888,38144,-140],-1],[[-48,42249,-1,36096,-44,36864,37120,-2,37376,-5,37888,38144,-140],-1],[[-256],46],[[-256],47],[[-256],49],[[-256],82],[[-256],42],[[-256],58],[[-256],91],[[-256],97],[[-256],50],[[-256],40],[[38918,39168,38930,39424,39139],-1],[[42522,42752,42723],99],[[43014,43264,43091,43520,43170],-1],[[-256],89],[[-256],4],[[40218,40448,40419],-1],[[-92,43776,-163],-1],[[40730,40960,40931],-1],[[-92,44032,-163],-1],[[-256],45],[[-256],31],[[-48,41737,-1,41984,-36,34560,-7,28416,28672,-2,28928,29184,-146],-1],[[-36,44288,-2,44544,-8,44809,-1,45056,-36,34560,-7,28416,28672,-2,28928,29184,-146],-1],[[-48,42249,-1,36096,-44,36864,37120,-2,37376,-5,37888,38144,-140],-1],[[42522,42752,42723],-1],[[-92,45312,-163],-1],[[43014,43264,43255],-1],[[-256],99],[[43014,43264,43255],100],[[-256],41],[[-256],54],[[-122,45568,45824,-132],-1],[[-119,46080,-136],-1],[[-36,44288,-2,44544,-8,44809,-1,45056,-36,34560,-7,28416,28672,-2,28928,29184,-146],-1],[[-36,46336,-11,46601,-1,46848,-24,47104,-11,34560,-7,28416,28672,-2,28928,29184,-146],-1],[[-256],100],[[-256],32],[[-256],48],[[-256],30],[[-114,47360,-1,47616,-3,47872,-135],-1],[[-36,46336,-11,46601,-1,46848,-24,47104,-11,34560,-7,28416,28672,-2,28928,29184,-146],-1],[[-48,48137,-1,48384,-36,34560,-7,28416,28672,-2,28928,29184,-146],-1],[[-256],85],[[-256],25],[[-256],37],[[-256],33],[[-48,48137,-1,48384,-36,34560,-7,28416,28672,-2,28928,29184,-146],-1],[[-48,48649,-1,48896,-36,34560,-7,28416,28672,-2,28928,29184,-146],-1],[[-48,48649,-1,48896,-36,34560,-7,28416,28672,-2,28928,29184,-146],-1],[[-36,49152,-11,49417,-1,49664,-36,34560,-7,28416,28672,-2,28928,29184,-146],-1],[[-118,49920,-137],-1],[[-36,49152,-11,49417,-1,49664,-36,34560,-7,28416,28672,-2,28928,29184,-146],-1],[[-48,50185,-1,49664,-36,34560,-7,28416,28672,-2,28928,29184,-146],-1],[[-256],26],[[-48,50185,-1,49664,-36,34560,-7,28416,28672,-2,28928,29184,-146],-1]]};
function XTermHandler(_29){
this._term=_29;
this._insertMode=false;
this._missingCommands={};
};
XTermHandler.prototype.BEL=function(_2a,_2b){
};
XTermHandler.prototype.BS=function(_2c,_2d){
var col=this._term.getColumn()-1;
col=Math.max(0,col);
this._term.setColumn(col);
};
XTermHandler.prototype.CHA=function(_2e,_2f){
var _30=0;
if(_2f.length>0){
_30=_2f-1;
}
this._term.setColumn(_30);
};
XTermHandler.prototype.CR=function(_31,_32){
this._term.setColumn(0);
};
XTermHandler.prototype.CUB=function(_33,_34){
var _35=1;
if(_34.length>0){
_35=_34-0;
if(_35==0){
_35=1;
}
}
var col=this._term.getColumn()-_35;
col=Math.max(0,col);
this._term.setColumn(col);
};
XTermHandler.prototype.CUD=function(_36,_37){
var _38=1;
if(_37.length>0){
_38=_37-0;
if(_38==0){
_38=1;
}
}
var _39=this._term.getRow();
var _3a=this._term.getScrollRegion().bottom;
var _3b;
if(_39<=_3a){
_3b=Math.min(_39+_38,_3a);
}else{
_3b=Math.min(_39+_38,this._term.getHeight()-1);
}
this._term.setRow(_3b);
};
XTermHandler.prototype.CUF=function(_3c,_3d){
var _3e=1;
if(_3d.length>0){
_3e=_3d-0;
if(_3e==0){
_3e=1;
}
}
var col=this._term.getColumn()+_3e;
col=Math.min(col,this._term.getWidth()-1);
this._term.setColumn(col);
};
XTermHandler.prototype.CUP=function(_3f,_40){
var row=0;
var col=0;
var _41=this._term.getHeight();
if(_40.length>0){
var _42=_40.split(/;/);
var row=_42[0]-1;
var col=_42[1]-1;
}
if(row>=_41){
var _43=_41-row;
row=_41-1;
this._term.scrollUp(_43);
}
this._term.setPosition(row,col);
};
XTermHandler.prototype.CUU=function(_44,_45){
var _46=1;
if(_45.length>0){
_46=_45-0;
if(_46==0){
_46=1;
}
}
var _47=this._term.getRow();
var _48=this._term.getScrollRegion().top;
var _49;
if(_48<=_47){
_49=Math.max(_48,_47-_46);
}else{
_49=Math.max(0,_47-_46);
}
this._term.setRow(_49);
};
XTermHandler.prototype.DCH=function(_4a,_4b){
var _4c=_4b-0;
this._term.deleteCharacter(_4c);
};
XTermHandler.prototype.DECALN=function(_4d,_4e){
this._term.clear("E");
};
XTermHandler.prototype.DECRC=function(_4f,_50){
this._term.popPosition();
};
XTermHandler.prototype.DECPAM=function(_51,_52){
this._term.setApplicationKeys(true);
};
XTermHandler.prototype.DECPNM=function(_53,_54){
this._term.setApplicationKeys(false);
};
XTermHandler.prototype.DECRST=function(_55,_56){
var _57=_56-0;
switch(_57){
case 1:
this._term.setApplicationKeys(false);
break;
case 3:
this._term.setWidth(80);
break;
case 25:
this._term.setCursorVisible(false);
break;
case 47:
this._term.popBuffer();
break;
case 1049:
this._term.popPosition();
this._term.popBuffer();
break;
default:
this.genericHandler(_55,_56);
break;
}
};
XTermHandler.prototype.DECSC=function(_58,_59){
this._term.pushPosition();
};
XTermHandler.prototype.DECSET=function(_5a,_5b){
var _5c=_5b-0;
switch(_5c){
case 1:
this._term.setApplicationKeys(true);
break;
case 3:
this._term.setWidth(132);
break;
case 25:
this._term.setCursorVisible(true);
break;
case 47:
this._term.pushBuffer();
break;
case 1049:
this._term.pushPosition();
this._term.pushBuffer();
break;
default:
this.genericHandler(_5a,_5b);
break;
}
};
XTermHandler.prototype.DECSTBM=function(_5d,_5e){
var _5f=_5e.split(/;/);
var top=_5f[0]-1;
var _60=_5f[1]-1;
this._term.setScrollRegion(top,0,_60,this._term.getWidth()-1);
};
XTermHandler.prototype.DL=function(_61,_62){
var _63=1;
if(_62.length>0){
_63=_62-0;
if(_63==0){
_63=1;
}
}
this._term.deleteLine(_63);
};
XTermHandler.prototype.ED=function(_64,_65){
var _66=_65-0;
switch(_66){
case 0:
this._term.clearAfter();
break;
case 1:
this._term.clearBefore();
break;
case 2:
this._term.clear();
break;
default:
this.genericHandler(_64+":"+_65,"");
break;
}
};
XTermHandler.prototype.EL=function(_67,_68){
var _69=_68-0;
switch(_69){
case 0:
this._term.clearRight();
break;
case 1:
this._term.clearLeft();
break;
case 2:
this._term.clearLine();
break;
default:
this.genericHandler(_67+":"+_68,"");
break;
}
};
XTermHandler.prototype.genericHandler=function(_6a,_6b){
if(this._missingCommands.hasOwnProperty(_6a)===false){
this._missingCommands[_6a]=0;
}
this._missingCommands[_6a]++;
};
XTermHandler.prototype.getMissingCommands=function(){
return this._missingCommands;
};
XTermHandler.prototype.HVP=XTermHandler.prototype.CUP;
XTermHandler.prototype.ICH=function(_6c,_6d){
var _6e=_6d-0;
this._term.insertCharacter(" ",_6e);
};
XTermHandler.prototype.IL=function(_6f,_70){
var _71=1;
if(_70.length>0){
_71=_70-0;
if(_71==0){
_71=1;
}
}
this._term.insertLine(_71);
};
XTermHandler.prototype.IND=function(_72,_73){
var _74=this._term.getRow();
var _75=this._term.getScrollRegion().bottom;
var _76=_74+1;
if(_74<=_75){
this._term.setRow(_76);
}else{
this._term.scrollUp(1);
this._term.setRow(_75);
}
};
XTermHandler.prototype.LF=function(_77,_78){
var _79=this._term;
var row=_79.getRow()+1;
var _7a=_79.getScrollRegion().bottom;
if(row>_7a){
_79.scrollUp();
row=_7a;
}
_79.setPosition(row,0);
};
XTermHandler.prototype.NEL=XTermHandler.prototype.LF;
XTermHandler.prototype.processCharacter=function(_7b,_7c){
if(this._insertMode){
this._term.insertCharacter(" ",1);
}
this._term.displayCharacters(_7c);
};
XTermHandler.prototype.RI=function(_7d,_7e){
var _7f=this._term.getRow();
var _80=this._term.getScrollRegion().top;
var _81=_7f-1;
if(_80<=_81){
this._term.setRow(_81);
}else{
this._term.scrollDown(1);
this._term.setRow(_80);
}
};
XTermHandler.prototype.RM=function(_82,_83){
var _84=_83-0;
switch(_84){
case 4:
this._insertMode=false;
break;
case 2:
case 12:
case 20:
default:
this.genericHandler(_82,_83);
break;
}
};
XTermHandler.prototype.SD=function(_85,_86){
var _87=1;
if(_86.length>0){
_87=_86-0;
}
var _88=this._term.getRow();
var _89=this._term.getScrollRegion().top;
var _8a=_88-_87;
if(_89<=_8a){
this._term.setRow(_8a);
}else{
this._term.scrollDown(_87);
this._term.setRow(_89);
}
};
XTermHandler.prototype.SET_TEXT_PARAMS=function(_8b,_8c){
var _8d=_8c.split(/;/);
var _8e=_8d[0]-0;
var _8f=_8d[1];
if(_8e==0){
this._term.setTitle(_8f);
}else{
this.genericHandler(_8b+":"+_8c,"");
}
};
XTermHandler.prototype.SET_TEXT_PARAMS2=XTermHandler.prototype.SET_TEXT_PARAMS;
XTermHandler.prototype.SGR=function(_90,_91){
var _92=this._term.getCurrentAttribute();
var _93=_91.split(/;/);
for(var i=0;i<_93.length;i++){
var _94=_93[i]-0;
if(_94<50){
var _95=Math.floor(_94/10);
var _96=_94%10;
switch(_95){
case 0:
switch(_96){
case 0:
_92.reset();
break;
case 1:
_92.bold=true;
break;
case 3:
_92.italic=true;
break;
case 4:
_92.underline=true;
break;
case 7:
_92.inverse=true;
break;
case 9:
_92.strikethrough=true;
break;
default:
this.genericHandler(_90+":"+_91,"");
break;
}
break;
case 2:
switch(_96){
case 2:
_92.bold=false;
break;
case 3:
_92.italic=false;
break;
case 4:
_92.underline=false;
break;
case 7:
_92.inverse=false;
break;
case 9:
_92.strikethough=false;
break;
default:
this.genericHandler(_90+":"+_91,"");
break;
}
break;
case 3:
switch(_96){
case 0:
case 1:
case 2:
case 3:
case 4:
case 5:
case 6:
case 7:
_92.foreground=_96;
break;
case 9:
_92.resetForeground();
break;
default:
this.genericHandler(_90+":"+_91,"");
break;
}
break;
case 4:
switch(_96){
case 0:
case 1:
case 2:
case 3:
case 4:
case 5:
case 6:
case 7:
_92.background=_96;
break;
case 9:
_92.resetBackground();
break;
default:
this.genericHandler(_90+":"+_91,"");
break;
}
break;
default:
this.genericHandler(_90+":"+_91,"");
break;
}
}else{
this.genericHandler(_90+":"+_91,"");
}
}
this._term.setCurrentAttribute(_92);
};
XTermHandler.prototype.SM=function(_97,_98){
var _99=_98-0;
switch(_99){
case 4:
this._insertMode=true;
break;
case 2:
case 12:
case 20:
default:
this.genericHandler(_97,_98);
break;
}
};
XTermHandler.prototype.SU=function(_9a,_9b){
var _9c=1;
if(_9b.length>0){
_9c=_9b-0;
}
var _9d=this._term.getRow();
var _9e=this._term.getScrollRegion().bottom;
var _9f=_9d+_9c;
if(_9d<=_9e){
this._term.setRow(_9f);
}else{
this._term.scrollUp(_9c);
this._term.setRow(_9e);
}
};
XTermHandler.prototype.TAB=function(_a0,_a1){
var _a2=this._term.getColumn();
var _a3=8-(_a2%8);
this._term.displayCharacters(new Array(_a3+1).join(" "));
};
XTermHandler.prototype.VPA=function(_a4,_a5){
var row=0;
if(_a5.length>0){
row=_a5-1;
}
this._term.setRow(row);
};
XTermHandler.prototype.VT=XTermHandler.prototype.LF;
function TermParser(_a6,_a7){
if(_a6===null||_a6===undefined){
throw new Error("Parsing tables must be defined when creating a new TermParser");
}
this._processTables(_a6);
this._actions=_a6.actions;
this._nodes=_a6.nodes;
this.setHandler(_a7);
};
TermParser.prototype.getHandler=function(){
return this._handler;
};
TermParser.prototype.parse=function(_a8){
var _a9=0;
var _aa=isString(_a8)?_a8.length:0;
while(_a9<_aa){
var _ab=0;
var _ac=this._nodes[_ab][1];
var _ad=(_ac==-1)?-2:_a9;
for(var i=_a9;i<_aa;i++){
var _ae=this._nodes[_ab];
if(_ae){
var _af=_a8.charCodeAt(i);
var _b0=_ae[0][_af];
if(_b0!=-1){
_ab=_b0;
var _b1=this._nodes[_ab][1];
if(_b1!=-1){
_ad=i;
_ac=_b1;
}
}else{
break;
}
}
}
if(_ac==-1){
if(this._handler!=null){
if(this._handler.processCharacter){
this._handler.processCharacter("processCharacter",_a8.charAt(_a9));
}
}
_a9++;
}else{
var _b2=_ad+1;
if(this._handler!=null){
var _b3=this._actions[_ac];
var _b4=_b3[0];
var _b5="";
if(_b3.length>=3&&_b3[1]!=-1&&_b3[2]!=-1){
_b5=_a8.substring(_a9+_b3[1],_b2-_b3[2]);
}
this._handler[_b4](_b4,_b5);
}
_a9=_b2;
}
}
};
TermParser.prototype._processTables=function(_b6){
if(_b6.hasOwnProperty("processed")==false||_b6.processed==false){
switch(_b6.format){
case "expanded":
break;
case "rle":
var mos=new Array(256);
for(var i=0;i<mos.length;i++){
mos[i]=-1;
}
var _b7=_b6.nodes;
for(var i=0;i<_b7.length;i++){
var _b8=_b7[i][0];
var _b9=[];
for(var j=0;j<_b8.length;j++){
var _ba=_b8[j];
if(_ba<0){
_b9=_b9.concat(mos.slice(0,-_ba));
}else{
var _bb=_ba>>8;
var _bc=(_ba&255)+1;
for(var k=0;k<_bc;k++){
_b9.push(_bb);
}
}
}
_b7[i][0]=_b9;
}
break;
default:
break;
}
_b6.processed=true;
}
};
TermParser.prototype.setHandler=function(_bd){
var _be=null;
if(_bd){
var _bf=null;
var _c0=function(_c1,_c2){
};
for(var i=0;i<this._actions.length;i++){
var _c3=this._actions[i];
var _c4=_c3[0];
if(!_bd[_c4]){
if(_be==null){
_be=protectedClone(_bd);
if(!_bd.genericHandler){
_bf=_c0;
}else{
_bf=_bd.genericHandler;
}
}
_be[_c4]=_bf;
}
}
}
if(_be==null){
this._handler=_bd;
}else{
this._handler=_be;
}
};
TermComm.POLLING_INTERVAL_MIN=125;
TermComm.POLLING_INTERVAL_MAX=2000;
TermComm.POLLING_GROWTH_RATE=2;
TermComm.DEFAULT_REQUEST_URL="/stream";
TermComm.DEFAULT_GET_UNIQUE_ID_URL="/id";
TermComm.DEFAULT_CHARACTER_SIZE_URL="/size";
function TermComm(_c5,_c6){
var _c7=this;
this.terminal=_c5;
this.keyHandler=_c5.getKeyHandler();
this.keyHandler.callback=function(){
_c7.update(true);
};
this.minInterval=125;
this.maxInterval=2000;
this.growthRate=2;
this.timeoutInterval=5000;
this.requestURL=TermComm.DEFAULT_REQUEST_URL;
this.getUniqueIdURL=TermComm.DEFAULT_GET_UNIQUE_ID_URL;
this.getCharacterSizeURL=TermComm.DEFAULT_CHARACTER_SIZE_URL;
if(isDefined(_c6)){
if(_c6.hasOwnProperty("minInterval")&&isNumber(_c6.minInterval)){
this.minInterval=_c6.minInterval;
}
if(_c6.hasOwnProperty("maxInterval")&&isNumber(_c6.maxInterval)){
this.maxInterval=_c6.maxInterval;
}
if(_c6.hasOwnProperty("growthRate")&&isNumber(_c6.growthRate)){
this.growthRate=_c6.growthRate;
}
if(_c6.hasOwnProperty("timeoutInterval")&&isNumber(_c6.timeoutInterval)){
this.timeoutInterval=_c6.timeoutInterval;
}
if(_c6.hasOwnProperty("requestURL")&&isString(_c6.requestURL)&&_c6.requestURL.length>0){
this.requestURL=_c6.requestURL;
}
if(_c6.hasOwnProperty("getUniqueIdURL")&&isString(_c6.getUniqueIdURL)&&_c6.getUniqueIdURL.length>0){
this.getUniqueIdURL=_c6.getUniqueIdURL;
}
if(_c6.hasOwnProperty("getCharacterSizeURL")&&isString(_c6.getCharacterSizeURL)&&_c6.getCharacterSizeURL.length>0){
this.getCharacterSizeURL=_c6.getCharacterSizeURL;
}
}
this.pollingInterval=this.minInterval;
this.watchdogID=null;
this.requestID=null;
this.running=false;
this.ie=(window.ActiveXObject)?true:false;
};
TermComm.prototype.getCharacterSize=function(){
var req=createXHR();
req.open("GET",this.getCharacterSizeURL,false);
req.send();
return req.responseText;
};
TermComm.prototype.getUniqueID=function(){
var req=createXHR();
req.open("GET",this.getUniqueIdURL,false);
req.send();
return req.responseText;
};
TermComm.prototype.isRunning=function(){
return this.running;
};
TermComm.prototype.processURL=function(URL){
var _c8=this;
var req=createXHR();
req.open("GET",URL+"?id="+this.terminal.getId(),true);
if(this.ie){
req.setRequestHeader("If-Modified-Since","Sat, 1 Jan 2000 00:00:00 GMT");
}
req.onreadystatechange=function(){
if(req.readyState==4){
if(_c8.watchdogID!==null){
window.clearTimeout(_c8.watchdogID);
_c8.watchdogID=null;
}
var _c9=req.responseText;
if(isString(_c9)&&_c9.length>0){
_c8.terminal.processCharacters(_c9);
_c8.pollingInterval=_c8.minInterval;
}else{
_c8.pollingInterval*=_c8.growthRate;
if(_c8.pollingInterval>_c8.maxInterval){
_c8.pollingInterval=_c8.maxInterval;
}
}
_c8.requestID=window.setTimeout(function(){
_c8.update();
},_c8.pollingInterval);
}
};
this.watchdogID=window.setTimeout(function(){
_c8.timeout();
},this.timeoutInterval);
req.send();
};
TermComm.prototype.sendKeys=function(){
var id=this.terminal.getId();
if(isDefined(this.keyHandler)&&id!==null){
if(this.keyHandler.hasContent()){
var req=createXHR();
req.open("POST",this.requestURL+"?id="+id,false);
req.send(this.keyHandler.dequeueAll());
}
}
};
TermComm.prototype.timeout=function(){
};
TermComm.prototype.toggleRunState=function(){
this.running=!this.running;
if(this.running){
this.update(true);
}
};
TermComm.prototype.update=function(_ca){
if(this.running&&this.terminal.getId()!==null){
if(isBoolean(_ca)){
if(this.requestID!==null){
window.clearTimeout(this.requestID);
this.requestID=null;
}
if(_ca){
this.sendKeys();
this.pollingInterval=this.minInterval;
}
}
this.processURL(this.requestURL);
}
};
Term.DEFAULT_ID="terminal";
Term.DEFAULT_HEIGHT=24;
Term.MIN_HEIGHT=5;
Term.MAX_HEIGHT=512;
function Term(id,_cb,_cc,_cd){
if(isString(id)===false||id.length===0){
id="terminal";
}
this._id=(_cd&&_cd.hasOwnProperty("id"))?_cd.id:null;
this._remainingText="";
this._rootNode=document.getElementById(id);
if(this._rootNode){
this._rootNode.className="webterm";
this._termNode=document.createElement("pre");
this._rootNode.appendChild(this._termNode);
this._width=(isNumber(_cb))?clamp(_cb,Line.MIN_WIDTH,Line.MAX_WIDTH):Line.DEFAULT_WIDTH;
this._height=(isNumber(_cc))?clamp(_cc,Term.MIN_HEIGHT,Term.MAX_HEIGHT):Term.DEFAULT_HEIGHT;
this._title="Aptana WebTerm";
this._row=0;
this._column=0;
this._scrollRegion={top:0,left:0,bottom:this._height-1,right:this._width-1};
this._cursorVisible=true;
this._buffers=[];
this._positions=[];
this._currentAttribute=new Attribute();
this._sendResizeSequence=(_cd&&_cd.hasOwnProperty("sendResizeSequence"))?_cd.sendResizeSequence:true;
var _ce=(_cd&&_cd.hasOwnProperty("handler"))?_cd.handler:new XTermHandler(this);
var _cf=(_cd&&_cd.hasOwnProperty("tables"))?_cd.tables:XTermTables;
var _d0=(_cd&&_cd.hasOwnProperty("parser"))?_cd.parser:new TermParser(_cf,_ce);
var _d1=(_cd&&_cd.hasOwnProperty("keyHandler"))?_cd.keyHandler:new KeyHandler();
this._parser=_d0;
this._keyHandler=_d1;
var _d2=(_cd&&_cd.hasOwnProperty("commHandler"))?_cd.commHandler:new TermComm(this);
var _d3=(_cd&&_cd.hasOwnProperty("autoStart"))?_cd.autoStart:true;
this._commHandler=_d2;
this.createBuffer();
this.refresh();
if(_d3){
this.toggleRunState();
}
}else{
throw new Error("Unable to create a new Term because there is no element named '"+id+"'");
}
};
Term.prototype.clear=function(ch){
for(var i=0;i<this._lines.length;i++){
this._lines[i].clear(ch);
}
this._row=0;
this._column=0;
};
Term.prototype.clearAfter=function(){
this._lines[this._row].clearRight(this._column);
for(var i=this._row+1;i<this._lines.length;i++){
this._lines[i].clear();
}
};
Term.prototype.clearBefore=function(){
this._lines[this._row].clearLeft(this._column);
for(var i=this._row-1;i>=0;i--){
this._lines[i].clear();
}
};
Term.prototype.clearLeft=function(){
this._lines[this._row].clearLeft(this._column);
};
Term.prototype.clearLine=function(){
this._lines[this._row].clear();
};
Term.prototype.clearRight=function(){
this._lines[this._row].clearRight(this._column);
};
Term.prototype.createBuffer=function(){
var _d4=new Array(this._height);
for(var i=0;i<_d4.length;i++){
_d4[i]=new Line(this._width);
}
this._lines=_d4;
};
Term.prototype.deleteCharacter=function(_d5){
this._lines[this._row].deleteCharacter(this._column,_d5);
};
Term.prototype.deleteLine=function(_d6){
_d6=(_d6===undefined)?1:_d6;
if(_d6>0){
var _d7=this._scrollRegion;
if(_d7.left==0&&_d7.right==this._width-1){
if(this._row+_d6>_d7.bottom){
_d6=_d7.bottom-this._row+1;
}
if(_d6==this._height){
this.clear();
}else{
var _d8=this._lines.splice(this._row,_d6);
for(var i=0;i<_d6;i++){
_d8[i].clear();
}
if(_d7.bottom+1==this.height){
this._lines=this._lines.concat(_d8);
}else{
for(var i=0;i<_d6;i++){
this._lines.splice(_d7.bottom-_d6+i+1,0,_d8[i]);
}
}
}
}else{
}
}
};
Term.prototype.displayCharacters=function(_d9){
if(isString(_d9)){
for(var i=0;i<_d9.length;i++){
var ch=_d9.charAt(i);
var _da=this._lines[this._row];
if(/[\x20-\x7F]+/.test(ch)==false){
ch=" ";
}
_da.putCharacter(ch,this._currentAttribute,this._column);
this._column++;
if(this._column>=this._width){
this._column=this._width-1;
}
}
}
};
Term.prototype.getColumn=function(){
return this._column;
};
Term.prototype.getCommunicationHandler=function(){
return this._commHandler;
};
Term.prototype.getCurrentAttribute=function(){
return this._currentAttribute.copy();
};
Term.prototype.getHeight=function(){
return this._height;
};
Term.prototype.getId=function(){
return this._id;
};
Term.prototype.getKeyHandler=function(){
return this._keyHandler;
};
Term.prototype.getParser=function(){
return this._parser;
};
Term.prototype.getRow=function(){
return this._row;
};
Term.prototype.getScrollRegion=function(){
return protectedClone(this._scrollRegion);
};
Term.prototype.getTitle=function(){
return this._title;
};
Term.prototype.getWidth=function(){
return this._width;
};
Term.prototype.insertCharacter=function(ch,_db){
this._lines[this._row].insertCharacter(ch,this._column,_db);
};
Term.prototype.insertLine=function(_dc){
_dc=(_dc===undefined)?1:_dc;
if(_dc>0){
var _dd=this._scrollRegion;
if(_dd.left==0&&_dd.right==this._width-1){
if(this._row+_dc>_dd.bottom){
_dc=_dd.bottom-this._row+1;
}
if(_dc==this._height){
this.clear();
}else{
var _de=this._lines.splice(_dd.bottom-_dc+1,_dc);
for(var i=0;i<_dc;i++){
_de[i].clear();
}
if(_dd.top==0){
this._lines=_de.concat(this._lines);
}else{
for(var i=0;i<_dc;i++){
this._lines.splice(_dd.top+i,0,_de[i]);
}
}
}
}else{
}
}
};
Term.prototype.popBuffer=function(){
if(this._buffers.length>0){
this._lines=this._buffers.pop();
}
};
Term.prototype.popPosition=function(){
if(this._positions.length>0){
var _df=this._positions.pop();
this._row=_df[0];
this._column=_df[1];
}
};
Term.prototype.processCharacters=function(_e0){
if(isString(_e0)&&_e0.length>0){
this._parser.parse(_e0);
this.refresh();
}
};
Term.prototype.pushBuffer=function(){
this._buffers.push(this._lines);
this.createBuffer();
};
Term.prototype.pushPosition=function(){
this._positions.push([this._row,this._column]);
};
Term.prototype.refresh=function(){
var _e1=[];
var _e2=null;
var _e3=this._title+" — "+this._width+"x"+this._height;
var _e4="<div class='title'>"+_e3+"</div>";
for(var row=0;row<this._height;row++){
var _e5=this._lines[row];
var _e6=(this._cursorVisible)?(row==this._row)?this._column:-1:-1;
var _e7=_e5.getHTMLInfo(_e2,_e6);
_e2=_e7.attribute;
_e1.push(_e7.html);
}
if(_e2!=null){
_e1[_e1.length-1]+=_e2.getEndingHTML();
}
this._termNode.innerHTML=_e4+_e1.join("<br />");
};
Term.prototype.scrollDown=function(_e8){
_e8=(_e8===undefined)?1:_e8;
if(_e8>0){
var _e9=this._scrollRegion;
if(_e9.left==0&&_e9.right==this._width-1){
var _ea=_e9.bottom-_e9.top+1;
if(_e8>=_ea){
this.clear();
}else{
var _eb=this._lines.splice(_e9.bottom-_e8+1,_e8);
for(var i=0;i<_e8;i++){
_eb[i].clear();
}
if(_e9.top==0){
this._lines=_eb.concat(this._lines);
}else{
for(var i=0;i<_e8;i++){
this._lines.splice(_e9.top+i,0,_eb[i]);
}
}
}
}else{
}
}
};
Term.prototype.scrollUp=function(_ec){
_ec=(_ec===undefined)?1:_ec;
if(_ec>0){
var _ed=this._scrollRegion;
if(_ed.left==0&&_ed.right==this._width-1){
var _ee=_ed.bottom-_ed.top+1;
if(_ec>=_ee){
this.clear();
}else{
var _ef=this._lines.splice(_ed.top,_ec);
for(var i=0;i<_ec;i++){
_ef[i].clear();
}
if(_ed.bottom+1==this.height){
this._lines=this._lines.concat(_ef);
}else{
for(var i=0;i<_ec;i++){
this._lines.splice(_ed.bottom-_ec+i+1,0,_ef[i]);
}
}
}
}else{
}
}
};
Term.prototype.setApplicationKeys=function(_f0){
if(isBoolean(_f0)){
this._keyHandler.setApplicationKeys(_f0);
}
};
Term.prototype.setColumn=function(_f1){
if(isNumber(_f1)&&0<=_f1&&_f1<this._width){
this._column=_f1;
}
};
Term.prototype.setCurrentAttribute=function(_f2){
if(isDefined(_f2)&&_f2.constructor===Attribute){
this._currentAttribute=_f2;
}
};
Term.prototype.setCursorVisible=function(_f3){
if(isBoolean(_f3)){
this._cursorVisible=_f3;
}
};
Term.prototype.setHeight=function(_f4){
this.setSize(this._width,_f4);
};
Term.prototype.setPosition=function(row,_f5){
if(isNumber(row)&&0<=row&&row<this._height){
this._row=row;
}
if(isNumber(_f5)&&0<=_f5&&_f5<this._width){
this._column=_f5;
}
};
Term.prototype.setRow=function(row){
if(0<=row&&row<this._height){
this._row=row;
}
};
Term.prototype.setScrollRegion=function(top,_f6,_f7,_f8){
if(isNumber(top)&&isNumber(_f6)&&isNumber(_f7)&&isNumber(_f8)){
if(top<_f7&&_f6<_f8){
var _f9=(0<=top&&top<this._height);
var _fa=(0<=_f6&&_f6<this._width);
var _fb=(0<=_f7&&_f7<this._height);
var _fc=(0<=_f8&&_f8<this._width);
if(_f9&&_fa&&_fb&&_fc){
this._scrollRegion={top:top,left:_f6,bottom:_f7,right:_f8};
}
}
}
};
Term.prototype.setSize=function(_fd,_fe){
var _ff=false;
if(isNumber(_fd)&&Line.MIN_WIDTH<=_fd&&_fd<=Line.MAX_WIDTH&&this._width!=_fd){
for(var i=0;i<this._height;i++){
this._lines[i].resize(_fd);
}
this._width=_fd;
this._column=Math.min(this._width-1,this._column);
_ff=true;
}
if(isNumber(_fe)&&Term.MIN_HEIGHT<=_fe&&_fe<=Term.MAX_HEIGHT&&this._height!=_fe){
if(_fe>this._height){
for(var i=this._height;i<_fe;i++){
this._lines.push(new Line(this._width));
}
}else{
this._lines=this._lines.splice(this._height-_fe,_fe);
}
this._height=_fe;
this._row=Math.min(this._height-1,this._row);
_ff=true;
}
if(_ff){
this.setScrollRegion(0,0,this._height-1,this._width-1);
if(this._sendResizeSequence){
var ESC=String.fromCharCode(27);
var CSI=ESC+"[";
this._keyHandler.addKeys(CSI+[8,this._height,this._width].join(";")+"t");
}
}
};
Term.prototype.setTitle=function(_100){
this._title=_100;
};
Term.prototype.toggleRunState=function(){
if(this._commHandler!==null){
if(this._id===null&&this._commHandler.isRunning()==false){
this._id=this._commHandler.getUniqueID();
}
this._commHandler.toggleRunState();
}
};
Term.prototype.setWidth=function(_101){
this.setSize(_101,this._height);
};
Term.prototype.toString=function(){
var _102=[];
for(var i=0;i<this._lines.length;i++){
_102.push(this._lines[i].toString());
}
return _102.join("\n");
};

