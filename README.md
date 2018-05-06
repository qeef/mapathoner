# Mapathoner
[Mapathoner] is a [JOSM] plugin with some useful tools for [HOT] and [Missing
Maps] mappers.

See the [Usage] page for how to install and use the plugin.

[Mapathoner]: https://qeef.github.io/mapathoner/
[JOSM]: https://josm.openstreetmap.de/
[HOT]: https://www.hotosm.org/
[Missing Maps]: http://www.missingmaps.org/
[Usage]: https://qeef.github.io/mapathoner/usage/

# Report
See the [Issues] for any kind of reports. All the feedback is greatly
appreticiated! All the discussion should be under the proper issue.

## Bugs
For misbehaviour, malfunction and all the other defects use the [Bug] label.

## Features
For enhancements and proposals use the [Feature] label.

[Issues]: https://github.com/qeef/mapathoner/issues
[Bug]: https://github.com/qeef/mapathoner/labels/bug
[Feature]: https://github.com/qeef/mapathoner/labels/feature

# Contribute
## Code
For quick orientation see the [changelog]. For the generated documentation see
the [JavaDoc] page.

[Gradle JOSM plugin] is used for the development. Source code adheres to [JOSM
Development Guidelines].

Please, think about [The seven rules of a great Git commit message] when making
commit. The project use [OneFlow] branching model with the `master` branch as
the branch where the development happens.

## Pages
[Hugo] with slightly modified [Coder] theme is used for pages generation. Pages
are hosted as [GitHub pages].

## License
This project is developed under [GNU GPLv3 license].

[changelog]: ./CHANGELOG.md
[JavaDoc]: https://qeef.github.io/mapathoner/javadoc/
[Gradle JOSM plugin]: https://plugins.gradle.org/plugin/org.openstreetmap.josm
[JOSM Development Guidelines]: https://josm.openstreetmap.de/wiki/DevelopersGuide/StyleGuide/
[The seven rules of a great Git commit message]: https://chris.beams.io/posts/git-commit/
[OneFlow]: http://endoflineblog.com/oneflow-a-git-branching-model-and-workflow
[Hugo]: http://gohugo.io/
[Coder]: https://github.com/luizdepra/hugo-coder
[GitHub pages]: https://pages.github.com/
[GNU GPLv3 license]: ./LICENSE

# Release
This is the release process:
- Create release branch from `master`.
- Update version in `build.gradle`, generate and update `jar`.
- Generate and update [Mapathoner] page.
- Generate and update [JavaDoc] page.
- Update changelog, tag the version and merge to `master`.

*Keep mapping!*
