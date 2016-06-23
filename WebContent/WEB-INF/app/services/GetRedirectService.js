'use strict';
var wsrp = angular.module('wsrp');

wsrp.factory('getTokenTM',['$http','envconf',function($http,envconf){
	
	return {
		getToken:function(){
			return $http.get(envconf.backend+'/tm/getToken/');
		}		
	}
}]);

//Call DCM API
wsrp.factory('getCamsDCMByNetWork',['$http','envconf',function($http,envconf){
	return{
		getCamsDCM:function(token,profileId,netWorkId){
			return $http.get(envconf.backend+'/dcm/getcams/'+token+"/"+profileId+"/"+netWorkId);
		}
	}
}]);

wsrp.factory('getAdvNameDCM',['$http','envconf',function($http,envconf){
	return{
		getAdvName:function(token,profileId,netWorkId,advId){
			return $http.get(envconf.backend+'/dcm/getadvname/'+token+"/"+profileId+"/"+netWorkId+"/"+advId);
		}
	}
}]);

wsrp.factory('getAdvNameDCMV1',['$http','envconf',function($http,envconf){
	return{
		getAdvNameV1:function(token,netWorkId,advId){
			return $http.get(envconf.backend+'/dcm/getadvnamev1/'+token+"/"+netWorkId+"/"+advId);
		}
	}
}]);



wsrp.factory('getCamDCMById',['$http','envconf',function($http,envconf){
	return{
		getCamDCM:function(token,profileId,camId){
			return $http.get(envconf.backend+'/dcm/getcambyid/'+token+"/"+profileId+"/"+camId);
		}
	}
}]);

wsrp.factory('getTMCam',['$http','envconf',function($http,envconf){
	return{
		getCamTM:function(advId){
			return $http.get(envconf.backend+'/tm/getcamtm/'+advId);
		}
	}
}]);

//Call DCM API
wsrp.factory('getSingleTagDCM',['$http','envconf',function($http,envconf){
	return {
		getTagDCM:function(token,camId,placeID,tagType,networkId,caseNB,profileId2){
			return $http.get(envconf.backend+'/dcm/getplacement/'+token+"/"+camId+"/"+placeID+"/"+tagType+"/"+networkId+"/"+caseNB+"/"+profileId2);
		}
	}
}]);


wsrp.factory('getCampaignWithAllRedirectsDCM',['$http','$q','envconf',function($http,$q,envconf){
	
	var getCamAllRedDCM = function(token,camId,networkId,caseNB,profileId2){
		
		var canceller=$q.defer();
		
		var cancel=function(){
			
			canceller.resolve("timeout");
		};
		
		var promise=$http.get(envconf.backend+'/dcm/getcam/'+token+"/"+camId+"/"+networkId+"/"+caseNB+"/"+profileId2,{timeout:canceller.promise}).then(function(response){
			return response;
		});
		
		return{
			promise:promise,
			cancel:cancel
		}
		
	};
	
	return {
		
		getCamAllRedDCM:getCamAllRedDCM
	};
	
}]);


wsrp.factory('getPlNBs',['$http','envconf',function($http,envconf){
	
	return {
		getplnbs:function(token,camId,networkId,caseNB,profileId2){
			return $http.get(envconf.backend+'/dcm/getcamplsnb/'+token+"/"+camId+"/"+networkId+"/"+caseNB+"/"+profileId2);
		}		
	}
}]);


wsrp.factory('getSitePlacement',['$http','envconf',function($http,envconf){
	
	return {
		rechargePlaTags:function(token,camId,networkId,siteId,caseNB,profileId){
			return $http.get(envconf.backend+'/dcm/rechargepla/'+token+"/"+camId+"/"+networkId+"/"+idPlacement+"/"+caseNB+"/"+profileId);
		}		
	}
}]);

wsrp.factory('rechargePlaTags',['$http','envconf',function($http,envconf){
	
	return {
		rechargePlaTags:function(token,camId,networkId,idPlacement,caseNB,profileId){
			return $http.get(envconf.backend+'/dcm/rechargepla/'+token+"/"+camId+"/"+networkId+"/"+idPlacement+"/"+caseNB+"/"+profileId);
		}		
	}
}]);


wsrp.factory('getPlaTagDCM',['$http','envconf',function($http,envconf){
	
	return {
		getPlaTagDCM:function(token,camId,networkId,idPlacement,caseNB,profileId){
			return $http.get(envconf.backend+'/dcm/getPlTagDCM/'+token+"/"+camId+"/"+networkId+"/"+idPlacement+"/"+caseNB+"/"+profileId);
		}		
	}
}]);


