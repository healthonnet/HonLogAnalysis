package org.hon.log.analysis.search.report

import java.util.Map;

import org.hon.log.analysis.search.SearchLogLine;
import org.hon.log.analysis.search.query.Term;

import org.grails.geoip.service.GeoIpService;
import com.maxmind.geoip.Location;
import groovy.time.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat

import org.grails.geoip.service.GeoIpService;
import com.maxmind.geoip.Location;

class DetailsService {

	GeoIpService geoIpService
	static transactional = true

	Map listByUser() {
		
		Map m=[:]
		List l=SearchLogLine.list().each {it->
			if (!it.userId || it.userId=='-'){
			return;
			}
			if (it.termList.size()==0){
				return;
			}
			
			String userId= it.userId
			if(m[userId] == null){
				m[userId]=[]
				
			}
			
			m[userId]<<it
			
		};
	
		
		
		m=m.sort {-it.value.size() }
		
		m
	}
	/*
	double averagedTimeByUser(){
		
		def stD,eD
		int nr=0
		long sum=0
		
		Map m=listByUser()
		m.each {i,List k->
			Map temp=[:]
			k.each {SearchLogLine sll->
				Date date=sll.date	
				def dt=date.format("yyyy-MM-dd")
				
				if(temp[dt] == null){
					temp[dt]=[]	
				}
				
				temp[dt]<<sll
			}
			
			temp.each {j,List l->
				l.sort{it.date}
				if(l.size()>1)
				{
					stD=l.first().date
					eD=l.last().date
					println stD
					println eD
					TimeDuration duration=TimeCategory.minus(eD,stD)
					sum+=duration.toMilliseconds()
					nr++
				}
				
				
			}
			
		};
		
		 double res=(sum/nr)/60000
		
		res.round(2)
	}
	*/
	Map listDetailsByUser(userId){
		Map ret=[:]
		TimeDuration duration
		
		Map m=listByUser()
		def objList=m[userId]
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
			
		generateFile(ret,userId)
		ret
	}
	
	void generateFile(Map details,userId){
		
		
		try{
			// Create file
			FileWriter fw = new FileWriter("userDetails"+userId+".txt");
			BufferedWriter out = new BufferedWriter(fw);
			details.each {k,v->
			out.write(k+" "+v);
			out.newLine()
			
			}
			//Close the output stream
			out.close();
			println "test"
			}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
			}
		
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
		
		Map m=[:]
		int total=0
		List l=SearchLogLine.list().sort{it.date}.each {it->
			if(it.termList.size()==0){
				return;
			}
			DateFormat df2 = new SimpleDateFormat("dd-MM-yyyy")
			String dt=df2.format(it.date)
			if(m[dt] == null){
				m[dt]=0
				
			}
			
			m[dt]++
			total++		
			
		};
	
		//m=m.collect(it).reverse()	
		//m.sort{a,b-> b.key <=> a.key}
	
		// ordered by date in desc order
		Map ret=[:]
		m.reverseEach {it->
				ret[it.key]=it.value
			}
		
		double avg=total/m.size()
		[avg.round(), ret,total]
		

	}
	

	Map distinctTermsByUser(){
		Map userTerms=[:]
		List l
		
		SearchLogLine.list().each{SearchLogLine sll ->
			
			if (!sll.userId || sll.userId=='-')
				return

				
			if(userTerms[sll.userId]==null){
					userTerms[sll.userId]=[];		
					
			}
			
			l=sll.termList;	
			println l
			if(l){
				l.each {String t->
					if(!userTerms[sll.userId].contains(t))
						userTerms[sll.userId]<< t;
						
				}
			}
		}
	   
		userTerms
		//sortTerms(userTerms)
		
	}
	
	Map sortTerms(mp){
		Map m=[:]
		mp.each {i,List v->
			m[i]=v.sort()
		}
		m
	}
	
	String getLocation(ip){
		
		Location location = geoIpService.getLocation(ip[0..ip.size()-18])
		location?.countryName?:'unknown'
		
}
	
	
}
