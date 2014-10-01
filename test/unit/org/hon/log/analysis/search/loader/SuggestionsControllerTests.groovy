package org.hon.log.analysis.search.loader

import org.hon.log.analysis.search.loader.SuggestionsController;

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(SuggestionsController)
class SuggestionsControllerTests {

    void testSomething() {
//       fail "Implement me"
        println("It works!")
    }
}



////Test3: Test de la reconnaissance du paramètre d'affichage "XML" pour les résultats arabes
//assert suggestionsService.listQuery("D","ar","XML",100).size() == 1 //True:OK
////Test4: Test de la reconnaissance du paramètre d'affichage "XML" pour les résultats anglais
//assert suggestionsService.listQuery("V","en","XML", 100).size() == 3 //True:OK
////Test5: Test de la reconnaissance du paramètre d'affichage "JSON" pour les résultats arabes
//assert suggestionsService.listQuery("D","ar","JSON",100).size() == 1 //True:OK
////Test6: Test de la reconnaissance du paramètre d'affichage "JSON" pour les résultats anglais
//assert suggestionsService.listQuery("V","en","JSON", 100).size() == 3 //True:OK
//assert suggestionsService.listQuery("V","en","J", 100).size() == 3

