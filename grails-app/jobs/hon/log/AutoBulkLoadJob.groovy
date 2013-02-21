package hon.log

import org.hon.log.analysis.search.LoadedFile;
import org.hon.log.analysis.search.loader.SearchLogLoaderService;


class AutoBulkLoadJob {
    static triggers = {
		//Everyday at 2am.
		cron name: 'dailyBulkLoad', cronExpression: "0 0 2 * * ?"
    }

	SearchLogLoaderService searchLogLoaderService
	def grailsApplication
	
    def execute() {
		/*
		 *	This is the original cron expression from crontab.
		 *	# call bulkLoad on the hon-log app in order to process the logs every day at 2am
		 *	0 2 * * * wget -O - 'http://localhost:8080/hon-log/load/bulkLoad?doAction=true' > /dev/null
		 *	
		 *	It is worth to not that the cron expression is slight different from the one used by Quartz the semantic is the same.
		 *
		*/
		
		log.info("Running cron process.")
		
		String filedir = grailsApplication.config.honlogDefault.filedir
		String filter =  grailsApplication.config.honlogDefault.filter
		
		File directory = new File(filedir);
		if (!directory?.isDirectory()) {
			return;
		}
			
		def files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) { //skip directory
				continue;
			} else if (LoadedFile.findByFilename(file.getName())) { // already analyzed
				continue;
			}
			// hard code 'hon' style logs for now
			def options = [origFile : file.getName(), filter : filter ];
			int currentLineCount = searchLogLoaderService.load('hon', file, options);
                        log.info("Loaded $currentLineCount lines in ${file.getName()}")
		}
		log.info("Cron process finished.")
    }
}
