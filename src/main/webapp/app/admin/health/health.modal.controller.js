(function() {
    'use strict';

    angular.module('dreadflicksApp')
        .controller('HealthModalController', HealthModalController);

    HealthModalController.$inject = ['$uibModalInstance', 'currentHealth', 'baseName', 'subSystemName'];

    function HealthModalController ($uibModalInstance, currentHealth, baseName, subSystemName) {
        var vm = this;

        vm.cancel = cancel;
        vm.currentHealth = currentHealth;
        vm.baseName = baseName;
        vm.subSystemName = subSystemName;

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();
