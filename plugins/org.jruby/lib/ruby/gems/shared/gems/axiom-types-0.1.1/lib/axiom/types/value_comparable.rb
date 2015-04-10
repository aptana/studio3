# encoding: utf-8

module Axiom
  module Types

    # Add a minimum and maximum value constraint to a type
    module ValueComparable

      # The range of allowed values
      #
      # @return [Range]
      #
      # @api public
      attr_reader :range

      # Hook called when module is extended
      #
      # Add #minimum and #maximum DSL methods to descendant.
      #
      # @param [Class<Axiom::Types::Type>] descendant
      #
      # @return [undefined]
      #
      # @api private
      def self.extended(descendant)
        super
        descendant.accept_options :minimum, :maximum
      end

      # Finalize by setting up a value range constraint
      #
      # @return [Axiom::Types::ValueComparable]
      #
      # @api private
      def finalize
        return self if frozen?
        @range = IceNine.deep_freeze(minimum..maximum)
        use_value_within_range
        super
      end

    private

      # Add a constraint for a value within a range
      #
      # @return [undefined]
      #
      # @todo freeze the minimum and maximum
      #
      # @api private
      def use_value_within_range
        constraint(range.method(:cover?))
      end

    end # module ValueComparable
  end # module Types
end # module Axiom
