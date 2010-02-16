module Ruble
  class Browser
    def self.open(symbol, url)
      # TODO Allow users to launch an external browser
      cmd_line = case symbol
      when :firefox
        # TODO Launch Firefox on *nix/Windows
        "/Applications/Firefox.app/Contents/MacOS/firefox-bin \"#{url.to_s}\" &"
      when :chrome
        # TODO Launch Chrome
      when :ie
        "cmd /C start \"#{url.to_s}\""
      when :safari
        # TODO What about Safari on Windows?
        "open \"#{url.to_s}\" &"
      when :webkit
        # TODO What about WebKit on Windows?
        # TODO Need to generate a tmp file that causes redirect, webkit assumes a file...
        "/Applications/WebKit.app/Contents/MacOS/WebKit \"#{url.to_s}\" &"
      when :opera
        # TODO Launch opera
      else
        # Use some default for each OS
        if Ruble.Platforms.include? :mac
        "open \"#{url.to_s}\" &"
        elsif Ruble.Platforms.include? :windows
        "cmd /C start \"#{url.to_s}\""
        else
          # TODO What is the default for Unix/Linux?
        end
      end      
      IO.popen(cmd_line, 'r')
      nil
    end        
    
    # Open an internal browser pointed at URL
    def open(url, options = {})
      browser_id = options[:new_window] ? nil : "singleton"
      title = options[:title]
      support = org.eclipse.ui.PlatformUI.workbench.browser_support
      wbs = org.eclipse.ui.browser.IWorkbenchBrowserSupport
      style = wbs::NAVIGATION_BAR | wbs::LOCATION_BAR | wbs::AS_EDITOR | wbs::STATUS
      if support.isInternalWebBrowserAvailable
        support.createBrowser(style, browser_id, title, nil).openURL(java.net.URL.new(url.to_s))
      end
    end
  end  
end
