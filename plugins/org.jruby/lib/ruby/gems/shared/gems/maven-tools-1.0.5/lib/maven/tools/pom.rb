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
require 'fileutils'
require 'stringio'
require 'maven/tools/model'
require 'maven/tools/dsl'
require 'maven/tools/visitor'
require 'rubygems/specification'

module Maven
  module Tools
    class POM
      include Maven::Tools::DSL

      def eval_spec( s, snapshot )
        @model = tesla do
          # spec = s, name = nil
          spec s, nil, :snapshot => snapshot, :no_rubygems_repo => true
        end
      end

      def eval_file( file )
        if file && ::File.directory?( file )
          dir = file
          file = nil
        else
          dir = '.'
        end

        unless file
          file = pom_file( 'pom.rb', dir )
          file ||= pom_file( 'Mavenfile', dir )
          file ||= pom_file( 'Gemfile', dir )
          #file ||= pom_file( 'Jarfile', dir )
          file ||= pom_file( '*.gemspec', dir )
        end

        if file
          FileUtils.cd( dir ) do
            @model = to_model( ::File.basename( file ) )
          end
        end
      end

      def initialize( file = nil, snapshot = false )
        if file.is_a? Gem::Specification
          eval_spec( file, snapshot )
        else
          eval_file( file )
        end
      end

      def pom_file( pom, dir = '.' )
        files = Dir[ ::File.join( dir, pom ) ]
        case files.size
        when 0
        when 1
          files.first
        else
          warn 'more than one pom file found'
        end
      end

      def to_s
        if @model
          io = StringIO.new
          v = ::Maven::Tools::Visitor.new( io )
          v.accept_project( @model )
          io.string
        end
      end

      def to_file( file )
        if @model
          v = ::Maven::Tools::Visitor.new( ::File.open( file, 'w' ) )
          v.accept_project( @model )
          true
        end
      end

      def to_model( file )
        if ::File.exists?( file )
          case file
          when /pom.rb/
            eval_pom( "tesla do\n#{ ::File.read( file ) }\nend", file )
          when /(Maven|Gem|Jar)file/
            eval_pom( "tesla do\n#{ ::File.read( file ) }\nend", file )
          when /.+\.gemspec/
            eval_pom( "tesla do\ngemspec( '#{ ::File.basename( file ) }' )\nend", file )
          end
        else
          eval_pom( "tesla do\n#{file}\nend", nil )
        end
      rescue ArgumentError => e
        warn 'fallback to old maven model'
        puts e.message
        puts e.backtrace.join("\n\t")
        raise 'TODO old maven model'
      end
    end
  end
end
