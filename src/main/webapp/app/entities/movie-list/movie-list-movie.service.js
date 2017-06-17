(function() {
    'use strict';
    angular
        .module('dreadflicksApp')
        .factory('MovieListMovie', MovieListMovie);

    MovieListMovie.$inject = ['$resource'];

    function MovieListMovie ($resource) {
        var resourceUrl =  'api/movie-lists/:list/movie/:movie';

        return $resource(resourceUrl, 
            { list: '@list', movie: '@movie' }, 
            { 
                'add': { method:'PUT' },
                'remove': { method: 'DELETE' }
            }
        );
    }
})();
