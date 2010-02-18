/*
 *
 * This program is Copyright (C) 2009 Aptana, Inc. All Rights Reserved
 *
 */
var BrowserDetect={init:function(){
this.browser=this.searchString(this.dataBrowser)||"An unknown browser";
this.version=this.searchVersion(navigator.userAgent)||this.searchVersion(navigator.appVersion)||"an unknown version";
this.OS=this.searchString(this.dataOS)||"an unknown OS";
},searchString:function(_1){
for(var i=0;i<_1.length;i++){
var _2=_1[i].string;
var _3=_1[i].prop;
this.versionSearchString=_1[i].versionSearch||_1[i].identity;
if(_2){
if(_2.indexOf(_1[i].subString)!=-1){
return _1[i].identity;
}
}else{
if(_3){
return _1[i].identity;
}
}
}
},searchVersion:function(_4){
var _5=_4.indexOf(this.versionSearchString);
if(_5==-1){
return;
}
return parseFloat(_4.substring(_5+this.versionSearchString.length+1));
},dataBrowser:[{string:navigator.userAgent,subString:"Chrome",identity:"Chrome"},{string:navigator.userAgent,subString:"OmniWeb",versionSearch:"OmniWeb/",identity:"OmniWeb"},{string:navigator.vendor,subString:"Apple",identity:"Safari",versionSearch:"Version"},{prop:window.opera,identity:"Opera"},{string:navigator.vendor,subString:"iCab",identity:"iCab"},{string:navigator.vendor,subString:"KDE",identity:"Konqueror"},{string:navigator.userAgent,subString:"Firefox",identity:"Firefox"},{string:navigator.vendor,subString:"Camino",identity:"Camino"},{string:navigator.userAgent,subString:"Netscape",identity:"Netscape"},{string:navigator.userAgent,subString:"MSIE",identity:"Explorer",versionSearch:"MSIE"},{string:navigator.userAgent,subString:"Gecko",identity:"Mozilla",versionSearch:"rv"},{string:navigator.userAgent,subString:"Mozilla",identity:"Netscape",versionSearch:"Mozilla"}],dataOS:[{string:navigator.platform,subString:"Win",identity:"Windows"},{string:navigator.platform,subString:"Mac",identity:"Mac"},{string:navigator.userAgent,subString:"iPhone",identity:"iPhone/iPod"},{string:navigator.platform,subString:"Linux",identity:"Linux"}]};
BrowserDetect.init();
function clamp(_6,_7,_8){
var _9=_6;
if(isNumber(_6)&&isNumber(_7)&&isNumber(_8)){
_9=Math.min(_8,Math.max(_7,_6));
}
return _9;
};
function createXHR(){
var _a;
if(window.XMLHttpRequest){
_a=new XMLHttpRequest();
}else{
_a=new ActiveXObject("Microsoft.XMLHTTP");
}
return _a;
};
function createURL(_b,_c){
var _d=_b;
var _e=[];
if(isDefined(_c)){
for(var k in _c){
if(_c.hasOwnProperty(k)){
_e.push(k+"="+_c[k]);
}
}
}
if(_e.length>0){
_d+="?"+_e.join("&");
}
return _d;
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
function protectedClone(_f){
var f=function(){
};
f.prototype=_f;
var _10=new f();
_10.$parent=_f;
return _10;
};
Attribute.DEFAULT_BACKGROUND="b";
Attribute.DEFAULT_FOREGROUND="f";
function Attribute(){
this.reset();
};
Attribute.prototype.copy=function(){
var _11=new Attribute();
_11.foreground=this.foreground;
_11.background=this.background;
_11.bold=this.bold;
_11.italic=this.italic;
_11.underline=this.underline;
_11.inverse=this.inverse;
_11.strikethrough=this.strikethrough;
_11.blink=this.blink;
return _11;
};
Attribute.prototype.equals=function(_12){
var _13=false;
if(_12 instanceof Attribute){
_13=this===_12||(this.foreground==_12.foreground&&this.background==_12.background&&this.bold==_12.bold&&this.italic==_12.italic&&this.underline==_12.underline&&this.inverse==_12.inverse&&this.strikethrough==_12.strikethrough&&this.blink==_12.blink);
}
return _13;
};
Attribute.prototype.getStartingHTML=function(){
var _14=[];
var _15=(isNumber(this.background))?this.background:Attribute.DEFAULT_BACKGROUND;
var _16=(isNumber(this.foreground))?this.foreground:Attribute.DEFAULT_FOREGROUND;
if(this.inverse){
_14.push("f"+_15);
_14.push("b"+_16);
}else{
_14.push("f"+_16);
_14.push("b"+_15);
}
if(this.bold){
_14.push("b");
}
if(this.italic){
_14.push("i");
}
if(this.underline){
_14.push("u");
}else{
if(this.strikethrough){
_14.push("lt");
}else{
if(this.blink){
_14.push("bl");
}
}
}
return "<span class=\""+_14.join(" ")+"\">";
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
function Line(_17){
if(isNumber(_17)){
_17=clamp(_17,Line.MIN_WIDTH,Line.MAX_WIDTH);
}else{
_17=Line.DEFAULT_WIDTH;
}
this._chars=new Array(_17);
this._attributes=new Array(_17);
this.clear();
};
Line.prototype.clear=function(ch){
ch=(isCharacter(ch))?ch.charAt(0):" ";
for(var i=0;i<this._chars.length;i++){
this._chars[i]=ch;
this._attributes[i]=new Attribute();
}
};
Line.prototype.clearLeft=function(_18){
if(isNumber(_18)&&0<=_18&&_18<this._chars.length){
for(var i=0;i<=_18;i++){
this._chars[i]=" ";
this._attributes[i]=new Attribute();
}
}
};
Line.prototype.clearRight=function(_19){
if(isNumber(_19)&&0<=_19&&_19<this._chars.length){
for(var i=_19;i<this._chars.length;i++){
this._chars[i]=" ";
this._attributes[i]=new Attribute();
}
}
};
Line.prototype.deleteCharacter=function(_1a,_1b){
if(isNumber(_1a)){
var _1c=this._chars.length;
_1b=(isNumber(_1b))?_1b:1;
if(_1b>0&&0<=_1a&&_1a<_1c){
if(_1a+_1b>_1c){
_1b=_1c-_1a;
}
this._chars.splice(_1a,_1b);
this._attributes.splice(_1a,_1b);
for(var i=0;i<_1b;i++){
this._chars.push(" ");
this._attributes.push(new Attribute());
}
}
}
};
Line.prototype.getHTMLInfo=function(_1d,_1e){
var _1f=[];
for(var i=0;i<this._chars.length;i++){
var ch=this._chars[i];
var _20=this._attributes[i];
if(_20&&_20.equals(_1d)==false){
if(_1d!==null){
_1f.push(_1d.getEndingHTML());
}
_1f.push(_20.getStartingHTML());
_1d=_20;
}
if(i===_1e){
_1f.push("<span class=\"cursor\">");
}
switch(ch){
case "&":
_1f.push("&amp;");
break;
case "<":
_1f.push("&lt;");
break;
case ">":
_1f.push("&gt;");
break;
case " ":
_1f.push("&nbsp;");
break;
default:
_1f.push(ch);
break;
}
if(i===_1e){
_1f.push("</span>");
}
}
return {html:_1f.join(""),attribute:_1d};
};
Line.prototype.getWidth=function(){
return this._chars.length;
};
Line.prototype.insertCharacter=function(ch,_21,_22){
if(isCharacter(ch)&&isNumber(_21)){
var _23=this._chars.length;
_22=(isNumber(_22))?_22:1;
if(_22>0&&0<=_21&&_21<_23){
ch=ch.charAt(0);
if(_21+_22>_23){
_22=_23-_21;
}
this._chars.splice(_23-_22,_22);
this._attributes.splice(_23-_22,_22);
var _24=new Array(_22);
var _25=new Array(_22);
for(var i=0;i<_22;i++){
this._chars.splice(_21+i,0,ch);
this._attributes.splice(_21+i,0,new Attribute());
}
}
}
};
Line.prototype.putCharacter=function(ch,_26,_27){
if(isCharacter(ch)&&isDefined(_26)&&_26.constructor==Attribute&&isNumber(_27)){
if(0<=_27&&_27<this._chars.length){
this._chars[_27]=ch.charAt(0);
this._attributes[_27]=_26;
}
}
};
Line.prototype.resize=function(_28){
if(isNumber(_28)){
var _29=this._chars.length;
if(Line.MIN_WIDTH<=_28&&_28<=Line.MAX_WIDTH&&_29!=_28){
this._chars.length=_28;
if(_28>_29){
for(var i=_29;i<_28;i++){
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
KeyHandler.KEY_DOWN="keydown";
KeyHandler.KEY_PRESS="keypress";
KeyHandler.RECORDING="recording";
KeyHandler.PLAYING="playing";
KeyHandler.STOPPED="stopped";
function KeyHandler(){
var _2a=this;
this._queue=[];
this._applicationKeys=false;
this._playbackState=KeyHandler.STOPPED;
this.clearEvents();
this._playbackID=null;
document.onkeypress=function(e){
return _2a.processKeyPress(e);
};
document.onkeydown=function(e){
return _2a.processKeyDown(e);
};
};
KeyHandler.prototype.addEvent=function(_2b,e){
if(e){
var _2c={};
switch(_2b){
case KeyHandler.KEY_DOWN:
_2c.keyCode=e.keyCode;
_2c.ctrlKey=e.ctrlKey;
_2c.altKey=e.altKey;
_2c.shiftKey=e.shiftKey;
break;
case KeyHandler.KEY_PRESS:
_2c.keyCode=e.keyCode;
_2c.which=e.which;
_2c.ctrlKey=e.ctrlKey;
_2c.altKey=e.altKey;
_2c.metaKey=e.metaKey;
break;
default:
return;
}
this._events.keys.push({time:new Date().getTime(),type:_2b,event:_2c});
}
};
KeyHandler.prototype.addKeys=function(_2d){
this._queue.push(_2d);
if(isDefined(this.callback)){
this.callback(true);
}
};
KeyHandler.prototype.clearEvents=function(){
this._events={user_agent:{browser:BrowserDetect.browser,version:BrowserDetect.version,os:BrowserDetect.OS},keys:[]};
};
KeyHandler.prototype.dequeueAll=function(){
var _2e=this._queue.join("");
this._queue.length=0;
return _2e;
};
KeyHandler.prototype.hasContent=function(){
return this._queue.length>0;
};
KeyHandler.prototype.play=function(_2f){
if(this._playbackState!=KeyHandler.PLAYING){
this._playbackState=KeyHandler.PLAYING;
_2f=_2f||this._events.keys;
var _30=this;
var i=0;
var _31=function(){
var _32=_2f[i++];
switch(_32.type){
case KeyHandler.KEY_DOWN:
_30.processKeyDown(_32.event);
break;
case KeyHandler.KEY_PRESS:
_30.processKeyPress(_32.event);
break;
default:
break;
}
if(_30._playbackState==KeyHandler.PLAYING&&i<_2f.length){
var _33=clamp(_2f[i].time-_32.time,0,1000);
this._playbackID=window.setTimeout(_31,_33);
}
};
_31();
}
};
KeyHandler.prototype.processKeyDown=function(e){
if(!e){
e=window.event;
}
if(this._playbackState==KeyHandler.RECORDING){
this.addEvent(KeyHandler.KEY_DOWN,e);
}
var _34=e.keyCode;
var _35=null;
var _36=this._applicationKeys;
if(BrowserDetect.browser=="Firefox"&&(e.keyCode==8||(37<=e.keyCode&&e.keyCode<=40))){
}else{
switch(_34){
case 8:
_35=KeyHandler.BACKSPACE;
break;
case 9:
_35=KeyHandler.TAB;
break;
case 27:
_35=KeyHandler.ESCAPE;
break;
case 33:
_35=KeyHandler.PAGE_UP;
break;
case 34:
_35=KeyHandler.PAGE_DOWN;
break;
case 35:
_35=(_36)?KeyHandler.APP_END:KeyHandler.END;
break;
case 36:
_35=(_36)?KeyHandler.APP_HOME:KeyHandler.HOME;
break;
case 37:
_35=(_36)?KeyHandler.APP_LEFT:KeyHandler.LEFT;
break;
case 38:
_35=(_36)?KeyHandler.APP_UP:KeyHandler.UP;
break;
case 39:
_35=(_36)?KeyHandler.APP_RIGHT:KeyHandler.RIGHT;
break;
case 40:
_35=(_36)?KeyHandler.APP_DOWN:KeyHandler.DOWN;
break;
case 45:
_35=KeyHandler.INSERT;
break;
case 46:
_35=KeyHandler.DELETE;
break;
case 112:
_35=e.shiftKey?KeyHandler.F13:KeyHandler.F1;
break;
case 113:
_35=e.shiftKey?KeyHandler.F14:KeyHandler.F2;
break;
case 114:
_35=e.shiftKey?KeyHandler.F15:KeyHandler.F3;
break;
case 115:
_35=e.shiftKey?KeyHandler.F16:KeyHandler.F4;
break;
case 116:
_35=e.shiftKey?KeyHandler.F17:KeyHandler.F5;
break;
case 117:
_35=e.shiftKey?KeyHandler.F18:KeyHandler.F6;
break;
case 118:
_35=e.shiftKey?KeyHandler.F19:KeyHandler.F7;
break;
case 119:
_35=e.shiftKey?KeyHandler.F20:KeyHandler.F8;
break;
case 120:
_35=KeyHandler.F9;
break;
case 121:
_35=KeyHandler.F10;
break;
case 122:
_35=KeyHandler.F11;
break;
case 123:
_35=KeyHandler.F12;
break;
default:
if(!e.ctrlKey||(e.ctrlKey&&e.altKey)||(e.keyCode==17)){
}else{
if(e.shiftKey){
switch(_34){
case 50:
_35=String.fromCharCode(0);
break;
case 54:
_35=String.fromCharCode(30);
break;
case 94:
_35=String.fromCharCode(30);
break;
case 109:
_35=String.fromCharCode(31);
break;
default:
break;
}
}else{
switch(_34){
case 32:
_35=String.fromCharCode(0);
break;
case 190:
_35=String.fromCharCode(30);
break;
case 219:
_35=String.fromCharCode(27);
break;
case 220:
_35=String.fromCharCode(28);
break;
case 221:
_35=String.fromCharCode(29);
break;
default:
if(65<=_34&&_34<=90){
_35=String.fromCharCode(_34-64);
}
break;
}
}
}
break;
}
}
if(_35!==null){
this.addKeys(_35);
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
if(this._playbackState==KeyHandler.RECORDING){
this.addEvent(KeyHandler.KEY_PRESS,e);
}
if(BrowserDetect.browser=="Firefox"&&(e.keyCode==8||(37<=e.keyCode&&e.keyCode<=40))){
var _37=this._applicationKeys;
var _38=null;
switch(e.keyCode){
case 8:
_38=KeyHandler.BACKSPACE;
break;
case 37:
_38=(_37)?KeyHandler.APP_LEFT:KeyHandler.LEFT;
break;
case 38:
_38=(_37)?KeyHandler.APP_UP:KeyHandler.UP;
break;
case 39:
_38=(_37)?KeyHandler.APP_RIGHT:KeyHandler.RIGHT;
break;
case 40:
_38=(_37)?KeyHandler.APP_DOWN:KeyHandler.DOWN;
break;
}
if(_38!==null){
this.addKeys(_38);
}
}else{
if((e.ctrlKey&&!e.altKey)||(e.which==0)||(e.keyCode==8)||(e.keyCode==16)){
}else{
var _39;
if(e.keyCode){
_39=e.keyCode;
}
if(e.which){
_39=e.which;
}
if(e.altKey&&!e.ctrlKey){
this.addKeys(KeyHandler.ESCAPE);
}
this.addKeys(String.fromCharCode(_39));
}
}
return this.stopEvent(e);
};
KeyHandler.prototype.record=function(){
if(this._playbackState!=KeyHandler.RECORDING){
if(this._playbackState==KeyHandler.PLAYING){
this.stop();
}
this.clearEvents();
this._playbackState=KeyHandler.RECORDING;
}
};
KeyHandler.prototype.setApplicationKeys=function(_3a){
if(isBoolean(_3a)){
this._applicationKeys=_3a;
}
};
KeyHandler.prototype.stop=function(){
if(this._playbackState!=KeyHandler.STOPPED){
this._playbackState=KeyHandler.STOPPED;
if(this._playbackID!==null){
window.clearTimeout(this._playbackID);
this._playbackID=null;
}
}
return this._events;
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
function XTermHandler(_3b){
this._term=_3b;
this._insertMode=false;
this._missingCommands={};
};
XTermHandler.prototype.BEL=function(_3c,_3d){
};
XTermHandler.prototype.BS=function(_3e,_3f){
var col=this._term.getColumn()-1;
col=Math.max(0,col);
this._term.setColumn(col);
};
XTermHandler.prototype.CHA=function(_40,_41){
var _42=0;
if(_41.length>0){
_42=_41-1;
}
this._term.setColumn(_42);
};
XTermHandler.prototype.CR=function(_43,_44){
this._term.setColumn(0);
};
XTermHandler.prototype.CUB=function(_45,_46){
var _47=1;
if(_46.length>0){
_47=_46-0;
if(_47==0){
_47=1;
}
}
var col=this._term.getColumn()-_47;
col=Math.max(0,col);
this._term.setColumn(col);
};
XTermHandler.prototype.CUD=function(_48,_49){
var _4a=1;
if(_49.length>0){
_4a=_49-0;
if(_4a==0){
_4a=1;
}
}
var _4b=this._term.getRow();
var _4c=this._term.getScrollRegion().bottom;
var _4d;
if(_4b<=_4c){
_4d=Math.min(_4b+_4a,_4c);
}else{
_4d=Math.min(_4b+_4a,this._term.getHeight()-1);
}
this._term.setRow(_4d);
};
XTermHandler.prototype.CUF=function(_4e,_4f){
var _50=1;
if(_4f.length>0){
_50=_4f-0;
if(_50==0){
_50=1;
}
}
var col=this._term.getColumn()+_50;
col=Math.min(col,this._term.getWidth()-1);
this._term.setColumn(col);
};
XTermHandler.prototype.CUP=function(_51,_52){
var row=0;
var col=0;
var _53=this._term.getHeight();
if(_52.length>0){
var _54=_52.split(/;/);
var row=_54[0]-1;
var col=_54[1]-1;
}
if(row>=_53){
var _55=_53-row;
row=_53-1;
this._term.scrollUp(_55);
}
this._term.setPosition(row,col);
};
XTermHandler.prototype.CUU=function(_56,_57){
var _58=1;
if(_57.length>0){
_58=_57-0;
if(_58==0){
_58=1;
}
}
var _59=this._term.getRow();
var _5a=this._term.getScrollRegion().top;
var _5b;
if(_5a<=_59){
_5b=Math.max(_5a,_59-_58);
}else{
_5b=Math.max(0,_59-_58);
}
this._term.setRow(_5b);
};
XTermHandler.prototype.DCH=function(_5c,_5d){
var _5e=_5d-0;
this._term.deleteCharacter(_5e);
};
XTermHandler.prototype.DECALN=function(_5f,_60){
this._term.clear("E");
};
XTermHandler.prototype.DECRC=function(_61,_62){
this._term.popPosition();
};
XTermHandler.prototype.DECPAM=function(_63,_64){
this._term.setApplicationKeys(true);
};
XTermHandler.prototype.DECPNM=function(_65,_66){
this._term.setApplicationKeys(false);
};
XTermHandler.prototype.DECRST=function(_67,_68){
var _69=_68-0;
switch(_69){
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
this.genericHandler(_67,_68);
break;
}
};
XTermHandler.prototype.DECSC=function(_6a,_6b){
this._term.pushPosition();
};
XTermHandler.prototype.DECSET=function(_6c,_6d){
var _6e=_6d-0;
switch(_6e){
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
this.genericHandler(_6c,_6d);
break;
}
};
XTermHandler.prototype.DECSTBM=function(_6f,_70){
var _71=_70.split(/;/);
var top=_71[0]-1;
var _72=_71[1]-1;
this._term.setScrollRegion(top,0,_72,this._term.getWidth()-1);
};
XTermHandler.prototype.DL=function(_73,_74){
var _75=1;
if(_74.length>0){
_75=_74-0;
if(_75==0){
_75=1;
}
}
this._term.deleteLine(_75);
};
XTermHandler.prototype.ED=function(_76,_77){
var _78=_77-0;
switch(_78){
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
this.genericHandler(_76+":"+_77,"");
break;
}
};
XTermHandler.prototype.EL=function(_79,_7a){
var _7b=_7a-0;
switch(_7b){
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
this.genericHandler(_79+":"+_7a,"");
break;
}
};
XTermHandler.prototype.genericHandler=function(_7c,_7d){
if(this._missingCommands.hasOwnProperty(_7c)===false){
this._missingCommands[_7c]=0;
}
this._missingCommands[_7c]++;
};
XTermHandler.prototype.getMissingCommands=function(){
return this._missingCommands;
};
XTermHandler.prototype.HVP=XTermHandler.prototype.CUP;
XTermHandler.prototype.ICH=function(_7e,_7f){
var _80=_7f-0;
this._term.insertCharacter(" ",_80);
};
XTermHandler.prototype.IL=function(_81,_82){
var _83=1;
if(_82.length>0){
_83=_82-0;
if(_83==0){
_83=1;
}
}
this._term.insertLine(_83);
};
XTermHandler.prototype.IND=function(_84,_85){
var _86=this._term.getRow();
var _87=this._term.getScrollRegion().bottom;
var _88=_86+1;
if(_86<=_87){
this._term.setRow(_88);
}else{
this._term.scrollUp(1);
this._term.setRow(_87);
}
};
XTermHandler.prototype.LF=function(_89,_8a){
var _8b=this._term;
var row=_8b.getRow()+1;
var _8c=_8b.getScrollRegion().bottom;
if(row>_8c){
_8b.scrollUp();
row=_8c;
}
_8b.setPosition(row,0);
};
XTermHandler.prototype.NEL=XTermHandler.prototype.LF;
XTermHandler.prototype.processCharacter=function(_8d,_8e){
if(this._insertMode){
this._term.insertCharacter(" ",1);
}
this._term.displayCharacters(_8e);
};
XTermHandler.prototype.RI=function(_8f,_90){
var _91=this._term.getRow();
var _92=this._term.getScrollRegion().top;
var _93=_91-1;
if(_92<=_93){
this._term.setRow(_93);
}else{
this._term.scrollDown(1);
this._term.setRow(_92);
}
};
XTermHandler.prototype.RM=function(_94,_95){
var _96=_95-0;
switch(_96){
case 4:
this._insertMode=false;
break;
case 2:
case 12:
case 20:
default:
this.genericHandler(_94,_95);
break;
}
};
XTermHandler.prototype.SD=function(_97,_98){
var _99=1;
if(_98.length>0){
_99=_98-0;
}
var _9a=this._term.getRow();
var _9b=this._term.getScrollRegion().top;
var _9c=_9a-_99;
if(_9b<=_9c){
this._term.setRow(_9c);
}else{
this._term.scrollDown(_99);
this._term.setRow(_9b);
}
};
XTermHandler.prototype.SET_TEXT_PARAMS=function(_9d,_9e){
var _9f=_9e.split(/;/);
var _a0=_9f[0]-0;
var _a1=_9f[1];
if(_a0==0){
this._term.setTitle(_a1);
}else{
this.genericHandler(_9d+":"+_9e,"");
}
};
XTermHandler.prototype.SET_TEXT_PARAMS2=XTermHandler.prototype.SET_TEXT_PARAMS;
XTermHandler.prototype.SGR=function(_a2,_a3){
var _a4=this._term.getCurrentAttribute();
var _a5=_a3.split(/;/);
for(var i=0;i<_a5.length;i++){
var _a6=_a5[i]-0;
if(_a6<50){
var _a7=Math.floor(_a6/10);
var _a8=_a6%10;
switch(_a7){
case 0:
switch(_a8){
case 0:
_a4.reset();
break;
case 1:
_a4.bold=true;
break;
case 3:
_a4.italic=true;
break;
case 4:
_a4.underline=true;
break;
case 7:
_a4.inverse=true;
break;
case 9:
_a4.strikethrough=true;
break;
default:
this.genericHandler(_a2+":"+_a3,"");
break;
}
break;
case 2:
switch(_a8){
case 2:
_a4.bold=false;
break;
case 3:
_a4.italic=false;
break;
case 4:
_a4.underline=false;
break;
case 7:
_a4.inverse=false;
break;
case 9:
_a4.strikethough=false;
break;
default:
this.genericHandler(_a2+":"+_a3,"");
break;
}
break;
case 3:
switch(_a8){
case 0:
case 1:
case 2:
case 3:
case 4:
case 5:
case 6:
case 7:
_a4.foreground=_a8;
break;
case 9:
_a4.resetForeground();
break;
default:
this.genericHandler(_a2+":"+_a3,"");
break;
}
break;
case 4:
switch(_a8){
case 0:
case 1:
case 2:
case 3:
case 4:
case 5:
case 6:
case 7:
_a4.background=_a8;
break;
case 9:
_a4.resetBackground();
break;
default:
this.genericHandler(_a2+":"+_a3,"");
break;
}
break;
default:
this.genericHandler(_a2+":"+_a3,"");
break;
}
}else{
this.genericHandler(_a2+":"+_a3,"");
}
}
this._term.setCurrentAttribute(_a4);
};
XTermHandler.prototype.SM=function(_a9,_aa){
var _ab=_aa-0;
switch(_ab){
case 4:
this._insertMode=true;
break;
case 2:
case 12:
case 20:
default:
this.genericHandler(_a9,_aa);
break;
}
};
XTermHandler.prototype.SU=function(_ac,_ad){
var _ae=1;
if(_ad.length>0){
_ae=_ad-0;
}
var _af=this._term.getRow();
var _b0=this._term.getScrollRegion().bottom;
var _b1=_af+_ae;
if(_af<=_b0){
this._term.setRow(_b1);
}else{
this._term.scrollUp(_ae);
this._term.setRow(_b0);
}
};
XTermHandler.prototype.TAB=function(_b2,_b3){
var _b4=this._term.getColumn();
var _b5=8-(_b4%8);
this._term.displayCharacters(new Array(_b5+1).join(" "));
};
XTermHandler.prototype.VPA=function(_b6,_b7){
var row=0;
if(_b7.length>0){
row=_b7-1;
}
this._term.setRow(row);
};
XTermHandler.prototype.VT=XTermHandler.prototype.LF;
function TermParser(_b8,_b9){
if(_b8===null||_b8===undefined){
throw new Error("Parsing tables must be defined when creating a new TermParser");
}
this._processTables(_b8);
this._actions=_b8.actions;
this._nodes=_b8.nodes;
this.setHandler(_b9);
this.singleStep=false;
this.offset=-1;
};
TermParser.prototype.getHandler=function(){
return this._handler;
};
TermParser.prototype.parse=function(_ba){
var _bb=0;
var _bc=isString(_ba)?_ba.length:0;
while(_bb<_bc){
var _bd=0;
var _be=this._nodes[_bd][1];
var _bf=(_be==-1)?-2:_bb;
for(var i=_bb;i<_bc;i++){
var _c0=this._nodes[_bd];
if(_c0){
var _c1=_ba.charCodeAt(i);
var _c2=_c0[0][_c1];
if(_c2!=-1){
_bd=_c2;
var _c3=this._nodes[_bd][1];
if(_c3!=-1){
_bf=i;
_be=_c3;
}
}else{
break;
}
}
}
if(_be==-1){
if(this._handler!=null){
if(this._handler.processCharacter){
this._handler.processCharacter("processCharacter",_ba.charAt(_bb));
}
}
_bb++;
}else{
var _c4=_bf+1;
if(this._handler!=null){
var _c5=this._actions[_be];
var _c6=_c5[0];
var _c7="";
if(_c5.length>=3&&_c5[1]!=-1&&_c5[2]!=-1){
_c7=_ba.substring(_bb+_c5[1],_c4-_c5[2]);
}
this._handler[_c6](_c6,_c7);
}
_bb=_c4;
if(this.singleStep){
this.offset=_bb;
break;
}
}
}
};
TermParser.prototype._processTables=function(_c8){
if(_c8.hasOwnProperty("processed")==false||_c8.processed==false){
switch(_c8.format){
case "expanded":
break;
case "rle":
var mos=new Array(256);
for(var i=0;i<mos.length;i++){
mos[i]=-1;
}
var _c9=_c8.nodes;
for(var i=0;i<_c9.length;i++){
var _ca=_c9[i][0];
var _cb=[];
for(var j=0;j<_ca.length;j++){
var _cc=_ca[j];
if(_cc<0){
_cb=_cb.concat(mos.slice(0,-_cc));
}else{
var _cd=_cc>>8;
var _ce=(_cc&255)+1;
for(var k=0;k<_ce;k++){
_cb.push(_cd);
}
}
}
_c9[i][0]=_cb;
}
break;
default:
break;
}
_c8.processed=true;
}
};
TermParser.prototype.setHandler=function(_cf){
var _d0=null;
if(_cf){
var _d1=null;
var _d2=function(_d3,_d4){
};
for(var i=0;i<this._actions.length;i++){
var _d5=this._actions[i];
var _d6=_d5[0];
if(!_cf[_d6]){
if(_d0==null){
_d0=protectedClone(_cf);
if(!_cf.genericHandler){
_d1=_d2;
}else{
_d1=_cf.genericHandler;
}
}
_d0[_d6]=_d1;
}
}
}
if(_d0==null){
this._handler=_cf;
}else{
this._handler=_d0;
}
};
TermComm.POLLING_INTERVAL_MIN=125;
TermComm.POLLING_INTERVAL_MAX=2000;
TermComm.POLLING_GROWTH_RATE=2;
TermComm.DEFAULT_REQUEST_URL="/stream";
TermComm.DEFAULT_GET_UNIQUE_ID_URL="/id";
function TermComm(_d7,_d8){
var _d9=this;
this.terminal=_d7;
this.keyHandler=_d7.getKeyHandler();
this.keyHandler.callback=function(){
_d9.sendKeys();
};
this.minInterval=125;
this.maxInterval=2000;
this.growthRate=2;
this.timeoutInterval=5000;
this.requestURL=TermComm.DEFAULT_REQUEST_URL;
this.getUniqueIdURL=TermComm.DEFAULT_GET_UNIQUE_ID_URL;
if(isDefined(_d8)){
if(_d8.hasOwnProperty("minInterval")&&isNumber(_d8.minInterval)){
this.minInterval=_d8.minInterval;
}
if(_d8.hasOwnProperty("maxInterval")&&isNumber(_d8.maxInterval)){
this.maxInterval=_d8.maxInterval;
}
if(_d8.hasOwnProperty("growthRate")&&isNumber(_d8.growthRate)){
this.growthRate=_d8.growthRate;
}
if(_d8.hasOwnProperty("timeoutInterval")&&isNumber(_d8.timeoutInterval)){
this.timeoutInterval=_d8.timeoutInterval;
}
if(_d8.hasOwnProperty("requestURL")&&isString(_d8.requestURL)&&_d8.requestURL.length>0){
this.requestURL=_d8.requestURL;
}
if(_d8.hasOwnProperty("getUniqueIdURL")&&isString(_d8.getUniqueIdURL)&&_d8.getUniqueIdURL.length>0){
this.getUniqueIdURL=_d8.getUniqueIdURL;
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
var _da=this;
var req=createXHR();
var _db={id:this.terminal.getId(),cb:new Date().getTime()+":"+this.cacheBusterID++};
req.open("GET",createURL(this.requestURL,_db),true);
if(this.ie){
req.setRequestHeader("If-Modified-Since","Sat, 1 Jan 2000 00:00:00 GMT");
}
req.onreadystatechange=function(){
if(req.readyState==4){
if(_da.watchdogID!==null){
window.clearTimeout(_da.watchdogID);
_da.watchdogID=null;
}
var _dc=req.responseText;
if(isString(_dc)&&_dc.length>0){
_da.terminal.processCharacters(_dc);
_da.pollingInterval=_da.minInterval;
}else{
_da.pollingInterval*=_da.growthRate;
if(_da.pollingInterval>_da.maxInterval){
_da.pollingInterval=_da.maxInterval;
}
}
_da.requestID=window.setTimeout(function(){
_da.update();
},(this.updateQueued)?0:_da.pollingInterval);
this.updateQueued=false;
}
};
this.watchdogID=window.setTimeout(function(){
_da.timeout();
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
var _dd=this;
var req=createXHR();
var _de={id:id};
req.open("POST",createURL(this.requestURL,_de),true);
req.onreadystatechange=function(){
if(req.readyState==4){
_dd.sendingKeys=false;
_dd.update(true);
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
TermComm.prototype.update=function(_df){
if(this.running&&this.terminal.getId()!==null){
if(isBoolean(_df)){
if(this.requestID!==null){
window.clearTimeout(this.requestID);
this.requestID=null;
}
if(_df){
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
function Term(id,_e0,_e1,_e2){
if(isString(id)===false||id.length===0){
id="terminal";
}
this._id=(_e2&&_e2.hasOwnProperty("id"))?_e2.id:null;
this._remainingText="";
this._rootNode=document.getElementById(id);
if(this._rootNode){
this._rootNode.className="webterm";
this._termNode=document.createElement("pre");
this._rootNode.appendChild(this._termNode);
this._width=(isNumber(_e0))?clamp(_e0,Line.MIN_WIDTH,Line.MAX_WIDTH):Line.DEFAULT_WIDTH;
this._height=(isNumber(_e1))?clamp(_e1,Term.MIN_HEIGHT,Term.MAX_HEIGHT):Term.DEFAULT_HEIGHT;
this._title="Aptana WebTerm";
this._row=0;
this._column=0;
this._scrollRegion={top:0,left:0,bottom:this._height-1,right:this._width-1};
this._cursorVisible=true;
this._buffers=[];
this._positions=[];
this._currentAttribute=new Attribute();
this._sendResizeSequence=(_e2&&_e2.hasOwnProperty("sendResizeSequence"))?_e2.sendResizeSequence:true;
this._showTitle=(_e2&&_e2.hasOwnProperty("showTitle"))?_e2.showTitle:true;
this._onTitleChange=(_e2&&_e2.hasOwnProperty("onTitleChange"))?_e2.onTitleChange:null;
var _e3=(_e2&&_e2.hasOwnProperty("handler"))?_e2.handler:new XTermHandler(this);
var _e4=(_e2&&_e2.hasOwnProperty("tables"))?_e2.tables:XTermTables;
var _e5=(_e2&&_e2.hasOwnProperty("parser"))?_e2.parser:new TermParser(_e4,_e3);
var _e6=(_e2&&_e2.hasOwnProperty("keyHandler"))?_e2.keyHandler:new KeyHandler();
this._parser=_e5;
this._keyHandler=_e6;
var _e7=(_e2&&_e2.hasOwnProperty("commHandler"))?_e2.commHandler:new TermComm(this,_e2);
var _e8=(_e2&&_e2.hasOwnProperty("autoStart"))?_e2.autoStart:true;
this._commHandler=_e7;
this.createBuffer();
this.refresh();
if(_e8){
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
var _e9=new Array(this._height);
for(var i=0;i<_e9.length;i++){
_e9[i]=new Line(this._width);
}
this._lines=_e9;
};
Term.prototype.deleteCharacter=function(_ea){
this._lines[this._row].deleteCharacter(this._column,_ea);
};
Term.prototype.deleteLine=function(_eb){
_eb=(_eb===undefined)?1:_eb;
if(_eb>0){
var _ec=this._scrollRegion;
if(_ec.left==0&&_ec.right==this._width-1){
if(this._row+_eb>_ec.bottom){
_eb=_ec.bottom-this._row+1;
}
if(_eb==this._height){
this.clear();
}else{
var _ed=this._lines.splice(this._row,_eb);
for(var i=0;i<_eb;i++){
_ed[i].clear();
}
if(_ec.bottom+1==this.height){
this._lines=this._lines.concat(_ed);
}else{
for(var i=0;i<_eb;i++){
this._lines.splice(_ec.bottom-_eb+i+1,0,_ed[i]);
}
}
}
}else{
}
}
};
Term.prototype.displayCharacters=function(_ee){
if(isString(_ee)){
for(var i=0;i<_ee.length;i++){
var ch=_ee.charAt(i);
var _ef=this._lines[this._row];
if(/[\x20-\x7F]+/.test(ch)==false){
ch=" ";
}
_ef.putCharacter(ch,this._currentAttribute,this._column);
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
Term.prototype.insertCharacter=function(ch,_f0){
this._lines[this._row].insertCharacter(ch,this._column,_f0);
};
Term.prototype.insertLine=function(_f1){
_f1=(_f1===undefined)?1:_f1;
if(_f1>0){
var _f2=this._scrollRegion;
if(_f2.left==0&&_f2.right==this._width-1){
if(this._row+_f1>_f2.bottom){
_f1=_f2.bottom-this._row+1;
}
if(_f1==this._height){
this.clear();
}else{
var _f3=this._lines.splice(_f2.bottom-_f1+1,_f1);
for(var i=0;i<_f1;i++){
_f3[i].clear();
}
if(this._row==0){
this._lines=_f3.concat(this._lines);
}else{
for(var i=0;i<_f1;i++){
this._lines.splice(this._row+i,0,_f3[i]);
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
var _f4=this._positions.pop();
this._row=_f4[0];
this._column=_f4[1];
}
};
Term.prototype.processCharacters=function(_f5){
if(isString(_f5)&&_f5.length>0){
this._parser.parse(_f5);
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
var _f6=[];
var _f7=null;
var _f8=this._title+" — "+this._width+"x"+this._height;
var _f9="<div class='title'>"+_f8+"</div>";
for(var row=0;row<this._height;row++){
var _fa=this._lines[row];
var _fb=(this._cursorVisible)?(row==this._row)?this._column:-1:-1;
var _fc=_fa.getHTMLInfo(_f7,_fb);
_f7=_fc.attribute;
_f6.push(_fc.html);
}
if(_f7!=null){
_f6[_f6.length-1]+=_f7.getEndingHTML();
}
if(this._showTitle){
this._termNode.innerHTML=_f9+_f6.join("<br />");
}else{
this._termNode.innerHTML=_f6.join("<br />");
}
};
Term.prototype.scrollDown=function(_fd){
_fd=(_fd===undefined)?1:_fd;
if(_fd>0){
var _fe=this._scrollRegion;
if(_fe.left==0&&_fe.right==this._width-1){
var _ff=_fe.bottom-_fe.top+1;
if(_fd>=_ff){
this.clear();
}else{
var _100=this._lines.splice(_fe.bottom-_fd+1,_fd);
for(var i=0;i<_fd;i++){
_100[i].clear();
}
if(_fe.top==0){
this._lines=_100.concat(this._lines);
}else{
for(var i=0;i<_fd;i++){
this._lines.splice(_fe.top+i,0,_100[i]);
}
}
}
}else{
}
}
};
Term.prototype.scrollUp=function(_101){
_101=(_101===undefined)?1:_101;
if(_101>0){
var _102=this._scrollRegion;
if(_102.left==0&&_102.right==this._width-1){
var _103=_102.bottom-_102.top+1;
if(_101>=_103){
this.clear();
}else{
var _104=this._lines.splice(_102.top,_101);
for(var i=0;i<_101;i++){
_104[i].clear();
}
if(_102.bottom+1==this.height){
this._lines=this._lines.concat(_104);
}else{
for(var i=0;i<_101;i++){
this._lines.splice(_102.bottom-_101+i+1,0,_104[i]);
}
}
}
}else{
}
}
};
Term.prototype.setApplicationKeys=function(_105){
if(isBoolean(_105)){
this._keyHandler.setApplicationKeys(_105);
}
};
Term.prototype.setColumn=function(_106){
if(isNumber(_106)&&0<=_106&&_106<this._width){
this._column=_106;
}
};
Term.prototype.setCurrentAttribute=function(attr){
if(isDefined(attr)&&attr.constructor===Attribute){
this._currentAttribute=attr;
}
};
Term.prototype.setCursorVisible=function(_107){
if(isBoolean(_107)){
this._cursorVisible=_107;
}
};
Term.prototype.setHeight=function(_108){
this.setSize(this._width,_108);
};
Term.prototype.setPosition=function(row,_109){
if(isNumber(row)&&0<=row&&row<this._height){
this._row=row;
}
if(isNumber(_109)&&0<=_109&&_109<this._width){
this._column=_109;
}
};
Term.prototype.setRow=function(row){
if(0<=row&&row<this._height){
this._row=row;
}
};
Term.prototype.setScrollRegion=function(top,left,_10a,_10b){
if(isNumber(top)&&isNumber(left)&&isNumber(_10a)&&isNumber(_10b)){
if(top<_10a&&left<_10b){
var _10c=(0<=top&&top<this._height);
var _10d=(0<=left&&left<this._width);
var _10e=(0<=_10a&&_10a<this._height);
var _10f=(0<=_10b&&_10b<this._width);
if(_10c&&_10d&&_10e&&_10f){
this._scrollRegion={top:top,left:left,bottom:_10a,right:_10b};
}
}
}
};
Term.prototype.setSize=function(_110,_111){
var _112=false;
if(isNumber(_110)&&Line.MIN_WIDTH<=_110&&_110<=Line.MAX_WIDTH&&this._width!=_110){
for(var i=0;i<this._height;i++){
this._lines[i].resize(_110);
}
this._width=_110;
this._column=Math.min(this._width-1,this._column);
_112=true;
}
if(isNumber(_111)&&Term.MIN_HEIGHT<=_111&&_111<=Term.MAX_HEIGHT&&this._height!=_111){
if(_111>this._height){
for(var i=this._height;i<_111;i++){
this._lines.push(new Line(this._width));
}
}else{
this._lines=this._lines.splice(this._height-_111,_111);
}
this._height=_111;
this._row=Math.min(this._height-1,this._row);
_112=true;
}
if(_112){
this.setScrollRegion(0,0,this._height-1,this._width-1);
if(this._sendResizeSequence){
var ESC=String.fromCharCode(27);
var CSI=ESC+"[";
this._keyHandler.addKeys(CSI+[8,this._height,this._width].join(";")+"t");
}
}
};
Term.prototype.setTitle=function(_113){
this._title=_113;
if(isFunction(this._onTitleChange)){
this._onTitleChange(_113);
}
};
Term.prototype.showTitle=function(_114){
if(isBoolean(_114)){
this._showTitle=_114;
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
Term.prototype.setWidth=function(_115){
this.setSize(_115,this._height);
};
Term.prototype.toString=function(){
var _116=[];
for(var i=0;i<this._lines.length;i++){
_116.push(this._lines[i].toString());
}
return _116.join("\n");
};

