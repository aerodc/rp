'use strict';
var wsrp = angular.module('wsrp');
wsrp.controller('dspcontroller',['$scope','$http','$location','TokenStorage','envconf','$window',
                                      function($scope,$http,$location,TokenStorage,envconf,$window){

	
	var tokenDSP = TokenStorage.retrieve();

	$scope.loader=true;

	$scope.adServerList=[
	                     {name:'DCM',id:1},
	                     {name:'Sizmek',id:2}
	                     ];
			
	angular.element(document).ready(function () {
				
		$("#bt1").trigger("click");
		$("#divSites").css({top:'190px'});
		$("#startDatepicker").datepicker();
		$("#endDatepicker").datepicker();

		$scope.fromTM = false;
		var token = TokenStorage.retrieve();
	});
	
	function getDateLongNumber(date){
		
		var dateArr = date.split('/');
		var newDate = new Date(dateArr[2] + '-' + dateArr[0] + '-' + dateArr[1]);	
		return newDate.getTime();
	}
	
	$scope.searchCamByDate=function(){
		
		if(!isRealValue($("#startDatepicker").val())){
			alert("Please select the start date");
		} 
		else if(!isRealValue($("#endDatepicker").val())){
			alert("Please select the end date");
		}
		else{
			var startTime = getDateLongNumber($("#startDatepicker").val());
			var endTime = getDateLongNumber($("#endDatepicker").val());
			
			console.log(startTime);
			
			if(startTime >= endTime){
				alert("Start date must before end date");
			} else {
				
				$scope.loader = false;
	        	$http.get(envconf.backend+"/getupdatedcams/" + tokenDSP + '/' + startTime + '/' + endTime).then(function(resultOK){

	    	    	$scope.rowCollection=resultOK.data.campaignsList;
	    	    	$scope.displayedCollection = [].concat($scope.rowCollection);
	        		$scope.loader = true;
	        		$scope.camRedirect=null;
	        		
	        	},function(resultKO){
	        		console.log("get getupdatedcams ko");
	        		$scope.loader = true;
	        		$scope.camRedirect=null;
	        	});
			}
		}
	}

	
	$scope.selectCam=function(row){

		$scope.loader = false;
    	$http.get(envconf.backend+"/getreds/" + tokenDSP + '/' + row.adserver + '/' + row.id).then(function(resultOK){

    		$scope.camRedirect=JSON.stringify(resultOK.data);
    		$scope.loader = true;
    		
    	},function(resultKO){
    		console.log("get getreds ko");
    		$scope.camRedirect=null;
    		$scope.loader = true;
    		alert("There is no redirects found for this campaign");
    	});
	};
	
	$scope.getRedirctByCamId=function(){
		
		if(!isRealValue($scope.campaignId) || isNaN($scope.campaignId)){
			alert("Please enter the campaign Id which should be number");
		}
		else if(!isRealValue($scope.adSelected)){
			alert("Please select the adserver");
		}
		else{
			$scope.loader = false;
	    	$http.get(envconf.backend+"/getreds/" + tokenDSP + '/' + $scope.adSelected.name + '/' + $scope.campaignId).then(function(resultOK){
	    		
	    		var campaign = {
	    			id: $scope.campaignId,
	    			name: resultOK.data.camName,
	    			adserver: $scope.adSelected.name
	    		};
	    		
    	    	$scope.rowCollection=[];
    	    	$scope.rowCollection.push(campaign);
    	    	$scope.displayedCollection = [].concat($scope.rowCollection);
	    		$scope.camRedirect=JSON.stringify(resultOK.data);
	    		$scope.loader = true;
	    		
	    	},function(resultKO){
	    		console.log("get getreds ko");
	    		$scope.camRedirect=null;
	    		$scope.loader = true;
	    		alert("There is no redirects found for this campaign");
	    	});
		}

	};
	
	$scope.logout=function(){
		$window.location.href=envconf.backend+"/#";
	};

	function isRealValue(obj){
		 if( obj && obj !== "null" && obj!== "undefined" && obj!== undefined && obj!== ""){
			 return true;
		 } else {
			 return false;
		 }
	}
}]);