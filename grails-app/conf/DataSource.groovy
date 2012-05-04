dataSource {
	pooled = true
}
hibernate {
	cache.use_second_level_cache = true
	cache.use_query_cache = true
	
	//jdbc.batch_size=50

	cache.provider_class = 'net.sf.ehcache.hibernate.EhCacheProvider'
}
// environment specific settings
environments {
	development {
		dataSource {
			dbCreate = "update" // one of 'create', 'create-drop','update'
			url = "jdbc:mysql://localhost/hon_log?useUnicode=yes&characterEncoding=UTF-8"
			username = "hon_log"
			password = "hon_log"
			dialect = 'org.hibernate.dialect.MySQL5InnoDBDialect'
			driverClassName = "com.mysql.jdbc.Driver"
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
			dialect = 'org.hibernate.dialect.MySQL5InnoDBDialect'
			driverClassName = "com.mysql.jdbc.Driver"
		}
	}
}
