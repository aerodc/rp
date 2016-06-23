package rest.controllers;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import security.Auth;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/")
public class IndexPageController {

    private static final Logger logger = Logger.getLogger(IndexPageController.class);

    Auth auth;

    @Autowired
    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    @RequestMapping(value = "/tm", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String index(@RequestParam("token") String token, @RequestParam("cid") Integer cid,
            @RequestParam("nwId") Integer nwId, @RequestParam("cTMId") Integer cTMId,
            @RequestParam("aTMNa") String aTMNa, @RequestParam("cTMNa") String cTMNa,
            @RequestParam("login") String login, @RequestParam("adsId") Integer adsId, HttpServletResponse response) {

        logger.info("TM User " + login + " visit Redirect Publisher");
        if (!auth.isTokenExpired(token) && cTMId != null && login != null && !login.equals(" ") && !login.equals("")
                && login.length() > 0) {

            if (adsId == 1 && cid != null && nwId != null && cid > 4000 && nwId > 100) {
                return "/static/index.html";
            } else if (adsId == 2) {
                return "/static/sizmek.html";
            }

            return "/static/login.html";
        } else {

            if (cTMId == null || cid == null || nwId == null || login == null || login.equals(" ") || login.equals("")
                    || login.length() == 0) {
                return "/static/login.html";
            } else {
                return "/static/views/404.html";
            }

        }
    }

    @RequestMapping(value = "/index", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String redirectDCMAuth(@RequestParam("code") String code) {

        return "/static/indexDCM.html?code=" + code;

    }

    @RequestMapping(value = "/sizmek", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String redirectSizmekAuth() {

        return "/static/sizmek.html";

    }

    @RequestMapping(value = "/dsp", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String redirectDspAuth() {

        return "/static/dsp.html";

    }

    @RequestMapping(value = "/help", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getHelpPage(@RequestParam("t") String t) {

        if (t != null && !auth.isTokenExpired(t)) { return "/static/video.html"; } else { return "/static/login.html"; }

    }

    @RequestMapping(value = "*", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String login() {

        return "/static/login.html";
    }

}
