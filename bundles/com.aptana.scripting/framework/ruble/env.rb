module Ruble
  class Env < BaseElement
    def initialize(scope, path, &block)
      super("environment-#{java.util.UUID.randomUUID().toString()}", path)

      @jobj.set_invoke_block(&block)
      @jobj.setScope(scope)
    end

    private

    def create_java_object
      com.aptana.scripting.model.EnvironmentElement.new(path)
    end

    class << self
      def define_variables(scope, &block)
        log_info("loading env variable contributor #{scope}")

        path = $0
        path = block.binding.eval("__FILE__") if block
        e = Env.new(scope, path, &block)

        # add env modifier to bundle
        bundle = BundleManager.bundle_from_path(e.path)

        if !bundle.nil?
          bundle.add_child(e)
        else
          log_warning("No bundle found for env #{name}: #{e.path}")
        end
      end
    end
  end
end

def env(scope, &block)
  Ruble::Env.define_variables(scope, &block)
end
