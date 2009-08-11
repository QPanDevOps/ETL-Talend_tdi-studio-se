// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.fileoutputxml.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.MetadataTable;
import org.talend.core.model.process.EConnectionType;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.utils.NodeUtil;
import org.talend.designer.fileoutputxml.FileOutputXMLComponent;
import org.talend.designer.fileoutputxml.data.Attribute;
import org.talend.designer.fileoutputxml.data.Element;
import org.talend.designer.fileoutputxml.data.FOXTreeNode;
import org.talend.designer.fileoutputxml.data.NameSpaceNode;
import org.talend.designer.fileoutputxml.util.TreeUtil;

/**
 * DOC ke class global comment. Detailled comment <br/>
 * 
 */
public class FOXManager {

    protected FileOutputXMLComponent foxComponent;

    protected UIManager uiManager;

    protected List<FOXTreeNode> treeData;

    // add by wzhang. for multifoxmanager to record all schema
    protected Map<String, List<FOXTreeNode>> contents = new HashMap<String, List<FOXTreeNode>>();

    protected String currentSchema;

    /**
     * 
     * wzhang Comment method "getCurrentSchema".
     * 
     * @return
     */
    public String getCurrentSchema() {
        return this.currentSchema;
    }

    /**
     * 
     * wzhang Comment method "setCurrentSchema".
     * 
     * @param currentSchema
     */
    public void setCurrentSchema(String currentSchema) {
        this.currentSchema = currentSchema;
    }

    /**
     * constructor.
     */
    public FOXManager(FileOutputXMLComponent foxComponent) {
        this.foxComponent = foxComponent;
        this.uiManager = new UIManager(this);
        initModel();
    }

    /**
     * Getter for k.
     * 
     * @return the foxComponent
     */
    public FileOutputXMLComponent getFoxComponent() {
        return this.foxComponent;
    }

    public boolean isNoLoopInComponent() {
        List<Map<String, String>> loopTable = (List<Map<String, String>>) foxComponent.getTableList(FileOutputXMLComponent.LOOP);
        if (currentSchema == null)
            return loopTable.size() <= 0;
        // modified by wzhang,for multiple schema
        List<Map<String, String>> newList = new ArrayList<Map<String, String>>();
        for (Map<String, String> loopMap : loopTable) {
            String columnName = loopMap.get(FileOutputXMLComponent.COLUMN);
            if (columnName == null || columnName.length() == 0 || !columnName.startsWith(currentSchema)) {
                continue;
            }
            newList.add(loopMap);
        }
        return newList.size() <= 0;
    }

    /**
     * Sets the foxComponent.
     * 
     * @param foxComponent the foxComponent to set
     */
    public void setFoxComponent(FileOutputXMLComponent foxComponent) {
        this.foxComponent = foxComponent;
    }

    /**
     * Getter for uiManager.
     * 
     * @return the uiManager
     */
    public UIManager getUiManager() {
        return this.uiManager;
    }

    /**
     * Sets the uiManager.
     * 
     * @param uiManager the uiManager to set
     */
    public void setUiManager(UIManager uiManager) {
        this.uiManager = uiManager;
    }

