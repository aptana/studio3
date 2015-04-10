module Coercible
  class Coercer

    module Configurable

      # Add configuration-specific option keys to the descendant
      #
      # @return [self]
      #
      # @api private
      def self.extended(coercer)
        coercer.accept_options :config_keys
        super
      end

      # Build configuration object for the coercer class
      #
      # @example
      #
      #   coercer_class = Class.new(Coercer::Object) do
      #     extend Configurable
      #
      #     config_keys [ :foo, :bar ]
      #   end
      #
      #   coercer_class.config do |config|
      #     config.foo = '1'
      #     config.bar = '2'
      #   end
      #
      # @yieldparam [Configuration]
      #
      # @return [Configuration]
      #
      # @api public
      def config(&block)
        configuration = configuration_class.build(config_keys)
        yield configuration
        configuration
      end

      # Return configuration name in the global config
      #
      # @return [Symbol]
      #
      # @api private
      def config_name
        name.downcase.split('::').last.to_sym
      end

      # Return configuration class
      #
      # @return [Class:Configuration]
      #
      # @api private
      def configuration_class
        Configuration
      end

    end # module Configurable

  end # class Coercer
end # module Coercible
