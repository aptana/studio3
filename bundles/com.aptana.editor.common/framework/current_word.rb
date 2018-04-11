# When creating a custom regex for Word.current_word, you need to keep a few things in mind:
#   The regex matches from your caret, so your caret position will always be the beginning of the line
#   Everything before your caret is reversed for the match, so the left of your caret will also match your caret position as ^
#   You must use a capture group () for the text you're trying to match
#   You must use a capture group () for the text before/after your match
# 
# EG: /(^[a-z]*)(.*$)/
#   The first capture group in that regex will match from your caret out until it can't find anymore lowercase letters. 
#   Then it'll match everything else in the line as the before/after match part.
#   You can currently only use a single regex to match before and after your caret.
# 
# Since your regex matches what's before your caret in reverse, you'll have to reverse specific stuff in your regex, eg:
#   /(.*):/  wouldn't match 'color' in ` color: `, but /:(.*)/ would.
#   It's reversed you see
# 
module Word
  def self.current_word(pat='a-zA-Z0-9', direction=:both)
    word = ENV['TM_SELECTED_TEXT']
    
    if word.nil? or word.empty?
      line, col = ENV['TM_CURRENT_LINE'], ENV['TM_LINE_INDEX'].to_i
      
      if pat.kind_of? Regexp
        @reg = pat
      else
        @reg = /(^[#{pat}]*)(.*$\r?\n?)/
      end
      
      left, before_match = *( line[0...col].reverse.match(@reg) ||[])[1..2]
      right, after_match = *( line[col..-1]        .match(@reg) ||[])[1..2]
      
      (before_match||='').reverse!
      (left||='').reverse!
      
      # p before_match, left, right, after_match
      
      case direction
        when :both then word = [left, right].join('')
        when :left then word = left
        when :right then word = right
        when :hash then word = {
          :line         => [before_match, left, right, after_match].join(''),
          :before_match => before_match,
          :left         => left,
          :right        => right,
          :after_match  => after_match,
        }
      end
    end
    
    word
  end
end

if __FILE__ == $0
  require "test/unit"
  class TestWord < Test::Unit::TestCase
# =begin    
    def test_with_spaces
      ENV['TM_SELECTED_TEXT']= nil
      ENV['TM_CURRENT_LINE'] = <<-EOF
    BeforeAfter    
      EOF
      ENV['TM_LINE_INDEX']   = '10'
      ENV['TM_TAB_SIZE']     = '2'
      assert_equal 'BeforeAfter', Word.current_word
      assert_equal 'Before',      Word.current_word('a-zA-Z0-9',:left)
      assert_equal 'After',       Word.current_word('a-zA-Z0-9',:right)
      
      assert_equal '    Before', Word.current_word(" a-zA-Z",:left)
      assert_equal 'After    ',  Word.current_word(" a-zA-Z",:right)
    end
    
    def test_with_tabs
      ENV['TM_SELECTED_TEXT']= nil
      ENV['TM_CURRENT_LINE'] = <<-EOF
    BeforeAfter   
      EOF
      ENV['TM_LINE_INDEX']   = '8'
      ENV['TM_TAB_SIZE']     = '2'
      assert_equal 'BeforeAfter', Word.current_word
      assert_equal 'Before',      Word.current_word('a-zA-Z0-9',:left)
      assert_equal 'After',       Word.current_word('a-zA-Z0-9',:right)
      
      assert_equal "\t\tBefore", Word.current_word("\ta-zA-Z",:left)
      assert_equal "After\t\t", Word.current_word("\ta-zA-Z",:right)
    end
    
    def test_with_dash
      ENV['TM_SELECTED_TEXT']= nil
      ENV['TM_CURRENT_LINE'] = <<-EOF
    Before--After    
      EOF
      ENV['TM_LINE_INDEX']   = '11'
      ENV['TM_TAB_SIZE']     = '2'
      assert_equal 'Before--After', Word.current_word('-a-zA-Z0-9')
      assert_equal 'Before-',      Word.current_word('-a-zA-Z0-9',:left)
      assert_equal '-After',       Word.current_word('-a-zA-Z0-9',:right)
      
      assert_equal 'Before-', Word.current_word("\ta-zA-Z\-",:left)
    end
    
    def test_hash_result
      ENV['TM_SELECTED_TEXT']= nil
      ENV['TM_CURRENT_LINE'] = <<-EOF
  before_match  BeforeAfter  after_match  
      EOF
      ENV['TM_LINE_INDEX']   = '22'
      ENV['TM_TAB_SIZE']     = '2'
      
      word = Word.current_word("a-zA-Z",:hash)
      
      assert_equal ENV['TM_CURRENT_LINE'], "#{word[:line]}"
      assert_equal 'Before', word[:left]
      assert_equal 'After', word[:right]
    end
=begin    
=end
    def test_both_result
      ENV['TM_SELECTED_TEXT']= nil
      ENV['TM_CURRENT_LINE'] = <<-EOF
  before_match  BeforeAfter  after_match  
      EOF
      ENV['TM_LINE_INDEX']   = '22'
      ENV['TM_TAB_SIZE']     = '2'
      
      assert_equal 'BeforeAfter', Word.current_word("a-zA-Z",:both)
    end
    
    def test_should_support_custom_regex
      ENV['TM_SELECTED_TEXT']= nil
      ENV['TM_CURRENT_LINE'] = <<-EOF
  before_match  BeforeAfter  after_match  
      EOF
      ENV['TM_LINE_INDEX']   = '22'
      ENV['TM_TAB_SIZE']     = '2'
      
      assert_equal '', Word.current_word(/[a-zA-Z]/,:both) # No match since no capture group was used!
      
      assert_equal 'ef', Word.current_word(/([a-z])/,:both) # Capture group, but only selecting a single caracter before and after the caret
      assert_equal 'eforefter', Word.current_word(/([a-z]+)/,:both) # Only lowercase characters
      assert_equal 'BeforeAfter', Word.current_word(/^([a-z]*)/i,:both) # Ignore case
      assert_equal 'BeforeAfter', Word.current_word(/^([a-zA-Z]*)/,:both) # Explicit case
    end
    
    def test_should_support_custom_regex_example1
      ENV['TM_SELECTED_TEXT']= nil
      ENV['TM_CURRENT_LINE'] = <<-EOF
  background-color: ;
      EOF
      ENV['TM_LINE_INDEX']   = '20'
      ENV['TM_TAB_SIZE']     = '2'
      
      assert_equal '  background-color: ', Word.current_word(/^(.*)/,:left)
      assert_equal 'background-color', Word.current_word(/:([-a-z]+)/,:left)
    end
    
    def test_should_support_custom_regex_example2
      ENV['TM_SELECTED_TEXT']= nil
      ENV['TM_CURRENT_LINE'] = <<-EOF
  <p class="className" id="flarm">Lorem ipsum dolor sit amet</p>
      EOF
      ENV['TM_LINE_INDEX']   = '44'
      ENV['TM_TAB_SIZE']     = '2'
      
      assert_equal %Q{\t<p class="className" id="flarm">Lorem ipsum}, Word.current_word(/^(.*)/,:left)
      assert_equal 'Lorem ipsum dolor sit amet', Word.current_word(/^([^<>]+)/i,:both)
      
      # You have to reverse the regex since it's matching against the reverse of the text before the caret
      assert_equal 'p', Word.current_word(/([-:a-z]+)</,:left)
      # You don't have to reverse your regex when matching text after the caret
      assert_equal 'p', Word.current_word(/<\/([-:a-z]+)/,:right)
    end
    
  end
end
