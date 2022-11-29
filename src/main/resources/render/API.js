const RELATION = {
    UNKNOWN:"",
    ZeroToOne:"0:1",
    ZeroToMany:"0:N",
    OneToOne:"1:1",
    OneToMany:"1:N"
}
function findRelationName(indexs){
    return Object.values(RELATION)[indexs];
}

const ENTITYTYPE=["","entity","weakEntity","subset","attribute"]

const DATATYPE = {
    UNKNOWN:0,
    CHAR:1,
    VARCHAR:2,
    TEXT:3,
    TINYINT:4,
    SMALLINT:5,
    INT:6,
    BIGINT:7,
    FLOAT:8,
    DOUBLE:9,
    DATETIME:10
}

function is_empty(item){
    return typeof item ==='undefined' || item === null;
}

const weakEntityNodeCategory = ENTITYTYPE[2]
const entityNodeCategory = ENTITYTYPE[1]
const subsetEntityNodeCategory = ENTITYTYPE[3]
const relationNodeCategory = "relation"
const ERLinkCategory = "entityLink";
const relationNodeName = "test";



const colors =
    {
        'lightblue': '#afd4fe',
        'lightgrey': '#a4a8ad',
        'lightyellow': '#fcffbe'
    }
// Common text styling
function textStyle() {
    return {
        margin: 6,
        wrap: go.TextBlock.WrapFit,
        textAlign: "center",
        editable: true,
    }
}


function showSchema(data){
    let isInitial = getInitialParameter(data);
    defineModel(isInitial);
    getSchema(data);
}

function getInitialParameter(data){
    let flag = false;
    let entities = data.schema.entityList;
    if (!is_empty(entities)) {
        for (let i = 0; i < entities.length; i++) {
            let entityNode = entities[i];
            if (is_empty(entityNode.layoutInfo)) {
                flag = true;
            }
            let attributeList = entityNode.attributeList;
            if (!is_empty(attributeList)) {
                for (let j = 0; j < attributeList.length; j++) {
                    let attributeNode = attributeList[j];
                    if (is_empty(attributeNode.layoutInfo)) {
                        flag = true;
                    }
                }
            }
        }
    }

    let relationList = data.schema.relationshipList;
    if (!is_empty(relationList)) {
        for (let i = 0; i < relationList.length; i++) {
            let relationNode = relationList[i];
            if (is_empty(relationNode.layoutInfo)) {
                flag = true;
            }

            let attributeList = relationNode.attributeList;

            if (!is_empty(attributeList)) {
                for (let j = 0; j < attributeList.length; j++) {
                    let attributeNode = attributeList[j];
                    if (is_empty(attributeNode.layoutInfo)) {
                        flag = true;
                    }
                }
            }
        }
    }
    return flag;
}

