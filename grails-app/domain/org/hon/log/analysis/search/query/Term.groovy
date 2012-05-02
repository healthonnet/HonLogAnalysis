package org.hon.log.analysis.search.query

import org.hon.log.analysis.search.SearchLogLine;

class Term {
		String value

		int size(){
			searchLogLines?.size()?:0
		}
				
		static belongsTo = SearchLogLine
		static hasMany = [searchLogLines:SearchLogLine]

    static constraints = {
			value unique:true, nullable:false
			
    }
		
		String toString() {
			value
	}
}
