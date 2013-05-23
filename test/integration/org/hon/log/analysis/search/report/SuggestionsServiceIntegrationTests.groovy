package org.hon.log.analysis.search.report

import grails.test.*

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
        searchLogLoaderService.load('hon', new ClassPathResource('resources/hon-2.txt').file)

        //Test1: Test de la recherche du bon nombre de requêtes arabes: OK
        assert suggestionsService.listQuery("g","ar", 100).size() == 1 //True:OK
        //assert suggestionsService.listQuery("g","ar", 100).size() == 2 //False:OK
        assert suggestionsService.listQuery("b","ar", 100).size() == 1 //True:OK
        //Attention à bien respecter la casse (fait la différence entre "d" et "D")
        assert suggestionsService.listQuery("D","ar", 100).size() == 1 //True:OK
        //assert suggestionsService.listQuery("d","ar", 100).size() == 1 //False:OK

        //Test2: Test de la recherche du bon nombre de requêtes anglaises: OK
        //assert suggestionsService.listQuery("V","en", 100).size() == 1 //False:OK
        assert suggestionsService.listQuery("V","en", 100).size() == 3 //True:OK
        assert suggestionsService.listQuery("I","en", 100).size() == 4 //True:OK
        assert suggestionsService.listQuery("T","en", 100).size() == 1 //True:OK
        assert suggestionsService.listQuery("t","en", 100).size() == 1 //True:OK
        assert suggestionsService.listQuery("O","en", 100).size() == 1 //True:OK
        assert suggestionsService.listQuery("F","en", 100).size() == 1 //True:OK
        //assert suggestionsService.listQuery("F","en", 100).size() == 4 //False:OK
        //assert suggestionsService.listQuery("S","en", 100).size() == 1 //False:OK
        assert suggestionsService.listQuery("s","en", 100).size() == 1 //True:OK

        //Test3: Test du paramètre de limite du nombre de résultats: OK
        assert suggestionsService.listQuery("s","en", 10).size() <= 10 //True:OK
        assert suggestionsService.listQuery("s","en", 50).size() <= 50 //True:OK
        assert suggestionsService.listQuery("s","en", 100).size() <= 100 //True:OK
        
    }
}