require 'java'
require 'ruble/ui'

module Ruble
  class Terminal
    class << self
      def open(command, working_dir = nil)
        # TODO Clean up the command string for display purposes here. Drop '"'? Drop ruby vm args?
        term = com.aptana.terminal.views.TerminalView.open(command.gsub(/:/, '_'), command, working_dir)
        return nil unless term
        wrapper = com.aptana.terminal.server.TerminalServer.instance.getProcess(term.getId())
        wrapper.sendText("#{command}\n")
        nil
      end
    end
  end
end
