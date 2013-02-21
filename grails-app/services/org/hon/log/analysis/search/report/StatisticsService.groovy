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
		String query = """select nb_terms,count(id) as counter
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
	
	Map countByReferer(options=[:]){
		int limit = options?.limit?:10
		
		//if count(*) is used, it is also shown the number of queries with referer = NULL
		//TODO: Determine weather it is better to use count(referer) or not.
		def query = """
			select referer, count(referer) as counter
			from search_log_line
			where referer is not null
			group by referer
			order by counter desc
			"""
		def totalCount = 0
		
		def db = new Sql(dataSource)
		Map refererCounts = [:]
		db.eachRow(query) { row ->
			def refererName = row[0]
			refererCounts[refererName] = row[1]
			totalCount += row[1]
		}
		
		Map sortedRefererCount = refererCounts
		if (sortedRefererCount.size() > limit) {
			//Sort the map by value to drop the not used entries
			sortedRefererCount = refererCounts.sort {it.value}
			sortedRefererCount = sortedRefererCount.drop(refererCounts.size() - limit)	
		}
		
		//Sort the map by value again, but this time in reverse order
		sortedRefererCount = sortedRefererCount.sort {a, b -> b.value <=> a.value}
		
		return [totalCount:totalCount, refererCounts:sortedRefererCount]
	}
	
	Map countBySession(){
		
		def query = """
			SELECT numQueries as sessionSize, count(*) as counts from 
				(
				SELECT count(*) as numQueries
				FROM search_log_line 
				group by session_id 
				) as Summary
			group by numQueries
			"""
		def numSessions = 0
	
		def db = new Sql(dataSource)
		Map sessionsCounts = [:]
		db.eachRow(query) { row ->
			sessionsCounts[row[0]] = row[1]
			numSessions += row[1]
		}
		return [totalCount:numSessions, sessionCounts:sessionsCounts]
	}
	
	Map countByQueries(options=[:]){
		int limit = options?.limit?:50
		def query = """
			SELECT orig_query as query, count(orig_query)
			FROM search_log_line
			GROUP BY orig_query
			"""
		
		def db = new Sql(dataSource)
		Map queryCounts = [:]
		db.eachRow(query) { row ->
			queryCounts[row[0].toString()] = row[1]
		}
		
		def numUniqueQueries = queryCounts.size()
		
		Map sortedQueryCount = queryCounts
		if (queryCounts.size() > limit){
			sortedQueryCount = queryCounts.sort {it.value}
			sortedQueryCount = sortedQueryCount.drop(queryCounts.size() - limit)
		}
		
		//Sort the map by value again, but this time in reverse order
		sortedQueryCount = sortedQueryCount.sort {a, b -> b.value <=> a.value}
		
		return [totalCount:numUniqueQueries, queryCounts:sortedQueryCount]
		
	}
}
