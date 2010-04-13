require 'java'
require 'ruble/ui'

module Ruble
  class Terminal
    class << self
      def open(command, working_dir = nil)
        # TODO Clean up the command string for display purposes here. Drop '"'? Drop ruby vm args?
        working_dir_path = org.eclipse.core.runtime.Path.new(working_dir) if working_dir
        term = com.aptana.terminal.views.TerminalView.openView(command.gsub(/:/, '_'), command, working_dir_path)
        return nil unless term
        term.sendInput("#{command}\n")
        nil
      end
    end
  end
end