function defineModel(isInitial){

    const $ = go.GraphObject.make;  // for conciseness in defining templates
    APIDiagram = $(go.Diagram, "model",  // must name or refer to the DIV HTML element
        {
            allowDelete: true,
            allowCopy: false,
            initialAutoScale: go.Diagram.Uniform,
            layout: $(go.LayeredDigraphLayout, {isInitial: isInitial, isOngoing: false}),
            "draggingTool.dragsLink": false,
            "draggingTool.isGridSnapEnabled": false,
            "undoManager.isEnabled": true,
            "maxSelectionCount": 1
        });

    /*
     4 ports
     */

    function leftPort(){
        // L port
        return $(go.Panel, "Vertical", {row: 1, column: 0},
            $(go.Shape, {width: 3, height: 3, portId: 3, toSpot: go.Spot.Left,fromSpot:go.Spot.Left,
                fromLinkable: true,toLinkable: true
            }));
    }
    function rightPort(){
        // R port
        return $(go.Panel, "Vertical", {row: 1, column: 2},
            $(go.Shape,  {width: 3, height: 3, portId: 4, toSpot: go.Spot.Right,fromSpot:go.Spot.Right,
                fromLinkable: true,toLinkable: true}));
    }
    function bottomPort(){
        // B port
        return $(go.Panel, "Horizontal", {row:2, column: 1},
            $(go.Shape, {width: 3, height: 3, portId: 2, toSpot: go.Spot.Bottom,fromSpot:go.Spot.Bottom,
                fromLinkable: true,toLinkable: true}));
    }
    function topPort(){
        // U port
        return $(go.Panel, "Vertical",{row: 0, column: 1},
            $(go.Shape, {width: 3, height: 3, portId: 1, toSpot: go.Spot.Top,fromSpot:go.Spot.Top,
                fromLinkable: true,toLinkable: true}));
    }

    /*
        All Node(Entity+Attribute) templates
     */

    go.Shape.defineFigureGenerator("WeakEntity", function(shape, w, h) {
        let geo = new go.Geometry();
        let fig = new go.PathFigure(0.05*w,0.05*w, true);  // clockwise

        geo.add(fig);

        if (w>h){
            fig.add(new go.PathSegment(go.PathSegment.Line, 0.05*w,h-0.05*w)); //下划线到h点
            fig.add(new go.PathSegment(go.PathSegment.Line, 0.95*w,h-0.05*w));//在0.h 画到 1.6w,h
            fig.add(new go.PathSegment(go.PathSegment.Line, 0.95*w,0.05*w));
            fig.add(new go.PathSegment(go.PathSegment.Line, 0.05*w,0.05*w).close());

        }else{
            fig.add(new go.PathSegment(go.PathSegment.Line, 0.05*h,w-0.05*h)); //下划线到h点
            fig.add(new go.PathSegment(go.PathSegment.Line, 0.95*h,w-0.05*h));//在0.h 画到 1.6w,h
            fig.add(new go.PathSegment(go.PathSegment.Line, 0.95*h,0.05*h));
            fig.add(new go.PathSegment(go.PathSegment.Line, 0.05*h,0.05*h).close());

        }

        fig.add(new go.PathSegment(go.PathSegment.Move, 0,0));
        fig.add(new go.PathSegment(go.PathSegment.Line, 0,h));
        fig.add(new go.PathSegment(go.PathSegment.Line, w,h));
        fig.add(new go.PathSegment(go.PathSegment.Line, w,0));
        fig.add(new go.PathSegment(go.PathSegment.Line, 0,0));
        return geo;
    });

    //strong entity template
    const entityTemplate =
        $(go.Node, "Table",  // the whole node panel
            {
                locationObject: "BODY",
                locationSpot: go.Spot.Center,
                selectionObjectName: "BODY",
            },
            {
                locationSpot: go.Spot.Center,
                selectionAdorned: true,
                resizable: false,
                layoutConditions: go.Part.LayoutStandard & ~go.Part.LayoutNodeSized,
                isShadowed: true,
                shadowOffset: new go.Point(3, 3),
                shadowColor: colors.lightblue,
            },
            new go.Binding("location", "location").makeTwoWay(),
            new go.Binding("desiredSize", "visible", new go.Size(NaN, NaN)).ofObject("LIST"),
            // the body
            $(go.Panel, "Auto",
                {
                    row: 1, column: 1, name: "BODY",
                },
                $(go.Shape, "RoundedRectangle",
                    {
                        fill: 'white',
                        portId: "",
                        stroke: colors.lightblue,
                        cursor: "pointer",
                        fromSpot: go.Spot.AllSides,
                        toSpot: go.Spot.AllSides,
                        strokeWidth: 3,
                        fromLinkableDuplicates: false, toLinkableDuplicates: false
                    }),
                $(go.TextBlock, textStyle(),
                    {
                        row: 0, alignment: go.Spot.Center,
                        margin: new go.Margin(5, 24, 5, 2),  // leave room for Button
                        font: "bold 16px sans-serif",
                        editable: true
                    },
                    new go.Binding("text", "name").makeTwoWay()),
            ),
            //port
            leftPort(),rightPort(),topPort(),bottomPort()
        );


    //relationNodeTemplate
    const relationTemplate =
        $(go.Node, "Table",
            {
                locationObject: "BODY",
                locationSpot:go.Spot.Center,
                selectionObjectName: "BODY"
            },
            {
                locationSpot: go.Spot.Center,
                selectionAdorned: true,
                resizable: false,
                layoutConditions: go.Part.LayoutStandard & ~go.Part.LayoutNodeSized,
                isShadowed: true,
                shadowOffset: new go.Point(3, 3),
                shadowColor: colors.lightblue,
            },
            new go.Binding("location", "location").makeTwoWay(),
            new go.Binding("desiredSize", "visible", new go.Size(NaN, NaN)).ofObject("LIST"),
            $(go.Panel,"Auto",
                {row: 1, column: 1, name: "BODY"},
                $(go.Shape, "Diamond",
                    {
                        fill: colors.lightyellow,
                        portId: "",
                        stroke: colors.lightblue,
                        cursor: "pointer",
                        fromSpot: go.Spot.AllSides,
                        toSpot: go.Spot.AllSides,
                        strokeWidth: 3,
                        // width: 100,
                        height: 50,
                        fromLinkableDuplicates: false, toLinkableDuplicates: false
                    },
                    new go.Binding("fromLinkable", "from").makeTwoWay(), new go.Binding("toLinkable", "to").makeTwoWay()),
                $(go.Panel, "Table",
                    { margin: 8, stretch: go.GraphObject.Fill },
                    $(go.RowColumnDefinition, { row: 0, sizing: go.RowColumnDefinition.None }),
                    $(go.TextBlock,textStyle(),
                        {
                            row: 0,
                            alignment: go.Spot.Center,
                            font: "bold 16px sans-serif",
                            editable: true
                        },
                        new go.Binding("text", "name").makeTwoWay()))
            ),
            //port
            leftPort(),rightPort(),topPort(),bottomPort()
        );

    // weak entity template
    const weakEntityTemplate =
        $(go.Node, "Table",  // the whole node panel
            {
                locationObject: "BODY",
                locationSpot:go.Spot.Center,
                selectionObjectName: "BODY"
            },
            {
                locationSpot: go.Spot.Center,
                selectionAdorned: true,
                resizable: false,
                layoutConditions: go.Part.LayoutStandard & ~go.Part.LayoutNodeSized,
                isShadowed: true,
                shadowOffset: new go.Point(3, 3),
                shadowColor: colors.lightblue,
            },
            new go.Binding("location", "location").makeTwoWay(),
            new go.Binding("desiredSize", "visible", new go.Size(NaN, NaN)).ofObject("LIST"),
            $(go.Panel,"Auto",
                {
                    row: 1, column: 1, name: "BODY",
                },
                $(go.Shape, weakEntityNodeCategory,
                    {
                        fill: 'white',
                        portId: "",
                        stroke: colors.lightgrey,
                        cursor: "pointer",
                        fromSpot: go.Spot.AllSides,
                        toSpot: go.Spot.AllSides,
                        strokeWidth: 3,
                        fromLinkableDuplicates: false, toLinkableDuplicates: false,
                    }),
                $(go.TextBlock, textStyle(),
                    {
                        row: 0, alignment: go.Spot.Center,
                        margin: new go.Margin(5, 24, 5, 2),  // leave room for Button
                        font: "bold 16px sans-serif",
                        editable: true
                    },
                    new go.Binding("text", "name").makeTwoWay()),
            ), //end Auto Panel Body
            //port
            leftPort(),rightPort(),topPort(),bottomPort()
        );

    // subset template
    const subsetTemplate =
        $(go.Node, "Table",  // the whole node panel
            {
                locationObject: "BODY",
                locationSpot: go.Spot.Center,
                selectionObjectName: "BODY",
            },
            {
                locationSpot: go.Spot.Center,
                selectionAdorned: true,
                resizable: false,
                layoutConditions: go.Part.LayoutStandard & ~go.Part.LayoutNodeSized,
                isShadowed: true,
                shadowOffset: new go.Point(3, 3),
                shadowColor: colors.lightgrey,
            },
            new go.Binding("location", "location").makeTwoWay(),
            new go.Binding("desiredSize", "visible", new go.Size(NaN, NaN)).ofObject("LIST"),
            // the table header
            $(go.Panel, "Auto",
                {row: 1, column: 1, name: "BODY"},
                $(go.Shape, "RoundedRectangle",
                    {
                        fill:"#e8c446",
                        portId: "",
                        stroke: "#e8c446",
                        cursor: "pointer",
                        fromSpot: go.Spot.AllSides,
                        toSpot: go.Spot.AllSides,
                        strokeWidth: 3,
                        fromLinkableDuplicates: false, toLinkableDuplicates: false
                    },
                    new go.Binding("fromLinkable", "from").makeTwoWay(), new go.Binding("toLinkable", "to").makeTwoWay()),
                $(go.TextBlock,textStyle(),
                    {
                        row: 0, alignment: go.Spot.Center,
                        margin: new go.Margin(5, 24, 5, 2),  // leave room for Button
                        font: "bold 16px sans-serif",
                        editable: true
                    },
                    new go.Binding("text", "name").makeTwoWay()),
                $(go.RowColumnDefinition, { row: 0, sizing: go.RowColumnDefinition.None }),
            ), // end Table Panel
            //port
            $(go.Panel, "Vertical", {row: 1, column: 1},
                $(go.Shape, {width: 0, height: 0, portId: 5,
                    fromLinkable: true,toLinkable: true,
                    fill: "#e8c446",stroke: "#e8c446",
                })),
        );

    // attribute template
    const attributeTemplate = $(go.Node, "Table",
        {
            locationObject: "MAINBODY",
            locationSpot: go.Spot.Center,
            selectionObjectName: "MAINBODY"
        },
        {
            selectionAdorned: true,
            // selectionAdornmentTemplate: attributeAdornment,
            resizable: false,
            layoutConditions: go.Part.LayoutStandard & ~go.Part.LayoutNodeSized,
            // movable: false,
        },
        new go.Binding("location", "location").makeTwoWay(),
        // textbox
        $(go.Panel, "Table",
            {row: 0, column: 1, name: "AttributeName"},
            $(go.TextBlock,
                {row: 0, column: 0},
                {
                    font: "bold 12px monospace",
                    margin: new go.Margin(0, 0, 0, 0), // leave room for Button
                    editable: true
                },
                new go.Binding("text", "name").makeTwoWay(),
                new go.Binding("isUnderline", "underline"),
            ),
            $(go.TextBlock,
                {row: 0, column: 1},
                {
                    font: "bold 12px monospace",
                    editable: false,
                },
                new go.Binding("text", "icon").makeTwoWay(),
            ),
        ),
        new go.Binding("isOptional").makeTwoWay(),
        new go.Binding("isMultiValue").makeTwoWay(),
        $(go.Panel, "Table",
            {row: 0, column: 0, name: "MAINBODY"},
            $(go.Panel, "Auto",
                {row: 1, column: 1},
                $(go.Shape, "Circle",
                    {
                        fill: colors.lightblue,
                        portId: "",
                        stroke: colors.lightblue,
                        cursor: "pointer",
                        fromSpot: go.Spot.AllSides,
                        toSpot: go.Spot.AllSides,
                        strokeWidth: 2,
                        fromLinkableDuplicates: false, toLinkableDuplicates: false,
                        desiredSize: new go.Size(10, 10),
                    }
                ),
            ),
            //port
            $(go.Panel, "Vertical", {row: 1, column: 1},
                $(go.Shape, {
                    width: 3, height: 3, portId: 5,
                    fromLinkable: true, toLinkable: true,
                    fill: colors.lightblue, stroke: colors.lightblue,
                })
            )
        )
    );

    // add all node template
    var templateMap = new go.Map();
    // default template
    APIDiagram.nodeTemplate = entityTemplate;
    templateMap.add("", entityTemplate);
    templateMap.add(entityNodeCategory, entityTemplate);
    templateMap.add(weakEntityNodeCategory, weakEntityTemplate);
    templateMap.add(subsetEntityNodeCategory, subsetTemplate);
    templateMap.add("Attribute", attributeTemplate);
    templateMap.add("relation_attribute", attributeTemplate);

    templateMap.add(relationNodeCategory, relationTemplate);

    APIDiagram.nodeTemplateMap = templateMap;

    const normalLink = $(go.Link,
        {
            selectionAdorned: true,
            layerName: "Foreground",
            // reshapable: true,
            // routing: go.Link.AvoidsNodes,
            // corner: 5,
            curve: go.Link.JumpOver,
            relinkableFrom: true,
            relinkableTo: true,
            deletable: false
        },
        $(go.Shape,  // the link shape
            {stroke: colors.lightblue, strokeWidth: 2.5 }),
        $(go.TextBlock, textStyle(), // the "from" label
            {
                textAlign: "center",
                font: "bold 14px sans-serif",
                stroke: colors.lightblue,
                segmentIndex: 0,
                segmentOffset: new go.Point(NaN, NaN),
                segmentOrientation: go.Link.OrientUpright
            },
            new go.Binding("text", "fromText").makeTwoWay()),
    );

    var subsetLink = $(go.Link,
        {
            deletable: false,
            selectionAdorned: true,
            layerName: "Foreground",
            reshapable: true,
            routing: go.Link.AvoidsNodes,
            corner: 5,
            curve: go.Link.JumpOver,
            relinkableFrom: true,
            relinkableTo: true
        },
        $(go.Shape,  // the link shape
            {stroke: "#e8c446", strokeWidth: 2.5 }),
        $(go.Shape,   // the arrowhead
            { toArrow: "OpenTriangle", fill: null, stroke:"#e8c446",strokeWidth:2.5}),
    );

    const entityLink = $(go.Link,
        {
            selectionAdorned: true,
            layerName: "Foreground",
            reshapable: true,
            routing: go.Link.AvoidsNodes,
            corner: 5,
            curve: go.Link.JumpOver,
            relinkableFrom: true,
            relinkableTo: true
        },
        $(go.Shape,  // the link shape
            {stroke: "#000000", strokeWidth: 2.5}),
        $(go.TextBlock, textStyle(), // the "from" label
            {
                textAlign: "center",
                font: "bold 14px sans-serif",
                stroke: "#1967B3",
                segmentIndex: 0,
                segmentOffset: new go.Point(NaN, NaN),
                segmentOrientation: go.Link.OrientUpright
            },
            new go.Binding("text", "fromText").makeTwoWay(),
            new go.Binding("isUnderline", "underline")),
    );


    const linkTemplateMap = new go.Map();
    linkTemplateMap.add("normalLink", normalLink);
    linkTemplateMap.add("subsetLink", subsetLink);
    linkTemplateMap.add(ERLinkCategory, entityLink);
    // default
    linkTemplateMap.add("", entityLink);
    APIDiagram.linkTemplateMap = linkTemplateMap;

    APIDiagram.model = new go.GraphLinksModel(
        {
            linkFromPortIdProperty: "fromPort",
            linkToPortIdProperty: "toPort",
            nodeDataArray: [],
            linkDataArray: []
        });
}

