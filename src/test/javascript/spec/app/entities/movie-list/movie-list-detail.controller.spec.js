'use strict';

describe('Controller Tests', function() {

    describe('MovieList Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockMovieList, MockUser, MockMovie;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockMovieList = jasmine.createSpy('MockMovieList');
            MockUser = jasmine.createSpy('MockUser');
            MockMovie = jasmine.createSpy('MockMovie');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'MovieList': MockMovieList,
                'User': MockUser,
                'Movie': MockMovie
            };
            createController = function() {
                $injector.get('$controller')("MovieListDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'dreadflicksApp:movieListUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
