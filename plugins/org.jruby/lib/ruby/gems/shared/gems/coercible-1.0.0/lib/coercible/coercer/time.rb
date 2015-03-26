module Coercible
  class Coercer

    # Coerce Time values
    class Time < Object
      include TimeCoercions

      primitive ::Time

      # Passthrough the value
      #
      # @example
      #   coercer[DateTime].to_time(time)  # => Time object
      #
      # @param [DateTime] value
      #
      # @return [Date]
      #
      # @api public
      def to_time(value)
        value
      end

      # Creates a Fixnum instance from a Time object
      #
      # @example
      #   Coercible::Coercion::Time.to_integer(time)  # => Fixnum object
      #
      # @param [Time] value
      #
      # @return [Fixnum]
      #
      # @api public
      def to_integer(value)
        value.to_i
      end

    end # class Time

  end # class Coercer
end # module Coercible
