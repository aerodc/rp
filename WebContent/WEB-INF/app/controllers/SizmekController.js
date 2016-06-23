'use strict';
var wsrp = angular.module('wsrp');
wsrp.controller('sizmekcontroller',['$scope','$location','getTMCam','getCamDesc','getCamRetriveDate','saveCamDesc',
                                      'getCampaignWithAllRedirectsDCM','getSingleTagTM','saveSingleTagTM','getLogByCampaignId','saveLogAtTM',
                                      'getSingleTagDCM','$window','getPlNBs','saveCampaignGInfo','rechargePlaTags','envconf','$interval','$q','$cookieStore','savePlacementGInfo',
                                      'getPlaTagDCM','validPlacement','validSite','getSiteDCM','TokenStorage','getCamsSizmekByToken','getSizmekAdvertiserById','refreshSizmekToken',
                                      'getSiteRedirectSizmek','getSizmekCamSites','rechargeSizmekPlaTags','getTokenTM','getSingleTagSizmek','getPlaTagSizmek','getSiteSizmek','SizmekClientToken','getCamSizmekById',
                                      function($scope,$location,getTMCam,getCamDesc,getCamRetriveDate,saveCamDesc,
                                    		  getCampaignWithAllRedirectsDCM,getSingleTagTM,saveSingleTagTM,getLogByCampaignId,saveLogAtTM,
                                    		  getSingleTagDCM,$window,getPlNBs,saveCampaignGInfo,rechargePlaTags,envconf,$interval,$q,$cookieStore,savePlacementGInfo,
                                    		  getPlaTagDCM,validPlacement,validSite,getSiteDCM,TokenStorage,getCamsSizmekByToken,getSizmekAdvertiserById,refreshSizmekToken,
                                    		  getSiteRedirectSizmek,getSizmekCamSites,rechargeSizmekPlaTags,getTokenTM,getSingleTagSizmek,getPlaTagSizmek,getSiteSizmek,SizmekClientToken,getCamSizmekById){
	
	
	$scope.camSelected={};
	
	$scope.camSelected.hasModifToRetrive=false;
	
	$scope.camSelAdvName;
	
	$scope.camTM;
	
	$scope.flag1=true;

	$scope.isSelectCam=false;
		
	$scope.descbgcolor="white";
	
	$scope.tokenSizmek = null;
	
	$scope.fromTM = true;
	
	var url=$location.absUrl();
	
	$scope.cbCI=true;
	$scope.cbIF=true;
	$scope.cbSC=true;
	 
	initPLbuttons();
	
    $("#selectCamList").select2({
        escapeMarkup: function(m) { return m; }
    });
			
	angular.element(document).ready(function () {
		
		$('#retriveDate').tooltip();
				
		$("#bt1").trigger("click");
		$("#divSites").css({top:'190px'});
		
		$scope.loader=false;
		$scope.showRelax=true;

		if(url.indexOf('adsId')>0){
			var arrayUrl=url.split('?');
			$scope.tokenTM =((arrayUrl[1].split('&'))[0].split('='))[1];
			$scope.campaignId =((arrayUrl[1].split('&'))[1].split('='))[1];
			$scope.login =((arrayUrl[1].split('&'))[6].split('='))[1];
			SizmekClientToken.getClientToken($scope.campaignId).then(function(result){
				if(result != null && result.data != null){
					$scope.tokenSizmek=result.data;
					$('#searchById').val($scope.campaignId);
					$scope.searchCamById();
				}
			});	
		}else{
			
			$scope.fromTM = false;
			var info = JSON.parse(TokenStorage.retrieve());
			
			if(info){
				$scope.login = info.login;		
				$scope.tokenSizmek = info.token;
				
				getTokenTM.getToken().then(function(result){
					
					$scope.tokenTM = result.data.tokenValue;
					
					getCamsSizmekByToken.getCamsSizmek($scope.tokenSizmek,$scope.tokenTM).then(function(result){

						$scope.loader=true;
						$scope.showRelax=false;
						
						console.log(result.data);
						
						if(result.data == null){
							alert("Your session has expired and please reconnect.");
							$window.location.href=envconf.backend+"/#";
						} else {
							$scope.camSelected=null;
							$scope.camList=result.data;
							$scope.camListCopy=result.data;
							
							if(!$scope.camList){
								alert("There might be an error by calling Sizmek API and please reconnect.");
								$window.location.href=envconf.backend+"/#";
							}
						}
						
					},function(resKO){
						alert("Your session has expired and please reconnect.");
						$window.location.href=envconf.backend+"/#";
						$scope.loader=true;
					});
					
				},function(resKO){
				});
				
			}else{
				$window.location.href=envconf.backend+"/#";
			}
		}

		var date = new Date();
		$scope.tokenExpireTime = date.getTime();
		var interval = $interval(function checkTokenExpire(){
			
			var now = new Date();
			
	        if ((now.getTime() - $scope.tokenExpireTime) > 4*60*1000){
	        	
	        	refreshSizmekToken.refreshToken($scope.tokenSizmek,$scope.tokenTM);
	        	$scope.tokenExpireTime = now.getTime();
	        }	
	    }, 1000*30);
	});
	
	$scope.selectCAM=function(){
		
		$scope.isSelectCam=true;
		
		$scope.loader=false;
		
		$scope.flag1=true;
		
		$scope.camSelected = JSON.parse($scope.camSelected);
		
		initPLbuttons();
		
		ga('send', 'pageview');
		
		getTMCam.getCamTM($scope.camSelected.camId).then(function(result){
			
			$scope.camTM = result.data;
		},function(resKO){
		});
		
		getCamDesc.getCamDesc($scope.camSelected.camId,2).then(function(result){
			$scope.camDesc=result.data;
		});
				
		$scope.camSizmekId=$scope.camSelected.camId;
		$scope.camSizmekName=$scope.camSelected.camName;
		$scope.advertiserID=$scope.camSelected.advertiserId;
		$scope.camBeginDate=$scope.camSelected.begindate;
		$scope.camEndDate=$scope.camSelected.enddate;
		$scope.siteList=$scope.camSelected.siteList;
		
		getRetriveDate($scope.camSizmekId);
		
		var getRetriveDateVar = setInterval(
				function(){ 
					getRetriveDate($scope.camSizmekId) 
				}, 30000);

		getSizmekAdvertiserById.getAdvertiser($scope.camSelected.advertiserId,$scope.tokenSizmek,$scope.tokenTM).then(function(result){
			$scope.camSelAdvName = result.data.advertiserName;	
		},function(resKO){
		});
		
		getSitesByCamId();
		
		getLogByCampaign($scope.camSizmekId);
	};
	
	function getSitesByCamId(){
		getSizmekCamSites.getCamSites($scope.camSizmekId, $scope.tokenSizmek,$scope.tokenTM).then(function(result){
			$scope.siteList = result.data;
			console.log($scope.siteList);
			$scope.loader=true;
		},function(resKO){
			$scope.loader=true;
		});
	}
		
	$scope.searchCamById=function(){
		
		var progress2=0;
		var progress1=0;
		
		$('.progress-bar').css('width', 0+'%').attr('aria-valuenow', 0);  		
		
		ga('send', 'pageview');
		
		var camId=$('#searchById').val().trim();
										
		$scope.siteList=null;
		
		$scope.isSelectCam=true;
		
		$scope.loader=false;
				
		initPLbuttons();
		
		getCamSizmekById.getCamSizmekById(camId,$scope.tokenSizmek,$scope.tokenTM).then(function(resOK){
			$scope.flag1=false;				
			
			$scope.camSelected=resOK.data;
			
			$scope.camSizmekName=$scope.camSelected.camName;
			$scope.advertiserID=$scope.camSelected.advertiserId;
			$scope.camBeginDate=$scope.camSelected.begindate;
			$scope.camEndDate=$scope.camSelected.enddate;
			
			getSizmekAdvertiserById.getAdvertiser($scope.camSelected.advertiserId,$scope.tokenSizmek,$scope.tokenTM).then(function(result){
				$scope.camSelAdvName = result.data.advertiserName;	
			},function(resKO){
			});
				
			getRetriveDate(camId);				
			var getRetriveDateVar = setInterval(function(){ getRetriveDate(camId) }, 30000);	
			
		},function(resKO){
			console.log("resKO");
			alert("Campaign with the ID "+ camId+" can't be found");
		});
				
		getTMCam.getCamTM(camId).then(function(result){
			
			$scope.camTM = result.data;
		},function(resKO){
		});
		
		getCamDesc.getCamDesc(camId,2).then(function(result){
			$scope.camDesc=result.data;
		});
				
		$scope.camSizmekId=camId;
		
		getRetriveDate($scope.camSizmekId);
		
		var getRetriveDateVar = setInterval(
				function(){ 
					getRetriveDate($scope.camSizmekId) 
				}, 30000);
		
		getSitesByCamId();
		
		getLogByCampaign($scope.camSizmekId);
	};
	
	
	$scope.saveCamDesc=function(){
		var desc= $('#descId').val();
		var camId=$('#searchById').val();
		saveCamDesc.saveCamDesc(camId,desc,2);
		$scope.camDesc=desc;
		ga('send', 'event', 'button', 'click', 'save description');	
	}
	
	$scope.deleteTag=function(placement,tagType){
		
		var confirmed = confirm("Do you really want to delete this Tag?");
	    if(confirmed){
	    	ga('send', 'event', 'button', 'click', 'Delete Tag:pId'+placement.placementId+' '+tagType);		
		for(var i=0;i<$scope.siteList.length;i++){
			if($scope.siteList[i].siteCharged == true){
				for(var j=0;j<$scope.siteList[i].redirectList.length;j++){
					if(($scope.siteList[i].redirectList)[j].placementId===placement.placementId){
					
						for(var k=0;k<($scope.siteList[i].redirectList)[j].redTagList.length;k++){
							if((($scope.siteList[i].redirectList)[j].redTagList)[k].tagType===tagType){
								(($scope.siteList[i].redirectList)[j].redTagList)[k].status="DELETE";
								((($scope.siteList)[i].redirectList)[j].redTagList)[k]["bgcolor"]="#FFCCCC";
								var tagV=$('#'+placement.placementId+tagType).val();
								var tagD=$('#desc_'+placement.placementId+tagType).val();
								var tag={
										"camId":$scope.camSizmekId,
										"placementId":placement.placementId,
										"tagType":tagType,
										"tagValue":tagV,
										"status":"DELETE",
										"tagDescription":tagD,
										"adServer":2
									};
								var placementNew={
										"camId":$scope.camSizmekId,
										"placementId":placement.placementId,
										"placementName":placement.placementName,
										"placementSize":placement.placementSize,
										"siteName":placement.siteName,
										"siteId":placement.siteId,
										"date_begin":placement.date_begin,
										"date_end":placement.date_end,
										"adServer":2
									};
								saveSingleTagTM.saveSingleTag($scope.tokenTM,tag,placementNew);
								saveLog($scope.camSizmekId,$scope.login,'DELETE',placement,placement.siteName,tagType,tagV);
								setTagMessageByValid(placement.placementId,tagType,$scope.login,'DELETE');
							}
						}
						
						_.remove(($scope.siteList[i].redirectList)[j].redTagList,{
							"tagType":tagType
						});		
				}
		    }
		  }
	     }
	  }		
	}
	
	$scope.resetTag=function(placementId,tagType,recharge){

		var confirmed = true;
		
		if(recharge == true){
			confirmed = confirm("Recharge the Tag?");
		}
		if(confirmed){
			ga('send', 'event', 'button', 'click', 'Recharge Tag:pID'+placementId+' '+tagType);
			$scope.loader=false;
		            //call sizmek service to recharge tag
					getSingleTagSizmek.getTagSizmek(placementId,tagType,$scope.tokenSizmek,$scope.tokenTM).then(
						function(result){
							if(isRealValue(result.data)){

								replaceTagValue(placementId,tagType,result.data.tagValue,result.data.tagDescription,$scope.login,dateFormat());
								
								$("#"+placementId+tagType).val(result.data.tagValue);
								$("#desc_"+placementId+tagType).val(result.data.tagDescription);
								$scope.loader=true;
							}
						}
					);				
			    }							
		}

	
	
	$scope.resetSite=function(site){
		
		$scope.loader=false;
		
		var confirmed = confirm("This will take a while, do you really want to recharge the site?");
		
		if(confirmed){
			
			if(!isRealValue(site.siteCharged) || site.siteCharged==false) {
				$scope.getSiteRedirect(site.siteId);
			}
			
			var index=_.findIndex($scope.siteList,function(chr){
				return chr.siteId==site.siteId;
			});
			
			var interval = $interval(function siteCharged(){
				
		        if (!isRealValue(site.siteCharged) || site.siteCharged==false){		        		
		        } else {
		        	
	        		$scope.loader=false;
		        	$interval.cancel(interval);
		        	getSiteSizmek.getSiteSizmek(site,$scope.tokenSizmek,$scope.tokenTM).then(function(result){
		        		if(isRealValue(result.data)){
		        			
		        			site = result.data;

		        			for(var j=0;j<site.redirectList.length;j++){
		        				
		        				site.redirectList[j]['show2']=true;
		        				site.redirectList[j]["charged"]=true;
		    					if(site.redirectList[j].status==="ARCHIVED"){
		    						site.redirectList[j]["bgcolor"]="#FFCCCC";
		    					}else{
		    						if(site.redirectList[j].status==='VALID'){
		    							site.redirectList[j]["bgcolor"]="#CCFFD5";
		    						}else{
		    							if(site.redirectList[j].status==='OOS'){
		    								site.redirectList[j]["bgcolor"]="#ff6e00";
		    							}else{
		    								site.redirectList[j].status='NEW';
		    								site.redirectList[j]["bgcolor"]="#FFF4B2";
		    							}
		    						}
		    					}
		        				
		        				for(var i=0;i<site.redirectList[j].redTagList.length;i++){
		        					
		        					site.redirectList[j].redTagList[i].show=true;
		        					$("#"+site.redirectList[j].placementId+site.redirectList[j].redTagList[i].tagType).val(site.redirectList[j].redTagList[i].tagValue);
		        					$("#desc_"+site.redirectList[j].placementId+site.redirectList[j].redTagList[i].tagType).val(site.redirectList[j].redTagList[i].tagDescription);
		        					
		        					if(!!site.redirectList[j].redTagList[i].status && site.redirectList[j].redTagList[i].status.toUpperCase().indexOf("VALI")>-1){
										(site.redirectList[j].redTagList)[i]["bgcolor"]="#CCFFD5";	
										(site.redirectList[j].redTagList)[i]["message"]="VALID"+" by "+(site.redirectList[j].redTagList)[i].modifyByName+" at "+(site.redirectList[j].redTagList)[i].lastupdatedDate;

									}else{
										if(!!site.redirectList[j].redTagList[i].status && site.redirectList[j].redTagList[i].status.toUpperCase().indexOf("DELETE")>-1){
											(site.redirectList[j].redTagList)[i]["bgcolor"]="#FFCCCC";
											(site.redirectList[j].redTagList)[i]["message"]="DELETE"+" by "+(site.redirectList[j].redTagList)[i].modifyByName+" at "+(site.redirectList[j].redTagList)[i].lastupdatedDate;
										}else{
											if(!!site.redirectList[j].redTagList[i].status && site.redirectList[j].redTagList[i].status.toUpperCase().indexOf("OOS")>-1){
												(site.redirectList[j].redTagList)[i]["bgcolor"]="#ff6e00";	
												(site.redirectList[j].redTagList)[i]["message"]="SET OUT OF SCOPE"+" by "+(site.redirectList[j].redTagList)[i].modifyByName+" at "+(site.redirectList[j].redTagList)[i].lastupdatedDate;

											}else{
												(site.redirectList[j].redTagList)[i]["bgcolor"]="#FFF4B2";
												(site.redirectList[j].redTagList)[i]["message"]="waiting for validation";
											}
										}
									}
		        				}
								
		        				site.redirectList[j].charged=true;	
		        			  }
		        			
		        			$scope.siteList[index]=site;
		        			$scope.siteList[index].siteCharged=true;
							}
	        				console.log($scope.siteList);
							$scope.loader=true;
					});
		        }		
		    }, 500);
		}
	}

	
	$scope.getSiteRedirect=function(siteId){
		
		$scope.loader=false;
		
		var index=_.findIndex($scope.siteList,function(chr){
			return chr.siteId==siteId;
		});
			
		getSiteRedirectSizmek.getSiteRedirectSizmek($scope.camSizmekId,siteId,$scope.tokenSizmek,$scope.tokenTM).then(function(result){
			if(isRealValue(result.data)){
				
				$scope.siteList[index].redirectList = result.data;	
				$scope.siteList[index]['siteCharged'] = true;
				
				for(var i=0;i<(($scope.siteList)[index].redirectList).length;i++){

					((($scope.siteList)[index].redirectList)[i]).siteName=$scope.siteList[index].siteName;
					((($scope.siteList)[index].redirectList)[i])["show2"]=true;
					((($scope.siteList)[index].redirectList)[i])["charged"]=false;
					if((($scope.siteList)[index].redirectList)[i].status==="ARCHIVED"){
						(($scope.siteList)[index].redirectList)[i]["bgcolor"]="#FFCCCC";
					}else{
						if((($scope.siteList)[index].redirectList)[i].status==='VALID'){
							$scope.siteList[index].redirectList[i]["bgcolor"]="#CCFFD5";
						}else{
							if((($scope.siteList)[index].redirectList)[i].status==='OOS'){
								$scope.siteList[index].redirectList[i]["bgcolor"]="#ff6e00";
							}else{
								(($scope.siteList)[index].redirectList)[i].status='NEW';
								$scope.siteList[index].redirectList[i]["bgcolor"]="#FFF4B2";
							}
						}
					}
					
					((($scope.siteList)[index].redirectList)[i]).redTagList= new Array(9);
					for(var j=0;j<9;j++){
						
						(((($scope.siteList)[index].redirectList)[i]).redTagList)[j]={
								tagType:"",
								tagValue:"",
								tagDescription:"",
								modifyByName:"",
								lastupdatedDate:"",
								status:"",
								bgcolor:"",
								show:false
						};
					}
				}

				 if(!$scope.nwPlButton && $scope.oosPlButton && $scope.archPlButton && $scope.valPlButton && $scope.allPlButton){
					 $scope.filterPlacemetsByStatus('NEW');
				 }
				 else if($scope.nwPlButton && !$scope.oosPlButton && $scope.archPlButton && $scope.valPlButton && $scope.allPlButton){
					 $scope.filterPlacemetsByStatus('OOS');
				 }
				 else if($scope.nwPlButton && $scope.oosPlButton && !$scope.archPlButton && $scope.valPlButton && $scope.allPlButton){
					 $scope.filterPlacemetsByStatus('ARCHIVED');
				 }
				 else if($scope.nwPlButton && $scope.oosPlButton && $scope.archPlButton && !$scope.valPlButton && $scope.allPlButton){
					 $scope.filterPlacemetsByStatus('VALID');
				 }
				 else if($scope.nwPlButton && $scope.oosPlButton && $scope.archPlButton && $scope.valPlButton && !$scope.allPlButton){
					 $scope.filterPlacemetsByStatus('ALL');
				 }
				
				$scope.loader=true;
			}
		}
	  );	
	}
	
	
	$scope.resetPlacement=function(placement, isConfirmed){
			
		if(!isConfirmed)
			var confirmed = confirm("Recharge the placement?");
		else
			var confirmed=true;
		if(confirmed){
			
			 ga('send', 'event', 'button', 'click', 'Recharge Placement:pId'+placement.placementId);
			 
			$scope.loader=false;
			
			if(!isRealValue(placement.charged) || placement.charged==false) {
				var promise = $scope.loadPlacementTags(placement, "loadTags");
				promise.then(function() {
					$scope.loader=false;
					placement.charged=true;
					if(placement.redTagList.length>0){
						
			            //call sizmek service to recharge placement
						getPlaTagSizmek.getPlaTagSizmek(placement.placementId,$scope.tokenSizmek,$scope.tokenTM).then(
							function(result){
								if(isRealValue(result.data)){

									for(var i=0;i<placement.redTagList.length;i++){
										for(var j=0;j<result.data.length;j++){
											if(placement.redTagList[i].tagType == result.data[j].tagType){
												$("#"+placement.placementId+placement.redTagList[i].tagType).val(result.data[j].tagValue);
												$("#desc_"+placement.placementId+placement.redTagList[i].tagType).val(result.data[j].tagDescription);
											}
										}							
									}
									$scope.loader=true;
								}
							}
						);			
					}
					
				}, function(noData) {
				});
			} else {

				if(placement.redTagList.length>0){
					
		            //call sizmek service to recharge placement
					getPlaTagSizmek.getPlaTagSizmek(placement.placementId,$scope.tokenSizmek,$scope.tokenTM).then(
						function(result){
							if(isRealValue(result.data)){

								for(var i=0;i<placement.redTagList.length;i++){
									for(var j=0;j<result.data.length;j++){
										if(placement.redTagList[i].tagType == result.data[j].tagType){
											$("#"+placement.placementId+placement.redTagList[i].tagType).val(result.data[j].tagValue);
											$("#desc_"+placement.placementId+placement.redTagList[i].tagType).val(result.data[j].tagDescription);
										}
									}							
								}
								$scope.loader=true;
							}
						}
					);			
				}
			}
		}
	}
	
	$scope.searchLog=function(searchPlacement){	
		
		ga('send', 'event', 'button', 'click', 'search log');	
		
		if(isRealValue(searchPlacement)){
        var searchList = [];
		for(var k=0;k<$scope.logListOrigin.length;k++){
			    
			   if(($scope.logListOrigin[k].placementId.toString().indexOf(searchPlacement)>-1)
					|| (($scope.logListOrigin[k].placementName.toUpperCase().indexOf(searchPlacement.toUpperCase())>-1))){	
					searchList[searchList.length] = $scope.logListOrigin[k];
				}
		}
		if(searchList.length > 0){
			$scope.logList = searchList;
		} else {
			alert("No log founded.");
		}

		}
		else {
			$scope.logList = $scope.logListOrigin;
		}
		
	}
	
	$scope.synRetriveDate=function(){
		getRetriveDate($scope.camSelected.camId);
	}
	
	$scope.filterPla=function(siteId,filterPlacement){
		
		ga('send', 'event', 'button', 'click', 'filter placement');	
		
		var fiterStatus = getFilterStatus();
		console.log(fiterStatus);
		var index=_.findIndex($scope.siteList,function(chr){
			return chr.siteId==siteId;
		});
		
		if(($scope.siteList)[index].redirectList&&($scope.siteList)[index].redirectList.length){
			
			if(typeof filterPlacement!="undefined"&&filterPlacement!=""&&filterPlacement!=" "){
				
				filterPlacement=filterPlacement.trim();
				for(var i=0;i<($scope.siteList)[index].redirectList.length;i++){
					var tmp= (($scope.siteList)[index].redirectList)[i].placementId+(($scope.siteList)[index].redirectList)[i].placementName;
					if(tmp.toUpperCase().indexOf(filterPlacement.toUpperCase())==-1 || (fiterStatus != 'ALL' && (($scope.siteList)[index].redirectList)[i].status != fiterStatus)){
						(($scope.siteList)[index].redirectList)[i]["show2"]=false;
					} else {
						(($scope.siteList)[index].redirectList)[i]["show2"]=true;
					}
				}
			}else{
				for(var i=0;i<($scope.siteList)[index].redirectList.length;i++){
					if(fiterStatus != 'ALL' && (($scope.siteList)[index].redirectList)[i].status != fiterStatus){
						(($scope.siteList)[index].redirectList)[i]["show2"]=false;
					} else {
						(($scope.siteList)[index].redirectList)[i]["show2"]=true;
					}
				}
			}
		}	
	}
	
	function getFilterStatus(){
		 if($scope.nwPlButton==false && $scope.oosPlButton==true && $scope.archPlButton==true && $scope.valPlButton==true){
			 return 'NEW';
		 }
		 if($scope.nwPlButton==false && $scope.oosPlButton==false && $scope.archPlButton==true && $scope.valPlButton==true){
			 return 'OOS';
		 }
		 if($scope.nwPlButton==false && $scope.oosPlButton==true && $scope.archPlButton==false && $scope.valPlButton==true){
			 return 'ARCHIVED';
		 }
		 if($scope.nwPlButton==false && $scope.oosPlButton==true && $scope.archPlButton==true && $scope.valPlButton==false){
			 return 'VALID';
		 }
		 return 'ALL';
	}
	
	$scope.changePlus=function(type,elementId,placement){
		
		if ($('#plus_'+elementId).find('span').hasClass('glyphicon-plus')) {
			$('#plus_'+elementId).find('span').removeClass('glyphicon-plus').addClass("glyphicon-minus");
		
			var isSiteClick = false;
			
			if(type==='SITE'){
				isSiteClick = true;
			}
			
			if(elementId === 'click'){
				isSiteClick = true;
			}
			
			if(isSiteClick === true){
			
				for(var k=0;k<$scope.siteList.length;k++){
					if($scope.siteList[k].siteId !== elementId){
						
						if ($('#plus_'+$scope.siteList[k].siteId).find('span').hasClass(
						'glyphicon-minus')){
							$('#plus_'+$scope.siteList[k].siteId).find('span').removeClass(
							'glyphicon-minus').addClass(
							"glyphicon-plus");
						}
					}
				}
				
				var index=_.findIndex($scope.siteList,function(chr){
					return chr.siteId==elementId;
				});
				if(!isRealValue($scope.siteList[index].siteCharged) || $scope.siteList[index].siteCharged != true){
					$scope.getSiteRedirect(elementId);
				}
				
			}else{
				if(type==='PLAC'){
					if(!placement.charged)
						$scope.loadPlacementTags(placement, 'loadTags');
				} else if(type==='log'){
					if(isRealValue($scope.siteList) && $scope.siteList.length > 0){
						for(var i=0;i<$scope.siteList.length;i++){
							$('#plus_'+$scope.siteList[i].siteId).find('span').removeClass('glyphicon-minus').addClass("glyphicon-plus");
						}
					}
				}
			}
		} else {
			if ($('#plus_'+elementId).find('span').hasClass('glyphicon-minus')) {
				$('#plus_'+elementId).find('span').removeClass('glyphicon-minus').addClass("glyphicon-plus");
			}
		}
	
	}
	
	
	$scope.loadPlacementTags=function(placement,type){		
	   return $q(function(resolve, reject) {
		   
			$scope.loader=false;	
			rechargeSizmekPlaTags.rechargePlaTags(placement.placementId,$scope.tokenSizmek,$scope.tokenTM).then(
					function(result){
						if(isRealValue(result.data)){
						
							var indexSite=_.findIndex($scope.siteList,function(chr){
								return chr.siteId==placement.siteId;
							});
															
							var indexPla=_.findIndex($scope.siteList[indexSite].redirectList,function(chr){
								return chr.placementId==placement.placementId;
							});
							
							var redTagList = result.data;
							
							for(var i=0;i<redTagList.length;i++){
						
								var tagTypetmp=(placement.redTagList)[i].tagType=redTagList[i].tagType;								
								
								(placement.redTagList)[i].tagValue=redTagList[i].tagValue;								
								
								$('#'+placement.placementId+tagTypetmp).val((placement.redTagList)[i].tagValue);
								
								(placement.redTagList)[i].tagDescription=redTagList[i].tagDescription;
								
								$('#desc_'+placement.placementId+tagTypetmp).val((placement.redTagList)[i].tagDescription);	
								
								(placement.redTagList)[i].modifyByName=redTagList[i].modifyByName;
								(placement.redTagList)[i].lastupdatedDate=redTagList[i].lastupdatedDate;
								(placement.redTagList)[i].status=((!!redTagList[i].status)?redTagList[i].status.toUpperCase():"");
								(placement.redTagList)[i].show=true;
								
								if(!!redTagList[i].status&&redTagList[i].status.toUpperCase().indexOf("VALI")>-1){
									(placement.redTagList)[i]["bgcolor"]="#CCFFD5";	
									(placement.redTagList)[i]["message"]="VALID"+" by "+(placement.redTagList)[i].modifyByName+" at "+(placement.redTagList)[i].lastupdatedDate;

								}else{
									if(!!redTagList[i].status&&redTagList[i].status.toUpperCase().indexOf("DELETE")>-1){
										(placement.redTagList)[i]["bgcolor"]="#FFCCCC";
										(placement.redTagList)[i]["message"]="DELETE"+" by "+(placement.redTagList)[i].modifyByName+" at "+(placement.redTagList)[i].lastupdatedDate;
									}else{
										if(!!redTagList[i].status&&redTagList[i].status.toUpperCase().indexOf("OOS")>-1){
											(placement.redTagList)[i]["bgcolor"]="#ff6e00";	
											(placement.redTagList)[i]["message"]="SET OUT OF SCOPE"+" by "+(placement.redTagList)[i].modifyByName+" at "+(placement.redTagList)[i].lastupdatedDate;

										}else{
											(placement.redTagList)[i]["bgcolor"]="#FFF4B2";
											(placement.redTagList)[i]["message"]="waiting for validation";
										}
									}
								}																															
							}

							$scope.siteList[indexSite].redirectList[indexPla].charged=true;
							
							deselectTagWhenChargeTag(placement.siteId);
							
							if(type == "loadTags"){
								$scope.loader=true;
							}
							
							resolve(true);
						} else {

							reject(true);
						}
					}
			);
	    });						
	};
	
	$scope.changeArrow=function(id){
		
		if($('#'+id).find('span').hasClass('glyphicon-chevron-down')){
			$('#'+id).find('span').removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-up');
		}else{
			if($('#'+id).find('span').hasClass('glyphicon-chevron-up')){
				$('#'+id).find('span').removeClass('glyphicon-chevron-up').addClass('glyphicon-chevron-down');
			}
		}
	}
	
	//click the rechargerButton
	$scope.validTag=function(placement,tagType,siteName){
			
			ga('send', 'event', 'button', 'click', 'Validation Tag:pId'+placement.placementId+' '+tagType);
			
			var camDCMToSave={
					"camId":$scope.camSizmekId,
					"camName":$scope.camSizmekName,
					"advertiserId":$scope.advertiserID,
					"networkId":0,
					"begindate":$scope.camBeginDate,
					"enddate":$scope.camEndDate,
					"adServer":'SIZMEK'
				};
			
			saveCampaignGInfo.saveCamInfo($scope.tokenTM,camDCMToSave);
			
			//get the tag value by realtime
			var tagV=$('#'+placement.placementId+tagType).val();
			var tagD=$('#desc_'+placement.placementId+tagType).val();
			var tag={
				"camId":$scope.camSizmekId,
				"placementId":placement.placementId,
				"tagType":tagType,
				"tagValue":tagV,
				"status":"VALID",
				"tagDescription":tagD,
				"adServer":2
			};
			
			var placementNew={
				"camId":$scope.camSizmekId,
				"placementId":placement.placementId,
				"placementName":placement.placementName,
				"placementSize":placement.placementSize,
				"siteName":placement.siteName,
				"siteId":placement.siteId,
				"date_begin":placement.date_begin,
				"date_end":placement.date_end,
				"status":"VALID",
				"adServer":2
			};
							
			$scope.tagValid=false;
			saveSingleTagTM.saveSingleTag($scope.tokenTM,tag,placementNew).then(
					 function(data, status, headers, config){
						 
						    if(data.status == 200){
						    
						    	if(placementNew.status=='VALID'){
						    		setTagColor(placement.placementId,tagType,"#CCFFD5");
									
							    	setTagMessageByValid(placement.placementId,tagType,$scope.login,'VALID');
								
							    	placement.status="VALID";
								
							    	placement["bgcolor"]="#CCFFD5";
						    	}
						    	
						    	if(placementNew.status=='OOS'){
						    		
						    		setTagColor(placement.placementId,tagType,"#ff6e00");
									
							    	setTagMessageByValid(placement.placementId,tagType,$scope.login,'OOS');
								
							    	placement.status="OOS";
								
							    	placement["bgcolor"]="#ff6e00";
						    	}
							
							//call set log
							saveLog($scope.camSizmekId,$scope.login,placementNew.status,placement,siteName,tagType,tagV);
						    } else {
						    	
						    	setTagColor(placement.placementId,tagType,"#FFCCCC");
								setTimeout(function(){
									setTagColor(placement.placementId,tagType,"#FFF4B2");
									$scope.$apply();
								}, 5000);
						    }
					  }
			   );
	}
	
	$scope.validSite=function(site,statusToChange){
		
		$scope.loader=false;
		
		if(!isRealValue(site.siteCharged) || site.siteCharged==false){
			$scope.getSiteRedirect(site.siteId);
		}
		
		var interval = $interval(function siteCharged(){
	        if (!isRealValue(site.siteCharged) || site.siteCharged==false){
	        	
	        } else {        	
        		$scope.loader=false;
	        	$interval.cancel(interval);
	    		
	    		if(!isRealValue(site.allLoaded) || site.allLoaded!=true){
	    			
	    			var allLoaded = [];
	    			for(var i=0;i<site.redirectList.length;i++){
	    			
	    				if(site.redirectList[i].charged == false){
	    					var promise = $scope.loadPlacementTags(site.redirectList[i], "loadSite");
	    					promise.then(function(loaded) {
	    					allLoaded.push(true);
	    					}, function(noData) {
	    						allLoaded.push(true);
	    					});
	    				} else {
	    					allLoaded.push(true);
	    				}
	    			}
	    		
	    			var interval1 = $interval(function testIdExist(){
	    	        if (allLoaded.length < site.redirectList.length){		        		
	    	        } else {			  
	    	        	$interval.cancel(interval1);
	    	        	site.allLoaded=true;

	    	        	validSiteBackend(site, statusToChange);
	    	        }		
	    	    }, 500);
	    		} else {
	    			validSiteBackend(site, statusToChange);
	    		}
	        }		
	    }, 500);
	}
	
	
	function validSiteBackend(site,statusToChange){
		
		$scope.loader=false;
		
		var statusOrigin = site.satus;
		var camDCMToSave={
				"camId":$scope.camSizmekId,
				"camName":$scope.camSizmekName,
				"advertiserId":$scope.advertiserID,
				"networkId":$scope.netWID,
				"begindate":$scope.camBeginDate,
				"enddate":$scope.camEndDate,
				"adServer":'SIZMEK'	
			};
		
		saveCampaignGInfo.saveCamInfo($scope.tokenTM,camDCMToSave);
		
		_.times(site.redirectList.length, function(m){
			
			site.redirectList[m].charged=true;
			
			if(site.redirectList[m].archived == false && site.redirectList[m].show2 == true){
				
				_.times(site.redirectList[m].redTagList.length, function(n){
				
					site.redirectList[m].redTagList[n]["toValid"] = site.redirectList[m].redTagList[n].show;
					site.redirectList[m].redTagList[n].adServer = 2;
					site.redirectList[m].redTagList[n].placementId = site.redirectList[m].placementId;
					site.redirectList[m].redTagList[n].status = statusToChange;
		
					site.redirectList[m].redTagList[n].tagValue =  $('#' + site.redirectList[m].placementId + site.redirectList[m].redTagList[n].tagType).val();
					site.redirectList[m].redTagList[n].tagDescription =  $('#desc_' + site.redirectList[m].placementId + site.redirectList[m].redTagList[n].tagType).val();
					});
			
				site.redirectList[m].camId = $scope.camSizmekId;
				site.redirectList[m].adServer = 2;
				site.redirectList[m].login = $scope.login;
				site.redirectList[m].status = statusToChange;
			}	
		});
	
		validSite.validSite($scope.tokenTM, site).then(function(result){
			
				var siteValid = true;
				
				_.times(result.data.redirectList.length, function(n){
					
					if(result.data.redirectList[n].archived == false){
						
					result.data.redirectList[n].charged=true;
					
					_.times(result.data.redirectList[n].redTagList.length, function(m){
						
						if(result.data.redirectList[n].redTagList[m].toValid == true){
							result.data.redirectList[n].redTagList[m]["show"] = result.data.redirectList[n].redTagList[m].toValid;
							if(result.data.redirectList[n].redTagList[m].status && result.data.redirectList[n].redTagList[m].status.toUpperCase().indexOf("VALI")>-1){
								result.data.redirectList[n].redTagList[m]["bgcolor"]="#CCFFD5";	
								result.data.redirectList[n].redTagList[m]["message"]="VALID"+" by "+result.data.redirectList[n].redTagList[m].modifyByName+" at "+result.data.redirectList[n].redTagList[m].lastupdatedDate;

							}else{
								if(result.data.redirectList[n].redTagList[m].status && result.data.redirectList[n].redTagList[m].status.toUpperCase().indexOf("DELETE")>-1){
									result.data.redirectList[n].redTagList[m]["bgcolor"]="#FFCCCC";
									result.data.redirectList[n].redTagList[m]["message"]="DELETE"+" by "+result.data.redirectList[n].redTagList[m].modifyByName+" at "+result.data.redirectList[n].redTagList[m].lastupdatedDate;
								}else{
									if(result.data.redirectList[n].redTagList[m].status && result.data.redirectList[n].redTagList[m].status.toUpperCase().indexOf("OOS")>-1){
										result.data.redirectList[n].redTagList[m]["bgcolor"]="#ff6e00";	
										result.data.redirectList[n].redTagList[m]["message"]="SET OUT OF SCOPE"+" by "+result.data.redirectList[n].redTagList[m].modifyByName+" at "+result.data.redirectList[n].redTagList[m].lastupdatedDate;

									}else{
										result.data.redirectList[n].redTagList[m]["bgcolor"]="#FFF4B2";
										result.data.redirectList[n].redTagList[m]["message"]="waiting for validation";
									}
								}
							}
						} else {
							result.data.redirectList[n].redTagList[m]["show"] = false;
							
							var index=_.findIndex(site.redirectList, function(chr) {
								  return chr.placementId == result.data.redirectList[n].placementId;
							});
							
							_.times(site.redirectList[index].redTagList.length, function(k){
								if(site.redirectList[index].redTagList[k].tagType == result.data.redirectList[n].redTagList[m].tagType){					
									result.data.redirectList[n].redTagList[m]["bgcolor"] = site.redirectList[index].redTagList[k].bgcolor;
									result.data.redirectList[n].redTagList[m]["message"] = site.redirectList[index].redTagList[k].message;
								}
							});
						}
					});
					
					var placementValid = true;
					_.times(result.data.redirectList[n].redTagList.length, function(j){
						
						if(result.data.redirectList[n].redTagList[j].wellSave == false){
							placementValid = false;
							siteValid = false;
						}
					});
					
					var index=_.findIndex(site.redirectList, function(chr) {
						  return chr.placementId == result.data.redirectList[n].placementId;
					});
					
					site.redirectList[index].redTagList = result.data.redirectList[n].redTagList;
					if(placementValid == true){
						site.redirectList[index].status = statusToChange;
						if(statusToChange == "VALID"){
							site.redirectList[index]["bgcolor"]="#CCFFD5";
						} else {
							site.redirectList[index]["bgcolor"]="#ff6e00";
						}
					} else {
						site.redirectList[index]["bgcolor"]="#FFCCCC";
						   $scope.$apply();
						   setTimeout(function(){
							   site.redirectList[index]["bgcolor"]=colorOrigin;
								$scope.$apply();					
							}, 3000);
					}
				  }
				});
				
				if(siteValid == true){
					if(statusToChange==='VALID'){
						$("#site_"+site.siteId).css("background-image","linear-gradient(to bottom, #ccffd5 0%, #ccffd5 100%)");
					}									
					if(statusToChange==='OOS'){
						$("#site_"+site.siteId).css("background-image","linear-gradient(to bottom, #ec971f 0%, #ec971f 100%)");
					}
					setTimeout(function(){
						$("#site_"+site.siteId).css("background-image","linear-gradient(to bottom, #FFFFFF 0%, #FFFFFF 100%)");
					}, 5000);
				} else {
					   $("#site_"+site.siteId).css("background-image","linear-gradient(to bottom, #FFCCCC 0%, #FFCCCC 100%)");
					   $scope.$apply();
					   setTimeout(function(){
						   $("#site_"+site.siteId).css("background-image","linear-gradient(to bottom, #FFFFFF 0%, #FFFFFF 100%)");
							$scope.$apply();
							
							alert("At least one tag is not well valided");
						}, 3000);
				}

				$scope.loader=true;
			},function(){
				$scope.loader=true;
			});	
	}
	
	$scope.validPlacement=function(placement,siteName,status){
		
		if(!placement.charged){
			var promise = $scope.loadPlacementTags(placement, "validPlacement");
			promise.then(function(loaded) {
				validPlacementBackend(placement, status);
			}, function(noData) {
				validPlacementBackend(placement, status);
			});
	    	
	    } else {
	    	validPlacementBackend(placement, status);
	    }
	}	
	
	function validPlacementBackend(placement, statusToChange){
		$scope.loader=false;
		ga('send', 'event', 'button', 'click', 'VALID Placement:pId'+placement.placementId);
		
		var statusOrigin = placement.satus;
		var colorOrigin = placement.bgcolor;
		var camDCMToSave={
				"camId":$scope.camSizmekId,
				"camName":$scope.camSizmekName,
				"advertiserId":$scope.advertiserID,
				"networkId":0,
				"begindate":$scope.camBeginDate,
				"enddate":$scope.camEndDate,
				"adServer":'SIZMEK'
			};
		
		saveCampaignGInfo.saveCamInfo($scope.tokenTM,camDCMToSave);
		
		_.times(placement.redTagList.length, function(n){
			
			placement.redTagList[n]["toValid"] = placement.redTagList[n].show;
			placement.redTagList[n].adServer = 2;
			placement.redTagList[n].placementId = placement.placementId;
			placement.redTagList[n].status = statusToChange;
		});
		
			placement.camId = $scope.camSizmekId;
			placement.adServer = 2;
			placement.login = $scope.login;
			placement.status = statusToChange;

			validPlacement.validPlacement($scope.tokenTM, placement).then(function(result){
				
				_.times(result.data.redTagList.length, function(m){
					
					if(result.data.redTagList[m].toValid == true){
						result.data.redTagList[m]["show"] = result.data.redTagList[m].toValid;
						if(result.data.redTagList[m].status && result.data.redTagList[m].status.toUpperCase().indexOf("VALI")>-1){
							result.data.redTagList[m]["bgcolor"]="#CCFFD5";	
							result.data.redTagList[m]["message"]="VALID"+" by "+result.data.redTagList[m].modifyByName+" at "+result.data.redTagList[m].lastupdatedDate;

						}else{
							if(result.data.redTagList[m].status && result.data.redTagList[m].status.toUpperCase().indexOf("DELETE")>-1){
								result.data.redTagList[m]["bgcolor"]="#FFCCCC";
								result.data.redTagList[m]["message"]="DELETE"+" by "+result.data.redTagList[m].modifyByName+" at "+result.data.redTagList[m].lastupdatedDate;
							}else{
								if(result.data.redTagList[m].status && result.data.redTagList[m].status.toUpperCase().indexOf("OOS")>-1){
									result.data.redTagList[m]["bgcolor"]="#ff6e00";	
									result.data.redTagList[m]["message"]="SET OUT OF SCOPE"+" by "+result.data.redTagList[m].modifyByName+" at "+result.data.redTagList[m].lastupdatedDate;

								}else{
									result.data.redTagList[m]["bgcolor"]="#FFF4B2";
									result.data.redTagList[m]["message"]="waiting for validation";
								}
							}
						}
					} else {
						result.data.redTagList[m]["show"] = false;
						_.times(placement.redTagList.length, function(n){
							if(placement.redTagList[n].tagType == result.data.redTagList[m].tagType){
								result.data.redTagList[m]["bgcolor"] = placement.redTagList[n].bgcolor;
								result.data.redTagList[m]["message"] = placement.redTagList[n].message;
							}
						});
					}
				});
				
				var placementValid = true;
				_.times(placement.redTagList.length, function(n){
					
					if(placement.redTagList[n].wellSave == false){
						placementValid = false;
					}
				});
				
				placement.redTagList = result.data.redTagList;
				if(placementValid == true){
					placement.status = statusToChange;
					if(statusToChange == "VALID"){
						placement["bgcolor"]="#CCFFD5";
					} else {
						placement["bgcolor"]="#ff6e00";
					}
				} else {
					   placement["bgcolor"]="#FFCCCC";
					   $scope.$apply();
					   setTimeout(function(){
						    placement["bgcolor"]=colorOrigin;
							$scope.$apply();
							
							alert("At least one tag is not well valided");
							
						}, 3000);
				}
				
				$scope.loader=true;
			},function(){
				$scope.loader=true;
			});	
	}
	
	$scope.checkClick=function(tagType,value,siteId){				
		var type;

		switch(tagType){
		case "CI":type="CreateIFrame";
			break;
		case "IF":type="IFrame";
			break;
		case "SC":type="Script";
			break;
		}
		
		for(var i=0;i<$scope.siteList.length;i++){
			
			if($scope.siteList[i].siteId===siteId){
				for(var j=0;j<$scope.siteList[i].redirectList.length;j++){
					for(var k=0;k<($scope.siteList[i].redirectList)[j].redTagList.length;k++){				
						if((($scope.siteList[i].redirectList)[j].redTagList)[k].tagType.toUpperCase()===type.toUpperCase()){
							(($scope.siteList[i].redirectList)[j].redTagList)[k].show=value;
						}
						
					}
				}
			}
		}
	}
	
	
	function deselectTagWhenChargeTag(siteId){
		
		if(!$('#' + siteId + '_cbCI').is(':checked')){
			$scope.checkClick('CI',false,siteId);
		}
		if(!$('#' + siteId + '_cbIF').is(':checked')){
			$scope.checkClick('IF',false,siteId);
		}
		if(!$('#' + siteId + '_cbSC').is(':checked')){
			$scope.checkClick('SC',false,siteId);
		}
	}	
	
	function replaceTagValue(pId,tType,tValue,tDesc,login,time){
		for(var i=0;i<$scope.siteList.length;i++){
			if($scope.siteList[i].siteCharged==true){
				for(var j=0;j<($scope.siteList)[i].redirectList.length;j++){
					if(($scope.siteList)[i].redirectList[j].placementId===pId){
						for(var k=0;k<($scope.siteList)[i].redirectList[j].redTagList.length;k++){
							if(((($scope.siteList)[i].redirectList)[j].redTagList)[k].tagType===tType){
								((($scope.siteList)[i].redirectList)[j].redTagList)[k].tagValue=tValue;
								((($scope.siteList)[i].redirectList)[j].redTagList)[k].tagDescription=tDesc;
								((($scope.siteList)[i].redirectList)[j].redTagList)[k].modifyByName=login;
								((($scope.siteList)[i].redirectList)[j].redTagList)[k].lastupdatedDate=time;
							}
						}
					}
				}
			}
		}
	}
	
	function setTagColor(pId,tType,color){
		
		for(var i=0;i<$scope.siteList.length;i++){
			if($scope.siteList[i].siteCharged == true){
				for(var j=0;j<($scope.siteList)[i].redirectList.length;j++){
					if(($scope.siteList)[i].redirectList[j].placementId===pId){
						for(var k=0;k<($scope.siteList)[i].redirectList[j].redTagList.length;k++){
							if(((($scope.siteList)[i].redirectList)[j].redTagList)[k].tagType===tType){
								((($scope.siteList)[i].redirectList)[j].redTagList)[k]["bgcolor"]=color;
							}
						}
					}
				}
			}
		}
	}
	
	function setTagMessage(pId,tType,status){
		for(var i=0;i<$scope.siteList.length;i++){
			for(var j=0;j<($scope.siteList)[i].redirectList.length;j++){
				if(($scope.siteList)[i].redirectList[j].placementId===pId){
					for(var k=0;k<($scope.siteList)[i].redirectList[j].redTagList.length;k++){
						if(((($scope.siteList)[i].redirectList)[j].redTagList)[k].tagType===tType){
							((($scope.siteList)[i].redirectList)[j].redTagList)[k]["message"]=status+" by "+((($scope.siteList)[i].redirectList)[j].redTagList)[k].modifyByName+" at "+((($scope.siteList)[i].redirectList)[j].redTagList)[k].lastupdatedDate;
						}
					}
				}
			}
		}
	}
	
	function setTagMessageByValid(pId,tType,login,status){
		for(var i=0;i<$scope.siteList.length;i++){
			if(($scope.siteList)[i].siteCharged == true){
				for(var j=0;j<($scope.siteList)[i].redirectList.length;j++){
					if(($scope.siteList)[i].redirectList[j].placementId===pId){
						for(var k=0;k<($scope.siteList)[i].redirectList[j].redTagList.length;k++){
							if(((($scope.siteList)[i].redirectList)[j].redTagList)[k].tagType===tType){
							
								((($scope.siteList)[i].redirectList)[j].redTagList)[k]["message"]=status+" by "+login+" at "+dateFormat();
							}
						}
					}
				}
			}
		}
	}

	
	
	function saveLog(camID,login,status,placement,site,tagType,tagValue){
		
		saveLogAtTM.saveLog(camID,login,status,placement,site,tagType,tagValue,2);
	}
	
	function getRetriveDate(campaignId){
			getCamRetriveDate.getCamRetriveDate(campaignId,2).then(function(result){
				$scope.camSelected.lastRetriveDate=result.data;
			});
		}
	
	function getLogByCampaign(camID){
		getLogByCampaignId.getCamAllLog(camID,2).then(
				 function(data, status, headers, config){
						$scope.logList=data.data;
						$scope.logListOrigin=$scope.logList;
				    }
				);
	}
	
	function isRealValue(obj){
		 if( obj && obj !== "null" && obj!== "undefined" && obj!== undefined && obj!== ""){
			 return true;
		 } else {
			 return false;
		 }
	}
	
	function dateFormat() {
		  var now = new Date();
		  var year = "" + now.getFullYear();
		  var month = "" + (now.getMonth() + 1); if (month.length == 1) { month = "0" + month; }
		  var day = "" + now.getDate(); if (day.length == 1) { day = "0" + day; }
		  var hour = "" + now.getHours(); if (hour.length == 1) { hour = "0" + hour; }
		  var minute = "" + now.getMinutes(); if (minute.length == 1) { minute = "0" + minute; }
		  var second = "" + now.getSeconds(); if (second.length == 1) { second = "0" + second; }
		  return year + "/" + month + "/" + day + " " + hour + ":" + minute + ":" + second;
		}
	
	
	function replaceAll(find, replace, str) {
		  return str.replace(new RegExp(find, 'g'), replace);
	}
	
	
	function setPlacementColor(pId,status,color){
		for(var k=0;k<$scope.siteList.length;k++){
			for(var u=0;u<$scope.siteList[k].redirectList.length;u++){
				if($scope.siteList[k].redirectList[u].placementId===pId){
					$scope.siteList[k].redirectList[u].status=status;
					$scope.siteList[k].redirectList[u]["bgcolor"]=color;
				}
			}
		}
		
	}
	
	$scope.filterPlacemetsByStatus=function(status){
		
		inversePLbuttons();
		 
		 if(status==='NEW'){
			 $scope.nwPlButton=false;
		 }
		 if(status==='OOS'){
			 $scope.oosPlButton=false;
		 }
		 if(status==="ARCHIVED"){
			 $scope.archPlButton=false;
		 }
		 if(status==='VALID'){
			 $scope.valPlButton=false;
		 }
		 if(status==='ALL'){
			 $scope.allPlButton=false;
		 }
			
			var placementFilter = false;
			var filterPlacement = $('#placementFilter').val();
			if(typeof filterPlacement != "undefined" && filterPlacement != "" && filterPlacement != " "){
				placementFilter = true;
			}
								
			for(var n=0;n<$scope.siteList.length;n++){
				
				if($scope.siteList[n].siteCharged==true){
				
				for(var i=0;i < (($scope.siteList)[n].redirectList).length; i++ ){
					
					var redTagList=((($scope.siteList)[n].redirectList)[i]).redTagList;
					
					if(status!=='ALL'){
						if((($scope.siteList)[n].redirectList)[i].status==status){
							((($scope.siteList)[n].redirectList)[i])["show2"]=true;
						}
						else{
							((($scope.siteList)[n].redirectList)[i])["show2"]=false;
						}
					} else {
						((($scope.siteList)[n].redirectList)[i])["show2"]=true;
					}
					
					if(((($scope.siteList)[n].redirectList)[i])["show2"]==true && placementFilter == true){
						filterPlacement = filterPlacement.trim();
						var tmp = (($scope.siteList)[n].redirectList)[i].placementId+(($scope.siteList)[n].redirectList)[i].placementName;			
						if(tmp.toUpperCase().indexOf(filterPlacement.toUpperCase())==-1){
							(($scope.siteList)[n].redirectList)[i]["show2"]=false;
						} else {
							(($scope.siteList)[n].redirectList)[i]["show2"]=true;
						}
					}
				}
			}
		 }
	}
	
	$scope.clickHelpMe=function(){
		$window.open(envconf.backend+"/help?t="+$scope.tokenTM);
	}
	
	function initPLbuttons(){
		 $scope.nwPlButton=false;
		 $scope.oosPlButton=false;
		 $scope.archPlButton=false;
		 $scope.valPlButton=false;
		 $scope.allPlButton=false;
	}
	
	
	function inversePLbuttons(){
		 $scope.nwPlButton=true;
		 $scope.oosPlButton=true;
		 $scope.archPlButton=true;
		 $scope.valPlButton=true;
		 $scope.allPlButton=true;
	}
	
	$scope.toggleList=function(){
		$scope.flag1=true;
	}
	
	$scope.setFlag1=function(){
		if($scope.fromTM == false){
			$scope.flag1=true;
		}
	};
		
	$scope.logout=function(){
		$window.location.href=envconf.backend+"/#";
	};
	
}]);