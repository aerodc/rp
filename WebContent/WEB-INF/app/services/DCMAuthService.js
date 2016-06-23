'use strict';
var wsrp = angular.module('wsrp');

wsrp.factory('DCMRequestURL',['$http','envconf',function($http,envconf){
	return {
		getRequestURL:function(){
			return $http.get(envconf.backend+"/dcm/geturl1");
		}
	}
}]);


wsrp.factory('DCMGetUserProfil',['$http','envconf',function($http,envconf){
	return {
		getUserProfile:function(code){
			return $http.get(envconf.backend+"/dcm/getuserprofile/"+code);
		}
	}
}]);


wsrp.factory('SizmekClientToken',['$http','envconf',function($http,envconf){
	return {
		getClientToken:function(){

			return $http.get(envconf.backend+"/sizmek/getClientToken/");
		}

	}

 }]);