package org.hon.log.analysis.search.report

import java.util.Map;

import org.hon.log.analysis.search.SearchLogLine;

import org.grails.geoip.service.GeoIpService;
import com.maxmind.geoip.Location;
import groovy.sql.Sql
import groovy.time.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat

import org.apache.poi.hssf.record.formula.functions.T

class DetailsService {

	static transactional = false
	
	javax.sql.DataSource dataSource;
	
	Map listByUser(options = [:]) {
		
        //Nombre max d'analyse de User
		int limit = options?.limit?:50
		
		String query = """select sll.user_id, count(*) as counter
                          from search_log_line sll, search_log_line_terms sllt
                          where sll.id=sllt.search_log_line_id
                          group by sll.user_id
                          order by counter desc
                          limit ?"""
		
		def db = new Sql(dataSource)
		
		return db.rows(query, [limit]).grep{it[0] != '-'}.collectEntries{row -> [row[0],row[1]]}
	}


	Map listDetailsByUser(userId){
		Map ret=[:]
		TimeDuration duration
		
		def objList=SearchLogLine.findAllByUserId(userId)
		objList.sort{it.date}
		
		SearchLogLine sll=objList[0]
		
		
		Date old=sll.date
		ret['00"']=sll.termList.toString()
		
		if(objList.size()>1){
			
			for(i in 1..objList.size()-1){
				sll=objList[i]
				duration=TimeCategory.minus(sll.date,old)
				def l=sll.termList
				
				
				if(duration.toMilliseconds()!=0 && l!=[]){
					String strDuration=formatStrDate(duration)
					ret[strDuration]=l
					old=sll.date
				}
			}
		}
			
		log.info("returning $ret")
		ret
	}
	
	
	String formatStrDate(duration){
		
		//format time interval ex: 12d 01h 27' 12''
		int d=duration.days
		int h=duration.hours
		int min=duration.minutes
		int s=duration.seconds
		
		String strDuration=""
		if(d!=0){
			if(d.toString().size()==1)
				strDuration+="0"+d+"d "
			else
			strDuration+=d+"d "
		}
			
		if(h!=0){
			if(h.toString().size()==1)
				strDuration+="0"+h+"h "
			else
			strDuration+=h+"h "
		}
		
		if(min!=0){
			if(min.toString().size()==1)
				strDuration+="0"+min+"' "
			else
				strDuration+=min+"' "
		}
		
		if(s!=0){
			if(s.toString().size()==1)
				strDuration+="0"+s+"''"
			else
				strDuration+=s+"''"
		}
		
		strDuration
	}
	List countQueriesPerDay() {
		
		String query = """SELECT day(date), month(date), year(date), count(*) 
							FROM search_log_line
							group by day(date),month(date),year(date)
                            order by year(date) desc, month(date) desc, day(date) desc"""
		// ordered by date in desc order
							
		def db = new Sql(dataSource)
		Map m = db.rows(query).collectEntries{ row -> 
			
			// zero-pad to two characters to get a nice dd-MM-yyy format
			def dateString = String.format('%02d',row[0]) + '-' +
			                 String.format('%02d',row[1]) + '-' +
							 row[2];
							 
			return [dateString,row[3]]
			}
		
		// calculate total and average
	    def total = m.values().sum{it}
		double avg=total/m.size()
		def result = [avg.round(), m, total]
		log.info('returning result ' + result)
		return result

	}


	public Map distinctTermsByUser(options = [:]){
		
		def limit = options?.limit?:50;
		
		
		// do in sql, it's faster
		
		String query = """select sll.user_id, count(distinct sllt.term_id) as counter
                          from search_log_line sll, search_log_line_terms sllt
                          where sll.id=sllt.search_log_line_id
                          group by sll.user_id
                          order by counter desc
                          limit ?
        """
		def db = new Sql(dataSource)
		return db.rows(query, [limit]).collectEntries { row -> [row[0],row[1]]}
	}
	
	Map sortTerms(mp){
		Map m=[:]
		mp.each {i,List v->
			m[i]=v.sort()
		}
		m
	}
	
	public String getLocation(String user) {
		// just take the first IP address the user is associated with and use that country
		String query = """select c.country_code
                          from search_log_line sll, ip_address ip, country c
                          where sll.ip_address_id = ip.id
                              and ip.country_id = c.id
                              and sll.user_id = ?
                          limit 1"""
		def db = new Sql(dataSource)
		return db.firstRow(query, [user])[0];
	}
	
	
}
