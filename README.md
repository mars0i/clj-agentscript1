# clj-agentscript
A simple illustration of the use of [AgentScript](http://agentscript.org)
in [Clojurescript](http://clojurescript.org)

## What it is

This a translation of Agentscript's jsmodel.html example model into
Clojurescript.  It's  a very simple model in which turtles run around
randomly.  However, the source code illustrates a few fine points about
using Agentscript with Clojurescript--"obvious once you know it"
points--that could confuse someone unfamiliar with one of these tools.

There is a runnable instance of the model at
http://members.logical.net/~marshall/cljmodel .

## Files

The source files used here are:

* resources/public/index.html (runs the model)
* src/cljmodel/core.cljs (main source file)
* resources/public/lib/*.js (Agentscript)
* project.clj (Leiningen configuration file)

The files in lib were copied from the Agentscript repo.  

## How to run it

Make sure you have a recent version of Java installed.

Install [Leiningen](http://leiningen.org).

Clone this repo, open a shell window, and change into the repo
directory.

Then execute 

    lein deps

to install Clojure and Clojurescript, if necessary, and to
install other necessary libraries.

When that's done, execute

    lein figwheel

After a few minutes, a new browser window should open with the model
running it, and you should see a Clojurescript REPL prompt in your
original shell window.  When you save changes to src/cljmodel/core.cljs, the
code will be compiled and the browser will reload the new configuration.
You can inspect variables in the running system at the REPL prompt.

To create a standalone version of this model that can be installed on
the web (for example), exit from the repl (Ctrl-D will do the job), and
run:

    lein do clean, cljsbuild once dist

This will put all of the files needed to be installed into a directory
tree under resources/public.  You'll need to install them on your
server, with the same directory structure.  "dist" refers to one of the
configuration options in project.clj. This configuration uses the Google
Closure compiler's "simple" compilation mode.

(The leiningen project.clj file also contains a "min" configuration that
is supposed to allow fully optimized Google Closure compilation into a
single Javascript file, but that won't work without some Clojurescript
wrapper files for the Agentscript libraries, to deal with the fact that
Closure renames functions in its advanced compilation mode.  I may work
on this at some point.)

## Clojurescript-Javascript interop tips

Mind the Clojurescript distinction between property accesses using
`(.-foo myobj)` and function calls using `(.bar myobj)`.

`set!` needs to see a literal property accessor such as `(.-foo
myobj)`; you can't assign the property accessor result to a variable and
then set! the variable.

Note that Clojurescript doesn't have a `this` pointer by default.
The `this-as` macro is used to provide an alias to whatever
is the current `this` in a given context.

You can refer to the model from which you run `setup` and `step`
functions that you've defined by using `this-as` within these function
defs.  (There are other ways to do this with step(), but then you'd
have to jump through hoops to avoid using `this-as` in setup, because
it's called automatically when you `new` the model, whose constructor
calls `setupAndEmit()` which calls `setup()`.)

## Agentscript tips

For Clojurescript-Javascript interop it can be helpful to understand a
little bit about the Javascript libraries you're using.  Here are some
things that I found useful along the way.

Agentscript is written in CoffeeScript, which is then compiled to
Javascript.  CoffeeScript source files end in ".coffee".

The main library file for Agentscript is agentscript.js (or
agentscript.min.js), which is compiled from model.coffee.  See that file
or the doc generated from it for more info about what's going on in
agentscript.js.  

In agentscript.js, `this` usually refers to an instance of the `Model`
class that's defined there.  This is why a "this" var that you define
using `this-as` in your Clojurescript `step`, `setup`, or `startup`
functions refer to this instance: They will be called by `Model` code
running in your instance of `Model`.

Most of the example simulations at agentscript.org and its github repo
are written in CoffeeScript.  (jsmodel.html is a Javascript model.)

You don't necessarily have to learn CoffeeScript (I didn't) to benefit
from reading some parts of the CoffeeScript source files, if you're
willing to do some guessing.  Among other things, the template.coffee
model contains many useful comments and configuration options (or see
its generated doc file).

## License

This software is copyright 2016 by [Marshall
Abrams](http://members.logical.net/~marshall/), and is distributed under
the [Gnu General Public License version
3.0](http://www.gnu.org/copyleft/gpl.html) as specified in the file
LICENSE, except where noted, or where code has been included that was
released under a different license.
