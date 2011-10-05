// Existing object
var obj = {
  hello: function( name ) {
    alert( "Hello " + name );
  }
},
// Create a Deferred
defer = $.Deferred();

// Set object as a promise. Note that this _should_ be also available as a result
// of jsut passing the object in, i.e. defer.promise( obj );
obj = defer.promise( obj );

obj.|