angular.module('FlyrPOCApp', [])
   .controller('FlyrPOCController', function($scope, $http, $timeout, $anchorScroll) {

        // sample utility function
        function initScopeVars() {
            // this code won't run until someone calls this function
            // from within this controller
            $scope.testInputVal = "test1";
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

        $scope.testTypeAhead = function() {
            console.log("it works");
        }

        // sample variable available on the view
        $scope.sampleVar = "something";


        // All code here will run when the page loads
        initScopeVars();
        console.log("Done initializing!");
    });
