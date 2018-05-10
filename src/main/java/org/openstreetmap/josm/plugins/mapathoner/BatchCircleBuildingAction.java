// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.plugins.mapathoner;

import static org.openstreetmap.josm.gui.help.HelpUtil.ht;
import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.CreateCircleAction;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.command.AddCommand;
import org.openstreetmap.josm.command.ChangePropertyCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.DeleteCommand;
import org.openstreetmap.josm.command.RemoveNodesCommand;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.data.coor.EastNorth;
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
 * @author qeef
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
        List<Way> ways = OsmPrimitive.getFilteredList(sel, Way.class);

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

        List<Node> nodes = existingWay.getNodes();
        List<EastNorth> ens = new ArrayList<EastNorth>();
        Map<String, String> tag_by = new HashMap<String, String>();
        tag_by.put("building", "yes");
        Collection<Way> cbuildings = new LinkedList<Way>();
        Collection<Command> cmds = new LinkedList<Command>();
        for (Node n : nodes) {
            ens.add(n.getEastNorth());
        }

        cmds.add(new RemoveNodesCommand(existingWay, nodes));
        cmds.add(new DeleteCommand(ds, existingWay));
        for (Node n: nodes) {
            cmds.add(new DeleteCommand(ds, n));
        }

        if (ens.size() % 2 != 0) {
            ens.remove(ens.size() - 1);
        }

        for (i = 0; i < ens.size() - 1; i += 2) {
            List<Node> cb_nodes = new ArrayList<Node>();
            Way cb_way = new Way();

            cb_nodes.add(new Node(ens.get(i)));
            cb_nodes.add(new Node(ens.get(i + 1)));

            for (Node n: cb_nodes) {
                cmds.add(new AddCommand(ds, n));
            }

            cb_nodes.add(cb_nodes.get(0));
            cb_way.setNodes(cb_nodes);
            cmds.add(new AddCommand(ds, cb_way));

            cbuildings.add(cb_way);
        }

        cmds.add(new ChangePropertyCommand(ds, cbuildings, tag_by));
        MainApplication.undoRedo.add(new SequenceCommand(
                    tr("Batch Circle Building"), cmds));

        CreateCircleAction cc = new CreateCircleAction();
        for (Way w: cbuildings) {
            ds.clearSelection();
            ds.addSelected(w);
            cc.actionPerformed(null);
            ds.clearSelection();
        }
    }
}
