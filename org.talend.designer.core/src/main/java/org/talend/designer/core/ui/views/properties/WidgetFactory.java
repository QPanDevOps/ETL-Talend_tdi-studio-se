// ============================================================================
//
// Talend Community Edition
//
// Copyright (C) 2006-2007 Talend - www.talend.com
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//
// ============================================================================
package org.talend.designer.core.ui.views.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;

/**
 * DOC nrousseau class global comment. Detailled comment <br/> adapted from the TabbedPropertySheetWidgetFactory
 * 
 */
public class WidgetFactory extends FormToolkit {

    /**
     * private constructor.
     */
    public WidgetFactory() {
        super(Display.getCurrent());
    }

    /**
     * Creates the tab folder as a part of the form.
     * 
     * @param parent the composite parent.
     * @param style the tab folder style.
     * @return the tab folder
     */
    public CTabFolder createTabFolder(Composite parent, int style) {
        CTabFolder tabFolder = new CTabFolder(parent, style);
        return tabFolder;
    }

    /**
     * Creates the tab item as a part of the tab folder.
     * 
     * @param tabFolder the parent.
     * @param style the tab folder style.
     * @return the tab item.
     */
    public CTabItem createTabItem(CTabFolder tabFolder, int style) {
        CTabItem tabItem = new CTabItem(tabFolder, style);
        return tabItem;
    }

    /**
     * Creates the list as a part of the form.
     * 
     * @param parent the composite parent.
     * @param style the list style.
     * @return the list.
     */
    public List createList(Composite parent, int style) {
        List list = new org.eclipse.swt.widgets.List(parent, style);
        return list;
    }

    public Composite createComposite(Composite parent, int style) {
        Composite c = super.createComposite(parent, style);
        paintBordersFor(c);
        return c;
    }

    public Composite createComposite(Composite parent) {
        Composite c = createComposite(parent, SWT.NONE);
        return c;
    }

    /**
     * Creates a plain composite as a part of the form.
     * 
     * @param parent the composite parent.
     * @param style the composite style.
     * @return the composite.
     */
    public Composite createPlainComposite(Composite parent, int style) {
        Composite c = super.createComposite(parent, style);
        c.setBackground(parent.getBackground());
        paintBordersFor(c);
        return c;
    }

    /**
     * Creates a scrolled composite as a part of the form.
     * 
     * @param parent the composite parent.
     * @param style the composite style.
     * @return the composite.
     */
    public ScrolledComposite createScrolledComposite(Composite parent, int style) {
        ScrolledComposite scrolledComposite = new ScrolledComposite(parent, style);
        return scrolledComposite;
    }

    /**
     * Creates a combo box as a part of the form.
     * 
     * @param parent the combo box parent.
     * @param comboStyle the combo box style.
     * @return the combo box.
     */
    public CCombo createCCombo(Composite parent, int comboStyle) {
        CCombo combo = new CCombo(parent, comboStyle);
        adapt(combo, true, false);
        // Bugzilla 145837 - workaround for no borders on Windows XP
        if (getBorderStyle() == SWT.BORDER) {
            combo.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
        }
        return combo;
    }

    /**
     * Creates a combo box as a part of the form.
     * 
     * @param parent the combo box parent.
     * @return the combo box.
     */
    public CCombo createCCombo(Composite parent) {
        return createCCombo(parent, SWT.FLAT | SWT.READ_ONLY);
    }

    /**
     * Creates a group as a part of the form.
     * 
     * @param parent the group parent.
     * @param text the group title.
     * @return the composite.
     */
    public Group createGroup(Composite parent, String text) {
        Group group = new Group(parent, SWT.SHADOW_NONE);
        group.setText(text);
        group.setBackground(getColors().getBackground());
        group.setForeground(getColors().getForeground());
        return group;
    }

    /**
     * Creates a flat form composite as a part of the form.
     * 
     * @param parent the composite parent.
     * @return the composite.
     */
    public Composite createFlatFormComposite(Composite parent) {
        Composite composite = createComposite(parent);
        FormLayout layout = new FormLayout();
        layout.marginWidth = ITabbedPropertyConstants.HSPACE + 2;
        layout.marginHeight = ITabbedPropertyConstants.VSPACE;
        layout.spacing = ITabbedPropertyConstants.VMARGIN + 1;
        composite.setLayout(layout);
        return composite;
    }

    /**
     * Creates a label as a part of the form.
     * 
     * @param parent the label parent.
     * @param text the label text.
     * @return the label.
     */
    public CLabel createCLabel(Composite parent, String text) {
        return createCLabel(parent, text, SWT.NONE);
    }

    /**
     * Creates a label as a part of the form.
     * 
     * @param parent the label parent.
     * @param text the label text.
     * @param style the label style.
     * @return the label.
     */
    public CLabel createCLabel(Composite parent, String text, int style) {
        final CLabel label = new CLabel(parent, style);
        label.setBackground(parent.getBackground());
        label.setText(text);
        return label;
    }

    public void dispose() {
        if (getColors() != null) {
            super.dispose();
        }
    }
}