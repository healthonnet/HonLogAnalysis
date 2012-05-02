package org.hon.log.analysis.search

import grails.test.*

import java.io.File

import org.hon.log.analysis.search.report.StatisticsService
import org.springframework.core.io.ClassPathResource

class StopWordRemoverTests extends GrailsUnitTestCase {
	StopWordRemover remover = [:]
	protected void setUp() {
		super.setUp()
	}

	protected void tearDown() {
		super.tearDown()
	}

	void test_one_term() {
		assert remover.cleanTerms(['Syndrome']) == ['syndrome']
	}
	void test_empty() {
		assert remover.cleanTerms(['Syndrome', '']) == ['syndrome']
	}

	void test_stopwords() {
		assert remover.cleanTerms(['Syndrome', 'de', 'prout']) == ['syndrome', 'prout']
		assert remover.cleanTerms(['auxiliaire']) == ['auxiliaire']
		assert remover.cleanTerms(['le', 'jeu', 'de', "l'avion"]) == ['jeu', 'avion']
		assert remover.cleanTerms(['le', 'jeu', 'de', "11","4"]) == ['jeu']
	}
}
