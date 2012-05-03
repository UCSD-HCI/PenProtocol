//!SCHEMA PaperPoint;

//!  activeComponent: org.ximtec.iserver.core.resource.ActiveComponent;
//!  entity: org.ximtec.iserver.core.Entity;
//!  group: org.ximtec.iserver.core.Group;
//!  individual: org.ximtec.iserver.core.Individual;
//!  layer: org.ximtec.iserver.core.Layer;
//!  link: org.ximtec.iserver.core.Link;
//!  medium: org.ximtec.iserver.core.Medium;
//!  omswe: org.ximtec.iserver.core.resource.OMSwe;
//!  resource: org.ximtec.iserver.core.Resource;
//!  complexResource: org.ximtec.iserver.core.ComplexResource;
//!  selector: org.ximtec.iserver.core.Selector;
//!  template: org.ximtec.iserver.core.resource.Template;
//!  textComp: org.ximtec.iserver.core.TextComp;
//!  User: org.ximtec.iserver.core.User;
//!  parameter: org.ximtec.iserver.core.Parameter;

//!  circle: org.ximtec.ipaper.graphics2D.Circle;
//!  complexShape: org.ximtec.ipaper.graphics2D.ComplexShape;
//!  ellipse: org.ximtec.ipaper.graphics2D.Ellipse;
//!  polygon: org.ximtec.ipaper.graphics2D.Polygon;
//!  rectangle: org.ximtec.ipaper.graphics2D.Rectangle;
//!  shape: org.ximtec.ipaper.graphics2D.Shape;

//!  dimension: org.sigtec.om.graphics2D.Dimension;
//!  point: org.sigtec.om.graphics2D.Point;

//!  document: org.ximtec.ipaper.core.Document;
//!  page: org.ximtec.ipaper.core.Page;

//!  button: org.ximtec.ipaper.core.resource.Button;

type  layer 
(  name            : string;
);

type  User 
(  name            : string;
   description     : string;
);

type  individual  subtype of  User
(  login           : string;
   password        : string;
);

type  group  subtype of  User
();

type  point 
(  x               : integer;
   y               : integer;
);

type  dimension 
(  width           : integer;
   height          : integer;
);

type  parameter 
(  name            : string;
   value           : string;
);

type  entity 
(  name            : string;
   properties      : set of  (string,string);
   anchors         : (  ) -> ( anchors: set of  entity );
);

type  link  subtype of  entity
();

type  selector  subtype of  entity
();

type  resource  subtype of  entity
();

type  shape  subtype of  selector
();

type  page  subtype of  resource
(  number          : integer;
   size            : dimension;
);

type  document  subtype of  resource
(  id              : string;
   size            : dimension;
   content         : mime;
);

type  textComp  subtype of  resource
(  content         : string;
);

type  medium  subtype of  resource
(  description     : string;
   content         : mime;
);

type  activeComponent  subtype of  resource
(  identifier      : string;
   timeout         : integer;
);

type  template  subtype of  resource
();

type  rectangle  subtype of  shape
(  upperLeft       : point;
   size            : dimension;
);

type  polygon  subtype of  shape
(  points          : ranking of  point;
);

type  ellipse  subtype of  shape
(  centre          : point;
   width           : integer;
   height          : integer;
);

type  circle  subtype of  shape
(  centre          : point;
   radius          : integer;
);

type  complexShape  subtype of  shape
();


