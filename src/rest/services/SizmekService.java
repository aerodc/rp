package rest.services;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eyeblaster.api.advertiser.AdvertiserServiceStub;
import com.eyeblaster.api.advertiser.message.GetAdvertisersRequest;
import com.eyeblaster.api.advertiser.message.GetAdvertisersResponse;
import com.eyeblaster.api.authentication.AuthenticationServiceStub;
import com.eyeblaster.api.authentication.ClientImpersonation;
import com.eyeblaster.api.authentication.ClientImpersonationResponse;
import com.eyeblaster.api.authentication.ClientLogin;
import com.eyeblaster.api.authentication.ClientLoginResponse;
import com.eyeblaster.api.authentication.message.GetClientInfoRequest;
import com.eyeblaster.api.campaign.CampaignServiceStub;
import com.eyeblaster.api.campaign.message.GetCampaignSitesRequest;
import com.eyeblaster.api.campaign.message.GetCampaignSitesResponse;
import com.eyeblaster.api.campaign.message.GetCampaignsRequest;
import com.eyeblaster.api.campaign.message.GetCampaignsResponse;
import com.eyeblaster.api.placement.PlacementServiceStub;
import com.eyeblaster.api.placement.message.GetPlacementTagRequest;
import com.eyeblaster.api.placement.message.GetPlacementTagSettingsRequest;
import com.eyeblaster.api.placement.message.GetPlacementTagsRequest;
import com.eyeblaster.api.placement.message.GetPlacementsRequest;
import com.eyeblaster.api.placement.message.GetPlacementsResponse;
import com.eyeblaster.api.v1.datacontracts.APIDateTime;
import com.eyeblaster.api.v1.datacontracts.AdvertiserIDFilter;
import com.eyeblaster.api.v1.datacontracts.AdvertiserInfo;
import com.eyeblaster.api.v1.datacontracts.ArrayOfAdvertiserServiceFilter;
import com.eyeblaster.api.v1.datacontracts.ArrayOfCampaignServiceFilter;
import com.eyeblaster.api.v1.datacontracts.ArrayOfPlacementServiceFilter;
import com.eyeblaster.api.v1.datacontracts.BasicInfo;
import com.eyeblaster.api.v1.datacontracts.CampaignIDFilter;
import com.eyeblaster.api.v1.datacontracts.CampaignInfo;
import com.eyeblaster.api.v1.datacontracts.CampaignRecentFilter;
import com.eyeblaster.api.v1.datacontracts.PlacementCampaignIDFilter;
import com.eyeblaster.api.v1.datacontracts.PlacementInfo;
import com.eyeblaster.api.v1.datacontracts.PlacementSiteIDFilter;
import com.eyeblaster.api.v1.datacontracts.PlacementTagSettingsInfo;
import com.eyeblaster.api.v1.datacontracts.PlacementTagTypes;
import com.eyeblaster.api.v2.datacontracts.ArrayOfPlacementTagsGenerationRequestInfo;
import com.eyeblaster.api.v2.datacontracts.PlacementTagsGenerationRequestInfo;
import com.eyeblaster.api.v2.datacontracts.PlacementTagsGenerationResponseInfo;

import dao.TMDao;
import dao.UserProfileDao;
import models.CampaignRP;
import models.Network;
import models.Redirect;
import models.RedirectTag;
import models.Site;

@Service
public class SizmekService {
	
	private static final Logger logger = Logger.getLogger(SizmekService.class);
	
	private static ResourceBundle sizmekRB = ResourceBundle.getBundle("properties.sizmek");
	
	private static final String SM_USER=sizmekRB.getString("sm.user");
	private static final String SM_PASSWORD=sizmekRB.getString("sm.password");
	private static final String SM_APIKEY=sizmekRB.getString("sm.apikey");
	
	private static final int PAGE_SIZE = 50;
	
	private TMDao tmDao;
	
	private UserProfileDao userProfileDao;
	
	@Autowired
	public void setTmDao(TMDao tmDao) {
		this.tmDao = tmDao;
	}
	
