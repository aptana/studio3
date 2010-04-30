var Files = Class.create({
  render: function(parentElement, data) {
    // Make sure that the Studio dispatch call is available
    if (typeof(dispatch) === 'undefined') {
      parentElement.appendChild(_studioOnlyContent());
      return;
    }
    var recentFiles = dispatch($H({controller:"portal.recentFiles", action:"getRecentFiles"}).toJSON());
    if (recentFiles) {
      var filesJSON = recentFiles.evalJSON();
      if (_isErrorStatus(filesJSON)) {
        parentElement.appendChild(_errorStatus(filesJSON));
          return;
      }
      var fileNames = $H(filesJSON).keys();
      var filePaths = $H(filesJSON).values();
      var filesCount = fileNames.size();
      var items;
      with(Elements.Builder) {
        // We have to wrap the rows in a tbody, otherwise, the internal browser
        // fails to display the rows.
        var tbodyItem;
        items = table({'id' : 'recentFilesTable'}, tbodyItem = tbody());
        for( var i = 0; i < filesCount; i++ ) {
          var openFileLink;
          var itemRow = tr(
            td(
            openFileLink = a({'href' : '#', 'file' : filePaths[i]}, fileNames[i])
          )
          );
          // make a call to open the file in the editor
          openFileLink.observe('click', function(event) {
              var fileToOpen = event.element().getAttribute('file');
            // make sure to nest the fileToOpen string as a JSON.
            // Another option is to pass it in an array just like that:
            // {controller:"portal.recentFiles", action:"openRecentFiles", args:[fileToOpen].toJSON()}
            dispatch($H({controller:"portal.recentFiles", action:"openRecentFiles", args:fileToOpen.toJSON()}).toJSON());
            // Stop the event, otherwise we loose the eclipse BroswerFunctions!
            event.stop();
              });
          tbodyItem.appendChild(itemRow);
        }
      }
      var oldTable = $('recentFilesTable');
      if (oldTable) {
        parentElement.replaceChild(items, oldTable);
      } else {
        parentElement.appendChild(items);
      }
    } else {
      _clearDescendants(parentElement);
      parentElement.appendChild(Elements.Builder.div('No Recent Files'));
    }
  }
});