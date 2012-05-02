class UrlMappings {

	static mappings = {
		"/load"(controller:'searchLogLoader', action:'index')
		"/load/$action?/$id?"(controller:'searchLogLoader')
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(view:"/index")
		"500"(view:'/error')
	}
}
