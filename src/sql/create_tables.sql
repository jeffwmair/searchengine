CREATE TABLE summarydata_i(
	item VARCHAR(100) NOT NULL,
	val BIGINT NOT NULL,
	updatedate DATETIME NOT NULL
);

CREATE TABLE domains(
	domainId BIGINT NOT NULL AUTO_INCREMENT,
	domain VARCHAR(250) NOT NULL,
	status SMALLINT(5) NOT NULL,
	crawlerId SMALLINT(5) NOT NULL,
	last_crawl DATETIME NULL,
	total_crawls SMALLINT(5) NOT NULL,
	locked BIT NOT NULL,
	PRIMARY KEY (domainId, domain)
);

CREATE UNIQUE INDEX uix_domains
    ON domains (domain);

CREATE TABLE pages(
	pageId BIGINT NOT NULL AUTO_INCREMENT,
	domainId BIGINT NOT NULL,
	url VARCHAR(300) NOT NULL,
	title VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL,
	description VARCHAR(500) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL,
	verified SMALLINT(5) NOT NULL,
	pagerank FLOAT(10,8) NOT NULL,
	last_crawl DATETIME NULL,
	fail_count SMALLINT(5) NOT NULL,
	PRIMARY KEY(pageId, url),
	FOREIGN KEY (domainId) REFERENCES domains (domainId)
);

CREATE TABLE pagesubmissions( 
	id BIGINT NOT NULL AUTO_INCREMENT,
	pageId BIGINT NOT NULL,
	submitdate DATETIME NOT NULL,
	ip VARCHAR(50) NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (pageId) REFERENCES pages (pageId)
)
;

CREATE TABLE pagelinks(
	pageLinkId BIGINT NOT NULL AUTO_INCREMENT,
	pageId BIGINT NOT NULL,
	destPageId BIGINT NOT NULL,
	PRIMARY KEY (pageLinkId, pageId, destPageId),
	FOREIGN KEY (pageId) REFERENCES pages (pageId),
	FOREIGN KEY (destPageId) REFERENCES pages (pageId)
)
;

CREATE UNIQUE INDEX uix_pages
    ON pages (url)
;

CREATE INDEX idx_pages_domain_id
	ON pages (domainId, last_crawl)
;

CREATE TABLE terms( 
	termId BIGINT NOT NULL AUTO_INCREMENT,
	term VARCHAR(250) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
	document_frequency SMALLINT(5) NOT NULL,
	PRIMARY KEY (termId, term)
)
;

CREATE INDEX ix_term
	ON terms (term)
;

CREATE UNIQUE INDEX uix_term
    ON terms (term)
;
	
CREATE TABLE pageterms( 
	postingTermId BIGINT NOT NULL AUTO_INCREMENT,
	pageId BIGINT NOT NULL,
	termId BIGINT NOT NULL,
	term_frequency SMALLINT(5) NOT NULL,
	PRIMARY KEY (postingTermId),
	FOREIGN KEY (pageId) REFERENCES pages (pageId),
	FOREIGN KEY (termId) REFERENCES terms (termId)
)
;

CREATE UNIQUE INDEX uix_pageterms
    ON pageterms (pageId, termId)
;

CREATE INDEX ix_termid
	ON pageterms (termId)
;

CREATE INDEX ix_pageterms_pageid
	ON pageterms (pageId)
;

CREATE TABLE pageterm_input(
	pagetermInputId BIGINT NOT NULL AUTO_INCREMENT,
	pageId BIGINT NOT NULL,
	term VARCHAR(250) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
	tf INT NOT NULL,
	PRIMARY KEY (pagetermInputId),
	FOREIGN KEY (pageId) REFERENCES pages (pageId)
)
;

CREATE INDEX ix_pageterm_input_pageid
	ON pageterm_input (pageId)
;

CREATE TABLE validextensions (
  extensionId BIGINT NOT NULL AUTO_INCREMENT,
  extType SMALLINT(5) NOT NULL,
  ext VARCHAR(20) NOT NULL,
  PRIMARY KEY (extensionId)
);


delimiter |

create procedure insert_url(IN pContainingPageUrl varchar(500), IN pDomain varchar(500), IN pUrl varchar(500), IN pCrawlerId int) 
begin

    set @domainid = (select domainid from domains where domain = pDomain);
    if @domainid is NULL then 
      insert into domains (domain, status, crawlerId, total_crawls, locked) values (pDomain, 0, pCrawlerId, 0, 0);
      SET @domainid = LAST_INSERT_ID();
    end if;

    /* insert the new url */
    insert ignore into pages (domainId, verified, url, pagerank, fail_count) values (@domainid, 0, pUrl, 0, 0);
    set @pageId = (select pageId from pages where url = pUrl);

    set @containingPageId = (select pageid from pages where url = pContainingPageUrl);
    
    /* add to the page links table for PageRank calculation */
    insert into pagelinks (pageId, destPageId) values (@containingPageId, @pageId);

end
|


create procedure insert_terms(IN pPageId int)
	
begin
		
    /* first delete any existing terms associated with this pageid */
    delete from pageterms where pageId = pPageId;

    /* insert terms that are not already in the dictionary */
    insert ignore into terms (term, document_frequency) select term, 0 from pageterm_input where pageId = pPageId;
	
    /* insert to pageterms (the actual links) */
    insert into pageterms (pageId, termId, term_frequency) 
		select pti.pageId, t.termId, pti.tf 
		from pageterm_input pti
		join terms t on pti.term = t.term
		where pti.pageId = pPageId
		order by pageTermInputId;

    /* update the doc frequencies */
    update terms set document_frequency = document_frequency + 1 where termId in (select termId from pageterms where pageId = pPageId order by termId);
	
    /* clear the input table */
    delete from pageterm_input where pageId = pPageId; 
		
end
|


delimiter ;

/* tld's */

insert into validextensions (extType, ext) values (1, '.biz');
insert into validextensions (extType, ext) values (1, '.com');
insert into validextensions (extType, ext) values (1, '.edu');
insert into validextensions (extType, ext) values (1, '.gov');
insert into validextensions (extType, ext) values (1, '.info');
insert into validextensions (extType, ext) values (1, '.net');
insert into validextensions (extType, ext) values (1, '.org');
insert into validextensions (extType, ext) values (1, '.tv');
insert into validextensions (extType, ext) values (1, '.io');

/* countries */
insert into validextensions (extType, ext) values (1, '.at');
insert into validextensions (extType, ext) values (1, '.ca');
insert into validextensions (extType, ext) values (1, '.fr');
insert into validextensions (extType, ext) values (1, '.kr');
insert into validextensions (extType, ext) values (1, '.uk');
insert into validextensions (extType, ext) values (1, '.us');
insert into validextensions (extType, ext) values (1, '.it');
insert into validextensions (extType, ext) values (1, '.jp');
insert into validextensions (extType, ext) values (1, '.me');
insert into validextensions (extType, ext) values (1, '.mu');
insert into validextensions (extType, ext) values (1, '.no');
insert into validextensions (extType, ext) values (1, '.se');


/* page extensions -- currently not used */
insert into validextensions (extType, ext) values (2, '.html');
insert into validextensions (extType, ext) values (2, '.htm');
insert into validextensions (extType, ext) values (2, '.asp');
insert into validextensions (extType, ext) values (2, '.aspx');
insert into validextensions (extType, ext) values (2, '.php');
