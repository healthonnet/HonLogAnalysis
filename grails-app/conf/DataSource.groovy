dataSource {
	pooled = true
}
hibernate {
	cache.use_second_level_cache = true
	cache.use_query_cache = true

	cache.provider_class = 'net.sf.ehcache.hibernate.EhCacheProvider'
}
// environment specific settings
environments {
	development {
		dataSource {
			dbCreate = "update" // one of 'create', 'create-drop','update'
			pooled = true
			url = "jdbc:mysql://localhost/hon_log?useUnicode=yes&characterEncoding=UTF-8"
			username = "hon_log"
			password = "hon_log"
		}
	}
	test {
		dataSource {
			dbCreate = "create-drop"
			driverClassName = "org.hsqldb.jdbcDriver"
			username = "sa"
			password = ""
			url = "jdbc:hsqldb:mem:testDb"
		}
	}
	production {
		dataSource {
			dbCreate = "update"
			grails.config.locations=[
				"file:${userHome}/${appName}-config.properties"
			]
			url = "jdbc:mysql://localhost/hon_log?useUnicode=yes&characterEncoding=UTF-8"
			username = "hon_log"
			password = "hon_log"
		}
	}
}
