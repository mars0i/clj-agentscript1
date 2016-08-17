# ags1
Experiments with [Clojurescript](http://clojurescript.org) and
[AgentScript](http://agentscript.org)

## License

This software is copyright 2016 by [Marshall
Abrams](http://members.logical.net/~marshall/), and is distributed
under the [Gnu General Public License version
3.0](http://www.gnu.org/copyleft/gpl.html) as specified in the file
LICENSE, except where noted, or where code has been included that was
released under a different license.  

## How to run it

Make sure you have a recent version of Java installed.

Install [Leiningen](http://leiningen.org).

Clone this repo, open a shell window, and change into the repo
directory.

Then execute 

    lein deps

to install Clojure and Clojurescript and to
install other necessary libraries.

When that's done, execute

    lein figwheel

After a few minutes, a new browser window should open with the model
running it, and you should see a Clojurescript REPL prompt in your
original shell window.  When you save changes to src/ags1/core.cljs, the
code will be compiled and the browser will reload the new configuration.
You can inspect variables in the running system at the REPL prompt.

To create a standalone version of this model that can be installed on
the web (for example), exit from the repl (Ctrl-D will do the job), and
run:

    lein do clean, cljsbuild once max

This will put all of the files needed to be installed into a directory
tree under resources/public.  "max" refers to one of the configuration
options in project.clj.  It uses the Google Closure "simple" compilation
mode.

(The leiningen project.clj file also contains a "min" configuration that
is supposed to allow fully optimized Google Closure compilation into a
single Javascript file, but that won't work without some Clojurescript
wrapper files for the Agentscript libraries, to deal with the fact that
Closure renames functions in its advanced compilation mode.  I may work
on this at some point.)

## Notes

### Files

The source files used here are:

* resources/public/index.html
* src/ags1/core.cljs (main source file)
* resources/public/css/site.css (empty at present)
* resources/public/lib/*.js
* project.clj (Leiningen configuration file)

The files in lib were copied from the Agentscript repo.  

### Clojurescript-Javascript interop tips

Mind the Clojurescript distinction between property accesses using
`(.-foo myobj)` and function calls using `(.bar myobj)`.

`set!` needs to see a literal property accessor such as `(.-foo
myobj)`; you can't assign the property accessor result to a variable and
then set! the variable.

Note that Clojurescript doesn't have a constant `this` pointer
available.  The `this-as` macro is used to provide an alias to whatever
is the current `this` in a given context.

You can refer to the model from which you run `setup` and `step`
functions that you've defined by using `this-as` within these function
defs.  (There are other ways to do this with step(), but you have to
jump through hoops to avoid using this-as in setup, because it's called
automatically when you new the model, whose constructor calls
setupAndEmit(), which calls setup().)

### Agentscript tips

Agentscript is written in Coffeescript, which is then compiled to
Javascript.  CoffeeScript source files end in ".coffee".

Most of the example simulations at agentscript.org and its github repo
are written in CoffeeScript, but jsmodel.html contains a Javascript
model.  The Clojurescript model here is a simple port of that model.

You don't necessarily have to learn CoffeeScript (I didn't) to benefit
from reading some parts of the CoffeeScript source files, if you're
willing to do some guessing.  template.coffee contains many useful
comments and configuration options (or see its generated doc file).

The main library file is agentscript.js (or agentscript.min.js), which
is compiled from model.coffee.  See that file or the doc generated from
it for more info about what's going on in agentscript.js.
