#-*- mode: ruby -*-

Gem::Specification.new do |s|
  s.name = 'bouncy-castle-java'
  s.version = "1.5.1"
  s.author = 'Hiroshi Nakamura'
  s.email = [ 'nahi@ruby-lang.org' ]
  s.rubyforge_project = "jruby-extras"
  s.homepage = 'http://github.com/jruby/jruby/tree/master/gems/bouncy-castle-java/'
  s.summary = 'Gem redistribution of Bouncy Castle jars'
  s.description = 'Gem redistribution of "Legion of the Bouncy Castle Java cryptography APIs" jars at http://www.bouncycastle.org/java.html'
  s.files = ['README', 'LICENSE.html', 'lib/bouncy-castle-java.rb' ] + Dir['lib/bc*.jar' ]  

  s.add_development_dependency 'minitest', '~> 4.4'
end

# vim: syntax=Ruby
