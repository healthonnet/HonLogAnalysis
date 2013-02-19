
modules = {

    core {
        dependsOn 'blueprint,jquery,jquery-ui,select-menu,blueprint'

        resource url : '/js/hon-log/hon-log.js'
        resource url : '/css/hon-log/hon-log.css'
    }

    'select-menu' {
        dependsOn 'jquery,jquery-ui'

        resource url : '/js/jquery-ui/ui.selectmenu.js', disposition : 'head'
        resource url : '/css/jquery-ui/ui.selectmenu.css', disposition : 'head'
    }
}