	@Autowired
	public void setUserProfileDao(UserProfileDao userProfileDao) {
		this.userProfileDao = userProfileDao;
	}
	
	//case 1:get a Sizmek Token for api request using data-it acount
	public String getClientResponseToken(int camId){
		
		String resKey = userProfileDao.getSizmekResKeyByCampaign(camId);
		Network network = userProfileDao.getNetworkByResKey(resKey, 2);
		
		ClientLogin clientLogin = new ClientLogin();
		clientLogin.setUsername(network.getLogin());
		clientLogin.setPassword(network.getPassword());
		clientLogin.setApplicationKey(resKey);
		AuthenticationServiceStub authService;
		ClientLoginResponse clientResponse = null;
		try {
			authService = new AuthenticationServiceStub();
			authService._getServiceClient().getOptions().setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, "false");
			clientResponse = authService.clientLogin(clientLogin);
			
		} catch (AxisFault e) {
			logger.error("getClientResponseToken error--camId: " + camId, e);
		} catch (RemoteException e) {
			logger.error("getClientResponseToken error--camId: " + camId, e);
		}
		
		if(clientResponse!=null)
			return clientResponse.getClientLoginResult();
		else
			return null;
		
	}	

	//case 2: get a Sizmek Token for api request using personal account
	public String getPersonalToken(String login, String pwd){
		
		ClientLogin clientLogin = new ClientLogin();
		try {
			AuthenticationServiceStub authService = new AuthenticationServiceStub();
			ClientLoginResponse clientResponse = null;
			ClientImpersonationResponse clientImpersonationResponse = null;
			clientLogin.setUsername(SM_USER);
			clientLogin.setPassword(SM_PASSWORD);
			clientLogin.setApplicationKey(SM_APIKEY);
			authService._getServiceClient().getOptions().setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, "false");
			clientResponse = authService.clientLogin(clientLogin);
				
			if(clientResponse != null){
				ClientImpersonation clientImpersonation = new ClientImpersonation();
				clientImpersonation.setUserSecurityToken(clientResponse.getClientLoginResult());
				clientImpersonation.setUsername(login);
				clientImpersonation.setPassword(pwd);
				clientImpersonationResponse = authService.clientImpersonation(clientImpersonation);
				if(clientImpersonationResponse != null){
					return clientImpersonationResponse.getClientImpersonationResult();
				}
			}	
		} catch (AxisFault e) {
			logger.error("getPersonalToken error--login: " + login, e);
			if(e.getMessage().equals("Read timed out")){
				return "time out";
			}
		} catch (RemoteException e) {
			logger.error("getPersonalToken error--login: " + login, e);
		}	
		
		return "false";
	}
	
	public CampaignRP getSizmekCampaignById(int camId, String apiToken){
		CampaignRP campaignRP = new CampaignRP();
		
		com.eyeblaster.api.campaign.message.UserSecurityToken campaignSecurityToken = new com.eyeblaster.api.campaign.message.UserSecurityToken();
		
		if(apiToken != null){
			campaignSecurityToken.setUserSecurityToken(apiToken);
			GetCampaignsRequest getCampaignsRequest = new GetCampaignsRequest();
			ArrayOfCampaignServiceFilter arrayOfCampaignServiceFilter = new ArrayOfCampaignServiceFilter();
			CampaignIDFilter campaignIDFilter = new CampaignIDFilter();
			campaignIDFilter.setCampaignID(camId);
			arrayOfCampaignServiceFilter.addCampaignServiceFilter(campaignIDFilter);
			getCampaignsRequest.setCampaignsFilter(arrayOfCampaignServiceFilter);
			com.eyeblaster.api.campaign.message.ListPaging paging = new com.eyeblaster.api.campaign.message.ListPaging();
			paging.setPageIndex(0);
			paging.setPageSize(1);
			getCampaignsRequest.setPaging(paging);
			getCampaignsRequest.setShowCampaignExtendedInfo(true);
			
			try {
				CampaignServiceStub campaignService = new CampaignServiceStub();
				campaignService._getServiceClient().getOptions().setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, "false");
				
				GetCampaignsResponse campaignsResponse = campaignService.getCampaigns(getCampaignsRequest, campaignSecurityToken);
				
				if(campaignsResponse!=null && campaignsResponse.getCampaigns()!=null){
					CampaignInfo camInfo=campaignsResponse.getCampaigns().getCampaignInfo()[0];
					campaignRP.setCamName(camInfo.getName());
					campaignRP.setAdServer("SIZMEK");
					campaignRP.setAdvertiserId(camInfo.getAdvertiserID());
					campaignRP.setBegindate(getDate(camInfo.getCampaignExtendedInfo().getStartDate()));
					campaignRP.setEnddate(getDate(camInfo.getCampaignExtendedInfo().getEndDate()));
					campaignRP.setCamId(camId);
					campaignRP.setDescription(camInfo.getSpecialInstructions());
					campaignRP.setHasModifToRetrive(false);
				}		
				return campaignRP;
				
			} catch (AxisFault e) {
				logger.error("getSizmekCampaignById error--camId: " + camId, e);
			} catch (RemoteException e) {
				logger.error("getSizmekCampaignById error--camId: " + camId, e);
			}
			return null;
		}else{
			return null;
		}
	}
	
	public List<Site> getSizmekCampaignSites(int camId, String apiToken){
		com.eyeblaster.api.campaign.message.UserSecurityToken campaignSecurityToken = new com.eyeblaster.api.campaign.message.UserSecurityToken();
		List<Site> sites = new ArrayList<Site>();
		
		if(apiToken != null){
	
			campaignSecurityToken.setUserSecurityToken(apiToken);
			GetCampaignSitesRequest getCampaignSitesRequest = new GetCampaignSitesRequest();			
			getCampaignSitesRequest.setCampaignID(camId);
				
			try {
				CampaignServiceStub campaignService = new CampaignServiceStub();
				campaignService._getServiceClient().getOptions().setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, "false");
				
				GetCampaignSitesResponse getCampaignSitesResponse = campaignService.getCampaignSites(getCampaignSitesRequest, campaignSecurityToken);
				
				if(getCampaignSitesResponse!=null && getCampaignSitesResponse.getCampaignSites()!=null){
					for(BasicInfo siteSM: getCampaignSitesResponse.getCampaignSites().getBasicInfo()){
						
						Site site = new Site();
						site.setSiteId(siteSM.getID());
						site.setSiteName(siteSM.getDisplayText());
						
						sites.add(site);
					}	
					return sites;
				}
			} catch (AxisFault e) {
				logger.error("getSizmekCampaignSites error--camId: " + camId, e);
			} catch (RemoteException e) {
				logger.error("getSizmekCampaignSites error--camId: " + camId, e);
			}
			return null;	
		}else{
			return null;
		}
	}

	public List<CampaignRP> getRecentCampagnesList(String apiToken){
		List<CampaignRP> result = new ArrayList<CampaignRP>();
		com.eyeblaster.api.authentication.message.UserSecurityToken securityToken= new com.eyeblaster.api.authentication.message.UserSecurityToken();
		com.eyeblaster.api.campaign.message.UserSecurityToken campaignSecurityToken=new com.eyeblaster.api.campaign.message.UserSecurityToken();
		securityToken.setUserSecurityToken(apiToken);
		campaignSecurityToken.setUserSecurityToken(apiToken);
		GetCampaignsRequest getCampaignsRequest = new GetCampaignsRequest();
		ArrayOfCampaignServiceFilter arrayOfCampaignServiceFilter = new ArrayOfCampaignServiceFilter();
		CampaignRecentFilter campaignRecentFilter = new CampaignRecentFilter();
		campaignRecentFilter.setIsRecent(true);
		arrayOfCampaignServiceFilter.addCampaignServiceFilter(campaignRecentFilter);
		getCampaignsRequest.setCampaignsFilter(arrayOfCampaignServiceFilter);
		com.eyeblaster.api.campaign.message.ListPaging paging = new com.eyeblaster.api.campaign.message.ListPaging();
		paging.setPageSize(PAGE_SIZE);
		getCampaignsRequest.setPaging(paging);
		getCampaignsRequest.setShowCampaignExtendedInfo(true);
		CampaignServiceStub campaignService;
		try {
			campaignService = new CampaignServiceStub();
			campaignService._getServiceClient().getOptions().setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, "false");
			int max=PAGE_SIZE;
			int i=0;
			do {
				try {
					GetCampaignsResponse getCampaignsResponse = campaignService.getCampaigns(getCampaignsRequest, campaignSecurityToken);
					paging.setPageIndex(i);
					getCampaignsRequest.setPaging(paging);
					
					if (getCampaignsResponse!=null){
						if(getCampaignsResponse.getTotalCount() >= max)
							max += PAGE_SIZE;
						
						if(getCampaignsResponse.getCampaigns().getCampaignInfo() != null){
							for(CampaignInfo camInfo: Arrays.asList(getCampaignsResponse.getCampaigns().getCampaignInfo())){
								CampaignRP camRP = new CampaignRP();
								
								camRP.setAdServer("SIZMEK");
								camRP.setCamName(camInfo.getName());
								camRP.setAdvertiserId(camInfo.getAdvertiserID());
								camRP.setBegindate(getDate(camInfo.getCampaignExtendedInfo().getStartDate()));
								camRP.setEnddate(getDate(camInfo.getCampaignExtendedInfo().getEndDate()));
								camRP.setCamId(camInfo.getID());
								camRP.setDescription(camInfo.getSpecialInstructions());
								camRP.setHasModifToRetrive(false);
								
								result.add(camRP);
							}
						} 
					}	
				} catch (RemoteException e) {
					logger.error("getRecentCampagnesList error--apiToken: " + apiToken, e);
					return result;
				}
			}while(++i*PAGE_SIZE<max);
			return result;
		} catch (AxisFault e) {
			logger.error("getRecentCampagnesList error--apiToken: " + apiToken, e);
		}
		return null;
	}
	
	public AdvertiserInfo getSizmekAdvertiserById(int advId, String apiToken){
		com.eyeblaster.api.advertiser.message.UserSecurityToken advertiserSecurityToken=new com.eyeblaster.api.advertiser.message.UserSecurityToken();
		if(apiToken!=null){
			advertiserSecurityToken.setUserSecurityToken(apiToken);
			GetAdvertisersRequest getAdvertisersRequest = new GetAdvertisersRequest();
			
			ArrayOfAdvertiserServiceFilter arrayOfAdvertiserServiceFilter = new ArrayOfAdvertiserServiceFilter();
			AdvertiserIDFilter advertiserIDFilter = new AdvertiserIDFilter();
			advertiserIDFilter.setAdvertiserID(advId);
			arrayOfAdvertiserServiceFilter.addAdvertiserServiceFilter(advertiserIDFilter);
			getAdvertisersRequest.setAdvertisersFilter(arrayOfAdvertiserServiceFilter);
			com.eyeblaster.api.advertiser.message.ListPaging paging = new com.eyeblaster.api.advertiser.message.ListPaging();
			paging.setPageIndex(0);
			paging.setPageSize(1);
			getAdvertisersRequest.setPaging(paging);
			
			try {
				AdvertiserServiceStub advertiserService = new AdvertiserServiceStub();
				advertiserService._getServiceClient().getOptions().setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, "false");
				
				GetAdvertisersResponse advertisersResponse = advertiserService.getAdvertisers(getAdvertisersRequest, advertiserSecurityToken);
				if(advertisersResponse!=null && advertisersResponse.getAdvertisers()!=null)
					return advertisersResponse.getAdvertisers().getAdvertiserInfo()[0];
				else
					return null;
				
			} catch (AxisFault e) {
				logger.error("getSizmekAdvertiserById error--advId: " + advId, e);
			} catch (RemoteException e) {
				logger.error("getSizmekAdvertiserById error--advId: " + advId, e);
			}
		}
		return null;
	}
	
	
	public Site getSite(Site site, String apiToken){
		for(int i = 0; i < site.getRedirectList().size(); i++){
			site.getRedirectList().get(i).setRedTagList(getAllTagsBylacementId((int)site.getRedirectList().get(i).getPlacementId(), apiToken));
		}
		return site;
	}

	public List<Redirect> getPlacementsByCampaignIdSiteId(int camId, int siteId, String apiToken){
		
		com.eyeblaster.api.placement.message.UserSecurityToken securityToken= new com.eyeblaster.api.placement.message.UserSecurityToken();
		
		com.eyeblaster.api.placement.message.UserSecurityToken placementSecurityToken=new com.eyeblaster.api.placement.message.UserSecurityToken();
		
		if(apiToken!=null){
			
			List<Redirect> result = new ArrayList<Redirect>();
			
			securityToken.setUserSecurityToken(apiToken);
			placementSecurityToken.setUserSecurityToken(apiToken);
			GetPlacementsRequest getPlacementsRequest = new GetPlacementsRequest();
			
			ArrayOfPlacementServiceFilter arrayOfPlacementServiceFilter = new ArrayOfPlacementServiceFilter();
			PlacementCampaignIDFilter placementCampaignIDFilter = new PlacementCampaignIDFilter();
			placementCampaignIDFilter.setCampaignID(camId);
			arrayOfPlacementServiceFilter.addPlacementServiceFilter(placementCampaignIDFilter);
			
			PlacementSiteIDFilter placementSiteIDFilter = new PlacementSiteIDFilter();
			placementSiteIDFilter.setSiteID(siteId);
			arrayOfPlacementServiceFilter.addPlacementServiceFilter(placementSiteIDFilter);
			
			getPlacementsRequest.setPlacementsFilter(arrayOfPlacementServiceFilter);
			
			com.eyeblaster.api.placement.message.ListPaging paging = new com.eyeblaster.api.placement.message.ListPaging();
			paging.setPageSize(PAGE_SIZE);
			
			
			PlacementServiceStub placementService;
			try {
				placementService = new PlacementServiceStub();
				placementService._getServiceClient().getOptions().setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, "false");
				int max=PAGE_SIZE;
				int i=0;
				
				do{
					paging.setPageIndex(i);
					getPlacementsRequest.setPaging(paging);
					getPlacementsRequest.setShowPlacementExtendedInfo(true);
					
					GetPlacementsResponse getPlacementsResponse = placementService.getPlacements(getPlacementsRequest, placementSecurityToken);
										
					if(getPlacementsResponse!=null&&getPlacementsResponse.getTotalCount()>0){
						if(getPlacementsResponse.getTotalCount()>=max)
							max+=PAGE_SIZE;
						
						for(PlacementInfo placementInfo : getPlacementsResponse.getPlacements().getPlacementInfo()){
							
							Redirect redirect = new Redirect();
							redirect.setAdServer(2);
							redirect.setCamId(camId);
							redirect.setPlacementName(placementInfo.getPlacementName());
							redirect.setPlacementId(placementInfo.getID());
							redirect.setSiteId(siteId);
							redirect.setStatus(sizmekStatusTransfor(placementInfo.getStatus().getValue()));
							redirect.setDate_begin(getDateTime(placementInfo.getPlacementExtendedInfo().getStartDate()));
							redirect.setDate_end(getDateTime(placementInfo.getPlacementExtendedInfo().getEndDate()));
							
							if(placementInfo.getPlacementExtendedInfo().getBannerSizeName() != null){		
								redirect.setPlacementSize(placementInfo.getPlacementExtendedInfo().getBannerSizeName().replace(" ", ""));
							}
							
							if(tmDao.isPlacementValid((int)redirect.getPlacementId(), 2) != null){
								redirect.setStatus(tmDao.isPlacementValid((int)redirect.getPlacementId(), 2));
							}
							
							result.add(redirect);
						}
					}
					
				}while(++i*PAGE_SIZE<max);
				
				return result;
				
			} catch (AxisFault e) {
				logger.error("getPlacementsByCampaignIdSiteId error--siteId: " + siteId, e);
				return result;
			} catch (RemoteException e) {
				logger.error("getPlacementsByCampaignIdSiteId error--siteId: " + siteId, e);
				return result;
			}
		}
		return null;
	}
	
	public String getUrl(){
		
		return sizmekRB.getString("sm.url");
	}

	
	public List<RedirectTag> getAllTagsBylacementId(int placementId, String apiToken){
		List<RedirectTag> tagsSM = new ArrayList<RedirectTag>();
		com.eyeblaster.api.placement.message.UserSecurityToken placementSecurityToken=new com.eyeblaster.api.placement.message.UserSecurityToken();
		if(apiToken!=null){
			List<PlacementTagTypes> tagTypeList=new ArrayList<>(Arrays.asList(PlacementTagTypes.CreateIFrame,PlacementTagTypes.IFrame,PlacementTagTypes.Script));
			placementSecurityToken.setUserSecurityToken(apiToken);
			GetPlacementTagsRequest getPlacementTagsRequest = new GetPlacementTagsRequest();
			ArrayOfPlacementTagsGenerationRequestInfo arrayOfPlacementTagsGenerationRequestInfo = new ArrayOfPlacementTagsGenerationRequestInfo();
			
			for(PlacementTagTypes placementTagTypes:tagTypeList){
				PlacementTagsGenerationRequestInfo placementTagsGenerationRequestInfo = new PlacementTagsGenerationRequestInfo();
				placementTagsGenerationRequestInfo.setPlacementID(placementId);		
				PlacementTagSettingsInfo placementTagSettingsInfo=new PlacementTagSettingsInfo();
				placementTagSettingsInfo.setTagType(placementTagTypes);
				placementTagsGenerationRequestInfo.setPlacementTagSetting(placementTagSettingsInfo);
				arrayOfPlacementTagsGenerationRequestInfo.addPlacementTagsGenerationRequestInfo(placementTagsGenerationRequestInfo);
			}
			
			getPlacementTagsRequest.setPlacementTagsGenerationRequestInfo(arrayOfPlacementTagsGenerationRequestInfo);
			
			com.eyeblaster.api.placement.message.ListPaging paging = new com.eyeblaster.api.placement.message.ListPaging();
			
			// normally we could get all the tags in a single page with the size of 50==> to test
			paging.setPageIndex(0);
			paging.setPageSize(PAGE_SIZE);
			
			getPlacementTagsRequest.setPaging(paging);

			GetPlacementTagSettingsRequest getPlacementTagSettingsRequest  = new GetPlacementTagSettingsRequest ();
			getPlacementTagSettingsRequest.setPlacementID(placementId);
			
			try {
				PlacementServiceStub placementService = new PlacementServiceStub();
				placementService._getServiceClient().getOptions().setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, "false");
				
				PlacementTagsGenerationResponseInfo[] tags = placementService.getPlacementTags(getPlacementTagsRequest, placementSecurityToken).getPlacementTagsGenerationInfo().getPlacementTagsGenerationResponseInfo();
				    
				int i=1;
				for(PlacementTagsGenerationResponseInfo tag: tags){
					RedirectTag redTag = new RedirectTag();
					redTag.setAdServer(2);
					redTag.setPlacementId(placementId);
					redTag.setTagValue(tag.getGeneratedTag());
					redTag.setTagType((i%3==1?"CREATEIFRAME":((i%3==2)?"IFRAME":"SCRIPT")));
					i++;
					tagsSM.add(redTag);
				}
			} catch (AxisFault e) {
				logger.error("getAllTagsBylacementId error--placementId: " + placementId, e);
			} catch (RemoteException e) {
				logger.error("getAllTagsBylacementId error--placementId: " + placementId, e);
			}
			return tagsSM;
		}else{
			return null;
		}
	}
	
	
	public RedirectTag getTagByType(int placementId, String tagType, String token){
		com.eyeblaster.api.placement.message.UserSecurityToken placementSecurityToken=new com.eyeblaster.api.placement.message.UserSecurityToken();
		if(token!=null){
			placementSecurityToken.setUserSecurityToken(token);
			PlacementServiceStub placementService;
			GetPlacementTagRequest getPlacementTagRequest = new GetPlacementTagRequest();
			PlacementTagSettingsInfo  placementTagSettingsInfo  = new PlacementTagSettingsInfo ();
			if(tagType.toUpperCase().equals("IFRAME"))
				placementTagSettingsInfo.setTagType(PlacementTagTypes.IFrame);
			
			if(tagType.toUpperCase().equals("SCRIPT"))
				placementTagSettingsInfo.setTagType(PlacementTagTypes.Script);
			
			if(tagType.toUpperCase().equals("CREATEIFRAME"))
				placementTagSettingsInfo.setTagType(PlacementTagTypes.CreateIFrame);
			
			getPlacementTagRequest.setPlacementID(placementId);
			getPlacementTagRequest.setPlacementTagSettings(placementTagSettingsInfo);
			
			try {
				placementService = new PlacementServiceStub();
				placementService._getServiceClient().getOptions().setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, "false");
				String tagValue=placementService.getPlacementTag(getPlacementTagRequest, placementSecurityToken).getGeneratedTag();
				RedirectTag redirectTag = new RedirectTag();
				redirectTag.setPlacementId(placementId);
				redirectTag.setAdServer(2);
				redirectTag.setTagType(tagType);
				redirectTag.setTagValue(tagValue);
				
				return redirectTag;

			} catch (AxisFault e) {
				logger.error("getTagByType error--placementId: " + placementId, e);
			} catch (RemoteException e) {
				logger.error("getTagByType error--placementId: " + placementId, e);
			}
		}
	
		return null;
	}
	
	
	public boolean refreshToken(String token){
		
		AuthenticationServiceStub authService;
		
		com.eyeblaster.api.authentication.message.UserSecurityToken securityToken= new com.eyeblaster.api.authentication.message.UserSecurityToken();
		
		try {
			authService = new AuthenticationServiceStub();
			authService._getServiceClient().getOptions().setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, "false");
			
			securityToken.setUserSecurityToken(token);
			
			GetClientInfoRequest getClientInfoRequest = new GetClientInfoRequest();
			
			authService.getClientInfo(getClientInfoRequest, securityToken);
			
			return true;
			
		} catch (AxisFault e) {
			logger.error("refreshToken error--token: " + token, e);
		} catch (RemoteException e) {
			logger.error("refreshToken error--token: " + token, e);
		}
		
		return false;
	}
	
	public String getDate(APIDateTime dateTime){
		String year = String.valueOf(dateTime.getYear());
		String month = "";
		if(dateTime.getMonth() < 10){
			month = "0";
		}
		month = month + String.valueOf(dateTime.getMonth());
		
		String day = "";
		if(dateTime.getDay() < 10){
			day = "0";
		}
		day = day + String.valueOf(dateTime.getDay());
		
		return (year + "-" + month + "-" + day);
	}
	
	public String getDateTime(APIDateTime dateTime){
		
		String year = String.valueOf(dateTime.getYear());
		
		String month = "";
		if(dateTime.getMonth() < 10){
			month = "0";
		}
		month = month + String.valueOf(dateTime.getMonth());
		
		String day = "";
		if(dateTime.getDay() < 10){
			day = "0";
		}
		day = day + String.valueOf(dateTime.getDay());
		
		String hour = "";
		if(dateTime.getHour() < 10){
			hour = "0";
		}
		hour = hour + String.valueOf(dateTime.getHour());
		
		String minute = "";
		if(dateTime.getMinute() < 10){
			hour = "0";
		}
		minute = minute + String.valueOf(dateTime.getMinute());
		
		String second = "";
		if(dateTime.getSecond() < 10){
			hour = "0";
		}
		second = minute + String.valueOf(dateTime.getSecond());
		
		return (year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second);
	}
	
	public String sizmekStatusTransfor(String realStatus){
		
		String status="ARCHIVED";
		
		if(realStatus.equals("HardStopApplied")){
			return status;
		}else{
			return "NEW";
		}
		
		
	}
	
	
}
