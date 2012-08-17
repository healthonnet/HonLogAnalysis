class UrlMappings {

	static mappings = {
		"/load"(controller:'searchLogLoader', action:'index')
		"/load/$action?/$id?"(controller:'searchLogLoader')
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(controller:'searchLogLoader', action:'index')
		"500"(view:'/error')
	}
}
