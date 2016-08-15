
(function() {
  console.log("Yow!! Loading FAKEntscript.js")

  var Model;

  Model = (function() {
    console.log("in outer Model");

    function Model(args) {
      console.log("in inner Model");
      console.log(this); // MARSHALL DEBUG
    }

    Model.prototype.setWorld = function(opts) {
      console.log("in setWorld");
      var w;
      w = {};
      return this.world = w;
    };

    return Model;

  })();

  this.ABM = {
    Model: Model
  };

}).call(this);
