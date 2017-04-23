(function() {
    'use strict';

    angular
        .module('dreadflicksApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('search', {
            parent: 'app',
            url: '/search',
            data: {
                authorities: []
            },
            views: {
                'content@': {
                    templateUrl: 'app/search/search.html',
                    controller: 'SearchController',
                    controllerAs: 'vm'
                }
            }
        });
    }
})();
