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
this._rootNode.className="fontInfo";
this._termNode=document.createElement("pre");
this._rootNode.appendChild(this._termNode);
this.reset();
this._useTracking;
};
FontInfo.prototype.getCharacterHeight=function(c,_37){
var _38=this.getCharacterSize(c,_37);
return _38[1];
};
FontInfo.prototype.getCharacterSize=function(c,_39){
var _3a=this._characterSizes;
_39=(this._useTracking)?"normal":_39;
if(this.isMonospaced()){
c=FontInfo.MONOSPACE;
}else{
if(_3a.hasOwnProperty(c)===false){
var _3b=this._termNode;
var _3c=function(_3d){
_3b.innerHTML=(c==" ")?"&nbsp;":c;
_3b.style.fontWeight=_3d;
var _3e=[_3b.clientWidth||_3b.offsetWidth,_3b.clientHeight||_3b.offsetHeight];
_3b.innerHTML="";
return _3e;
};
_3a[c]={normal:_3c("normal"),bold:_3c("bolder")};
}
}
return _3a[c][_39||"normal"];
};
FontInfo.prototype.getCharacterWidth=function(c,_3f){
var _40=this.getCharacterSize(c,_3f);
return _40[0];
};
FontInfo.prototype.getTracking=function(){
var _41=0;
if(this.isMonospaced){
var M=this._characterSizes.M;
_41=M.normal[0]-M.bold[0];
}
return _41+"px";
};
FontInfo.prototype.isMonospaced=function(){
return this._characterSizes.hasOwnProperty(FontInfo.MONOSPACE);
};
FontInfo.prototype.reset=function(){
this._characterSizes={};
var _42=this.getCharacterSize(" ");
var i=this.getCharacterSize("i");
var M=this.getCharacterSize("M");
if(_42[0]==i[0]&&i[0]==M[0]){
this._characterSizes[FontInfo.MONOSPACE]={normal:M,bold:this.getCharacterSize("M","bold")};
}
};
FontInfo.prototype.useTracking=function(_43){
this._useTracking=_43;
};
Attribute.DEFAULT_BACKGROUND="b";
Attribute.DEFAULT_FOREGROUND="f";
function Attribute(){
this.reset();
};
Attribute.prototype.copy=function(){
var _44=new Attribute();
_44.foreground=this.foreground;
_44.background=this.background;
_44.bold=this.bold;
_44.italic=this.italic;
_44.underline=this.underline;
_44.inverse=this.inverse;
_44.strikethrough=this.strikethrough;
_44.blink=this.blink;
_44.selected=this.selected;
return _44;
};
Attribute.prototype.equals=function(_45){
var _46=false;
if(_45 instanceof Attribute){
_46=this===_45||(this.foreground==_45.foreground&&this.background==_45.background&&this.bold==_45.bold&&this.italic==_45.italic&&this.underline==_45.underline&&this.inverse==_45.inverse&&this.strikethrough==_45.strikethrough&&this.blink==_45.blink&&this.selected==_45.selected);
}
return _46;
};
Attribute.prototype.getStartingHTML=function(){
var _47=[];
var _48=(isNumber(this.background))?this.background:Attribute.DEFAULT_BACKGROUND;
var _49=(isNumber(this.foreground))?this.foreground:Attribute.DEFAULT_FOREGROUND;
if(this.inverse){
_47.push("f"+_48);
_47.push("b"+((this.selected)?"s":_49));
}else{
_47.push("f"+_49);
_47.push("b"+((this.selected)?"s":_48));
}
if(this.bold){
_47.push("b");
}
if(this.italic){
_47.push("i");
}
if(this.underline){
_47.push("u");
}else{
if(this.strikethrough){
_47.push("lt");
}else{
if(this.blink){
_47.push("bl");
}
}
}
return "<span class=\""+_47.join(" ")+"\">";
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
function Range(_4a,_4b){
if(isNumber(_4a)===false){
_4a=0;
}
if(isNumber(_4b)===false){
_4b=0;
}
this.startingOffset=Math.min(_4a,_4b);
this.endingOffset=Math.max(_4a,_4b);
};
Range.prototype.clamp=function(_4c){
var _4d;
if(this.isOverlapping(_4c)){
_4d=new Range(Math.max(this.startingOffset,_4c.startingOffset),Math.min(this.endingOffset,_4c.endingOffset));
}else{
_4d=new Range(0,0);
}
return _4d;
};
Range.prototype.contains=function(_4e){
return this.startingOffset<=_4e&&_4e<this.endingOffset;
};
Range.prototype.isEmpty=function(){
return this.startingOffset===this.endingOffset;
};
Range.prototype.isOverlapping=function(_4f){
var _50=this.startingOffset;
var _51=_4f.startingOffset;
var _52=this.endingOffset-1;
var _53=_4f.endingOffset-1;
return (_51<=_50&&_50<=_53||_51<=_52&&_52<=_53||_50<=_51&&_51<=_52||_50<=_53&&_53<=_52);
};
Range.prototype.merge=function(_54){
return new Range(Math.min(this.startingOffset,_54.startingOffset),Math.max(this.endingOffset,_54.endingOffset));
};
Range.prototype.move=function(_55){
return new Range(this.startingOffset+_55,this.endingOffset+_55);
};
Line.DEFAULT_WIDTH=80;
Line.MIN_WIDTH=20;
Line.MAX_WIDTH=512;
function Line(_56,_57){
if(isNumber(_56)){
_56=clamp(_56,Line.MIN_WIDTH,Line.MAX_WIDTH);
}else{
_56=Line.DEFAULT_WIDTH;
}
this._fontInfo=_57;
this._chars=new Array(_56);
this._attributes=new Array(_56);
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
Line.prototype.clearLeft=function(_58){
if(isNumber(_58)&&0<=_58&&_58<this._chars.length){
for(var i=0;i<=_58;i++){
this._chars[i]=" ";
this._attributes[i]=new Attribute();
}
this.clearCache();
}
};
Line.prototype.clearRight=function(_59){
if(isNumber(_59)&&0<=_59&&_59<this._chars.length){
for(var i=_59;i<this._chars.length;i++){
this._chars[i]=" ";
this._attributes[i]=new Attribute();
}
this.clearCache();
}
};
Line.prototype.clearSelection=function(){
var _5a=this._attributes;
var _5b=_5a.length;
var _5c=false;
for(var i=0;i<_5b;i++){
var _5d=_5a[i];
if(_5d.selected){
_5c=true;
}
_5d.selected=false;
}
if(_5c){
this.clearCache();
}
};
Line.prototype.deleteCharacter=function(_5e,_5f){
if(isNumber(_5e)){
var _60=this._chars.length;
_5f=(isNumber(_5f))?_5f:1;
if(_5f>0&&0<=_5e&&_5e<_60){
if(_5e+_5f>_60){
_5f=_60-_5e;
}
this._chars.splice(_5e,_5f);
this._attributes.splice(_5e,_5f);
for(var i=0;i<_5f;i++){
this._chars.push(" ");
this._attributes.push(new Attribute());
}
this.clearCache();
}
}
};
Line.prototype.deselect=function(_61){
var _62=new Range(0,this._chars.length);
var _61=_61.clamp(_62);
var _63=this._attributes;
var _64=_61.endingOffset;
var _65=false;
for(var i=_61.startingOffset;i<_64;i++){
var _66=_63[i];
if(_66.selected){
_66.copy();
_66.selected=false;
_63[i]=_66;
_65=true;
}
}
if(_65){
this.clearCache();
}
};
Line.prototype.getHTMLInfo=function(_67,_68){
if(this._lastInfo===null||this._lastCursorOffset!==_68){
var _69=[];
for(var i=0;i<this._chars.length;i++){
var ch=this._chars[i];
var _6a=this._attributes[i];
if(_6a&&_6a.equals(_67)==false){
if(_67!==null){
_69.push(_67.getEndingHTML());
}
_69.push(_6a.getStartingHTML());
_67=_6a;
}
if(i===_68){
_69.push("<span class=\"cursor\">");
}
switch(ch){
case "&":
_69.push("&amp;");
break;
case "<":
_69.push("&lt;");
break;
case ">":
_69.push("&gt;");
break;
case " ":
_69.push("&nbsp;");
break;
default:
_69.push(ch);
break;
}
if(i===_68){
_69.push("</span>");
}
}
this._lastInfo={html:_69.join(""),attribute:_67};
this._lastCursorOffset=_68;
}
return this._lastInfo;
};
Line.prototype.getLastNonWhiteOffset=function(){
var _6b=0;
var _6c=this._chars.length;
for(var i=_6c-1;i>=0;i--){
if(this._chars[i].match(/\S/)){
_6b=i+1;
break;
}
}
return _6b;
};
Line.prototype.getLineHeight=function(){
var _6d=this._chars;
var _6e=this._attributes;
var _6f=_6d.length;
var _70=0;
for(var i=0;i<_6f;i++){
var ch=_6d[i];
var _71=_6e[i];
var _72=(_71.bold)?"bold":"normal";
var _73=this._fontInfo.getCharacterHeight(ch,_72);
_70=Math.max(_70,_73);
}
return _70;
};
Line.prototype.getOffsetFromPosition=function(x){
var _74=0;
var _75;
if(this._fontInfo.isMonospaced()){
var _76=this._fontInfo.getCharacterWidth("M");
_75=Math.floor(x/_76);
}else{
var _77=this._chars;
var _78=this._attributes;
var _79=_77.length;
for(var i=0;i<_79;i++){
var ch=_77[i];
var _7a=_78[i];
var _7b=(_7a.bold)?"bold":"normal";
var _7c=_74+this._fontInfo.getCharacterWidth(ch,_7b);
if(_74<=x&&x<_7c){
_75=i;
break;
}else{
_74=_7c;
}
}
}
return _75;
};
Line.prototype.getSelectedText=function(){
var _7d=this._chars;
var _7e=this._attributes;
var _7f=Math.min(this.getLastNonWhiteOffset(),_7e.length);
var _80=null;
for(var i=0;i<_7f;i++){
if(_7e[i].selected){
if(_80===null){
_80=[];
}
_80.push(_7d[i]);
}
}
return (_80!==null)?_80.join(""):null;
};
Line.prototype.getWidth=function(){
return this._chars.length;
};
Line.prototype.insertCharacter=function(ch,_81,_82){
if(isCharacter(ch)&&isNumber(_81)){
var _83=this._chars.length;
_82=(isNumber(_82))?_82:1;
if(_82>0&&0<=_81&&_81<_83){
ch=ch.charAt(0);
if(_81+_82>_83){
_82=_83-_81;
}
this._chars.splice(_83-_82,_82);
this._attributes.splice(_83-_82,_82);
var _84=new Array(_82);
var _85=new Array(_82);
for(var i=0;i<_82;i++){
this._chars.splice(_81+i,0,ch);
this._attributes.splice(_81+i,0,new Attribute());
}
this.clearCache();
}
}
};
Line.prototype.putCharacter=function(ch,_86,_87){
if(isCharacter(ch)&&isDefined(_86)&&_86.constructor==Attribute&&isNumber(_87)){
if(0<=_87&&_87<this._chars.length){
this._chars[_87]=ch.charAt(0);
this._attributes[_87]=_86;
this.clearCache();
}
}
};
Line.prototype.resize=function(_88){
if(isNumber(_88)){
var _89=this._chars.length;
if(Line.MIN_WIDTH<=_88&&_88<=Line.MAX_WIDTH&&_89!=_88){
this._chars.length=_88;
if(_88>_89){
for(var i=_89;i<_88;i++){
this._chars[i]=" ";
this._attributes[i]=new Attribute();
}
}
this.clearCache();
}
}
};
Line.prototype.select=function(_8a,_8b){
var _8c=(_8b)?this._chars.length:this.getLastNonWhiteOffset();
var _8d=new Range(0,_8c);
var _8a=_8a.clamp(_8d);
var _8e=this._attributes;
var _8c=_8a.endingOffset;
var _8f=false;
for(var i=_8a.startingOffset;i<_8c;i++){
var _90=_8e[i];
if(_90.selected===false){
_90=_90.copy();
_90.selected=true;
_8e[i]=_90;
_8f=true;
}
}
if(_8f){
this.clearCache();
}
return _8f;
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
var _91=this;
this._queue=[];
this._applicationKeys=false;
this._playbackState=KeyHandler.STOPPED;
this.clearEvents();
this._playbackID=null;
document.onkeypress=function(e){
return _91.processKeyPress(e);
};
document.onkeydown=function(e){
return _91.processKeyDown(e);
};
};
KeyHandler.prototype.addEvent=function(_92,e){
if(e){
var _93={};
switch(_92){
case KeyHandler.KEY_DOWN:
_93.keyCode=e.keyCode;
_93.ctrlKey=e.ctrlKey;
_93.altKey=e.altKey;
_93.shiftKey=e.shiftKey;
break;
case KeyHandler.KEY_PRESS:
_93.keyCode=e.keyCode;
_93.which=e.which;
_93.ctrlKey=e.ctrlKey;
_93.altKey=e.altKey;
_93.metaKey=e.metaKey;
break;
default:
return;
}
this._events.keys.push({time:new Date().getTime(),type:_92,event:_93});
}
};
KeyHandler.prototype.addKeys=function(_94){
this._queue.push(_94);
if(isDefined(this.callback)){
this.callback(true);
}
};
KeyHandler.prototype.clearEvents=function(){
this._events={user_agent:{browser:BrowserDetect.browser,version:BrowserDetect.version,os:BrowserDetect.OS},keys:[]};
};
KeyHandler.prototype.dequeueAll=function(){
var _95=this._queue.join("");
this._queue.length=0;
return _95;
};
KeyHandler.prototype.getApplicationKeys=function(){
return this._applicationKeys;
};
KeyHandler.prototype.hasContent=function(){
return this._queue.length>0;
};
KeyHandler.prototype.play=function(_96){
if(this._playbackState!=KeyHandler.PLAYING){
this._playbackState=KeyHandler.PLAYING;
_96=_96||this._events.keys;
var _97=this;
var i=0;
var _98=function(){
var _99=_96[i++];
switch(_99.type){
case KeyHandler.KEY_DOWN:
_97.processKeyDown(_99.event);
break;
case KeyHandler.KEY_PRESS:
_97.processKeyPress(_99.event);
break;
default:
break;
}
if(_97._playbackState==KeyHandler.PLAYING&&i<_96.length){
var _9a=clamp(_96[i].time-_99.time,0,1000);
this._playbackID=window.setTimeout(_98,_9a);
}
};
_98();
}
};
KeyHandler.prototype.processKeyDown=function(e){
if(!e){
e=window.event;
}
if(this._playbackState==KeyHandler.RECORDING){
this.addEvent(KeyHandler.KEY_DOWN,e);
}
var _9b=e.keyCode;
var _9c=null;
var _9d=this._applicationKeys;
if(BrowserDetect.browser=="Firefox"&&(e.keyCode==8||(37<=e.keyCode&&e.keyCode<=40))){
}else{
switch(_9b){
case 8:
_9c=KeyHandler.BACKSPACE;
break;
case 9:
_9c=KeyHandler.TAB;
break;
case 27:
_9c=KeyHandler.ESCAPE;
break;
case 33:
_9c=KeyHandler.PAGE_UP;
break;
case 34:
_9c=KeyHandler.PAGE_DOWN;
break;
case 35:
_9c=(_9d)?KeyHandler.APP_END:KeyHandler.END;
break;
case 36:
_9c=(_9d)?KeyHandler.APP_HOME:KeyHandler.HOME;
break;
case 37:
_9c=(_9d)?KeyHandler.APP_LEFT:KeyHandler.LEFT;
break;
case 38:
_9c=(_9d)?KeyHandler.APP_UP:KeyHandler.UP;
break;
case 39:
_9c=(_9d)?KeyHandler.APP_RIGHT:KeyHandler.RIGHT;
break;
case 40:
_9c=(_9d)?KeyHandler.APP_DOWN:KeyHandler.DOWN;
break;
case 45:
_9c=KeyHandler.INSERT;
break;
case 46:
_9c=KeyHandler.DELETE;
break;
case 112:
_9c=e.shiftKey?KeyHandler.F13:KeyHandler.F1;
break;
case 113:
_9c=e.shiftKey?KeyHandler.F14:KeyHandler.F2;
break;
case 114:
_9c=e.shiftKey?KeyHandler.F15:KeyHandler.F3;
break;
case 115:
_9c=e.shiftKey?KeyHandler.F16:KeyHandler.F4;
break;
case 116:
_9c=e.shiftKey?KeyHandler.F17:KeyHandler.F5;
break;
case 117:
_9c=e.shiftKey?KeyHandler.F18:KeyHandler.F6;
break;
case 118:
_9c=e.shiftKey?KeyHandler.F19:KeyHandler.F7;
break;
case 119:
_9c=e.shiftKey?KeyHandler.F20:KeyHandler.F8;
break;
case 120:
_9c=KeyHandler.F9;
break;
case 121:
_9c=KeyHandler.F10;
break;
case 122:
_9c=KeyHandler.F11;
break;
case 123:
_9c=KeyHandler.F12;
break;
default:
if(!e.ctrlKey||(e.ctrlKey&&e.altKey)||(e.keyCode==17)){
}else{
if(e.shiftKey){
switch(_9b){
case 50:
_9c=String.fromCharCode(0);
break;
case 54:
_9c=String.fromCharCode(30);
break;
case 94:
_9c=String.fromCharCode(30);
break;
case 109:
_9c=String.fromCharCode(31);
break;
default:
break;
}
}else{
switch(_9b){
case 32:
_9c=String.fromCharCode(0);
break;
case 190:
_9c=String.fromCharCode(30);
break;
case 219:
_9c=String.fromCharCode(27);
break;
case 220:
_9c=String.fromCharCode(28);
break;
case 221:
_9c=String.fromCharCode(29);
break;
default:
if(65<=_9b&&_9b<=90){
_9c=String.fromCharCode(_9b-64);
}
break;
}
}
}
break;
}
}
if(_9c!==null){
this.addKeys(_9c);
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
var _9e=this._applicationKeys;
var _9f=null;
switch(e.keyCode){
case 8:
_9f=KeyHandler.BACKSPACE;
break;
case 37:
_9f=(_9e)?KeyHandler.APP_LEFT:KeyHandler.LEFT;
break;
case 38:
_9f=(_9e)?KeyHandler.APP_UP:KeyHandler.UP;
break;
case 39:
_9f=(_9e)?KeyHandler.APP_RIGHT:KeyHandler.RIGHT;
break;
case 40:
_9f=(_9e)?KeyHandler.APP_DOWN:KeyHandler.DOWN;
break;
}
if(_9f!==null){
this.addKeys(_9f);
}
}else{
if((e.ctrlKey&&!e.altKey)||(e.which==0)||(e.keyCode==8)||(e.keyCode==16)){
}else{
var _a0;
if(e.keyCode){
_a0=e.keyCode;
}
if(e.which){
_a0=e.which;
}
if(e.altKey&&!e.ctrlKey){
this.addKeys(KeyHandler.ESCAPE);
}
this.addKeys(String.fromCharCode(_a0));
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
KeyHandler.prototype.setApplicationKeys=function(_a1){
if(isBoolean(_a1)){
this._applicationKeys=_a1;
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
function XTermHandler(_a2){
this._term=_a2;
this._insertMode=false;
this._missingCommands={};
};
XTermHandler.prototype.BEL=function(_a3,_a4){
};
XTermHandler.prototype.BS=function(_a5,_a6){
var col=this._term.getColumn()-1;
col=Math.max(0,col);
this._term.setColumn(col);
};
XTermHandler.prototype.CHA=function(_a7,_a8){
var _a9=0;
if(_a8.length>0){
_a9=_a8-1;
}
this._term.setColumn(_a9);
};
XTermHandler.prototype.CR=function(_aa,_ab){
this._term.setColumn(0);
};
XTermHandler.prototype.CUB=function(_ac,_ad){
var _ae=1;
if(_ad.length>0){
_ae=_ad-0;
if(_ae==0){
_ae=1;
}
}
var col=this._term.getColumn()-_ae;
col=Math.max(0,col);
this._term.setColumn(col);
};
XTermHandler.prototype.CUD=function(_af,_b0){
var _b1=1;
if(_b0.length>0){
_b1=_b0-0;
if(_b1==0){
_b1=1;
}
}
var _b2=this._term.getRow();
var _b3=this._term.getScrollRegion().bottom;
var _b4;
if(_b2<=_b3){
_b4=Math.min(_b2+_b1,_b3);
}else{
_b4=Math.min(_b2+_b1,this._term.getHeight()-1);
}
this._term.setRow(_b4);
};
XTermHandler.prototype.CUF=function(_b5,_b6){
var _b7=1;
if(_b6.length>0){
_b7=_b6-0;
if(_b7==0){
_b7=1;
}
}
var col=this._term.getColumn()+_b7;
col=Math.min(col,this._term.getWidth()-1);
this._term.setColumn(col);
};
XTermHandler.prototype.CUP=function(_b8,_b9){
var row=0;
var col=0;
var _ba=this._term.getHeight();
if(_b9.length>0){
var _bb=_b9.split(/;/);
var row=_bb[0]-1;
var col=_bb[1]-1;
}
if(row>=_ba){
var _bc=_ba-row;
row=_ba-1;
this._term.scrollUp(_bc);
}
this._term.setPosition(row,col);
};
XTermHandler.prototype.CUU=function(_bd,_be){
var _bf=1;
if(_be.length>0){
_bf=_be-0;
if(_bf==0){
_bf=1;
}
}
var _c0=this._term.getRow();
var _c1=this._term.getScrollRegion().top;
var _c2;
if(_c1<=_c0){
_c2=Math.max(_c1,_c0-_bf);
}else{
_c2=Math.max(0,_c0-_bf);
}
this._term.setRow(_c2);
};
XTermHandler.prototype.DCH=function(_c3,_c4){
var _c5=_c4-0;
this._term.deleteCharacter(_c5);
};
XTermHandler.prototype.DECALN=function(_c6,_c7){
this._term.clear("E");
};
XTermHandler.prototype.DECRC=function(_c8,_c9){
this._term.popPosition();
};
XTermHandler.prototype.DECPAM=function(_ca,_cb){
this._term.setApplicationKeys(true);
};
XTermHandler.prototype.DECPNM=function(_cc,_cd){
this._term.setApplicationKeys(false);
};
XTermHandler.prototype.DECRST=function(_ce,_cf){
var _d0=_cf-0;
switch(_d0){
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
this.genericHandler(_ce,_cf);
break;
}
};
XTermHandler.prototype.DECSC=function(_d1,_d2){
this._term.pushPosition();
};
XTermHandler.prototype.DECSET=function(_d3,_d4){
var _d5=_d4-0;
switch(_d5){
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
this.genericHandler(_d3,_d4);
break;
}
};
XTermHandler.prototype.DECSTBM=function(_d6,_d7){
var _d8=_d7.split(/;/);
var top=_d8[0]-1;
var _d9=_d8[1]-1;
this._term.setScrollRegion(top,0,_d9,this._term.getWidth()-1);
};
XTermHandler.prototype.DL=function(_da,_db){
var _dc=1;
if(_db.length>0){
_dc=_db-0;
if(_dc==0){
_dc=1;
}
}
this._term.deleteLine(_dc);
};
XTermHandler.prototype.ED=function(_dd,_de){
var _df=_de-0;
switch(_df){
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
this.genericHandler(_dd+":"+_de,"");
break;
}
};
XTermHandler.prototype.EL=function(_e0,_e1){
var _e2=_e1-0;
switch(_e2){
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
this.genericHandler(_e0+":"+_e1,"");
break;
}
};
XTermHandler.prototype.genericHandler=function(_e3,_e4){
if(this._missingCommands.hasOwnProperty(_e3)===false){
this._missingCommands[_e3]=0;
}
this._missingCommands[_e3]++;
};
XTermHandler.prototype.getMissingCommands=function(){
return this._missingCommands;
};
XTermHandler.prototype.HVP=XTermHandler.prototype.CUP;
XTermHandler.prototype.ICH=function(_e5,_e6){
var _e7=_e6-0;
this._term.insertCharacter(" ",_e7);
};
XTermHandler.prototype.IL=function(_e8,_e9){
var _ea=1;
if(_e9.length>0){
_ea=_e9-0;
if(_ea==0){
_ea=1;
}
}
this._term.insertLine(_ea);
};
XTermHandler.prototype.IND=function(_eb,_ec){
var _ed=this._term.getRow();
var _ee=this._term.getScrollRegion().bottom;
var _ef=_ed+1;
if(_ed<=_ee){
this._term.setRow(_ef);
}else{
this._term.scrollUp(1);
this._term.setRow(_ee);
}
};
XTermHandler.prototype.LF=function(_f0,_f1){
var _f2=this._term;
var row=_f2.getRow()+1;
var _f3=_f2.getScrollRegion().bottom;
if(row>_f3){
_f2.scrollUp();
row=_f3;
}
_f2.setPosition(row,0);
};
XTermHandler.prototype.NEL=XTermHandler.prototype.LF;
XTermHandler.prototype.processCharacter=function(_f4,_f5){
if(this._insertMode){
this._term.insertCharacter(" ",1);
}
this._term.displayCharacters(_f5);
};
XTermHandler.prototype.RI=function(_f6,_f7){
var _f8=this._term.getRow();
var _f9=this._term.getScrollRegion().top;
var _fa=_f8-1;
if(_f9<=_fa){
this._term.setRow(_fa);
}else{
this._term.scrollDown(1);
this._term.setRow(_f9);
}
};
XTermHandler.prototype.RM=function(_fb,_fc){
var _fd=_fc-0;
switch(_fd){
case 4:
this._insertMode=false;
break;
case 2:
case 12:
case 20:
default:
this.genericHandler(_fb,_fc);
break;
}
};
XTermHandler.prototype.SD=function(_fe,_ff){
var _100=1;
if(_ff.length>0){
_100=_ff-0;
}
var _101=this._term.getRow();
var _102=this._term.getScrollRegion().top;
var _103=_101-_100;
if(_102<=_103){
this._term.setRow(_103);
}else{
this._term.scrollDown(_100);
this._term.setRow(_102);
}
};
XTermHandler.prototype.SET_TEXT_PARAMS=function(_104,_105){
var _106=_105.split(/;/);
var code=_106[0]-0;
var text=_106[1];
if(code==0){
this._term.setTitle(text);
}else{
this.genericHandler(_104+":"+_105,"");
}
};
XTermHandler.prototype.SET_TEXT_PARAMS2=XTermHandler.prototype.SET_TEXT_PARAMS;
XTermHandler.prototype.SGR=function(_107,_108){
var attr=this._term.getCurrentAttribute();
var _109=_108.split(/;/);
for(var i=0;i<_109.length;i++){
var _10a=_109[i]-0;
if(_10a<50){
var tens=Math.floor(_10a/10);
var ones=_10a%10;
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
this.genericHandler(_107+":"+_108,"");
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
this.genericHandler(_107+":"+_108,"");
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
this.genericHandler(_107+":"+_108,"");
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
this.genericHandler(_107+":"+_108,"");
break;
}
break;
default:
this.genericHandler(_107+":"+_108,"");
break;
}
}else{
this.genericHandler(_107+":"+_108,"");
}
}
this._term.setCurrentAttribute(attr);
};
XTermHandler.prototype.SM=function(_10b,_10c){
var mode=_10c-0;
switch(mode){
case 4:
this._insertMode=true;
break;
case 2:
case 12:
case 20:
default:
this.genericHandler(_10b,_10c);
break;
}
};
XTermHandler.prototype.SU=function(_10d,_10e){
var _10f=1;
if(_10e.length>0){
_10f=_10e-0;
}
var _110=this._term.getRow();
var _111=this._term.getScrollRegion().bottom;
var _112=_110+_10f;
if(_110<=_111){
this._term.setRow(_112);
}else{
this._term.scrollUp(_10f);
this._term.setRow(_111);
}
};
XTermHandler.prototype.TAB=function(_113,_114){
var _115=this._term.getColumn();
var _116=8-(_115%8);
this._term.displayCharacters(new Array(_116+1).join(" "));
};
XTermHandler.prototype.VPA=function(_117,_118){
var row=0;
if(_118.length>0){
row=_118-1;
}
this._term.setRow(row);
};
XTermHandler.prototype.VT=XTermHandler.prototype.LF;
function TermParser(_119,_11a){
if(_119===null||_119===undefined){
throw new Error("Parsing tables must be defined when creating a new TermParser");
}
this._processTables(_119);
this._actions=_119.actions;
this._nodes=_119.nodes;
this.setHandler(_11a);
this.singleStep=false;
this.offset=-1;
};
TermParser.prototype.getHandler=function(){
return this._handler;
};
TermParser.prototype.parse=function(_11b){
var _11c=0;
var _11d=isString(_11b)?_11b.length:0;
while(_11c<_11d){
var _11e=0;
var _11f=this._nodes[_11e][1];
var _120=(_11f==-1)?-2:_11c;
for(var i=_11c;i<_11d;i++){
var _121=this._nodes[_11e];
if(_121){
var _122=_11b.charCodeAt(i);
var _123=_121[0][_122];
if(_123!=-1){
_11e=_123;
var _124=this._nodes[_11e][1];
if(_124!=-1){
_120=i;
_11f=_124;
}
}else{
break;
}
}
}
if(_11f==-1){
if(this._handler!=null){
if(this._handler.processCharacter){
this._handler.processCharacter("processCharacter",_11b.charAt(_11c));
}
}
_11c++;
}else{
var _125=_120+1;
if(this._handler!=null){
var info=this._actions[_11f];
var _126=info[0];
var _127="";
if(info.length>=3&&info[1]!=-1&&info[2]!=-1){
_127=_11b.substring(_11c+info[1],_125-info[2]);
}
this._handler[_126](_126,_127);
}
_11c=_125;
if(this.singleStep){
this.offset=_11c;
break;
}
}
}
};
TermParser.prototype._processTables=function(_128){
if(_128.hasOwnProperty("processed")==false||_128.processed==false){
switch(_128.format){
case "expanded":
break;
case "rle":
var mos=new Array(256);
for(var i=0;i<mos.length;i++){
mos[i]=-1;
}
var _129=_128.nodes;
for(var i=0;i<_129.length;i++){
var _12a=_129[i][0];
var _12b=[];
for(var j=0;j<_12a.length;j++){
var _12c=_12a[j];
if(_12c<0){
_12b=_12b.concat(mos.slice(0,-_12c));
}else{
var _12d=_12c>>8;
var _12e=(_12c&255)+1;
for(var k=0;k<_12e;k++){
_12b.push(_12d);
}
}
}
_129[i][0]=_12b;
}
break;
default:
break;
}
_128.processed=true;
}
};
TermParser.prototype.setHandler=function(_12f){
var _130=null;
if(_12f){
var _131=null;
var _132=function(_133,_134){
};
for(var i=0;i<this._actions.length;i++){
var _135=this._actions[i];
var _136=_135[0];
if(!_12f[_136]){
if(_130==null){
_130=protectedClone(_12f);
if(!_12f.genericHandler){
_131=_132;
}else{
_131=_12f.genericHandler;
}
}
_130[_136]=_131;
}
}
}
if(_130==null){
this._handler=_12f;
}else{
this._handler=_130;
}
};
TermComm.POLLING_INTERVAL_MIN=125;
TermComm.POLLING_INTERVAL_MAX=2000;
TermComm.POLLING_GROWTH_RATE=2;
TermComm.DEFAULT_REQUEST_URL="/stream";
TermComm.DEFAULT_GET_UNIQUE_ID_URL="/id";
function TermComm(_137,_138){
var self=this;
this.terminal=_137;
this.keyHandler=_137.getKeyHandler();
this.keyHandler.callback=function(){
self.sendKeys();
};
this.minInterval=125;
this.maxInterval=2000;
this.growthRate=2;
this.timeoutInterval=5000;
this.requestURL=TermComm.DEFAULT_REQUEST_URL;
this.getUniqueIdURL=TermComm.DEFAULT_GET_UNIQUE_ID_URL;
if(isDefined(_138)){
if(_138.hasOwnProperty("minInterval")&&isNumber(_138.minInterval)){
this.minInterval=_138.minInterval;
}
if(_138.hasOwnProperty("maxInterval")&&isNumber(_138.maxInterval)){
this.maxInterval=_138.maxInterval;
}
if(_138.hasOwnProperty("growthRate")&&isNumber(_138.growthRate)){
this.growthRate=_138.growthRate;
}
if(_138.hasOwnProperty("timeoutInterval")&&isNumber(_138.timeoutInterval)){
this.timeoutInterval=_138.timeoutInterval;
}
if(_138.hasOwnProperty("requestURL")&&isString(_138.requestURL)&&_138.requestURL.length>0){
this.requestURL=_138.requestURL;
}
if(_138.hasOwnProperty("getUniqueIdURL")&&isString(_138.getUniqueIdURL)&&_138.getUniqueIdURL.length>0){
this.getUniqueIdURL=_138.getUniqueIdURL;
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
var _139={id:this.terminal.getId(),cb:new Date().getTime()+":"+this.cacheBusterID++};
req.open("GET",createURL(this.requestURL,_139),true);
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
var _13a={id:id};
req.open("POST",createURL(this.requestURL,_13a),true);
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
TermComm.prototype.update=function(_13b){
if(this.running&&this.terminal.getId()!==null){
if(isBoolean(_13b)){
if(this.requestID!==null){
window.clearTimeout(this.requestID);
this.requestID=null;
}
if(_13b){
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
function Term(id,_13c,_13d,_13e){
if(isString(id)===false||id.length===0){
id="terminal";
}
this._id=(_13e&&_13e.hasOwnProperty("id"))?_13e.id:null;
this._remainingText="";
this._rootNode=document.getElementById(id);
if(this._rootNode){
this._rootNode.className="webterm";
this._termNode=document.createElement("pre");
this._rootNode.appendChild(this._termNode);
this._width=(isNumber(_13c))?clamp(_13c,Line.MIN_WIDTH,Line.MAX_WIDTH):Line.DEFAULT_WIDTH;
this._height=(isNumber(_13d))?clamp(_13d,Term.MIN_HEIGHT,Term.MAX_HEIGHT):Term.DEFAULT_HEIGHT;
this._title="Aptana WebTerm";
this._row=0;
this._column=0;
this._scrollRegion={top:0,left:0,bottom:this._height-1,right:this._width-1};
this._cursorVisible=true;
this._buffers=[];
this._positions=[];
this._currentAttribute=new Attribute();
this._sendResizeSequence=(_13e&&_13e.hasOwnProperty("sendResizeSequence"))?_13e.sendResizeSequence:true;
this._showTitle=(_13e&&_13e.hasOwnProperty("showTitle"))?_13e.showTitle:true;
this._onTitleChange=(_13e&&_13e.hasOwnProperty("onTitleChange"))?_13e.onTitleChange:null;
this._hasSelection=false;
this._fontInfo=new FontInfo("fontInfo");
this._lastStartingOffset=null;
this._lastEndingOffset=null;
var _13f=(_13e&&_13e.hasOwnProperty("handler"))?_13e.handler:new XTermHandler(this);
var _140=(_13e&&_13e.hasOwnProperty("tables"))?_13e.tables:XTermTables;
var _141=(_13e&&_13e.hasOwnProperty("parser"))?_13e.parser:new TermParser(_140,_13f);
var _142=(_13e&&_13e.hasOwnProperty("keyHandler"))?_13e.keyHandler:new KeyHandler();
this._parser=_141;
this._keyHandler=_142;
var _143=(_13e&&_13e.hasOwnProperty("commHandler"))?_13e.commHandler:new TermComm(this,_13e);
var _144=(_13e&&_13e.hasOwnProperty("autoStart"))?_13e.autoStart:true;
this._commHandler=_143;
this.createBuffer();
var self=this;
dragger(this._rootNode,function(sx,sy,ex,ey){
self.updateSelection(sx,sy,ex,ey);
});
this.refresh();
if(_144){
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
var _145=new Array(this._height);
for(var i=0;i<_145.length;i++){
_145[i]=this.createLine();
}
this._lines=_145;
};
Term.prototype.createLine=function(){
return new Line(this._width,this._fontInfo);
};
Term.prototype.clearSelection=function(_146){
var _147=this._lines;
var _148=_147.length;
for(var i=0;i<_148;i++){
_147[i].clearSelection();
}
this._hasSelection=false;
this._lastStartingOffset=null;
this._lastEndingOffset=null;
if((isBoolean(_146))?_146:true){
this.refresh();
}
};
Term.prototype.deleteCharacter=function(_149){
this._lines[this._row].deleteCharacter(this._column,_149);
};
Term.prototype.deleteLine=function(_14a){
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
var _14c=this._lines.splice(this._row,_14a);
for(var i=0;i<_14a;i++){
_14c[i].clear();
}
if(_14b.bottom+1==this.height){
this._lines=this._lines.concat(_14c);
}else{
for(var i=0;i<_14a;i++){
this._lines.splice(_14b.bottom-_14a+i+1,0,_14c[i]);
}
}
}
}else{
}
}
};
Term.prototype.deselect=function(s,e){
var _14d=this.getWidth();
var _14e=this.getHeight();
var _14f=new Range(s,e).clamp(new Range(0,_14d*_14e));
if(_14f.isEmpty()===false){
var _150=Math.floor(_14f.startingOffset/_14d);
var _151=Math.ceil(_14f.endingOffset/_14d);
var _152=_150*_14d;
for(var i=_150;i<=_151;i++){
var _153=_152+_14d;
var _154=new Range(_152,_153).clamp(_14f);
if(_154.isEmpty()===false){
var _155=_154.move(-_152);
var line=this._lines[i];
line.deselect(_155);
}
_152=_153;
}
}
};
Term.prototype.displayCharacters=function(_156){
if(isString(_156)){
for(var i=0;i<_156.length;i++){
var ch=_156.charAt(i);
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
var _157=null;
if(this.hasSelection()){
var _158=this._lines;
var _159=_158.length;
for(var i=0;i<_159;i++){
var _15a=_158[i].getSelectedText();
if(_15a!==null){
if(_157===null){
_157=[];
}
_157.push(_15a);
}
}
}
return (_157!==null)?_157.join("\n"):null;
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
Term.prototype.insertCharacter=function(ch,_15b){
this._lines[this._row].insertCharacter(ch,this._column,_15b);
};
Term.prototype.insertLine=function(_15c){
_15c=(_15c===undefined)?1:_15c;
if(_15c>0){
var _15d=this._scrollRegion;
if(_15d.left==0&&_15d.right==this._width-1){
if(this._row+_15c>_15d.bottom){
_15c=_15d.bottom-this._row+1;
}
if(_15c==this._height){
this.clear();
}else{
var _15e=this._lines.splice(_15d.bottom-_15c+1,_15c);
for(var i=0;i<_15c;i++){
_15e[i].clear();
}
if(this._row==0){
this._lines=_15e.concat(this._lines);
}else{
for(var i=0;i<_15c;i++){
this._lines.splice(this._row+i,0,_15e[i]);
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
var _15f=this._positions.pop();
this._row=_15f[0];
this._column=_15f[1];
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
var _160=[];
var attr=null;
var _161=this._title+" — "+this._width+"x"+this._height;
var _162="<div class='title'>"+_161+"</div>";
for(var row=0;row<this._height;row++){
var line=this._lines[row];
var _163=(this._cursorVisible)?(row==this._row)?this._column:-1:-1;
var _164=line.getHTMLInfo(attr,_163);
attr=_164.attribute;
_160.push(_164.html);
}
if(attr!=null){
_160[_160.length-1]+=attr.getEndingHTML();
}
if(this._showTitle){
this._termNode.innerHTML=_162+_160.join("<br />");
}else{
this._termNode.innerHTML=_160.join("<br />");
}
};
Term.prototype.scrollDown=function(_165){
_165=(_165===undefined)?1:_165;
if(_165>0){
var _166=this._scrollRegion;
if(_166.left==0&&_166.right==this._width-1){
var _167=_166.bottom-_166.top+1;
if(_165>=_167){
this.clear();
}else{
var _168=this._lines.splice(_166.bottom-_165+1,_165);
for(var i=0;i<_165;i++){
_168[i].clear();
}
if(_166.top==0){
this._lines=_168.concat(this._lines);
}else{
for(var i=0;i<_165;i++){
this._lines.splice(_166.top+i,0,_168[i]);
}
}
}
}else{
}
}
};
Term.prototype.scrollUp=function(_169){
_169=(_169===undefined)?1:_169;
if(_169>0){
var _16a=this._scrollRegion;
if(_16a.left==0&&_16a.right==this._width-1){
var _16b=_16a.bottom-_16a.top+1;
if(_169>=_16b){
this.clear();
}else{
var _16c=this._lines.splice(_16a.top,_169);
for(var i=0;i<_169;i++){
_16c[i].clear();
}
if(_16a.bottom+1==this.height){
this._lines=this._lines.concat(_16c);
}else{
for(var i=0;i<_169;i++){
this._lines.splice(_16a.bottom-_169+i+1,0,_16c[i]);
}
}
}
}else{
}
}
};
Term.prototype.select=function(s,e,_16d){
var _16e=this.getWidth();
var _16f=this.getHeight();
var _170=new Range(s,e).clamp(new Range(0,_16e*_16f));
var _171=this.hasSelection()&&!_16d;
var _172=false;
if(_171){
this.clearSelection(false);
}
if(_170.isEmpty()===false){
var _173=Math.floor(_170.startingOffset/_16e);
var _174=Math.ceil(_170.endingOffset/_16e);
var _175=_173*_16e;
for(var i=_173;i<=_174;i++){
var _176=_175+_16e;
var _177=new Range(_175,_176).clamp(_170);
if(_177.isEmpty()===false){
var _178=_170.endingOffset>_177.endingOffset;
var _179=_177.move(-_175);
var line=this._lines[i];
if(line.select(_179,_178)){
_172=true;
}
}
_175=_176;
}
}
this._hasSelection=_172;
if(_171||_172){
this.refresh();
}
};
Term.prototype.selectAll=function(){
this.select(0,this._width*this._height);
};
Term.prototype.setApplicationKeys=function(_17a){
if(isBoolean(_17a)){
this._keyHandler.setApplicationKeys(_17a);
}
};
Term.prototype.setColumn=function(_17b){
if(isNumber(_17b)&&0<=_17b&&_17b<this._width){
this._column=_17b;
}
};
Term.prototype.setCurrentAttribute=function(attr){
if(isDefined(attr)&&attr.constructor===Attribute){
this._currentAttribute=attr;
}
};
Term.prototype.setCursorVisible=function(_17c){
if(isBoolean(_17c)){
this._cursorVisible=_17c;
}
};
Term.prototype.setHeight=function(_17d){
this.setSize(this._width,_17d);
};
Term.prototype.setPosition=function(row,_17e){
if(isNumber(row)&&0<=row&&row<this._height){
this._row=row;
}
if(isNumber(_17e)&&0<=_17e&&_17e<this._width){
this._column=_17e;
}
};
Term.prototype.setRow=function(row){
if(0<=row&&row<this._height){
this._row=row;
}
};
Term.prototype.setScrollRegion=function(top,left,_17f,_180){
if(isNumber(top)&&isNumber(left)&&isNumber(_17f)&&isNumber(_180)){
if(top<_17f&&left<_180){
var _181=(0<=top&&top<this._height);
var _182=(0<=left&&left<this._width);
var _183=(0<=_17f&&_17f<this._height);
var _184=(0<=_180&&_180<this._width);
if(_181&&_182&&_183&&_184){
this._scrollRegion={top:top,left:left,bottom:_17f,right:_180};
}
}
}
};
Term.prototype.setSize=function(_185,_186){
var _187=false;
if(isNumber(_185)&&Line.MIN_WIDTH<=_185&&_185<=Line.MAX_WIDTH&&this._width!=_185){
for(var i=0;i<this._height;i++){
this._lines[i].resize(_185);
}
this._width=_185;
this._column=Math.min(this._width-1,this._column);
_187=true;
}
if(isNumber(_186)&&Term.MIN_HEIGHT<=_186&&_186<=Term.MAX_HEIGHT&&this._height!=_186){
if(_186>this._height){
for(var i=this._height;i<_186;i++){
this._lines.push(this.createLine());
}
}else{
this._lines=this._lines.splice(this._height-_186,_186);
}
this._height=_186;
this._row=Math.min(this._height-1,this._row);
_187=true;
}
if(_187){
this.setScrollRegion(0,0,this._height-1,this._width-1);
if(this._sendResizeSequence){
var ESC=String.fromCharCode(27);
var CSI=ESC+"[";
this._keyHandler.addKeys(CSI+[8,this._height,this._width].join(";")+"t");
}
}
};
Term.prototype.setTitle=function(_188){
this._title=_188;
if(isFunction(this._onTitleChange)){
this._onTitleChange(_188);
}
};
Term.prototype.showTitle=function(_189){
if(isBoolean(_189)){
this._showTitle=_189;
this.refresh();
}
};
Term.prototype.sizeToWindow=function(){
var m=this._fontInfo.getCharacterSize("M");
var _18a=m[0];
var _18b=m[1];
var _18c=Math.floor(getWindowWidth()/_18a)-1;
var _18d=Math.floor(getWindowHeight()/_18b);
this.setSize(_18c,_18d);
};
Term.prototype.toggleRunState=function(){
if(this._commHandler!==null){
if(this._id===null&&this._commHandler.isRunning()==false){
this._id=this._commHandler.getUniqueID();
}
this._commHandler.toggleRunState();
}
};
Term.prototype.setWidth=function(_18e){
this.setSize(_18e,this._height);
};
Term.prototype.toString=function(){
var _18f=[];
for(var i=0;i<this._lines.length;i++){
_18f.push(this._lines[i].toString());
}
return _18f.join("\n");
};
Term.prototype.updateSelection=function(_190,_191,endX,endY){
if(isNumber(_190)&&isNumber(_191)&&isNumber(endX)&&isNumber(endY)){
var _192=this._lines;
var _193=this._fontInfo.getCharacterHeight("M");
var _194=function(y){
var _195=_192.length;
var _196=0;
var _197=null;
for(var i=0;i<_195;i++){
var line=_192[i];
var _198=_196+_193;
if(_196<=y&&y<_198){
_197=i;
break;
}else{
_196=_198;
}
}
return _197;
};
var _199=_194(_191);
var _19a=_194(endY);
if(_199!==null&&_19a!==null){
var _19b=_192[_199].getOffsetFromPosition(_190);
var _19c=_192[_19a].getOffsetFromPosition(endX);
var _19d=_199*this.getWidth()+_19b;
var _19e=_19a*this.getWidth()+_19c;
if(this._lastStartingOffset!==_19d||this._lastEndingOffset!==_19e){
this.select(_19d,_19e);
this._lastStartingOffset=_19d;
this._lastEndingOffset=_19e;
}
}
}
};

