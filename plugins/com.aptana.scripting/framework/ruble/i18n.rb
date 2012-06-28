require 'yaml'

class Hash
  def deep_symbolize_keys
    dup.deep_symbolize_keys!
  end

  def deep_stringify_keys
    dup.deep_stringify_keys!
  end

  def deep_symbolize_keys!
    deep_change_keys!(:to_sym)
  end

  def deep_stringify_keys!
    deep_change_keys!(:to_s)
  end

  def deep_change_keys!(change_method = :to_s)
    self.keys.each do |k|
      new_key = k.send(change_method)
      current_value = self.delete(k)
      self[new_key] = current_value.is_a?(Hash) ? current_value.dup.deep_change_keys!(change_method) : current_value
    end
    self
  end

  def deep_merge!(other_hash)
    replace(deep_merge(other_hash))
  end

  def deep_merge(other_hash)
    self.merge(other_hash) do |key, oldval, newval|
      oldval = oldval.to_hash if oldval.respond_to?(:to_hash)
      newval = newval.to_hash if newval.respond_to?(:to_hash)
      oldval.class.to_s == 'Hash' && newval.class.to_s == 'Hash' ? oldval.deep_merge(newval) : newval
    end
  end
end

module Ruble
  class MissingInterpolationArgument < ArgumentError
    attr_reader :values, :string
    def initialize(values, string)
      @values, @string = values, string
      super "missing interpolation argument in #{string.inspect} (#{values.inspect} given)"
    end
  end

  class I18n
    def translate(key, variables = {})
      init_translations unless initialized?
      locale = java.util.Locale.default.language
      trace("Current locale: #{locale}")
      locale_translations = nil
      locale_translations = (translations[locale] || translations[:en]) if translations
      locale_translations ||= {}
      entry = locale_translations[key]
      entry ||= translations[:en][key] if translations and translations[:en]
      entry ||= key.to_s
      entry = interpolate(entry, variables) if variables && variables.size > 0
      entry
    end

    alias :t :translate

    protected

    def translations
      @translations ||= {}
    end

    def load_file(filename)
      data = YAML.load_file(filename)
      data.each { |locale, d| store_translations(locale, d || {}) }
    end

    def store_translations(locale, data, options = {})
      locale = locale.to_sym
      translations[locale] ||= {}
      data = data.deep_symbolize_keys
      translations[locale].deep_merge!(data)
    end

    def initialized?
      @initialized ||= {}
      @initialized[bundle_path] ||= false
    end

    def init_translations
      load_translations
      @initialized[bundle_path] = true
    end

    def load_translations
      trace("Loading translations for bundle: #{bundle_path}")
      locales_dir = File.join(bundle_path, 'config', 'locales')
      if File.exist? locales_dir
        Dir.chdir(locales_dir) do
          filenames = Dir.glob("*.yml")
          trace("Locale files: #{filenames}")
          filenames = filenames.map {|f| File.join(locales_dir, f) }
          filenames.each { |filename| load_file(filename) }
        end
      end
    end

    def bundle_path
      bundle_file = Ruble::BundleManager.manager.getBundleDirectory(java.io.File.new($0))
      bundle_file.getAbsolutePath
    end

    INTERPOLATION_PATTERN = Regexp.union(
      /%%/,
      /%\{(\w+)\}/,                               # matches placeholders like "%{foo}"
      /%<(\w+)>(.*?\d*\.?\d*[bBdiouxXeEfgGcps])/  # matches placeholders like "%<foo>.d"
    )

    def interpolate(string, values)
      string.gsub(INTERPOLATION_PATTERN) do |match|
        if match == '%%'
          '%'
        else
          key = ($1 || $2).to_sym
          value = values.key?(key) ? values[key] : raise(MissingInterpolationArgument.new(values, string))
          value = value.call(values) if value.respond_to?(:call)
          $3 ? sprintf("%#{$3}", value) : value
        end
      end
    end
  end
end

def t(key, variables = {})
  Ruble::I18n.new.translate(key, variables)
end
