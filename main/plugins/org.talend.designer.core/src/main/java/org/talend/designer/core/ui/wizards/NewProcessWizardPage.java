// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.core.ui.wizards;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.designer.core.i18n.Messages;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IProxyRepositoryService;
import org.talend.repository.model.RepositoryConstants;
import org.talend.repository.ui.wizards.PropertiesWizardPage;

/**
 * Page for new project details. <br/>
 * 
 * $Id$
 * 
 */
public class NewProcessWizardPage extends PropertiesWizardPage {

    private static final String DESC = Messages.getString("NewProcessWizard.description"); //$NON-NLS-1$

    private static String CLASS = ".class"; //$NON-NLS-1$

    /**
     * Constructs a new NewProjectWizardPage.
     * 
     */
    public NewProcessWizardPage(Property property, IPath destinationPath) {
        super("WizardPage", property, destinationPath); //$NON-NLS-1$

        setTitle(Messages.getString("NewProcessWizard.title")); //$NON-NLS-1$
        setDescription(DESC);
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        container.setLayout(layout);

        super.createControl(container);

        setControl(container);
        updateContent();
        addListeners();
        // setPageComplete(false);
    }

    @Override
    public ERepositoryObjectType getRepositoryObjectType() {
        return ERepositoryObjectType.PROCESS;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.wizards.PropertiesWizardPage#evaluateTextField()
     */
    @Override
    protected void evaluateTextField() {
        if (super.readOnly) {
            return;
        }
        if (nameText == null || nameText.isDisposed()) {
            return;
        }
        if (nameText.getText().length() == 0) {
            nameStatus = createStatus(IStatus.ERROR, Messages.getString("PropertiesWizardPage.NameEmptyError")); //$NON-NLS-1$
        } else if (!Pattern.matches(RepositoryConstants.getPattern(getRepositoryObjectType()), nameText.getText())
                || nameText.getText().trim().contains(" ")) { //$NON-NLS-1$
            nameStatus = createStatus(IStatus.ERROR, Messages.getString("PropertiesWizardPage.NameFormatError")); //$NON-NLS-1$
        } else if (JavaConventions.validateClassFileName(nameText.getText() + CLASS,
                JavaCore.getOption(JavaCore.COMPILER_SOURCE), JavaCore.getOption(JavaCore.COMPILER_COMPLIANCE)).getSeverity() == IStatus.ERROR
                || "java".equalsIgnoreCase(nameText.getText())) {//$NON-NLS-1$
            nameStatus = createStatus(IStatus.ERROR, Messages.getString("PropertiesWizardPage.KeywordsError")); //$NON-NLS-1$
        } else if (nameText.getText().equalsIgnoreCase(ProjectManager.getInstance().getCurrentProject().getLabel())) {
            nameStatus = createStatus(IStatus.ERROR, Messages.getString("PropertiesWizardPage.SameAsProjectname")); //$NON-NLS-1$
        } else if (super.nameModifiedByUser) {
            if (super.retrieveNameFinished) {
                if (!isValid(nameText.getText())) {
                    nameStatus = createStatus(IStatus.ERROR, Messages.getString("PropertiesWizardPage.ItemExistsError")); //$NON-NLS-1$
                } else {
                    nameStatus = createOkStatus();
                }
            } else {
                nameStatus = createStatus(IStatus.ERROR, "Looking for current items name list"); //$NON-NLS-1$
            }
        } else {
            nameStatus = createOkStatus();
        }
        if (property != null && nameStatus.getSeverity() == IStatus.OK) {
            property.setLabel(getPropertyLabel(StringUtils.trimToNull(nameText.getText())));
            property.setDisplayName(StringUtils.trimToNull(nameText.getText()));
            property.setModificationDate(new Date());
        }
        updatePageStatus();
        if (nameStatus.getSeverity() == IStatus.OK) {
            evaluateNameInRoutine();
        }
    }

    @Override
    protected List<IRepositoryViewObject> loadRepViewObjectWithOtherTypes() throws PersistenceException {
        List<IRepositoryViewObject> list = new ArrayList<IRepositoryViewObject>();

        // List for all other processes
        List<IRepositoryViewObject> processList = getAllProcessTypeObjectsWithoutCurrentType();
        if (processList != null && !processList.isEmpty()) {
            list.addAll(processList);
        }

        // List for routine
        if (ERepositoryObjectType.ROUTINES != null) {
            if (GlobalServiceRegister.getDefault().isServiceRegistered(IProxyRepositoryService.class)) {
                IProxyRepositoryService service = (IProxyRepositoryService) GlobalServiceRegister.getDefault().getService(
                        IProxyRepositoryService.class);

                List<IRepositoryViewObject> mrList = service.getProxyRepositoryFactory().getAll(ERepositoryObjectType.ROUTINES,
                        true, false);
                if (mrList != null && mrList.size() > 0) {
                    list.addAll(mrList);
                }
            }
        }

        // List for esb route
        ERepositoryObjectType routeType = ERepositoryObjectType.valueOf(ERepositoryObjectType.class, "ROUTES");
        if (routeType != null) {
            if (GlobalServiceRegister.getDefault().isServiceRegistered(IProxyRepositoryService.class)) {
                IProxyRepositoryService service = (IProxyRepositoryService) GlobalServiceRegister.getDefault().getService(
                        IProxyRepositoryService.class);
                List<IRepositoryViewObject> routeList = service.getProxyRepositoryFactory().getAll(routeType, true, false);
                if (routeList != null && routeList.size() > 0) {
                    list.addAll(routeList);
                }
            }
        }
        return list;
    }

}
