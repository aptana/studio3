/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Benjamin Muskalla -  Bug 29633 [EditorMgmt] "Open" menu should
 *                          have Open With-->Other
 *******************************************************************************/
// $codepro.audit.disable staticFieldNamingConvention
// $codepro.audit.disable interfaceNamingConvention

package com.aptana.ui.io.epl;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeSettings;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.EditorSelectionDialog;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;

import com.aptana.core.logging.IdeLog;

/**
 * A menu for opening files in the workbench.
 * <p>
 * An <code>OpenWithMenu</code> is used to populate a menu with "Open With"
 * actions. One action is added for each editor which is applicable to the
 * selected file. If the user selects one of these items, the corresponding
 * editor is opened on the file.
 * </p>
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * Modified from org.eclipse.ui.actions.OpenWithMenu to support opening file
 * system files.
 * 
 * @noextend This class is not intended to be subclassed by clients.
 * 
 */
@SuppressWarnings("restriction")
public class OpenWithMenu extends ContributionItem {

	public static interface Client {

		public void openEditor(IFileStore file, IEditorDescriptor editorDescriptor);
	}

    private IAdaptable file;

    private IEditorRegistry registry = PlatformUI.getWorkbench()
            .getEditorRegistry();

    private static Map<ImageDescriptor, Image> imageCache = new Hashtable<ImageDescriptor, Image>(11);

    /**
     * The id of this action.
     */
    public static final String ID = PlatformUI.PLUGIN_ID + ".OpenWithMenu";//$NON-NLS-1$

    private Client client;

    /*
     * Compares the labels from two IEditorDescriptor objects
     */
    private static final Comparator<IEditorDescriptor> comparer = new Comparator<IEditorDescriptor>() {
        private Collator collator = Collator.getInstance();

        public int compare(IEditorDescriptor o1, IEditorDescriptor o2) {
            String s1 = o1.getLabel();
            String s2 = o2.getLabel();
            return collator.compare(s1, s2);
        }
    };

    /**
     * Constructs a new instance of <code>OpenWithMenu</code>.
     *
     * @param page the page where the editor is opened if an item within
     *      the menu is selected
     * @param file the selected file
     */
    public OpenWithMenu(IWorkbenchPage page, IAdaptable file, Client client) {
        super(ID);
        this.file = file;
        this.client = client;
    }

    /**
     * Returns an image to show for the corresponding editor descriptor.
     *
     * @param editorDesc the editor descriptor, or null for the system editor
     * @return the image or null
     */
    private Image getImage(IEditorDescriptor editorDesc) {
        ImageDescriptor imageDesc = getImageDescriptor(editorDesc);
        if (imageDesc == null) {
            return null;
        }
        Image image = (Image) imageCache.get(imageDesc);
        if (image == null) {
            image = imageDesc.createImage();
            imageCache.put(imageDesc, image);
        }
        return image;
    }

    /**
     * Returns the image descriptor for the given editor descriptor,
     * or null if it has no image.
     */
    private ImageDescriptor getImageDescriptor(IEditorDescriptor editorDesc) {
        IFileStore file = getFileResource();
        if (file == null) {
            return null;
        }
        ImageDescriptor imageDesc = null;
        if (editorDesc == null) {
            imageDesc = registry.getImageDescriptor(file.getName());
            //TODO: is this case valid, and if so, what are the implications for content-type editor bindings?
        } else {
            imageDesc = editorDesc.getImageDescriptor();
        }
        if (imageDesc == null) {
            if (editorDesc.getId().equals(
                    IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID)) {
                imageDesc = registry
                        .getSystemExternalEditorImageDescriptor(file.getName());
            }
        }
        return imageDesc;
    }

    /**
     * Creates the menu item for the editor descriptor.
     *
     * @param menu the menu to add the item to
     * @param descriptor the editor descriptor, or null for the system editor
     * @param preferredEditor the descriptor of the preferred editor, or <code>null</code>
     */
    private void createMenuItem(Menu menu, final IEditorDescriptor descriptor,
            final IEditorDescriptor preferredEditor) {
        // XXX: Would be better to use bold here, but SWT does not support it.
        final MenuItem menuItem = new MenuItem(menu, SWT.RADIO);
        boolean isPreferred = preferredEditor != null
                && descriptor.getId().equals(preferredEditor.getId());
        menuItem.setSelection(isPreferred);
        menuItem.setText(descriptor.getLabel());
        Image image = getImage(descriptor);
        if (image != null) {
            menuItem.setImage(image);
        }
        Listener listener = new Listener() {
            public void handleEvent(Event event) {
                switch (event.type) {
                case SWT.Selection:
                    if (menuItem.getSelection()) {
                        openEditor(descriptor, false);
                    }
                    break;
                }
            }
        };
        menuItem.addListener(SWT.Selection, listener);
    }

