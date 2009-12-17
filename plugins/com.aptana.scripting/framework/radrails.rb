require "radrails/bundle"
require "radrails/command"
require "radrails/logger"
require "radrails/menu"
require "radrails/snippet"

def bundle(name, &block)
  RadRails::Bundle.define_bundle(name, {}, &block)
end

def command(name, &block)
  RadRails::Command.define_command(name, &block)
end

def logError(error)
  RadRails::Logger.logError(error)
end

def logInfo(info)
  RadRails::Logger.logInfo(info)
end

def logWarning(warning)
  RadRails::Logger.logWarning(warning)
end

def trace(message)
  RadRails::Logger.trace(message)
end

def menu(name, &block)
  RadRails::Menu.define_menu(name, &block)
end

def snippet(name, &block)
  RadRails::Snippet.define_snippet(name, &block)
end

def with_defaults(values, &block)
  bundle = RadRails::BundleManager.bundle_from_path(File.dirname($fullpath))
  
  if bundle.nil?
    bundle = RadRails::Bundle.define_bundle("<unknown>", values, &block)
  else
    bundle.defaults = values
    block.call(bundle) if block_given?
  end
end

module RadRails
  class << self
    def current_bundle(&block)
      with_defaults({}, &block)
    end
  end
end
