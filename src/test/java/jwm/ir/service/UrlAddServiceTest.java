package jwm.ir.service;

import jwm.ir.domain.DomainRepository;
import jwm.ir.domain.Page;
import jwm.ir.domain.PageLinkRepository;
import jwm.ir.domain.PageRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * Created by Jeff on 2016-08-01.
 */
public class UrlAddServiceTest {

	private PageRepository pageRepository;
	private DomainRepository domainRepository;
	private PageLinkRepository pageLinkRepository;

	@Test
	public void test_page_already_exists_throws_exception() {

		String url = "www.google.com/a";
		String parentUrl = "www.google.com/b";
		UrlAddService sut = new UrlAddService();
		when(pageRepository.pageExists(url)).thenReturn(true);

		try {
			sut.addUrlForCrawling(url, parentUrl, pageRepository, domainRepository, pageLinkRepository);
			Assert.fail("should throw exception");
		}
		catch(IllegalStateException ex) {}

	}

	@Test
	public void test_page_does_not_exist_domain_does_not_exist___page_and_domain_created() {
		String url = "www.google.com/a";
		Page page = new Page();
		page.setUrl(url);
		String parenturl = "www.google.com/b";
		UrlAddService sut = new UrlAddService();
		when(pageRepository.pageExists(url)).thenReturn(false);
		when(pageRepository.create(url, domainRepository)).thenReturn(page);
		when(domainRepository.domainExists("google.com")).thenReturn(false);
		sut.addUrlForCrawling(url, parenturl, pageRepository, domainRepository, pageLinkRepository);

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
		UrlAddService sut = new UrlAddService();
		when(pageRepository.pageExists(url)).thenReturn(false);
		when(pageRepository.create(url, domainRepository)).thenReturn(page);
		when(domainRepository.domainExists("google.com")).thenReturn(true);
		sut.addUrlForCrawling(url, parentUrl, pageRepository, domainRepository, pageLinkRepository);

		verify(pageRepository).create(url, domainRepository);
		verify(domainRepository).getDomain("google.com");
		verify(domainRepository, times(0)).create("google.com");
	}

	@Before
	public void setup() {
		pageRepository = mock(PageRepository.class);
		domainRepository = mock(DomainRepository.class);
		pageLinkRepository = mock(PageLinkRepository.class);
	}
}
