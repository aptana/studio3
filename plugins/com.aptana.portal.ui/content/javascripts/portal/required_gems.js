RequiredGems = ItemsView.create({
  /**
   * Returns a plugins list along with their id's, min-version, update-site and feature-id.
   */
  getItems: function() {
    // TODO - We might need to be OS specific here (as we did with the Apps)
    return $H({'Rails': ['rails', '2.3.5', ''],
               'Ruby Debug': ['ruby-debug-ide', '0.4.9', ''], 
               'SQLite3' : ['sqlite3-ruby', '1.2.5', '']
             });
  }, 
  
  getItemsNames: function() {
    return this.items.keys();
  }, 
  
  dispatchForItems: function() {
    return dispatch($H({controller:"portal.gems", action:"getInstalledGems"}).toJSON());
  }, 
  
  getDispatchResponseData: function(dispatchResponse) {
    return $H(dispatchResponse.data.gems);
  }, 
  
  createRow: function(item, itemName, itemRowElements) {
    // In case the item's name is not in our item's list, regect this row.
    if (!this.items.get(itemName)) {
      return false;
    }
    var itemRow;
    var gemSpan;
    var actionsSpan;
    with (Elements.Builder) {
      itemRow = tr(
        td(
          gemSpan = span({'class' : 'unknown ' + itemName, 'gemName' : itemName, 'minVersion' : item[1]}, itemName), 
          actionsSpan = span()
        )
      );
      itemRowElements.set('itemSpan', gemSpan);
      itemRowElements.set('actionsSpan', actionsSpan);
    }
    return itemRow;
  }, 
  
  dispatchUpdate: function(event) {
    this.dispatchInstall(event);
  }, 
  
  dispatchInstall: function(event) {
    var siteURL = event.target.getAttribute('install-site') || "";
    // TODO - Dispatch an install gem
    // For now, we open a light-box
    var message;
    with (Elements.Builder) {
      message = div(
        div("To install this gem, please open a shell and type in:"), 
        div({'class' : 'gem-command'}, "gem install " + event.target.getAttribute("gemName")), 
        div("Make sure that the installed version is at least " + event.target.getAttribute("gemMinVersion")));
    }
    DialogHelper.createInfoBox("Gem Install", message, "back to portal", true);
    event.stop();
    return true;
  }, 
  
  showTooltip: function(event) {
    var gName = event.target.getAttribute('gemName');
    var gMinVersion = event.target.getAttribute('minVersion');
    var gInstalledVersion = event.target.getAttribute('installedVersion');
    var rawVersionOutput = event.target.getAttribute('rawVersionOutput');
    var gStatus = event.target.getAttribute('itemStatus');
    var ttHtml = '<div><b>Gem:</b> ' + gName + '</div>';
    if (gStatus) {
      switch (gStatus) {
        case 'install':
          ttHtml += '<div><b>Version:</b> ' + gMinVersion + '</div><div><b>Status:</b> This gem is not installed</div>';
        break;
        case 'ok':
          ttHtml += '<div><b>Version:</b> ' + rawVersionOutput + '</div><div><b>Status:</b> This gem is installed</div>';
        break;
        case 'update':
          ttHtml += '<div><b>Version:</b> ' + gInstalledVersion + '</div><div><b>Status:</b> This gem is installed but needs to be updated to version '+gMinVersion+'</div>';
        break;
      }
    }
    tooltip.show(ttHtml);
  },

  createUpdateLink: function(item) {
    var iLink;
    with (Elements.Builder) {
      iLink = a({'href' : '#', 'gemName' : item[0], 'gemMinVersion' : item[1], 'install-site' : item[2]}, 'update');
    }
    return iLink;
  },

  createInstallLink: function(item) {
    var iLink;
    with (Elements.Builder) {
      iLink  = a({'href' : '#', 'gemName' : item[0], 'gemMinVersion' : item[1], 'install-site' : item[2]}, 'install');
    }
    return iLink;
  },
  
  createExtendedElements: function() {
    var links;
    var checkLink;
    var showAllGemsLink;
    with (Elements.Builder) {
      links = div(
        div(checkLink = a({'href':'#'}, "Check for installed gems")), 
        div(showAllGemsLink = a({'href':'#'}, "Show all gems"))
      );
    }
    checkLink.observe('click', this.dispatchCheck.bind(this));
    showAllGemsLink.observe('click', function(event) {
      var allGems = dispatch($H({controller:"portal.gems", action:"computeAllGems"}).toJSON());
      allGems = allGems.evalJSON();
      allGems = $H(allGems.gems).values();
      //alert($H(allGems[0]).inspect());
      var gemsCount = allGems.size();
      var content;
      with (Elements.Builder) {
        content = div({'id' : 'all-gems'});
        for (var i = 0; i < gemsCount; i++ ) {
          content.appendChild(div({'class' : 'gem'}, allGems[i].rawOutput));
        }
      }
      DialogHelper.createInfoBox("Gems", content, "back to portal", true);
      event.stop();
    });
    return links;
  }, 

  /**
   * Dispatch an gems check request
   */
  dispatchCheck: function(event) {
    if (typeof(dispatch) !== 'undefined') {
      dispatch($H({controller:"portal.gems", action:"computeInstalledGems", args : this.items.values().toJSON()}).toJSON());
      if (event) {
        // Stop the event, otherwise we loose the eclipse BroswerFunctions!
        event.stop();
      }
    }
  }
});
