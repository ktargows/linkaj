(ns map-experiments.directed-graph.protocol)

(defprotocol Relational
  "Protocol for an object with relations and opposites which can be added or removed." 
  (relations       [o]       "Returns a bijection between all relations.")
  (related-in?     [o r1 r2] "Tells if two relations are related in the object.")
  (add-relation    [o r1 r2] "Adds the relation r1 & r2 to the object.")
  (remove-relation [o r1 r2] "Removes the specified relation from the object."))

(defprotocol Constrained
  "Protocol for an object with arbitrary constraints which can be added or removed."
  (constraints [o]
    "Returns a map of all constraints enforced by the object.")
  (add-constraint [o k f]
    "Adds a constraint function f to the object o with the key k. Future alterations to the object will be handed to the constraint function to verify and modify.")
  (remove-constraint [o k]
    "Removes constraint with key k from the object. Future alterations will not be checked against this constraint.")
  (assert-constraints [o]
    "Checks every constraint against every part of the object. Useful only in the rare situation when constraints are added to post-construction and are desired to be backwards-checked against the existing object."))

(defprotocol IDirectedGraph
  "Protocol for a directed graph."
  
  ; Methods acting on nodes:
  (nodes [graph] [graph query]
    "Returns all graph nodes matching the query.")
  (node? [graph x]
    "Returns true if x is a node key in the graph.")
  (get-node [graph node-key]
    "Returns a map of all attributes for node-key.")
  (add-node [graph attributes]
    "Adds a node with attributes to the graph.")
  (remove-node [graph node-key]
    "Removes all nodes in node-keys from the graph, as well as all edges which are directly connected to these nodes.")
  (assoc-node [graph node-key attributes]
    "Associates all nodes in node-keys with attributes.")
  (dissoc-node [graph node-key attribute-keys]
    "Dissociates all nodes in node-keys from attribute-keys.")
  
  ; Methods acting on edges:
  (edges [graph] [graph query]
    "Returns all graph edges matching the query.")
  (edge? [graph x]
    "Returns true if x is an edge in the graph.")
  (get-edge [graph edge-key]
    "Returns a map of all attributes for edge-key.")
  (add-edge [graph attributes]
    "Adds an edge with attributes to the graph. Attributes must contain exactly two relations, and they must be each others' opposites. If a relation points to a non-extant node, that node will be created.")
  (remove-edge [graph edge-key]
    "Removes all edges in edge-keys from the graph.")
  (assoc-edges [graph edge-key attributes]
    "Associates all edges in edge-keys with attributes. This may change relations if relations are used in attributes.")
  (dissoc-edges [graph edge-key attribute-keys]
    "Dissociates all edge in edge-keys from attribute-keys. Relations cannot be dissociated."))
