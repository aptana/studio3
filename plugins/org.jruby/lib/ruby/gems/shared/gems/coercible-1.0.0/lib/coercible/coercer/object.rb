module Coercible
  class Coercer

    # Coerce Object values
    class Object
      extend TypeLookup, Options

      accept_options :primitive

      primitive ::Object

      COERCION_METHOD_REGEXP = /\Ato_/.freeze

      # Return coercers object
      #
      # @return [Coercer]
      #
      # @api private
      attr_reader :coercers

      # Initialize a new coercer instance
      #
      # @param [Coercer] coercers
      #
      # @return [undefined]
      #
      # @api private
      def initialize(coercers = Coercer.new)
        @coercers = coercers
      end

      # Inspect the coercer object
      #
      # @example 
      #   coercer[Object].inspect # => "<Coercer::Object primitive=Object>"
      #
      # @return [String]
      #
      # @api public
      def inspect
        "#<#{self.class} primitive=#{self.class.primitive}>"
      end

      # Create an Array from any Object
      #
      # @example with an object that does not respond to #to_a or #to_ary
      #   coercer[Object].to_array(value)         # => [ value ]
      #
      # @example with an object that responds to #to_a
      #   coercer[Object].to_array(Set[ value ])  # => [ value ]
      #
      # @example with n object that responds to #to_ary
      #   coercer[Object].to_array([ value ])     # => [ value ]
      #
      # @param [#to_a,#to_ary,Object] value
      # @param [#to_a,#to_ary,Object] value
      #
      # @return [Array]
      #
      # @api public
      def to_array(value)
        Array(value)
      end

      # Create a Hash from the Object if possible
      #
      # @example with a coercible object
      #   coercer[Object].to_hash(key => value)  # => { key => value }
      #
      # @example with an object that is not coercible
      #   coercer[Object].to_hash(value)  # => value
      #
      # @param [#to_hash, Object] value
      #
      # @return [Hash]
      #   returns a Hash when the object can be coerced
      # @return [Object]
      #   returns the value when the object cannot be coerced
      #
      # @api public
      def to_hash(value)
        coerce_with_method(value, :to_hash, __method__)
      end

      # Create a String from the Object if possible
      #
      # @example with a coercible object
      #   coercer[Object].to_string("string")  # => "string"
      #
      # @example with an object that is not coercible
      #   coercer[Object].to_string(value)  # => value
      #
      # @param [#to_str, Object] value
      #
      # @return [String]
      #   returns a String when the object can be coerced
      # @return [Object]
      #   returns the value when the object cannot be coerced
      #
      # @api public
      def to_string(value)
        coerce_with_method(value, :to_str, __method__)
      end

      # Create an Integer from the Object if possible
      #
      # @example with a coercible object
      #   coercer[Object].to_integer(1)  # => 1
      #
      # @example with an object that is not coercible
      #   coercer[Object].to_integer(value)  # => value
      #
      # @param [#to_int, Object] value
      #
      # @return [Integer]
      #   returns an Integer when the object can be coerced
      # @return [Object]
      #   returns the value when the object cannot be coerced
      #
      # @api public
      def to_integer(value)
        coerce_with_method(value, :to_int, __method__)
      end

      # Return if the value was successfuly coerced
      #
      # @example when coercion was successful
      #   coercer[String].coerced?(1) # => true
      #
      # @example when coercion was NOT successful
      #   coercer[String].coerced?("foo") # => false
      #
      # @return [TrueClass,FalseClass]
      #
      # @api public
      def coerced?(value)
        value.kind_of?(self.class.primitive)
      end

      private

      # Raise an unsupported coercion error
      #
      # @raises [UnsupportedCoercion]
      #
      # @return [undefined]
      #
      # @api private
      def raise_unsupported_coercion(value, method)
        raise(
          UnsupportedCoercion,
          "#{self.class}##{method} doesn't know how to coerce #{value.inspect}"
        )
      end

      # Passthrough given value
      #
      # @param [Object] value
      #
      # @return [Object]
      #
      # @api private
      def method_missing(method, *args)
        if method.to_s =~ COERCION_METHOD_REGEXP && args.size == 1
          args.first
        else
          super
        end
      end

      # Try to use native coercion method on the given value
      #
      # @param [Object] value
      #
      # @param [Symbol] method
      #
      # @return [Object]
      #
      # @api private
      def coerce_with_method(value, method, ref_method)
        value.respond_to?(method) ? value.public_send(method) : raise_unsupported_coercion(value, ref_method)
      end

    end # class Object

  end # class Coercer
end # module Coercible
