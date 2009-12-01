require "radrails/bundle"
require "radrails/command"
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
