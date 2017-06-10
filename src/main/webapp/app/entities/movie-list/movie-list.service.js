(function() {
    'use strict';
    angular
        .module('dreadflicksApp')
        .factory('MovieList', MovieList);

    MovieList.$inject = ['$resource'];

    function MovieList ($resource) {
        var resourceUrl =  'api/movie-lists/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
