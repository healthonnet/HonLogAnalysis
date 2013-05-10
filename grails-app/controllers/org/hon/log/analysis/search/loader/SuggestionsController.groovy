package org.hon.log.analysis.search.loader

import org.hon.log.analysis.search.SearchLogLine;
import org.hon.log.analysis.search.report.DetailsService;
import org.hon.log.analysis.search.report.SuggestionsService;
import grails.converters.*
import java.util.Map;
import org.hon.log.analysis.search.report.StatisticsService;
import org.hon.log.analysis.search.query.Term

class SuggestionsController  {

    SuggestionsService  suggestionsService
    def listByUser = {
        def listByUserResult = suggestionsService.listByUser();
        [
            nbUserId:listByUserResult.size(),
            userList:listByUserResult,
            nbTotal:SearchLogLine.count()
        ]
    }
    def listQuery = {
        def listQueryResult = suggestionsService.listQuery();
       println  listQueryResult
        [
            queryList:listQueryResult,
            nbTotal:listQueryResult.size()
        ]
    }
    
    def listEnglishQuery={
        def listEnglishQueryResult = suggestionsService.listEnglishQuery();
        println  listEnglishQueryResult
         [
             EnglishQueryList:listEnglishQueryResult,
             nbTotalEnglishQuery:listEnglishQueryResult.size()
         ]
    }
    
    def listArabicQuery={
        def listArabicQueryResult = suggestionsService.listArabicQuery();
        println  listArabicQueryResult
         [
             ArabicQueryList:listArabicQueryResult,
             nbTotalArabicQuery:listArabicQueryResult.size()
         ]
    }
}
