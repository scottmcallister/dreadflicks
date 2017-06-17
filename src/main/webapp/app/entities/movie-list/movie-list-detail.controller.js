(function() {
    'use strict';

    angular
        .module('dreadflicksApp')
        .controller('MovieListDetailController', MovieListDetailController);

    MovieListDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'MovieList', 'MovieListMovie', 'User', 'Movie'];

    function MovieListDetailController($scope, $rootScope, $stateParams, previousState, entity, MovieList, MovieListMovie, User, Movie) {
        var vm = this;

        vm.movieList = entity;
        vm.previousState = previousState.name;
        vm.removeMovie = function(movieId, listId) {
            var payload = { 
                'list': listId,
                'movie': movieId,
            };
            MovieListMovie.remove({}, payload, function(){
                var movies = vm.movieList.movies;
                for(var i = 0; i < movies.length; i++) {
                    if(movies[i].id === movieId) {
                        movies.splice(i, 1);
                        break;
                    }
                }
            });
        };

        var unsubscribe = $rootScope.$on('dreadflicksApp:movieListUpdate', function(event, result) {
            vm.movieList = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
