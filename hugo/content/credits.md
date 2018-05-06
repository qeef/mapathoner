---
title: "Credits"
date: 2018-05-06T11:27:40+02:00
---
And what about the history of the plugin? It evolved from [JOSM scripts].

[JOSM scripts]: https://github.com/qeef/josm-scripts

## [Easy buildings] - autumn 2016
Credits: It's me.

This was the first attempt to deal with my laziness. More than a few
clicks and one shortcut keypress is no way. [BuildingsTools] make the job
for square buildings but not for circle (and I do not copy + resize them
because for me it's too heavy too) nor for residential areas.

[Easy buildings]: https://github.com/qeef/josm-scripts/blob/master/doc/user/easy_buildings.md
[BuildingsTools]: https://wiki.openstreetmap.org/wiki/JOSM/Plugins/BuildingsTools

## [Pick residential] - 2017-03-28, [SVĚT-HUB]
Credits: [@Piskvor]

We discussed [Easy buildings] that was not usable for [@Piskvor] because it
does not fit to his workflow. However, he proposed some script that would
make residential area around buildings on the screen. Not exactly what he
wanted (buildings have to be selected before script run) but better than
nothing.

[Pick residential]: https://github.com/qeef/josm-scripts/blob/master/doc/user/pick_residential.md
[@Piskvor]: https://github.com/piskvor
[SVĚT-HUB]: http://www.svet-hub.cz/

## [Batch buildings] - 2017-09-26
Credits: [@marxin]

This feature fits to [@marxin]'s workflow - just click as many buildings of
same kind as possible. It works for batch of circle buildings and for batch of
orthogonal buildings. Backward compatibility ensured and it was not such big
pain.

What is new with this feature is that collaboration on github slowly
establishes.

[Batch buildings]: https://github.com/qeef/josm-scripts/issues/11
[@marxin]: https://github.com/marxin

## [Mapathoner plugin] - 2018-04-24
Credits: [@marxin]

We discussed [@marxin]'s contribution to [JOSM] that was accepted (hurray!) and
the conversation goes around if the [JOSM scripts] could be rewritten to Java
and pushed upstream also. However scripts are pretty specific so I decided to
develop [JOSM plugin] instead.

I named the plugin *Mapathoner* after 3 days bad sleeping thinking about the
name. I wanted to avoid direct connection to [HOT] or [Missing Maps] because of
potential legal issues. And in fact - the mapathons are where we thought and
discussed the scripts, where the scripts were born. Where the *Mapathoner*
plugin was born.

[Mapathoner plugin]: http://qeef.github.io/mapathoner/
[JOSM]: https://josm.openstreetmap.de/
[JOSM plugin]: https://josm.openstreetmap.de/wiki/Plugins
[HOT]: https://www.hotosm.org/
[Missing Maps]: http://www.missingmaps.org/
