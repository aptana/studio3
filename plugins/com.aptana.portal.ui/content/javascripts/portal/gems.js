AllGems = Class.create({
  initialize: function() {
      // TODO - Provide a list of gems we want the user to have.
      // Every item has a display name and an array that contains: gem name to use in gem install, min version, optional gem location url.
      this.gemList = $H({
          'Rails': ['rails', '2.3.5', ''],
          'Ruby Debug': ['ruby-debug-ide', '0.4.9', ''], 
          'SQLite3' : ['sqlite3-ruby ', '1.2.5', '']
      });
    },
    
    /**
     * Get the installed gems from the Studio and render them
     * @param parentElement
     * @param data - A JSON object, or null
     */
    render: function(parentElement, data) {
      if (typeof(dispatch) === 'undefined') {
        parentElement.appendChild(_studioOnlyContent());
        return;
      }
      // Make sure we have the data as JSON
      var gems = data;
      if (!gems) {
        gems = dispatch($H({controller:"portal.gems", action:"getInstalledGems"}).toJSON());
        if (gems) {
            gems = gems.evalJSON();
        }
      }
      if (gems) {
        if (_isErrorStatus(gems)) {
          _clearDescendants(parentElement);
          parentElement.appendChild(_errorStatus(gems));
            return;
        }
        var gemsContent;
        var status = gems.data.status;
        with(Elements.Builder) {
          // Prepare some common elements
          var checkLink;
          var tbodyItem;
          var checkDiv = div({'class' : 'check'}, checkLink = a({'href' : '#'}, 'Check for Gems'));
          gemsContent = div({'class':'test'}, table({'id' : 'installedGems'}, tbodyItem = tbody()));
          // Do an actual check for the returned JSON 
          if (status == ConfigurationStatus.UNKNOWN) {
            // Display a link to check for the installed gems
            gemsContent = checkDiv;
          } else if (status == ConfigurationStatus.PROCESSING) {
              gemsContent = div({'class' : 'processing'}, 'Checking for Gems...');
          } else if (status == ConfigurationStatus.ERROR) {
            gemsContent = div({'class' : 'gemsError'}, 'Error while checking for gems', checkDiv);
          } else {
            // Read the result from the given JSON and list the gems in a table
            if (gems.data && gems.data.gems) {
              var gemNames = gems.data.gems.split(';');
              var gemsCount = gemNames.size();
              for( var i = 0; i < gemsCount; i++ ) {
                var itemRow = tr(
                  td({'class' : 'gem'},
                    gemNames[i]
                  )
                );
                tbodyItem.appendChild(itemRow);
              }
            } else {
              gemsContent.appendChild(div({'class' : 'no-gems'}, 'No gems were found'));
            }
            gemsContent.appendChild(checkDiv);
          }
          // Dispatch a check for gems when the user clicks the link
          checkLink.observe('click', this.dispatchCheck.bind(this));
        }
        // Clear anything that we had under this parent element before and put there the new elements
        _clearDescendants(parentElement);
        parentElement.appendChild(gemsContent);
      }
    }, 
    
    /**
     * Dispatch a request to check for gems
     */
    dispatchCheck: function(event) {
      if (typeof(dispatch) !== 'undefined') {
        dispatch($H({controller:"portal.gems", action:"computeInstalledGems"}).toJSON());
        if (event) {
          // Stop the event, otherwise we loose the eclipse BroswerFunctions!
          event.stop();
        }
      }
    }
});