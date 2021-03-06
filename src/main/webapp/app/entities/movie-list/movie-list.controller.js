(function() {
    'use strict';

    angular
        .module('dreadflicksApp')
        .controller('MovieListController', MovieListController);

    MovieListController.$inject = [
        '$state',
        'MovieList',
        'MovieListSearch',
        'ParseLinks',
        'AlertService',
        'paginationConstants',
        'pagingParams',
        'Principal'
    ];

    function MovieListController($state, MovieList, MovieListSearch, ParseLinks, AlertService,
        paginationConstants, pagingParams, Principal) {
        var vm = this;

        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;
        vm.searchQuery = pagingParams.search;
        vm.currentSearch = pagingParams.search;
        vm.account = null;
        vm.atLimit = false;

        Principal.identity().then(function(account) {
            vm.account = account;
            loadAll();
        });

        function loadAll () {
            MovieList.query({
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort()
            }, onSuccess, onError);
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'id') {
                    result.push('id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.atLimit = vm.totalItems >= 6;
                vm.queryCount = vm.totalItems;
                vm.movieLists = data;
                vm.page = pagingParams.page;
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        function loadPage(page) {
            vm.page = page;
            vm.transition();
        }

        function transition() {
            $state.transitionTo($state.$current, {
                page: vm.page,
                sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
                search: vm.currentSearch
            });
        }

        function search(searchQuery) {
            if (!searchQuery){
                return vm.clear();
            }
            vm.links = null;
            vm.page = 1;
            vm.predicate = '_score';
            vm.reverse = false;
            vm.currentSearch = searchQuery;
            vm.transition();
        }

        function clear() {
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'name';
            vm.reverse = true;
            vm.currentSearch = null;
            vm.transition();
        }
    }
})();
