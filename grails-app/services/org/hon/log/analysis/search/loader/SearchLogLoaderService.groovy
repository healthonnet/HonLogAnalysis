package org.hon.log.analysis.search.loader

import java.io.File;
import java.util.Map;

import org.apache.tools.ant.types.FileList.FileName;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.hon.log.analysis.search.LoadedFile;
import org.hon.log.analysis.search.SearchLogLine;
import org.springframework.beans.factory.InitializingBean;

class SearchLogLoaderService implements InitializingBean{
	GrailsApplication grailsApplication
	Map loaders

	static transactional = true

	/**
	 * load all {@link SearchLogLine} from the file
	 * @param source
	 * @param file
	 * @return the number of actually saved {@link SearchLogLine}
	 */
	public int load(String source, File file , options=[:]){
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
//				sll.setTermList(sll.getTermList())
				
				loadedFile.addToSearchLogLines(sll)
				n++
				if(n%100 == 0){
					loadedFile.save(failOnError:true, flush:true)
				}
			}catch(Exception e){
				log.error("Cannot parse $line", e)
			}
		}
		loadedFile.save(failOnError:true)
		
		n
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
