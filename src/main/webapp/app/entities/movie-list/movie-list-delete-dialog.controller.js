(function() {
    'use strict';

    angular
        .module('dreadflicksApp')
        .controller('MovieListDeleteController',MovieListDeleteController);

    MovieListDeleteController.$inject = ['$uibModalInstance', 'entity', 'MovieList'];

    function MovieListDeleteController($uibModalInstance, entity, MovieList) {
        var vm = this;

        vm.movieList = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            MovieList.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
