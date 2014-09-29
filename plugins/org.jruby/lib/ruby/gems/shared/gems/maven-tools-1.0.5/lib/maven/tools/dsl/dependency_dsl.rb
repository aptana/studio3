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
require 'maven/tools/artifact'
require 'maven/tools/dsl/exclusions_dsl'
module Maven
  module Tools
    module DSL
      class DependencyDSL < Artifact
        extend Options
        include Models

        class << self
          def create( parent, type, scope, *args, &block )
            a = DependencyDSL.from( type, *args, &block )
            a.scope = scope if scope
            
            options = process_exclusions( a.model, a.dup )
            a.instance_eval &block if block
            fill_options( a.model, options, :type )
            parent.dependencies << a.model
            a
          end
          
          private

          def process_exclusions( dep, options )
            exclusions = options.delete( :exclusions )
            if exclusions && exclusions.size > 0# && dep.exclusions.size == 0
              ExclusionsDSL.create( dep, *exclusions )
            end
            options
          end
        end
        
        def initialize( *args )
          super
          @model = Dependency.new
        end
        
        def help
          type = self[:type]
          warn self.class.help( type, :group_id, :artifact_id, :version, :classifier, :exclusions, :scope => '{:compile|:test|:provided|:runtime}', :exclusions => nil, :exclusion => nil) + <<EOS
argument: #{type} 'group_id:artifact_id:version'
argument: #{type} 'group_id:artifact_id:classifier:version'
arguments: #{type} 'group_id:artifact_id','version'
arguments: #{type} 'group_id:artifact_id','classifier,'version'
arguments: #{type} 'group_id','artifact_id','version'
arguments: #{type} 'group_id','artifact_id','classifier,'version'
EOS
        end

        def exclusions( *args, &block )
          # TODO remove this part
          if args.empty?
            super
          else
            self[ :exclusions ] = ExclusionsDSL.create( @model, *args, &block ).to_coordinate_array
          end
        end

        def exclusion( *args, &block )
          e = ExclusionDSL.create( @model, *args, &block )
          # TODO remove this part
          self[ :exclusions ] ||= []
          self[ :exclusions ] << e.to_coordinate
        end
      end
    end
  end
end
