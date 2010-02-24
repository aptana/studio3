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
function dragger(_11,_12){
var _13=null,_14=null;
var _15,_16;
var _17=function(e){
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
var _18=function(e){
if(e){
e.cancelBubble=true;
if(e.stopPropagtion){
e.stopPropagation();
}
}
return true;
};
var _19=function(e){
if(isFunction(_12)){
var _1a=_1b(_11);
var _1c=_1a[0];
var _1d=_1a[1];
if(_13===null||_14===null){
_13=e.x-_1c;
_14=e.y-_1d;
}
_15=e.x-_1c;
_16=e.y-_1d;
_12(_13,_14,_15,_16);
}
return _17(e);
};
var _1b=function(obj){
var _1e=curtop=0;
if(obj.offsetParent){
do{
_1e+=obj.offsetLeft;
curtop+=obj.offsetTop;
}while(obj=obj.offsetParent);
}
return [_1e,curtop];
};
var _1f=function(e){
if(e.button!=0){
return _17(e);
}
window.focus();
_11.removeEventListener("mousedown",_1f);
_11.addEventListener("mousemove",_20,false);
_11.addEventListener("mouseup",_21,false);
_11.addEventListener("mouseout",_21,false);
return _19(e);
};
var _20=function(e){
return _19(e);
};
var _21=function(e){
_11.removeEventListener("mousemove",_20);
_11.removeEventListener("mouseup",_21);
_11.removeEventListener("mouseout",_21);
_11.addEventListener("mousedown",_1f,false);
var _22=_19(e);
_13=_14=_15=_16=null;
return _22;
};
if(isDefined(_11)){
_11.addEventListener("mousedown",_1f,false);
}
};
Attribute.DEFAULT_BACKGROUND="b";
Attribute.DEFAULT_FOREGROUND="f";
function Attribute(){
this.reset();
};
Attribute.prototype.copy=function(){
var _23=new Attribute();
_23.foreground=this.foreground;
_23.background=this.background;
_23.bold=this.bold;
_23.italic=this.italic;
_23.underline=this.underline;
_23.inverse=this.inverse;
_23.strikethrough=this.strikethrough;
_23.blink=this.blink;
_23.selected=this.selected;
return _23;
};
Attribute.prototype.equals=function(_24){
var _25=false;
if(_24 instanceof Attribute){
_25=this===_24||(this.foreground==_24.foreground&&this.background==_24.background&&this.bold==_24.bold&&this.italic==_24.italic&&this.underline==_24.underline&&this.inverse==_24.inverse&&this.strikethrough==_24.strikethrough&&this.blink==_24.blink&&this.selected==_24.selected);
}
return _25;
};
Attribute.prototype.getStartingHTML=function(){
var _26=[];
var _27=(isNumber(this.background))?this.background:Attribute.DEFAULT_BACKGROUND;
var _28=(isNumber(this.foreground))?this.foreground:Attribute.DEFAULT_FOREGROUND;
if(this.inverse){
_26.push("f"+_27);
_26.push("b"+((this.selected)?"s":_28));
}else{
_26.push("f"+_28);
_26.push("b"+((this.selected)?"s":_27));
}
if(this.bold){
_26.push("b");
}
if(this.italic){
_26.push("i");
}
if(this.underline){
_26.push("u");
}else{
if(this.strikethrough){
_26.push("lt");
}else{
if(this.blink){
_26.push("bl");
}
}
}
return "<span class=\""+_26.join(" ")+"\">";
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
function Range(_29,_2a){
if(isNumber(_29)===false){
_29=0;
}
if(isNumber(_2a)===false){
_2a=0;
}
this.startingOffset=Math.min(_29,_2a);
this.endingOffset=Math.max(_29,_2a);
};
Range.prototype.clamp=function(_2b){
var _2c;
if(this.isOverlapping(_2b)){
_2c=new Range(Math.max(this.startingOffset,_2b.startingOffset),Math.min(this.endingOffset,_2b.endingOffset));
}else{
_2c=new Range(0,0);
}
return _2c;
};
Range.prototype.contains=function(_2d){
return this.startingOffset<=_2d&&_2d<this.endingOffset;
};
Range.prototype.isEmpty=function(){
return this.startingOffset===this.endingOffset;
};
Range.prototype.isOverlapping=function(_2e){
var _2f=this.startingOffset;
var _30=_2e.startingOffset;
var _31=this.endingOffset-1;
var _32=_2e.endingOffset-1;
return (_30<=_2f&&_2f<=_32||_30<=_31&&_31<=_32||_2f<=_30&&_30<=_31||_2f<=_32&&_32<=_31);
};
Range.prototype.merge=function(_33){
return new Range(Math.min(this.startingOffset,_33.startingOffset),Math.max(this.endingOffset,_33.endingOffset));
};
Range.prototype.move=function(_34){
return new Range(this.startingOffset+_34,this.endingOffset+_34);
};
Line.DEFAULT_WIDTH=80;
Line.MIN_WIDTH=20;
Line.MAX_WIDTH=512;
function Line(_35){
if(isNumber(_35)){
_35=clamp(_35,Line.MIN_WIDTH,Line.MAX_WIDTH);
}else{
_35=Line.DEFAULT_WIDTH;
}
this._chars=new Array(_35);
this._attributes=new Array(_35);
this.clear();
};
Line.prototype.clear=function(ch){
ch=(isCharacter(ch))?ch.charAt(0):" ";
for(var i=0;i<this._chars.length;i++){
this._chars[i]=ch;
this._attributes[i]=new Attribute();
}
};
Line.prototype.clearLeft=function(_36){
if(isNumber(_36)&&0<=_36&&_36<this._chars.length){
for(var i=0;i<=_36;i++){
this._chars[i]=" ";
this._attributes[i]=new Attribute();
}
}
};
Line.prototype.clearRight=function(_37){
if(isNumber(_37)&&0<=_37&&_37<this._chars.length){
for(var i=_37;i<this._chars.length;i++){
this._chars[i]=" ";
this._attributes[i]=new Attribute();
}
}
};
Line.prototype.clearSelection=function(){
var _38=this._attributes;
var _39=_38.length;
for(var i=0;i<_39;i++){
_38[i].selected=false;
}
};
Line.prototype.deleteCharacter=function(_3a,_3b){
if(isNumber(_3a)){
var _3c=this._chars.length;
_3b=(isNumber(_3b))?_3b:1;
if(_3b>0&&0<=_3a&&_3a<_3c){
if(_3a+_3b>_3c){
_3b=_3c-_3a;
}
this._chars.splice(_3a,_3b);
this._attributes.splice(_3a,_3b);
for(var i=0;i<_3b;i++){
this._chars.push(" ");
this._attributes.push(new Attribute());
}
}
}
};
Line.prototype.getHTMLInfo=function(_3d,_3e){
var _3f=[];
for(var i=0;i<this._chars.length;i++){
var ch=this._chars[i];
var _40=this._attributes[i];
if(_40&&_40.equals(_3d)==false){
if(_3d!==null){
_3f.push(_3d.getEndingHTML());
}
_3f.push(_40.getStartingHTML());
_3d=_40;
}
if(i===_3e){
_3f.push("<span class=\"cursor\">");
}
switch(ch){
case "&":
_3f.push("&amp;");
break;
case "<":
_3f.push("&lt;");
break;
case ">":
_3f.push("&gt;");
break;
case " ":
_3f.push("&nbsp;");
break;
default:
_3f.push(ch);
break;
}
if(i===_3e){
_3f.push("</span>");
}
}
return {html:_3f.join(""),attribute:_3d};
};
Line.prototype.getLastNonWhiteOffset=function(){
var _41=0;
var _42=this._chars.length;
for(var i=_42-1;i>=0;i--){
if(this._chars[i].match(/\S/)){
_41=i+1;
break;
}
}
return _41;
};
Line.prototype.getSelectedText=function(){
var _43=this._chars;
var _44=this._attributes;
var _45=Math.min(this.getLastNonWhiteOffset(),_44.length);
var _46=null;
for(var i=0;i<_45;i++){
if(_44[i].selected){
if(_46===null){
_46=[];
}
_46.push(_43[i]);
}
}
return (_46!==null)?_46.join(""):null;
};
Line.prototype.getWidth=function(){
return this._chars.length;
};
Line.prototype.insertCharacter=function(ch,_47,_48){
if(isCharacter(ch)&&isNumber(_47)){
var _49=this._chars.length;
_48=(isNumber(_48))?_48:1;
if(_48>0&&0<=_47&&_47<_49){
ch=ch.charAt(0);
if(_47+_48>_49){
_48=_49-_47;
}
this._chars.splice(_49-_48,_48);
this._attributes.splice(_49-_48,_48);
var _4a=new Array(_48);
var _4b=new Array(_48);
for(var i=0;i<_48;i++){
this._chars.splice(_47+i,0,ch);
this._attributes.splice(_47+i,0,new Attribute());
}
}
}
};
Line.prototype.putCharacter=function(ch,_4c,_4d){
if(isCharacter(ch)&&isDefined(_4c)&&_4c.constructor==Attribute&&isNumber(_4d)){
if(0<=_4d&&_4d<this._chars.length){
this._chars[_4d]=ch.charAt(0);
this._attributes[_4d]=_4c;
}
}
};
Line.prototype.resize=function(_4e){
if(isNumber(_4e)){
var _4f=this._chars.length;
if(Line.MIN_WIDTH<=_4e&&_4e<=Line.MAX_WIDTH&&_4f!=_4e){
this._chars.length=_4e;
if(_4e>_4f){
for(var i=_4f;i<_4e;i++){
this._chars[i]=" ";
this._attributes[i]=new Attribute();
}
}
}
}
};
Line.prototype.select=function(_50,_51){
var _52=(_51)?this._chars.length:this.getLastNonWhiteOffset();
var _53=new Range(0,_52);
var _50=_50.clamp(_53);
var _54=this._attributes;
var _52=_50.endingOffset;
var _55=false;
for(var i=_50.startingOffset;i<_52;i++){
var _56=_54[i].copy();
_56.selected=true;
_54[i]=_56;
_55=true;
}
return _55;
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
var _57=this;
this._queue=[];
this._applicationKeys=false;
this._playbackState=KeyHandler.STOPPED;
this.clearEvents();
this._playbackID=null;
document.onkeypress=function(e){
return _57.processKeyPress(e);
};
document.onkeydown=function(e){
return _57.processKeyDown(e);
};
};
KeyHandler.prototype.addEvent=function(_58,e){
if(e){
var _59={};
switch(_58){
case KeyHandler.KEY_DOWN:
_59.keyCode=e.keyCode;
_59.ctrlKey=e.ctrlKey;
_59.altKey=e.altKey;
_59.shiftKey=e.shiftKey;
break;
case KeyHandler.KEY_PRESS:
_59.keyCode=e.keyCode;
_59.which=e.which;
_59.ctrlKey=e.ctrlKey;
_59.altKey=e.altKey;
_59.metaKey=e.metaKey;
break;
default:
return;
}
this._events.keys.push({time:new Date().getTime(),type:_58,event:_59});
}
};
KeyHandler.prototype.addKeys=function(_5a){
this._queue.push(_5a);
if(isDefined(this.callback)){
this.callback(true);
}
};
KeyHandler.prototype.clearEvents=function(){
this._events={user_agent:{browser:BrowserDetect.browser,version:BrowserDetect.version,os:BrowserDetect.OS},keys:[]};
};
KeyHandler.prototype.dequeueAll=function(){
var _5b=this._queue.join("");
this._queue.length=0;
return _5b;
};
KeyHandler.prototype.hasContent=function(){
return this._queue.length>0;
};
KeyHandler.prototype.play=function(_5c){
if(this._playbackState!=KeyHandler.PLAYING){
this._playbackState=KeyHandler.PLAYING;
_5c=_5c||this._events.keys;
var _5d=this;
var i=0;
var _5e=function(){
var _5f=_5c[i++];
switch(_5f.type){
case KeyHandler.KEY_DOWN:
_5d.processKeyDown(_5f.event);
break;
case KeyHandler.KEY_PRESS:
_5d.processKeyPress(_5f.event);
break;
default:
break;
}
if(_5d._playbackState==KeyHandler.PLAYING&&i<_5c.length){
var _60=clamp(_5c[i].time-_5f.time,0,1000);
this._playbackID=window.setTimeout(_5e,_60);
}
};
_5e();
}
};
KeyHandler.prototype.processKeyDown=function(e){
if(!e){
e=window.event;
}
if(this._playbackState==KeyHandler.RECORDING){
this.addEvent(KeyHandler.KEY_DOWN,e);
}
var _61=e.keyCode;
var _62=null;
var _63=this._applicationKeys;
if(BrowserDetect.browser=="Firefox"&&(e.keyCode==8||(37<=e.keyCode&&e.keyCode<=40))){
}else{
switch(_61){
case 8:
_62=KeyHandler.BACKSPACE;
break;
case 9:
_62=KeyHandler.TAB;
break;
case 27:
_62=KeyHandler.ESCAPE;
break;
case 33:
_62=KeyHandler.PAGE_UP;
break;
case 34:
_62=KeyHandler.PAGE_DOWN;
break;
case 35:
_62=(_63)?KeyHandler.APP_END:KeyHandler.END;
break;
case 36:
_62=(_63)?KeyHandler.APP_HOME:KeyHandler.HOME;
break;
case 37:
_62=(_63)?KeyHandler.APP_LEFT:KeyHandler.LEFT;
break;
case 38:
_62=(_63)?KeyHandler.APP_UP:KeyHandler.UP;
break;
case 39:
_62=(_63)?KeyHandler.APP_RIGHT:KeyHandler.RIGHT;
break;
case 40:
_62=(_63)?KeyHandler.APP_DOWN:KeyHandler.DOWN;
break;
case 45:
_62=KeyHandler.INSERT;
break;
case 46:
_62=KeyHandler.DELETE;
break;
case 112:
_62=e.shiftKey?KeyHandler.F13:KeyHandler.F1;
break;
case 113:
_62=e.shiftKey?KeyHandler.F14:KeyHandler.F2;
break;
case 114:
_62=e.shiftKey?KeyHandler.F15:KeyHandler.F3;
break;
case 115:
_62=e.shiftKey?KeyHandler.F16:KeyHandler.F4;
break;
case 116:
_62=e.shiftKey?KeyHandler.F17:KeyHandler.F5;
break;
case 117:
_62=e.shiftKey?KeyHandler.F18:KeyHandler.F6;
break;
case 118:
_62=e.shiftKey?KeyHandler.F19:KeyHandler.F7;
break;
case 119:
_62=e.shiftKey?KeyHandler.F20:KeyHandler.F8;
break;
case 120:
_62=KeyHandler.F9;
break;
case 121:
_62=KeyHandler.F10;
break;
case 122:
_62=KeyHandler.F11;
break;
case 123:
_62=KeyHandler.F12;
break;
default:
if(!e.ctrlKey||(e.ctrlKey&&e.altKey)||(e.keyCode==17)){
}else{
if(e.shiftKey){
switch(_61){
case 50:
_62=String.fromCharCode(0);
break;
case 54:
_62=String.fromCharCode(30);
break;
case 94:
_62=String.fromCharCode(30);
break;
case 109:
_62=String.fromCharCode(31);
break;
default:
break;
}
}else{
switch(_61){
case 32:
_62=String.fromCharCode(0);
break;
case 190:
_62=String.fromCharCode(30);
break;
case 219:
_62=String.fromCharCode(27);
break;
case 220:
_62=String.fromCharCode(28);
break;
case 221:
_62=String.fromCharCode(29);
break;
default:
if(65<=_61&&_61<=90){
_62=String.fromCharCode(_61-64);
}
break;
}
}
}
break;
}
}
if(_62!==null){
this.addKeys(_62);
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
var _64=this._applicationKeys;
var _65=null;
switch(e.keyCode){
case 8:
_65=KeyHandler.BACKSPACE;
break;
case 37:
_65=(_64)?KeyHandler.APP_LEFT:KeyHandler.LEFT;
break;
case 38:
_65=(_64)?KeyHandler.APP_UP:KeyHandler.UP;
break;
case 39:
_65=(_64)?KeyHandler.APP_RIGHT:KeyHandler.RIGHT;
break;
case 40:
_65=(_64)?KeyHandler.APP_DOWN:KeyHandler.DOWN;
break;
}
if(_65!==null){
this.addKeys(_65);
}
}else{
if((e.ctrlKey&&!e.altKey)||(e.which==0)||(e.keyCode==8)||(e.keyCode==16)){
}else{
var _66;
if(e.keyCode){
_66=e.keyCode;
}
if(e.which){
_66=e.which;
}
if(e.altKey&&!e.ctrlKey){
this.addKeys(KeyHandler.ESCAPE);
}
this.addKeys(String.fromCharCode(_66));
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
KeyHandler.prototype.setApplicationKeys=function(_67){
if(isBoolean(_67)){
this._applicationKeys=_67;
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
function XTermHandler(_68){
this._term=_68;
this._insertMode=false;
this._missingCommands={};
};
XTermHandler.prototype.BEL=function(_69,_6a){
};
XTermHandler.prototype.BS=function(_6b,_6c){
var col=this._term.getColumn()-1;
col=Math.max(0,col);
this._term.setColumn(col);
};
XTermHandler.prototype.CHA=function(_6d,_6e){
var _6f=0;
if(_6e.length>0){
_6f=_6e-1;
}
this._term.setColumn(_6f);
};
XTermHandler.prototype.CR=function(_70,_71){
this._term.setColumn(0);
};
XTermHandler.prototype.CUB=function(_72,_73){
var _74=1;
if(_73.length>0){
_74=_73-0;
if(_74==0){
_74=1;
}
}
var col=this._term.getColumn()-_74;
col=Math.max(0,col);
this._term.setColumn(col);
};
XTermHandler.prototype.CUD=function(_75,_76){
var _77=1;
if(_76.length>0){
_77=_76-0;
if(_77==0){
_77=1;
}
}
var _78=this._term.getRow();
var _79=this._term.getScrollRegion().bottom;
var _7a;
if(_78<=_79){
_7a=Math.min(_78+_77,_79);
}else{
_7a=Math.min(_78+_77,this._term.getHeight()-1);
}
this._term.setRow(_7a);
};
XTermHandler.prototype.CUF=function(_7b,_7c){
var _7d=1;
if(_7c.length>0){
_7d=_7c-0;
if(_7d==0){
_7d=1;
}
}
var col=this._term.getColumn()+_7d;
col=Math.min(col,this._term.getWidth()-1);
this._term.setColumn(col);
};
XTermHandler.prototype.CUP=function(_7e,_7f){
var row=0;
var col=0;
var _80=this._term.getHeight();
if(_7f.length>0){
var _81=_7f.split(/;/);
var row=_81[0]-1;
var col=_81[1]-1;
}
if(row>=_80){
var _82=_80-row;
row=_80-1;
this._term.scrollUp(_82);
}
this._term.setPosition(row,col);
};
XTermHandler.prototype.CUU=function(_83,_84){
var _85=1;
if(_84.length>0){
_85=_84-0;
if(_85==0){
_85=1;
}
}
var _86=this._term.getRow();
var _87=this._term.getScrollRegion().top;
var _88;
if(_87<=_86){
_88=Math.max(_87,_86-_85);
}else{
_88=Math.max(0,_86-_85);
}
this._term.setRow(_88);
};
XTermHandler.prototype.DCH=function(_89,_8a){
var _8b=_8a-0;
this._term.deleteCharacter(_8b);
};
XTermHandler.prototype.DECALN=function(_8c,_8d){
this._term.clear("E");
};
XTermHandler.prototype.DECRC=function(_8e,_8f){
this._term.popPosition();
};
XTermHandler.prototype.DECPAM=function(_90,_91){
this._term.setApplicationKeys(true);
};
XTermHandler.prototype.DECPNM=function(_92,_93){
this._term.setApplicationKeys(false);
};
XTermHandler.prototype.DECRST=function(_94,_95){
var _96=_95-0;
switch(_96){
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
this.genericHandler(_94,_95);
break;
}
};
XTermHandler.prototype.DECSC=function(_97,_98){
this._term.pushPosition();
};
XTermHandler.prototype.DECSET=function(_99,_9a){
var _9b=_9a-0;
switch(_9b){
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
this.genericHandler(_99,_9a);
break;
}
};
XTermHandler.prototype.DECSTBM=function(_9c,_9d){
var _9e=_9d.split(/;/);
var top=_9e[0]-1;
var _9f=_9e[1]-1;
this._term.setScrollRegion(top,0,_9f,this._term.getWidth()-1);
};
XTermHandler.prototype.DL=function(_a0,_a1){
var _a2=1;
if(_a1.length>0){
_a2=_a1-0;
if(_a2==0){
_a2=1;
}
}
this._term.deleteLine(_a2);
};
XTermHandler.prototype.ED=function(_a3,_a4){
var _a5=_a4-0;
switch(_a5){
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
this.genericHandler(_a3+":"+_a4,"");
break;
}
};
XTermHandler.prototype.EL=function(_a6,_a7){
var _a8=_a7-0;
switch(_a8){
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
this.genericHandler(_a6+":"+_a7,"");
break;
}
};
XTermHandler.prototype.genericHandler=function(_a9,_aa){
if(this._missingCommands.hasOwnProperty(_a9)===false){
this._missingCommands[_a9]=0;
}
this._missingCommands[_a9]++;
};
XTermHandler.prototype.getMissingCommands=function(){
return this._missingCommands;
};
XTermHandler.prototype.HVP=XTermHandler.prototype.CUP;
XTermHandler.prototype.ICH=function(_ab,_ac){
var _ad=_ac-0;
this._term.insertCharacter(" ",_ad);
};
XTermHandler.prototype.IL=function(_ae,_af){
var _b0=1;
if(_af.length>0){
_b0=_af-0;
if(_b0==0){
_b0=1;
}
}
this._term.insertLine(_b0);
};
XTermHandler.prototype.IND=function(_b1,_b2){
var _b3=this._term.getRow();
var _b4=this._term.getScrollRegion().bottom;
var _b5=_b3+1;
if(_b3<=_b4){
this._term.setRow(_b5);
}else{
this._term.scrollUp(1);
this._term.setRow(_b4);
}
};
XTermHandler.prototype.LF=function(_b6,_b7){
var _b8=this._term;
var row=_b8.getRow()+1;
var _b9=_b8.getScrollRegion().bottom;
if(row>_b9){
_b8.scrollUp();
row=_b9;
}
_b8.setPosition(row,0);
};
XTermHandler.prototype.NEL=XTermHandler.prototype.LF;
XTermHandler.prototype.processCharacter=function(_ba,_bb){
if(this._insertMode){
this._term.insertCharacter(" ",1);
}
this._term.displayCharacters(_bb);
};
XTermHandler.prototype.RI=function(_bc,_bd){
var _be=this._term.getRow();
var _bf=this._term.getScrollRegion().top;
var _c0=_be-1;
if(_bf<=_c0){
this._term.setRow(_c0);
}else{
this._term.scrollDown(1);
this._term.setRow(_bf);
}
};
XTermHandler.prototype.RM=function(_c1,_c2){
var _c3=_c2-0;
switch(_c3){
case 4:
this._insertMode=false;
break;
case 2:
case 12:
case 20:
default:
this.genericHandler(_c1,_c2);
break;
}
};
XTermHandler.prototype.SD=function(_c4,_c5){
var _c6=1;
if(_c5.length>0){
_c6=_c5-0;
}
var _c7=this._term.getRow();
var _c8=this._term.getScrollRegion().top;
var _c9=_c7-_c6;
if(_c8<=_c9){
this._term.setRow(_c9);
}else{
this._term.scrollDown(_c6);
this._term.setRow(_c8);
}
};
XTermHandler.prototype.SET_TEXT_PARAMS=function(_ca,_cb){
var _cc=_cb.split(/;/);
var _cd=_cc[0]-0;
var _ce=_cc[1];
if(_cd==0){
this._term.setTitle(_ce);
}else{
this.genericHandler(_ca+":"+_cb,"");
}
};
XTermHandler.prototype.SET_TEXT_PARAMS2=XTermHandler.prototype.SET_TEXT_PARAMS;
XTermHandler.prototype.SGR=function(_cf,_d0){
var _d1=this._term.getCurrentAttribute();
var _d2=_d0.split(/;/);
for(var i=0;i<_d2.length;i++){
var _d3=_d2[i]-0;
if(_d3<50){
var _d4=Math.floor(_d3/10);
var _d5=_d3%10;
switch(_d4){
case 0:
switch(_d5){
case 0:
_d1.reset();
break;
case 1:
_d1.bold=true;
break;
case 3:
_d1.italic=true;
break;
case 4:
_d1.underline=true;
break;
case 7:
_d1.inverse=true;
break;
case 9:
_d1.strikethrough=true;
break;
default:
this.genericHandler(_cf+":"+_d0,"");
break;
}
break;
case 2:
switch(_d5){
case 2:
_d1.bold=false;
break;
case 3:
_d1.italic=false;
break;
case 4:
_d1.underline=false;
break;
case 7:
_d1.inverse=false;
break;
case 9:
_d1.strikethough=false;
break;
default:
this.genericHandler(_cf+":"+_d0,"");
break;
}
break;
case 3:
switch(_d5){
case 0:
case 1:
case 2:
case 3:
case 4:
case 5:
case 6:
case 7:
_d1.foreground=_d5;
break;
case 9:
_d1.resetForeground();
break;
default:
this.genericHandler(_cf+":"+_d0,"");
break;
}
break;
case 4:
switch(_d5){
case 0:
case 1:
case 2:
case 3:
case 4:
case 5:
case 6:
case 7:
_d1.background=_d5;
break;
case 9:
_d1.resetBackground();
break;
default:
this.genericHandler(_cf+":"+_d0,"");
break;
}
break;
default:
this.genericHandler(_cf+":"+_d0,"");
break;
}
}else{
this.genericHandler(_cf+":"+_d0,"");
}
}
this._term.setCurrentAttribute(_d1);
};
XTermHandler.prototype.SM=function(_d6,_d7){
var _d8=_d7-0;
switch(_d8){
case 4:
this._insertMode=true;
break;
case 2:
case 12:
case 20:
default:
this.genericHandler(_d6,_d7);
break;
}
};
XTermHandler.prototype.SU=function(_d9,_da){
var _db=1;
if(_da.length>0){
_db=_da-0;
}
var _dc=this._term.getRow();
var _dd=this._term.getScrollRegion().bottom;
var _de=_dc+_db;
if(_dc<=_dd){
this._term.setRow(_de);
}else{
this._term.scrollUp(_db);
this._term.setRow(_dd);
}
};
XTermHandler.prototype.TAB=function(_df,_e0){
var _e1=this._term.getColumn();
var _e2=8-(_e1%8);
this._term.displayCharacters(new Array(_e2+1).join(" "));
};
XTermHandler.prototype.VPA=function(_e3,_e4){
var row=0;
if(_e4.length>0){
row=_e4-1;
}
this._term.setRow(row);
};
XTermHandler.prototype.VT=XTermHandler.prototype.LF;
function TermParser(_e5,_e6){
if(_e5===null||_e5===undefined){
throw new Error("Parsing tables must be defined when creating a new TermParser");
}
this._processTables(_e5);
this._actions=_e5.actions;
this._nodes=_e5.nodes;
this.setHandler(_e6);
this.singleStep=false;
this.offset=-1;
};
TermParser.prototype.getHandler=function(){
return this._handler;
};
TermParser.prototype.parse=function(_e7){
var _e8=0;
var _e9=isString(_e7)?_e7.length:0;
while(_e8<_e9){
var _ea=0;
var _eb=this._nodes[_ea][1];
var _ec=(_eb==-1)?-2:_e8;
for(var i=_e8;i<_e9;i++){
var _ed=this._nodes[_ea];
if(_ed){
var _ee=_e7.charCodeAt(i);
var _ef=_ed[0][_ee];
if(_ef!=-1){
_ea=_ef;
var _f0=this._nodes[_ea][1];
if(_f0!=-1){
_ec=i;
_eb=_f0;
}
}else{
break;
}
}
}
if(_eb==-1){
if(this._handler!=null){
if(this._handler.processCharacter){
this._handler.processCharacter("processCharacter",_e7.charAt(_e8));
}
}
_e8++;
}else{
var _f1=_ec+1;
if(this._handler!=null){
var _f2=this._actions[_eb];
var _f3=_f2[0];
var _f4="";
if(_f2.length>=3&&_f2[1]!=-1&&_f2[2]!=-1){
_f4=_e7.substring(_e8+_f2[1],_f1-_f2[2]);
}
this._handler[_f3](_f3,_f4);
}
_e8=_f1;
if(this.singleStep){
this.offset=_e8;
break;
}
}
}
};
TermParser.prototype._processTables=function(_f5){
if(_f5.hasOwnProperty("processed")==false||_f5.processed==false){
switch(_f5.format){
case "expanded":
break;
case "rle":
var mos=new Array(256);
for(var i=0;i<mos.length;i++){
mos[i]=-1;
}
var _f6=_f5.nodes;
for(var i=0;i<_f6.length;i++){
var _f7=_f6[i][0];
var _f8=[];
for(var j=0;j<_f7.length;j++){
var _f9=_f7[j];
if(_f9<0){
_f8=_f8.concat(mos.slice(0,-_f9));
}else{
var _fa=_f9>>8;
var _fb=(_f9&255)+1;
for(var k=0;k<_fb;k++){
_f8.push(_fa);
}
}
}
_f6[i][0]=_f8;
}
break;
default:
break;
}
_f5.processed=true;
}
};
TermParser.prototype.setHandler=function(_fc){
var _fd=null;
if(_fc){
var _fe=null;
var _ff=function(_100,_101){
};
for(var i=0;i<this._actions.length;i++){
var _102=this._actions[i];
var _103=_102[0];
if(!_fc[_103]){
if(_fd==null){
_fd=protectedClone(_fc);
if(!_fc.genericHandler){
_fe=_ff;
}else{
_fe=_fc.genericHandler;
}
}
_fd[_103]=_fe;
}
}
}
if(_fd==null){
this._handler=_fc;
}else{
this._handler=_fd;
}
};
TermComm.POLLING_INTERVAL_MIN=125;
TermComm.POLLING_INTERVAL_MAX=2000;
TermComm.POLLING_GROWTH_RATE=2;
TermComm.DEFAULT_REQUEST_URL="/stream";
TermComm.DEFAULT_GET_UNIQUE_ID_URL="/id";
function TermComm(_104,_105){
var self=this;
this.terminal=_104;
this.keyHandler=_104.getKeyHandler();
this.keyHandler.callback=function(){
self.sendKeys();
};
this.minInterval=125;
this.maxInterval=2000;
this.growthRate=2;
this.timeoutInterval=5000;
this.requestURL=TermComm.DEFAULT_REQUEST_URL;
this.getUniqueIdURL=TermComm.DEFAULT_GET_UNIQUE_ID_URL;
if(isDefined(_105)){
if(_105.hasOwnProperty("minInterval")&&isNumber(_105.minInterval)){
this.minInterval=_105.minInterval;
}
if(_105.hasOwnProperty("maxInterval")&&isNumber(_105.maxInterval)){
this.maxInterval=_105.maxInterval;
}
if(_105.hasOwnProperty("growthRate")&&isNumber(_105.growthRate)){
this.growthRate=_105.growthRate;
}
if(_105.hasOwnProperty("timeoutInterval")&&isNumber(_105.timeoutInterval)){
this.timeoutInterval=_105.timeoutInterval;
}
if(_105.hasOwnProperty("requestURL")&&isString(_105.requestURL)&&_105.requestURL.length>0){
this.requestURL=_105.requestURL;
}
if(_105.hasOwnProperty("getUniqueIdURL")&&isString(_105.getUniqueIdURL)&&_105.getUniqueIdURL.length>0){
this.getUniqueIdURL=_105.getUniqueIdURL;
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
var self=this;
var req=createXHR();
var _106={id:this.terminal.getId(),cb:new Date().getTime()+":"+this.cacheBusterID++};
req.open("GET",createURL(this.requestURL,_106),true);
if(this.ie){
req.setRequestHeader("If-Modified-Since","Sat, 1 Jan 2000 00:00:00 GMT");
}
req.onreadystatechange=function(){
if(req.readyState==4){
if(self.watchdogID!==null){
window.clearTimeout(self.watchdogID);
self.watchdogID=null;
}
var text=req.responseText;
if(isString(text)&&text.length>0){
self.terminal.processCharacters(text);
self.pollingInterval=self.minInterval;
}else{
self.pollingInterval*=self.growthRate;
if(self.pollingInterval>self.maxInterval){
self.pollingInterval=self.maxInterval;
}
}
self.requestID=window.setTimeout(function(){
self.update();
},(this.updateQueued)?0:self.pollingInterval);
this.updateQueued=false;
}
};
this.watchdogID=window.setTimeout(function(){
self.timeout();
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
var self=this;
var req=createXHR();
var _107={id:id};
req.open("POST",createURL(this.requestURL,_107),true);
req.onreadystatechange=function(){
if(req.readyState==4){
self.sendingKeys=false;
self.update(true);
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
TermComm.prototype.update=function(_108){
if(this.running&&this.terminal.getId()!==null){
if(isBoolean(_108)){
if(this.requestID!==null){
window.clearTimeout(this.requestID);
this.requestID=null;
}
if(_108){
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
function Term(id,_109,_10a,_10b){
if(isString(id)===false||id.length===0){
id="terminal";
}
this._id=(_10b&&_10b.hasOwnProperty("id"))?_10b.id:null;
this._remainingText="";
this._rootNode=document.getElementById(id);
if(this._rootNode){
this._rootNode.className="webterm";
this._termNode=document.createElement("pre");
this._rootNode.appendChild(this._termNode);
this._width=(isNumber(_109))?clamp(_109,Line.MIN_WIDTH,Line.MAX_WIDTH):Line.DEFAULT_WIDTH;
this._height=(isNumber(_10a))?clamp(_10a,Term.MIN_HEIGHT,Term.MAX_HEIGHT):Term.DEFAULT_HEIGHT;
this._title="Aptana WebTerm";
this._row=0;
this._column=0;
this._scrollRegion={top:0,left:0,bottom:this._height-1,right:this._width-1};
this._cursorVisible=true;
this._buffers=[];
this._positions=[];
this._currentAttribute=new Attribute();
this._hasSelection=false;
this._sendResizeSequence=(_10b&&_10b.hasOwnProperty("sendResizeSequence"))?_10b.sendResizeSequence:true;
this._showTitle=(_10b&&_10b.hasOwnProperty("showTitle"))?_10b.showTitle:true;
this._onTitleChange=(_10b&&_10b.hasOwnProperty("onTitleChange"))?_10b.onTitleChange:null;
var _10c=(_10b&&_10b.hasOwnProperty("handler"))?_10b.handler:new XTermHandler(this);
var _10d=(_10b&&_10b.hasOwnProperty("tables"))?_10b.tables:XTermTables;
var _10e=(_10b&&_10b.hasOwnProperty("parser"))?_10b.parser:new TermParser(_10d,_10c);
var _10f=(_10b&&_10b.hasOwnProperty("keyHandler"))?_10b.keyHandler:new KeyHandler();
this._parser=_10e;
this._keyHandler=_10f;
var _110=(_10b&&_10b.hasOwnProperty("commHandler"))?_10b.commHandler:new TermComm(this,_10b);
var _111=(_10b&&_10b.hasOwnProperty("autoStart"))?_10b.autoStart:true;
this._commHandler=_110;
this.createBuffer();
var self=this;
dragger(this._rootNode,function(sx,sy,ex,ey){
self.updateSelection(sx,sy,ex,ey);
});
this.refresh();
if(_111){
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
var _112=new Array(this._height);
for(var i=0;i<_112.length;i++){
_112[i]=new Line(this._width);
}
this._lines=_112;
};
Term.prototype.clearSelection=function(_113){
var _114=this._lines;
var _115=_114.length;
for(var i=0;i<_115;i++){
_114[i].clearSelection();
}
this._hasSelection=false;
if((isBoolean(_113))?_113:true){
this.refresh();
}
};
Term.prototype.deleteCharacter=function(_116){
this._lines[this._row].deleteCharacter(this._column,_116);
};
Term.prototype.deleteLine=function(_117){
_117=(_117===undefined)?1:_117;
if(_117>0){
var _118=this._scrollRegion;
if(_118.left==0&&_118.right==this._width-1){
if(this._row+_117>_118.bottom){
_117=_118.bottom-this._row+1;
}
if(_117==this._height){
this.clear();
}else{
var _119=this._lines.splice(this._row,_117);
for(var i=0;i<_117;i++){
_119[i].clear();
}
if(_118.bottom+1==this.height){
this._lines=this._lines.concat(_119);
}else{
for(var i=0;i<_117;i++){
this._lines.splice(_118.bottom-_117+i+1,0,_119[i]);
}
}
}
}else{
}
}
};
Term.prototype.displayCharacters=function(_11a){
if(isString(_11a)){
for(var i=0;i<_11a.length;i++){
var ch=_11a.charAt(i);
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
var _11b=null;
if(this.hasSelection()){
var _11c=this._lines;
var _11d=_11c.length;
for(var i=0;i<_11d;i++){
var _11e=_11c[i].getSelectedText();
if(_11e!==null){
if(_11b===null){
_11b=[];
}
_11b.push(_11e);
}
}
}
return (_11b!==null)?_11b.join("\n"):null;
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
Term.prototype.insertCharacter=function(ch,_11f){
this._lines[this._row].insertCharacter(ch,this._column,_11f);
};
Term.prototype.insertLine=function(_120){
_120=(_120===undefined)?1:_120;
if(_120>0){
var _121=this._scrollRegion;
if(_121.left==0&&_121.right==this._width-1){
if(this._row+_120>_121.bottom){
_120=_121.bottom-this._row+1;
}
if(_120==this._height){
this.clear();
}else{
var _122=this._lines.splice(_121.bottom-_120+1,_120);
for(var i=0;i<_120;i++){
_122[i].clear();
}
if(this._row==0){
this._lines=_122.concat(this._lines);
}else{
for(var i=0;i<_120;i++){
this._lines.splice(this._row+i,0,_122[i]);
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
var _123=this._positions.pop();
this._row=_123[0];
this._column=_123[1];
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
var _124=[];
var attr=null;
var _125=this._title+" — "+this._width+"x"+this._height;
var _126="<div class='title'>"+_125+"</div>";
for(var row=0;row<this._height;row++){
var line=this._lines[row];
var _127=(this._cursorVisible)?(row==this._row)?this._column:-1:-1;
var _128=line.getHTMLInfo(attr,_127);
attr=_128.attribute;
_124.push(_128.html);
}
if(attr!=null){
_124[_124.length-1]+=attr.getEndingHTML();
}
if(this._showTitle){
this._termNode.innerHTML=_126+_124.join("<br />");
}else{
this._termNode.innerHTML=_124.join("<br />");
}
};
Term.prototype.scrollDown=function(_129){
_129=(_129===undefined)?1:_129;
if(_129>0){
var _12a=this._scrollRegion;
if(_12a.left==0&&_12a.right==this._width-1){
var _12b=_12a.bottom-_12a.top+1;
if(_129>=_12b){
this.clear();
}else{
var _12c=this._lines.splice(_12a.bottom-_129+1,_129);
for(var i=0;i<_129;i++){
_12c[i].clear();
}
if(_12a.top==0){
this._lines=_12c.concat(this._lines);
}else{
for(var i=0;i<_129;i++){
this._lines.splice(_12a.top+i,0,_12c[i]);
}
}
}
}else{
}
}
};
Term.prototype.scrollUp=function(_12d){
_12d=(_12d===undefined)?1:_12d;
if(_12d>0){
var _12e=this._scrollRegion;
if(_12e.left==0&&_12e.right==this._width-1){
var _12f=_12e.bottom-_12e.top+1;
if(_12d>=_12f){
this.clear();
}else{
var _130=this._lines.splice(_12e.top,_12d);
for(var i=0;i<_12d;i++){
_130[i].clear();
}
if(_12e.bottom+1==this.height){
this._lines=this._lines.concat(_130);
}else{
for(var i=0;i<_12d;i++){
this._lines.splice(_12e.bottom-_12d+i+1,0,_130[i]);
}
}
}
}else{
}
}
};
Term.prototype.select=function(s,e){
var _131=this.getWidth();
var _132=this.getHeight();
var _133=new Range(s,e).clamp(new Range(0,_131*_132));
var _134=this.hasSelection();
var _135=false;
if(_134){
this.clearSelection(false);
}
if(_133.isEmpty()===false){
var _136=0;
for(var i=0;i<_132;i++){
var _137=_136+_131;
var _138=new Range(_136,_137).clamp(_133);
if(_138.isEmpty()===false){
var _139=_133.endingOffset>_138.endingOffset;
var _13a=_138.move(-_136);
var line=this._lines[i];
if(line.select(_13a,_139)){
_135=true;
}
}
_136=_137;
}
}
this._hasSelection=_135;
if(_134||_135){
this.refresh();
}
};
Term.prototype.setApplicationKeys=function(_13b){
if(isBoolean(_13b)){
this._keyHandler.setApplicationKeys(_13b);
}
};
Term.prototype.setColumn=function(_13c){
if(isNumber(_13c)&&0<=_13c&&_13c<this._width){
this._column=_13c;
}
};
Term.prototype.setCurrentAttribute=function(attr){
if(isDefined(attr)&&attr.constructor===Attribute){
this._currentAttribute=attr;
}
};
Term.prototype.setCursorVisible=function(_13d){
if(isBoolean(_13d)){
this._cursorVisible=_13d;
}
};
Term.prototype.setHeight=function(_13e){
this.setSize(this._width,_13e);
};
Term.prototype.setPosition=function(row,_13f){
if(isNumber(row)&&0<=row&&row<this._height){
this._row=row;
}
if(isNumber(_13f)&&0<=_13f&&_13f<this._width){
this._column=_13f;
}
};
Term.prototype.setRow=function(row){
if(0<=row&&row<this._height){
this._row=row;
}
};
Term.prototype.setScrollRegion=function(top,left,_140,_141){
if(isNumber(top)&&isNumber(left)&&isNumber(_140)&&isNumber(_141)){
if(top<_140&&left<_141){
var _142=(0<=top&&top<this._height);
var _143=(0<=left&&left<this._width);
var _144=(0<=_140&&_140<this._height);
var _145=(0<=_141&&_141<this._width);
if(_142&&_143&&_144&&_145){
this._scrollRegion={top:top,left:left,bottom:_140,right:_141};
}
}
}
};
Term.prototype.setSize=function(_146,_147){
var _148=false;
if(isNumber(_146)&&Line.MIN_WIDTH<=_146&&_146<=Line.MAX_WIDTH&&this._width!=_146){
for(var i=0;i<this._height;i++){
this._lines[i].resize(_146);
}
this._width=_146;
this._column=Math.min(this._width-1,this._column);
_148=true;
}
if(isNumber(_147)&&Term.MIN_HEIGHT<=_147&&_147<=Term.MAX_HEIGHT&&this._height!=_147){
if(_147>this._height){
for(var i=this._height;i<_147;i++){
this._lines.push(new Line(this._width));
}
}else{
this._lines=this._lines.splice(this._height-_147,_147);
}
this._height=_147;
this._row=Math.min(this._height-1,this._row);
_148=true;
}
if(_148){
this.setScrollRegion(0,0,this._height-1,this._width-1);
if(this._sendResizeSequence){
var ESC=String.fromCharCode(27);
var CSI=ESC+"[";
this._keyHandler.addKeys(CSI+[8,this._height,this._width].join(";")+"t");
}
}
};
Term.prototype.setTitle=function(_149){
this._title=_149;
if(isFunction(this._onTitleChange)){
this._onTitleChange(_149);
}
};
Term.prototype.showTitle=function(_14a){
if(isBoolean(_14a)){
this._showTitle=_14a;
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
Term.prototype.setWidth=function(_14b){
this.setSize(_14b,this._height);
};
Term.prototype.toString=function(){
var _14c=[];
for(var i=0;i<this._lines.length;i++){
_14c.push(this._lines[i].toString());
}
return _14c.join("\n");
};
Term.prototype.updateSelection=function(_14d,_14e,endX,endY){
var _14f=Math.floor(_14d/characterWidth+0.5);
var _150=Math.floor(_14e/characterHeight+0.5);
var _151=Math.floor(endX/characterWidth+0.5);
var _152=Math.floor(endY/characterHeight+0.5);
var _153=_150*this.getWidth()+_14f;
var _154=_152*this.getWidth()+_151;
this.select(_153,_154);
};

