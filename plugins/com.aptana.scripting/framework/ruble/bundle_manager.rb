require "java"

module Ruble
  class BundleManager
    @@bundles_by_path = {}
    
    class << self
      def manager
        @jobj ||= com.aptana.scripting.model.BundleManager.instance;
      end

      def add_bundle(bundle)
        manager.add_bundle(bundle.java_object)
      end
      
      def bundle_from_path(path)
        # try current directory
        test_path = (File.directory? path) ? path : File.dirname(path)
        bundle = @@bundles_by_path[test_path]

        # else try parent (assuming we're a snippet or command)
        if bundle.nil?
          test_path = File.dirname test_path
          bundle = @@bundles_by_path[test_path]
        end
        
        return (bundle.nil?) ? nil : bundle
      end
      
      def reference_bundle(bundle)
        path = bundle.java_object.bundle_directory.absolute_path
        @@bundles_by_path[path] = bundle
      end
    end
  end
end