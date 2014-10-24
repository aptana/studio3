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
module Maven
  module Tools
    module Coordinate
      def to_split_coordinate_with_scope( line )
        line = line.sub( /#.*^/, '' )
        scope = :compile
        line.sub!( /,\s+:scope\s+=>\s(:provided|:runtime|:compile|:test)/ ) do |part|
          scope = part.sub( /.*:/, '' ).to_sym
          ''
        end
	coord = to_split_coordinate( line ) 
        [ scope ] + coord if coord
      end

      def to_split_coordinate( line )
        if line =~ /^\s*(jar|pom)\s/
          packaging = line.strip.sub(/\s+.*/, '')

          # Remove packaging, comments and whitespaces
          sanitized_line = line.sub(/\s*[a-z]+\s+/, '').sub(/#.*/,'').gsub(/\s+/,'')

          exclusions = nil
          sanitized_line.gsub!( /[,:](\[.+:.+\]|'\[.+:.+\]'|"\[.+:.+\]")/ ) do |match|
            exclusions = match.gsub( /['"]/, '' )[2..-2].split( /,\s*/ )
            nil
          end

          # split to compartments
          parts = sanitized_line.split( /[,]/ ).collect{|o| o.gsub( /['"]/, '' ) }
          # fix no version on one argument
          if parts.size == 1
            parts << '[0,)'
          end

          # split first argument
          parts[ 0 ] = parts[ 0 ].split( /:/ )
          parts.flatten!
         
          # convert ruby version to maven version
          versions = parts.select { |i| i.match( /[~><=!]/ ) }
          if ! versions.empty?
            version = to_version( *versions )
            parts = parts - versions
            parts << version
          else
            # concat maven version ranges
            versions = parts.select { |i| i.match( /[\[\]()]/ ) }
            if ! versions.empty?
              version = versions.join( ',' )
              parts = parts - versions
              parts << version
            end
          end
          
          # insert packing and exclusion
          parts.insert( 2, packaging )
          parts << exclusions
                       
          # make sure there are no nils
          parts.compact
        end
      end

      def to_coordinate( line )
        result = to_split_coordinate( line )
        if result
          exclusion = result.last.inspect.gsub( /[" ]/, '' )
          ( result[0..-2] + [ exclusion ] ).join( ':' )
        end
      end
      
      def group_artifact(*args)
        case args.size
        when 1
          name = args[0]
          if name =~ /:/
            [name.sub(/:[^:]+$/, ''), name.sub(/.*:/, '')]
          else
            ["rubygems", name]
          end
        else
          [args[0], args[1]]
        end
      end

      def gav(*args)
        if args[0] =~ /:/
          [args[0].sub(/:[^:]+$/, ''), args[0].sub(/.*:/, ''), maven_version(*args[1, 2])]
        else
          [args[0], args[1], maven_version(*args[2,3])]
        end
      end

      def to_version(*args)
        maven_version(*args) || "[0,)"
      end
      
      private

      def maven_version(*args)
        if args.size == 0 || (args.size == 1 && args[0].nil?)
          nil
        else
          low, high = convert(args[0])
          low, high = convert(args[1], low, high) if args[1] =~ /[=~><]/
          if low == high
            low
          else
            "#{low || '[0'},#{high || ')'}"
          end
        end
      end

      def snapshot_version( val )
        if val.match(/[a-z]|[A-Z]/) && !val.match(/-SNAPSHOT|[${}]/)
          val + '-SNAPSHOT'
        else
          val
        end
      end

      def convert(arg, low = nil, high = nil)
        if arg =~ /~>/
          val = arg.sub(/~>\s*/, '')
          last = val=~/\./ ? val.sub(/\.[0-9]*[a-z]+.*$/, '').sub(/\.[^.]+$/, '.99999') : '99999'
          ["[#{snapshot_version(val)}", "#{snapshot_version(last)}]"]
        elsif arg =~ />=/
          val = arg.sub(/>=\s*/, '')
          ["[#{snapshot_version(val)}", (nil || high)]
        elsif arg =~ /<=/
          val = arg.sub(/<=\s*/, '')
          [(nil || low), "#{snapshot_version(val)}]"]
          # treat '!' the same way as '>' since maven can not describe such range
        elsif arg =~ /[!>]/  
          val = arg.sub(/[!>]\s*/, '')
          ["(#{snapshot_version(val)}", (nil || high)]
        elsif arg =~ /</
          val = arg.sub(/<\s*/, '')
          [(nil || low), "#{snapshot_version(val)})"]
        elsif arg =~ /\=/
          val = arg.sub(/=\s*/, '')
          # for prereleased version pick the maven version (no version range)
          if val.match /[a-z]|[A-Z]/
            [ val, val ]
          else
            ["[#{val}", "#{val}.0.0.0.0.1)"]
          end
       else
          # no conversion here, i.e. assume maven version
          [arg, arg]
        end
      end
    end
  end
end
