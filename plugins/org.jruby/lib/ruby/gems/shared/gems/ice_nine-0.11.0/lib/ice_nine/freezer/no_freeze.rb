# encoding: utf-8

module IceNine
  class Freezer

    # A freezer class that does not freeze anything
    class NoFreeze < self

      # Pass through the object without freezing it
      #
      # @example
      #   object = IceNine::Freezer::NoFreeze.deep_freeze(object)
      #   object.frozen?  # => false
      #
      # @param [Object] object
      # @param [RecursionGuard] _recursion_guard
      #
      # @return [Object]
      def self.guarded_deep_freeze(object, _recursion_guard)
        object
      end

    end # NoFreeze
  end # Freezer
end # IceNine
