require 'java'
require 'radrails/ui'

module RadRails
  class Terminal
    class << self
      def open(command, working_dir = nil)
        page = RadRails::UI.active_page
        com.aptana.terminal.TerminalBrowser.setStartingDirectory(working_dir)
        term = page.showView(com.aptana.terminal.views.TerminalView::ID, command.gsub(/:/, '_'), org.eclipse.ui.IWorkbenchPage::VIEW_ACTIVATE)
        # TODO Clean up the command string for display purposes here. Drop '"'? Drop ruby vm args?
        term.setPartName(command)
        wrapper = com.aptana.terminal.server.HttpServer.instance.getProcess(term.getId())
        wrapper.sendText("#{command}\n")
        nil
      end
    end
  end
end
