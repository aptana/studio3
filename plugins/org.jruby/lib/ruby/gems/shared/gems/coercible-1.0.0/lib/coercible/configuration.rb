module Coercible

  # Configuration object for global and per coercer type settings
  #
  class Configuration

    # Build a configuration instance
    #
    # @param [Array] list of accessor keys
    #
    # @return [Configuration]
    #
    # @api private
    def self.build(keys, &block)
      config = new
      keys.each do |key|
        config.instance_eval <<-RUBY
          def #{key}
            @#{key}
          end

          def #{key}=(value)
            @#{key} = value
          end
        RUBY
      end
      yield(config) if block_given?
      config
    end

  end # class Configuration

end # module Coercible
