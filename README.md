# Showcase for zio and tAPIr integration

Example that demonstrates how to integrate tAPIr and not worry about picking http server library

Also playing with setting API version via HTTP header

```
❯ http localhost:8080/pets/1003
HTTP/1.1 400 Bad Request
Content-Length: 19
Content-Type: text/plain; charset=UTF-8
Date: Thu, 15 Apr 2021 02:19:29 GMT

Unknown pet id 1003


❯ http localhost:8080/pets/1003 Version:2.0
HTTP/1.1 200 OK
Content-Length: 69
Content-Type: application/json
Date: Thu, 15 Apr 2021 02:19:36 GMT

{
"species": "Platypus",
"url": "https://en.wikipedia.org/wiki/Platypus"
}
```