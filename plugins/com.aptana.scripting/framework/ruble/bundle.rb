require "java"
require "ruble/base_element"
require "ruble/bundle_manager"

module Ruble
  
  class Bundle < BaseElement
    @@defaults = {}
    
    def initialize(name, default_values={})
      if name.kind_of? String
        super(name)
        @@defaults[path.to_sym] = default_values
      else
        # hack to pass in java object...should test type
        @jobj = name
      end
    end
    
    def add_command(command)
      @jobj.add_command command.java_object
    end
    
    def add_menu(menu)
      @jobj.add_menu menu.java_object
    end
    
    def add_snippet(snippet)
      @jobj.add_snippet snippet.java_object
    end
    
    def apply_defaults(obj)
      if obj.nil? == false
        defaults.each do |k,v|
          property = "#{k.to_s}=".to_sym
          
          if obj.respond_to?(property)
            obj.send(property, v)
          end
        end
      end
    end
    
    def author
      @jobj.author
    end
    
    def author=(author)
      @jobj.author = author
    end
    
    def copyright
      @jobj.copyright
    end
    
    def copyright=(copyright)
      @jobj.copyright = copyright
    end
    
    def defaults
      @@defaults[path.to_sym] ||= {}
    end
    
    def defaults=(defaults)
      @@defaults[path.to_sym] = defaults
    end
    
    def description
      @jobj.description
    end
    
    def description=(description)
      @jobj.description = description
    end
    
    def menu(name, &block)
      Menu.define_menu(name, &block)
    end
    
    def name
      @jobj.display_name
    end
    
    def name=(name)
      @jobj.display_name = name
    end
    
    def repository
      @jobj.repository
    end
    
    def repository=(repository)
      @jobj.repository = repository
    end
    
    def license
      @jobj.license
    end
    
    def license=(license)
      @jobj.license = license.join("\n")
    end
    
    def license_url
      @jobj.license_url
    end
    
    def license_url=(license_url)
      @jobj.license_url = license_url.join("\n")
    end
    
    def register_file_type(file_type, scope, options = {})
      @jobj.registerFileType(file_type, scope.to_s.gsub(/_/, '.'))
    end
    
    def to_env
      { :TM_BUNDLE_SUPPORT => File.join(File.dirname(path), "lib") }
    end
    
    def to_s
      <<-EOS
      bundle(
        author: #{author}
      )
      EOS
    end
    
    class << self
      def define_bundle(name, values, &block)
        log_info("loading bundle #{name}")
        
        # create new bundle and add to bundle manager so the block, if given
        # can lookup the bundle by path name
        bundle = Bundle.new(name, values)
        BundleManager.add_bundle(bundle)
        block.call(bundle) if block_given?
      end
    end
  
  private
    
    def create_java_object
      com.aptana.scripting.model.BundleElement.new($fullpath)
    end
  end
  
end

# define top-level convenience methods

def bundle(name, &block)
  Ruble::Bundle.define_bundle(name, {}, &block)
end

def with_defaults(values, &block)
  bundle = Ruble::BundleManager.bundle_from_path(File.dirname($fullpath))
  
  if bundle.nil?
    bundle = Ruble::Bundle.define_bundle("", values, &block)
  else
    bundle.defaults = values
    block.call(bundle) if block_given?
    bundle.defaults = {}
  end
end

module Ruble
  class << self
    def current_bundle(&block)
      with_defaults({}, &block)
    end
  end
end
