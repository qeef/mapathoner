package org.openstreetmap.josm.plugins.mapathoner;

import static org.openstreetmap.josm.gui.help.HelpUtil.ht;
import static org.openstreetmap.josm.tools.I18n.tr;

import javax.swing.JMenu;
import java.awt.event.KeyEvent;

import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MainMenu;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;

import org.openstreetmap.josm.plugins.mapathoner.BatchCircleBuildingAction;
import org.openstreetmap.josm.plugins.mapathoner.BatchOrthogonalBuildingAction;
import org.openstreetmap.josm.plugins.mapathoner.PickResidentialAreaAction;

/**
 * Mapathoner - some useful tools for HOT and Missing Maps mappers.
 *
 * @author Jiri Hubacek
 * @since xxx
 */
public class MapathonerPlugin extends Plugin
{
    /**
     * Constructs a new {@code MapathonerPlugin}.
     */
    public MapathonerPlugin(PluginInformation info)
    {
        super(info);
        MainMenu mm = MainApplication.getMenu();
        JMenu hm = mm.addMenu("Mapathoner",
                tr("Mapathoner"),
                KeyEvent.VK_M,
                mm.getDefaultMenuPos(),
                ht("/Plugin/Mapathoner"));

        hm.setMnemonic(KeyEvent.VK_M);

        mm.add(hm, new BatchCircleBuildingAction());
        mm.add(hm, new BatchOrthogonalBuildingAction());
        mm.add(hm, new PickResidentialAreaAction());
    }
}
