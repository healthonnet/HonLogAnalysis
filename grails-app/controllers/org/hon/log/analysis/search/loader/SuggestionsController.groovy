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
    SuggestionsService  suggestionsService

    //Pour permettre la redirection vers la bonne page
    def index = {
        // TODO: don't repeat this logic for every controller
        if(params.paction){
            def paction = params.paction
            params.remove('paction')
            if(paction != 'index')
                redirect(action:paction, params: [query:params.query,language:params.language, display:params.display])
        }
    }

    def listQuery = {
        //Dans le controleur on écrit la fonction qui permet de récupérer ce qui a été rentré dans l'interface utilisateur
        def listQueryBeginWithCResult = suggestionsService.listQuery(params.query, params.language);

        if(params.display=="XML"){
            //Pour avoir de l'UTF8 pour l'affichage de l'écriture arabe 
            response.setCharacterEncoding('UTF-8')
            render listQueryBeginWithCResult as XML        
        }
        else if (params.display=="JSON"){
            render listQueryBeginWithCResult as JSON
        }else{

            [   queryList:listQueryBeginWithCResult,
                nbTotal:listQueryBeginWithCResult.size()
            ]
        }
    }
}
