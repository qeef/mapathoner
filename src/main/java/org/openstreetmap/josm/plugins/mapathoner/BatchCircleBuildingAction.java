// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.plugins.mapathoner;

import static org.openstreetmap.josm.gui.help.HelpUtil.ht;
import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JOptionPane;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.CreateCircleAction;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.command.ChangePropertyCommand;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.Notification;
import org.openstreetmap.josm.tools.ImageProvider;
import org.openstreetmap.josm.tools.Shortcut;

/**
 * Creates multiple circle buildings based on nodes of way.
 * <p>
 * Usage:
 * <ul>
 * <li>Creates a way that consists of nodes at circle building diameter.
 * <li>Runs the action.
 * </ul>
 * <p>
 * Performs:
 * <ul>
 * <li>From pairs of way nodes creates the circle.
 * <li>Tags the circle as building=yes.
 * </ul>
 * <p>
 * Notes:
 * <ul>
 * <li>Useful for HOT and Missing Maps mappers.
 * <li>This action uses <code>CreateCircleAction</code>.
 * </ul>
 *
 * @author Jiri Hubacek
 * @since xxx
 */

public final class BatchCircleBuildingAction extends JosmAction
{
    /**
     * Constructs a new {@code BatchCircleBuildingAction}.
     */
    public BatchCircleBuildingAction()
    {
        super(tr("Batch Circle Building"),
                (ImageProvider) null,
                tr("Create multiple circle buildings from way."),
                Shortcut.registerShortcut("mapathoner:batchcirclebuilding",
                    tr("Mapathoner: {0}", tr("Batch Circle Building")),
                    KeyEvent.VK_W,
                    Shortcut.CTRL_SHIFT),
                true,
                "batchcirclebuilding",
                true);
        putValue("help", ht("/Action/BatchCircleBuilding"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isEnabled())
            return;

        DataSet ds = getLayerManager().getEditDataSet();
        if (ds == null)
            return;

        Collection<OsmPrimitive> sel = ds.getSelected();
        List<Node> nodes = new ArrayList<Node>();
        List<Way> ways = OsmPrimitive.getFilteredList(sel, Way.class);

        CreateCircleAction createCircle = new CreateCircleAction();
        Way existingWay = null;
        int i;

        if (ways.size() != 1) {
            new Notification(
                    tr("Please create way with even number of nodes."))
                    .setIcon(JOptionPane.INFORMATION_MESSAGE)
                    .setDuration(Notification.TIME_LONG)
                    .show();
            return;
        }

        existingWay = ways.get(0);
        if (existingWay.getNodesCount() < 2) {
            new Notification(
                    tr("Please create way with even number of nodes."))
                    .setIcon(JOptionPane.INFORMATION_MESSAGE)
                    .setDuration(Notification.TIME_LONG)
                    .show();
            return;
        }

        for (Node n : existingWay.getNodes()) {
            if (!nodes.contains(n)) {
                nodes.add(n);
            }
        }

        if (nodes.size() % 2 != 0) {
            ds.removePrimitive(nodes.get(nodes.size() - 1));
            nodes.remove(nodes.size() - 1);
        }

        ds.clearSelection();
        ds.removePrimitive(existingWay);

        for (i = 0; i < nodes.size() - 1; i += 2) {
            ds.addSelected(nodes.get(i));
            ds.addSelected(nodes.get(i + 1));
            createCircle.actionPerformed(null);
            ds.clearSelection();

            for (Way w: ds.getWays()) {
                if (w.getNodes().contains(nodes.get(i))) {
                    ds.addSelected(w);
                    MainApplication.undoRedo.add(
                            new ChangePropertyCommand(w, "building", "yes"));
                    break;
                }
            }

            ds.clearSelection();
        }
    }
}
