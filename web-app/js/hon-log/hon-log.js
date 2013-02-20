/*! Main HonLogAnalysis javascript file.
 *
 */
var HonLog;

(function($){
    HonLog = {
        bulkLoad : function() {
            // load in iframe
            $('.bulk-load-form').submit(function(e){
                e.preventDefault();
                $('#bulk-load-frame').show().attr('src', $(this).attr('ACTION') + '?' + $(this).serialize() + '&doAction=true');

                // auto scroll down
                var oldHeight;
                var scrollIframeIntervalId = setInterval(function(){
                    var iframe = $('#bulk-load-frame');
                    var height = $('#bulk-load-frame').contents()[0].body.scrollHeight;

                    if (oldHeight && height > 0) {
                        if (height > oldHeight) {
                            // scroll down
                            iframe.contents().scrollTop(height);
                        } else {
                            clearInterval(scrollIframeIntervalId);
                        }
                    }
                    oldHeight = height;
                }, 200);
            });
        },

        onPageLoad : function(){
            //
            // style the select menu
            //
            $('select.menu').selectmenu({
                style     : 'dropdown',
                icons     : [
                    {find : '.pastel-24.pastel-24-page'},
                    {find : '.pastel-24.pastel-24-calculator'},
                    {find : '.pastel-24.pastel-24-page-information'}
                ]
            });
            $('select.menu').change(function(){
                $(this).parent().submit();
            });

            // add some top-level icons
            var icons = [
                "pastel-24 pastel-24-page",
                "pastel-24 pastel-24-calculator",
                "pastel-24 pastel-24-page-information"
            ];

            var selectMenus = $('a.ui-selectmenu');
            for (var i = 0; i < selectMenus.size(); i++) {
                $(selectMenus.get(i)).addClass(icons[i]);
            }
        }
    }

    HonLog.onPageLoad();

})(jQuery);