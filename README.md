# ags1
Experiments with Clojurescript and AgentScript

## License

This software is copyright 2016 by [Marshall
Abrams](http://members.logical.net/~marshall/), and is distributed
under the [Gnu General Public License version
3.0](http://www.gnu.org/copyleft/gpl.html) as specified in the file
LICENSE, except where noted, or where code has been included that was
released under a different license.  

## Notes

### interop

The definition of Model in agentscript.js makes repeated references
to 'this', which in that context refers to the instance
of the Model.  
Works fine when run from Javascript, but when I run it
from Clojurescript (with :optimizations :none, so no name munging),
by default this in the Model def refers to the top-level window
(and I don't see any way to fix this using 'this-as').
i.e. that's what happens if you create a new model using e.g. (.Model ...).
However, if you create the new model using 'new', it gets the right 'this'.

Then you can refer to the model from which you run setup and step functions
that you've defined by using this-as within these function defs.
There are other ways to do this with step(), but you have to jump through
hoops to avoid using this-as in setup, because it's called automatically
when you new the model, whose constructor calls setupAndEmit(), which
calls setup().

Note that set! needs to see the literal field access; you can't 
put the field access result in a variable and then set! it.
If you are setting w.x.y.z, you apparently need to put the whole
path in: (set! (.-z (.-y (.-x w))) newval).

### Agentscript tips

Agentscript is written in Coffeescript, which is then compiled to
Javascript.  CoffeeScript source files end in ".coffee".

Most of the example simulations are written in CoffeeScript, but
jsmodel.html contains a Javascript model.  The Clojurescript model
here is a simple port of that model.

You don't necessarily have to learn CoffeeScript (I didn't) to benefit
from reading some parts of the CoffeeScript source files, if you're
willing to do some guessing.

template.coffee contains many useful comments and configuration options
(or see its generated doc file).

The main library file is agentscript.js, which is compiled from
model.coffee.  See that file or the doc generated from it for more info
about what's going on in agentscript.js.
