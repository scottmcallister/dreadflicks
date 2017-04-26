(function() {
    'use strict';
    angular
        .module('dreadflicksApp')
        .factory('Movie', Movie);

    Movie.$inject = ['$resource'];

    function Movie ($resource) {
        var resourceUrl =  'api/movies/:id';

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
