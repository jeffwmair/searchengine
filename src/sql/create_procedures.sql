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

    /*
    problem here: we keep adding to the document_frequency if we re-index the same page (which we should be careful of
    anyway)
    */
    update terms set document_frequency = document_frequency + 1 where termId in (select termId from pageterms where pageId = pPageId order by termId);

    /* clear the input table */
    delete from pageterm_input where pageId = pPageId; 
		
end
|

