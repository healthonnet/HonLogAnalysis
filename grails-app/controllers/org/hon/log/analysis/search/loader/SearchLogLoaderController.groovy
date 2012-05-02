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
					loadedFiles:LoadedFile.list().sort({it.loadedAt}).reverse(),
				]
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
