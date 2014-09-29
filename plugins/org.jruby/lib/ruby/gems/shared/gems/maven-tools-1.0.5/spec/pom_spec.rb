require File.expand_path( 'spec_helper', File.dirname( __FILE__ ) )
require 'maven/tools/pom'

describe Maven::Tools::POM do

  ( Dir[ File.join( File.dirname( __FILE__ ), 'gem*' ) ] + Dir[ File.join( File.dirname( __FILE__ ), 'pom*' ) ] + Dir[ File.join( File.dirname( __FILE__ ), 'mavenfile*' ) ] ).each do |dir|
    if File.directory?( dir )
      it "should convert #{dir}" do
        pom = Maven::Tools::POM.new( dir )
        file = File.join( dir, 'pom.xml' )
        file = File.join( File.dirname( dir ), 'pom.xml' ) unless File.exists? file
        pom_xml = File.read( file )
        pom_xml.sub!( /<!--(.|\n)*-->\n/, '' )
        pom_xml.sub!( /<?.*?>\n/, '' )
        pom_xml.sub!( /<project([^>]|\n)*>/, '<project>' )

        pom.to_s.gsub( /#{File.expand_path( dir )}\//, '' ).must_equal pom_xml
      end
    end
  end

end
