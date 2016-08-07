module Coercible
  class Coercer

    # Coerce Fixnum values
    class Integer < Numeric
      extend Configurable

      primitive ::Integer

      config_keys [ :datetime_format, :datetime_proc, :boolean_map ]

      # Return default config for Integer coercer type
      #
      # @return [Configuration]
      #
      # @see Configurable#config
      #
      # @api private
      def self.config
        super do |config|
          # FIXME: Remove after Rubinius 2.0 is released
          config.datetime_format, config.datetime_proc =
            if Coercible.rbx?
              [ '%Q', Proc.new { |value| "#{value * 10**3}" } ]
            else
              [ '%s', Proc.new { |value| "#{value}" } ]
            end

          config.boolean_map = { 0 => false, 1 => true }
        end
      end

      # Return datetime format from config
      #
      # @return [::String]
      #
      # @api private
      attr_reader :datetime_format

      # Return datetime proc from config
      #
      # @return [Proc]
      #
      # @api private
      attr_reader :datetime_proc

      # Return boolean map from config
      #
      # @return [::Hash]
      #
      # @api private
      attr_reader :boolean_map

      # Initialize a new Integer coercer instance and set its configuration
      #
      # @return [undefined]
      #
      # @api private
      def initialize(coercer = Coercer.new, config = self.class.config)
        super(coercer)
        @boolean_map     = config.boolean_map
        @datetime_format = config.datetime_format
        @datetime_proc   = config.datetime_proc
      end

      # Coerce given value to String
      #
      # @example
      #   coercer[Integer].to_string(1)  # => "1"
      #
      # @param [Fixnum] value
      #
      # @return [String]
      #
      # @api public
      def to_string(value)
        value.to_s
      end

      # Passthrough the value
      #
      # @example
      #   coercer[Integer].to_integer(1)  # => 1
      #
      # @param [Fixnum] value
      #
      # @return [Float]
      #
      # @api public
      def to_integer(value)
        value
      end

      # Coerce given value to a Boolean
      #
      # @example with a 1
      #   coercer[Integer].to_boolean(1)  # => true
      #
      # @example with a 0
      #   coercer[Integer].to_boolean(0)  # => false
      #
      # @param [Fixnum] value
      #
      # @return [BigDecimal]
      #
      # @api public
      def to_boolean(value)
        boolean_map.fetch(value) {
          raise_unsupported_coercion(value, __method__)
        }
      end

      # Coerce given value to a DateTime
      #
      # @example
      #   coercer[Integer].to_datetime(0)  # => Thu, 01 Jan 1970 00:00:00 +0000
      #
      # @param [Integer] value
      #
      # @return [DateTime]
      #
      # @api public
      def to_datetime(value)
        ::DateTime.strptime(datetime_proc.call(value), datetime_format)
      end

    end # class Fixnum

  end # class Coercer
end # module Coercible