    public void initModel() {
        IMetadataTable metadataTable = foxComponent.getMetadataTable();
        if (metadataTable == null) {
            metadataTable = new MetadataTable();
        }

        if (foxComponent.getComponent().getName().equals("tWriteXMLField")) { //$NON-NLS-1$
            IConnection inConn = null;
            for (IConnection conn : foxComponent.getIncomingConnections()) {
                if ((conn.getLineStyle().equals(EConnectionType.FLOW_MAIN))
                        || (conn.getLineStyle().equals(EConnectionType.FLOW_REF))) {
                    inConn = conn;
                    break;
                }
            }
            if (inConn != null) {
                metadataTable = inConn.getMetadataTable();
            }
        }

        treeData = new ArrayList<FOXTreeNode>();
        FOXTreeNode rootNode = null;
        FOXTreeNode current = null;
        FOXTreeNode temp = null;
        FOXTreeNode mainNode = null;
        String mainPath = null;
        String currentPath = null;
        String defaultValue = null;

        // build root tree
        List<Map<String, String>> rootTable = (List<Map<String, String>>) foxComponent.getTableList(FileOutputXMLComponent.ROOT);
        for (Map<String, String> rootMap : rootTable) {
            String newPath = rootMap.get(FileOutputXMLComponent.PATH);
            defaultValue = rootMap.get(FileOutputXMLComponent.VALUE);
            if (rootMap.get(FileOutputXMLComponent.ATTRIBUTE).equals("attri")) { //$NON-NLS-1$
                temp = new Attribute(newPath);
                temp.setDefaultValue(defaultValue);
                current.addChild(temp);
            } else if (rootMap.get(FileOutputXMLComponent.ATTRIBUTE).equals("ns")) { //$NON-NLS-1$
                temp = new NameSpaceNode(newPath);
                temp.setDefaultValue(defaultValue);
                current.addChild(temp);
            } else {
                temp = this.addElement(current, currentPath, newPath, defaultValue);
                if (rootNode == null) {
                    rootNode = temp;
                }
                if (rootMap.get(FileOutputXMLComponent.ATTRIBUTE).equals("main")) { //$NON-NLS-1$
                    temp.setMain(true);
                    mainNode = temp;
                    mainPath = newPath;
                }
                current = temp;
                currentPath = newPath;
            }
            String columnName = rootMap.get(FileOutputXMLComponent.COLUMN);
            if (columnName != null && columnName.length() > 0) {
                temp.setColumn(metadataTable.getColumn(columnName));
            }
        }

        // build group tree
        current = mainNode;
        currentPath = mainPath;
        boolean isFirst = true;
        List<Map<String, String>> groupTable = (List<Map<String, String>>) foxComponent
                .getTableList(FileOutputXMLComponent.GROUP);
        for (Map<String, String> groupMap : groupTable) {
            String newPath = groupMap.get(FileOutputXMLComponent.PATH);
            defaultValue = groupMap.get(FileOutputXMLComponent.VALUE);
            if (groupMap.get(FileOutputXMLComponent.ATTRIBUTE).equals("attri")) { //$NON-NLS-1$
                temp = new Attribute(newPath);
                temp.setDefaultValue(defaultValue);
                current.addChild(temp);
            } else if (groupMap.get(FileOutputXMLComponent.ATTRIBUTE).equals("ns")) { //$NON-NLS-1$
                temp = new NameSpaceNode(newPath);
                temp.setDefaultValue(defaultValue);
                current.addChild(temp);
            } else {
                temp = this.addElement(current, currentPath, newPath, defaultValue);
                if (groupMap.get(FileOutputXMLComponent.ATTRIBUTE).equals("main")) { //$NON-NLS-1$
                    temp.setMain(true);
                    mainNode = temp;
                    mainPath = newPath;
                }
                if (isFirst) {
                    temp.setGroup(true);
                    isFirst = false;
                }
                current = temp;
                currentPath = newPath;
            }
            String columnName = groupMap.get(FileOutputXMLComponent.COLUMN);
            if (columnName != null && columnName.length() > 0) {
                temp.setColumn(metadataTable.getColumn(columnName));
            }
        }

        // build loop tree
        current = mainNode;
        currentPath = mainPath;
        isFirst = true;
        List<Map<String, String>> loopTable = (List<Map<String, String>>) foxComponent.getTableList(FileOutputXMLComponent.LOOP);
        for (Map<String, String> loopMap : loopTable) {
            String newPath = loopMap.get(FileOutputXMLComponent.PATH);
            defaultValue = loopMap.get(FileOutputXMLComponent.VALUE);
            if (loopMap.get(FileOutputXMLComponent.ATTRIBUTE).equals("attri")) { //$NON-NLS-1$
                temp = new Attribute(newPath);
                temp.setDefaultValue(defaultValue);
                current.addChild(temp);
            } else if (loopMap.get(FileOutputXMLComponent.ATTRIBUTE).equals("ns")) { //$NON-NLS-1$
                temp = new NameSpaceNode(newPath);
                temp.setDefaultValue(defaultValue);
                current.addChild(temp);
            } else {
                temp = this.addElement(current, currentPath, newPath, defaultValue);
                if (loopMap.get(FileOutputXMLComponent.ATTRIBUTE).equals("main")) { //$NON-NLS-1$
                    temp.setMain(true);
                }
                if (isFirst) {
                    temp.setLoop(true);
                    isFirst = false;
                }
                current = temp;
                currentPath = newPath;
            }
            String columnName = loopMap.get(FileOutputXMLComponent.COLUMN);
            if (columnName != null && columnName.length() > 0) {
                temp.setColumn(metadataTable.getColumn(columnName));
            }
        }

        if (rootNode == null) {
            rootNode = new Element("rootTag"); //$NON-NLS-1$
        }

        rootNode.setParent(null);
        treeData.add(rootNode);

    }

