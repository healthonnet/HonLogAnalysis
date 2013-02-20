
modules = {

    core {

        dependsOn 'blueprint,jquery,jquery-ui,select-menu,blueprint,pastel-24'

        defaultBundle 'single-bundle'

        resource url : '/js/hon-log/hon-log.js'
        resource url : '/css/hon-log/hon-log.css'
    }

    'select-menu' {

        dependsOn 'jquery,jquery-ui'

        defaultBundle 'single-bundle'

        resource url : '/js/jquery-ui/ui.selectmenu.js', disposition : 'head'
        resource url : '/css/jquery-ui/ui.selectmenu.css', disposition : 'head'
    }

    'pastel-24' {

        defaultBundle 'single-bundle'

        resource url : '/css/pastel/pastel-24.css'
    }

    overrides {

        jquery {
            defaultBundle 'single-bundle'
            resource id:'js', bundle: 'single-bundle'
        }

        'jquery-theme' {
            defaultBundle 'single-bundle'

            resource id:'theme', bundle : 'single-bundle'
        }

        'jquery-ui' {
            defaultBundle 'single-bundle'

            resource id:'js', bundle : 'single-bundle'
        }

        blueprint {
            defaultBundle 'single-bundle'

            resource id:'main', bundle : 'single-bundle'
            resource id:'ie', bundle : 'single-bundle'
        }
    }
}