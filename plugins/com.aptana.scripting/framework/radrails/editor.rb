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
          editor = RadRails::Editor.new(org.eclipse.ui.ide.IDE.openEditor(page, java.net.URI.create(uri_string), desc.getId, true))
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
      def go_to(options = {})
        default_line = options.has_key?(:file) ? 1 : ENV['TM_LINE_NUMBER']
        options = {:file => ENV['TM_FILEPATH'], :line => default_line, :column => 1}.merge(options)
        editor = nil
        if options[:file]
          editor = RadRails::Editor.open("file://#{options[:file]}")
        else
          editor = RadRails::Editor.active
        end
        return unless editor        
        region = editor.document.getLineInformation(options[:line].to_i - 1)
        editor.selection = [region.offset + options[:column].to_i - 1, 0]
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
  end
end
