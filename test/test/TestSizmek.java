import com.eyeblaster.api.v1.datacontracts.AdvertiserInfo;
import models.Site;
import rest.services.SizmekService;

import java.util.List;

public class TestSizmek {

    public static void main(String[] args) {

        SizmekService sizmekService = new SizmekService();

        String personalToken = sizmekService.getPersonalToken("florian.martins", "Clavier17");

        List<Site> sites = sizmekService.getSizmekCampaignById(627394, personalToken).getSiteList();

        //System.out.println(caminfo.getName()+caminfo.getTotalPlacments());

        int siteId = sites.get(0).getSiteId();

        System.out.println(siteId);

        AdvertiserInfo advInfo = sizmekService.getSizmekAdvertiserById(134413, personalToken);

        System.out.println(advInfo.getAdvertiserName());

        AdvertiserInfo advInfo2 = sizmekService.getSizmekAdvertiserById(134413, personalToken);

        System.out.println(advInfo2.getAdvertiserName());

        //List<Redirect> result = sizmekService.getPlacementsByCampaignIdSiteId(627394,siteId, personalToken);

        //16433000
        //List<RedirectTag> result2=sizmekService.getAllTagsBylacementId(1, 16433000, personalToken);

        //		if(result2.size()>0){
        //
        //		for(int i=0; i<result2.size();i++){
        //			System.out.println(result2.get(i).getTagValue());
        //			System.out.println("--------------");
        //		}
        //		}

        System.out.println(sizmekService.getRecentCampagnesList(personalToken).size());

        System.out.println(sizmekService.getTagByType(16433000, "Script", personalToken).getTagValue());

        System.out.println(sizmekService.refreshToken(personalToken));
    }

}
