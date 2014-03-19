# lein-version-spec

A Leiningen plugin for setting (rewriting) the project.clj version according to a format specifier

## Usage

Put `[lein-version-spec "0.0.4"]` into the `:plugins` vector of your project.clj.

Add a `:version-spec "0.1.~{:env/circle_build_num}"` to your project.clj. Then run

    $ lein version-spec

This will compute your new version number, and *update* your project.clj on disk to contain the new version

## Rules

:version-spec uses the same syntax as `clojure.core.strint/<<`. namespaced keywords starting with :env, like `~{:env/foo_bar}` will look for an environment variable named FOO_BAR. Function calling syntax can also be used ~(foo/bar). If your function isn't built into clojure.core, we recommend putting your code into a new lein plugin. See https://github.com/circleci/version-specs for an example.

lein-version-spec only uses `apply` for `~()` function calls, so things like `~(if foo (bar))` won't work. If you need logic, put it in a plugin.

## License

Copyright © 2014 Allen Rohner

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
