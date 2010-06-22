OUTPUT_PATH = ARGV.first + "/"

# Generate the filename for a class/type name
def file_name(klass)
  file_name = OUTPUT_PATH + klass.to_s.downcase
  file_name.gsub!("::", "/")
  file_name << ".rb"
  return file_name
end

# Generate directory names for a filename/path
def dir_names(file_name)
  last_slash = file_name.rindex("/")
  return nil if last_slash.nil?
  file_name[0...last_slash]
end

# Get all the classes/modules in the system
def get_classes
  klasses = Module.constants.select {|c| ["Class", "Module"].include?(eval("#{c}.class").to_s) }
  klasses = klasses.reject {|klass| klass.to_s[0].chr == "f" or klass.to_s == "JavaPackageModuleTemplate" }
  klasses = klasses.collect {|k| eval("#{k}")}  
  klasses = klasses.uniq.sort_by {|klass| klass.to_s }
end

# Print a type out to a string
def print_type(klass)
  f = "#{klass.class.to_s.downcase} #{klass}"
  f << " < #{klass.superclass.to_s }" if klass.respond_to?(:superclass) and !klass.superclass.nil?
  f << "\n"
  klass.included_modules.each {|mod| f << "  include #{mod.to_s}\n" unless mod.to_s == "Kernel" && klass.to_s != "Object"}
  f << "\n"

  klass.methods(false).sort_by {|m| m.to_s }.each do |method_name|
    method = eval("#{klass}").method(method_name) rescue nil
    f << print_method(klass, method, method_name.to_s, true)
  end
  f << "\n"
  klass.public_instance_methods(false).sort_by {|m| m.to_s }.each do |method_name|
    f << print_instance_method(klass, method_name)
  end
  f << "\n  protected\n\n"
  klass.protected_instance_methods(false).sort_by {|m| m.to_s }.each do |method_name|
    f << print_instance_method(klass, method_name)
  end
  f << "\n  private\n\n"
  klass.private_instance_methods(false).sort_by {|m| m.to_s }.each do |method_name|
    f << print_instance_method(klass, method_name)
  end
  f << "end\n"
  f
end

# Try to grab a reference to the instance method for a type
def grab_instance_method(klass, method_name)
  # TODO Fix it so we can get a hold of the module instance methods properly
  begin
    obj = nil
    if klass.class.to_s == "Module"
      obj = Object.new
      obj.extend(klass)
    elsif klass.to_s == "Symbol" 
      obj = :symbol
    elsif klass.to_s == "Integer" 
      obj = 1
    elsif klass.to_s == "Bignum" 
      obj = 1
    elsif klass.to_s == "MatchData" 
      obj =  /(.)(.)(.)/.match("abc")
    elsif klass.to_s == "Fixnum" 
      obj = 1
    elsif klass.to_s == "Float" 
      obj = 1.0
    elsif klass.to_s == "TrueClass" 
      obj = true
    elsif klass.to_s == "FalseClass" 
      obj = false
    elsif klass.to_s == "NilClass" 
      obj = nil
    elsif klass.to_s == "CGI"
      ENV["REQUEST_METHOD"] = "GET"
      obj = klass.new
    else
      obj = klass.new
    end
    return obj.method(method_name.to_s)
  rescue StandardError => e
    STDERR.puts "Couldn't grab method #{method_name.to_s}: #{e}"
    # TODO If we can't create an instance of a class, generate dynamic subclass where we can, and then
    # grab methods from there
    begin
      # If we're a module, we may need to force the function to be more visible to grab it
      unless obj.nil? 
        obj.module_eval do
          module_function(method_name.to_s)
        end
        return obj.method(method_name.to_s)      
      end
    rescue StandardError => e
      STDERR.puts e
    end
  end
  nil
end

def print_instance_method(klass, method_name)
  method = grab_instance_method(klass, method_name)
  print_method(klass, method, method_name.to_s)
end

# Print out a method to a string
def print_method(klass, method, method_name, singleton = false)
  str = "  def "
  str << "self." if singleton
  str << method_name.to_s
  if !method.nil? and method.arity != 0
    # TODO We need to handle methods that take blocks!
    str << "(#{print_args(method.arity)})"
  end
  str << "\n  end\n\n"
  str
end

# print arguments for a method, given the method arity
def print_args(arity)
  args = []
  if arity < 0
    (arity.abs + 1).times {|i| args << "arg#{i}" }
    args << "*rest"  
  else
    arity.times {|i| args << "arg#{i}" }    
  end
  args.join(", ")
end

# Now do the actual main loop, which is to get all the types and then print them out to files.
require 'fileutils'
get_classes.each do |klass|
  file_name = file_name(klass)
  dirs = dir_names(file_name)
  FileUtils.mkdir_p(dirs) if !dirs.nil? and !File.exist?(file_name)
  open(file_name, 'w') do |f|
    f << print_type(klass)
  end
  # Spit out constants to some file... i.e. ARGV
  open(file_name('constants'), 'w') do |f|
    f << "ARGV = []\n"
    f << "TRUE = true\n"
    f << "FALSE = false\n"
    f << "STDOUT = IO.new\n"
    f << "STDIN = IO.new\n"
    f << "STDERR = IO.new\n"
    f << "ENV = {}\n"
    f << "NIL = nil\n"
    f << "VERSION = ''\n"
    f << "RUBY_PATCHLEVEL = 123\n"
    f << "RUBY_COPYRIGHT = 'ruby - Copyright (C) 1993-2009 Yukihiro Matsumoto'\n"
    f << "TOPLEVEL_BINDING = Binding.new\n"
    f << "RUBY_VERSION = '1.8.7'\n"
    f << "RUBY_PLATFORM = ''\n"
    f << "PLATFORM = ''\n"
  end
end