    public List<Map<String, String>> getLoopTable() {
        if (currentSchema != null) {
            treeData = contents.get(currentSchema);
        }
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        Element loopNode = (Element) TreeUtil.getLoopNode(this.treeData.get(0));
        if (loopNode != null) {
            String path = TreeUtil.getPath(loopNode);
            tableLoader(loopNode, path.substring(0, path.lastIndexOf("/")), result, loopNode.getDefaultValue()); //$NON-NLS-1$
        }
        return result;

    }

    public List<Map<String, String>> getGroupTable() {
        if (currentSchema != null) {
            treeData = contents.get(currentSchema);
        }
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        Element groupNode = (Element) TreeUtil.getGroupNode(this.treeData.get(0));
        if (groupNode != null) {
            String path = TreeUtil.getPath(groupNode);
            tableLoader(groupNode, path.substring(0, path.lastIndexOf("/")), result, groupNode.getDefaultValue()); //$NON-NLS-1$
        }
        return result;
    }

    public List<Map<String, String>> getRootTable() {
        if (currentSchema != null) {
            treeData = contents.get(currentSchema);
        }
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        FOXTreeNode rootNode = treeData.get(0);
        if (rootNode != null) {
            this.tableLoader((Element) rootNode, "", result, rootNode.getDefaultValue()); //$NON-NLS-1$
        }
        return result;
    }

    protected void tableLoader(Element element, String parentPath, List<Map<String, String>> table, String defaultValue) {
        if ("a8".equals(element.getLabel())) {
            System.out.println("*********************");
            System.out.println("&&&&&&&&&&&&&&&&&&&7&&");
            System.out.println("1111111111111111111111");
        }
        if ("a9".equals(element.getLabel())) {
            System.out.println("*********************");
            System.out.println("&&&&&&&&&&&&&&&&&&&7&&");
            System.out.println("1111111111111111111111");
        }

        Map<String, String> newMap = new HashMap<String, String>();
        String currentPath = parentPath + "/" + element.getLabel(); //$NON-NLS-1$
        newMap.put(FileOutputXMLComponent.PATH, currentPath);
        newMap.put(FileOutputXMLComponent.COLUMN, element.getColumnLabel());
        newMap.put(FileOutputXMLComponent.ATTRIBUTE, element.isMain() ? "main" : "branch"); //$NON-NLS-1$ //$NON-NLS-2$
        newMap.put(FileOutputXMLComponent.VALUE, defaultValue); //$NON-NLS-1$
        table.add(newMap);
        for (FOXTreeNode att : element.getAttributeChildren()) {
            newMap = new HashMap<String, String>();
            newMap.put(FileOutputXMLComponent.PATH, att.getLabel());
            newMap.put(FileOutputXMLComponent.COLUMN, att.getColumnLabel());
            newMap.put(FileOutputXMLComponent.ATTRIBUTE, "attri"); //$NON-NLS-1$
            newMap.put(FileOutputXMLComponent.VALUE, att.getDefaultValue()); //$NON-NLS-1$
            table.add(newMap);
        }
        for (FOXTreeNode att : element.getNameSpaceChildren()) {
            newMap = new HashMap<String, String>();
            newMap.put(FileOutputXMLComponent.PATH, att.getLabel());
            newMap.put(FileOutputXMLComponent.COLUMN, att.getColumnLabel());
            newMap.put(FileOutputXMLComponent.ATTRIBUTE, "ns"); //$NON-NLS-1$
            newMap.put(FileOutputXMLComponent.VALUE, att.getDefaultValue()); //$NON-NLS-1$
            table.add(newMap);
        }
        List<FOXTreeNode> children = element.getElementChildren();
        for (FOXTreeNode child : children) {
            if (!child.isGroup() && !child.isLoop()) {
                tableLoader((Element) child, currentPath, table, child.getDefaultValue());
            }
        }
    }

