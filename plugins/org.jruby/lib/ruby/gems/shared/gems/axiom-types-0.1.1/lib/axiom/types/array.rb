# encoding: utf-8

module Axiom
  module Types

    # Represents an array type
    class Array < Collection
      primitive       ::Array
      coercion_method :to_array

      # Test if the type is a base type
      #
      # @return [Boolean]
      #
      # @api private
      def self.base?
        equal?(Array)
      end

      private_class_method :base?

    end # class Array
  end # module Types
end # module Axiom
