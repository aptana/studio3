module Ruble
  class Env < BaseElement
    def initialize(scope, &block)
      super("snippet-#{java.util.UUID.randomUUID().toString()}")

      @jobj.set_invoke_block(&block)
      @jobj.setScope(scope)
    end

    private

    def create_java_object
      com.aptana.scripting.model.EnvironmentElement.new($fullpath)
    end

    class << self
      def define_variables(scope, &block)
        log_info("loading env variable contributor #{scope}")

        e = Env.new(scope, &block)

        # add env modifier to bundle
        bundle = BundleManager.bundle_from_path(e.path)

        if !bundle.nil?
          bundle.add_env(e)
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
