package rest.controllers;

import com.eyeblaster.api.v1.datacontracts.AdvertiserInfo;
import dao.RedirectDao;
import models.CampaignRP;
import models.Redirect;
import models.RedirectTag;
import models.Site;
import models.SiteWithToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import rest.services.SizmekService;
import rest.services.TMService;
import security.Auth;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/sizmek")
public class SizmekController {

    private TMService tmService;

    private SizmekService sizmekService;

    private Auth auth;

    private RedirectDao redirectDao;

    @Autowired
    public void setSizmekService(SizmekService sizmekService) {
        this.sizmekService = sizmekService;
    }

    @Autowired
    public void setTmService(TMService tmService) {
        this.tmService = tmService;
    }

    @Autowired
    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    @Autowired
    public void setRedirectDao(RedirectDao redirectDao) {
        this.redirectDao = redirectDao;
    }

    @RequestMapping(value = "/gettoken/{login}/{password}", method = RequestMethod.GET, produces = "text/plain;"
            + "charset=utf-8")
    @ResponseBody
    public String getSizmekToken(@PathVariable String login, @PathVariable String password) {

        return sizmekService.getPersonalToken(login, password);
    }

    @RequestMapping(value = "/geturl", method = RequestMethod.GET, produces = "text/plain;charset=utf-8")
    @ResponseBody
    public String getSizmekUrl() {

        return sizmekService.getUrl();
    }

    @RequestMapping(value = "/getcambyid/{camId}/{tokenSizmek}/{tokenTM}", method = RequestMethod.GET, produces =
            "application/json")
    @ResponseBody
    public CampaignRP getCampsByToken(@PathVariable int camId, @PathVariable String tokenSizmek,
            @PathVariable String tokenTM) {
        if (!auth.isTokenExpired(tokenTM)) {
            return sizmekService.getSizmekCampaignById(camId, tokenSizmek);
        }

        return null;
    }

    @RequestMapping(value = "/getcams/{tokenSizmek}/{tokenTM}", method = RequestMethod.GET, produces =
            "application/json")
    @ResponseBody
    public List<CampaignRP> getCampsByToken(@PathVariable String tokenSizmek, @PathVariable String tokenTM) {
        if (!auth.isTokenExpired(tokenTM)) {
            //			List<CampaignRP> camList = new ArrayList<CampaignRP>();
            //			camList.add(sizmekService.getSizmekCampaignById(627394, tokenSizmek));
            //			return camList;
            return sizmekService.getRecentCampagnesList(tokenSizmek);
        }

        return null;
    }

    @RequestMapping(value = "/getAdvertiser/{advId}/{tokenSizmek}/{tokenTM}", method = RequestMethod.GET, produces =
            "application/json")
    @ResponseBody
    public AdvertiserInfo getAdvertiserById(@PathVariable Integer advId, @PathVariable String tokenSizmek,
            @PathVariable String tokenTM) {

        if (!auth.isTokenExpired(tokenTM)) {
            return sizmekService.getSizmekAdvertiserById(advId, tokenSizmek);
        }
        return null;
    }

    @RequestMapping(value = "/refreshToken/{tokenSizmek}/{tokenTM}", method = RequestMethod.GET, produces =
            "application/json")
    @ResponseBody
    public boolean refreshSizmekToken(@PathVariable String tokenSizmek, @PathVariable String tokenTM) {

        if (!auth.isTokenExpired(tokenTM)) {
            return sizmekService.refreshToken(tokenSizmek);
        }
        return false;
    }

    @RequestMapping(value = "/getsiteRedirect/{camSizmekId}/{siteId}/{tokenSizmek}/{tokenTM}", method = RequestMethod
            .GET, produces = "application/json")
    @ResponseBody
    public List<Redirect> getsiteRedirect(@PathVariable Integer camSizmekId, @PathVariable Integer siteId,
            @PathVariable String tokenSizmek, @PathVariable String tokenTM) {

        if (!auth.isTokenExpired(tokenTM)) {
            return sizmekService.getPlacementsByCampaignIdSiteId(camSizmekId, siteId, tokenSizmek);
        }
        return null;
    }

