DialogHelper = {
  createConfirmBox: function(header, message, callback){
    var viewFunct = function(){
      var contents;
      var confirm;
      var lightbox = this;

      with(Elements.Builder) {
        contents = div(h2(header), 
                    p(message), 
                      confirm = input({'type':'button', 'value': 'Confirm'}),
                        " or ",
                          a("Cancel", {'class':'cancel', 'href':'#'}));
      }
      
      confirm.observe('click', function(){
        callback();
        lightbox.deactivate();
      });
      
      return contents;
    }

    new Lightbox( viewFunct, {cache: false, ajaxify: false}).activate();
  },
  
  createInfoBox: function(header, message, returnLinkText, messageAsHTML){
    if (!returnLinkText)
      returnLinkText = "Close Dialog"
      
    var viewFunct = function(){
      var contents;
      var returnLink;
      var lightbox = this;

      with(Elements.Builder) {
        var pContent;
        if (messageAsHTML)
        {
          pContent = p();
          pContent.appendChild(message);
        } else {
          pContent = p(message);
        }
        contents = div(h2(header), 
                      pContent, 
                      returnLink = a("<< " + returnLinkText, {'href':'#'}));
      }
      
      returnLink.observe('click', function(event){
        lightbox.deactivate();
        event.stop();
      });
      
      return contents;
    }

    new Lightbox(viewFunct).activate();
  }
}