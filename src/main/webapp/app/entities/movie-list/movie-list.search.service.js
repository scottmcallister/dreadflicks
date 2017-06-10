(function() {
    'use strict';

    angular
        .module('dreadflicksApp')
        .factory('MovieListSearch', MovieListSearch);

    MovieListSearch.$inject = ['$resource'];

    function MovieListSearch($resource) {
        var resourceUrl =  'api/_search/movie-lists/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
