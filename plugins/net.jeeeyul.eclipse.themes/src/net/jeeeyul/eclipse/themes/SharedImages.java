package net.jeeeyul.eclipse.themes;

import java.io.File;
import java.net.URI;
import java.net.URL;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.*;
import org.osgi.framework.Bundle;

public class SharedImages
{

    public SharedImages()
    {
    }

    public static Image getImage(String key)
    {
        Image result = REGISTRY.get(key);
        if(result == null)
        {
            result = loadImage(key);
            REGISTRY.put(key, result);
        }
        return result;
    }

    public static ImageDescriptor getImageDescriptor(String key)
    {
        ImageDescriptor result = REGISTRY.getDescriptor(key);
        if(result == null)
        {
            result = loadImageDescriptor(key);
            REGISTRY.put(key, result);
        }
        return result;
    }

    private static Image loadImage(String key)
    {
        try
        {
            Bundle bundle = Platform.getBundle("net.jeeeyul.eclipse.themes");
            URL resource = null;
            if(bundle != null)
                resource = Platform.getBundle("net.jeeeyul.eclipse.themes").getResource(key);
            else
                resource = (new File(key)).toURI().toURL();
            Image image = new Image(null, resource.openStream());
            return image;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return PlatformUI.getWorkbench().getSharedImages().getImage("IMG_OBJS_ERROR_TSK");
    }

    private static ImageDescriptor loadImageDescriptor(String key)
    {
        try
        {
            Bundle bundle = Platform.getBundle("net.jeeeyul.eclipse.themes");
            URL resource = null;
            if(bundle != null)
                resource = Platform.getBundle("net.jeeeyul.eclipse.themes").getResource(key);
            else
                resource = (new File(key)).toURI().toURL();
            ImageDescriptor descriptor = ImageDescriptor.createFromURL(resource);
            return descriptor;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor("IMG_OBJS_ERROR_TSK");
    }

    public static final String ACTIVE_PART = "icons/active-part.png";
    public static final String ADD = "icons/add.gif";
    public static final String CLOSE_ACTIVE = "icons/close-active.png";
    public static final String CLOSE_NORMAL = "icons/close-normal.png";
    public static final String ECLIPSE = "icons/eclipse.gif";
    public static final String EDITOR_AREA = "icons/editor-area.gif";
    public static final String FONT = "icons/font.gif";
    public static final String FRAME = "icons/frame.gif";
    public static final String GITHUB = "icons/github.png";
    public static final String HANDLE_EMBOSSED = "icons/handle-embossed.gif";
    public static final String HANDLE = "icons/handle.gif";
    public static final String INFO_TSK = "icons/info_tsk.gif";
    public static final String LAYOUT = "icons/layout.gif";
    public static final String MAXMIZE = "icons/maxmize.gif";
    public static final String MENU = "icons/menu.gif";
    public static final String MINIMIZE = "icons/minimize.gif";
    public static final String OPEN_PERSPECTIVE = "icons/open-perspective.gif";
    public static final String PALETTE = "icons/palette.gif";
    public static final String PART_HOVER = "icons/part-hover.png";
    public static final String PART = "icons/part.gif";
    public static final String PLUGIN = "icons/plugin.gif";
    public static final String PRESET = "icons/preset.gif";
    public static final String REFRESH = "icons/refresh.gif";
    public static final String TOOLBAR = "icons/toolbar.gif";
    public static final String TRANSPARENT = "icons/transparent.gif";
    public static final String WARN_TSK = "icons/warn_tsk.gif";
    private static final ImageRegistry REGISTRY = new ImageRegistry(Display.getDefault());

}
