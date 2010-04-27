/**
 * This file contains the observer-observable pattern that we'll use
 * to update the portal on every eclipse-initiated notification.
 */

/**
 * Observable definition.
 * The observable holds a hash od event IDs to functions lists. 
 * The notify should get an event JSON and call each of the registered 
 * functions for that 'event'.
 */
var Observable = Class.create({
    initialize: function() {
        this.observers = new Hash();
    },
    notify: function(eventJSON) {
        var e = eventJSON.evalJSON();
        var listeners = this.observers.get(e.event);
        if (listeners) {
            var m_count = listeners.count();
            for (var i = 0; i < m_count; i++) {
                listeners.getAt(i).call(this, e);
            }
        }
    },
    addObserver: function(eventID, func) {
        if (!eventID || !func)
        throw 'Argument error. eventId or func is null!';
        var listeners = this.observers.get(eventID);
        if (!listeners) {
            listeners = new ArrayList();
        }
        listeners.add(func);
        this.observers.set(eventID, listeners);
    },
    removeObserver: function(eventID, func) {
        if (!eventID || !func)
        throw 'Argument error. eventId or func is null!';
        var listeners = this.observers.get(eventID);
        if (listeners) {
            listeners.removeAt(listeners.indexOf(func, 0));
        }
    }
});

// Creates the eventsDispatcher which contains the notify() function.
var eventsDispatcher = new Observable();

/**
 * Simulate inheritance
 * Example:
 *   To make anElement inherits from Observer, use:
 *   inherits(new Observer(), anElement);
 * (You can also use Prototype syntax)
 */
function inherits(base, extension)
 {
    for (var property in base)
    {
        try
        {
            extension[property] = base[property];
        }
        catch(warning) {}
    }
}

/**
 * Observer definition.
 * See inherit when if you want to set an element as an Observer.
 */
function Observer()
 {
    this.update = function()
    {
        return;
    }
}
