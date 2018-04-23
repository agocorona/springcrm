# springcrm
basic CRM API exercise. Implemented all the endpoints, Oauth-2.

Unit test works with

```
> mvn test
```

Authentication seems not to work very well with unit tests and Oauth2. It seems that using Oauth2 with automated tests allow access permission, but principal is null, so user validation does not work, neither  @createdBy or @lastModifiedBy directives (that need principal info). Performing the same requests by hand in the console with curl works well



obtain access token for the user jlong:

```

memet@DESKTOP-NCPHB6J MINGW64 ~/Desktop/springboot/springcrm (master)
$ curl -X POST -vu android-crmapi:123456 http://localhost:8080/oauth/token -H "Accept: appli
cation/json" -d "password=password&username=jlong&grant_type=password&scope=write&client_sec
ret=123456&client_id=android-crmapi"

{"access_token":"03f221c1-fad8-4748-98c3-37bcc041da7c","token_type":"bearer","refresh_token":"816dbc2b-463e-485e-9e29-175206817c0b","expires_in":43199,"scope":"write"}* Connection #0 to host localhost left intact

```
Using wrong access token
```
memet@DESKTOP-NCPHB6J MINGW64 ~/Desktop/springboot/springcrm (master)
$ curl -H "Authorization: Bearer d2317cf7-bfee-4a9e-a86f-19f84e64cc06" -H "Content-Type: app
lication/json" -X POST -d '{"name":"name","surname":"surname"}' http://localhost:8080/crmapi
/customers/add
{"error":"invalid_token","error_description":"Invalid access token: d2317cf7-bfee-4a9e-a86f-19f84e64cc06"}


```
right token, creating a customer
```
memet@DESKTOP-NCPHB6J MINGW64 ~/Desktop/springboot/springcrm (master)
$ curl -H "Authorization: Bearer 03f221c1-fad8-4748-98c3-37bcc041da7c" -H "Content-Type: app
lication/json" -X POST -d '{"name":"name","surname":"surname"}' http://localhost:8080/crmapi
/customers/add

```
verifying creation
```
memet@DESKTOP-NCPHB6J MINGW64 ~/Desktop/springboot/springcrm (master)
$ curl -H "Authorization: Bearer 03f221c1-fad8-4748-98c3-37bcc041da7c"  http://localhost:808
0/crmapi/customers/name
{"customer":{"id":17,"name":"name","surname":"surname","photo":"","createdBy":"jlong","modifiedBy":"jlong"}}

```
modify customer
```

memet@DESKTOP-NCPHB6J MINGW64 ~/Desktop/springboot/springcrm (master)
$ curl -H "Authorization: Bearer 03f221c1-fad8-4748-98c3-37bcc041da7c" -H "Content-Type: app
lication/json" -X POST -d '{"id":17,"name":"name","surname":"surname changed"}' http://local
host:8080/crmapi/customers/modify

```
verifying modification
```
memet@DESKTOP-NCPHB6J MINGW64 ~/Desktop/springboot/springcrm (master)
$ curl -H "Authorization: Bearer 03f221c1-fad8-4748-98c3-37bcc041da7c"  http://localhost:808
0/crmapi/customers/name
{"customer":{"id":17,"name":"name","surname":"surname changed","photo":"","createdBy":"jlong","modifiedBy":"jlong"}}

```
obtaining access token for jhoeller

```
memet@DESKTOP-NCPHB6J MINGW64 ~/Desktop/springboot/springcrm (master)
$ curl -X POST -vu android-crmapi:123456 http://localhost:8080/oauth/token -H "Accept: appli
cation/json" -d "password=password&username=jhoeller&grant_type=password&scope=write&client_
secret=123456&client_id=android-crmapi"

{"access_token":"d2826e00-37fe-4168-b025-c52e0bfc8a8f","token_type":"bearer","refresh_token":"5d019e45-eae0-4157-800c-2864d963e23e","expires_in":43199,"scope":"write"}* Connection #0 to host localhost left intact

```
modify the same customer
```
memet@DESKTOP-NCPHB6J MINGW64 ~/Desktop/springboot/springcrm (master)
$ curl -H "Authorization: Bearer d2826e00-37fe-4168-b025-c52e0bfc8a8f" -H "Content-Type: app
lication/json" -X POST -d '{"id":17,"name":"name","surname":"surname changed by jhoeller"}'
http://localhost:8080/crmapi/customers/modify

```
verifying that modifiedBy and surmane have changed
```


memet@DESKTOP-NCPHB6J MINGW64 ~/Desktop/springboot/springcrm (master)
$ curl -H "Authorization: Bearer 03f221c1-fad8-4748-98c3-37bcc041da7c"  http://localhost:808
0/crmapi/customers/name
{"customer":{"id":17,"name":"name","surname":"surname changed by jhoeller","photo":"","createdBy":"jlong","modifiedBy":"jhoeller"}}

```
trying to list account by a normal user
```




memet@DESKTOP-NCPHB6J MINGW64 ~/Desktop/springboot/springcrm (master)
$ curl -H "Authorization: Bearer d2826e00-37fe-4168-b025-c52e0bfc8a8f"  http://localhost:808
0/crmapi/accounts
{"timestamp":1524490189329,"status":500,"error":"Internal Server Error","exception":"crmapi.AccessDeniedException","message":"Access denied to user 'jhoeller'.","path":"/crmapi/accounts"}

```
obtaining token for admin

