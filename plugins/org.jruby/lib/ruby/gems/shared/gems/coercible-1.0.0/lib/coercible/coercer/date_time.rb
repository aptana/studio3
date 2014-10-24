module Coercible
  class Coercer

    # Coerce DateTime values
    class DateTime < Object
      primitive ::DateTime

      include TimeCoercions

      # Passthrough the value
      #
      # @example
      #   coercer[DateTime].to_datetime(datetime)  # => DateTime object
      #
      # @param [DateTime] value
      #
      # @return [Date]
      #
      # @api public
      def to_datetime(value)
        value
      end

    end # class DateTime

  end # class Coercer
end # module Coercible
