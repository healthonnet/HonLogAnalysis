package org.hon.log.analysis.search

class LoadedFile {
		String filename
		Date loadedAt = new Date()
		static hasMany = [searchLogLines:SearchLogLine]
		int size(){
			searchLogLines?.size()?:0
		}

    static constraints = {
    }
}
