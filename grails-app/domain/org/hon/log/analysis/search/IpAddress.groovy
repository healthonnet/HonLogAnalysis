package org.hon.log.analysis.search

class IpAddress {
	
	String value;
	Country country
	
    static constraints = {
		value unique:true, nullable:false
    }
	
	static hasMany = [searchLogLines:SearchLogLine]
	static belongsTo = [country : Country]
	
	// disable hibernate optimistic locking - it forces a constant update of the 'version' field, which is costly
	// plus, there's only one thread that reads from the db at once
	static mapping = {
		version false
		
		// why the hell would we need a join table for a 1-to-many mapping?
		searchLogLines column : "ip_address_id",joinTable:false
	}
	
	String toString() {
		"IpAddress[$value]"
	}
}
