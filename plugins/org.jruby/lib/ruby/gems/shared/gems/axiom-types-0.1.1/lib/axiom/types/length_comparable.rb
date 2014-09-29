# encoding: utf-8

module Axiom
  module Types

    # Add a minimum and maximum length constraint to a type
    module LengthComparable

      # The range of allowed lengths
      #
      # @return [Range]
      #
      # @api public
      attr_reader :range

      # Hook called when module is extended
      #
      # Add #minimum_length and #maximum_length DSL methods to descendant.
      #
      # @param [Class<Axiom::Types::Type>] descendant
      #
      # @return [undefined]
      #
      # @api private
      def self.extended(descendant)
        super
        descendant.class_eval do
          accept_options :minimum_length, :maximum_length
          minimum_length Infinity.instance
          maximum_length NegativeInfinity.instance
        end
      end

      # Finalize by setting up a length range constraint
      #
      # @return [Axiom::Types::LengthComparable]
      #
      # @api private
      def finalize
        return self if frozen?
        @range = IceNine.deep_freeze(minimum_length..maximum_length)
        use_length_within_range
        super
      end

    private

      # Add a constraint for a length within a range
      #
      # @return [undefined]
      #
      # @todo freeze the minimum_length and maximum_length
      #
      # @api private
      def use_length_within_range
        constraint { |object| range.cover?(object.length) }
      end

    end # module LengthComparable
  end # module Types
end # module Axiom
