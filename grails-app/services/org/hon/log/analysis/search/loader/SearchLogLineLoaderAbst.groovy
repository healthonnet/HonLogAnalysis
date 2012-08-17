package org.hon.log.analysis.search.loader

import org.grails.geoip.service.GeoIpService;
import org.hon.log.analysis.search.Country;
import org.hon.log.analysis.search.IpAddress
import org.hon.log.analysis.search.SearchLogLine;

import com.maxmind.geoip.Location

abstract class SearchLogLineLoaderAbst {
	GeoIpService geoIpService
	
	/**
	 * init this parser instance (date shift, etc)
	 * @param options
	 */
	abstract void init(Map options)
	
	/**
	 * 
	 * @return the source it is supposed to parse "MedHunt", "Tel" etc.
	 */
	abstract String getSource()
	
	/**
	 * 
	 * @param line
	 * @return true if the line is to be parsed
	 */
	abstract boolean acceptLine(String line)
	
	/**
	 * return a new {@link SearchLogLine} object from a given line
	 * @param line
	 * @return
	 */
	abstract SearchLogLine parseLine(String line)
	
	/**
	 * Create a new IpAddress if necessary and associate it with the given searchLogLine
	 * @param searchLogLine
	 * @param remoteIp
	 */
	void associateSearchLogLineWithRemoteIp(SearchLogLine searchLogLine, String remoteIp) {
		
		if (!remoteIp) {
			return
		}
		
		def ipAddress = IpAddress.findByValue(remoteIp)
		
		if (!ipAddress) {  // create new
		    ipAddress = new IpAddress(value:remoteIp)
			
			// do a geoip lookup
			Location location = geoIpService?.getLocation(remoteIp)
			def countryCode = location?.countryCode?:'unknown';
				
			// create a new country object or update an existing one
			Country country = Country.findByCountryCode(countryCode);
			if (!country) { // create new
			    country = new Country(
					countryCode : countryCode, 
					countryName : location?.countryName?:'unknown',
					).save(failOnError: true, validate: false);
			}
			ipAddress.country = country;
			ipAddress.save(failOnError: true);
		}
		searchLogLine.ipAddress = ipAddress;
		
	}

}
