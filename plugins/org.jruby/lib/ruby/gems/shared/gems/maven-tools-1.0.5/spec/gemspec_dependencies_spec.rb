require File.expand_path( 'spec_helper', File.dirname( __FILE__ ) )
require 'maven/tools/gemspec_dependencies'

describe Maven::Tools::GemspecDependencies do

  let( :spec ) do
    Gem::Specification.new do |s|
      s.add_dependency 'thor', '>= 0.14.6', '< 2.0'
      s.add_dependency 'maven-tools', "~> 0.32.3" 
      s.add_development_dependency 'minitest', '~> 5.0'  
      s.add_development_dependency 'rake', '~> 10.0'
      s.requirements << 'jar sdas:das:tes, 123'
      s.requirements << 'jar sdas:das, 123'
      s.requirements << 'jar sdas.asd:das, 123, [fds:fre]'
      s.requirements << 'jar sdas.asd:das:bla, 123,[fds:fre, ferf:de]'
      s.requirements << 'jar sdas.asd:das, blub, 123,[fds:fre, ferf:de]'
      s.requirements << 'jar "de.sdas:das:tes",123'
      s.requirements << 'jar de.sdas:das, "123"'
      s.requirements << 'jar "de.sdas.asd:das", 123, ["fds:fre"]'
      s.requirements << "jar 'de.sdas.asd:das:bla', '123',['fds:fre', 'ferf:de']"
      s.requirements << 'jar "de.sdas.asd:das", "blub", 123,"[fds:fre, ferf:de]"'
    end
  end
  
  subject { Maven::Tools::GemspecDependencies.new( spec ) } 

  it 'should setup artifact' do
    subject.runtime.must_equal ["rubygems:thor:[0.14.6,2.0)", "rubygems:maven-tools:[0.32.3,0.32.99999]"]
    subject.development.must_equal ["rubygems:minitest:[5.0,5.99999]", "rubygems:rake:[10.0,10.99999]"]
    subject.java_runtime.must_equal [ ["sdas", "das", "jar", "tes", "123"],
                           ["sdas", "das", "jar", "123"],
                           ["sdas.asd", "das", "jar", "123", ["fds:fre"]],
                           ["sdas.asd", "das", "jar", "bla", "123", ["fds:fre", "ferf:de"]],
                           ["sdas.asd", "das", "jar", "blub", "123", ["fds:fre", "ferf:de"]],
                           ["de.sdas", "das", "jar", "tes", "123"],
                           ["de.sdas", "das", "jar", "123"],
                           ["de.sdas.asd", "das", "jar", "123", ["fds:fre"]],
                           ["de.sdas.asd", "das", "jar", "bla", "123", ["fds:fre", "ferf:de"]],
                           ["de.sdas.asd", "das", "jar", "blub", "123", ["fds:fre","ferf:de"]] ]
    subject.java_dependencies.must_equal [ [:compile, "sdas", "das", "jar", "tes", "123"],
                           [:compile, "sdas", "das", "jar", "123"],
                           [:compile, "sdas.asd", "das", "jar", "123", ["fds:fre"]],
                           [:compile, "sdas.asd", "das", "jar", "bla", "123", ["fds:fre", "ferf:de"]],
                           [:compile, "sdas.asd", "das", "jar", "blub", "123", ["fds:fre", "ferf:de"]],
                           [:compile, "de.sdas", "das", "jar", "tes", "123"],
                           [:compile, "de.sdas", "das", "jar", "123"],
                           [:compile, "de.sdas.asd", "das", "jar", "123", ["fds:fre"]],
                           [:compile, "de.sdas.asd", "das", "jar", "bla", "123", ["fds:fre", "ferf:de"]],
                           [:compile, "de.sdas.asd", "das", "jar", "blub", "123", ["fds:fre","ferf:de"]] ]
  end
end