wsrp.factory('getSiteDCM',['$http','envconf',function($http,envconf){
	
	return {
		getSiteDCM:function(token,camId,networkId,siteId,caseNB,profileId){
			return $http.get(envconf.backend+'/dcm/getSiteDCM/'+token+"/"+camId+"/"+networkId+"/"+siteId+"/"+caseNB+"/"+profileId);
		}		
	}
}]);


//Call TM API
wsrp.factory('getCampaignWithAllRedirectsTM',['$http','envconf',function($http,envconf){
	
	return {
		getCamAllRed:function(token,camId){
			return $http.get(envconf.backend+'/tm/getcam/'+token+"/"+camId);
		}
	}
}]);

//call TM Service
wsrp.factory('getSingleTagTM',['$http','envconf',function($http,envconf){
	return {
		getTagTM:function(token,placeID,tagType,adServer){
			return $http.get(envconf.backend+'/tm/gettag/'+token+'/'+placeID+'/'+tagType+'/'+adServer);
		}
	}
}]);

//call TM Service
wsrp.factory('getTagsByPlacementTM',['$http',function($http){
	
}]);

//call TM Service
wsrp.factory('getPlacementsTM',[function(){
	
}]);

wsrp.factory('saveSingleTagTM',['$http','envconf',function($http,envconf){
	return {
		saveSingleTag:function(token,tag,placementNew){
			return $http.post(envconf.backend+'/tm/savetag',{"tokenValue":token,"redirectTag":tag,"redirect":placementNew});
		}
	}
}]);

wsrp.factory('savePlacementAllTags',['$http','envconf',function($http,envconf){
	return{
		savePlTags:function(token,placement){
			return $http.post(envconf.backend+'/tm/savepltags',{"tokenValue":token,"redirect":placement});
		}
	}
}]);

wsrp.factory('validPlacement',['$http','envconf',function($http,envconf){
	return{
		validPlacement:function(token,placement){
			return $http.post(envconf.backend+'/tm/validPlacement',{"tokenValue":token,"redirect":placement});
		}
	}
}]);

wsrp.factory('validSite',['$http','envconf',function($http,envconf){
	return{
		validSite:function(token,site){
			return $http.post(envconf.backend+'/tm/validSite',{"tokenTM":token,"site":site});
		}
	}
}]);

wsrp.factory('saveCampaignGInfo',['$http','envconf',function($http,envconf){
	return {
		saveCamInfo:function(token,cam){
			return $http.post(envconf.backend+'/tm/savecam',{"tokenValue":token,"campaignRP":cam});
		}
	}
}]);

wsrp.factory('savePlacementGInfo',['$http','envconf',function($http,envconf){
	return {
		savePlInfo:function(token,plm){
			return $http.post(envconf.backend+'/tm/saveplacement',{"tokenValue":token,"redirect":plm});
		}
	}
}]);


wsrp.factory('saveCampaignByDate',['$http',function($http){
	return {
		saveCamByDate:function(token,cam){
		}
	}
}]);


wsrp.factory('getLogByCampaignId',['$http','envconf',function($http,envconf){

	return {
		getCamAllLog:function(camId,adServer){
			return $http.get(envconf.backend+'/log/getlog/'+camId+"/"+adServer);
		}
	}
}]); 

wsrp.factory('saveLogAtTM',['$http','envconf',function($http,envconf){

	return {
		saveLog:function(camId,login,status,placement,site,tagType,tagValue,adServer){
			var logDCM = {
					"campaignId" : camId,
					"modifyByName" : login,
					"status" : status,
					"placementId" : placement.placementId,
					"placementName" : placement.placementName,
					"site" : site,
					"tagType" : tagType,
					"tagValue": tagValue,
					"adServer":adServer
			};
			var response = $http.post(envconf.backend+'/log/savelog', logDCM);
			response.success(function(data, status, headers, config) {
			});
		}
	}
}]); 

wsrp.factory('getCamDesc',['$http','envconf',function($http,envconf){
	return{
		getCamDesc:function(camId,adServer){
			return $http.get(envconf.backend+'/dcm/getcamdesc/'+camId+"/"+adServer);
		}
	}
}]);

