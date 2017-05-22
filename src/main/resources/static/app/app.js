
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
                   templateUrl : 'app/results_dom.html',
                   controller  : 'resultController'
               })
       });
    console.log("app running");

       // create the controller and inject Angular's $scope
       eventiaApp.controller('mainController', function($scope) {

       });

       eventiaApp.controller('searchController', function($filter, $scope, $http, $timeout, $anchorScroll, $rootScope,$location) {

                    $scope.processingRequest = false;
               // sample utility function
               function initScopeVars() {
                   // this code won't run until someone calls this function
                   // from within this controller
                   $scope.testInputVal = "test1";
                   $scope.eventTitle = "";
                   $scope.eventSelectedFlag = false;
                   $scope.searchCriteria = {origin: "ATL", travelerNbr: 1};
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


                   console.log("Ready to make avail request");
                   $http.post("http://10.160.7.156/tdprest-2/api/air/avail", availRequest)
                       .then(
                           function successCallback(response) {
                               console.log("AirAvailRS:");
                               console.log(response.data);
                               $scope.availResponse = response.data;
                           },
                           function errorCallback(response) {
                               console.log("Unable to get AirAvailRS");
                               console.log(reponse);
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
                function getOffer(eventId,origin){
                    $scope.processingRequest = true;
                                        $http.get("http://localhost:8080/offer/"+eventId+"/"+origin)


                                    //$http.get("http://localhost:8085/offer")
                                                        .then(
                                                            function successCallback(response) {

                                                                console.log("Offer received:");
                                                                console.log(response.data);
                                                               $rootScope.offer=response.data;
                                                               $rootScope.offer.flights.sort($scope.sortFlightsByPrice);
                                                               console.log("loading...");
                                                                 $scope.processingRequest = false;
                                                                $location.path("/results");
                                                            },
                                                            function errorCallback(response) {
                                                                console.log("Unable to get event"+response);
                                                                $scope.errorMessage = "Unable to get event";
                                                                 $scope.processingRequest = false;
                                                            });

                                        }
               $scope.selectEvent = function() {
                   $scope.eventSelected = true;
               }

               $scope.sortFlightsByPrice = function(flight1, flight2) {
                    if (flight1.price > flight2.price) {
                            return 1;
                        } else {
                            return -1;
                        }
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
                                 $rootScope.sampleValue = "something";
                                 $rootScope.searchCriteria = $scope.searchCriteria;
                                  getOffer($scope.searchCriteria.id,$scope.searchCriteria.origin);
                           }
           });





       eventiaApp.controller('resultController', function($rootScope, $scope, $http, $timeout,  $location, $filter)  {
                     Date.prototype.addHours = function (h){
                          this.setTime(this.getTime() + (h*60*60*1000));
                         return this;
                   }
            // Dominique - moved init to the end of the controller definition
            // (so that all dependent functions have been defined)

            // Dominique
            $scope.toggleSummary = function() {
                $scope.showSummary = !$scope.showSummary;
                $scope.showOverview = !$scope.showOverview;
                if ($scope.showSummary) {
                    $scope.otherViewLabel = "Overview";
                } else {
                    $scope.otherViewLabel = "Summary";
                }
            }

            $scope.toggleBiais = function() {
                console.log("toggleBiais()");
                $scope.priceBiais = !$scope.priceBiais;
                $scope.flexBiais= !$scope.flexBiais;
                if ($scope.priceBiais) {
                    $scope.biaisLabel = "Flex Bias";
                    selectOfferData($scope.offer);
                } else {
                    $scope.biaisLabel = "Price Bias";
                    $scope.selectFlexOptions();
                }
            }

            // Fusion Charts

            $rootScope.generateChart = function() {
                console.log("initializing fusion charts ...");
                var fusioncharts = new FusionCharts({
                    type: 'doughnut2d',
                    renderAt: 'chart-container',
                    width: '100%',
                    height: '600',
                    dataFormat: 'json',
                    dataSource: {
                        "chart": {
                            "caption": "Your Itinerary",
                            "subCaption": "Selected by Eventia Smart Engine",
                            // caption cosmetics
                            "captionFont": "Arial",
                            "captionFontSize": "36",
                            "captionFontColor": "#AAAAAA",
                            "captionFontBold": "1",
                            "subcaptionFont": "Arial",
                            "subcaptionFontSize": "24",
                            "subcaptionFontColor": "#AAAAAA",
                            "subcaptionFontBold": "0",
                            // end caption cosmetics
                            // label cosmetics
                            "labelFont": "Arial",
                            "labelFontColor": "000080",
                            "labelFontSize": "36",
                            //        "labelBorderColor": "000000",
                            //        "labelBorderPadding": "5",
                            //        "labelBorderRadius": "2",
                            //        "labelBorderDashed": "1",
                            // end label cosmetics
                            "numberPrefix": "$",
                            "showBorder": "0",
                            "use3DLighting": "0",
                            "enableSmartLabels": "0",
                            "startingAngle": "310",
                            "showLabels": "0",
                            "showPercentValues": "1",
                            "showLegend": "1",
                            "defaultCenterLabel": "Total: " + $scope.totalPrice,
                            "centerLabel": "$label: $value",
                            "centerLabelBold": "1",
                            "showTooltip": "0",
                            "decimals": "0",
                            "useDataPlotColorForLabels": "1",
                            "theme": "fint"
                        },
                        "data": [{
                            "label": "Air",
                            "value": $scope.flight.price
                            }, {
                            "label": "Taxi",
                            "value": $scope.taxi.price
                            }, {
                            "label": "Hotel",
                            "value": $scope.hotel.price
                            }
                        ]
                    }
                });
                console.log("ready to render ...");

                fusioncharts.render();
                console.log("done rendering fusion charts");
             }


            console.log("resultController");
            console.log("sampleValue = " + $rootScope.sampleValue);
            console.log("Search criteria object: ");
            console.log($rootScope.searchCriteria);
            $scope.requestedEvent = $rootScope.searchCriteria;
            // Dominique
            $scope.showSummary = true;
            $scope.showOverview = false;
            $scope.otherViewLabel = "Overview";
            $scope.priceBiais = true;
            $scope.flexBiais = false;
            $scope.biaisLabel = "Flex Bias";
            // note: this will have to be triggered when the results have been received from the server

            $scope.selectFlight = function(flightIndex) {
                $scope.flight = $scope.offer.flights[flightIndex];
                 var total = 0;
                  total = $scope.flight.price +  $scope.hotel.price +$scope.taxi.price;
                  $scope.totalPrice= total;
                  var parts =$scope.flight.originDestinations[0].arrival.time.split(':');
                  var pickupTime = new Date(0,0,0,parts[0],parts[1]);
                  pickupTime.addHours(1);
                  $scope.taxi.pickupTime = $filter('date')(pickupTime, "HH:mm");

                 $rootScope.generateChart();
            }

             $scope.selectFlexOptions = function() {
                console.log("selectFlexOptions");
                $scope.flight = $scope.offer.flights[$scope.offer.flights.length -1];
                $scope.hotel = $scope.offer.hotels[$scope.offer.hotels.length -1];
                $scope.taxi = $scope.offer.taxis[$scope.offer.taxis.length -1];
                var total = 0;
                total = $scope.flight.price +  $scope.hotel.price +$scope.taxi.price;
                $scope.totalPrice= total;
                 var parts =$scope.flight.originDestinations[0].arrival.time.split(':');
                var pickupTime = new Date(0,0,0,parts[0],parts[1]);
                 pickupTime.addHours(1);
                $scope.taxi.pickupTime = $filter('date')(pickupTime, "HH:mm");
                 $rootScope.generateChart();
              }


                      function selectOfferData(response){
                                          $scope.flight= response.flights[0];
                                          console.log("flight:");
                                          console.log($scope.flight);
                                          console.log("flights:");
                                          console.log(response.flights);
                                          $scope.hotel= response.hotels[0];
                                          $scope.ancillaries= response.ancillaries[0];
                                          $scope.taxi= response.taxis[0];
                                           var parts =$scope.flight.originDestinations[0].arrival.time.split(':');
                                           var pickupTime = new Date(0,0,0,parts[0],parts[1]);
                                           pickupTime.addHours(1);
                                           $scope.taxi.pickupTime = $filter('date')(pickupTime, "HH:mm");
                                          var total = 0;
                                          total = $scope.flight.price +  $scope.hotel.price +$scope.taxi.price;
                                          $scope.totalPrice= total;
                                          $rootScope.generateChart();
                                     }


                 selectOfferData($scope.offer );

       });



