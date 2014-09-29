# -*- encoding: utf-8 -*-
# stub: jruby-openssl 0.9.5 java lib

Gem::Specification.new do |s|
  s.name = "jruby-openssl"
  s.version = "0.9.5"
  s.platform = "java"

  s.required_rubygems_version = Gem::Requirement.new(">= 0") if s.respond_to? :required_rubygems_version=
  s.authors = ["Ola Bini", "JRuby contributors"]
  s.date = "2014-06-24"
  s.description = "JRuby-OpenSSL is an add-on gem for JRuby that emulates the Ruby OpenSSL native library."
  s.email = "ola.bini@gmail.com"
  s.files = ["License.txt", "Rakefile", "README.txt", "Mavenfile", "History.txt", "lib/jruby-openssl.rb", "lib/openssl.rb", "lib/jopenssl.jar", "lib/jopenssl19/openssl.rb", "lib/jopenssl19/openssl/bn.rb", "lib/jopenssl19/openssl/x509.rb", "lib/jopenssl19/openssl/cipher.rb", "lib/jopenssl19/openssl/ssl.rb", "lib/jopenssl19/openssl/config.rb", "lib/jopenssl19/openssl/digest.rb", "lib/jopenssl19/openssl/ssl-internal.rb", "lib/jopenssl19/openssl/buffering.rb", "lib/jopenssl19/openssl/x509-internal.rb", "lib/openssl/pkcs7.rb", "lib/openssl/bn.rb", "lib/openssl/x509.rb", "lib/openssl/pkcs12.rb", "lib/openssl/cipher.rb", "lib/openssl/ssl.rb", "lib/openssl/config.rb", "lib/openssl/digest.rb", "lib/openssl/ssl-internal.rb", "lib/openssl/buffering.rb", "lib/openssl/x509-internal.rb", "lib/jopenssl21/openssl.rb", "lib/jopenssl21/openssl/bn.rb", "lib/jopenssl21/openssl/x509.rb", "lib/jopenssl21/openssl/cipher.rb", "lib/jopenssl21/openssl/ssl.rb", "lib/jopenssl21/openssl/config.rb", "lib/jopenssl21/openssl/digest.rb", "lib/jopenssl21/openssl/buffering.rb", "lib/jopenssl18/openssl.rb", "lib/jopenssl18/openssl/pkcs7.rb", "lib/jopenssl18/openssl/bn.rb", "lib/jopenssl18/openssl/x509.rb", "lib/jopenssl18/openssl/cipher.rb", "lib/jopenssl18/openssl/ssl.rb", "lib/jopenssl18/openssl/config.rb", "lib/jopenssl18/openssl/digest.rb", "lib/jopenssl18/openssl/ssl-internal.rb", "lib/jopenssl18/openssl/buffering.rb", "lib/jopenssl18/openssl/x509-internal.rb", "lib/org/bouncycastle/bcpkix-jdk15on/1.47/bcpkix-jdk15on-1.47.jar", "lib/org/bouncycastle/bcprov-jdk15on/1.47/bcprov-jdk15on-1.47.jar", "lib/jopenssl/version.rb", "lib/jopenssl/load.rb"]
  s.homepage = "https://github.com/jruby/jruby"
  s.require_paths = ["lib"]
  s.requirements = ["jar org.bouncycastle:bcpkix-jdk15on, 1.47", "jar org.bouncycastle:bcprov-jdk15on, 1.47"]
  s.rubyforge_project = "jruby/jruby"
  s.rubygems_version = "2.1.9"
  s.summary = "JRuby OpenSSL"
end
