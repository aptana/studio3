var elements = new Array();
var ids = new Array('header', 'repo_menu', 'repo_sub_menu', 'repos', 'footer', 'triangle');
elements.push(document.getElementById('network').children.item(0));
elements.push(document.getElementById('network').children.item(1));
elements.push(document.getElementById('network').children.item(2));
for(i=0; i<ids.length; i++) {
  elements.push(document.getElementById(ids[i]));
}
for(i=0; i<elements.length; i++) {
  elements[i].style.display = 'none';
}
