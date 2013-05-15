class UrlMappings {

	static mappings = {
		"/load"(controller:'searchLogLoader', action:'index')
		"/statistics"(controller:'statistics', action:'countByCountry')
		"/details"(controller:'details', action:'countQueriesPerDay')
        "/suggestions"(controller:'suggestions', action:'listEnglishQuery')
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
