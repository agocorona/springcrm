# springcrm
basic CRM API exercise. Implemented all the endpoints, Oauth-2.

Unit test works with
> mvn test

Authentication seems not to work very well with unit tests and Oauth2. To check that  permissions and @lastModifiedBy works well, here are some curl request: 

Some queries so far:

*To obtain the Oauth  token:*

```
$ curl -X POST -vu android-crmapi:123456 http://localhost:8080/oauth/token -H "Accept: appli
cation/json" -d "password=password&username=jlong&grant_type=password&scope=write&client_sec
ret=123456&client_id=android-crmapi"
Note: Unnecessary use of -X or --request, POST is already inferred.
*   Trying ::1...
* TCP_NODELAY set
* Connected to localhost (::1) port 8080 (#0)
* Server auth using Basic with user 'android-crmapi'
> POST /oauth/token HTTP/1.1
> Host: localhost:8080
> Authorization: Basic YW5kcm9pZC1jcm1hcGk6MTIzNDU2
> User-Agent: curl/7.57.0
> Accept: application/json
> Content-Length: 110
> Content-Type: application/x-www-form-urlencoded
>
* upload completely sent off: 110 out of 110 bytes
< HTTP/1.1 200
< X-Content-Type-Options: nosniff
< X-XSS-Protection: 1; mode=block
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Expires: 0
< X-Frame-Options: DENY
< Content-Type: application/json;charset=UTF-8
< Transfer-Encoding: chunked
< Date: Wed, 18 Apr 2018 12:05:08 GMT
<
{"access_token":"YOUR-TOKEN","token_type":"bearer","refresh_token":"f69771e0-7d0b-4fa2-a98c-c6b1881193d0","expires_in":43199,"scope":"write"}* Connection #0 to host localhost left intact
```

*obtain all the customers*

```
$ curl http://127.0.0.1:8080/crmapi/customers -H "Authorization: Bearer YOUR-TOKEN"
{"_embedded":{"customerResourceList":[{"customer":{"id":1,"name":"jhoellerCustomer1","surname":"A description"}},{"customer":{"id":2,"name":"jhoellerCustomer2","surname":"A description"}},{"customer":{"id":3,"name":"dsyerCustomer1","surname":"A description"}},{"customer":{"id":4,"name":"dsyerCustomer2","surname":"A description"}},{"customer":{"id":5,"name":"pwebbCustomer1","surname":"A description"}},{"customer":{"id":6,"name":"pwebbCustomer2","surname":"A description"}},{"customer":{"id":7,"name":"ogierkeCustomer1","surname":"A description"}},{"customer":{"id":8,"name":"ogierkeCustomer2","surname":"A description"}},{"customer":{"id":9,"name":"rwinchCustomer1","surname":"A description"}},{"customer":{"id":10,"name":"rwinchCustomer2","surname":"A description"}},{"customer":{"id":11,"name":"mfisherCustomer1","surname":"A description"}},{"customer":{"id":12,"name":"mfisherCustomer2","surname":"A description"}},{"customer":{"id":13,"name":"mpollackCustomer1","surname":"A description"}},{"customer":{"id":14,"name":"mpollackCustomer2","surname":"A description"}},{"customer":{"id":15,"name":"jlongCustomer1","surname":"A description"}},{"customer":{"id":16,"name":"jlongCustomer2","surname":"A description"}}]}}
```

There is a test request that try to modify the surname of a customer. The user of the previous request "jlong" has no admin permissions so the request will be rejected. I will add more details.
