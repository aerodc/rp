package rest.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import dao.RedirectDao;
import models.CamDesc;
import models.CampaignRP;
import models.Redirect;
import models.RedirectTag;
import models.Site;
import models.UserProfileDCMList;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import rest.services.DCMService;
import rest.services.ServiceAccount;
import security.Auth;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@RestController
@RequestMapping("/dcm")
public class DCMController {

    private static final Logger logger = Logger.getLogger(DCMController.class);

    private DCMService dcmService;

    private Auth auth;

    private RedirectDao redirectDao;

    private ResourceBundle doubleClickRB = ResourceBundle.getBundle("properties.doubleclick");

    @Autowired
    public void setDcmService(DCMService dcmService) {
        this.dcmService = dcmService;
    }

    @Autowired
    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    @Autowired
    public void setRedirectDao(RedirectDao redirectDao) {
        this.redirectDao = redirectDao;
    }

    @RequestMapping(value = "/getcam/{token}/{idCamDCM}/{networkId}/{caseNB}/{profileId2}", method = RequestMethod
            .GET, produces = "application/json")
    @ResponseBody
    public CampaignRP getRedirectsByCampaignId(@PathVariable String token, @PathVariable String idCamDCM,
            @PathVariable int networkId, @PathVariable int caseNB, @PathVariable long profileId2) {

        //Call DCM API Service
        if (!auth.isTokenExpired(token)) {
            return dcmService.getRedirectsByCampaignId(idCamDCM, networkId, caseNB, token, profileId2);
        } else {
            return null;
        }

    }

