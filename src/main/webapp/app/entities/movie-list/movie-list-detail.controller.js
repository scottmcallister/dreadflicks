(function() {
    'use strict';

    angular
        .module('dreadflicksApp')
        .controller('MovieListDetailController', MovieListDetailController);

    MovieListDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'MovieList', 'User', 'Movie'];

    function MovieListDetailController($scope, $rootScope, $stateParams, previousState, entity, MovieList, User, Movie) {
        var vm = this;

        vm.movieList = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('dreadflicksApp:movieListUpdate', function(event, result) {
            vm.movieList = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