    @RequestMapping(value = "/getsites/{camSizmekId}/{tokenSizmek}/{tokenTM}", method = RequestMethod.GET, produces =
            "application/json")
    @ResponseBody
    public List<Site> getCamsites(@PathVariable Integer camSizmekId, @PathVariable String tokenSizmek,
            @PathVariable String tokenTM) {

        if (!auth.isTokenExpired(tokenTM)) {
            return sizmekService.getSizmekCampaignSites(camSizmekId, tokenSizmek);
        }
        return null;
    }

    @RequestMapping(value = "/rechargepla/{placementId}/{tokenSizmek}/{tokenTM}", method = RequestMethod.GET,
            produces = "application/json")
    @ResponseBody
    public List<RedirectTag> getPlacementTags(@PathVariable Integer placementId, @PathVariable String tokenSizmek,
            @PathVariable String tokenTM) {

        if (!auth.isTokenExpired(tokenTM)) {
            List<RedirectTag> redirectTagList = new ArrayList<RedirectTag>();
            redirectTagList = sizmekService.getAllTagsBylacementId(placementId, tokenSizmek);

            for (int i = 0; i < redirectTagList.size(); i++) {
                RedirectTag redirectTagTM = tmService
                        .getSingleTagTM(String.valueOf(placementId), redirectTagList.get(i).getTagType(), 2);
                if (redirectTagTM != null) {
                    redirectTagList.set(i, redirectTagTM);
                }
            }

            return redirectTagList;
        }
        return null;
    }

    @RequestMapping(value = "/getTagSizmek/{placementId}/{tagType}/{tokenSizmek}/{tokenTM}", method = RequestMethod
            .GET, produces = "application/json")
    @ResponseBody
    public RedirectTag getTagSizmek(@PathVariable Integer placementId, @PathVariable String tagType,
            @PathVariable String tokenSizmek, @PathVariable String tokenTM) {

        if (!auth.isTokenExpired(tokenTM)) {
            return sizmekService.getTagByType(placementId, tagType, tokenSizmek);
        }
        return null;
    }

    @RequestMapping(value = "/getPlTagSizmek/{placementId}/{tokenSizmek}/{tokenTM}", method = RequestMethod.GET,
            produces = "application/json")
    @ResponseBody
    public List<RedirectTag> getPlacementTagSizmek(@PathVariable Integer placementId, @PathVariable String tokenSizmek,
            @PathVariable String tokenTM) {

        if (!auth.isTokenExpired(tokenTM)) {
            return sizmekService.getAllTagsBylacementId(placementId, tokenSizmek);
        }
        return null;
    }

    @RequestMapping(value = "/getSiteSizmek", method = RequestMethod.POST)
    @ResponseBody
    public Site getPlacementTagSizmek(@RequestBody SiteWithToken siteWithToken) {

        if (!auth.isTokenExpired(siteWithToken.getTokenTM())) {

            Site siteSizmek = sizmekService.getSite(siteWithToken.getSite(), siteWithToken.getTokenSizmek());
            for (int i = 0; i < siteSizmek.getRedirectList().size(); i++) {
                String redirectStatus = redirectDao.getPlacementStatus(siteSizmek.getRedirectList().get(i).getCamId(),
                        siteSizmek.getRedirectList().get(i).getPlacementId(), 2);
                if (redirectStatus != null) {
                    siteSizmek.getRedirectList().get(i).setStatus(redirectStatus);
                }

                for (int j = 0; j < siteSizmek.getRedirectList().get(i).getRedTagList().size(); j++) {

                    RedirectTag redirectTag = redirectDao
                            .getSingRedTag(String.valueOf(siteSizmek.getRedirectList().get(i).getPlacementId()),
                                    siteSizmek.getRedirectList().get(i).getRedTagList().get(j).getTagType(), 2);

                    if (redirectTag != null) {
                        siteSizmek.getRedirectList().get(i).getRedTagList().get(j).setStatus(redirectTag.getStatus());
                        siteSizmek.getRedirectList().get(i).getRedTagList().get(j)
                                .setLastupdatedDate(redirectTag.getLastupdatedDate());
                        siteSizmek.getRedirectList().get(i).getRedTagList().get(j)
                                .setModifyByName(redirectTag.getModifyByName());
                    }
                }
            }

            return siteSizmek;
        }
        return null;
    }

    @RequestMapping(value = "/getClientToken/{camId}", method = RequestMethod.GET, produces = "text/plain;charset=utf-8")
    @ResponseBody
    public String getSizmekClientToken(@PathVariable Integer camId) {

        return sizmekService.getClientResponseToken(camId);
    }
}
