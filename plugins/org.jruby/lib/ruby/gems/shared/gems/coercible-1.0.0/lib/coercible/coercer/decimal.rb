module Coercible
  class Coercer

    # Coerce BigDecimal values
    class Decimal < Numeric
      primitive ::BigDecimal

      FLOAT_FORMAT = 'F'.freeze

      # Coerce given value to String
      #
      # @example
      #   coercer[BigDecimal].to_string(BigDecimal('1.0')) # => "1.0"
      #
      # @param [BigDecimal] value
      #
      # @return [String]
      #
      # @api public
      def to_string(value)
        value.to_s(FLOAT_FORMAT)
      end

      # Passthrough the value
      #
      # @example
      #   Coercible::Coercion::BigDecimal.to_decimal(BigDecimal('1.0')) # => BigDecimal('1.0')
      #
      # @param [BigDecimal] value
      #
      # @return [Fixnum]
      #
      # @api public
      def to_decimal(value)
        value
      end

    end # class BigDecimal

  end # class Coercer
end # module Coercible
