insert into domains(domain, crawlerId, status, total_crawls, locked) values ('localhost/searchengine_test/', 1, 1, 0, 0) ;
insert into pages(domainid, url, verified, pagerank, fail_count) values (LAST_INSERT_ID(), 'http://localhost/searchengine_test/page1.html', 1, 0, 0) ;
