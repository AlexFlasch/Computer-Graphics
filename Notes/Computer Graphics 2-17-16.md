# Computer Graphics 2/17/16
## Fractals
### IFS files

* They are a set of transformations.  
* Each line in the IFS file gives you a transformation

One line: *0.50 0.00 0.00 0.50 0.00 1.96 0.3401*  
This line results in the following matrix:  
{  
( 0.50, 0, 0 ),  
( 0, 0.50, 1.96 ),  
( 0, 0, 1)  
}

### Spray paint algorithm
* Compute the composite transformations for each of the child objects
* Pick any point and draw the point
* Loop until you determine you've drawn enough points
  * Draw the current point
  * Pick a transformation at random
  * Use the transformation to compute a new current point from the old point

### Draw-by-level algorithm
* Start with a seed polygon
* Using seed polygon, compute new polygons
* Recurse using new polygon as next seed polygon