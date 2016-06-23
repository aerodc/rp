'use strict';
var wsrp = angular.module('wsrp');

wsrp.directive('pageSelect', function() {
    return {
      restrict: 'E',
      template: '<input style="width: 50px; margin: -5px 0; text-align: center;" type="text" class="select-page" ng-model="inputPage" ng-change="selectPage(inputPage)">',
      link: function(scope, element, attrs) {
        scope.$watch('currentPage', function(c) {
          scope.inputPage = c;
        });
      }
    }
  }
);