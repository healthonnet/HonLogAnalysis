package org.hon.log.analysis.search.report

import java.util.List;
import java.util.Map;
import org.exolab.castor.dsml.*
import org.exolab.castor.mapping.xml.Sql
import groovy.sql.Sql
import org.grails.geoip.service.GeoIpService;
import org.hon.log.analysis.search.SearchLogLine
import org.hon.log.analysis.search.query.Term
import com.maxmind.geoip.Location;

class SuggestionsService {
    static transactional = false
    javax.sql.DataSource dataSource;
    //R�cup�ration de la requ�te et du nombre d'occurences de la requ�te
    Map listQuery(options = [:]){
        String query= """select hon_log.search_log_line.id, hon_log.search_log_line.language, hon_log.search_log_line.orig_query, hon_log.search_log_line_terms.search_log_line_id, hon_log.search_log_line_terms.term_id, count(hon_log.search_log_line_terms.term_id) as counter
        from hon_log.search_log_line,hon_log.search_log_line_terms
        where hon_log.search_log_line_terms.search_log_line_id=hon_log.search_log_line.id
        group by hon_log.search_log_line_terms.term_id
        order by counter desc"""
        def db = new Sql(dataSource)
        //S�lection des listes (colonnes) que l'on veut afficher
        return db.rows(query).grep{it[0] != '-'}.collectEntries{row -> [row[2], row[5]]}
    }

    //R�cup�ration des requ�tes en anglais
    Map listEnglishQuery(options = [:]){
        String query= """select hon_log.search_log_line.language, hon_log.search_log_line.orig_query
        from hon_log.search_log_line
        where hon_log.search_log_line.language LIKE '%en%'
        """
        def db = new Sql(dataSource)
        //S�lection des listes (colonnes) que l'on veut afficher
        return db.rows(query).grep{it[0] != '-'}.collectEntries{row -> [row[1], row[2]]}
    }

    //R�cup�ration des requ�tes en arabe
    Map listArabicQuery(options = [:]){
        String query= """select hon_log.search_log_line.language, hon_log.search_log_line.orig_query
        from hon_log.search_log_line
        where hon_log.search_log_line.language LIKE '%ar%'
        """
        def db = new Sql(dataSource)
        //S�lection des listes (colonnes) que l'on veut afficher
        return db.rows(query).grep{it[0] != '-'}.collectEntries{row -> [row[1], row[2]]}
    }

    //Fonction permettant d'afficher les autosuggestions selon le input rentr� par l'utilisateur
    def listQueryBeginWithC(String queryEntry){
        String likeStatement = "${queryEntry}%";
        String query = """
            select hon_log.search_log_line.id, hon_log.search_log_line.language,
                    hon_log.search_log_line_terms.search_log_line_id, 
                    hon_log.search_log_line_terms.term_id, hon_log.search_log_line.orig_query, 
                    count(hon_log.search_log_line_terms.term_id) as counter
            from hon_log.search_log_line,hon_log.search_log_line_terms
            where hon_log.search_log_line_terms.search_log_line_id=hon_log.search_log_line.id 
                    AND hon_log.search_log_line.orig_query LIKE ? 
            group by hon_log.search_log_line_terms.term_id
            order by counter desc"""

        def db = new Sql(dataSource)
        //S�lection des listes (colonnes) que l'on veut afficher
        return  db.rows(query, likeStatement).collect{
            [term: it[4], counter: it[5]]
        }
    }
}

