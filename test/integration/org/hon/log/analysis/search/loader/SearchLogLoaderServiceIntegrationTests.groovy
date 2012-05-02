package org.hon.log.analysis.search.loader

import java.io.File;

import grails.test.*

import org.hon.log.analysis.search.LoadedFile
import org.hon.log.analysis.search.SearchLogLine
import org.springframework.core.io.ClassPathResource

class SearchLogLoaderServiceIntegrationTests extends GroovyTestCase {
	SearchLogLoaderService searchLogLoaderService

	protected void setUp() {
		super.setUp()
	}

	protected void tearDown() {
		super.tearDown()
	}

	void test_service_alive() {
		assert searchLogLoaderService
	}

	void test_loaders_list(){
		assert searchLogLoaderService.loaders.size() == 3
	}
	
	void test_check_loaded_files(){
		searchLogLoaderService.load('tel', new ClassPathResource('resources/tel-1.log').file)
		
		List lf = LoadedFile.list();
		assert lf.size() == 1
		LoadedFile f = lf[0]
		assert f.filename.endsWith('tel-1.log')
		assert f.size() == 22
		assert f.loadedAt
		
		SearchLogLine.list().each{println it}
		
	}
	
	void test_dbs_loading() {

		int n = searchLogLoaderService.load('dbs', testFile('dbs-1.log'), [origFile:'dbs-1.log'])
		assert n == 11
		assert SearchLogLine.count() == 11

		LoadedFile loadedfile =LoadedFile.findByFilename('dbs-1.log')
		assert loadedfile
		assert loadedfile.size() == 11

		SearchLogLine sll = SearchLogLine.list()[2]
		assert sll
		assert sll.terms.size()>0
	}


	void test_tel_loading() {
		int n = searchLogLoaderService.load('tel', testFile('tel-1.log'))
		assert n == 22
		assert SearchLogLine.count() == 22
	}

	void test_hon_loading() {
		int n = searchLogLoaderService.load('hon', testFile('hon-1.log'))
		assert n == 6
		assert SearchLogLine.count() == 6
	}

	File testFile(String filename){
		new ClassPathResource('resources/'+filename).file
	}

}
