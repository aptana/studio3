/*
 * A base view for all the parts that display a list of items, check their status, and display user feedback
 * in a form of icons, tooltips and links.
 */
ItemsView = {};

ItemsView.Base = Class.create({
  initialize: function(){
    this.items = this.getItems();
  },
  /**
   * Render the items into the parentElement.
   * @param parentElement
   * @param tableClass The class to use for the rendered table style
   * @param data An optional pre-fetched data
   */
  render: function(parentElement, tableClass, data){
    // Check that we have the dispatch browser function
    if (typeof(dispatch) === 'undefined') {
      parentElement.appendChild(_studioOnlyContent());
      return;
    }
    // Make a dispatch call to get all the items
    var itemsResponce = this.dispatchForItems();
    itemsResponce = itemsResponce.evalJSON();
    var status = itemsResponce.data.status;
    // Get the data part of the response
    var allItems = this.getDispatchResponseData(itemsResponce);
    var items;
    var links;
    with (Elements.Builder) {
      // We have to wrap the rows in a tbody, otherwise, the internal browser
      // fails to display the rows.
      var tbodyItem;
      items = table({'class' : tableClass}, tbodyItem = tbody());
      var itemsNames = this.getItemsNames();
      var itemsCount = itemsNames.size();
      var errorShown = false;
      // Have the row elements stored in a hash, so we
      // can assign them when we create row function.
      var itemRowElements = $H();
      for( var i = 0; i < itemsCount; i++ ) {
        var item = this.items.get(itemsNames[i]);
        var itemRow = this.createRow(item, itemsNames[i], itemRowElements);
        // Get the items that were assigned as row elements
        var itemSpan = itemRowElements.get('itemSpan');
        var actionsSpan = itemRowElements.get('actionsSpan');
        // Check the status of the item. 
        // In case needed, disply a link to install/update the item.
        switch(status) {
          case ConfigurationStatus.PROCESSING:
            itemSpan.setAttribute('className', 'processing');
          break;
          case ConfigurationStatus.ERROR:
            if (!errorShown) {
              // Show this error with a timeout to simulate an asynchronous call.
              // We need this to give the page a chance to get the OK notification 
              // after the initial error notification.
              window.setTimeout(function() { 
                showError("Oops! we have a problem", "There was an problem updating the items status. More information about this problem can be found at the Aptana Studio error log."); 
                }, 1000);
              errorShown = true;
            }
            // Do not break this case!
          case ConfigurationStatus.OK:
            // In this case, we need to check whether this item needs an update/install
            var itemInfo = allItems.get(item[0]);
            // In case we never dispatched the check, the itemInfo will be undefined at this point.
            if (itemInfo) {
              if (itemInfo.exists == "yes") {
                // We have this item installed, and we just need to check it has the right version
                itemSpan.setAttribute('rawVersionOutput', itemInfo.rawOutput);
                itemSpan.setAttribute('installedVersion', itemInfo.version);
                if (itemInfo.compatibility == 'ok') {
                  itemSpan.setAttribute('className', 'ok');
                  itemSpan.setAttribute('itemStatus', 'ok');
                } else {
                  itemSpan.setAttribute('className', 'notice');
                  itemSpan.setAttribute('itemStatus', 'update');
                  var updateLink = this.createUpdateLink(item);
                  var updateSpan = span({'class' : 'action update'},  updateLink);
                  actionsSpan.appendChild(updateSpan);
                  updateLink.observe('click', this.dispatchUpdate.bind(this));
                }
              } else {
                // We don't have this item installed. Display an install link.
                var installLink = this.createInstallLink(item);
                var installSpan = span({'class' : 'action install'},  installLink);
                itemSpan.setAttribute('itemStatus', 'install');
                itemSpan.setAttribute('className', 'missing');
                actionsSpan.appendChild(installSpan);
                installLink.observe('click', this.dispatchInstall.bind(this));
              }
              if (typeof(this.setAdditionalItemAttributes) !== 'undefined') {
                // Set more attributes on this item in a subclassing class
                this.setAdditionalItemAttributes(itemInfo, itemSpan);
              }
            }
          break;
        }
        tbodyItem.appendChild(itemRow);
        // register the tooltips
        var tt = function(e) {
          this.showTooltip(e);
          e.stop();
          return true;
        }
        itemSpan.observe('mouseover', tt.bind(this));
        itemSpan.observe('mouseout', function(e) {
          tooltip.hide();
          return true;
        });
      }
      // Create optional links that we'll display below the table
      if (typeof(this.createExtendedElements) !== 'undefined') {
        links = this.createExtendedElements();
      }
    }
    _clearDescendants(parentElement);
    parentElement.appendChild(items);
    if (links) {
      parentElement.appendChild(links)
    }
  }
});

ItemsView.create = function(arg) {
  var klass;
  var classMethods = {};
  
  klass = Class.create(ItemsView.Base, {
    getItems: arg.getItems,
    dispatchForItems: arg.dispatchForItems,
    getDispatchResponseData: arg.getDispatchResponseData, 
    getItemsNames: arg.getItemsNames,
    createRow: arg.createRow, 
    dispatchUpdate: arg.dispatchUpdate, 
    dispatchInstall: arg.dispatchInstall,
    dispatchCheck: arg.dispatchCheck,
    showTooltip: arg.showTooltip, 
    createUpdateLink: arg.createUpdateLink, 
    createInstallLink: arg.createInstallLink, 
    createExtendedElements: arg.createExtendedElements, 
    setAdditionalItemAttributes: arg.setAdditionalItemAttributes
  });
  
  Object.extend(classMethods, arg);  
  Object.extend(klass, classMethods);
  
  klass.isItemsView = true;
  
  return klass;
};