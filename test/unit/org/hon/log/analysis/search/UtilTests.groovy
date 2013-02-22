package org.hon.log.analysis.search

import org.junit.*

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
class UtilTests {

    @Test
    void testRemoveFourByteUtf8() {

        assert StringUtil.replaceFourByteUtf8("hello world").getBytes('UTF-8') == "hello world".getBytes('UTF-8');
        assert StringUtil.replaceFourByteUtf8("foo \uDBC0\uDC7A bar").getBytes('UTF-8') ==
                "foo \uFFFD bar".getBytes('UTF-8')

    }
}
