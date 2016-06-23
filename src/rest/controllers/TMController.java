package rest.controllers;

import models.CamTM;
import models.CamWithToken;
import models.CampaignRP;
import models.RedTagWithToken;
import models.Redirect;
import models.RedirectTag;
import models.RedirectWithToken;
import models.Site;
import models.SiteWithToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import rest.services.TMService;
import security.Auth;
import security.Token;

import java.util.List;

@RestController
@RequestMapping("/tm")
public class TMController {

    private TMService tmService;

    private Auth auth;

    @Autowired
    public void setTmService(TMService tmService) {
        this.tmService = tmService;
    }

    @Autowired
    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    @RequestMapping(value = "/gettag/{token}/{placementId}/{tagType}/{adServer}", method = RequestMethod.GET,
            produces = "application/json")
    @ResponseBody
    public RedirectTag getSingleTagTM(@PathVariable String token, @PathVariable String placementId,
            @PathVariable String tagType, @PathVariable int adServer) {

        if (!auth.isTokenExpired(token)) {
            if (placementId.length() > 0 && tagType.length() > 0) {

                return tmService.getSingleTagTM(placementId, tagType, adServer);
            } else {
                //System.out.println("return null");
                return null;
            }
        } else {
            return null;
        }
    }

    @RequestMapping(value = "/gettags/{placementId}/{adServer}", method = RequestMethod.GET, produces =
            "application/json")
    @ResponseBody
    public List<RedirectTag> getTagsByPlacementTM(@PathVariable String placementId, @PathVariable int adServer) {

        return tmService.getTagsByPlacementTM(placementId, adServer);
    }

    @RequestMapping(value = "/getcam/{token}/{camId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public CampaignRP getPlacementByCampaignTM(@PathVariable String token, @PathVariable String camId) {

        if (!auth.isTokenExpired(token)) {
            return tmService.getPlacementByCampaignTM(camId, 1);
        } else { return null; }
    }

    @RequestMapping(value = "/savetag", method = RequestMethod.POST)
    public synchronized
    @ResponseBody
    RedirectTag saveSingleTagTM(@RequestBody RedTagWithToken redTagWithToken) {

        if (!auth.isTokenExpired(redTagWithToken.getTokenValue())) {
            if (tmService.saveSingleTagTM(redTagWithToken.getRedirectTag(), redTagWithToken.getRedirect()) == true) {
                return redTagWithToken.getRedirectTag();
            }
        }

        return null;
    }

    @RequestMapping(value = "/saveplacement", method = RequestMethod.POST)
    public
    @ResponseBody
    void savePlacementTM(@RequestBody RedirectWithToken redirectWithToken) {
        if (!auth.isTokenExpired(redirectWithToken.getTokenValue())) {
            tmService.savePlacementTM(redirectWithToken.getRedirect());
        }
    }

    @RequestMapping(value = "/savepltags", method = RequestMethod.POST)
    public
    @ResponseBody
    void savePlacementtagsTM(@RequestBody RedirectWithToken redirectWithToken) {
        if (!auth.isTokenExpired(redirectWithToken.getTokenValue())) {
            tmService.savePlacementtagsTM(redirectWithToken.getRedirect());
        }
    }

    @RequestMapping(value = "/validPlacement", method = RequestMethod.POST)
    public
    @ResponseBody
    Redirect validPlacementTM(@RequestBody RedirectWithToken redirectWithToken) {
        if (!auth.isTokenExpired(redirectWithToken.getTokenValue())) {
            return tmService.validPlacementTM(redirectWithToken.getRedirect());
        }
        return null;
    }

    @RequestMapping(value = "/validSite", method = RequestMethod.POST)
    public
    @ResponseBody
    Site validSiteTM(@RequestBody SiteWithToken siteWithToken) {
        if (!auth.isTokenExpired(siteWithToken.getTokenTM())) {
            return tmService.validSiteTM2(siteWithToken.getSite());
        }
        return null;
    }

    @RequestMapping(value = "/savecam", method = RequestMethod.POST)
    public
    @ResponseBody
    void saveCamTM(@RequestBody CamWithToken camWithToken) {
        if (!auth.isTokenExpired(camWithToken.getTokenValue())) { tmService.saveCamTM(camWithToken.getCampaignRP()); }
    }

    @RequestMapping(value = "/getcamtm/{camDCMId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public CamTM getCamTM(@PathVariable int camDCMId) {
        return tmService.getCamTM(camDCMId);
    }

    @RequestMapping(value = "/getToken", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Token getTokenTM() {
        return auth.generateToken();
    }

}
