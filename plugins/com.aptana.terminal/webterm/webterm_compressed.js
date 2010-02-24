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
_11.selected=this.selected;
return _11;
};
Attribute.prototype.equals=function(_12){
var _13=false;
if(_12 instanceof Attribute){
_13=this===_12||(this.foreground==_12.foreground&&this.background==_12.background&&this.bold==_12.bold&&this.italic==_12.italic&&this.underline==_12.underline&&this.inverse==_12.inverse&&this.strikethrough==_12.strikethrough&&this.blink==_12.blink&&this.selected==_12.selected);
}
return _13;
};
Attribute.prototype.getStartingHTML=function(){
var _14=[];
var _15=(isNumber(this.background))?this.background:Attribute.DEFAULT_BACKGROUND;
var _16=(isNumber(this.foreground))?this.foreground:Attribute.DEFAULT_FOREGROUND;
if(this.inverse){
_14.push("f"+_15);
_14.push("b"+((this.selected)?"s":_16));
}else{
_14.push("f"+_16);
_14.push("b"+((this.selected)?"s":_15));
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
this.selected=false;
};
Attribute.prototype.resetBackground=function(){
this.background=Attribute.DEFAULT_BACKGROUND;
};
Attribute.prototype.resetForeground=function(){
this.foreground=Attribute.DEFAULT_FOREGROUND;
};
function Range(_17,_18){
if(isNumber(_17)===false){
_17=0;
}
if(isNumber(_18)===false){
_18=0;
}
this.startingOffset=Math.min(_17,_18);
this.endingOffset=Math.max(_17,_18);
};
Range.prototype.clamp=function(_19){
var _1a;
if(this.isOverlapping(_19)){
_1a=new Range(Math.max(this.startingOffset,_19.startingOffset),Math.min(this.endingOffset,_19.endingOffset));
}else{
_1a=new Range(0,0);
}
return _1a;
};
Range.prototype.contains=function(_1b){
return this.startingOffset<=_1b&&_1b<this.endingOffset;
};
Range.prototype.isEmpty=function(){
return this.startingOffset===this.endingOffset;
};
Range.prototype.isOverlapping=function(_1c){
var _1d=this.startingOffset;
var _1e=_1c.startingOffset;
var _1f=this.endingOffset-1;
var _20=_1c.endingOffset-1;
return (_1e<=_1d&&_1d<=_20||_1e<=_1f&&_1f<=_20||_1d<=_1e&&_1e<=_1f||_1d<=_20&&_20<=_1f);
};
Range.prototype.merge=function(_21){
return new Range(Math.min(this.startingOffset,_21.startingOffset),Math.max(this.endingOffset,_21.endingOffset));
};
Range.prototype.move=function(_22){
return new Range(this.startingOffset+_22,this.endingOffset+_22);
};
Line.DEFAULT_WIDTH=80;
Line.MIN_WIDTH=20;
Line.MAX_WIDTH=512;
function Line(_23){
if(isNumber(_23)){
_23=clamp(_23,Line.MIN_WIDTH,Line.MAX_WIDTH);
}else{
_23=Line.DEFAULT_WIDTH;
}
this._chars=new Array(_23);
this._attributes=new Array(_23);
this.clear();
};
Line.prototype.clear=function(ch){
ch=(isCharacter(ch))?ch.charAt(0):" ";
for(var i=0;i<this._chars.length;i++){
this._chars[i]=ch;
this._attributes[i]=new Attribute();
}
};
Line.prototype.clearLeft=function(_24){
if(isNumber(_24)&&0<=_24&&_24<this._chars.length){
for(var i=0;i<=_24;i++){
this._chars[i]=" ";
this._attributes[i]=new Attribute();
}
}
};
Line.prototype.clearRight=function(_25){
if(isNumber(_25)&&0<=_25&&_25<this._chars.length){
for(var i=_25;i<this._chars.length;i++){
this._chars[i]=" ";
this._attributes[i]=new Attribute();
}
}
};
Line.prototype.clearSelection=function(){
var _26=this._attributes;
var _27=_26.length;
for(var i=0;i<_27;i++){
_26[i].selected=false;
}
};
Line.prototype.deleteCharacter=function(_28,_29){
if(isNumber(_28)){
var _2a=this._chars.length;
_29=(isNumber(_29))?_29:1;
if(_29>0&&0<=_28&&_28<_2a){
if(_28+_29>_2a){
_29=_2a-_28;
}
this._chars.splice(_28,_29);
this._attributes.splice(_28,_29);
for(var i=0;i<_29;i++){
this._chars.push(" ");
this._attributes.push(new Attribute());
}
}
}
};
Line.prototype.getHTMLInfo=function(_2b,_2c){
var _2d=[];
for(var i=0;i<this._chars.length;i++){
var ch=this._chars[i];
var _2e=this._attributes[i];
if(_2e&&_2e.equals(_2b)==false){
if(_2b!==null){
_2d.push(_2b.getEndingHTML());
}
_2d.push(_2e.getStartingHTML());
_2b=_2e;
}
if(i===_2c){
_2d.push("<span class=\"cursor\">");
}
switch(ch){
case "&":
_2d.push("&amp;");
break;
case "<":
_2d.push("&lt;");
break;
case ">":
_2d.push("&gt;");
break;
case " ":
_2d.push("&nbsp;");
break;
default:
_2d.push(ch);
break;
}
if(i===_2c){
_2d.push("</span>");
}
}
return {html:_2d.join(""),attribute:_2b};
};
Line.prototype.getLastNonWhiteOffset=function(){
var _2f=0;
var _30=this._chars.length;
for(var i=_30-1;i>=0;i--){
if(this._chars[i].match(/\S/)){
_2f=i;
break;
}
}
return _2f;
};
Line.prototype.getSelectedText=function(){
var _31=this._chars;
var _32=this._attributes;
var _33=Math.min(this.getLastNonWhiteOffset(),_32.length);
var _34=null;
for(var i=0;i<_33;i++){
if(_32[i].selected){
if(_34===null){
_34=[];
}
_34.push(_31[i]);
}
}
return (_34!==null)?_34.join(""):null;
};
Line.prototype.getWidth=function(){
return this._chars.length;
};
Line.prototype.insertCharacter=function(ch,_35,_36){
if(isCharacter(ch)&&isNumber(_35)){
var _37=this._chars.length;
_36=(isNumber(_36))?_36:1;
if(_36>0&&0<=_35&&_35<_37){
ch=ch.charAt(0);
if(_35+_36>_37){
_36=_37-_35;
}
this._chars.splice(_37-_36,_36);
this._attributes.splice(_37-_36,_36);
var _38=new Array(_36);
var _39=new Array(_36);
for(var i=0;i<_36;i++){
this._chars.splice(_35+i,0,ch);
this._attributes.splice(_35+i,0,new Attribute());
}
}
}
};
Line.prototype.putCharacter=function(ch,_3a,_3b){
if(isCharacter(ch)&&isDefined(_3a)&&_3a.constructor==Attribute&&isNumber(_3b)){
if(0<=_3b&&_3b<this._chars.length){
this._chars[_3b]=ch.charAt(0);
this._attributes[_3b]=_3a;
}
}
};
Line.prototype.resize=function(_3c){
if(isNumber(_3c)){
var _3d=this._chars.length;
if(Line.MIN_WIDTH<=_3c&&_3c<=Line.MAX_WIDTH&&_3d!=_3c){
this._chars.length=_3c;
if(_3c>_3d){
for(var i=_3d;i<_3c;i++){
this._chars[i]=" ";
this._attributes[i]=new Attribute();
}
}
}
}
};
Line.prototype.select=function(_3e,_3f){
var _40=(_3f)?this._chars.length:this.getLastNonWhiteOffset();
var _41=new Range(0,_40);
var _3e=_3e.clamp(_41);
var _42=this._attributes;
var _40=_3e.endingOffset;
var _43=false;
for(var i=_3e.startingOffset;i<_40;i++){
var _44=_42[i].copy();
_44.selected=true;
_42[i]=_44;
_43=true;
}
return _43;
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
var _45=this;
this._queue=[];
this._applicationKeys=false;
this._playbackState=KeyHandler.STOPPED;
this.clearEvents();
this._playbackID=null;
document.onkeypress=function(e){
return _45.processKeyPress(e);
};
document.onkeydown=function(e){
return _45.processKeyDown(e);
};
};
KeyHandler.prototype.addEvent=function(_46,e){
if(e){
var _47={};
switch(_46){
case KeyHandler.KEY_DOWN:
_47.keyCode=e.keyCode;
_47.ctrlKey=e.ctrlKey;
_47.altKey=e.altKey;
_47.shiftKey=e.shiftKey;
break;
case KeyHandler.KEY_PRESS:
_47.keyCode=e.keyCode;
_47.which=e.which;
_47.ctrlKey=e.ctrlKey;
_47.altKey=e.altKey;
_47.metaKey=e.metaKey;
break;
default:
return;
}
this._events.keys.push({time:new Date().getTime(),type:_46,event:_47});
}
};
KeyHandler.prototype.addKeys=function(_48){
this._queue.push(_48);
if(isDefined(this.callback)){
this.callback(true);
}
};
KeyHandler.prototype.clearEvents=function(){
this._events={user_agent:{browser:BrowserDetect.browser,version:BrowserDetect.version,os:BrowserDetect.OS},keys:[]};
};
KeyHandler.prototype.dequeueAll=function(){
var _49=this._queue.join("");
this._queue.length=0;
return _49;
};
KeyHandler.prototype.hasContent=function(){
return this._queue.length>0;
};
KeyHandler.prototype.play=function(_4a){
if(this._playbackState!=KeyHandler.PLAYING){
this._playbackState=KeyHandler.PLAYING;
_4a=_4a||this._events.keys;
var _4b=this;
var i=0;
var _4c=function(){
var _4d=_4a[i++];
switch(_4d.type){
case KeyHandler.KEY_DOWN:
_4b.processKeyDown(_4d.event);
break;
case KeyHandler.KEY_PRESS:
_4b.processKeyPress(_4d.event);
break;
default:
break;
}
if(_4b._playbackState==KeyHandler.PLAYING&&i<_4a.length){
var _4e=clamp(_4a[i].time-_4d.time,0,1000);
this._playbackID=window.setTimeout(_4c,_4e);
}
};
_4c();
}
};
KeyHandler.prototype.processKeyDown=function(e){
if(!e){
e=window.event;
}
if(this._playbackState==KeyHandler.RECORDING){
this.addEvent(KeyHandler.KEY_DOWN,e);
}
var _4f=e.keyCode;
var _50=null;
var _51=this._applicationKeys;
if(BrowserDetect.browser=="Firefox"&&(e.keyCode==8||(37<=e.keyCode&&e.keyCode<=40))){
}else{
switch(_4f){
case 8:
_50=KeyHandler.BACKSPACE;
break;
case 9:
_50=KeyHandler.TAB;
break;
case 27:
_50=KeyHandler.ESCAPE;
break;
case 33:
_50=KeyHandler.PAGE_UP;
break;
case 34:
_50=KeyHandler.PAGE_DOWN;
break;
case 35:
_50=(_51)?KeyHandler.APP_END:KeyHandler.END;
break;
case 36:
_50=(_51)?KeyHandler.APP_HOME:KeyHandler.HOME;
break;
case 37:
_50=(_51)?KeyHandler.APP_LEFT:KeyHandler.LEFT;
break;
case 38:
_50=(_51)?KeyHandler.APP_UP:KeyHandler.UP;
break;
case 39:
_50=(_51)?KeyHandler.APP_RIGHT:KeyHandler.RIGHT;
break;
case 40:
_50=(_51)?KeyHandler.APP_DOWN:KeyHandler.DOWN;
break;
case 45:
_50=KeyHandler.INSERT;
break;
case 46:
_50=KeyHandler.DELETE;
break;
case 112:
_50=e.shiftKey?KeyHandler.F13:KeyHandler.F1;
break;
case 113:
_50=e.shiftKey?KeyHandler.F14:KeyHandler.F2;
break;
case 114:
_50=e.shiftKey?KeyHandler.F15:KeyHandler.F3;
break;
case 115:
_50=e.shiftKey?KeyHandler.F16:KeyHandler.F4;
break;
case 116:
_50=e.shiftKey?KeyHandler.F17:KeyHandler.F5;
break;
case 117:
_50=e.shiftKey?KeyHandler.F18:KeyHandler.F6;
break;
case 118:
_50=e.shiftKey?KeyHandler.F19:KeyHandler.F7;
break;
case 119:
_50=e.shiftKey?KeyHandler.F20:KeyHandler.F8;
break;
case 120:
_50=KeyHandler.F9;
break;
case 121:
_50=KeyHandler.F10;
break;
case 122:
_50=KeyHandler.F11;
break;
case 123:
_50=KeyHandler.F12;
break;
default:
if(!e.ctrlKey||(e.ctrlKey&&e.altKey)||(e.keyCode==17)){
}else{
if(e.shiftKey){
switch(_4f){
case 50:
_50=String.fromCharCode(0);
break;
case 54:
_50=String.fromCharCode(30);
break;
case 94:
_50=String.fromCharCode(30);
break;
case 109:
_50=String.fromCharCode(31);
break;
default:
break;
}
}else{
switch(_4f){
case 32:
_50=String.fromCharCode(0);
break;
case 190:
_50=String.fromCharCode(30);
break;
case 219:
_50=String.fromCharCode(27);
break;
case 220:
_50=String.fromCharCode(28);
break;
case 221:
_50=String.fromCharCode(29);
break;
default:
if(65<=_4f&&_4f<=90){
_50=String.fromCharCode(_4f-64);
}
break;
}
}
}
break;
}
}
if(_50!==null){
this.addKeys(_50);
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
var _52=this._applicationKeys;
var _53=null;
switch(e.keyCode){
case 8:
_53=KeyHandler.BACKSPACE;
break;
case 37:
_53=(_52)?KeyHandler.APP_LEFT:KeyHandler.LEFT;
break;
case 38:
_53=(_52)?KeyHandler.APP_UP:KeyHandler.UP;
break;
case 39:
_53=(_52)?KeyHandler.APP_RIGHT:KeyHandler.RIGHT;
break;
case 40:
_53=(_52)?KeyHandler.APP_DOWN:KeyHandler.DOWN;
break;
}
if(_53!==null){
this.addKeys(_53);
}
}else{
if((e.ctrlKey&&!e.altKey)||(e.which==0)||(e.keyCode==8)||(e.keyCode==16)){
}else{
var _54;
if(e.keyCode){
_54=e.keyCode;
}
if(e.which){
_54=e.which;
}
if(e.altKey&&!e.ctrlKey){
this.addKeys(KeyHandler.ESCAPE);
}
this.addKeys(String.fromCharCode(_54));
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
KeyHandler.prototype.setApplicationKeys=function(_55){
if(isBoolean(_55)){
this._applicationKeys=_55;
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
function XTermHandler(_56){
this._term=_56;
this._insertMode=false;
this._missingCommands={};
};
XTermHandler.prototype.BEL=function(_57,_58){
};
XTermHandler.prototype.BS=function(_59,_5a){
var col=this._term.getColumn()-1;
col=Math.max(0,col);
this._term.setColumn(col);
};
XTermHandler.prototype.CHA=function(_5b,_5c){
var _5d=0;
if(_5c.length>0){
_5d=_5c-1;
}
this._term.setColumn(_5d);
};
XTermHandler.prototype.CR=function(_5e,_5f){
this._term.setColumn(0);
};
XTermHandler.prototype.CUB=function(_60,_61){
var _62=1;
if(_61.length>0){
_62=_61-0;
if(_62==0){
_62=1;
}
}
var col=this._term.getColumn()-_62;
col=Math.max(0,col);
this._term.setColumn(col);
};
XTermHandler.prototype.CUD=function(_63,_64){
var _65=1;
if(_64.length>0){
_65=_64-0;
if(_65==0){
_65=1;
}
}
var _66=this._term.getRow();
var _67=this._term.getScrollRegion().bottom;
var _68;
if(_66<=_67){
_68=Math.min(_66+_65,_67);
}else{
_68=Math.min(_66+_65,this._term.getHeight()-1);
}
this._term.setRow(_68);
};
XTermHandler.prototype.CUF=function(_69,_6a){
var _6b=1;
if(_6a.length>0){
_6b=_6a-0;
if(_6b==0){
_6b=1;
}
}
var col=this._term.getColumn()+_6b;
col=Math.min(col,this._term.getWidth()-1);
this._term.setColumn(col);
};
XTermHandler.prototype.CUP=function(_6c,_6d){
var row=0;
var col=0;
var _6e=this._term.getHeight();
if(_6d.length>0){
var _6f=_6d.split(/;/);
var row=_6f[0]-1;
var col=_6f[1]-1;
}
if(row>=_6e){
var _70=_6e-row;
row=_6e-1;
this._term.scrollUp(_70);
}
this._term.setPosition(row,col);
};
XTermHandler.prototype.CUU=function(_71,_72){
var _73=1;
if(_72.length>0){
_73=_72-0;
if(_73==0){
_73=1;
}
}
var _74=this._term.getRow();
var _75=this._term.getScrollRegion().top;
var _76;
if(_75<=_74){
_76=Math.max(_75,_74-_73);
}else{
_76=Math.max(0,_74-_73);
}
this._term.setRow(_76);
};
XTermHandler.prototype.DCH=function(_77,_78){
var _79=_78-0;
this._term.deleteCharacter(_79);
};
XTermHandler.prototype.DECALN=function(_7a,_7b){
this._term.clear("E");
};
XTermHandler.prototype.DECRC=function(_7c,_7d){
this._term.popPosition();
};
XTermHandler.prototype.DECPAM=function(_7e,_7f){
this._term.setApplicationKeys(true);
};
XTermHandler.prototype.DECPNM=function(_80,_81){
this._term.setApplicationKeys(false);
};
XTermHandler.prototype.DECRST=function(_82,_83){
var _84=_83-0;
switch(_84){
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
this.genericHandler(_82,_83);
break;
}
};
XTermHandler.prototype.DECSC=function(_85,_86){
this._term.pushPosition();
};
XTermHandler.prototype.DECSET=function(_87,_88){
var _89=_88-0;
switch(_89){
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
this.genericHandler(_87,_88);
break;
}
};
XTermHandler.prototype.DECSTBM=function(_8a,_8b){
var _8c=_8b.split(/;/);
var top=_8c[0]-1;
var _8d=_8c[1]-1;
this._term.setScrollRegion(top,0,_8d,this._term.getWidth()-1);
};
XTermHandler.prototype.DL=function(_8e,_8f){
var _90=1;
if(_8f.length>0){
_90=_8f-0;
if(_90==0){
_90=1;
}
}
this._term.deleteLine(_90);
};
XTermHandler.prototype.ED=function(_91,_92){
var _93=_92-0;
switch(_93){
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
this.genericHandler(_91+":"+_92,"");
break;
}
};
XTermHandler.prototype.EL=function(_94,_95){
var _96=_95-0;
switch(_96){
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
this.genericHandler(_94+":"+_95,"");
break;
}
};
XTermHandler.prototype.genericHandler=function(_97,_98){
if(this._missingCommands.hasOwnProperty(_97)===false){
this._missingCommands[_97]=0;
}
this._missingCommands[_97]++;
};
XTermHandler.prototype.getMissingCommands=function(){
return this._missingCommands;
};
XTermHandler.prototype.HVP=XTermHandler.prototype.CUP;
XTermHandler.prototype.ICH=function(_99,_9a){
var _9b=_9a-0;
this._term.insertCharacter(" ",_9b);
};
XTermHandler.prototype.IL=function(_9c,_9d){
var _9e=1;
if(_9d.length>0){
_9e=_9d-0;
if(_9e==0){
_9e=1;
}
}
this._term.insertLine(_9e);
};
XTermHandler.prototype.IND=function(_9f,_a0){
var _a1=this._term.getRow();
var _a2=this._term.getScrollRegion().bottom;
var _a3=_a1+1;
if(_a1<=_a2){
this._term.setRow(_a3);
}else{
this._term.scrollUp(1);
this._term.setRow(_a2);
}
};
XTermHandler.prototype.LF=function(_a4,_a5){
var _a6=this._term;
var row=_a6.getRow()+1;
var _a7=_a6.getScrollRegion().bottom;
if(row>_a7){
_a6.scrollUp();
row=_a7;
}
_a6.setPosition(row,0);
};
XTermHandler.prototype.NEL=XTermHandler.prototype.LF;
XTermHandler.prototype.processCharacter=function(_a8,_a9){
if(this._insertMode){
this._term.insertCharacter(" ",1);
}
this._term.displayCharacters(_a9);
};
XTermHandler.prototype.RI=function(_aa,_ab){
var _ac=this._term.getRow();
var _ad=this._term.getScrollRegion().top;
var _ae=_ac-1;
if(_ad<=_ae){
this._term.setRow(_ae);
}else{
this._term.scrollDown(1);
this._term.setRow(_ad);
}
};
XTermHandler.prototype.RM=function(_af,_b0){
var _b1=_b0-0;
switch(_b1){
case 4:
this._insertMode=false;
break;
case 2:
case 12:
case 20:
default:
this.genericHandler(_af,_b0);
break;
}
};
XTermHandler.prototype.SD=function(_b2,_b3){
var _b4=1;
if(_b3.length>0){
_b4=_b3-0;
}
var _b5=this._term.getRow();
var _b6=this._term.getScrollRegion().top;
var _b7=_b5-_b4;
if(_b6<=_b7){
this._term.setRow(_b7);
}else{
this._term.scrollDown(_b4);
this._term.setRow(_b6);
}
};
XTermHandler.prototype.SET_TEXT_PARAMS=function(_b8,_b9){
var _ba=_b9.split(/;/);
var _bb=_ba[0]-0;
var _bc=_ba[1];
if(_bb==0){
this._term.setTitle(_bc);
}else{
this.genericHandler(_b8+":"+_b9,"");
}
};
XTermHandler.prototype.SET_TEXT_PARAMS2=XTermHandler.prototype.SET_TEXT_PARAMS;
XTermHandler.prototype.SGR=function(_bd,_be){
var _bf=this._term.getCurrentAttribute();
var _c0=_be.split(/;/);
for(var i=0;i<_c0.length;i++){
var _c1=_c0[i]-0;
if(_c1<50){
var _c2=Math.floor(_c1/10);
var _c3=_c1%10;
switch(_c2){
case 0:
switch(_c3){
case 0:
_bf.reset();
break;
case 1:
_bf.bold=true;
break;
case 3:
_bf.italic=true;
break;
case 4:
_bf.underline=true;
break;
case 7:
_bf.inverse=true;
break;
case 9:
_bf.strikethrough=true;
break;
default:
this.genericHandler(_bd+":"+_be,"");
break;
}
break;
case 2:
switch(_c3){
case 2:
_bf.bold=false;
break;
case 3:
_bf.italic=false;
break;
case 4:
_bf.underline=false;
break;
case 7:
_bf.inverse=false;
break;
case 9:
_bf.strikethough=false;
break;
default:
this.genericHandler(_bd+":"+_be,"");
break;
}
break;
case 3:
switch(_c3){
case 0:
case 1:
case 2:
case 3:
case 4:
case 5:
case 6:
case 7:
_bf.foreground=_c3;
break;
case 9:
_bf.resetForeground();
break;
default:
this.genericHandler(_bd+":"+_be,"");
break;
}
break;
case 4:
switch(_c3){
case 0:
case 1:
case 2:
case 3:
case 4:
case 5:
case 6:
case 7:
_bf.background=_c3;
break;
case 9:
_bf.resetBackground();
break;
default:
this.genericHandler(_bd+":"+_be,"");
break;
}
break;
default:
this.genericHandler(_bd+":"+_be,"");
break;
}
}else{
this.genericHandler(_bd+":"+_be,"");
}
}
this._term.setCurrentAttribute(_bf);
};
XTermHandler.prototype.SM=function(_c4,_c5){
var _c6=_c5-0;
switch(_c6){
case 4:
this._insertMode=true;
break;
case 2:
case 12:
case 20:
default:
this.genericHandler(_c4,_c5);
break;
}
};
XTermHandler.prototype.SU=function(_c7,_c8){
var _c9=1;
if(_c8.length>0){
_c9=_c8-0;
}
var _ca=this._term.getRow();
var _cb=this._term.getScrollRegion().bottom;
var _cc=_ca+_c9;
if(_ca<=_cb){
this._term.setRow(_cc);
}else{
this._term.scrollUp(_c9);
this._term.setRow(_cb);
}
};
XTermHandler.prototype.TAB=function(_cd,_ce){
var _cf=this._term.getColumn();
var _d0=8-(_cf%8);
this._term.displayCharacters(new Array(_d0+1).join(" "));
};
XTermHandler.prototype.VPA=function(_d1,_d2){
var row=0;
if(_d2.length>0){
row=_d2-1;
}
this._term.setRow(row);
};
XTermHandler.prototype.VT=XTermHandler.prototype.LF;
function TermParser(_d3,_d4){
if(_d3===null||_d3===undefined){
throw new Error("Parsing tables must be defined when creating a new TermParser");
}
this._processTables(_d3);
this._actions=_d3.actions;
this._nodes=_d3.nodes;
this.setHandler(_d4);
this.singleStep=false;
this.offset=-1;
};
TermParser.prototype.getHandler=function(){
return this._handler;
};
TermParser.prototype.parse=function(_d5){
var _d6=0;
var _d7=isString(_d5)?_d5.length:0;
while(_d6<_d7){
var _d8=0;
var _d9=this._nodes[_d8][1];
var _da=(_d9==-1)?-2:_d6;
for(var i=_d6;i<_d7;i++){
var _db=this._nodes[_d8];
if(_db){
var _dc=_d5.charCodeAt(i);
var _dd=_db[0][_dc];
if(_dd!=-1){
_d8=_dd;
var _de=this._nodes[_d8][1];
if(_de!=-1){
_da=i;
_d9=_de;
}
}else{
break;
}
}
}
if(_d9==-1){
if(this._handler!=null){
if(this._handler.processCharacter){
this._handler.processCharacter("processCharacter",_d5.charAt(_d6));
}
}
_d6++;
}else{
var _df=_da+1;
if(this._handler!=null){
var _e0=this._actions[_d9];
var _e1=_e0[0];
var _e2="";
if(_e0.length>=3&&_e0[1]!=-1&&_e0[2]!=-1){
_e2=_d5.substring(_d6+_e0[1],_df-_e0[2]);
}
this._handler[_e1](_e1,_e2);
}
_d6=_df;
if(this.singleStep){
this.offset=_d6;
break;
}
}
}
};
TermParser.prototype._processTables=function(_e3){
if(_e3.hasOwnProperty("processed")==false||_e3.processed==false){
switch(_e3.format){
case "expanded":
break;
case "rle":
var mos=new Array(256);
for(var i=0;i<mos.length;i++){
mos[i]=-1;
}
var _e4=_e3.nodes;
for(var i=0;i<_e4.length;i++){
var _e5=_e4[i][0];
var _e6=[];
for(var j=0;j<_e5.length;j++){
var _e7=_e5[j];
if(_e7<0){
_e6=_e6.concat(mos.slice(0,-_e7));
}else{
var _e8=_e7>>8;
var _e9=(_e7&255)+1;
for(var k=0;k<_e9;k++){
_e6.push(_e8);
}
}
}
_e4[i][0]=_e6;
}
break;
default:
break;
}
_e3.processed=true;
}
};
TermParser.prototype.setHandler=function(_ea){
var _eb=null;
if(_ea){
var _ec=null;
var _ed=function(_ee,_ef){
};
for(var i=0;i<this._actions.length;i++){
var _f0=this._actions[i];
var _f1=_f0[0];
if(!_ea[_f1]){
if(_eb==null){
_eb=protectedClone(_ea);
if(!_ea.genericHandler){
_ec=_ed;
}else{
_ec=_ea.genericHandler;
}
}
_eb[_f1]=_ec;
}
}
}
if(_eb==null){
this._handler=_ea;
}else{
this._handler=_eb;
}
};
TermComm.POLLING_INTERVAL_MIN=125;
TermComm.POLLING_INTERVAL_MAX=2000;
TermComm.POLLING_GROWTH_RATE=2;
TermComm.DEFAULT_REQUEST_URL="/stream";
TermComm.DEFAULT_GET_UNIQUE_ID_URL="/id";
function TermComm(_f2,_f3){
var _f4=this;
this.terminal=_f2;
this.keyHandler=_f2.getKeyHandler();
this.keyHandler.callback=function(){
_f4.sendKeys();
};
this.minInterval=125;
this.maxInterval=2000;
this.growthRate=2;
this.timeoutInterval=5000;
this.requestURL=TermComm.DEFAULT_REQUEST_URL;
this.getUniqueIdURL=TermComm.DEFAULT_GET_UNIQUE_ID_URL;
if(isDefined(_f3)){
if(_f3.hasOwnProperty("minInterval")&&isNumber(_f3.minInterval)){
this.minInterval=_f3.minInterval;
}
if(_f3.hasOwnProperty("maxInterval")&&isNumber(_f3.maxInterval)){
this.maxInterval=_f3.maxInterval;
}
if(_f3.hasOwnProperty("growthRate")&&isNumber(_f3.growthRate)){
this.growthRate=_f3.growthRate;
}
if(_f3.hasOwnProperty("timeoutInterval")&&isNumber(_f3.timeoutInterval)){
this.timeoutInterval=_f3.timeoutInterval;
}
if(_f3.hasOwnProperty("requestURL")&&isString(_f3.requestURL)&&_f3.requestURL.length>0){
this.requestURL=_f3.requestURL;
}
if(_f3.hasOwnProperty("getUniqueIdURL")&&isString(_f3.getUniqueIdURL)&&_f3.getUniqueIdURL.length>0){
this.getUniqueIdURL=_f3.getUniqueIdURL;
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
var _f5=this;
var req=createXHR();
var _f6={id:this.terminal.getId(),cb:new Date().getTime()+":"+this.cacheBusterID++};
req.open("GET",createURL(this.requestURL,_f6),true);
if(this.ie){
req.setRequestHeader("If-Modified-Since","Sat, 1 Jan 2000 00:00:00 GMT");
}
req.onreadystatechange=function(){
if(req.readyState==4){
if(_f5.watchdogID!==null){
window.clearTimeout(_f5.watchdogID);
_f5.watchdogID=null;
}
var _f7=req.responseText;
if(isString(_f7)&&_f7.length>0){
_f5.terminal.processCharacters(_f7);
_f5.pollingInterval=_f5.minInterval;
}else{
_f5.pollingInterval*=_f5.growthRate;
if(_f5.pollingInterval>_f5.maxInterval){
_f5.pollingInterval=_f5.maxInterval;
}
}
_f5.requestID=window.setTimeout(function(){
_f5.update();
},(this.updateQueued)?0:_f5.pollingInterval);
this.updateQueued=false;
}
};
this.watchdogID=window.setTimeout(function(){
_f5.timeout();
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
var _f8=this;
var req=createXHR();
var _f9={id:id};
req.open("POST",createURL(this.requestURL,_f9),true);
req.onreadystatechange=function(){
if(req.readyState==4){
_f8.sendingKeys=false;
_f8.update(true);
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
TermComm.prototype.update=function(_fa){
if(this.running&&this.terminal.getId()!==null){
if(isBoolean(_fa)){
if(this.requestID!==null){
window.clearTimeout(this.requestID);
this.requestID=null;
}
if(_fa){
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
function Term(id,_fb,_fc,_fd){
if(isString(id)===false||id.length===0){
id="terminal";
}
this._id=(_fd&&_fd.hasOwnProperty("id"))?_fd.id:null;
this._remainingText="";
this._rootNode=document.getElementById(id);
if(this._rootNode){
this._rootNode.className="webterm";
this._termNode=document.createElement("pre");
this._rootNode.appendChild(this._termNode);
this._width=(isNumber(_fb))?clamp(_fb,Line.MIN_WIDTH,Line.MAX_WIDTH):Line.DEFAULT_WIDTH;
this._height=(isNumber(_fc))?clamp(_fc,Term.MIN_HEIGHT,Term.MAX_HEIGHT):Term.DEFAULT_HEIGHT;
this._title="Aptana WebTerm";
this._row=0;
this._column=0;
this._scrollRegion={top:0,left:0,bottom:this._height-1,right:this._width-1};
this._cursorVisible=true;
this._buffers=[];
this._positions=[];
this._currentAttribute=new Attribute();
this._hasSelection=false;
this._sendResizeSequence=(_fd&&_fd.hasOwnProperty("sendResizeSequence"))?_fd.sendResizeSequence:true;
this._showTitle=(_fd&&_fd.hasOwnProperty("showTitle"))?_fd.showTitle:true;
this._onTitleChange=(_fd&&_fd.hasOwnProperty("onTitleChange"))?_fd.onTitleChange:null;
var _fe=(_fd&&_fd.hasOwnProperty("handler"))?_fd.handler:new XTermHandler(this);
var _ff=(_fd&&_fd.hasOwnProperty("tables"))?_fd.tables:XTermTables;
var _100=(_fd&&_fd.hasOwnProperty("parser"))?_fd.parser:new TermParser(_ff,_fe);
var _101=(_fd&&_fd.hasOwnProperty("keyHandler"))?_fd.keyHandler:new KeyHandler();
this._parser=_100;
this._keyHandler=_101;
var _102=(_fd&&_fd.hasOwnProperty("commHandler"))?_fd.commHandler:new TermComm(this,_fd);
var _103=(_fd&&_fd.hasOwnProperty("autoStart"))?_fd.autoStart:true;
this._commHandler=_102;
this.createBuffer();
this.refresh();
if(_103){
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
var _104=new Array(this._height);
for(var i=0;i<_104.length;i++){
_104[i]=new Line(this._width);
}
this._lines=_104;
};
Term.prototype.clearSelection=function(_105){
var _106=this._lines;
var _107=_106.length;
for(var i=0;i<_107;i++){
_106[i].clearSelection();
}
this._hasSelection=false;
if((isBoolean(_105))?_105:true){
this.refresh();
}
};
Term.prototype.deleteCharacter=function(_108){
this._lines[this._row].deleteCharacter(this._column,_108);
};
Term.prototype.deleteLine=function(_109){
_109=(_109===undefined)?1:_109;
if(_109>0){
var _10a=this._scrollRegion;
if(_10a.left==0&&_10a.right==this._width-1){
if(this._row+_109>_10a.bottom){
_109=_10a.bottom-this._row+1;
}
if(_109==this._height){
this.clear();
}else{
var _10b=this._lines.splice(this._row,_109);
for(var i=0;i<_109;i++){
_10b[i].clear();
}
if(_10a.bottom+1==this.height){
this._lines=this._lines.concat(_10b);
}else{
for(var i=0;i<_109;i++){
this._lines.splice(_10a.bottom-_109+i+1,0,_10b[i]);
}
}
}
}else{
}
}
};
Term.prototype.displayCharacters=function(_10c){
if(isString(_10c)){
for(var i=0;i<_10c.length;i++){
var ch=_10c.charAt(i);
var line=this._lines[this._row];
if(/[\x20-\x7F]+/.test(ch)==false){
ch=" ";
}
line.putCharacter(ch,this._currentAttribute,this._column);
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
Term.prototype.getSelectedText=function(){
var _10d=null;
if(this.hasSelection()){
var _10e=this._lines;
var _10f=_10e.length;
for(var i=0;i<_10f;i++){
var _110=_10e[i].getSelectedText();
if(_110!==null){
if(_10d===null){
_10d=[];
}
_10d.push(_110);
}
}
}
return (_10d!==null)?_10d.join("\n"):null;
};
Term.prototype.getTitle=function(){
return this._title;
};
Term.prototype.getWidth=function(){
return this._width;
};
Term.prototype.hasSelection=function(){
return this._hasSelection;
};
Term.prototype.insertCharacter=function(ch,_111){
this._lines[this._row].insertCharacter(ch,this._column,_111);
};
Term.prototype.insertLine=function(_112){
_112=(_112===undefined)?1:_112;
if(_112>0){
var _113=this._scrollRegion;
if(_113.left==0&&_113.right==this._width-1){
if(this._row+_112>_113.bottom){
_112=_113.bottom-this._row+1;
}
if(_112==this._height){
this.clear();
}else{
var _114=this._lines.splice(_113.bottom-_112+1,_112);
for(var i=0;i<_112;i++){
_114[i].clear();
}
if(this._row==0){
this._lines=_114.concat(this._lines);
}else{
for(var i=0;i<_112;i++){
this._lines.splice(this._row+i,0,_114[i]);
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
var _115=this._positions.pop();
this._row=_115[0];
this._column=_115[1];
}
};
Term.prototype.processCharacters=function(text){
if(isString(text)&&text.length>0){
this._parser.parse(text);
this.clearSelection();
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
var _116=[];
var attr=null;
var _117=this._title+" — "+this._width+"x"+this._height;
var _118="<div class='title'>"+_117+"</div>";
for(var row=0;row<this._height;row++){
var line=this._lines[row];
var _119=(this._cursorVisible)?(row==this._row)?this._column:-1:-1;
var _11a=line.getHTMLInfo(attr,_119);
attr=_11a.attribute;
_116.push(_11a.html);
}
if(attr!=null){
_116[_116.length-1]+=attr.getEndingHTML();
}
if(this._showTitle){
this._termNode.innerHTML=_118+_116.join("<br />");
}else{
this._termNode.innerHTML=_116.join("<br />");
}
};
Term.prototype.scrollDown=function(_11b){
_11b=(_11b===undefined)?1:_11b;
if(_11b>0){
var _11c=this._scrollRegion;
if(_11c.left==0&&_11c.right==this._width-1){
var _11d=_11c.bottom-_11c.top+1;
if(_11b>=_11d){
this.clear();
}else{
var _11e=this._lines.splice(_11c.bottom-_11b+1,_11b);
for(var i=0;i<_11b;i++){
_11e[i].clear();
}
if(_11c.top==0){
this._lines=_11e.concat(this._lines);
}else{
for(var i=0;i<_11b;i++){
this._lines.splice(_11c.top+i,0,_11e[i]);
}
}
}
}else{
}
}
};
Term.prototype.scrollUp=function(_11f){
_11f=(_11f===undefined)?1:_11f;
if(_11f>0){
var _120=this._scrollRegion;
if(_120.left==0&&_120.right==this._width-1){
var _121=_120.bottom-_120.top+1;
if(_11f>=_121){
this.clear();
}else{
var _122=this._lines.splice(_120.top,_11f);
for(var i=0;i<_11f;i++){
_122[i].clear();
}
if(_120.bottom+1==this.height){
this._lines=this._lines.concat(_122);
}else{
for(var i=0;i<_11f;i++){
this._lines.splice(_120.bottom-_11f+i+1,0,_122[i]);
}
}
}
}else{
}
}
};
Term.prototype.select=function(s,e){
var _123=this.getWidth();
var _124=this.getHeight();
var _125=new Range(s,e).clamp(new Range(0,_123*_124));
var _126=this.hasSelection();
var _127=false;
if(_126){
this.clearSelection(false);
}
if(_125.isEmpty()===false){
var _128=0;
for(var i=0;i<_124;i++){
var _129=_128+_123;
var _12a=new Range(_128,_129).clamp(_125);
if(_12a.isEmpty()===false){
var _12b=_125.endingOffset>_12a.endingOffset;
var _12c=_12a.move(-_128);
var line=this._lines[i];
if(line.select(_12c,_12b)){
_127=true;
}
}
_128=_129;
}
}
this._hasSelection=_127;
if(_126||_127){
this.refresh();
}
};
Term.prototype.setApplicationKeys=function(_12d){
if(isBoolean(_12d)){
this._keyHandler.setApplicationKeys(_12d);
}
};
Term.prototype.setColumn=function(_12e){
if(isNumber(_12e)&&0<=_12e&&_12e<this._width){
this._column=_12e;
}
};
Term.prototype.setCurrentAttribute=function(attr){
if(isDefined(attr)&&attr.constructor===Attribute){
this._currentAttribute=attr;
}
};
Term.prototype.setCursorVisible=function(_12f){
if(isBoolean(_12f)){
this._cursorVisible=_12f;
}
};
Term.prototype.setHeight=function(_130){
this.setSize(this._width,_130);
};
Term.prototype.setPosition=function(row,_131){
if(isNumber(row)&&0<=row&&row<this._height){
this._row=row;
}
if(isNumber(_131)&&0<=_131&&_131<this._width){
this._column=_131;
}
};
Term.prototype.setRow=function(row){
if(0<=row&&row<this._height){
this._row=row;
}
};
Term.prototype.setScrollRegion=function(top,left,_132,_133){
if(isNumber(top)&&isNumber(left)&&isNumber(_132)&&isNumber(_133)){
if(top<_132&&left<_133){
var _134=(0<=top&&top<this._height);
var _135=(0<=left&&left<this._width);
var _136=(0<=_132&&_132<this._height);
var _137=(0<=_133&&_133<this._width);
if(_134&&_135&&_136&&_137){
this._scrollRegion={top:top,left:left,bottom:_132,right:_133};
}
}
}
};
Term.prototype.setSize=function(_138,_139){
var _13a=false;
if(isNumber(_138)&&Line.MIN_WIDTH<=_138&&_138<=Line.MAX_WIDTH&&this._width!=_138){
for(var i=0;i<this._height;i++){
this._lines[i].resize(_138);
}
this._width=_138;
this._column=Math.min(this._width-1,this._column);
_13a=true;
}
if(isNumber(_139)&&Term.MIN_HEIGHT<=_139&&_139<=Term.MAX_HEIGHT&&this._height!=_139){
if(_139>this._height){
for(var i=this._height;i<_139;i++){
this._lines.push(new Line(this._width));
}
}else{
this._lines=this._lines.splice(this._height-_139,_139);
}
this._height=_139;
this._row=Math.min(this._height-1,this._row);
_13a=true;
}
if(_13a){
this.setScrollRegion(0,0,this._height-1,this._width-1);
if(this._sendResizeSequence){
var ESC=String.fromCharCode(27);
var CSI=ESC+"[";
this._keyHandler.addKeys(CSI+[8,this._height,this._width].join(";")+"t");
}
}
};
Term.prototype.setTitle=function(_13b){
this._title=_13b;
if(isFunction(this._onTitleChange)){
this._onTitleChange(_13b);
}
};
Term.prototype.showTitle=function(_13c){
if(isBoolean(_13c)){
this._showTitle=_13c;
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
Term.prototype.setWidth=function(_13d){
this.setSize(_13d,this._height);
};
Term.prototype.toString=function(){
var _13e=[];
for(var i=0;i<this._lines.length;i++){
_13e.push(this._lines[i].toString());
}
return _13e.join("\n");
};

