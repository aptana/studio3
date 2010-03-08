require "java"

module Ruble
  class BundleManager
    @@bundles_by_path = {}
    
    class << self
      def manager
        @jobj ||= com.aptana.scripting.model.BundleManager.instance;
      end

      def add_bundle(bundle)
        java_object = bundle.java_object
        
        # add to Java bundle manager
        manager.add_bundle(bundle.java_object)
        
        # use the instance on the Java side now
        path = java_object.bundle_directory.absolute_path
        @@bundles_by_path.delete(path)
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
        
        # else try BundleManager (can happen when reloading existing
        # commands/snippets if we're in a different runtime
        if bundle.nil?
          test_path = (File.directory? path) ? path : File.dirname(path)
          java_bundle = manager.get_bundle_from_path(test_path)
          
          # else try parent
          if java_bundle.nil?
            test_path = File.dirname test_path
            java_bundle = manager.get_bundle_from_path(test_path)
          end
          
          bundle = Bundle.new(java_bundle) unless java_bundle.nil?
        end
        
        return bundle
      end
      
      def reference_bundle(bundle)
        path = bundle.java_object.bundle_directory.absolute_path
        @@bundles_by_path[path] = bundle
      end
    end
  end
end