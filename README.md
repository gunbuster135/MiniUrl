# MiniUrl
A reactive implementation of a URL minifier

### Build
To build & run all tests \
```mvn verify```

You can also build a docker image by appending `jib:dockerBuild`\
```mvn verify jib:dockerBuild```

### Running the application
MiniUrl requires a Redis server running locally on port `6379`
To run the application after just building \
```java -jar target/miniurl-0.0.1-SNAPSHOT.jar```

If you want to run a docker container (after running `jib:dockerBuild`) \
```docker run -p 8080:8080 -t miniurl:latest```

Or even easier with `docker-compose` if you don't feel like setting up redis (after running `jib:dockerBuild`) \
```docker-compose up```

### Usage
This section assumes you are running MiniUrl locally on port `8080` 

To hash a URL
```
curl --header "Content-Type: application/json" \
      --request POST \
      --data '{"url":"www.google.com"}' \
localhost:8080/

{
  "hash":"08316aaa"
}
``` 
If you wish to set a custom ttl on your about-to-be minified url, see following json
```json
{
  "url":"www.google.com",
  "ttl": 15000
}
```
where `ttl` is expressed in milliseconds

Calling the API with the hash
```
curl localhost:8080/08316aaa

...
< HTTP/1.1 302 Found
< Location: www.google.com
< content-length: 0
...
```





