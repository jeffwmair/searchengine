use searchengine;
insert into domains(domain, crawlerId, status, total_crawls) values ('jefftron.com', 1, 1, 0);
insert into domains(domainid, url, verified, pagerank, fail_count) values (LAST_INSERT_ID(), 'http://jefftron.com/', 1, 0, 0);

insert into domains(domain, crawlerId, status, total_crawls) values ('cbc.ca', 2, 1, 0);
insert into domains(domainid, url, verified, pagerank, fail_count) values (LAST_INSERT_ID(), 'http://cbc.ca/news/', 1, 0, 0);

insert into domains(domain, crawlerId, status, total_crawls) values ('news.ycombinator.com/', 3, 1, 0);
insert into domains(domainid, url, verified, pagerank, fail_count) values (LAST_INSERT_ID(), 'https://news.ycombinator.com/', 1, 0, 0);

insert into domains(domain, crawlerId, status, total_crawls) values ('ehow.com/', 4, 1, 0);
insert into domains(domainid, url, verified, pagerank, fail_count) values (LAST_INSERT_ID(), 'http://www.ehow.com/how_6738049_start-consulting-business-ontario.html', 1, 0, 0);

insert into domains(domain, crawlerId, status, total_crawls) values ('naver.com/', 5, 1, 0);
insert into domains(domainid, url, verified, pagerank, fail_count) values (LAST_INSERT_ID(), 'http://naver.com/', 1, 0, 0);	