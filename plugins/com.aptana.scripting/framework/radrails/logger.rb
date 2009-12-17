require "java"
require "radrails/bundle_manager"

module RadRails
  
  class Logger
    class << self
      def logError(error)
        com.aptana.scripting.ScriptLogger.logError(error)
      end
      
      def logInfo(info)
        com.aptana.scripting.ScriptLogger.logInfo(info)
      end
      
      def logWarning(warning)
        com.aptana.scripting.ScriptLogger.logWarning(warning)
      end
      
      def trace(message)
        com.aptana.scripting.ScriptLogger.trace(message)
      end
    end
  end
  
end
