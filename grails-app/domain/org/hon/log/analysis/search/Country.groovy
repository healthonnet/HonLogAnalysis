package org.hon.log.analysis.search

class Country {
	
	String countryName;
	String countryCode;
	
	static constraints = {
		countryCode unique:true, nullable:false
		countryName unique:false, nullable:false
	}
	
	static hasMany = [ipAddresses:IpAddress]
	
	// disable hibernate optimistic locking - it forces a constant update of the 'version' field, which is costly
	// plus, there's only one thread that reads from the db at once
	static mapping = {
		version false
		
		// why the hell would we need a join table for a 1-to-many mapping?
		ipAddresses column : "country_id",joinTable:false
	}
	
	String toString() {
		"Country[$countryName, $countryCode]"
	}
}
