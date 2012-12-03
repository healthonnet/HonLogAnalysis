import com.maxmind.geoip.LookupService;

// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if(System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

grails.views.javascript.library="jquery"
//hostip.datasource.database="hostip"
//hostip.datasource.host="localhost"
//hostip.datasource.username="hostip"
//hostip.datasource.password="h0st1p"

geoip.data.cache = LookupService.GEOIP_STANDARD

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [ html: ['text/html','application/xhtml+xml'],
                      xml: ['text/xml', 'application/xml'],
                      text: 'text/plain',
                      js: 'text/javascript',
                      rss: 'application/rss+xml',
                      atom: 'application/atom+xml',
                      css: 'text/css',
                      csv: 'text/csv',
                      all: '*/*',
                      json: ['application/json','text/json'],
                      form: 'application/x-www-form-urlencoded',
                      multipartForm: 'multipart/form-data'
                    ]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// whether to install the java.util.logging bridge for sl4j. Disable for AppEngine!
grails.logging.jul.usebridge = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

grails.plugin.databasemigration.updateOnStartFileNames = ['changelog.xml']


// set per-environment serverURL stem for creating absolute links

def username = System.getProperty('user.name')
environments {
    production {
		geoip.data.resource= "/WEB-INF/classes/GeoLiteCity.dat"
		grails.plugin.databasemigration.updateOnStart = true // run the database migrations automatically
				grails.config.locations=[
					"classpath:${appName}-config.properties"
				]
				    }
    development {
		geoip.data.resource= "/../src/groovy/GeoLiteCity.dat"
		grails.plugin.databasemigration.updateOnStart = true // run the database migrations automatically
        grails.serverURL = "http://localhost:8080/${appName}"
				grails.config.locations=[
					"file:$username-dev.properties"
				]
	
    }
    test {
		geoip.data.resource= "/../src/groovy/GeoLiteCity.dat"
        grails.serverURL = "http://localhost:8080/${appName}"
    }

}

// log4j configuration
log4j = {
    warn  'org.codehaus',
		'org.springframework',
		'org.hibernate',
		'net.sf.ehcache.hibernate',
		'org.mortbay.log',
		'org.codehaus.groovy.grails',
		'services'

	info 'org.hon',
	     'com.linkedin.grails' // profiler
		 
	 appenders {
		 
				 console name:'stdout',
				 layout: pattern(conversionPattern: "%d{yyyy-MMM-dd HH:mm:ss,SSS} [%t] %c %x%n %-5p %m%n")
				 
				 console name:'stacktrace'
		 
			 }
	 root {
		 info 'stdout'
		 additivity=true
	 }
}
