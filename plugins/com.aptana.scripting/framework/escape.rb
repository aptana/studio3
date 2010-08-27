# escape text to make it useable in a shell script as one ÒwordÓ (string)
def e_sh(str)
  str.to_s.gsub(/(?=[^a-zA-Z0-9_.\/\-\x7F-\xFF\n])/n, '\\').gsub(/\n/, "'\n'").sub(/^$/, "''")
end

# escape text for use in a TextMate snippet
def e_sn(str)
  str.to_s.gsub(/(?=[$`\\\/])/, '\\')
end

# escape text for use in a TextMate snippet placeholder
def e_snp(str)
  str.to_s.gsub(/(?=[$`\\}])/, '\\')
end

# escape text for use in an AppleScript string
def e_as(str)
  str.to_s.gsub(/(?=["\\])/, '\\')
end

# URL escape a string but preserve slashes (idea being we have a file system path that we want to use with file://)
def e_url(str)
  str.gsub(/([^a-zA-Z0-9\/_.-]+)/n) do
    '%' + $1.unpack('H2' * $1.size).join('%').upcase
  end
end

# Make string suitable for display as HTML, preserve spaces. Set :no_newline_after_br => true
# to cause Ò\nÓ to be substituted by Ò<br>Ó instead of Ò<br>\nÓ
def htmlize(str, opts = {})
  str = str.to_s.gsub("&", "&amp;").gsub("<", "&lt;")
  str = str.gsub(/\t+/, '<span style="white-space:pre;">\0</span>')
  str = str.reverse.gsub(/ (?= |$)/, ';psbn&').reverse
  if opts[:no_newline_after_br].nil?
    str.gsub("\n", "<br>\n")
  else
    str.gsub("\n", "<br>")
  end
end
