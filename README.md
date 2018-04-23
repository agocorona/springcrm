# springcrm
basic CRM API exercise. Implemented all the endpoints, Oauth-2.

Unit test works with

```
> mvn test
```

Authentication seems not to work very well with unit tests and Oauth2. It seems that using Oauth2 with automated tests allow access permission, but principal is null, so user validation does not work, neither  @createdBy or @lastModifiedBy directives (that need principal info). Performing the same requests by hand in the console with curl works well

To check that  permissions and @lastModifiedBy works well, here are some curl request: 


*To obtain the Oauth  token:*

```
$ curl -X POST -vu android-crmapi:123456 http://localhost:8080/oauth/token -H "Accept: appli
cation/json" -d "password=password&username=jlong&grant_type=password&scope=write&client_sec
ret=123456&client_id=android-crmapi"

{"access_token":"YOUR-TOKEN","token_type":"bearer","refresh_token":"f69771e0-7d0b-4fa2-a98c-c6b1881193d0","expires_in":43199,"scope":"write"}* Connection #0 to host localhost left intact
```

*obtain all the customers*

```
$ curl http://127.0.0.1:8080/crmapi/customers -H "Authorization: Bearer YOUR-TOKEN"
{"_embedded":{"customerResourceList":[{"customer":{"id":1,"name":"jhoellerCustomer1","surname":"A description"}},{"customer":{"id":2,"name":"jhoellerCustomer2","surname":"A description"}},{"customer":{"id":3,"name":"dsyerCustomer1","surname":"A description"}},{"customer":{"id":4,"name":"dsyerCustomer2","surname":"A description"}},{"customer":{"id":5,"name":"pwebbCustomer1","surname":"A description"}},{"customer":{"id":6,"name":"pwebbCustomer2","surname":"A description"}},{"customer":{"id":7,"name":"ogierkeCustomer1","surname":"A description"}},{"customer":{"id":8,"name":"ogierkeCustomer2","surname":"A description"}},{"customer":{"id":9,"name":"rwinchCustomer1","surname":"A description"}},{"customer":{"id":10,"name":"rwinchCustomer2","surname":"A description"}},{"customer":{"id":11,"name":"mfisherCustomer1","surname":"A description"}},{"customer":{"id":12,"name":"mfisherCustomer2","surname":"A description"}},{"customer":{"id":13,"name":"mpollackCustomer1","surname":"A description"}},{"customer":{"id":14,"name":"mpollackCustomer2","surname":"A description"}},{"customer":{"id":15,"name":"jlongCustomer1","surname":"A description"}},{"customer":{"id":16,"name":"jlongCustomer2","surname":"A description"}}]}}
```

In order to perform register modification permissions with curl, there is a test request that  modify the surname of a customer. 

Get the register of a customer. The createdBy and modifiedBy fields are null because they have been created at initialization time

```
$  curl http://127.0.0.1:8080/crmapi/customers/dsyerCustomer1 -H "Authorization: Bearer 7a43
6ea6-99d4-4405-9251-945691451852"
{"customer":{"id":3,"name":"dsyerCustomer1","surname":"A description","photo":"","createdBy":null,"modifiedBy":null}}
```

Now modify this register with the test request:

```
$  curl http://127.0.0.1:8080/crmapi/customers/modifytest/dsyerCustomer1 -H "Authorization:
Bearer 7a436ea6-99d4-4405-9251-945691451852"
```

Access to the register to verify the changes

```
 curl http://127.0.0.1:8080/crmapi/customers/dsyerCustomer1 -H "Authorization: Bearer 7a43
6ea6-99d4-4405-9251-945691451852"
{"customer":{"id":3,"name":"dsyerCustomer1","surname":"changed","photo":"","createdBy":null,"modifiedBy":"jlong"}}
```

The modifiedBy field has been updated too.  This later does not work in the unit tests probably because it needs an specific configuration.

More curl tests are coming.