```
memet@DESKTOP-NCPHB6J MINGW64 ~/Desktop/springboot/springcrm (master)
$ curl -X POST -vu android-crmapi:123456 http://localhost:8080/oauth/token -H "Accept: appli
cation/json" -d "password=password&username=admin&grant_type=password&scope=write&client_sec
ret=123456&client_id=android-crmapi"

{"access_token":"8b788b7e-0d79-4a4a-aaa9-17d81d0a7238","token_type":"bearer","refresh_token":"38ae2a2a-c1dc-4b15-8356-34f6dad0d4d0","expires_in":43199,"scope":"write"}* Connection #0 to host localhost left intact

```
admin list the users: the admin user has the admin flag on:
```
memet@DESKTOP-NCPHB6J MINGW64 ~/Desktop/springboot/springcrm (master)
$ curl -H "Authorization: Bearer 8b788b7e-0d79-4a4a-aaa9-17d81d0a7238"  http://localhost:808
0/crmapi/accounts
[{"id":1,"username":"admin","password":"password","isAdmin":true},{"id":2,"username":"jhoeller","password":"password","isAdmin":false},{"id":3,"username":"dsyer","password":"password","isAdmin":false},{"id":4,"username":"pwebb","password":"password","isAdmin":false},{"id":5,"username":"ogierke","password":"password","isAdmin":false},{"id":6,"username":"rwinch","password":"password","isAdmin":false},{"id":7,"username":"mfisher","password":"password","isAdmin":false},{"id":8,"username":"mpollack","password":"password","isAdmin":false},{"id":9,"username":"jlong","password":"password","isAdmin":false}]

```
add a new user
```


$ curl -H "Authorization: Bearer 8b788b7e-0d79-4a4a-aaa9-17d81d0a7238" -H "Content-Type: app
lication/json" -X POST -d '{"username":"newUser","password":"password", "isAdmin":false}' ht
tp://localhost:8080/crmapi/accounts/add
{"id":10,"username":"newUser","password":"password","isAdmin":false}

```
modify this user, change password and make it administrator
```
memet@DESKTOP-NCPHB6J MINGW64 ~/Desktop/springboot/springcrm (master)
$ curl -H "Authorization: Bearer 8b788b7e-0d79-4a4a-aaa9-17d81d0a7238" -H "Content-Type: app
lication/json" -X POST -d '{"username":"newUser","password":"newpassword", "isAdmin":true}'
http://localhost:8080/crmapi/accounts/modify

```
list accounts: the las register has the new user modified

```
memet@DESKTOP-NCPHB6J MINGW64 ~/Desktop/springboot/springcrm (master)
$ curl -H "Authorization: Bearer 8b788b7e-0d79-4a4a-aaa9-17d81d0a7238"  http://localhost:808
0/crmapi/accounts
[{"id":1,"username":"admin","password":"password","isAdmin":true},{"id":2,"username":"jhoeller","password":"password","isAdmin":false},{"id":3,"username":"dsyer","password":"password","isAdmin":false},{"id":4,"username":"pwebb","password":"password","isAdmin":false},{"id":5,"username":"ogierke","password":"password","isAdmin":false},{"id":6,"username":"rwinch","password":"password","isAdmin":false},{"id":7,"username":"mfisher","password":"password","isAdmin":false},{"id":8,"username":"mpollack","password":"password","isAdmin":false},{"id":9,"username":"jlong","password":"password","isAdmin":false},{"id":10,"username":"newUser","password":"newpassword","isAdmin":true}]

```
delete this user
```
memet@DESKTOP-NCPHB6J MINGW64 ~/Desktop/springboot/springcrm (master)
$ curl -X DELETE -H "Authorization: Bearer 8b788b7e-0d79-4a4a-aaa9-17d81d0a7238"  http://loc
alhost:8080/crmapi/accounts/newUser

```
verifying deletion

```

memet@DESKTOP-NCPHB6J MINGW64 ~/Desktop/springboot/springcrm (master)
$ curl -H "Authorization: Bearer 8b788b7e-0d79-4a4a-aaa9-17d81d0a7238"  http://localhost:808
0/crmapi/accounts
[{"id":1,"username":"admin","password":"password","isAdmin":true},{"id":2,"username":"jhoeller","password":"password","isAdmin":false},{"id":3,"username":"dsyer","password":"password","isAdmin":false},{"id":4,"username":"pwebb","password":"password","isAdmin":false},{"id":5,"username":"ogierke","password":"password","isAdmin":false},{"id":6,"username":"rwinch","password":"password","isAdmin":false},{"id":7,"username":"mfisher","password":"password","isAdmin":false},{"id":8,"username":"mpollack","password":"password","isAdmin":false},{"id":9,"username":"jlong","password":"password","isAdmin":false}]
```
