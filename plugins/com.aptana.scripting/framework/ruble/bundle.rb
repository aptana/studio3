require "java"
require "ruble/base_element"
require "ruble/bundle_manager"

module Ruble

  class Bundle < BaseElement
    @@defaults = {}

    def initialize(name, path = nil)
      if name.kind_of? String
        super(name, path)
      else
        # hack to pass in java object...should test type
        @jobj = name
      end
    end

    def add_child(child)
      @jobj.add_child child.java_object
    end

    def apply_defaults(obj)
      if obj.nil? == false
        defaults.each do |k,v|
          property = "#{k.to_s}=".to_sym

          if obj.respond_to?(property)
            obj.send(property, v)
          end
        end
      end
    end

    def author
      @jobj.author
    end

    def author=(author)
      @jobj.author = author
    end

    def copyright
      @jobj.copyright
    end

    def copyright=(copyright)
      @jobj.copyright = copyright
    end

    def defaults
      value_hashes = @@defaults[path.to_sym]

      (value_hashes.nil? || value_hashes.length == 0) ? {} : value_hashes[-1]
    end

    def pop_defaults()
      value_hashes = @@defaults[path.to_sym]

      value_hashes.pop if !value_hashes.nil?
    end

    def push_defaults(defaults)
      value_hashes = @@defaults[path.to_sym]

      if value_hashes.nil?
        @@defaults[path.to_sym] = [defaults]
      else
        value_hashes.push((value_hashes.length == 0) ? defaults : value_hashes[-1].merge(defaults))
      end
    end

    def description
      @jobj.description
    end

    def description=(description)
      @jobj.description = description
    end

    def menu(name, &block)
      Menu.define_menu(name, &block)
    end

    def name
      @jobj.display_name
    end

    def project_build_path
      BuildPathProxy.new(@jobj, @path)
    end

    def repository
      @jobj.repository
    end

    def repository=(repository)
      @jobj.repository = repository
    end

    def license
      @jobj.license
    end

    def license=(license)
      @jobj.license = license.join("\n")
    end

    def license_url
      @jobj.license_url
    end

    def license_url=(license_url)
      @jobj.license_url = license_url.join("\n")
    end

    # A proxy class to make syntax pretty...
    class FileTypesProxy
      def initialize(jobj)
        @jobj = jobj
      end

      def []=(scope, array)
        [array].flatten.each do |file_type|
          @jobj.associateFileType(file_type.to_s)
          @jobj.associateScope(file_type.to_s, scope.to_s.gsub(/_/, '.'))
        end
      end
    end

    # Used to associate a top-level scope and the Aptana editors with an array of filetype patterns
    # i.e. bundle.file_types['scope.name'] = '*.xml', '*xsl', '*.xslt'
    def file_types
      FileTypesProxy.new(@jobj)
    end    
        
    # A proxy class to make syntax pretty...
    class FoldingProxy
      def initialize(jobj)
        @jobj = jobj
      end

      def []=(scope, array)
        raise "Need two regexp to define folding" if array.size != 2
        @jobj.setFoldingMarkers(scope.to_s.gsub(/_/, '.'), array.first, array.last)
      end
    end

    def folding
      # return an object that responds to hash methods
      FoldingProxy.new(@jobj)
    end
    
    # A proxy class to make syntax pretty...
    class IndentProxy
      def initialize(jobj)
        @jobj = jobj
      end

      def []=(scope, array)
        raise "Need two regexp to define indent" if array.size != 2
        @jobj.setIndentMarkers(scope.to_s.gsub(/_/, '.'), array.first, array.last)
      end
    end

    def indent
      # return an object that responds to hash methods
      IndentProxy.new(@jobj)
    end

    def to_env
      { :TM_BUNDLE_SUPPORT => File.join(File.dirname(path), "lib"), :TM_BUNDLE_PATH => File.dirname(path) }
    end

    def to_s
      <<-EOS
      bundle(
        author: #{author}
      )
      EOS
    end

    class BuildPathProxy
      def initialize(jobj, path)
        @jobj = jobj
        @path = path
      end

      def []=(name, path)
        child = com.aptana.scripting.model.BuildPathElement.new(@path)
        child.display_name = name
        child.build_path = path
        @jobj.add_child child
      end
    end

    class << self
      def define_bundle(name="", values={}, &block)
        log_info("loading bundle #{name}")
        path = $0
        path = block.binding.eval("__FILE__") if block
        if File.basename(path) != "bundle.rb" || File.basename(File.dirname(path)) =~ /^(?:commands|snippets|templates|samples)$/
          log_error("Attempted to define a bundle in a file other than the bundle's bundle.rb file: #{path}")
        else
          # try to grab a cached bundle
          bundle = Ruble::BundleManager.bundle_from_path(File.dirname(path))
          
          # flag if we're using a cached bundle or not
          add_bundle = bundle.nil?
          
          # create a new bundle if we didn't have a cached one
          bundle = Bundle.new(name, path) if bundle.nil?

          # associate default values
          bundle.push_defaults values

          # add to bundle manager so the block, if given, can lookup the bundle
          # by path name
          BundleManager.reference_bundle bundle if add_bundle

          # process block
          block.call(bundle) if block_given?

          # remove defaults
          bundle.pop_defaults

          # add the bundle, if we created a new one
          BundleManager.add_bundle(bundle) if add_bundle
          
          nil
        end
      end
    end

  private

    def create_java_object
      com.aptana.scripting.model.BundleElement.new(path)
    end
  end

end

# define top-level convenience methods

def bundle(name="", &block)
  Ruble::Bundle.define_bundle(name, {}, &block)
end

def with_defaults(values, &block)
  path = block.binding.eval("__FILE__")
  bundle = Ruble::BundleManager.bundle_from_path(File.dirname(path))

  if bundle.nil?
    Ruble::Bundle.define_bundle("", values, &block)
  else
    bundle.push_defaults values
    block.call(bundle) if block_given?
    bundle.pop_defaults
  end
end
