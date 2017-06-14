(function() {
    'use strict';

    angular
        .module('dreadflicksApp')
        .controller('MovieListDialogController', MovieListDialogController);

    MovieListDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'MovieList', 'User', 'Movie', 'Principal'];

    function MovieListDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, MovieList, User, Movie, Principal) {
        var vm = this;

        vm.movieList = entity;
        vm.clear = clear;
        vm.save = save;
        vm.users = User.query();
        vm.movies = Movie.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.movieList.id !== null) {
                MovieList.update(vm.movieList, onSaveSuccess, onSaveError);
            } else {
                MovieList.save(vm.movieList, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('dreadflicksApp:movieListUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
