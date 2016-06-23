package rest.controllers;

import models.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import rest.services.LogService;

import java.util.List;

@RestController
@RequestMapping("/log")
public class LogController {

    LogService logService;

    @Autowired
    public void setLogService(LogService logService) {
        this.logService = logService;
    }

    @RequestMapping(value = "/getlog/{camId}/{adServer}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<Log> getLogByCamId(@PathVariable long camId, @PathVariable int adServer) {

        return logService.getLogByCamId(camId, adServer);
    }

    @RequestMapping(value = "/savelog", method = RequestMethod.POST)
    public
    @ResponseBody
    void saveLog(@RequestBody Log log) {

        logService.saveLog(log);
    }

    @RequestMapping(value = "/savelog/{login}/{status}/{placementId}/{site}", method = RequestMethod.POST, produces =
            "application/json")
    @ResponseBody
    public void saveSingleTagTM(@PathVariable String login, @PathVariable String status,
            @PathVariable String placementId, @PathVariable String site) {

        Log log = new Log();
        log.setModifyByName(login);
        log.setStatus(status);
        log.setPlacementId(Long.valueOf(placementId));
        log.setSite(site);

        logService.saveLog(log);
    }

}
