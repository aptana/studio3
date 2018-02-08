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
          path = path_that_exists(com.aptana.core.util.PlatformUtil.expandEnvironmentStrings("%PROGRAMFILES%/Mozilla Firefox/firefox.exe"),
            com.aptana.core.util.PlatformUtil.expandEnvironmentStrings("%PROGRAMFILES(x86)%/Mozilla Firefox/firefox.exe"),
            com.aptana.core.util.PlatformUtil.expandEnvironmentStrings("%PROGRAMW6432%/Mozilla Firefox/firefox.exe"))
          "ruby -e \"IO.popen('#{path} \"#{url.to_s}\"')\""
        else
          "/usr/bin/firefox \"#{url.to_s}\" &"
        end
      when :chrome
        if Ruble.platforms.include? :mac
          # FIXME Seems to open a new instance and it reports an error about loading profile data
          "\"/Applications/Google Chrome.app/Contents/MacOS/Google Chrome\" \"#{url.to_s}\" &"
        elsif Ruble.platforms.include? :windows
          path = com.aptana.core.util.PlatformUtil.expandEnvironmentStrings("%LOCAL_APPDATA%/Google/Chrome/Application/chrome.exe")
          "ruby -e \"IO.popen('#{path} \"#{url.to_s}\"')\""
        else
          "/usr/bin/google-chrome \"#{url.to_s}\" &"
        end
      when :ie
        path = path_that_exists(com.aptana.core.util.PlatformUtil.expandEnvironmentStrings("%PROGRAMFILES%/Internet Explorer/iexplore.exe"),
          com.aptana.core.util.PlatformUtil.expandEnvironmentStrings("%PROGRAMFILES(X86)%/Internet Explorer/iexplore.exe"),
          com.aptana.core.util.PlatformUtil.expandEnvironmentStrings("%PROGRAMW6432%/Internet Explorer/iexplore.exe"))
        "ruby -e \"IO.popen('#{path} \"#{url.to_s}\"')\""
      when :safari
        if Ruble.platforms.include? :mac
          # FIXME Opens in new tab/window
          "osascript -e \"tell application \\\"Safari\\\"\nopen location \\\"#{url.to_s}\\\"\nend tell\""          
        elsif Ruble.platforms.include? :windows
          path = path_that_exists(com.aptana.core.util.PlatformUtil.expandEnvironmentStrings("%PROGRAMFILES%/Safari/Safari.exe"),
            com.aptana.core.util.PlatformUtil.expandEnvironmentStrings("%PROGRAMFILES(X86)%/Safari/Safari.exe"),
            com.aptana.core.util.PlatformUtil.expandEnvironmentStrings("%PROGRAMW6432%/Safari/Safari.exe"))
          # FIXME Doesn't seem to take URL on Windows XP...
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
          path = path_that_exists(com.aptana.core.util.PlatformUtil.expandEnvironmentStrings("%PROGRAMFILES%/Opera/opera.exe"),
            com.aptana.core.util.PlatformUtil.expandEnvironmentStrings("%PROGRAMFILES(X86)%/Opera/opera.exe"),
            com.aptana.core.util.PlatformUtil.expandEnvironmentStrings("%PROGRAMW6432%/Opera/opera.exe"))
          "ruby -e \"IO.popen('#{path} \"#{url.to_s}\"')\""
        else
          "/usr/bin/opera \"#{url.to_s}\" &"
        end
      else
        # Use some default for each OS
        if Ruble.platforms.include? :mac
          "open \"#{url.to_s}\" &"
        elsif Ruble.platforms.include? :windows
          "ruby -e \"IO.popen('#{default_browser} \"#{url.to_s}\"')\""
        else
          # TODO Test
          "xdg-open \"#{url.to_s}\" &"
        end
      end      
      IO.popen(cmd_line, 'r')
      nil
    end        
        
    def path_that_exists(*array)
      # For some reason JRuby needs the drive prefix for File.exist?
      drives = java.io.File.listRoots.map {|r| r.toString()[0..-2] } 
      array.each do |filepath|
        drives.each {|d| return filepath if File.exist?(d + filepath) }
      end
      array.first # Just return the first one, though none exist...
    end    
    
    def default_browser
      if Ruble.platforms.include? :windows
        result = IO.popen("reg query HKEY_CLASSES_ROOT\\http\\shell\\open\\command") {|io| io.read }
        result = result.strip
        result = result[(result.index('REG_SZ') + 6)..-8].strip
        result = result.sub(/"/, '').sub(/"/, '')
        result
      else
        nil
      end
    end
  end  
end
