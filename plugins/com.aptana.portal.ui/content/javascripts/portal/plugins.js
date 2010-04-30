var Plugins = Class.create({
    initialize: function() {
      // A list of plugins that we display and query for.
      // Every item has a display name and an array that contains: plug-in id for existance check, min version, update site url, feature id on the update site.
      this.pluginList = $H({'Aptana Git': ['com.aptana.git.core', '2.0.0', 'http://download.aptana.org/tools/studio/plugin/install/studiso', 'com.aptana.ide.feature.studio'],
          'Aptana JavaScript': ['com.aptana.editor.js', '1.0.0', 'http://download.aptana.org/tools/studio/plugin/install/studio', 'com.aptana.ide.feature.studio'], 
          'Eclipse PHP Developer Tools' : ['org.eclipse.php', '2.0.0', 'http://download.eclipse.org/tools/pdt/updates/2.0/', 'org.eclipse.php']
      });
    },

    /**
     * Render the plugins section
     */
    render: function(parentElement, data) {
      if (typeof(dispatch) === 'undefined') {
        parentElement.appendChild(_studioOnlyContent());
        return;
      }
      // We ask for a list of all the installed plugin. This list will then be checked for some specific IDs to verify 
      // if the Studio has a specific plugin.
      var allPlugins = dispatch($H({controller:"portal.plugins", action:"getInstalledPlugins"}).toJSON());
      allPlugins = allPlugins.evalJSON();
      var status = allPlugins.data.status;
      allPlugins = $H(allPlugins.data.plugins);
      var items;
      var links;
      var installLink;
      var checkLink;
      with(Elements.Builder) {
        // We have to wrap the rows in a tbody, otherwise, the internal browser
        // fails to display the rows.
        var tbodyItem;
        items = table({'id' : 'plugins'}, tbodyItem = tbody());
        var pluginNames = this.pluginList.keys();
        var items_count = pluginNames.size();
        var errorShown = false;
        var pluginSpan;
        var actionsSpan;
        for( var i = 0; i < items_count; i++ ) {
          var plugin = this.pluginList.get(pluginNames[i]);
          var itemRow = tr(
            td(
              pluginSpan = span({'class' : 'unknown ' + plugin[0], 'pluginId' : plugin[0], 'minVersion' : plugin[1]}, pluginNames[i]), 
              actionsSpan = span()
            )
          );
          // Check the status of the plugin. In case needed, disply a link to install/update the plugin.
          switch(status) {
            case ConfigurationStatus.PROCESSING:
              pluginSpan.setAttribute('className', 'processing');
            break;
            case ConfigurationStatus.ERROR:
              if (!errorShown) {
                // Show this error with a timeout to simulate an asynchronous call.
                // We need this to give the page a chance to get the OK notification 
                // after the initial error notification.
                window.setTimeout(function() { 
                  showError("Plugin Installation Problem", "There was an problem updating / installing this plug-in. You can find more information about this problem at the Aptana Studio error log."); 
                  }, 1000);
                errorShown = true;
              }
              // Do not break this case!
            case ConfigurationStatus.OK:
              // In this case, we need to check whether this plugin needs an update/install
              var pluginInfo = allPlugins.get(plugin[0]);
              // In case we never dispatched the check, the pluginInfo will be undefined at this point.
              if (pluginInfo) {
                if (pluginInfo.exists == "yes") {
                  // We have this plugin installed, and we just need to check it has the right version
                  pluginSpan.setAttribute('installedVersion', pluginInfo.version);
                  if (pluginInfo.compatibility == 'ok') {
                    pluginSpan.setAttribute('className', 'ok');
                    pluginSpan.setAttribute('pluginStatus', 'ok');
                  } else {
                    pluginSpan.setAttribute('className', 'notice');
                    pluginSpan.setAttribute('pluginStatus', 'update');
                    var updateLink;
                    var updateSpan = span({'class' : 'action update'},  updateLink = a({'href' : '#', 'update-site' : plugin[2], 'feature-id' : plugin[3]}, 'update'));
                    actionsSpan.appendChild(updateSpan);
                    updateLink.observe('click', this._dispatchInstallPlugin.bind(this));
                  }
                } else {
                  // We don't have this plugin installed. Display an install link.
                  var installLink;
                  var installSpan = span({'class' : 'action install'},  installLink = a({'href' : '#', 'update-site' : plugin[2], 'feature-id' : plugin[3]}, 'install'));
                  pluginSpan.setAttribute('pluginStatus', 'install');
                  pluginSpan.setAttribute('className', 'missing');
                  actionsSpan.appendChild(installSpan);
                  installLink.observe('click', this._dispatchInstallPlugin.bind(this));
                }
              }
            break;
          }
          tbodyItem.appendChild(itemRow);
          // register the tooltips
          pluginSpan.observe('mouseover', function(e) {
            _showTooltip(e);
            e.stop();
            return true;
          });
          pluginSpan.observe('mouseout', function(e) {
            tooltip.hide();
            return true;
          });
        }
        
        links = div(
          div(installLink = a({'href':'#'}, "Install a new plug-in")),
          div(checkLink = a({'href':'#'}, "Check for installed plug-ins"))
        );
        
        installLink.observe('click', this._dispatchInstallDialog.bind(this));
        checkLink.observe('click', this.dispatchCheck.bind(this));
      }
      _clearDescendants(parentElement);
      parentElement.appendChild(items);
      parentElement.appendChild(links);
    },
    /**
     * Dispatch an install request to open the install dialog
     */
    _dispatchInstallDialog: function(event) {
      // We pass the plugins list to this function as well. The function will re-compute the installed plugins right after the 
      // 'New Software' dialog is closed. The first and second args are the update-site URL and the feature-id, which are not needed in this case.
      dispatch($H({controller:"portal.plugins", action:"openPluginsDialog", args : ["", "", this.pluginList.values()].toJSON()}).toJSON());
      // Stop the event, otherwise we loose the eclipse BroswerFunctions!
      event.stop();
    },

    /**
     * Dispatch an plugin check request
     */
    dispatchCheck: function(event) {
      if (typeof(dispatch) !== 'undefined') {
        dispatch($H({controller:"portal.plugins", action:"computeInstalledPlugins", args : this.pluginList.values().toJSON()}).toJSON());
        if (event) {
          // Stop the event, otherwise we loose the eclipse BroswerFunctions!
          event.stop();
        }
      }
    }, 

    /**
     * Dispatch an event that will trigger the eclipse 'Install New Software' dialog to appear, preloaded with a specific update site.
     * The update site is drawn from the event's target 'update-site' attribute. 
     * In case this.postVerificationPlugins is set, the plugins represented in this JSON will be checked right after the installation is done.
     * @param event The event that triggered this call.
     */
     _dispatchInstallPlugin: function (event) {
       var siteURL = event.target.getAttribute('update-site');
       var featureId = event.target.getAttribute('feature-id');
       if (!siteURL || !featureId) {
         showError("Plugin Installation Problem", "There was a problem retrieving the update site for this plugin. Please try to refresh the page.\nIf that did not resolve the issue, contact Aptana's support.");
         return false;
       }
       // We pass the plugins list to this function as well. The function will re-compute the installed plugins right after the 
       // 'New Software' dialog is closed. 
       // The first arg param is the update-site URL.
       // The second arg param is the feature-id that we want to install.
       // The third arg param is the postVerificationPlugins JSON
       dispatch($H({controller:"portal.plugins", action:"openPluginsDialog", args : [siteURL, featureId, this.pluginList.values()].toJSON()}).toJSON());
       event.stop();
       return true;
     }
});
