# Provides alternative to native TraceLineNumbers.lnums_for_str implemented
# currently only for C Ruby in linecache gem, ext/trace_nums.c

module TraceLineNumbers
  
  # Trivial implementation allowing to stop on every line.
  def lnums_for_str(code)
    (1..code.entries.size).to_a
  end
  module_function :lnums_for_str
  
end
