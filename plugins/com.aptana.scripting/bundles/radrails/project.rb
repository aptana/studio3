require "java"

# FIXME For some reason when the file is executed the second time (after a save) it hangs on the first job execution!
module RadRails
  # Wraps an Eclipse Job and allows us to use a block to implement the one abstract method we need
  class RubyJob < org.eclipse.core.runtime.jobs.Job
    def initialize(name, &blk)
      super(name)
      @block = blk
    end
    
    def run(monitor)
      @block.call(monitor)
      return org.eclipse.core.runtime.Status::OK_STATUS
    end
  end

  # Wraps the eclipse IProject
  class Project
  
    def initialize(iproject)
      @project = iproject
    end
    class << self
      # Find the named project in the workspace
      def find(name)
        RadRails::Project.new(org.eclipse.core.resources.ResourcesPlugin.workspace.root.project(name))
      end
      
      # Return all projects in an array
      def all
        projects = []
        org.eclipse.core.resources.ResourcesPlugin.workspace.root.projects.each {|p| projects << RadRails::Project.new(p) }
        projects
      end
      
      # Create a new project with the given name
      def create(name, options = {})
        return_proj = find(name)
        return return_proj if return_proj.exists?
        # FIXME Allow setting a non-standard location or default set of nature Ids using the options hash!
        job = RubyJob.new("Create project") {|monitor| find(name).project.create(monitor) }
        job.schedule
        job.join
        return_proj
      end
      
      # Returns the "current"/"active" project highlighted in the App Explorer
      def current
        preferences_service = org.eclipse.core.runtime.Platform.preferences_service
        qualifier = com.aptana.explorer.ExplorerPlugin::PLUGIN_ID
        key = com.aptana.explorer.IPreferenceConstants::ACTIVE_PROJECT
        current_project_name = preferences_service.getString(qualifier, key, nil, nil)
        return nil if current_project_name.nil?
        find(current_project_name)
      end
    end
    
    # Return the underlying java IProject
    def project
      @project
    end
    
    def name
      project.name
    end
    
    def exists?
      project.exists?
    end
    
    # Return an array of String nature IDs
    def natures
      natures = []
      project.description.nature_ids.each {|n| natures << n }
      natures
    end
    
    # Add a new nature to the project. +nature_id+ is a String 
    def add_nature(nature_id)
      return unless project.exists?
      job = RubyJob.new("Add Nature to project") do |monitor| 
        description = project.description
        new_natures = natures + [nature_id]
        description.nature_ids = new_natures.to_java(:string)
        project.setDescription(description, monitor)
      end
      job.schedule
      job.join 
    end
    
    # Query to see if a project has a particular nature
    def has_nature?(nature_id)
      exists? and is_open? and project.has_nature?(nature_id.to_s)
    end
    
    # Converts the project to a Dir object so you can query it's file listing
    def to_dir
      Dir.new(project.location.toOSString)
    end

    # Forces a refresh of the project. Pass in true to force only a shallow refresh of the project and direct members
    def refresh(shallow = false)
      to_dir.refresh(shallow)
    end
    
    def is_open?
      project.is_open?
    end
    
    def open
      return if is_open?
      job = RubyJob.new("Open project") {|monitor| project.open(monitor) }
      job.schedule
      job.join
    end
    
    def is_closed?
      !is_open?
    end
    
    def close
      return if is_closed?
      job = RubyJob.new("Close project") {|monitor| project.close(monitor) }
      job.schedule
      job.join
    end
    
    def delete
      return if !exists?
      job = RubyJob.new("Delete project") {|monitor| project.delete(true, true, monitor) }
      job.schedule
      job.join
    end
    
    def make_current
      scope = org.eclipse.core.runtime.preferences.InstanceScope.new
      prefs = scope.getNode(com.aptana.explorer.ExplorerPlugin::PLUGIN_ID)
      prefs.put(com.aptana.explorer.IPreferenceConstants::ACTIVE_PROJECT, name)
      prefs.flush
    end
   
    def add_listener(recursive = true, &blk)
      com.aptana.scripting.FileChangeNotifier.add_listener(project.location.toOSString, recursive, &blk)
    end
  end
end

class Dir
  # Forces a refresh of the project. Pass in true to force only a shallow refresh of the project and direct members
  def refresh(shallow = false)
    depth = shallow ? org.eclipse.core.resources.IResource::DEPTH_ONE : org.eclipse.core.resources.IResource::DEPTH_INFINITE
    job = RubyJob.new("Refresh Directory") {|monitor| resource.refresh_local(depth, monitor) }
    job.schedule
    job.join
  end
  
  # Grabs the IResource (IContainer) that Eclipse uses that we then operate on
  def resource
    ipath = org.eclipse.core.runtime.Path.new(path)
    org.eclipse.core.resources.ResourcesPlugin.workspace.root.getContainerForLocation(ipath)
  end
end

class File

  # Forces a refresh of the file
  def refresh
    depth = org.eclipse.core.resources.IResource::DEPTH_ZERO
    job = RubyJob.new("Refresh File") {|monitor| resource.refresh_local(depth, monitor) }
    job.schedule
    job.join
  end
  
  # Grabs the IResource (IFile) that Eclipse uses that we then operate on
  def resource
    ipath = org.eclipse.core.runtime.Path.new(path)
    org.eclipse.core.resources.ResourcesPlugin.workspace.root.getFileForLocation(ipath)
  end
end

# Re-open the event class
class com.aptana.scripting.FileChangeNotifier::FileModificationEvent
  alias :old_type :type
  # Coerce the java integer type into a symbol to make it more ruby-like
  def type
    case old_type
    when CREATED
      :created
    when DELETED
      :deleted
    when RENAMED
      :renamed
    else
      :modified
    end
  end
end
