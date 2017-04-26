(function() {
    'use strict';

    angular
        .module('dreadflicksApp')
        .controller('MovieDeleteController',MovieDeleteController);

    MovieDeleteController.$inject = ['$uibModalInstance', 'entity', 'Movie'];

    function MovieDeleteController($uibModalInstance, entity, Movie) {
        var vm = this;

        vm.movie = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Movie.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
