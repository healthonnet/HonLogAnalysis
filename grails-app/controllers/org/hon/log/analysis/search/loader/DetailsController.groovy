package org.hon.log.analysis.search.loader

import java.util.Map;

import org.hon.log.analysis.search.SearchLogLine;
import org.hon.log.analysis.search.report.DetailsService;
import org.hon.log.analysis.search.report.StatisticsService;




class DetailsController {
	
	DetailsService  detailsService
	

	def index = {
		if(params.paction){
			def paction = params.paction
			params.remove('paction')
			if(paction != 'index')
				redirect(action:paction)
		}
	}
	
	
	def listByUser = {
		[			
					nbUserId:detailsService.listByUser().size(),
					userList:detailsService.listByUser(),
					nbTotal:SearchLogLine.count()
				]
	}

	def listDetailsByUser={
		[
					user:params.id,
					country:detailsService.getLocation(params.id),
					details:detailsService.listDetailsByUser(params.id)
			
			]
		
	}
	def countQueriesPerDay = {
		def l=detailsService.countQueriesPerDay()
		[
					nbTotal:SearchLogLine.count(),
					average:l[0],
					countBy:googleVisualizationDataFromCount(l[1], [category:'user']),
					total:l[2]
				]
	}
	
	def termsByUser = {
		[
					userTerms:googleVisualizationDetails(detailsService.distinctTermsByUser()),
					nbTotal:SearchLogLine.count()
				]
		
		//chain(controller:'jasper',action:'index',model:[data:l],params:params)
		
	}
	
	def djReport = {
		redirect(controller:'djReport', params:[entity:'searchLogLine'])
	}
	
	
	
	
	Map googleVisualizationDataFromCount(countBy, Map options=[:]){
		def ret =[:]
		ret.columns  = [
			[
				'string',
				options.category?:'category'
				
			],
			[
				'number',
				options.count?:'count'
				
			]
		]
		ret.values= []
		if(countBy in Map){
			countBy.each{k, v -> ret.values <<[k, v]}
		}else{
			countBy.eachWithIndex{v, i -> if(v) ret.values << [i, v]}
		}
		ret
	}
	
	
	Map googleVisualizationDetails(countBy, Map options=[:]){
		def ret =[:]
		ret.columns  = [
			[
				'string',
				options.category?:'User'
			],
			[
				'string',
				options.terms?:'Terms'
			]
		]
		ret.values= []
		if(countBy in Map){
			String temp;
			countBy.each{k, v ->
			   temp=''
			   if(!v)
			   	return
				   
			   v.each{String str->
				   temp+=str+', '
				   }
			   temp=temp[0..-3]
			   
			
				ret.values <<[k, temp]}
		}else{
		
			countBy.eachWithIndex{v, i -> if(v) ret.values << [i, '', v]}
		}
		ret
	}
	
	
}
