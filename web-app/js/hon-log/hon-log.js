/*! Main HonLogAnalysis javascript file.
 *
 */
var HonLog;

(function($){
    HonLog = {
        // TODO: put interesting javascript logic here
    }

    $('select.menu').selectmenu({style:'dropdown'});
    $('select.menu').change(function(){
        $(this).parent().submit();
    });

})(jQuery);