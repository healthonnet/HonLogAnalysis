package org.hon.log.analysis.search.report


import java.util.Map
import groovy.sql.Sql

import org.grails.geoip.service.GeoIpService;
import org.hon.log.analysis.search.SearchLogLine
import org.hon.log.analysis.search.query.Term

import com.maxmind.geoip.Location;

class StatisticsService {
	static transactional = false
	
	javax.sql.DataSource dataSource;

	/**
	 * 
	 * @return the total number of queries done counted by language
	 */
	Map countByLanguage() {
		
		def UNKNOWN_STRING = "(Unknown)"; // TODO: i18n
		
		String query = """select language, count(*) from search_log_line group by language"""
		def db = new Sql(dataSource)
		return db.rows(query).collectEntries { row -> [row[0]?:UNKNOWN_STRING, row[1]]}
	}
	/**
	 * 
	 * @return the total number of queries counted by query length (number of terms)
	 */
	Map countByQueryLength() {
		String query = """select nb_terms,count(distinct orig_query) as counter
                          from search_log_line
                          group by nb_terms 
                          order by nb_terms """
		def db = new Sql(dataSource)
		return db.rows(query).collectEntries { row -> [row[0],row[1]]}
	}

	/**
	 *
	 * list the most used terms
	 *
	 * options.limit the max number terms (50 default)
	 *
	 * @return a sort map term->count
	 */
	Map mostUsedTerms(options = [:]){
		// do in sql; it's faster
		
		int limit = options.limit?:50
        
		String query = """
                select t.value, grouped.counter
                from (
                    select term_id, count(term_id) as counter
                    from search_log_line_terms
                    group by term_id
                    order by counter desc
                    limit ?
                ) grouped
                join term t on t.id=grouped.term_id
                """

		def db = new Sql(dataSource)
		
		return db.rows(query, [limit]).collectEntries { row -> [row[0],row[1]]}
	}

	Map countriesByTerm(term){

		// do in sql; it's faster
		
		String query = """select c.country_code, count(*), c.country_name
            from country c, ip_address ip, search_log_line sll, search_log_line_terms sllt, term t
            where c.id = ip.country_id
                and ip.id = sll.ip_address_id
                and sll.id = sllt.search_log_line_id
                and sllt.term_id = t.id
                and t.value = ?
            group by c.id
        """
		
		def db = new Sql(dataSource)
		
		return db.rows(query, [term]).collectEntries{ row -> [row[0],[row[1],row[2]]]}
	}
	
	Map mostFrequentTermCoOccurence(options=[:]){
		
		int limit = options?.limit?:50
		
		String query = """select t1.value as firstTerm, t2.value as secondTerm, count(*) as counter
                          from term t1, search_log_line_terms sllt1, search_log_line_terms sllt2, term t2
                          where t1.id=sllt1.term_id 
                              and sllt1.search_log_line_id = sllt2.search_log_line_id
                              and sllt2.term_id = t2.id
                              and t1.value < t2.value
                          group by t1.value,t2.value
                          order by counter desc, t1.value, t2.value
                          limit ?"""
		
		def db = new Sql(dataSource)
		
		return db.rows(query, [limit]).collectEntries {row -> [row[0]+"|"+row[1], row[2]]}
	}

	Map countByCountry(options=[:]){
		
		// do in raw sql because it's faster
		
		def db = new Sql(dataSource)
		def query = """
            select country_code, country_name, sum_ip_counts
            from (
                select country_id, sum(ip_count) as sum_ip_counts
                from (
                    select ip_address_id, count(ip_address_id) as ip_count
                    from search_log_line
                    group by ip_address_id
                ) grouped
                join ip_address ip on grouped.ip_address_id = ip.id
                group by ip.country_id
            ) grouped2
            join country c on grouped2.country_id = c.id"""
		def totalCount = 0;
		Map countryCodeCounts=[:]

		db.eachRow(query) { row ->
			def countryCode = row[0];
			def countryName = row[1];
			def logCount = row[2];
			
			countryCodeCounts[countryCode] = [logCount, countryName]
			totalCount += logCount;
		}
		[totalCount : totalCount,
			countryCodeCounts : countryCodeCounts]
	}

}
