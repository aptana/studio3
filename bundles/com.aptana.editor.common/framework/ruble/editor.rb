require "java"
require "ruble/ui"

module Ruble
  # Due to classloading bugs, we can't reach TextSelection.emptySelection, so we create our own here.
  class EmptySelection
    def getOffset
      -1
    end
    
    def getLength
      -1
    end
    
    def getEndLine
      -1
    end
    
    def getStartLine
      -1
    end
    
    def getText
      nil
    end
    
    def isEmpty
      true
    end
    
    alias :empty? :isEmpty
    alias :is_empty? :isEmpty
    alias :isempty :isEmpty
    alias :is_empty :isEmpty
    alias :text :getText
    alias :gettext :getText
    alias :get_text :getText
    alias :start_line :getStartLine
    alias :getstartline :getStartLine
    alias :get_start_line :getStartLine
    alias :end_line :getEndLine
    alias :getendline :getEndLine
    alias :get_end_line :getEndLine
    alias :length :getLength
    alias :getlength :getLength
    alias :get_length :getLength
    alias :offset :getOffset
    alias :getoffset :getOffset
    alias :get_offset :getOffset
  end
  
  class Editor
    class << self
      
      # Opens the editor on a given file. Argument can be a URI object or a String. No URI scheme assumes file:.
      # Ability to open the file in an editor is up to the underlying Eclipse filesystem API and whether 
      # there's a registered handler for the scheme. Local files should always open.
      def open(absolute_path)
        uri = absolute_path
        require "addressable/uri"
        # We use Addressable::URI instead of standard URI class to be able to parse windows paths correctly
        uri = Addressable::URI.convert_path(uri) if !uri.respond_to? :scheme
        absolute_path = uri.path
        uri_string = uri.scheme ? uri.to_s : "file:#{absolute_path}"
        editor = nil
        Ruble::UI.run("Opening editor") do          
          page = Ruble::UI.active_page
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
          editor_id = "com.aptana.editor.text"
          editor_id = desc.getId unless desc.nil?
          editor = Ruble::Editor.new(org.eclipse.ui.ide.IDE.openEditor(page, java.net.URI.create(uri_string), editor_id, true))
        end
        return editor
      end
      
      # Return the active editor
      def active
        editor = nil
        Ruble::UI.run("Getting reference to active editor") do          
          page = Ruble::UI.active_page
          return nil unless page
          editor = Ruble::Editor.new(page.active_editor)
        end
        return editor
      end
      
      # Opens an editor to a specific file, line and column. If no file is specified, assume the active editor
      # Line numbers begin at 1. If not specified, line will be 1.
      # Columns begin at 1. If not specified, column will be 1.
      # Returns reference to opened Ruble::Editor
      def go_to(options = {})
        default_line = options.has_key?(:file) ? 1 : ENV['TM_LINE_NUMBER']
        options = {:file => ENV['TM_FILEPATH'], :line => default_line, :column => 1}.merge(options)
        editor = nil
        if options[:file]
          editor = Ruble::Editor.open("file://#{options[:file]}")
        else
          editor = Ruble::Editor.active
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
      Ruble::UI.run("Close Editor") { closed = Ruble::UI.active_page.close_editor(editor_part, save) }
      closed
    end
    
    # Close without saving
    def close!
      close(false)
    end
    
    # Saves the editor. Pass in false to avoid confirm dialog if editor is dirty.
    def save(confirm = true)
      saved = false
      Ruble::UI.run("Save Editor") { saved = Ruble::UI.active_page.save_editor(editor_part, confirm) }
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
    
    def hide
      Ruble::UI.run("Hide Editor") { Ruble::UI.active_page.hide_editor(editor_reference) }
    end
    
    def show
      Ruble::UI.run("Show Editor") { Ruble::UI.active_page.show_editor(editor_reference) }
    end
    
    def editor_reference
      Ruble::UI.active_page.editor_references.find {|ref| ref.getEditor(false) == editor_part }
    end
    
    def editor_input
      editor_part.editor_input
    end
    
    def document
      if editor_part.respond_to? :document_provider
      # TODO Wrap in a proxy class/object so we can use array notation for getting/replacing portions of the text
        editor_part.document_provider.getDocument(editor_input)
      else
        nil
      end
    end
    
    def document=(src)
      Ruble::UI.run("Change Editor Contents") { document.set(src) }
    end
    
    # Is the editor editable?
    def editable?
      editor_part.editable?
    end
    
    # Replace a portion of the editor's contents
    # Assumes that the args in the brackets are offset and length, and that the value is a string
    def []=(offset, length, src)    
      Ruble::UI.run("Replacing Editor Contents") do
        # Send along verify event so this is treated like user actually inserted text live, since events don't get sent when replacing programmatically
        event = org.eclipse.swt.widgets.Event.new
        event.type = org.eclipse.swt.SWT::Verify
        event.keyCode = 0
        event.text = src
        event.start = offset
        event.end = offset + length
        styled_text.notifyListeners(event.type, event) # Send Verify, for auto-indent
        document.replace(offset, length, src) if event.doit
      end
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
      if editor_part.respond_to? :selection_provider
        editor_part.selection_provider.selection
      else
        Ruble::EmptySelection.new
      end
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
      Ruble::UI.run("Changing Editor Selection") { editor_part.select_and_reveal(offset, length) }
    end
    
    def styled_text
      editor_part.get_adapter(org.eclipse.swt.widgets.Control.java_class)
    end
    
    def current_scope
      scope_at_offset caret_offset
    end
    
    def scope_at_offset(offset)
      if editor_part.respond_to? :source_viewer
        com.aptana.editor.common.CommonEditorPlugin.getDefault.get_document_scope_manager.get_scope_at_offset(editor_part.source_viewer, offset)
      else
        ''
      end
    end
    
    def caret_column
      selection.offset - offset_at_line(selection.start_line)
    end
    
    def caret_line
      doc = document
      doc.nil? ? 0 : doc.getLineOfOffset(caret_offset)
    end
    
    # Returns a region, whose offset and length can be queried
    def line_information(line_number)
      doc = document
      doc.nil? ? nil : doc.getLineInformation(line_number)
    end
    
    def caret_offset
      # Need to convert because there may be folded regions?
      offset = styled_text.nil? ? 0 : styled_text.caret_offset
      editor_part.source_viewer.widgetOffset2ModelOffset(offset) rescue offset
    end
    
    def current_line
      line(caret_line)
    end
    
    # Returns the string content on the line
    def line(line_number)
      doc = document
      return '' if doc.nil?
      region = doc.getLineInformation(line_number)
      doc.get(region.offset, region.length)
    end
    
    def insert_as_text(text)
      self[caret_offset, 0] = snippet
    end
    
    def offset_at_line(line)
      doc = document
      doc.nil? ? 0 : doc.getLineOffset(line)
    end

    def insert_as_snippet(snippet)
      region = org.eclipse.jface.text.Region.new(caret_offset, 0)
      text_viewer = editor_part.source_viewer
      com.aptana.editor.common.scripting.snippets.SnippetsCompletionProcessor.insertAsTemplate(text_viewer, region, snippet, nil)
    end
    
    def to_env
      result = {}
      
      if editor_part.kind_of?(com.aptana.editor.common.AbstractThemeableEditor)
        result['TM_SOFT_TABS'] = editor_part.isTabsToSpacesConversionEnabled() ? "YES" : "NO"
        result['TM_TAB_SIZE'] = editor_part.getTabSize()
      else
        result['TM_SOFT_TABS'] = "NO"
        result['TM_TAB_SIZE'] = 4
      end
      
      input = editor_input
      if input.respond_to? :file
        ifile = input.file
        file = ifile.location.to_file
        
        result["TM_SELECTED_FILE"] = file.absolute_path
        result["TM_FILEPATH"] = file.absolute_path
        result["TM_FILENAME"] = file.name
        result["TM_DIRECTORY"] = file.parent_file.absolute_path
      elsif input.respond_to? :getURI
        uri = input.getURI
        if uri.scheme == "file"
          file = java.io.File.new(uri)
          
          result["TM_SELECTED_FILE"] = file.absolute_path
          result["TM_FILEPATH"] = file.absolute_path
          result["TM_FILENAME"] = file.name
          result["TM_DIRECTORY"] = file.parent_file.absolute_path
        end
      end
      # FIXME What if we have a URIEditorInput, we could still fill in some of these values!
      
      if !selection.empty? and selection.text.length > 0
        result["TM_SELECTED_TEXT"] = selection.text
        result["TM_LINE_NUMBER"] = selection.start_line + 1
        result["TM_SELECTION_OFFSET"] = selection.offset
        result["TM_SELECTION_LENGTH"] = selection.length
        result["TM_SELECTION_START_LINE_NUMBER"] = result["TM_LINE_NUMBER"]
        result["TM_SELECTION_END_LINE_NUMBER"] = selection.end_line + 1
        result["TM_INPUT_START_LINE_INDEX"] = caret_column
        result["TM_INPUT_START_COLUMN"] = result['TM_INPUT_START_LINE_INDEX'] + 1
        result["TM_INPUT_START_LINE"] = result["TM_SELECTION_START_LINE_NUMBER"]
      else
        result["TM_LINE_NUMBER"] = caret_line + 1
        result["TM_COLUMN_NUMBER"] = caret_column + 1
      end
      result["TM_LINE_INDEX"] = caret_column
      result["TM_CARET_LINE_NUMBER"] = caret_line + 1
      result["TM_CARET_LINE_TEXT"] = current_line
      result["TM_CARET_OFFSET"] = caret_offset
      result["TM_CURRENT_LINE"] = current_line
      
      # I'm sure there's a better way to extract the word at the current caret position
      if current_line !~ /^\s*$/
        starting_offset = 0
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
      # Allow each bundle to modify env vars based on scope, in order.
      scopes = current_scope.split(' ')
      scopes.each { |scope| result = modify_env(scope, result) }
      
      result
    end
    
    # Default impl returns back unmodified
    def modify_env(scope, env)
      env
    end
    
  end
end
