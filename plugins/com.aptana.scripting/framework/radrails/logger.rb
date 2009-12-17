require "java"
require "radrails/bundle_manager"

module RadRails
  
  class Logger
    class << self
      def log_error(error)
        com.aptana.scripting.ScriptLogger.logError(error)
      end
      
      def log_level
        com.aptana.scripting.ScriptLogger.instance.logLevel.name.to_sym
      end
      
      def log_level=(level)
        com.aptana.scripting.ScriptLogger.instance.logLevel = level.to_s
      end
      
      def log_info(info)
        com.aptana.scripting.ScriptLogger.logInfo(info)
      end
      
      def log_warning(warning)
        com.aptana.scripting.ScriptLogger.logWarning(warning)
      end
      
      def trace(message)
        com.aptana.scripting.ScriptLogger.trace(message)
      end
    end
  end
  
end
