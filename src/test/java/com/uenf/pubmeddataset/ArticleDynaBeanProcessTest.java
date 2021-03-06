/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uenf.pubmeddataset;

import java.util.List;
import com.uenf.pubmeddataset.internet.DownloadConfiguration;
import com.uenf.pubmeddataset.internet.ParameterName;
import com.uenf.pubmeddataset.util.ArticleAttribute;
import com.uenf.pubmeddataset.util.DynaArticle;
import com.uenf.pubmeddataset.util.EmptyObjectException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import com.uenf.pubmeddataset.internet.ArticleDownloader;
import java.util.Iterator;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static com.uenf.pubmeddataset.internet.ParameterName.*;

/**
 *
 * @author Kirill
 */
public class ArticleDynaBeanProcessTest {

    private static List<DynaArticle> articles;

    @BeforeClass
    public static void setUp() {
        DownloadConfiguration config = new DownloadConfiguration(ABSTRACT, TITLE);
        ArticleDownloader downloader = new ArticleDownloader(config);
        articles = downloader.getDynaArticles("mycobacterium tuberculosis", 10);
    }

    @Test
    public void downloadConfigurationShouldHaveParameterNamesAndClasses() throws Exception {
        assertEquals("AbstractText", ParameterName.ABSTRACT.getAttributeName());
        assertEquals("MeshTerms", ParameterName.MESH_TERMS.getAttributeName());
        assertEquals("Title", ParameterName.TITLE.getAttributeName());
        assertEquals("AuthorKeyWords", ParameterName.AUTHOR_KWS.getAttributeName());
        assertEquals(Class.forName("java.lang.String"), ParameterName.ABSTRACT.getParameterClass());
        assertEquals(Class.forName("java.util.Set"), ParameterName.MESH_TERMS.getParameterClass());
        assertEquals(Class.forName("java.lang.String"), ParameterName.TITLE.getParameterClass());
        assertEquals(Class.forName("java.util.Set"), ParameterName.AUTHOR_KWS.getParameterClass());
    }

    @Test
    public void createArticleDynaBeanWithDownloadConfigurationFile() throws Exception {
        DownloadConfiguration config = new DownloadConfiguration(ABSTRACT, TITLE);
        DynaArticle article = new DynaArticle(config);
        Map attributes = article.getAttributes();
        assertAttributesContain(attributes, "AbstractText", "Title");
    }

    @Test
    public void throwExceptionWhenInstantiateADynaArticleWithNoAtributesInConfigurations() throws Exception {
        DownloadConfiguration config = new DownloadConfiguration();
        try {
            DynaArticle a = new DynaArticle(config);
            a.getAttributes();
            fail("Expected Exception");
        } catch (EmptyObjectException e) {
            assert (true);
        }
    }

    @Test
    public void throwExceptionWhenRequestInexistentAttributeFromDynaArticle() throws Exception {
        DownloadConfiguration config = new DownloadConfiguration(PMID);
        DynaArticle a = new DynaArticle(config);
        try {
            a.getAttribute(ABSTRACT);
            fail("Should have thrown Exception");
        } catch (Exception e) {
            assert (true);
        }
    }

    @Test
    public void shouldDownloadRequestedParameters() throws Exception {
        assertArticlesContainData(articles);
    }

//    @Test
//    public void shouldDownloadMeshTermsAndAuthorKwsAsSet() throws Exception {
//        DownloadConfiguration config = new DownloadConfiguration(AUTHOR_KWS, MESH_TERMS);
//        ArticleDownloader downloader = new ArticleDownloader(config);
//        Set<DynaArticle> articles = downloader.getDynaArticles("whey protein", 2000);
//        
//        assertArticlesContainData(articles);
//        
//        for (DynaArticle article : articles) {
//            Set<String> meshTerms = (Set<String>) article.getAttribute(MESH_TERMS).getValue();
//            Set<String> authorKWs = (Set<String>) article.getAttribute(AUTHOR_KWS).getValue();
//            assertNotNull(meshTerms);
//            assertTrue(!meshTerms.isEmpty());
//            assertNotNull(authorKWs);
//            assertTrue(!authorKWs.isEmpty());
//        }
//    }
    
    @Test
    public void throwEmptyObjectExceptionWhenRequestEmptyGeneratedKwsList() throws Exception {
        DynaArticle da = articles.iterator().next();
        try {
            Collection<Object> genKws = da.getGeneratedKws();
            fail("Sould have thrown Exception");
        } catch (EmptyObjectException e) {
            assert (true);
        }
    }
    
    @Test
    public void allowDownloadOfNullValues() {
        DownloadConfiguration config = new DownloadConfiguration(MESH_TERMS);
        ArticleDownloader downloader = new ArticleDownloader(config,false);
        List<DynaArticle> articles = downloader.downloadArticles(downloader.getIds("tuberculosis", 0, 50));
        assertTrue(articles.size() == 50);
    }

    private void assertAttributesContain(Map attributes, String... attributesStr) {
        assertEquals(attributesStr.length, attributes.size());
        for (String attribute : attributesStr) {
            assertTrue(attributes.containsKey(attribute));
        }
    }

    private void assertArticlesContainData(List<DynaArticle> articles) throws EmptyObjectException, ClassNotFoundException, Exception {
        if (articles.isEmpty()) {
            fail("There are no articles in the set evaluated");
        }
        for (Iterator<DynaArticle> aIt = articles.iterator(); aIt.hasNext();) {
            DynaArticle a = aIt.next();
            for (ArticleAttribute attr : a.getAttributes().values()) {
                assertNotNull(attr.getValue());
            }
        }
    }
}
