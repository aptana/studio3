require 'cgi'
require 'yaml'

module Debugger

  class XmlPrinter # :nodoc:
    attr_accessor :interface
    
    def initialize(interface)
      @interface = interface
    end
    
    def print_msg(*args)
      msg, *args = args
      xml_message = CGI.escapeHTML(msg % args)
      print "<message>#{xml_message}</message>"
    end

    # Sends debug message to the frontend if XML debug logging flag (--xml-debug) is on.
    def print_debug(*args)
      Debugger.print_debug(*args)
      if Debugger.xml_debug
        msg, *args = args
        xml_message = CGI.escapeHTML(msg % args)
        @interface.print("<message debug='true'>#{xml_message}</message>")
      end
    end

    def print_error(*args)
      print_element("error") do
        msg, *args = args
        print CGI.escapeHTML(msg % args)
      end
    end
    
    def print_frames(context, current_frame_id)
      print_element("frames") do
        (0...context.stack_size).each do |id|
          print_frame(context, id, current_frame_id)
        end
      end
    end
    
    def print_current_frame(frame_pos)
      print_debug "Selected frame no #{frame_pos}"
    end
    
    def print_frame(context, frame_id, current_frame_id)
      # idx + 1: one-based numbering as classic-debugger
      file = context.frame_file(frame_id)
      print "<frame no=\'%s\' file=\'%s\' line=\'%s\' #{"current='true' " if frame_id == current_frame_id}/>",
        frame_id + 1, File.expand_path(file), context.frame_line(frame_id)
    end
    
    def print_contexts(contexts)
      print_element("threads") do
        contexts.each do |c|
          print_context(c) unless c.ignored?
        end
      end
    end
    
    def print_context(context)
      current = 'current="yes"' if context.thread == Thread.current
      print "<thread id=\"%s\" status=\"%s\" #{current}/>", context.thnum, context.thread.status
    end
    
    def print_variables(vars, kind)
      print_element("variables") do
        # print self at top position
        print_variable('self', yield('self'), kind) if vars.include?('self')
        vars.sort.each do |v|
          print_variable(v, yield(v), kind) unless v == 'self'
        end
      end
    end
    
    def print_array(array)
      print_element("variables") do
        index = 0 
        array.each { |e|
          print_variable('[' + index.to_s + ']', e, 'instance') 
          index += 1 
        }
      end
    end
    
    def print_hash(hash)
      print_element("variables") do
        hash.keys.each { | k |
          if k.class.name == "String"
            name = '\'' + k + '\''
          else
            name = k.to_s
          end
          print_variable(name, hash[k], 'instance') 
        }
      end
    end
    
    def print_variable(name, value, kind)
      unless value
        print("<variable name=\"%s\" kind=\"%s\"/>", CGI.escapeHTML(name), kind)
        return
      end
      if value.is_a?(Array) || value.is_a?(Hash)
        has_children = !value.empty?
        unless has_children
          value_str = "Empty #{value.class}"
        else
          value_str = "#{value.class} (#{value.size} element(s))"
        end
      else
        has_children = !value.instance_variables.empty? || !value.class.class_variables.empty?
        value_str = value.to_s || 'nil' rescue "<#to_s method raised exception: #$!>"
        unless value_str.is_a?(String)
          value_str = "ERROR: #{value.class}.to_s method returns #{value_str.class}. Should return String." 
        end
        if value_str =~ /^\"(.*)"$/
          value_str = $1
        end
      end
      value_str = "[Binary Data]" if value_str.is_binary_data?
      print("<variable name=\"%s\" kind=\"%s\" value=\"%s\" type=\"%s\" hasChildren=\"%s\" objectId=\"%#+x\"/>",
          CGI.escapeHTML(name), kind, CGI.escapeHTML(value_str), value.class,
          has_children, value.respond_to?(:object_id) ? value.object_id : value.id)
    end
    
    def print_breakpoints(breakpoints)
      print_element 'breakpoints' do
        breakpoints.sort_by{|b| b.id }.each do |b|
          print "<breakpoint n=\"%d\" file=\"%s\" line=\"%s\" />", b.id, b.source, b.pos.to_s
        end
      end
    end
    
    def print_breakpoint_added(b)
      print "<breakpointAdded no=\"%s\" location=\"%s:%s\"/>", b.id, b.source, b.pos
    end
    
    def print_breakpoint_deleted(b)
      print "<breakpointDeleted no=\"%s\"/>", b.id
    end
    
    def print_breakpoint_enabled(b)
      print "<breakpointEnabled bp_id=\"%s\"/>", b.id
    end
    
    def print_breakpoint_disabled(b)
      print "<breakpointDisabled bp_id=\"%s\"/>", b.id
    end
    
    def print_contdition_set(bp_id)
      print "<conditionSet bp_id=\"%d\"/>", bp_id
    end

    def print_catchpoint_set(exception_class_name)
      print "<catchpointSet exception=\"%s\"/>", exception_class_name
    end

    def print_expressions(exps)
      print_element "expressions" do
        exps.each_with_index do |(exp, value), idx|
          print_expression(exp, value, idx+1)
        end
      end unless exps.empty?
    end
    
    def print_expression(exp, value, idx)
      print "<dispay name=\"%s\" value=\"%s\" no=\"%d\" />", exp, value, idx
    end
    
    def print_eval(exp, value)
      print "<eval expression=\"%s\" value=\"%s\" />",  CGI.escapeHTML(exp), value
    end
    
    def print_pp(value)
      print value
    end
    
    def print_list(b, e, file, line)
      print "[%d, %d] in %s\n", b, e, file
      if lines = Debugger.source_for(file)
        b.upto(e) do |n|
          if n > 0 && lines[n-1]
            if n == line
              print "=> %d  %s\n", n, lines[n-1].chomp
            else
              print "   %d  %s\n", n, lines[n-1].chomp
            end
          end
        end
      else
        print "No sourcefile available for %s\n", file
      end
    end
    
    def print_methods(methods)
      print_element "methods" do
        methods.each do |method|
          print "<method name=\"%s\" />", method
        end
      end
    end
    
    # Events
    
    def print_breakpoint(n, breakpoint)
      print("<breakpoint file=\"%s\" line=\"%s\" threadId=\"%d\"/>", 
      breakpoint.source, breakpoint.pos, Debugger.current_context.thnum)
    end
    
    def print_catchpoint(exception)
      context = Debugger.current_context
      print("<exception file=\"%s\" line=\"%s\" type=\"%s\" message=\"%s\" threadId=\"%d\"/>", 
      context.frame_file(0), context.frame_line(0), exception.class, CGI.escapeHTML(exception.to_s), context.thnum)
    end
    
    def print_trace(context, file, line)
      Debugger::print_debug "trace: location=\"%s:%s\", threadId=%d", file, line, context.thnum
      # TBD: do we want to clog fronend with the <trace> elements? There are tons of them.
      # print "<trace file=\"%s\" line=\"%s\" threadId=\"%d\" />", file, line, context.thnum
    end
    
    def print_at_line(file, line)
      print "<suspended file=\'%s\' line=\'%s\' threadId=\'%d\' frames=\'%d\'/>",
        File.expand_path(file), line, Debugger.current_context.thnum, Debugger.current_context.stack_size
    end
    
    def print_exception(exception, binding)
      print "<processingException type=\"%s\" message=\"%s\"/>", 
        exception.class, CGI.escapeHTML(exception.to_s)
    end
    
    def print_inspect(eval_result)
      print_element("variables") do 
        print_variable("eval_result", eval_result, 'local')
      end
    end
    
    def print_load_result(file, exception=nil)
      if exception then
        print("<loadResult file=\"%s\" exceptionType=\"%s\" exceptionMessage=\"%s\"/>", file, exception.class, CGI.escapeHTML(exception.to_s))        
      else
        print("<loadResult file=\"%s\" status=\"OK\"/>", file)        
       end    
    end

    def print_element(name)
      print("<#{name}>")
      begin
        yield
      ensure
        print("</#{name}>")
      end
    end

    private
    
    def print(*params)
      Debugger::print_debug(*params)
      @interface.print(*params)
    end
    
  end

end