function getSchema(data){

    APIDiagram.model = new go.GraphLinksModel(
        { linkFromPortIdProperty: "fromPort",
            linkToPortIdProperty: "toPort",
            nodeDataArray: [],
            linkDataArray: []
        });

    let subsetLinkMap = new Map(); //key is subset id, value is strong entity id

    const entityList = data.schema.entityList;
    const relationshipList = data.schema.relationshipList;

    if(!is_empty(entityList)) {
        for (let i = 0; i < entityList.length; i++) {
            // add entity node
            let entityData = entityList[i]

            let node = {
                key: entityData.id, name: entityData.name, category: ENTITYTYPE[entityData.entityType],
                from: true, to: true
            };

            if (!is_empty(entityData.layoutInfo)) {
                node.location = {
                    "class": "go.Point",
                    "x": entityData.layoutInfo.layoutX,
                    "y": entityData.layoutInfo.layoutY
                };
            }
            if (node.category === subsetEntityNodeCategory) {
                // add link
                if (!is_empty(node.belongStrongEntityID)) {
                    let aimPort = entityData.aimPort;
                    if (aimPort === -1) {
                        aimPort = 2;
                    }
                    subsetLinkMap.set(node.key, [entityData.belongStrongEntityID, aimPort]);
                }
            }


            APIDiagram.model.addNodeData(node);

            let attributeList = entityData.attributeList;
            if (!is_empty(attributeList)) {
                for (let j = 0; j < attributeList.length; j++) {
                    // step 1: get attribute Info
                    let attributeData = attributeList[j];
                    let attributeNode = extractAttributeInfo(attributeData, node);
                    APIDiagram.model.addNodeData(attributeNode);
                    //step 2: add normal link data and attribute data
                    let normalLinkData = {
                        "from": node.key, "to": attributeNode.key, "category": "normalLink",
                        "fromPort": 4, "toPort": 5
                    }
                    //todo remove?
                    if (attributeData.aimPort !== -1) {
                        normalLinkData.fromPort = attributeData.aimPort;
                    }
                    if (node.category === subsetEntityNodeCategory) {
                        normalLinkData.fromPort = 5;
                    }
                    APIDiagram.model.addLinkData(normalLinkData);
                }
            }
        }
    }

    /*
        add subset links
     */

    for (let [key, value] of subsetLinkMap) {
        let subsetLinkData = {"from":key,"to":parseInt(value[0]),"fromPort":5,"toPort":value[1], "category":"subsetLink"}
        if (value[1] === -1) {
            subsetLinkData.toPort = 2
        }
        APIDiagram.model.addLinkData(subsetLinkData);
    }


    if(!is_empty(relationshipList)) {
        for (let i = 0; i < relationshipList.length; i++) {
            let relationData = relationshipList[i];
            // step 1: add relationship nodes
            let relationNode = {
                "key": "relation_" + relationData.id, "name": relationData.name,
                "category": "relation", "from": true, "to": true
            };

            if (!is_empty(relationData.layoutInfo)) {
                relationNode.location = {
                    "class": "go.Point",
                    "x": relationData.layoutInfo.layoutX,
                    "y": relationData.layoutInfo.layoutY
                };
            }
            APIDiagram.model.addNodeData(relationNode);
            // step 2: add relationship attributes
            let attributeList = relationData.attributeList;
            if (!is_empty(attributeList)) {
                for (let j = 0; j < attributeList.length; j++) {
                    const attributeData = attributeList[j];
                    // step 2.1: get attribute Info
                    let attributeNode = extractAttributeInfo(attributeData, relationNode)
                    //step 2.2: add normal link data and attribute data
                    APIDiagram.model.addNodeData(attributeNode);

                    let normalLinkData = {
                        "from": relationNode.key, "to": attributeNode.key, "category": "normalLink",
                        "fromPort": 4, "toPort": 5
                    }
                    if (attributeNode.aimPort !== -1) {
                        normalLinkData.fromPort = attributeData.aimPort;
                    }
                    APIDiagram.model.addLinkData(normalLinkData);
                }
            }
        }
    }

    /*
    create edges
     */
    if(!is_empty(relationshipList)) {
        for (let i = 0; i < relationshipList.length; i++) {
            const relationData = relationshipList[i]
            const relationEdgeList = relationData.edgeList;
            let counter = 1;
            if(!is_empty(relationEdgeList)) {
                for (let j = 0; j < relationEdgeList.length; j++) {
                    let edgeData = relationEdgeList[j];
                    let edgeLinkData = {"key":edgeData.id,"from":edgeData.belongObjID,"to":"relation_"+relationData.id,
                        "fromText":findRelationName(edgeData.cardinality), "category":"entityLink"};
                    // set the from key, catch when it from one relation node
                    if (edgeData.belongObjType === 3){
                        edgeLinkData.from = "relation_"+ edgeData.belongObjID;
                    }
                    // set the port
                    if(edgeData.portAtBelongObj!==-1 && edgeData.portAtRelationship!==-1){
                        edgeLinkData.fromPort=edgeData.portAtBelongObj;
                        edgeLinkData.toPort=edgeData.portAtRelationship;
                    }else{
                        edgeLinkData.fromPort=2;
                        edgeLinkData.toPort=counter+1;
                        counter = (counter+1)%2;
                    }
                    APIDiagram.model.addLinkData(edgeLinkData);
                }
            }
        }
    }
    let modelStr = APIDiagram.model.toJSON();
    return modelStr;
}

function extractAttributeInfo(attributeData, parentNode){
    // step 1: initial node data
    let attributeNode = {"name":attributeData.name, "category":"Attribute",
        "dataType":attributeData.dataType, "parentId":parentNode.key, "isPrimary":attributeData.isPrimary,
        "key":attributeData.id+"_"+attributeData.name,"underline":attributeData.isPrimary,
        "allowNotNull":attributeData.nullable, "icon": "", "isOptional" : false, "isMultiValue" : false};

    // step2: set the layout information
    if(!is_empty(attributeData.layoutInfo)){attributeNode.location={"class":"go.Point",
        "x":attributeData.layoutInfo.layoutX, "y":attributeData.layoutInfo.layoutY};
    }

    // step3: set the attribute type
    switch(attributeData.attributeType) {
        case 4:
            attributeNode.icon = "*";
            attributeNode.isOptional = true;
            attributeNode.isMultiValue = true;
            break;
        case 3:
            attributeNode.icon = "+";
            attributeNode.isOptional = false;
            attributeNode.isMultiValue = true;
            break;
        case 2:
            attributeNode.icon = "?";
            attributeNode.isOptional = true;
            attributeNode.isMultiValue = false;
            break;
        default:
            break;
    }

    return attributeNode;
}