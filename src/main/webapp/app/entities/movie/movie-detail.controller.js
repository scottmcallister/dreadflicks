(function() {
    'use strict';

    angular
        .module('dreadflicksApp')
        .controller('MovieDetailController', MovieDetailController);

    MovieDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Movie'];

    function MovieDetailController($scope, $rootScope, $stateParams, previousState, entity, Movie) {
        var vm = this;

        vm.movie = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('dreadflicksApp:movieUpdate', function(event, result) {
            vm.movie = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