    public boolean saveDataToComponent() {
        boolean result = false;
        if (foxComponent.setTableElementParameter(getRootTable(), FileOutputXMLComponent.ROOT)) {
            result = true;
        }
        if (foxComponent.setTableElementParameter(getLoopTable(), FileOutputXMLComponent.LOOP)) {
            result = true;
        }
        if (foxComponent.setTableElementParameter(getGroupTable(), FileOutputXMLComponent.GROUP)) {
            result = true;
        }
        return result;
    }

    public List<FOXTreeNode> getTreeData(String curSchema) {
        if (currentSchema == null) {
            return treeData;
        } else {
            return contents.get(curSchema);
        }

    }

    public List<FOXTreeNode> getTreeData() {
        if (currentSchema == null) {
            return treeData;
        } else {
            return getOriginalNodes();
        }
    }

    /**
     * 
     * DOC wzhang Comment method "getOriginalNodes".
     * 
     * @return
     */
    protected List<FOXTreeNode> getOriginalNodes() {
        List<FOXTreeNode> tmpTreeData = new ArrayList<FOXTreeNode>();
        List<? extends IConnection> incomingConnections = NodeUtil.getIncomingConnections(this.getFoxComponent(),
                IConnectionCategory.FLOW);
        if (incomingConnections.size() > 0) {
            for (IConnection conn : incomingConnections) {
                String uniqueName = conn.getUniqueName();
                List<FOXTreeNode> list = contents.get(uniqueName);
                tmpTreeData.addAll(list);
            }
        }
        return tmpTreeData;
    }

    public List<IMetadataColumn> getSchemaData() {
        if (foxComponent.getMetadataTable().getListColumns().size() == 0) {
            List<? extends IConnection> incomingConnections = NodeUtil.getIncomingConnections(this.getFoxComponent(),
                    IConnectionCategory.FLOW);
            if (incomingConnections.size() > 0) {
                return incomingConnections.get(0).getMetadataTable().getListColumns();
            }
        }

        return foxComponent.getMetadataTable().getListColumns();
    }

    public void setTreeData(List<FOXTreeNode> treeData) {
        this.treeData = treeData;
        contents.put(currentSchema, treeData);
    }

    protected FOXTreeNode addElement(FOXTreeNode current, String currentPath, String newPath, String defaultValue) {
        String name = newPath.substring(newPath.lastIndexOf("/") + 1); //$NON-NLS-1$
        String parentPath = newPath.substring(0, newPath.lastIndexOf("/")); //$NON-NLS-1$
        FOXTreeNode temp = new Element(name, defaultValue);

        if (current == null) {// root node
            return temp;
        }

        if (currentPath.equals(parentPath)) {
            current.addChild(temp);
        } else {
            String[] nods = currentPath.split("/"); //$NON-NLS-1$
            String[] newNods = parentPath.split("/"); //$NON-NLS-1$
            int parentLevel = 0;
            int checkLength = nods.length < newNods.length ? nods.length : newNods.length;
            for (int i = 1; i < checkLength; i++) {
                if (nods[i].equals(newNods[i])) {
                    parentLevel = i;
                }
            }
            FOXTreeNode parent = current;
            for (int i = 0; i < nods.length - (parentLevel + 1); i++) {
                FOXTreeNode tmpParent = parent.getParent();
                if (tmpParent == null) {
                    break;
                }
                parent = tmpParent;
            }

            if (parent != null)
                parent.addChild(temp);
        }

        return temp;
    }

    /**
     * 
     * DOC wzhang Comment method "getRootFOXTreeNode".
     * 
     * @param node
     * @return
     */
    public FOXTreeNode getRootFOXTreeNode(FOXTreeNode node) {
        if (node != null) {
            FOXTreeNode parent = node.getParent();
            if (parent == null) {
                return node;
            }
            return getRootFOXTreeNode(parent);
        }
        return null;
    }
}
