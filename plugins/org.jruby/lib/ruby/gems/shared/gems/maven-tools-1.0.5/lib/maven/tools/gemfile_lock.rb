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
    class GemfileLock < Hash
     
      class Dependency
        attr_accessor :name, :version, :dependencies
        def initialize(line, deps = {})
          @name = line.sub(/\ .*/,'')
          @version =  line.sub(/.*\(/, '').sub(/\).*/, '').sub(/-java$/, '')
          @dependencies = deps
        end
        
        def add(line)
          dependencies[line.sub(/\ .*/,'')] = line.sub(/.*\(/, '').sub(/\).*/, '')
        end
      end

      def initialize(file)
        super()
        current = nil
        f = file.is_a?(File) ? file.path: file
        if File.exists? f
          File.readlines(f).each do |line|
            if line =~ /^    [^ ]/
              line.strip!
              current = Dependency.new(line)
              self[current.name] = current
            elsif line =~ /^      [^ ]/
              line.strip!
              current.add(line) if current
            end
          end
        end
      end

      def recurse(result, dep)
        if d = self[dep]
          result[dep] = d.version if  !result.key?(dep)
          d.dependencies.each do |name, version|
            unless result.key? name
              if name != 'bundler'
                result[name] = self[name].nil?? version : self[name].version
                recurse(result, name)
              end
            end
          end
        end
      end

      def dependency_hull(deps = [])
        deps = deps.is_a?(Array) ? deps : [deps]
        result = {}
        deps.each do |dep|
          recurse(result, dep)
        end
        result
      end

      def hull
        dependency_hull(keys)
      end
    end
  end
end
