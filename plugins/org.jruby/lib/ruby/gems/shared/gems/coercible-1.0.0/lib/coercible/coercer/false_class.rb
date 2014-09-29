module Coercible
  class Coercer

    # Coerce false values
    class FalseClass < Object
      primitive ::FalseClass

      # Coerce given value to String
      #
      # @example
      #   coercer[FalseClass].to_string(false)  # => "false"
      #
      # @param [FalseClass] value
      #
      # @return [String]
      #
      # @api public
      def to_string(value)
        value.to_s
      end

    end # class FalseClass

  end # class Coercer
end # module Coercible
