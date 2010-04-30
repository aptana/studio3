var Configurations = Class.create({
    initialize: function() {
      // A list of configuration items (hash of hashes) that we display and query for.
      // The outer hash provides a filter for OS type ('windows', 'macosx', 'linux').
      // Every inner hash has a display name and an array that contains a command name, min-version, URL for installation.
      // The command-name will be translated into a dispatch controller that will be invoked at the right time.
      this.configItems = $H({
        'windows' : $H({
          'Ruby': ['ruby', '1.8.7', 'http://rubyinstaller.org/'],
          'Git': ['git', '1.8.0', 'http://code.google.com/p/msysgit/']
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
    },

    /**
     * Render the configurations section
     */
    render: function(parentElement, data) {
      if (typeof(dispatch) === 'undefined') {
        parentElement.appendChild(_studioOnlyContent());
        return;
      }
      // We ask for a list of all the installed configuration items. This list will then be checked to verify 
      // that the item is installed and has the right version.
      var allConfigurations = dispatch($H({controller:"portal.system.versions", action:"getInstalledVersions"}).toJSON());
      allConfigurations = allConfigurations.evalJSON();
      var status = allConfigurations.data.status;
      allConfigurations = $H(allConfigurations.data.configurations);
      var items;
      var links;
      var installLink;
      var checkLink;
      with(Elements.Builder) {
        // We have to wrap the rows in a tbody, otherwise, the internal browser
        // fails to display the rows.
        var tbodyItem;
        items = table({'class' : 'configurations'}, tbodyItem = tbody());
        // Get the right configurations group by the current OS name.
        var configurables = this.configItems.get(getOS());
        var configurableNames = configurables.keys();
        var items_count = configurableNames.size();
        var errorShown = false;
        var configItemSpan;
        var actionsSpan;
        for( var i = 0; i < items_count; i++ ) {
          var item = configurables.get(configurableNames[i]);
          var itemRow = tr(
            td(
              configItemSpan = span({'class' : 'unknown ' + item[0], 'name' : configurableNames[i], 'minVersion' : item[1]}, configurableNames[i]), 
              actionsSpan = span()
            )
          );
          // Check the status of the config-item. In case needed, disply a link to install/update the item.
          switch(status) {
            case ConfigurationStatus.PROCESSING:
              configItemSpan.setAttribute('className', 'processing');
            break;
            case ConfigurationStatus.ERROR:
              if (!errorShown) {
                // Show this error with a timeout to simulate an asynchronous call.
                // We need this to give the page a chance to get the OK notification 
                // after the initial error notification.
                window.setTimeout(function() { 
                  showError("Configurations Problem", "There was an problem updating the configurations status. More information about this problem can be found at the Aptana Studio error log."); 
                  }, 1000);
                errorShown = true;
              }
              // Do not break this case!
            case ConfigurationStatus.OK:
              // In this case, we need to check whether this item needs an update/install
              var configItemInfo = allConfigurations.get(item[0]);
              // In case we never dispatched the check, the configItemInfo will be undefined at this point.
              if (configItemInfo) {
                if (configItemInfo.exists == "yes") {
                  // We have this item installed, and we just need to check it has the right version
                  configItemSpan.setAttribute('rawVersionOutput', configItemInfo.rawOutput);
                  configItemSpan.setAttribute('installedVersion', configItemInfo.version);
                  if (configItemInfo.compatibility == 'ok') {
                    configItemSpan.setAttribute('className', 'ok');
                    configItemSpan.setAttribute('itemStatus', 'ok');
                  } else {
                    configItemSpan.setAttribute('className', 'notice');
                    configItemSpan.setAttribute('itemStatus', 'update');
                    var updateLink;
                    var updateSpan = span({'class' : 'action update'},  updateLink = a({'href' : '#', 'install-site' : item[2]}, 'update'));
                    actionsSpan.appendChild(updateSpan);
                    updateLink.observe('click', this._dispatchInstall.bind(this));
                  }
                } else {
                  // We don't have this item installed. Display an install link.
                  var installLink;
                  var installSpan = span({'class' : 'action install'},  installLink = a({'href' : '#', 'install-site' : item[2]}, 'install'));
                  configItemSpan.setAttribute('itemStatus', 'install');
                  configItemSpan.setAttribute('className', 'missing');
                  actionsSpan.appendChild(installSpan);
                  installLink.observe('click', this._dispatchInstall.bind(this));
                }
              }
            break;
          }
          tbodyItem.appendChild(itemRow);
          // register the tooltips
          var tt = function(e) {
            this._showTooltip(e);
            e.stop();
            return true;
          }
          configItemSpan.observe('mouseover', tt.bind(this));
          configItemSpan.observe('mouseout', function(e) {
            tooltip.hide();
            return true;
          });
        }
        
        links = div(
          div(checkLink = a({'href':'#'}, "Check versions"))
        );
        
        checkLink.observe('click', this.dispatchCheck.bind(this));
      }
      _clearDescendants(parentElement);
      parentElement.appendChild(items);
      parentElement.appendChild(links);
    },
    
    /**
     * Dispatch a confgurations check request
     */
    dispatchCheck: function(event) {
      if (typeof(dispatch) !== 'undefined') {
        dispatch($H({controller:"portal.system.versions", action:"computeInstalledVersions", args : this.configItems.get(getOS()).values().toJSON()}).toJSON());
        if (event) {
          // Stop the event, otherwise we loose the eclipse BroswerFunctions!
          event.stop();
        }
      }
    }, 

    /**
     * Redirect to an installation site.
     * The site URL is drawn from the event's target 'install-site' attribute. 
     * 
     * @param event The event that triggered this call.
     */
     _dispatchInstall: function (event) {
       var siteURL = event.target.getAttribute('install-site');
       // Right now, we just redirect to the installation site
       window.location = siteURL;
       event.stop();
       return true;
     }, 

    /**
     * Show a tooltip for the config-item details.
     */
    _showTooltip: function(event) {
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
    }
});
