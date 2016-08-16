package jwm.ir.webapp.controller;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

/**
 * Created by Jeff on 2016-08-15.
 */
@Controller
public class IndexController {

    Logger log = LogManager.getLogger(IndexController.class);

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String view(ModelMap model) {

        log.debug("hiya -- index controller");
        model.addAttribute("message", new Date());
        return "index";
    }

}