    /**
     * Creates the Other... menu item
     *
     * @param menu the menu to add the item to
     */
    private void createOtherMenuItem(final Menu menu) {
        final IFileStore file = getFileResource();
        if (file == null) {
            return;
        }
        new MenuItem(menu, SWT.SEPARATOR);
        final MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
        menuItem.setText(IDEWorkbenchMessages.OpenWithMenu_Other);
        Listener listener = new Listener() {
            public void handleEvent(Event event) {
                switch (event.type) {
                case SWT.Selection:
                    EditorSelectionDialog dialog = new EditorSelectionDialog(
                            menu.getShell());
                    dialog
                            .setMessage(NLS
                                    .bind(
                                            IDEWorkbenchMessages.OpenWithMenu_OtherDialogDescription,
                                            file.getName()));
                    if (dialog.open() == Window.OK) {
                        IEditorDescriptor editor = dialog.getSelectedEditor();
                        if (editor != null) {
                            openEditor(editor, editor.isOpenExternal());
                        }
                    }
                    break;
                }
            }
        };
        menuItem.addListener(SWT.Selection, listener);
    }
    
    /* (non-Javadoc)
     * Fills the menu with perspective items.
     */
    public void fill(Menu menu, int index) {
        IFileStore file = getFileResource();
        if (file == null) {
            return;
        }

        IEditorDescriptor defaultEditor = registry
                .findEditor(IDEWorkbenchPlugin.DEFAULT_TEXT_EDITOR_ID); // may be null
        IEditorDescriptor preferredEditor = null;
        try {
            preferredEditor = IDE.getEditorDescriptor(file.getName()); // may be null
        } catch (PartInitException e) {
        	IdeLog.logError(IOUIEplPlugin.getDefault(), e);
        }

        IContentType finalType = null;
        for (IContentType type : Platform.getContentTypeManager()
                .getAllContentTypes()) {
            if (finalType != null) {
                break;
            }
            try {
                for (String settings : type.getSettings(null).getFileSpecs(
                        IContentTypeSettings.FILE_NAME_SPEC)) {
                    if (settings.equals(file.getName())) {
                        finalType = type;
                        break;
                    }
                }
                if (finalType == null) {
                    for (String settings : type.getSettings(null).getFileSpecs(
                            IContentTypeSettings.FILE_EXTENSION_SPEC)) {
                        if (settings.equals(getExtension(file.getName()))) {
                            finalType = type;
                            break;
                        }
                    }
                }
            } catch (Exception e) {
            	IdeLog.logWarning(IOUIEplPlugin.getDefault(), e);
            }
        }
        IEditorDescriptor[] editors = registry.getEditors(file.getName(), finalType);
        Collections.sort(Arrays.asList(editors), comparer);

        boolean defaultFound = false;

        //Check that we don't add it twice. This is possible
        //if the same editor goes to two mappings.
        List<IEditorDescriptor> alreadyMapped = new ArrayList<IEditorDescriptor>();

        for (IEditorDescriptor editor : editors) {
            if (!alreadyMapped.contains(editor)) {
                createMenuItem(menu, editor, preferredEditor);
                if (defaultEditor != null
                        && editor.getId().equals(defaultEditor.getId())) {
                    defaultFound = true;
                }
                alreadyMapped.add(editor);
            }
        }

        // Only add a separator if there is something to separate
        if (editors.length > 0) {
            new MenuItem(menu, SWT.SEPARATOR);
        }

        // Add default editor. Check it if it is saved as the preference.
        if (!defaultFound && defaultEditor != null) {
            createMenuItem(menu, defaultEditor, preferredEditor);
        }

        // Add system editor (should never be null)
        IEditorDescriptor descriptor = registry
                .findEditor(IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID);
        createMenuItem(menu, descriptor, preferredEditor);

        // Add system in-place editor (can be null)
        descriptor = registry
                .findEditor(IEditorRegistry.SYSTEM_INPLACE_EDITOR_ID);
        if (descriptor != null) {
            createMenuItem(menu, descriptor, preferredEditor);
        }

        // add Other... menu item
        createOtherMenuItem(menu);
    }

    /**
     * Converts the IAdaptable file to File or null.
     */
    private IFileStore getFileResource() {
        IFileStore fileStore = (IFileStore) this.file.getAdapter(IFileStore.class);
        if (fileStore == null) {
            File file = (File) this.file.getAdapter(File.class);
            if (file != null) {
                fileStore = EFS.getLocalFileSystem().fromLocalFile(file);
            }
        }
        return fileStore;
    }

    /* (non-Javadoc)
     * Returns whether this menu is dynamic.
     */
    public boolean isDynamic() {
        return true;
    }

    /**
     * Opens the given editor on the selected file.
     *
     * @param editorDescriptor the editor descriptor, or null for the system editor
     * @param openUsingDescriptor use the descriptor's editor ID for opening if false (normal case),
     * or use the descriptor itself if true (needed to fix bug 178235).
     *
     * @since 3.5
     */
    protected void openEditor(IEditorDescriptor editorDescriptor, boolean openUsingDescriptor) {
        IFileStore file = getFileResource();
        if (file == null || client == null) {
            return;
        }
        client.openEditor(file, editorDescriptor);
    }

    /**
     * Get the extension.
     * 
     * @param fileName
     *            File name
     * @return the extension
     */
    public static String getExtension(String fileName) {
        if (fileName == null || fileName.equals("")) { //$NON-NLS-1$
            return fileName;
        }

        int index = fileName.lastIndexOf('.');
        if (index == -1) {
            return ""; //$NON-NLS-1$
        }
        if (index == fileName.length()) {
            return ""; //$NON-NLS-1$
        }
        return fileName.substring(index + 1, fileName.length());
    }
}
