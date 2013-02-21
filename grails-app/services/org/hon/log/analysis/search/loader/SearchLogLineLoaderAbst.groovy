package org.hon.log.analysis.search.loader

import org.grails.geoip.service.GeoIpService;
import org.hon.log.analysis.search.Country;
import org.hon.log.analysis.search.IpAddress
import org.hon.log.analysis.search.SearchLogLine;
import org.hon.log.analysis.search.Referer;
import org.hon.log.analysis.search.Domain;

import com.google.common.base.Strings;

import com.maxmind.geoip.Location
import org.hon.log.analysis.search.util.URLUtil

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

    /**
     * Create a new Domain if necessary and associate it with the given searchLogLine.referer
     * @param searchLogLine
     * @param remoteIp
     */
    void associateSearchLogLineWithRefererDomain(SearchLogLine searchLogLine, String refererUrl) {

        if (!refererUrl) {
            return
        }


        def referer = Referer.findByUrl(refererUrl)

        if (!referer) {  // create new
            referer = new Referer(url:refererUrl)

            // domain extraction
            String refererDomain = URLUtil.getDomainFromUrl(refererUrl);

            if (refererDomain != null) { // not an invalid domain

                // create a new domain object or update an existing one
                Domain domain = Domain.findByValue(refererDomain);
                if (!domain) { // create new
                    domain = new Domain(
                            value : refererDomain
                    ).save(failOnError: true, validate: false);
                }
                referer.domain = domain;
            }
            referer.save(failOnError: true);
        }
        searchLogLine.referer = referer;

    }

}
