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
			File directory = new File(filedir);
			
			if (!directory?.isDirectory()) {
				return [output : "Directory '$filedir' not found."];
			}
			
			int lineCount = 0;
			int fileCount = 0;
			Map<String,Object> displayResults = new TreeMap<String, Object>();
			long startTime = System.currentTimeMillis();
			
			// iterate through each file in the directory and analyze it
			for (File file : directory.listFiles()) {
				if (file.isDirectory()) {
					continue;
				} else if (LoadedFile.findByFilename(file.getName())) { // already analyzed
					displayResults.put([file.getName(), "already loaded"]);
					continue;
				}
				// hard code 'hon' style logs for now
				def options = [
					origFile : file.getName(),
					filter : filter
					];
				int currentLineCount = searchLogLoaderService.load('hon', file, options);
				
				lineCount += currentLineCount;
				fileCount++;
				displayResults.put([file.getName(), currentLineCount]);
			}
			
			long totalTime = System.currentTimeMillis() - startTime;
			
			// show some meaningful display to the user
			def displayOutput = new StringBuilder("Loaded $lineCount lines in $fileCount files in $totalTime ms.");
			for (Map.Entry<String,Integer> entry : displayResults.entrySet()) {
				String filename = entry.getKey();
				String count = entry.getValue();
				displayOutput.append("\n$filename: $count");
			}
			return [output : displayOutput]
		} else {
			return [output : null] 
		}
	}
	
	def upload = {
		if(!params.logfile)
			return [loaderTypes:searchLogLoaderService.loaders.keySet().sort()]

		def f = request.getFile('logfile')
		if(!f.empty) {
			File ftmp =File.createTempFile("loglines", '.log')
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
		LoadedFile lf =LoadedFile.findById(params.id)
		int n = lf.size()
		String fname = lf.filename

		lf.delete()

		flash.message="deleted $fname ($n)"
		redirect (action:'index')
	}
}
