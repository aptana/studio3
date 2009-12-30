LIPSUM = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat."

def tag_preview(selector_list)
  html = 'TEXT_INSERT'
  selectors = selector_list.split(/\s+/)
  last_tag = ''
  text_insert = "Generated preview for CSS selector #{selector_list}."

  star_class = ''
  star_id = ''
  html_class = ''
  html_id = ''
  body_class = ''
  body_id = ''

  selectors.reverse.each do | selector |
    singlet = false
    tag = selector.clone
    if (tag =~ /#(.+)/)
      id = (tag.scan(/#(.+)/))[0][0]
      id.gsub!(/\..+/, '')
    else
      id = nil
    end
    if (tag =~ /\.(.+)/)
      cls = (tag.scan(/\.(.+)/))[0][0]
      cls.gsub!(/\./, ' ')
      cls.gsub!(/\#.+/, '')
    else
      cls = nil
    end

    tag.downcase!
    tag.sub!(/#(.+)/, '');
    tag.sub!(/\.(.+)/, '');
    tag.sub!(/:.+/, '')

    case tag
    when '*'
      star_class = " #{cls}" if cls
      star_id = " id=\"#{id}\"" if id
      cls = nil
      id = nil
      tag = 'div'
    when 'body'
      body_class = " #{cls}" if cls
      body_id = " id=\"#{id}\"" if id
      cls = nil
      id = nil
      tag = 'div'
    when 'html'
      html_class = " #{cls}" if cls
      html_id = " id=\"#{id}\"" if id
      cls = nil
      id = nil
      tag = 'div'
    end

    next if tag == '+'

    if selector =~ /^[#.]/
      case last_tag
      when 'li'
        tag = 'ul'
      when 'td'
        tag = 'tr'
      when 'tr'
        tag = 'table'
      when /^h\d/
        tag = 'div'
      else
        tag = 'span'
      end
    end

    if (tag =~ /\[(.+?)\]/)
      tag_attr = (tag.scan(/\[(.+?)\]/))[0][0]
      tag.gsub!(/\[.+?\]/, '')
    else
      tag_attr = nil
    end
    part = "<" + tag
    part += " #{tag_attr}" if tag_attr
    part += " id=\"#{id}\"" if id
    part += " class=\"#{cls}\"" if cls

    # defaults for img tag
    case tag
    when 'img'
      part += " src=\"http://www.google.com/intl/en/images/logo.gif\""
      part += " alt=\"Preview of #{selector_list}\""
      singlet = true
    when 'a'
      part += " href=\"\#\""
    when 'input'
      open_tag = part.clone
      part += " type=\"radio\" /> Radio"
      part += "#{open_tag} type=\"checkbox\" /> Checkbox<br />"
      part += "#{open_tag} type=\"text\" value=\"Text Field\" />"
      part += "#{open_tag} type=\"button\" value=\"Button\""
      singlet = true
    when 'select'
      part += "><option>Option 1</option><option>Option 2</option"
      html = ''
    end

    if (singlet)
      part += " />"
    else
      part += ">"
      part += html
      part += "</" + tag + ">"
    end

    case tag
    when /^h\d/
      text_insert = tag.sub(/^h(\d+)/, "Heading \\1")
    when 'p'
      text_insert = LIPSUM
    when 'object', 'img', 'input'
      text_insert = ""
    end

    html = part
    last_tag = tag
  end

  if (last_tag)
    case last_tag
    when 'em', 'strong', 'b', 'i'
      html = "<p>#{html}</p>"
    when 'li'
      html = "<ul>#{html}</ul>"
    when 'td'
      html = "<table><tr>#{html}</tr></table>"
    when 'tr'
      html = "<table>#{html}</table>"
    when 'input', 'textarea', 'select'
      html = "<form method=\"get\">#{html}</form>"
    end
  end

  html = "<div>#{html}</div>"
  html.sub!(/TEXT_INSERT/, text_insert)

  return <<EOT
<div class="__wrap_wrap"><div class="__star_wrap#{star_class}"#{star_id}><div class="__html_wrap#{html_class}"#{html_id}><div class="__body_wrap#{body_class}"#{body_id}>#{html}</div></div></div></div>
EOT
end

def preview_css(str, env = {})
  orig_css = str.clone  
  orig_css.gsub!(/\b\*\b/, '.__star_wrap')
  orig_css.gsub!(/\bbody\b/, '.__body_wrap')
  orig_css.gsub!(/\bhtml\b/, '.__html_wrap')

  orig_css.gsub!(/<.+?>/, '')
  orig_css.gsub!(/&lt;\/?style\b.*?&gt;/m, '')
  orig_css.strip!

  rules = str.scan(/\s*(.+?)\s*{\s*(.+?)\s*}/m)
  
  html = ''
  css = ''
  rule_num = 0

  rules.each do | rule |
    selector = rule[0].gsub(/<.+?>/, '')
    styles = rule[1].gsub(/<.+?>/, '')
    styles.gsub!(/^\s*\{\n*/m, '')
    styles.gsub!(/\s*\}\s*$/m, '')
    styles.gsub!(/\t/, ' ' * env['TM_TAB_SIZE'].to_i)
    selectors = selector.split(/\s*,\s*/m)
    selectors.each do | single_selector |
      rule_num += 1
      html += "<div class=\"__rule_clear\"></div>\n\n" if html != ''
      html += "<div class=\"__rule_selector\">#{single_selector} <a class=\"__view_link\" href=\"javascript:viewCSS('__rule#{rule_num}')\" title=\"Click to toggle CSS view\">CSS</a><div class=\"__rule\" id=\"__rule#{rule_num}\" style=\"display: none\">#{styles}</div></div>\n\n"
      html += tag_preview(single_selector) + "\n\n"
    end
  end

  filename = ENV['TM_FILENAME'] || 'untitled'
  base = ''
  base = "<base href=\"file://#{env['TM_FILEPATH']}\" />" if env['TM_FILEPATH']

  return <<EOT
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" 
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
    #{base}
    <meta http-equiv="Content-type" content="text/html; charset=utf-8" />
    <meta http-equiv="Content-Language" content="en-us" />
    <title>CSS Preview for #{filename}</title>
    <style type="text/css">
#{orig_css}
.__wrap_wrap {
  position: relative;
  margin-top: 5px;
  margin-bottom: 20px;
  border-top: 1px solid #ccc;
}
.__rule_selector {
  font-family: Times;
  font-size: 16px;
  border-top: 1px solid #ccc;
}
.__rule {
  white-space: pre;
  word-wrap: break-word;
  font-family: Monaco;
  font-size: 11px;
}
.__view_link {
  font-family: Monaco;
  font-size: 11px;
}
.__rule_clear:after {
  content: "."; 
  display: block; 
  height: 0; 
  clear: both; 
  visibility: hidden;
}
    </style>
    <script type="text/javascript">
    function viewCSS(rule_id) {
      var el = document.getElementById(rule_id);
      if (el) {
        if (el.style.display == 'none')
          el.style.display = 'block';
        else
          el.style.display = 'none';
      }
    }
    </script>
  </head>
  
  <body>
#{html}
  </body>
</html>
EOT
end
