package org.hon.log.analysis.search.loader

import java.io.File;
import java.util.Map;

import org.apache.poi.hssf.record.formula.functions.T
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.hibernate.FlushMode
import org.hon.log.analysis.search.LoadedFile;
import org.hon.log.analysis.search.SearchLogLine;
import org.springframework.beans.factory.InitializingBean;

class SearchLogLoaderService implements InitializingBean{
	GrailsApplication grailsApplication
	Map loaders

	static transactional = true

	final BATCH_SIZE = 100;
	
	def sessionFactory;
	
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

		int n=0;
		file.eachLine{
			line ->
			try{
				if(!service.acceptLine(line)){
					return;
				}
				SearchLogLine sll=service.parseLine(line)
				if(sll.origQuery.length()>=1000){
					return
				}
				
				loadedFile.addToSearchLogLines(sll)
				if(++n % BATCH_SIZE == 0){
					saveFile(loadedFile);
				}
			}catch(Exception e){
				log.error("Cannot parse $line", e)
			}
		}
		saveFile(loadedFile)
		
		long totalTime = System.currentTimeMillis() - startTime
		log.info("Loaded $n lines in $simpleFilename in $totalTime ms.")
		
		n
	}
	
	def saveFile(loadedFile) {
		try {
			loadedFile.save(failOnError:true)
			
			// if we don't manually clear the session, we end up with a memory leak where
			// each document takes long and longer until the JVM finally crashes
			sessionFactory.currentSession.flush()
			sessionFactory.currentSession.clear()
		} catch (Throwable t) {
			t.printStackTrace();
			log.warn("Found error while processing log line", t);
			throw new RuntimeException(t);
		}
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
