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
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.actions.OrthogonalizeAction;
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
 * Create multiple orthogonal buildings based on nodes of a way.
 * <p>
 * Usage:
 * <ul>
 * <li>Creates a way that consists of nodes at 3 building corners.
 * <li>Runs action.
 * </ul>
 * <p>
 * Performs:
 * <ul>
 * <li>From triplets of way nodes creates rectangle.
 * <li>Tags the rectangle as building=yes.
 * </ul>
 * <p>
 * Notes:
 * <ul>
 * <li>Useful for HOT and Missing Maps mappers.
 * <li>This action uses <code>OrthogonalizeAction</code>.
 * </ul>
 *
 * @author Jiri Hubacek
 * @since xxx
 */

public final class BatchOrthogonalBuildingAction extends JosmAction
{
    /**
     * Constructs a new {@code BatchOrthogonalBuildingAction}.
     */
    public BatchOrthogonalBuildingAction()
    {
        super(tr("Batch Orthogonal Building"),
                (ImageProvider) null,
                tr("Create multiple orthogonal buildings from a way."),
                Shortcut.registerShortcut("mapathoner:batchorthogonalbuilding",
                    tr("Mapathoner: {0}", tr("Batch Orthogonal Building")),
                    KeyEvent.VK_E,
                    Shortcut.CTRL_SHIFT),
                true,
                "batchorthogonalbuilding",
                true);
        putValue("help", ht("/Action/BatchOrthogonalBuilding"));
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
                    tr("Please create a way with nodes count divisible by 3."))
                    .setIcon(JOptionPane.INFORMATION_MESSAGE)
                    .setDuration(Notification.TIME_LONG)
                    .show();
            return;
        }

        existingWay = ways.get(0);
        if (existingWay.getNodesCount() < 3) {
            new Notification(
                    tr("Please create a way with nodes count divisible by 3."))
                    .setIcon(JOptionPane.INFORMATION_MESSAGE)
                    .setDuration(Notification.TIME_LONG)
                    .show();
            return;
        }

        List<Node> nodes = existingWay.getNodes();
        List<EastNorth> ens = new ArrayList<EastNorth>();
        Map<String, String> tag_by = new HashMap<String, String>();
        tag_by.put("building", "yes");
        Collection<Way> obuildings = new LinkedList<Way>();
        Collection<Command> cmds = new LinkedList<Command>();
        for (Node n : nodes) {
            ens.add(n.getEastNorth());
        }

        cmds.add(new RemoveNodesCommand(existingWay, nodes));
        cmds.add(new DeleteCommand(ds, existingWay));
        for (Node n : nodes) {
            cmds.add(new DeleteCommand(ds, n));
        }

        if (ens.size() % 3 != 0) {
            ens.remove(ens.size() - 1);
        }
        if (ens.size() % 3 != 0) {
            ens.remove(ens.size() - 1);
        }

        for (i = 0; i < ens.size() - 2; i += 3) {
            List<Node> ob_nodes = new ArrayList<Node>();
            Way ob_way = new Way();

            EastNorth n0 = ens.get(i);
            EastNorth n1 = ens.get(i + 1);
            EastNorth n2 = ens.get(i + 2);
            EastNorth n3 = new EastNorth(n0.east() + n2.east() - n1.east(),
                    n0.north() + n2.north() - n1.north());

            ob_nodes.add(new Node(n0));
            ob_nodes.add(new Node(n1));
            ob_nodes.add(new Node(n2));
            ob_nodes.add(new Node(n3));

            for (Node n: ob_nodes) {
                cmds.add(new AddCommand(ds, n));
            }

            ob_nodes.add(ob_nodes.get(0));
            ob_way.setNodes(ob_nodes);
            cmds.add(new AddCommand(ds, ob_way));

            obuildings.add(ob_way);
        }

        cmds.add(new ChangePropertyCommand(ds, obuildings, tag_by));
        MainApplication.undoRedo.add(new SequenceCommand(
                    tr("Batch Orthogonal Building"), cmds));

        OrthogonalizeAction oa = new OrthogonalizeAction();
        for (Way w: obuildings) {
                ds.clearSelection();
                ds.addSelected(w);
                oa.actionPerformed(null);
                ds.clearSelection();
        }
    }
}
