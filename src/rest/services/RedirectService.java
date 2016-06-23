package rest.services;

import dao.RedirectTagDao;
import models.Log;
import models.Redirect;
import models.Redirect2;
import models.RedirectTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import utils.TimeUtil;

import java.sql.Connection;
import java.util.concurrent.Future;

@Service
public class RedirectService {

    @Autowired
    LogService logService;
    @Autowired
    RedirectTagDao redirectTagDao;

    @Async
    public Future<Redirect2> setRedirectTag(Redirect2 redirect, int adServer) {

        redirect.setRedTagList(redirectTagDao.getRedTagsByPlacementId2(redirect.getPlacementId(), adServer));

        return new AsyncResult<Redirect2>(redirect);
    }

    @Async
    public Future<Boolean> saveTag(Redirect redirect, RedirectTag tag, Connection connection) {

        boolean tagSaved = true;
        tag.setWellSave(true);//initialize the save status

        if (!tag.getStatus().toUpperCase().equals("DELETE") && tag.isToValid()) {

            if (tag.getTagValue() != null && tag.getTagValue().length() > 2000) {
                tag.setTagValue(tag.getTagValue().substring(0, 1990));
                tag.setTagDescription(
                        "Due to tag length limit, this tag value has been cut partly; " + tag.getTagDescription());
            }

            tag.setStatus(redirect.getStatus());

            try {
                redirectTagDao.saveSingleRedTag2(tag, redirect, connection);
                Log log = new Log(redirect.getCamId(), redirect.getSiteName(), redirect.getPlacementId(),
                        redirect.getPlacementName(), tag.getTagType(), tag.getTagValue(), redirect.getStatus(),
                        redirect.getLogin(), "", redirect.getAdServer());
                logService.saveLog2(log);

                tag.setLastupdatedDate(TimeUtil.currentTextDate("yyyy-MM-dd HH:mm:ss"));
                tag.setStatus(redirect.getStatus());
                tag.setModifyByName(redirect.getLogin());
            } catch (Exception e) {
                e.printStackTrace();
                tag.setWellSave(false);
                tagSaved = false;
            }
        }
        return new AsyncResult<Boolean>(tagSaved);
    }

}
