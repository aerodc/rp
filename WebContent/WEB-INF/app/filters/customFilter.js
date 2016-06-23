'use strict';
var wsrp = angular.module('wsrp');

wsrp.filter('optionFilter1',function(){
	return function(value){
		var nw = value.networkId+' : '+value.networkName;
		
		if(null !== value.subAccountName){
			nw = nw +" : "+value.subAccountName;
		}
		return nw;
	}
});

wsrp.filter('tagFilter', function() {
	
	  return function(input) {
		    if(input == 'PLACEMENT_TAG_TRACKING'){
		    	return 'IMPRESSION IMAGE';
		    } else if(input == 'PLACEMENT_TAG_TRACKING_IFRAME') {
		    	return 'IMPRESSION IFRAME';
		    } else if(input == 'PLACEMENT_TAG_TRACKING_JAVASCRIPT'){
		    	return 'TRACKING CLICK';
		    } else {
		        return input.substring(14, input.length);
		    }
		        
}});