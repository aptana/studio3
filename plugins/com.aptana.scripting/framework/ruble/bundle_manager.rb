require "java"

module Ruble
  
  class BundleManager
    def self.manager
      @jobj ||= com.aptana.scripting.model.BundleManager.instance;
    end
    
    def self.add_bundle(bundle)
      manager.add_bundle(bundle.java_object)
    end
    
    def self.bundle_from_path(path)
      # try current directory
      test_path = (File.directory? path) ? path : File.dirname(path)
      bundle = manager.getBundleFromPath(test_path)
      
      # else try parent (assuming we're a snippet or command)
      if bundle.nil?
        test_path = File.dirname test_path
        bundle = manager.getBundleFromPath(test_path)
      end
      
      return (bundle.nil?) ? nil : Bundle.new(bundle)
    end
  end
  
end