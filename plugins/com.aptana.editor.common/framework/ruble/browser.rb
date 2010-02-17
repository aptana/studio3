module Ruble
  class Browser        
    
    # Open an internal browser pointed at URL
    def open(url, options = {})
      if options[:browser].nil?
        browser_id = options[:new_window] ? nil : "singleton"
        title = options[:title]
        support = org.eclipse.ui.PlatformUI.workbench.browser_support
        wbs = org.eclipse.ui.browser.IWorkbenchBrowserSupport
        style = wbs::NAVIGATION_BAR | wbs::LOCATION_BAR | wbs::AS_EDITOR | wbs::STATUS
        support.createBrowser(style, browser_id, title, nil).openURL(java.net.URL.new(url.to_s))
      else
        external_open(options[:browser], url)
      end
    end    
    
    private
    def external_open(symbol, url)
      # TODO Set to default for OS if not valid for the current OS
      valid_browsers = [:safari, :firefox, :chrome, :opera]
      if Ruble.platforms.include? :windows
        valid_browsers << :ie
      elsif Ruble.platforms.include? :mac
        valid_browsers << :webkit
      end

      # Force default case if browser choice is invalid for OS
      symbol = nil if !valid_browsers.include? symbol

      cmd_line = case symbol
      when :firefox
        if Ruble.platforms.include? :mac
          "/Applications/Firefox.app/Contents/MacOS/firefox-bin \"#{url.to_s}\" &"
        elsif Ruble.platforms.include? :windows
          path = path_that_exists("/Program Files/Mozilla Firefox/firefox.exe", "/Program Files (x86)/Mozilla Firefox/firefox.exe")
          "ruby -e \"IO.popen('#{path} \"#{url.to_s}\"')\""
        else
          "/usr/bin/firefox \"#{url.to_s}\" &"
        end
      when :chrome
        if Ruble.platforms.include? :mac
          # FIXME Seems to open a new instance and it reports an error about loading profile data
          "\"/Applications/Google Chrome.app/Contents/MacOS/Google Chrome\" \"#{url.to_s}\" &"
        elsif Ruble.platforms.include? :windows
          path = path_that_exists("/Documents and Settings/#{ENV['TM_FULLNAME']}/Local Settings/Application Data/Google/Chrome/Application/chrome.exe", "/Users/#{ENV['TM_FULLNAME']}/AppData/Local/Google/Chrome/Application/chrome.exe")
          "ruby -e \"IO.popen('#{path} \"#{url.to_s}\"')\""
        else
          "/usr/bin/google-chrome \"#{url.to_s}\" &"
        end
      when :ie
        "cmd /C start \"#{url.to_s}\""
      when :safari
        if Ruble.platforms.include? :mac
          # FIXME Opens in new tab/window
          "osascript -e \"tell application \\\"Safari\\\"\nopen location \\\"#{url.to_s}\\\"\nend tell\""          
        elsif Ruble.platforms.include? :windows
          # FIXME Doesn't seem to take URL...
          path = path_that_exists("/Program Files/Safari/Safari.exe", "/Program Files (x86)/Safari/Safari.exe")
          "ruby -e \"IO.popen('#{path} \"#{url.to_s}\"')\""
        end
      when :webkit
        # TODO What about WebKit on Windows?
        # FIXME Opens in new tab/window
        "osascript -e \"tell application \\\"WebKit\\\"\nopen location \\\"#{url.to_s}\\\"\nend tell\""
      when :opera
        if Ruble.platforms.include? :mac
          "/Applications/Opera.app/Contents/MacOS/Opera \"#{url.to_s}\" &"
        elsif Ruble.platforms.include? :windows
          path = path_that_exists("/Program Files/Opera/opera.exe", "/Program Files (x86)/Opera/opera.exe")
          "ruby -e \"IO.popen('#{path} \"#{url.to_s}\"')\""
        else
          "/usr/bin/opera \"#{url.to_s}\" &"
        end
      else
        # Use some default for each OS
        if Ruble.platforms.include? :mac
          "open \"#{url.to_s}\" &"
        elsif Ruble.platforms.include? :windows
          "cmd /C start \"#{url.to_s}\""
        else
          # TODO Test
          "xdg-open \"#{url.to_s}\" &"
        end
      end      
      IO.popen(cmd_line, 'r')
      nil
    end        
        
    def path_that_exists(*array)
      array.each do |filepath|
        return filepath if File.exist?("C:" + filepath)
      end
      array.first # Just return the first one, though none exist...
    end    
    
  end  
end
