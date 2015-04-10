# encoding: utf-8

module IceNine

  # The default class that handles freezing objects
  class Freezer

    # Cache the Freezer classes returned for each type
    @freezer_cache = Hash.new do |cache, mod|
      cache[mod] = nil
      mod.ancestors.each do |ancestor|
        freezer = find(ancestor.name.to_s)
        break cache[mod] = freezer if freezer
      end
      cache[mod]
    end

    # Look up the Freezer descendant by object type
    #
    # @example
    #   freezer_class = IceNine::Freezer[mod]
    #
    # @param [Module] mod
    #
    # @return [Class<Freezer>]
    #
    # @api public
    def self.[](mod)
      @freezer_cache[mod]
    end

    # Deep freeze an object with a particular Freezer
    #
    # @see IceNine.deep_freeze
    #
    # @param [Object] object
    #
    # @return [Object]
    #
    # @api public
    def self.deep_freeze(object)
      guarded_deep_freeze(object, RecursionGuard::ObjectSet.new)
    end

    # Deep freeze an object with a particular Freezer
    #
    # @see IceNine.deep_freeze!
    #
    # @param [Object] object
    #
    # @return [Object]
    #
    # @api public
    def self.deep_freeze!(object)
      guarded_deep_freeze(object, RecursionGuard::Frozen.new)
    end

    # Find a Freezer descendant by name
    #
    # @param [String] name
    #
    # @return [Class<Freezer>]
    #   returned if a matching freezer is found
    # @return [nil]
    #   returned if no matching freezer is found
    #
    # @api private
    def self.find(name)
      freezer = name.split('::').reduce(self) do |mod, const|
        mod.const_lookup(const) or break mod
      end
      freezer if freezer < self  # only return a descendant freezer
    end

    private_class_method :find

    # Look up a constant in the namespace
    #
    # @param [String] namespace
    #
    # @return [Module]
    #   returned if a matching constant is found
    # @return [nil]
    #   returned if no matching constant is found
    #
    # @api private
    def self.const_lookup(namespace)
      const_get(namespace) if const_defined?(namespace, nil)
    end

    # Deep freeze an object with a particular Freezer and RecursionGuard
    #
    # @param [Object] object
    # @param [RecursionGuard] recursion_guard
    #
    # @return [Object]
    #
    # @api private
    def self.guarded_deep_freeze(object, recursion_guard)
      recursion_guard.guard(object) do
        Freezer[object.class].guarded_deep_freeze(object, recursion_guard)
      end
    end

    class << self
      protected :const_lookup, :guarded_deep_freeze
    end

  end # Freezer
end # IceNine