wsrp.factory('getCamRetriveDate',['$http','envconf',function($http,envconf){
	return{
		getCamRetriveDate:function(camId,adServer){
			return $http.get(envconf.backend+'/dcm/getCamRetriveDate/'+camId+"/"+adServer);
		}
	}
}]);

wsrp.factory('saveCamDesc',['$http','envconf',function($http,envconf){
	return{
		saveCamDesc:function(camId,desc,adServer){
			return $http.post(envconf.backend+'/dcm/savecamdesc',{"camId":camId,"desc":desc,"adServer":adServer});
		}
	}
}]);

//Call SIZMEK API
wsrp.factory('getCamsSizmekByToken',['$http','envconf',function($http,envconf){
	return{
		getCamsSizmek:function(tokenSizmek,tokenTM){
			return $http.get(envconf.backend+'/sizmek/getcams/'+tokenSizmek + '/' + tokenTM);
		}
	}
}]);


wsrp.factory('getCamSizmekById',['$http','envconf',function($http,envconf){
	return{
		getCamSizmekById:function(camId,tokenSizmek,tokenTM){
			return $http.get(envconf.backend+'/sizmek/getcambyid/'+camId+'/'+tokenSizmek + '/' + tokenTM);
		}
	}
}]);


wsrp.factory('TokenStorage', function() {
	var storageKey = 'auth_token';
	return {		
		store : function(token) {
			return localStorage.setItem(storageKey, token);
		},
		retrieve : function() {
			return localStorage.getItem(storageKey);
		},
		clear : function() {
			return localStorage.removeItem(storageKey);
		}
	};
});

wsrp.factory('refreshSizmekToken',['$http','envconf',function($http,envconf){
	return{
		refreshToken:function(tokenSizmek,tokenTM){
			return $http.get(envconf.backend+'/sizmek/refreshToken/' + tokenSizmek + '/' + tokenTM);
		}
	}
}]);

wsrp.factory('getSizmekAdvertiserById',['$http','envconf',function($http,envconf){
	return{
		getAdvertiser:function(advId, tokenSizmek,tokenTM){
			return $http.get(envconf.backend+'/sizmek/getAdvertiser/' + advId + '/' + tokenSizmek + '/' + tokenTM);
		}
	}
}]);

wsrp.factory('getSiteRedirectSizmek',['$http','envconf',function($http,envconf){
	return{
		getSiteRedirectSizmek:function(camSizmekId, siteId, tokenSizmek,tokenTM){
			return $http.get(envconf.backend+'/sizmek/getsiteRedirect/' + camSizmekId + '/' + siteId + '/' + tokenSizmek + '/' + tokenTM);
		}
	}
}]);

wsrp.factory('getSizmekCamSites',['$http','envconf',function($http,envconf){
	return{
		getCamSites:function(camSizmekId, tokenSizmek,tokenTM){
			return $http.get(envconf.backend+'/sizmek/getsites/' + camSizmekId + '/' + tokenSizmek + '/' + tokenTM);
		}
	}
}]);

wsrp.factory('rechargeSizmekPlaTags',['$http','envconf',function($http,envconf){
	
	return {
		rechargePlaTags:function(idPlacement,tokenSizmek,tokenTM){
			return $http.get(envconf.backend+'/sizmek/rechargepla/'+idPlacement+"/"+tokenSizmek + '/' + tokenTM);
		}		
	}
}]);

wsrp.factory('getSingleTagSizmek',['$http','envconf',function($http,envconf){
	
	return {
		getTagSizmek:function(idPlacement,tagType,tokenSizmek,tokenTM){
			return $http.get(envconf.backend+'/sizmek/getTagSizmek/'+idPlacement+"/"+tagType+"/"+tokenSizmek + '/' + tokenTM);
		}		
	}
}]);

wsrp.factory('getPlaTagSizmek',['$http','envconf',function($http,envconf){
	
	return {
		getPlaTagSizmek:function(idPlacement,tokenSizmek,tokenTM){
			return $http.get(envconf.backend+'/sizmek/getPlTagSizmek/'+idPlacement+"/"+tokenSizmek + '/' + tokenTM);
		}		
	}
}]);

wsrp.factory('getSiteSizmek',['$http','envconf',function($http,envconf){
	
	return {
		getSiteSizmek:function(site,tokenSizmek,tokenTM){
			return $http.post(envconf.backend+'/sizmek/getSiteSizmek/',{"tokenSizmek":tokenSizmek,"tokenTM":tokenTM,"site":site});
		}		
	}
}]);