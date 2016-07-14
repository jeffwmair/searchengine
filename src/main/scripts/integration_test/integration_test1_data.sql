insert into domains(domain, crawlerId, status, total_crawls, locked) values ('news.ycombinator.com', 1, 1, 0, 0) ;
insert into pages(domainid, url, verified, pagerank, fail_count) values (LAST_INSERT_ID(), 'https://news.ycombinator.com/', 1, 0, 0) ;
