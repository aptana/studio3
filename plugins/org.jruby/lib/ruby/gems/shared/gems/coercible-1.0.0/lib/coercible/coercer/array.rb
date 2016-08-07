module Coercible
  class Coercer

    # Coerce Array values
    class Array < Object
      primitive ::Array

      TIME_SEGMENTS = [ :year, :month, :day, :hour, :min, :sec ].freeze

      # Creates a Set instance from an Array
      #
      # @param [Array] value
      #
      # @return [Array]
      #
      # @api private
      def to_set(value)
        value.to_set
      end

    end # class Array

  end # class Coercer
end # module Coercible
