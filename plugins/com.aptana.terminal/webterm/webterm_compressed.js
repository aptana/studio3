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
function createURL(_6,_7){
var _8=_6;
var _9=[];
if(isDefined(_7)){
for(var k in _7){
if(_7.hasOwnProperty(k)){
_9.push(k+"="+_7[k]);
}
}
}
if(_9.length>0){
_8+="?"+_9.join("&");
}
return _8;
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
function isFunction(f){
return f!==null&&f!==undefined&&f.constructor===Function;
};
function isNumber(n){
return n!==null&&n!==undefined&&n.constructor===Number;
};
function isString(s){
return s!==null&&s!==undefined&&s.constructor===String;
};
function protectedClone(_a){
var f=function(){
};
f.prototype=_a;
var _b=new f();
_b.$parent=_a;
return _b;
};
Attribute.DEFAULT_BACKGROUND="b";
Attribute.DEFAULT_FOREGROUND="f";
function Attribute(){
this.reset();
};
Attribute.prototype.copy=function(){
var _c=new Attribute();
_c.foreground=this.foreground;
_c.background=this.background;
_c.bold=this.bold;
_c.italic=this.italic;
_c.underline=this.underline;
_c.inverse=this.inverse;
_c.strikethrough=this.strikethrough;
_c.blink=this.blink;
return _c;
};
Attribute.prototype.equals=function(_d){
var _e=false;
if(_d instanceof Attribute){
_e=this===_d||(this.foreground==_d.foreground&&this.background==_d.background&&this.bold==_d.bold&&this.italic==_d.italic&&this.underline==_d.underline&&this.inverse==_d.inverse&&this.strikethrough==_d.strikethrough&&this.blink==_d.blink);
}
return _e;
};
Attribute.prototype.getStartingHTML=function(){
var _f=[];
var _10=(isNumber(this.background))?this.background:Attribute.DEFAULT_BACKGROUND;
var _11=(isNumber(this.foreground))?this.foreground:Attribute.DEFAULT_FOREGROUND;
if(this.inverse){
_f.push("f"+_10);
_f.push("b"+_11);
}else{
_f.push("f"+_11);
_f.push("b"+_10);
}
if(this.bold){
_f.push("b");
}
if(this.italic){
_f.push("i");
}
if(this.underline){
_f.push("u");
}else{
if(this.strikethrough){
_f.push("lt");
}else{
if(this.blink){
_f.push("bl");
}
}
}
return "<span class=\""+_f.join(" ")+"\">";
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
function Line(_12){
if(isNumber(_12)){
_12=clamp(_12,Line.MIN_WIDTH,Line.MAX_WIDTH);
}else{
_12=Line.DEFAULT_WIDTH;
}
this._chars=new Array(_12);
this._attributes=new Array(_12);
this.clear();
};
Line.prototype.clear=function(ch){
ch=(isCharacter(ch))?ch.charAt(0):" ";
for(var i=0;i<this._chars.length;i++){
this._chars[i]=ch;
this._attributes[i]=new Attribute();
}
};
Line.prototype.clearLeft=function(_13){
if(isNumber(_13)&&0<=_13&&_13<this._chars.length){
for(var i=0;i<=_13;i++){
this._chars[i]=" ";
this._attributes[i]=new Attribute();
}
}
};
Line.prototype.clearRight=function(_14){
if(isNumber(_14)&&0<=_14&&_14<this._chars.length){
for(var i=_14;i<this._chars.length;i++){
this._chars[i]=" ";
this._attributes[i]=new Attribute();
}
}
};
Line.prototype.deleteCharacter=function(_15,_16){
if(isNumber(_15)){
var _17=this._chars.length;
_16=(isNumber(_16))?_16:1;
if(_16>0&&0<=_15&&_15<_17){
if(_15+_16>_17){
_16=_17-_15;
}
this._chars.splice(_15,_16);
this._attributes.splice(_15,_16);
for(var i=0;i<_16;i++){
this._chars.push(" ");
this._attributes.push(new Attribute());
}
}
}
};
Line.prototype.getHTMLInfo=function(_18,_19){
var _1a=[];
for(var i=0;i<this._chars.length;i++){
var ch=this._chars[i];
var _1b=this._attributes[i];
if(_1b&&_1b.equals(_18)==false){
if(_18!==null){
_1a.push(_18.getEndingHTML());
}
_1a.push(_1b.getStartingHTML());
_18=_1b;
}
if(i===_19){
_1a.push("<span class=\"cursor\">");
}
switch(ch){
case "&":
_1a.push("&amp;");
break;
case "<":
_1a.push("&lt;");
break;
case ">":
_1a.push("&gt;");
break;
case " ":
_1a.push("&nbsp;");
break;
default:
_1a.push(ch);
break;
}
if(i===_19){
_1a.push("</span>");
}
}
return {html:_1a.join(""),attribute:_18};
};
Line.prototype.getWidth=function(){
return this._chars.length;
};
Line.prototype.insertCharacter=function(ch,_1c,_1d){
if(isCharacter(ch)&&isNumber(_1c)){
var _1e=this._chars.length;
_1d=(isNumber(_1d))?_1d:1;
if(_1d>0&&0<=_1c&&_1c<_1e){
ch=ch.charAt(0);
if(_1c+_1d>_1e){
_1d=_1e-_1c;
}
this._chars.splice(_1e-_1d,_1d);
this._attributes.splice(_1e-_1d,_1d);
var _1f=new Array(_1d);
var _20=new Array(_1d);
for(var i=0;i<_1d;i++){
this._chars.splice(_1c+i,0,ch);
this._attributes.splice(_1c+i,0,new Attribute());
}
}
}
};
Line.prototype.putCharacter=function(ch,_21,_22){
if(isCharacter(ch)&&isDefined(_21)&&_21.constructor==Attribute&&isNumber(_22)){
if(0<=_22&&_22<this._chars.length){
this._chars[_22]=ch.charAt(0);
this._attributes[_22]=_21;
}
}
};
Line.prototype.resize=function(_23){
if(isNumber(_23)){
var _24=this._chars.length;
if(Line.MIN_WIDTH<=_23&&_23<=Line.MAX_WIDTH&&_24!=_23){
this._chars.length=_23;
if(_23>_24){
for(var i=_24;i<_23;i++){
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
var _25=this;
this._queue=[];
this._applicationKeys=false;
document.onkeypress=function(e){
return _25.processKeyPress(e);
};
document.onkeydown=function(e){
return _25.processKeyDown(e);
};
};
KeyHandler.prototype.addKeys=function(_26){
this._queue.push(_26);
if(isDefined(this.callback)){
this.callback(true);
}
};
KeyHandler.prototype.dequeueAll=function(){
var _27=this._queue.join("");
this._queue.length=0;
return _27;
};
KeyHandler.prototype.hasContent=function(){
return this._queue.length>0;
};
KeyHandler.prototype.processKeyDown=function(e){
if(!e){
e=window.event;
}
var _28=e.keyCode;
var _29=null;
var _2a=this._applicationKeys;
switch(_28){
case 8:
_29=KeyHandler.BACKSPACE;
break;
case 9:
_29=KeyHandler.TAB;
break;
case 27:
_29=KeyHandler.ESCAPE;
break;
case 33:
_29=KeyHandler.PAGE_UP;
break;
case 34:
_29=KeyHandler.PAGE_DOWN;
break;
case 35:
_29=(_2a)?KeyHandler.APP_END:KeyHandler.END;
break;
case 36:
_29=(_2a)?KeyHandler.APP_HOME:KeyHandler.HOME;
break;
case 37:
_29=(_2a)?KeyHandler.APP_LEFT:KeyHandler.LEFT;
break;
case 38:
_29=(_2a)?KeyHandler.APP_UP:KeyHandler.UP;
break;
case 39:
_29=(_2a)?KeyHandler.APP_RIGHT:KeyHandler.RIGHT;
break;
case 40:
_29=(_2a)?KeyHandler.APP_DOWN:KeyHandler.DOWN;
break;
case 45:
_29=KeyHandler.INSERT;
break;
case 46:
_29=KeyHandler.DELETE;
break;
case 112:
_29=e.shiftKey?KeyHandler.F13:KeyHandler.F1;
break;
case 113:
_29=e.shiftKey?KeyHandler.F14:KeyHandler.F2;
break;
case 114:
_29=e.shiftKey?KeyHandler.F15:KeyHandler.F3;
break;
case 115:
_29=e.shiftKey?KeyHandler.F16:KeyHandler.F4;
break;
case 116:
_29=e.shiftKey?KeyHandler.F17:KeyHandler.F5;
break;
case 117:
_29=e.shiftKey?KeyHandler.F18:KeyHandler.F6;
break;
case 118:
_29=e.shiftKey?KeyHandler.F19:KeyHandler.F7;
break;
case 119:
_29=e.shiftKey?KeyHandler.F20:KeyHandler.F8;
break;
case 120:
_29=KeyHandler.F9;
break;
case 121:
_29=KeyHandler.F10;
break;
case 122:
_29=KeyHandler.F11;
break;
case 123:
_29=KeyHandler.F12;
break;
default:
if(!e.ctrlKey||(e.ctrlKey&&e.altKey)||(e.keyCode==17)){
}else{
if(e.shiftKey){
switch(_28){
case 50:
_29=String.fromCharCode(0);
break;
case 54:
_29=String.fromCharCode(30);
break;
case 94:
_29=String.fromCharCode(30);
break;
case 109:
_29=String.fromCharCode(31);
break;
default:
break;
}
}else{
switch(_28){
case 32:
_29=String.fromCharCode(0);
break;
case 190:
_29=String.fromCharCode(30);
break;
case 219:
_29=String.fromCharCode(27);
break;
case 220:
_29=String.fromCharCode(28);
break;
case 221:
_29=String.fromCharCode(29);
break;
default:
if(65<=_28&&_28<=90){
_29=String.fromCharCode(_28-64);
}
break;
}
}
}
break;
}
if(_29!==null){
this.addKeys(_29);
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
if((e.ctrlKey&&!e.altKey)||(e.which==0)||(e.keyCode==16)){
}else{
var _2b;
if(e.keyCode){
_2b=e.keyCode;
}
if(e.which){
_2b=e.which;
}
if(_2b==8){
this.addKeys(KeyHandler.BACKSPACE);
}else{
if(e.altKey&&!e.ctrlKey){
this.addKeys(KeyHandler.ESCAPE);
}
this.addKeys(String.fromCharCode(_2b));
}
}
return this.stopEvent(e);
};
KeyHandler.prototype.setApplicationKeys=function(_2c){
if(isBoolean(_2c)){
this._applicationKeys=_2c;
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
function XTermHandler(_2d){
this._term=_2d;
this._insertMode=false;
this._missingCommands={};
};
XTermHandler.prototype.BEL=function(_2e,_2f){
};
XTermHandler.prototype.BS=function(_30,_31){
var col=this._term.getColumn()-1;
col=Math.max(0,col);
this._term.setColumn(col);
};
XTermHandler.prototype.CHA=function(_32,_33){
var _34=0;
if(_33.length>0){
_34=_33-1;
}
this._term.setColumn(_34);
};
XTermHandler.prototype.CR=function(_35,_36){
this._term.setColumn(0);
};
XTermHandler.prototype.CUB=function(_37,_38){
var _39=1;
if(_38.length>0){
_39=_38-0;
if(_39==0){
_39=1;
}
}
var col=this._term.getColumn()-_39;
col=Math.max(0,col);
this._term.setColumn(col);
};
XTermHandler.prototype.CUD=function(_3a,_3b){
var _3c=1;
if(_3b.length>0){
_3c=_3b-0;
if(_3c==0){
_3c=1;
}
}
var _3d=this._term.getRow();
var _3e=this._term.getScrollRegion().bottom;
var _3f;
if(_3d<=_3e){
_3f=Math.min(_3d+_3c,_3e);
}else{
_3f=Math.min(_3d+_3c,this._term.getHeight()-1);
}
this._term.setRow(_3f);
};
XTermHandler.prototype.CUF=function(_40,_41){
var _42=1;
if(_41.length>0){
_42=_41-0;
if(_42==0){
_42=1;
}
}
var col=this._term.getColumn()+_42;
col=Math.min(col,this._term.getWidth()-1);
this._term.setColumn(col);
};
XTermHandler.prototype.CUP=function(_43,_44){
var row=0;
var col=0;
var _45=this._term.getHeight();
if(_44.length>0){
var _46=_44.split(/;/);
var row=_46[0]-1;
var col=_46[1]-1;
}
if(row>=_45){
var _47=_45-row;
row=_45-1;
this._term.scrollUp(_47);
}
this._term.setPosition(row,col);
};
XTermHandler.prototype.CUU=function(_48,_49){
var _4a=1;
if(_49.length>0){
_4a=_49-0;
if(_4a==0){
_4a=1;
}
}
var _4b=this._term.getRow();
var _4c=this._term.getScrollRegion().top;
var _4d;
if(_4c<=_4b){
_4d=Math.max(_4c,_4b-_4a);
}else{
_4d=Math.max(0,_4b-_4a);
}
this._term.setRow(_4d);
};
XTermHandler.prototype.DCH=function(_4e,_4f){
var _50=_4f-0;
this._term.deleteCharacter(_50);
};
XTermHandler.prototype.DECALN=function(_51,_52){
this._term.clear("E");
};
XTermHandler.prototype.DECRC=function(_53,_54){
this._term.popPosition();
};
XTermHandler.prototype.DECPAM=function(_55,_56){
this._term.setApplicationKeys(true);
};
XTermHandler.prototype.DECPNM=function(_57,_58){
this._term.setApplicationKeys(false);
};
XTermHandler.prototype.DECRST=function(_59,_5a){
var _5b=_5a-0;
switch(_5b){
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
this.genericHandler(_59,_5a);
break;
}
};
XTermHandler.prototype.DECSC=function(_5c,_5d){
this._term.pushPosition();
};
XTermHandler.prototype.DECSET=function(_5e,_5f){
var _60=_5f-0;
switch(_60){
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
this.genericHandler(_5e,_5f);
break;
}
};
XTermHandler.prototype.DECSTBM=function(_61,_62){
var _63=_62.split(/;/);
var top=_63[0]-1;
var _64=_63[1]-1;
this._term.setScrollRegion(top,0,_64,this._term.getWidth()-1);
};
XTermHandler.prototype.DL=function(_65,_66){
var _67=1;
if(_66.length>0){
_67=_66-0;
if(_67==0){
_67=1;
}
}
this._term.deleteLine(_67);
};
XTermHandler.prototype.ED=function(_68,_69){
var _6a=_69-0;
switch(_6a){
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
this.genericHandler(_68+":"+_69,"");
break;
}
};
XTermHandler.prototype.EL=function(_6b,_6c){
var _6d=_6c-0;
switch(_6d){
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
this.genericHandler(_6b+":"+_6c,"");
break;
}
};
XTermHandler.prototype.genericHandler=function(_6e,_6f){
if(this._missingCommands.hasOwnProperty(_6e)===false){
this._missingCommands[_6e]=0;
}
this._missingCommands[_6e]++;
};
XTermHandler.prototype.getMissingCommands=function(){
return this._missingCommands;
};
XTermHandler.prototype.HVP=XTermHandler.prototype.CUP;
XTermHandler.prototype.ICH=function(_70,_71){
var _72=_71-0;
this._term.insertCharacter(" ",_72);
};
XTermHandler.prototype.IL=function(_73,_74){
var _75=1;
if(_74.length>0){
_75=_74-0;
if(_75==0){
_75=1;
}
}
this._term.insertLine(_75);
};
XTermHandler.prototype.IND=function(_76,_77){
var _78=this._term.getRow();
var _79=this._term.getScrollRegion().bottom;
var _7a=_78+1;
if(_78<=_79){
this._term.setRow(_7a);
}else{
this._term.scrollUp(1);
this._term.setRow(_79);
}
};
XTermHandler.prototype.LF=function(_7b,_7c){
var _7d=this._term;
var row=_7d.getRow()+1;
var _7e=_7d.getScrollRegion().bottom;
if(row>_7e){
_7d.scrollUp();
row=_7e;
}
_7d.setPosition(row,0);
};
XTermHandler.prototype.NEL=XTermHandler.prototype.LF;
XTermHandler.prototype.processCharacter=function(_7f,_80){
if(this._insertMode){
this._term.insertCharacter(" ",1);
}
this._term.displayCharacters(_80);
};
XTermHandler.prototype.RI=function(_81,_82){
var _83=this._term.getRow();
var _84=this._term.getScrollRegion().top;
var _85=_83-1;
if(_84<=_85){
this._term.setRow(_85);
}else{
this._term.scrollDown(1);
this._term.setRow(_84);
}
};
XTermHandler.prototype.RM=function(_86,_87){
var _88=_87-0;
switch(_88){
case 4:
this._insertMode=false;
break;
case 2:
case 12:
case 20:
default:
this.genericHandler(_86,_87);
break;
}
};
XTermHandler.prototype.SD=function(_89,_8a){
var _8b=1;
if(_8a.length>0){
_8b=_8a-0;
}
var _8c=this._term.getRow();
var _8d=this._term.getScrollRegion().top;
var _8e=_8c-_8b;
if(_8d<=_8e){
this._term.setRow(_8e);
}else{
this._term.scrollDown(_8b);
this._term.setRow(_8d);
}
};
XTermHandler.prototype.SET_TEXT_PARAMS=function(_8f,_90){
var _91=_90.split(/;/);
var _92=_91[0]-0;
var _93=_91[1];
if(_92==0){
this._term.setTitle(_93);
}else{
this.genericHandler(_8f+":"+_90,"");
}
};
XTermHandler.prototype.SET_TEXT_PARAMS2=XTermHandler.prototype.SET_TEXT_PARAMS;
XTermHandler.prototype.SGR=function(_94,_95){
var _96=this._term.getCurrentAttribute();
var _97=_95.split(/;/);
for(var i=0;i<_97.length;i++){
var _98=_97[i]-0;
if(_98<50){
var _99=Math.floor(_98/10);
var _9a=_98%10;
switch(_99){
case 0:
switch(_9a){
case 0:
_96.reset();
break;
case 1:
_96.bold=true;
break;
case 3:
_96.italic=true;
break;
case 4:
_96.underline=true;
break;
case 7:
_96.inverse=true;
break;
case 9:
_96.strikethrough=true;
break;
default:
this.genericHandler(_94+":"+_95,"");
break;
}
break;
case 2:
switch(_9a){
case 2:
_96.bold=false;
break;
case 3:
_96.italic=false;
break;
case 4:
_96.underline=false;
break;
case 7:
_96.inverse=false;
break;
case 9:
_96.strikethough=false;
break;
default:
this.genericHandler(_94+":"+_95,"");
break;
}
break;
case 3:
switch(_9a){
case 0:
case 1:
case 2:
case 3:
case 4:
case 5:
case 6:
case 7:
_96.foreground=_9a;
break;
case 9:
_96.resetForeground();
break;
default:
this.genericHandler(_94+":"+_95,"");
break;
}
break;
case 4:
switch(_9a){
case 0:
case 1:
case 2:
case 3:
case 4:
case 5:
case 6:
case 7:
_96.background=_9a;
break;
case 9:
_96.resetBackground();
break;
default:
this.genericHandler(_94+":"+_95,"");
break;
}
break;
default:
this.genericHandler(_94+":"+_95,"");
break;
}
}else{
this.genericHandler(_94+":"+_95,"");
}
}
this._term.setCurrentAttribute(_96);
};
XTermHandler.prototype.SM=function(_9b,_9c){
var _9d=_9c-0;
switch(_9d){
case 4:
this._insertMode=true;
break;
case 2:
case 12:
case 20:
default:
this.genericHandler(_9b,_9c);
break;
}
};
XTermHandler.prototype.SU=function(_9e,_9f){
var _a0=1;
if(_9f.length>0){
_a0=_9f-0;
}
var _a1=this._term.getRow();
var _a2=this._term.getScrollRegion().bottom;
var _a3=_a1+_a0;
if(_a1<=_a2){
this._term.setRow(_a3);
}else{
this._term.scrollUp(_a0);
this._term.setRow(_a2);
}
};
XTermHandler.prototype.TAB=function(_a4,_a5){
var _a6=this._term.getColumn();
var _a7=8-(_a6%8);
this._term.displayCharacters(new Array(_a7+1).join(" "));
};
XTermHandler.prototype.VPA=function(_a8,_a9){
var row=0;
if(_a9.length>0){
row=_a9-1;
}
this._term.setRow(row);
};
XTermHandler.prototype.VT=XTermHandler.prototype.LF;
function TermParser(_aa,_ab){
if(_aa===null||_aa===undefined){
throw new Error("Parsing tables must be defined when creating a new TermParser");
}
this._processTables(_aa);
this._actions=_aa.actions;
this._nodes=_aa.nodes;
this.setHandler(_ab);
this.singleStep=false;
this.offset=-1;
};
TermParser.prototype.getHandler=function(){
return this._handler;
};
TermParser.prototype.parse=function(_ac){
var _ad=0;
var _ae=isString(_ac)?_ac.length:0;
while(_ad<_ae){
var _af=0;
var _b0=this._nodes[_af][1];
var _b1=(_b0==-1)?-2:_ad;
for(var i=_ad;i<_ae;i++){
var _b2=this._nodes[_af];
if(_b2){
var _b3=_ac.charCodeAt(i);
var _b4=_b2[0][_b3];
if(_b4!=-1){
_af=_b4;
var _b5=this._nodes[_af][1];
if(_b5!=-1){
_b1=i;
_b0=_b5;
}
}else{
break;
}
}
}
if(_b0==-1){
if(this._handler!=null){
if(this._handler.processCharacter){
this._handler.processCharacter("processCharacter",_ac.charAt(_ad));
}
}
_ad++;
}else{
var _b6=_b1+1;
if(this._handler!=null){
var _b7=this._actions[_b0];
var _b8=_b7[0];
var _b9="";
if(_b7.length>=3&&_b7[1]!=-1&&_b7[2]!=-1){
_b9=_ac.substring(_ad+_b7[1],_b6-_b7[2]);
}
this._handler[_b8](_b8,_b9);
}
_ad=_b6;
if(this.singleStep){
this.offset=_ad;
break;
}
}
}
};
TermParser.prototype._processTables=function(_ba){
if(_ba.hasOwnProperty("processed")==false||_ba.processed==false){
switch(_ba.format){
case "expanded":
break;
case "rle":
var mos=new Array(256);
for(var i=0;i<mos.length;i++){
mos[i]=-1;
}
var _bb=_ba.nodes;
for(var i=0;i<_bb.length;i++){
var _bc=_bb[i][0];
var _bd=[];
for(var j=0;j<_bc.length;j++){
var _be=_bc[j];
if(_be<0){
_bd=_bd.concat(mos.slice(0,-_be));
}else{
var _bf=_be>>8;
var _c0=(_be&255)+1;
for(var k=0;k<_c0;k++){
_bd.push(_bf);
}
}
}
_bb[i][0]=_bd;
}
break;
default:
break;
}
_ba.processed=true;
}
};
TermParser.prototype.setHandler=function(_c1){
var _c2=null;
if(_c1){
var _c3=null;
var _c4=function(_c5,_c6){
};
for(var i=0;i<this._actions.length;i++){
var _c7=this._actions[i];
var _c8=_c7[0];
if(!_c1[_c8]){
if(_c2==null){
_c2=protectedClone(_c1);
if(!_c1.genericHandler){
_c3=_c4;
}else{
_c3=_c1.genericHandler;
}
}
_c2[_c8]=_c3;
}
}
}
if(_c2==null){
this._handler=_c1;
}else{
this._handler=_c2;
}
};
TermComm.POLLING_INTERVAL_MIN=125;
TermComm.POLLING_INTERVAL_MAX=2000;
TermComm.POLLING_GROWTH_RATE=2;
TermComm.DEFAULT_REQUEST_URL="/stream";
TermComm.DEFAULT_GET_UNIQUE_ID_URL="/id";
function TermComm(_c9,_ca){
var _cb=this;
this.terminal=_c9;
this.keyHandler=_c9.getKeyHandler();
this.keyHandler.callback=function(){
_cb.sendKeys();
};
this.minInterval=125;
this.maxInterval=2000;
this.growthRate=2;
this.timeoutInterval=5000;
this.requestURL=TermComm.DEFAULT_REQUEST_URL;
this.getUniqueIdURL=TermComm.DEFAULT_GET_UNIQUE_ID_URL;
if(isDefined(_ca)){
if(_ca.hasOwnProperty("minInterval")&&isNumber(_ca.minInterval)){
this.minInterval=_ca.minInterval;
}
if(_ca.hasOwnProperty("maxInterval")&&isNumber(_ca.maxInterval)){
this.maxInterval=_ca.maxInterval;
}
if(_ca.hasOwnProperty("growthRate")&&isNumber(_ca.growthRate)){
this.growthRate=_ca.growthRate;
}
if(_ca.hasOwnProperty("timeoutInterval")&&isNumber(_ca.timeoutInterval)){
this.timeoutInterval=_ca.timeoutInterval;
}
if(_ca.hasOwnProperty("requestURL")&&isString(_ca.requestURL)&&_ca.requestURL.length>0){
this.requestURL=_ca.requestURL;
}
if(_ca.hasOwnProperty("getUniqueIdURL")&&isString(_ca.getUniqueIdURL)&&_ca.getUniqueIdURL.length>0){
this.getUniqueIdURL=_ca.getUniqueIdURL;
}
}
this.pollingInterval=this.minInterval;
this.watchdogID=null;
this.requestID=null;
this.running=false;
this.gettingInput=false;
this.updateQueued=false;
this.sendingKeys=false;
this.cacheBusterID=0;
this.ie=(window.ActiveXObject)?true:false;
};
TermComm.prototype.getUniqueID=function(){
var req=createXHR();
req.open("GET",this.getUniqueIdURL,false);
req.send("");
return req.responseText;
};
TermComm.prototype.isRunning=function(){
return this.running;
};
TermComm.prototype.getInput=function(){
if(this.watchdogID===null){
var _cc=this;
var req=createXHR();
var _cd={id:this.terminal.getId(),cb:new Date().getTime()+":"+this.cacheBusterID++};
req.open("GET",createURL(this.requestURL,_cd),true);
if(this.ie){
req.setRequestHeader("If-Modified-Since","Sat, 1 Jan 2000 00:00:00 GMT");
}
req.onreadystatechange=function(){
if(req.readyState==4){
if(_cc.watchdogID!==null){
window.clearTimeout(_cc.watchdogID);
_cc.watchdogID=null;
}
var _ce=req.responseText;
if(isString(_ce)&&_ce.length>0){
_cc.terminal.processCharacters(_ce);
_cc.pollingInterval=_cc.minInterval;
}else{
_cc.pollingInterval*=_cc.growthRate;
if(_cc.pollingInterval>_cc.maxInterval){
_cc.pollingInterval=_cc.maxInterval;
}
}
_cc.requestID=window.setTimeout(function(){
_cc.update();
},(this.updateQueued)?0:_cc.pollingInterval);
this.updateQueued=false;
}
};
this.watchdogID=window.setTimeout(function(){
_cc.timeout();
},this.timeoutInterval);
req.send("");
}else{
this.updateQueued=true;
}
};
TermComm.prototype.sendKeys=function(){
var id=this.terminal.getId();
if(isDefined(this.keyHandler)&&id!==null){
if(this.keyHandler.hasContent()&&this.sendingKeys===false){
this.sendingKeys=true;
var _cf=this;
var req=createXHR();
var _d0={id:id};
req.open("POST",createURL(this.requestURL,_d0),true);
req.onreadystatechange=function(){
if(req.readyState==4){
_cf.sendingKeys=false;
_cf.update(true);
}
};
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
TermComm.prototype.update=function(_d1){
if(this.running&&this.terminal.getId()!==null){
if(isBoolean(_d1)){
if(this.requestID!==null){
window.clearTimeout(this.requestID);
this.requestID=null;
}
if(_d1){
this.sendKeys();
this.pollingInterval=this.minInterval;
}
}
this.getInput();
}
};
Term.DEFAULT_ID="terminal";
Term.DEFAULT_HEIGHT=24;
Term.MIN_HEIGHT=5;
Term.MAX_HEIGHT=512;
function Term(id,_d2,_d3,_d4){
if(isString(id)===false||id.length===0){
id="terminal";
}
this._id=(_d4&&_d4.hasOwnProperty("id"))?_d4.id:null;
this._remainingText="";
this._rootNode=document.getElementById(id);
if(this._rootNode){
this._rootNode.className="webterm";
this._termNode=document.createElement("pre");
this._rootNode.appendChild(this._termNode);
this._width=(isNumber(_d2))?clamp(_d2,Line.MIN_WIDTH,Line.MAX_WIDTH):Line.DEFAULT_WIDTH;
this._height=(isNumber(_d3))?clamp(_d3,Term.MIN_HEIGHT,Term.MAX_HEIGHT):Term.DEFAULT_HEIGHT;
this._title="Aptana WebTerm";
this._row=0;
this._column=0;
this._scrollRegion={top:0,left:0,bottom:this._height-1,right:this._width-1};
this._cursorVisible=true;
this._buffers=[];
this._positions=[];
this._currentAttribute=new Attribute();
this._sendResizeSequence=(_d4&&_d4.hasOwnProperty("sendResizeSequence"))?_d4.sendResizeSequence:true;
this._showTitle=(_d4&&_d4.hasOwnProperty("showTitle"))?_d4.showTitle:true;
this._onTitleChange=(_d4&&_d4.hasOwnProperty("onTitleChange"))?_d4.onTitleChange:null;
var _d5=(_d4&&_d4.hasOwnProperty("handler"))?_d4.handler:new XTermHandler(this);
var _d6=(_d4&&_d4.hasOwnProperty("tables"))?_d4.tables:XTermTables;
var _d7=(_d4&&_d4.hasOwnProperty("parser"))?_d4.parser:new TermParser(_d6,_d5);
var _d8=(_d4&&_d4.hasOwnProperty("keyHandler"))?_d4.keyHandler:new KeyHandler();
this._parser=_d7;
this._keyHandler=_d8;
var _d9=(_d4&&_d4.hasOwnProperty("commHandler"))?_d4.commHandler:new TermComm(this,_d4);
var _da=(_d4&&_d4.hasOwnProperty("autoStart"))?_d4.autoStart:true;
this._commHandler=_d9;
this.createBuffer();
this.refresh();
if(_da){
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
var _db=new Array(this._height);
for(var i=0;i<_db.length;i++){
_db[i]=new Line(this._width);
}
this._lines=_db;
};
Term.prototype.deleteCharacter=function(_dc){
this._lines[this._row].deleteCharacter(this._column,_dc);
};
Term.prototype.deleteLine=function(_dd){
_dd=(_dd===undefined)?1:_dd;
if(_dd>0){
var _de=this._scrollRegion;
if(_de.left==0&&_de.right==this._width-1){
if(this._row+_dd>_de.bottom){
_dd=_de.bottom-this._row+1;
}
if(_dd==this._height){
this.clear();
}else{
var _df=this._lines.splice(this._row,_dd);
for(var i=0;i<_dd;i++){
_df[i].clear();
}
if(_de.bottom+1==this.height){
this._lines=this._lines.concat(_df);
}else{
for(var i=0;i<_dd;i++){
this._lines.splice(_de.bottom-_dd+i+1,0,_df[i]);
}
}
}
}else{
}
}
};
Term.prototype.displayCharacters=function(_e0){
if(isString(_e0)){
for(var i=0;i<_e0.length;i++){
var ch=_e0.charAt(i);
var _e1=this._lines[this._row];
if(/[\x20-\x7F]+/.test(ch)==false){
ch=" ";
}
_e1.putCharacter(ch,this._currentAttribute,this._column);
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
Term.prototype.insertCharacter=function(ch,_e2){
this._lines[this._row].insertCharacter(ch,this._column,_e2);
};
Term.prototype.insertLine=function(_e3){
_e3=(_e3===undefined)?1:_e3;
if(_e3>0){
var _e4=this._scrollRegion;
if(_e4.left==0&&_e4.right==this._width-1){
if(this._row+_e3>_e4.bottom){
_e3=_e4.bottom-this._row+1;
}
if(_e3==this._height){
this.clear();
}else{
var _e5=this._lines.splice(_e4.bottom-_e3+1,_e3);
for(var i=0;i<_e3;i++){
_e5[i].clear();
}
if(this._row==0){
this._lines=_e5.concat(this._lines);
}else{
for(var i=0;i<_e3;i++){
this._lines.splice(this._row+i,0,_e5[i]);
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
var _e6=this._positions.pop();
this._row=_e6[0];
this._column=_e6[1];
}
};
Term.prototype.processCharacters=function(_e7){
if(isString(_e7)&&_e7.length>0){
this._parser.parse(_e7);
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
var _e8=[];
var _e9=null;
var _ea=this._title+" — "+this._width+"x"+this._height;
var _eb="<div class='title'>"+_ea+"</div>";
for(var row=0;row<this._height;row++){
var _ec=this._lines[row];
var _ed=(this._cursorVisible)?(row==this._row)?this._column:-1:-1;
var _ee=_ec.getHTMLInfo(_e9,_ed);
_e9=_ee.attribute;
_e8.push(_ee.html);
}
if(_e9!=null){
_e8[_e8.length-1]+=_e9.getEndingHTML();
}
if(this._showTitle){
this._termNode.innerHTML=_eb+_e8.join("<br />");
}else{
this._termNode.innerHTML=_e8.join("<br />");
}
};
Term.prototype.scrollDown=function(_ef){
_ef=(_ef===undefined)?1:_ef;
if(_ef>0){
var _f0=this._scrollRegion;
if(_f0.left==0&&_f0.right==this._width-1){
var _f1=_f0.bottom-_f0.top+1;
if(_ef>=_f1){
this.clear();
}else{
var _f2=this._lines.splice(_f0.bottom-_ef+1,_ef);
for(var i=0;i<_ef;i++){
_f2[i].clear();
}
if(_f0.top==0){
this._lines=_f2.concat(this._lines);
}else{
for(var i=0;i<_ef;i++){
this._lines.splice(_f0.top+i,0,_f2[i]);
}
}
}
}else{
}
}
};
Term.prototype.scrollUp=function(_f3){
_f3=(_f3===undefined)?1:_f3;
if(_f3>0){
var _f4=this._scrollRegion;
if(_f4.left==0&&_f4.right==this._width-1){
var _f5=_f4.bottom-_f4.top+1;
if(_f3>=_f5){
this.clear();
}else{
var _f6=this._lines.splice(_f4.top,_f3);
for(var i=0;i<_f3;i++){
_f6[i].clear();
}
if(_f4.bottom+1==this.height){
this._lines=this._lines.concat(_f6);
}else{
for(var i=0;i<_f3;i++){
this._lines.splice(_f4.bottom-_f3+i+1,0,_f6[i]);
}
}
}
}else{
}
}
};
Term.prototype.setApplicationKeys=function(_f7){
if(isBoolean(_f7)){
this._keyHandler.setApplicationKeys(_f7);
}
};
Term.prototype.setColumn=function(_f8){
if(isNumber(_f8)&&0<=_f8&&_f8<this._width){
this._column=_f8;
}
};
Term.prototype.setCurrentAttribute=function(_f9){
if(isDefined(_f9)&&_f9.constructor===Attribute){
this._currentAttribute=_f9;
}
};
Term.prototype.setCursorVisible=function(_fa){
if(isBoolean(_fa)){
this._cursorVisible=_fa;
}
};
Term.prototype.setHeight=function(_fb){
this.setSize(this._width,_fb);
};
Term.prototype.setPosition=function(row,_fc){
if(isNumber(row)&&0<=row&&row<this._height){
this._row=row;
}
if(isNumber(_fc)&&0<=_fc&&_fc<this._width){
this._column=_fc;
}
};
Term.prototype.setRow=function(row){
if(0<=row&&row<this._height){
this._row=row;
}
};
Term.prototype.setScrollRegion=function(top,_fd,_fe,_ff){
if(isNumber(top)&&isNumber(_fd)&&isNumber(_fe)&&isNumber(_ff)){
if(top<_fe&&_fd<_ff){
var _100=(0<=top&&top<this._height);
var _101=(0<=_fd&&_fd<this._width);
var _102=(0<=_fe&&_fe<this._height);
var _103=(0<=_ff&&_ff<this._width);
if(_100&&_101&&_102&&_103){
this._scrollRegion={top:top,left:_fd,bottom:_fe,right:_ff};
}
}
}
};
Term.prototype.setSize=function(_104,_105){
var _106=false;
if(isNumber(_104)&&Line.MIN_WIDTH<=_104&&_104<=Line.MAX_WIDTH&&this._width!=_104){
for(var i=0;i<this._height;i++){
this._lines[i].resize(_104);
}
this._width=_104;
this._column=Math.min(this._width-1,this._column);
_106=true;
}
if(isNumber(_105)&&Term.MIN_HEIGHT<=_105&&_105<=Term.MAX_HEIGHT&&this._height!=_105){
if(_105>this._height){
for(var i=this._height;i<_105;i++){
this._lines.push(new Line(this._width));
}
}else{
this._lines=this._lines.splice(this._height-_105,_105);
}
this._height=_105;
this._row=Math.min(this._height-1,this._row);
_106=true;
}
if(_106){
this.setScrollRegion(0,0,this._height-1,this._width-1);
if(this._sendResizeSequence){
var ESC=String.fromCharCode(27);
var CSI=ESC+"[";
this._keyHandler.addKeys(CSI+[8,this._height,this._width].join(";")+"t");
}
}
};
Term.prototype.setTitle=function(_107){
this._title=_107;
if(isFunction(this._onTitleChange)){
this._onTitleChange(_107);
}
};
Term.prototype.showTitle=function(_108){
if(isBoolean(_108)){
this._showTitle=_108;
this.refresh();
}
};
Term.prototype.toggleRunState=function(){
if(this._commHandler!==null){
if(this._id===null&&this._commHandler.isRunning()==false){
this._id=this._commHandler.getUniqueID();
}
this._commHandler.toggleRunState();
}
};
Term.prototype.setWidth=function(_109){
this.setSize(_109,this._height);
};
Term.prototype.toString=function(){
var _10a=[];
for(var i=0;i<this._lines.length;i++){
_10a.push(this._lines[i].toString());
}
return _10a.join("\n");
};

