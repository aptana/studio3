require 'maven/tools/coordinate'

module Maven
  module Tools
    class GemspecDependencies

      def initialize( gemspec )
        if gemspec.is_a? Gem::Specification
          @spec = gemspec
        else
          @spec = Gem::Specification.load( gemspec )
        end
        _setup
      end

      def java_runtime
        warn 'deprecated us java_dependency_artifacts instead'
        _deps( :java ).select { |d| d[0] == :compile }.collect { |d| d[ 1..-1] }
      end

      def java_dependencies
        warn 'deprecated us java_dependency_artifacts instead'
        _deps( :java )
      end

      def java_dependency_artifacts
        _deps( :java ).collect do |d|
          scope = d.shift
          d += [nil, nil, { :scope => scope } ][ (d.size - 4 )..2 ]
          Maven::Tools::Artifact.new( *d )
        end
      end
      def runtime
        _deps( :runtime )
      end

      def development
        _deps( :development )
      end

      private

      include Coordinate

      def _deps( type )
        @deps ||= {}
        @deps[ type ] ||= []
      end

      def _setup
        @spec.dependencies.each do |dep|
          versions = dep.requirement.requirements.collect do |req|
            # use this construct to get the same result in 1.8.x and 1.9.x
            req.collect{ |i| i.to_s }.join
          end
          _deps( dep.type ) << "rubygems:#{dep.name}:#{to_version( *versions )}"
        end
        @spec.requirements.each do |req|
          req.sub!( /#.*^/, '' )
          coord = to_split_coordinate_with_scope( req )
          if coord && coord.size > 1
            _deps( :java ) << coord
          end
        end
      end
    end
  end
end
