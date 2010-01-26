require "java"
require "radrails/bundle_manager"

module RadRails
  
  class Logger
    class << self
      def log_error(error)
        com.aptana.scripting.ScriptLogger.logError(error.to_s)
      end
      
      def log_level
        com.aptana.scripting.ScriptLogger.instance.logLevel.name.to_sym
      end
      
      def log_level=(level)
        com.aptana.scripting.ScriptLogger.instance.logLevel = level.to_s
      end
      
      def log_info(info)
        com.aptana.scripting.ScriptLogger.logInfo(info.to_s)
      end
      
      def log_warning(warning)
        com.aptana.scripting.ScriptLogger.logWarning(warning.to_s)
      end
      
      def trace(message)
        com.aptana.scripting.ScriptLogger.trace(message.to_s)
      end
    end
  end
  
end

# define top-level convenience methods

def log_error(error)
  RadRails::Logger.log_error(error)
end

def log_info(info)
  RadRails::Logger.log_info(info)
end

def log_warning(warning)
  RadRails::Logger.log_warning(warning)
end

def trace(message)
  RadRails::Logger.trace(message)
end
