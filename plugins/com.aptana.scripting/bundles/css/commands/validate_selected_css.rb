require "radrails"

command "Validate Selected CSS" do |cmd|
  cmd.key_binding = [ :M1, :shift, :V ] # TODO Get right keybinding
  cmd.output = :show_as_html
  #cmd.input = [:selection, :scope]
  cmd.input = :selection
  cmd.scope = "source.css"
  cmd.invoke do |context|
    str = '<html><head><meta http-equiv="Refresh" content="0; URL='
    str << 'http://jigsaw.w3.org/css-validator/validator?warning=1&profile=none&usermedium=all&text='
    scope = context.in.read
    scope.gsub!(/<\/?style.*?>/, '')
    if !scope.nil? and scope.size > 0
      scope.each_byte do |b|
        if b == 32 # whitespace
          str << '+'
        elsif b.chr =~ /\w/
          str << b.chr
        else
          str << sprintf('%%%02x', b)
        end
      end
    end
    str << '#errors"></head><body></body></html>'
  end
end