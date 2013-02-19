package org.hon.log.analysis.search.loader

import org.hon.log.analysis.search.LoadedFile;

class SearchLogLoaderController {
	SearchLogLoaderService searchLogLoaderService

	def index = {
		if(params.paction){
			def paction = params.paction
			params.remove('paction')
			if(paction != 'index')
				redirect(action:paction)
		}

		[
			loadedFiles:searchLogLoaderService.listAllLoadedFileSummaries().sort({it.loadedAt}).reverse()
				]
	}

	/**
	 * Do a bulk upload of multiple files given a directory on the local disk.  Assumes all logs are
	 * in HON-style format, and doesn't allow for duplicates.
	 * 
	 * @author nolan
	 */
	def bulkLoad = {
		def filedir = params.filedir?: grailsApplication.config.honlogDefault.filedir;
		def filter = params.filter ?: grailsApplication.config.honlogDefault.filter;

		if (params.doAction) {
            def out = new PrintStream(new BufferedOutputStream(response.outputStream));

			File directory = new File(filedir);

			if (!directory?.isDirectory()) {
                logAndPrint("not a valid directory: $directory", out);
                return;
			}
			
			int lineCount = 0;
			int fileCount = 0;
			Map<String,Object> displayResults = new TreeMap<String, Object>();
			long startTime = System.currentTimeMillis();

            def files = directory.listFiles();

			// iterate through each file in the directory and analyze it
            logAndPrint("Preparing to load ${files.length} files...", out);

			for (File file : files) {
				if (file.isDirectory()) {
                    logAndPrint("${file.getName()}: is directory", out);
					continue;
				} else if (LoadedFile.findByFilename(file.getName())) { // already analyzed
                    logAndPrint("${file.getName()}: already loaded", out);
					continue;
				}
				// hard code 'hon' style logs for now
				def options = [
					origFile : file.getName(),
					filter : filter
					];
				int currentLineCount = searchLogLoaderService.load('hon', file, options);

                logAndPrint("${file.getName()}: loaded $currentLineCount lines", out);

				lineCount += currentLineCount;
				fileCount++;
			}
			
			long totalTime = System.currentTimeMillis() - startTime;

            logAndPrint("Loaded $lineCount lines in $fileCount files in $totalTime ms.", out)

            out.close();
		} else {
            return [:];
        }

	}
	
	def upload = {
		if(!params.logfile)
			return [loaderTypes:searchLogLoaderService.loaders.keySet().sort()]

		def f = request.getFile('logfile')
		if(!f.empty) {
			File ftmp = File.createTempFile("loglines", '.log')
			f.transferTo( ftmp)
			int n = searchLogLoaderService.load(params.type, ftmp, [origFile:f.originalFilename])
			ftmp.delete()
			flash.message="loaded $n log lines"
			redirect(action:'index')
		}
		else {
			flash.error = 'file cannot be empty'
		}
	}

	def delete = {
		Map r = searchLogLoaderService.deleteById(params.id.toLong())
		flash.message = "deleted $r.filename ($r.deleted)"
		redirect (action:'index')
	}
	
	def deleteAll = {
		int deletedRows = searchLogLoaderService.deleteAll()
		flash.message = "Deleted $deletedRows rows"
		redirect (action:'index')
	}

    def logAndPrint(string, out) {
        log.info(string);
        out.println(string)
        out.flush();
    }
}
