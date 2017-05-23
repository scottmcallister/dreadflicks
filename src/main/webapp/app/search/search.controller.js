(function() {
    'use strict';

    angular
        .module('dreadflicksApp')
        .controller('SearchController', SearchController);

    SearchController.$inject = ['$state', 'Movie', 'MovieSearch', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams'];

    function SearchController($state, Movie, MovieSearch, ParseLinks, AlertService, paginationConstants, pagingParams) {

        var vm = this;

        vm.loadPage = loadPage;
        vm.predicate = 'criticScore';
        vm.reverse = pagingParams.descending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;
        vm.searchQuery = pagingParams.search;
        vm.currentSearch = pagingParams.search;
        vm.criticMax = 99;
        vm.criticMin = 0;
        vm.userMax = 99;
        vm.userMin = 0;
        vm.yearMax = 2017;
        vm.yearMin = 1960;
        vm.criticSlider = {
            floor: 0,
            ceil: 100,
            step: 1,
            onEnd: function(id, lowValue, highValue, pointerType){
                if(pointerType === 'min') {
                    vm.criticMin = lowValue;
                } else {
                    vm.criticMax = highValue;
                }

            }
        };
        vm.userSlider = {
            floor: 0,
            ceil: 100,
            step: 1,
            onEnd: function(id, lowValue, highValue, pointerType){
                if(pointerType === 'min') {
                    vm.userMin = lowValue;
                } else {
                    vm.userMax = highValue;
                }

            }
        };
        vm.yearSlider = {
            floor: 1960,
            ceil: 2017,
            step: 1
        };

        loadAll();

        function loadAll () {
            MovieSearch.query({
                query: '*',
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort(),
                criticMax: vm.criticMax,
                criticMin: vm.criticMin,
                userMax: vm.userMax,
                userMin: vm.userMin,
                yearMax: vm.yearMax,
                yearMin: vm.yearMin
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
                vm.queryCount = vm.totalItems;
                vm.movies = data;
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
            vm.predicate = 'id';
            vm.reverse = true;
            vm.currentSearch = null;
            vm.transition();
        }
    }
})();
