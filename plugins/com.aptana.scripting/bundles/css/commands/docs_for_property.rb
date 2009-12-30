require 'radrails'
require 'properties'
 
#
# Lookup current word as a CSS property on w3c.org
#
command "Documentation for Property" do |cmd|
  cmd.key_binding = [ :M1, :M2, :H ] # TODO Get right keybinding
  cmd.output = :show_as_html
  cmd.input = :selection 
  cmd.scope = "source.css"
  cmd.invoke do |context|
    cur_line = context.environment['TM_CURRENT_LINE']
    cur_word = context.environment['TM_CURRENT_WORD']

    # since dash (Ô-Õ) is not a word character, extend current word to neighboring word and dash characters
    prop_name = /[-\w]*#{Regexp.escape cur_word}[-\w]*/.match(cur_line)[0]

    prop_url = CSS_PROPERTIES[prop_name]
    prop_url = "" if prop_url.nil?
    url = "http://www.w3.org/TR/CSS2/" + prop_url
    "<meta http-equiv='Refresh' content='0;URL=#{url}'>"
  end
end