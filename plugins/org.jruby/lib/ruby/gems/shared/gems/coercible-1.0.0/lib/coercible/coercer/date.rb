module Coercible
  class Coercer

    # Coerce Date values
    class Date < Object
      include TimeCoercions

      primitive ::Date

      # Passthrough the value
      #
      # @example
      #   coercer[DateTime].to_date(date)  # => Date object
      #
      # @param [DateTime] value
      #
      # @return [Date]
      #
      # @api public
      def to_date(value)
        value
      end

    end # class Date

  end # class Coercer
end # module Coercible