    @RequestMapping(value = "/getcamplsnb/{token}/{idCamDCM}/{networkId}/{caseNB}/{profileId2}", method =
            RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public int getRedirectsNBByCampaignId(@PathVariable String token, @PathVariable String idCamDCM,
            @PathVariable int networkId, @PathVariable int caseNB, @PathVariable long profileId2) {

        //Call DCM API Service
        if (!auth.isTokenExpired(token)) {
            return dcmService.getRedirectsNBByCampaignId(idCamDCM, networkId, caseNB, token, profileId2);
        } else {
            return 0;
        }

    }

    @RequestMapping(value = "/rechargepla/{token}/{idCamDCM}/{networkId}/{idPlacement}/{caseNB}/{profileId2}", method
            = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Redirect rechargePlacementByID(@PathVariable String token, @PathVariable String idCamDCM,
            @PathVariable int networkId, @PathVariable int idPlacement, @PathVariable int caseNB,
            @PathVariable long profileId2) {

        if (!auth.isTokenExpired(token)) {
            return dcmService.rechargePlacmentById(idCamDCM, networkId, idPlacement, caseNB, token, profileId2);
        } else {
            return null;
        }
    }

    @RequestMapping(value = "/getPlTagDCM/{token}/{idCamDCM}/{networkId}/{idPlacement}/{caseNB}/{profileId}", method =
            RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Redirect getPlTagDCMById(@PathVariable String token, @PathVariable String idCamDCM,
            @PathVariable int networkId, @PathVariable int idPlacement, @PathVariable int caseNB, @PathVariable long profileId) {

        if (!auth.isTokenExpired(token)) {
            return dcmService.getPlacmentTagDCMById(idCamDCM, networkId, idPlacement, caseNB, token, profileId);
        } else {
            return null;
        }
    }

    @RequestMapping(value = "/getSiteDCM/{token}/{idCamDCM}/{networkId}/{siteId}/{caseNB}/{profileId}", method =
            RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Site getSiteDCM(@PathVariable String token, @PathVariable String idCamDCM, @PathVariable int networkId,
            @PathVariable int siteId, @PathVariable int caseNB, @PathVariable long profileId) {

        if (!auth.isTokenExpired(token)) {

            Site site = dcmService.getSiteDCM(idCamDCM, networkId, siteId, caseNB, token, profileId);
            for (int i = 0; i < site.getRedirectList().size(); i++) {
                String redirectStatus = redirectDao.getPlacementStatus(site.getRedirectList().get(i).getCamId(),
                        site.getRedirectList().get(i).getPlacementId(), 1);
                if (redirectStatus != null) {
                    site.getRedirectList().get(i).setStatus(redirectStatus);
                }

                for (int j = 0; j < site.getRedirectList().get(i).getRedTagList().size(); j++) {

                    RedirectTag redirectTag = redirectDao
                            .getSingRedTag(String.valueOf(site.getRedirectList().get(i).getPlacementId()),
                                    site.getRedirectList().get(i).getRedTagList().get(j).getTagType(), 1);

                    if (redirectTag != null) {
                        site.getRedirectList().get(i).getRedTagList().get(j).setStatus(redirectTag.getStatus());
                        site.getRedirectList().get(i).getRedTagList().get(j)
                                .setLastupdatedDate(redirectTag.getLastupdatedDate());
                        site.getRedirectList().get(i).getRedTagList().get(j)
                                .setModifyByName(redirectTag.getModifyByName());
                    }
                }
            }

            return site;
        } else {
            return null;
        }
    }

    @RequestMapping(value = "/getcambyid/{token}/{profileId}/{camId}", method = RequestMethod.GET, produces =
            "application/json")
    @ResponseBody
    public CampaignRP getCamDCMById(@PathVariable String token, @PathVariable long profileId, @PathVariable long camId,
            HttpServletResponse response) {

        CampaignRP result = dcmService.getCamBySearchId(token, profileId, camId);
        if (result != null) {
            response.setStatus(200);
            return result;
        } else {
            response.setStatus(404);
            return null;
        }
    }

    @RequestMapping(value = "/getplacement/{token}/{idCamDCM}/{idPlacement}/{tagType}/{networkId}/{caseNB"
            + "}/{profileId2}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public RedirectTag getRedirectByCamIdandRedId(@PathVariable String token, @PathVariable String idCamDCM,
            @PathVariable String idPlacement, @PathVariable String tagType, @PathVariable int networkId,
            @PathVariable int caseNB, @PathVariable long profileId2) {

        //Call DCM API Service
        if (!auth.isTokenExpired(token)) {
            return dcmService.getTag(idCamDCM, idPlacement, tagType, networkId, caseNB, token, profileId2);
        } else {
            return null;
        }

    }

    @RequestMapping(value = "/oauth2", method = RequestMethod.GET)
    public void Oauth2RequestToken(@RequestParam("code") String code, HttpServletResponse httpServletResponse) {

        try {

            httpServletResponse.sendRedirect(doubleClickRB.getString("path1") + "/index?code=" + code);

        } catch (UnsupportedEncodingException e) {
            logger.error("Oauth2RequestToken error--code: " + code, e);
        } catch (ClientProtocolException e) {
            logger.error("Oauth2RequestToken error--code: " + code, e);
        } catch (IOException e) {
            logger.error("Oauth2RequestToken error--code: " + code, e);
        }

    }

    @RequestMapping(value = "/getuserprofile/{code}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public UserProfileDCMList getUserProfileByCode(@PathVariable String code) {

        code = new String(Base64.decodeBase64(code));

        // save DCM token

        String url = "https://www.googleapis.com/oauth2/v3/token";
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        post.setHeader("User-Agent", "Mozilla/5.0");
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

        ResourceBundle rb = ResourceBundle.getBundle("properties.doubleclick");

        urlParameters.add(new BasicNameValuePair("code", code));
        urlParameters.add(new BasicNameValuePair("client_id", rb.getString("clientId.datait")));
        urlParameters.add(new BasicNameValuePair("client_secret", rb.getString("clientSecret.datait")));
        urlParameters.add(new BasicNameValuePair("grant_type", "authorization_code"));
        urlParameters.add(new BasicNameValuePair("redirect_uri", rb.getString("dcm.redirect_uri_normal")));

        try {
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
            HttpResponse response = client.execute(post);

            if (response.getStatusLine().getStatusCode() == 200) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                StringBuffer result = new StringBuffer();
                String line = "";
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }

                Map<String, String> map = new HashMap<String, String>();
                ObjectMapper mapper = new ObjectMapper();

                map = mapper.readValue(result.toString(), new TypeReference<HashMap<String, String>>() {});

                //call dcm get user profile by list

                auth.saveDCMToken(map.get("access_token"));

                return dcmService.getUserProfileItems(map.get("access_token"));
            } else {
                return null;
            }

        } catch (UnsupportedEncodingException e) {
            logger.error("getUserProfileByCode error--code: " + code, e);
            return null;
        } catch (ClientProtocolException e) {
            logger.error("getUserProfileByCode error--code: " + code, e);
            return null;
        } catch (IOException e) {
            logger.error("getUserProfileByCode error--code: " + code, e);
            return null;
        }
    }

    @RequestMapping(value = "/geturl1", method = RequestMethod.GET, produces = "text/plain;charset=utf-8")
    public String getUrl1() {
        return ServiceAccount.getDCMUrl();
    }

    @RequestMapping(value = "/getcams/{token}/{profileID}/{netWorkId}", method = RequestMethod.GET, produces =
            "application/json")
    @ResponseBody
    public List<CampaignRP> getCampsByNetWork(@PathVariable String token, @PathVariable long profileID,
            @PathVariable long netWorkId) {
        return dcmService.getCamListByNetwork(token, netWorkId, profileID);
    }

    @RequestMapping(value = "/getadvname/{token}/{profileID}/{netWorkId}/{advId}", method = RequestMethod.GET, produces = "appliation/json")
    @ResponseBody
    public String getAdvDCMName(@PathVariable String token, @PathVariable long profileID, @PathVariable long netWorkId,
            @PathVariable long advId) {
        return dcmService.getAdvName(token, netWorkId, profileID, advId);
    }

    @RequestMapping(value = "/getadvnamev1/{token}/{netWorkId}/{advId}", method = RequestMethod.GET, produces = "appliation/json")
    @ResponseBody
    public String getAdvDCMNameV1(@PathVariable String token, @PathVariable long netWorkId, @PathVariable long advId) {

        if (!auth.isTokenExpired(token)) {
            return dcmService.getAdvNameV1(netWorkId, advId);
        } else {
            return null;
        }
    }

    @RequestMapping(value = "/savecamdesc", method = RequestMethod.POST)
    @ResponseBody
    public boolean saveCamDesc(@RequestBody CamDesc camDesc) {
        return dcmService.saveCamDesc(camDesc.getCamId(), camDesc.getAdServer(), camDesc.getDesc());
    }

    @RequestMapping(value = "/getcamdesc/{camId}/{adServer}", method = RequestMethod.GET, produces = "text/plain;charset=utf-8")
    public String getCamDesc(@PathVariable int camId, @PathVariable int adServer) {
        return dcmService.getCamDesc(camId, adServer);
    }

    @RequestMapping(value = "/getCamRetriveDate/{camId}/{adServer}", method = RequestMethod.GET, produces = "text/plain;charset=utf-8")
    public String getCamRetriveDate(@PathVariable int camId, @PathVariable int adServer) {
        return dcmService.getCamRetriveDate(camId, adServer);
    }
}
