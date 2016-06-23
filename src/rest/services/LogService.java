package rest.services;

import dao.LogDao;
import models.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jdo.annotations.Transactional;
import java.util.List;

@Service
public class LogService {

    LogDao logDao;

    Log log;

    @Autowired
    public void setLogDao(LogDao logDao) {
        this.logDao = logDao;
    }

    public List<Log> getLogByCamId(long campaignId, int adServer) {

        return logDao.getLogByCamId(campaignId, adServer);
    }

    @Transactional
    public void saveLog(Log log) {

        logDao.saveLog(log);
    }

    @Transactional
    public void saveLog2(Log log) {

        logDao.saveLog2(log);
    }
}
