# encoding: utf-8

module IceNine

  # Protect against infinite recursion
  #
  # @private
  class RecursionGuard

    # Protects against infinite recursion by never yielding with the same
    # object more than once.
    class ObjectSet < self

      # Initialize a recursion guard
      #
      # @return [undefined]
      def initialize
        @object_ids = {}
      end

      # Guard against recursively calling a block with the same object
      #
      # @example
      #   recursion_guard = IceNine::RecursionGuard::ObjectSet.new
      #   recursion_guard.guard(object) do
      #     logic_which_may_be_recursively_called_with_object(recursion_guard)
      #   end
      #
      # @param [Object] object
      #
      # @return [Object]
      def guard(object)
        caller_object_id = object.__id__
        return object if @object_ids.key?(caller_object_id)
        @object_ids[caller_object_id] = nil
        yield
      end

    end # ObjectSet

    # Protects against infinite recursion by not yielding with frozen objects
    class Frozen < self

      # Guard against recursively calling a block with the same frozen object
      #
      # @param [Object] object
      #
      # @return [Object]
      def guard(object)
        return object if object.frozen?
        yield
      end

    end # Frozen

  end # RecursionGuard
end # IceNine
