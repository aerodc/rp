'use strict';
var wsrp = angular.module('wsrp');
wsrp.controller('indexdcmcontroller',['$scope','DCMGetUserProfil','$location','getCamsDCMByNetWork','getAdvNameDCM','getTMCam','getCamDCMById','getCamDesc','getCamRetriveDate','saveCamDesc',
                                      'getCampaignWithAllRedirectsDCM','getSingleTagTM','saveSingleTagTM','getLogByCampaignId','saveLogAtTM',
                                      'getSingleTagDCM','$window','getPlNBs','saveCampaignGInfo','rechargePlaTags','envconf','$interval','$q','$cookieStore','savePlacementGInfo',
                                      'getPlaTagDCM','validPlacement','validSite','getSiteDCM',
                                      function($scope,DCMGetUserProfil,$location,getCamsDCMByNetWork,getAdvNameDCM,getTMCam,getCamDCMById,getCamDesc,getCamRetriveDate,saveCamDesc,
                                    		  getCampaignWithAllRedirectsDCM,getSingleTagTM,saveSingleTagTM,getLogByCampaignId,saveLogAtTM,
                                    		  getSingleTagDCM,$window,getPlNBs,saveCampaignGInfo,rechargePlaTags,envconf,$interval,$q,$cookieStore,savePlacementGInfo,
                                    		  getPlaTagDCM,validPlacement,validSite,getSiteDCM){
	
	
	var arrayUrl=$location.absUrl().split('?');
	
	var code =((arrayUrl[1].split('&'))[0].split('='))[1];
	
	code=window.btoa(code);
	
	$scope.nwSelected;
	
	$scope.camSelected={};
	
	$scope.camSelected.hasModifToRetrive=false;
	
	$scope.camSelAdvName;
	
	$scope.camTM;
	
	$scope.flag1=true;
		
	$scope.showPlNb=false;
	
	$scope.selectNetwork=false;
		
	$scope.descbgcolor="white";
	
	var camID,login;
	
	var token, profileId, nwId,myInterval;
	
	var token2;
	
	var request1;
	
	var progress2=0;
	var progress1=0;
	$scope.loader=false;	
	
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
	 cleanCookieTimer();
	 $cookieStore.put("mytimer",JSON.stringify([]));
	 var loginTime = localStorage.getItem("loginTime");
	 
	 
	 if(isRealValue(loginTime)){
		 var date = new Date();
		 
		 if((date.getTime() - loginTime) > 60*60*1000){
				alert("Your session has expired and please reconnect.");
				cleanCookieTimer();
				localStorage.clear();
				$window.location.href=envconf.backend+"/#";
		 } else {
			 token2 = localStorage.getItem("token2");
			 var networkArr = localStorage.getItem("networkList").split('***');
			 $scope.networkList = [];
			 for(var i=0;i<networkArr.length;i++){
				 if(isRealValue(networkArr[i])){
					 $scope.networkList.push(JSON.parse(networkArr[i]));
				 }
			 }
			 $scope.loader=true;
		 }
	 } else if(isRealValue(code)) {
		 DCMGetUserProfil.getUserProfile(code).then(function(result){
			 $scope.loader=true;
			 if(isRealValue(result.data.token)){
				 $scope.networkList=result.data.userProfileList;
				 console.log("test: "+ result.data.userProfileList);
				 token2=result.data.token;
				 var date = new Date();
				 localStorage.setItem("loginTime", date.getTime());
				 localStorage.setItem("token2", token2);
				 for(var i=0;i<$scope.networkList.length;i++){
					 localStorage.setItem("networkList", (localStorage.getItem("networkList")==null?"":localStorage.getItem("networkList")  + JSON.stringify($scope.networkList[i]) + "***"));
				 }
			 }else{
				 alert("Your session has expired and please reconnect.");
				 $window.location.href=envconf.backend+"/#";
			 }
		});
	} else {
		cleanCookieTimer();
		localStorage.clear();
		alert("You don't have any access to Doubleclick Campaign Manager.");
		$window.location.href=envconf.backend+"/#";
	}
		
	angular.element(document).ready(function () {
		
		$('#retriveDate').tooltip();
		
        $("#selectCamList").select2({
	        escapeMarkup: function(m) { return m; }
	    });
				
		$("#bt1").trigger("click");
		$("#divSites").css({top:'190px'});
		
		$scope.clickHelpMe=function(){
			$window.open(envconf.backend+"/help?t="+token2);
		}
	});
	
	$scope.selectNW=function(item){
		
		cleanCookieTimer();
		clearInterval(myInterval);

		initPLbuttons();
		
		progress2=0;
		progress1=0;
		$('.progress-bar').css('width', 0+'%').attr('aria-valuenow', 0);  
		
		$("#selectCamList").select2("destroy");
        $("#selectCamList").select2({
	        escapeMarkup: function(m) { return m; }
	    });
		
		$scope.camSelected=null;
		$scope.camSelAdvName=null;
		$scope.camTM=null;
		$scope.flag1=true;
		login=item.userName.split("@")[0];
		if($('#searchById').val()!==''){
			$('#searchById').val('');
		}
		
		 $scope.loader=false;
		 $scope.siteList=null;
		 $scope.flag1=true;
		 $scope.selectNetwork=true;
		 $scope.netWID=item.networkId;
		 
		getCamsDCMByNetWork.getCamsDCM(item.token,item.profileId,item.networkId).then(function(result){
			
			$scope.camSelected=null;
			token=item.token;
			profileId=item.profileId;
			nwId=item.networkId;
			$scope.camList=result.data;
			$scope.camListCopy=result.data;
			$scope.loader=true;
			if(!$scope.camList||$scope.camList.length===0){
				alert("There might be an error by calling DCM API and please reconnect.");
				$window.location.href=envconf.backend+"/#";
				cleanCookieTimer();
			}
		},function(resKO){
			alert("Your session has expired and please reconnect.");
			$window.location.href=envconf.backend+"/#";
			cleanCookieTimer();
		});
		
	};
	
	$scope.selectCAM=function(){
		
		progress2=0;
		progress1=0;
		cleanCookieTimer();
		clearInterval(myInterval);
		
		initPLbuttons();
		$scope.indicationText=" placements for this campaign are charging from DCM......";
		$('.progress-bar').css('width', 0+'%').attr('aria-valuenow', 0);  
		
		ga('send', 'pageview');
		
		$scope.camSelected = JSON.parse($scope.camSelected);
	
		var item = $scope.camSelected;
		$scope.flag1=true;
		$scope.camTM=null;
		$scope.camSelAdvName=null;
		$scope.siteList=null;
		camID=item.camId;
				
		$scope.camDCMId=item.camId;
		$scope.camDCMName=item.camName;
		$scope.advertiserID=item.advertiserId;
		$scope.camBeginDate=item.begindate;
		$scope.camEndDate=item.enddate;
		
		
		getRetriveDate(camID);
		
		var getRetriveDateVar = setInterval(function(){ getRetriveDate(camID) }, 30000);

		
		$('#searchById').val(camID);
		$scope.camSelected=item;
		getAdvNameDCM.getAdvName(token,profileId,nwId,item.advertiserId).then(function(result){
			$scope.camSelAdvName=result.data;
		});
		
		getTMCam.getCamTM(camID).then(function(result){
			$scope.camTM=result.data;			
		});
		
		getCamDesc.getCamDesc(camID,1).then(function(result){
			$scope.camDesc=result.data;
		});
		

		
		$scope.loader=false;

		///*****
		
		
		
		getPlNBs.getplnbs(token,camID,nwId,2,profileId).then(function(result){
			
			if(isRealValue(result)&&isRealValue(result.data)){
				$scope.placementNB=result.data;
				$scope.showPlNb=true;
				
				var mytimer1;
				
				if($scope.placementNB < 150){
					    mytimer1=setTimeout(function(){
						$scope.loader=true;
						$scope.showPlNb=false; 
						
						console.log("timeoutModal11");
						$('#timeoutModal').modal('show');
						request1.cancel();
						
					 },1000*($scope.placementNB+10));
				}
	
				var arr1=JSON.parse($cookieStore.get("mytimer"));
				arr1.push(mytimer1);
				$cookieStore.put("mytimer",JSON.stringify(arr1));
				
				myInterval=setInterval(function(){
					
					progress1=progress1+5;
					progress2=Math.round((progress1/$scope.placementNB)*100);
										
					$('.progress-bar').css('width', progress2+'%').attr('aria-valuenow', progress2);  
					
				},200);			
			}		
		});
		getSitesbyCam(token,camID,nwId,profileId);
	};
	
	
	$scope.setFlag1=function(){
	   $scope.flag1=true;
	   $("#selectCamList").select2({
	        escapeMarkup: function(m) { return m; }
	    });
	};
	
	
	$scope.searchCamById=function(){
		
		progress2=0;
		progress1=0;
		
		cleanCookieTimer();
		clearInterval(myInterval);
		$scope.indicationText=" placements for this campaign are charging from DCM......";
		$('.progress-bar').css('width', 0+'%').attr('aria-valuenow', 0);  		
		
		ga('send', 'pageview');
		
		var camId=$('#searchById').val();
				
		camID=camId;
				
		$scope.loader=false;
		
		$scope.siteList=null;
		
		getCamDCMById.getCamDCM(token,profileId,camId).then(function(result1){
			
			if(isRealValue(result1)&&result1.status!=404){
				$scope.flag1=false;				
				if ($("#selectCamList").data('select2'))
					$("#selectCamList").select2("destroy");
				
				$scope.camSelected=result1.data;
											
				$scope.camDCMId=camId;
				$scope.camDCMName=$scope.camSelected.camName;
				$scope.advertiserID=$scope.camSelected.advertiserId;
				$scope.camBeginDate=$scope.camSelected.begindate;
				$scope.camEndDate=$scope.camSelected.enddate;
				
				getRetriveDate(camID);				
				var getRetriveDateVar = setInterval(function(){ getRetriveDate(camID) }, 30000);
				
				getAdvNameDCM.getAdvName(token,profileId,nwId,$scope.camSelected.advertiserId).then(function(result3){
					$scope.camSelAdvName=result3.data;
				});
				
				getTMCam.getCamTM($scope.camSelected.camId).then(function(result2){
					$scope.camTM=result2.data;
				});
				
				getPlNBs.getplnbs(token,camID,nwId,2,profileId).then(function(result){
					
					if(isRealValue(result)&&isRealValue(result.data)){
						$scope.placementNB=result.data;
						$scope.showPlNb=true;
						
						var mytimer2;
						
						if($scope.showPlNb < 150){
							    mytimer2=setTimeout(function(){
								$scope.loader=true;
								$scope.showPlNb=false; 
								 
								$('#timeoutModal').modal('show');
								request1.cancel();
								
							 },1000*($scope.placementNB+10));
						}
						
						var arr2=JSON.parse($cookieStore.get("mytimer"));
						arr2.push(mytimer2);
						$cookieStore.put("mytimer",JSON.stringify(arr2));
						
						myInterval=setInterval(function(){
							
							progress1=progress1+4;
							progress2=Math.round((progress1/$scope.placementNB)*100);
														
							$('.progress-bar').css('width', progress2+'%').attr('aria-valuenow', progress2);  
							
						},200);
						
					}
					
				});
				
				getSitesbyCam(token,$scope.camSelected.camId,nwId,profileId);
				
			}else{
				$scope.loader=true;
				$scope.showPlNb=false;
				progress2=0;
				progress1=0;
			}
			
		},function(err){
			$scope.loader=true;
			$scope.showPlNb=false;
			if(isRealValue(err)&&err.status===404){
				alert("Campaign with the ID "+ camId+" can't be found");
			} 
			else if(isRealValue(err)&&err.status===400) {
				
		        var camListCopy1 = [];
				for(var k=0;k<$scope.camListCopy.length;k++){
					    
					   if($scope.camListCopy[k].camName.indexOf(camId)>-1){	
						   camListCopy1[camListCopy1.length] = $scope.camListCopy[k];
						}
				}
				if(camListCopy1.length > 0){
					$scope.camList = camListCopy1;
					$scope.loader=true;
					$scope.showPlNb=false;
					progress2=0;
					progress1=0;
				} else {
					$scope.camList = $scope.camListCopy;
					$scope.loader=true;
					$scope.showPlNb=false;
					progress2=0;
					progress1=0;
				    alert("Campaign can't be found");
				}
			}
		});
	};
	
	
	$scope.saveCamDesc=function(){
		var desc= $('#descId').val();
		var camId=$('#searchById').val();
		saveCamDesc.saveCamDesc(camId,desc,1);
		$scope.camDesc=desc;
		//$scope.descbgcolor="#09D084";
		ga('send', 'event', 'button', 'click', 'save description');	
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
								saveSingleTagTM.saveSingleTag(token,tag,placementNew);
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
	
	$scope.resetTag=function(placementId,tagType,recharge){

		var confirmed = true;
		
		if(recharge == true){
			confirmed = confirm("Recharge the Tag?");
		}
		if(confirmed){
			ga('send', 'event', 'button', 'click', 'Recharge Tag:pID'+placementId+' '+tagType);
			$scope.loader=false;
		            //call dcm service to recharge tag
				getSingleTagDCM.getTagDCM(token,camID,placementId,tagType,$scope.netWID,2,profileId).then(
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
			
			var index=_.findIndex($scope.siteList,function(chr){
				return chr.siteId==site.siteId;
			});
			
			getSiteDCM.getSiteDCM(token,camID,$scope.netWID,site.siteId,2,profileId).then(function(result){
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
						getPlaTagDCM.getPlaTagDCM(token,camID,$scope.netWID,placement.placementId,2,profileId).then(
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
					getPlaTagDCM.getPlaTagDCM(token,camID,$scope.netWID,placement.placementId,2,profileId).then(
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
	
	$scope.toggleList=function(){
		$scope.flag1=true;
		$("#selectCamList").select2({
	        escapeMarkup: function(m) { return m; }
	    });
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

		cleanCookieTimer();
		
		ga('send', 'event', 'button', 'click', 'filter placement');	
		
		var fiterStatus = getFilterStatus();
		console.log(fiterStatus);
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
	
	}
	
	
	$scope.loadPlacementTags=function(placement,type){
		
	   return $q(function(resolve, reject) {
		   
			$scope.loader=false;	
			rechargePlaTags.rechargePlaTags(token,camID,$scope.netWID,placement.placementId,2,profileId).then(
					function(result){
						if(isRealValue(result.data)){
						
							var indexSite=_.findIndex($scope.siteList,function(chr){
								return chr.siteId==placement.siteId;
							});
															
							var indexPla=_.findIndex($scope.siteList[indexSite].redirectList,function(chr){
								return chr.placementId==placement.placementId;
							});
							
							for(var i=0;i<result.data.redTagList.length;i++){
						
								var tagTypetmp=(placement.redTagList)[i].tagType=result.data.redTagList[i].tagType;								
								
								(placement.redTagList)[i].tagValue=result.data.redTagList[i].tagValue;								
								
								$('#'+placement.placementId+tagTypetmp).val((placement.redTagList)[i].tagValue);
								
								(placement.redTagList)[i].tagDescription=result.data.redTagList[i].tagDescription;
								
								$('#desc_'+placement.placementId+tagTypetmp).val((placement.redTagList)[i].tagDescription);

								(placement.redTagList)[i].modifyByName=result.data.redTagList[i].modifyByName;
								(placement.redTagList)[i].lastupdatedDate=result.data.redTagList[i].lastupdatedDate;
								(placement.redTagList)[i].status=((!!result.data.redTagList[i].status)?result.data.redTagList[i].status.toUpperCase():"");
								(placement.redTagList)[i].show=true;
								
								if(!!result.data.redTagList[i].status&&result.data.redTagList[i].status.toUpperCase().indexOf("VALI")>-1){
									(placement.redTagList)[i]["bgcolor"]="#CCFFD5";	
									(placement.redTagList)[i]["message"]="VALID"+" by "+(placement.redTagList)[i].modifyByName+" at "+(placement.redTagList)[i].lastupdatedDate;

								}else{
									if(!!result.data.redTagList[i].status&&result.data.redTagList[i].status.toUpperCase().indexOf("DELETE")>-1){
										(placement.redTagList)[i]["bgcolor"]="#FFCCCC";
										(placement.redTagList)[i]["message"]="DELETE"+" by "+(placement.redTagList)[i].modifyByName+" at "+(placement.redTagList)[i].lastupdatedDate;
									}else{
										if(!!result.data.redTagList[i].status&&result.data.redTagList[i].status.toUpperCase().indexOf("OOS")>-1){
											(placement.redTagList)[i]["bgcolor"]="#ff6e00";	
											(placement.redTagList)[i]["message"]="SET OUT OF SCOPE"+" by "+(placement.redTagList)[i].modifyByName+" at "+(placement.redTagList)[i].lastupdatedDate;

										}else{
											(placement.redTagList)[i]["bgcolor"]="#FFF4B2";
											(placement.redTagList)[i]["message"]="waiting for validation";
										}
									}
								}																															
							}
							
							deselectTagWhenChargeTag(placement.siteId);
							
							
							
							if(type == "loadTags"){
								$scope.loader=true;
							}
							placement.charged=true;	
							
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
		
		var confirmed=true;
				
		if(tagType==='PLACEMENT_TAG_INSTREAM_VIDEO_PREFETCH'
			||(tagType==='PLACEMENT_TAG_TRACKING_JAVASCRIPT'&& (($('textarea#'+placement.placementId+tagType).val()).match(/https/g)||[]).length>1)){
			confirmed=confirm("Attention: This tag could be non considered by Affiperf MetaDSP!")
		}
		
		if(confirmed){
			
			ga('send', 'event', 'button', 'click', 'Validation Tag:pId'+placement.placementId+' '+tagType);
			
			var camDCMToSave={
					"camId":$scope.camDCMId,
					"camName":$scope.camDCMName,
					"advertiserId":$scope.advertiserID,
					"networkId":$scope.netWID,
					"begindate":$scope.camBeginDate,
					"enddate":$scope.camEndDate,
					"adServer":'DCM'
				};
			
			
			saveCampaignGInfo.saveCamInfo(token,camDCMToSave);
			
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
							
			$scope.tagValid=false;
			saveSingleTagTM.saveSingleTag(token,tag,placementNew).then(
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
							saveLog(camID,login,placementNew.status,placement,siteName,tagType,tagV);
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
	}
	
	$scope.validSite=function(site,statusToChange){

		$scope.loader=false;
		
		progress2=0;
		progress1=0;
		cleanCookieTimer();
		clearInterval(myInterval);
		$scope.showPlNb=true;
		$scope.placementNB=site.redirectList.length;
		$scope.indicationText=" placements are in process of validating......";
		$('.progress-bar').css('width', 0+'%').attr('aria-valuenow', 0);  
		
		myInterval=setInterval(function(){
			progress1=progress1+5;
			progress2=Math.round((progress1/site.redirectList.length)*100);			
			$('.progress-bar').css('width', progress2+'%').attr('aria-valuenow', progress2); 
		},1500);
		
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
	        if (!(allLoaded.length < site.redirectList.length)){
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
					   setTimeout(function(){
						    placement["bgcolor"]=colorOrigin;
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
	
	function replaceTagValue(pId,tType,tValue,tDesc,login,time){
		for(var i=0;i<$scope.siteList.length;i++){
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
	
	function getRetriveDate(campaignId){
			getCamRetriveDate.getCamRetriveDate(camID,1).then(function(result){
				$scope.camSelected.lastRetriveDate=result.data;
			});
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
	
	function getSitesbyCam(token,camId,networkId,profileID){
		
		request1=getCampaignWithAllRedirectsDCM.getCamAllRedDCM(token,camId,networkId,2,profileID);
		
		request1.promise.then(
				function(result2, status, headers, config){	
					
					if(result2.status == 200){
					if(isRealValue(result2) && isRealValue(result2.data) && isRealValue(result2.data.siteList)){
						result2=result2.data;
						
						$scope.camSelected.lastRetriveDate=result2.lastRetriveDate;
						$scope.camSelected.hasModifToRetrive=result2.hasModifToRetrive;
						
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
							cleanCookieTimer();
							clearInterval(myInterval);
							progress2=0;
							progress1=0;
							}
							
						
						} else {
							$scope.loader=true;
							$scope.showPlNb=false;
							cleanCookieTimer();
							clearInterval(myInterval);
							progress2=0;
							progress1=0;
							alert("There is no data for this campaign, please verify campaign information.");
						}
						
				} else {
					$scope.loader=true;
					$scope.showPlNb=false;
					cleanCookieTimer();
					clearInterval(myInterval);
					progress2=0;
					progress1=0;
					alert("Server error: The adserver has not responded.");
				}
					}
			);
		
		getLogByCampaign(getLogByCampaignId,camId);
			
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
	
	
	function cleanCookieTimer(){
		
		if($cookieStore.get("mytimer")!=null){
			
			var arr =JSON.parse($cookieStore.get("mytimer"));
			for(var i=0; i< arr.length;i++){
				clearTimeout(arr[i]);
			}
		}
	}
	
	$scope.logout=function(){
		localStorage.clear();
		$window.location.href=envconf.backend+"/#";
	};
	
}]);