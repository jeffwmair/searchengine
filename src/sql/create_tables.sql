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

