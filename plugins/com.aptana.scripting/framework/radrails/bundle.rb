require "java"
require "radrails/bundle_manager"

module RadRails
  
  class Bundle
    @@defaults = {}
    
    def initialize(name, default_values={})
      if name.kind_of? String
        @jobj = com.aptana.scripting.model.BundleElement.new($fullpath)
        @jobj.display_name = name
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
    
    def git_repo
      @jobj.git_repo
    end
    
    def git_repo=(git_repo)
      @jobj.git_repo = git_repo
    end
    
    def java_object
      @jobj
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
    
    def path
      @jobj.path
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
        logInfo("loading bundle #{name}")
        
        # create new bundle and add to bundle manager so the block, if given
        # can lookup the bundle by path name
        bundle = Bundle.new(name, values)
        BundleManager.add_bundle(bundle)
        block.call(bundle) if block_given?
      end
    end
  end
  
end
