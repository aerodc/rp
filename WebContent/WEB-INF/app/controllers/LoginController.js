'use strict';
var wsrp = angular.module('wsrp');

wsrp.controller('loginController',['$scope','DCMRequestURL','$http','$window','$location','SizmekRequestToken','SizmekUrl','TokenStorage','envconf',
                                   function($scope,DCMRequestURL,$http,$window,$location,SizmekRequestToken,SizmekUrl,TokenStorage,envconf){
		
	$scope.adServerList=[
	                     {name:'DoubleClick',id:1},
	                     {name:'Sizmek',id:2}
	                     //{name:'DSP',id:0}
	                     ];
	
	$scope.adSelected;
	
	$scope.isShow=false;
	
	$scope.loginError = false;
	
	$scope.loader = true;
	
	$scope.errorMessage = 'Password or Login is not correct or your account has no access of this API';
	
	TokenStorage.clear();
	
	$scope.selectAds=function(value){
		
		if(!!value){
			
			if(value.id===1){
				$scope.isShow=false;
				DCMRequestURL.getRequestURL().then(
						function(result, status, headers, config){
							if(!!result){
								$window.location.href=result.data;
							}
						});
			}
			else{
				$scope.isShow=true;
			}
		}
	}
	
	$scope.loginAction=function(){
		
		$scope.loader = false;
		
		if($scope.adSelected.id == 2){
			SizmekRequestToken.getRequestToken($scope.login, $scope.password).then(function(result){
				
				if(result != null && result.data != null && result.data != 'false'){
				
					SizmekUrl.getUrl().then(function(result1){
					
						$scope.loader = true;
						var info = {
						    'login': $scope.login,
						    'token': result.data
						}
						TokenStorage.store(JSON.stringify(info));		
						$window.location.href=result1.data;
					});
				} else {
					if(result.data == 'time out'){
						$scope.errorMessage = 'Sizmek authentication time out';
					}
					
					$scope.loader = true;
					$scope.loginError = true;
				}
			});
		}
		else if($scope.adSelected.id == 0){
			
        	$http.get(envconf.backend+"/gettokenWithUrl/" + $scope.login + '/' +$scope.password).then(function(resultOK){
            	
        		if(resultOK.data.responseCode == 'OK'){
        			
        			TokenStorage.store(resultOK.data.token);
        			$window.location.href=resultOK.data.url;
        			
        		} else {
        			$scope.loginError = true;
        			$scope.errorMessage = 'Password or Login is not correct or your account has no access of TM API';
        		}   		
        	
        		$scope.loader = true;
        		
        	},function(resultKO){
        		console.log("get Token ko");
        	});
		}
	}
	
}]);