/* globals $ */
(function() {
    'use strict';

    angular
        .module('dreadflicksApp')
        .directive('img', img);

    function img () {
        return {
            restrict: 'E',        
            link: function (scope, element, attrs) {     
                // show an image-missing image
                element.error(function () {
                    var url = '/content/images/image-404-alt.png';
                    element.prop('src', url);
                });
            }
        };
    }
})();
