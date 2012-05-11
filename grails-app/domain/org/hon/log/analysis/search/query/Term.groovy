package org.hon.log.analysis.search.query

import org.hon.log.analysis.search.SearchLogLine;

class Term {
		String value

		int size(){
			searchLogLines?.size()?:0
		}
				
		static belongsTo = SearchLogLine
		static hasMany = [searchLogLines:SearchLogLine]

	// disable hibernate optimistic locking - it forces a constant update of the 'version' field, which is costly
	// plus, there's only one thread that reads from the db at once
	static mapping = {
		version false
	}
	
    static constraints = {
			value unique:true, nullable:false
			
    }
		
		String toString() {
			value
	}
}
