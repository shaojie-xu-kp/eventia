
    // create the module and name it scotchApp
    var eventiaApp = angular.module('FlyrPOCApp', ['ngRoute','ngSanitize', 'ui.bootstrap']);

    // configure our routes
       eventiaApp.config(function($routeProvider) {
           $routeProvider

               // route for the home page
               .when('/', {
                   templateUrl : 'app/search.html',
                   controller  : 'searchController'
               })

               // route for the about page
               .when('/results', {
                   templateUrl : 'app/results.html',
                   controller  : 'resultController'
               })
       });
    console.log("app running");
       // create the controller and inject Angular's $scope
       eventiaApp.controller('mainController', function($scope) {

       });

       eventiaApp.controller('searchController', function($filter, $scope, $http, $timeout, $anchorScroll, $rootScope,$location) {

               // sample utility function
               function initScopeVars() {
                   // this code won't run until someone calls this function
                   // from within this controller
                   $scope.testInputVal = "test1";
                   $scope.eventTitle = "";
                   $scope.eventSelectedFlag = false;
                   $scope.searchCriteria = {origin: "BOS", travelerNbr: 1};
                   $scope.selected = "";
                   $scope.origin = "";
               }

               function getLocation() {
                   $scope.origin = "Test";
               }

               function buildAvailRequest() {
                   var availRQ = {};
                   availRQ.pos = "JETBLUEWEB_US";
                   availRQ.originDestination = [];
                   var outbound = {};
                   outbound.origin = {};
                   outbound.origin.date = "2017-08-21";
                   outbound.origin.locationRef = {"uri": "/geo/locations/A/SFO"};
                   outbound.destination = {};
                   outbound.destination.locationRef = {"uri": "/geo/locations/A/JFK"};
                   availRQ.originDestination.push(outbound);
                   var returnSeg = {};
                   returnSeg.origin = {};
                   returnSeg.origin.date = "2017-08-23";
                   returnSeg.origin.locationRef = {"uri": "/geo/locations/A/JFK"};
                   returnSeg.destination = {};
                   returnSeg.destination.locationRef = {"uri": "/geo/locations/A/SFO"};
                   availRQ.originDestination.push(returnSeg);

                   availRQ.travelerComposition = [];
                   var guestType = {"count": 1, "typeRef": { "uri": "/info/traveler-types/AIR/ADT"}};
                   availRQ.travelerComposition.push(guestType);
                   return availRQ;
               }


               // sample function available on the view
               $scope.sampleViewFunction = function() {
                   // ...
               }

               $scope.testFunction = function() {
                   console.log("testInputVal = " + $scope.testInputVal);
                   $scope.testInputVal = "test1";
               }

               $scope.getAvail = function() {
                   console.log("getting ready to get availability data");

                   var availRequest = buildAvailRequest();

                   $scope.processingRequest = true;
                   console.log("Ready to make avail request");
                   $http.post("http://10.160.7.156/tdprest-2/api/air/avail", availRequest)
                       .then(
                           function successCallback(response) {
                               console.log("AirAvailRS:");
                               console.log(response.data);
                               $scope.availResponse = response.data;
                               $scope.processingRequest = false;
                           },
                           function errorCallback(response) {
                               console.log("Unable to get AirAvailRS");
                               console.log(reponse);
                               $scope.processingRequest = false;
                               $scope.errorMessage = "Unable to get Availability";
                           });

               }

               function loadEvents() {
                   console.log("getting ready to get availability data");

                   $http.get("http://localhost:8080/events")
                       .then(
                           function successCallback(response) {
                               console.log("Events:");
                               console.log(response.data);
                               console.log(response.data.length);

                               var events = [];
                               var eventsByName = [];
                               for (var i = 0; i < response.data.length; i++) {
                                   var event = {};
                                   event = response.data[i];
                                   eventsByName[event.title] = event;
                                   events.push(response.data[i].title)
                               }
                               $scope.states = events;
                               $scope.eventsByName = eventsByName;
                           },
                           function errorCallback(response) {
                               console.log("Unable to get events");
                               console.log(reponse);
                           });
               }

               $scope.testTypeAhead = function() {
                   console.log("it works");
               }

               $rootScope.testingSelect = function(selectedItem) {
                   console.log("testingSelect");
                   console.log(selectedItem);
                   console.log($scope.eventsByName[selectedItem.label]);
                   $scope.eventSelectedFlag = true;
                   $scope.eventSelected = $scope.eventsByName[selectedItem.label];
       //            alert("1");
                   if ($scope.eventsByName[selectedItem.label]) {
                       $scope.searchCriteria.departureDate = $filter('date')($scope.eventsByName[selectedItem.label].start, "yyyy-MM-dd");
       //                $scope.eventsByName[selectedItem.label].start;
                       $scope.searchCriteria.arrivalDate = $filter('date')($scope.eventsByName[selectedItem.label].end, "yyyy-MM-dd");
                       $scope.searchCriteria.id = $scope.eventsByName[selectedItem.label].id;
                       $scope.searchCriteria.title = $scope.eventsByName[selectedItem.label].title;
                       console.log("ID of selected event = " + $scope.searchCriteria.id);
                   }
       //            alert("2");
               }

               $scope.selectEvent = function() {
                   $scope.eventSelected = true;
               }

               $scope.mySelectMatch = function(myIndex) {
                   $scope.selectMatch(myIndex);
                   console.log("does this work?");
               }

               // sample variable available on the view
               $scope.sampleVar = "something";

               // All code here will run when the page loads
               initScopeVars();
               loadEvents();
               console.log("Done initializing!");

                $scope.goToResults = function() {
                               console.log("goToResults()");
                                 $location.path("/results");
                                 $rootScope.sampleValue = "something";
                                 $rootScope.searchCriteria = $scope.searchCriteria;
                           }
           });





       eventiaApp.controller('resultController', function($rootScope, $scope, $http, $timeout,  $location)  {

            console.log("resultController");
            console.log("sampleValue = " + $rootScope.sampleValue);
            console.log("Search criteria object: ");
            console.log($rootScope.searchCriteria);
            $scope.requestedEvent = $rootScope.searchCriteria;



                      function selectOfferData(response){
                                          $scope.flight= response.flights[0];
                                          $scope.hotel= response.hotels[0];
                                          $scope.ancillaries= response.ancillaries[0];
                                          $scope.taxi= response.taxis[0];
                                     }

//origin, numTrav, eventId
                       function getOffer(eventId,origin){
                                        $http.get("http://localhost:8080/offer/"+eventId+"/"+origin)
                                                        .then(
                                                            function successCallback(response) {
                                                                console.log("Offer received:");
                                                                console.log(response.data);
                                                                $scope.offer=response.data;
                                                                selectOfferData(response.data);

                                                            },
                                                            function errorCallback(response) {
                                                                console.log("Unable to get event"+response);
                                                                $scope.errorMessage = "Unable to get event";
                                                            });

                                        }




                getOffer($scope.requestedEvent.id,$scope.requestedEvent.origin);

       });



