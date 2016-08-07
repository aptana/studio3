module Coercible

  # Coercer object
  #
  #
  # @example
  #
  #   coercer = Coercible::Coercer.new
  #
  #   coercer[String].to_boolean('yes') # => true
  #   coercer[Integer].to_string(1)     # => '1'
  #
  # @api public
  class Coercer

    # Return coercer instances
    #
    # @return [Array<Coercer::Object>]
    #
    # @api private
    attr_reader :coercers

    # Returns global configuration for coercers
    #
    # @return [Configuration]
    #
    # @api private
    attr_reader :config

    # Build a new coercer
    #
    # @example
    #
    #   Coercible::Coercer.new { |config| # set configuration }
    #
    # @yieldparam [Configuration]
    #
    # @return [Coercer]
    #
    # @api public
    def self.new(&block)
      configuration = Configuration.build(config_keys)

      configurable_coercers.each do |coercer|
        configuration.send("#{coercer.config_name}=", coercer.config)
      end

      yield(configuration) if block_given?

      super(configuration)
    end

    # Return configuration keys for Coercer instance
    #
    # @return [Array<Symbol>]
    #
    # @api private
    def self.config_keys
      configurable_coercers.map(&:config_name)
    end
    private_class_method :config_keys

    # Return coercer classes that are configurable
    #
    # @return [Array<Class>]
    #
    # @api private
    def self.configurable_coercers(&block)
      Coercer::Object.descendants.select { |descendant|
        descendant.respond_to?(:config)
      }
    end
    private_class_method :configurable_coercers

    # Initialize a new coercer instance
    #
    # @param [Hash] coercers
    #
    # @param [Configuration] config
    #
    # @return [undefined]
    #
    # @api private
    def initialize(config, coercers = {})
      @coercers = coercers
      @config   = config
    end

    # Access a specific coercer object for the given type
    #
    # @example
    #
    #   coercer[String] # => string coercer
    #   coercer[Integer] # => integer coercer
    #
    # @param [Class] type
    #
    # @return [Coercer::Object]
    #
    # @api public
    def [](klass)
      coercers[klass] || initialize_coercer(klass)
    end

    private

    # Initialize a new coercer instance for the given type
    #
    # If a coercer class supports configuration it will receive it from the
    # global configuration object
    #
    # @return [Coercer::Object]
    #
    # @api private
    def initialize_coercer(klass)
      coercers[klass] =
        begin
          coercer = Coercer::Object.determine_type(klass) || Coercer::Object
          args    = [ self ]
          args   << config_for(coercer) if coercer.respond_to?(:config_name)
          coercer.new(*args)
        end
    end

    # Find configuration for the given coercer type
    #
    # @return [Configuration]
    #
    # @api private
    def config_for(coercer)
      config.send(coercer.config_name)
    end

  end # class Coercer

end # module Coercible
