
modules = {

    core {
        dependsOn 'blueprint,jquery,jquery-ui,select-menu'

        resource url : '/js/hon-log/hon-log.js'
    }

    'select-menu' {
        dependsOn 'jquery,jquery-ui'

        resource url : '/js/jquery-ui/ui.selectmenu.js'
        resource url : '/css/jquery-ui/ui.selectmenu.css'
    }
}