require "java"
require "radrails/bundle_manager"

module RadRails
  
  class Bundle
    def initialize(name)
      if name.kind_of? String
        @jobj = com.aptana.scripting.model.Bundle.new(File.dirname($fullpath))
        @jobj.display_name = name;
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
    
    def display_name
      @jobj.display_name
    end
    
    def display_name=(display_name)
      @jobj.display_name = display_name
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
    
    def to_s
      <<-EOS
      bundle(
        author: #{author}
      )
      EOS
    end
    
    class << self
      def define_bundle(name, &block)
        bundle = Bundle.new(name)
        block.call(bundle) if block_given?
        
        BundleManager.add_bundle(bundle)
      end
    end
  end
  
end
