require "radrails/bundle"
require "radrails/command"
require "radrails/menu"
require "radrails/snippet"

def bundle(name, &block)
  RadRails::Bundle.define_bundle(name, &block)
end

def menu(name, &block)
  RadRails::Menu.define_menu(name, &block)
end

def snippet(name, &block)
  RadRails::Snippet.define_snippet(name, &block)
end

def command(name, &block)
  RadRails::Command.define_command(name, &block)
end

module RadRails
  class << self
    def current_bundle(&block)
      bundle = BundleManager.bundle_from_path(File.dirname($fullpath))
      
      if bundle.nil?
        Bundle.define_bundle("<unknown>", &block)
      else
        block.call(bundle) if block_given?
      end
    end
  end
end
