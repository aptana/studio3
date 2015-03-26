#
# Copyright (C) 2014 Christian Meier
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
require 'maven/tools/dsl/options'
require 'maven/tools/dsl/models'
module Maven
  module Tools
    module DSL
      class RepositoryDSL
        extend Options
        include Models

        def self.create( parent, *args, &block )
          
        end
      end
      class JRubyDSL
        extend Options

        def self.create( parent, scope, *args, &block )
          args, options = args_and_options( *args )
          j = JRubyDSL.new( parent, scope, &block )
          j.version args[0]
          j.no_asm args[1]
          fill_options( j, options || {} )
          j.apply
          j
        end

        def initialize( parent, scope, &block )
          @parent = parent 
          @data = {}
          self.scope scope
          self.instance_eval( &block ) if block
        end

        def dependency( type, artifact_id )
          dep = Dependency.new
          dep.type = type
          dep.group_id = 'org.jruby'
          dep.artifact_id = artifact_id
          dep.version = version
          dep.scope = scope
          @parent.dependencies << dep
        end
        private :dependency

        def apply
          if version.nil?
            # nothing to do
          elsif( version < '1.6' )
            raise 'jruby before 1.6 are not supported'
          elsif ( version < '1.7' )
            warn 'jruby version below 1.7 uses jruby-complete'
            dependency :jar, 'jruby-complete'
          elsif ( version.sub( /1\.7\./, '').to_i < 5 )
            dependency :jar, 'jruby-core'
          elsif no_asm
            dependency :pom, 'jruby-noasm'
          else
            dependency :pom, 'jruby-noasm'
          end
        end

        def legacy_version
          v = version
          v+= '-no_asm' if no_asm
          v
        end

        def help
          warn self.class.help( 'jruby', :scope, :version, :no_asm => true, :jar => nil ) + <<EOS
argument: jruby 'version'
arguments: jruby 'version', true
EOS
        end

        def jar( *args, &block )
          DependencyDSL.create( @parent, :jar, scope, *args, &block )
        end

        [ :scope, :version, :no_asm ].each do |meth|
          define_method( meth ) do |arg = nil|
            @data[ meth ] = arg if arg
            @data[ meth ] 
          end
          define_method( "#{meth}=" ) do |arg|
            @data[ meth ] = arg
          end
        end
      end
    end
  end
end
