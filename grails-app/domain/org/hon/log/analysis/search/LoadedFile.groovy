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
	
	// disable hibernate optimistic locking - it forces a constant update of the 'version' field, which is costly
	// plus, there's only one thread that reads from the db at once
	static mapping = {
		version false
		
		
		searchLogLines column : "loaded_file_id",
		        joinTable:false // why the hell would we need a join table for a 1-to-many mapping?
	}
}
