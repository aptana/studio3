module Coercible
  class Coercer

    # Coerce true values
    class TrueClass < Object
      primitive ::TrueClass

      # Coerce given value to String
      #
      # @example
      #   coercer[TrueClass].to_string(true)  # => "true"
      #
      # @param [TrueClass] value
      #
      # @return [String]
      #
      # @api public
      def to_string(value)
        value.to_s
      end

    end # class TrueClass

  end # class Coercer
end # module Coercible
