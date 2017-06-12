(function() {
    'use strict';

    angular
        .module('dreadflicksApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('movie-list', {
            parent: 'entity',
            url: '/movie-list?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'MovieLists'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/movie-list/movie-lists.html',
                    controller: 'MovieListController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }]
            }
        })
        .state('movie-list-detail', {
            parent: 'movie-list',
            url: '/movie-list/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'MovieList'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/movie-list/movie-list-detail.html',
                    controller: 'MovieListDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'MovieList', function($stateParams, MovieList) {
                    return MovieList.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'movie-list',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('movie-list-detail.edit', {
            parent: 'movie-list-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/movie-list/movie-list-dialog.html',
                    controller: 'MovieListDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['MovieList', function(MovieList) {
                            return MovieList.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('movie-list-detail.delete', {
            parent: 'movie-list-detail',
            url: '/detail/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/movie-list/movie-list-delete-dialog.html',
                    controller: 'MovieListDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['MovieList', function(MovieList) {
                            return MovieList.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('movie-list', null, { reload: 'movie-list' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('movie-list.new', {
            parent: 'movie-list',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/movie-list/movie-list-dialog.html',
                    controller: 'MovieListDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('movie-list', null, { reload: 'movie-list' });
                }, function() {
                    $state.go('movie-list');
                });
            }]
        })
        .state('movie-list.edit', {
            parent: 'movie-list',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/movie-list/movie-list-dialog.html',
                    controller: 'MovieListDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['MovieList', function(MovieList) {
                            return MovieList.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('movie-list', null, { reload: 'movie-list' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('movie-list.delete', {
            parent: 'movie-list',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/movie-list/movie-list-delete-dialog.html',
                    controller: 'MovieListDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['MovieList', function(MovieList) {
                            return MovieList.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('movie-list', null, { reload: 'movie-list' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
