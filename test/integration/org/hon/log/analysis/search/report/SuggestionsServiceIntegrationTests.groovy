package org.hon.log.analysis.search.report

import grails.test.*
import groovy.json.JsonSlurper

import org.hon.log.analysis.search.SearchLogLine;
import org.hon.log.analysis.search.loader.SearchLogLoaderService
import org.junit.Ignore;
import org.springframework.core.io.ClassPathResource;

class SuggestionsServiceIntegrationTests extends GroovyTestCase {
    SearchLogLoaderService searchLogLoaderService

    SuggestionsService suggestionsService

    def sessionFactory;

    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    def testListQuery(){
        //Fichier utilisé lors des premiers tests
//        searchLogLoaderService.load('hon', new ClassPathResource('resources/hon-2.txt').file)
        //Fichier utilisé lors des seconds tests (tri des requêtes avec Solr) (4 fichiers de log)
//        searchLogLoaderService.load('hon', new ClassPathResource('resources/hon-3.txt').file)

        //Test1: Test de la recherche du bon nombre de requêtes arabes: OK
//        assert suggestionsService.listQuery("g","ar", 100).size() == 1 //True:OK
        //assert suggestionsService.listQuery("g","ar", 100).size() == 2 //False:OK
//        assert suggestionsService.listQuery("b","ar", 100).size() == 1 //True:OK
        //Attention à bien respecter la casse (fait la différence entre "d" et "D")
//        assert suggestionsService.listQuery("D","ar", 100).size() == 1 //True:OK
        //assert suggestionsService.listQuery("d","ar", 100).size() == 1 //False:OK

        //Test2: Test de la recherche du bon nombre de requêtes anglaises: OK
        //assert suggestionsService.listQuery("V","en", 100).size() == 1 //False:OK
//        assert suggestionsService.listQuery("V","en", 100).size() == 3 //True:OK
//        assert suggestionsService.listQuery("I","en", 100).size() == 4 //True:OK
//        assert suggestionsService.listQuery("T","en", 100).size() == 1 //True:OK
//        assert suggestionsService.listQuery("t","en", 100).size() == 1 //True:OK
//        assert suggestionsService.listQuery("O","en", 100).size() == 1 //True:OK
//        assert suggestionsService.listQuery("F","en", 100).size() == 1 //True:OK
        //assert suggestionsService.listQuery("F","en", 100).size() == 4 //False:OK
        //assert suggestionsService.listQuery("S","en", 100).size() == 1 //False:OK
//        assert suggestionsService.listQuery("s","en", 100).size() == 1 //True:OK

        //Test3: Test du paramètre de limite du nombre de résultats: OK
//        assert suggestionsService.listQuery("s","en", 10).size() <= 10 //True:OK
//        assert suggestionsService.listQuery("s","en", 50).size() <= 50 //True:OK
//        assert suggestionsService.listQuery("s","en", 100).size() <= 100 //True:OK
    }
        
    def testListQueryAfterVerificationWithSolr(){
        //Test 4: Test du bon tri à partir de Solr 

//        //Test pour les requêtes arabes
//        def slurper = new JsonSlurper()
//        def lien
//        def resultat
//        def counterSolr
//
//        lien= "http://services.hon.ch:7010/solr/select/?rows=0&defType=edismax&qf=text_ar%20title_ar&q=أكلات&wt=json"
//        resultat = slurper.parseText(lien.toURL().text)
//        //Récupération du nombre de présence de ce terme dans KAAHE
//        counterSolr= resultat.response.numFound
//
//        //Test pour la requête "أكلات"
//        assert counterSolr == 447
        
        //Test pour les requêtes anglaises
		def solrProxy = "http://www.kaahe.org/en/utils/solr-proxy.php"
        def slurper1 = new JsonSlurper()
        def lien1
        def resultat1
        def counterSolr1
        
        lien1= solrProxy + "?rows=0&defType=edismax&qf=text_en%20title_en&q=nose&wt=json"
        resultat1 = slurper1.parseText(lien1.toURL().text)
        //Récupération du nombre de présence de ce terme dans KAAHE
        counterSolr1= resultat1.response.numFound

        //Test pour la requête "nose"
        assert counterSolr1 == 132
       
    }       
}