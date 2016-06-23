'use strict';
var wsrp = angular.module('wsrp');

wsrp.factory('SizmekRequestToken',['$http','envconf',function($http,envconf){
	return {
		getRequestToken:function(login,password){
			return $http.get(envconf.backend+"/sizmek/gettoken/" + login + '/' + password);
		}
	}
}]);

wsrp.factory('SizmekUrl',['$http','envconf',function($http,envconf){
	return {
		getUrl:function(login,password){
			return $http.get(envconf.backend+"/sizmek/geturl/");
		}
	}
}]);

wsrp.factory('SizmekClientToken',['$http','envconf',function($http,envconf){
	return {
		getClientToken:function(camId){
			return $http.get(envconf.backend+"/sizmek/getClientToken/"+camId);
		}
	}
}]);