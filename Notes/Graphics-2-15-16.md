# Graphics 2/15/16
## Transformations
**Transformation** - 3x3 matrix  
**Point** - 3x1 matrix in homogeneous coordinates

Any sequence of concatenated linear transformations is wrapped up in six magic numbers  
(*x*, *y*) -> (*Rzz* * *x* * *y* + *Tz*, *ryz* * *x* + *Ryy* * *y* + *Ty*)

Scaling  
{  
(*Sx*, 0, 0),  
(0, *Sy*, 0),  
(0, 0, 1)  
}

Translation  
{  
(1, 0, *Tx*),  
(0, 1, *Ty*),  
(0, 0, 1)  
}

Rotation  
{  
(*cosΘ*, *-sinΘ*, 0),  
(*sinΘ*, *cosΘ*, 0),  
(0, 0, 1)  
}

### Unit Circle
| Degrees | Cos       | Sin       |
| ------- | --------- | --------- |
| 0       | 1         | 0         |
| 30      | sqrt(3)/2 | 1/2       |
| 45      | sqrt(2)/2 | sqrt(2)/2 |
| 60      | 1/2       | sqrt(3)/2 |
| 90      | 0         | 1         |

## Fractals
**Fractal** - an image that has smaller scale replications of istelf within itself

### Ways to generate a fractal
**IFS (Iterative Function Systems)** - list of transformations to apply to an image in order to create a fractal  
