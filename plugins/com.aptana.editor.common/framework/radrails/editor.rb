require "java"
require "uri"
require "radrails/ui"

module RadRails
  class Editor
    class << self
      
      # Opens the editor on a given file. Argument can be a URI object or a String. No URI scheme assumes file:.
      # Ability to open the file in an editor is up to the underlying Eclipse filesystem API and whether 
      # there's a registered handler for the scheme. Local files should always open.
      def open(absolute_path)
        uri = absolute_path
        uri = URI.parse(uri) if !uri.respond_to? :scheme
        absolute_path = uri.path
        uri_string = uri.scheme ? uri.to_s : "file:#{absolute_path}"
        editor = nil
        RadRails::UI.run("Opening editor") do          
          page = RadRails::UI.active_page
          return nil unless page
          ipath = org.eclipse.core.runtime.Path.new(absolute_path)
          ifile = org.eclipse.core.resources.ResourcesPlugin.workspace.root.getFileForLocation(ipath)
          if ifile.nil?
            file_name = ipath.last_segment
            editor_registry = org.eclipse.ui.PlatformUI.workbench.editor_registry
            content_type = org.eclipse.core.runtime.Platform.content_type_manager.findContentTypeFor(file_name)
            desc = editor_registry.getDefaultEditor(file_name, content_type)
          else
            desc = org.eclipse.ui.ide.IDE.getDefaultEditor(ifile)
          end
          editor_id = "org.eclipse.ui.DefaultTextEditor" # FIXME Set to out own text editor if/when we have one!
          editor_id = desc.getId unless desc.nil?
          editor = RadRails::Editor.new(org.eclipse.ui.ide.IDE.openEditor(page, java.net.URI.create(uri_string), editor_id, true))
        end
        return editor
      end
      
      # Return the active editor
      def active
        editor = nil
        RadRails::UI.run("Getting reference to active editor") do          
          page = RadRails::UI.active_page
          return nil unless page
          editor = RadRails::Editor.new(page.active_editor)
        end
        return editor
      end
      
      # Opens an editor to a specific file, line and column. If no file is specified, assume the active editor
      # Line numbers begin at 1. If not specified, line will be 1.
      # Columns begin at 1. If not specified, column will be 1.
      # Returns reference to opened RadRails::Editor
      def go_to(options = {})
        default_line = options.has_key?(:file) ? 1 : ENV['TM_LINE_NUMBER']
        options = {:file => ENV['TM_FILEPATH'], :line => default_line, :column => 1}.merge(options)
        editor = nil
        if options[:file]
          editor = RadRails::Editor.open("file://#{options[:file]}")
        else
          editor = RadRails::Editor.active
        end
        return nil unless editor        
        region = editor.document.getLineInformation(options[:line].to_i - 1)
        editor.selection = [region.offset + options[:column].to_i - 1, 0]
        editor
      end
    end
    
    # Not meant to be invoked by end users. If you want an editor instance, open it.
    def initialize(editor_part)
      @editor_part = editor_part
    end
    
    # Return the wrapped IEditorPart from Java
    def editor_part
      @editor_part
    end
    
    # Closes the editor. Pass in false to avoid saving before closing.
    def close(save = true)     
      closed = false 
      RadRails::UI.run("Close Editor") { closed = RadRails::UI.active_page.close_editor(editor_part, save) }
      closed
    end
    
    # Close without saving
    def close!
      close(false)
    end
    
    # Saves the editor. Pass in false to avoid confirm dialog if editor is dirty.
    def save(confirm = true)
      saved = false
      RadRails::UI.run("Save Editor") { saved = RadRails::UI.active_page.save_editor(editor_part, confirm) }
      saved
    end
    
    # Save without confirming
    def save!
      save(false)
    end
    
    # Is this editor dirty? (i.e. does it have unsaved edits)
    def dirty?
      editor_part.dirty?
    end
    
    # FIXME This method only exists in Eclipse 3.5+
    def hide
      RadRails::UI.run("Hide Editor") { RadRails::UI.active_page.hide_editor(editor_reference) }
    end
    
    # FIXME This method only exists in Eclipse 3.5+
    def show
      RadRails::UI.run("Show Editor") { RadRails::UI.active_page.show_editor(editor_reference) }
    end
    
    def editor_reference
      RadRails::UI.active_page.editor_references.find {|ref| ref.getEditor(false) == editor_part }
    end
    
    def editor_input
      editor_part.editor_input
    end
    
    def document
      # TODO Wrap in a proxy class/object so we can use array notation for getting/replacing portions of the text
      editor_part.document_provider.getDocument(editor_input)
    end
    
    def document=(src)
      RadRails::UI.run("Change Editor Contents") { document.set(src) }
    end
    
    # Is the editor editable?
    def editable?
      editor_part.editable?
    end
    
    # Replace a portion of the editor's contents
    # Assumes that the args in the brackets are offset and length, and that the value is a string
    def []=(offset, length, src)    
      RadRails::UI.run("Replacing Editor Contents") { document.replace(offset, length, src) }
    end
    
    # TODO Just forward missing methods over to editor_part?
    
    # Return the current selection. Should typically be an ITextSelection which means you should 
    # be able to call the following methods:
    # * length
    # * offset
    # * text
    # * start_line
    # * end_line
    def selection
      editor_part.selection_provider.selection
    end
    
    # Argument is a 2 integer array with first being offset, second being length; 
    # or a Range object with the range of offsets
    def selection=(array_or_range)
      if array_or_range.respond_to? :length # array
        offset = array_or_range.first
        length = array_or_range.last
      else
        offset = array_or_range.first
        length = array_or_range.last - offset
      end
      RadRails::UI.run("Changing Editor Selection") { editor_part.select_and_reveal(offset, length) }
    end
    
    def styled_text
      editor_part.get_adapter(org.eclipse.swt.widgets.Control.java_class)
    end
    
    def current_scope
      scope_at_offset caret_offset
    end
    
    def scope_at_offset(offset)
      ctype = content_type(offset)
      
      if ctype.nil?
        document.content_type(offset)
      else
        com.aptana.editor.common.tmp.ContentTypeTranslation.default.translate(ctype).to_s
      end
    end
    
    def caret_column
      selection.offset - styled_text.offset_at_line(selection.start_line)
    end
    
    def caret_line
      styled_text.line_at_offset(caret_offset)
    end
    
    def caret_offset
      styled_text.caret_offset
    end
    
    def content_type(offset)
      com.aptana.editor.common.DocumentContentTypeManager.instance.get_content_type(document, offset)
    end
    
    def current_line
      styled_text.line(caret_line)
    end
    
    def to_env
      input = editor_input
      result = {}
      
      if editor_part.kind_of?(com.aptana.editor.common.AbstractThemeableEditor)
        result['TM_SOFT_TABS'] = editor_part.isTabsToSpacesConversionEnabled() ? "YES" : "NO"
        result['TM_TAB_SIZE'] = editor_part.getTabSize()
      else
        result['TM_SOFT_TABS'] = "NO"
        result['TM_TAB_SIZE'] = 4
      end
      
      if input
        ifile = input.file
        file = ifile.location.to_file
        
        result["TM_SELECTED_FILE"] = file.absolute_path
        result["TM_FILEPATH"] = file.absolute_path
        result["TM_FILENAME"] = file.name
        result["TM_DIRECTORY"] = file.parent_file.absolute_path
        result["TM_PROJECT_DIRECTORY"] = ifile.project.location.to_file.absolute_path # duplicate name in project.rb
        
        if !selection.empty? and selection.text.length > 0
          result["TM_SELECTED_TEXT"] = selection.text
          result["TM_LINE_NUMBER"] = selection.start_line + 1
          result["TM_SELECTION_OFFSET"] = selection.offset
          result["TM_SELECTION_LENGTH"] = selection.length
          result["TM_SELECTION_START_LINE_NUMBER"] = selection.start_line
          result["TM_SELECTION_END_LINE_NUMBER"] = selection.end_line
        end
        
        result["TM_LINE_INDEX"] = caret_column
        result["TM_CARET_LINE_NUMBER"] = caret_line + 1
        result["TM_CARET_LINE_TEXT"] = current_line
        result["TM_CARET_OFFSET"] = caret_offset
        result["TM_CURRENT_LINE"] = current_line
        
        # I'm sure there's a better way to extract the word at the current caret position
        if current_line !~ /^\s*$/
          starting_offset = caret_column
          ending_offset = current_line.length
          
          (caret_column - 1).downto(0) do |n|
            if current_line[n,1] =~ /\W/
              starting_offset = n + 1
              break
            end
          end
          (caret_column...current_line.length).each do |n|
            if current_line[n,1] =~ /\W/
              ending_offset = n
              break
            end
          end
          
          result["TM_CURRENT_WORD"] = current_line[starting_offset, ending_offset - starting_offset]
        else
          result["TM_CURRENT_WORD"] = ""
        end
        
        result["TM_CURRENT_SCOPE"] = current_scope
        result["TM_SCOPE"] = result["TM_CURRENT_SCOPE"]
      end
      
      result
    end
    
  end
end
