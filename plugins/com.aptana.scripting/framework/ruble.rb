require 'java'
# 'require' contributed files that are considered part of the Ruble framework
com.aptana.scripting.ScriptingEngine.instance.framework_files.each do |name|
  require name
end

# used for debugging
#Ruble::Logger.log_level = :trace
