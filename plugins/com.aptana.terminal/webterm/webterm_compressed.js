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
function getCSSRule(_f){
var _10=null;
var _11=document.styleSheets[0];
var _12;
if(_11.cssRules){
_12=_11.cssRules;
}else{
if(_11.rules){
_12=_11.rules;
}
}
for(var i=0;i<_12.length;i++){
if(_12[i].selectorText==_f){
_10=_12[i];
break;
}
}
return _10;
};
function getWindowWidth(){
var _13=0;
if(window.innerWidth){
_13=window.innerWidth;
}else{
if(document.documentElement&&document.documentElement.clientWidth){
_13=document.documentElement.clientWidth;
}else{
if(document.body&&document.body.clientWidth){
_13=document.body.clientWidth;
}
}
}
return _13;
};
function getWindowHeight(){
var _14=0;
if(window.innerHeight){
_14=window.innerHeight;
}else{
if(document.documentElement&&document.documentElement.clientHeight){
_14=document.documentElement.clientHeight;
}else{
if(document.body&&document.body.clientHeight){
_14=document.body.clientHeight;
}
}
}
return _14;
};
function getQuery(){
var _15=window.location.search.substring(1);
var _16=_15.split("&");
var _17={};
for(var i=0;i<_16.length;i++){
var _18=_16[i].split("=");
var key=_18[0];
var _19=_18[1];
_17[key]=_19;
}
return _17;
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
function protectedClone(_1a){
var f=function(){
};
f.prototype=_1a;
var _1b=new f();
_1b.$parent=_1a;
return _1b;
};
function dragger(_1c,_1d){
var _1e=null,_1f=null;
var _20,_21;
var _22=null,_23=null;
var _24=function(e){
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
var _25=function(e){
if(e){
e.cancelBubble=true;
if(e.stopPropagtion){
e.stopPropagation();
}
}
return true;
};
var _26=function(e){
if(isFunction(_1d)){
var _27=_28(_1c);
var _29=_27[0];
var _2a=_27[1];
var _2b=false;
if(_1e===null||_1f===null){
_1e=e.clientX-_29;
_1f=e.clientY-_2a;
_2b=true;
}
_20=e.clientX-_29;
_21=e.clientY-_2a;
if(_2b||_22!=_20||_23!=_21){
_1d(_1e,_1f,_20,_21);
_22=_20;
_23=_21;
}
}
return _24(e);
};
var _28=function(obj){
var _2c=curtop=0;
if(obj.offsetParent){
do{
_2c+=obj.offsetLeft;
curtop+=obj.offsetTop;
}while(obj=obj.offsetParent);
}
return [_2c,curtop];
};
var _2d=function(e){
if(e.button!=0){
return _24(e);
}
window.focus();
_1c.removeEventListener("mousedown",_2d,false);
_1c.addEventListener("mousemove",_2e,false);
_1c.addEventListener("mouseup",_2f,false);
return _26(e);
};
var _2e=function(e){
return _26(e);
};
var _2f=function(e){
_1c.removeEventListener("mousemove",_2e,false);
_1c.removeEventListener("mouseup",_2f,false);
_1c.addEventListener("mousedown",_2d,false);
var _30=_26(e);
_1e=_1f=_20=_21=_22=_23=null;
return _30;
};
if(isDefined(_1c)){
_1c.addEventListener("mousedown",_2d,false);
}
};
FontInfo.MONOSPACE="monospace";
function FontInfo(){
this._rootNode=document.getElementById("fontInfo");
this._rootNode.className="webterm";
this._termNode=document.createElement("pre");
this._rootNode.appendChild(this._termNode);
this.reset();
this._useTracking;
};
FontInfo.prototype.getCharacterHeight=function(c,_31){
var _32=this.getCharacterSize(c,_31);
return _32[1];
};
FontInfo.prototype.getCharacterSize=function(c,_33){
var _34=this._characterSizes;
_33=(this._useTracking)?"normal":_33;
if(this.isMonospaced()){
c=FontInfo.MONOSPACE;
}else{
if(_34.hasOwnProperty(c)===false){
var _35=this._termNode;
var _36=function(_37){
_35.innerHTML=c;
_35.style.fontWeight=_37;
var _38=[_35.clientWidth,_35.clientHeight];
_35.innerHTML="";
return _38;
};
_34[c]={normal:_36("normal"),bold:_36("bolder")};
}
}
return _34[c][_33||"normal"];
};
FontInfo.prototype.getCharacterWidth=function(c,_39){
var _3a=this.getCharacterSize(c,_39);
return _3a[0];
};
FontInfo.prototype.getTracking=function(){
var _3b=0;
if(this.isMonospaced){
var M=this._characterSizes.M;
_3b=M.normal[0]-M.bold[0];
}
return _3b+"px";
};
FontInfo.prototype.isMonospaced=function(){
return this._characterSizes.hasOwnProperty(FontInfo.MONOSPACE);
};
FontInfo.prototype.reset=function(){
this._characterSizes={};
var _3c=this.getCharacterSize(" ");
var i=this.getCharacterSize("i");
var M=this.getCharacterSize("M");
if(_3c[0]==i[0]&&i[0]==M[0]){
this._characterSizes[FontInfo.MONOSPACE]={normal:M,bold:this.getCharacterSize("M","bold")};
}
};
FontInfo.prototype.useTracking=function(_3d){
this._useTracking=_3d;
};
Attribute.DEFAULT_BACKGROUND="b";
Attribute.DEFAULT_FOREGROUND="f";
function Attribute(){
this.reset();
};
Attribute.prototype.copy=function(){
var _3e=new Attribute();
_3e.foreground=this.foreground;
_3e.background=this.background;
_3e.bold=this.bold;
_3e.italic=this.italic;
_3e.underline=this.underline;
_3e.inverse=this.inverse;
_3e.strikethrough=this.strikethrough;
_3e.blink=this.blink;
_3e.selected=this.selected;
return _3e;
};
Attribute.prototype.equals=function(_3f){
var _40=false;
if(_3f instanceof Attribute){
_40=this===_3f||(this.foreground==_3f.foreground&&this.background==_3f.background&&this.bold==_3f.bold&&this.italic==_3f.italic&&this.underline==_3f.underline&&this.inverse==_3f.inverse&&this.strikethrough==_3f.strikethrough&&this.blink==_3f.blink&&this.selected==_3f.selected);
}
return _40;
};
Attribute.prototype.getStartingHTML=function(){
var _41=[];
var _42=(isNumber(this.background))?this.background:Attribute.DEFAULT_BACKGROUND;
var _43=(isNumber(this.foreground))?this.foreground:Attribute.DEFAULT_FOREGROUND;
if(this.inverse){
_41.push("f"+_42);
_41.push("b"+((this.selected)?"s":_43));
}else{
_41.push("f"+_43);
_41.push("b"+((this.selected)?"s":_42));
}
if(this.bold){
_41.push("b");
}
if(this.italic){
_41.push("i");
}
if(this.underline){
_41.push("u");
}else{
if(this.strikethrough){
_41.push("lt");
}else{
if(this.blink){
_41.push("bl");
}
}
}
return "<span class=\""+_41.join(" ")+"\">";
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
function Range(_44,_45){
if(isNumber(_44)===false){
_44=0;
}
if(isNumber(_45)===false){
_45=0;
}
this.startingOffset=Math.min(_44,_45);
this.endingOffset=Math.max(_44,_45);
};
Range.prototype.clamp=function(_46){
var _47;
if(this.isOverlapping(_46)){
_47=new Range(Math.max(this.startingOffset,_46.startingOffset),Math.min(this.endingOffset,_46.endingOffset));
}else{
_47=new Range(0,0);
}
return _47;
};
Range.prototype.contains=function(_48){
return this.startingOffset<=_48&&_48<this.endingOffset;
};
Range.prototype.isEmpty=function(){
return this.startingOffset===this.endingOffset;
};
Range.prototype.isOverlapping=function(_49){
var _4a=this.startingOffset;
var _4b=_49.startingOffset;
var _4c=this.endingOffset-1;
var _4d=_49.endingOffset-1;
return (_4b<=_4a&&_4a<=_4d||_4b<=_4c&&_4c<=_4d||_4a<=_4b&&_4b<=_4c||_4a<=_4d&&_4d<=_4c);
};
Range.prototype.merge=function(_4e){
return new Range(Math.min(this.startingOffset,_4e.startingOffset),Math.max(this.endingOffset,_4e.endingOffset));
};
Range.prototype.move=function(_4f){
return new Range(this.startingOffset+_4f,this.endingOffset+_4f);
};
Line.DEFAULT_WIDTH=80;
Line.MIN_WIDTH=20;
Line.MAX_WIDTH=512;
function Line(_50,_51){
if(isNumber(_50)){
_50=clamp(_50,Line.MIN_WIDTH,Line.MAX_WIDTH);
}else{
_50=Line.DEFAULT_WIDTH;
}
this._fontInfo=_51;
this._chars=new Array(_50);
this._attributes=new Array(_50);
this.clear();
this._lastInfo=null;
this._lastCursorOffset=null;
};
Line.prototype.clear=function(ch){
ch=(isCharacter(ch))?ch.charAt(0):" ";
for(var i=0;i<this._chars.length;i++){
this._chars[i]=ch;
this._attributes[i]=new Attribute();
}
this.clearCache();
};
Line.prototype.clearCache=function(){
this._lastInfo=null;
this._lastCursorOffset=null;
};
Line.prototype.clearLeft=function(_52){
if(isNumber(_52)&&0<=_52&&_52<this._chars.length){
for(var i=0;i<=_52;i++){
this._chars[i]=" ";
this._attributes[i]=new Attribute();
}
this.clearCache();
}
};
Line.prototype.clearRight=function(_53){
if(isNumber(_53)&&0<=_53&&_53<this._chars.length){
for(var i=_53;i<this._chars.length;i++){
this._chars[i]=" ";
this._attributes[i]=new Attribute();
}
this.clearCache();
}
};
Line.prototype.clearSelection=function(){
var _54=this._attributes;
var _55=_54.length;
var _56=false;
for(var i=0;i<_55;i++){
var _57=_54[i];
if(_57.selected){
_56=true;
}
_57.selected=false;
}
if(_56){
this.clearCache();
}
};
Line.prototype.deleteCharacter=function(_58,_59){
if(isNumber(_58)){
var _5a=this._chars.length;
_59=(isNumber(_59))?_59:1;
if(_59>0&&0<=_58&&_58<_5a){
if(_58+_59>_5a){
_59=_5a-_58;
}
this._chars.splice(_58,_59);
this._attributes.splice(_58,_59);
for(var i=0;i<_59;i++){
this._chars.push(" ");
this._attributes.push(new Attribute());
}
this.clearCache();
}
}
};
Line.prototype.getHTMLInfo=function(_5b,_5c){
if(this._lastInfo===null||this._lastCursorOffset!==_5c){
var _5d=[];
for(var i=0;i<this._chars.length;i++){
var ch=this._chars[i];
var _5e=this._attributes[i];
if(_5e&&_5e.equals(_5b)==false){
if(_5b!==null){
_5d.push(_5b.getEndingHTML());
}
_5d.push(_5e.getStartingHTML());
_5b=_5e;
}
if(i===_5c){
_5d.push("<span class=\"cursor\">");
}
switch(ch){
case "&":
_5d.push("&amp;");
break;
case "<":
_5d.push("&lt;");
break;
case ">":
_5d.push("&gt;");
break;
case " ":
_5d.push("&nbsp;");
break;
default:
_5d.push(ch);
break;
}
if(i===_5c){
_5d.push("</span>");
}
}
this._lastInfo={html:_5d.join(""),attribute:_5b};
this._lastCursorOffset=_5c;
}
return this._lastInfo;
};
Line.prototype.getLastNonWhiteOffset=function(){
var _5f=0;
var _60=this._chars.length;
for(var i=_60-1;i>=0;i--){
if(this._chars[i].match(/\S/)){
_5f=i+1;
break;
}
}
return _5f;
};
Line.prototype.getLineHeight=function(){
var _61=this._chars;
var _62=this._attributes;
var _63=_61.length;
var _64=0;
for(var i=0;i<_63;i++){
var ch=_61[i];
var _65=_62[i];
var _66=(_65.bold)?"bold":"normal";
var _67=this._fontInfo.getCharacterHeight(ch,_66);
_64=Math.max(_64,_67);
}
return _64;
};
Line.prototype.getOffsetFromPosition=function(x){
var _68=0;
var _69;
if(this._fontInfo.isMonospaced()){
var _6a=this._fontInfo.getCharacterWidth("M");
_69=Math.floor(x/_6a);
}else{
var _6b=this._chars;
var _6c=this._attributes;
var _6d=_6b.length;
for(var i=0;i<_6d;i++){
var ch=_6b[i];
var _6e=_6c[i];
var _6f=(_6e.bold)?"bold":"normal";
var _70=_68+this._fontInfo.getCharacterWidth(ch,_6f);
if(_68<=x&&x<_70){
_69=i;
break;
}else{
_68=_70;
}
}
}
return _69;
};
Line.prototype.getSelectedText=function(){
var _71=this._chars;
var _72=this._attributes;
var _73=Math.min(this.getLastNonWhiteOffset(),_72.length);
var _74=null;
for(var i=0;i<_73;i++){
if(_72[i].selected){
if(_74===null){
_74=[];
}
_74.push(_71[i]);
}
}
return (_74!==null)?_74.join(""):null;
};
Line.prototype.getWidth=function(){
return this._chars.length;
};
Line.prototype.insertCharacter=function(ch,_75,_76){
if(isCharacter(ch)&&isNumber(_75)){
var _77=this._chars.length;
_76=(isNumber(_76))?_76:1;
if(_76>0&&0<=_75&&_75<_77){
ch=ch.charAt(0);
if(_75+_76>_77){
_76=_77-_75;
}
this._chars.splice(_77-_76,_76);
this._attributes.splice(_77-_76,_76);
var _78=new Array(_76);
var _79=new Array(_76);
for(var i=0;i<_76;i++){
this._chars.splice(_75+i,0,ch);
this._attributes.splice(_75+i,0,new Attribute());
}
this.clearCache();
}
}
};
Line.prototype.putCharacter=function(ch,_7a,_7b){
if(isCharacter(ch)&&isDefined(_7a)&&_7a.constructor==Attribute&&isNumber(_7b)){
if(0<=_7b&&_7b<this._chars.length){
this._chars[_7b]=ch.charAt(0);
this._attributes[_7b]=_7a;
this.clearCache();
}
}
};
Line.prototype.resize=function(_7c){
if(isNumber(_7c)){
var _7d=this._chars.length;
if(Line.MIN_WIDTH<=_7c&&_7c<=Line.MAX_WIDTH&&_7d!=_7c){
this._chars.length=_7c;
if(_7c>_7d){
for(var i=_7d;i<_7c;i++){
this._chars[i]=" ";
this._attributes[i]=new Attribute();
}
}
this.clearCache();
}
}
};
Line.prototype.select=function(_7e,_7f){
var _80=(_7f)?this._chars.length:this.getLastNonWhiteOffset();
var _81=new Range(0,_80);
var _7e=_7e.clamp(_81);
var _82=this._attributes;
var _80=_7e.endingOffset;
var _83=false;
for(var i=_7e.startingOffset;i<_80;i++){
var _84=_82[i].copy();
_84.selected=true;
_82[i]=_84;
_83=true;
}
if(_83){
this.clearCache();
}
return _83;
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
var _85=this;
this._queue=[];
this._applicationKeys=false;
this._playbackState=KeyHandler.STOPPED;
this.clearEvents();
this._playbackID=null;
document.onkeypress=function(e){
return _85.processKeyPress(e);
};
document.onkeydown=function(e){
return _85.processKeyDown(e);
};
};
KeyHandler.prototype.addEvent=function(_86,e){
if(e){
var _87={};
switch(_86){
case KeyHandler.KEY_DOWN:
_87.keyCode=e.keyCode;
_87.ctrlKey=e.ctrlKey;
_87.altKey=e.altKey;
_87.shiftKey=e.shiftKey;
break;
case KeyHandler.KEY_PRESS:
_87.keyCode=e.keyCode;
_87.which=e.which;
_87.ctrlKey=e.ctrlKey;
_87.altKey=e.altKey;
_87.metaKey=e.metaKey;
break;
default:
return;
}
this._events.keys.push({time:new Date().getTime(),type:_86,event:_87});
}
};
KeyHandler.prototype.addKeys=function(_88){
this._queue.push(_88);
if(isDefined(this.callback)){
this.callback(true);
}
};
KeyHandler.prototype.clearEvents=function(){
this._events={user_agent:{browser:BrowserDetect.browser,version:BrowserDetect.version,os:BrowserDetect.OS},keys:[]};
};
KeyHandler.prototype.dequeueAll=function(){
var _89=this._queue.join("");
this._queue.length=0;
return _89;
};
KeyHandler.prototype.hasContent=function(){
return this._queue.length>0;
};
KeyHandler.prototype.play=function(_8a){
if(this._playbackState!=KeyHandler.PLAYING){
this._playbackState=KeyHandler.PLAYING;
_8a=_8a||this._events.keys;
var _8b=this;
var i=0;
var _8c=function(){
var _8d=_8a[i++];
switch(_8d.type){
case KeyHandler.KEY_DOWN:
_8b.processKeyDown(_8d.event);
break;
case KeyHandler.KEY_PRESS:
_8b.processKeyPress(_8d.event);
break;
default:
break;
}
if(_8b._playbackState==KeyHandler.PLAYING&&i<_8a.length){
var _8e=clamp(_8a[i].time-_8d.time,0,1000);
this._playbackID=window.setTimeout(_8c,_8e);
}
};
_8c();
}
};
KeyHandler.prototype.processKeyDown=function(e){
if(!e){
e=window.event;
}
if(this._playbackState==KeyHandler.RECORDING){
this.addEvent(KeyHandler.KEY_DOWN,e);
}
var _8f=e.keyCode;
var _90=null;
var _91=this._applicationKeys;
if(BrowserDetect.browser=="Firefox"&&(e.keyCode==8||(37<=e.keyCode&&e.keyCode<=40))){
}else{
switch(_8f){
case 8:
_90=KeyHandler.BACKSPACE;
break;
case 9:
_90=KeyHandler.TAB;
break;
case 27:
_90=KeyHandler.ESCAPE;
break;
case 33:
_90=KeyHandler.PAGE_UP;
break;
case 34:
_90=KeyHandler.PAGE_DOWN;
break;
case 35:
_90=(_91)?KeyHandler.APP_END:KeyHandler.END;
break;
case 36:
_90=(_91)?KeyHandler.APP_HOME:KeyHandler.HOME;
break;
case 37:
_90=(_91)?KeyHandler.APP_LEFT:KeyHandler.LEFT;
break;
case 38:
_90=(_91)?KeyHandler.APP_UP:KeyHandler.UP;
break;
case 39:
_90=(_91)?KeyHandler.APP_RIGHT:KeyHandler.RIGHT;
break;
case 40:
_90=(_91)?KeyHandler.APP_DOWN:KeyHandler.DOWN;
break;
case 45:
_90=KeyHandler.INSERT;
break;
case 46:
_90=KeyHandler.DELETE;
break;
case 112:
_90=e.shiftKey?KeyHandler.F13:KeyHandler.F1;
break;
case 113:
_90=e.shiftKey?KeyHandler.F14:KeyHandler.F2;
break;
case 114:
_90=e.shiftKey?KeyHandler.F15:KeyHandler.F3;
break;
case 115:
_90=e.shiftKey?KeyHandler.F16:KeyHandler.F4;
break;
case 116:
_90=e.shiftKey?KeyHandler.F17:KeyHandler.F5;
break;
case 117:
_90=e.shiftKey?KeyHandler.F18:KeyHandler.F6;
break;
case 118:
_90=e.shiftKey?KeyHandler.F19:KeyHandler.F7;
break;
case 119:
_90=e.shiftKey?KeyHandler.F20:KeyHandler.F8;
break;
case 120:
_90=KeyHandler.F9;
break;
case 121:
_90=KeyHandler.F10;
break;
case 122:
_90=KeyHandler.F11;
break;
case 123:
_90=KeyHandler.F12;
break;
default:
if(!e.ctrlKey||(e.ctrlKey&&e.altKey)||(e.keyCode==17)){
}else{
if(e.shiftKey){
switch(_8f){
case 50:
_90=String.fromCharCode(0);
break;
case 54:
_90=String.fromCharCode(30);
break;
case 94:
_90=String.fromCharCode(30);
break;
case 109:
_90=String.fromCharCode(31);
break;
default:
break;
}
}else{
switch(_8f){
case 32:
_90=String.fromCharCode(0);
break;
case 190:
_90=String.fromCharCode(30);
break;
case 219:
_90=String.fromCharCode(27);
break;
case 220:
_90=String.fromCharCode(28);
break;
case 221:
_90=String.fromCharCode(29);
break;
default:
if(65<=_8f&&_8f<=90){
_90=String.fromCharCode(_8f-64);
}
break;
}
}
}
break;
}
}
if(_90!==null){
this.addKeys(_90);
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
var _92=this._applicationKeys;
var _93=null;
switch(e.keyCode){
case 8:
_93=KeyHandler.BACKSPACE;
break;
case 37:
_93=(_92)?KeyHandler.APP_LEFT:KeyHandler.LEFT;
break;
case 38:
_93=(_92)?KeyHandler.APP_UP:KeyHandler.UP;
break;
case 39:
_93=(_92)?KeyHandler.APP_RIGHT:KeyHandler.RIGHT;
break;
case 40:
_93=(_92)?KeyHandler.APP_DOWN:KeyHandler.DOWN;
break;
}
if(_93!==null){
this.addKeys(_93);
}
}else{
if((e.ctrlKey&&!e.altKey)||(e.which==0)||(e.keyCode==8)||(e.keyCode==16)){
}else{
var _94;
if(e.keyCode){
_94=e.keyCode;
}
if(e.which){
_94=e.which;
}
if(e.altKey&&!e.ctrlKey){
this.addKeys(KeyHandler.ESCAPE);
}
this.addKeys(String.fromCharCode(_94));
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
KeyHandler.prototype.setApplicationKeys=function(_95){
if(isBoolean(_95)){
this._applicationKeys=_95;
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
function XTermHandler(_96){
this._term=_96;
this._insertMode=false;
this._missingCommands={};
};
XTermHandler.prototype.BEL=function(_97,_98){
};
XTermHandler.prototype.BS=function(_99,_9a){
var col=this._term.getColumn()-1;
col=Math.max(0,col);
this._term.setColumn(col);
};
XTermHandler.prototype.CHA=function(_9b,_9c){
var _9d=0;
if(_9c.length>0){
_9d=_9c-1;
}
this._term.setColumn(_9d);
};
XTermHandler.prototype.CR=function(_9e,_9f){
this._term.setColumn(0);
};
XTermHandler.prototype.CUB=function(_a0,_a1){
var _a2=1;
if(_a1.length>0){
_a2=_a1-0;
if(_a2==0){
_a2=1;
}
}
var col=this._term.getColumn()-_a2;
col=Math.max(0,col);
this._term.setColumn(col);
};
XTermHandler.prototype.CUD=function(_a3,_a4){
var _a5=1;
if(_a4.length>0){
_a5=_a4-0;
if(_a5==0){
_a5=1;
}
}
var _a6=this._term.getRow();
var _a7=this._term.getScrollRegion().bottom;
var _a8;
if(_a6<=_a7){
_a8=Math.min(_a6+_a5,_a7);
}else{
_a8=Math.min(_a6+_a5,this._term.getHeight()-1);
}
this._term.setRow(_a8);
};
XTermHandler.prototype.CUF=function(_a9,_aa){
var _ab=1;
if(_aa.length>0){
_ab=_aa-0;
if(_ab==0){
_ab=1;
}
}
var col=this._term.getColumn()+_ab;
col=Math.min(col,this._term.getWidth()-1);
this._term.setColumn(col);
};
XTermHandler.prototype.CUP=function(_ac,_ad){
var row=0;
var col=0;
var _ae=this._term.getHeight();
if(_ad.length>0){
var _af=_ad.split(/;/);
var row=_af[0]-1;
var col=_af[1]-1;
}
if(row>=_ae){
var _b0=_ae-row;
row=_ae-1;
this._term.scrollUp(_b0);
}
this._term.setPosition(row,col);
};
XTermHandler.prototype.CUU=function(_b1,_b2){
var _b3=1;
if(_b2.length>0){
_b3=_b2-0;
if(_b3==0){
_b3=1;
}
}
var _b4=this._term.getRow();
var _b5=this._term.getScrollRegion().top;
var _b6;
if(_b5<=_b4){
_b6=Math.max(_b5,_b4-_b3);
}else{
_b6=Math.max(0,_b4-_b3);
}
this._term.setRow(_b6);
};
XTermHandler.prototype.DCH=function(_b7,_b8){
var _b9=_b8-0;
this._term.deleteCharacter(_b9);
};
XTermHandler.prototype.DECALN=function(_ba,_bb){
this._term.clear("E");
};
XTermHandler.prototype.DECRC=function(_bc,_bd){
this._term.popPosition();
};
XTermHandler.prototype.DECPAM=function(_be,_bf){
this._term.setApplicationKeys(true);
};
XTermHandler.prototype.DECPNM=function(_c0,_c1){
this._term.setApplicationKeys(false);
};
XTermHandler.prototype.DECRST=function(_c2,_c3){
var _c4=_c3-0;
switch(_c4){
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
this.genericHandler(_c2,_c3);
break;
}
};
XTermHandler.prototype.DECSC=function(_c5,_c6){
this._term.pushPosition();
};
XTermHandler.prototype.DECSET=function(_c7,_c8){
var _c9=_c8-0;
switch(_c9){
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
this.genericHandler(_c7,_c8);
break;
}
};
XTermHandler.prototype.DECSTBM=function(_ca,_cb){
var _cc=_cb.split(/;/);
var top=_cc[0]-1;
var _cd=_cc[1]-1;
this._term.setScrollRegion(top,0,_cd,this._term.getWidth()-1);
};
XTermHandler.prototype.DL=function(_ce,_cf){
var _d0=1;
if(_cf.length>0){
_d0=_cf-0;
if(_d0==0){
_d0=1;
}
}
this._term.deleteLine(_d0);
};
XTermHandler.prototype.ED=function(_d1,_d2){
var _d3=_d2-0;
switch(_d3){
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
this.genericHandler(_d1+":"+_d2,"");
break;
}
};
XTermHandler.prototype.EL=function(_d4,_d5){
var _d6=_d5-0;
switch(_d6){
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
this.genericHandler(_d4+":"+_d5,"");
break;
}
};
XTermHandler.prototype.genericHandler=function(_d7,_d8){
if(this._missingCommands.hasOwnProperty(_d7)===false){
this._missingCommands[_d7]=0;
}
this._missingCommands[_d7]++;
};
XTermHandler.prototype.getMissingCommands=function(){
return this._missingCommands;
};
XTermHandler.prototype.HVP=XTermHandler.prototype.CUP;
XTermHandler.prototype.ICH=function(_d9,_da){
var _db=_da-0;
this._term.insertCharacter(" ",_db);
};
XTermHandler.prototype.IL=function(_dc,_dd){
var _de=1;
if(_dd.length>0){
_de=_dd-0;
if(_de==0){
_de=1;
}
}
this._term.insertLine(_de);
};
XTermHandler.prototype.IND=function(_df,_e0){
var _e1=this._term.getRow();
var _e2=this._term.getScrollRegion().bottom;
var _e3=_e1+1;
if(_e1<=_e2){
this._term.setRow(_e3);
}else{
this._term.scrollUp(1);
this._term.setRow(_e2);
}
};
XTermHandler.prototype.LF=function(_e4,_e5){
var _e6=this._term;
var row=_e6.getRow()+1;
var _e7=_e6.getScrollRegion().bottom;
if(row>_e7){
_e6.scrollUp();
row=_e7;
}
_e6.setPosition(row,0);
};
XTermHandler.prototype.NEL=XTermHandler.prototype.LF;
XTermHandler.prototype.processCharacter=function(_e8,_e9){
if(this._insertMode){
this._term.insertCharacter(" ",1);
}
this._term.displayCharacters(_e9);
};
XTermHandler.prototype.RI=function(_ea,_eb){
var _ec=this._term.getRow();
var _ed=this._term.getScrollRegion().top;
var _ee=_ec-1;
if(_ed<=_ee){
this._term.setRow(_ee);
}else{
this._term.scrollDown(1);
this._term.setRow(_ed);
}
};
XTermHandler.prototype.RM=function(_ef,_f0){
var _f1=_f0-0;
switch(_f1){
case 4:
this._insertMode=false;
break;
case 2:
case 12:
case 20:
default:
this.genericHandler(_ef,_f0);
break;
}
};
XTermHandler.prototype.SD=function(_f2,_f3){
var _f4=1;
if(_f3.length>0){
_f4=_f3-0;
}
var _f5=this._term.getRow();
var _f6=this._term.getScrollRegion().top;
var _f7=_f5-_f4;
if(_f6<=_f7){
this._term.setRow(_f7);
}else{
this._term.scrollDown(_f4);
this._term.setRow(_f6);
}
};
XTermHandler.prototype.SET_TEXT_PARAMS=function(_f8,_f9){
var _fa=_f9.split(/;/);
var _fb=_fa[0]-0;
var _fc=_fa[1];
if(_fb==0){
this._term.setTitle(_fc);
}else{
this.genericHandler(_f8+":"+_f9,"");
}
};
XTermHandler.prototype.SET_TEXT_PARAMS2=XTermHandler.prototype.SET_TEXT_PARAMS;
XTermHandler.prototype.SGR=function(_fd,_fe){
var _ff=this._term.getCurrentAttribute();
var _100=_fe.split(/;/);
for(var i=0;i<_100.length;i++){
var _101=_100[i]-0;
if(_101<50){
var tens=Math.floor(_101/10);
var ones=_101%10;
switch(tens){
case 0:
switch(ones){
case 0:
_ff.reset();
break;
case 1:
_ff.bold=true;
break;
case 3:
_ff.italic=true;
break;
case 4:
_ff.underline=true;
break;
case 7:
_ff.inverse=true;
break;
case 9:
_ff.strikethrough=true;
break;
default:
this.genericHandler(_fd+":"+_fe,"");
break;
}
break;
case 2:
switch(ones){
case 2:
_ff.bold=false;
break;
case 3:
_ff.italic=false;
break;
case 4:
_ff.underline=false;
break;
case 7:
_ff.inverse=false;
break;
case 9:
_ff.strikethough=false;
break;
default:
this.genericHandler(_fd+":"+_fe,"");
break;
}
break;
case 3:
switch(ones){
case 0:
case 1:
case 2:
case 3:
case 4:
case 5:
case 6:
case 7:
_ff.foreground=ones;
break;
case 9:
_ff.resetForeground();
break;
default:
this.genericHandler(_fd+":"+_fe,"");
break;
}
break;
case 4:
switch(ones){
case 0:
case 1:
case 2:
case 3:
case 4:
case 5:
case 6:
case 7:
_ff.background=ones;
break;
case 9:
_ff.resetBackground();
break;
default:
this.genericHandler(_fd+":"+_fe,"");
break;
}
break;
default:
this.genericHandler(_fd+":"+_fe,"");
break;
}
}else{
this.genericHandler(_fd+":"+_fe,"");
}
}
this._term.setCurrentAttribute(_ff);
};
XTermHandler.prototype.SM=function(_102,_103){
var mode=_103-0;
switch(mode){
case 4:
this._insertMode=true;
break;
case 2:
case 12:
case 20:
default:
this.genericHandler(_102,_103);
break;
}
};
XTermHandler.prototype.SU=function(_104,_105){
var _106=1;
if(_105.length>0){
_106=_105-0;
}
var _107=this._term.getRow();
var _108=this._term.getScrollRegion().bottom;
var _109=_107+_106;
if(_107<=_108){
this._term.setRow(_109);
}else{
this._term.scrollUp(_106);
this._term.setRow(_108);
}
};
XTermHandler.prototype.TAB=function(_10a,_10b){
var _10c=this._term.getColumn();
var _10d=8-(_10c%8);
this._term.displayCharacters(new Array(_10d+1).join(" "));
};
XTermHandler.prototype.VPA=function(_10e,_10f){
var row=0;
if(_10f.length>0){
row=_10f-1;
}
this._term.setRow(row);
};
XTermHandler.prototype.VT=XTermHandler.prototype.LF;
function TermParser(_110,_111){
if(_110===null||_110===undefined){
throw new Error("Parsing tables must be defined when creating a new TermParser");
}
this._processTables(_110);
this._actions=_110.actions;
this._nodes=_110.nodes;
this.setHandler(_111);
this.singleStep=false;
this.offset=-1;
};
TermParser.prototype.getHandler=function(){
return this._handler;
};
TermParser.prototype.parse=function(_112){
var _113=0;
var _114=isString(_112)?_112.length:0;
while(_113<_114){
var _115=0;
var _116=this._nodes[_115][1];
var _117=(_116==-1)?-2:_113;
for(var i=_113;i<_114;i++){
var _118=this._nodes[_115];
if(_118){
var _119=_112.charCodeAt(i);
var _11a=_118[0][_119];
if(_11a!=-1){
_115=_11a;
var _11b=this._nodes[_115][1];
if(_11b!=-1){
_117=i;
_116=_11b;
}
}else{
break;
}
}
}
if(_116==-1){
if(this._handler!=null){
if(this._handler.processCharacter){
this._handler.processCharacter("processCharacter",_112.charAt(_113));
}
}
_113++;
}else{
var _11c=_117+1;
if(this._handler!=null){
var info=this._actions[_116];
var _11d=info[0];
var _11e="";
if(info.length>=3&&info[1]!=-1&&info[2]!=-1){
_11e=_112.substring(_113+info[1],_11c-info[2]);
}
this._handler[_11d](_11d,_11e);
}
_113=_11c;
if(this.singleStep){
this.offset=_113;
break;
}
}
}
};
TermParser.prototype._processTables=function(_11f){
if(_11f.hasOwnProperty("processed")==false||_11f.processed==false){
switch(_11f.format){
case "expanded":
break;
case "rle":
var mos=new Array(256);
for(var i=0;i<mos.length;i++){
mos[i]=-1;
}
var _120=_11f.nodes;
for(var i=0;i<_120.length;i++){
var _121=_120[i][0];
var _122=[];
for(var j=0;j<_121.length;j++){
var _123=_121[j];
if(_123<0){
_122=_122.concat(mos.slice(0,-_123));
}else{
var _124=_123>>8;
var _125=(_123&255)+1;
for(var k=0;k<_125;k++){
_122.push(_124);
}
}
}
_120[i][0]=_122;
}
break;
default:
break;
}
_11f.processed=true;
}
};
TermParser.prototype.setHandler=function(_126){
var _127=null;
if(_126){
var _128=null;
var _129=function(_12a,_12b){
};
for(var i=0;i<this._actions.length;i++){
var _12c=this._actions[i];
var _12d=_12c[0];
if(!_126[_12d]){
if(_127==null){
_127=protectedClone(_126);
if(!_126.genericHandler){
_128=_129;
}else{
_128=_126.genericHandler;
}
}
_127[_12d]=_128;
}
}
}
if(_127==null){
this._handler=_126;
}else{
this._handler=_127;
}
};
TermComm.POLLING_INTERVAL_MIN=125;
TermComm.POLLING_INTERVAL_MAX=2000;
TermComm.POLLING_GROWTH_RATE=2;
TermComm.DEFAULT_REQUEST_URL="/stream";
TermComm.DEFAULT_GET_UNIQUE_ID_URL="/id";
function TermComm(_12e,_12f){
var self=this;
this.terminal=_12e;
this.keyHandler=_12e.getKeyHandler();
this.keyHandler.callback=function(){
self.sendKeys();
};
this.minInterval=125;
this.maxInterval=2000;
this.growthRate=2;
this.timeoutInterval=5000;
this.requestURL=TermComm.DEFAULT_REQUEST_URL;
this.getUniqueIdURL=TermComm.DEFAULT_GET_UNIQUE_ID_URL;
if(isDefined(_12f)){
if(_12f.hasOwnProperty("minInterval")&&isNumber(_12f.minInterval)){
this.minInterval=_12f.minInterval;
}
if(_12f.hasOwnProperty("maxInterval")&&isNumber(_12f.maxInterval)){
this.maxInterval=_12f.maxInterval;
}
if(_12f.hasOwnProperty("growthRate")&&isNumber(_12f.growthRate)){
this.growthRate=_12f.growthRate;
}
if(_12f.hasOwnProperty("timeoutInterval")&&isNumber(_12f.timeoutInterval)){
this.timeoutInterval=_12f.timeoutInterval;
}
if(_12f.hasOwnProperty("requestURL")&&isString(_12f.requestURL)&&_12f.requestURL.length>0){
this.requestURL=_12f.requestURL;
}
if(_12f.hasOwnProperty("getUniqueIdURL")&&isString(_12f.getUniqueIdURL)&&_12f.getUniqueIdURL.length>0){
this.getUniqueIdURL=_12f.getUniqueIdURL;
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
var _130={id:this.terminal.getId(),cb:new Date().getTime()+":"+this.cacheBusterID++};
req.open("GET",createURL(this.requestURL,_130),true);
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
var _131={id:id};
req.open("POST",createURL(this.requestURL,_131),true);
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
TermComm.prototype.update=function(_132){
if(this.running&&this.terminal.getId()!==null){
if(isBoolean(_132)){
if(this.requestID!==null){
window.clearTimeout(this.requestID);
this.requestID=null;
}
if(_132){
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
function Term(id,_133,_134,_135){
if(isString(id)===false||id.length===0){
id="terminal";
}
this._id=(_135&&_135.hasOwnProperty("id"))?_135.id:null;
this._remainingText="";
this._rootNode=document.getElementById(id);
if(this._rootNode){
this._rootNode.className="webterm";
this._termNode=document.createElement("pre");
this._rootNode.appendChild(this._termNode);
this._width=(isNumber(_133))?clamp(_133,Line.MIN_WIDTH,Line.MAX_WIDTH):Line.DEFAULT_WIDTH;
this._height=(isNumber(_134))?clamp(_134,Term.MIN_HEIGHT,Term.MAX_HEIGHT):Term.DEFAULT_HEIGHT;
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
this._lastStartingOffset=null;
this._lastEndingOffset=null;
this._sendResizeSequence=(_135&&_135.hasOwnProperty("sendResizeSequence"))?_135.sendResizeSequence:true;
this._showTitle=(_135&&_135.hasOwnProperty("showTitle"))?_135.showTitle:true;
this._onTitleChange=(_135&&_135.hasOwnProperty("onTitleChange"))?_135.onTitleChange:null;
var _136=(_135&&_135.hasOwnProperty("handler"))?_135.handler:new XTermHandler(this);
var _137=(_135&&_135.hasOwnProperty("tables"))?_135.tables:XTermTables;
var _138=(_135&&_135.hasOwnProperty("parser"))?_135.parser:new TermParser(_137,_136);
var _139=(_135&&_135.hasOwnProperty("keyHandler"))?_135.keyHandler:new KeyHandler();
this._parser=_138;
this._keyHandler=_139;
var _13a=(_135&&_135.hasOwnProperty("commHandler"))?_135.commHandler:new TermComm(this,_135);
var _13b=(_135&&_135.hasOwnProperty("autoStart"))?_135.autoStart:true;
this._commHandler=_13a;
this.createBuffer();
var self=this;
dragger(this._rootNode,function(sx,sy,ex,ey){
self.updateSelection(sx,sy,ex,ey);
});
this.refresh();
if(_13b){
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
var _13c=new Array(this._height);
for(var i=0;i<_13c.length;i++){
_13c[i]=this.createLine();
}
this._lines=_13c;
};
Term.prototype.createLine=function(){
return new Line(this._width,this._fontInfo);
};
Term.prototype.clearSelection=function(_13d){
var _13e=this._lines;
var _13f=_13e.length;
for(var i=0;i<_13f;i++){
_13e[i].clearSelection();
}
this._hasSelection=false;
this._lastStartingOffset=null;
this._lastEndingOffset=null;
if((isBoolean(_13d))?_13d:true){
this.refresh();
}
};
Term.prototype.deleteCharacter=function(_140){
this._lines[this._row].deleteCharacter(this._column,_140);
};
Term.prototype.deleteLine=function(_141){
_141=(_141===undefined)?1:_141;
if(_141>0){
var _142=this._scrollRegion;
if(_142.left==0&&_142.right==this._width-1){
if(this._row+_141>_142.bottom){
_141=_142.bottom-this._row+1;
}
if(_141==this._height){
this.clear();
}else{
var _143=this._lines.splice(this._row,_141);
for(var i=0;i<_141;i++){
_143[i].clear();
}
if(_142.bottom+1==this.height){
this._lines=this._lines.concat(_143);
}else{
for(var i=0;i<_141;i++){
this._lines.splice(_142.bottom-_141+i+1,0,_143[i]);
}
}
}
}else{
}
}
};
Term.prototype.displayCharacters=function(_144){
if(isString(_144)){
for(var i=0;i<_144.length;i++){
var ch=_144.charAt(i);
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
var _145=null;
if(this.hasSelection()){
var _146=this._lines;
var _147=_146.length;
for(var i=0;i<_147;i++){
var _148=_146[i].getSelectedText();
if(_148!==null){
if(_145===null){
_145=[];
}
_145.push(_148);
}
}
}
return (_145!==null)?_145.join("\n"):null;
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
Term.prototype.insertCharacter=function(ch,_149){
this._lines[this._row].insertCharacter(ch,this._column,_149);
};
Term.prototype.insertLine=function(_14a){
_14a=(_14a===undefined)?1:_14a;
if(_14a>0){
var _14b=this._scrollRegion;
if(_14b.left==0&&_14b.right==this._width-1){
if(this._row+_14a>_14b.bottom){
_14a=_14b.bottom-this._row+1;
}
if(_14a==this._height){
this.clear();
}else{
var _14c=this._lines.splice(_14b.bottom-_14a+1,_14a);
for(var i=0;i<_14a;i++){
_14c[i].clear();
}
if(this._row==0){
this._lines=_14c.concat(this._lines);
}else{
for(var i=0;i<_14a;i++){
this._lines.splice(this._row+i,0,_14c[i]);
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
var _14d=this._positions.pop();
this._row=_14d[0];
this._column=_14d[1];
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
var _14e=[];
var attr=null;
var _14f=this._title+" — "+this._width+"x"+this._height;
var _150="<div class='title'>"+_14f+"</div>";
for(var row=0;row<this._height;row++){
var line=this._lines[row];
var _151=(this._cursorVisible)?(row==this._row)?this._column:-1:-1;
var _152=line.getHTMLInfo(attr,_151);
attr=_152.attribute;
_14e.push(_152.html);
}
if(attr!=null){
_14e[_14e.length-1]+=attr.getEndingHTML();
}
if(this._showTitle){
this._termNode.innerHTML=_150+_14e.join("<br />");
}else{
this._termNode.innerHTML=_14e.join("<br />");
}
};
Term.prototype.scrollDown=function(_153){
_153=(_153===undefined)?1:_153;
if(_153>0){
var _154=this._scrollRegion;
if(_154.left==0&&_154.right==this._width-1){
var _155=_154.bottom-_154.top+1;
if(_153>=_155){
this.clear();
}else{
var _156=this._lines.splice(_154.bottom-_153+1,_153);
for(var i=0;i<_153;i++){
_156[i].clear();
}
if(_154.top==0){
this._lines=_156.concat(this._lines);
}else{
for(var i=0;i<_153;i++){
this._lines.splice(_154.top+i,0,_156[i]);
}
}
}
}else{
}
}
};
Term.prototype.scrollUp=function(_157){
_157=(_157===undefined)?1:_157;
if(_157>0){
var _158=this._scrollRegion;
if(_158.left==0&&_158.right==this._width-1){
var _159=_158.bottom-_158.top+1;
if(_157>=_159){
this.clear();
}else{
var _15a=this._lines.splice(_158.top,_157);
for(var i=0;i<_157;i++){
_15a[i].clear();
}
if(_158.bottom+1==this.height){
this._lines=this._lines.concat(_15a);
}else{
for(var i=0;i<_157;i++){
this._lines.splice(_158.bottom-_157+i+1,0,_15a[i]);
}
}
}
}else{
}
}
};
Term.prototype.select=function(s,e){
var _15b=this.getWidth();
var _15c=this.getHeight();
var _15d=new Range(s,e).clamp(new Range(0,_15b*_15c));
var _15e=this.hasSelection();
var _15f=false;
if(_15e){
this.clearSelection(false);
}
if(_15d.isEmpty()===false){
var _160=0;
for(var i=0;i<_15c;i++){
var _161=_160+_15b;
var _162=new Range(_160,_161).clamp(_15d);
if(_162.isEmpty()===false){
var _163=_15d.endingOffset>_162.endingOffset;
var _164=_162.move(-_160);
var line=this._lines[i];
if(line.select(_164,_163)){
_15f=true;
}
}
_160=_161;
}
}
this._hasSelection=_15f;
if(_15e||_15f){
this.refresh();
}
};
Term.prototype.selectAll=function(){
this.select(0,this._width*this._height);
};
Term.prototype.setApplicationKeys=function(_165){
if(isBoolean(_165)){
this._keyHandler.setApplicationKeys(_165);
}
};
Term.prototype.setColumn=function(_166){
if(isNumber(_166)&&0<=_166&&_166<this._width){
this._column=_166;
}
};
Term.prototype.setCurrentAttribute=function(attr){
if(isDefined(attr)&&attr.constructor===Attribute){
this._currentAttribute=attr;
}
};
Term.prototype.setCursorVisible=function(_167){
if(isBoolean(_167)){
this._cursorVisible=_167;
}
};
Term.prototype.setHeight=function(_168){
this.setSize(this._width,_168);
};
Term.prototype.setPosition=function(row,_169){
if(isNumber(row)&&0<=row&&row<this._height){
this._row=row;
}
if(isNumber(_169)&&0<=_169&&_169<this._width){
this._column=_169;
}
};
Term.prototype.setRow=function(row){
if(0<=row&&row<this._height){
this._row=row;
}
};
Term.prototype.setScrollRegion=function(top,left,_16a,_16b){
if(isNumber(top)&&isNumber(left)&&isNumber(_16a)&&isNumber(_16b)){
if(top<_16a&&left<_16b){
var _16c=(0<=top&&top<this._height);
var _16d=(0<=left&&left<this._width);
var _16e=(0<=_16a&&_16a<this._height);
var _16f=(0<=_16b&&_16b<this._width);
if(_16c&&_16d&&_16e&&_16f){
this._scrollRegion={top:top,left:left,bottom:_16a,right:_16b};
}
}
}
};
Term.prototype.setSize=function(_170,_171){
var _172=false;
if(isNumber(_170)&&Line.MIN_WIDTH<=_170&&_170<=Line.MAX_WIDTH&&this._width!=_170){
for(var i=0;i<this._height;i++){
this._lines[i].resize(_170);
}
this._width=_170;
this._column=Math.min(this._width-1,this._column);
_172=true;
}
if(isNumber(_171)&&Term.MIN_HEIGHT<=_171&&_171<=Term.MAX_HEIGHT&&this._height!=_171){
if(_171>this._height){
for(var i=this._height;i<_171;i++){
this._lines.push(this.createLine());
}
}else{
this._lines=this._lines.splice(this._height-_171,_171);
}
this._height=_171;
this._row=Math.min(this._height-1,this._row);
_172=true;
}
if(_172){
this.setScrollRegion(0,0,this._height-1,this._width-1);
if(this._sendResizeSequence){
var ESC=String.fromCharCode(27);
var CSI=ESC+"[";
this._keyHandler.addKeys(CSI+[8,this._height,this._width].join(";")+"t");
}
}
};
Term.prototype.setTitle=function(_173){
this._title=_173;
if(isFunction(this._onTitleChange)){
this._onTitleChange(_173);
}
};
Term.prototype.showTitle=function(_174){
if(isBoolean(_174)){
this._showTitle=_174;
this.refresh();
}
};
Term.prototype.sizeToWindow=function(){
var m=this._fontInfo.getCharacterSize("M");
var _175=m[0];
var _176=m[1];
var _177=Math.floor(getWindowWidth()/_175)-1;
var _178=Math.floor(getWindowHeight()/_176);
this.setSize(_177,_178);
};
Term.prototype.toggleRunState=function(){
if(this._commHandler!==null){
if(this._id===null&&this._commHandler.isRunning()==false){
this._id=this._commHandler.getUniqueID();
}
this._commHandler.toggleRunState();
}
};
Term.prototype.setWidth=function(_179){
this.setSize(_179,this._height);
};
Term.prototype.toString=function(){
var _17a=[];
for(var i=0;i<this._lines.length;i++){
_17a.push(this._lines[i].toString());
}
return _17a.join("\n");
};
Term.prototype.updateSelection=function(_17b,_17c,endX,endY){
if(isNumber(_17b)&&isNumber(_17c)&&isNumber(endX)&&isNumber(endY)){
var _17d=this._lines;
var _17e=this._fontInfo.getCharacterHeight("M");
var _17f=function(y){
var _180=_17d.length;
var _181=0;
var _182=null;
for(var i=0;i<_180;i++){
var line=_17d[i];
var _183=_181+_17e;
if(_181<=y&&y<_183){
_182=i;
break;
}else{
_181=_183;
}
}
return _182;
};
var _184=_17f(_17c);
var _185=_17f(endY);
if(_184!==null&&_185!==null){
var _186=_17d[_184].getOffsetFromPosition(_17b);
var _187=_17d[_185].getOffsetFromPosition(endX);
var _188=_184*this.getWidth()+_186;
var _189=_185*this.getWidth()+_187;
if(this._lastStartingOffset!==_188||this._lastEndingOffset!==_189){
this.select(_188,_189);
this._lastStartingOffset=_188;
this._lastEndingOffset=_189;
}
}
}
};

