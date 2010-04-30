activeLightbox = null;

Lightbox = Class.create({
  // If ajaxify option is true then this will convert normal form requests into
  // ajax requests where the content is rendered back into the lightbox. By
  // default it is true.
  initialize: function(viewFunct, options) {
    this.viewFunct = viewFunct;
    this.activeResizeHandler = this._resizeHandler.bind(this);
    this.options = {
      ajaxify: true,
      verticalPercentDown: 30 // percentage from top to place vertical center of lightbox
    };
    Object.extend(this.options, options || {});
    this.hiddenObjectTags = [];
  },

  activate: function(){
    if (Portal.activeLightbox) {
      Portal.activeLightbox.deactivate();
    }
    Portal.activeLightbox = this;
  
    this.overlay = this._createOverlayElement();
    this.lightbox = this._createLightboxElement();
    
    var content = this.viewFunct();
    this._prepareView(content);
    
    this.contentEl.appendChild(content);
    
    document.body.appendChild(this.overlay);
    document.body.appendChild(this.lightbox);
    
    this.overlay.observe('click', function(event){
      Portal.activeLightbox.deactivate();
    });
    
    // save the initial height if an explicit height was not provided
    if (!this.options.height)
      this.options.height = this.lightbox.getHeight();
      
    if (!this.options.width)
      this.options.width = this.lightbox.getWidth();
    
	  // Hide object elements, like flash containers, when opening this lightbox
	  var objectTags = $$('object');
    for (var i = 0; i < objectTags.length; i++) {
      if (objectTags[i].visible()) {
        objectTags[i].hide();
        this.hiddenObjectTags.push(objectTags[i]);
      }
    }
	
    Event.observe(window, 'resize', this.activeResizeHandler);
    this.activeResizeHandler();
    // call again in case any scripts that executed changed the size
    this.activeResizeHandler.delay(0.05);
    return this;
  },
  
  hideCloseLink: function(){
    this.spacerEl.show();
    this.footerEl.hide();
    return this;
  },
  
  showCloseLink: function(){
    this.spacerEl.hide();
    this.footerEl.show();
    return this;
  },
  
  deactivate: function(e){
    Event.stopObserving(window, 'resize', this.activeResizeHandler);
    this.overlay.remove();
    var lightbox = this.lightbox;
    lightbox.fade({
      duration: 0.4,
      afterFinish: function(){
        lightbox.remove();
      }
    });
    
    //this.lightbox.remove();
    this.overlay = this.lightbox = null;
    Portal.activeLightbox = null;
    
    // Un-hide the hidden object elements
    for (var i = 0; i < this.hiddenObjectTags.length; i++) {
      this.hiddenObjectTags[i].show();
    }
    
    return this;
  },
  
  _prepareView: function(view){
    var lightbox = this;
    
    var hasCancelLink = false;
    
    view.select('.cancel').each(function(el){
      hasCancelLink = true;
      el.observe('click', function(event){
        lightbox.deactivate();
        event.stop();
      });
    });
    
    if (!hasCancelLink) {
      lightbox.showCloseLink();
    } else {
      lightbox.hideCloseLink();
    }
    
    if (this.options.ajaxify) {
      view.select('form').each(function(form){
        form.onsubmit = function(){
          console.debug("Submitting lightbox form to " + form.action);
          // TODO: do some type of effect to show that the form is submitting and updating
          var updateContainer = {
            success: view,
            failure: document.body
          }
          new Ajax.Updater(updateContainer, form.action, {
            asynchronous:true, 
            evalScripts:true, 
            parameters: Form.serialize(form),
            onComplete: function(response){ 
              lightbox._prepareView(view); 
              lightbox.activeResizeHandler();
            }
          });
          return false;
        }
      });
    }
    
    return this;
  },
  
  _resizeHandler: function(){
    var content = this.contentEl;
    content.setStyle({
      height: "",
      overflowY: 'visible'
    });
    
    var p = this.options.verticalPercentDown / 100;
    
    var maxHeight = document.viewport.getHeight();
    var maxWidth  = document.viewport.getWidth();
    var height    = this.lightbox.getHeight();
    var width     = this.lightbox.getWidth();
    var targetWidth = this.options.width;
    
    var top = ((Math.max(maxHeight-height, 0)) * p) - (maxHeight / 2);
    
    this.lightbox.setStyle({
      width: targetWidth + "px",
      marginLeft: -(width / 2) + "px",
      marginTop:  top +"px"
    });
    
    if (height > maxHeight) {
      var overflow = height - maxHeight;
      
      var targetContentHeight = content.getHeight() - overflow;

      content.setStyle({
        height: targetContentHeight + 'px',
        overflowY: 'scroll'
      });
      
      // at this point we may still not be the correct height because of padding adding to the height
      // so we need to recompute one last time to account for it.
      
      var paddingDelta = content.getHeight() - targetContentHeight;
      if (paddingDelta > 0) {
        content.setStyle({
          height: (targetContentHeight - paddingDelta) + 'px'
        });
      }
    }
    
    if (width > maxWidth) {
      this.lightbox.setStyle({
        marginLeft: -(maxWidth / 2) + "px"
      });
    }
    return this;
  },
  
  _createOverlayElement: function(){
    return Elements.Builder.div({'class': 'lightbox-overlay'});
  },
  
  _createLightboxElement: function(innerContent){
    var deactivateLink;
    var element;
    
    var t = this;
    
    with(Elements.Builder) {
      element = div({'class': 'lightbox'},
          div({'class': 'lightbox-inner1'},
              div({'class': 'lightbox-inner3'},
                t.contentEl = div({'class': 'lightbox-content'}),
                t.spacerEl = div({'class': 'lightbox-spacer'}),
                t.footerEl = div({'class': 'lightbox-footer'},
                  deactivateLink = a({'href': '#', 'class': 'lightbox-deactivate'}))), 
              div({'class': 'lightbox-inner2'}, '')));
    }
    var deactivateFunc = function(event){
      event.stop();
      this.deactivate();
      return false;
    };
    deactivateLink.observe('click', deactivateFunc.bind(this))
    return element;
  }
});
