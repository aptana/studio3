module Ruble
  class ScopeSelector
    
    def initialize(selector)
      case selector
      when String
        @selector = selector
      when Symbol
        @selector = selector.to_s.tr("_", ".")
      when Array
        ors = selector.map do |orExpr|
          case orExpr
          when String
            orExpr
          when Symbol
            orExpr.to_s.tr("_", ".")
          when Array
            ands = orExpr.map do |andExpr|
              case andExpr
                when String then andExpr
                when Symbol then andExpr.to_s.tr("_", ".")
                else "?"
              end
            end
            
            ands.join " "
          end
        end
        @selector = ors.join ", "
      else
        @selector = "?"
      end
    end
    
    def to_s
      @selector
    end
  end
end
