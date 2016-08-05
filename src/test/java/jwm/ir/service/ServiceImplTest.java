package jwm.ir.service;

import jwm.ir.domain.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * Created by Jeff on 2016-07-27.
 */
public class ServiceImplTest {

	private PageRepository pageRepository;
	private PageLinkRepository pageLinkRepository;
	private DomainRepository domainRepository;
	private SessionFactory sessionFactory;
	private RepositoryFactory repositoryFactory;


	@Test
	public void test_page_already_exists_do_not_throw() {

		String url = "www.google.com/a";
		String parentUrl = "www.google.com/b";
		Service sut = new ServiceImpl(sessionFactory, repositoryFactory);
		when(pageRepository.pageExists(url)).thenReturn(true);
		sut.addUrlForCrawling(url, parentUrl);

	}

	@Test
	public void test_page_does_not_exist_domain_does_not_exist___page_and_domain_created() {
		String url = "www.google.com/a";
		Page page = new Page();
		page.setUrl(url);
		String parenturl = "www.google.com/b";
		ServiceImpl sut = new ServiceImpl(sessionFactory, repositoryFactory);
		when(pageRepository.pageExists(url)).thenReturn(false);
		when(pageRepository.create(url, domainRepository)).thenReturn(page);
		when(domainRepository.domainExists("google.com")).thenReturn(false);
		sut.addUrlForCrawling(url, parenturl);

		verify(pageRepository).create(url, domainRepository);
		verify(domainRepository).create("google.com");
		verify(domainRepository, times(0)).getDomain("google.com");
	}

	@Test
	public void test_page_does_not_exist_domain_does_exist___page_is_created_domain_is_not() {
		String parentUrl = "www.google.com/b";
		String url = "www.google.com/a";
		Page page = new Page();
		page.setUrl(url);
		ServiceImpl sut = new ServiceImpl(sessionFactory, repositoryFactory);
		when(pageRepository.pageExists(url)).thenReturn(false);
		when(pageRepository.create(url, domainRepository)).thenReturn(page);
		when(domainRepository.domainExists("google.com")).thenReturn(true);
		sut.addUrlForCrawling(url, parentUrl);

		verify(pageRepository).create(url, domainRepository);
		verify(domainRepository).getDomain("google.com");
		verify(domainRepository, times(0)).create("google.com");
	}


	@Before
	public void setup() {
		sessionFactory = mock(SessionFactory.class);
		Session session = mock(Session.class);
		when(sessionFactory.openSession()).thenReturn(session);
		Transaction tx = mock(Transaction.class);
		when(session.beginTransaction()).thenReturn(tx);
		pageRepository = mock(PageRepository.class);
		domainRepository = mock(DomainRepository.class);
		repositoryFactory = mock(RepositoryFactory.class);
		pageLinkRepository = mock(PageLinkRepository.class);
		when(repositoryFactory.createDomainRepository(session)).thenReturn(domainRepository);
		when(repositoryFactory.createPageRepository(session)).thenReturn(pageRepository);
		when(repositoryFactory.createPageLinkRepository(session)).thenReturn(pageLinkRepository);
	}
}
