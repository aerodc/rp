'use strict';
var wsrp = angular.module('wsrp');

wsrp.controller('indexController',['$scope','$location','$cookieStore','getCampaignWithAllRedirectsTM','getCampaignWithAllRedirectsDCM','getSingleTagDCM','getSingleTagTM',
                                    'getLogByCampaignId','saveCampaignGInfo','saveLogAtTM','saveSingleTagTM','savePlacementGInfo',
                                    'savePlacementAllTags','getCamDesc','saveCamDesc','getAdvNameDCMV1','rechargePlaTags','getPlNBs','$window','$interval','$q','envconf',
                                    'getPlaTagDCM','validPlacement','validSite','getSiteDCM','getCamRetriveDate',
                                    function($scope,$location,$cookieStore,getCampaignWithAllRedirectsTM,getCampaignWithAllRedirectsDCM,getSingleTagDCM,getSingleTagTM,
                                    		getLogByCampaignId,saveCampaignGInfo,saveLogAtTM,saveSingleTagTM,savePlacementGInfo,
                                    		savePlacementAllTags,getCamDesc,saveCamDesc,getAdvNameDCMV1,rechargePlaTags,getPlNBs,$window,$interval,$q,envconf,
                                    		getPlaTagDCM,validPlacement,validSite,getSiteDCM,getCamRetriveDate){
	
	// parse the url to get the paras
	var arrayUrl=$location.absUrl().split('?');
	
	var token =((arrayUrl[1].split('&'))[0].split('='))[1];
	var camID =((arrayUrl[1].split('&'))[1].split('='))[1];
	var netWID =((arrayUrl[1].split('&'))[2].split('='))[1];
	var camTMId =((arrayUrl[1].split('&'))[3].split('='))[1];
	var advTMName =((arrayUrl[1].split('&'))[4].split('='))[1];
	advTMName=advTMName.replace("%20"," ");
	var camTMName =((arrayUrl[1].split('&'))[5].split('='))[1];
	camTMName=replaceAll("%20"," ",camTMName);

	var login =((arrayUrl[1].split('&'))[6].split('='))[1];
	
	var request1, request2;
	var mytimer,myInterval;
	
	$scope.netWID=netWID;
	$scope.camTMId=camTMId;
	$scope.advTMName=advTMName;
	$scope.camTMName=camTMName;
	$scope.login=login;
	$scope.token=token;
	$scope.hasModifToRetrive=false;
	
	 $scope.cbST=true;
	 $scope.cbJS=true;
	 $scope.cbIJ=true;
	 $scope.cbIR=true;
	 $scope.cbCT=true;
	 $scope.cbII=true;
	 $scope.cbIIF=true;
	 $scope.cbCL=true;
	 $scope.cbPF=true;
	 $scope.cbJSLG=true;
	 $scope.cbIJLG=true;
	 
	 initPLbuttons();
	 
	 $scope.messageSyn="";
	
	 $scope.camDesc;
	 
	 $scope.showPlNb=false;
	 
	 var progress2=0;
	 var progress1=0;
	 $scope.indicationText=" placements for this campaign are charging from DCM......";
	if(isRealValue(token)&&isRealValue(camID)&&isRealValue(netWID)){
		//Show Page
		//call web service TM =>get campaign and his placements
		getCampaignWithAllRedirectsTM.getCamAllRed(token,camID).then(
		 function(result){
			 if(isRealValue(result)&&isRealValue(result.data)){
				 result=result.data;
					//console.log(result);
					$scope.camDCMId=result.camId;
					$scope.camDCMName=result.camName;
					$scope.advertiserID=result.advertiserId;
					$scope.camBeginDate=result.begindate;
					$scope.camEndDate=result.enddate;
					$scope.hasModifToRetrive=result.hasModifToRetrive;
					$scope.lastRetriveDate=result.lastRetriveDate;
					if(isRealValue(result.siteList)){
						$scope.siteList=result.siteList.sort(function(a,b){
							 if (a.siteId > b.siteId)
							      return 1;
							    if (a.siteId < b.siteId)
							      return -1;
							    return 0;
						});
					}
					getAdvNameDCMV1.getAdvNameV1(token,$scope.netWID,$scope.advertiserID).then(function(result){
						$scope.camSelAdvName=result.data;
					});
					
			 }else{
				 $scope.camDCMId=0;
			 }

				//if campaign is null
				  //call DCM Service --> get Camp and list of placements
			 
			 getPlNBs.getplnbs(token,camID,netWID,1,0).then(function(result){
					
					if(isRealValue(result)&&isRealValue(result.data)){
						$scope.placementNB=result.data;
						$scope.showPlNb=true;
						
						if($scope.placementNB < 150){
							 mytimer=setTimeout(function(){
								 
								 $('#timeoutModal').modal('show');
								 if(isRealValue(request1))
									 request1.cancel();
								 
								 if(isRealValue(request2))
									 request2.cancel();
								 $scope.loader=true;
								 $scope.showPlNb=false;
								 
							 },1000*($scope.placementNB+10));
						}

						 myInterval=setInterval(function(){
								progress1=progress1+2;
								progress2=Math.round((progress1/$scope.placementNB)*100);
								
								$('.progress-bar').css('width', progress2+'%').attr('aria-valuenow', progress2);  
								
						 },200);
							
					}
					
				});
			 
				if($scope.camDCMId==0||!isRealValue($scope.camDCMName)){
					
					request1=getCampaignWithAllRedirectsDCM.getCamAllRedDCM(token,camID,netWID,1,0);
					
					request1.promise.then(
						function(result1, status, headers, config){
							
							if(result1.status == 200){
							if(isRealValue(result1) && isRealValue(result1.data) && isRealValue(result1.data.siteList)){
								result1=result1.data;
								$scope.camDCMId=result1.camId;
								$scope.camDCMName=result1.camName;
								$scope.advertiserID=result1.advertiserId;
								$scope.camBeginDate=result1.begindate;
								$scope.camEndDate=result1.enddate;
								$scope.siteList=result1.siteList.sort(function(a,b){
									 if (a.siteId > b.siteId)
									      return 1;
									    if (a.siteId < b.siteId)
									      return -1;
									    return 0;
								});
								
								var camDCMToSave={
									"camId":$scope.camDCMId,
									"camName":$scope.camDCMName,
									"advertiserId":$scope.advertiserID,
									"networkId":$scope.netWID,
									"begindate":$scope.camBeginDate,
									"enddate":$scope.camEndDate,
									"adServer":'DCM'
								};
								
								getAdvNameDCMV1.getAdvNameV1(token,$scope.netWID,$scope.advertiserID).then(function(result){
									$scope.camSelAdvName=result.data;
								});
								saveCampaignGInfo.saveCamInfo($scope.token,camDCMToSave);
							}
						} else {
							$scope.loader=true;
							$scope.showPlNb=false;
							clearTimeout(mytimer);
							clearInterval(myInterval);
							progress2=0;
							progress1=0;
							alert("Server error: The adserver has not responded.");
						}
						}
					);
				}

					//else campaign is not null
					  //call DCM Service -- > get Camp and list of placements
				
					
				request2=getCampaignWithAllRedirectsDCM.getCamAllRedDCM(token,camID,netWID,1,0);
				
				request2.promise.then(
							function(result2, status, headers, config){									
								if(result2.status == 200){
								if(isRealValue(result2) && isRealValue(result2.data) && isRealValue(result2.data.siteList)){
									result2=result2.data;
									
									// loop in the list of placements
									var i,j;
									//to see if there is new site
									if(isRealValue(result2.siteList)){
										
										$scope.siteList=result2.siteList.sort(function(a,b){
											 if (a.siteId > b.siteId)
											      return 1;
											    if (a.siteId < b.siteId)
											      return -1;
											    return 0;
										});

											
										var siteLength=$scope.siteList.length;
								
										for(var n=0;n<$scope.siteList.length;n++){
											
											//$scope.siteList[n].placementListDCM;
											for(i=0;i < (($scope.siteList)[n].redirectList).length ; i++ ){									
												var redTagList=((($scope.siteList)[n].redirectList)[i]).redTagList;
												((($scope.siteList)[n].redirectList)[i])["show2"]=true;
												((($scope.siteList)[n].redirectList)[i])["charged"]=false;
												if((($scope.siteList)[n].redirectList)[i].status==="ARCHIVED"){
													(($scope.siteList)[n].redirectList)[i]["bgcolor"]="#FFCCCC";
												}else{
													if((($scope.siteList)[n].redirectList)[i].status==='VALID'){
														$scope.siteList[n].redirectList[i]["bgcolor"]="#CCFFD5";
													}else{
														if((($scope.siteList)[n].redirectList)[i].status==='OOS'){
															$scope.siteList[n].redirectList[i]["bgcolor"]="#ff6e00";
														}else{
															(($scope.siteList)[n].redirectList)[i].status='NEW';
															$scope.siteList[n].redirectList[i]["bgcolor"]="#FFF4B2";
														}
													}
												}
												
												((($scope.siteList)[n].redirectList)[i]).redTagList= new Array(9);
												for(j=0;j<9;j++){
													
													(((($scope.siteList)[n].redirectList)[i]).redTagList)[j]={
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
											
										}
										
										$scope.loader=true;
										$scope.showPlNb=false;
										clearTimeout(mytimer);
										clearInterval(myInterval);
										progress2=0;
										progress1=0;
										}
										
									
									} else {
										$scope.loader=true;
										$scope.showPlNb=false;
										clearTimeout(mytimer);
										clearInterval(myInterval);
										progress2=0;
										progress1=0;
										alert("There is no data for this campaign, please verify campaign information.");
									}
									
							} else {
								$scope.loader=true;
								$scope.showPlNb=false;
								clearTimeout(mytimer);
								clearInterval(myInterval);
								progress2=0;
								progress1=0;
								alert("Server error: The adserver has not responded.");
							}
								}
						);

		}
		);
				
		//get description of campaigne
		getCamDesc.getCamDesc(camID,1).then(function(result){
			$scope.camDesc=result.data;
		});
		
		//get log
		getLogByCampaign(getLogByCampaignId,camID);

		
	}
	

	$scope.saveCamDesc=function(){
		var desc= $('#descId').val();
		var camId=$('#searchById').val();
		saveCamDesc.saveCamDesc(camId,desc,1);
	}
	
	
	$scope.deleteTag=function(placement,tagType){
		
		var confirmed = confirm("Do you really want to delete this Tag?");
	    if(confirmed){
	    	ga('send', 'event', 'button', 'click', 'Delete Tag:pId'+placement.placementId+' '+tagType);
		for(var i=0;i<$scope.siteList.length;i++){
			
				for(var j=0;j<$scope.siteList[i].redirectList.length;j++){
					if(($scope.siteList[i].redirectList)[j].placementId===placement.placementId){
					
						for(var k=0;k<($scope.siteList[i].redirectList)[j].redTagList.length;k++){
							if((($scope.siteList[i].redirectList)[j].redTagList)[k].tagType===tagType){
								(($scope.siteList[i].redirectList)[j].redTagList)[k].status="DELETE";
								((($scope.siteList)[i].redirectList)[j].redTagList)[k]["bgcolor"]="#FFCCCC";
								var tagV=$('textarea#'+placement.placementId+tagType).val();
								var tagD=$('textarea#desc_'+placement.placementId+tagType).val();
								var tag={
										"camId":camID,
										"placementId":placement.placementId,
										"tagType":tagType,
										"tagValue":tagV,
										"status":"DELETE",
										"tagDescription":tagD,
										"adServer":1
									};
								var placementNew={
										"camId":camID,
										"placementId":placement.placementId,
										"placementName":placement.placementName,
										"placementSize":placement.placementSize,
										"siteName":placement.siteName,
										"siteId":placement.siteId,
										"date_begin":placement.date_begin,
										"date_end":placement.date_end,
										"adServer":1
									};
								saveSingleTagTM.saveSingleTag($scope.token,tag,placementNew);
								saveLog(camID,login,'DELETE',placement,placement.siteName,tagType,tagV);
								setTagMessageByValid(placement.placementId,tagType,login,'DELETE');
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
	
	 angular.element(document).ready(function () {
		 
		 	$('#retriveDate').tooltip();
					
			$("#bt1").trigger("click");
			$("#divSites").css({top:'190px'});

			$scope.resetTag=function(placementId,tagType,recharge){

				var confirmed = true;
				
				if(recharge == true){
					confirmed = confirm("Recharge the Tag?");
				}
				if(confirmed){
					ga('send', 'event', 'button', 'click', 'Recharge Tag:pID'+placementId+' '+tagType);
					$scope.loader=false;
				            //call dcm service to recharge tag
						getSingleTagDCM.getTagDCM(token,camID,placementId,tagType,$scope.netWID,1,0).then(
								function(result){
									if(isRealValue(result.data)){

										replaceTagValue(placementId,tagType,result.data.tagValue,result.data.tagDescription,login,dateFormat());
										
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
				
				var confirmed = confirm("Recharge the site?");
				
				if(confirmed){
					
					getSiteDCM.getSiteDCM(token,camID,$scope.netWID,site.siteId,1,0).then(function(result){
						if(isRealValue(result.data)){
									
							site = result.data;
							for(var j=0;j<site.redirectList.length;j++){
								for(var i=0;i<site.redirectList[j].redTagList.length;i++){
									
									$("#"+site.redirectList[j].placementId+site.redirectList[j].redTagList[i].tagType).val(site.redirectList[j].redTagList[i].tagValue);
									$("#desc_"+site.redirectList[j].placementId+site.redirectList[j].redTagList[i].tagType).val(site.redirectList[j].redTagList[i].tagDescription);
								}
							}
							
							$scope.loader=true;
						}
					}
				);	
				}
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
								
					            //call dcm service to recharge placement
								getPlaTagDCM.getPlaTagDCM(token,camID,$scope.netWID,placement.placementId,1,0).then(
									function(result){
										if(isRealValue(result.data)){
											
											placement = result.data;

											for(var i=0;i<placement.redTagList.length;i++){
												
												$("#"+placement.placementId+placement.redTagList[i].tagType).val(placement.redTagList[i].tagValue);
												$("#desc_"+placement.placementId+placement.redTagList[i].tagType).val(placement.redTagList[i].tagDescription);
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
						
							//call dcm service to recharge placement
							getPlaTagDCM.getPlaTagDCM(token,camID,$scope.netWID,placement.placementId,1,0).then(
									function(result){
										if(isRealValue(result.data)){
									
											placement = result.data;

											for(var i=0;i<placement.redTagList.length;i++){
										
												$("#"+placement.placementId+placement.redTagList[i].tagType).val(placement.redTagList[i].tagValue);
												$("#desc_"+placement.placementId+placement.redTagList[i].tagType).val(placement.redTagList[i].tagDescription);
											}
											$scope.loader=true;
										}
									}
							);			
						}
					}
				}
			}
			
			$scope.loadPlacementTags=function(placement, type){
				
				return $q(function(resolve, reject) {
				
				$scope.loader=false;
				rechargePlaTags.rechargePlaTags(token,camID,netWID,placement.placementId,1,0).then(
						function(result){
							if(isRealValue(result.data)){
							
								var indexSite=_.findIndex($scope.siteList,function(chr){
									return chr.siteId==placement.siteId;
								});
																
								var indexPla=_.findIndex($scope.siteList[indexSite].redirectList,function(chr){
									return chr.placementId==placement.placementId;
								});
								
								for(var i=0;i<result.data.redTagList.length;i++){
									
							
									((($scope.siteList)[indexSite].redirectList)[indexPla].redTagList)[i].tagType=result.data.redTagList[i].tagType;
									((($scope.siteList)[indexSite].redirectList)[indexPla].redTagList)[i].tagValue=result.data.redTagList[i].tagValue;
									((($scope.siteList)[indexSite].redirectList)[indexPla].redTagList)[i].tagDescription=result.data.redTagList[i].tagDescription;
									((($scope.siteList)[indexSite].redirectList)[indexPla].redTagList)[i].modifyByName=result.data.redTagList[i].modifyByName;
									((($scope.siteList)[indexSite].redirectList)[indexPla].redTagList)[i].lastupdatedDate=result.data.redTagList[i].lastupdatedDate;
									((($scope.siteList)[indexSite].redirectList)[indexPla].redTagList)[i].status=((!!result.data.redTagList[i].status)?result.data.redTagList[i].status.toUpperCase():"");
									((($scope.siteList)[indexSite].redirectList)[indexPla].redTagList)[i].show=true;	
									if(!!result.data.redTagList[i].status&&result.data.redTagList[i].status.toUpperCase().indexOf("VALI")>-1){
										(($scope.siteList)[indexSite].redirectList)[indexPla].redTagList[i]["bgcolor"]="#CCFFD5";	
										(($scope.siteList)[indexSite].redirectList)[indexPla].redTagList[i]["message"]="VALID"+" by "+(($scope.siteList)[indexSite].redirectList)[indexPla].redTagList[i].modifyByName+" at "+(($scope.siteList)[indexSite].redirectList)[indexPla].redTagList[i].lastupdatedDate;

									}else{
										if(!!result.data.redTagList[i].status&&result.data.redTagList[i].status.toUpperCase().indexOf("DELETE")>-1){
											(($scope.siteList)[indexSite].redirectList)[indexPla].redTagList[i]["bgcolor"]="#FFCCCC";
											(($scope.siteList)[indexSite].redirectList)[indexPla].redTagList[i]["message"]="DELETE"+" by "+(($scope.siteList)[indexSite].redirectList)[indexPla].redTagList[i].modifyByName+" at "+(($scope.siteList)[indexSite].redirectList)[indexPla].redTagList[i].lastupdatedDate;
										}else{
											if(!!result.data.redTagList[i].status&&result.data.redTagList[i].status.toUpperCase().indexOf("OOS")>-1){
												(($scope.siteList)[indexSite].redirectList)[indexPla].redTagList[i]["bgcolor"]="#ff6e00";	
												(($scope.siteList)[indexSite].redirectList)[indexPla].redTagList[i]["message"]="SET OUT OF SCOPE"+" by "+(($scope.siteList)[indexSite].redirectList)[indexPla].redTagList[i].modifyByName+" at "+(($scope.siteList)[indexSite].redirectList)[indexPla].redTagList[i].lastupdatedDate;

											}else{
												(($scope.siteList)[indexSite].redirectList)[indexPla].redTagList[i]["bgcolor"]="#FFF4B2";
												(($scope.siteList)[indexSite].redirectList)[indexPla].redTagList[i]["message"]="waiting for validation";
											}
											
										}
									}
																	
																	
								}
								
								deselectTagWhenChargeTag(placement.siteId);
								
								if(type == "loadTags"){
									$scope.loader=true;
								}
								$scope.showPlNb=false;
								clearTimeout(mytimer);
								clearInterval(myInterval);
								progress2=0;
								progress1=0;
								(($scope.siteList)[indexSite].redirectList)[indexPla].charged=true;
								
								resolve(true);
							} else {
								reject(true);
							}
						}
				
					);
				});					
			};
			
			
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
				
			};
		
			
		$scope.filterPla=function(siteId,filterPlacement){
				
				ga('send', 'event', 'button', 'click', 'filter placement');	

				var fiterStatus = getFilterStatus();
				var index=_.findIndex($scope.siteList,function(chr){
					return chr.siteId==siteId;
				});

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
		};
				
				
				
				$scope.changeArrow=function(id){
					
					if($('#'+id).find('span').hasClass('glyphicon-chevron-down')){
						$('#'+id).find('span').removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-up');
					}else{
						if($('#'+id).find('span').hasClass('glyphicon-chevron-up')){
							$('#'+id).find('span').removeClass('glyphicon-chevron-up').addClass('glyphicon-chevron-down');
						}
					}
				};
	
			
			//click the rechargerButton
			$scope.validTag=function(placement,tagType,siteName){
				
				var confirmed=true;
				
				if(tagType==='PLACEMENT_TAG_INSTREAM_VIDEO_PREFETCH'
					||(tagType==='PLACEMENT_TAG_TRACKING_JAVASCRIPT'&& (($('textarea#'+placement.placementId+tagType).val()).match(/https/g)||[]).length>1)){
					confirmed=confirm("Attention: This tag could be non considered by Affiperf MetaDSP!")
				}
				
				if(confirmed){
					
					console.log("click validTag");
					
					ga('send', 'event', 'button', 'click', 'Validation Tag:pId'+placement.placementId+' '+tagType);
					//get the tag value by realtime
					var tagV=$('textarea#'+placement.placementId+tagType).val();
					var tagD=$('textarea#desc_'+placement.placementId+tagType).val();
					var tag={
						"camId":camID,
						"placementId":placement.placementId,
						"tagType":tagType,
						"tagValue":tagV,
						"status":"VALID",
						"tagDescription":tagD,
						"adServer":1
					};
					
					var placementNew={
						"camId":camID,
						"placementId":placement.placementId,
						"placementName":placement.placementName,
						"placementSize":placement.placementSize,
						"siteName":placement.siteName,
						"siteId":placement.siteId,
						"date_begin":placement.date_begin,
						"date_end":placement.date_end,
						"status":"VALID",
						"adServer":1
					};
									
					saveSingleTagTM.saveSingleTag($scope.token,tag,placementNew).then(
							 function(data, status, headers, config){
								 
								    if(data.status == 200){
								    	
								    if(placementNew.status=='VALID'){

								    	setTagColor(placement.placementId,tagType,"#CCFFD5");
									
								    	setTagMessageByValid(placement.placementId,tagType,login,'VALID');
									
								    	placement.status="VALID";
									
								    	placement["bgcolor"]="#CCFFD5";
								    }
								    
								    if(placementNew.status=='OOS'){
							    		
							    		setTagColor(placement.placementId,tagType,"#ff6e00");
										
								    	setTagMessageByValid(placement.placementId,tagType,login,'OOS');
									
								    	placement.status="OOS";
									
								    	placement["bgcolor"]="#ff6e00";
							    	}

									
									//call set log
									saveLog(camID,login,'VALID',placement,siteName,tagType,tagV);
								    } else {
								    	setTagColor(placement.placementId,tagType,"#FFCCCC");
										setTimeout(function(){
											setTagColor(placement.placementId,tagType,"#FFF4B2");
											$scope.$apply();
										}, 5000);
								    	//alert("Tag is not valided correctly, please revalid.");
								    }
							  }
						);
					
				}
				
				
			}
			
			
			$scope.validSite=function(site,statusToChange){
				
				$scope.loader=false;
				
				progress2=0;
				progress1=0;
				clearInterval(myInterval);
				$scope.showPlNb=true;
				$scope.placementNB=site.redirectList.length;
				$scope.indicationText=" placements are in process of validating......";
				$('.progress-bar').css('width', 0+'%').attr('aria-valuenow', 0);  
				
				if(site.allLoaded!=true){
					var allLoaded = [];
					for(var i=0;i<site.redirectList.length;i++){
						
						if(site.redirectList[i].charged == false && site.redirectList[i].show2 == true){
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
				
			    var interval = $interval(function testIdExist(){
			        if (allLoaded.length < site.redirectList.length){		        		
			        } else {			        	
			        	$interval.cancel(interval);
			        	site.allLoaded=true;

			        	validSiteBackend(site, statusToChange);
			        }		
			    }, 500);
				} else {
					validSiteBackend(site, statusToChange);
				}
			}
			
			
			function validSiteBackend(site,statusToChange){
				
				$scope.loader=false;
				
				var statusOrigin = site.satus;
				var camDCMToSave={
						"camId":$scope.camDCMId,
						"camName":$scope.camDCMName,
						"advertiserId":$scope.advertiserID,
						"networkId":$scope.netWID,
						"begindate":$scope.camBeginDate,
						"enddate":$scope.camEndDate									
					};
				
				saveCampaignGInfo.saveCamInfo(token,camDCMToSave);
				
				_.times(site.redirectList.length, function(m){
					
					if(site.redirectList[m].archived == false && site.redirectList[m].show2 == true){
						_.times(site.redirectList[m].redTagList.length, function(n){
						
							site.redirectList[m].redTagList[n]["toValid"] = site.redirectList[m].redTagList[n].show;
							site.redirectList[m].redTagList[n].adServer = 1;
							site.redirectList[m].redTagList[n].placementId = site.redirectList[m].placementId;
							site.redirectList[m].redTagList[n].status = statusToChange;
							site.redirectList[m].redTagList[n].tagValue =  $('#' + site.redirectList[m].placementId + site.redirectList[m].redTagList[n].tagType).val();
							site.redirectList[m].redTagList[n].tagDescription =  $('#desc_' + site.redirectList[m].placementId + site.redirectList[m].redTagList[n].tagType).val();
							});
					
						site.redirectList[m].camId = $scope.camDCMId;
						site.redirectList[m].adServer = 1;
						site.redirectList[m].login = login;
						site.redirectList[m].status = statusToChange;
					}
					
				});
			
				validSite.validSite(token, site).then(function(result){
					
						var siteValid = true;
						
						_.times(result.data.redirectList.length, function(n){
							
							if(result.data.redirectList[n].archived == false && result.data.redirectList[n].show2 == true){
							
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
				
				console.log(placement);
				
				$scope.loader=false;
				ga('send', 'event', 'button', 'click', 'VALID Placement:pId'+placement.placementId);
				
				var statusOrigin = placement.satus;
				var colorOrigin = placement.bgcolor;
				var camDCMToSave={
						"camId":$scope.camDCMId,
						"camName":$scope.camDCMName,
						"advertiserId":$scope.advertiserID,
						"networkId":$scope.netWID,
						"begindate":$scope.camBeginDate,
						"enddate":$scope.camEndDate									
					};
				
				saveCampaignGInfo.saveCamInfo(token,camDCMToSave);
				
				var confirmed=true;
				
				_.times(placement.redTagList.length, function(n){
					
					placement.redTagList[n]["toValid"] = placement.redTagList[n].show;
					placement.redTagList[n].adServer = 1;
					placement.redTagList[n].placementId = placement.placementId;
					placement.redTagList[n].status = statusToChange;
					
					if(statusToChange == "VALID"){
						if(placement.redTagList[n].tagType==='PLACEMENT_TAG_INSTREAM_VIDEO_PREFETCH'
							||(placement.redTagList[n].tagType==='PLACEMENT_TAG_TRACKING_JAVASCRIPT'&& ((placement.redTagList[n].tagValue).match(/https/g)||[]).length>1)){
							confirmed=confirm("Attention: There is tag which could not be considered by Affiperf MetaDSP!");
						}
					}
				});
				
				if(confirmed == true){
					placement.camId = $scope.camDCMId;
					placement.adServer = 1;
					placement.login = login;
					placement.status = statusToChange;

					validPlacement.validPlacement(token, placement).then(function(result){
						
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
						
						placement.redTagList = result.data.redTagList;
						
						var placementValid = true;
						_.times(placement.redTagList.length, function(n){
							
							if(placement.redTagList[n].wellSave == false){
								placementValid = false;
							}
						});
											
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
			}
			
			
			$scope.checkClick=function(tagType,value,siteId){				
				var type;
				
				switch(tagType){
				case "JS":type="PLACEMENT_TAG_JAVASCRIPT";
					break;
				case "IJ":type="PLACEMENT_TAG_IFRAME_JAVASCRIPT";
					break;
				case "IR":type="PLACEMENT_TAG_INTERNAL_REDIRECT";
					break;
				case "CT":type="PLACEMENT_TAG_CLICK_COMMANDS";
					break;
				case "ST":type="PLACEMENT_TAG_STANDARD";
					break;
				case "II":type="PLACEMENT_TAG_TRACKING";
			        break;
		        case "IIF":type="PLACEMENT_TAG_TRACKING_IFRAME";
			        break;
		        case "CL":type="PLACEMENT_TAG_TRACKING_JAVASCRIPT";
			        break;
		        case "PF":type="PLACEMENT_TAG_INSTREAM_VIDEO_PREFETCH";
		            break;
		        case "JSLG": type="PLACEMENT_TAG_JAVASCRIPT_LEGACY";
		    		break;
		        case "IJLG": type="PLACEMENT_TAG_IFRAME_JAVASCRIPT_LEGACY";
	    			break;
	    			
				
				}
				
				for(var i=0;i<$scope.siteList.length;i++){
					
					if($scope.siteList[i].siteId===siteId){
						for(var j=0;j<$scope.siteList[i].redirectList.length;j++){
							for(var k=0;k<($scope.siteList[i].redirectList)[j].redTagList.length;k++){
								if((($scope.siteList[i].redirectList)[j].redTagList)[k].tagType===type){
									(($scope.siteList[i].redirectList)[j].redTagList)[k].show=value;
								}
							}
						}
					}
				}
				
			}
			
			$scope.clickHelpMe=function(){
				$window.open(envconf.backend+"/help?t="+token);
			}
	 });
	 
	
		function deselectTagWhenChargeTag(siteId){
			
			if(!$('#' + siteId + '_cbJS').is(':checked')){
				$scope.checkClick('JS',false,siteId);
			}
			if(!$('#' + siteId + '_cbIJ').is(':checked')){
				$scope.checkClick('IJ',false,siteId);
			}
			if(!$('#' + siteId + '_cbIR').is(':checked')){
				$scope.checkClick('IR',false,siteId);
			}
			if(!$('#' + siteId + '_cbCT').is(':checked')){
				$scope.checkClick('CT',false,siteId);
			}
			if(!$('#' + siteId + '_cbST').is(':checked')){
				$scope.checkClick('ST',false,siteId);
			}
			if(!$('#' + siteId + '_cbII').is(':checked')){
				$scope.checkClick('II',false,siteId);
			}
			if(!$('#' + siteId + '_cbIIF').is(':checked')){
				$scope.checkClick('IIF',false,siteId);
			}
			if(!$('#' + siteId + '_cbCL').is(':checked')){
				$scope.checkClick('CL',false,siteId);
			}
			if(!$('#' + siteId + '_cbPF').is(':checked')){
				$scope.checkClick('PF',false,siteId);
			}
			if(!$('#' + siteId + '_cbJSLG').is(':checked')){
				$scope.checkClick('JSLG',false,siteId);
			}
			if(!$('#' + siteId + '_cbIJLG').is(':checked')){
				$scope.checkClick('IJLG',false,siteId);
			}
		}
	
	
	function replaceTagValue(pId,tType,tValue,tDesc,color,login,time,status){
		for(var i=0;i<$scope.siteList.length;i++){
			for(var j=0;j<($scope.siteList)[i].redirectList.length;j++){
				if(($scope.siteList)[i].redirectList[j].placementId===pId){
					for(var k=0;k<($scope.siteList)[i].redirectList[j].redTagList.length;k++){
						if(((($scope.siteList)[i].redirectList)[j].redTagList)[k].tagType===tType){
							((($scope.siteList)[i].redirectList)[j].redTagList)[k].tagValue=tValue;
							((($scope.siteList)[i].redirectList)[j].redTagList)[k].tagDescription=tDesc;
							((($scope.siteList)[i].redirectList)[j].redTagList)[k]["bgcolor"]=color;
							((($scope.siteList)[i].redirectList)[j].redTagList)[k].modifyByName=login;
							((($scope.siteList)[i].redirectList)[j].redTagList)[k].lastupdatedDate=time;
							((($scope.siteList)[i].redirectList)[j].redTagList)[k].status=status;
						}
					}
				}
			}
		}
	}
	
	function setTagColor(pId,tType,color){
		
		for(var i=0;i<$scope.siteList.length;i++){
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

	
	
	function saveLog(camID,login,status,placement,site,tagType,tagValue){
		saveLogAtTM.saveLog(camID,login,status,placement,site,tagType,tagValue,1);
	}
	
	function getLogByCampaign(getLogByCampaignId,camID){
		getLogByCampaignId.getCamAllLog(camID,1).then(
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
				
				for(var i=0;i < (($scope.siteList)[n].redirectList).length ; i++ ){
					
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
	
	function getRetriveDate(campaignId){
		getCamRetriveDate.getCamRetriveDate(camID,1).then(function(result){
			$scope.lastRetriveDate=result.data;
		});
	}
	
	$scope.synRetriveDate=function(){
		getRetriveDate($scope.camDCMId);
	}
	
	var getRetriveDateVar = setInterval(function(){ getRetriveDate($scope.camDCMId) }, 30000);
	
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
	
	$scope.logout=function(){
		$window.location.href=envconf.backend+"/#";
	};
	
}]);