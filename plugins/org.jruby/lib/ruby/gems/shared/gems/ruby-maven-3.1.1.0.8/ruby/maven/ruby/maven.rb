#
# Copyright (C) 2013 Christian Meier
#
# Permission is hereby granted, free of charge, to any person obtaining a copy of
# this software and associated documentation files (the "Software"), to deal in
# the Software without restriction, including without limitation the rights to
# use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
# the Software, and to permit persons to whom the Software is furnished to do so,
# subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in all
# copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
# FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
# COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
# IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
# CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
#
unless defined? JRUBY_VERSION
  require 'maven/tools/model'
end
require 'maven/tools/visitor'
require 'fileutils'
require 'tesla_maven'

module Maven
  module Ruby
    class Maven

      attr_accessor :embedded

      private

      def launch_jruby(args)
        java.lang.System.setProperty( "classworlds.conf",
                                      TeslaMaven.bin( "m2jruby.conf" ) )

        java.lang.System.setProperty( 'maven.home', TeslaMaven.maven_home )
        java.lang.System.setProperty( 'tesla.home', TeslaMaven.home )

        # loading here is needed to let tesla-ruby find jruby on
        # classloader
        self.class.require_classpath( TeslaMaven.maven_boot )
        self.class.require_classpath( TeslaMaven.maven_lib )
        self.class.require_classpath( TeslaMaven.lib )

        # this is a hack to ensure ruby-maven also works inside the
        # jruby development itself where the lib/jruby.jar gets build
        # during the execution of maven and the jar used to launch jruby
        # will be replaced.

        jar = 'lib/jruby.jar'
        jar_copy = 'lib/jruby-copy.jar'
        if File.exists? jar
          FileUtils.mv( jar, jar_copy )
        end

        org.codehaus.plexus.classworlds.launcher.Launcher.main_with_exit_code( args )
      ensure
        if File.exists? jar_copy
          FileUtils.mv( jar_copy, jar )
        end
      end

      def launch_java(args)
        # TODO works only on unix like OS
        system "java -cp #{self.class.classpath( TeslaMaven.maven_boot )} -Dmaven.home=#{TeslaMaven.maven_home} -Dtesla.home=#{TeslaMaven.home} -Dclassworlds.conf=#{TeslaMaven.bin( 'm2.conf' )} org.codehaus.plexus.classworlds.launcher.Launcher #{args.join ' '}"
      end

      def self.classpath_array( dir )
        Dir.glob( File.join( dir, "*jar" ) )
      end

      def self.classpath( dir )
        classpath_array( dir ).join( File::PATH_SEPARATOR )
      end

      def self.require_classpath( dir )
        classpath_array( dir ).each do |jar|
          require jar
        end
      end

      def options_array
        options.collect do |k,v|
          if k =~ /^-D/
            v = "=#{v}" unless v.nil?
            "#{k}#{v}"
          else
            if v.nil?
              "#{k}"
            else
              ["#{k}", "#{v}"]
            end
          end
        end.flatten
      end

      public

      def initialize( project = nil, temp_pom = nil )
        super()
        if project
          f = File.expand_path( temp_pom || '.pom.xml' )
          v = ::Maven::Tools::Visitor.new( File.open( f, 'w' ) )
          # parse project and write out to temp_pom file
          v.accept_project( project )
          # tell maven to use the generated file
          options[ '-f' ] = f
          @embedded = true
        end
      end

      def options
        @options ||= {}
      end

      def verbose= v
        @verbose = v
      end

      def property(key, value = nil)
        options["-D#{key}"] = value
      end

      def verbose
        if @verbose.nil?
          @verbose = options.delete('-Dverbose').to_s == 'true'
        else
          @verbose
        end
      end

      def exec(*args)
        a = args.dup + options_array
        if a.delete( '-Dverbose=true' ) || a.delete( '-Dverbose' ) || verbose
          puts "mvn #{a.join(' ')}"
        end   
        if defined? Bundler
          # it can be switching from ruby to jruby with invoking maven
          # just keep it clean
          Bundler.with_clean_env do
            launch( a )
          end
        else
          launch( a )
        end
      end

      def launch( args )
        if defined? JRUBY_VERSION
          puts "using jruby #{JRUBY_VERSION} invokation" if verbose
          result = launch_jruby( args )
        else
          puts "using java invokation" if verbose
          result = launch_java( args )
        end
        if @embedded and not result
          raise "error in executing maven #{result}"
        else
          result
        end
      end

      def method_missing( method, *args )
        method = method.to_s.gsub( /_/, '-' ).to_sym
        exec( *([ method ] + args) )
      end
    end
  end
end
