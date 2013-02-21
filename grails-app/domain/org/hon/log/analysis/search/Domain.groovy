package org.hon.log.analysis.search

class Domain {
	
	String value;
	
	static constraints = {
		value unique:true, nullable:false
	}
	
	static hasMany = [referers:Referer]
	
	// disable hibernate optimistic locking - it forces a constant update of the 'version' field, which is costly
	// plus, there's only one thread that reads from the db at once
	static mapping = {
		version false
		
		// why the hell would we need a join table for a 1-to-many mapping?
		referers column : "domain_id",joinTable:false
	}
	
	String toString() {
		"Domain[$value]"
	}
}
