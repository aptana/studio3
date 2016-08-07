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
module Maven
  module Tools
    module DSL
      module Options

        def args_and_options( *args )
          if args.last.is_a? Hash
            [ args[0..-2], args.last ]
          else
            [ args, {} ]
          end
        end
        
        def fill_options( receiver, options, *allow_defaults )
          options.each do |k,v|
            if ! allow_defaults.member?( k ) && receiver.send( "#{k}".to_sym )
              raise "#{receiver} has attribute #{k} already set"
            end
            receiver.send( "#{k}=".to_sym, v )
          end
        end

        def help( name, *args )
          args, options = args_and_options( *args )
          args.each do |a|
            options[ a ] = a.to_s if a && !options.key?( a )
          end
          opts = options.select{ |k,v| v }
          t = "\n# " + name.to_s.upcase + " #\n\n"
          unless opts.empty?
            t += "hash options: #{name} #{opts.inspect.gsub( /\"[{]/, '(' ).gsub( /[}]\"/, ')' )}\n"
          end
          t += "nested: #{name} do\n"
          t = append_nested_block( options, t )
          t += "        end\n"
          t
        end

        def help_block( *args )
          args, options = help_args_and_options( *args )
          append_nested_block( options )
        end

        private

        def help_args_and_options( *args )
          args, options = args_and_options( *args )
          args.each do |a|
            options[ a ] = a.to_s if a && !options.key?( a )
          end
          [ args, options ]
        end

        def append_nested_block( options, t = "")
          options.each do |k,v|
            if v
              t += "          #{k} #{v.inspect.gsub( /\"[{]/, '(' ).gsub( /[}]\"/, ')' )}\n"
            else
              t += "          #{k} # nested element\n"
            end
          end
          t
        end
      end
    end
  end
end
