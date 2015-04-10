# encoding: utf-8

module Axiom
  module Types

    # Represents a set type
    class Set < Collection
      primitive       ::Set
      coercion_method :to_set

      # Test if the type is a base type
      #
      # @return [Boolean]
      #
      # @api private
      def self.base?
        equal?(Set)
      end

      private_class_method :base?

    end # class Set
  end # module Types
end # module Axiom
