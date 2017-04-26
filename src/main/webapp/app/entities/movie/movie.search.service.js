(function() {
    'use strict';

    angular
        .module('dreadflicksApp')
        .factory('MovieSearch', MovieSearch);

    MovieSearch.$inject = ['$resource'];

    function MovieSearch($resource) {
        var resourceUrl =  'api/_search/movies/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
