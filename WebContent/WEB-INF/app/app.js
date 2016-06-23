+'use strict';
var wsrp=angular.module('wsrp',[
'ngCookies',
'ngResource',
'ngAnimate',
'angularSpinner',
'smart-table'
]);

wsrp.constant('envconf',{
	'backend':'/wsrp'
	//'backend':''// only for production
});