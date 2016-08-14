package jwm.ir.service;

import jwm.ir.entity.Page;
import jwm.ir.entity.dao.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * Created by Jeff on 2016-07-27.
 */
public class ServiceImplTest {

	private PageDao pageDao;
	private PageLinkDao pageLinkDao;
	private DomainDao domainDao;
	private SessionFactory sessionFactory;
	private DaoFactory daoFactory;
	private Session session;

	@Test
	public void test_add_document_term_when_term_does_not_exist_should_add_term_too() {

		ServiceImpl sut = new ServiceImpl(sessionFactory, daoFactory);

		PageTermDao pageTermDao = mock(PageTermDao.class);
		when(daoFactory.createPageTermDao(session)).thenReturn(pageTermDao);

		long pageId = 1;
		Map<String, Integer> terms = new HashMap<>();
		terms.put("hello", 1);
		terms.put("world", 2);

		sut.addDocumentTerms(pageId, terms);

		verify(pageTermDao).create(1, "hello", 1);
		verify(pageTermDao).create(1, "world", 2);
	}

	@Test
	public void test_page_already_exists_do_not_throw() {

		String url = "www.google.com/a";
		String parentUrl = "www.google.com/b";
		Service sut = new ServiceImpl(sessionFactory, daoFactory);
		when(pageDao.pageExists(url)).thenReturn(true);
		sut.addUrlForCrawling(url, parentUrl);

	}

	@Test
	public void test_page_does_not_exist_domain_does_not_exist___page_and_domain_created() {
		String url = "www.google.com/a";
		Page page = new Page();
		page.setUrl(url);
		String parenturl = "www.google.com/b";
		ServiceImpl sut = new ServiceImpl(sessionFactory, daoFactory);
		when(pageDao.pageExists(url)).thenReturn(false);
		when(pageDao.create(url, domainDao)).thenReturn(page);
		when(domainDao.domainExists("google.com")).thenReturn(false);
		sut.addUrlForCrawling(url, parenturl);

		verify(pageDao).create(url, domainDao);
		verify(domainDao).create("google.com");
		verify(domainDao, times(0)).getDomain("google.com");
	}

	@Test
	public void test_page_does_not_exist_domain_does_exist___page_is_created_domain_is_not() {
		String parentUrl = "www.google.com/b";
		String url = "www.google.com/a";
		Page page = new Page();
		page.setUrl(url);
		ServiceImpl sut = new ServiceImpl(sessionFactory, daoFactory);
		when(pageDao.pageExists(url)).thenReturn(false);
		when(pageDao.create(url, domainDao)).thenReturn(page);
		when(domainDao.domainExists("google.com")).thenReturn(true);
		sut.addUrlForCrawling(url, parentUrl);

		verify(pageDao).create(url, domainDao);
		verify(domainDao).getDomain("google.com");
		verify(domainDao, times(0)).create("google.com");
	}


	@Before
	public void setup() {
		sessionFactory = mock(SessionFactory.class);
		session = mock(Session.class);
		when(sessionFactory.openSession()).thenReturn(session);
		Transaction tx = mock(Transaction.class);
		when(session.beginTransaction()).thenReturn(tx);
		pageDao = mock(PageDao.class);
		domainDao = mock(DomainDao.class);
		daoFactory = mock(DaoFactory.class);
		pageLinkDao = mock(PageLinkDao.class);
		when(daoFactory.createDomainRepository(session)).thenReturn(domainDao);
		when(daoFactory.createPageRepository(session)).thenReturn(pageDao);
		when(daoFactory.createPageLinkRepository(session)).thenReturn(pageLinkDao);
	}
}
