# diff tool poc

## how to

- run the image:

`docker run -p 8088:8088 nbittich/diff-tool`

- go to `http://localhost:8088`
- upload two rdf models 
- click submit
- you get a zip file containing the intersection (same triples), difference between a & b, difference between b & a
