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
begin
  require 'maven/tools/coordinate'
rescue LoadError
  # that gives an load error on jruby-maven-plugin/gem-maven-plugin ITs
end
module Maven
  module Tools
    class Artifact < Hash

      class Helper
        include Coordinate
      end

      def self.new_local( path, type, options = {} )
        name = ::File.basename( path ).sub( /.#{type}$/, '' )
        if ind = name.rindex( '-' )
          version = name[ind + 1..-1]
          name = name[0..ind - 1]
        else
          version = '0'
        end
        self.new( "ruby.maven-tools.#{type}", name, type,
                  nil, version, nil,
                  options.merge( { :system_path => path,
                                   :scope => :system } ) )
      end

      def self.from( type, *args )
        if args.last.is_a? Hash
          options = args.last.dup
          args = args[0..-2]
        end
        helper = Helper.new
        case args.size
        when 1
          # jar "asd:Asd:123
          # jar "asd:Asd:123:test"
          # jar "asd:Asd:123:[dsa:rew,fe:fer]"
          # jar "asd:Asd:123:test:[dsa:rew,fe:fer]"
          group_id, artifact_id, version, classifier, exclusions = args[0].split( /:/ )
          self.new( group_id, artifact_id, type,
                    version, classifier, exclusions,
                    options )
        when 2
          # jar "asd:Asd", 123
          # jar "asd:Asd:test", 123
          # jar "asd:Asd:[dsa:rew,fe:fer]", 123
          # jar "asd:Asd:test:[dsa:rew,fe:fer]", 123
          group_id, artifact_id, classifier, exclusions = args[0].split( /:/ )
          self.new( group_id, artifact_id, type,
                    helper.to_version( args[ 1 ] ),
                    classifier, exclusions,
                    options )
        when 3
          # jar "asd:Asd",'>123', '<345'
          # jar "asd:Asd:test",'>123', '<345'
          # jar "asd:Asd:[dsa:rew,fe:fer]",'>123', '<345'
          # jar "asd:Asd:test:[dsa:rew,fe:fer]",'>123', '<345'
          # jar "asd:Asd:test:[dsa:rew,fe:fer]", '123', 'source'
          if args[ 0 ].match /:/
            v = helper.to_version( *args[1..-1] )         
            case v
            when String
              group_id, artifact_id, classifier, exclusions = args[0].split( /:/ )
              self.new( group_id, artifact_id, type,
                        v, classifier, exclusions,
                        options )
            else
              group_id, artifact_id = args[0].split( /:/ )
              self.new( group_id, artifact_id, type,
                        args[1], args[2], nil,
                        options )
            end
          else
            self.new( args[ 0 ], args[ 1 ], type,
                      args[ 2 ], nil, nil,
                      options )            
          end
        else
          nil
        end
      end

      def self.from_coordinate( coord )
        args = coord.split( /:/ )
        # maven coordinates differ :(
        if args.size == 5
          classifier = args[ 4 ]
          args[ 4 ] = args[ 3 ]
          args[ 3 ] = classifier
        end
        new( *args )
      end

      def initialize( group_id, artifact_id, type,  
                      version = nil, classifier = nil, exclusions = nil,
                      options = {} )
        if exclusions.nil?
          if version.nil? and !classifier.nil?
            version = classifier
            classifier = nil
          elsif classifier.is_a?( Array )
            exclusions = classifier#version
            #version = classifier
            classifier = nil
          end
        end
        self[ :type ] = type
        self[ :group_id ] = group_id
        self[ :artifact_id ] = artifact_id
        self[ :version ] = version
        self[ :classifier ] = classifier if classifier
        self[ :exclusions ] = exclusions if exclusions
        if options
          self[ :group_id ] ||= options[ :group_id ]
          self[ :artifact_id ] ||= options[ :artifact_id ]
          self[ :version ] ||= options[ :version ]
          self[ :classifier ] ||= options[ :classifier ] if options[ :classifier ] 
          self[ :exclusions ] ||= prepare( options[ :exclusions ] ) if options[ :exclusions ]
          options.delete( :group_id )
          options.delete( :artifact_id )
          options.delete( :version )
          options.delete( :classifier )
          options.delete( :exclusions )
          options.delete( :scope ) if options[ :scope ] == :compile
          self.merge!( options )
        end
      end

      def prepare( excl )
        excl.collect do |e|
          case e
          when String
            e
          when Array
            e.join ':'
          else
            raise 'only String and Array allowed'
          end
        end
      end
      private :prepare
      
      ATTRS = :type=, :group_id=, :artifact_id=, :version=, :classifier=, :exclusions=, :scope=
      def method_missing( m, arg = nil )
        if ATTRS.member? m
          # setter
          self[ m.to_s[ 0..-2].to_sym ] = arg
        elsif ATTRS.member?( "#{m}=".to_sym )
          if arg.nil?
            # getter
            self[ m ]
          else
            # setter
            self[ m ] = arg
          end
        else
          super
        end
      end
      def respond_to?( m )
        ATTRS.member? m
      end

      def gav
        [ self[:group_id], self[:artifact_id], self[:version], self[:classifier] ].select { |o| o }.join( ':' )
      end

      def key
        @key ||= [ self[:group_id], self[:artifact_id], self[:classifier] ].select { |o| o }.join( ':' )
      end

      def exclusions
        if key?( :exclusions )
          self[:exclusions].inspect.gsub( /[\[\]" ]/, '' ).split( /,/ )
        end
      end

      def to_coordinate
        [ self[:group_id], self[:artifact_id], self[:type], self[:classifier], self[:version] ].select { |o| o }.join( ':' )
      end

      def to_s
        [ self[:group_id], self[:artifact_id], self[:type], self[:classifier], self[:version], key?( :exclusions )? self[:exclusions].inspect.gsub( /[" ]/, '' ) : nil ].select { |o| o }.join( ':' )
      end
    end
  end
end
