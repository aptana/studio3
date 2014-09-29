# encoding: utf-8

module IceNine
  class Freezer

    # A freezer class for handling Object instances
    class Object < self

      # Deep Freeze an object
      #
      # @example
      #   object = IceNine.deep_freeze(Object.new)
      #
      # @param [Object] object
      # @param [RecursionGuard] recursion_guard
      #
      # @return [Object]
      def self.guarded_deep_freeze(object, recursion_guard)
        object.freeze
        freeze_instance_variables(object, recursion_guard)
        object
      end

      # Handle freezing the object's instance variables
      #
      # @param [Object] object
      # @param [RecursionGuard] recursion_guard
      #
      # @return [undefined]
      #
      # @api private
      def self.freeze_instance_variables(object, recursion_guard)
        object.instance_variables.each do |ivar_name|
          Freezer.guarded_deep_freeze(
            object.instance_variable_get(ivar_name),
            recursion_guard
          )
        end
      end

      private_class_method :freeze_instance_variables

    end # Object

    BasicObject = Object
  end # Freezer
end # IceNine
