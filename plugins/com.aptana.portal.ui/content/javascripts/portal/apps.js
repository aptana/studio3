Apps = ItemsView.create({
  /**
   * Returns an application list along with their command name, min-version and URL for installation
   */
  getItems: function() {
    // A list of configuration items (hash of hashes) that we display and query for.
    // The outer hash provides a filter for OS type ('windows', 'macosx', 'linux').
    // Every inner hash has a display name and an array that contains a command name, min-version, URL for installation.
    var allOSItems = $H({
      'windows' : $H({
        'Ruby': ['ruby', '1.8.7', 'http://rubyinstaller.org/'],
        'Git': ['git', '1.7.0', 'http://code.google.com/p/msysgit/']
      }), 
      'macosx' : $H({
        'Ruby': ['ruby', '1.8.7', 'http://rubyinstaller.org/'],
        'Git': ['git', '1.7.0', 'http://code.google.com/p/git-osx-installer/downloads/list?can=3']
      }), 
      'linux' : $H({
        'Ruby': ['ruby', '1.8.7', 'http://rubyinstaller.org/'],
        'Git': ['git', '1.7.0', 'http://git-scm.com/download']
      })
    });
    return allOSItems.get(getOS())
  }, 
  
  getItemsNames: function() {
    return this.items.keys();
  }, 
  
  dispatchForItems: function() {
    return dispatch($H({controller:"portal.system.versions", action:"getInstalledVersions"}).toJSON());
  }, 
  
  getDispatchResponseData: function(dispatchResponse) {
    return $H(dispatchResponse.data.configurations);
  }, 
  
  createRow: function(item, itemName, itemRowElements) {
    var itemRow;
    var configItemSpan;
    var actionsSpan;
    with (Elements.Builder) {
      itemRow = tr(
        td(
          configItemSpan = span({'class' : 'unknown ' + item[0], 'name' : itemName, 'minVersion' : item[1]}, itemName),
          actionsSpan = span()
        )
      );
      itemRowElements.set('itemSpan', configItemSpan);
      itemRowElements.set('actionsSpan', actionsSpan);
    }
    return itemRow;
  }, 
  
  dispatchUpdate: function(event) {
    this.dispatchInstall(event);
  }, 
  
  dispatchInstall: function(event) {
    var siteURL = event.target.getAttribute('install-site') || "";
    // Right now, we just redirect to the installation site
    // window.location = siteURL;
    window.open(siteURL);
    event.stop();
    return true;
  }, 
  
  showTooltip: function(event) {
    var name = event.target.getAttribute('name');
    var minVersion = event.target.getAttribute('minVersion');
    var installedVersion = event.target.getAttribute('installedVersion');
    var rawVersionOutput = event.target.getAttribute('rawVersionOutput');
    var status = event.target.getAttribute('itemStatus');
    var ttHtml = '<div><b>' + name + '</b></div>';
    if (status) {
      switch (status) {
        case 'install':
          ttHtml += '<div><b>Version:</b> ' + minVersion + '</div><div><b>Status:</b> This application is not installed</div>';
        break;
        case 'ok':
          ttHtml += '<div><b>Version:</b> ' + rawVersionOutput + '</div><div><b>Status:</b> This application is installed</div>';
        break;
        case 'update':
          ttHtml += '<div><b>Version:</b> ' + installedVersion + '</div><div><b>Status:</b> This application is installed but needs to be updated to version '+minVersion+'</div>';
        break;
      }
    } else {
      // Display a note that a check is needed.
      ttHtml += "<div>Click check to resolve the installed version</div>";
    }
    tooltip.show(ttHtml);
  },
  
  createUpdateLink: function(item) {
    var iLink;
    with (Elements.Builder) {
      iLink = a({'href' : '#', 'install-site' : item[2]}, 'update');
    }
    return iLink;
  },
  
  createInstallLink: function(item) {
    var iLink;
    with (Elements.Builder) {
      iLink  = a({'href' : '#', 'install-site' : item[2]}, 'install');
    }
    return iLink;
  },
  
  createExtendedElements: function() {
    var links;
    var checkLink;
    with (Elements.Builder) {
      links = div(
        div(checkLink = a({'href':'#'}, "Check versions"))
      );
    }
    checkLink.observe('click', this.dispatchCheck.bind(this));
    return links;
  }, 
  
  /**
   * Dispatch an plugin check request
   */
  dispatchCheck: function(event) {
    if (typeof(dispatch) !== 'undefined') {
      dispatch($H({controller:"portal.system.versions", action:"computeInstalledVersions", args : this.items.values().toJSON()}).toJSON());
      if (event) {
        // Stop the event, otherwise we loose the eclipse BroswerFunctions!
        event.stop();
      }
    }
  }
});