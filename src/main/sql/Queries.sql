select * from domains order by total_crawls desc;
select * from domains order by domainid desc limit 10;
select count(*) from domains where not last_crawl is null;
select sum(total_crawls) from domains;
select * from pages order by last_crawl desc limit 500;
select * from pages where verified = -1;

select count(*) from pages where verified = 1;

show index from pages;

/*
insert into domains(domain, crawlerId, status, total_crawls) values ('naver.com/', 6, 1, 0);
insert into domains(domainid, url, verified, fail_count) values (LAST_INSERT_ID(), 'http://www.naver.com/', 1, 0);
*/
select crawlerid, sum(total_crawls) from domains
group by crawlerid;

SELECT table_schema "Data Base Name", 
sum( data_length + index_length ) / 1024 / 
1024 "Data Base Size in MB", 
sum( data_free )/ 1024 / 1024 "Free Space in MB" 
FROM information_schema.TABLES 
GROUP BY table_schema;