// attach the items
$(document).ready(function() {
        $('th.browser').each(function() {
                var $entry = $(this);
                var browser = jQuery.trim($entry.text());
                $entry.append("<br /><img src='../../../content/button_off.gif' browser=\"" + browser + "\" onclick='toggleRows(\"" + browser + "\", this)' style='padding-top:5px' title='Hide items not available on this platform'>");
        });

		$('.details-property').hide();
		$('.details-method').hide();
		if (window.location.toString().indexOf("visibility=advanced") > 0) {
			$('.advanced').attr("hide", "false");
		}
		else
		{
			$('.advanced').hide().attr("hide", "true");			
		}
		$('.item').attr("details", "false");
});

var arr = new Array();

function toggleAdvanced(image)
{
    	var val = image.state;
        if(val == undefined)
        {
        	val = false;
        }

        if (val == false)
		{
         	var tr = $('.advanced').fadeIn("fast");
        	tr.attr("hide", "false");
			tr.each(function(index, obj) {
					var details = $(this).attr("details");
					console.log(details);
    				if (details == "true") {
						$(this).next().show();
					}
				});
			$(".advanced-toggle").attr("src", "../../../content/hide_advanced.gif");
        }
        else
        {
        	var tr = $('.advanced').fadeOut("fast");
        	tr.attr("hide", "true");
			tr.next().hide();
            $(".advanced-toggle").attr("src", "../../../content/show_advanced.gif");
        }
        image.state = !val;
}

function toggleRows(browserName, image)
{
        var val = arr[browserName];
        if(val == undefined)
        {
               val = false;
        }

        if (val == true) {

                var tr = $('td.no[title="' + browserName + '"]').parent();
				tr.each(function(index, obj) {
					var hide = $(this).attr("hide");
    				if (hide == "false" || hide == undefined) {
						$(this).fadeIn("fast");
					}
					
					var details = $(this).attr("details");
					console.log(details);
    				if (details == "true") {
						$(this).next().show();
					}
				});

                $('img[browser="' + browserName + '"]').attr("src", "../../../content/button_off.gif");

                for (var word in arr)
                {
                        var state = arr[word];
                        if(word != browserName && state == true)
                        {
                                var tr2 = $('td.no[title="' + word + '"]').parent();
                                tr2.hide();
                                tr2.next().hide();
                        }
                }
        }
        else
        {
                var tr = $('td.no[title="' + browserName + '"]').parent();
                tr.fadeOut("fast");
                tr.next().hide();
                $('img[browser="' + browserName + '"]').attr("src", "../../../content/button_on.gif");
        }
        arr[browserName] = !val;
}

function toggleRowDetails(row)
{
	var state = $(row).parent().parent().attr("details");
	if(state == undefined)
	{
		state = "true";
	}
	
	$(row).empty();
	if(state == "false")
	{
		$(row).append("Hide Details");
	}
	else
	{
		$(row).append("Show Details");		
	}
	
	$(row).parent().parent().next().toggle();
	$(row).parent().parent().attr("details", state == "false" ? "true" : "false");
}

function toggleClickDetails(row)
{
	$(row).parent().parent().parent().next().toggle();
	var state = $(row).parent().parent().parent().attr("details");
	$(row).parent().parent().parent().attr("details", state == "false" ? "true" : "false");
}