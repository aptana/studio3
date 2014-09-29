module Coercible
  class Coercer

    # Coerce Symbol values
    class Symbol < Object
      primitive ::Symbol

      # Coerce given value to String
      #
      # @example
      #   coercer[Symbol].to_string(:name)  # => "name"
      #
      # @param [Symbol] value
      #
      # @return [String]
      #
      # @api public
      def to_string(value)
        value.to_s
      end

    end # class Symbol

  end # class Coercer
end # module Coercible
