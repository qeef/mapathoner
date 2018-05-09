// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.plugins.mapathoner;

import static org.openstreetmap.josm.gui.help.HelpUtil.ht;
import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.command.AddCommand;
import org.openstreetmap.josm.command.ChangePropertyCommand;
import org.openstreetmap.josm.command.Command;
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
 * Create residential area around the selected buildings.
 * <p>
 * Usage:
 * <ul>
 * <li>Select buildings.
 * <li>Run action.
 * </ul>
 * <p>
 * Performs:
 * <ul>
 * <li>Create a closed way around the selected buildings.
 * <li>The way is tagged as landuse=residential.
 * </ul>
 * <p>
 * Notes:
 * <ul>
 * <li>Useful for HOT and Missing Maps mappers.
 * </ul>
 *
 * @author Jiri Hubacek
 * @since xxx
 */

public final class PickResidentialAreaAction extends JosmAction
{
    /**
     * Constructs a new {@code PickResidentialAreaAction}.
     */
    public PickResidentialAreaAction()
    {
        super(tr("Pick Residential Area"),
                (ImageProvider) null,
                tr("Create residential area around the selected buildings."),
                Shortcut.registerShortcut("mapathoner:pickresidentialarea",
                    tr("Mapathoner: {0}", tr("Pick Residential Area")),
                    KeyEvent.VK_R,
                    Shortcut.CTRL_SHIFT),
                true,
                "pickresidentialarea",
                true);
        putValue("help", ht("/Action/PickResidentialArea"));
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
        double distance = 6 / Main.getProjection().getMetersPerUnit();

        if (ways.size() < 2) {
            new Notification(
                    tr("Please select at least 2 buildings."))
                    .setIcon(JOptionPane.INFORMATION_MESSAGE)
                    .setDuration(Notification.TIME_LONG)
                    .show();
            return;
        }

        for (Way w: ways) {
            if (w.hasTag("building", "yes")) {
                nodes.addAll(find_ltbr(w, distance));
            }
        }

        nodes = graham_scan(nodes);
        Map<String, String> tag_lr = new HashMap<String, String>();
        Collection<OsmPrimitive> rareas = new LinkedList<OsmPrimitive>();
        Collection<Command> cmds = new LinkedList<Command>();

        for (Node n: nodes) {
            cmds.add(new AddCommand(ds, n));
        }

        nodes.add(nodes.get(0));
        Way rarea = new Way();
        rarea.setNodes(nodes);
        cmds.add(new AddCommand(ds, rarea));

        rareas.add(rarea);
        tag_lr.put("landuse", "residential");
        cmds.add(new ChangePropertyCommand(ds, rareas, tag_lr));

        MainApplication.undoRedo.add(new SequenceCommand(
                                tr("Pick Residential Area"), cmds));
        ds.clearSelection();
        }

    /**
     * Return list of border nodes for the building way.
     *
     * The "ltbr" is acronym for "left, top, bottom, right".
     *
     * @param w The way that represents building.
     * @param dist The distance from building to border.
     * @return The list of nodes around the building.
     */
    private static List<Node> find_ltbr(Way w, double dist)
    {
        EastNorth n0 = w.getNodes().get(0).getEastNorth();
        double l = n0.east();
        double t = n0.north();
        double b = n0.north();
        double r = n0.east();

        for (Node n: w.getNodes()) {
            if (n.getEastNorth().east() < l) l = n.getEastNorth().east();
            if (n.getEastNorth().north() < t) t = n.getEastNorth().north();
            if (n.getEastNorth().north() > b) b = n.getEastNorth().north();
            if (n.getEastNorth().east() > r) r = n.getEastNorth().east();
        }

        List<Node> nodes = new ArrayList<Node>();
        nodes.add(new Node(new EastNorth(l - dist, t - dist)));
        nodes.add(new Node(new EastNorth(r + dist, t - dist)));
        nodes.add(new Node(new EastNorth(r + dist, b + dist)));
        nodes.add(new Node(new EastNorth(l - dist, b + dist)));
        return nodes;
    }

    /**
     * Return number presenting if 3 nodes are counter-clockwise.
     *
     * If counter-clockwise, positive number is returned. If clockwise,
     * negative number is returned and zero if collinear.
     *
     * @param n1 1st node.
     * @param n2 2nd node.
     * @param n3 3rd node.
     * @see https://en.wikipedia.org/wiki/Graham_scan
     */
    private static double ccw(EastNorth en1, EastNorth en2, EastNorth en3)
    {
        double tmp1 = (en2.east() - en1.east())*(en3.north() - en1.north());
        double tmp2 = (en2.north() - en1.north())*(en3.east() - en1.east());
        return tmp1 - tmp2;
    }

    /**
     * Return convex hull of nodes.
     *
     * @param nodes List of nodes to find convex hull.
     * @return List of nodes representing convex hull.
     * @see https://en.wikipedia.org/wiki/Graham_scan
     */
    private static List<Node> graham_scan(List<Node> nodes)
    {
        List<EastNorth> ens = new ArrayList<EastNorth>();
        int i;

        // Data structure with x, y coordinates needed.
        for (Node n: nodes) {
            ens.add(n.getEastNorth());
        }

        // Find minimum
        EastNorth en_min = ens.get(0);
        int en_min_i = 0;
        for (i = 0; i < ens.size(); i += 1) {
            if (ens.get(i).north() < en_min.north()) {
                en_min = ens.get(i);
                en_min_i = i;
            } else if (ens.get(i).north() == en_min.north() &&
                    ens.get(i).east() < en_min.east()) {
                en_min = ens.get(i);
                en_min_i = i;
            }
        }

        // Preprocess the data - order by angle, ascending
        final EastNorth fen_min = en_min;
        ens.sort(new Comparator<EastNorth>() {
            @Override
            public int compare(EastNorth en1, EastNorth en2)
            {
                double d1e = en1.east() - fen_min.east();
                double d1n = en1.north() - fen_min.north();
                double d2e = en2.east() - fen_min.east();
                double d2n = en2.north() - fen_min.north();

                // @see https://en.wikipedia.org/wiki/Polar_coordinate_system
                if (Math.atan2(d1n, d1e) > Math.atan2(d2n, d2e)) return 1;
                if (Math.atan2(d1n, d1e) < Math.atan2(d2n, d2e)) return -1;
                return 0;
            }
        });

        // Shift start index - start from 1. At index 0 position put sentinel
        // (the last element of list).
        ens.add(ens.get(ens.size() - 1));
        for (i = ens.size() - 2; i >= 0; i -= 1) {
            ens.set(i + 1, ens.get(i));
        }
        ens.set(0, ens.get(ens.size() - 1));

        // Graham scan algorithm
        int m = 1;
        for (i = 2; i < ens.size(); i += 1) {
            while (ccw(ens.get(m - 1), ens.get(m), ens.get(i)) <= 0) {
                if (m > 1) {
                    m -= 1;
                    continue;
                } else if (i == ens.size() - 1) {
                    break;
                } else {
                    i += 1;
                }
            }

            m += 1;
            en_min = ens.get(m);
            ens.set(m, ens.get(i));
            ens.set(i, en_min);
        }

        // Result list of nodes
        List<Node> hull = new ArrayList<Node>();
        for (i = 1; i < m + 1; i += 1) {
            hull.add(new Node(ens.get(i)));
        }

        return hull;
    }
}
