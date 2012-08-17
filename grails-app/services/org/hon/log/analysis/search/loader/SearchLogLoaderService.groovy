package org.hon.log.analysis.search.loader

import java.io.File;
import java.util.Map;

import groovy.sql.Sql
import org.apache.poi.hssf.record.formula.functions.T
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.hibernate.FlushMode
import org.hon.log.analysis.search.LoadedFile;
import org.hon.log.analysis.search.LoadedFileSummary;
import org.hon.log.analysis.search.SearchLogLine;
import org.springframework.beans.factory.InitializingBean;

class SearchLogLoaderService implements InitializingBean{
	GrailsApplication grailsApplication
	Map loaders

	static final MAX_QUERY_SIZE = 1000
	static final BATCH_SIZE = 100
	
	static transactional = true

	javax.sql.DataSource dataSource;
	def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP
	
	def sessionFactory;
	
	/**
	 * Summary view of the loaded files which displays more quickly, because Hibernate isn't loading the searchLogLines
	 * (and their dependencies) just to do a count of them.
	 * 
	 * If you can't tell, I'm not a big fan of ORM. Google "ORM as anti-pattern" sometime. - nolan
	 * @return
	 */
	public List listAllLoadedFileSummaries() {
		
		String query = """select lf.id, lf.filename, lf.loaded_at, count(sll.id)
                          from loaded_file lf left join search_log_line sll
                          on lf.id = sll.loaded_file_id
                          group by lf.id"""
		
		def db = new Sql(dataSource)
		return db.rows(query).collect{row ->
			
			new LoadedFileSummary(
				id : row[0],
				filename : row[1],
				loadedAt : row[2],
				size : row[3]
				)
			
			
			}
		
	}
	
	/**
	 * load all {@link SearchLogLine} from the file
	 * @param source
	 * @param file
	 * @return the number of actually saved {@link SearchLogLine}
	 */
	public int load(String source, File file , options=[:]){
		
		// since we're batch inserting, we'll tell hibernate when to flush
		sessionFactory.currentSession.setFlushMode(FlushMode.MANUAL);
		
		def simpleFilename = file.getName();
		
		log.info("Loading $simpleFilename...")
		long startTime = System.currentTimeMillis()
		
		SearchLogLineLoaderAbst service = getLoaderService(source)
		LoadedFile loadedFile = new LoadedFile(filename: options.origFile?:file.absolutePath).save(failOnError:true)

		def filter = options?.filter;
		
		int n=0;
		file.eachLine{
			line ->
			try{
				
				if(!service.acceptLine(line) || (filter && !line.contains(filter))){
					return;
				}
				SearchLogLine sll=service.parseLine(line)
				if(sll.origQuery.length()>=MAX_QUERY_SIZE){
					return
				}
				sll.loadedFile = loadedFile;
				loadedFile.addToSearchLogLines(sll)
				sll = sll.save(failOnError: true);
				if (++n % BATCH_SIZE == 0) {
					cleanUpGorm();
				}
			}catch(Exception e){
				log.error("Cannot parse $line", e)
				throw new RuntimeException(e);
			}
		}
		
		long totalTime = System.currentTimeMillis() - startTime
		log.info("Loaded $n lines in $simpleFilename in $totalTime ms.")
		
		n
	}
	
	def cleanUpGorm() {
			// if we don't manually clear the session, we end up with a memory leak where
			// each document takes long and longer until the JVM finally crashes
		    // see discussion here: http://naleid.com/blog/2009/10/01/batch-import-performance-with-grails-and-mysql/
			sessionFactory.currentSession.flush()
			sessionFactory.currentSession.clear()
			propertyInstanceMap.get().clear();
	}
	
	/**
	 * 
	 * @param source
	 * @return the log loader associated with the given source
	 */
	SearchLogLineLoaderAbst getLoaderService(String source){
		SearchLogLineLoaderAbst service = loaders[source]
		if(!service){
			throw new RuntimeException("no log loader avaialble for source of type $source")
		}
		service
	}

	/**
	 * build the list of loader services
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		def ctx=grailsApplication.mainContext

		loaders = [:]
		ctx.getBeansOfType(org.hon.log.analysis.search.loader.SearchLogLineLoaderAbst).each{
			name, srv ->
			loaders[srv.source] = srv;
		}
	}
}
