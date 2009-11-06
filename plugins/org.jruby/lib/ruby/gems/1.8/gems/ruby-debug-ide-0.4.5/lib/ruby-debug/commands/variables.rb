module Debugger
  class VarConstantCommand < Command # :nodoc:
    def regexp
      /^\s*v(?:ar)?\s+c(?:onst(?:ant)?)?\s+/
    end
    
    def execute
      obj = debug_eval(@match.post_match)
      unless obj.kind_of? Module
        print_msg "Should be Class/Module: %s", @match.post_match
      else
        print_variables(obj.constants, "constant") do |var|
          obj.const_get(var)
        end
      end
    end
    
    class << self
      def help_command
        'var'
      end
      
      def help(cmd)
        %{
          v[ar] c[onst] <object>\t\tshow constants of object
        }
      end
    end
  end
  
  class VarGlobalCommand < Command # :nodoc:
    def regexp
      /^\s*v(?:ar)?\s+g(?:lobal)?\s*$/
    end
    
    def execute
      print_variables(global_variables, 'global') do |var|
        debug_eval(var)
      end
    end
    
    class << self
      def help_command
        'var'
      end
      
      def help(cmd)
        %{
          v[ar] g[lobal]\t\t\tshow global variables
        }
      end
    end
  end
  
  class VarInstanceCommand < Command # :nodoc:
    def regexp
      # id will be read as first match, name as post match
      /^\s*v(?:ar)?\s+i(?:nstance)?\s+((?:[\\+-]0x)[\dabcdef]+)?/
    end
    
    def execute
      if (@match[1])
        obj = ObjectSpace._id2ref(@match[1].hex) rescue nil
        unless obj
          # TODO: ensure that empty variables frame will be printed
          @printer.print_msg("Unknown object id : %s", @match[1])
        end
      else
        obj = debug_eval(@match.post_match)
      end
      return unless obj
      if (obj.is_a?(Array)) then
        print_array(obj)
      elsif (obj.is_a?(Hash)) then
        print_hash(obj)
      else
        print_element("variables") do
          # instance variables
          kind = 'instance'
          inst_vars = obj.instance_variables
          instance_binding = obj.instance_eval{binding()}
          # print self at top position
          print_variable('self', debug_eval('self', instance_binding), kind) if inst_vars.include?('self')
          inst_vars.sort.each do |var|
            print_variable(var, debug_eval(var, instance_binding), kind) unless var == 'self'
          end
          
          # class variables
          class_binding = obj.class.class_eval('binding()')
          obj.class.class_variables.sort.each do |var|
            print_variable(var, debug_eval(var, class_binding), 'class')
          end
        end
      end 
    end
    
    class << self
      def help_command
        'var'
      end
      
      def help(cmd)
        %{
          v[ar] i[nstance] <object>\tshow instance variables of object, object can be given by its id or an expression
        }
      end
    end
  end
  
  class VarLocalCommand < Command # :nodoc:
    def regexp
      /^\s*v(?:ar)?\s+l(?:ocal)?\s*$/
    end
    
    def execute
      locals = @state.context.frame_locals(@state.frame_pos)
      _self = @state.context.frame_self(@state.frame_pos) 
      locals['self'] = _self unless _self.to_s == "main"
      print_variables(locals.keys, 'local') do |var|
        locals[var]
      end
    end
    
    class << self
      def help_command
        'var'
      end
      
      def help(cmd)
        %{
          v[ar] l[ocal]\t\t\tshow local variables
        }
      end
    end
  end
end
