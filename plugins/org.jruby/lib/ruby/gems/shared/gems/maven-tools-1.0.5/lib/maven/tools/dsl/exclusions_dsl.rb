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
      class ExclusionsDSL < Array
        extend Options
        def self.create( dep, *args, &block )
          a = ExclusionsDSL.new( dep )
          args.each do |arg|
            a << ExclusionDSL.create( dep, *arg )
          end
          a.instance_eval( &block ) if block
          a
        end

        def initialize( dep )
          @dep = dep
        end

        def help
          warn self.class.help( 'exclusions', :exclusion => nil ) + <<EOS
arguments: exclusions 'group_id:artifact_id1', 'group_id:artifact_id2'
arguments: exclusions ['group_id','artifact_id1'], ['group_id','artifact_id2']
EOS
        end

        def exclusion( *args, &block )
          self << ExclusionDSL.create( @dep, *args, &block )
        end
        
        def to_coordinate_array
          self.collect do |a|
            a.to_coordinate
          end
        end
      end

      class ExclusionDSL
        extend Options
        include Models

        def self.create( dep, *args, &block )
          args, options = args_and_options( *args )
          e = ExclusionDSL.new
          case args.size
          when 1
            e.group_id, e.artifact_id = args[0].split( /:/ )
          when 2
            e.group_id, e.artifact_id = *args
          end
          e.instance_eval( &block ) if block
          fill_options( e, options || {} )
          dep.exclusions << e.model
          e
        end

        def initialize
          @model = Exclusion.new
        end
        
        def help
          warn self.class.help( 'exclusion', :group_id, :artifact_id ) + <<EOS
argument: exclusion 'group_id:artifact_id'
arguments: exclusion 'group_id','artifact_id'
EOS
        end

        def to_coordinate
          "#{@model.group_id}:#{@model.artifact_id}"
        end
      end
    end
  end
end
