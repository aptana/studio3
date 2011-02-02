module Ruble
  class SmartTypingPairs < BaseElement
    def initialize(scope, path, pairs)
      super("smart_typing_pairs-#{java.util.UUID.randomUUID().toString()}", path)

      pairs.each {|p| @jobj.addPairCharacter(p) }
      @jobj.setScope(scope)
    end

    private

    def create_java_object
      com.aptana.scripting.model.SmartTypingPairsElement.new(path)
    end

    class << self
      def define_pairs(scope, pairs)
        log_info("loading smart typing pairs contributor #{scope}")

        path = $0
        p = SmartTypingPairs.new(scope, path, pairs)

        # add smart typing pairs modifier to bundle
        bundle = BundleManager.bundle_from_path(path)

        if !bundle.nil?
          bundle.add_child(p)
        else
          log_warning("No bundle found for smart_typing_pairs #{name}: #{path}")
        end
      end
    end
  end
  
  class SmartTypingPairsProxy
    def []=(scope, pairs)
      Ruble::SmartTypingPairs.define_pairs(scope, pairs)
    end
  end
end

def smart_typing_pairs
  Ruble::SmartTypingPairsProxy.new
end
