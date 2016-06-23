package rest.controllers;

import models.CamUpdate;
import models.CampaignRP2;
import models.TokenResp;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import rest.services.DSPService;
import security.Auth;
import security.Token;

import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.ResourceBundle;

@RestController
@RequestMapping("/")
public class DSPController {

    private static final Logger logger = Logger.getLogger(DSPController.class);

    private static ResourceBundle dspRB = ResourceBundle.getBundle("properties.dsp");

    private Auth auth;

    private DSPService dspService;

    @Autowired
    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    @Autowired
    public void setDspService(DSPService dspService) {
        this.dspService = dspService;
    }

    @RequestMapping(value = "/gettoken/{login}/{pwd}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public TokenResp getToken(@PathVariable String login, @PathVariable String pwd, HttpServletResponse response) {
        response.setStatus(200);
        Token token = new Token();
        token = auth.firstHandShake(login, pwd);
        logger.info("User: " + login + " Requests Token");
        if (token == null) {
            logger.info("User: " + login + " Requests Token --> Failed");
            return new TokenResp("KO",
                    "Please first contact Data-it(data-it@havasmedia.com) to get the access of API TM for DSP");
        } else {
            logger.info("User: " + login + " Requests Token --> Successed");
            return new TokenResp("OK", token.getTokenValue());
        }
    }

    @RequestMapping(value = "/gettokenWithUrl/{login}/{pwd}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public TokenResp getTokenWithUrl(@PathVariable String login, @PathVariable String pwd,
            HttpServletResponse response) {
        response.setStatus(200);
        Token token = new Token();
        token = auth.firstHandShake(login, pwd);
        logger.info("User: " + login + " Requests Token");
        if (token == null) {
            logger.info("User: " + login + " Requests Token --> Failed");
            return new TokenResp("KO",
                    "Please first contact Data-it(data-it@havasmedia.com) to get the access of API TM for DSP");
        } else {
            logger.info("User: " + login + " Requests Token --> Successed");
            TokenResp tokenResp = new TokenResp("OK", token.getTokenValue());
            tokenResp.setUrl(dspRB.getString("dsp.url"));
            return tokenResp;
        }
    }

    //todo requete add header and requete add body
    @RequestMapping(value = "/getreds/{token}/{adServer}/{idCamAdserver}", method = RequestMethod.GET, produces =
            "application/json")
    @ResponseBody
    public CampaignRP2 getRedirectsByCampaignId(@PathVariable String token, @PathVariable String adServer,
            @PathVariable long idCamAdserver, HttpServletResponse response) {

        logger.info("Rest Get Request: getreds for IDCAM " + idCamAdserver + " AdServer " + adServer + " arrives");

        if (!auth.isTokenExpired(token) && (("DCM".equalsIgnoreCase(adServer) || "DART".equalsIgnoreCase(adServer))
                || "SIZMEK".equalsIgnoreCase(adServer))) {

            CampaignRP2 campaignRP2;
            if ("DCM".equalsIgnoreCase(adServer) || "DART".equalsIgnoreCase(adServer)) {
                campaignRP2 = dspService.getRedirectsByCampaignId(idCamAdserver, 1);
            } else { campaignRP2 = dspService.getRedirectsByCampaignId(idCamAdserver, 2); }

            if (campaignRP2.getResponseCode().equals("KO")) {

                response.setStatus(404);

                logger.info("Rest Get Request: getreds for IDCAM " + idCamAdserver + " AdServer " + adServer
                        + " found no result 404");
                return null;
            } else {
                response.setStatus(200);

                logger.info("Rest Get Request: getreds for IDCAM " + idCamAdserver + " AdServer " + adServer
                        + " found result 200");
                return campaignRP2;
            }
        } else {
            response.setStatus(401);
            logger.info("Rest Get Request: getreds for IDCAM " + idCamAdserver + " AdServer " + adServer
                    + " token expired or url not correct 401");
            /*CampaignRP2 campaignRP2 = new CampaignRP2();
			campaignRP2.setResponseCode("KO");
			return campaignRP2;*/
            return null;
        }

    }

    @RequestMapping(value = "/getupdatedcams/{token}/{beginDate}/{endDate}", method = RequestMethod.GET, produces =
            "application/json")
    @ResponseBody
    public CamUpdate getUpdatedCampaigns(@PathVariable String token, @PathVariable String beginDate,
            @PathVariable String endDate, HttpServletResponse response) {

        logger.info("Rest Get Request: getupdatedcams between  " + beginDate + " and " + endDate + " arrives");

        if (!auth.isTokenExpired(token)) {
            response.setStatus(200);
            Timestamp beginTime = new Timestamp(Long.valueOf(beginDate));
            Timestamp endTime = new Timestamp(Long.valueOf(endDate));
            logger.info("Rest Get Request: getupdatedcams between  " + beginDate + " and " + endDate + " found 200");

            return dspService.getUpdatedCampaigns(beginTime, endTime);
        } else {
            response.setStatus(401);

            logger.info("Rest Get Request: getupdatedcams between  " + beginDate + " and " + endDate
                    + " token expired or url not correct 401");

			/*CamUpdate camUpdate = new CamUpdate();
			camUpdate.setResponseCode("KO");
			return camUpdate;*/
            return null;
        }

    }

}
