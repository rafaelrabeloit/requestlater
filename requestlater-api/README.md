RequestLater API Service
=========================
This project is about scheduling API requests, in a microservice context.  
The original idea was about making "scraping-as-a-service", but as everything 
that I code, I took a more generic approach and make the request itself a 
service.
  
Motive
------
My reason to have a "request-as-a-service" is that are several occasions in 
which it may be helpful to have a request to a REST service been delayed 
to a certain time, for microservices.  
The scraping service is one example. I may want to have a data for some site 
transformed into data constantly.  
Yet another useful situation would be a limited resource machine scheduling 
calls to moments when it thinks it will have time to process something.  
Or heavy jobs being scheduled to low request times.  
Or mailing services.  
Or anything that would be done with cron rules.  
*I would go farther:* Lets make asynchronous API. Not only pubsub, but really 
async, by making requests being made at a some point and only a response about 
it being accepted and not processed sent back to the client. The process would 
be made in an arbitrary time and the result would be returned in a new request, 
fired by the API itself, to the "client".  
My ultimate motive is that it was fun to do. :)