# 'require' contributed files that are considered part of the RadRails framework
com.aptana.scripting.ScriptingEngine.instance.framework_files.each do |name|
  require name
end

# TEMP: for debugging
RadRails::Logger.log_level = :trace
