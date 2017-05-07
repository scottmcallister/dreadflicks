(function() {
    'use strict';

    angular
        .module('dreadflicksApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('movie', {
            parent: 'entity',
            url: '/movie?page&sort&search',
            data: {
                authorities: [],
                pageTitle: 'Movies'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/movie/movies.html',
                    controller: 'MovieController',
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
        .state('movie-detail', {
            parent: 'movie',
            url: '/movie/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Movie'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/movie/movie-detail.html',
                    controller: 'MovieDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Movie', function($stateParams, Movie) {
                    return Movie.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'movie',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('movie-detail.edit', {
            parent: 'movie-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/movie/movie-dialog.html',
                    controller: 'MovieDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Movie', function(Movie) {
                            return Movie.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('movie.new', {
            parent: 'movie',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/movie/movie-dialog.html',
                    controller: 'MovieDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                title: null,
                                director: null,
                                year: null,
                                country: null,
                                criticScore: null,
                                userScore: null,
                                poster: null,
                                rtUrl: null,
                                imdbRating: null,
                                imdbKeywords: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('movie', null, { reload: 'movie' });
                }, function() {
                    $state.go('movie');
                });
            }]
        })
        .state('movie.edit', {
            parent: 'movie',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/movie/movie-dialog.html',
                    controller: 'MovieDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Movie', function(Movie) {
                            return Movie.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('movie', null, { reload: 'movie' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('movie.delete', {
            parent: 'movie',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/movie/movie-delete-dialog.html',
                    controller: 'MovieDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Movie', function(Movie) {
                            return Movie.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('movie', null, { reload: 'movie' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
