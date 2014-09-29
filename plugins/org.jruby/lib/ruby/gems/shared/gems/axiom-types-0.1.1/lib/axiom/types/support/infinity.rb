# encoding: utf-8

module Axiom
  module Types

    # Represent an infinite number
    class Infinity
      extend Options, DescendantsTracker
      include ::Comparable, ::Singleton

      accept_options :inverse, :number

      number ::Float::INFINITY

      # Test the number against infinity
      #
      # @param [Numeric, Infinity] other
      #
      # @return [0]
      #   returned if the other object is infinity
      # @return [1]
      #   returned if the other object is something other than infinity
      #
      # @api private
      def <=>(other)
        klass = self.class
        case other
        when BigDecimal                      then 1
        when ->(arg) { arg == klass.number } then 0
        when ::Numeric, klass.inverse        then 1
        end
      end

      # Coerce a number into an Infinity instance for comparison
      #
      # @param [::Numeric] other
      #
      # @return [Array(Infinity, Infinity)]
      #
      # @api private
      def coerce(other)
        case other
        when BigDecimal        then [inverse, self]
        when self.class.number then [self,    self]
        when ::Numeric         then [inverse, self]
        else
          fail TypeError, "#{other.class} cannot be coerced"
        end
      end

      # Return the next successive object, which is always self
      #
      # @return [Infinity]
      #
      # @api private
      def succ
        self
      end

    private

      # The inverse instance
      #
      # @return [Infinity]
      #
      # @api private
      def inverse
        self.class.inverse.instance
      end

    end # class Infinity

    # Represent a negative infinite number
    class NegativeInfinity < Infinity
      number(-::Float::INFINITY)

      # Test the number against negative infinity
      #
      # @param [Numeric, Infinity] _other
      #
      # @return [0]
      #   returned if the other object is negative infinity
      # @return [-1]
      #   returned if the other object is not negative infinity
      #
      # @api private
      def <=>(_other)
        comparison = super
        -comparison if comparison
      end

    end # class NegativeInfinity

    # Define inverse classes
    Infinity.inverse NegativeInfinity
    NegativeInfinity.inverse Infinity

  end # module Types
end # module Axiom
