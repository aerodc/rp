'use strict';
var wsrp = angular.module('wsrp');

wsrp.directive('messagefadeout2',['$animate','$timeout',function($animate,$timeout){
	
	return function(scope,element,attrs){
		scope.$watch('redtag.message',function(newValue,oldValue){
			
			if (!isRealValue(newValue)||!isRealValue(oldValue)||newValue.trim() === oldValue.trim()) {
					return;
			}else{				
				 $animate.removeClass(element, 'message-animation-return');
				 $animate.addClass(element, 'message-animation');
				 $timeout(function(){
					 $animate.removeClass(element, 'message-animation');
					 $animate.addClass(element, 'message-animation-return');
				 },1000);
			}
			 
		});
	};
	
	
	function isRealValue(obj){
		 if( obj && obj !== "null" && obj!== "undefined" && obj!== ""){
			 return true;
		 } else {
			 return false;
		 }
	}
}]);


wsrp.directive('messagefadeout3',['$animate','$timeout',function($animate,$timeout){
	
	return function(scope,element,attrs){
				
		scope.$watch('camDesc',function(newValue,oldValue){
			
			if (!isRealValue(newValue)||!isRealValue(oldValue)||newValue.trim() === oldValue.trim()) {
					return;
			}else{	
				 scope.descbgcolor='#09D084';
				 $timeout(function(){
					 scope.descbgcolor='white';
				 },1500);
			}
			 
});
	
};
	
	
	function isRealValue(obj){
		 if( obj && obj !== "null" && obj!== "undefined" && obj!== ""){
			 return true;
		 } else {
			 return false;
		 }
	}
}]);