collection  AccessibleTo    : set of  (entity,User);
collection  ActiveComponents: set of  activeComponent;
collection  ActiveLayers    : set of  layer;
collection  Circles         : set of  circle;
collection  ComplexShapes   : set of  complexShape;
collection  Containers      : set of  resource;
collection  Contains        : set of  (resource,resource);
collection  ContainsPages   : set of  (document,page);
collection  CreatedBy       : set of  (entity,individual);
collection  Dimensions      : set of  dimension;
collection  Documents       : set of  document;
collection  Ellipses        : set of  ellipse;
collection  Entities        : set of  entity;
collection  Groups          : set of  group;
collection  HasMembers      : set of  (group,User);
collection  HasPreferences  : set of  (User,parameter);
collection  HasShapes       : set of  (complexShape,shape);
collection  HasSource       : set of  (link,entity);
collection  HasTarget       : set of  (link,entity);
collection  HasTemplate     : set of  (resource,template);
collection  Images          : set of  medium;
collection  InaccessibleTo  : set of  (entity,User);
collection  Individuals     : set of  individual;
collection  Layers          : ranking of  layer;
collection  Links           : set of  link;
collection  Media           : set of  medium;
collection  Movies          : set of  medium;
collection  OnLayer         : set of  (selector,layer);
collection  OnPage          : set of  (shape,page);
collection  Pages           : set of  page;
collection  Parameters      : set of  parameter;
collection  Points          : set of  point;
collection  Polygons        : set of  polygon;
collection  Preferences     : set of  parameter;
collection  Rectangles      : set of  rectangle;
collection  RefersTo        : set of  (selector,resource);
collection  Resources       : set of  resource;
collection  Selectors       : set of  selector;
collection  Shapes          : set of  shape;
collection  Templates       : set of  template;
collection  Texts           : set of  textComp;
collection  Users           : set of  User;
collection  Webpages        : set of  medium;


constraint  AccessibleTo    association from  Entities (0 : *)  to Users (0 : *);
constraint  Contains        association from  Containers (1 : *)  to Resources (0 : *);
constraint  ContainsPages   association from  Documents (0 : *)  to Pages (1 : 1);
constraint  CreatedBy       association from  Entities (1 : 1)  to Individuals (0 : *);
constraint  HasMembers      association from  Groups (0 : *)  to Users (0 : *);
constraint  HasPreferences  association from  Users (0 : *)  to Preferences (0 : *);
constraint  HasShapes       association from  ComplexShapes (2 : *)  to Shapes (0 : *);
constraint  HasSource       association from  Links (1 : *)  to Entities (0 : *);
constraint  HasTarget       association from  Links (1 : *)  to Entities (0 : *);
constraint  HasTemplate     association from  Resources (0 : *)  to Templates (0 : *);
constraint  InaccessibleTo  association from  Entities (0 : *)  to Users (0 : *);
constraint  OnLayer         association from  Selectors (1 : 1)  to Layers (0 : *);
constraint  OnPage          association from  Shapes (1 : 1)  to Pages (0 : *);
constraint  RefersTo        association from  Selectors (1 : 1)  to Resources (0 : *);

constraint  ActiveComponents subcollection of  Resources;
constraint  ActiveLayers    subcollection of  Layers;
constraint  Circles         subcollection of  Shapes;
constraint  ComplexShapes   subcollection of  Shapes;
constraint  Containers      subcollection of  Resources;
constraint  ContainsPages   subcollection of  Contains;
constraint  Documents       subcollection of  Containers;
constraint  Ellipses        subcollection of  Shapes;
constraint  Groups          subcollection of  Users;
constraint  Images          subcollection of  Media;
constraint  Individuals     subcollection of  Users;
constraint  Links           subcollection of  Entities;
constraint  Media           subcollection of  Resources;
constraint  Movies          subcollection of  Media;
constraint  OnPage          subcollection of  RefersTo;
constraint  Pages           subcollection of  Resources;
constraint  Polygons        subcollection of  Shapes;
constraint  Rectangles      subcollection of  Shapes;
constraint  Resources       subcollection of  Entities;
constraint  Selectors       subcollection of  Entities;
constraint  Shapes          subcollection of  Selectors;
constraint  Templates       subcollection of  Resources;
constraint  Texts           subcollection of  Resources;
constraint  Webpages        subcollection of  Media;

constraint  (ComplexShapes and Circles and Ellipses and Polygons and Rectangles)  partition  Shapes;
constraint  (Resources and Selectors and Links)  partition  Entities;
constraint  (Groups and Individuals)  partition  Users;

//!END PaperPoint;
