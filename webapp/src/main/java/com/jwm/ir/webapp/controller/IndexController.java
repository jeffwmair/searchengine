package com.jwm.ir.webapp.controller;

import com.jwm.searchservice.SearchService;
import com.jwm.searchservice.document.RankedDocument;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Set;

/**
 * Created by Jeff on 2016-08-15.
 */
@Controller
public class IndexController {

    Logger log = LogManager.getLogger(IndexController.class);

    @Autowired
    SearchService searchService;

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String view(ModelMap model) {
        log.info("Performing search...");
        Set<RankedDocument> rankedDocumentList = searchService.getRankedDocumentsForQuery("star");
        log.info("Fetched ranked documents list, size:"+rankedDocumentList.size());
        model.addAttribute("searchResults", rankedDocumentList);
        return "index";
    }

}
