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

    String test() {
        return "HELLO"
    }

    Map listByUser(options = [:]) {
        int limit = options?.limit?:50

        String query = """select sll.user_id, count(*) as counter
                          from search_log_line sll, search_log_line_terms sllt
                          where sll.id=sllt.search_log_line_id
                          group by sll.user_id
                          order by counter desc
                          limit ?"""
        def db = new Sql(dataSource)
        return db.rows(query, [limit]).grep{it[0] != '-'}.collectEntries{row -> [row[0], row[1]]}
    }

    Map listQuery(options = [:]){

        //        //RŽcupŽration de la requte et du nombre d'occurences de la requte
        //        String query= """select hon_log.search_log_line.id, hon_log.search_log_line.orig_query, hon_log.search_log_line_terms.search_log_line_id, hon_log.search_log_line_terms.term_id, count(hon_log.search_log_line_terms.term_id) as counter
        //                         from hon_log.search_log_line,hon_log.search_log_line_terms
        //                         where hon_log.search_log_line_terms.search_log_line_id=hon_log.search_log_line.id
        //                           group by hon_log.search_log_line_terms.term_id
        //                        order by counter desc"""
        //
        //        def db = new Sql(dataSource)
        //        //SŽlection des listes (colonnes) que l'on veut afficher
        //        return db.rows(query).grep{it[0] != '-'}.collectEntries{row -> [row[1], row[4]]}

        //requeteSQL2.sql
        //RŽcupŽration de la requte et du nombre d'occurences de la requte
        String query= """select hon_log.search_log_line.id, hon_log.search_log_line.language, hon_log.search_log_line.orig_query, hon_log.search_log_line_terms.search_log_line_id, hon_log.search_log_line_terms.term_id, count(hon_log.search_log_line_terms.term_id) as counter
        from hon_log.search_log_line,hon_log.search_log_line_terms
        where hon_log.search_log_line_terms.search_log_line_id=hon_log.search_log_line.id
        group by hon_log.search_log_line_terms.term_id
        order by counter desc"""

        def db = new Sql(dataSource)
        //SŽlection des listes (colonnes) que l'on veut afficher
        return db.rows(query).grep{it[0] != '-'}.collectEntries{row -> [row[2], row[5]]}
    }


    Map listEnglishQuery(options = [:]){
        //requeteSQL3.sql
        //RŽcupŽration des requtes en anglais
        String query= """select hon_log.search_log_line.language, hon_log.search_log_line.orig_query
        from hon_log.search_log_line
        where hon_log.search_log_line.language LIKE '%en%'
        """
        def db = new Sql(dataSource)
        //SŽlection des listes (colonnes) que l'on veut afficher
        return db.rows(query).grep{it[0] != '-'}.collectEntries{row -> [row[1], row[2]]}
    }
    
    Map listArabicQuery(options = [:]){
        //requeteSQL3.sql
        //RŽcupŽration des requtes en anglais
        String query= """select hon_log.search_log_line.language, hon_log.search_log_line.orig_query
        from hon_log.search_log_line
        where hon_log.search_log_line.language LIKE '%ar%'
        """
        def db = new Sql(dataSource)
        //SŽlection des listes (colonnes) que l'on veut afficher
        return db.rows(query).grep{it[0] != '-'}.collectEntries{row -> [row[1], row[2]]}
    }
}

