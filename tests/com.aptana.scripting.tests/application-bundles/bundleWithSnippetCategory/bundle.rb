require 'ruble'

bundle do
  snippet_category "GoodCategory" do |s|
    s.icon_path = 'close.gif'
  end
  
  snippet_category "NoIconCategory"
  
  snippet_category "InvalidIconCategory" do |s|
    s.icon_path = 'invalid.gif'
  end

  snippet_category "MalformedIconCategory" do |s|
    s.icon_path = '\ad/a""invalid.gif'
  end
    
  snippet_category "UrlCategory" do |s|
    s.icon_path = 'http://preview.appcelerator.com/dashboard/img/icons/icon_to_do_list.png'
  end
 
  snippet_category "InvalidUrlCategory" do |s|
    s.icon_path = 'http://preview.appcelerator.com/dashboard/img/icons/icon_to_do_listxxx.png'
  end
end