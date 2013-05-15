package org.hon.log.analysis.search.loader

import org.hon.log.analysis.*
import org.hon.log.analysis.search.SearchLogLine;
import org.hon.log.analysis.search.report.DetailsService;
import org.hon.log.analysis.search.report.SuggestionsService;
import grails.converters.*
import java.lang.Object
import java.util.Map;
import java.*
import org.hon.log.analysis.search.report.StatisticsService;
import org.hon.log.analysis.search.query.Term

class SuggestionsController  {

    //Pour permettre la redirection vers la bonne page
    def index = {
        // TODO: don't repeat this logic for every controller
        if(params.paction){
            def paction = params.paction
            params.remove('paction')
            if(paction != 'index')
                redirect(action:paction)
        }
    }

    SuggestionsService  suggestionsService

    def listQuery = {
        def listQueryResult = suggestionsService.listQuery();
        [
            queryList:listQueryResult,
            nbTotal:listQueryResult.size()
        ]
    }

    def listEnglishQuery={
        def listEnglishQueryResult = suggestionsService.listEnglishQuery();
        [
            EnglishQueryList:listEnglishQueryResult,
            nbTotalEnglishQuery:listEnglishQueryResult.size()
        ]
    }

    def listArabicQuery={
        def listArabicQueryResult = suggestionsService.listArabicQuery();
        [
            ArabicQueryList:listArabicQueryResult,
            nbTotalArabicQuery:listArabicQueryResult.size()
        ]
    }

    //Fonction permettant d'afficher les autosuggestions selon le input rentr� par l'utilisateur
    def listQueryBeginWithC={
        //Dans le controleur on �crit la fonction qui permet de r�cup�rer ce qui a �t� rentr� dans l'interface utilisateur
        flash.message="Query succeed"
        def listQueryBeginWithCResult = suggestionsService.listQueryBeginWithC(params.query);
        [
            BeginWithCQueryList:listQueryBeginWithCResult,
            nbTotalBeginWithCQuery:listQueryBeginWithCResult.size()
        ]
    }
}
