require 'ruble'
require 'ruble/ui'

command "Completion: Ruby (rcodetools)" do |cmd|
  cmd.key_binding = 'M3+ESC'
  cmd.output = :insert_as_text
  cmd.input = :document
  cmd.scope = "source.ruby"
  cmd.invoke do |context|
	require "pathname"

	ruby_exe    = ENV["TM_RUBY"] || "ruby"
	rcodetools_dir = "#{ENV['TM_BUNDLE_SUPPORT']}/vendor/rcodetools"
	
	RAILS_DIR = nil
	dir = File.dirname(ENV["TM_FILEPATH"]) rescue ENV["TM_PROJECT_DIRECTORY"]
	if dir
	  dir = Pathname.new(dir)
	  loop do
	    if (dir + "config/environment.rb").exist?
	      Object.send(:remove_const, :RAILS_DIR)
	      RAILS_DIR = dir.to_s
	      break
	    end
	    
	    break if dir.to_s == "/"
	    
	    dir += ".."
	  end
	end
	
	command     = <<END_COMMAND.tr("\n", " ").strip
"#{ruby_exe}"
-I "#{rcodetools_dir}/lib"
--
"#{rcodetools_dir}/bin/rct-complete"
#{"-r \"#{RAILS_DIR}/config/environment.rb\"" if RAILS_DIR}
--line=#{ENV['TM_LINE_NUMBER']}
--column=#{ENV['TM_LINE_INDEX']}
2> /dev/null
END_COMMAND

    result = IO.popen(command, "r+") do |io|
      io.write STDIN.read
      io.close_write # let the process know you've given it all the data 
      io.read
    end

	completions = result.to_a.map { |l| l.strip }.select { |l| l.length > 0 && l =~ /\S/ }

	if not $?.success?
	  Ruble::UI.tool_tip "Parse error."
	elsif completions.size == 1
	  selected = completions.first
	elsif completions.size > 1
	  selected = completions[Ruble::UI.menu(completions)] rescue exit
	else
	  Ruble::UI.tool_tip "No matches were found."
	end
	
	if selected
	  selected.sub(/\A#{Regexp.escape(ENV['TM_CURRENT_WORD'].to_s)}/, "")
	else
	  nil
	end
  end
end