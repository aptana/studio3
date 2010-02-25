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
_a.overrideMimeType("text/plain");
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
_13=e.clientX-_1c;
_14=e.clientY-_1d;
}
_15=e.clientX-_1c;
_16=e.clientY-_1d;
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
_11.removeEventListener("mousedown",_1f,false);
_11.addEventListener("mousemove",_20,false);
_11.addEventListener("mouseup",_21,false);
return _19(e);
};
var _20=function(e){
return _19(e);
};
var _21=function(e){
_11.removeEventListener("mousemove",_20,false);
_11.removeEventListener("mouseup",_21,false);
_11.addEventListener("mousedown",_1f,false);
var _22=_19(e);
_13=_14=_15=_16=null;
return _22;
};
if(isDefined(_11)){
_11.addEventListener("mousedown",_1f,false);
}
};
function FontInfo(){
this._rootNode=document.getElementById("fontInfo");
this._rootNode.className="webterm";
this._termNode=document.createElement("pre");
this._rootNode.appendChild(this._termNode);
this._characterSizes={};
};
FontInfo.prototype.clearSizes=function(){
this._characterSizes={};
};
FontInfo.prototype.getCharacterHeight=function(c,_23){
var _24=this.getCharacterSize(c,_23);
return _24[1];
};
FontInfo.prototype.getCharacterSize=function(c,_25){
var _26=this._characterSizes;
_25=(isDefined(_25))?_25:"normal";
if(_26.hasOwnProperty(c)===false){
this._termNode.style.fontWeight="normal";
this._termNode.innerHTML=c;
var _27=this._termNode.clientWidth;
var _28=this._termNode.clientHeight;
this._termNode.style.fontWeight="bolder";
var _29=this._termNode.clientWidth;
var _2a=this._termNode.clientHeight;
_26[c]={normal:[_27,_28],bold:[_29,_2a]};
this._termNode.innerHTML=[c,_27+","+_28,_29+","+_2a].join("<br />");
}
return _26[c][_25];
};
FontInfo.prototype.getCharacterWidth=function(c,_2b){
var _2c=this.getCharacterSize(c,_2b);
return _2c[0];
};
Attribute.DEFAULT_BACKGROUND="b";
Attribute.DEFAULT_FOREGROUND="f";
function Attribute(){
this.reset();
};
Attribute.prototype.copy=function(){
var _2d=new Attribute();
_2d.foreground=this.foreground;
_2d.background=this.background;
_2d.bold=this.bold;
_2d.italic=this.italic;
_2d.underline=this.underline;
_2d.inverse=this.inverse;
_2d.strikethrough=this.strikethrough;
_2d.blink=this.blink;
_2d.selected=this.selected;
return _2d;
};
Attribute.prototype.equals=function(_2e){
var _2f=false;
if(_2e instanceof Attribute){
_2f=this===_2e||(this.foreground==_2e.foreground&&this.background==_2e.background&&this.bold==_2e.bold&&this.italic==_2e.italic&&this.underline==_2e.underline&&this.inverse==_2e.inverse&&this.strikethrough==_2e.strikethrough&&this.blink==_2e.blink&&this.selected==_2e.selected);
}
return _2f;
};
Attribute.prototype.getStartingHTML=function(){
var _30=[];
var _31=(isNumber(this.background))?this.background:Attribute.DEFAULT_BACKGROUND;
var _32=(isNumber(this.foreground))?this.foreground:Attribute.DEFAULT_FOREGROUND;
if(this.inverse){
_30.push("f"+_31);
_30.push("b"+((this.selected)?"s":_32));
}else{
_30.push("f"+_32);
_30.push("b"+((this.selected)?"s":_31));
}
if(this.bold){
_30.push("b");
}
if(this.italic){
_30.push("i");
}
if(this.underline){
_30.push("u");
}else{
if(this.strikethrough){
_30.push("lt");
}else{
if(this.blink){
_30.push("bl");
}
}
}
return "<span class=\""+_30.join(" ")+"\">";
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
function Range(_33,_34){
if(isNumber(_33)===false){
_33=0;
}
if(isNumber(_34)===false){
_34=0;
}
this.startingOffset=Math.min(_33,_34);
this.endingOffset=Math.max(_33,_34);
};
Range.prototype.clamp=function(_35){
var _36;
if(this.isOverlapping(_35)){
_36=new Range(Math.max(this.startingOffset,_35.startingOffset),Math.min(this.endingOffset,_35.endingOffset));
}else{
_36=new Range(0,0);
}
return _36;
};
Range.prototype.contains=function(_37){
return this.startingOffset<=_37&&_37<this.endingOffset;
};
Range.prototype.isEmpty=function(){
return this.startingOffset===this.endingOffset;
};
Range.prototype.isOverlapping=function(_38){
var _39=this.startingOffset;
var _3a=_38.startingOffset;
var _3b=this.endingOffset-1;
var _3c=_38.endingOffset-1;
return (_3a<=_39&&_39<=_3c||_3a<=_3b&&_3b<=_3c||_39<=_3a&&_3a<=_3b||_39<=_3c&&_3c<=_3b);
};
Range.prototype.merge=function(_3d){
return new Range(Math.min(this.startingOffset,_3d.startingOffset),Math.max(this.endingOffset,_3d.endingOffset));
};
Range.prototype.move=function(_3e){
return new Range(this.startingOffset+_3e,this.endingOffset+_3e);
};
Line.DEFAULT_WIDTH=80;
Line.MIN_WIDTH=20;
Line.MAX_WIDTH=512;
function Line(_3f,_40){
if(isNumber(_3f)){
_3f=clamp(_3f,Line.MIN_WIDTH,Line.MAX_WIDTH);
}else{
_3f=Line.DEFAULT_WIDTH;
}
this._fontInfo=_40;
this._chars=new Array(_3f);
this._attributes=new Array(_3f);
this.clear();
};
Line.prototype.clear=function(ch){
ch=(isCharacter(ch))?ch.charAt(0):" ";
for(var i=0;i<this._chars.length;i++){
this._chars[i]=ch;
this._attributes[i]=new Attribute();
}
};
Line.prototype.clearLeft=function(_41){
if(isNumber(_41)&&0<=_41&&_41<this._chars.length){
for(var i=0;i<=_41;i++){
this._chars[i]=" ";
this._attributes[i]=new Attribute();
}
}
};
Line.prototype.clearRight=function(_42){
if(isNumber(_42)&&0<=_42&&_42<this._chars.length){
for(var i=_42;i<this._chars.length;i++){
this._chars[i]=" ";
this._attributes[i]=new Attribute();
}
}
};
Line.prototype.clearSelection=function(){
var _43=this._attributes;
var _44=_43.length;
for(var i=0;i<_44;i++){
_43[i].selected=false;
}
};
Line.prototype.deleteCharacter=function(_45,_46){
if(isNumber(_45)){
var _47=this._chars.length;
_46=(isNumber(_46))?_46:1;
if(_46>0&&0<=_45&&_45<_47){
if(_45+_46>_47){
_46=_47-_45;
}
this._chars.splice(_45,_46);
this._attributes.splice(_45,_46);
for(var i=0;i<_46;i++){
this._chars.push(" ");
this._attributes.push(new Attribute());
}
}
}
};
Line.prototype.getHTMLInfo=function(_48,_49){
var _4a=[];
for(var i=0;i<this._chars.length;i++){
var ch=this._chars[i];
var _4b=this._attributes[i];
if(_4b&&_4b.equals(_48)==false){
if(_48!==null){
_4a.push(_48.getEndingHTML());
}
_4a.push(_4b.getStartingHTML());
_48=_4b;
}
if(i===_49){
_4a.push("<span class=\"cursor\">");
}
switch(ch){
case "&":
_4a.push("&amp;");
break;
case "<":
_4a.push("&lt;");
break;
case ">":
_4a.push("&gt;");
break;
case " ":
_4a.push("&nbsp;");
break;
default:
_4a.push(ch);
break;
}
if(i===_49){
_4a.push("</span>");
}
}
return {html:_4a.join(""),attribute:_48};
};
Line.prototype.getLastNonWhiteOffset=function(){
var _4c=0;
var _4d=this._chars.length;
for(var i=_4d-1;i>=0;i--){
if(this._chars[i].match(/\S/)){
_4c=i+1;
break;
}
}
return _4c;
};
Line.prototype.getLineHeight=function(){
var _4e=this._chars;
var _4f=this._attributes;
var _50=_4e.length;
var _51=0;
for(var i=0;i<_50;i++){
var ch=_4e[i];
var _52=_4f[i];
var _53=(_52.bold)?"bold":"normal";
var _54=this._fontInfo.getCharacterHeight(ch,_53);
_51=Math.max(_51,_54);
}
return _51;
};
Line.prototype.getOffsetFromPosition=function(x){
var _55=0;
var _56=this._chars;
var _57=this._attributes;
var _58=_56.length;
var _59;
for(var i=0;i<_58;i++){
var ch=_56[i];
var _5a=_57[i];
var _5b=(_5a.bold)?"bold":"normal";
var _5c=_55+this._fontInfo.getCharacterWidth(ch,_5b);
if(_55<=x&&x<_5c){
_59=i;
break;
}else{
_55=_5c;
}
}
return _59;
};
Line.prototype.getSelectedText=function(){
var _5d=this._chars;
var _5e=this._attributes;
var _5f=Math.min(this.getLastNonWhiteOffset(),_5e.length);
var _60=null;
for(var i=0;i<_5f;i++){
if(_5e[i].selected){
if(_60===null){
_60=[];
}
_60.push(_5d[i]);
}
}
return (_60!==null)?_60.join(""):null;
};
Line.prototype.getWidth=function(){
return this._chars.length;
};
Line.prototype.insertCharacter=function(ch,_61,_62){
if(isCharacter(ch)&&isNumber(_61)){
var _63=this._chars.length;
_62=(isNumber(_62))?_62:1;
if(_62>0&&0<=_61&&_61<_63){
ch=ch.charAt(0);
if(_61+_62>_63){
_62=_63-_61;
}
this._chars.splice(_63-_62,_62);
this._attributes.splice(_63-_62,_62);
var _64=new Array(_62);
var _65=new Array(_62);
for(var i=0;i<_62;i++){
this._chars.splice(_61+i,0,ch);
this._attributes.splice(_61+i,0,new Attribute());
}
}
}
};
Line.prototype.putCharacter=function(ch,_66,_67){
if(isCharacter(ch)&&isDefined(_66)&&_66.constructor==Attribute&&isNumber(_67)){
if(0<=_67&&_67<this._chars.length){
this._chars[_67]=ch.charAt(0);
this._attributes[_67]=_66;
}
}
};
Line.prototype.resize=function(_68){
if(isNumber(_68)){
var _69=this._chars.length;
if(Line.MIN_WIDTH<=_68&&_68<=Line.MAX_WIDTH&&_69!=_68){
this._chars.length=_68;
if(_68>_69){
for(var i=_69;i<_68;i++){
this._chars[i]=" ";
this._attributes[i]=new Attribute();
}
}
}
}
};
Line.prototype.select=function(_6a,_6b){
var _6c=(_6b)?this._chars.length:this.getLastNonWhiteOffset();
var _6d=new Range(0,_6c);
var _6a=_6a.clamp(_6d);
var _6e=this._attributes;
var _6c=_6a.endingOffset;
var _6f=false;
for(var i=_6a.startingOffset;i<_6c;i++){
var _70=_6e[i].copy();
_70.selected=true;
_6e[i]=_70;
_6f=true;
}
return _6f;
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
var _71=this;
this._queue=[];
this._applicationKeys=false;
this._playbackState=KeyHandler.STOPPED;
this.clearEvents();
this._playbackID=null;
document.onkeypress=function(e){
return _71.processKeyPress(e);
};
document.onkeydown=function(e){
return _71.processKeyDown(e);
};
};
KeyHandler.prototype.addEvent=function(_72,e){
if(e){
var _73={};
switch(_72){
case KeyHandler.KEY_DOWN:
_73.keyCode=e.keyCode;
_73.ctrlKey=e.ctrlKey;
_73.altKey=e.altKey;
_73.shiftKey=e.shiftKey;
break;
case KeyHandler.KEY_PRESS:
_73.keyCode=e.keyCode;
_73.which=e.which;
_73.ctrlKey=e.ctrlKey;
_73.altKey=e.altKey;
_73.metaKey=e.metaKey;
break;
default:
return;
}
this._events.keys.push({time:new Date().getTime(),type:_72,event:_73});
}
};
KeyHandler.prototype.addKeys=function(_74){
this._queue.push(_74);
if(isDefined(this.callback)){
this.callback(true);
}
};
KeyHandler.prototype.clearEvents=function(){
this._events={user_agent:{browser:BrowserDetect.browser,version:BrowserDetect.version,os:BrowserDetect.OS},keys:[]};
};
KeyHandler.prototype.dequeueAll=function(){
var _75=this._queue.join("");
this._queue.length=0;
return _75;
};
KeyHandler.prototype.hasContent=function(){
return this._queue.length>0;
};
KeyHandler.prototype.play=function(_76){
if(this._playbackState!=KeyHandler.PLAYING){
this._playbackState=KeyHandler.PLAYING;
_76=_76||this._events.keys;
var _77=this;
var i=0;
var _78=function(){
var _79=_76[i++];
switch(_79.type){
case KeyHandler.KEY_DOWN:
_77.processKeyDown(_79.event);
break;
case KeyHandler.KEY_PRESS:
_77.processKeyPress(_79.event);
break;
default:
break;
}
if(_77._playbackState==KeyHandler.PLAYING&&i<_76.length){
var _7a=clamp(_76[i].time-_79.time,0,1000);
this._playbackID=window.setTimeout(_78,_7a);
}
};
_78();
}
};
KeyHandler.prototype.processKeyDown=function(e){
if(!e){
e=window.event;
}
if(this._playbackState==KeyHandler.RECORDING){
this.addEvent(KeyHandler.KEY_DOWN,e);
}
var _7b=e.keyCode;
var _7c=null;
var _7d=this._applicationKeys;
if(BrowserDetect.browser=="Firefox"&&(e.keyCode==8||(37<=e.keyCode&&e.keyCode<=40))){
}else{
switch(_7b){
case 8:
_7c=KeyHandler.BACKSPACE;
break;
case 9:
_7c=KeyHandler.TAB;
break;
case 27:
_7c=KeyHandler.ESCAPE;
break;
case 33:
_7c=KeyHandler.PAGE_UP;
break;
case 34:
_7c=KeyHandler.PAGE_DOWN;
break;
case 35:
_7c=(_7d)?KeyHandler.APP_END:KeyHandler.END;
break;
case 36:
_7c=(_7d)?KeyHandler.APP_HOME:KeyHandler.HOME;
break;
case 37:
_7c=(_7d)?KeyHandler.APP_LEFT:KeyHandler.LEFT;
break;
case 38:
_7c=(_7d)?KeyHandler.APP_UP:KeyHandler.UP;
break;
case 39:
_7c=(_7d)?KeyHandler.APP_RIGHT:KeyHandler.RIGHT;
break;
case 40:
_7c=(_7d)?KeyHandler.APP_DOWN:KeyHandler.DOWN;
break;
case 45:
_7c=KeyHandler.INSERT;
break;
case 46:
_7c=KeyHandler.DELETE;
break;
case 112:
_7c=e.shiftKey?KeyHandler.F13:KeyHandler.F1;
break;
case 113:
_7c=e.shiftKey?KeyHandler.F14:KeyHandler.F2;
break;
case 114:
_7c=e.shiftKey?KeyHandler.F15:KeyHandler.F3;
break;
case 115:
_7c=e.shiftKey?KeyHandler.F16:KeyHandler.F4;
break;
case 116:
_7c=e.shiftKey?KeyHandler.F17:KeyHandler.F5;
break;
case 117:
_7c=e.shiftKey?KeyHandler.F18:KeyHandler.F6;
break;
case 118:
_7c=e.shiftKey?KeyHandler.F19:KeyHandler.F7;
break;
case 119:
_7c=e.shiftKey?KeyHandler.F20:KeyHandler.F8;
break;
case 120:
_7c=KeyHandler.F9;
break;
case 121:
_7c=KeyHandler.F10;
break;
case 122:
_7c=KeyHandler.F11;
break;
case 123:
_7c=KeyHandler.F12;
break;
default:
if(!e.ctrlKey||(e.ctrlKey&&e.altKey)||(e.keyCode==17)){
}else{
if(e.shiftKey){
switch(_7b){
case 50:
_7c=String.fromCharCode(0);
break;
case 54:
_7c=String.fromCharCode(30);
break;
case 94:
_7c=String.fromCharCode(30);
break;
case 109:
_7c=String.fromCharCode(31);
break;
default:
break;
}
}else{
switch(_7b){
case 32:
_7c=String.fromCharCode(0);
break;
case 190:
_7c=String.fromCharCode(30);
break;
case 219:
_7c=String.fromCharCode(27);
break;
case 220:
_7c=String.fromCharCode(28);
break;
case 221:
_7c=String.fromCharCode(29);
break;
default:
if(65<=_7b&&_7b<=90){
_7c=String.fromCharCode(_7b-64);
}
break;
}
}
}
break;
}
}
if(_7c!==null){
this.addKeys(_7c);
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
var _7e=this._applicationKeys;
var _7f=null;
switch(e.keyCode){
case 8:
_7f=KeyHandler.BACKSPACE;
break;
case 37:
_7f=(_7e)?KeyHandler.APP_LEFT:KeyHandler.LEFT;
break;
case 38:
_7f=(_7e)?KeyHandler.APP_UP:KeyHandler.UP;
break;
case 39:
_7f=(_7e)?KeyHandler.APP_RIGHT:KeyHandler.RIGHT;
break;
case 40:
_7f=(_7e)?KeyHandler.APP_DOWN:KeyHandler.DOWN;
break;
}
if(_7f!==null){
this.addKeys(_7f);
}
}else{
if((e.ctrlKey&&!e.altKey)||(e.which==0)||(e.keyCode==8)||(e.keyCode==16)){
}else{
var _80;
if(e.keyCode){
_80=e.keyCode;
}
if(e.which){
_80=e.which;
}
if(e.altKey&&!e.ctrlKey){
this.addKeys(KeyHandler.ESCAPE);
}
this.addKeys(String.fromCharCode(_80));
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
KeyHandler.prototype.setApplicationKeys=function(_81){
if(isBoolean(_81)){
this._applicationKeys=_81;
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
function XTermHandler(_82){
this._term=_82;
this._insertMode=false;
this._missingCommands={};
};
XTermHandler.prototype.BEL=function(_83,_84){
};
XTermHandler.prototype.BS=function(_85,_86){
var col=this._term.getColumn()-1;
col=Math.max(0,col);
this._term.setColumn(col);
};
XTermHandler.prototype.CHA=function(_87,_88){
var _89=0;
if(_88.length>0){
_89=_88-1;
}
this._term.setColumn(_89);
};
XTermHandler.prototype.CR=function(_8a,_8b){
this._term.setColumn(0);
};
XTermHandler.prototype.CUB=function(_8c,_8d){
var _8e=1;
if(_8d.length>0){
_8e=_8d-0;
if(_8e==0){
_8e=1;
}
}
var col=this._term.getColumn()-_8e;
col=Math.max(0,col);
this._term.setColumn(col);
};
XTermHandler.prototype.CUD=function(_8f,_90){
var _91=1;
if(_90.length>0){
_91=_90-0;
if(_91==0){
_91=1;
}
}
var _92=this._term.getRow();
var _93=this._term.getScrollRegion().bottom;
var _94;
if(_92<=_93){
_94=Math.min(_92+_91,_93);
}else{
_94=Math.min(_92+_91,this._term.getHeight()-1);
}
this._term.setRow(_94);
};
XTermHandler.prototype.CUF=function(_95,_96){
var _97=1;
if(_96.length>0){
_97=_96-0;
if(_97==0){
_97=1;
}
}
var col=this._term.getColumn()+_97;
col=Math.min(col,this._term.getWidth()-1);
this._term.setColumn(col);
};
XTermHandler.prototype.CUP=function(_98,_99){
var row=0;
var col=0;
var _9a=this._term.getHeight();
if(_99.length>0){
var _9b=_99.split(/;/);
var row=_9b[0]-1;
var col=_9b[1]-1;
}
if(row>=_9a){
var _9c=_9a-row;
row=_9a-1;
this._term.scrollUp(_9c);
}
this._term.setPosition(row,col);
};
XTermHandler.prototype.CUU=function(_9d,_9e){
var _9f=1;
if(_9e.length>0){
_9f=_9e-0;
if(_9f==0){
_9f=1;
}
}
var _a0=this._term.getRow();
var _a1=this._term.getScrollRegion().top;
var _a2;
if(_a1<=_a0){
_a2=Math.max(_a1,_a0-_9f);
}else{
_a2=Math.max(0,_a0-_9f);
}
this._term.setRow(_a2);
};
XTermHandler.prototype.DCH=function(_a3,_a4){
var _a5=_a4-0;
this._term.deleteCharacter(_a5);
};
XTermHandler.prototype.DECALN=function(_a6,_a7){
this._term.clear("E");
};
XTermHandler.prototype.DECRC=function(_a8,_a9){
this._term.popPosition();
};
XTermHandler.prototype.DECPAM=function(_aa,_ab){
this._term.setApplicationKeys(true);
};
XTermHandler.prototype.DECPNM=function(_ac,_ad){
this._term.setApplicationKeys(false);
};
XTermHandler.prototype.DECRST=function(_ae,_af){
var _b0=_af-0;
switch(_b0){
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
this.genericHandler(_ae,_af);
break;
}
};
XTermHandler.prototype.DECSC=function(_b1,_b2){
this._term.pushPosition();
};
XTermHandler.prototype.DECSET=function(_b3,_b4){
var _b5=_b4-0;
switch(_b5){
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
this.genericHandler(_b3,_b4);
break;
}
};
XTermHandler.prototype.DECSTBM=function(_b6,_b7){
var _b8=_b7.split(/;/);
var top=_b8[0]-1;
var _b9=_b8[1]-1;
this._term.setScrollRegion(top,0,_b9,this._term.getWidth()-1);
};
XTermHandler.prototype.DL=function(_ba,_bb){
var _bc=1;
if(_bb.length>0){
_bc=_bb-0;
if(_bc==0){
_bc=1;
}
}
this._term.deleteLine(_bc);
};
XTermHandler.prototype.ED=function(_bd,_be){
var _bf=_be-0;
switch(_bf){
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
this.genericHandler(_bd+":"+_be,"");
break;
}
};
XTermHandler.prototype.EL=function(_c0,_c1){
var _c2=_c1-0;
switch(_c2){
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
this.genericHandler(_c0+":"+_c1,"");
break;
}
};
XTermHandler.prototype.genericHandler=function(_c3,_c4){
if(this._missingCommands.hasOwnProperty(_c3)===false){
this._missingCommands[_c3]=0;
}
this._missingCommands[_c3]++;
};
XTermHandler.prototype.getMissingCommands=function(){
return this._missingCommands;
};
XTermHandler.prototype.HVP=XTermHandler.prototype.CUP;
XTermHandler.prototype.ICH=function(_c5,_c6){
var _c7=_c6-0;
this._term.insertCharacter(" ",_c7);
};
XTermHandler.prototype.IL=function(_c8,_c9){
var _ca=1;
if(_c9.length>0){
_ca=_c9-0;
if(_ca==0){
_ca=1;
}
}
this._term.insertLine(_ca);
};
XTermHandler.prototype.IND=function(_cb,_cc){
var _cd=this._term.getRow();
var _ce=this._term.getScrollRegion().bottom;
var _cf=_cd+1;
if(_cd<=_ce){
this._term.setRow(_cf);
}else{
this._term.scrollUp(1);
this._term.setRow(_ce);
}
};
XTermHandler.prototype.LF=function(_d0,_d1){
var _d2=this._term;
var row=_d2.getRow()+1;
var _d3=_d2.getScrollRegion().bottom;
if(row>_d3){
_d2.scrollUp();
row=_d3;
}
_d2.setPosition(row,0);
};
XTermHandler.prototype.NEL=XTermHandler.prototype.LF;
XTermHandler.prototype.processCharacter=function(_d4,_d5){
if(this._insertMode){
this._term.insertCharacter(" ",1);
}
this._term.displayCharacters(_d5);
};
XTermHandler.prototype.RI=function(_d6,_d7){
var _d8=this._term.getRow();
var _d9=this._term.getScrollRegion().top;
var _da=_d8-1;
if(_d9<=_da){
this._term.setRow(_da);
}else{
this._term.scrollDown(1);
this._term.setRow(_d9);
}
};
XTermHandler.prototype.RM=function(_db,_dc){
var _dd=_dc-0;
switch(_dd){
case 4:
this._insertMode=false;
break;
case 2:
case 12:
case 20:
default:
this.genericHandler(_db,_dc);
break;
}
};
XTermHandler.prototype.SD=function(_de,_df){
var _e0=1;
if(_df.length>0){
_e0=_df-0;
}
var _e1=this._term.getRow();
var _e2=this._term.getScrollRegion().top;
var _e3=_e1-_e0;
if(_e2<=_e3){
this._term.setRow(_e3);
}else{
this._term.scrollDown(_e0);
this._term.setRow(_e2);
}
};
XTermHandler.prototype.SET_TEXT_PARAMS=function(_e4,_e5){
var _e6=_e5.split(/;/);
var _e7=_e6[0]-0;
var _e8=_e6[1];
if(_e7==0){
this._term.setTitle(_e8);
}else{
this.genericHandler(_e4+":"+_e5,"");
}
};
XTermHandler.prototype.SET_TEXT_PARAMS2=XTermHandler.prototype.SET_TEXT_PARAMS;
XTermHandler.prototype.SGR=function(_e9,_ea){
var _eb=this._term.getCurrentAttribute();
var _ec=_ea.split(/;/);
for(var i=0;i<_ec.length;i++){
var _ed=_ec[i]-0;
if(_ed<50){
var _ee=Math.floor(_ed/10);
var _ef=_ed%10;
switch(_ee){
case 0:
switch(_ef){
case 0:
_eb.reset();
break;
case 1:
_eb.bold=true;
break;
case 3:
_eb.italic=true;
break;
case 4:
_eb.underline=true;
break;
case 7:
_eb.inverse=true;
break;
case 9:
_eb.strikethrough=true;
break;
default:
this.genericHandler(_e9+":"+_ea,"");
break;
}
break;
case 2:
switch(_ef){
case 2:
_eb.bold=false;
break;
case 3:
_eb.italic=false;
break;
case 4:
_eb.underline=false;
break;
case 7:
_eb.inverse=false;
break;
case 9:
_eb.strikethough=false;
break;
default:
this.genericHandler(_e9+":"+_ea,"");
break;
}
break;
case 3:
switch(_ef){
case 0:
case 1:
case 2:
case 3:
case 4:
case 5:
case 6:
case 7:
_eb.foreground=_ef;
break;
case 9:
_eb.resetForeground();
break;
default:
this.genericHandler(_e9+":"+_ea,"");
break;
}
break;
case 4:
switch(_ef){
case 0:
case 1:
case 2:
case 3:
case 4:
case 5:
case 6:
case 7:
_eb.background=_ef;
break;
case 9:
_eb.resetBackground();
break;
default:
this.genericHandler(_e9+":"+_ea,"");
break;
}
break;
default:
this.genericHandler(_e9+":"+_ea,"");
break;
}
}else{
this.genericHandler(_e9+":"+_ea,"");
}
}
this._term.setCurrentAttribute(_eb);
};
XTermHandler.prototype.SM=function(_f0,_f1){
var _f2=_f1-0;
switch(_f2){
case 4:
this._insertMode=true;
break;
case 2:
case 12:
case 20:
default:
this.genericHandler(_f0,_f1);
break;
}
};
XTermHandler.prototype.SU=function(_f3,_f4){
var _f5=1;
if(_f4.length>0){
_f5=_f4-0;
}
var _f6=this._term.getRow();
var _f7=this._term.getScrollRegion().bottom;
var _f8=_f6+_f5;
if(_f6<=_f7){
this._term.setRow(_f8);
}else{
this._term.scrollUp(_f5);
this._term.setRow(_f7);
}
};
XTermHandler.prototype.TAB=function(_f9,_fa){
var _fb=this._term.getColumn();
var _fc=8-(_fb%8);
this._term.displayCharacters(new Array(_fc+1).join(" "));
};
XTermHandler.prototype.VPA=function(_fd,_fe){
var row=0;
if(_fe.length>0){
row=_fe-1;
}
this._term.setRow(row);
};
XTermHandler.prototype.VT=XTermHandler.prototype.LF;
function TermParser(_ff,_100){
if(_ff===null||_ff===undefined){
throw new Error("Parsing tables must be defined when creating a new TermParser");
}
this._processTables(_ff);
this._actions=_ff.actions;
this._nodes=_ff.nodes;
this.setHandler(_100);
this.singleStep=false;
this.offset=-1;
};
TermParser.prototype.getHandler=function(){
return this._handler;
};
TermParser.prototype.parse=function(_101){
var _102=0;
var _103=isString(_101)?_101.length:0;
while(_102<_103){
var _104=0;
var _105=this._nodes[_104][1];
var _106=(_105==-1)?-2:_102;
for(var i=_102;i<_103;i++){
var _107=this._nodes[_104];
if(_107){
var _108=_101.charCodeAt(i);
var _109=_107[0][_108];
if(_109!=-1){
_104=_109;
var _10a=this._nodes[_104][1];
if(_10a!=-1){
_106=i;
_105=_10a;
}
}else{
break;
}
}
}
if(_105==-1){
if(this._handler!=null){
if(this._handler.processCharacter){
this._handler.processCharacter("processCharacter",_101.charAt(_102));
}
}
_102++;
}else{
var _10b=_106+1;
if(this._handler!=null){
var info=this._actions[_105];
var _10c=info[0];
var _10d="";
if(info.length>=3&&info[1]!=-1&&info[2]!=-1){
_10d=_101.substring(_102+info[1],_10b-info[2]);
}
this._handler[_10c](_10c,_10d);
}
_102=_10b;
if(this.singleStep){
this.offset=_102;
break;
}
}
}
};
TermParser.prototype._processTables=function(_10e){
if(_10e.hasOwnProperty("processed")==false||_10e.processed==false){
switch(_10e.format){
case "expanded":
break;
case "rle":
var mos=new Array(256);
for(var i=0;i<mos.length;i++){
mos[i]=-1;
}
var _10f=_10e.nodes;
for(var i=0;i<_10f.length;i++){
var _110=_10f[i][0];
var _111=[];
for(var j=0;j<_110.length;j++){
var _112=_110[j];
if(_112<0){
_111=_111.concat(mos.slice(0,-_112));
}else{
var _113=_112>>8;
var _114=(_112&255)+1;
for(var k=0;k<_114;k++){
_111.push(_113);
}
}
}
_10f[i][0]=_111;
}
break;
default:
break;
}
_10e.processed=true;
}
};
TermParser.prototype.setHandler=function(_115){
var _116=null;
if(_115){
var _117=null;
var _118=function(_119,_11a){
};
for(var i=0;i<this._actions.length;i++){
var _11b=this._actions[i];
var _11c=_11b[0];
if(!_115[_11c]){
if(_116==null){
_116=protectedClone(_115);
if(!_115.genericHandler){
_117=_118;
}else{
_117=_115.genericHandler;
}
}
_116[_11c]=_117;
}
}
}
if(_116==null){
this._handler=_115;
}else{
this._handler=_116;
}
};
TermComm.POLLING_INTERVAL_MIN=125;
TermComm.POLLING_INTERVAL_MAX=2000;
TermComm.POLLING_GROWTH_RATE=2;
TermComm.DEFAULT_REQUEST_URL="/stream";
TermComm.DEFAULT_GET_UNIQUE_ID_URL="/id";
function TermComm(_11d,_11e){
var self=this;
this.terminal=_11d;
this.keyHandler=_11d.getKeyHandler();
this.keyHandler.callback=function(){
self.sendKeys();
};
this.minInterval=125;
this.maxInterval=2000;
this.growthRate=2;
this.timeoutInterval=5000;
this.requestURL=TermComm.DEFAULT_REQUEST_URL;
this.getUniqueIdURL=TermComm.DEFAULT_GET_UNIQUE_ID_URL;
if(isDefined(_11e)){
if(_11e.hasOwnProperty("minInterval")&&isNumber(_11e.minInterval)){
this.minInterval=_11e.minInterval;
}
if(_11e.hasOwnProperty("maxInterval")&&isNumber(_11e.maxInterval)){
this.maxInterval=_11e.maxInterval;
}
if(_11e.hasOwnProperty("growthRate")&&isNumber(_11e.growthRate)){
this.growthRate=_11e.growthRate;
}
if(_11e.hasOwnProperty("timeoutInterval")&&isNumber(_11e.timeoutInterval)){
this.timeoutInterval=_11e.timeoutInterval;
}
if(_11e.hasOwnProperty("requestURL")&&isString(_11e.requestURL)&&_11e.requestURL.length>0){
this.requestURL=_11e.requestURL;
}
if(_11e.hasOwnProperty("getUniqueIdURL")&&isString(_11e.getUniqueIdURL)&&_11e.getUniqueIdURL.length>0){
this.getUniqueIdURL=_11e.getUniqueIdURL;
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
var _11f={id:this.terminal.getId(),cb:new Date().getTime()+":"+this.cacheBusterID++};
req.open("GET",createURL(this.requestURL,_11f),true);
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
var _120={id:id};
req.open("POST",createURL(this.requestURL,_120),true);
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
TermComm.prototype.update=function(_121){
if(this.running&&this.terminal.getId()!==null){
if(isBoolean(_121)){
if(this.requestID!==null){
window.clearTimeout(this.requestID);
this.requestID=null;
}
if(_121){
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
function Term(id,_122,_123,_124){
if(isString(id)===false||id.length===0){
id="terminal";
}
this._id=(_124&&_124.hasOwnProperty("id"))?_124.id:null;
this._remainingText="";
this._rootNode=document.getElementById(id);
if(this._rootNode){
this._rootNode.className="webterm";
this._termNode=document.createElement("pre");
this._rootNode.appendChild(this._termNode);
this._width=(isNumber(_122))?clamp(_122,Line.MIN_WIDTH,Line.MAX_WIDTH):Line.DEFAULT_WIDTH;
this._height=(isNumber(_123))?clamp(_123,Term.MIN_HEIGHT,Term.MAX_HEIGHT):Term.DEFAULT_HEIGHT;
this._title="Aptana WebTerm";
this._row=0;
this._column=0;
this._scrollRegion={top:0,left:0,bottom:this._height-1,right:this._width-1};
this._cursorVisible=true;
this._buffers=[];
this._positions=[];
this._currentAttribute=new Attribute();
this._hasSelection=false;
this._fontInfo=new FontInfo();
this._sendResizeSequence=(_124&&_124.hasOwnProperty("sendResizeSequence"))?_124.sendResizeSequence:true;
this._showTitle=(_124&&_124.hasOwnProperty("showTitle"))?_124.showTitle:true;
this._onTitleChange=(_124&&_124.hasOwnProperty("onTitleChange"))?_124.onTitleChange:null;
var _125=(_124&&_124.hasOwnProperty("handler"))?_124.handler:new XTermHandler(this);
var _126=(_124&&_124.hasOwnProperty("tables"))?_124.tables:XTermTables;
var _127=(_124&&_124.hasOwnProperty("parser"))?_124.parser:new TermParser(_126,_125);
var _128=(_124&&_124.hasOwnProperty("keyHandler"))?_124.keyHandler:new KeyHandler();
this._parser=_127;
this._keyHandler=_128;
var _129=(_124&&_124.hasOwnProperty("commHandler"))?_124.commHandler:new TermComm(this,_124);
var _12a=(_124&&_124.hasOwnProperty("autoStart"))?_124.autoStart:true;
this._commHandler=_129;
this.createBuffer();
var self=this;
dragger(this._rootNode,function(sx,sy,ex,ey){
self.updateSelection(sx,sy,ex,ey);
});
this.refresh();
if(_12a){
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
Term.prototype.clearCharacterSizes=function(){
this._characterSizes={};
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
var _12b=new Array(this._height);
for(var i=0;i<_12b.length;i++){
_12b[i]=this.createLine();
}
this._lines=_12b;
};
Term.prototype.createLine=function(){
return new Line(this._width,this._fontInfo);
};
Term.prototype.clearSelection=function(_12c){
var _12d=this._lines;
var _12e=_12d.length;
for(var i=0;i<_12e;i++){
_12d[i].clearSelection();
}
this._hasSelection=false;
if((isBoolean(_12c))?_12c:true){
this.refresh();
}
};
Term.prototype.deleteCharacter=function(_12f){
this._lines[this._row].deleteCharacter(this._column,_12f);
};
Term.prototype.deleteLine=function(_130){
_130=(_130===undefined)?1:_130;
if(_130>0){
var _131=this._scrollRegion;
if(_131.left==0&&_131.right==this._width-1){
if(this._row+_130>_131.bottom){
_130=_131.bottom-this._row+1;
}
if(_130==this._height){
this.clear();
}else{
var _132=this._lines.splice(this._row,_130);
for(var i=0;i<_130;i++){
_132[i].clear();
}
if(_131.bottom+1==this.height){
this._lines=this._lines.concat(_132);
}else{
for(var i=0;i<_130;i++){
this._lines.splice(_131.bottom-_130+i+1,0,_132[i]);
}
}
}
}else{
}
}
};
Term.prototype.displayCharacters=function(_133){
if(isString(_133)){
for(var i=0;i<_133.length;i++){
var ch=_133.charAt(i);
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
Term.prototype.getFontInfo=function(){
return this._fontInfo;
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
var _134=null;
if(this.hasSelection()){
var _135=this._lines;
var _136=_135.length;
for(var i=0;i<_136;i++){
var _137=_135[i].getSelectedText();
if(_137!==null){
if(_134===null){
_134=[];
}
_134.push(_137);
}
}
}
return (_134!==null)?_134.join("\n"):null;
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
Term.prototype.insertCharacter=function(ch,_138){
this._lines[this._row].insertCharacter(ch,this._column,_138);
};
Term.prototype.insertLine=function(_139){
_139=(_139===undefined)?1:_139;
if(_139>0){
var _13a=this._scrollRegion;
if(_13a.left==0&&_13a.right==this._width-1){
if(this._row+_139>_13a.bottom){
_139=_13a.bottom-this._row+1;
}
if(_139==this._height){
this.clear();
}else{
var _13b=this._lines.splice(_13a.bottom-_139+1,_139);
for(var i=0;i<_139;i++){
_13b[i].clear();
}
if(this._row==0){
this._lines=_13b.concat(this._lines);
}else{
for(var i=0;i<_139;i++){
this._lines.splice(this._row+i,0,_13b[i]);
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
var _13c=this._positions.pop();
this._row=_13c[0];
this._column=_13c[1];
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
var _13d=[];
var attr=null;
var _13e=this._title+" — "+this._width+"x"+this._height;
var _13f="<div class='title'>"+_13e+"</div>";
for(var row=0;row<this._height;row++){
var line=this._lines[row];
var _140=(this._cursorVisible)?(row==this._row)?this._column:-1:-1;
var _141=line.getHTMLInfo(attr,_140);
attr=_141.attribute;
_13d.push(_141.html);
}
if(attr!=null){
_13d[_13d.length-1]+=attr.getEndingHTML();
}
if(this._showTitle){
this._termNode.innerHTML=_13f+_13d.join("<br />");
}else{
this._termNode.innerHTML=_13d.join("<br />");
}
};
Term.prototype.scrollDown=function(_142){
_142=(_142===undefined)?1:_142;
if(_142>0){
var _143=this._scrollRegion;
if(_143.left==0&&_143.right==this._width-1){
var _144=_143.bottom-_143.top+1;
if(_142>=_144){
this.clear();
}else{
var _145=this._lines.splice(_143.bottom-_142+1,_142);
for(var i=0;i<_142;i++){
_145[i].clear();
}
if(_143.top==0){
this._lines=_145.concat(this._lines);
}else{
for(var i=0;i<_142;i++){
this._lines.splice(_143.top+i,0,_145[i]);
}
}
}
}else{
}
}
};
Term.prototype.scrollUp=function(_146){
_146=(_146===undefined)?1:_146;
if(_146>0){
var _147=this._scrollRegion;
if(_147.left==0&&_147.right==this._width-1){
var _148=_147.bottom-_147.top+1;
if(_146>=_148){
this.clear();
}else{
var _149=this._lines.splice(_147.top,_146);
for(var i=0;i<_146;i++){
_149[i].clear();
}
if(_147.bottom+1==this.height){
this._lines=this._lines.concat(_149);
}else{
for(var i=0;i<_146;i++){
this._lines.splice(_147.bottom-_146+i+1,0,_149[i]);
}
}
}
}else{
}
}
};
Term.prototype.select=function(s,e){
var _14a=this.getWidth();
var _14b=this.getHeight();
var _14c=new Range(s,e).clamp(new Range(0,_14a*_14b));
var _14d=this.hasSelection();
var _14e=false;
if(_14d){
this.clearSelection(false);
}
if(_14c.isEmpty()===false){
var _14f=0;
for(var i=0;i<_14b;i++){
var _150=_14f+_14a;
var _151=new Range(_14f,_150).clamp(_14c);
if(_151.isEmpty()===false){
var _152=_14c.endingOffset>_151.endingOffset;
var _153=_151.move(-_14f);
var line=this._lines[i];
if(line.select(_153,_152)){
_14e=true;
}
}
_14f=_150;
}
}
this._hasSelection=_14e;
if(_14d||_14e){
this.refresh();
}
};
Term.prototype.selectAll=function(){
this.select(0,this._width*this._height);
};
Term.prototype.setApplicationKeys=function(_154){
if(isBoolean(_154)){
this._keyHandler.setApplicationKeys(_154);
}
};
Term.prototype.setColumn=function(_155){
if(isNumber(_155)&&0<=_155&&_155<this._width){
this._column=_155;
}
};
Term.prototype.setCurrentAttribute=function(attr){
if(isDefined(attr)&&attr.constructor===Attribute){
this._currentAttribute=attr;
}
};
Term.prototype.setCursorVisible=function(_156){
if(isBoolean(_156)){
this._cursorVisible=_156;
}
};
Term.prototype.setHeight=function(_157){
this.setSize(this._width,_157);
};
Term.prototype.setPosition=function(row,_158){
if(isNumber(row)&&0<=row&&row<this._height){
this._row=row;
}
if(isNumber(_158)&&0<=_158&&_158<this._width){
this._column=_158;
}
};
Term.prototype.setRow=function(row){
if(0<=row&&row<this._height){
this._row=row;
}
};
Term.prototype.setScrollRegion=function(top,left,_159,_15a){
if(isNumber(top)&&isNumber(left)&&isNumber(_159)&&isNumber(_15a)){
if(top<_159&&left<_15a){
var _15b=(0<=top&&top<this._height);
var _15c=(0<=left&&left<this._width);
var _15d=(0<=_159&&_159<this._height);
var _15e=(0<=_15a&&_15a<this._width);
if(_15b&&_15c&&_15d&&_15e){
this._scrollRegion={top:top,left:left,bottom:_159,right:_15a};
}
}
}
};
Term.prototype.setSize=function(_15f,_160){
var _161=false;
if(isNumber(_15f)&&Line.MIN_WIDTH<=_15f&&_15f<=Line.MAX_WIDTH&&this._width!=_15f){
for(var i=0;i<this._height;i++){
this._lines[i].resize(_15f);
}
this._width=_15f;
this._column=Math.min(this._width-1,this._column);
_161=true;
}
if(isNumber(_160)&&Term.MIN_HEIGHT<=_160&&_160<=Term.MAX_HEIGHT&&this._height!=_160){
if(_160>this._height){
for(var i=this._height;i<_160;i++){
this._lines.push(this.createLine());
}
}else{
this._lines=this._lines.splice(this._height-_160,_160);
}
this._height=_160;
this._row=Math.min(this._height-1,this._row);
_161=true;
}
if(_161){
this.setScrollRegion(0,0,this._height-1,this._width-1);
if(this._sendResizeSequence){
var ESC=String.fromCharCode(27);
var CSI=ESC+"[";
this._keyHandler.addKeys(CSI+[8,this._height,this._width].join(";")+"t");
}
}
};
Term.prototype.setTitle=function(_162){
this._title=_162;
if(isFunction(this._onTitleChange)){
this._onTitleChange(_162);
}
};
Term.prototype.showTitle=function(_163){
if(isBoolean(_163)){
this._showTitle=_163;
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
Term.prototype.setWidth=function(_164){
this.setSize(_164,this._height);
};
Term.prototype.toString=function(){
var _165=[];
for(var i=0;i<this._lines.length;i++){
_165.push(this._lines[i].toString());
}
return _165.join("\n");
};
Term.prototype.updateSelection=function(_166,_167,endX,endY){
if(isNumber(_166)&&isNumber(_167)&&isNumber(endX)&&isNumber(endY)){
var _168=this._lines;
var _169=function(y){
var _16a=_168.length;
var _16b=0;
var _16c=null;
for(var i=0;i<_16a;i++){
var line=_168[i];
var _16d=line.getLineHeight();
var _16e=_16b+_16d;
if(_16b<=y&&y<_16e){
_16c=i;
break;
}else{
_16b=_16e;
}
}
return _16c;
};
var _16f=_169(_167);
var _170=_169(endY);
if(_16f!==null&&_170!==null){
var _171=_168[_16f].getOffsetFromPosition(_166);
var _172=_168[_170].getOffsetFromPosition(endX);
var _173=_16f*this.getWidth()+_171;
var _174=_170*this.getWidth()+_172;
this.select(_173,_174);
}
}
};

