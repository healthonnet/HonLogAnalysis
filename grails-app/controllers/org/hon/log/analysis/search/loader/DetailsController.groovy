package org.hon.log.analysis.search.loader

import java.util.Map;

import org.hon.log.analysis.search.SearchLogLine;
import org.hon.log.analysis.search.report.DetailsService;
import org.hon.log.analysis.search.report.StatisticsService;




class DetailsController extends LogAnalysisControllerAbst {
	
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
		def listByUserResult = detailsService.listByUser();
		[			
					nbUserId:listByUserResult.size(),
					userList:listByUserResult,
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
					userTerms:googleVisualizationDataFromCount(detailsService.distinctTermsByUser(), [category:'user']),
					nbTotal:SearchLogLine.count()
				]
		
		//chain(controller:'jasper',action:'index',model:[data:l],params:params)
		
	}
	
	def djReport = {
		redirect(controller:'djReport', params:[entity:'searchLogLine'])
	}
		
}
