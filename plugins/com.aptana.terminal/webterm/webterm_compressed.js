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
if(_a.overrideMimeType){
_a.overrideMimeType("text/plain");
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
var _24=function(obj){
var _25=curtop=0;
if(obj.offsetParent){
do{
_25+=obj.offsetLeft;
curtop+=obj.offsetTop;
}while(obj=obj.offsetParent);
}
return [_25,curtop];
};
var _26=function(e){
if(isFunction(_1d)){
var _27=_24(_1c);
var _28=_27[0];
var _29=_27[1];
var _2a=false;
if(_1e===null||_1f===null){
_1e=e.clientX-_28;
_1f=e.clientY-_29;
_2a=true;
}
_20=e.clientX-_28;
_21=e.clientY-_29;
if(_2a||_22!=_20||_23!=_21){
_1d(_1e,_1f,_20,_21);
_22=_20;
_23=_21;
}
}
return _2b(e);
};
var _2c=function(_2d,_2e){
if(_1c.addEventListener){
_1c.addEventListener(_2d,_2e,false);
}else{
if(_1c.attachEvent){
_1c.attachEvent("on"+_2d,_2e);
}
}
};
var _2f=function(_30,_31){
if(_1c.removeEventListener){
_1c.removeEventListener(_30,_31,false);
}else{
if(_1c.detachEvent){
_1c.detachEvent("on"+_30,_31);
}
}
};
var _2b=function(e){
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
var _32=function(e){
if(e){
e.cancelBubble=true;
if(e.stopPropagtion){
e.stopPropagation();
}
}
return true;
};
var _33=function(e){
if(e.button!=0&&e.button!=1){
return _2b(e);
}
window.focus();
_2f("mousedown",_33);
_2c("mousemove",_34);
_2c("mouseup",_35);
return _26(e);
};
var _34=function(e){
return _26(e);
};
var _35=function(e){
_2f("mousemove",_34);
_2f("mouseup",_35);
_2c("mousedown",_33);
var _36=_26(e);
_1e=_1f=_20=_21=_22=_23=null;
return _36;
};
if(isDefined(_1c)){
_2c("mousedown",_33);
}
};
FontInfo.MONOSPACE="monospace";
function FontInfo(id){
id=(id)?id:"fontInfo";
this._rootNode=document.getElementById(id);
if(this._rootNode){
this._rootNode.className="fontInfo";
this._termNode=document.createElement("pre");
this._rootNode.appendChild(this._termNode);
}
this.reset();
this._useTracking;
};
FontInfo.prototype.forceSize=function(_37,_38){
this._characterSizes[FontInfo.MONOSPACE]={normal:[_37,_38],bold:[_37,_38]};
};
FontInfo.prototype.getCharacterHeight=function(c,_39){
var _3a=this.getCharacterSize(c,_39);
return _3a[1];
};
FontInfo.prototype.getCharacterSize=function(c,_3b){
var _3c=this._characterSizes;
_3b=(this._useTracking)?"normal":_3b;
if(this.isMonospaced()){
c=FontInfo.MONOSPACE;
}else{
if(_3c.hasOwnProperty(c)===false){
var _3d=this._termNode;
var _3e=function(_3f){
_3d.innerHTML=(c==" ")?"&nbsp;":c;
_3d.style.fontWeight=_3f;
var _40=[_3d.clientWidth||_3d.offsetWidth,_3d.clientHeight||_3d.offsetHeight];
_3d.innerHTML="";
return _40;
};
_3c[c]={normal:_3e("normal"),bold:_3e("bolder")};
}
}
return _3c[c][_3b||"normal"];
};
FontInfo.prototype.getCharacterWidth=function(c,_41){
var _42=this.getCharacterSize(c,_41);
return _42[0];
};
FontInfo.prototype.getTracking=function(){
var _43=0;
if(this.isMonospaced){
var M=this._characterSizes[FontInfo.MONOSPACE];
_43=M.normal[0]-M.bold[0];
}
return _43+"px";
};
FontInfo.prototype.isMonospaced=function(){
return this._characterSizes.hasOwnProperty(FontInfo.MONOSPACE);
};
FontInfo.prototype.reset=function(){
this._characterSizes={};
if(this._rootNode){
var _44=this.getCharacterSize(" ");
var i=this.getCharacterSize("i");
var M=this.getCharacterSize("M");
if(_44[0]==i[0]&&i[0]==M[0]){
this._characterSizes[FontInfo.MONOSPACE]={normal:M,bold:this.getCharacterSize("M","bold")};
}
}
};
FontInfo.prototype.useTracking=function(_45){
this._useTracking=_45;
};
Attribute.DEFAULT_BACKGROUND="b";
Attribute.DEFAULT_FOREGROUND="f";
function Attribute(){
this.reset();
};
Attribute.prototype.copy=function(){
var _46=new Attribute();
_46.foreground=this.foreground;
_46.background=this.background;
_46.bold=this.bold;
_46.italic=this.italic;
_46.underline=this.underline;
_46.inverse=this.inverse;
_46.strikethrough=this.strikethrough;
_46.blink=this.blink;
_46.selected=this.selected;
return _46;
};
Attribute.prototype.equals=function(_47){
var _48=false;
if(_47 instanceof Attribute){
_48=this===_47||(this.foreground==_47.foreground&&this.background==_47.background&&this.bold==_47.bold&&this.italic==_47.italic&&this.underline==_47.underline&&this.inverse==_47.inverse&&this.strikethrough==_47.strikethrough&&this.blink==_47.blink&&this.selected==_47.selected);
}
return _48;
};
Attribute.prototype.getStartingHTML=function(){
var _49=[];
var _4a=(isNumber(this.background))?this.background:Attribute.DEFAULT_BACKGROUND;
var _4b=(isNumber(this.foreground))?this.foreground:Attribute.DEFAULT_FOREGROUND;
if(this.inverse){
_49.push("f"+_4a);
_49.push("b"+((this.selected)?"s":_4b));
}else{
_49.push("f"+_4b);
_49.push("b"+((this.selected)?"s":_4a));
}
if(this.bold){
_49.push("b");
}
if(this.italic){
_49.push("i");
}
if(this.underline){
_49.push("u");
}else{
if(this.strikethrough){
_49.push("lt");
}else{
if(this.blink){
_49.push("bl");
}
}
}
return "<span class=\""+_49.join(" ")+"\">";
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
function Range(_4c,_4d){
if(isNumber(_4c)===false){
_4c=0;
}
if(isNumber(_4d)===false){
_4d=0;
}
this.startingOffset=Math.min(_4c,_4d);
this.endingOffset=Math.max(_4c,_4d);
};
Range.prototype.clamp=function(_4e){
var _4f;
if(this.isOverlapping(_4e)){
_4f=new Range(Math.max(this.startingOffset,_4e.startingOffset),Math.min(this.endingOffset,_4e.endingOffset));
}else{
_4f=new Range(0,0);
}
return _4f;
};
Range.prototype.contains=function(_50){
return this.startingOffset<=_50&&_50<this.endingOffset;
};
Range.prototype.isEmpty=function(){
return this.startingOffset===this.endingOffset;
};
Range.prototype.isOverlapping=function(_51){
var _52=this.startingOffset;
var _53=_51.startingOffset;
var _54=this.endingOffset-1;
var _55=_51.endingOffset-1;
return (_53<=_52&&_52<=_55||_53<=_54&&_54<=_55||_52<=_53&&_53<=_54||_52<=_55&&_55<=_54);
};
Range.prototype.merge=function(_56){
return new Range(Math.min(this.startingOffset,_56.startingOffset),Math.max(this.endingOffset,_56.endingOffset));
};
Range.prototype.move=function(_57){
return new Range(this.startingOffset+_57,this.endingOffset+_57);
};
Line.DEFAULT_WIDTH=80;
Line.MIN_WIDTH=20;
Line.MAX_WIDTH=512;
function Line(_58,_59){
if(isNumber(_58)){
_58=clamp(_58,Line.MIN_WIDTH,Line.MAX_WIDTH);
}else{
_58=Line.DEFAULT_WIDTH;
}
this._fontInfo=_59;
this._chars=new Array(_58);
this._attributes=new Array(_58);
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
Line.prototype.clearLeft=function(_5a){
if(isNumber(_5a)&&0<=_5a&&_5a<this._chars.length){
for(var i=0;i<=_5a;i++){
this._chars[i]=" ";
this._attributes[i]=new Attribute();
}
this.clearCache();
}
};
Line.prototype.clearRight=function(_5b){
if(isNumber(_5b)&&0<=_5b&&_5b<this._chars.length){
for(var i=_5b;i<this._chars.length;i++){
this._chars[i]=" ";
this._attributes[i]=new Attribute();
}
this.clearCache();
}
};
Line.prototype.clearSelection=function(){
var _5c=this._attributes;
var _5d=_5c.length;
var _5e=false;
for(var i=0;i<_5d;i++){
var _5f=_5c[i];
if(_5f.selected){
_5e=true;
}
_5f.selected=false;
}
if(_5e){
this.clearCache();
}
};
Line.prototype.deleteCharacter=function(_60,_61){
if(isNumber(_60)){
var _62=this._chars.length;
_61=(isNumber(_61))?_61:1;
if(_61>0&&0<=_60&&_60<_62){
if(_60+_61>_62){
_61=_62-_60;
}
this._chars.splice(_60,_61);
this._attributes.splice(_60,_61);
for(var i=0;i<_61;i++){
this._chars.push(" ");
this._attributes.push(new Attribute());
}
this.clearCache();
}
}
};
Line.prototype.deselect=function(_63){
var _64=new Range(0,this._chars.length);
var _63=_63.clamp(_64);
var _65=this._attributes;
var _66=_63.endingOffset;
var _67=false;
for(var i=_63.startingOffset;i<_66;i++){
var _68=_65[i];
if(_68.selected){
_68.copy();
_68.selected=false;
_65[i]=_68;
_67=true;
}
}
if(_67){
this.clearCache();
}
};
Line.prototype.getHTMLInfo=function(_69,_6a){
this.clearCache();
if(this._lastInfo===null||this._lastCursorOffset!==_6a){
var _6b=[];
for(var i=0;i<this._chars.length;i++){
var ch=this._chars[i];
var _6c=this._attributes[i];
if(_6c&&_6c.equals(_69)==false){
if(_69!==null){
_6b.push(_69.getEndingHTML());
}
_6b.push(_6c.getStartingHTML());
_69=_6c;
}
if(i===_6a){
_6b.push("<span class=\"cursor\">");
}
switch(ch){
case "&":
_6b.push("&amp;");
break;
case "<":
_6b.push("&lt;");
break;
case ">":
_6b.push("&gt;");
break;
case " ":
_6b.push("&nbsp;");
break;
default:
_6b.push(ch);
break;
}
if(i===_6a){
_6b.push("</span>");
}
}
this._lastInfo={html:_6b.join(""),attribute:_69};
this._lastCursorOffset=_6a;
}
return this._lastInfo;
};
Line.prototype.getLastNonWhiteOffset=function(){
var _6d=0;
var _6e=this._chars.length;
for(var i=_6e-1;i>=0;i--){
if(this._chars[i].match(/\S/)){
_6d=i+1;
break;
}
}
return _6d;
};
Line.prototype.getLineHeight=function(){
var _6f=this._chars;
var _70=this._attributes;
var _71=_6f.length;
var _72=0;
for(var i=0;i<_71;i++){
var ch=_6f[i];
var _73=_70[i];
var _74=(_73.bold)?"bold":"normal";
var _75=this._fontInfo.getCharacterHeight(ch,_74);
_72=Math.max(_72,_75);
}
return _72;
};
Line.prototype.getOffsetFromPosition=function(x){
var _76=0;
var _77;
if(this._fontInfo.isMonospaced()){
var _78=this._fontInfo.getCharacterWidth("M");
_77=Math.floor(x/_78);
}else{
var _79=this._chars;
var _7a=this._attributes;
var _7b=_79.length;
for(var i=0;i<_7b;i++){
var ch=_79[i];
var _7c=_7a[i];
var _7d=(_7c.bold)?"bold":"normal";
var _7e=_76+this._fontInfo.getCharacterWidth(ch,_7d);
if(_76<=x&&x<_7e){
_77=i;
break;
}else{
_76=_7e;
}
}
}
return _77;
};
Line.prototype.getSelectedText=function(){
var _7f=this._chars;
var _80=this._attributes;
var _81=Math.min(this.getLastNonWhiteOffset(),_80.length);
var _82=null;
for(var i=0;i<_81;i++){
if(_80[i].selected){
if(_82===null){
_82=[];
}
_82.push(_7f[i]);
}
}
return (_82!==null)?_82.join(""):null;
};
Line.prototype.getWidth=function(){
return this._chars.length;
};
Line.prototype.insertCharacter=function(ch,_83,_84){
if(isCharacter(ch)&&isNumber(_83)){
var _85=this._chars.length;
_84=(isNumber(_84))?_84:1;
if(_84>0&&0<=_83&&_83<_85){
ch=ch.charAt(0);
if(_83+_84>_85){
_84=_85-_83;
}
this._chars.splice(_85-_84,_84);
this._attributes.splice(_85-_84,_84);
var _86=new Array(_84);
var _87=new Array(_84);
for(var i=0;i<_84;i++){
this._chars.splice(_83+i,0,ch);
this._attributes.splice(_83+i,0,new Attribute());
}
this.clearCache();
}
}
};
Line.prototype.putCharacter=function(ch,_88,_89){
if(isCharacter(ch)&&isDefined(_88)&&_88.constructor==Attribute&&isNumber(_89)){
if(0<=_89&&_89<this._chars.length){
this._chars[_89]=ch.charAt(0);
this._attributes[_89]=_88;
this.clearCache();
}
}
};
Line.prototype.resize=function(_8a){
if(isNumber(_8a)){
var _8b=this._chars.length;
if(Line.MIN_WIDTH<=_8a&&_8a<=Line.MAX_WIDTH&&_8b!=_8a){
this._chars.length=_8a;
if(_8a>_8b){
for(var i=_8b;i<_8a;i++){
this._chars[i]=" ";
this._attributes[i]=new Attribute();
}
}
this.clearCache();
}
}
};
Line.prototype.select=function(_8c,_8d){
var _8e=(_8d)?this._chars.length:this.getLastNonWhiteOffset();
var _8f=new Range(0,_8e);
var _8c=_8c.clamp(_8f);
var _90=this._attributes;
var _8e=_8c.endingOffset;
var _91=false;
for(var i=_8c.startingOffset;i<_8e;i++){
var _92=_90[i];
if(_92.selected===false){
_92=_92.copy();
_92.selected=true;
_90[i]=_92;
_91=true;
}
}
if(_91){
this.clearCache();
}
return _91;
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
var _93=this;
this._queue=[];
this._applicationKeys=false;
this._playbackState=KeyHandler.STOPPED;
this.clearEvents();
this._playbackID=null;
document.onkeypress=function(e){
return _93.processKeyPress(e);
};
document.onkeydown=function(e){
return _93.processKeyDown(e);
};
};
KeyHandler.prototype.addEvent=function(_94,e){
if(e){
var _95={};
switch(_94){
case KeyHandler.KEY_DOWN:
_95.keyCode=e.keyCode;
_95.ctrlKey=e.ctrlKey;
_95.altKey=e.altKey;
_95.shiftKey=e.shiftKey;
break;
case KeyHandler.KEY_PRESS:
_95.keyCode=e.keyCode;
_95.which=e.which;
_95.ctrlKey=e.ctrlKey;
_95.altKey=e.altKey;
_95.metaKey=e.metaKey;
break;
default:
return;
}
this._events.keys.push({time:new Date().getTime(),type:_94,event:_95});
}
};
KeyHandler.prototype.addKeys=function(_96){
this._queue.push(_96);
if(isDefined(this.callback)){
this.callback(true);
}
};
KeyHandler.prototype.clearEvents=function(){
this._events={user_agent:{browser:BrowserDetect.browser,version:BrowserDetect.version,os:BrowserDetect.OS},keys:[]};
};
KeyHandler.prototype.dequeueAll=function(){
var _97=this._queue.join("");
this._queue.length=0;
return _97;
};
KeyHandler.prototype.getApplicationKeys=function(){
return this._applicationKeys;
};
KeyHandler.prototype.hasContent=function(){
return this._queue.length>0;
};
KeyHandler.prototype.play=function(_98){
if(this._playbackState!=KeyHandler.PLAYING){
this._playbackState=KeyHandler.PLAYING;
_98=_98||this._events.keys;
var _99=this;
var i=0;
var _9a=function(){
var _9b=_98[i++];
switch(_9b.type){
case KeyHandler.KEY_DOWN:
_99.processKeyDown(_9b.event);
break;
case KeyHandler.KEY_PRESS:
_99.processKeyPress(_9b.event);
break;
default:
break;
}
if(_99._playbackState==KeyHandler.PLAYING&&i<_98.length){
var _9c=clamp(_98[i].time-_9b.time,0,1000);
this._playbackID=window.setTimeout(_9a,_9c);
}
};
_9a();
}
};
KeyHandler.prototype.processKeyDown=function(e){
if(!e){
e=window.event;
}
if(this._playbackState==KeyHandler.RECORDING){
this.addEvent(KeyHandler.KEY_DOWN,e);
}
var _9d=e.keyCode;
var _9e=null;
var _9f=this._applicationKeys;
if(BrowserDetect.browser=="Firefox"&&(e.keyCode==8||(37<=e.keyCode&&e.keyCode<=40))){
}else{
switch(_9d){
case 8:
_9e=KeyHandler.BACKSPACE;
break;
case 9:
_9e=KeyHandler.TAB;
break;
case 27:
_9e=KeyHandler.ESCAPE;
break;
case 33:
_9e=KeyHandler.PAGE_UP;
break;
case 34:
_9e=KeyHandler.PAGE_DOWN;
break;
case 35:
_9e=(_9f)?KeyHandler.APP_END:KeyHandler.END;
break;
case 36:
_9e=(_9f)?KeyHandler.APP_HOME:KeyHandler.HOME;
break;
case 37:
_9e=(_9f)?KeyHandler.APP_LEFT:KeyHandler.LEFT;
break;
case 38:
_9e=(_9f)?KeyHandler.APP_UP:KeyHandler.UP;
break;
case 39:
_9e=(_9f)?KeyHandler.APP_RIGHT:KeyHandler.RIGHT;
break;
case 40:
_9e=(_9f)?KeyHandler.APP_DOWN:KeyHandler.DOWN;
break;
case 45:
_9e=KeyHandler.INSERT;
break;
case 46:
_9e=KeyHandler.DELETE;
break;
case 112:
_9e=e.shiftKey?KeyHandler.F13:KeyHandler.F1;
break;
case 113:
_9e=e.shiftKey?KeyHandler.F14:KeyHandler.F2;
break;
case 114:
_9e=e.shiftKey?KeyHandler.F15:KeyHandler.F3;
break;
case 115:
_9e=e.shiftKey?KeyHandler.F16:KeyHandler.F4;
break;
case 116:
_9e=e.shiftKey?KeyHandler.F17:KeyHandler.F5;
break;
case 117:
_9e=e.shiftKey?KeyHandler.F18:KeyHandler.F6;
break;
case 118:
_9e=e.shiftKey?KeyHandler.F19:KeyHandler.F7;
break;
case 119:
_9e=e.shiftKey?KeyHandler.F20:KeyHandler.F8;
break;
case 120:
_9e=KeyHandler.F9;
break;
case 121:
_9e=KeyHandler.F10;
break;
case 122:
_9e=KeyHandler.F11;
break;
case 123:
_9e=KeyHandler.F12;
break;
default:
if(!e.ctrlKey||(e.ctrlKey&&e.altKey)||(e.keyCode==17)){
}else{
if(e.shiftKey){
switch(_9d){
case 50:
_9e=String.fromCharCode(0);
break;
case 54:
_9e=String.fromCharCode(30);
break;
case 94:
_9e=String.fromCharCode(30);
break;
case 109:
_9e=String.fromCharCode(31);
break;
default:
break;
}
}else{
switch(_9d){
case 32:
_9e=String.fromCharCode(0);
break;
case 190:
_9e=String.fromCharCode(30);
break;
case 219:
_9e=String.fromCharCode(27);
break;
case 220:
_9e=String.fromCharCode(28);
break;
case 221:
_9e=String.fromCharCode(29);
break;
default:
if(65<=_9d&&_9d<=90){
_9e=String.fromCharCode(_9d-64);
}
break;
}
}
}
break;
}
}
if(_9e!==null){
this.addKeys(_9e);
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
var _a0=this._applicationKeys;
var _a1=null;
switch(e.keyCode){
case 8:
_a1=KeyHandler.BACKSPACE;
break;
case 37:
_a1=(_a0)?KeyHandler.APP_LEFT:KeyHandler.LEFT;
break;
case 38:
_a1=(_a0)?KeyHandler.APP_UP:KeyHandler.UP;
break;
case 39:
_a1=(_a0)?KeyHandler.APP_RIGHT:KeyHandler.RIGHT;
break;
case 40:
_a1=(_a0)?KeyHandler.APP_DOWN:KeyHandler.DOWN;
break;
}
if(_a1!==null){
this.addKeys(_a1);
}
}else{
if((e.ctrlKey&&!e.altKey)||(e.which==0)||(e.keyCode==8)||(e.keyCode==16)){
}else{
var _a2;
if(e.keyCode){
_a2=e.keyCode;
}
if(e.which){
_a2=e.which;
}
if(e.altKey&&!e.ctrlKey){
this.addKeys(KeyHandler.ESCAPE);
}
this.addKeys(String.fromCharCode(_a2));
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
KeyHandler.prototype.setApplicationKeys=function(_a3){
if(isBoolean(_a3)){
this._applicationKeys=_a3;
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
function XTermHandler(_a4){
this._term=_a4;
this._insertMode=false;
this._missingCommands={};
};
XTermHandler.prototype.BEL=function(_a5,_a6){
};
XTermHandler.prototype.BS=function(_a7,_a8){
var col=this._term.getColumn()-1;
col=Math.max(0,col);
this._term.setColumn(col);
};
XTermHandler.prototype.CHA=function(_a9,_aa){
var _ab=0;
if(_aa.length>0){
_ab=_aa-1;
}
this._term.setColumn(_ab);
};
XTermHandler.prototype.CR=function(_ac,_ad){
this._term.setColumn(0);
};
XTermHandler.prototype.CUB=function(_ae,_af){
var _b0=1;
if(_af.length>0){
_b0=_af-0;
if(_b0==0){
_b0=1;
}
}
var col=this._term.getColumn()-_b0;
col=Math.max(0,col);
this._term.setColumn(col);
};
XTermHandler.prototype.CUD=function(_b1,_b2){
var _b3=1;
if(_b2.length>0){
_b3=_b2-0;
if(_b3==0){
_b3=1;
}
}
var _b4=this._term.getRow();
var _b5=this._term.getScrollRegion().bottom;
var _b6;
if(_b4<=_b5){
_b6=Math.min(_b4+_b3,_b5);
}else{
_b6=Math.min(_b4+_b3,this._term.getHeight()-1);
}
this._term.setRow(_b6);
};
XTermHandler.prototype.CUF=function(_b7,_b8){
var _b9=1;
if(_b8.length>0){
_b9=_b8-0;
if(_b9==0){
_b9=1;
}
}
var col=this._term.getColumn()+_b9;
col=Math.min(col,this._term.getWidth()-1);
this._term.setColumn(col);
};
XTermHandler.prototype.CUP=function(_ba,_bb){
var row=0;
var col=0;
var _bc=this._term.getHeight();
if(_bb.length>0){
var _bd=_bb.split(/;/);
var row=_bd[0]-1;
var col=_bd[1]-1;
}
if(row>=_bc){
var _be=_bc-row;
row=_bc-1;
this._term.scrollUp(_be);
}
this._term.setPosition(row,col);
};
XTermHandler.prototype.CUU=function(_bf,_c0){
var _c1=1;
if(_c0.length>0){
_c1=_c0-0;
if(_c1==0){
_c1=1;
}
}
var _c2=this._term.getRow();
var _c3=this._term.getScrollRegion().top;
var _c4;
if(_c3<=_c2){
_c4=Math.max(_c3,_c2-_c1);
}else{
_c4=Math.max(0,_c2-_c1);
}
this._term.setRow(_c4);
};
XTermHandler.prototype.DCH=function(_c5,_c6){
var _c7=_c6-0;
this._term.deleteCharacter(_c7);
};
XTermHandler.prototype.DECALN=function(_c8,_c9){
this._term.clear("E");
};
XTermHandler.prototype.DECRC=function(_ca,_cb){
this._term.popPosition();
};
XTermHandler.prototype.DECPAM=function(_cc,_cd){
this._term.setApplicationKeys(true);
};
XTermHandler.prototype.DECPNM=function(_ce,_cf){
this._term.setApplicationKeys(false);
};
XTermHandler.prototype.DECRST=function(_d0,_d1){
var _d2=_d1-0;
switch(_d2){
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
this.genericHandler(_d0,_d1);
break;
}
};
XTermHandler.prototype.DECSC=function(_d3,_d4){
this._term.pushPosition();
};
XTermHandler.prototype.DECSET=function(_d5,_d6){
var _d7=_d6-0;
switch(_d7){
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
this.genericHandler(_d5,_d6);
break;
}
};
XTermHandler.prototype.DECSTBM=function(_d8,_d9){
var _da=_d9.split(/;/);
var top=_da[0]-1;
var _db=_da[1]-1;
this._term.setScrollRegion(top,0,_db,this._term.getWidth()-1);
};
XTermHandler.prototype.DL=function(_dc,_dd){
var _de=1;
if(_dd.length>0){
_de=_dd-0;
if(_de==0){
_de=1;
}
}
this._term.deleteLine(_de);
};
XTermHandler.prototype.ED=function(_df,_e0){
var _e1=_e0-0;
switch(_e1){
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
this.genericHandler(_df+":"+_e0,"");
break;
}
};
XTermHandler.prototype.EL=function(_e2,_e3){
var _e4=_e3-0;
switch(_e4){
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
this.genericHandler(_e2+":"+_e3,"");
break;
}
};
XTermHandler.prototype.genericHandler=function(_e5,_e6){
if(this._missingCommands.hasOwnProperty(_e5)===false){
this._missingCommands[_e5]=0;
}
this._missingCommands[_e5]++;
};
XTermHandler.prototype.getMissingCommands=function(){
return this._missingCommands;
};
XTermHandler.prototype.HVP=XTermHandler.prototype.CUP;
XTermHandler.prototype.ICH=function(_e7,_e8){
var _e9=_e8-0;
this._term.insertCharacter(" ",_e9);
};
XTermHandler.prototype.IL=function(_ea,_eb){
var _ec=1;
if(_eb.length>0){
_ec=_eb-0;
if(_ec==0){
_ec=1;
}
}
this._term.insertLine(_ec);
};
XTermHandler.prototype.IND=function(_ed,_ee){
var _ef=this._term.getRow();
var _f0=this._term.getScrollRegion().bottom;
var _f1=_ef+1;
if(_ef<=_f0){
this._term.setRow(_f1);
}else{
this._term.scrollUp(1);
this._term.setRow(_f0);
}
};
XTermHandler.prototype.LF=function(_f2,_f3){
var _f4=this._term;
var row=_f4.getRow()+1;
var _f5=_f4.getScrollRegion().bottom;
if(row>_f5){
_f4.scrollUp();
row=_f5;
}
_f4.setPosition(row,0);
};
XTermHandler.prototype.NEL=XTermHandler.prototype.LF;
XTermHandler.prototype.processCharacter=function(_f6,_f7){
if(this._insertMode){
this._term.insertCharacter(" ",1);
}
this._term.displayCharacters(_f7);
};
XTermHandler.prototype.RI=function(_f8,_f9){
var _fa=this._term.getRow();
var _fb=this._term.getScrollRegion().top;
var _fc=_fa-1;
if(_fb<=_fc){
this._term.setRow(_fc);
}else{
this._term.scrollDown(1);
this._term.setRow(_fb);
}
};
XTermHandler.prototype.RM=function(_fd,_fe){
var _ff=_fe-0;
switch(_ff){
case 4:
this._insertMode=false;
break;
case 2:
case 12:
case 20:
default:
this.genericHandler(_fd,_fe);
break;
}
};
XTermHandler.prototype.SD=function(_100,_101){
var _102=1;
if(_101.length>0){
_102=_101-0;
}
var _103=this._term.getRow();
var _104=this._term.getScrollRegion().top;
var _105=_103-_102;
if(_104<=_105){
this._term.setRow(_105);
}else{
this._term.scrollDown(_102);
this._term.setRow(_104);
}
};
XTermHandler.prototype.SET_TEXT_PARAMS=function(_106,_107){
var _108=_107.split(/;/);
var code=_108[0]-0;
var text=_108[1];
if(code==0){
this._term.setTitle(text);
}else{
this.genericHandler(_106+":"+_107,"");
}
};
XTermHandler.prototype.SET_TEXT_PARAMS2=XTermHandler.prototype.SET_TEXT_PARAMS;
XTermHandler.prototype.SGR=function(_109,_10a){
var attr=this._term.getCurrentAttribute();
var _10b=_10a.split(/;/);
for(var i=0;i<_10b.length;i++){
var _10c=_10b[i]-0;
if(_10c<50){
var tens=Math.floor(_10c/10);
var ones=_10c%10;
switch(tens){
case 0:
switch(ones){
case 0:
attr.reset();
break;
case 1:
attr.bold=true;
break;
case 3:
attr.italic=true;
break;
case 4:
attr.underline=true;
break;
case 7:
attr.inverse=true;
break;
case 9:
attr.strikethrough=true;
break;
default:
this.genericHandler(_109+":"+_10a,"");
break;
}
break;
case 2:
switch(ones){
case 2:
attr.bold=false;
break;
case 3:
attr.italic=false;
break;
case 4:
attr.underline=false;
break;
case 7:
attr.inverse=false;
break;
case 9:
attr.strikethough=false;
break;
default:
this.genericHandler(_109+":"+_10a,"");
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
attr.foreground=ones;
break;
case 9:
attr.resetForeground();
break;
default:
this.genericHandler(_109+":"+_10a,"");
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
attr.background=ones;
break;
case 9:
attr.resetBackground();
break;
default:
this.genericHandler(_109+":"+_10a,"");
break;
}
break;
default:
this.genericHandler(_109+":"+_10a,"");
break;
}
}else{
this.genericHandler(_109+":"+_10a,"");
}
}
this._term.setCurrentAttribute(attr);
};
XTermHandler.prototype.SM=function(_10d,_10e){
var mode=_10e-0;
switch(mode){
case 4:
this._insertMode=true;
break;
case 2:
case 12:
case 20:
default:
this.genericHandler(_10d,_10e);
break;
}
};
XTermHandler.prototype.SU=function(_10f,_110){
var _111=1;
if(_110.length>0){
_111=_110-0;
}
var _112=this._term.getRow();
var _113=this._term.getScrollRegion().bottom;
var _114=_112+_111;
if(_112<=_113){
this._term.setRow(_114);
}else{
this._term.scrollUp(_111);
this._term.setRow(_113);
}
};
XTermHandler.prototype.TAB=function(_115,_116){
var _117=this._term.getColumn();
var _118=8-(_117%8);
this._term.displayCharacters(new Array(_118+1).join(" "));
};
XTermHandler.prototype.VPA=function(_119,_11a){
var row=0;
if(_11a.length>0){
row=_11a-1;
}
this._term.setRow(row);
};
XTermHandler.prototype.VT=XTermHandler.prototype.LF;
function TermParser(_11b,_11c){
if(_11b===null||_11b===undefined){
throw new Error("Parsing tables must be defined when creating a new TermParser");
}
this._processTables(_11b);
this._actions=_11b.actions;
this._nodes=_11b.nodes;
this.setHandler(_11c);
this.singleStep=false;
this.offset=-1;
};
TermParser.prototype.getHandler=function(){
return this._handler;
};
TermParser.prototype.parse=function(_11d){
var _11e=0;
var _11f=isString(_11d)?_11d.length:0;
while(_11e<_11f){
var _120=0;
var _121=this._nodes[_120][1];
var _122=(_121==-1)?-2:_11e;
for(var i=_11e;i<_11f;i++){
var _123=this._nodes[_120];
if(_123){
var _124=_11d.charCodeAt(i);
var _125=_123[0][_124];
if(_125!=-1){
_120=_125;
var _126=this._nodes[_120][1];
if(_126!=-1){
_122=i;
_121=_126;
}
}else{
break;
}
}
}
if(_121==-1){
if(this._handler!=null){
if(this._handler.processCharacter){
this._handler.processCharacter("processCharacter",_11d.charAt(_11e));
}
}
_11e++;
}else{
var _127=_122+1;
if(this._handler!=null){
var info=this._actions[_121];
var _128=info[0];
var _129="";
if(info.length>=3&&info[1]!=-1&&info[2]!=-1){
_129=_11d.substring(_11e+info[1],_127-info[2]);
}
this._handler[_128](_128,_129);
}
_11e=_127;
if(this.singleStep){
this.offset=_11e;
break;
}
}
}
};
TermParser.prototype._processTables=function(_12a){
if(_12a.hasOwnProperty("processed")==false||_12a.processed==false){
switch(_12a.format){
case "expanded":
break;
case "rle":
var mos=new Array(256);
for(var i=0;i<mos.length;i++){
mos[i]=-1;
}
var _12b=_12a.nodes;
for(var i=0;i<_12b.length;i++){
var _12c=_12b[i][0];
var _12d=[];
for(var j=0;j<_12c.length;j++){
var _12e=_12c[j];
if(_12e<0){
_12d=_12d.concat(mos.slice(0,-_12e));
}else{
var _12f=_12e>>8;
var _130=(_12e&255)+1;
for(var k=0;k<_130;k++){
_12d.push(_12f);
}
}
}
_12b[i][0]=_12d;
}
break;
default:
break;
}
_12a.processed=true;
}
};
TermParser.prototype.setHandler=function(_131){
var _132=null;
if(_131){
var _133=null;
var _134=function(_135,_136){
};
for(var i=0;i<this._actions.length;i++){
var _137=this._actions[i];
var _138=_137[0];
if(!_131[_138]){
if(_132==null){
_132=protectedClone(_131);
if(!_131.genericHandler){
_133=_134;
}else{
_133=_131.genericHandler;
}
}
_132[_138]=_133;
}
}
}
if(_132==null){
this._handler=_131;
}else{
this._handler=_132;
}
};
TermComm.POLLING_INTERVAL_MIN=125;
TermComm.POLLING_INTERVAL_MAX=2000;
TermComm.POLLING_GROWTH_RATE=2;
TermComm.DEFAULT_REQUEST_URL="/stream";
TermComm.DEFAULT_GET_UNIQUE_ID_URL="/id";
function TermComm(_139,_13a){
var self=this;
this.terminal=_139;
this.keyHandler=_139.getKeyHandler();
this.keyHandler.callback=function(){
self.sendKeys();
};
this.minInterval=125;
this.maxInterval=2000;
this.growthRate=2;
this.timeoutInterval=5000;
this.requestURL=TermComm.DEFAULT_REQUEST_URL;
this.getUniqueIdURL=TermComm.DEFAULT_GET_UNIQUE_ID_URL;
if(isDefined(_13a)){
if(_13a.hasOwnProperty("minInterval")&&isNumber(_13a.minInterval)){
this.minInterval=_13a.minInterval;
}
if(_13a.hasOwnProperty("maxInterval")&&isNumber(_13a.maxInterval)){
this.maxInterval=_13a.maxInterval;
}
if(_13a.hasOwnProperty("growthRate")&&isNumber(_13a.growthRate)){
this.growthRate=_13a.growthRate;
}
if(_13a.hasOwnProperty("timeoutInterval")&&isNumber(_13a.timeoutInterval)){
this.timeoutInterval=_13a.timeoutInterval;
}
if(_13a.hasOwnProperty("requestURL")&&isString(_13a.requestURL)&&_13a.requestURL.length>0){
this.requestURL=_13a.requestURL;
}
if(_13a.hasOwnProperty("getUniqueIdURL")&&isString(_13a.getUniqueIdURL)&&_13a.getUniqueIdURL.length>0){
this.getUniqueIdURL=_13a.getUniqueIdURL;
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
var _13b={id:this.terminal.getId(),cb:new Date().getTime()+":"+this.cacheBusterID++};
req.open("GET",createURL(this.requestURL,_13b),true);
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
var _13c={id:id};
req.open("POST",createURL(this.requestURL,_13c),true);
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
TermComm.prototype.update=function(_13d){
if(this.running&&this.terminal.getId()!==null){
if(isBoolean(_13d)){
if(this.requestID!==null){
window.clearTimeout(this.requestID);
this.requestID=null;
}
if(_13d){
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
function Term(id,_13e,_13f,_140){
if(isString(id)===false||id.length===0){
id="terminal";
}
this._id=(_140&&_140.hasOwnProperty("id"))?_140.id:null;
this._remainingText="";
this._rootNode=document.getElementById(id);
if(this._rootNode){
this._rootNode.className="webterm";
this._termNode=document.createElement("pre");
this._rootNode.appendChild(this._termNode);
this._width=(isNumber(_13e))?clamp(_13e,Line.MIN_WIDTH,Line.MAX_WIDTH):Line.DEFAULT_WIDTH;
this._height=(isNumber(_13f))?clamp(_13f,Term.MIN_HEIGHT,Term.MAX_HEIGHT):Term.DEFAULT_HEIGHT;
this._title="Aptana WebTerm";
this._row=0;
this._column=0;
this._scrollRegion={top:0,left:0,bottom:this._height-1,right:this._width-1};
this._cursorVisible=true;
this._buffers=[];
this._positions=[];
this._currentAttribute=new Attribute();
this._sendResizeSequence=(_140&&_140.hasOwnProperty("sendResizeSequence"))?_140.sendResizeSequence:true;
this._showTitle=(_140&&_140.hasOwnProperty("showTitle"))?_140.showTitle:true;
this._onTitleChange=(_140&&_140.hasOwnProperty("onTitleChange"))?_140.onTitleChange:null;
this._useNativeCopy=(_140&&_140.hasOwnProperty("useNativeCopy")?_140.useNativeCopy:true);
if(this._useNativeCopy===false){
this._hasSelection=false;
this._lastStartingOffset=null;
this._lastEndingOffset=null;
var self=this;
dragger(this._rootNode,function(sx,sy,ex,ey){
self.updateSelection(sx,sy,ex,ey);
});
this._fontInfo=new FontInfo("fontInfo");
}else{
this._fontInfo=new FontInfo("");
}
var _141=(_140&&_140.hasOwnProperty("handler"))?_140.handler:new XTermHandler(this);
var _142=(_140&&_140.hasOwnProperty("tables"))?_140.tables:XTermTables;
var _143=(_140&&_140.hasOwnProperty("parser"))?_140.parser:new TermParser(_142,_141);
var _144=(_140&&_140.hasOwnProperty("keyHandler"))?_140.keyHandler:new KeyHandler();
this._parser=_143;
this._keyHandler=_144;
var _145=(_140&&_140.hasOwnProperty("commHandler"))?_140.commHandler:new TermComm(this,_140);
var _146=(_140&&_140.hasOwnProperty("autoStart"))?_140.autoStart:true;
this._commHandler=_145;
this.createBuffer();
this.refresh();
if(_146){
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
var _147=new Array(this._height);
for(var i=0;i<_147.length;i++){
_147[i]=this.createLine();
}
this._lines=_147;
};
Term.prototype.createLine=function(){
return new Line(this._width,this._fontInfo);
};
Term.prototype.clearSelection=function(_148){
var _149=this._lines;
var _14a=_149.length;
for(var i=0;i<_14a;i++){
_149[i].clearSelection();
}
this._hasSelection=false;
this._lastStartingOffset=null;
this._lastEndingOffset=null;
if((isBoolean(_148))?_148:true){
this.refresh();
}
};
Term.prototype.deleteCharacter=function(_14b){
this._lines[this._row].deleteCharacter(this._column,_14b);
};
Term.prototype.deleteLine=function(_14c){
_14c=(_14c===undefined)?1:_14c;
if(_14c>0){
var _14d=this._scrollRegion;
if(_14d.left==0&&_14d.right==this._width-1){
if(this._row+_14c>_14d.bottom){
_14c=_14d.bottom-this._row+1;
}
if(_14c==this._height){
this.clear();
}else{
var _14e=this._lines.splice(this._row,_14c);
for(var i=0;i<_14c;i++){
_14e[i].clear();
}
if(_14d.bottom+1==this.height){
this._lines=this._lines.concat(_14e);
}else{
for(var i=0;i<_14c;i++){
this._lines.splice(_14d.bottom-_14c+i+1,0,_14e[i]);
}
}
}
}else{
}
}
};
Term.prototype.deselect=function(s,e){
var _14f=this.getWidth();
var _150=this.getHeight();
var _151=new Range(s,e).clamp(new Range(0,_14f*_150));
if(_151.isEmpty()===false){
var _152=Math.floor(_151.startingOffset/_14f);
var _153=Math.ceil(_151.endingOffset/_14f);
var _154=_152*_14f;
for(var i=_152;i<=_153;i++){
var _155=_154+_14f;
var _156=new Range(_154,_155).clamp(_151);
if(_156.isEmpty()===false){
var _157=_156.move(-_154);
var line=this._lines[i];
line.deselect(_157);
}
_154=_155;
}
}
};
Term.prototype.displayCharacters=function(_158){
if(isString(_158)){
for(var i=0;i<_158.length;i++){
var ch=_158.charAt(i);
var line=this._lines[this._row];
if(/[\x20-\x7F]+/.test(ch)==false){
ch=" ";
}
line.putCharacter(ch,this._currentAttribute,this._column);
this._column++;
if(this._column>=this._width){
if(this._keyHandler.getApplicationKeys()){
this._column=this._width-1;
}else{
var _159=this._row;
var _15a=this._scrollRegion.bottom;
var _15b=_159+1;
if(_159<=_15a){
this._row=_15b;
}else{
this.scrollUp(1);
this._row=_15a;
}
this._column=0;
}
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
var _15c=(BrowserDetect.OS=="Windows")?"\r\n":"\n";
var _15d=null;
if(this._useNativeCopy===false){
if(this.hasSelection()){
var _15e=this._lines;
var _15f=_15e.length;
for(var i=0;i<_15f;i++){
var _160=_15e[i].getSelectedText();
if(_160!==null){
if(_15d===null){
_15d=[];
}
_15d.push(_160);
}
}
}
}else{
if(window.getSelection){
_15d=window.getSelection().toString();
_15d=_15d.split(/\r\n|\r|\n/);
}else{
if(document.selection){
_15d=document.selection.createRange().text;
var _161=0;
var _15e=[];
for(var i=0;i<this._height&&_161<_15d.length;i++,_161+=this._width){
_15e.push(_15d.substring(_161,_161+this._width));
}
_15d=_15e;
}
}
if(_15d!==null){
for(var i=0;i<_15d.length;i++){
_15d[i]=_15d[i].replace(/\s+$/,"");
}
}
}
return (_15d!==null)?_15d.join(_15c):null;
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
Term.prototype.insertCharacter=function(ch,_162){
this._lines[this._row].insertCharacter(ch,this._column,_162);
};
Term.prototype.insertLine=function(_163){
_163=(_163===undefined)?1:_163;
if(_163>0){
var _164=this._scrollRegion;
if(_164.left==0&&_164.right==this._width-1){
if(this._row+_163>_164.bottom){
_163=_164.bottom-this._row+1;
}
if(_163==this._height){
this.clear();
}else{
var _165=this._lines.splice(_164.bottom-_163+1,_163);
for(var i=0;i<_163;i++){
_165[i].clear();
}
if(this._row==0){
this._lines=_165.concat(this._lines);
}else{
for(var i=0;i<_163;i++){
this._lines.splice(this._row+i,0,_165[i]);
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
var _166=this._positions.pop();
this._row=_166[0];
this._column=_166[1];
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
var _167=[];
var attr=null;
var _168=this._title+" — "+this._width+"x"+this._height;
var _169="<div class='title'>"+_168+"</div>";
for(var row=0;row<this._height;row++){
var line=this._lines[row];
var _16a=(this._cursorVisible)?(row==this._row)?this._column:-1:-1;
var _16b=line.getHTMLInfo(attr,_16a);
attr=_16b.attribute;
_167.push(_16b.html);
}
if(attr!=null){
_167[_167.length-1]+=attr.getEndingHTML();
}
if(this._showTitle){
this._termNode.innerHTML=_169+_167.join("<br />");
}else{
this._termNode.innerHTML=_167.join("<br />");
}
};
Term.prototype.scrollDown=function(_16c){
_16c=(_16c===undefined)?1:_16c;
if(_16c>0){
var _16d=this._scrollRegion;
if(_16d.left==0&&_16d.right==this._width-1){
var _16e=_16d.bottom-_16d.top+1;
if(_16c>=_16e){
this.clear();
}else{
var _16f=this._lines.splice(_16d.bottom-_16c+1,_16c);
for(var i=0;i<_16c;i++){
_16f[i].clear();
}
if(_16d.top==0){
this._lines=_16f.concat(this._lines);
}else{
for(var i=0;i<_16c;i++){
this._lines.splice(_16d.top+i,0,_16f[i]);
}
}
}
}else{
}
}
};
Term.prototype.scrollUp=function(_170){
_170=(_170===undefined)?1:_170;
if(_170>0){
var _171=this._scrollRegion;
if(_171.left==0&&_171.right==this._width-1){
var _172=_171.bottom-_171.top+1;
if(_170>=_172){
this.clear();
}else{
var _173=this._lines.splice(_171.top,_170);
for(var i=0;i<_170;i++){
_173[i].clear();
}
if(_171.bottom+1==this.height){
this._lines=this._lines.concat(_173);
}else{
for(var i=0;i<_170;i++){
this._lines.splice(_171.bottom-_170+i+1,0,_173[i]);
}
}
}
}else{
}
}
};
Term.prototype.select=function(s,e,_174){
var _175=this.getWidth();
var _176=this.getHeight();
var _177=new Range(s,e).clamp(new Range(0,_175*_176));
var _178=this.hasSelection()&&!_174;
var _179=false;
if(_178){
this.clearSelection(false);
}
if(_177.isEmpty()===false){
var _17a=Math.floor(_177.startingOffset/_175);
var _17b=Math.ceil(_177.endingOffset/_175);
var _17c=_17a*_175;
for(var i=_17a;i<=_17b;i++){
var _17d=_17c+_175;
var _17e=new Range(_17c,_17d).clamp(_177);
if(_17e.isEmpty()===false){
var _17f=_177.endingOffset>_17e.endingOffset;
var _180=_17e.move(-_17c);
var line=this._lines[i];
if(line.select(_180,_17f)){
_179=true;
}
}
_17c=_17d;
}
}
this._hasSelection=_179;
if(_178||_179){
this.refresh();
}
};
Term.prototype.selectAll=function(){
if(this._useNativeCopy===false){
this.select(0,this._width*this._height);
}else{
var node=this._termNode;
var _181=(node.innerText)?node.innerText:node.textContent;
var _182=0;
var _183=_181.length;
if(window.getSelection&&document.createRange){
var _184=window.getSelection();
var _185=document.createRange();
_185.selectNodeContents(node);
_184.removeAllRanges();
_184.addRange(_185);
}else{
if(document.selection&&document.selection.createRange){
alert("document.selection.createRange");
var _185=document.selection.createRange();
_185.moveToElementText(node);
_185.select();
}else{
alert("Didn't see a way to set the selection natively");
}
}
}
};
Term.prototype.setApplicationKeys=function(_186){
if(isBoolean(_186)){
this._keyHandler.setApplicationKeys(_186);
}
};
Term.prototype.setColumn=function(_187){
if(isNumber(_187)&&0<=_187&&_187<this._width){
this._column=_187;
}
};
Term.prototype.setCurrentAttribute=function(attr){
if(isDefined(attr)&&attr.constructor===Attribute){
this._currentAttribute=attr;
}
};
Term.prototype.setCursorVisible=function(_188){
if(isBoolean(_188)){
this._cursorVisible=_188;
}
};
Term.prototype.setHeight=function(_189){
this.setSize(this._width,_189);
};
Term.prototype.setPosition=function(row,_18a){
if(isNumber(row)&&0<=row&&row<this._height){
this._row=row;
}
if(isNumber(_18a)&&0<=_18a&&_18a<this._width){
this._column=_18a;
}
};
Term.prototype.setRow=function(row){
if(0<=row&&row<this._height){
this._row=row;
}
};
Term.prototype.setScrollRegion=function(top,left,_18b,_18c){
if(isNumber(top)&&isNumber(left)&&isNumber(_18b)&&isNumber(_18c)){
if(top<_18b&&left<_18c){
var _18d=(0<=top&&top<this._height);
var _18e=(0<=left&&left<this._width);
var _18f=(0<=_18b&&_18b<this._height);
var _190=(0<=_18c&&_18c<this._width);
if(_18d&&_18e&&_18f&&_190){
this._scrollRegion={top:top,left:left,bottom:_18b,right:_18c};
}
}
}
};
Term.prototype.setSize=function(_191,_192){
var _193=false;
if(isNumber(_191)&&Line.MIN_WIDTH<=_191&&_191<=Line.MAX_WIDTH&&this._width!=_191){
for(var i=0;i<this._height;i++){
this._lines[i].resize(_191);
}
this._width=_191;
this._column=Math.min(this._width-1,this._column);
_193=true;
}
if(isNumber(_192)&&Term.MIN_HEIGHT<=_192&&_192<=Term.MAX_HEIGHT&&this._height!=_192){
if(_192>this._height){
for(var i=this._height;i<_192;i++){
this._lines.push(this.createLine());
}
}else{
this._lines=this._lines.splice(this._height-_192,_192);
}
this._height=_192;
this._row=Math.min(this._height-1,this._row);
_193=true;
}
if(_193){
this.setScrollRegion(0,0,this._height-1,this._width-1);
if(this._sendResizeSequence){
var ESC=String.fromCharCode(27);
var CSI=ESC+"[";
this._keyHandler.addKeys(CSI+[8,this._height,this._width].join(";")+"t");
}
this.refresh();
}
};
Term.prototype.setTitle=function(_194){
this._title=_194;
if(isFunction(this._onTitleChange)){
this._onTitleChange(_194);
}
};
Term.prototype.showTitle=function(_195){
if(isBoolean(_195)){
this._showTitle=_195;
this.refresh();
}
};
Term.prototype.sizeToWindow=function(){
if(this._useNativeCopy&&this._fontInfo.isMonospaced()===false){
var div=this._rootNode;
var _196=div.style.fontWeight;
div.style.fontWeight="bold";
var _197=div.offsetWidth;
var _198=div.offsetHeight;
div.style.fontWeight=_196;
var _199=_197/this.getWidth();
var _19a=_198/this.getHeight();
this._fontInfo.forceSize(_199,_19a);
}
var m=this._fontInfo.getCharacterSize("M");
var _19b=m[0];
var _19c=m[1];
var _199=Math.floor(getWindowWidth()/_19b)-1;
var _19a=Math.floor(getWindowHeight()/_19c);
this.setSize(_199,_19a);
};
Term.prototype.toggleRunState=function(){
if(this._commHandler!==null){
if(this._id===null&&this._commHandler.isRunning()==false){
this._id=this._commHandler.getUniqueID();
}
this._commHandler.toggleRunState();
}
};
Term.prototype.setWidth=function(_19d){
this.setSize(_19d,this._height);
};
Term.prototype.toString=function(){
var _19e=[];
for(var i=0;i<this._lines.length;i++){
_19e.push(this._lines[i].toString());
}
return _19e.join("\n");
};
Term.prototype.updateSelection=function(_19f,_1a0,endX,endY){
if(isNumber(_19f)&&isNumber(_1a0)&&isNumber(endX)&&isNumber(endY)){
var _1a1=this._lines;
var _1a2=this._fontInfo.getCharacterHeight("M");
var _1a3=function(y){
var _1a4=_1a1.length;
var _1a5=0;
var _1a6=null;
for(var i=0;i<_1a4;i++){
var line=_1a1[i];
var _1a7=_1a5+_1a2;
if(_1a5<=y&&y<_1a7){
_1a6=i;
break;
}else{
_1a5=_1a7;
}
}
return _1a6;
};
var _1a8=_1a3(_1a0);
var _1a9=_1a3(endY);
if(_1a8!==null&&_1a9!==null){
var _1aa=_1a1[_1a8].getOffsetFromPosition(_19f);
var _1ab=_1a1[_1a9].getOffsetFromPosition(endX);
var _1ac=_1a8*this.getWidth()+_1aa;
var _1ad=_1a9*this.getWidth()+_1ab;
if(this._lastStartingOffset!==_1ac||this._lastEndingOffset!==_1ad){
this.select(_1ac,_1ad);
this._lastStartingOffset=_1ac;
this._lastEndingOffset=_1ad;
}
}
}
};

