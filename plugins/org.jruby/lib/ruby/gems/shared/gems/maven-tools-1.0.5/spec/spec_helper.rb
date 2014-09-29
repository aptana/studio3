begin
  require 'minitest'
rescue LoadError
end
require 'minitest/autorun'

$LOAD_PATH.unshift File.join( File.dirname( File.expand_path( __FILE__ ) ),
                              '..', 'lib' )
# due to development dependencies we have a cycle, so remove it
$LOAD_PATH.delete_if { |lp| lp.match /maven-tools-/ }
