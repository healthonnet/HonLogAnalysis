/*! Main HonLogAnalysis javascript file.
 *
 */
var HonLog;

(function($){
    HonLog = {
        // TODO: put interesting javascript logic here
    }

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

})(jQuery);