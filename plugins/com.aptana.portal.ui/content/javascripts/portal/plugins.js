Plugins = ItemsView.create({
  /**
   * Returns a plugins list along with their id's, min-version, update-site and feature-id.
   */
  getItems: function() {
    return $H({'Aptana Git': ['com.aptana.git.core', '2.0.0', 'http://download.aptana.org/tools/studio/plugin/install/studio', 'com.aptana.ide.feature.studio'],
               'Aptana JavaScript': ['com.aptana.editor.js', '1.0.0', 'http://download.aptana.org/tools/studio/plugin/install/studio', 'com.aptana.ide.feature.studio'], 
               'Eclipse PHP Developer Tools' : ['org.eclipse.php', '2.0.0', 'http://download.eclipse.org/tools/pdt/updates/2.0/', 'org.eclipse.php']
            });
  }, 
  
  getItemsNames: function() {
    return this.items.keys();
  }, 
  
  dispatchForItems: function() {
    return dispatch($H({controller:"portal.plugins", action:"getInstalledPlugins"}).toJSON());
  }, 
  
  getDispatchResponseData: function(dispatchResponse) {
    return $H(dispatchResponse.data.plugins);
  }, 
  
  createRow: function(item, itemName, itemRowElements) {
    var itemRow;
    var pluginSpan;
    var actionsSpan;
    with (Elements.Builder) {
      itemRow = tr(
        td(
          pluginSpan = span({'class' : 'unknown ' + item[0], 'pluginId' : item[0], 'minVersion' : item[1]}, itemName), 
          actionsSpan = span()
        )
      );
      itemRowElements.set('itemSpan', pluginSpan);
      itemRowElements.set('actionsSpan', actionsSpan);
    }
    return itemRow;
  }, 
  
  dispatchUpdate: function(event) {
    this.dispatchInstall(event);
  }, 
  
  dispatchInstall: function(event) {
    var siteURL = event.target.getAttribute('install-site') || "";
    var featureId = event.target.getAttribute('feature-id') || "";
    // We pass the plugins list to this function as well. The function will re-compute the installed plugins right after the 
    // 'New Software' dialog is closed. 
    // The first arg param is the update-site URL.
    // The second arg param is the feature-id that we want to install.
    // The third arg param is the postVerificationPlugins JSON
    dispatch($H({controller:"portal.plugins", action:"openPluginsDialog", args : [siteURL, featureId, this.items.values()].toJSON()}).toJSON());
    event.stop();
    return true;
  }, 
  
  showTooltip: function(event) {
    var pid = event.target.getAttribute('pluginId');
    var pMinVersion = event.target.getAttribute('minVersion');
    var pInstalledVersion = event.target.getAttribute('installedVersion');
    var pStatus = event.target.getAttribute('itemStatus');
    var ttHtml = '<div><b>Plugin-ID:</b> ' + pid + '</div>';
    if (pStatus) {
      switch (pStatus) {
        case 'install':
          ttHtml += '<div><b>Version:</b> ' + pMinVersion + '</div><div><b>Status:</b> This plugins is not installed</div>';
        break;
        case 'ok':
          ttHtml += '<div><b>Version:</b> ' + pInstalledVersion + '</div><div><b>Status:</b> This plugins is installed</div>';
        break;
        case 'update':
          ttHtml += '<div><b>Version:</b> ' + pInstalledVersion + '</div><div><b>Status:</b> This plugins is installed but needs to be updated to version '+pMinVersion+'</div>';
        break;
      }
    }
    tooltip.show(ttHtml);
  },

  createUpdateLink: function(item) {
    var iLink;
    with (Elements.Builder) {
      iLink = a({'href' : '#', 'install-site' : item[2], 'feature-id' : item[3]}, 'update');
    }
    return iLink;
  },

  createInstallLink: function(item) {
    var iLink;
    with (Elements.Builder) {
      iLink  = a({'href' : '#', 'install-site' : item[2], 'feature-id' : item[3]}, 'install');
    }
    return iLink;
  },
  
  createExtendedElements: function() {
    var links;
    var installLink; 
    var checkLink;
    with (Elements.Builder) {
      links = div(
        div(installLink = a({'href':'#'}, "Install a new plug-in")),
        div(checkLink = a({'href':'#'}, "Check for installed plug-ins"))
      );
    }
    installLink.observe('click', this.dispatchInstall.bind(this));
    checkLink.observe('click', this.dispatchCheck.bind(this));
    return links;
  }, 

  /**
   * Dispatch an plugin check request
   */
  dispatchCheck: function(event) {
    if (typeof(dispatch) !== 'undefined') {
      dispatch($H({controller:"portal.plugins", action:"computeInstalledPlugins", args : this.items.values().toJSON()}).toJSON());
      if (event) {
        // Stop the event, otherwise we loose the eclipse BroswerFunctions!
        event.stop();
      }
    }
  }
});