require 'ruby-debug/helper'

module Debugger

  class Command # :nodoc:
    SubcmdStruct=Struct.new(:name, :min, :short_help, :long_help) unless
      defined?(SubcmdStruct)

    # Find param in subcmds. param id downcased and can be abbreviated
    # to the minimum length listed in the subcommands
    def find(subcmds, param)
      param.downcase!
      for try_subcmd in subcmds do
        if (param.size >= try_subcmd.min) and
            (try_subcmd.name[0..param.size-1] == param)
          return try_subcmd
        end
      end
      return nil
    end

    class << self
      def commands
        @commands ||= []
      end
      
      DEF_OPTIONS = {
        :event => true, 
        :control => false, 
        :unknown => false,
        :need_context => false,
      }
      
      def inherited(klass)
        DEF_OPTIONS.each do |o, v|
          klass.options[o] = v if klass.options[o].nil?
        end
        commands << klass
      end 

      def load_commands
        dir = File.dirname(__FILE__)
        Dir[File.join(dir, 'commands', '*')].each do |file|
          require file if file =~ /\.rb$/
        end
        Debugger.constants.grep(/Functions$/).map { |name| Debugger.const_get(name) }.each do |mod|
          include mod
        end
      end
      
      def method_missing(meth, *args, &block)
        if meth.to_s =~ /^(.+?)=$/
          @options[$1.intern] = args.first
        else
          if @options.has_key?(meth)
            @options[meth]
          else
            super
          end
        end
      end
      
      def options
        @options ||= {}
      end
    end
    
    def initialize(state, printer)
      @state, @printer = state, printer
    end
    
    def match(input)
      @match = regexp.match(input)
    end

    protected

    def method_missing(meth, *args, &block)
      if @printer.respond_to? meth
        @printer.send meth, *args, &block
      else
        super
      end
    end
    
    # FIXME: use delegate? 
    def errmsg(*args)
      @printer.print_error(*args)
    end

    def print(*args)
      @state.print(*args)
    end

    # see Timeout::timeout, the difference is that we must use a DebugThread
    # because every other thread would be halted when the event hook is reached
    # in ruby-debug.c
    def timeout(sec)
      return yield if sec == nil or sec.zero?
      raise ThreadError, "timeout within critical session" if Thread.critical
      begin
        x = Thread.current
        y = DebugThread.start {
          sleep sec
          x.raise StandardError, "Timeout: evaluation took longer than #{sec} seconds." if x.alive?
        }
        yield sec
      ensure
        y.kill if y and y.alive?
      end
    end
    
    def debug_eval(str, b = get_binding)
      begin
        max_time = 10
        to_inspect = str.gsub(/\\n/, "\n")
        @printer.print_debug("Evaluating with timeout after %i sec", max_time)
        timeout(max_time) do
          eval(to_inspect, b)
        end
      rescue StandardError, ScriptError => e
        @printer.print_exception(e, @state.binding) 
        throw :debug_error
      end
    end

    def debug_silent_eval(str)
      begin
        eval(str, get_binding)
      rescue StandardError, ScriptError
        nil
      end
    end
    
    def hbinding(hash)
      code = hash.keys.map{|k| "#{k} = hash['#{k}']"}.join(';') + ';binding'
      if obj = @state.context.frame_self(@state.frame_pos)
        obj.instance_eval code
      else
        eval code
      end
    end
    private :hbinding
    
    def get_binding
      binding = @state.context.frame_binding(@state.frame_pos)
      binding || hbinding(@state.context.frame_locals(@state.frame_pos))
    end

    def line_at(file, line)
      Debugger.line_at(file, line)
    end

    def get_context(thnum)
      Debugger.contexts.find{|c| c.thnum == thnum}
    end  
  end
  
  Command.load_commands
end
