require 'java'
require 'ruble/ui'
require 'ruble/project'

module Ruble
  class Terminal
    class << self
      # FIXME Should probably be able to pass in secondary id, title, etc as options in a hash so we can specify them even without a command or a working dir, etc
      def open(command, working_dir = nil, secondary_id = nil, title = nil)
        # TODO Clean up the command string for display purposes here. Drop '"'? Drop ruby vm args?
        working_dir_path = org.eclipse.core.runtime.Path.new(working_dir) if working_dir
        secondary_id ||= Ruble::Project.current.name
        title ||= Ruble::Project.current.name
        if title.nil? and !working_dir.nil?
          title = working_dir
        end
        term = com.aptana.terminal.views.TerminalView.openView(secondary_id, title, working_dir_path)
        return nil unless term
        term.sendInput("#{command}\n") unless command.nil?
        nil
      end
    end
  end
end
