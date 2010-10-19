#
# Indentation
#
module Alpha
class Beta
	def main(a)
		if a % 2 == 0
			print "even"
		end
		case a % 2
		when 1
			puts "odd"
		end
		for i in 1..10
			puts i
		end
	end
end
end
