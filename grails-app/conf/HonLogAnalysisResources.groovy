
modules = {

    core {

        dependsOn 'blueprint,jquery,jquery-ui,select-menu,blueprint,pastel-24'

        defaultBundle 'core-bundle'

        resource url : '/js/hon-log/hon-log.js'
        resource url : '/css/hon-log/hon-log.css'
    }

    'select-menu' {

        dependsOn 'jquery,jquery-ui'

        defaultBundle 'third-party-bundle'

        resource url : '/js/jquery-ui/ui.selectmenu.js', disposition : 'head'
        resource url : '/css/jquery-ui/ui.selectmenu.css', disposition : 'head'
    }

    'pastel-24' {

        defaultBundle 'third-party-bundle'

        resource url : '/css/pastel/pastel-24.css'
    }

    overrides {

        jquery {
            defaultBundle 'third-party-bundle'
            resource id:'js', bundle: 'third-party-bundle'
        }

        'jquery-theme' {
            defaultBundle 'third-party-bundle'

            resource id:'theme', bundle : 'third-party-bundle'
        }

        'jquery-ui' {
            defaultBundle 'third-party-bundle'

            resource id:'js', bundle : 'third-party-bundle'
        }

        blueprint {
            defaultBundle 'third-party-bundle'

            resource id:'main', bundle : 'third-party-bundle'
            resource id:'ie', bundle : 'third-party-bundle'
        }
    }
}