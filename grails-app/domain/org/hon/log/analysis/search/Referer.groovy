package org.hon.log.analysis.search

class Referer {
	
	String url
	Domain domain
	
    static constraints = {
		url unique:true, nullable:false
        domain nullable:true
    }
	
	static hasMany = [searchLogLines:SearchLogLine]
	static belongsTo = [domain : Domain]
	
	// disable hibernate optimistic locking - it forces a constant update of the 'version' field, which is costly
	// plus, there's only one thread that reads from the db at once
	static mapping = {
		version false
		
		// why the hell would we need a join table for a 1-to-many mapping?
		searchLogLines column : "referer_id",joinTable:false

        url type: "text"
	}
	
	String toString() {
		"Referer[$url]"
	}
}
