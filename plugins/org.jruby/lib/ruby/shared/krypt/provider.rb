require_relative 'provider/provider'

##
# If JRuby is configured with native API access disabled, requiring the FFI
# provider will result in a LoadError. The FFI provider is not required at
# runtime as there is always a default (Java-based) provider.
#

def java?
  !! (RUBY_PLATFORM =~ /java/)
end

def native_disabled?
  require 'jruby'
  !JRuby.runtime.instance_config.native_enabled
end

unless java?
  require_relative 'provider/ffi'
end
