require "java"

module Ruble
  
  # A class to make it easy to generate UIJobs. Takes a block which is 
  # then called as the body of runInUIThread
  class UIJob < org.eclipse.ui.progress.UIJob
    def initialize(name, &blk)
      super(name)
      @block = blk
    end
    
    def runInUIThread(monitor)
      @block.call(monitor)
      return org.eclipse.core.runtime.Status::OK_STATUS
    end
  end
  
  module UI
    class << self
      # Opens a modal dialog
      # +style+ should be one of :warning, :info, or :error
   	  def alert(style, title, message, *buttons)
        styles = [:warning, :info, :error]
        raise "style must be one of #{types.inspect}" unless styles.include?(style)
        
        type = org.eclipse.jface.dialogs.MessageDialog::INFORMATION # default to info
        case style
        when :warning
          type = org.eclipse.jface.dialogs.MessageDialog::WARNING
        when :info
          type = org.eclipse.jface.dialogs.MessageDialog::INFORMATION
        when :error
          type = org.eclipse.jface.dialogs.MessageDialog::ERROR
        end
		    dialog = org.eclipse.jface.dialogs.MessageDialog.new(shell, title, nil, message, type, buttons.to_java(:String), 0)
		    button_index = dialog.open
		    buttons[button_index]
      end
      
      # show the system color picker and return a hex-format color (#RRGGBB).
      # If the input string is a recognizable hex string, the default color will be set to it.
      # If the dialog is dismissed, we return nil
      def request_color(string = nil)
        string = '#999' unless string.to_s.match(/#?[0-9A-F]{3,6}/i)
        color  = string
        prefix, string = string.match(/(#?)([0-9A-F]{3,6})/i)[1,2]
        string = $1 * 2 + $2 * 2 + $3 * 2 if string =~ /^(.)(.)(.)$/
		    r = string[0...2].hex
		    g = string[2...4].hex
		    b = string[4...6].hex 
        value = org.eclipse.swt.graphics.RGB.new(r, g, b)
        color_dialog = org.eclipse.swt.widgets.ColorDialog.new(shell)
		    color_dialog.setRGB(value)
		    new_rgb = color_dialog.open
		    if new_rgb.nil?
		      nil
		    else
          "#{prefix}#{new_rgb.red.to_s(16).rjust(2, '0')}#{new_rgb.green.to_s(16).rjust(2, '0')}#{new_rgb.blue.to_s(16).rjust(2, '0')}"
        end
      end
      
      # Opens a simple info alert
      # Possible Options:
      # :title => String - title of dialog
      # :summary => String - message/summary shown in dialog
      def simple_notification(options)
        raise if options.empty?

        title    = options[:title]   || ''
        summary  = options[:summary] || ''
        log      = options[:log]     || ''
		    alert(:info, title, summary, "OK")        
      end
      
      # show a standard open file dialog
      # Possible Options:
      # :only_directories => true - limit to directory selection. If not present or false, it will be limited to only file selection
      # :default => String - message added to dialog if selecting directories
      # :title => String - title of dialog
      # :directory => String - opening directory path for dialog
      def request_file(options = Hash.new,&block)      
        dialog = nil
        if options[:only_directories]
          dialog = org.eclipse.swt.widgets.DirectoryDialog.new(shell)
          dialog.message = options[:default] || ""
        else
          dialog = org.eclipse.swt.widgets.FileDialog.new(shell, org.eclipse.swt.SWT::OPEN)
        end
        dialog.text = options[:title] || "Select File"
        dialog.filter_path = options[:directory] if options[:directory]
        dialog.open
        # FIXME Handle when block is given
      end
      
      # show a standard open file dialog, allowing multiple selections
      # Possible Options:
      # :only_directories => true - limit to directory selection. If not present or false, it will be limited to only file selection
      # :default => String - message added to dialog if selecting directories
      # :title => String - title of dialog
      # :directory => String - opening directory path for dialog
      def request_files(options = Hash.new,&block)
        # TODO Combine common code with request_file!
        dialog = nil
        if options[:only_directories]
          dialog = org.eclipse.swt.widgets.DirectoryDialog.new(shell, org.eclipse.swt.SWT::MULTI)
          dialog.message = options[:default] || ""
        else
          dialog = org.eclipse.swt.widgets.FileDialog.new(shell, org.eclipse.swt.SWT::OPEN | org.eclipse.swt.SWT::MULTI)
        end
        dialog.text = options[:title] || "Select File(s)"
        dialog.filter_path = options[:directory] if options[:directory]
        dialog.open
        # FIXME Handle when block is given
        # FIXME WHen user selects multiple files we don't return all of them here...
      end
      
      # Post a confirmation alert
      # Possible options:
      # :button1 => String - label of the default button
      # :button2 => String - label of the secondary button
      # :title => String - Title of the dialog
      # :prompt => String - message in the dialog
      def request_confirmation(options = Hash.new,&block)
        button1 = options[:button1] || "Continue"
        button2 = options[:button2] || "Cancel"
        title   = options[:title]   || "Something Happened"
        prompt  = options[:prompt]  || "Should we continue or cancel?"

        res = alert(:info, title, prompt, button1, button2)

        if res == button1 then
          block_given? ? yield : true
        else
          block_given? ? raise(SystemExit) : false
        end
      end
      
      # Request an item from a list of items
      def request_item(options = Hash.new,&block)
        items = options[:items] || []
        case items.size
        when 0 then block_given? ? raise(SystemExit) : nil
        when 1 then block_given? ? yield(items[0]) : items[0]
        else
          params = default_buttons(options)
          params["title"] = options[:title] || "Select item:"
          params["prompt"] = options[:prompt] || ""
          params["string"] = options[:default] || ""
          params["items"] = items

          dialog = org.eclipse.ui.dialogs.ListDialog.new(shell)
          dialog.content_provider = org.eclipse.jface.viewers.ArrayContentProvider.new
          dialog.label_provider = org.eclipse.jface.viewers.LabelProvider.new
          dialog.input = items
          dialog.message = params["prompt"]
          dialog.setInitialSelections([params["string"]].to_java(:object))
          dialog.title = params["title"]
          
          return_value = nil
          return_value = dialog.result[0].to_s if dialog.open == org.eclipse.jface.window.Window::OK

          if return_value == nil then
            block_given? ? raise(SystemExit) : nil
          else
            block_given? ? yield(return_value) : return_value
          end
        end
      end
      
      # request a single, simple string
      def request_string(options = Hash.new,&block)
        request_string_core('Enter string:', false, options, &block)
      end
      
      # request a password or other text which should be obscured from view
      def request_secure_string(options = Hash.new,&block)
        request_string_core('Enter password:', true, options, &block)
      end
      
      # Show Tooltip using current cursor location. +content+ is shown as bold text at top of tooltip.
      # Possible options:
      # :balloon => true - pop up a balloon style tooltip
      # :icon => :error, :info, or :warning add an image icon in upper left of tooltip. Only used with balloon tooltips
      # :message => String, an optional explanatory string that is shown below the content
      def tool_tip(content, options={})
        style = org.eclipse.swt.SWT::NONE
        message = options[:message] || ''
        if options[:balloon]
          style = org.eclipse.swt.SWT::BALLOON
          case options[:icon]
          when :error
            style = style | org.eclipse.swt.SWT::ICON_ERROR
          when :info
            style = style | org.eclipse.swt.SWT::ICON_INFORMATION
          when :warning
            style = style | org.eclipse.swt.SWT::ICON_WARNING
          end
        end
        tooltip = org.eclipse.swt.widgets.ToolTip.new(shell, style)
        tooltip.text = content.to_s
        tooltip.message = message
        tooltip.visible = true
        nil      
      end

      alias :tooltip :tool_tip

      # pop up a menu on screen
      # +options+ may be an Array of Strings, or an Array of Hashes
      # If it's an Array of Hashes, it's expected that each Hash will contain:
      # 'display' => String - entry to show in menu
      # 'image' = > String - image to show alongside the entry
      # 'insert' => String - a snippet to insert when the entry is chosen
      # 'tool_tip' => String - tooltip to display for menu entry
      def menu(options)
        return nil if options.empty?

        return_hash = true
        if options[0].kind_of?(String)
          return_hash = false
          options = options.collect { |e| e == nil ? { 'separator' => 1 } : { 'title' => e } }
        end

		    dialog = com.aptana.scripting.ui.MenuDialog.new(shell, options.to_java("java.util.Map"))
		    # TODO set selection to first item
		    # TODO Open to caret, not mouse?
		    index = dialog.open
		    return nil if index == -1
        return return_hash ? options[index] : index
      end
      
      def active_window
        100.times do
          window = org.eclipse.ui.PlatformUI.workbench.active_workbench_window
          return window if window
          java.lang.Thread.yield
        end
        nil
      end
    
      def active_page
        active_window.active_page if active_window
      end

      # Executes a block inline if we're already in the UI thread or in a UIJob if we're not. if run in a job, we run synchronously by joining the thread.
      def run(title, &blk)
        if in_ui_thread?
          blk.call(org.eclipse.core.runtime.NullProgressMonitor.new)
        else
          job = UIJob.new(title, &blk)
          job.schedule
          job.join
        end
      end
      
      private

      # Used to request a secure string
      class PasswordInputDialog < org.eclipse.jface.dialogs.InputDialog
        def getInputTextStyle
          org.eclipse.swt.SWT::SINGLE | org.eclipse.swt.SWT::BORDER | org.eclipse.swt.SWT::PASSWORD
        end
      end
      
      # common to request_string, request_secure_string
      def request_string_core(default_prompt, secure, options, &block)
        params = default_buttons(options)
        params["title"] = options[:title] || default_prompt
        params["prompt"] = options[:prompt] || ""
        params["string"] = options[:default] || ""

        klass = secure ? PasswordInputDialog : org.eclipse.jface.dialogs.InputDialog
        # FIXME Need to support button\d options and build buttons dynamically!
        dialog = klass.new(shell, params["title"], params["prompt"], params["string"], nil)
        return_value = nil
        return_value = dialog.value if dialog.open == org.eclipse.jface.window.Window::OK
        
        if return_value == nil
          block_given? ? raise(SystemExit) : nil
        else
          block_given? ? yield(return_value) : return_value
        end
      end
      
      def default_buttons(user_options = Hash.new)
        options = Hash.new
        options['button1'] = user_options[:button1] || "OK"
        options['button2'] = user_options[:button2] || "Cancel"
        options
      end

      def in_ui_thread?
        !org.eclipse.swt.widgets.Display.current.nil?
      end
      
      def display
        org.eclipse.swt.widgets.Display.current || org.eclipse.swt.widgets.Display.default
      end
          
      def shell
        display.active_shell || org.eclipse.swt.widgets.Shell.new(display)
      end
    end
  end
end
