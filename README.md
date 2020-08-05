# API-Rate-limit
High Level Diagram


High Level Diagram for Rate Limit: 
Rate Limiter will be responsible for deciding which request will be served by the API servers and
which request will be declined.


Basic Design and Algorithm Used :

For Example we want to limit the number of requests per user. For Each unique user , we would keep a count representing how many request the user has made and a timestamp when the request was made. For each user we maintain a  Queue . And if there is a new request by user , we make a object of id of request and timestamp , and if the queue is empty we push the object in queue. 
As there will be rate limit on number of request of particular user(we have kept threshHold request to be configurable) , so for every new request ,before pushing the object into queue, we first dequeue objects when deltaTime (currentTimeStamp - timeStamp Of object) is greater than threshold time in which rate limit check should be applied(this property is also configurable),  then  we check the size of the queue for a particular user , and if the size of queue is less than rate limit number of particular user , then we push the object in queue and notify the web-service that the request can be accepted fr current user and if size of queue is greater than equal to rate limit number of particular user, we dont insert object and in quue and notifies the web-service that dont accept current user request and notifies the client with Http code 429(Too Many request).


Sequence Diagram for some scenarios :

Let’s assume our rate limiter is allowing three requests per minute per user, so whenever a new request
comes in, our rate limiter will perform the following steps:
1.If the ‘UserID’ is not present in the configuration file, then default rate Limit value of the API is set and fetch the default object related with Api from Redis.
2. Otherwise find the record associated with the user from Redis.
3. We check the peek element from queue which is present in object, and calculate deltaTime(curentTime - timeStamp over peek object in queue). If deltaTime is greater than thresholdLimitTime , then we dequeue that object . We continue this , till we found element which has delta time less than n thresholdLimitTime . 
In this case we are allowing 3 request/min
Then our thresholdLimitTime is 1 min.
4.We check the size of queue , if size is less than rateLimit number for that user , we insert element in queue and notifies the web-service to accept the request otherwise notifies the web-service to reject the request.
5. Also I have tried to take here threadsafe queue , i.e ConcurrentLinkedQueue , to make it thread safe.









README - Steps to configure application 

Steps to configure eclipse:
There are two projects :
work-sample[rate-limit] //implemnetation for rate limitting
restapp // implementation of sample Api with rate limit, Configuration of rate limit
	 	 	 	
Steps to run configure your application on Eclipse :

    1. Setup the Redis Server and start redis service. Currently I am using default hostName as “localhost ” and default port of redis i.e  6379, also the same is configure in rate-limit project.
    2. Build your work-sample project , I curently using maven for build ,using command mvn clean install.
    3. Import your restapp project in IDE such as Eclipse or Intellij .
    4. In Pom.xml of restapp make sure to have a dependency of work-sample project.
    5. Do a mvn clean install for restapp project.
    6. Rest App project is to write new apis and to add rate limit configuration over them.
    7. For Configuration of Rate Limit properties , I am currently using config.json file.
    8. You can see sample configuration as below :
[ {
       "api":"/api/v1/organizations",
       "limit" : "100"
   },{
       "api":"/api/v1/developers",
       "limit" : "100"
   },{
       "userId":"user1",
       "api":"/api/v1/developers",
       "limit" : "100"
   },{
       "userId":"user1",
       "api":"/api/v1/organizations",
       "limit" : "100"
   }]
I am taking here rateMap as Map<String,Integers> and it stores pairs of UserId+Api and rateLimit threshold. Eg
/api/v1/organizations : 150 is default rateLimit of the api
user1/api/v1/developers : 50 is the rateLimit of the api for ueser1

There are one additional properties in configuration.
TIME_TO_RESET_IN_RATE_LIMITER_MAP_IN_MS : this is the time interval when my map which stores UserId+Api and rateLimit threshold to be reset after some interval.
TIME_TO_RESET_IN_RATE_LIMITER_MAP_IN_MS is store in application.properties
RateMap is loaded into application during boot time of application.

8. This RateMap is initialized in RateLimit project during booting of application and we process the map into Object corresponding to each user+Api and store it in redis.
9.RateLimit is initialized and on whichever Api you want to put rate limit of number of request , you can use this as used in the rateApp Controller.
Eg:
public ResponseEntity getDevelopers(HttpServletRequest request) {
   String clientId = request.getHeader("ClientId") != null ?request.getHeader("ClientId") :"";
   if(rateLimitManager.pegTraffic(clientId,request.getRequestURI())){   //rateLimit is being checked here
       List<String> name= new ArrayList();
       name.add("John");
       name.add("Ravi");
       return new ResponseEntity(name, HttpStatus.OK);
   }else{
       return new ResponseEntity(HttpStatus.TOO_MANY_REQUESTS);
   }
}
In above example of given Api , I am finding which user is hitting the API by request header , this is the assumption I am making , that user needs to put a header with key and value as “ClientId”:”<user-Id>”. Also the URI of the API can be found by HttpServletRequest in parameters.
10. When Any user hits a request on API , the request first transfer to ratelimit project and there the corresponding object , related to User is fetched from Redis if it is present , other wise there will be default Api object .
11. Now the evaluation on that object takes place. We remove elements from the rear ,where deltaTime(currentTime - timeStampIn Object) exceeds  threshHold Time i.e TIME_TO_RESET_IN_RATE_LIMITER_MAP_IN_MS
12. AfterThat we check the size of the queue. If the size is less than threshHold Limit we insert the object in queue and notify our api to accept the request.
13 Otherwise we notify the Api to reject the user request.
14 Rate Limit Application is tried to make thread safe . Wherever needed ThreadSafe collections like ConcurrentLinkedQueue have been used.
15. Created Unit test case where you can test a users ratelimit for an api and also could configure rate limit where unit test are acting like rest client.
Assumptions Made :
    1. For determining which user is requesting for API , the user need to pass in header “ClientId”: “user-Id”. 
    2. Information about configuration of user and api is currently taken fromm config file.
    3. At runtime added information in config file, does not affect rate limit.
 	
Improvements :
1 .  Today rate limit is taken for userId+ Api combination. It can be extended based on user geo location or user IP.
2. At runtime Information added should can be made configurable in future	
3. Improve on consistency and memory if we will implement it for distributed applications.


Jmeter : 

Also I am attaching a Jmeter TestPlan file named as “For User2 Developers Api.jmx”  where Number of Threads are 102 and Ramp up Period is 300 , and in header i pass it as “ClientId”:”user2”.
And in configuration user2+”/api/v1/developers” (api) has rate limit threshold as 150.

So from the result we can see that 100 Request are accepted as the threshold is 100 and 2 request are rejected.

So for more scenarios , some more test cases can also be checked using same test plan jmx file.

Dependency :
Redis Server 
