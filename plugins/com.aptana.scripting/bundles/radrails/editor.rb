require "java"
require "radrails/ui"

module RadRails
  class Editor
    class << self
      
      # Opens the editor on a given file. Path should be absolute
      def open(absolute_path)
        editor = nil
        job = UIJob.new("Opening editor") do          
          page = RadRails::UI.active_page
          return nil unless page
          ipath = org.eclipse.core.runtime.Path.new(absolute_path)
          ifile = org.eclipse.core.resources.ResourcesPlugin.workspace.root.getFileForLocation(ipath)
          desc = org.eclipse.ui.ide.IDE.getDefaultEditor(ifile)
          editor = RadRails::Editor.new(org.eclipse.ui.ide.IDE.openEditor(page, java.net.URI.create("file:#{absolute_path}"), desc.getId, true))
        # TODO Have user pass in a URI so we can open files over SFTP/FTP/S3/etc using the filesystem API!
        end
        job.schedule
        job.join
        return editor
      end
    end
    
    def initialize(editor_part)
      @editor_part = editor_part
    end
    
    # Return the wrapped IEditorPart from Java
    def editor_part
      @editor_part
    end
    
    def close(save = true)     
      closed = false 
      job = UIJob.new("Close Editor") do |monitor|
        closed = RadRails::UI.active_page.close_editor(editor_part, save)
      end
      job.schedule
      job.join
      closed
    end
    
    def close!
      close(false)
    end
    
    def save(confirm = true)
      saved = false
      job = UIJob.new("Save Editor") do |monitor|
        saved = RadRails::UI.active_page.save_editor(editor_part, confirm)
      end
      job.schedule
      job.join
      saved
    end
    
    def save!
      save(false)
    end
    
    def dirty?
      editor_part.dirty?
    end
    
    def hide
      job = UIJob.new("Save Editor") do |monitor|
        RadRails::UI.active_page.hide_editor(editor_reference)
      end
      job.schedule
      job.join
    end
    
    def show
    end
    
    def editor_reference
      RadRails::UI.active_page.editor_references.find {|ref| ref.getEditor(false) == editor_part }
    end
    
  end
end
