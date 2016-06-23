package rest.services;

import com.google.api.client.util.Strings;
import com.google.api.services.dfareporting.Dfareporting;
import com.google.api.services.dfareporting.Dfareporting.Placements.Generatetags;
import com.google.api.services.dfareporting.model.Campaign;
import com.google.api.services.dfareporting.model.CampaignsListResponse;
import com.google.api.services.dfareporting.model.Placement;
import com.google.api.services.dfareporting.model.PlacementTag;
import com.google.api.services.dfareporting.model.PlacementsGenerateTagsResponse;
import com.google.api.services.dfareporting.model.PlacementsListResponse;
import com.google.api.services.dfareporting.model.SitesListResponse;
import com.google.api.services.dfareporting.model.TagData;
import com.google.api.services.dfareporting.model.UserProfile;
import com.google.common.collect.ImmutableList;
import dao.RedirectDao;
import dao.TMDao;
import dao.UserProfileDao;
import models.CampaignRP;
import models.Redirect;
import models.RedirectTag;
import models.Site;
import models.UserProfileDCM;
import models.UserProfileDCMList;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utils.TimeUtil;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DCMService {

    private static final Logger logger = Logger.getLogger(DCMService.class);

    private RedirectDao redirectDao;
    private TMDao tmDao;
    private TMService tmService;
    private UserProfileDao userProfileDao;

    @Autowired
    public void setRedirectDao(RedirectDao redirectDao) {
        this.redirectDao = redirectDao;
    }

    @Autowired
    public void setTmDao(TMDao tmDao) {
        this.tmDao = tmDao;
    }

    @Autowired
    public void setTmService(TMService tmService) {
        this.tmService = tmService;
    }

    @Autowired
    public void setUserProfileDao(UserProfileDao userProfileDao) {
        this.userProfileDao = userProfileDao;
    }

    // The tag formats to generate
    private static final List<String> TAG_FORMATS = ImmutableList
            .of("PLACEMENT_TAG_STANDARD", "PLACEMENT_TAG_JAVASCRIPT", "PLACEMENT_TAG_IFRAME_JAVASCRIPT",
                    "PLACEMENT_TAG_INTERNAL_REDIRECT", "PLACEMENT_TAG_CLICK_COMMANDS", "PLACEMENT_TAG_TRACKING",
                    "PLACEMENT_TAG_TRACKING_IFRAME", "PLACEMENT_TAG_TRACKING_JAVASCRIPT",
                    "PLACEMENT_TAG_INSTREAM_VIDEO_PREFETCH", "PLACEMENT_TAG_IFRAME_JAVASCRIPT_LEGACY",
                    "PLACEMENT_TAG_JAVASCRIPT_LEGACY");

    UserProfileService userProfileService;

    @Autowired
    public void setUserProfileService(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    /**
     * Method to get the campaign's redirects by campaignId and placementId
     *
     * @param reporting
     * @param profileId
     * @param campagneId
     * @param placementId
     * @return list of PlacementTag of the redirects
     * @throws Exception
     */
    public static List<PlacementTag> getRedirectsByEmplacement(Dfareporting reporting, long profileId, long campaignId,
            long placementId) {

        List<String> tagFormats = TAG_FORMATS;
        List<PlacementTag> placementTagList = new ArrayList<PlacementTag>();

        try {
            // Generate the placement activity tags.
            Generatetags request = reporting.placements().generatetags(profileId);
            request.setCampaignId(campaignId);
            request.setTagFormats(tagFormats);
            request.setPlacementIds(ImmutableList.of(placementId));

            PlacementsGenerateTagsResponse response = request.execute();
            placementTagList = response.getPlacementTags();
        } catch (IOException e) {
            logger.error("getRedirectsByEmplacement error--placementId: " + placementId, e);
            return null;
        }

        return placementTagList;
    }

    /**
     * Method to get the campaign's placements by campaign Id
     *
     * @param campagneId
     * @return list of redirects
     * @throws Exception
     */
    public static List<Site> getCampaignRedirects(Dfareporting reporting, long profileId, long campaignId) {

        List<Long> campaignIdList = new ArrayList<Long>();
        campaignIdList.add(campaignId);

        // Limit the fields returned.
        String fields = "nextPageToken,placements(id,lastModifiedInfo,name,siteId,size,pricingSchedule,status,"
                + "archived,comment)";

        PlacementsListResponse placements = null;
        String nextPageToken = null;
        Map<Long, Site> sites = new HashMap<>();

        do {
            try {
                // Create and execute the placements list request.
                placements = reporting.placements().list(profileId).setCampaignIds(campaignIdList).setFields(fields)
                        .setPageToken(nextPageToken).execute();
            } catch (IOException e) {
                logger.error("getCampaignRedirects error--campaignId: " + campaignId, e);
                return null;
            }

            //List<Long> uniqueSiteIdList = new ArrayList<Long>();

            for (Placement placement : placements.getPlacements()) {
                Site site = sites.get(placement.getSiteId());
                if (site == null) {
                    site = new Site(Integer.parseInt(placement.getSiteId() + ""),
                            getSiteName(reporting, profileId, placement.getSiteId()));
                    sites.put(placement.getSiteId(), site);
                }

                Redirect redirect = new Redirect();
                redirect.setPlacementId(placement.getId());
                redirect.setDate_last_modify(
                        TimeUtil.longToString(placement.getLastModifiedInfo().getTime(), "yyyy/MM/dd hh:mm:ss"));
                redirect.setPlacementName(placement.getName());
                redirect.setSiteId((int) (long) placement.getSiteId());
                redirect.setSiteName(site.getSiteName());
                redirect.setPlacementSize(String.valueOf(placement.getSize().getWidth()) + "X" + String
                        .valueOf(placement.getSize().getHeight()));
                redirect.setDate_begin(placement.getPricingSchedule().getStartDate().toString());
                redirect.setDate_end(placement.getPricingSchedule().getEndDate().toString());
                redirect.setStatus(placement.getStatus());
                redirect.setArchived(placement.getArchived());
                if (placement.getArchived()) {
                    redirect.setStatus("ARCHIVED");
                }
                redirect.setDescription(placement.getComment());
                List<RedirectTag> redTagList = new ArrayList<RedirectTag>();
                redirect.setRedTagList(redTagList);
                site.addRedirect(redirect);
                sites.put(placement.getSiteId(), site);
            }

            // Update the next page token.
            nextPageToken = placements.getNextPageToken();
        } while (!placements.getPlacements().isEmpty() && !Strings.isNullOrEmpty(nextPageToken));

        return new ArrayList<Site>(sites.values());
    }

    public static Site getSite(Dfareporting reporting, long profileId, long campaignId, int siteId) {

        List<Long> campaignIdList = new ArrayList<Long>();
        campaignIdList.add(campaignId);

        List<Long> siteIdList = new ArrayList<Long>();
        siteIdList.add((long) siteId);

        // Limit the fields returned.
        String fields = "nextPageToken,placements(id,lastModifiedInfo,name,siteId,size,pricingSchedule,status,"
                + "archived,comment)";
        PlacementsListResponse placements = null;
        String nextPageToken = null;
        Site site = new Site(siteId, getSiteName(reporting, profileId, siteId));
        do {
            try {
                placements = reporting.placements().list(profileId).setCampaignIds(campaignIdList).setFields(fields)
                        .setPageToken(nextPageToken).execute();
                for (Placement placement : placements.getPlacements()) {
                    Redirect redirect = new Redirect();
                    redirect.setPlacementId(placement.getId());
                    site.addRedirect(redirect);
                }
            } catch (IOException e) {
                logger.error("getSite error--siteId: " + siteId, e);
                return null;
            }

            // Update the next page token.
            nextPageToken = placements.getNextPageToken();
        } while (!placements.getPlacements().isEmpty() && !Strings.isNullOrEmpty(nextPageToken));
        return site;
    }

    public static List<Site> getCampaignRedirectsHeavy(Dfareporting reporting, long profileId, long campaignId) {

        List<Long> campaignIdList = new ArrayList<Long>();
        campaignIdList.add(campaignId);

        // Limit the fields returned.
        String fields = "nextPageToken,placements(id,lastModifiedInfo,name,siteId,size,pricingSchedule,status,"
                + "archived,comment)";

        PlacementsListResponse placements = null;
        String nextPageToken = null;
        Map<Long, Site> sites = new HashMap<>();
        do {
            try {
                // Create and execute the placements list request.
                placements = reporting.placements().list(profileId).setCampaignIds(campaignIdList).setFields(fields)
                        .setPageToken(nextPageToken).execute();
            } catch (IOException e) {
                logger.error("getCampaignRedirectsHeavy error--campaignId: " + campaignId, e);
                return null;
            }

            for (Placement placement : placements.getPlacements()) {
                Site site = sites.get(placement.getSiteId());
                if (site == null) {
                    site = new Site(Integer.parseInt(placement.getSiteId() + ""),
                            getSiteName(reporting, profileId, placement.getSiteId()));
                    sites.put(placement.getSiteId(), site);
                }

                List<PlacementTag> placementTagList = getRedirectsByEmplacement(reporting, profileId, campaignId,
                        placement.getId());

                if (placementTagList != null) {

                    for (PlacementTag placementTag : placementTagList) {

                        Redirect redirect = new Redirect();
                        redirect.setPlacementId(placement.getId());
                        redirect.setDate_last_modify(TimeUtil.longToString(placement.getLastModifiedInfo().getTime(),
                                "yyyy/MM/dd hh:mm:ss"));
                        redirect.setPlacementName(placement.getName());
                        redirect.setSiteId((int) (long) placement.getSiteId());
                        redirect.setSiteName(site.getSiteName());
                        redirect.setPlacementSize(String.valueOf(placement.getSize().getWidth()) + "X" + String
                                .valueOf(placement.getSize().getHeight()));
                        redirect.setDate_begin(placement.getPricingSchedule().getStartDate().toString());
                        redirect.setDate_end(placement.getPricingSchedule().getEndDate().toString());
                        redirect.setStatus(placement.getStatus());
                        redirect.setArchived(placement.getArchived());
                        if (placement.getArchived()) {
                            redirect.setStatus("ARCHIVED");
                        }
                        redirect.setDescription(placement.getComment());

                        if (placementTag != null && placementTag.getTagDatas() != null) {
                            Map<String, RedirectTag> redirectTags = new HashMap<>();

                            for (TagData tagData : placementTag.getTagDatas()) {
                                RedirectTag redirectTag = redirectTags.get(tagData.getFormat());
                                String tagValue = tagData.getImpressionTag();
                                if (tagData.getFormat().equals("PLACEMENT_TAG_TRACKING_JAVASCRIPT") || tagData
                                        .getFormat().equals("PLACEMENT_TAG_CLICK_COMMANDS")) {
                                    tagValue = tagData.getClickTag();
                                }

                                if (redirectTag == null) {
                                    redirectTag = new RedirectTag(placement.getId(), tagData.getFormat(), tagValue);
                                    redirectTags.put(tagData.getFormat(), redirectTag);
                                } else {
                                    redirectTag.setTagValue(redirectTag.getTagValue() + "\n\n" + tagValue);
                                }
                            }

                            redirect.setRedTagList(new ArrayList<RedirectTag>(redirectTags.values()));
                        }

                        site.addRedirect(redirect);

                        //	    				List<RedirectTag> redTagList = new ArrayList<RedirectTag>();
                        //	    				if(placementTag != null && placementTag.getTagDatas() != null){
                        //
                        //	    					int trackingIndex=0;
                        //	    					int trackingIframeIndex=0;
                        //	    					int trackingJavascriptIndex=0;
                        //
                        //	    					for(int i=0; i<placementTag.getTagDatas().size(); i++){
                        //	    						if(placementTag.getTagDatas().get(i).getFormat().equals
                        // ("PLACEMENT_TAG_TRACKING")
                        //	             					&& trackingIndex != 0){
                        //
                        //	             						redTagList.get(trackingIndex).setTagValue(redTagList
                        // .get(trackingIndex).getTagValue() +
                        //	             							 "\n\n" + placementTag.getTagDatas().get(i)
                        // .getImpressionTag());
                        //
                        //	    						} else if (placementTag.getTagDatas().get(i).getFormat()
                        // .equals("PLACEMENT_TAG_TRACKING_IFRAME")
                        //	             					&& trackingIframeIndex != 0){
                        //
                        //	             					redTagList.get(trackingIframeIndex).setTagValue(redTagList
                        // .get(trackingIframeIndex).getTagValue() +
                        //	             							"\n\n" + placementTag.getTagDatas().get(i)
                        // .getImpressionTag());
                        //
                        //	    						} else if (placementTag.getTagDatas().get(i).getFormat()
                        // .equals("PLACEMENT_TAG_TRACKING_JAVASCRIPT")
                        //	             					&& trackingJavascriptIndex != 0){
                        //
                        //	    							redTagList.get(trackingJavascriptIndex).setTagValue
                        // (redTagList.get(trackingJavascriptIndex).getTagValue() +
                        //	             							"\n\n" + placementTag.getTagDatas().get(i)
                        // .getClickTag());
                        //
                        //	    						} else {
                        //
                        //	    							RedirectTag redirectTag = new RedirectTag();
                        //	    							redirectTag.setPlacementId(placement.getId());
                        //	    							redirectTag.setTagType(placementTag.getTagDatas().get(i)
                        // .getFormat());
                        //
                        //	    							if(placementTag.getTagDatas().get(i).getFormat().equals
                        // ("PLACEMENT_TAG_TRACKING")) {
                        //	    								redirectTag.setTagValue(placementTag.getTagDatas().get
                        // (i).getImpressionTag());
                        //	    								trackingIndex = redTagList.size();
                        //	    							}
                        //	    							else if (placementTag.getTagDatas().get(i).getFormat()
                        // .equals("PLACEMENT_TAG_TRACKING_IFRAME")) {
                        //	    								redirectTag.setTagValue(placementTag.getTagDatas().get
                        // (i).getImpressionTag());
                        //	    								trackingIframeIndex = redTagList.size();
                        //	    							}
                        //	    							else if (placementTag.getTagDatas().get(i).getFormat()
                        // .equals("PLACEMENT_TAG_TRACKING_JAVASCRIPT")) {
                        //	    								redirectTag.setTagValue(placementTag.getTagDatas().get
                        // (i).getClickTag());
                        //	    								trackingJavascriptIndex = redTagList.size();
                        //	    							}
                        //	    							else if(placementTag.getTagDatas().get(i).getFormat()
                        // .equals("PLACEMENT_TAG_CLICK_COMMANDS")){
                        //	    								redirectTag.setTagValue(placementTag.getTagDatas().get
                        // (i).getClickTag());
                        //	    							}
                        //	    							else {
                        //	    								//redirectTag.setTagValue(placementTag.getTagDatas()
                        // .get(i).getImpressionTag());
                        //	    							}
                        //
                        //	    							if(placementTag.getTagDatas().get(i).getFormat().equals
                        // ("PLACEMENT_TAG_TRACKING")
                        //	    									||placementTag.getTagDatas().get(i).getFormat()
                        // .equals("PLACEMENT_TAG_TRACKING_IFRAME")
                        //	    									||placementTag.getTagDatas().get(i).getFormat()
                        // .equals("PLACEMENT_TAG_TRACKING_JAVASCRIPT")
                        //	    									||placementTag.getTagDatas().get(i).getFormat()
                        // .equals("PLACEMENT_TAG_CLICK_COMMANDS")){
                        //	    								redTagList.add(redirectTag);
                        //	    							}
                        //	    						}
                        //	    					}
                        //
                        //	    					if(redTagList.size()==0){
                        //	    						for(int k=0; k<placementTag.getTagDatas().size(); k++){
                        //	    							RedirectTag redirectTag = new RedirectTag();
                        //	    							redirectTag.setPlacementId(placement.getId());
                        //	    							redirectTag.setTagType(placementTag.getTagDatas().get(k)
                        // .getFormat());
                        //	    							redirectTag.setTagValue(placementTag.getTagDatas().get(k)
                        // .getImpressionTag());
                        //	    							redTagList.add(redirectTag);
                        //	    						}
                        //	    					}
                        //	    					redirect.setRedTagList(redTagList);
                        //	    					site.addRedirect(redirect);
                        //	    				}
                    }
                }
            }

            // Update the next page token.
            nextPageToken = placements.getNextPageToken();
        } while (!placements.getPlacements().isEmpty() && !Strings.isNullOrEmpty(nextPageToken));

        return new ArrayList<Site>(sites.values());
    }

    public static Site getSiteRedirect(Dfareporting reporting, long profileId, long campaignId, long siteId) {

        Site site = new Site();
        site.setSiteId((int) siteId);

        List<Long> campaignIdList = new ArrayList<Long>();
        campaignIdList.add(campaignId);

        List<Long> siteIdList = new ArrayList<Long>();
        siteIdList.add(siteId);

        // Limit the fields returned.
        String fields = "nextPageToken,placements(id,lastModifiedInfo,name,siteId,size,pricingSchedule,status,"
                + "archived,comment)";

        PlacementsListResponse placements = null;
        String nextPageToken = null;

        do {

            try {
                // Create and execute the placements list request.
                placements = reporting.placements().list(profileId).setCampaignIds(campaignIdList)
                        .setSiteIds(siteIdList).setFields(fields).setPageToken(nextPageToken).execute();

            } catch (IOException e) {
                logger.error("getSiteRedirect error--siteId: " + siteId, e);
                return null;
            }

            List<Redirect> redirectList = new ArrayList<Redirect>();
            for (Placement placement : placements.getPlacements()) {

                String siteName = getSiteName(reporting, profileId, placement.getSiteId());
                site.setSiteName(siteName);

                List<PlacementTag> placementTagList = getRedirectsByEmplacement(reporting, profileId, campaignId,
                        placement.getId());

                if (placementTagList != null) {

                    for (PlacementTag placementTag : placementTagList) {

                        Redirect redirect = new Redirect();
                        redirect.setCamId(campaignId);
                        redirect.setPlacementId(placement.getId());
                        redirect.setDate_last_modify(TimeUtil.longToString(placement.getLastModifiedInfo().getTime(),
                                "yyyy/MM/dd hh:mm:ss"));
                        redirect.setPlacementName(placement.getName());
                        redirect.setSiteId((int) (long) placement.getSiteId());
                        redirect.setSiteName(siteName);
                        redirect.setPlacementSize(String.valueOf(placement.getSize().getWidth()) + "X" + String
                                .valueOf(placement.getSize().getHeight()));
                        redirect.setDate_begin(placement.getPricingSchedule().getStartDate().toString());
                        redirect.setDate_end(placement.getPricingSchedule().getEndDate().toString());
                        redirect.setStatus(placement.getStatus());
                        redirect.setArchived(placement.getArchived());
                        if (placement.getArchived()) {
                            redirect.setStatus("ARCHIVED");
                        }
                        redirect.setDescription(placement.getComment());

                        if (placementTag != null && placementTag.getTagDatas() != null) {
                            Map<String, RedirectTag> redirectTags = new HashMap<>();

                            for (TagData tagData : placementTag.getTagDatas()) {
                                RedirectTag redirectTag = redirectTags.get(tagData.getFormat());
                                String tagValue = tagData.getImpressionTag();
                                if (tagData.getFormat().equals("PLACEMENT_TAG_TRACKING_JAVASCRIPT") || tagData
                                        .getFormat().equals("PLACEMENT_TAG_CLICK_COMMANDS")) {
                                    tagValue = tagData.getClickTag();
                                }

                                if (redirectTag == null) {
                                    redirectTag = new RedirectTag(placement.getId(), tagData.getFormat(), tagValue);
                                    redirectTags.put(tagData.getFormat(), redirectTag);
                                } else {
                                    redirectTag.setTagValue(redirectTag.getTagValue() + "\n\n" + tagValue);
                                }
                            }

                            redirect.setRedTagList(new ArrayList<RedirectTag>(redirectTags.values()));
                        }

                        redirectList.add(redirect);
                    }
                }

                site.setRedirectList(redirectList);
            }

            // Update the next page token.
            nextPageToken = placements.getNextPageToken();
        } while (!placements.getPlacements().isEmpty() && !Strings.isNullOrEmpty(nextPageToken));

        return site;

    }

    public static int getCampaignRedirectsNB(Dfareporting reporting, long profileId, long campaignId) {

        List<Long> campaignIdList = new ArrayList<Long>();
        campaignIdList.add(campaignId);

        // Limit the fields returned.
        String fields = "nextPageToken,placements(id)";

        PlacementsListResponse placements = null;
        String nextPageToken = null;

        int result = 0;

        try {

            do {

                placements = reporting.placements().list(profileId).setCampaignIds(campaignIdList).setFields(fields)
                        .setPageToken(nextPageToken).execute();

                result = result + placements.getPlacements().size();

                nextPageToken = placements.getNextPageToken();

            } while (!placements.getPlacements().isEmpty() && !Strings.isNullOrEmpty(nextPageToken));

        } catch (IOException e) {
            logger.error("getCampaignRedirectsNB error--campaignId: " + campaignId, e);
            return result;
        }

        return result;

    }

    /**
     * Method to get the campaign with redirects by campaign Id
     *
     * @param campagneId
     * @return campaign with the redirects
     * @throws Exception
     */
    public CampaignRP getRedirectsByCampaignId(String campaignId, int networkId, int caseNB, String token,
            long profileId2) {

        Dfareporting reporting = null;
        try {
            if (caseNB == 1) { reporting = ServiceAccount.getServiceAccount(); } else {
                reporting = ServiceAccount.getServiceAccount2(token);
            }

        } catch (Exception e) {
            logger.error("getRedirectsByCampaignId error--campaignId: " + campaignId, e);
            return null;
        }

        List<Long> campaignIdList = new ArrayList<Long>();
        campaignIdList.add(Long.parseLong(campaignId));

        // Limit the fields returned.
        String fields = "nextPageToken,campaigns(id,name,accountId,advertiserId,archived,endDate,startDate)";

        CampaignsListResponse campaignsListResponse = null;
        //String nextPageToken = null;

        CampaignRP campaignRP = new CampaignRP();
        List<Site> siteList = new ArrayList<Site>();

        try {

            long profileId;

            if (caseNB == 1) { profileId = userProfileService.getUserProfileIdByResId(networkId); } else {
                profileId = profileId2;
            }
            if (profileId != 0) {
                siteList = getCampaignRedirects(reporting, profileId, Long.parseLong(campaignId));
                // Create and execute the campaigns list request.
                campaignsListResponse = reporting.campaigns().list(profileId).setFields(fields).setIds(campaignIdList)
                        .execute();
            }
        } catch (IOException e) {
            logger.error("getRedirectsByCampaignId error--campaignId: " + campaignId, e);
            return null;
        }

        if (campaignsListResponse != null && !campaignsListResponse.getCampaigns().isEmpty()) {
            Campaign campaign = campaignsListResponse.getCampaigns().get(0);
            campaignRP.setAdServer("DCM");
            campaignRP.setCamId(campaign.getId());
            campaignRP.setNetworkId(campaign.getAccountId());
            campaignRP.setCamName(campaign.getName());
            campaignRP.setAdvertiserId(campaign.getAdvertiserId());
            campaignRP.setBegindate(campaign.getStartDate().toString());
            campaignRP.setEnddate(campaign.getEndDate().toString());
            campaignRP.setSiteList(siteList);
            if (campaign.getArchived()) {
                for (Site site : campaignRP.getSiteList()) {
                    for (Redirect red : site.getRedirectList()) {
                        red.setStatus("ARCHIVED");

                    }
                }
            } else {
                for (Site site : campaignRP.getSiteList()) {
                    for (Redirect red : site.getRedirectList()) {
                        if (tmDao.isPlacementValid((int) red.getPlacementId(), 1) != null) {
                            red.setStatus(tmDao.isPlacementValid((int) red.getPlacementId(), 1));
                        }

                    }
                }
            }
        }

        CampaignRP camRPBase = redirectDao.getAllRedirectsByCamId(Long.valueOf(campaignId), 1);

        if (camRPBase != null && camRPBase.getLastRetriveDate() != null) {
            campaignRP.setLastRetriveDate(camRPBase.getLastRetriveDate());
        }
        if (camRPBase != null) {
            campaignRP.setHasModifToRetrive(camRPBase.isHasModifToRetrive());
        }

        return campaignRP;
    }

    public int getRedirectsNBByCampaignId(String campaignId, int networkId, int caseNB, String token, long profileId2) {

        Dfareporting reporting = null;
        try {
            if (caseNB == 1) { reporting = ServiceAccount.getServiceAccount(); } else {
                reporting = ServiceAccount.getServiceAccount2(token);
            }

        } catch (Exception e) {
            logger.error("getRedirectsNBByCampaignId error--campaignId: " + campaignId, e);
        }

        List<Long> campaignIdList = new ArrayList<Long>();
        campaignIdList.add(Long.parseLong(campaignId));

        int result = 0;

        long profileId;

        if (caseNB == 1) { profileId = userProfileService.getUserProfileIdByResId(networkId); } else {
            profileId = profileId2;
        }

        if (profileId != 0) {
            result = getCampaignRedirectsNB(reporting, profileId, Long.parseLong(campaignId));
        }

        return result;
    }

    public Redirect rechargePlacmentById(String campaignId, int networkId, int idPlacement, int caseNB, String token,
            long profileId2) {
        Redirect result = new Redirect();
        result.setCamId(Long.parseLong(campaignId));
        result.setPlacementId(idPlacement);

        List<RedirectTag> redTagList = new ArrayList<RedirectTag>();
        redTagList = rechargesTags(campaignId, String.valueOf(idPlacement), networkId, caseNB, token, profileId2);
        //search in TM after
        for (int i = 0; i < redTagList.size(); i++) {
            RedirectTag redirectTag;
            redirectTag = tmService.getSingleTagTM(String.valueOf(idPlacement), redTagList.get(i).getTagType(), 1);

            if (null != redirectTag) {
                redTagList.set(i, redirectTag);
            }
        }

        result.setRedTagList(redTagList);

        return result;
    }

    public Redirect getPlacmentTagDCMById(String campaignId, int networkId, long idPlacement, int caseNB,
            String token, long profileId) {
        Redirect result = new Redirect();
        result.setCamId(Long.parseLong(campaignId));
        result.setPlacementId(idPlacement);

        List<RedirectTag> redTagList = new ArrayList<RedirectTag>();
        if(caseNB == 1){
        	profileId = userProfileDao.getUserProfileId(networkId);
        }
        redTagList = rechargesTags(campaignId, String.valueOf(idPlacement), networkId, caseNB, token, profileId);

        result.setRedTagList(redTagList);

        return result;
    }

    public Site getSiteDCM(String campaignId, int networkId, int siteId, int caseNB, String token, long profileId) {

        Dfareporting reporting = null;
        try {
            if (caseNB == 1) {
                reporting = ServiceAccount.getServiceAccount();
                profileId = userProfileService.getUserProfileIdByResId(networkId);
            } else {
                reporting = ServiceAccount.getServiceAccount2(token);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            return null;
        }

        Site site = getSiteRedirect(reporting, profileId, Long.valueOf(campaignId), siteId);

        return site;
    }

    /**
     * Method to get the tag by placement id and tag type
     *
     * @param campagneId
     * @return campaign with the redirects
     * @throws Exception
     */
    public RedirectTag getTag(String campaignId, String idPlacement, String tagType, int networkId, int caseNB,
            String token, long profileId2) {

        Dfareporting reporting = null;
        try {
            if (caseNB == 1) { reporting = ServiceAccount.getServiceAccount(); } else {
                reporting = ServiceAccount.getServiceAccount2(token);
            }
        } catch (Exception e) {
            logger.error("getTag error--campaignId: " + campaignId, e);
            return null;
        }

        long profileId;
        if (caseNB == 1) { profileId = userProfileService.getUserProfileIdByResId(networkId); } else {
            profileId = profileId2;
        }

        List<Site> siteList = new ArrayList<Site>();
        siteList = getCampaignRedirectsHeavy(reporting, profileId, Long.parseLong(campaignId));

        RedirectTag redirectTag = new RedirectTag();
        for (Site site : siteList) {
            for (Redirect redirect : site.getRedirectList()) {
                if (Long.valueOf(idPlacement) == redirect.getPlacementId()) {
                    for (RedirectTag redTag : redirect.getRedTagList()) {
                        if (redTag.getTagType().equals(tagType)) {

                            redirectTag = redTag;
                        }
                    }
                }
            }
        }

        return redirectTag;
    }

    public List<RedirectTag> rechargesTags(String campaignId, String idPlacement, int networkId, int caseNB,
            String token, long profileId2) {

        List<RedirectTag> result = new ArrayList<RedirectTag>();

        Dfareporting reporting = null;
        try {
            if (caseNB == 1) { 
            	reporting = ServiceAccount.getServiceAccount(); 
            } else {
                reporting = ServiceAccount.getServiceAccount2(token);
            }
        } catch (Exception e) {
            logger.error("rechargesTags error--idPlacement: " + idPlacement, e);
            return null;
        }

        long profileId;
        if (caseNB == 1) { 
        	profileId = userProfileService.getUserProfileIdByResId(networkId); 
        } else {
            profileId = profileId2;
        }

        List<PlacementTag> placementTagList = getRedirectsByEmplacement(reporting, profileId,
                Long.parseLong(campaignId), Long.parseLong(idPlacement));

        if (placementTagList != null) {
            for (PlacementTag placementTag : placementTagList) {
                if (placementTag != null && placementTag.getTagDatas() != null) {

                    int trackingIndex = 0;
                    int trackingIframeIndex = 0;
                    int trackingJavascriptIndex = 0;

                    for (int i = 0; i < placementTag.getTagDatas().size(); i++) {

                        if (placementTag.getTagDatas().get(i).getFormat().equals("PLACEMENT_TAG_TRACKING")
                                && trackingIndex != 0) {

                            result.get(trackingIndex).setTagValue(result.get(trackingIndex).getTagValue() +
                                    "\n\n" + placementTag.getTagDatas().get(i).getImpressionTag());

                        } else if (placementTag.getTagDatas().get(i).getFormat().equals("PLACEMENT_TAG_TRACKING_IFRAME")
                                && trackingIframeIndex != 0) {

                            result.get(trackingIframeIndex).setTagValue(result.get(trackingIframeIndex).getTagValue() +
                                    "\n\n" + placementTag.getTagDatas().get(i).getImpressionTag());

                        } else if (placementTag.getTagDatas().get(i).getFormat()
                                .equals("PLACEMENT_TAG_TRACKING_JAVASCRIPT") && trackingJavascriptIndex != 0) {

                            result.get(trackingJavascriptIndex)
                                    .setTagValue(result.get(trackingJavascriptIndex).getTagValue() +
                                            "\n\n" + placementTag.getTagDatas().get(i).getClickTag());

                        } else {

                            RedirectTag redirectTag = new RedirectTag();
                            redirectTag.setPlacementId(Long.parseLong(idPlacement));
                            redirectTag.setTagType(placementTag.getTagDatas().get(i).getFormat());

                            if (placementTag.getTagDatas().get(i).getFormat().equals("PLACEMENT_TAG_TRACKING")) {

                                redirectTag.setTagValue(placementTag.getTagDatas().get(i).getImpressionTag());
                                trackingIndex = result.size();
                            } else if (placementTag.getTagDatas().get(i).getFormat()
                                    .equals("PLACEMENT_TAG_TRACKING_IFRAME")) {

                                redirectTag.setTagValue(placementTag.getTagDatas().get(i).getImpressionTag());
                                trackingIframeIndex = result.size();
                            } else if (placementTag.getTagDatas().get(i).getFormat()
                                    .equals("PLACEMENT_TAG_TRACKING_JAVASCRIPT")) {

                                redirectTag.setTagValue(placementTag.getTagDatas().get(i).getClickTag());
                                trackingJavascriptIndex = result.size();
                            } else if (placementTag.getTagDatas().get(i).getFormat()
                                    .equals("PLACEMENT_TAG_CLICK_COMMANDS")) {

                                redirectTag.setTagValue(placementTag.getTagDatas().get(i).getClickTag());
                            } else {

                                //redirectTag.setTagValue(placementTag.getTagDatas().get(i).getImpressionTag());
                            }

                            if (placementTag.getTagDatas().get(i).getFormat().equals("PLACEMENT_TAG_TRACKING")
                                    || placementTag.getTagDatas().get(i).getFormat()
                                    .equals("PLACEMENT_TAG_TRACKING_IFRAME") || placementTag.getTagDatas().get(i)
                                    .getFormat().equals("PLACEMENT_TAG_TRACKING_JAVASCRIPT") || placementTag
                                    .getTagDatas().get(i).getFormat().equals("PLACEMENT_TAG_CLICK_COMMANDS")) {
                                result.add(redirectTag);
                            }
                        }
                    }

                    if (result.size() == 0) {

                        for (int l = 0; l < placementTag.getTagDatas().size(); l++) {
                            RedirectTag redirectTag = new RedirectTag();
                            redirectTag.setPlacementId(Long.parseLong(idPlacement));
                            redirectTag.setTagType(placementTag.getTagDatas().get(l).getFormat());
                            redirectTag.setTagValue(placementTag.getTagDatas().get(l).getImpressionTag());

                            result.add(redirectTag);
                        }
                    }

                }
            }
        }

        return result;
    }

    /**
     * M�thode pour r�cup�rer des sites
     *
     * @param reporting
     * @param profileId
     * @param siteId
     * @return site name
     * @throws Exception
     */
    public static String getSiteName(Dfareporting reporting, long profileId, long siteId) {

        String siteName = null;
        List<Long> siteList = new ArrayList<Long>();
        siteList.add(siteId);
        // Limit the fields returned.
        String fields = "nextPageToken,sites(id,name)";

        SitesListResponse sitesListResponse = null;
        String nextPageToken = null;

        // Create and execute the sites list request.
        try {
            sitesListResponse = reporting.sites().list(profileId).setFields(fields).setIds(siteList)
                    .setPageToken(nextPageToken).execute();
        } catch (IOException e) {
            logger.error("getSiteName error--siteId: " + siteId, e);
            return null;
        }

        if (sitesListResponse != null && sitesListResponse.getSites() != null) {
            siteName = sitesListResponse.getSites().get(0).getName();
        }
        return siteName;
    }

    public UserProfileDCMList getUserProfileItems(String accessToken) {
        Dfareporting report = ServiceAccount.getServiceAccount2(accessToken);
        List<UserProfile> result = null;
        List<UserProfileDCM> result2 = new ArrayList<UserProfileDCM>();
        try {
            result = report.userProfiles().list().execute().getItems();

            for (UserProfile up : result) {

                UserProfileDCM updcm = new UserProfileDCM();
                updcm.setNetworkId(up.getAccountId());
                updcm.setNetworkName(up.getAccountName());
                updcm.setProfileId(up.getProfileId());
                updcm.setUserName(up.getUserName());
                updcm.setToken(accessToken);
                updcm.setHavasOwned(true);
                if (up.getSubAccountName() != null && up.getSubAccountName() != "") {
                    updcm.setSubAccountId(up.getSubAccountId());
                    updcm.setSubAccountName(up.getSubAccountName());
                }

                result2.add(updcm);

            }
            Collections.sort(result2, new Comparator<UserProfileDCM>() {

                @Override
                public int compare(UserProfileDCM o1, UserProfileDCM o2) {
                    if (o1.getNetworkId() < o2.getNetworkId()) { return -1; }
                    if (o1.getNetworkId() == o2.getNetworkId()) { return 0; } else { return 1; }
                }

            });

            UserProfileDCMList resultFinal = new UserProfileDCMList();
            resultFinal.setToken(accessToken);
            resultFinal.setUserProfileList(result2);

            return resultFinal;
        } catch (IOException e) {
            logger.error("getUserProfileItems error--accessToken: " + accessToken, e);
            return null;
        }

    }

    public List<CampaignRP> getCamListByNetwork(String accessToken, long networkId, long profileId) {

        Dfareporting report = ServiceAccount.getServiceAccount2(accessToken);

        try {

            String fields = "nextPageToken,campaigns(id,name,accountId,advertiserId,archived,endDate,startDate)";
            List<CampaignRP> result2 = new ArrayList<CampaignRP>();
            String nextPageToken = null;

            CampaignsListResponse result;

            do {
                result = report.campaigns().list(profileId).setFields(fields).setPageToken(nextPageToken)
                        .setArchived(false).setSortField("ID").setSortOrder("ASCENDING").execute();

                if (result.getCampaigns() != null) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    for (Campaign cam : result.getCampaigns()) {
                        CampaignRP camRP = new CampaignRP();
                        try {

                            Date tmp = formatter.parse(cam.getEndDate().toString());

                            if (new Date().compareTo(tmp) <= 0) {
                                camRP.setAdServer("DCM");
                                camRP.setAdvertiserId(cam.getAdvertiserId());
                                camRP.setCamId(cam.getId());
                                camRP.setCamName(cam.getName());
                                camRP.setBegindate(cam.getStartDate().toString());
                                camRP.setEnddate(cam.getEndDate().toString());
                                result2.add(camRP);
                            }

                        } catch (ParseException e) {
                            logger.error("getCamListByNetwork error--networkId: " + networkId, e);
                        }

                    }
                }

                nextPageToken = result.getNextPageToken();
            } while (!result.getCampaigns().isEmpty() && !Strings.isNullOrEmpty(nextPageToken));

            return result2;
        } catch (IOException e) {
            logger.error("getCamListByNetwork error--networkId: " + networkId, e);
            return null;
        }
    }

    public String getAdvName(String accessToken, long networkId, long profileId, long advId) {
        Dfareporting report = ServiceAccount.getServiceAccount2(accessToken);
        String fields = "name";
        try {
            return report.advertisers().get(profileId, advId).setFields(fields).execute().getName();
        } catch (IOException e) {
            logger.error("getAdvName error--advId: " + advId, e);
            return "";
        }
    }

    public String getAdvNameV1(long networkId, long advId) {
        Dfareporting report;
        try {
            report = ServiceAccount.getServiceAccount();
            String fields = "name";
            long profileId = userProfileService.getUserProfileIdByResId((int) networkId);
            return report.advertisers().get(profileId, advId).setFields(fields).execute().getName();
        } catch (IOException e) {
            logger.error("getAdvNameV1 error--advId: " + advId, e);
            return "N/A";
        } catch (Exception e) {
            logger.error("getAdvNameV1 error--advId: " + advId, e);
            return "N/A";
        }

    }

    public CampaignRP getCamBySearchId(String token, long profileId, long camId) {
        Dfareporting report = ServiceAccount.getServiceAccount2(token);
        String fields = "id,name,accountId,advertiserId,archived,endDate,startDate";

        try {
            Campaign cam = report.campaigns().get(profileId, camId).setFields(fields).execute();
            CampaignRP camRP = new CampaignRP();
            if (cam != null) {
                camRP.setAdServer("DCM");
                camRP.setAdvertiserId(cam.getAdvertiserId());
                camRP.setBegindate(cam.getStartDate().toString());
                camRP.setCamId(cam.getId());
                camRP.setCamName(cam.getName());
                camRP.setEnddate(cam.getEndDate().toString());
                camRP.setNetworkId(cam.getAccountId());

            }
            return camRP;
        } catch (IOException e) {

            logger.error("getCamBySearchId error--camId: " + camId, e);
            return null;
        }

    }

    public boolean saveCamDesc(int camId, int adServer, String desc) {
        return redirectDao.saveCamDesc(camId, adServer, desc);
    }

    public String getCamDesc(int camId, int adServer) {
        return redirectDao.getCamDesc(camId, adServer);
    }

    public String getCamRetriveDate(int camId, int adServer) {
        return redirectDao.getCamRetriveDate(camId, adServer);
    }
}
