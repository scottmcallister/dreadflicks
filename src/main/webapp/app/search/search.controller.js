(function() {
    'use strict';

    angular
        .module('dreadflicksApp')
        .controller('SearchController', SearchController);

    SearchController.$inject = [
        '$state',
        'Movie',
        'MovieSearch',
        'MovieList',
        'MovieListMovie',
        'ParseLinks',
        'AlertService',
        'Principal',
        'paginationConstants',
        'pagingParams'
    ];

    function SearchController($state,
                              Movie,
                              MovieSearch,
                              MovieList,
                              MovieListMovie,
                              ParseLinks,
                              AlertService,
                              Principal,
                              paginationConstants,
                              pagingParams) {

        var vm = this;

        vm.isAuthenticated = Principal.isAuthenticated;
        vm.loadPage = loadPage;
        vm.predicate = 'criticScore';
        vm.direction = 'desc';
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;
        vm.searchQuery = '*';
        vm.currentSearch = pagingParams.search;
        vm.criticMax = 99;
        vm.criticMin = 0;
        vm.userMax = 99;
        vm.userMin = 0;
        vm.yearMax = 2017;
        vm.yearMin = 1960;
        vm.imdbMax = 10;
        vm.imdbMin = 0;
        vm.inputText = '';
        vm.imdbSlider = {
            floor: 0,
            ceil: 10,
            step: 0.1,
            precision: 1,
            enforceStep: false,
            onEnd: function(id, lowValue, highValue, pointerType){
                if(pointerType === 'min') {
                    vm.imdbMin = lowValue;
                } else {
                    vm.imdbMax = highValue;
                }
                resetPagination();
                loadAll();
            }
        };
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
                resetPagination();
                loadAll();
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
                resetPagination();
                loadAll();
            }
        };
        vm.yearSlider = {
            floor: 1960,
            ceil: 2017,
            step: 1,
            onEnd: function(id, lowValue, highValue, pointerType){
                if(pointerType === 'min') {
                    vm.yearMin = lowValue;
                } else {
                    vm.yearsMax = highValue;
                }
                resetPagination();
                loadAll();
            }
        };
        vm.doSearch = function() {
            if (vm.inputText === '') {
                vm.searchQuery = '*';
            } else {
                vm.searchQuery = vm.inputText;
            }
            resetPagination();
            loadAll();
        };
        vm.totalItems = -1;

        vm.sortList = [
            {
                value: 'title',
                name: 'Title'
            },
            {
                value: 'criticScore',
                name: 'Critic Score'
            },
            {
                value: 'userScore',
                name: 'User Score'
            },
            {
                value: 'year',
                name: 'Year'
            },
            {
                value: 'imdbRating',
                name: 'IMDB Rating',
            },
        ];

        vm.types = [
            'alien',
            'alien abduction',
            'based on novel',
            'clown',
            'demon',
            'devil',
            'doll',
            'exorcism',
            'found footage',
            'ghost',
            'gore',
            'graveyard',
            'haunted house',
            'haunting',
            'home invasion',
            'hotel',
            'infection',
            'isolation',
            'monster',
            'paranormal',
            'possession',
            'puppet',
            'satanism',
            'shark',
            'slasher',
            'snake',
            'vampire',
            'werewolf',
            'witchcraft',
            'zombie'
        ];
        vm.selectedTypes = [];

        vm.movieLists = [];
        if(vm.isAuthenticated()){
            MovieList.query({
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: ['name,asc', 'id'],
            },
            function(data, headers) {
                vm.movieLists = data;
            },
            function(error) {
                AlertService.error(error.data.message);
            });
        }
        vm.addList = function(movieId, listId) {
            var payload = { 
                'list': listId,
                'movie': movieId,
            };
            MovieListMovie.add(payload, payload);
        };

        loadAll();

        function loadAll () {
            MovieSearch.query({
                query: vm.searchQuery,
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort(),
                criticMax: vm.criticMax,
                criticMin: vm.criticMin,
                userMax: vm.userMax,
                userMin: vm.userMin,
                yearMax: vm.yearMax,
                yearMin: vm.yearMin,
                imdbMax: vm.imdbMax,
                imdbMin: vm.imdbMin,
                types: vm.selectedTypes.join(),
            }, onSuccess, onError);
            function sort() {
                var result = [vm.predicate + ',' + vm.direction];
                if (vm.predicate !== 'id') {
                    result.push('id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = parseInt(headers('X-Total-Count'));
                vm.queryCount = vm.totalItems;
                vm.movies = data;
                vm.page = pagingParams.page;
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        function resetPagination() {
            pagingParams.page = 1;
        }

        function loadPage(page) {
            vm.page = page;
            vm.transition();
        }

        function transition() {
            pagingParams.page = vm.page;
            loadAll();
            scroll(0,0);
        }

        function search(searchQuery) {
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
