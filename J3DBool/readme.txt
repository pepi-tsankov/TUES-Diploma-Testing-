UNBBOOLEAN
by Danilo Balby (danbalby@yahoo.com)
Homepage: http://unbboolean.sourceforge.net/

INTRODUCTION:
UnBBoolean's Library for Boolean Set Operations (j3dbool) is an API to apply boolean set operations on solids on Java 3D applications.  	

COMPILED WITH:
Java Runtime Environment (JRE) v6 and Java3D v1.5.2

PRECONDITION:
The solid faces are classified according to the other solid surface. So, the solid faces must compose a surface: they must have their normals pointing "outside" the solid defined by them and they must cover the entire surface. Besides, all the solid faces are understood by the algorithm as being part of its surface, so, they must be there. Submit solids that don't consider the preconditions will result in a new solid with dangling faces in its surface. 

HOW TO USE:
1 - Create a 'Solid' object. It may be created by submitting to its constructors data arrays (colors, indices and vertices) or a file containing the coordinates (see 'coordinates' folder on UnBBoolean's application). The data arrays (or the arrays obtained by the file) must be able to be accepted by a IndexedGeometryArray without errors to work, otherwise, an exception is thrown.

2 - Submit two solids created as above to the 'BooleanModeller' constructor. As below:
	
	BooleanModeller bm = new BooleanModeller(solid1, solid2);

3 - Get the solid resulting from the application of the desired boolean operation. As below:

	bm.getUnion();		//for union
	bm.getIntersection();	//for intersection
	bm.getDifference();	//for difference

4 - Use the resulting solid as you like. You can deal with the own solid, adding it into a scene graph and editing its appearance and other features (since it descends from Shape3d), or you can get the data arrays and use then in another way.

