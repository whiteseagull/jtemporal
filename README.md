# jtemporal

JTemporal is an open-source, easy to use, framework of components providing functionality for time related applications. Provides basic functionality handling the most common temporal aspects (for the moment instant, period, mediators managing temporal associations). It allows easy design and implementation of time-related applications.

The cornerstone of the system is that Instant is an interface you implement. It depends on your business what the instant actually is. A particle physicist will not use the same kind of "instant" as a geologist. Also, java.util.Date is somewhat controversial, and many developers prefer other date frameworks, for immutability and date-only semantics.

JTemporal is aimed at replacing some common temporal design patterns [1] by reusable components.

JTemporal proposes an API hiding the way the data is stored (in-memory collections, relational or temporal database, or whatever) and gives some temporal facilities to non temporal storage.

Some functionalities are inspired by those provided by the temporal databases [2] and by SQL practice [3]. JTemporal tries to move the (redundant) temporal logic from the SQL to OO components. 

[1] Andy Carlson, Sharon Estepp and Martin Fowler, Pattern Languages of Program Design 4, Temporal Patterns, Addison Wesley, 1999.

[2] TSQL Temporal Query Language, Workshop on Temporal Databases Zürich 1995, TSQL2 and SQL3

[3] Richard T. Snodgrass, Developing Time-Oriented Database Applications in SQL, Morgan Kaufmann, 2000. Downloadable from his home page. 


Content taken from: http://jtemporal.sourceforge.net/
